package rs.pedjaapps.smc.object.maryo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import rs.pedjaapps.smc.audio.MusicManager;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.Box;
import rs.pedjaapps.smc.object.DynamicObject;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.LevelEntry;
import rs.pedjaapps.smc.object.LevelExit;
import rs.pedjaapps.smc.object.MovingPlatform;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.enemy.Enemy;
import rs.pedjaapps.smc.object.enemy.Turtle;
import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.screen.LoadingScreen;
import rs.pedjaapps.smc.shader.Shader;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.LevelLoader;
import rs.pedjaapps.smc.utility.MyMathUtils;
import rs.pedjaapps.smc.utility.TextUtils;

import static rs.pedjaapps.smc.object.LevelExit.LEVEL_EXIT_BEAM;
import static rs.pedjaapps.smc.object.Sprite.GROUND_ICE;

public class Maryo extends DynamicObject
{
    private enum Keys
    {
        LEFT, RIGHT, UP, DOWN, JUMP, FIRE
    }

    private static final int POWER_JUMP_DELTA = 1;

    private static final float MAX_JUMP_SPEED = 10f;
    private static final float POWER_MAX_JUMP_SPEED = 12f;
    private float mMaxJumpSpeed = MAX_JUMP_SPEED;

    public boolean jumped;

    private float downPressTime;

    private Set<Keys> keys = new HashSet<>(Keys.values().length);

    public enum MaryoState
    {
        small, big, fire, ice
    }

    public static float STAR_EFFECT_TIMEOUT = 15f;
    private static final float GLIM_COLOR_START_ALPHA = 0f;
    private static final float GLIM_COLOR_MAX_ALPHA = 0.95f;

    //this could be all done dynamically, but this way we minimize allocation in game loop
    //omg, this is a lot of constants :D
    private static final int A_KEY_WALKING_SMALL = 0;
    private static final int A_KEY_CLIMB_SMALL = 1;
    private static final int A_KEY_WALKING_BIG = 2;
    private static final int A_KEY_CLIMB_BIG = 3;
    private static final int A_KEY_WALKING_FIRE = 4;
    private static final int A_KEY_THROW_FIRE = 5;
    private static final int A_KEY_CLIMB_FIRE = 6;
    private static final int A_KEY_WALKING_ICE = 9;
    private static final int A_KEY_THROW_ICE = 10;
    private static final int A_KEY_CLIMB_ICE = 11;

    private static final int T_KEY_DUCK_RIGHT_SMALL = 0;
    private static final int T_KEY_JUMP_RIGHT_SMALL = 1;
    private static final int T_KEY_FALL_RIGHT_SMALL = 2;
    private static final int T_KEY_DEAD_RIGHT_SMALL = 3;
    private static final int T_KEY_STAND_RIGHT_SMALL = 4;

    private static final int T_KEY_DUCK_RIGHT_BIG = 5;
    private static final int T_KEY_JUMP_RIGHT_BIG = 6;
    private static final int T_KEY_FALL_RIGHT_BIG = 7;
    private static final int T_KEY_DEAD_RIGHT_BIG = 8;
    private static final int T_KEY_STAND_RIGHT_BIG = 9;

    private static final int T_KEY_DUCK_RIGHT_FIRE = 10;
    private static final int T_KEY_JUMP_RIGHT_FIRE = 11;
    private static final int T_KEY_FALL_RIGHT_FIRE = 12;
    private static final int T_KEY_DEAD_RIGHT_FIRE = 13;
    private static final int T_KEY_STAND_RIGHT_FIRE = 14;

    private static final int T_KEY_DUCK_RIGHT_ICE = 20;
    private static final int T_KEY_JUMP_RIGHT_ICE = 21;
    private static final int T_KEY_FALL_RIGHT_ICE = 22;
    private static final int T_KEY_DEAD_RIGHT_ICE = 23;
    private static final int T_KEY_STAND_RIGHT_ICE = 24;

    public static final float POSITION_Z = 0.0999f;

    private static final float RUNNING_FRAME_DURATION = 0.08f;
    private static final float CLIMB_FRAME_DURATION = 0.25f;
    private static final float THROW_FRAME_DURATION = 0.1f;
    private static final float RESIZE_ANIMATION_DURATION = 0.977f;
    private static final float RESIZE_ANIMATION_FRAME_DURATION = RESIZE_ANIMATION_DURATION / 8f;

    private static final float MAX_VEL = 4f;
    private static final float GOD_MOD_TIMEOUT = 3000;//3 sec

    private static final float BULLET_COOLDOWN = 1f;//1 sec

    private WorldState worldState = WorldState.JUMPING;
    private MaryoState maryoState = GameSave.save.playerState;
    public boolean facingLeft = false;

    private boolean handleCollision = true;
    private DyingAnimation dyingAnim = new DyingAnimation();

    private Sound jumpSound = null;

    public Rectangle debugRayRect = new Rectangle();

    /**
     * Makes player invincible and transparent for all enemies
     * Used (for limited time) when player is downgraded (or if you hack the game :D
     */
    private boolean godMode = false;
    private long godModeActivatedTime;

    private Animation resizingAnimation;
    private float resizeAnimStartTime;
    private MaryoState newState;//used with resize animation
    private MaryoState oldState;//used with resize animation

    //exit, enter
    private float enterStartTime;
    public boolean exiting, entering;
    private LevelExit exit;
    private LevelEntry entry;
    private Vector3 exitEnterStartPosition = new Vector3();
    private static final float exitEnterVelocity = 1.3f;
    private int rotation = 0;
    private ParticleEffect powerJumpEffect, starEffect;
    private boolean powerJump;
    private float bulletShotTime = BULLET_COOLDOWN;
    private boolean fire;
    private float fireAnimationStateTime;

    public boolean mInvincibleStar;
    private final Color glimColor = new Color(0.160784314f, 0.654901961f, 1f, GLIM_COLOR_START_ALPHA);
    private float glimCounter;
    private boolean glimMode = true;
    private float starEffectTime;
    public boolean canWalkOnAir = false;

    //textures
    private TextureRegion[] tMap = new TextureRegion[25];
    private Animation[] aMap = new Animation[12];

    private MovingPlatform attachedTo;
    private float distanceOnPlatform;
    private Vector3 prevPos = new Vector3();

    private float velocityDumpOrig;

    public final boolean isReflection;//is reflection of other player (multiplayer)


    public Maryo(World world, Vector3 position, Vector2 size)
    {
        this(world, position, size, false);
    }

