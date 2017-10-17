package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Collections;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.DynamicObject;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.shader.Shader;
import rs.pedjaapps.smc.utility.LevelLoader;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public abstract class Enemy extends DynamicObject
{
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

    public void setDirection(Direction direction)
    {
        this.direction = direction;
    }

    public Direction getDirection()
    {
        return direction;
    }

    /**
     * Called when player has hit an enemy(most likely by jumping on top of it), and should disable/kill it
     *
     * @return true if
     */
    public int hitByPlayer(Maryo maryo, boolean vertical)
    {
        if (maryo.velocity.y < 0 && vertical && maryo.mColRect.y > mColRect.y)//enemy death from above
        {
            downgradeOrDie(maryo, false, false);
            return HIT_RESOLUTION_ENEMY_DIED;
        }
        else
        {
            return HIT_RESOLUTION_PLAYER_DIED;
        }
    }

    public void downgradeOrDie(GameObject killedBy, boolean forceBulletKill, boolean isStarKill)
    {
        if (forceBulletKill || killedBy.isBullet())
        {
            deadByBullet = true;
            handleCollision = false;
            position.z = 1;
            Collections.sort(world.level.gameObjects, new LevelLoader.ZSpriteComparator());
            playDeadSound(isStarKill);
        }
        else
        {
            handleCollision = false;
            world.trashObjects.add(this);
        }
        if(mKillPoints > 0)((GameScreen)world.screen).killPointsTextHandler.add(mKillPoints, position.x, position.y + mDrawRect.height);
    }

    protected String getDeadSound()
    {
        return Assets.SOUND_ENEMY_DIE_FURBALL;
    }

    private enum CLASS
    {
        eato, flyon, furball, turtle, gee, krush, rokko, spika, spikeball, thromp, turtleboss, _static("static");

        String mValue;

        CLASS(String mValue)
        {
            this.mValue = mValue;
        }

        CLASS()
        {
        }

        public static CLASS fromString(String string)
        {
            for (CLASS cls : values())
            {
                if (cls.toString().equals(string))
                    return cls;
            }
            throw new GdxRuntimeException("Unknown enemy class: '" + string + "'");
        }


        @Override
        public String toString()
        {
            return mValue == null ? super.toString() : mValue;
        }
    }

    public enum ContactType
    {
        stopper, player, enemy
    }

    protected Enemy(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        world.screen.game.assets.manager.load(getDeadSound(), Sound.class);
        world.screen.game.assets.manager.load(Assets.SOUND_ITEM_ICE_KILL, Sound.class);
    }

    protected void playDeadSound(boolean starKill)
    {
        AssetManager manager = world.screen.game.assets.manager;
        String soundKey = starKill ? Assets.SOUND_ITEM_STAR_KILL :
                frozen ? Assets.SOUND_ITEM_ICE_KILL : getDeadSound();
        if(manager.isLoaded(soundKey))
        {
            Sound sound = manager.get(soundKey);
            SoundManager.play(sound);
        }
    }

    public static Enemy initEnemy(World world, JsonValue jEnemy)
    {
        Vector3 position = new Vector3(jEnemy.getFloat("posx"), jEnemy.getFloat("posy"), 0);
        String enemyClassString = jEnemy.getString("enemy_class");
        Vector2 size = new Vector2(jEnemy.getFloat("width"), jEnemy.getFloat("height"));
        CLASS enemyClass = CLASS.fromString(enemyClassString);
        Enemy enemy = null;
        switch (enemyClass)
        {
            case eato:
                enemy = new Eato(world, size, position, jEnemy.getString("direction", ""));
                break;
            case flyon:
                enemy = new Flyon(world, size, position, jEnemy.getFloat("max_distance"), jEnemy.getFloat("speed"), jEnemy.getString("direction", "up"));
                break;
            case furball:
                position.z = Furball.POS_Z;
                enemy = new Furball(world, size, position, jEnemy.getInt("max_downgrade_count", 0));
                break;
            case turtle:
                position.z = Turtle.POS_Z;
                enemy = new Turtle(world, size, position, jEnemy.getString("color", ""));
                break;
            case gee:
                enemy = new Gee(world, size, position, jEnemy.getFloat("fly_distance"), jEnemy.getString("color"), jEnemy.getString("direction"), jEnemy.getFloat("wait_time"));
                break;
            case krush:
                enemy = new Krush(world, size, position);
                break;
            case thromp:
                enemy = new Thromp(world, size, position, jEnemy.getFloat("max_distance"), jEnemy.getFloat("speed"), jEnemy.getString("direction", "up"));
                break;
            case spika:
                enemy = new Spika(world, size, position, jEnemy.getString("color", ""));
                break;
            case rokko:
                enemy = new Rokko(world, size, position, jEnemy.getString("direction", ""));
                world.screen.game.assets.manager.load(Assets.SOUND_ENEMY_ROKKO_HIT, Sound.class);
                break;
            case _static:
                enemy = new Static(world, size, position, jEnemy.getInt("rotation_speed", 0), jEnemy.getInt("fire_resistance", 0), jEnemy.getInt("ice_resistance", 0));
                break;
            case spikeball:
                enemy = new Spikeball(world, size, position);
                break;
        }
        return enemy;
    }

    @Override
    public final void _render(SpriteBatch spriteBatch)
    {
        if (frozen)
        {
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
        }
        else
        {
            doRender(spriteBatch);
        }
    }

    private void doRender(SpriteBatch batch)
    {
        if (deadByBullet)
        {
            TextureRegion frame = getDeadTextureRegion();
            float width = Utility.getWidth(frame, mDrawRect.height);
            float originX = width * 0.5f;
            float originY = mDrawRect.height * 0.5f;
            batch.draw(frame, mDrawRect.x, mDrawRect.y, originX, originY, width, mDrawRect.height, 1, 1, 180);
        }
        else
        {
            render(batch);
        }
    }

    protected abstract TextureRegion getDeadTextureRegion();

    protected abstract void render(SpriteBatch spriteBatch);
    //protected abstract int getKilledPoints(SpriteBatch spriteBatch);
    public abstract boolean canBeKilledByJumpingOnTop();

    @Override
    public final void _update(float delta)
    {
        if (frozen)
        {
            velocity.x *= velocityDump;

            if(!deadByBullet)freezeCounter -= delta;
            if (freezeCounter <= 0)
            {
                frozen = false;
                freezeCounter = freezeCounterMax = 0;
                velocityDump = DEF_VEL_DUMP;
                frozenColor.a = FROZEN_COLOR_START_ALPHA;
            }
            if(deadByBullet)update(delta);
        }
        else
        {
            update(delta);
        }
        turned = false;
    }

    protected void update(float delta)
    {
        stateTime += delta;
    }


    public void handleCollision(ContactType ContactType)
    {
        // subclasses should implement this
    }

    @Override
    public float maxVelocity()
    {
        return DEF_MAX_VEL;
    }

    @Override
    protected boolean handleDroppedBelowWorld()
    {
        world.trashObjects.add(this);
        return true;
    }

    public void freeze()
    {
        frozen = true;
        frozenColor.a = FROZEN_COLOR_START_ALPHA;
        freezeCounter = FREEZE_TIMER_DEF;
        if (mIceResistance > 0.0f)
        {
            freezeCounter *= (mIceResistance * -1) + 1;
        }
        freezeCounterMax = freezeCounter;
        velocityDump = 0.95f;
        velocity.y = 0;
    }

    public void turn()
    {
        if(turned)return;
        direction = direction == Direction.right ? Direction.left : Direction.right;
        turnStartTime = stateTime;
        turn = true;
        velocity.x = velocity.x > 0 ? -velocity.x : Math.abs(velocity.x);
        turned = true;
    }

    @Override
    protected boolean handleLevelEdge()
    {
        turn();
        return false;
    }

    @Override
    public boolean isBullet()
    {
        return false;
    }

    public boolean canBeKilledByStar()
    {
        return true;
    }
}
