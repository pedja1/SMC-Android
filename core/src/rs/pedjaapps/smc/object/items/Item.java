package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import rs.pedjaapps.smc.object.Box;
import rs.pedjaapps.smc.object.DynamicObject;
import rs.pedjaapps.smc.object.WorldState;
import rs.pedjaapps.smc.object.items.mushroom.Mushroom;
import rs.pedjaapps.smc.object.items.mushroom.MushroomBlue;
import rs.pedjaapps.smc.object.items.mushroom.MushroomDefault;

/**
 * Created by pedja on 24.5.14..
 */
public abstract class Item extends DynamicObject {
    //item types
    public static final int
            TYPE_POWERUP = 23,
            TYPE_MUSHROOM_DEFAULT = 25,
            TYPE_MUSHROOM_LIVE_1 = 35,
            TYPE_MUSHROOM_POISON = 49,
            TYPE_MUSHROOM_BLUE = 51,
            TYPE_MUSHROOM_GHOST = 52,
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

    public enum CLASS {
        goldpiece, moon, jstar, mushroom, fireplant
    }

    WorldState worldState = WorldState.IDLE;
    //is drawn
    public boolean visible = true;

    protected float originalPosY;
    //collectible by player
    public boolean collectible = true;
    public TextureRegion texture;

    /**
     * Coin will smoothly pop out of the box
     */
    public boolean popFromBox;
    private boolean dropping;
    public boolean isInBox;

    public Item(float x, float y, float z, float width, float height) {
        super(x, y, z, width, height);
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

    public static String getSavedItemTextureName(int itemType) {
        switch (itemType) {
            case TYPE_MUSHROOM_BLUE:
                return MushroomBlue.TEXTURE_NAME;
            case TYPE_MUSHROOM_DEFAULT:
                return MushroomDefault.TEXTURE_NAME;
            case TYPE_FIREPLANT:
                return "game_items_fireplant";
            default:
                return null;
        }

    }

    public static Item createObject(int itemType, String objectClassString, float x, float y, float z, float width, float height) {
        CLASS itemClass = CLASS.valueOf(objectClassString);
        Item object = null;
        switch (itemClass) {
            case goldpiece:
                object = new Coin(x, y, z, width, height, itemType);
                break;
            case moon:
                Box.createMoon();//load assets
                object = Box.createMoon(x, y, z, false, false);
                break;
            case jstar:
                Box.createStar();//load assets
                object = Box.createStar(x, y, z, false, false);
                ((Star) object).moving = true;
                break;
            case mushroom:
                Box.createMushroom(itemType);//load assets
                object = Box.createMushroom(x, y, z, itemType, false, false);
                ((Mushroom) object).moving = true;
                break;
            case fireplant:
                Box.createFireplant();//load assets
                object = Box.createFireplant(x, y, z);
                break;
        }
        if (object != null)
            object.visible = true;
        return object;
    }

    @Override
    public final void update(float delta) {
        if (dropping) {
            stateTime += delta;
            velocity.y = -1f;
            velocity.x = 0;

            velocity.scl(delta);

            position.add(velocity);
            colRect.x = position.x;
            colRect.y = position.y;
            updateBounds();

            velocity.scl(1 / delta);
        } else {
            updateItem(delta);
        }
    }

    public void popOutFromBox(float popTargetPosY) {
        this.popTargetPosY = popTargetPosY;
    }

    public abstract void hitPlayer();

    public void updateItem(float delta) {
        stateTime += delta;
    }

    public void drop() {
        dropping = true;
        playerHit = false;
    }

    @Override
    public void dispose() {

    }
}
