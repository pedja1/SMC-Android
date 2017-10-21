package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.Box;
import rs.pedjaapps.smc.object.DynamicObject;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.items.mushroom.Mushroom;

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
    public enum CLASS
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
        ppEnabled = false;
    }

    public abstract int getType();

    public static String getClassFromItemType(int itemType) {
        switch (itemType) {
            case TYPE_MUSHROOM_BLUE:
            case TYPE_MUSHROOM_DEFAULT:
            case TYPE_MUSHROOM_POISON:
            case TYPE_MUSHROOM_LIVE_1:
                return CLASS.mushroom.toString();
            case TYPE_GOLDPIECE:
                return CLASS.goldpiece.toString();
            case TYPE_STAR:
                return CLASS.jstar.toString();
            case TYPE_MOON:
                return CLASS.moon.toString();
            case TYPE_FIREPLANT:
                return CLASS.fireplant.toString();
            default:
                return null;
        }
    }

    public static Item createObject(World world, Assets assets, int itemType, String objectClassString, Vector2 size, Vector3 position)
    {
        CLASS itemClass = CLASS.valueOf(objectClassString);
        Item object = null;
        switch (itemClass)
        {
            case goldpiece:
                object = new Coin(world, size, position, itemType);
                break;
            case moon:
                Box.createMoon(assets);//load assets
                object = Box.createMoon(world, position, false, assets, false);
                break;
            case jstar:
                Box.createStar(assets);//load assets
                object = Box.createStar(world, position, false, assets, false);
                ((Star)object).moving = true;
                break;
            case mushroom:
                Box.createMushroom(assets, itemType);//load assets
                object = Box.createMushroom(world, position, itemType, false, assets, false);
                ((Mushroom)object).moving = true;
                break;
            case fireplant:
                Box.createFireplant(assets);//load assets
                object = Box.createFireplant(world, position);
                break;
        }
        if(object != null)
            object.visible = true;
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
