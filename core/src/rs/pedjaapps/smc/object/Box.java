package rs.pedjaapps.smc.object;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import org.json.JSONException;
import org.json.JSONObject;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.object.items.Coin;
import rs.pedjaapps.smc.object.items.Fireplant;
import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.object.items.Moon;
import rs.pedjaapps.smc.object.items.mushroom.Mushroom;
import rs.pedjaapps.smc.object.items.mushroom.MushroomBlue;
import rs.pedjaapps.smc.object.items.mushroom.MushroomDefault;
import rs.pedjaapps.smc.object.items.mushroom.MushroomGhost;
import rs.pedjaapps.smc.object.items.mushroom.MushroomLive1;
import rs.pedjaapps.smc.object.items.mushroom.MushroomPoison;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.GameSaveUtility;
import rs.pedjaapps.smc.utility.LevelLoader;
import rs.pedjaapps.smc.utility.Utility;

public class Box extends Sprite
{
    //visibility type
    public static final int
            // always visible
            BOX_VISIBLE = 0,
    // visible after activation
    BOX_INVISIBLE_MASSIVE = 1,
    // only visible in ghost mode
    BOX_GHOST = 2,
    // visible after activation and only touchable in the activation direction
    BOX_INVISIBLE_SEMI_MASSIVE = 3;


    public static final float SIZE = 0.67f;
    String goldColor, animation, boxType, text;
    boolean forceBestItem, invisible;
    int usableCount, item;

    protected float stateTime;

    TextureRegion txDisabled;

    boolean hitByPlayer;
    float originalPosY;

    //item that pops out when box is hit by player
    Item itemObject;

    public Box(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        type = Type.massive;
        originalPosY = position.y;
    }

    @Override
    public void initAssets()
    {
        txDisabled = new TextureRegion(Assets.manager.get("data/game/box/brown1_1.png", Texture.class));
        if (animation == null || "default".equalsIgnoreCase(animation))
        {
            if (textureName == null)
            {
                textureName = "data/game/box/yellow/default.png";
            }
            return;
        }
        if (textureAtlas == null) return;
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureRegion> frames = new Array<TextureRegion>();
        float animSpeed = 0;
        if ("bonus".equalsIgnoreCase(animation))
        {
            frames.add(atlas.findRegion("1"));
            frames.add(atlas.findRegion("2"));
            frames.add(atlas.findRegion("3"));
            frames.add(atlas.findRegion("4"));
            frames.add(atlas.findRegion("5"));
            frames.add(atlas.findRegion("6"));
            animSpeed = 0.15f;
        }
        if ("power".equalsIgnoreCase(animation))
        {
            frames.add(atlas.findRegion("power-1"));
            frames.add(atlas.findRegion("power-2"));
            frames.add(atlas.findRegion("power-3"));
            frames.add(atlas.findRegion("power-4"));
            animSpeed = 0.2f;
        }
        if ("spin".equalsIgnoreCase(boxType) || "spin".equalsIgnoreCase(animation))
        {
            frames.add(new TextureRegion(Assets.manager.get("data/game/box/yellow/default.png", Texture.class)));
            frames.add(atlas.findRegion("1"));
            frames.add(atlas.findRegion("2"));
            frames.add(atlas.findRegion("3"));
            frames.add(atlas.findRegion("4"));
            frames.add(atlas.findRegion("5"));
            txDisabled = atlas.findRegion("6");
            animSpeed = 0.13f;
        }
        Assets.animations.put(textureAtlas, new Animation(animSpeed, frames));
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        if (invisible)
        {
            return;
        }
        if (itemObject != null)
        {
            itemObject.render(spriteBatch);
        }
        if (usableCount == 0)
        {
            Utility.draw(spriteBatch, txDisabled, position.x, position.y, bounds.height);
        }
        else
        {
            if (textureName != null)
            {
                Texture tx = Assets.manager.get(textureName);
                Utility.draw(spriteBatch, tx, position.x, position.y, bounds.height);
            }
            if (textureAtlas != null && animation != null && !"default".equalsIgnoreCase(animation))
            {
                TextureRegion frame = Assets.animations.get(textureAtlas).getKeyFrame(stateTime, true);
                Utility.draw(spriteBatch, frame, position.x, position.y, bounds.height);
            }
        }
    }

