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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.AnimationKey;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.assets.TextureKey;
import rs.pedjaapps.smc.audio.MusicManager;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.Box;
import rs.pedjaapps.smc.object.DynamicObject;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.LevelEntry;
import rs.pedjaapps.smc.object.LevelExit;
import rs.pedjaapps.smc.object.MovingPlatform;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.WorldState;
import rs.pedjaapps.smc.object.enemy.Enemy;
import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.screen.LoadingScreen;
import rs.pedjaapps.smc.shader.Shader;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.LevelLoader;
import rs.pedjaapps.smc.utility.TextUtils;

import static rs.pedjaapps.smc.object.LevelExit.LEVEL_EXIT_BEAM;
import static rs.pedjaapps.smc.object.Sprite.GROUND_ICE;

public class Maryo extends DynamicObject {

    public static final float DEFAULT_SIZE = 0.9f;

    private enum Keys {
        LEFT, RIGHT, UP, DOWN, JUMP, FIRE
    }

    private static final int POWER_JUMP_DELTA = 1;

    private static final float MAX_JUMP_SPEED = 10f;
    private static final float POWER_MAX_JUMP_SPEED = 12f;

    private float maxJumpSpeed = MAX_JUMP_SPEED;

    public boolean jumpPeakReached;

    private float downPressTime;

    private Set<Keys> keys = new HashSet<>(Keys.values().length);

    public enum MaryoState {
        small, big, fire, ice;

        public static int toInt(MaryoState ms) {
            switch (ms) {
                case big:
                    return 1;
                case fire:
                    return 2;
                case ice:
                    return 3;
                default:
                    return 0;
            }
        }

        public static MaryoState fromInt(int i) {
            switch (i) {
                case 1:
                    return big;
                case 2:
                    return fire;
                case 3:
                    return ice;
                default:
                    return small;
            }
        }
    }

    private static final float GHOST_EFFECT_TIMEOUT = 10f;
    public static float STAR_EFFECT_TIMEOUT = 16f;
    public static final float STAR_EFFECT_SPEEDFACTOR = 1.2f;
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
    private MaryoState maryoState;
    private boolean facingLeft = false;

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

    private Animation<TextureRegion> resizingAnimation;
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
    private boolean starMusicPlaying;
    private boolean glimMode = true;
    private float starEffectTime;
    public boolean canWalkOnAir = false;

    public boolean ghostmode;
    private float leftGhostTime;

    //textures
    private TextureRegion[] tMap = new TextureRegion[25];
    private Animation<TextureRegion>[] aMap = new Animation[12];

    private MovingPlatform attachedTo;
    private float distanceOnPlatform;
    private Vector3 prevPos = new Vector3();

    private float velocityDumpOrig;

    public Maryo(float x, float y, float z, float width, float height) {
        super(x, y, z, width, height);
        maryoState = GameSave.getMaryoState();
        setupBoundingBox();

        position.y = colRect.y = drawRect.y += 0.5f;
        MaryoGame.game.assets.load("data/animation/particles/maryo_power_jump_emitter.p", ParticleEffect.class, Assets.PARTICLE_EFFECT_PARAMETER);
        velocityDumpOrig = velocityDump;
    }

    private void setupBoundingBox() {
        float centerX = position.x + colRect.width / 2;
        switch (maryoState) {
            case small:
                drawRect.width = 0.9f;
                drawRect.height = 0.9f;
                break;
            case big:
            case fire:
            case ice:
                drawRect.height = 1.09f;
                drawRect.width = 1.09f;
                break;
        }
        colRect.x = drawRect.x + drawRect.width / 4;
        colRect.width = drawRect.width / 2;
        position.x = colRect.x;

        if (worldState == WorldState.DUCKING) {
            colRect.height = drawRect.height / 2;
        } else {
            colRect.height = drawRect.height * 0.9f;
        }

        position.x = colRect.x = centerX - colRect.width / 2;
    }

    @Override
    public void updateBounds() {
        drawRect.x = colRect.x - drawRect.width / 4;
        drawRect.y = colRect.y;
    }

    public void initAssets() {
        MaryoState[] states = new MaryoState[]{MaryoState.small, MaryoState.big, MaryoState.fire, MaryoState.ice};
        for (MaryoState ms : states) {
            loadTextures(ms);
        }
        setJumpSound();
        powerJumpEffect = new ParticleEffect(MaryoGame.game.assets.get("data/animation/particles/maryo_power_jump_emitter.p", ParticleEffect.class));
        starEffect = new ParticleEffect(MaryoGame.game.assets.get("data/animation/particles/maryo_star.p", ParticleEffect.class));

    }

    @Override
    public void dispose() {
        //we dont actually ahve to do anything here, since maryo is always present, and no new reources are created
    }