    public Maryo(World world, Vector3 position, Vector2 size, boolean isReflection)
    {
        super(world, size, position);
        setupBoundingBox();

        position.y = mColRect.y = mDrawRect.y += 0.5f;
        world.screen.game.assets.manager.load("data/animation/particles/maryo_power_jump_emitter.p", ParticleEffect.class, world.screen.game.assets.particleEffectParameter);
        velocityDumpOrig = velocityDump;
        this.isReflection = isReflection;
        if(isReflection)
            handleCollision = false;
    }

    private void setupBoundingBox()
    {
        float centerX = position.x + mColRect.width / 2;
        switch (maryoState)
        {
            case small:
                mDrawRect.width = 0.9f;
                mDrawRect.height = 0.9f;
                break;
            case big:
            case fire:
            case ice:
                mDrawRect.height = 1.09f;
                mDrawRect.width = 1.09f;
                break;
        }
        mColRect.x = mDrawRect.x + mDrawRect.width / 4;
        mColRect.width = mDrawRect.width / 2;
        position.x = mColRect.x;

        if (worldState == WorldState.DUCKING)
        {
            mColRect.height = mDrawRect.height / 2;
        }
        else
        {
            mColRect.height = mDrawRect.height * 0.9f;
        }

        position.x = mColRect.x = centerX - mColRect.width / 2;
    }

    @Override
    public void updateBounds()
    {
        mDrawRect.x = mColRect.x - mDrawRect.width / 4;
        mDrawRect.y = mColRect.y;
    }

    public void initAssets()
    {
        MaryoState[] states = new MaryoState[]{MaryoState.small, MaryoState.big, MaryoState.fire, MaryoState.ice};
        for (MaryoState ms : states)
        {
            loadTextures(ms);
        }
        setJumpSound();
        powerJumpEffect = new ParticleEffect(world.screen.game.assets.manager.get("data/animation/particles/maryo_power_jump_emitter.p", ParticleEffect.class));
        starEffect = new ParticleEffect(world.screen.game.assets.manager.get("data/animation/particles/maryo_star.p", ParticleEffect.class));

    }

    @Override
    public void dispose()
    {
        //we dont actually ahve to do anything here, since maryo is always present, and no new reources are created
    }

    private void loadTextures(MaryoState state)
    {
        TextureAtlas atlas = world.screen.game.assets.manager.get("data/maryo/" + state + ".pack");

        TextureRegion tmpStandRight;
        tMap[tIndex(state, TKey.stand_right)] = tmpStandRight = atlas.findRegion(TKey.stand_right.toString());

        TextureRegion[] walkFrames = new TextureRegion[4];
        walkFrames[0] = tmpStandRight;
        walkFrames[1] = atlas.findRegion("walk_right", 1);
        walkFrames[2] = atlas.findRegion("walk_right", 2);
        walkFrames[3] = walkFrames[1];
        aMap[aIndex(state, AKey.walk)] = new Animation(RUNNING_FRAME_DURATION, walkFrames);

        TextureRegion[] climbFrames = new TextureRegion[2];
        climbFrames[0] = atlas.findRegion(TKey.climb_left + "");
        climbFrames[1] = atlas.findRegion(TKey.climb_right + "");
        aMap[aIndex(state, AKey.climb)] = new Animation(CLIMB_FRAME_DURATION, climbFrames);

        if (state == MaryoState.ice || state == MaryoState.fire)
        {
            TextureRegion[] throwFrames = new TextureRegion[2];
            throwFrames[0] = atlas.findRegion("throw_right", 1);
            throwFrames[1] = atlas.findRegion("throw_right", 2);
            aMap[aIndex(state, AKey._throw)] = new Animation(THROW_FRAME_DURATION, throwFrames);
        }

        tMap[tIndex(state, TKey.jump_right)] = atlas.findRegion(TKey.jump_right.toString());
        tMap[tIndex(state, TKey.fall_right)] = atlas.findRegion(TKey.fall_right.toString());

        if (MaryoState.small == state)
        {
            tMap[tIndex(state, TKey.dead_right)] = atlas.findRegion(TKey.dead_right.toString());
        }

        tMap[tIndex(state, TKey.duck_right)] = atlas.findRegion(TKey.duck_right.toString());
    }

