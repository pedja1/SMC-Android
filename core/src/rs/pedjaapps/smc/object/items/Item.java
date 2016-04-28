package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.object.DynamicObject;
import rs.pedjaapps.smc.object.World;

/**
 * Created by pedja on 24.5.14..
 */
public abstract class Item extends DynamicObject
{
	//item types
	public static final int
	TYPE_POWERUP = 23,
	TYPE_MUSHROOM_DEFAULT = 25,
	TYPE_MUSHROOM_LIVE_1 = 35,
	TYPE_MUSHROOM_POISON = 49,
	TYPE_MUSHROOM_BLUE = 51,
	TYPE_FIREPLANT = 24,
	TYPE_JUMPING_GOLDPIECE = 22,
	TYPE_FALLING_GOLDPIECE = 48,
	TYPE_GOLDPIECE = 8,
	TYPE_MOON = 37,
	TYPE_STAR = 39;

    public String textureName, textureAtlas;

    protected float popTargetPosY;
    public boolean playerHit;
    protected float stateTime;
    enum CLASS
    {
        goldpiece, moon, jstar, mushroom, fireplant
    }
    WorldState worldState = WorldState.IDLE;
    //is drawn
    public boolean visible = true;

    protected float originalPosY;
    //collectible by player
    public boolean collectible = true;
    public Texture texture;

    /**
     * Coin will smoothly pop out of the box*/
    public boolean popFromBox;
    private boolean dropping;
    public boolean isInBox;

    public Item(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        position.z = 0.05f;
    }

    public static Item initObject(World world, String objectClassString, Vector2 size, Vector3 position)
    {
        CLASS itemClass = CLASS.valueOf(objectClassString);
        Item object = null;
        switch (itemClass)
        {
            case goldpiece:
                object = new Coin(world, size, position);
                break;
            case mushroom:
                break;
        }
        return object;
    }

    @Override
    public final void _update(float delta)
    {
        if(dropping)
        {
            stateTime += delta;
            velocity.y = -1f;
            velocity.x = 0;

            velocity.scl(delta);

            position.add(velocity);
            mColRect.x = position.x;
            mColRect.y = position.y;
            updateBounds();

            velocity.scl(1 / delta);
        }
        else
        {
            updateItem(delta);
        }
    }
	
	public void popOutFromBox(float popTargetPosY)
	{
		this.popTargetPosY = popTargetPosY;
	}

    public abstract void hitPlayer();

    public void updateItem(float delta)
    {
        stateTime += delta;
    }

    public void drop()
    {
        dropping = true;
        playerHit = false;
    }

    @Override
    public void dispose()
    {
        if(texture != null)texture.dispose();
    }
}