    private void loadTextures(MaryoState state) {
        TextureAtlas atlas = MaryoGame.game.assets.get(Assets.ATLAS_DYNAMIC);
        String prefix = "maryo_" + state + "_";

        TextureRegion tmpStandRight;
        tMap[tIndex(state, TextureKey.stand_right)] = tmpStandRight = atlas.findRegion(prefix + TextureKey.stand_right.toString());

        TextureRegion[] walkFrames = new TextureRegion[4];
        walkFrames[0] = tmpStandRight;
        walkFrames[1] = atlas.findRegion(prefix + "walk_right_1");
        walkFrames[2] = atlas.findRegion(prefix + "walk_right_2");
        walkFrames[3] = walkFrames[1];
        aMap[aIndex(state, AnimationKey.walk)] = new Animation<>(RUNNING_FRAME_DURATION, walkFrames);

        TextureRegion[] climbFrames = new TextureRegion[2];
        climbFrames[0] = atlas.findRegion(prefix + TextureKey.climb_left + "");
        climbFrames[1] = atlas.findRegion(prefix + TextureKey.climb_right + "");
        aMap[aIndex(state, AnimationKey.climb)] = new Animation<>(CLIMB_FRAME_DURATION, climbFrames);

        if (state == MaryoState.ice || state == MaryoState.fire) {
            TextureRegion[] throwFrames = new TextureRegion[2];
            throwFrames[0] = atlas.findRegion(prefix + "throw_right_1");
            throwFrames[1] = atlas.findRegion(prefix + "throw_right_2");
            aMap[aIndex(state, AnimationKey._throw)] = new Animation<>(THROW_FRAME_DURATION, throwFrames);
        }

        tMap[tIndex(state, TextureKey.jump_right)] = atlas.findRegion(prefix + TextureKey.jump_right.toString());
        tMap[tIndex(state, TextureKey.fall_right)] = atlas.findRegion(prefix + TextureKey.fall_right.toString());

        if (MaryoState.small == state) {
            tMap[tIndex(state, TextureKey.dead_right)] = atlas.findRegion(prefix + TextureKey.dead_right.toString());
        }

        tMap[tIndex(state, TextureKey.duck_right)] = atlas.findRegion(prefix + TextureKey.duck_right.toString());
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        TextureRegion marioFrame;
        if (exiting || entering) {
            marioFrame = tMap[tIndex(maryoState, TextureKey.stand_right)];

            float originX = drawRect.width * 0.5f;
            float originY = drawRect.height * 0.5f;
            spriteBatch.draw(marioFrame, drawRect.x, drawRect.y, originX, originY, drawRect.width, drawRect.height, 1, 1, rotation);

            return;
        }
        if (resizingAnimation != null && stateTime > resizeAnimStartTime + RESIZE_ANIMATION_DURATION) {
            if (newState == MaryoState.small) {
                godMode = true;
                godModeActivatedTime = System.currentTimeMillis();

                dropSavedItem();
            } else {
                godMode = false;
            }
            resizeAnimStartTime = 0;
            resizingAnimation = null;
            maryoState = newState;
            MaryoGame.game.setGameState(GameScreen.GAME_STATE.GAME_RUNNING);
            newState = null;
            oldState = null;
            setupBoundingBox();
            GameSave.setMaryoState(maryoState);
        }
        if (resizingAnimation != null) {
            int index = resizingAnimation.getKeyFrameIndex(stateTime);
            marioFrame = resizingAnimation.getKeyFrame(stateTime);
            if (index == 0) {
                maryoState = oldState;
                setupBoundingBox();
            } else {
                maryoState = newState;
                setupBoundingBox();
            }
        } else if (fire && (hasFireAbility())) {
            Animation<TextureRegion> animation = aMap[aIndex(maryoState, AnimationKey._throw)];
            marioFrame = animation.getKeyFrame(fireAnimationStateTime, false);
            if (animation.isAnimationFinished(fireAnimationStateTime)) {
                fire = false;
                fireAnimationStateTime = 0;
                //doFire();
            }
        } else if (worldState.equals(WorldState.WALKING)) {
            marioFrame = aMap[aIndex(maryoState, AnimationKey.walk)].getKeyFrame(stateTime, true);
        } else if (worldState == WorldState.DUCKING) {
            marioFrame = tMap[tIndex(maryoState, TextureKey.duck_right)];
        } else if (getWorldState().equals(WorldState.JUMPING)) {
            if (velocity.y > 0) {
                marioFrame = tMap[tIndex(maryoState, TextureKey.jump_right)];
            } else {
                marioFrame = tMap[tIndex(maryoState, TextureKey.fall_right)];
            }
        } else if (worldState == WorldState.DYING) {
            marioFrame = tMap[tIndex(MaryoState.small, TextureKey.dead_right)];
        } else if (worldState == WorldState.CLIMBING) {
            TextureRegion[] frames = aMap[aIndex(maryoState, AnimationKey.climb)].getKeyFrames();
            float distance = position.y - exitEnterStartPosition.y;
            marioFrame = frames[Math.floor(distance / 0.3f) % 2 == 0 ? 0 : 1];
        } else {
            marioFrame = tMap[tIndex(maryoState, TextureKey.stand_right)];
        }

        if (mInvincibleStar) {
            starEffect.setPosition(colRect.x + colRect.width * 0.5f, colRect.y + colRect.height * 0.5f);
            starEffect.draw(spriteBatch);
            spriteBatch.setShader(Shader.NORMAL_BLEND_SHADER);

            if (glimMode) {
                glimColor.a = Math.max(glimCounter, 0);
                if (glimCounter > GLIM_COLOR_MAX_ALPHA) {
                    glimMode = false;
                    glimCounter = GLIM_COLOR_MAX_ALPHA;
                }
            } else {
                glimColor.a = Math.max(glimCounter, 0);
                if (glimCounter < GLIM_COLOR_START_ALPHA) {
                    glimMode = true;
                    glimCounter = GLIM_COLOR_START_ALPHA;
                }
            }
            spriteBatch.setColor(glimColor);
        } else if (ghostmode)
            spriteBatch.setShader(Shader.GS_SHADER);

        marioFrame.flip(facingLeft, false);
        //if god mode, make player half-transparent
        if (godMode) {
            Color color = spriteBatch.getColor();
            float oldA = color.a;

            color.a = 0.5f;
            spriteBatch.setColor(color);

            spriteBatch.draw(marioFrame, drawRect.x, drawRect.y, drawRect.width, drawRect.height);

            color.a = oldA;
            spriteBatch.setColor(color);
        } else {
            spriteBatch.draw(marioFrame, drawRect.x, drawRect.y, drawRect.width, drawRect.height);
        }
        marioFrame.flip(facingLeft, false);
        if (worldState == WorldState.DUCKING && powerJump) {
            powerJumpEffect.setPosition(position.x, position.y + 0.05f);
            if (powerJumpEffect.isComplete())
                powerJumpEffect.reset();
        }
        if (!powerJumpEffect.isComplete())
            powerJumpEffect.draw(spriteBatch);

        if (mInvincibleStar || ghostmode)
            spriteBatch.setShader(null);

        spriteBatch.setColor(Color.WHITE);
    }