    @Override
    public void _render(SpriteBatch spriteBatch)
    {
        TextureRegion marioFrame;
        if (exiting || entering)
        {
            marioFrame = tMap[tIndex(maryoState, TKey.stand_right)];

            float originX = mDrawRect.width * 0.5f;
            float originY = mDrawRect.height * 0.5f;
            spriteBatch.draw(marioFrame, mDrawRect.x, mDrawRect.y, originX, originY, mDrawRect.width, mDrawRect.height, 1, 1, rotation);

            return;
        }
        if (resizingAnimation != null && stateTime > resizeAnimStartTime + RESIZE_ANIMATION_DURATION)
        {
            if (newState == MaryoState.small)
            {
                godMode = true;
                godModeActivatedTime = System.currentTimeMillis();

                GameSave.dropItem(world);
            }
            else
            {
                godMode = false;
            }
            resizeAnimStartTime = 0;
            resizingAnimation = null;
            ((GameScreen) world.screen).setGameState(GameScreen.GAME_STATE.GAME_RUNNING);
            maryoState = newState;
            newState = null;
            oldState = null;
            setupBoundingBox();
            GameSave.save.playerState = maryoState;
        }
        if (resizingAnimation != null)
        {
            int index = resizingAnimation.getKeyFrameIndex(stateTime);
            marioFrame = resizingAnimation.getKeyFrames()[index];
            if (index == 0)
            {
                maryoState = oldState;
                setupBoundingBox();
            }
            else
            {
                maryoState = newState;
                setupBoundingBox();
            }
        }
        else if (fire && (maryoState == MaryoState.fire || maryoState == MaryoState.ice))
        {
            Animation animation = aMap[aIndex(maryoState, AKey._throw)];
            marioFrame = animation.getKeyFrame(fireAnimationStateTime, false);
            if (animation.isAnimationFinished(fireAnimationStateTime))
            {
                fire = false;
                fireAnimationStateTime = 0;
                //doFire();
            }
        }
        else if (worldState.equals(WorldState.WALKING))
        {
            marioFrame = aMap[aIndex(maryoState, AKey.walk)].getKeyFrame(stateTime, true);
        }
        else if (worldState == WorldState.DUCKING)
        {
            marioFrame = tMap[tIndex(maryoState, TKey.duck_right)];
        }
        else if (getWorldState().equals(WorldState.JUMPING))
        {
            if (velocity.y > 0)
            {
                marioFrame = tMap[tIndex(maryoState, TKey.jump_right)];
            }
            else
            {
                marioFrame = tMap[tIndex(maryoState, TKey.fall_right)];
            }
        }
        else if (worldState == WorldState.DYING)
        {
            marioFrame = tMap[tIndex(MaryoState.small, TKey.dead_right)];
        }
        else if (worldState == WorldState.CLIMBING)
        {
            TextureRegion[] frames = aMap[aIndex(maryoState, AKey.climb)].getKeyFrames();
            float distance = position.y - exitEnterStartPosition.y;
            marioFrame = frames[Math.floor(distance / 0.3f) % 2 == 0 ? 0 : 1];
        }
        else
        {
            marioFrame = tMap[tIndex(maryoState, TKey.stand_right)];
        }

        if (mInvincibleStar)
        {
            starEffect.setPosition(mColRect.x + mColRect.width * 0.5f, mColRect.y + mColRect.height * 0.5f);
            starEffect.draw(spriteBatch);
            spriteBatch.setShader(Shader.NORMAL_BLEND_SHADER);

            if (glimMode)
            {
                glimColor.a = MyMathUtils.max(glimCounter, 0);
                if (glimCounter > GLIM_COLOR_MAX_ALPHA)
                {
                    glimMode = false;
                    glimCounter = GLIM_COLOR_MAX_ALPHA;
                }
            }
            else
            {
                glimColor.a = MyMathUtils.max(glimCounter, 0);
                if (glimCounter < GLIM_COLOR_START_ALPHA)
                {
                    glimMode = true;
                    glimCounter = GLIM_COLOR_START_ALPHA;
                }
            }
            spriteBatch.setColor(glimColor);
        }

        marioFrame.flip(facingLeft, false);
        //if god mode, make player half-transparent
        if (godMode || isReflection)
        {
            Color color = spriteBatch.getColor();
            float oldA = color.a;

            color.a = 0.5f;
            spriteBatch.setColor(color);

            spriteBatch.draw(marioFrame, mDrawRect.x, mDrawRect.y, mDrawRect.width, mDrawRect.height);

            color.a = oldA;
            spriteBatch.setColor(color);
        }
        else
        {
            spriteBatch.draw(marioFrame, mDrawRect.x, mDrawRect.y, mDrawRect.width, mDrawRect.height);
        }
        marioFrame.flip(facingLeft, false);
        if (worldState == WorldState.DUCKING && powerJump)
        {
            powerJumpEffect.setPosition(position.x, position.y + 0.05f);
            powerJumpEffect.draw(spriteBatch);
        }
        spriteBatch.setShader(null);
        spriteBatch.setColor(Color.WHITE);
    }

    private int tIndex(MaryoState state, TKey tkey)
    {
        switch (tkey)
        {
            case stand_right:
                switch (state)
                {
                    case small:
                        return T_KEY_STAND_RIGHT_SMALL;
                    case big:
                        return T_KEY_STAND_RIGHT_BIG;
                    case fire:
                        return T_KEY_STAND_RIGHT_FIRE;
                    case ice:
                        return T_KEY_STAND_RIGHT_ICE;
                }
                break;
            case jump_right:
                switch (state)
                {
                    case small:
                        return T_KEY_JUMP_RIGHT_SMALL;
                    case big:
                        return T_KEY_JUMP_RIGHT_BIG;
                    case fire:
                        return T_KEY_JUMP_RIGHT_FIRE;
                    case ice:
                        return T_KEY_JUMP_RIGHT_ICE;
                }
                break;
            case fall_right:
                switch (state)
                {
                    case small:
                        return T_KEY_FALL_RIGHT_SMALL;
                    case big:
                        return T_KEY_FALL_RIGHT_BIG;
                    case fire:
                        return T_KEY_FALL_RIGHT_FIRE;
                    case ice:
                        return T_KEY_FALL_RIGHT_ICE;
                }
                break;
            case dead_right:
                switch (state)
                {
                    case small:
                        return T_KEY_DEAD_RIGHT_SMALL;
                    case big:
                        return T_KEY_DEAD_RIGHT_BIG;
                    case fire:
                        return T_KEY_DEAD_RIGHT_FIRE;
                    case ice:
                        return T_KEY_DEAD_RIGHT_ICE;
                }
                break;
            case duck_right:
                switch (state)
                {
                    case small:
                        return T_KEY_DUCK_RIGHT_SMALL;
                    case big:
                        return T_KEY_DUCK_RIGHT_BIG;
                    case fire:
                        return T_KEY_DUCK_RIGHT_FIRE;
                    case ice:
                        return T_KEY_DUCK_RIGHT_ICE;
                }
                break;
        }
        throw new IllegalArgumentException("Unknown texture key '" + tkey + "' or maryoState '" + maryoState + "'");
    }

    private int aIndex(MaryoState state, AKey akey)
    {
        switch (state)
        {
            case small:
                switch (akey)
                {
                    case walk:
                        return A_KEY_WALKING_SMALL;
                    case climb:
                        return A_KEY_CLIMB_SMALL;
                }
                break;
            case big:
                switch (akey)
                {
                    case walk:
                        return A_KEY_WALKING_BIG;
                    case climb:
                        return A_KEY_CLIMB_BIG;
                }
                break;
            case fire:
                switch (akey)
                {
                    case walk:
                        return A_KEY_WALKING_FIRE;
                    case climb:
                        return A_KEY_CLIMB_FIRE;
                    case _throw:
                        return A_KEY_THROW_FIRE;
                }
                break;
            case ice:
                switch (akey)
                {
                    case walk:
                        return A_KEY_WALKING_ICE;
                    case climb:
                        return A_KEY_CLIMB_ICE;
                    case _throw:
                        return A_KEY_THROW_ICE;
                }
                break;
        }
        throw new IllegalArgumentException("Unknown animation key '" + akey + "' or maryoState '" + maryoState + "'");
    }

