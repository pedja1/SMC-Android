package rs.pedjaapps.smc.object;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.HashSet;
import java.util.Set;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.MusicManager;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.enemy.Enemy;
import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.shader.Shader;
import rs.pedjaapps.smc.utility.Utility;

public class Player extends DynamicObject
{
    private static final float IDLE_ANIMATION_KEY_FRAME_DURATION = 0.5f;
    private static final float DEAD_ANIMATION_KEY_FRAME_DURATION = 0.5f;
    private static final float JUMP_ANIMATION_KEY_FRAME_DURATION = 0.5f;
    private static final float RUN_ANIMATION_KEY_FRAME_DURATION = 0.08f;
    private static final float SLIDE_ANIMATION_KEY_FRAME_DURATION = 0.5f;

    private enum PressedKey
    {
        UP, DOWN, JUMP
    }

    private static final float MAX_JUMP_SPEED = 9f;

    private boolean jumped;

    private static Set<PressedKey> PRESSED_KEYS = new HashSet<>(PressedKey.values().length);

    private static final float INVINCIBLE_EFFECT_TIMEOUT = 15f;
    private static final float GLIM_COLOR_START_ALPHA = 0f;
    private static final float GLIM_COLOR_MAX_ALPHA = 0.95f;

    public static final float POSITION_Z = 0.0999f;

    private static final float MAX_VEL = 4f;

    private WorldState worldState = WorldState.jumping;

    private boolean handleCollision = true;

    public Rectangle debugRayRect = new Rectangle();

    private boolean mInvincible;
    private float mInvincibleStartTime;
    private final Color glimColor = new Color(0.160784314f, 0.654901961f, 1f, GLIM_COLOR_START_ALPHA);
    private float glimCounter;
    private boolean glimMode = true;

    public float startPositionX;

    //assets, loaded only once
    private static Animation animationIdle, animationRunning, animationJumping, animationDying, animationSliding;
    private static Sound jumpSound = null;

    public Player(float x, float y, float width, float height)
    {
        super(x, y, width, height);

        velocity.x = 4.5f;
        startPositionX = position.x;
    }

    public void initAssets()
    {
        TextureAtlas atlas = Assets.manager.get(Assets.DEFAULT_ATLAS);

        if (animationIdle == null)
        {
            //init idle animation
            TextureRegion[] regions = new TextureRegion[10];
            regions[0] = atlas.findRegion("character/idle-01");
            regions[1] = atlas.findRegion("character/idle-02");
            regions[2] = atlas.findRegion("character/idle-03");
            regions[3] = atlas.findRegion("character/idle-04");
            regions[4] = atlas.findRegion("character/idle-05");
            regions[5] = atlas.findRegion("character/idle-06");
            regions[6] = atlas.findRegion("character/idle-07");
            regions[7] = atlas.findRegion("character/idle-08");
            regions[8] = atlas.findRegion("character/idle-09");
            regions[9] = atlas.findRegion("character/idle-10");
            animationIdle = new Animation(IDLE_ANIMATION_KEY_FRAME_DURATION, regions);
        }

        if (animationDying == null)
        {
            //init dying animation
            TextureRegion[] regions = new TextureRegion[10];
            regions[0] = atlas.findRegion("character/dead-01");
            regions[1] = atlas.findRegion("character/dead-02");
            regions[2] = atlas.findRegion("character/dead-03");
            regions[3] = atlas.findRegion("character/dead-04");
            regions[4] = atlas.findRegion("character/dead-05");
            regions[5] = atlas.findRegion("character/dead-06");
            regions[6] = atlas.findRegion("character/dead-07");
            regions[7] = atlas.findRegion("character/dead-08");
            regions[8] = atlas.findRegion("character/dead-09");
            regions[9] = atlas.findRegion("character/dead-10");
            animationDying = new Animation(DEAD_ANIMATION_KEY_FRAME_DURATION, regions);
        }

        if (animationJumping == null)
        {
            //init jump animation
            TextureRegion[] regions = new TextureRegion[10];
            regions[0] = atlas.findRegion("character/jump-01");
            regions[1] = atlas.findRegion("character/jump-02");
            regions[2] = atlas.findRegion("character/jump-03");
            regions[3] = atlas.findRegion("character/jump-04");
            regions[4] = atlas.findRegion("character/jump-05");
            regions[5] = atlas.findRegion("character/jump-06");
            regions[6] = atlas.findRegion("character/jump-07");
            regions[7] = atlas.findRegion("character/jump-08");
            regions[8] = atlas.findRegion("character/jump-09");
            regions[9] = atlas.findRegion("character/jump-10");
            animationJumping = new Animation(JUMP_ANIMATION_KEY_FRAME_DURATION, regions);
        }

        if (animationRunning == null)
        {
            //init running animation
            TextureRegion[] regions = new TextureRegion[8];
            regions[0] = atlas.findRegion("character/run-01");
            regions[1] = atlas.findRegion("character/run-02");
            regions[2] = atlas.findRegion("character/run-03");
            regions[3] = atlas.findRegion("character/run-04");
            regions[4] = atlas.findRegion("character/run-05");
            regions[5] = atlas.findRegion("character/run-06");
            regions[6] = atlas.findRegion("character/run-07");
            regions[7] = atlas.findRegion("character/run-08");
            animationRunning = new Animation(RUN_ANIMATION_KEY_FRAME_DURATION, regions);
        }

        if (animationSliding == null)
        {
            //init sliding animation
            TextureRegion[] regions = new TextureRegion[5];
            regions[0] = atlas.findRegion("character/slide-01");
            regions[1] = atlas.findRegion("character/slide-02");
            regions[2] = atlas.findRegion("character/slide-03");
            regions[3] = atlas.findRegion("character/slide-04");
            regions[4] = atlas.findRegion("character/slide-05");
            animationSliding = new Animation(SLIDE_ANIMATION_KEY_FRAME_DURATION, regions);
        }

        if (jumpSound == null)
        {
            jumpSound = Assets.manager.get("data/sounds/player/jump.mp3");
        }

    }