    private void dropSavedItem() {
        if (GameSave.getItem() == 0)
            return;
        //drop item

        int itemType = GameSave.getItem();

        OrthographicCamera cam = ((GameScreen) MaryoGame.game.currentScreen).cam;

        Item item = Item.createObject(itemType, Item.getClassFromItemType(itemType), cam.position.x, cam.position.y + cam.viewportHeight * 0.5f - 1.5f, 0, 0, 0);
        item.initAssets();
        MaryoGame.game.addObject(item);
        Sound sound = MaryoGame.game.assets.get(Assets.SOUND_ITEMBOX_GET);
        SoundManager.play(sound);
        item.drop();
        GameSave.setItem(0);
    }

    private int tIndex(MaryoState state, TextureKey tkey) {
        switch (tkey) {
            case stand_right:
                switch (state) {
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
                switch (state) {
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
                switch (state) {
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
                switch (state) {
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
                switch (state) {
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

    private int aIndex(MaryoState state, AnimationKey akey) {
        switch (state) {
            case small:
                switch (akey) {
                    case walk:
                        return A_KEY_WALKING_SMALL;
                    case climb:
                        return A_KEY_CLIMB_SMALL;
                }
                break;
            case big:
                switch (akey) {
                    case walk:
                        return A_KEY_WALKING_BIG;
                    case climb:
                        return A_KEY_CLIMB_BIG;
                }
                break;
            case fire:
                switch (akey) {
                    case walk:
                        return A_KEY_WALKING_FIRE;
                    case climb:
                        return A_KEY_CLIMB_FIRE;
                    case _throw:
                        return A_KEY_THROW_FIRE;
                }
                break;
            case ice:
                switch (akey) {
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
    public void update(float delta) {
        if (MaryoGame.game.getGameState() == GameScreen.GAME_STATE.GAME_RUNNING) {
            if (downPressTime > POWER_JUMP_DELTA) {
                maxJumpSpeed = POWER_MAX_JUMP_SPEED;
                powerJump = true;
            } else {
                maxJumpSpeed = MAX_JUMP_SPEED;
                powerJump = false;
            }
            grounded = position.y - groundY < 0.1f;
            if (!grounded && getWorldState() != WorldState.CLIMBING) {
                setWorldState(WorldState.JUMPING);
            }
            boolean resetDownPressedTime = true;
            if (keys.contains(Keys.JUMP)) {
                if (!jumpPeakReached && velocity.y < maxJumpSpeed) {
                    float jumpTime = 0.1f;
                    float acceleration = maxJumpSpeed / (jumpTime / delta);
                    if (velocity.y + acceleration > maxJumpSpeed) {
                        velocity.y = maxJumpSpeed;
                        jumpPeakReached = true;
                    } else {
                        velocity.add(0, acceleration, 0);
                    }

                    resetDownPressedTime = false;
                } else {
                    jumpPeakReached = true;
                }
            }
            if (getWorldState() == WorldState.CLIMBING) {
                if (keys.contains(Keys.LEFT)) {
                    // left is pressed
                    position.x -= 1.2f * delta;
                } else if (keys.contains(Keys.RIGHT)) {
                    // right is pressed
                    position.x += 1.2f * delta;
                }
                if (keys.contains(Keys.UP)) {
                    position.y += 1.8f * delta;
                } else if (keys.contains(Keys.DOWN)) {
                    position.y -= 1.8f * delta;
                }
            } else {
                float speedFactor = (mInvincibleStar ? STAR_EFFECT_SPEEDFACTOR : 1f);
                if (keys.contains(Keys.LEFT)) {
                    // left is pressed
                    facingLeft = true;
                    if (getWorldState() != WorldState.JUMPING) {
                        setWorldState(WorldState.WALKING);
                    }
                    velocity.set(velocity.x = -4.5f * speedFactor, velocity.y, velocity.z);
                } else if (keys.contains(Keys.RIGHT)) {
                    // right is pressed
                    facingLeft = false;
                    if (getWorldState() != WorldState.JUMPING) {
                        setWorldState(WorldState.WALKING);
                    }
                    velocity.set(velocity.x = +4.5f * speedFactor, velocity.y, velocity.z);
                } else if (keys.contains(Keys.DOWN)) {
                    downPressTime += delta;
                    resetDownPressedTime = resetDownPressedTime & !grounded;
                    if (getWorldState() != WorldState.JUMPING) {
                        setWorldState(WorldState.DUCKING);
                    }
                } else {
                    if (getWorldState() != WorldState.JUMPING) {
                        setWorldState(WorldState.IDLE);
                    }
                    //slowly decrease linear velocity on x axes
                    //velocity.set(velocity.x * 0.7f, /*vel.y > 0 ? vel.y * 0.7f : */velocity.y, velocity.z);
                }
            }
            if (resetDownPressedTime) {
                downPressTime = 0;
                powerJumpEffect.allowCompletion();
            }
            if (grounded && getWorldState().equals(WorldState.JUMPING)) {
                setWorldState(WorldState.IDLE);
            }
        }
        if (exiting) {
            boolean isDone = false;
            float velDelta = exitEnterVelocity * delta;
            if ("up".equals(exit.direction)) {
                if (position.y >= exitEnterStartPosition.y + drawRect.height) {
                    isDone = true;
                } else {
                    colRect.y = position.y += drawRect.height * velDelta;
                }
            } else if ("down".equals(exit.direction)) {
                if (position.y <= exitEnterStartPosition.y - drawRect.height) {
                    isDone = true;
                } else {
                    colRect.y = position.y -= drawRect.height * velDelta;
                }
            } else if ("right".equals(exit.direction)) {
                if (position.x >= exitEnterStartPosition.x + drawRect.width) {
                    isDone = true;
                } else {
                    rotation = -90;
                    colRect.x = position.x += drawRect.width * velDelta;
                }
            } else if ("left".equals(exit.direction)) {
                if (exitEnterStartPosition.x - position.x >= drawRect.width) {
                    isDone = true;
                } else {
                    rotation = 90;
                    colRect.x = position.x -= drawRect.width * velDelta;
                }
            }
            if (isDone) {
                exiting = false;
                //((GameScreen)world.screen).setGameState(GameScreen.GAME_STATE.GAME_RUNNING);

                doExit();
            } else {
                updateBounds();
            }
            return;
        }
        if (entering) {
            enterStartTime += delta;
            if (enterStartTime < 1) {
                return;
            }
            boolean isDone = false;
            float velDelta = exitEnterVelocity * delta;
            float offset = 0.1f;
            if ("up".equals(entry.direction)) {
                if (position.y - offset > entry.colRect.y + entry.colRect.height) {
                    isDone = true;
                } else {
                    colRect.y = position.y += drawRect.height * velDelta;
                }
            } else if ("down".equals(entry.direction)) {
                if (position.y + drawRect.height + offset < entry.colRect.y) {
                    isDone = true;
                } else {
                    colRect.y = position.y -= drawRect.height * velDelta;
                }
            } else if ("right".equals(entry.direction)) {
                if (position.x + offset > entry.colRect.x + entry.colRect.width) {
                    isDone = true;
                } else {
                    rotation = -90;
                    colRect.x = position.x += drawRect.width * velDelta;
                }
            } else if ("left".equals(entry.direction)) {
                if (position.x + drawRect.width + offset < entry.colRect.x) {
                    isDone = true;
                } else {
                    rotation = 90;
                    colRect.x = position.x -= drawRect.width * velDelta;
                }
            }
            if (isDone) {
                position.z = POSITION_Z;
                MaryoGame.game.sortLevel();
                entering = false;
                MaryoGame.game.setGameState(GameScreen.GAME_STATE.GAME_RUNNING);
            } else {
                updateBounds();
            }
            return;
        }
        if (fire) {
            fireAnimationStateTime += delta;
        }
        //disable godmod after timeot
        if (godMode && System.currentTimeMillis() - godModeActivatedTime > GOD_MOD_TIMEOUT) {
            godMode = false;
        }
        if (worldState == WorldState.DYING) {
            stateTime += delta;
            if (dyingAnim.update(delta)) {
                acceleration.y = Constants.GRAVITY;
                acceleration.scl(delta);

                velocity.add(acceleration);

                checkCollisionWithBlocks(delta, false, false);

                stateTime += delta;
            }
        } else if (resizingAnimation != null) {
            stateTime += delta;
        } else {
            if (worldState == WorldState.CLIMBING) {
                checkCollisionWithBlocks(delta);
                boolean climbing = false;
                Array<GameObject> vo = MaryoGame.game.currentScreen.world.getVisibleObjects();
                for (int i = 0, size = vo.size; i < size; i++) {
                    GameObject go = vo.get(i);
                    if (go instanceof Sprite && ((Sprite) go).type == Sprite.Type.climbable && go.colRect.overlaps(colRect)) {
                        climbing = true;
                        break;
                    }
                }
                if (!climbing) setWorldState(WorldState.JUMPING);
                stateTime += delta;
            } else {
                if (closestObject instanceof Sprite) {
                    float groundMod = 1.0f;

                    // ground type
                    switch (((Sprite) closestObject).groundType) {
                        case GROUND_ICE: {
                            groundMod = 1.220f;
                            break;
                        }
                    }
                    velocityDump = velocityDump * groundMod;
                }
                super.update(delta);
                velocityDump = velocityDumpOrig;

                if (closestObject != null) {
                    debugRayRect.set(position.x, closestObject.drawRect.y + closestObject.drawRect.height, colRect.width, position.y - (closestObject.drawRect.y + closestObject.drawRect.height));
                } else {
                    debugRayRect.set(position.x, 0, colRect.width, position.y);
                }

                if (closestObject != null
                        && closestObject instanceof Sprite
                        && ((Sprite) closestObject).type == Sprite.Type.halfmassive
                        && worldState == WorldState.DUCKING) {
                    position.y -= 0.1f;
                }
                if (position.y - groundY < 0.1f && closestObject instanceof MovingPlatform && ((MovingPlatform) closestObject).canAttachTo) {
                    if (attachedTo != closestObject) {
                        attachedTo = (MovingPlatform) closestObject;
                        attachedTo.platformState = MovingPlatform.MOVING_PLATFORM_TOUCHED;
                        attachedTo.touched = true;
                        distanceOnPlatform = position.x - attachedTo.position.x;
                    }
                } else {
                    attachedTo = null;
                    distanceOnPlatform = 0;
                    prevPos.x = 0;
                }
                if (attachedTo != null) {
                    if (prevPos.x != 0) distanceOnPlatform += position.x - prevPos.x;
                    colRect.x = position.x = attachedTo.position.x + distanceOnPlatform;
                    if (velocity.y <= 0) {
                        colRect.y = position.y = attachedTo.position.y + attachedTo.colRect.height;
                    }
                    updateBounds();
                    prevPos.set(position);
                }
            }
        }
        if (!powerJumpEffect.isComplete()) {
            powerJumpEffect.update(delta);
        }
        if (mInvincibleStar) {
            starEffect.update(delta);
        }
        bulletShotTime += delta;
        if (mInvincibleStar) {
            starEffectTime += delta;
            if (glimMode) {
                glimCounter += (delta * 8f);
            } else {
                glimCounter -= (delta * 6f);
            }
            // Musik 1 Sekunde vor Ablauf beenden und blinken ebenfalls
            if (starEffectTime >= STAR_EFFECT_TIMEOUT - 1f) {
                if (starMusicPlaying)
                    MusicManager.stop(false);
                starMusicPlaying = false;
                glimCounter = GLIM_COLOR_START_ALPHA;
            } else
                starMusicPlaying = true;
            //und wenn die Zeit um ist, dann ganz beenden
            if (starEffectTime >= STAR_EFFECT_TIMEOUT) {
                mInvincibleStar = false;
                starEffectTime = 0;
            }
        }
        if (ghostmode) {
            leftGhostTime -= delta;
            if (leftGhostTime < 0) {
                ghostmode = false;
                SoundManager.play(MaryoGame.game.assets.get(Assets.SOUND_PLAYER_GHOSTEND, Sound.class));
            }
        }
    }

    private void doExit() {
        //just change level
        String nextLevelName;
        //next level in list
        GameScreen gameScreen = (GameScreen) MaryoGame.game.currentScreen;
        if (TextUtils.isEmpty(exit.levelName) && TextUtils.isEmpty(exit.entry))
            gameScreen.endLevel();

            //go to sublevel
        else {
            //same level
            if (TextUtils.isEmpty(exit.levelName)) {
                LevelEntry entry = MaryoGame.game.currentScreen.world.level.findEntryOrThrow(exit.entry);
                enterLevel(entry);
            } else//another level
            {
                /*if (TextUtils.isEmpty(exit.entry))
                {
                    throw new GdxRuntimeException("Cannot go to sublevel, entry is null");
                }*/
                nextLevelName = exit.levelName;
                GameScreen parent = gameScreen.parent;

                boolean resume = false;
                GameScreen newScreen;
                //we are exiting sublevel
                if (parent != null && parent.levelName.equals(nextLevelName)) {
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
                else {
                    if (parent != null) {
                        parent.dispose();
                    }
                    parent = gameScreen;
                    MaryoGame.game.currentScreen.world.visitedSubLevels.add(nextLevelName);
                    newScreen = new GameScreen(false, nextLevelName, parent);
                    newScreen.entryName = exit.entry;
                }
                MaryoGame.game.changeScreen(new LoadingScreen(newScreen, resume));
            }
        }
    }

    @Override
    protected boolean handleCollision(GameObject object, boolean vertical) {
        if (!handleCollision) return false;
        super.handleCollision(object, vertical);
        if (object instanceof Item) {
            Item item = (Item) object;
            if (!item.playerHit) item.hitPlayer();
            //world.trashObjects.add(item);
        } else if (object instanceof Enemy && ((Enemy) object).handleCollision) {
            boolean deadAnyway = isDeadByJumpingOnTopOfEnemy((Enemy) object);
            if (mInvincibleStar) {
                if (worldState != WorldState.IDLE && worldState != WorldState.DUCKING) {
                    if (((Enemy) object).canBeKilledByStar()) {
                        ((Enemy) object).downgradeOrDie(this, true, true);
                        GameSave.addScore(((Enemy) object).mKillPoints);
                    } else {
                        hitEnemy((Enemy) object, vertical);
                    }
                } else {
                    ((Enemy) object).turn();
                }
            } else if (((Enemy) object).frozen) {
                ((Enemy) object).downgradeOrDie(this, true, false);
                GameSave.addScore(((Enemy) object).mKillPoints);
            } else if (deadAnyway) {
                if (!godMode)
                    downgradeOrDie(false);
            } else {
                hitEnemy((Enemy) object, vertical);


            }
        } else if (object instanceof Box && position.y + colRect.height <= object.position.y) {
            ((Box) object).activate();
        } else if (object instanceof LevelExit && !exiting) {
            if (keys.contains(Keys.LEFT))
                checkLeave("left");
            else if (keys.contains(Keys.RIGHT))
                checkLeave("right");
            else if (keys.contains(Keys.UP))
                checkLeave("up");
            // down nicht beachtet, da der Spieler eh drauf steht
        }
        return false;
    }

    private void hitEnemy(Enemy enemy, boolean vertical) {
        boolean doBounce = false;
        int resolution = enemy.hitByPlayer(this, vertical);
        if (resolution == Enemy.HIT_RESOLUTION_ENEMY_DIED) {
            doBounce = true;
            GameSave.addScore(enemy.mKillPoints);
        } else if (resolution == Enemy.HIT_RESOLUTION_PLAYER_DIED) {
            if (!godMode)
                downgradeOrDie(false);
        } else if (vertical)
            doBounce = true;

        if (doBounce) {
            velocity.y = 5f * Gdx.graphics.getDeltaTime();
            if (keys.contains(Keys.JUMP)) {
                jumpPeakReached = false;
                downPressTime = POWER_JUMP_DELTA + 1;
            }
        }
    }

    private boolean isDeadByJumpingOnTopOfEnemy(Enemy enemy) {
        return !enemy.canBeKilledByJumpingOnTop();
    }

    public WorldState getWorldState() {
        return worldState;
    }

    public void setWorldState(WorldState newWorldState) {
        if (worldState == WorldState.DYING) return;
        this.worldState = newWorldState;
        if (worldState == WorldState.DUCKING) {
            colRect.height = drawRect.height / 2;
        } else {
            colRect.height = drawRect.height * 0.9f;
        }
        if (worldState == WorldState.CLIMBING) {
            exitEnterStartPosition.set(position);
            velocity.x = 0;
            velocity.y = 0;
        }
    }

    @Override
    public float maxVelocity() {
        if (mInvincibleStar)
            return MAX_VEL * STAR_EFFECT_SPEEDFACTOR;
        else
            return MAX_VEL;
    }

    public void downgradeOrDie(boolean forceDie) {
        if (maryoState == MaryoState.small || forceDie) {
            if (mInvincibleStar)
                MusicManager.stop(false);

            worldState = WorldState.DYING;
            GameSave.setMaryoState(MaryoState.small);
            setMarioState(MaryoState.small);
            updateBounds();
            GameSave.setItem(0);
            dyingAnim.start();
        } else {
            SoundManager.play(MaryoGame.game.assets.get(Assets.SOUND_PLAYER_POWERDOWN, Sound.class));
            upgrade(MaryoState.small, null, true);
        }
    }

    /*
     * Level up*/
    public void upgrade(MaryoState newState, Item item, boolean downgrade) {
        //cant upgrade from ice/fire to big
        if (!downgrade && (maryoState == newState && (newState == MaryoState.big || newState == MaryoState.ice || newState == MaryoState.fire))
                || (newState == MaryoState.big && (maryoState == MaryoState.ice || maryoState == MaryoState.fire))) {
            Sound sound = MaryoGame.game.assets.get(Assets.SOUND_ITEMBOX_SET);
            SoundManager.play(sound);
            GameSave.setItem(item.getType());
            return;
        } else if (maryoState == newState) {
            return;
        }
        this.newState = newState;
        oldState = maryoState;
        Array<TextureRegion> frames = generateResizeAnimationFrames(maryoState, newState);
        resizingAnimation = new Animation<>(RESIZE_ANIMATION_FRAME_DURATION, frames);
        resizingAnimation.setPlayMode(Animation.PlayMode.LOOP);
        resizeAnimStartTime = stateTime;
        godMode = true;

        MaryoGame.game.setGameState(GameScreen.GAME_STATE.PLAYER_UPDATING);

        //play new state sound
        Sound sound = upgradeSound(newState, downgrade);
        SoundManager.play(sound);
        fire = false;
    }

    private Sound upgradeSound(MaryoState newState, boolean downgrade) {
        switch (newState) {
            case big:
                return MaryoGame.game.assets.get(Assets.SOUND_ITEM_MUSHROOM);
            case fire:
                return MaryoGame.game.assets.get(Assets.SOUND_ITEM_FIREPLANT);
            case ice:
                return MaryoGame.game.assets.get(Assets.SOUND_ITEM_MUSHROOM_BLUE);
        }
        return null;
    }

    private Array<TextureRegion> generateResizeAnimationFrames(MaryoState stateFrom, MaryoState stateTo) {
        Array<TextureRegion> regions = new Array<>();
        if (worldState.equals(WorldState.WALKING)) {
            regions.add(aMap[aIndex(stateFrom, AnimationKey.walk)].getKeyFrame(stateTime, true));
            regions.add(aMap[aIndex(stateTo, AnimationKey.walk)].getKeyFrame(stateTime, true));
        } else if (worldState == WorldState.DUCKING) {
            regions.add(tMap[tIndex(stateFrom, TextureKey.duck_right)]);
            regions.add(tMap[tIndex(stateTo, TextureKey.duck_right)]);
        } else if (getWorldState().equals(WorldState.JUMPING)) {
            if (velocity.y > 0) {
                regions.add(tMap[tIndex(stateFrom, TextureKey.jump_right)]);
                regions.add(tMap[tIndex(stateTo, TextureKey.jump_right)]);
            } else {
                regions.add(tMap[tIndex(stateFrom, TextureKey.fall_right)]);
                regions.add(tMap[tIndex(stateTo, TextureKey.fall_right)]);
            }
        } else if (worldState == WorldState.DYING) {
            regions.add(tMap[tIndex(stateFrom, TextureKey.dead_right)]);
            regions.add(tMap[tIndex(stateTo, TextureKey.dead_right)]);
        } else {
            regions.add(tMap[tIndex(stateFrom, TextureKey.stand_right)]);
            regions.add(tMap[tIndex(stateTo, TextureKey.stand_right)]);
        }
        return regions;
    }

    public class DyingAnimation {
        private float diedTime;
        boolean upAnimFinished, dyedReset, firstDelayFinished;
        Vector3 diedPosition;
        boolean upBoost;

        public void start() {
            diedTime = stateTime;
            MusicManager.stop(true);
            handleCollision = false;
            diedPosition = new Vector3(position);
            Sound sound = MaryoGame.game.assets.get(Assets.SOUND_PLAYER_DEAD);
            SoundManager.play(sound);
            MaryoGame.game.setGameState(GameScreen.GAME_STATE.PLAYER_DEAD);
        }

        public boolean update(float delta) {
            velocity.x = 0;
            position.x = diedPosition.x;
            if (drawRect.y + drawRect.height < 0)//first check if player is visible
            {
                GameSave.setMaryoState(MaryoState.small);
                MaryoGame.game.setGameState(GameScreen.GAME_STATE.PLAYER_DIED);
                trashThisObject();
                return false;
            }

            if (!firstDelayFinished && stateTime - diedTime < 0.5f)//delay 500ms
            {
                return false;
            } else {
                firstDelayFinished = true;
            }

            if (!upBoost) {
                //animate player up a bit
                velocity.y = 8f;
                upBoost = true;
            }

            return true;
        }
    }

    @Override
    protected boolean handleDroppedBelowWorld() {
        if (!canWalkOnAir) {
            if (worldState != WorldState.DYING) {
                downgradeOrDie(true);
            }
            return true;
        } else {
            return super.handleDroppedBelowWorld();
        }
    }

    private void setJumpSound() {
        switch (maryoState) {
            case small:
                if (powerJump) {
                    jumpSound = MaryoGame.game.assets.get(Assets.SOUND_JUMP_SMALL_POWER);
                } else {
                    jumpSound = MaryoGame.game.assets.get(Assets.SOUND_JUMP_SMALL);
                }
                break;
            case big:
            case fire:
            case ice:
                if (powerJump) {
                    jumpSound = MaryoGame.game.assets.get(Assets.SOUND_JUMP_BIG_POWER);
                } else {
                    jumpSound = MaryoGame.game.assets.get(Assets.SOUND_JUMP_BIG);
                }
                break;
        }
    }

    public MaryoState getMarioState() {
        return maryoState;
    }

    public void setMarioState(MaryoState marioState) {
        this.maryoState = marioState;
        setJumpSound();
    }

    public void checkLevelEnter(String entry_) {
        LevelEntry entry = MaryoGame.game.currentScreen.world.level.findEntry(entry_);
        if (entry != null) {
            if (entry.type == LEVEL_EXIT_BEAM) {
                float entryCenter = entry.colRect.x + entry.colRect.width * 0.5f;
                position.x = colRect.x = entryCenter - colRect.width * 0.5f;
                position.y = colRect.y = entry.colRect.y + entry.colRect.height + colRect.height;
                updateBounds();
            } else {
                enterLevel(entry);
            }
        }
    }

    public void enterLevel(LevelEntry entry) {
        Sound sound = MaryoGame.game.assets.get(Assets.SOUND_ENTER_PIPE);
        SoundManager.play(sound);
        MaryoGame.game.setGameState(GameScreen.GAME_STATE.PLAYER_UPDATING);
        entering = true;
        this.entry = entry;
        if (entry.type == LevelExit.LEVEL_EXIT_WARP) {
            // left
            if ("left".equals(entry.direction)) {
                position.x = colRect.x = entry.colRect.x + entry.colRect.width;

                float entryCenter = entry.colRect.y + entry.colRect.height * 0.5f;
                position.y = colRect.y = entryCenter - colRect.height * 0.5f;
            }
            // right
            else if ("right".equals(entry.direction)) {
                position.x = colRect.x = entry.colRect.x - colRect.width;


                float entryCenter = entry.colRect.y + entry.colRect.height * 0.5f;
                position.y = colRect.y = entryCenter - colRect.height * 0.5f;
            }
            //up
            else if ("up".equals(entry.direction)) {
                position.y = colRect.y = entry.colRect.y - colRect.height;

                float entryCenter = entry.colRect.x + entry.colRect.width * 0.5f;
                position.x = colRect.x = entryCenter - colRect.width * 0.5f;
            }
            // down
            else if ("down".equals(entry.direction)) {
                position.y = colRect.y = entry.colRect.y;

                float entryCenter = entry.colRect.x + entry.colRect.width * 0.5f;
                position.x = colRect.x = entryCenter - colRect.width * 0.5f;
            }
        } else if (entry.type == LEVEL_EXIT_BEAM) {
            float entryCenter = entry.colRect.x + entry.colRect.width * 0.5f;
            position.x = colRect.x = entryCenter - colRect.width * 0.5f;
            position.y = colRect.y = entry.colRect.y + entry.colRect.height + colRect.height;
        }
        updateBounds();
        exitEnterStartPosition.set(position);
        position.z = LevelLoader.m_pos_z_passive_start;
        Collections.sort(MaryoGame.game.currentScreen.world.level.gameObjects, new LevelLoader.ZSpriteComparator());

        //todo sound
    }

    public void exitLevel(LevelExit exit) {
        // already visited sublevels are not allowed
        if (exit.levelName != null && MaryoGame.game.currentScreen.world.visitedSubLevels.contains(exit.levelName, false))
            return;

        switch (exit.type) {
            case LEVEL_EXIT_BEAM:
                if (exiting) return;
                exiting = true;
                this.exit = exit;
                doExit();
                break;
            case LevelExit.LEVEL_EXIT_WARP:
                if (exiting) return;
                Sound sound = MaryoGame.game.assets.get(Assets.SOUND_LEAVE_PIPE);
                SoundManager.play(sound);
                MaryoGame.game.setGameState(GameScreen.GAME_STATE.PLAYER_UPDATING);
                exiting = true;
                this.exit = exit;
                if ("up".equals(exit.direction) || "down".equals(exit.direction)) {
                    float exitCenter = exit.colRect.x + exit.colRect.width * 0.5f;
                    position.x = colRect.x = exitCenter - colRect.width * 0.5f;
                } else {
                    float exitCenter = exit.colRect.y + exit.colRect.height * 0.5f;
                    position.y = colRect.y = exitCenter - colRect.height * 0.5f;
                }
                updateBounds();
                exitEnterStartPosition.set(position);
                position.z = LevelLoader.m_pos_z_passive_start;
                MaryoGame.game.sortLevel();

                //todo sound
                break;
        }
    }

    public void fire() {
        if (worldState == WorldState.DUCKING || !hasFireAbility())
            return;
        if (bulletShotTime < BULLET_COOLDOWN)
            return;
        fire = true;
        doFire();
    }

    public boolean hasFireAbility() {
        return maryoState == MaryoState.fire || maryoState == MaryoState.ice;
    }

    private void doFire() {
        Sound sound = null;
        if (maryoState == MaryoState.fire) {
            addFireball(0f);
            if (mInvincibleStar) {
                addFireball(Fireball.VELOCITY_Y * 0.5f);
            }
            bulletShotTime = 0;
            sound = MaryoGame.game.assets.get(Assets.SOUND_ITEM_FIREBALL);
        } else if (maryoState == MaryoState.ice) {
            addIceball(0f);
            if (mInvincibleStar) {
                addIceball(Fireball.VELOCITY_Y * 0.5f);
            }
            bulletShotTime = 0;
            sound = MaryoGame.game.assets.get(Assets.SOUND_ITEM_FIREBALL);
        }

        SoundManager.play(sound);
    }

    private void addIceball(float velY) {
        Iceball iceball = MaryoGame.ICEBALL_POOL.obtain();
        iceball.colRect.x = iceball.position.x = drawRect.x + drawRect.width * 0.5f;
        iceball.colRect.y = iceball.position.y = drawRect.y + drawRect.height * 0.5f;
        iceball.updateBounds();
        iceball.reset();
        iceball.direction = facingLeft ? Direction.left : Direction.right;
        iceball.velocity.y = velY;
        MaryoGame.game.addObject(iceball);
        MaryoGame.game.sortLevel();
    }

    private void addFireball(float velY) {
        Fireball fireball = MaryoGame.FIREBALL_POOL.obtain();
        fireball.colRect.x = fireball.position.x = drawRect.x + drawRect.width * 0.5f;
        fireball.colRect.y = fireball.position.y = drawRect.y + drawRect.height * 0.5f;
        fireball.updateBounds();
        fireball.reset();
        fireball.direction = facingLeft ? Direction.left : Direction.right;
        fireball.velocity.y = velY;
        MaryoGame.game.addObject(fireball);
        MaryoGame.game.sortLevel();
    }

    public void starPicked() {
        starEffectTime = 0;
        mInvincibleStar = true;
    }

    public void enableGhostMode() {
        leftGhostTime = GHOST_EFFECT_TIMEOUT;
        ghostmode = true;
    }

    public void leftPressed() {
        keys.add(Keys.LEFT);
        //checkLeave("left");
    }

    public void rightPressed() {
        keys.add(Keys.RIGHT);
        //checkLeave("right");
    }

    public void upPressed() {
        keys.add(Keys.UP);
        boolean climbing = false;
        //checkLeave("up");
        //!exiting ist wahrscheinlich unnötig, aber vorher kam dieser Fall hier auch nur vor wenn kein Leave da war
        if (!exiting && getWorldState() != WorldState.CLIMBING) {
            Array<GameObject> vo = MaryoGame.game.currentScreen.world.getVisibleObjects();
            for (int i = 0, size = vo.size; i < size; i++) {
                GameObject go = vo.get(i);
                if (go instanceof Sprite && ((Sprite) go).type == Sprite.Type.climbable && go.colRect.overlaps(colRect)) {
                    climbing = true;
                    break;
                }
            }
            if (climbing) setWorldState(WorldState.CLIMBING);
        }
    }

    private void checkLeave(String dir) {
        Array<GameObject> vo = MaryoGame.game.currentScreen.world.getVisibleObjects();
        //for(GameObject go : world.getVisibleObjects())
        for (int i = 0, size = vo.size; i < size; i++) {
            GameObject go = vo.get(i);
            if (go instanceof LevelExit
                    && go.colRect.overlaps(colRect)
                    && (((LevelExit) go).type == LEVEL_EXIT_BEAM || (((LevelExit) go).type == LevelExit.LEVEL_EXIT_WARP && dir.equals(((LevelExit) go).direction)))) {
                /*String nextLevelName = Level.levels[++GameSaveUtility.getInstance().save.currentLevel];
                world.screen.game.setScreen(new LoadingScreen(new GameScreen(world.screen.game, false, nextLevelName), false));*/
                exitLevel((LevelExit) go);
                return;
            }
        }
    }

    public void downPressed() {
        keys.add(Keys.DOWN);
        checkLeave("down");
    }

    public void jumpPressed() {
        keys.add(Keys.JUMP);
        if (grounded || getWorldState() == WorldState.CLIMBING) {
            setWorldState(WorldState.JUMPING);
            jumpPeakReached = false;

            setJumpSound();
            Sound sound = jumpSound;
            SoundManager.play(sound);
        } else
            jumpPeakReached = true;
    }

    public void firePressed() {
        keys.add(Keys.FIRE);
        fire();
    }

    public void leftReleased() {
        keys.remove(Keys.LEFT);
    }

    public void rightReleased() {
        keys.remove(Keys.RIGHT);
    }

    public void upReleased() {
        keys.remove(Keys.UP);
    }

    public void downReleased() {
        keys.remove(Keys.DOWN);
    }

    public void jumpReleased() {
        keys.remove(Keys.JUMP);
    }

    public void fireReleased() {
        keys.remove(Keys.FIRE);
    }
}