    public static Box initBox(World world, JSONObject jBox, LevelLoader loader) throws JSONException
    {
        Vector3 position = new Vector3((float) jBox.getDouble(LevelLoader.KEY.posx.toString()), (float) jBox.getDouble(LevelLoader.KEY.posy.toString()), 0);
        Vector2 size = new Vector2(SIZE, SIZE/*(float) jBox.getDouble(LevelLoader.KEY.width.toString()), (float) jBox.getDouble(LevelLoader.KEY.height.toString())*/);

        Box box = new Box(world, size, position);

        box.goldColor = jBox.optString(LevelLoader.KEY.gold_color.toString());
        box.animation = jBox.optString(LevelLoader.KEY.animation.toString(), null);
        box.boxType = jBox.optString(LevelLoader.KEY.type.toString());
        box.text = jBox.optString(LevelLoader.KEY.text.toString());
        box.forceBestItem = jBox.optInt(LevelLoader.KEY.force_best_item.toString(), 0) == 1;
        box.invisible = jBox.optInt(LevelLoader.KEY.invisible.toString(), 0) == 1;
        box.usableCount = jBox.optInt(LevelLoader.KEY.useable_count.toString(), -1);
        box.item = jBox.optInt(LevelLoader.KEY.item.toString(), 0);

        box.textureName = jBox.optString(LevelLoader.KEY.texture_name.toString(), null);
        if(box.textureAtlas != null && !LevelLoader.TXT_NAME_IN_ATLAS.matcher(box.textureName).matches())
        {
            Assets.manager.load(box.textureName, Texture.class, Assets.textureParameter);
        }
        if (jBox.has(LevelLoader.KEY.texture_atlas.toString()))
        {
            box.textureAtlas = jBox.getString(LevelLoader.KEY.texture_atlas.toString());
            Assets.manager.load(box.textureAtlas, TextureAtlas.class);
        }
        Assets.manager.load("data/game/box/yellow/default.png", Texture.class, Assets.textureParameter);
        addBoxItem(box, true);
        //addBoxItem(box, loader.getLevel());
        return box;
    }

    private static void addBoxItem(Box box, boolean loadAssets)
    {
        //create item contained in box
        if (!box.forceBestItem && ( box.item == Item.TYPE_FIREPLANT || box.item == Item.TYPE_MUSHROOM_BLUE ) &&
                (GameSaveUtility.getInstance().save.playerState == Maryo.MaryoState.small
                        || ((GameSaveUtility.getInstance().save.playerState == Maryo.MaryoState.fire
                        || GameSaveUtility.getInstance().save.playerState == Maryo.MaryoState.ice))))
        {
            box.item = Item.TYPE_MUSHROOM_DEFAULT;
            createMushroom(box, loadAssets);
        }
        else if(box.item == Item.TYPE_GOLDPIECE)
        {
            if(loadAssets)
            {
                Assets.manager.load(Coin.DEF_ATL, TextureAtlas.class);
            }
            else
            {
                createCoin(box);
            }
        }
        else if(box.item == Item.TYPE_MUSHROOM_DEFAULT || box.item == Item.TYPE_MUSHROOM_LIVE_1
                || box.item == Item.TYPE_MUSHROOM_BLUE || box.item == Item.TYPE_MUSHROOM_GHOST
                || box.item == Item.TYPE_MUSHROOM_POISON)
        {
            createMushroom(box, loadAssets);
        }
        else if(box.item == Item.TYPE_FIREPLANT)
        {
            if(loadAssets)
            {
                Assets.manager.load("data/game/items/fireplant.pack", TextureAtlas.class);
                Assets.manager.load("data/animation/particles/fireplant_emitter.p", ParticleEffect.class, Assets.particleEffectParameter);
            }
            else
            {
                createFireplant(box);
            }
        }
        else if(box.item == Item.TYPE_STAR)
        {
            if(loadAssets)
            {
                //Assets.manager.load("data/game/items/fireplant.pack", TextureAtlas.class);
            }
            else
            {

            }
        }
        else if(box.item == Item.TYPE_MOON)
        {
            if(loadAssets)
            {
                Assets.manager.load("data/game/items/moon.pack", TextureAtlas.class);
            }
            else
            {
                createMoon(box);
            }
        }
    }

