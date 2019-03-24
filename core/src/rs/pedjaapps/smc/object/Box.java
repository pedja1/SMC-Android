package rs.pedjaapps.smc.object;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.JsonValue;

import rs.pedjaapps.smc.MaryoGame;
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
import rs.pedjaapps.smc.object.items.mushroom.MushroomGhost;
import rs.pedjaapps.smc.object.items.mushroom.MushroomLive1;
import rs.pedjaapps.smc.object.items.mushroom.MushroomPoison;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.Utility;

public class Box extends Sprite {
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
    private boolean forceBestItem;
    private int usableCount, item, invisible;

    protected float stateTime;

    private TextureRegion txDisabled;

    private boolean activated;
    private float originalPosY;

    //item that pops out when box is hit by player
    //public Item itemObject;
    private boolean spinning;
    private float spinningTime;
    private Animation<TextureRegion> animation;
    private TextureRegion texture;

    private ParticleEffect itemEffect;

    public Box(float x, float y, float z, float width, float height, Rectangle rectangle) {
        super(x, y, z, width, height, rectangle);
        type = Type.massive;
        originalPosY = position.y;
        position.z = POSITION_Z;
    }

    @Override
    public void initAssets() {
        TextureAtlas atlas = MaryoGame.game.assets.get(Assets.ATLAS_STATIC, TextureAtlas.class);
        txDisabled = atlas.findRegion("game_box_brown1_1");
        if (textureName == null)
            textureName = "game_box_yellow_default";

        texture = atlas.findRegion(textureName);

        if (animationName == null || "default".equalsIgnoreCase(animationName)) {
            if (!"spin".equals(boxType)) return;
        }

        Array<TextureRegion> frames = new Array<TextureRegion>();
        float animSpeed = 0;
        if ("bonus".equalsIgnoreCase(animationName)) {
            frames.add(atlas.findRegion("game_box_yellow_bonus_1"));
            frames.add(atlas.findRegion("game_box_yellow_bonus_2"));
            frames.add(atlas.findRegion("game_box_yellow_bonus_3"));
            frames.add(atlas.findRegion("game_box_yellow_bonus_4"));
            frames.add(atlas.findRegion("game_box_yellow_bonus_5"));
            frames.add(atlas.findRegion("game_box_yellow_bonus_6"));
            animSpeed = 0.09f;
        }
        if ("power".equalsIgnoreCase(animationName)) {
            frames.add(atlas.findRegion("game_box_yellow_power_1"));
            frames.add(atlas.findRegion("game_box_yellow_power_2"));
            frames.add(atlas.findRegion("game_box_yellow_power_3"));
            frames.add(atlas.findRegion("game_box_yellow_power_4"));
            animSpeed = 0.1f;
        }
        if ("spin".equalsIgnoreCase(boxType) || "spin".equalsIgnoreCase(animationName)) {
            animationName = "spin";
            frames.add(atlas.findRegion("game_box_yellow_default"));
            frames.add(atlas.findRegion("game_box_yellow_spin_1"));
            frames.add(atlas.findRegion("game_box_yellow_spin_2"));
            frames.add(atlas.findRegion("game_box_yellow_spin_3"));
            frames.add(atlas.findRegion("game_box_yellow_spin_4"));
            frames.add(atlas.findRegion("game_box_yellow_spin_5"));
            txDisabled = atlas.findRegion("game_box_yellow_spin_6");
            animSpeed = 0.08f;
        }
        if (frames.size == 0 && animationName != null)
            throw new GdxRuntimeException("No frames in animation. boxType=" + boxType + " animationName=" + animationName);
        if (frames.size > 0)
            animation = new Animation(animSpeed, frames);
    }

