package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import rs.pedjaapps.smc.object.DynamicObject;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Maryo;
import rs.pedjaapps.smc.object.World;

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
        if (maryo.velocity.y < 0 && vertical && maryo.body.y > body.y)//enemy death from above
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
        //TODO implement in subclasses and add animation...
        handleCollision = false;
        world.trashObjects.add(this);
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

    public static Enemy initEnemy(World world, String enemyClassString, Vector2 size, Vector3 position, int maxDowngradeCount, String color)
    {
        CLASS enemyClass = CLASS.valueOf(enemyClassString);
        Enemy enemy = null;
        switch (enemyClass)
        {
            case eato:
                enemy = new Eato(world, size, position);
                break;
            case flyon:
                enemy = new Flyon(world, size, position);
                break;
			case furball:
                position.z = Furball.POS_Z;
                enemy = new Furball(world, size, position, maxDowngradeCount);
                break;
            case turtle:
                position.z = Turtle.POS_Z;
                enemy = new Turtle(world, size, position, color);
                break;
        }
        return enemy;
    }

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
}
