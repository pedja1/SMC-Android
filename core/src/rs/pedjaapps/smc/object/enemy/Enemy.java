package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;

import rs.pedjaapps.smc.object.DynamicObject;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Maryo;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.LevelLoader;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public abstract class Enemy extends DynamicObject
{
    /**
     * Used with {@link #hitByPlayer(Maryo, boolean)}
     * Player has killed the enemy(or downgraded it/decrease lives count...)*/
    public static final int HIT_RESOLUTION_ENEMY_DIED = 0;

    /**
     * Used with {@link #hitByPlayer(Maryo, boolean)}
     * Enemy has killed the player (or downgraded)*/
    public static final int HIT_RESOLUTION_PLAYER_DIED = 1;

    /**
     * Used with {@link #hitByPlayer(Maryo, boolean)}
     * For example player has picked up the shell of turtle*/
    public static final int HIT_RESOLUTION_CUSTOM = 2;

    public String textureAtlas;
    public String textureName;//name of texture from pack
	protected Direction direction = Direction.right;
    public boolean handleCollision = true;
    boolean deadByBullet;

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
     * @return true if */
    public int hitByPlayer(Maryo maryo, boolean vertical)
    {
        //TODO implement this in subclasses and dont call super
        //TODO this is just here to remove enemies that aren't finished yet
        if (maryo.velocity.y < 0 && vertical && maryo.mColRect.y > mColRect.y)//enemy death from above
        {
            downgradeOrDie(maryo);
            return HIT_RESOLUTION_ENEMY_DIED;
        }
        else
        {
            return HIT_RESOLUTION_PLAYER_DIED;
        }
    }

    public void downgradeOrDie(GameObject killedBy)
    {
        if(killedBy instanceof Turtle || killedBy instanceof Spika)//todo bullet, fireball...
        {
            deadByBullet = true;
            handleCollision = false;
            position.z = 1;
            Collections.sort(world.level.gameObjects, new LevelLoader.ZSpriteComparator());
        }
        else
        {
            handleCollision = false;
            world.trashObjects.add(this);
        }

    }

    public enum Direction
	{
		right, left
	}
	
    enum CLASS
    {
        eato, flyon, furball, turtle, gee, krush, rokko, spika, spikeball, thromp, turtleboss
    }
	
	public enum ContactType
	{
		stopper, player, enemy
	}

    WorldState worldState = WorldState.IDLE;

    protected Enemy(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
    }

    public static Enemy initEnemy(World world, JSONObject jEnemy) throws JSONException
    {
        Vector3 position = new Vector3((float) jEnemy.getDouble(LevelLoader.KEY.posx.toString()), (float) jEnemy.getDouble(LevelLoader.KEY.posy.toString()), 0);
        String enemyClassString = jEnemy.getString(LevelLoader.KEY.enemy_class.toString());
        Vector2 size = new Vector2((float) jEnemy.getDouble(LevelLoader.KEY.width.toString()), (float) jEnemy.getDouble(LevelLoader.KEY.height.toString()));
        CLASS enemyClass = CLASS.valueOf(enemyClassString);
        Enemy enemy = null;
        switch (enemyClass)
        {
            case eato:
                enemy = new Eato(world, size, position);
                break;
            case flyon:
                enemy = new Flyon(world, size, position, (float) jEnemy.getDouble(LevelLoader.KEY.max_distance.toString()), (float) jEnemy.getDouble(LevelLoader.KEY.speed.toString()), jEnemy.optString(LevelLoader.KEY.direction.toString(), "up"));
                break;
			case furball:
                position.z = Furball.POS_Z;
                enemy = new Furball(world, size, position, jEnemy.optInt(LevelLoader.KEY.max_downgrade_count.toString()));
                break;
            case turtle:
                position.z = Turtle.POS_Z;
                enemy = new Turtle(world, size, position, jEnemy.optString(LevelLoader.KEY.color.toString()));
                break;
            case gee:
                enemy = new Gee(world, size, position, (float)jEnemy.getDouble("fly_distance"), jEnemy.getString("color"), jEnemy.getString("direction"), (float)jEnemy.getDouble("wait_time"));
                break;
            case krush:
                enemy = new Krush(world, size, position);
                break;
            case thromp:
                enemy = new Thromp(world, size, position, (float) jEnemy.getDouble(LevelLoader.KEY.max_distance.toString()), (float) jEnemy.getDouble(LevelLoader.KEY.speed.toString()), jEnemy.optString(LevelLoader.KEY.direction.toString(), "up"));
                break;
            case spika:
                enemy = new Spika(world, size, position, jEnemy.optString(LevelLoader.KEY.color.toString()));
                break;
        }
        return enemy;
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        if(deadByBullet)
        {
            TextureRegion frame = getDeadTextureRegion();
            float width = Utility.getWidth(frame, mDrawRect.height);
            float originX = width * 0.5f;
            float originY = mDrawRect.height * 0.5f;
            spriteBatch.draw(frame, mDrawRect.x, mDrawRect.y, originX, originY, width, mDrawRect.height, 1, 1, 180);
        }
        else
        {
            draw(spriteBatch);
        }
    }

    protected abstract TextureRegion getDeadTextureRegion();
    protected abstract void draw(SpriteBatch spriteBatch);
    //protected abstract int getKilledPoints(SpriteBatch spriteBatch);

    @Override
    public void update(float delta)
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
    protected void handleDroppedBelowWorld()
    {
        world.trashObjects.add(this);
    }
}
