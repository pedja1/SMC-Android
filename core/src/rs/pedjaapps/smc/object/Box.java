package rs.pedjaapps.smc.object;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.items.Coin;
import rs.pedjaapps.smc.object.items.Fireplant;
import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.object.items.Moon;
import rs.pedjaapps.smc.object.items.Star;
import rs.pedjaapps.smc.object.items.mushroom.Mushroom;
import rs.pedjaapps.smc.object.items.mushroom.MushroomBlue;
import rs.pedjaapps.smc.object.items.mushroom.MushroomDefault;
import rs.pedjaapps.smc.object.items.mushroom.MushroomLive1;
import rs.pedjaapps.smc.object.items.mushroom.MushroomPoison;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.LevelLoader;
import rs.pedjaapps.smc.utility.TextUtils;
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

    public static final float SIZE = 0.671875f;
    private String goldColor, animationName, boxType;
    public String text;
    private boolean forceBestItem, invisible;
    private int usableCount, item;

    protected float stateTime;

    private TextureRegion txDisabled;

    private boolean activated;
    private float originalPosY;

    //item that pops out when box is hit by player
    //public Item itemObject;
    private boolean spinning;
    private float spinningTime;
    private Animation animation;
    private Texture texture;

    private ParticleEffect itemEffect;

    public Box(World world, Vector2 size, Vector3 position, Rectangle rectangle)
    {
        super(world, size, position, rectangle);
        type = Type.massive;
        originalPosY = position.y;
        position.z = POSITION_Z;
    }

    @Override
    public void initAssets()
    {
        txDisabled = new TextureRegion(world.screen.game.assets.manager.get("data/game/box/brown1_1.png", Texture.class));
        if (textureName == null)
        {
            textureName = "data/game/box/yellow/default.png";
        }
        texture = world.screen.game.assets.manager.get(textureName);
        if (animationName == null || "default".equalsIgnoreCase(animationName))
        {
            if(!"spin".equals(boxType))return;
        }
        if("spin".equals(boxType))
        {
            textureAtlas = "data/game/box/yellow/spin.pack";
        }
        if (textureAtlas == null) return;
        TextureAtlas atlas = world.screen.game.assets.manager.get(textureAtlas);
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
            frames.add(atlas.findRegion("power", 1));
            frames.add(atlas.findRegion("power", 2));
            frames.add(atlas.findRegion("power", 3));
            frames.add(atlas.findRegion("power", 4));
            animSpeed = 0.1f;
        }
        if ("spin".equalsIgnoreCase(boxType) || "spin".equalsIgnoreCase(animationName))
        {
            frames.add(new TextureRegion(world.screen.game.assets.manager.get("data/game/box/yellow/default.png", Texture.class)));
            frames.add(atlas.findRegion("1"));
            frames.add(atlas.findRegion("2"));
            frames.add(atlas.findRegion("3"));
            frames.add(atlas.findRegion("4"));
            frames.add(atlas.findRegion("5"));
            txDisabled = atlas.findRegion("6");
            animSpeed = 0.08f;
        }
        if(frames.size == 0)
            throw new GdxRuntimeException("No frames in animation. boxType=" + boxType + " animationName=" + animationName);
        animation = new Animation(animSpeed, frames);
    }

    @Override
    public void dispose()
    {
        txDisabled = null;
        texture = null;
        animation = null;
        if(itemEffect != null)
        {
            itemEffect.dispose();
            itemEffect = null;
        }
    }

    @Override
    public void _render(SpriteBatch spriteBatch)
    {
        if (invisible)
        {
            return;
        }

        if(itemEffect != null)
        {
            itemEffect.setPosition(position.x, position.y + mDrawRect.height);
            itemEffect.draw(spriteBatch);
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

    public static Box initBox(World world, JSONObject jBox, Assets assets) throws JSONException
    {
        Vector3 position = new Vector3((float) jBox.getDouble("posx"), (float) jBox.getDouble("posy"), 0);
        Vector2 size = new Vector2(SIZE, SIZE);

        Box box = new Box(world, size, position, null);

        box.goldColor = jBox.optString("gold_color");
        box.animationName = jBox.optString("animation", null);
        box.boxType = jBox.optString("type");
        box.text = jBox.optString("text");
        box.forceBestItem = jBox.optInt("force_best_item", 0) == 1;
        box.invisible = jBox.optInt("invisible", 0) == 1;
        box.usableCount = jBox.optInt("useable_count", -1);
        box.item = jBox.optInt("item", 0);

        box.textureName = jBox.optString("texture_name", null);
        if(!TextUtils.isEmpty(box.textureName) && !LevelLoader.TXT_NAME_IN_ATLAS.matcher(box.textureName).matches())
        {
            world.screen.game.assets.manager.load(box.textureName, Texture.class, world.screen.game.assets.textureParameter);
        }
        if (jBox.has("texture_atlas"))
        {
            box.textureAtlas = jBox.getString("texture_atlas");
            world.screen.game.assets.manager.load(box.textureAtlas, TextureAtlas.class);
        }
        if("spin".equals(box.boxType))
        {
            world.screen.game.assets.manager.load("data/game/box/yellow/spin.pack", TextureAtlas.class);
        }
        world.screen.game.assets.manager.load("data/game/box/yellow/default.png", Texture.class, world.screen.game.assets.textureParameter);
        world.screen.game.assets.manager.load("data/game/box/brown1_1.png", Texture.class, world.screen.game.assets.textureParameter);
        addBoxItem(box, true, assets);
        //addBoxItem(box, loader.getLevel());
        return box;
    }

    private static Item addBoxItem(Box box, boolean loadAssets, Assets assets)
    {
        //create item contained in box
        if (!box.forceBestItem && ( box.item == Item.TYPE_FIREPLANT || box.item == Item.TYPE_MUSHROOM_BLUE ) &&
                (GameSave.save.playerState == Maryo.MaryoState.small
                        || ((GameSave.save.playerState == Maryo.MaryoState.fire
                        || GameSave.save.playerState == Maryo.MaryoState.ice))))
        {
            int defBoxItem = box.item;
            box.item = Item.TYPE_MUSHROOM_DEFAULT;
            Item item = createMushroom(box, loadAssets, assets);
            if(loadAssets)
            {
                if(defBoxItem != box.item)
                {
                    box.item = defBoxItem;
                    boolean tmpForceBestItem = box.forceBestItem;
                    box.forceBestItem = true;
                    addBoxItem(box, true, assets);
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
                assets.manager.load(Coin.DEF_ATL, TextureAtlas.class);
            }
            else
            {
                return createCoin(box);
            }
        }
        else if(box.item == Item.TYPE_MUSHROOM_DEFAULT || box.item == Item.TYPE_MUSHROOM_LIVE_1
                || box.item == Item.TYPE_MUSHROOM_BLUE || box.item == Item.TYPE_MUSHROOM_POISON)
        {
            return createMushroom(box, loadAssets, assets);
        }
        else if(box.item == Item.TYPE_FIREPLANT)
        {
            if(loadAssets)
            {
                return createFireplant(box, true, assets);
            }
            else
            {
                return createFireplant(box, false, assets);
            }
        }
        else if(box.item == Item.TYPE_STAR)
        {
            if(loadAssets)
            {
                return createStar(box, true, assets);
            }
            else
            {
                return createStar(box, false, assets);
            }
        }
        else if(box.item == Item.TYPE_MOON)
        {
            if(loadAssets)
            {
                return createMoon(box, true, assets);
            }
            else
            {
                return createMoon(box, false, assets);
            }
        }
        return null;
    }

    private static Item createMushroom(Box box, boolean loadAssets, Assets assets)
    {
        return createMushroom(box.world, box.position, box.item, loadAssets, assets, true);
    }

    public static Item createMushroom(Assets assets, int mushroomType)
    {
        return createMushroom(null, null, mushroomType, true, assets, false);
    }

    public static Item createMushroom(World world, Vector3 position, int mushroomType, boolean loadAssets, Assets assets, boolean initAssets)
    {
        if(loadAssets)
        {
            switch (mushroomType)
            {
                case Item.TYPE_MUSHROOM_DEFAULT:
                default:
                    assets.manager.load("data/game/items/mushroom_red.png", Texture.class, assets.textureParameter);
                    assets.manager.load("data/sounds/item/mushroom.mp3", Sound.class);
                    break;
                case Item.TYPE_MUSHROOM_LIVE_1:
                    assets.manager.load("data/game/items/mushroom_green.png", Texture.class, assets.textureParameter);
                    break;
                case Item.TYPE_MUSHROOM_BLUE:
                    assets.manager.load("data/game/items/mushroom_blue.png", Texture.class, assets.textureParameter);
                    assets.manager.load("data/sounds/item/mushroom_blue.mp3", Sound.class);
                    break;
                case Item.TYPE_MUSHROOM_POISON:
                    assets.manager.load("data/game/items/mushroom_poison.png", Texture.class, assets.textureParameter);
                    break;
            }
            assets.manager.load("data/animation/particles/box_activated.p", ParticleEffect.class, assets.particleEffectParameter);
        }
        else
        {
            Mushroom mushroom;
            switch (mushroomType)
            {
                case Item.TYPE_MUSHROOM_DEFAULT:
                default:
                    mushroom = new MushroomDefault(world, new Vector2(Mushroom.DEF_SIZE, Mushroom.DEF_SIZE), new Vector3(position));
                    break;
                case Item.TYPE_MUSHROOM_LIVE_1:
                    mushroom = new MushroomLive1(world, new Vector2(Mushroom.DEF_SIZE, Mushroom.DEF_SIZE), new Vector3(position));
                    break;
                case Item.TYPE_MUSHROOM_BLUE:
                    mushroom = new MushroomBlue(world, new Vector2(Mushroom.DEF_SIZE, Mushroom.DEF_SIZE), new Vector3(position));
                    break;
                case Item.TYPE_MUSHROOM_POISON:
                    mushroom = new MushroomPoison(world, new Vector2(Mushroom.DEF_SIZE, Mushroom.DEF_SIZE), new Vector3(position));
                    break;
            }

            mushroom.visible = false;
            if(initAssets)mushroom.initAssets();
            return mushroom;
        }
        return null;
    }

    private static Item createCoin(Box box)
    {
        Coin coin = new Coin(box.world, new Vector2(Coin.DEF_SIZE, Coin.DEF_SIZE), new Vector3(box.position));
        String ta = Coin.DEF_ATL;
        coin.textureAtlas = ta;
        coin.initAssets();
        coin.collectible = false;
        coin.visible = false;

        return coin;
    }

    public static Item createFireplant(World world, Vector3 position)
    {
        Vector2 size = new Vector2(Fireplant.DEF_SIZE, Fireplant.DEF_SIZE);
        Vector3 pos = new Vector3(position);
        return new Fireplant(world, size, pos);
    }

    public static Item createFireplant(Assets assets)
    {
        return createFireplant(null, true, assets);
    }

    private static Item createFireplant(Box box, boolean loadAssets, Assets assets)
    {
        if(loadAssets)
        {
            assets.manager.load("data/animation/particles/box_activated.p", ParticleEffect.class, assets.particleEffectParameter);
            assets.manager.load("data/sounds/item/fireplant.mp3", Sound.class);
            assets.manager.load("data/game/items/fireplant.pack", TextureAtlas.class);
            assets.manager.load("data/animation/particles/fireplant_emitter.p", ParticleEffect.class, assets.particleEffectParameter);
        }
        else
        {
            Vector2 size = new Vector2(Fireplant.DEF_SIZE, Fireplant.DEF_SIZE);
            Vector3 pos = new Vector3(box.position);
            pos.x = box.position.x + box.mDrawRect.width * 0.5f - size.x * 0.5f;
            Fireplant fireplant = new Fireplant(box.world, size, pos);
            fireplant.initAssets();
            fireplant.visible = false;
            return fireplant;
        }
        return null;
    }

    private static Item createMoon(Box box, boolean loadAssets, Assets assets)
    {
        return createMoon(box.world, box.position, loadAssets, assets, true);
    }

    public static Item createMoon(Assets assets)
    {
        return createMoon(null, null, true, assets, false);
    }

    public static Item createMoon(World world, Vector3 position, boolean loadAssets, Assets assets, boolean initAssets)
    {
        if(loadAssets)
        {
            assets.manager.load("data/animation/particles/box_activated.p", ParticleEffect.class, assets.particleEffectParameter);
            assets.manager.load("data/game/items/moon.pack", TextureAtlas.class);
            assets.manager.load("data/sounds/item/moon.mp3", Sound.class);
        }
        else
        {
            Moon moon = new Moon(world, new Vector2(Moon.DEF_SIZE, Moon.DEF_SIZE), new Vector3(position));
            if(initAssets)moon.initAssets();
            moon.visible = false;
            return moon;
        }

        return null;
    }

    private static Item createStar(Box box, boolean loadAssets, Assets assets)
    {
        return createStar(box.world, box.position, loadAssets, assets, true);
    }

    public static Item createStar(Assets assets)
    {
        return createStar(null, null, true, assets, false);
    }

    public static Item createStar(World world, Vector3 position, boolean loadAssets, Assets assets, boolean initAssets)
    {
        if(loadAssets)
        {
            assets.manager.load("data/animation/particles/box_activated.p", ParticleEffect.class, assets.particleEffectParameter);
            assets.manager.load("data/game/items/star.png", Texture.class, assets.textureParameter);
        }
        else
        {
            Star star = new Star(world, new Vector2(Star.DEF_SIZE, Star.DEF_SIZE), new Vector3(position));
            if(initAssets)star.initAssets();
            star.visible = false;

            return star;
        }
        return null;
    }

    public void activate()
    {
        if (activated) return;
		if(invisible)invisible = false;
        Sound sound = null;
        if("text".equals(boxType))
        {
            activated = true;
            ((GameScreen)world.screen).showBoxText(this);
        }
        else if("spin".equals(boxType))
        {
            spinning = true;
            activated = true;
            type = Type.passive;
        }
        else if ((usableCount == -1 || usableCount > 0))//is disabled(no more items)
        {
            if (usableCount != -1) usableCount--;
            activated = true;
            velocity.y = 3f;
            Item item = addBoxItem(this, false, world.screen.game.assets);
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
                        sound = world.screen.game.assets.manager.get("data/sounds/item/goldpiece_1.mp3");
                    }
                    else
                    {
                        sound = world.screen.game.assets.manager.get("data/sounds/item/goldpiece_red.mp3");
                    }
                }
                else if(item instanceof Mushroom)
                {
                    createItemEffect();
                    sound = world.screen.game.assets.manager.get("data/sounds/sprout_1.mp3");
                }
                else if(item instanceof Moon)
                {
                    createItemEffect();
                }//TODO sound effects
                else if(item instanceof Fireplant)
                {
                    createItemEffect();
                }
                else if(item instanceof Star)
                {
                    createItemEffect();
                }
            }
        }
        else
        {
            sound = world.screen.game.assets.manager.get("data/sounds/wall_hit.mp3");
        }
        SoundManager.play(sound);
    }

    private void createItemEffect()
    {
        itemEffect = new ParticleEffect(world.screen.game.assets.manager.get("data/animation/particles/box_activated.p", ParticleEffect.class));
        itemEffect.start();
    }

    @Override
    public void _update(float delta)
    {
        if(itemEffect != null)
        {
            itemEffect.update(delta);
        }

        if(spinning)
        {
            spinningTime += delta;
        }

        if (activated)
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
                activated = false;
                position.y = originalPosY;
                mColRect.y = position.y;
                updateBounds();
            }
        }
        stateTime += delta;
    }
}
