package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.Box;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Level;
import rs.pedjaapps.smc.object.LevelEntry;
import rs.pedjaapps.smc.object.LevelExit;
import rs.pedjaapps.smc.object.MovingPlatform;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.enemy.Enemy;
import rs.pedjaapps.smc.object.enemy.EnemyStopper;
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
public class LevelLoader
{
    public static final Pattern TXT_NAME_IN_ATLAS = Pattern.compile(".+\\.pack:.+");
    public Level level;
    private boolean levelParsed = false;

    private enum ObjectClass
    {
        sprite, item, box, player, enemy, moving_platform, enemy_stopper, level_entry, level_exit,
    }

    public static final float m_pos_z_passive_start = 0.01f;
    private static final float m_pos_z_massive_start = 0.08f;
    private static final float m_pos_z_front_passive_start = 0.1f;
    private static final float m_pos_z_halfmassive_start = 0.04f;

    /**
     * Use this constructor only from pc when you want to automatically fix assets dependencies
     */
    public LevelLoader(String levelName)
    {
        level = new Level(levelName);
    }

    public synchronized void parseLevel(World world)
    {
        JsonValue jLevel;
        try
        {
            jLevel = new JsonReader().parse (Gdx.files.internal("data/levels/" + level.levelName + Level.LEVEL_EXT));
            parseInfo(jLevel, world.screen.game.assets);
            parseParticleEffect(jLevel, world.screen.game.assets);
            parseBg(jLevel, world.screen.game.assets);
            parseGameObjects(world, jLevel, world.screen.game.assets);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw new RuntimeException("Unable to load level! " + e.getMessage());
        }
        levelParsed = true;
    }

    private void parseParticleEffect(JsonValue jLevel, Assets assets)
    {
        JsonValue jParticleEffect = jLevel.get("particle_effect");
        if(jParticleEffect != null)
        {
            String effect = jParticleEffect.getString("effect", "");
            if(!TextUtils.isEmpty(effect))
            {
                assets.manager.load(effect, ParticleEffect.class, assets.particleEffectParameter);
                level.particleEffect = effect;
            }
        }
    }

    private void parseGameObjects(World world, JsonValue level, Assets assets)
    {
        JsonValue jObjects = level.get("objects");
        for (JsonValue jObject = jObjects.child; jObject != null; jObject = jObject.next)
        {
            switch (ObjectClass.valueOf(jObject.getString("obj_class")))
            {
                case sprite:
                    parseSprite(world, jObject, assets);
                    break;
                case player:
                    parsePlayer(jObject, world);
                    break;
                case item:
                    parseItem(world, jObject, assets);
                    break;
                case enemy:
                    parseEnemy(world, jObject, assets);
                    break;
                case enemy_stopper:
                    parseEnemyStopper(world, jObject);
                    break;
                case box:
                    parseBox(world, jObject, assets);
                    break;
                case level_entry:
                    /*{"direction":"up","posy":-228,"name":"1","posx":8074,"type":1,"obj_class":"level_entry"}*/
                    parseLevelEntry(world, jObject);
                    break;
                case level_exit:
                    parseLevelExit(world, jObject);
                    break;
                case moving_platform:
                    parseMovingPlatform(world, jObject, assets);
                    break;
            }
        }
        //this.level.gameObjects.sort(new ZSpriteComparator());
        Collections.sort(this.level.gameObjects, new ZSpriteComparator());
    }

    private void parseInfo(JsonValue jLevel, Assets assets)
    {
        JsonValue jInfo = jLevel.get("info");
        float width = jInfo.getFloat("level_width");
        float height = jInfo.getFloat("level_height");
        level.width = width;
        level.height = Math.max(height, Constants.CAMERA_HEIGHT);
        if (jInfo.has("level_music"))
        {
            JsonValue jMusic = jInfo.get("level_music");
            Array<String> music = new Array<>();
            for (JsonValue thisMusic = jMusic.child; thisMusic != null; thisMusic = thisMusic.next)
            {
                String tmp = thisMusic.asString();
                assets.manager.load(tmp, Music.class);
                if (!levelParsed) music.add(thisMusic.asString());
            }
            if (!levelParsed) level.music = music;
        }
    }

