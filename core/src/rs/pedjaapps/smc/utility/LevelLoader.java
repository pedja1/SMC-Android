package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.Box;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Level;
import rs.pedjaapps.smc.object.LevelEntry;
import rs.pedjaapps.smc.object.LevelExit;
import rs.pedjaapps.smc.object.MovingPlatform;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.enemy.Eato;
import rs.pedjaapps.smc.object.enemy.Enemy;
import rs.pedjaapps.smc.object.enemy.EnemyClass;
import rs.pedjaapps.smc.object.enemy.EnemyStopper;
import rs.pedjaapps.smc.object.enemy.Flyon;
import rs.pedjaapps.smc.object.enemy.Furball;
import rs.pedjaapps.smc.object.enemy.Gee;
import rs.pedjaapps.smc.object.enemy.Krush;
import rs.pedjaapps.smc.object.enemy.Rokko;
import rs.pedjaapps.smc.object.enemy.Spika;
import rs.pedjaapps.smc.object.enemy.Spikeball;
import rs.pedjaapps.smc.object.enemy.Static;
import rs.pedjaapps.smc.object.enemy.Thromp;
import rs.pedjaapps.smc.object.enemy.Turtle;
import rs.pedjaapps.smc.object.items.Coin;
import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.view.Background;

import static rs.pedjaapps.smc.view.Background.BG_GR_HOR;
import static rs.pedjaapps.smc.view.Background.BG_GR_VER;
import static rs.pedjaapps.smc.view.Background.BG_IMG_ALL;
import static rs.pedjaapps.smc.view.Background.BG_IMG_BOTTOM;
import static rs.pedjaapps.smc.view.Background.BG_IMG_TOP;

/**
 * Created by pedja on 2/2/14.
 */

/**
 * This class loads level from json
 */
public class LevelLoader {
    public static final Pattern TXT_NAME_IN_ATLAS = Pattern.compile(".+\\.atlas:.+");
    public Level level;
    private boolean levelParsed = false;

    private enum ObjectClass {
        sprite, item, box, player, enemy, moving_platform, enemy_stopper, level_entry, level_exit,
    }

    public static final float m_pos_z_passive_start = 0.01f;
    private static final float m_pos_z_massive_start = 0.08f;
    private static final float m_pos_z_front_passive_start = 0.1f;
    private static final float m_pos_z_halfmassive_start = 0.04f;

    /**
     * Use this constructor only from pc when you want to automatically fix assets dependencies
     */
    public LevelLoader(String levelName) {
        level = new Level(levelName);
    }

    public synchronized void parseLevel(World world) {
        JsonValue jLevel;
        try {
            jLevel = new JsonReader().parse(Gdx.files.internal("data/levels/" + level.levelName + Level.LEVEL_EXT));
            parseInfo(jLevel);
            parseParticleEffect(jLevel);
            parseBg(jLevel);
            parseGameObjects(world, jLevel);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to load level! " + e.getMessage());
        }
        levelParsed = true;
    }

    private void parseParticleEffect(JsonValue jLevel) {
        JsonValue jParticleEffect = jLevel.get("particle_effect");
        if (jParticleEffect != null) {
            String effect = jParticleEffect.getString("effect", "");
            if (!TextUtils.isEmpty(effect)) {
                MaryoGame.game.assets.load(effect, ParticleEffect.class, Assets.PARTICLE_EFFECT_PARAMETER);
                level.particleEffect = effect;
            }
        }
    }

    private void parseGameObjects(World world, JsonValue level) {
        JsonValue jObjects = level.get("objects");
        for (JsonValue jObject = jObjects.child; jObject != null; jObject = jObject.next) {
            switch (ObjectClass.valueOf(jObject.getString("obj_class"))) {
                case sprite:
                    parseSprite(jObject);
                    break;
                case player:
                    parsePlayer(jObject, world);
                    break;
                case item:
                    parseItem(jObject);
                    break;
                case enemy:
                    parseEnemy(jObject);
                    break;
                case enemy_stopper:
                    parseEnemyStopper(jObject);
                    break;
                case box:
                    parseBox(jObject);
                    break;
                case level_entry:
                    parseLevelEntry(jObject);
                    break;
                case level_exit:
                    parseLevelExit(jObject);
                    break;
                case moving_platform:
                    parseMovingPlatform(jObject);
                    break;
            }
        }
        //this.level.gameObjects.sort(new ZSpriteComparator());
        Collections.sort(this.level.gameObjects, new ZSpriteComparator());
    }