    public static void createMushroom(Box box, boolean loadAssets)
    {
        Mushroom mushroom;
        switch (box.item)
        {
            case Item.TYPE_MUSHROOM_DEFAULT:
            default:
                mushroom = new MushroomDefault(box.world, new Vector2(Mushroom.DEF_SIZE, Mushroom.DEF_SIZE), new Vector3(box.position));
                break;
            case Item.TYPE_MUSHROOM_LIVE_1:
                mushroom = new MushroomLive1(box.world, new Vector2(Mushroom.DEF_SIZE, Mushroom.DEF_SIZE), new Vector3(box.position));
                break;
            case Item.TYPE_MUSHROOM_BLUE:
                mushroom = new MushroomBlue(box.world, new Vector2(Mushroom.DEF_SIZE, Mushroom.DEF_SIZE), new Vector3(box.position));
                break;
            case Item.TYPE_MUSHROOM_GHOST:
                mushroom = new MushroomGhost(box.world, new Vector2(Mushroom.DEF_SIZE, Mushroom.DEF_SIZE), new Vector3(box.position));
                break;
            case Item.TYPE_MUSHROOM_POISON:
                mushroom = new MushroomPoison(box.world, new Vector2(Mushroom.DEF_SIZE, Mushroom.DEF_SIZE), new Vector3(box.position));
                break;
        }

        mushroom.visible = false;

        if(loadAssets)
        {
            Assets.manager.load(mushroom.textureName, Texture.class, Assets.textureParameter);
        }
        else
        {
            box.itemObject = mushroom;
        }
    }

    public static void createCoin(Box box)
    {
        Coin coin = new Coin(box.world, new Vector2(Coin.DEF_SIZE, Coin.DEF_SIZE), new Vector3(box.position));
        String ta = Coin.DEF_ATL;
        coin.textureAtlas = ta;
        coin.initAssets();
        coin.collectible = false;
        coin.visible = false;

        box.itemObject = coin;
    }

    public static void createFireplant(Box box)
    {
        Fireplant fireplant = new Fireplant(box.world, new Vector2(Fireplant.DEF_SIZE, Fireplant.DEF_SIZE), new Vector3(box.position));
        fireplant.initAssets();
        fireplant.visible = false;

        box.itemObject = fireplant;
    }

    public static void createMoon(Box box)
    {
        Moon moon = new Moon(box.world, new Vector2(Moon.DEF_SIZE, Moon.DEF_SIZE), new Vector3(box.position));
        moon.initAssets();
        moon.visible = false;

        box.itemObject = moon;
    }


    public void handleHitByPlayer()
    {
        if (hitByPlayer) return;
        Sound sound = null;
        if ((usableCount == -1 || usableCount > 0))//is disabled(no more items)
        {
            if (usableCount != -1) usableCount--;
            hitByPlayer = true;
            velocity.y = 3f;
            addBoxItem(this, false);
            if (itemObject != null)
            {
                itemObject.popOutFromBox(position.y + bounds.height);
                if (itemObject instanceof Coin)
                {
                    if (itemObject.textureAtlas.contains("yellow"))
                    {
                        sound = Assets.manager.get("data/sounds/item/goldpiece_1.ogg");
                    }
                    else
                    {
                        sound = Assets.manager.get("data/sounds/item/goldpiece_red.wav");
                    }
                }
                else if(itemObject instanceof Mushroom)
                {
                    sound = Assets.manager.get("data/sounds/sprout_1.ogg");
                }
            }
        }
        else
        {
            sound = Assets.manager.get("data/sounds/wall_hit.wav");
        }
        if (sound != null && Assets.playSounds) sound.play();
    }

    @Override
    public void update(float delta)
    {
        if (itemObject != null && world.isObjectVisible(itemObject, true))
        {
            itemObject.update(delta);
        }
        else
        {
            if(!(itemObject instanceof Fireplant))
                itemObject = null;
        }
        if (hitByPlayer)
        {
            // Setting initial vertical acceleration
            acceleration.y = Constants.GRAVITY;

            // Convert acceleration to frame time
            acceleration.scl(delta);

            // apply acceleration to change velocity
            velocity.add(acceleration);

            // scale velocity to frame units
            velocity.scl(delta);

            // update position
            position.add(velocity);
            body.y = position.y;
            updateBounds();

            // un-scale velocity (not in frame time)
            velocity.scl(1 / delta);

            if (position.y <= originalPosY)
            {
                hitByPlayer = false;
                position.y = originalPosY;
                body.y = position.y;
                updateBounds();
            }
        }
        stateTime += delta;
    }
}
