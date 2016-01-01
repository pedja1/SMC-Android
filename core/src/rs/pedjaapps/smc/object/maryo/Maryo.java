package rs.pedjaapps.smc.object.maryo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.HashSet;
import java.util.Set;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.MusicManager;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.DynamicObject;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.enemy.Enemy;
import rs.pedjaapps.smc.object.enemy.Turtle;
import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.shader.Shader;
import rs.pedjaapps.smc.utility.GameSave;

public class Maryo extends DynamicObject
{
    enum Keys
    {
        UP, DOWN, JUMP, FIRE
    }

    private static final float MAX_JUMP_SPEED = 9f;

    private boolean jumped;

    static Set<Keys> keys = new HashSet<>(Keys.values().length);

    public enum MaryoState
    {
        small, big, fire, ice
    }

    public static final float STAR_EFFECT_TIMEOUT = 15f;
    public static final float GLIM_COLOR_START_ALPHA = 0f;
    public static final float GLIM_COLOR_MAX_ALPHA = 0.95f;

    //this could be all done dynamically, but this way we minimize allocation in game loop
    //omg, this is a lot of constants :D
    private static final int A_KEY_WALKING_SMALL = 0;
    private static final int A_KEY_WALKING_BIG = 2;
    private static final int A_KEY_WALKING_FIRE = 4;
    private static final int A_KEY_THROW_FIRE = 5;
    private static final int A_KEY_WALKING_ICE = 9;
    private static final int A_KEY_THROW_ICE = 10;

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
    private static final float THROW_FRAME_DURATION = 0.1f;
    private static final float RESIZE_ANIMATION_DURATION = 0.977f;
    private static final float RESIZE_ANIMATION_FRAME_DURATION = RESIZE_ANIMATION_DURATION / 8f;

    protected static final float MAX_VEL = 4f;
    private static final float GOD_MOD_TIMEOUT = 3000;//3 sec

    private static final float BULLET_COOLDOWN = 1f;//1 sec

    WorldState worldState = WorldState.JUMPING;
    private MaryoState maryoState = GameSave.save.playerState;
    public boolean facingLeft = false;

    private boolean handleCollision = true;
    DyingAnimation dyingAnim = new DyingAnimation();

    public Sound jumpSound = null;

    public Rectangle debugRayRect = new Rectangle();

    /**
     * Makes player invincible and transparent for all enemies
     * Used (for limited time) when player is downgraded (or if you hack the game :D
     */
    boolean godMode = false;
    long godModeActivatedTime;

    Animation resizingAnimation;
    float resizeAnimStartTime;
    private MaryoState newState;//used with resize animation
    private MaryoState oldState;//used with resize animation

    public ParticleEffect starEffect;
    private float bulletShotTime = BULLET_COOLDOWN;
    private boolean fire;
    private float fireAnimationStateTime;

    private boolean mInvincibleStar;
    private final Color glimColor = new Color(0.160784314f, 0.654901961f, 1f, GLIM_COLOR_START_ALPHA);
    private float glimCounter;
    private boolean glimMode = true;
    private float starEffectTime;

    public float startPositionX;

    //textures
    private TextureRegion[] tMap = new TextureRegion[25];
    private Animation[] aMap = new Animation[12];

    public Maryo(World world, Vector3 position, Vector2 size)
    {
        super(world, size, position);
        setupBoundingBox();

        position.y = mColRect.y = mDrawRect.y += 0.5f;
        velocity.x = 4.5f;
        startPositionX = position.x;
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
        starEffect = new ParticleEffect(Assets.manager.get("data/animation/particles/maryo_star.p", ParticleEffect.class));

    }

    @Override
    public void dispose()
    {
        //we dont actually ahve to do anything here, since maryo is always present, and no new reources are created
    }