    @Override
    public void _update(float delta)
    {
        if(isReflection)
            return;
        if (((GameScreen) world.screen).getGameState() == GameScreen.GAME_STATE.GAME_RUNNING)
        {
            if (downPressTime > POWER_JUMP_DELTA)
            {
                mMaxJumpSpeed = POWER_MAX_JUMP_SPEED;
                powerJump = true;
            }
            else
            {
                mMaxJumpSpeed = MAX_JUMP_SPEED;
                powerJump = false;
            }
            grounded = position.y - groundY < 0.1f;
            if (!grounded && getWorldState() != GameObject.WorldState.CLIMBING)
            {
                setWorldState(Maryo.WorldState.JUMPING);
            }
            boolean resetDownPressedTime = true;
            if (keys.contains(Keys.JUMP))
            {
                if (!jumped && velocity.y < mMaxJumpSpeed)
                {
                    float jumpTime = 0.1f;
                    float acceleration = mMaxJumpSpeed / (jumpTime / delta);
                    if (velocity.y + acceleration > mMaxJumpSpeed)
                    {
                        velocity.y = mMaxJumpSpeed;
                        jumped = true;
                    }
                    else
                    {
                        velocity.add(0, acceleration, 0);
                    }

                    resetDownPressedTime = false;
                }
                else
                {
                    jumped = true;
                }
            }
            if (getWorldState() == GameObject.WorldState.CLIMBING)
            {
                if (keys.contains(Keys.LEFT))
                {
                    // left is pressed
                    position.x -= 1.2f * delta;
                }
                else if (keys.contains(Keys.RIGHT))
                {
                    // right is pressed
                    position.x += 1.2f * delta;
                }
                if (keys.contains(Keys.UP))
                {
                    position.y += 1.8f * delta;
                }
                else if (keys.contains(Keys.DOWN))
                {
                    position.y -= 1.8f * delta;
                }
            }
            else
            {
                if (keys.contains(Keys.LEFT))
                {
                    // left is pressed
                    facingLeft = true;
                    if (getWorldState() != Maryo.WorldState.JUMPING)
                    {
                        setWorldState(Maryo.WorldState.WALKING);
                    }
                    velocity.set(velocity.x = -4.5f, velocity.y, velocity.z);
                }
                else if (keys.contains(Keys.RIGHT))
                {
                    // right is pressed
                    facingLeft = false;
                    if (getWorldState() != Maryo.WorldState.JUMPING)
                    {
                        setWorldState(Maryo.WorldState.WALKING);
                    }
                    velocity.set(velocity.x = +4.5f, velocity.y, velocity.z);
                }
                else if (keys.contains(Keys.DOWN))
                {
                    downPressTime += delta;
                    resetDownPressedTime = resetDownPressedTime & !grounded;
                    if (getWorldState() != Maryo.WorldState.JUMPING)
                    {
                        setWorldState(Maryo.WorldState.DUCKING);
                    }
                }
                else
                {
                    if (getWorldState() != Maryo.WorldState.JUMPING)
                    {
                        setWorldState(Maryo.WorldState.IDLE);
                    }
                    //slowly decrease linear velocity on x axes
                    //velocity.set(velocity.x * 0.7f, /*vel.y > 0 ? vel.y * 0.7f : */velocity.y, velocity.z);
                }
            }
            if (resetDownPressedTime)
            {
                downPressTime = 0;
                powerJumpEffect.reset();
            }
            if (grounded && getWorldState().equals(Maryo.WorldState.JUMPING))
            {
                setWorldState(Maryo.WorldState.IDLE);
            }
        }
        if (exiting)
        {
            boolean isDone = false;
            float velDelta = exitEnterVelocity * delta;
            if ("up".equals(exit.direction))
            {
                if (position.y >= exitEnterStartPosition.y + mDrawRect.height)
                {
                    isDone = true;
                }
                else
                {
                    mColRect.y = position.y += mDrawRect.height * velDelta;
                }
            }
            else if ("down".equals(exit.direction))
            {
                if (position.y <= exitEnterStartPosition.y - mDrawRect.height)
                {
                    isDone = true;
                }
                else
                {
                    mColRect.y = position.y -= mDrawRect.height * velDelta;
                }
            }
            else if ("right".equals(exit.direction))
            {
                if (position.x >= exitEnterStartPosition.x + mDrawRect.width)
                {
                    isDone = true;
                }
                else
                {
                    rotation = -90;
                    mColRect.x = position.x += mDrawRect.width * velDelta;
                }
            }
            else if ("left".equals(exit.direction))
            {
                if (exitEnterStartPosition.x - position.x >= mDrawRect.width)
                {
                    isDone = true;
                }
                else
                {
                    rotation = 90;
                    mColRect.x = position.x -= mDrawRect.width * velDelta;
                }
            }
            if (isDone)
            {
                exiting = false;
                //((GameScreen)world.screen).setGameState(GameScreen.GAME_STATE.GAME_RUNNING);

                doExit();
            }
            else
            {
                updateBounds();
            }
            return;
        }
        if (entering)
        {
            enterStartTime += delta;
            if (enterStartTime < 1)
            {
                return;
            }
            boolean isDone = false;
            float velDelta = exitEnterVelocity * delta;
            float offset = 0.1f;
            if ("up".equals(entry.direction))
            {
                if (position.y - offset > entry.mColRect.y + entry.mColRect.height)
                {
                    isDone = true;
                }
                else
                {
                    mColRect.y = position.y += mDrawRect.height * velDelta;
                }
            }
            else if ("down".equals(entry.direction))
            {
                if (position.y + mDrawRect.height + offset < entry.mColRect.y)
                {
                    isDone = true;
                }
                else
                {
                    mColRect.y = position.y -= mDrawRect.height * velDelta;
                }
            }
            else if ("right".equals(entry.direction))
            {
                if (position.x + offset > entry.mColRect.x + entry.mColRect.width)
                {
                    isDone = true;
                }
                else
                {
                    rotation = -90;
                    mColRect.x = position.x += mDrawRect.width * velDelta;
                }
            }
            else if ("left".equals(entry.direction))
            {
                if (position.x + mDrawRect.width + offset < entry.mColRect.x)
                {
                    isDone = true;
                }
                else
                {
                    rotation = 90;
                    mColRect.x = position.x -= mDrawRect.width * velDelta;
                }
            }
            if (isDone)
            {
                position.z = POSITION_Z;
                Collections.sort(world.level.gameObjects, new LevelLoader.ZSpriteComparator());
                entering = false;
                ((GameScreen) world.screen).setGameState(GameScreen.GAME_STATE.GAME_RUNNING);
            }
            else
            {
                updateBounds();
            }
            return;
        }
        if (fire)
        {
            fireAnimationStateTime += delta;
        }
        //disable godmod after timeot
        if (godMode && System.currentTimeMillis() - godModeActivatedTime > GOD_MOD_TIMEOUT)
        {
            godMode = false;
        }
        if (worldState == WorldState.DYING)
        {
            stateTime += delta;
            if (dyingAnim.update(delta))
            {
                acceleration.y = Constants.GRAVITY;
                acceleration.scl(delta);

                velocity.add(acceleration);

                checkCollisionWithBlocks(delta, false, false);

                stateTime += delta;
            }
        }
        else if (resizingAnimation != null)
        {
            stateTime += delta;
        }
        else
        {
            if (worldState == WorldState.CLIMBING)
            {
                checkCollisionWithBlocks(delta);
                boolean climbing = false;
                Array<GameObject> vo = world.getVisibleObjects();
                for (int i = 0, size = vo.size; i < size; i++)
                {
                    GameObject go = vo.get(i);
                    if (go instanceof Sprite && ((Sprite) go).type == Sprite.Type.climbable && go.mColRect.overlaps(mColRect))
                    {
                        climbing = true;
                        break;
                    }
                }
                if (!climbing) setWorldState(WorldState.JUMPING);
                stateTime += delta;
            }
            else
            {
                if (closestObject instanceof Sprite)
                {
                    float groundMod = 1.0f;

                    // ground type
                    switch (((Sprite) closestObject).groundType)
                    {
                        case GROUND_ICE:
                        {
                            groundMod = 1.220f;
                            break;
                        }
                    }
                    velocityDump = velocityDump * groundMod;
                }
                super._update(delta);
                velocityDump = velocityDumpOrig;

                if (closestObject != null)
                {
                    debugRayRect.set(position.x, closestObject.mDrawRect.y + closestObject.mDrawRect.height, mColRect.width, position.y - (closestObject.mDrawRect.y + closestObject.mDrawRect.height));
                }
                else
                {
                    debugRayRect.set(position.x, 0, mColRect.width, position.y);
                }

                if (closestObject != null
                        && closestObject instanceof Sprite
                        && ((Sprite) closestObject).type == Sprite.Type.halfmassive
                        && worldState == WorldState.DUCKING)
                {
                    position.y -= 0.1f;
                }
                if (position.y - groundY < 0.1f && closestObject instanceof MovingPlatform && ((MovingPlatform) closestObject).canAttachTo)
                {
                    if (attachedTo != closestObject)
                    {
                        attachedTo = (MovingPlatform) closestObject;
                        attachedTo.platformState = MovingPlatform.MOVING_PLATFORM_TOUCHED;
                        attachedTo.touched = true;
                        distanceOnPlatform = position.x - attachedTo.position.x;
                    }
                }
                else
                {
                    attachedTo = null;
                    distanceOnPlatform = 0;
                    prevPos.x = 0;
                }
                if (attachedTo != null)
                {
                    if (prevPos.x != 0) distanceOnPlatform += position.x - prevPos.x;
                    mColRect.x = position.x = attachedTo.position.x + distanceOnPlatform;
                    if (velocity.y <= 0)
                    {
                        mColRect.y = position.y = attachedTo.position.y + attachedTo.mColRect.height;
                    }
                    updateBounds();
                    prevPos.set(position);
                }
            }
        }
        if (powerJump)
        {
            powerJumpEffect.update(delta);
        }
        if (mInvincibleStar)
        {
            starEffect.update(delta);
        }
        bulletShotTime += delta;
        if (mInvincibleStar)
        {
            starEffectTime += delta;
            if (glimMode)
            {
                glimCounter += (delta * 8f);
            }
            else
            {
                glimCounter -= (delta * 6f);
            }
            if (starEffectTime >= STAR_EFFECT_TIMEOUT)
            {
                mInvincibleStar = false;
                starEffectTime = 0;
                MusicManager.stop(false);
            }
        }
    }

