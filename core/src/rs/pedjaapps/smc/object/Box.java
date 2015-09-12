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

import java.util.Collections;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.Audio;
import rs.pedjaapps.smc.object.items.Coin;
import rs.pedjaapps.smc.object.items.Fireplant;
import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.object.items.Moon;
import rs.pedjaapps.smc.object.items.Star;
import rs.pedjaapps.smc.object.items.mushroom.Mushroom;
import rs.pedjaapps.smc.object.items.mushroom.MushroomBlue;
import rs.pedjaapps.smc.object.items.mushroom.MushroomDefault;
import rs.pedjaapps.smc.object.items.mushroom.MushroomGhost;
import rs.pedjaapps.smc.object.items.mushroom.MushroomLive1;
import rs.pedjaapps.smc.object.items.mushroom.MushroomPoison;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.screen.GameScreen;
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

    public static final float POSITION_Z = 0.055f;
    public static final float SPINNING_TIME = 5;

    public static final float SIZE = 0.67f;
    String goldColor, animationName, boxType;
    public String text;
    boolean forceBestItem, invisible;
    int usableCount, item;

    protected float stateTime;

    TextureRegion txDisabled;

    boolean hitByPlayer;
    float originalPosY;

    //item that pops out when box is hit by player
    //public Item itemObject;
    private boolean spinning;
    private float spinningTime;
    private Animation animation;
    private Texture texture;

    public Box(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        type = Type.massive;
        originalPosY = position.y;
        position.z = POSITION_Z;
    }

    @Override
    public void initAssets()
    {
        txDisabled = new TextureRegion(Assets.manager.get("data/game/box/brown1_1.png", Texture.class));
        if (textureName == null)
        {
            textureName = "data/game/box/yellow/default.png";
        }
        texture = Assets.manager.get(textureName);
        if (animationName == null || "default".equalsIgnoreCase(animationName))
        {
            if(!"spin".equals(boxType))return;
        }
        if("spin".equals(boxType))
        {
            textureAtlas = "data/game/box/yellow/spin.pack";
        }
        if (textureAtlas == null) return;
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureRegion> frames = new Array<TextureRegion>();
        float animSpeed = 0;
        if ("bonus".equalsIgnoreCase(animationName))
        {
            frames.add(atlas.findRegion("1"));
            frames.add(atlas.findRegion("2"));
            frames.add(atlas.findRegion("3"));
            frames.add(atlas.findRegion("4"));
            frames.add(atlas.findRegion("5"));
            frames.add(atlas.findRegion("6"));
            animSpeed = 0.09f;
        }
        if ("power".equalsIgnoreCase(animationName))
        {
            frames.add(atlas.findRegion("power-1"));
            frames.add(atlas.findRegion("power-2"));
            frames.add(atlas.findRegion("power-3"));
            frames.add(atlas.findRegion("power-4"));
            animSpeed = 0.2f;
        }
        if ("spin".equalsIgnoreCase(boxType) || "spin".equalsIgnoreCase(animationName))
        {
            frames.add(new TextureRegion(Assets.manager.get("data/game/box/yellow/default.png", Texture.class)));
            frames.add(atlas.findRegion("1"));
            frames.add(atlas.findRegion("2"));
            frames.add(atlas.findRegion("3"));
            frames.add(atlas.findRegion("4"));
            frames.add(atlas.findRegion("5"));
            txDisabled = atlas.findRegion("6");
            animSpeed = 0.08f;
        }
        animation = new Animation(animSpeed, frames);
    }

    @Override
    public void dispose()
    {
        txDisabled = null;
        texture = null;
        animation = null;
    }

    @Override
    public void _render(SpriteBatch spriteBatch)
    {
        if (invisible)
        {
            return;
        }

        if (usableCount == 0)
        {
            Utility.draw(spriteBatch, txDisabled, position.x, position.y, mDrawRect.height);
        }
        else
        {
            if("spin".equals(boxType))
            {
                if(spinning)
                {
                    if(spinningTime >= SPINNING_TIME)
                    {
                        if(!world.maryo.mColRect.overlaps(mColRect))//continue spinning as long as maryo is over the box
                        {
                            spinning = false;
                            spinningTime = 0;
                            type = Type.massive;
                        }
                    }
                    TextureRegion frame = animation.getKeyFrame(stateTime, true);
                    Utility.draw(spriteBatch, frame, position.x, position.y, mDrawRect.height);
                }
                else
                {
                    Utility.draw(spriteBatch, texture, position.x, position.y, mDrawRect.height);
                }
            }
            else
            {
                if (textureName != null)
                {
                    Utility.draw(spriteBatch, texture, position.x, position.y, mDrawRect.height);
                }
                if (textureAtlas != null && animationName != null && !"default".equalsIgnoreCase(animationName))
                {
                    TextureRegion frame = animation.getKeyFrame(stateTime, true);
                    Utility.draw(spriteBatch, frame, position.x, position.y, mDrawRect.height);
                }
            }
        }
    }

    public static Box initBox(World world, JSONObject jBox, LevelLoader loader) throws JSONException
    {
        Vector3 position = new Vector3((float) jBox.getDouble("posx"), (float) jBox.getDouble("posy"), 0);
        Vector2 size = new Vector2(SIZE, SIZE);

        Box box = new Box(world, size, position);

        box.goldColor = jBox.optString("gold_color");
        box.animationName = jBox.optString("animation", null);
        box.boxType = jBox.optString("type");
        box.text = jBox.optString("text");
        box.forceBestItem = jBox.optInt("force_best_item", 0) == 1;
        box.invisible = jBox.optInt("invisible", 0) == 1;
        box.usableCount = jBox.optInt("useable_count", -1);
        box.item = jBox.optInt("item", 0);

        box.textureName = jBox.optString("texture_name", null);
        if(box.textureAtlas != null && !LevelLoader.TXT_NAME_IN_ATLAS.matcher(box.textureName).matches())
        {
            Assets.manager.load(box.textureName, Texture.class, Assets.textureParameter);
        }
        if (jBox.has("texture_atlas"))
        {
            box.textureAtlas = jBox.getString("texture_atlas");
            Assets.manager.load(box.textureAtlas, TextureAtlas.class, Assets.atlasTextureParameter);
        }
        if("spin".equals(box.boxType))
        {
            Assets.manager.load("data/game/box/yellow/spin.pack", TextureAtlas.class, Assets.atlasTextureParameter);
        }
        Assets.manager.load("data/game/box/yellow/default.png", Texture.class, Assets.textureParameter);
        Assets.manager.load("data/game/box/brown1_1.png", Texture.class, Assets.textureParameter);
        addBoxItem(box, true);
        //addBoxItem(box, loader.getLevel());
        return box;
    }

    private static Item addBoxItem(Box box, boolean loadAssets)
    {
        //create item contained in box
        if (!box.forceBestItem && ( box.item == Item.TYPE_FIREPLANT || box.item == Item.TYPE_MUSHROOM_BLUE ) &&
                (GameSaveUtility.getInstance().save.playerState == Maryo.MaryoState.small
                        || ((GameSaveUtility.getInstance().save.playerState == Maryo.MaryoState.fire
                        || GameSaveUtility.getInstance().save.playerState == Maryo.MaryoState.ice))))
        {
            int defBoxItem = box.item;
            box.item = Item.TYPE_MUSHROOM_DEFAULT;
            Item item = createMushroom(box, loadAssets);
            if(loadAssets)
            {
                if(defBoxItem != box.item)
                {
                    box.item = defBoxItem;
                    boolean tmpForceBestItem = box.forceBestItem;
                    box.forceBestItem = true;
                    addBoxItem(box, true);
                    box.forceBestItem = tmpForceBestItem;
                }
                box.item = defBoxItem;
            }
            return item;
        }
        else if(box.item == Item.TYPE_GOLDPIECE)
        {
            if(loadAssets)
            {
                Assets.manager.load(Coin.DEF_ATL, TextureAtlas.class, Assets.atlasTextureParameter);
            }
            else
            {
                return createCoin(box);
            }
        }
        else if(box.item == Item.TYPE_MUSHROOM_DEFAULT || box.item == Item.TYPE_MUSHROOM_LIVE_1
                || box.item == Item.TYPE_MUSHROOM_BLUE || box.item == Item.TYPE_MUSHROOM_GHOST
                || box.item == Item.TYPE_MUSHROOM_POISON)
        {
            return createMushroom(box, loadAssets);
        }
        else if(box.item == Item.TYPE_FIREPLANT)
        {
            if(loadAssets)
            {
                Assets.manager.load("data/sounds/item/fireplant.ogg", Sound.class);
                Assets.manager.load("data/game/items/fireplant.pack", TextureAtlas.class, Assets.atlasTextureParameter);
                Assets.manager.load("data/animation/particles/fireplant_emitter.p", ParticleEffect.class, Assets.particleEffectParameter);
            }
            else
            {
                return createFireplant(box);
            }
        }
        else if(box.item == Item.TYPE_STAR)
        {
            if(loadAssets)
            {
                Assets.manager.load("data/game/items/star.png", Texture.class, Assets.textureParameter);
            }
            else
            {
                return createStar(box);
            }
        }
        else if(box.item == Item.TYPE_MOON)
        {
            if(loadAssets)
            {
                Assets.manager.load("data/game/items/moon.pack", TextureAtlas.class, Assets.atlasTextureParameter);
            }
            else
            {
                return createMoon(box);
            }
        }
        return null;
    }

    public static Item createMushroom(Box box, boolean loadAssets)
    {
        if(loadAssets)
        {
            switch (box.item)
            {
                case Item.TYPE_MUSHROOM_DEFAULT:
                default:
                    Assets.manager.load("data/game/items/mushroom_red.png", Texture.class, Assets.textureParameter);
                    Assets.manager.load("data/sounds/item/mushroom.ogg", Sound.class);
                    break;
                case Item.TYPE_MUSHROOM_LIVE_1:
                    Assets.manager.load("data/game/items/mushroom_green.png", Texture.class, Assets.textureParameter);
                    break;
                case Item.TYPE_MUSHROOM_BLUE:
                    Assets.manager.load("data/game/items/mushroom_blue.png", Texture.class, Assets.textureParameter);
                    Assets.manager.load("data/sounds/item/mushroom_blue.wav", Sound.class);
                    break;
                case Item.TYPE_MUSHROOM_GHOST:
                    Assets.manager.load("data/game/items/mushroom_ghost.png", Texture.class, Assets.textureParameter);
                    Assets.manager.load("data/sounds/item/mushroom_ghost.ogg", Sound.class);
                    break;
                case Item.TYPE_MUSHROOM_POISON:
                    Assets.manager.load("data/game/items/mushroom_poison.png", Texture.class, Assets.textureParameter);
                    break;
            }
        }
        else
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
            mushroom.initAssets();
            return mushroom;
        }
        return null;
    }

    public static Item createCoin(Box box)
    {
        Coin coin = new Coin(box.world, new Vector2(Coin.DEF_SIZE, Coin.DEF_SIZE), new Vector3(box.position));
        String ta = Coin.DEF_ATL;
        coin.textureAtlas = ta;
        coin.initAssets();
        coin.collectible = false;
        coin.visible = false;

        return coin;
    }

    public static Item createFireplant(Box box)
    {
        Fireplant fireplant = new Fireplant(box.world, new Vector2(Fireplant.DEF_SIZE, Fireplant.DEF_SIZE), new Vector3(box.position));
        fireplant.initAssets();
        fireplant.visible = false;

        return fireplant;
    }

    public static Item createMoon(Box box)
    {
        Moon moon = new Moon(box.world, new Vector2(Moon.DEF_SIZE, Moon.DEF_SIZE), new Vector3(box.position));
        moon.initAssets();
        moon.visible = false;

        return moon;
    }

    public static Item createStar(Box box)
    {
        Star star = new Star(box.world, new Vector2(Star.DEF_SIZE, Star.DEF_SIZE), new Vector3(box.position));
        star.initAssets();
        star.visible = false;

        return star;
    }


    public void handleHitByPlayer()
    {
        if (hitByPlayer) return;
        Sound sound = null;
        if("text".equals(boxType))
        {
            hitByPlayer = true;
            ((GameScreen)world.screen).showBoxText(this);
        }
        else if("spin".equals(boxType))
        {
            spinning = true;
            hitByPlayer = true;
            type = Type.passive;
        }
        else if ((usableCount == -1 || usableCount > 0))//is disabled(no more items)
        {
            if (usableCount != -1) usableCount--;
            hitByPlayer = true;
            velocity.y = 3f;
            Item item = addBoxItem(this, false);
            if (item != null)
            {
                item.isInBox = true;
                world.level.gameObjects.add(item);
                Collections.sort(world.level.gameObjects, new LevelLoader.ZSpriteComparator());
                item.popOutFromBox(position.y + mDrawRect.height);
                if (item instanceof Coin)
                {
                    if (item.textureAtlas.contains("yellow"))
                    {
                        sound = Assets.manager.get("data/sounds/item/goldpiece_1.ogg");
                    }
                    else
                    {
                        sound = Assets.manager.get("data/sounds/item/goldpiece_red.wav");
                    }
                }
                else if(item instanceof Mushroom)
                {
                    sound = Assets.manager.get("data/sounds/sprout_1.ogg");
                }
            }
        }
        else
        {
            sound = Assets.manager.get("data/sounds/wall_hit.wav");
        }
        Audio.play(sound);
    }

    @Override
    public void _update(float delta)
    {
        if(spinning)
        {
            spinningTime += delta;
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
            mColRect.y = position.y;
            updateBounds();

            // un-scale velocity (not in frame time)
            velocity.scl(1 / delta);

            if (position.y <= originalPosY)
            {
                hitByPlayer = false;
                position.y = originalPosY;
                mColRect.y = position.y;
                updateBounds();
            }
        }
        stateTime += delta;
    }
}