    private void loadTextures(MaryoState state)
    {
        TextureAtlas atlas = Assets.manager.get("data/maryo/" + state + ".pack");

        TextureRegion tmpStandRight;
        tMap[tIndex(state, TKey.stand_right)] = tmpStandRight = atlas.findRegion(TKey.stand_right.toString());

        TextureRegion[] walkFrames = new TextureRegion[4];
        walkFrames[0] = tmpStandRight;
        walkFrames[1] = atlas.findRegion("walk_right", 1);
        walkFrames[2] = atlas.findRegion("walk_right", 2);
        walkFrames[3] = walkFrames[1];
        aMap[aIndex(state, AKey.walk)] = new Animation(RUNNING_FRAME_DURATION, walkFrames);

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
        if (resizingAnimation != null && stateTime > resizeAnimStartTime + RESIZE_ANIMATION_DURATION)
        {
            if(newState == MaryoState.small)
            {
                godMode = true;
                godModeActivatedTime = System.currentTimeMillis();

                if (GameSave.save.item != null)
                {
                    //drop item
                    Item item = GameSave.save.item;
                    OrthographicCamera cam = ((GameScreen) world.screen).cam;

                    item.mColRect.x = item.position.x = cam.position.x - item.mColRect.width * 0.5f;
                    item.mColRect.y = item.position.y = cam.position.y + cam.viewportHeight * 0.5f - 1.5f;

                    item.updateBounds();

                    world.level.gameObjects.add(item);
                    item.drop();

                    GameSave.save.item = null;
                }
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
        else if (fire)
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
        else
        {
            marioFrame = tMap[tIndex(maryoState, TKey.stand_right)];
        }

        if(mInvincibleStar)
        {
            starEffect.setPosition(mColRect.x + mColRect.width * 0.5f, mColRect.y + mColRect.height * 0.5f);
            starEffect.draw(spriteBatch);
            spriteBatch.setShader(Shader.NORMAL_BLEND_SHADER);

            if (glimMode)
            {
                glimColor.a = Math.max(glimCounter, 0);
                if (glimCounter > GLIM_COLOR_MAX_ALPHA)
                {
                    glimMode = false;
                    glimCounter = GLIM_COLOR_MAX_ALPHA;
                }
            }
            else
            {
                glimColor.a = Math.max(glimCounter, 0);
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
        if (godMode)
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
                }
                break;
            case big:
                switch (akey)
                {
                    case walk:
                        return A_KEY_WALKING_BIG;
                }
                break;
            case fire:
                switch (akey)
                {
                    case walk:
                        return A_KEY_WALKING_FIRE;
                    case _throw:
                        return A_KEY_THROW_FIRE;
                }
                break;
            case ice:
                switch (akey)
                {
                    case walk:
                        return A_KEY_WALKING_ICE;
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
        if(((GameScreen)world.screen).getGameState() == GameScreen.GAME_STATE.GAME_RUNNING)
        {
            grounded = position.y - groundY < 0.1f;
            if(!grounded)
            {
                setWorldState(Maryo.WorldState.JUMPING);
            }
            if (keys.contains(Keys.JUMP))
            {
                if (!jumped && velocity.y < MAX_JUMP_SPEED)
                {
                    //vel.scl(delta);

                    velocity.add(0, 120f * delta, 0);

                    //vel.scl(1 / delta);
                    //maryo.velocity.set(vel.x, vel.y += 2f, maryo.velocity.z);
                }
                else
                {
                    jumped = true;
                }
            }

            {
                if (keys.contains(Keys.DOWN))
                {
                    if (getWorldState() != Maryo.WorldState.JUMPING)
                    {
                        setWorldState(Maryo.WorldState.DUCKING);
                    }
                }
                else
                {
                    if (getWorldState() != Maryo.WorldState.JUMPING)
                    {
                        setWorldState(WorldState.WALKING);
                    }
                }
            }
            if (grounded && getWorldState().equals(Maryo.WorldState.JUMPING))
            {
                setWorldState(WorldState.WALKING);
            }
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
            if (dyingAnim.update(delta)) super._update(delta);
        }
        else if (resizingAnimation != null)
        {
            stateTime += delta;
        }
        else
        {
            {
                super._update(delta);
                if(closestObject != null)
                {
                    debugRayRect.set(position.x, closestObject.mDrawRect.y + closestObject.mDrawRect.height, mColRect.width, position.y - (closestObject.mDrawRect.y + closestObject.mDrawRect.height));
                }

                if (closestObject != null
                        && closestObject instanceof Sprite
                        && ((Sprite) closestObject).type == Sprite.Type.halfmassive
                        && worldState == WorldState.DUCKING)
                {
                    position.y -= 0.1f;
                }
            }
        }
        if(mInvincibleStar)
        {
            starEffect.update(delta);
        }
        bulletShotTime += delta;
        if(mInvincibleStar)
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
            if(starEffectTime >= STAR_EFFECT_TIMEOUT)
            {
                mInvincibleStar = false;
                starEffectTime = 0;
                MusicManager.stop(false);
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
                if(mInvincibleStar)
                {
                    if(worldState != WorldState.DUCKING)
                    {
                        ((Enemy) object).downgradeOrDie(this, true);
                        GameSave.save.points += ((Enemy) object).mKillPoints;
                    }
                    else
                    {
                        ((Enemy) object).turn();
                    }
                }
                else if (((Enemy) object).frozen)
                {
                    ((Enemy) object).downgradeOrDie(this, true);
                    GameSave.save.points += ((Enemy) object).mKillPoints;
                }
                else if (deadAnyway)
                {
                    downgradeOrDie(false);
                }
                else
                {
                    if(object instanceof Turtle && ((Turtle)object).isShell && !((Turtle)object).isShellMoving)
                    {
                        Turtle turtle = (Turtle) object;
                        turtle.isShellMoving = true;
                        if(turtle.mColRect.x > mColRect.x)
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
                        int resolution = ((Enemy) object).hitByPlayer(this, vertical);
                        if (resolution == Enemy.HIT_RESOLUTION_ENEMY_DIED)
                        {
                            velocity.y = 5f * Gdx.graphics.getDeltaTime();
                            GameSave.save.points += ((Enemy) object).mKillPoints;
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
                }
            }
        }
        return false;
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
            dyingAnim.start();
        }
        else
        {
            SoundManager.play(Assets.manager.get("data/sounds/player/powerdown.mp3", Sound.class));
            upgrade(MaryoState.small, false, null, true);
        }
    }

    /*
    * Level up*/
    public void upgrade(MaryoState newState, boolean tempUpdate, Item item, boolean downgrade)
    {
        //cant upgrade from ice/fire to big
        if (!downgrade && (maryoState == newState && (newState == MaryoState.big || newState == MaryoState.ice || newState == MaryoState.fire))
                || (newState == MaryoState.big && (maryoState == MaryoState.ice || maryoState == MaryoState.fire)))
        {
            GameSave.save.item = item;
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
        Sound sound = upgradeSound(newState, downgrade);
        SoundManager.play(sound);
		fire = false;
    }

    private Sound upgradeSound(MaryoState newState, boolean downgrade)
    {
        if(downgrade)
        {
            //TODO
        }
        switch (newState)
        {
            case big:
                return Assets.manager.get("data/sounds/item/mushroom.mp3");
            case fire:
                return Assets.manager.get("data/sounds/item/fireplant.mp3");
            case ice:
                return Assets.manager.get("data/sounds/item/mushroom_blue.mp3");
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
            Sound sound = Assets.manager.get("data/sounds/player/dead.mp3");
            SoundManager.play(sound);
            ((GameScreen) world.screen).setGameState(GameScreen.GAME_STATE.PLAYER_DEAD);
            GameSave.save.lifes--;
        }

        public boolean update(float delat)
        {
            velocity.x = 0;
            position.x = diedPosition.x;
            if (mDrawRect.y + mDrawRect.height < 0)//first check if player is visible
            {
                ((GameScreen) world.screen).setGameState(GameScreen.GAME_STATE.GAME_OVER);
                world.level.gameObjects.removeValue(Maryo.this, true);
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
        if (worldState != WorldState.DYING)
        {
            downgradeOrDie(true);
        }
        return true;
    }

    private void setJumpSound()
    {
        switch (maryoState)
        {
            case small:
                jumpSound = Assets.manager.get("data/sounds/player/jump_small.mp3");
                break;
            case big:
            case fire:
            case ice:
                jumpSound = Assets.manager.get("data/sounds/player/jump_big.mp3");
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
        if (maryoState == MaryoState.fire)
        {
            addFireball(0f);
            if(mInvincibleStar)
            {
                addFireball(Fireball.VELOCITY_Y * 0.5f);
            }
            bulletShotTime = 0;
        }
        else if (maryoState == MaryoState.ice)
        {
            addIceball(0f);
            if(mInvincibleStar)
            {
                addIceball(Fireball.VELOCITY_Y * 0.5f);
            }
            bulletShotTime = 0;
        }
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
        //Collections.sort(world.level.gameObjects, new LevelLoader.ZSpriteComparator());
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
        //Collections.sort(world.level.gameObjects, new LevelLoader.ZSpriteComparator());
    }

    public void starPicked()
    {
        mInvincibleStar = true;
    }

    public void upPressed()
    {
        keys.add(Keys.UP);
    }

    public void downPressed()
    {
        keys.add(Keys.DOWN);
    }

    public void jumpPressed()
    {
        if(grounded)
        {
            setWorldState(GameObject.WorldState.JUMPING);
            keys.add(Keys.JUMP);

            Sound sound = jumpSound;
            SoundManager.play(sound);
        }
    }

    public void firePressed()
    {
        keys.add(Keys.FIRE);
        fire();
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