    private void parseBg(JsonValue jLevel, Assets assets)
    {
        JsonValue jBgs = jLevel.get("backgrounds");
        if (jBgs != null)
        {
            for (JsonValue jBg = jBgs.child; jBg != null; jBg = jBg.next)
            {
                int type = jBg.getInt("type", 0);
                if (type == BG_IMG_ALL || type == BG_IMG_BOTTOM || type == BG_IMG_TOP)
                {
                    String textureName = jBg.getString("texture_name", null);
                    if (textureName != null)
                        assets.manager.load(textureName, Texture.class, assets.textureParameter);
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
                }
                else if (type == BG_GR_VER || type == BG_GR_HOR)
                {
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

    private void parsePlayer(JsonValue jPlayer, World world)
    {
        if (levelParsed) return;
        float x = jPlayer.getFloat("posx");
        float y = jPlayer.getFloat("posy");
        level.spanPosition = new Vector3(x, y, Maryo.POSITION_Z);
        Maryo maryo = new Maryo(world, level.spanPosition, new Vector2(0.9f, 0.9f));
        world.maryo = maryo;
        level.gameObjects.add(maryo);
    }

    private void parseSprite(World world, JsonValue jSprite, Assets assets)
    {
        Vector3 position = new Vector3(jSprite.getFloat("posx"), jSprite.getFloat("posy"), 0);
        Sprite.Type sType = null;
        if (jSprite.has("massive_type"))
        {
            sType = Sprite.Type.valueOf(jSprite.getString("massive_type"));
            switch (sType)
            {
                case massive:
                    position.z = m_pos_z_massive_start;
                    break;
                case passive:
                    position.z = m_pos_z_passive_start;
                    break;
                case halfmassive:
                    position.z = m_pos_z_halfmassive_start;
                    break;
                case front_passive:
                    position.z = m_pos_z_front_passive_start;
                    break;
                case climbable:
                    position.z = m_pos_z_halfmassive_start;
                    break;
            }
        }
        else
        {
            position.z = m_pos_z_front_passive_start;
        }
        Vector2 size = new Vector2(jSprite.getFloat("width"), jSprite.getFloat("height"));

        Rectangle rectangle = new Rectangle();
        rectangle.x = jSprite.getFloat("c_posx", 0);
        rectangle.y = jSprite.getFloat("c_posy", 0);
        rectangle.width = jSprite.getFloat("c_width", size.x);
        rectangle.height = jSprite.getFloat("c_height", size.y);
        Sprite sprite = new Sprite(world, size, position, rectangle);
        sprite.type = sType;
        sprite.groundType = jSprite.getInt("ground_type", Sprite.GROUND_NORMAL);

        sprite.textureName = jSprite.getString("texture_name");
        sprite.textureAtlas = jSprite.getString("texture_atlas", null);

        if (TextUtils.isEmpty(sprite.textureName) && TextUtils.isEmpty(sprite.textureAtlas))
        {
            throw new GdxRuntimeException("Both textureName and textureAtlas are null");
        }

        if (TextUtils.isEmpty(sprite.textureName))
        {
            throw new IllegalArgumentException("texture name is invalid: \"" + sprite.textureName + "\"");
        }

        if (!TXT_NAME_IN_ATLAS.matcher(sprite.textureName).matches())
        {
            assets.manager.load(sprite.textureName, Texture.class, assets.textureParameter);
        }

        if (!TextUtils.isEmpty(sprite.textureAtlas))
        {
            assets.manager.load(sprite.textureAtlas, TextureAtlas.class);
        }

        sprite.mRotationX = jSprite.getInt("rotationX", 0);
        sprite.mRotationY = jSprite.getInt("rotationY", 0);
        sprite.mRotationZ = jSprite.getInt("rotationZ", 0);
        if (sprite.mRotationZ == 270)
        {
            sprite.mRotationZ = -sprite.mRotationZ;
        }
        if (!levelParsed) level.gameObjects.add(sprite);

    }

    private void parseEnemy(World world, JsonValue jEnemy, Assets assets)
    {
        Enemy enemy = Enemy.initEnemy(world, jEnemy);
        if (enemy == null) return;
        if (jEnemy.has("texture_atlas"))
        {
            enemy.textureAtlas = jEnemy.getString("texture_atlas");
            assets.manager.load(enemy.textureAtlas, TextureAtlas.class);
        }
        if (jEnemy.has("texture_name"))
        {
            enemy.textureName = jEnemy.getString("texture_name");
            assets.manager.load(enemy.textureName, Texture.class, assets.textureParameter);
        }
        if (!levelParsed) level.gameObjects.add(enemy);
    }

    private void parseEnemyStopper(World world, JsonValue jEnemyStopper)
    {
        if (levelParsed) return;
        Vector3 position = new Vector3(jEnemyStopper.getFloat("posx"), jEnemyStopper.getFloat("posy"), 0);
        float width = jEnemyStopper.getFloat("width");
        float height = jEnemyStopper.getFloat("height");

        EnemyStopper stopper = new EnemyStopper(world, new Vector2(width, height), position);

        level.gameObjects.add(stopper);
    }

    private void parseLevelEntry(World world, JsonValue jEntry)
    {
        if (levelParsed) return;
        Vector3 position = new Vector3(jEntry.getFloat("posx"), jEntry.getFloat("posy"), 0);
        float width = jEntry.getFloat("width");
        float height = jEntry.getFloat("height");

        LevelEntry entry = new LevelEntry(world, new Vector2(width, height), position);
        entry.direction = jEntry.getString("direction", "");
        entry.type = jEntry.getInt("type", 0);
        entry.name = jEntry.getString("name", "");

        level.gameObjects.add(entry);
    }

    private void parseLevelExit(World world, JsonValue jExit)
    {
        if (levelParsed) return;
        Vector3 position = new Vector3(jExit.getFloat("posx"), jExit.getFloat("posy"), 0);
        float width = jExit.getFloat("width");
        float height = jExit.getFloat("height");
        LevelExit exit = new LevelExit(world, new Vector2(width, height), position, jExit.getInt("type", 0), jExit.getString("direction", ""));
        exit.cameraMotion = jExit.getInt("camera_motion", 0);
        exit.levelName = jExit.getString("level_name", null);
        exit.entry = jExit.getString("entry", "");

        level.gameObjects.add(exit);
    }

    private void parseMovingPlatform(World world, JsonValue jMovingPlatform, Assets assets)
    {
        if (levelParsed) return;
        Vector3 position = new Vector3(jMovingPlatform.getFloat("posx"), jMovingPlatform.getFloat("posy"), 0);
        float width = jMovingPlatform.getFloat("width");
        float height = jMovingPlatform.getFloat("height");
        MovingPlatform platform = new MovingPlatform(world, new Vector2(width, height), position, null);
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
        if (platform.textureAtlas != null && !platform.textureAtlas.trim().isEmpty())
        {
            assets.manager.load(platform.textureAtlas, TextureAtlas.class);
        }
        else
        {
            assets.manager.load(platform.image_top_left, Texture.class);
            assets.manager.load(platform.image_top_middle, Texture.class);
            assets.manager.load(platform.image_top_right, Texture.class);
        }

        Sprite.Type sType = null;
        if (jMovingPlatform.has("massive_type"))
        {
            sType = Sprite.Type.valueOf(jMovingPlatform.getString("massive_type"));
            switch (sType)
            {
                case massive:
                    position.z = m_pos_z_massive_start;
                    break;
                case passive:
                    position.z = m_pos_z_passive_start;
                    break;
                case halfmassive:
                    position.z = m_pos_z_halfmassive_start;
                    break;
                case front_passive:
                    position.z = m_pos_z_front_passive_start;
                    break;
                case climbable:
                    position.z = m_pos_z_halfmassive_start;
                    break;
            }
        }
        else
        {
            position.z = m_pos_z_front_passive_start;
        }
        platform.type = sType;

        JsonValue jPath = jMovingPlatform.get("path");
        if (platform.move_type == MovingPlatform.MOVING_PLATFORM_TYPE_PATH && jPath == null)
        {
            throw new GdxRuntimeException("MovingPlatform type is 'path' but no path defined");
        }
        if (jPath != null)
        {
            MovingPlatform.Path path = new MovingPlatform.Path();
            path.posx = jPath.getFloat("posx", 0);
            path.posy = jPath.getFloat("posy", 0);
            path.rewind = jPath.getInt("rewind", 0);

            JsonValue jSegments = jPath.get("segments");
            if (jSegments == null || jSegments.child == null)
            {
                throw new GdxRuntimeException("Path doesn't contain segments. Level: " + level.levelName);
            }
            for (JsonValue jSegment = jSegments.child; jSegment != null; jSegment = jSegment.next)
            {
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

    private void parseItem(World world, JsonValue jItem, Assets assets)
    {
        Vector3 position = new Vector3(jItem.getFloat("posx", 0), jItem.getFloat("posy", 0), 0);

        Item item = Item.createObject(world, assets, jItem.getInt("mushroom_type", 0), jItem.getString("type"), new Vector2(jItem.getFloat("width"), jItem.getFloat("height")), position);
        if (item == null) return;
        if (jItem.has("texture_atlas"))
        {
            item.textureAtlas = jItem.getString("texture_atlas");
            assets.manager.load(item.textureAtlas, TextureAtlas.class);
        }
        if (!levelParsed) level.gameObjects.add(item);
    }

    private void parseBox(World world, JsonValue jBox, Assets assets)
    {
        Box box = Box.initBox(world, jBox, assets);
        if (!levelParsed) level.gameObjects.add(box);
    }

    /**
     * Comparator used for sorting, sorts in ascending order (biggset z to smallest z).
     *
     * @author mzechner
     */
    public static class ZSpriteComparator implements Comparator<GameObject>
    {
        @Override
        public int compare(GameObject sprite1, GameObject sprite2)
        {
            if (sprite1.position.z > sprite2.position.z) return 1;
            if (sprite1.position.z < sprite2.position.z) return -1;
            return 0;
        }
    }

}