    private void parseInfo(JsonValue jLevel) {
        JsonValue jInfo = jLevel.get("info");
        float width = jInfo.getFloat("level_width");
        float height = jInfo.getFloat("level_height");
        level.width = width;
        level.height = Math.max(height, Constants.CAMERA_HEIGHT);
        if (jInfo.has("level_music")) {
            JsonValue jMusic = jInfo.get("level_music");
            Array<String> music = new Array<>();
            for (JsonValue thisMusic = jMusic.child; thisMusic != null; thisMusic = thisMusic.next) {
                String tmp = thisMusic.asString();
                MaryoGame.game.assets.load(tmp, Music.class);
                if (!levelParsed) music.add(thisMusic.asString());
            }
            if (!levelParsed) level.music = music;
        }
    }

    private void parseBg(JsonValue jLevel) {
        JsonValue jBgs = jLevel.get("backgrounds");
        if (jBgs != null) {
            for (JsonValue jBg = jBgs.child; jBg != null; jBg = jBg.next) {
                int type = jBg.getInt("type", 0);
                if (type == BG_IMG_ALL || type == BG_IMG_BOTTOM || type == BG_IMG_TOP) {
                    String textureName = jBg.getString("texture_name", null);
                    if (textureName != null)
                        MaryoGame.game.assets.load(textureName, Texture.class, Assets.TEXTURE_PARAMETER);
                    if (levelParsed) return;

                    Vector2 speed = new Vector2();

                    speed.x = jBg.getFloat("speedx", 0);
                    speed.y = jBg.getFloat("speedy", 0);

                    Vector2 pos = new Vector2();

                    pos.x = jBg.getFloat("posx", 0);
                    pos.y = jBg.getFloat("posy", 0);

                    float width = jBg.getFloat("width", 0);
                    float height = jBg.getFloat("height", 0);

                    Background bg = new Background(pos, speed, textureName, width, height, level.width, level.height, type);

                    bg.width = jBg.getFloat("width", 0);
                    bg.height = jBg.getFloat("height", 0);
                    level.backgrounds.add(bg);
                } else if (type == BG_GR_VER || type == BG_GR_HOR) {
                    Background bg = new Background(type);
                    float r1 = jBg.getFloat("r_1") / 255;//convert from 0-255 range to 0-1 range
                    float r2 = jBg.getFloat("r_2") / 255;
                    float g1 = jBg.getFloat("g_1") / 255;
                    float g2 = jBg.getFloat("g_2") / 255;
                    float b1 = jBg.getFloat("b_1") / 255;
                    float b2 = jBg.getFloat("b_2") / 255;

                    Color color1 = new Color(r1, g1, b1, 0f);//color is 0-1 range where 1 = 255
                    Color color2 = new Color(r2, g2, b2, 0f);

                    bg.setColors(color1, color2);
                    level.backgrounds.add(bg);
                }
            }
        }
    }

    private void parsePlayer(JsonValue jPlayer, World world) {
        if (levelParsed) return;
        Maryo maryo = new Maryo(jPlayer.getFloat("posx"), jPlayer.getFloat("posy"), Maryo.POSITION_Z, 0.9f, 0.9f);
        world.maryo = maryo;
        level.gameObjects.add(maryo);
    }