    private void doExit()
    {
        //just change level
        String nextLevelName;
        //next level in list
        if (TextUtils.isEmpty(exit.levelName) && TextUtils.isEmpty(exit.entry))
        {
            String currentLevel = ((GameScreen) world.screen).parent == null ? ((GameScreen) world.screen).levelName : ((GameScreen) world.screen).parent.levelName;
            nextLevelName = GameSave.getNextLevel(currentLevel);

            if (((GameScreen) world.screen).parent != null)
            {
                ((GameScreen) world.screen).parent.dispose();
                ((GameScreen) world.screen).parent = null;
            }
            if (exit.type == LevelExit.LEVEL_EXIT_BEAM)
            {
                ((GameScreen) world.screen).endLevel(nextLevelName);
            }
            else
            {
                world.screen.game.levelEnd(((GameScreen) world.screen).levelName, true);
                world.screen.game.setScreen(new LoadingScreen(new GameScreen(world.screen.game, false, nextLevelName), false, facingLeft));
            }
        }
        //go to sublevel
        else
        {
            //same level
            if (TextUtils.isEmpty(exit.levelName))
            {
                LevelEntry entry = world.level.findEntryOrThrow(exit.entry);
                enterLevel(entry);
            }
            else//another level
            {
                /*if (TextUtils.isEmpty(exit.entry))
                {
                    throw new GdxRuntimeException("Cannot go to sublevel, entry is null");
                }*/
                nextLevelName = exit.levelName;
                GameScreen parent = ((GameScreen) world.screen).parent;

                boolean resume = false;
                GameScreen newScreen;
                //we are exiting sublevel
                if (parent != null && parent.levelName.equals(nextLevelName))
                {
                    newScreen = parent;
                    newScreen.entryName = exit.entry;
                    newScreen.forceCheckEnter = true;

                    //update maryo state in new level
                    newScreen.getWorld().maryo.setMarioState(maryoState);
                    newScreen.getWorld().maryo.mInvincibleStar = mInvincibleStar;
                    newScreen.getWorld().maryo.starEffectTime = starEffectTime;
                    newScreen.getWorld().maryo.canWalkOnAir = canWalkOnAir;

                    resume = true;
                }
                //new level or sublevel
                else
                {
                    if (parent != null)
                    {
                        parent.dispose();
                    }
                    parent = (GameScreen) world.screen;
                    newScreen = new GameScreen(world.screen.game, false, nextLevelName, parent);
                    newScreen.entryName = exit.entry;
                }
                world.screen.game.levelEnd(((GameScreen) world.screen).levelName, true);
                world.screen.game.setScreen(new LoadingScreen(newScreen, resume, facingLeft));
            }
        }
    }

