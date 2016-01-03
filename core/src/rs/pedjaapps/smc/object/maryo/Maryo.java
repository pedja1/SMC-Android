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
import com.badlogic.gdx.math.Vector3;

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
        UP, DOWN, JUMP
    }

    private static final float MAX_JUMP_SPEED = 9f;

    private boolean jumped;

    static Set<Keys> keys = new HashSet<>(Keys.values().length);

    public static final float STAR_EFFECT_TIMEOUT = 15f;
    public static final float GLIM_COLOR_START_ALPHA = 0f;
    public static final float GLIM_COLOR_MAX_ALPHA = 0.95f;

    //this could be all done dynamically, but this way we minimize allocation in game loop
    //omg, this is a lot of constants :D
    private static final int A_KEY_WALKING_SMALL = 0;

    private static final int T_KEY_DUCK_RIGHT_SMALL = 0;
    private static final int T_KEY_JUMP_RIGHT_SMALL = 1;
    private static final int T_KEY_FALL_RIGHT_SMALL = 2;
    private static final int T_KEY_DEAD_RIGHT_SMALL = 3;
    private static final int T_KEY_STAND_RIGHT_SMALL = 4;

    public static final float POSITION_Z = 0.0999f;

    private static final float RUNNING_FRAME_DURATION = 0.08f;
    private static final float THROW_FRAME_DURATION = 0.1f;
    private static final float RESIZE_ANIMATION_DURATION = 0.977f;
    private static final float RESIZE_ANIMATION_FRAME_DURATION = RESIZE_ANIMATION_DURATION / 8f;

    protected static final float MAX_VEL = 4f;
    private static final float GOD_MOD_TIMEOUT = 3000;//3 sec

    private static final float BULLET_COOLDOWN = 1f;//1 sec

    WorldState worldState = WorldState.JUMPING;
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

    public ParticleEffect starEffect;
    private float bulletShotTime = BULLET_COOLDOWN;

    private boolean mInvincibleStar;
    private final Color glimColor = new Color(0.160784314f, 0.654901961f, 1f, GLIM_COLOR_START_ALPHA);
    private float glimCounter;
    private boolean glimMode = true;
    private float starEffectTime;

    public float startPositionX;

    //textures
    private TextureRegion[] tMap = new TextureRegion[25];
    private Animation walkAnimation;

    public Maryo(World world, Vector3 position, float width, float height)
    {
        super(world, position, width, height);
        mColRect = new Rectangle(mDrawRect);
        setupBoundingBox();

        position.y = mColRect.y = mDrawRect.y += 0.5f;
        velocity.x = 4.5f;
        startPositionX = position.x;
    }

    private void setupBoundingBox()
    {
        float centerX = position.x + mColRect.width / 2;
        mDrawRect.width = 0.9f;
        mDrawRect.height = 0.9f;
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
        TextureAtlas atlas = Assets.manager.get("data/maryo/small.pack");

        TextureRegion tmpStandRight;
        tMap[tIndex(TKey.stand_right)] = tmpStandRight = atlas.findRegion(TKey.stand_right.toString());

        TextureRegion[] walkFrames = new TextureRegion[4];
        walkFrames[0] = tmpStandRight;
        walkFrames[1] = atlas.findRegion("walk_right", 1);
        walkFrames[2] = atlas.findRegion("walk_right", 2);
        walkFrames[3] = walkFrames[1];
        walkAnimation = new Animation(RUNNING_FRAME_DURATION, walkFrames);

        tMap[tIndex(TKey.jump_right)] = atlas.findRegion(TKey.jump_right.toString());
        tMap[tIndex(TKey.fall_right)] = atlas.findRegion(TKey.fall_right.toString());
        tMap[tIndex(TKey.dead_right)] = atlas.findRegion(TKey.dead_right.toString());
        tMap[tIndex( TKey.duck_right)] = atlas.findRegion(TKey.duck_right.toString());

        setJumpSound();
        starEffect = new ParticleEffect(Assets.manager.get("data/animation/particles/maryo_star.p", ParticleEffect.class));

    }

    @Override
    public void dispose()
    {
        //we dont actually ahve to do anything here, since maryo is always present, and no new reources are created
    }

    @Override
    public void _render(SpriteBatch spriteBatch)
    {
        TextureRegion marioFrame;
        if (worldState.equals(WorldState.WALKING))
        {
            marioFrame = walkAnimation.getKeyFrame(stateTime, true);
        }
        else if (worldState == WorldState.DUCKING)
        {
            marioFrame = tMap[tIndex(TKey.duck_right)];
        }
        else if (getWorldState().equals(WorldState.JUMPING))
        {
            if (velocity.y > 0)
            {
                marioFrame = tMap[tIndex(TKey.jump_right)];
            }
            else
            {
                marioFrame = tMap[tIndex(TKey.fall_right)];
            }
        }
        else if (worldState == WorldState.DYING)
        {
            marioFrame = tMap[tIndex(TKey.dead_right)];
        }
        else
        {
            marioFrame = tMap[tIndex(TKey.stand_right)];
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

    private int tIndex(TKey tkey)
    {
        switch (tkey)
        {
            case stand_right:
                return T_KEY_STAND_RIGHT_SMALL;
            case jump_right:
                return T_KEY_JUMP_RIGHT_SMALL;
            case fall_right:
                return T_KEY_FALL_RIGHT_SMALL;
            case dead_right:
                return T_KEY_DEAD_RIGHT_SMALL;
            case duck_right:
                return T_KEY_DUCK_RIGHT_SMALL;
        }
        throw new IllegalArgumentException("Unknown texture key '" + tkey + "'");
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
                    die();
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
                            die();
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

    public void die()
    {
        worldState = WorldState.DYING;
        dyingAnim.start();
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
            die();
        }
        return true;
    }

    private void setJumpSound()
    {
        jumpSound = Assets.manager.get("data/sounds/player/jump_small.mp3");
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
}