    private void parseSprite(JsonValue jSprite) {
        float positionZ = 0;
        Sprite.Type sType = null;
        if (jSprite.has("massive_type")) {
            sType = Sprite.Type.valueOf(jSprite.getString("massive_type"));
            switch (sType) {
                case massive:
                    positionZ = m_pos_z_massive_start;
                    break;
                case passive:
                    positionZ = m_pos_z_passive_start;
                    break;
                case halfmassive:
                    positionZ = m_pos_z_halfmassive_start;
                    break;
                case front_passive:
                    positionZ = m_pos_z_front_passive_start;
                    break;
                case climbable:
                    positionZ = m_pos_z_halfmassive_start;
                    break;
            }
        } else {
            positionZ = m_pos_z_front_passive_start;
        }

        float width = jSprite.getFloat("width");
        float height = jSprite.getFloat("height");

        Rectangle rectangle = new Rectangle();
        rectangle.x = jSprite.getFloat("c_posx", 0);
        rectangle.y = jSprite.getFloat("c_posy", 0);
        rectangle.width = jSprite.getFloat("c_width", width);
        rectangle.height = jSprite.getFloat("c_height", height);
        Sprite sprite = new Sprite(jSprite.getFloat("posx"), jSprite.getFloat("posy"), positionZ, width, height, rectangle);
        sprite.type = sType;
        sprite.groundType = jSprite.getInt("ground_type", Sprite.GROUND_NORMAL);

        sprite.textureName = jSprite.getString("texture_name");
        sprite.textureAtlas = jSprite.getString("texture_atlas", null);

        if (TextUtils.isEmpty(sprite.textureName) && TextUtils.isEmpty(sprite.textureAtlas)) {
            throw new GdxRuntimeException("Both textureName and textureAtlas are null");
        }

        if (TextUtils.isEmpty(sprite.textureName)) {
            throw new IllegalArgumentException("texture name is invalid: \"" + sprite.textureName + "\"");
        }

        if (!TXT_NAME_IN_ATLAS.matcher(sprite.textureName).matches()) {
            MaryoGame.game.assets.load(sprite.textureName, Texture.class, Assets.TEXTURE_PARAMETER);
        }

        if (!TextUtils.isEmpty(sprite.textureAtlas)) {
            MaryoGame.game.assets.load(sprite.textureAtlas, TextureAtlas.class);
        }

        sprite.rotationX = jSprite.getInt("rotationX", 0);
        sprite.rotationY = jSprite.getInt("rotationY", 0);
        sprite.rotationZ = jSprite.getInt("rotationZ", 0);
        if (sprite.rotationZ == 270) {
            sprite.rotationZ = -sprite.rotationZ;
        }
        if (!levelParsed) level.gameObjects.add(sprite);

    }

    private void parseEnemy(JsonValue jEnemy) {
        Enemy enemy = initEnemy(jEnemy);
        if (enemy == null) return;
        if (jEnemy.has("texture_atlas")) {
            enemy.textureAtlas = jEnemy.getString("texture_atlas");
            MaryoGame.game.assets.load(enemy.textureAtlas, TextureAtlas.class);
        }
        if (jEnemy.has("texture_name"))
            enemy.textureName = jEnemy.getString("texture_name");

        if (!levelParsed) level.gameObjects.add(enemy);
    }