    @Override
    protected boolean handleCollision(GameObject object, boolean vertical)
    {
        if (!handleCollision) return false;
        super.handleCollision(object, vertical);
        if (object instanceof Item)
        {
            Item item = (Item) object;
            if (!item.playerHit) item.hitPlayer();
            //world.trashObjects.add(item);
        }
        else if (object instanceof Enemy && ((Enemy) object).handleCollision)
        {
            if (!godMode)
            {
                boolean deadAnyway = isDeadByJumpingOnTopOfEnemy((Enemy) object);
                if (mInvincibleStar)
                {
                    if (worldState != WorldState.IDLE && worldState != WorldState.DUCKING)
                    {
                        if (((Enemy) object).canBeKilledByStar())
                        {
                            ((Enemy) object).downgradeOrDie(this, true, true);
                            GameSave.save.points += ((Enemy) object).mKillPoints;
                        }
                        else
                        {
                            hitEnemy((Enemy) object, vertical);
                        }
                    }
                    else
                    {
                        ((Enemy) object).turn();
                    }
                }
                else if (((Enemy) object).frozen)
                {
                    ((Enemy) object).downgradeOrDie(this, true, false);
                    GameSave.save.points += ((Enemy) object).mKillPoints;
                }
                else if (deadAnyway)
                {
                    downgradeOrDie(false);
                }
                else
                {
                    if (object instanceof Turtle && ((Turtle) object).isShell && !((Turtle) object).isShellMoving)
                    {
                        Turtle turtle = (Turtle) object;
                        turtle.isShellMoving = true;
                        if (turtle.mColRect.x > mColRect.x)
                        {
                            turtle.setDirection(Direction.right);
                            turtle.mColRect.x = turtle.position.x = mColRect.x + mColRect.width + 0.1f;
                            turtle.updateBounds();
                        }
                        else
                        {
                            turtle.setDirection(Direction.left);
                            turtle.mColRect.x = turtle.position.x = mColRect.x - turtle.mColRect.width - 0.1f;
                            turtle.updateBounds();
                        }
                    }
                    else
                    {
                        hitEnemy((Enemy) object, vertical);
                    }
                }
            }
        }
        else if (object instanceof Box && position.y + mColRect.height <= object.position.y)
        {
            ((Box) object).activate();
        }
        return false;
    }

    private void hitEnemy(Enemy enemy, boolean vertical)
    {
        int resolution = enemy.hitByPlayer(this, vertical);
        if (resolution == Enemy.HIT_RESOLUTION_ENEMY_DIED)
        {
            velocity.y = 5f * Gdx.graphics.getDeltaTime();
            GameSave.save.points += enemy.mKillPoints;
        }
        else if (resolution == Enemy.HIT_RESOLUTION_PLAYER_DIED)
        {
            downgradeOrDie(false);
        }
        else
        {
            velocity.y = 5f * Gdx.graphics.getDeltaTime();
        }
    }

    private boolean isDeadByJumpingOnTopOfEnemy(Enemy enemy)
    {
        return !enemy.canBeKilledByJumpingOnTop();
    }

    public WorldState getWorldState()
    {
        return worldState;
    }

    public void setWorldState(WorldState newWorldState)
    {
        if (worldState == WorldState.DYING) return;
        this.worldState = newWorldState;
        if (worldState == WorldState.DUCKING)
        {
            mColRect.height = mDrawRect.height / 2;
        }
        else
        {
            mColRect.height = mDrawRect.height * 0.9f;
        }
        if (worldState == WorldState.CLIMBING)
        {
            exitEnterStartPosition.set(position);
            velocity.x = 0;
            velocity.y = 0;
        }
    }

    @Override
    public float maxVelocity()
    {
        return MAX_VEL;
    }

    public void downgradeOrDie(boolean forceDie)
    {
        if (maryoState == MaryoState.small || forceDie)
        {
            worldState = WorldState.DYING;
            GameSave.save.playerState = MaryoState.small;
            setMarioState(MaryoState.small);
            updateBounds();
            GameSave.setItem(null, null);
            dyingAnim.start();
        }
        else
        {
            if(!isReflection)
                SoundManager.play(world.screen.game.assets.manager.get("data/sounds/player/powerdown.mp3", Sound.class));
            upgrade(MaryoState.small, null, true);
        }
    }

    /*
    * Level up*/
    public void upgrade(MaryoState newState, Item item, boolean downgrade)
    {
        //cant upgrade from ice/fire to big
        if (!downgrade && (maryoState == newState && (newState == MaryoState.big || newState == MaryoState.ice || newState == MaryoState.fire))
                || (newState == MaryoState.big && (maryoState == MaryoState.ice || maryoState == MaryoState.fire)))
        {
            GameSave.setItem(world.screen, item);
            return;
        }
        else if (maryoState == newState)
        {
            return;
        }
        this.newState = newState;
        oldState = maryoState;
        Array<TextureRegion> frames = generateResizeAnimationFrames(maryoState, newState);
        resizingAnimation = new Animation(RESIZE_ANIMATION_FRAME_DURATION, frames);
        resizingAnimation.setPlayMode(Animation.PlayMode.LOOP);
        resizeAnimStartTime = stateTime;
        godMode = true;

        ((GameScreen) world.screen).setGameState(GameScreen.GAME_STATE.PLAYER_UPDATING);

        //play new state sound
        if(!isReflection)
        {
            Sound sound = upgradeSound(newState, downgrade);
            SoundManager.play(sound);
        }
        fire = false;
    }

    private Sound upgradeSound(MaryoState newState, boolean downgrade)
    {
        switch (newState)
        {
            case big:
                return world.screen.game.assets.manager.get("data/sounds/item/mushroom.mp3");
            case fire:
                return world.screen.game.assets.manager.get("data/sounds/item/fireplant.mp3");
            case ice:
                return world.screen.game.assets.manager.get("data/sounds/item/mushroom_blue.mp3");
        }
        return null;
    }

    private Array<TextureRegion> generateResizeAnimationFrames(MaryoState stateFrom, MaryoState stateTo)
    {
        Array<TextureRegion> regions = new Array<>();
        if (worldState.equals(WorldState.WALKING))
        {
            regions.add(aMap[aIndex(stateFrom, AKey.walk)].getKeyFrame(stateTime, true));
            regions.add(aMap[aIndex(stateTo, AKey.walk)].getKeyFrame(stateTime, true));
        }
        else if (worldState == WorldState.DUCKING)
        {
            regions.add(tMap[tIndex(stateFrom, TKey.duck_right)]);
            regions.add(tMap[tIndex(stateTo, TKey.duck_right)]);
        }
        else if (getWorldState().equals(WorldState.JUMPING))
        {
            if (velocity.y > 0)
            {
                regions.add(tMap[tIndex(stateFrom, TKey.jump_right)]);
                regions.add(tMap[tIndex(stateTo, TKey.jump_right)]);
            }
            else
            {
                regions.add(tMap[tIndex(stateFrom, TKey.fall_right)]);
                regions.add(tMap[tIndex(stateTo, TKey.fall_right)]);
            }
        }
        else if (worldState == WorldState.DYING)
        {
            regions.add(tMap[tIndex(stateFrom, TKey.dead_right)]);
            regions.add(tMap[tIndex(stateTo, TKey.dead_right)]);
        }
        else
        {
            regions.add(tMap[tIndex(stateFrom, TKey.stand_right)]);
            regions.add(tMap[tIndex(stateTo, TKey.stand_right)]);
        }
        return regions;
    }