    @Override
    public void dispose() {
        txDisabled = null;
        texture = null;
        animation = null;
        if (itemEffect != null) {
            itemEffect.dispose();
            itemEffect = null;
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (invisible > 0 && !MaryoGame.game.currentScreen.world.maryo.ghostmode)
            return;
        else if (invisible > 0)
            spriteBatch.setColor(1, 1, 1, .5f);

        if (itemEffect != null) {
            itemEffect.setPosition(position.x, position.y + drawRect.height);
            itemEffect.draw(spriteBatch);
        }

        if (usableCount == 0) {
            Utility.draw(spriteBatch, txDisabled, position.x, position.y, drawRect.height);
        } else {
            if ("spin".equals(boxType)) {
                if (spinning) {
                    if (spinningTime >= SPINNING_TIME) {
                        if (!MaryoGame.game.currentScreen.world.maryo.colRect.overlaps(colRect))//continue spinning as long as maryo is over the box
                        {
                            spinning = false;
                            spinningTime = 0;
                            type = Type.massive;
                        }
                    }
                    TextureRegion frame = animation.getKeyFrame(stateTime, true);
                    Utility.draw(spriteBatch, frame, position.x, position.y, drawRect.height);
                } else {
                    Utility.draw(spriteBatch, texture, position.x, position.y, drawRect.height);
                }
            } else {
                if (textureName != null) {
                    Utility.draw(spriteBatch, texture, position.x, position.y, drawRect.height);
                }
                if (animation != null && animationName != null && !"default".equalsIgnoreCase(animationName)) {
                    TextureRegion frame = animation.getKeyFrame(stateTime, true);
                    Utility.draw(spriteBatch, frame, position.x, position.y, drawRect.height);
                }
            }
        }

        if (invisible > 0)
            spriteBatch.setColor(Color.WHITE);
    }

    public static Box initBox(JsonValue jBox) {
        Box box = new Box((float) jBox.getDouble("posx"), (float) jBox.getDouble("posy"), 0, SIZE, SIZE, null);

        box.goldColor = jBox.getString("gold_color", "");
        box.animationName = jBox.getString("animation", null);
        box.boxType = jBox.getString("type", "");
        box.text = jBox.getString("text", "");
        box.forceBestItem = jBox.getInt("force_best_item", 0) == 1;
        //TODO invisible 2 meint dass das Item auch nur mit ghost abholbar ist
        box.invisible = jBox.getInt("invisible", 0);
        box.usableCount = jBox.getInt("useable_count", -1);
        box.item = jBox.getInt("item", 0);

        box.textureName = jBox.getString("texture_name", null);
        addBoxItem(box, true);
        //addBoxItem(box, loader.getLevel());
        return box;
    }

    private static Item addBoxItem(Box box, boolean loadAssets) {
        //create item contained in box
        if (!box.forceBestItem && (box.item == Item.TYPE_FIREPLANT || box.item == Item.TYPE_MUSHROOM_BLUE) &&
                (GameSave.getMaryoState() == Maryo.MaryoState.small
                        || ((GameSave.getMaryoState() == Maryo.MaryoState.fire
                        || GameSave.getMaryoState() == Maryo.MaryoState.ice)))) {
            int defBoxItem = box.item;
            box.item = Item.TYPE_MUSHROOM_DEFAULT;
            Item item = createMushroom(box, loadAssets);
            if (loadAssets) {
                if (defBoxItem != box.item) {
                    box.item = defBoxItem;
                    boolean tmpForceBestItem = box.forceBestItem;
                    box.forceBestItem = true;
                    addBoxItem(box, true);
                    box.forceBestItem = tmpForceBestItem;
                }
                box.item = defBoxItem;
            }
            return item;
        } else if (box.item == Item.TYPE_GOLDPIECE) {
            if (!loadAssets)
                return createCoin(box);
        } else if (box.item == Item.TYPE_MUSHROOM_DEFAULT || box.item == Item.TYPE_MUSHROOM_LIVE_1
                || box.item == Item.TYPE_MUSHROOM_BLUE || box.item == Item.TYPE_MUSHROOM_POISON
                || box.item == Item.TYPE_MUSHROOM_GHOST) {
            return createMushroom(box, loadAssets);
        } else if (box.item == Item.TYPE_FIREPLANT)
            return createFireplant(box, loadAssets);

        else if (box.item == Item.TYPE_STAR)
            return createStar(box, loadAssets);

        else if (box.item == Item.TYPE_MOON)
            return createMoon(box, loadAssets);

        return null;
    }

    private static Item createMushroom(Box box, boolean loadAssets) {
        return createMushroom(box.position.x, box.position.y, box.position.z, box.item, loadAssets, true);
    }

    public static Item createMushroom(int mushroomType) {
        return createMushroom(0, 0, 0, mushroomType, true, false);
    }

    public static Item createMushroom(float x, float y, float z, int mushroomType, boolean loadAssets, boolean initAssets) {
        if (loadAssets) {
            switch (mushroomType) {
                case Item.TYPE_MUSHROOM_DEFAULT:
                default:
                    MaryoGame.game.assets.load(Assets.SOUND_ITEM_MUSHROOM, Sound.class);
                    break;
                case Item.TYPE_MUSHROOM_LIVE_1:
                    break;
                case Item.TYPE_MUSHROOM_BLUE:
                    MaryoGame.game.assets.load(Assets.SOUND_ITEM_MUSHROOM_BLUE, Sound.class);
                    break;
                case Item.TYPE_MUSHROOM_GHOST:
                    MaryoGame.game.assets.load(Assets.SOUND_ITEM_MUSHROOM_GHOST, Sound.class);
                    MaryoGame.game.assets.load(Assets.SOUND_PLAYER_GHOSTEND, Sound.class);
                    break;
                case Item.TYPE_MUSHROOM_POISON:
                    break;
            }
            MaryoGame.game.assets.load(Assets.PARTICLES_BOX_ACTIVATED, ParticleEffect.class, Assets.PARTICLE_EFFECT_PARAMETER);
        } else {
            Mushroom mushroom;
            switch (mushroomType) {
                case Item.TYPE_MUSHROOM_DEFAULT:
                default:
                    mushroom = new MushroomDefault(x, y, z, Mushroom.DEF_SIZE, Mushroom.DEF_SIZE);
                    break;
                case Item.TYPE_MUSHROOM_LIVE_1:
                    mushroom = new MushroomLive1(x, y, z, Mushroom.DEF_SIZE, Mushroom.DEF_SIZE);
                    break;
                case Item.TYPE_MUSHROOM_BLUE:
                    mushroom = new MushroomBlue(x, y, z, Mushroom.DEF_SIZE, Mushroom.DEF_SIZE);
                    break;
                case Item.TYPE_MUSHROOM_POISON:
                    mushroom = new MushroomPoison(x, y, z, Mushroom.DEF_SIZE, Mushroom.DEF_SIZE);
                    break;
                case Item.TYPE_MUSHROOM_GHOST:
                    mushroom = new MushroomGhost(x, y, z, Mushroom.DEF_SIZE, Mushroom.DEF_SIZE);
                    break;
            }

            mushroom.visible = false;
            if (initAssets) mushroom.initAssets();
            return mushroom;
        }
        return null;
    }

    private static Item createCoin(Box box) {
        Coin coin = new Coin(box.position.x, box.position.y, box.position.z, Coin.DEF_SIZE, Coin.DEF_SIZE,
                box.goldColor.equals("yellow") ? Coin.TYPE_YELLOW : Coin.TYPE_RED);
        coin.initAssets();
        coin.collectible = false;
        coin.visible = false;

        return coin;
    }

    public static Item createFireplant(float x, float y, float z) {
        return new Fireplant(x, y, z, Fireplant.DEF_SIZE, Fireplant.DEF_SIZE);
    }

    public static Item createFireplant() {
        return createFireplant(null, true);
    }

    private static Item createFireplant(Box box, boolean loadAssets) {
        if (loadAssets) {
            MaryoGame.game.assets.load(Assets.PARTICLES_BOX_ACTIVATED, ParticleEffect.class, Assets.PARTICLE_EFFECT_PARAMETER);
            MaryoGame.game.assets.load(Assets.SOUND_ITEM_FIREPLANT, Sound.class);
            MaryoGame.game.assets.load("data/animation/particles/fireplant_emitter.p", ParticleEffect.class, Assets.PARTICLE_EFFECT_PARAMETER);
        } else {
            float x = box.position.x + box.drawRect.width * 0.5f - Fireplant.DEF_SIZE * 0.5f;
            Fireplant fireplant = new Fireplant(x, box.position.y, box.position.z, Fireplant.DEF_SIZE, Fireplant.DEF_SIZE);
            fireplant.initAssets();
            fireplant.visible = false;
            return fireplant;
        }
        return null;
    }

    private static Item createMoon(Box box, boolean loadAssets) {
        return createMoon(box.position.x, box.position.y, box.position.z, loadAssets, true);
    }

    public static Item createMoon() {
        return createMoon(0, 0, 0, true, false);
    }

    public static Item createMoon(float x, float y, float z, boolean loadAssets, boolean initAssets) {
        if (loadAssets) {
            MaryoGame.game.assets.load("data/animation/particles/box_activated.p", ParticleEffect.class, Assets.PARTICLE_EFFECT_PARAMETER);
            MaryoGame.game.assets.load(Assets.SOUND_ITEM_MOON, Sound.class);
        } else {
            Moon moon = new Moon(x, y, z, Moon.DEF_SIZE, Moon.DEF_SIZE);
            if (initAssets) moon.initAssets();
            moon.visible = false;
            return moon;
        }

        return null;
    }

    private static Item createStar(Box box, boolean loadAssets) {
        return createStar(box.position.x, box.position.y, box.position.z, loadAssets, true);
    }

    public static Item createStar() {
        return createStar(0, 0, 0, true, false);
    }

    public static Item createStar(float x, float y, float z, boolean loadAssets, boolean initAssets) {
        if (loadAssets)
            MaryoGame.game.assets.load(Assets.PARTICLES_BOX_ACTIVATED, ParticleEffect.class, Assets.PARTICLE_EFFECT_PARAMETER);

        else {
            Star star = new Star(x, y, z, Star.DEF_SIZE, Star.DEF_SIZE);
            if (initAssets) star.initAssets();
            star.visible = false;

            return star;
        }
        return null;
    }

    public void activate() {
        if (activated) return;
        invisible = BOX_VISIBLE;
        Sound sound = null;
        if ("text".equals(boxType)) {
            activated = true;
            ((GameScreen) MaryoGame.game.currentScreen).showBoxText(this);
        } else if ("spin".equals(boxType)) {
            spinning = true;
            activated = true;
            type = Type.passive;
        } else if ((usableCount == -1 || usableCount > 0))//is disabled(no more items)
        {
            if (usableCount != -1) usableCount--;
            activated = true;
            velocity.y = 3f;
            Item item = addBoxItem(this, false);
            if (item != null) {
                item.isInBox = true;
                MaryoGame.game.addObject(item);
                MaryoGame.game.sortLevel();
                item.popOutFromBox(position.y + drawRect.height);
                if (item instanceof Coin)
                    sound = ((Coin) item).getSound();
                else if (item instanceof Mushroom) {
                    createItemEffect();
                    sound = MaryoGame.game.assets.get(Assets.SOUND_SPROUT);
                } else if (item instanceof Moon) {
                    createItemEffect();
                }//TODO sound effects
                else if (item instanceof Fireplant) {
                    createItemEffect();
                } else if (item instanceof Star) {
                    createItemEffect();
                }
            }
        } else {
            sound = MaryoGame.game.assets.get(Assets.SOUND_WALL_HIT);
        }
        SoundManager.play(sound);
    }

    private void createItemEffect() {
        itemEffect = new ParticleEffect(MaryoGame.game.assets.get(Assets.PARTICLES_BOX_ACTIVATED, ParticleEffect.class));
        itemEffect.start();
    }

    @Override
    public void update(float delta) {
        if (itemEffect != null) {
            itemEffect.update(delta);
        }

        if (spinning) {
            spinningTime += delta;
        }

        if (activated) {
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
            colRect.y = position.y;
            updateBounds();

            // un-scale velocity (not in frame time)
            velocity.scl(1 / delta);

            if (position.y <= originalPosY) {
                activated = false;
                position.y = originalPosY;
                colRect.y = position.y;
                updateBounds();
            }
        }
        stateTime += delta;
    }
}