    @Override
    public void dispose()
    {
        //we dont actually ahve to do anything here, since maryo is always present, and no new reources are created
    }

    @Override
    protected void _render(SpriteBatch spriteBatch)
    {
        TextureRegion marioFrame;
        if (worldState.equals(WorldState.running))
        {
            marioFrame = animationRunning.getKeyFrame(stateTime, true);
        }
        else if (getWorldState().equals(WorldState.jumping))
        {
            if (velocity.y > 0)
            {
                marioFrame = animationJumping.getKeyFrame(stateTime, false);
            }
            else
            {
                //for falling show last frame
                marioFrame = animationJumping.getKeyFrames()[animationRunning.getKeyFrames().length - 1];
            }
        }
        else if (worldState == WorldState.dying)
        {
            marioFrame = animationDying.getKeyFrame(stateTime, false);
        }
        else if (worldState == WorldState.sliding)
        {
            marioFrame = animationSliding.getKeyFrame(stateTime, false);
        }
        else //if (worldState == WorldState.idle)
        {
            marioFrame = animationIdle.getKeyFrame(stateTime, true);
        }

        if (mInvincible)
        {
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
        Utility.draw(spriteBatch, marioFrame, bounds.x, bounds.y, bounds.height);
        spriteBatch.setShader(null);
        spriteBatch.setColor(Color.WHITE);
    }

    @Override
    protected void _update(float delta)
    {
        if (((GameScreen) World.getInstance().screen).getGameState() == GameScreen.GAME_STATE.GAME_RUNNING)
        {
            grounded = position.y - groundY < 0.1f;
            if (!grounded)
            {
                setWorldState(Player.WorldState.jumping);
            }
            if (PRESSED_KEYS.contains(PressedKey.JUMP))
            {
                if (!jumped && velocity.y < MAX_JUMP_SPEED)
                {
                    velocity.add(0, 120f * delta);
                }
                else
                {
                    jumped = true;
                }
            }

            {
                if (!PRESSED_KEYS.contains(PressedKey.DOWN))
                {
                    if (getWorldState() != WorldState.jumping)
                    {
                        setWorldState(WorldState.running);
                    }
                }
            }
            if (grounded && getWorldState().equals(Player.WorldState.jumping))
            {
                setWorldState(WorldState.running);
            }
        }

        if (worldState == WorldState.dying)
        {
            stateTime += delta;
        }
        else
        {
            {
                super._update(delta);
                if (closestObject != null)
                {
                    debugRayRect.set(position.x, closestObject.bounds.y + closestObject.bounds.height, collider.width, position.y - (closestObject.bounds.y + closestObject.bounds.height));
                }

                if (closestObject != null
                        && closestObject instanceof Sprite
                        && ((Sprite) closestObject).type == Sprite.Type.halfmassive
                        && PRESSED_KEYS.contains(PressedKey.DOWN))
                {
                    position.y -= 0.1f;
                }
            }
        }

        if (mInvincible)
        {
            if (glimMode)
            {
                glimCounter += (delta * 8f);
            }
            else
            {
                glimCounter -= (delta * 6f);
            }
            if (mInvincibleStartTime >= INVINCIBLE_EFFECT_TIMEOUT)
            {
                mInvincible = false;
                mInvincibleStartTime = 0;
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
            if (!mInvincible)
            {
                die();
            }
        }
        return false;
    }

    private WorldState getWorldState()
    {
        return worldState;
    }

    private void setWorldState(WorldState newWorldState)
    {
        if (worldState == WorldState.dying) return;
        this.worldState = newWorldState;
    }

    @Override
    public float maxVelocity()
    {
        return MAX_VEL;
    }

    public void die()
    {
        worldState = WorldState.dying;
    }

    @Override
    protected boolean handleDroppedBelowWorld()
    {
        if (worldState != WorldState.dying)
        {
            die();
        }
        return true;
    }

    public void starPicked()
    {
        mInvincible = true;
    }

    public void upPressed()
    {
        PRESSED_KEYS.add(PressedKey.UP);
    }

    public void downPressed()
    {
        PRESSED_KEYS.add(PressedKey.DOWN);
    }

    public void jumpPressed()
    {
        if (grounded)
        {
            setWorldState(GameObject.WorldState.jumping);
            PRESSED_KEYS.add(PressedKey.JUMP);

            Sound sound = jumpSound;
            SoundManager.play(sound);
        }
    }

    public void upReleased()
    {
        PRESSED_KEYS.remove(PressedKey.UP);
    }

    public void downReleased()
    {
        PRESSED_KEYS.remove(PressedKey.DOWN);
    }

    public void jumpReleased()
    {
        PRESSED_KEYS.remove(PressedKey.JUMP);
        jumped = false;
    }
}