    public class DyingAnimation
    {
        private float diedTime;
        boolean upAnimFinished, dyedReset, firstDelayFinished;
        Vector3 diedPosition;
        boolean upBoost;

        public void start()
        {
            diedTime = stateTime;
            handleCollision = false;
            diedPosition = new Vector3(position);
            if(!isReflection)
            {
                Sound sound = world.screen.game.assets.manager.get("data/sounds/player/dead.mp3");
                SoundManager.play(sound);
            }
            ((GameScreen) world.screen).setGameState(GameScreen.GAME_STATE.PLAYER_DEAD);
            GameSave.save.lifes--;
        }

        public boolean update(float delat)
        {
            velocity.x = 0;
            position.x = diedPosition.x;
            if (mDrawRect.y + mDrawRect.height < 0)//first check if player is visible
            {
                GameSave.save.playerState = MaryoState.small;
                ((GameScreen) world.screen).setGameState(GameScreen.GAME_STATE.GAME_OVER);
                world.trashObjects.add(Maryo.this);
                return false;
            }

            if (!firstDelayFinished && stateTime - diedTime < 0.5f)//delay 500ms
            {
                return false;
            }
            else
            {
                firstDelayFinished = true;
            }

            if (!upBoost)
            {
                //animate player up a bit
                velocity.y = 8f;
                upBoost = true;
            }

            return true;
        }
    }

    @Override
    protected boolean handleDroppedBelowWorld()
    {
        if (!canWalkOnAir)
        {
            if (worldState != WorldState.DYING)
            {
                downgradeOrDie(true);
            }
            return true;
        }
        else
        {
            return super.handleDroppedBelowWorld();
        }
    }

    private void setJumpSound()
    {
        if(isReflection)
            return;
        switch (maryoState)
        {
            case small:
                if (powerJump)
                {
                    jumpSound = world.screen.game.assets.manager.get("data/sounds/player/jump_small_power.mp3");
                }
                else
                {
                    jumpSound = world.screen.game.assets.manager.get("data/sounds/player/jump_small.mp3");
                }
                break;
            case big:
            case fire:
            case ice:
                if (powerJump)
                {
                    jumpSound = world.screen.game.assets.manager.get("data/sounds/player/jump_big_power.mp3");
                }
                else
                {
                    jumpSound = world.screen.game.assets.manager.get("data/sounds/player/jump_big.mp3");
                }
                break;
        }
    }

    public MaryoState getMarioState()
    {
        return maryoState;
    }

    public void setMarioState(MaryoState marioState)
    {
        this.maryoState = marioState;
        setJumpSound();
    }

    public void checkLevelEnter(String entry_)
    {
        LevelEntry entry = world.level.findEntry(entry_);
        if (entry != null)
        {
            if (entry.type == LEVEL_EXIT_BEAM)
            {
                float entryCenter = entry.mColRect.x + entry.mColRect.width * 0.5f;
                position.x = mColRect.x = entryCenter - mColRect.width * 0.5f;
                position.y = mColRect.y = entry.mColRect.y + entry.mColRect.height + mColRect.height;
                updateBounds();
            }
            else
            {
                enterLevel(entry);
            }
        }
    }

    public void enterLevel(LevelEntry entry)
    {
        if(isReflection)
            return;
        Sound sound = world.screen.game.assets.manager.get("data/sounds/enter_pipe.mp3");
        SoundManager.play(sound);
        ((GameScreen) world.screen).setGameState(GameScreen.GAME_STATE.PLAYER_UPDATING);
        entering = true;
        this.entry = entry;
        if (entry.type == LevelExit.LEVEL_EXIT_WARP)
        {
            // left
            if ("left".equals(entry.direction))
            {
                position.x = mColRect.x = entry.mColRect.x + entry.mColRect.width;

                float entryCenter = entry.mColRect.y + entry.mColRect.height * 0.5f;
                position.y = mColRect.y = entryCenter - mColRect.height * 0.5f;
            }
            // right
            else if ("right".equals(entry.direction))
            {
                position.x = mColRect.x = entry.mColRect.x - mColRect.width;


                float entryCenter = entry.mColRect.y + entry.mColRect.height * 0.5f;
                position.y = mColRect.y = entryCenter - mColRect.height * 0.5f;
            }
            //up
            else if ("up".equals(entry.direction))
            {
                position.y = mColRect.y = entry.mColRect.y - mColRect.height;

                float entryCenter = entry.mColRect.x + entry.mColRect.width * 0.5f;
                position.x = mColRect.x = entryCenter - mColRect.width * 0.5f;
            }
            // down
            else if ("down".equals(entry.direction))
            {
                position.y = mColRect.y = entry.mColRect.y;

                float entryCenter = entry.mColRect.x + entry.mColRect.width * 0.5f;
                position.x = mColRect.x = entryCenter - mColRect.width * 0.5f;
            }
        }
        else if (entry.type == LEVEL_EXIT_BEAM)
        {
            float entryCenter = entry.mColRect.x + entry.mColRect.width * 0.5f;
            position.x = mColRect.x = entryCenter - mColRect.width * 0.5f;
            position.y = mColRect.y = entry.mColRect.y + entry.mColRect.height + mColRect.height;
        }
        updateBounds();
        exitEnterStartPosition.set(position);
        position.z = LevelLoader.m_pos_z_passive_start;
        Collections.sort(world.level.gameObjects, new LevelLoader.ZSpriteComparator());

        //todo sound
    }

