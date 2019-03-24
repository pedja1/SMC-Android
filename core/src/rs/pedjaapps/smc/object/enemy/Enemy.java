package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.DynamicObject;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.shader.Shader;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public abstract class Enemy extends DynamicObject {
    /**
     * Used with {@link #hitByPlayer(Maryo, boolean)}
     * Player has killed the enemy(or downgraded it/decrease lives count...)
     */
    public static final int HIT_RESOLUTION_ENEMY_DIED = 0;

    /**
     * Used with {@link #hitByPlayer(Maryo, boolean)}
     * Enemy has killed the player (or downgraded)
     */
    public static final int HIT_RESOLUTION_PLAYER_DIED = 1;

    /**
     * Used with {@link #hitByPlayer(Maryo, boolean)}
     * For example player has picked up the shell of turtle
     */
    public static final int HIT_RESOLUTION_CUSTOM = 2;

    private static final float FROZEN_COLOR_START_ALPHA = .99f;
    private static final float FREEZE_TIMER_DEF = 10;

    public String textureAtlas;
    public String textureName;//name of texture from pack
    protected Direction direction = Direction.right;
    public boolean handleCollision = true;
    boolean deadByBullet;
    public int mKillPoints;

    public int mFireResistant;
    public float mIceResistance;
    public float mSpeed, mRotationX, mRotationY, mRotationZ, mCanBeHitFromShell;

    public boolean frozen;
    private final Color frozenColor = new Color(0.160784314f, 0.654901961f, 1f, FROZEN_COLOR_START_ALPHA);
    private float freezeCounter, freezeCounterMax;

    protected boolean turn;
    protected float turnStartTime;

    protected boolean turned = false;

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    /**
     * Called when player has hit an enemy(most likely by jumping on top of it), and should disable/kill it
     *
     * @return true if
     */
    public int hitByPlayer(Maryo maryo, boolean vertical) {
        if (maryo.velocity.y < 0 && vertical && maryo.colRect.y > colRect.y)//enemy death from above
        {
            downgradeOrDie(maryo, false, false);
            return HIT_RESOLUTION_ENEMY_DIED;
        } else {
            return HIT_RESOLUTION_PLAYER_DIED;
        }
    }

    public void downgradeOrDie(GameObject killedBy, boolean forceBulletKill, boolean isStarKill) {
        if (forceBulletKill || killedBy.isBullet()) {
            deadByBullet = true;
            handleCollision = false;
            position.z = 1;
            MaryoGame.game.sortLevel();
            playDeadSound(isStarKill);
        } else {
            handleCollision = false;
            MaryoGame.game.trashObject(this);
        }
        if (mKillPoints > 0) {
            MaryoGame.game.addKillPoints(mKillPoints, position.x, position.y + drawRect.height);
        }
    }

    protected String getDeadSound() {
        return Assets.SOUND_ENEMY_DIE_FURBALL;
    }


    protected Enemy(float x, float y, float z, float width, float height) {
        super(x, y, z, width, height);
        MaryoGame.game.assets.load(getDeadSound(), Sound.class);
        MaryoGame.game.assets.load(Assets.SOUND_ITEM_ICE_KILL, Sound.class);
    }

    protected void playDeadSound(boolean starKill) {
        String soundKey = starKill ? Assets.SOUND_ITEM_STAR_KILL :
                frozen ? Assets.SOUND_ITEM_ICE_KILL : getDeadSound();
        if (MaryoGame.game.assets.isLoaded(soundKey)) {
            Sound sound = MaryoGame.game.assets.get(soundKey);
            SoundManager.play(sound);
        }
    }

    @Override
    public final void render(SpriteBatch spriteBatch) {
        if (frozen) {
            //set shader
            float brightness = .5f / (freezeCounterMax / freezeCounter);
            spriteBatch.setShader(Shader.FREEZE_SHADER);
            spriteBatch.getShader().setUniformf("u_contrast", 1.1f);
            spriteBatch.getShader().setUniformf("u_brightness", brightness);

            //set color
            frozenColor.a = FROZEN_COLOR_START_ALPHA / (freezeCounterMax / freezeCounter);
            spriteBatch.setColor(frozenColor);

            //render
            doRender(spriteBatch);

            //reset shader
            spriteBatch.setShader(null);

            //reset color
            spriteBatch.setColor(Color.WHITE);
        } else {
            doRender(spriteBatch);
        }
    }

    private void doRender(SpriteBatch batch) {
        if (deadByBullet) {
            TextureRegion frame = getDeadTextureRegion();
            float width = Utility.getWidth(frame, drawRect.height);
            float originX = width * 0.5f;
            float originY = drawRect.height * 0.5f;
            batch.draw(frame, drawRect.x, drawRect.y, originX, originY, width, drawRect.height, 1, 1, 180);
        } else {
            _render(batch);
        }
    }

    protected abstract TextureRegion getDeadTextureRegion();

    protected abstract void _render(SpriteBatch spriteBatch);

    //protected abstract int getKilledPoints(SpriteBatch spriteBatch);
    public abstract boolean canBeKilledByJumpingOnTop();

    @Override
    public final void update(float delta) {
        if (frozen) {
            velocity.x *= velocityDump;

            if (!deadByBullet) freezeCounter -= delta;
            if (freezeCounter <= 0) {
                frozen = false;
                freezeCounter = freezeCounterMax = 0;
                velocityDump = DEF_VEL_DUMP;
                frozenColor.a = FROZEN_COLOR_START_ALPHA;
            }
            if (deadByBullet) _update(delta);
        } else {
            _update(delta);
        }
        turned = false;
    }

    protected void _update(float delta) {
        stateTime += delta;
    }


    public void handleCollision(ContactType ContactType) {
        // subclasses should implement this
    }

    @Override
    public float maxVelocity() {
        return DEF_MAX_VEL;
    }

    @Override
    protected boolean handleDroppedBelowWorld() {
        MaryoGame.game.trashObject(this);
        return true;
    }

    public void freeze() {
        frozen = true;
        frozenColor.a = FROZEN_COLOR_START_ALPHA;
        freezeCounter = FREEZE_TIMER_DEF;
        if (mIceResistance > 0.0f) {
            freezeCounter *= (mIceResistance * -1) + 1;
        }
        freezeCounterMax = freezeCounter;
        velocityDump = 0.95f;
        velocity.y = 0;
    }

    public void turn() {
        if (turned) return;
        direction = direction == Direction.right ? Direction.left : Direction.right;
        turnStartTime = stateTime;
        turn = true;
        velocity.x = velocity.x > 0 ? -velocity.x : Math.abs(velocity.x);
        turned = true;
    }

    @Override
    protected boolean handleLevelEdge() {
        turn();
        return false;
    }

    @Override
    public boolean isBullet() {
        return false;
    }

    public boolean canBeKilledByStar() {
        return true;
    }
}