    private Enemy initEnemy(JsonValue jEnemy) {
        float x = jEnemy.getFloat("posx");
        float y = jEnemy.getFloat("posy");
        String enemyClassString = jEnemy.getString("enemy_class");
        float width = jEnemy.getFloat("width");
        float height = jEnemy.getFloat("height");
        EnemyClass enemyClass = EnemyClass.fromString(enemyClassString);
        Enemy enemy = null;
        switch (enemyClass) {
            case eato:
                enemy = new Eato(x, y, 0, width, height, jEnemy.getString("direction", ""), jEnemy.getString("color", ""));
                break;
            case flyon:
                enemy = new Flyon(x, y, 0, width, height, jEnemy.getFloat("max_distance"), jEnemy.getFloat("speed"), jEnemy.getString("direction", "up"));
                break;
            case furball:
                enemy = new Furball(x, y, 0, width, height, jEnemy.getInt("max_downgrade_count", 0), jEnemy.getString("color", ""));
                break;
            case turtle:
                enemy = new Turtle(x, y, 0, width, height, jEnemy.getString("color", ""));
                break;
            case gee:
                enemy = new Gee(x, y, 0, width, height, jEnemy.getFloat("fly_distance"), jEnemy.getString("color"), jEnemy.getString("direction"), jEnemy.getFloat("wait_time"));
                break;
            case krush:
                enemy = new Krush(x, y, 0, width, height);
                break;
            case thromp:
                enemy = new Thromp(x, y, 0, width, height, jEnemy.getFloat("max_distance"), jEnemy.getFloat("speed"), jEnemy.getString("direction", "up"));
                break;
            case spika:
                enemy = new Spika(x, y, 0, width, height, jEnemy.getString("color", ""));
                break;
            case rokko:
                enemy = new Rokko(x, y, 0, width, height, jEnemy.getString("direction", ""));
                MaryoGame.game.assets.load(Assets.SOUND_ENEMY_ROKKO_HIT, Sound.class);
                break;
            case _static:
                enemy = new Static(x, y, 0, width, height, jEnemy.getInt("rotation_speed", 0), jEnemy.getInt("fire_resistance", 0), jEnemy.getInt("ice_resistance", 0));
                break;
            case spikeball:
                enemy = new Spikeball(x, y, 0, width, height);
                break;
        }
        return enemy;
    }


    private void parseEnemyStopper(JsonValue jEnemyStopper) {
        if (levelParsed) return;

        EnemyStopper stopper = new EnemyStopper(jEnemyStopper.getFloat("posx"), jEnemyStopper.getFloat("posy"), 0, jEnemyStopper.getFloat("width"), jEnemyStopper.getFloat("height"));

        level.gameObjects.add(stopper);
    }

    private void parseLevelEntry(JsonValue jEntry) {
        if (levelParsed) return;

        LevelEntry entry = new LevelEntry(jEntry.getFloat("posx"), jEntry.getFloat("posy"), 0, jEntry.getFloat("width"), jEntry.getFloat("height"));
        entry.direction = jEntry.getString("direction", "");
        entry.type = jEntry.getInt("type", 0);
        entry.name = jEntry.getString("name", "");

        level.gameObjects.add(entry);
    }

    private void parseLevelExit(JsonValue jExit) {
        if (levelParsed) return;
        LevelExit exit = new LevelExit(jExit.getFloat("posx"), jExit.getFloat("posy"), 0, jExit.getFloat("width"),
                jExit.getFloat("height"), jExit.getInt("type", 0), jExit.getString("direction", ""));
        exit.cameraMotion = jExit.getInt("camera_motion", 0);
        exit.levelName = jExit.getString("level_name", null);
        exit.entry = jExit.getString("entry", "");

        level.gameObjects.add(exit);
    }

