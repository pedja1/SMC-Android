package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.controller.MarioController;
import rs.pedjaapps.smc.object.Background;
import rs.pedjaapps.smc.object.BackgroundColor;
import rs.pedjaapps.smc.object.Box;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Level;
import rs.pedjaapps.smc.object.LevelEntry;
import rs.pedjaapps.smc.object.LevelExit;
import rs.pedjaapps.smc.object.Maryo;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.enemy.Enemy;
import rs.pedjaapps.smc.object.enemy.EnemyStopper;
import rs.pedjaapps.smc.object.items.Item;

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
    boolean levelParsed = false;

    public enum KEY
    {
        sprites, posx, posy, width, height, texture_atlas, texture_name, info, player, level_width,
        level_height, collision_bodies, flip_x, flip_y, is_front, background, r_1, r_2,
        g_1, g_2, b_1, b_2, level_music, enemies, enemy_class, objects, object_class, obj_class,
        massive_type, type, enemy_filter, gold_color, item, text, useable_count, invisible, animation,
        force_best_item, max_downgrade_count, direction, level_name, name, camera_motion, entry, color,
        max_distance, speed
    }

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

    public synchronized void parseLevel(World world, MarioController controller)
    {
        JSONObject jLevel;
        try
        {
            jLevel = new JSONObject(Gdx.files.internal("data/levels/" + level.levelName + Level.LEVEL_EXT).readString());
            parseInfo(jLevel);
            parseBg(jLevel);
            parseGameObjects(world, controller, jLevel);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Unable to load level! " + e.getMessage());
        }
        levelParsed = true;
    }

    private void parseGameObjects(World world, MarioController controller, JSONObject level) throws JSONException
    {
        JSONArray jObjects = level.getJSONArray(KEY.objects.toString());
        for (int i = 0; i < jObjects.length(); i++)
        {
            JSONObject jObject = jObjects.getJSONObject(i);
            switch (ObjectClass.valueOf(jObject.getString(KEY.obj_class.toString())))
            {
                case sprite:
                    parseSprite(world, jObject);
                    break;
                case player:
                    parsePlayer(jObject, world, controller);
                    break;
                case item:
                    parseItem(world, jObject);
                    break;
                case enemy:
                    parseEnemy(world, jObject);
                    break;
                case enemy_stopper:
                    parseEnemyStopper(world, jObject);
                    break;
                case box:
                    parseBox(world, jObject);
                    break;
                case level_entry:
                    /*{"direction":"up","posy":-228,"name":"1","posx":8074,"type":1,"obj_class":"level_entry"}*/
                    parseLevelEntry(world, jObject);
                    break;
                case level_exit:
                    parseLevelExit(world, jObject);
                    break;
            }
        }
        //this.level.gameObjects.sort(new ZSpriteComparator());
        Collections.sort(this.level.gameObjects, new ZSpriteComparator());
    }

    private void parseInfo(JSONObject jLevel) throws JSONException
    {
        JSONObject jInfo = jLevel.getJSONObject(KEY.info.toString());
        float width = (float) jInfo.getDouble(KEY.level_width.toString());
        float height = (float) jInfo.getDouble(KEY.level_height.toString());
        level.width = width;
        level.height = height;
        if (jInfo.has(KEY.level_music.toString()))
        {
            JSONArray jMusic = jInfo.getJSONArray(KEY.level_music.toString());
            Array<String> music = new Array<String>();
            for (int i = 0; i < jMusic.length(); i++)
            {
                String tmp = jMusic.getString(i);
                Assets.manager.load(tmp, Music.class);
                if(!levelParsed)music.add(jMusic.getString(i));
            }
            if(!levelParsed)level.music = music;
        }
    }

    private void parseBg(JSONObject jLevel) throws JSONException
    {
        if (jLevel.has(KEY.background.toString()))
        {
            JSONObject jBg = jLevel.getJSONObject(KEY.background.toString());
            String textureName = jBg.optString(KEY.texture_name.toString(), null);
            if(textureName != null)Assets.manager.load(textureName, Texture.class, Assets.textureParameter);
            if(levelParsed)return;

			if(textureName != null)
			{
            	Background bg = new Background(new Vector2(0, 0), textureName);
            	level.bg1 = bg;
            	bg = new Background(new Vector2(Background.WIDTH, 0), textureName);
            	level.bg2 = bg;
			}
            //TODO this is stupid, we should dinamically repeat background

            float r1 = (float) jBg.getDouble(KEY.r_1.toString()) / 255;//convert from 0-255 range to 0-1 range
            float r2 = (float) jBg.getDouble(KEY.r_2.toString()) / 255;
            float g1 = (float) jBg.getDouble(KEY.g_1.toString()) / 255;
            float g2 = (float) jBg.getDouble(KEY.g_2.toString()) / 255;
            float b1 = (float) jBg.getDouble(KEY.b_1.toString()) / 255;
            float b2 = (float) jBg.getDouble(KEY.b_2.toString()) / 255;

            BackgroundColor bgColor = new BackgroundColor();
            bgColor.color1 = new Color(r1, g1, b1, 0f);//color is 0-1 range where 1 = 255
            bgColor.color2 = new Color(r2, g2, b2, 0f);
            level.bgColor = bgColor;
        }
        else
        {
            throw new IllegalStateException("level must have \"background\" object");
        }
    }

    private void parsePlayer(JSONObject jPlayer, World world, MarioController controller) throws JSONException
    {
        if(levelParsed)return;
        float x = (float) jPlayer.getDouble(KEY.posx.toString());
        float y = (float) jPlayer.getDouble(KEY.posy.toString());
        level.spanPosition = new Vector3(x, y, Maryo.POSITION_Z);
        if (controller != null)
        {
            Maryo maryo = new Maryo(world, level.spanPosition, new Vector2(0.9f, 0.9f));
            world.maryo = maryo;
            level.gameObjects.add(maryo);
            controller.setMaryo(maryo);
        }
    }

    private void parseSprite(World world, JSONObject jSprite) throws JSONException
    {
        Vector3 position = new Vector3((float) jSprite.getDouble(KEY.posx.toString()), (float) jSprite.getDouble(KEY.posy.toString()), 0);
        Sprite.Type sType = null;
        if (jSprite.has(KEY.massive_type.toString()))
        {
            sType = Sprite.Type.valueOf(jSprite.getString(KEY.massive_type.toString()));
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

        Sprite sprite = new Sprite(world, new Vector2((float) jSprite.getDouble(KEY.width.toString()), (float) jSprite.getDouble(KEY.height.toString())), position);
        sprite.type = sType;

        sprite.textureName = jSprite.getString(KEY.texture_name.toString());
        if (sprite.textureName == null || sprite.textureName.isEmpty())
        {
            throw new IllegalArgumentException("texture name is invalid: \"" + sprite.textureName + "\"");
        }

        if(!TXT_NAME_IN_ATLAS.matcher(sprite.textureName).matches())
        {
            Assets.manager.load(sprite.textureName, Texture.class, Assets.textureParameter);
        }
        if (jSprite.has(KEY.is_front.toString()))
        {
            sprite.isFront = jSprite.getBoolean(KEY.is_front.toString());
        }

        sprite.textureAtlas = jSprite.optString(KEY.texture_atlas.toString(), null);
        if(sprite.textureAtlas != null)
        {
            Assets.manager.load(sprite.textureAtlas, TextureAtlas.class);
        }
        sprite.mRotationX = jSprite.optInt("rotationX");
        sprite.mRotationY = jSprite.optInt("rotationY");
        sprite.mRotationZ = jSprite.optInt("rotationZ");
        if(!levelParsed)level.gameObjects.add(sprite);

    }

    private void parseEnemy(World world, JSONObject jEnemy) throws JSONException
    {
        Enemy enemy = Enemy.initEnemy(world, jEnemy);
        if (enemy == null) return;//TODO this has to go aways after levels are fixed
        if (jEnemy.has(KEY.texture_atlas.toString()))
        {
            enemy.textureAtlas = jEnemy.getString(KEY.texture_atlas.toString());
            Assets.manager.load(enemy.textureAtlas, TextureAtlas.class);
        }
        if(!levelParsed)level.gameObjects.add(enemy);
    }

    private void parseEnemyStopper(World world, JSONObject jEnemyStopper) throws JSONException
    {
        if(levelParsed)return;
        Vector3 position = new Vector3((float) jEnemyStopper.getDouble(KEY.posx.toString()), (float) jEnemyStopper.getDouble(KEY.posy.toString()), 0);
        float width = (float) jEnemyStopper.getDouble(KEY.width.toString());
        float height = (float) jEnemyStopper.getDouble(KEY.height.toString());

        EnemyStopper stopper = new EnemyStopper(world, new Vector2(width, height), position);

        level.gameObjects.add(stopper);
    }

    private void parseLevelEntry(World world, JSONObject jEntry) throws JSONException
    {
        if(levelParsed)return;
        Vector3 position = new Vector3((float) jEntry.getDouble(KEY.posx.toString()), (float) jEntry.getDouble(KEY.posy.toString()), 0);
        float width = (float) jEntry.getDouble(KEY.width.toString());
        float height = (float) jEntry.getDouble(KEY.height.toString());

        LevelEntry entry = new LevelEntry(world, new Vector2(width, height), position);
        entry.direction = jEntry.optString(KEY.direction.toString());
        entry.type = jEntry.optInt(KEY.type.toString());
        entry.name = jEntry.optString(KEY.name.toString());

        level.gameObjects.add(entry);
    }

    private void parseLevelExit(World world, JSONObject jExit) throws JSONException
    {
        if(levelParsed)return;
        Vector3 position = new Vector3((float) jExit.getDouble(KEY.posx.toString()), (float) jExit.getDouble(KEY.posy.toString()), 0);
        float width = (float) jExit.getDouble(KEY.width.toString());
        float height = (float) jExit.getDouble(KEY.height.toString());
        LevelExit exit = new LevelExit(world, new Vector2(width, height), position);
        exit.cameraMotion = jExit.optInt(KEY.camera_motion.toString());
        exit.type = jExit.optInt(KEY.type.toString());
        exit.levelName = jExit.optString(KEY.level_name.toString(), null);
        exit.entry = jExit.optString(KEY.entry.toString());
        exit.direction = jExit.optString(KEY.direction.toString());

        level.gameObjects.add(exit);
    }

    private void parseItem(World world, JSONObject jItem) throws JSONException
    {
        Vector3 position = new Vector3((float) jItem.getDouble(KEY.posx.toString()), (float) jItem.getDouble(KEY.posy.toString()), 0);

        Item item = Item.initObject(world, jItem.getString(KEY.type.toString()), new Vector2((float) jItem.getDouble(KEY.width.toString()), (float) jItem.getDouble(KEY.height.toString())), position);
        if (item == null) return;
        if (jItem.has(KEY.texture_atlas.toString()))
        {
            item.textureAtlas = jItem.getString(KEY.texture_atlas.toString());
            Assets.manager.load(item.textureAtlas, TextureAtlas.class);
        }
        if(!levelParsed)level.gameObjects.add(item);
    }

    private void parseBox(World world, JSONObject jBox) throws JSONException
    {
        Box box = Box.initBox(world, jBox, this);
        if(!levelParsed)level.gameObjects.add(box);
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