    public void exitLevel(LevelExit exit)
    {
        if(isReflection)
            return;
        switch (exit.type)
        {
            case LEVEL_EXIT_BEAM:
                if (exiting) return;
                exiting = true;
                this.exit = exit;
                doExit();
                break;
            case LevelExit.LEVEL_EXIT_WARP:
                if (exiting) return;
                Sound sound = world.screen.game.assets.manager.get("data/sounds/leave_pipe.mp3");
                SoundManager.play(sound);
                ((GameScreen) world.screen).setGameState(GameScreen.GAME_STATE.PLAYER_UPDATING);
                exiting = true;
                this.exit = exit;
                if ("up".equals(exit.direction) || "down".equals(exit.direction))
                {
                    float exitCenter = exit.mColRect.x + exit.mColRect.width * 0.5f;
                    position.x = mColRect.x = exitCenter - mColRect.width * 0.5f;
                }
                else
                {
                    float exitCenter = exit.mColRect.y + exit.mColRect.height * 0.5f;
                    position.y = mColRect.y = exitCenter - mColRect.height * 0.5f;
                }
                updateBounds();
                exitEnterStartPosition.set(position);
                position.z = LevelLoader.m_pos_z_passive_start;
                Collections.sort(world.level.gameObjects, new LevelLoader.ZSpriteComparator());

                //todo sound
                break;
        }
    }

    public void fire()
    {
        if (worldState == WorldState.DUCKING || (maryoState != MaryoState.fire && maryoState != MaryoState.ice))
            return;
        if (bulletShotTime < BULLET_COOLDOWN)
            return;
        fire = true;
        doFire();
    }

    private void doFire()
    {
        if(isReflection)
            return;
        Sound sound = null;
        if (maryoState == MaryoState.fire)
        {
            addFireball(0f);
            if (mInvincibleStar)
            {
                addFireball(Fireball.VELOCITY_Y * 0.5f);
            }
            bulletShotTime = 0;
            sound = world.screen.game.assets.manager.get("data/sounds/item/fireball.mp3");
        }
        else if (maryoState == MaryoState.ice)
        {
            addIceball(0f);
            if (mInvincibleStar)
            {
                addIceball(Fireball.VELOCITY_Y * 0.5f);
            }
            bulletShotTime = 0;
            sound = world.screen.game.assets.manager.get("data/sounds/item/iceball.mp3");
        }

        SoundManager.play(sound);
    }

    private void addIceball(float velY)
    {
        Iceball iceball = world.ICEBALL_POOL.obtain();
        iceball.mColRect.x = iceball.position.x = mDrawRect.x + mDrawRect.width * 0.5f;
        iceball.mColRect.y = iceball.position.y = mDrawRect.y + mDrawRect.height * 0.5f;
        iceball.updateBounds();
        iceball.reset();
        iceball.direction = facingLeft ? Direction.left : Direction.right;
        iceball.velocity.y = velY;
        world.level.gameObjects.add(iceball);
        Collections.sort(world.level.gameObjects, new LevelLoader.ZSpriteComparator());
    }

    private void addFireball(float velY)
    {
        Fireball fireball = world.FIREBALL_POOL.obtain();
        fireball.mColRect.x = fireball.position.x = mDrawRect.x + mDrawRect.width * 0.5f;
        fireball.mColRect.y = fireball.position.y = mDrawRect.y + mDrawRect.height * 0.5f;
        fireball.updateBounds();
        fireball.reset();
        fireball.direction = facingLeft ? Direction.left : Direction.right;
        fireball.velocity.y = velY;
        world.level.gameObjects.add(fireball);
        Collections.sort(world.level.gameObjects, new LevelLoader.ZSpriteComparator());
    }

    public void starPicked()
    {
        mInvincibleStar = true;
    }

    public void leftPressed()
    {
        keys.add(Keys.LEFT);
        checkLeave("left");
    }

    public void rightPressed()
    {
        keys.add(Keys.RIGHT);
        checkLeave("right");
    }

    public void upPressed()
    {
        keys.add(Keys.UP);
        boolean climbing = false;
        Array<GameObject> vo = world.getVisibleObjects();
        for (int i = 0, size = vo.size; i < size; i++)
        {
            GameObject go = vo.get(i);
            if (go instanceof LevelExit
                    && go.mColRect.overlaps(mColRect)
                    && (((LevelExit) go).type == LEVEL_EXIT_BEAM || (((LevelExit) go).type == LevelExit.LEVEL_EXIT_WARP && "up".equals(((LevelExit) go).direction))))
            {
                exitLevel((LevelExit) go);
                break;
            }
            else if (getWorldState() != GameObject.WorldState.CLIMBING &&
                    go instanceof Sprite && ((Sprite) go).type == Sprite.Type.climbable && go.mColRect.overlaps(mColRect))
            {
                climbing = true;
                break;
            }
        }
        if (climbing) setWorldState(GameObject.WorldState.CLIMBING);
    }

    private void checkLeave(String dir)
    {
        Array<GameObject> vo = world.getVisibleObjects();
        //for(GameObject go : world.getVisibleObjects())
        for (int i = 0, size = vo.size; i < size; i++)
        {
            GameObject go = vo.get(i);
            if (go instanceof LevelExit
                    && go.mColRect.overlaps(mColRect)
                    && (((LevelExit) go).type == LEVEL_EXIT_BEAM || (((LevelExit) go).type == LevelExit.LEVEL_EXIT_WARP && dir.equals(((LevelExit) go).direction))))
            {
                /*String nextLevelName = Level.levels[++GameSaveUtility.getInstance().save.currentLevel];
                world.screen.game.setScreen(new LoadingScreen(new GameScreen(world.screen.game, false, nextLevelName), false));*/
                exitLevel((LevelExit) go);
                return;
            }
        }
    }

    public void downPressed()
    {
        keys.add(Keys.DOWN);
        checkLeave("down");
    }

    public void jumpPressed()
    {
        if (grounded || getWorldState() == GameObject.WorldState.CLIMBING)
        {
            setWorldState(GameObject.WorldState.JUMPING);
            keys.add(Keys.JUMP);

            if(isReflection)
                return;
            setJumpSound();
            Sound sound = jumpSound;
            SoundManager.play(sound);
        }
    }

    public void firePressed()
    {
        keys.add(Keys.FIRE);
        fire();
    }

    public void leftReleased()
    {
        keys.remove(Keys.LEFT);
    }

    public void rightReleased()
    {
        keys.remove(Keys.RIGHT);
    }

    public void upReleased()
    {
        keys.remove(Keys.UP);
    }

    public void downReleased()
    {
        keys.remove(Keys.DOWN);
    }

    public void jumpReleased()
    {
        keys.remove(Keys.JUMP);
        jumped = false;
    }

    public void fireReleased()
    {
        keys.remove(Keys.FIRE);
    }
}