    private void parseMovingPlatform(JsonValue jMovingPlatform) {
        if (levelParsed) return;
        float positionZ = 0;
        Sprite.Type sType = null;
        if (jMovingPlatform.has("massive_type")) {
            sType = Sprite.Type.valueOf(jMovingPlatform.getString("massive_type"));
            switch (sType) {
                case massive:
                    positionZ = m_pos_z_massive_start;
                    break;
                case passive:
                    positionZ = m_pos_z_passive_start;
                    break;
                case halfmassive:
                    positionZ = m_pos_z_halfmassive_start;
                    break;
                case front_passive:
                    positionZ = m_pos_z_front_passive_start;
                    break;
                case climbable:
                    positionZ = m_pos_z_halfmassive_start;
                    break;
            }
        } else {
            positionZ = m_pos_z_front_passive_start;
        }
        MovingPlatform platform = new MovingPlatform(jMovingPlatform.getFloat("posx"), jMovingPlatform.getFloat("posy"),
                positionZ, jMovingPlatform.getFloat("width"), jMovingPlatform.getFloat("height"), null);
        platform.max_distance = jMovingPlatform.getInt("max_distance", 0);
        platform.speed = jMovingPlatform.getFloat("speed", 0);
        platform.touch_time = jMovingPlatform.getFloat("touch_time", 0);
        platform.shake_time = jMovingPlatform.getFloat("shake_time", 0);
        platform.touch_move_time = jMovingPlatform.getFloat("touch_move_time", 0);
        platform.move_type = jMovingPlatform.getInt("move_type", 0);
        platform.middle_img_count = jMovingPlatform.getInt("middle_img_count", 0);
        platform.direction = jMovingPlatform.getString("direction", "");
        platform.image_top_left = jMovingPlatform.getString("image_top_left", "");
        platform.image_top_middle = jMovingPlatform.getString("image_top_middle", "");
        platform.image_top_right = jMovingPlatform.getString("image_top_right", "");
        platform.textureAtlas = jMovingPlatform.getString("texture_atlas", "");
        if (platform.textureAtlas != null && !platform.textureAtlas.trim().isEmpty()) {
            MaryoGame.game.assets.load(platform.textureAtlas, TextureAtlas.class);
        } else {
            MaryoGame.game.assets.load(platform.image_top_left, Texture.class);
            MaryoGame.game.assets.load(platform.image_top_middle, Texture.class);
            MaryoGame.game.assets.load(platform.image_top_right, Texture.class);
        }

        platform.type = sType;

        JsonValue jPath = jMovingPlatform.get("path");
        if (platform.move_type == MovingPlatform.MOVING_PLATFORM_TYPE_PATH && jPath == null) {
            throw new GdxRuntimeException("MovingPlatform type is 'path' but no path defined");
        }
        if (jPath != null) {
            MovingPlatform.Path path = new MovingPlatform.Path();
            path.posx = jPath.getFloat("posx", 0);
            path.posy = jPath.getFloat("posy", 0);
            path.rewind = jPath.getInt("rewind", 0);

            JsonValue jSegments = jPath.get("segments");
            if (jSegments == null || jSegments.child == null) {
                throw new GdxRuntimeException("Path doesn't contain segments. Level: " + level.levelName);
            }
            for (JsonValue jSegment = jSegments.child; jSegment != null; jSegment = jSegment.next) {
                MovingPlatform.Path.Segment segment = new MovingPlatform.Path.Segment();
                segment.start.x = jSegment.getFloat("startx", 0);
                segment.start.y = jSegment.getFloat("starty", 0);
                segment.end.x = jSegment.getFloat("endx", 0);
                segment.end.y = jSegment.getFloat("endy", 0);
                path.segments.add(segment);
            }
            platform.path = path;
        }

        level.gameObjects.add(platform);
    }

    private void parseItem(JsonValue jItem) {
        String type = jItem.getString("type");
        int itemSubtype;
        switch (Item.CLASS.valueOf(type)) {
            case mushroom:
                itemSubtype = jItem.getInt("mushroom_type", 0);
                break;
            case goldpiece:
                itemSubtype = (jItem.getString("color", "").equals("red") ? Coin.TYPE_RED : Coin.TYPE_YELLOW);
                break;
            default:
                itemSubtype = 0;
        }

        Item item = Item.createObject(itemSubtype, type, jItem.getFloat("posx", 0), jItem.getFloat("posy", 0),
                0, jItem.getFloat("width"), jItem.getFloat("height"));

        if (item == null) return;
        if (jItem.has("texture_atlas")) {
            item.textureAtlas = jItem.getString("texture_atlas");
            MaryoGame.game.assets.load(item.textureAtlas, TextureAtlas.class);
        }
        if (!levelParsed) level.gameObjects.add(item);
    }

    private void parseBox(JsonValue jBox) {
        Box box = Box.initBox(jBox);
        if (!levelParsed) level.gameObjects.add(box);
    }

    /**
     * Comparator used for sorting, sorts in ascending order (biggset z to smallest z).
     *
     * @author mzechner
     */
    public static class ZSpriteComparator implements Comparator<GameObject> {
        @Override
        public int compare(GameObject sprite1, GameObject sprite2) {
            if (sprite1.position.z > sprite2.position.z) return 1;
            if (sprite1.position.z < sprite2.position.z) return -1;
            return 0;
        }
    }

}
