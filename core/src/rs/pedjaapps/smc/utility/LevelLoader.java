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
import rs.pedjaapps.smc.view.Background;
import rs.pedjaapps.smc.object.Box;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Level;
import rs.pedjaapps.smc.object.LevelEntry;
import rs.pedjaapps.smc.object.LevelExit;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.enemy.Enemy;
import rs.pedjaapps.smc.object.enemy.EnemyStopper;
import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.object.maryo.Maryo;

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
        JSONObject jLevel;
        try
        {
            jLevel = new JSONObject(Gdx.files.internal("data/levels/" + level.levelName + Level.LEVEL_EXT).readString());
            parseInfo(jLevel);
            parseBg(jLevel);
            parseGameObjects(world, jLevel);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Unable to load level! " + e.getMessage());
        }
        levelParsed = true;
    }

    private void parseGameObjects(World world, JSONObject level) throws JSONException
    {
        JSONArray jObjects = level.getJSONArray("objects");
        for (int i = 0; i < jObjects.length(); i++)
        {
            JSONObject jObject = jObjects.getJSONObject(i);
            switch (ObjectClass.valueOf(jObject.getString("obj_class")))
            {
                case sprite:
                    parseSprite(world, jObject);
                    break;
                case player:
                    parsePlayer(jObject, world);
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
        JSONObject jInfo = jLevel.getJSONObject("info");
        float width = (float) jInfo.getDouble("level_width");
        float height = (float) jInfo.getDouble("level_height");
        level.width = width;
        level.height = height;
        if (jInfo.has("level_music"))
        {
            JSONArray jMusic = jInfo.getJSONArray("level_music");
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
        if (jLevel.has("background"))
        {
            JSONObject jBg = jLevel.getJSONObject("background");
            String textureName = jBg.optString("texture_name", null);
            if(textureName != null)Assets.manager.load(textureName, Texture.class, Assets.textureParameter);
            if(levelParsed)return;
			
			Vector2 speed = new Vector2();
			
			speed.x = (float) jBg.optDouble("speedx");
			speed.y = (float) jBg.optDouble("speedy");
			
			Vector2 pos = new Vector2();

			pos.x = (float) jBg.optDouble("posx");
			pos.y = (float) jBg.optDouble("posy");
			
            Background bg = new Background(pos, speed, textureName);
            
			bg.width = (float) jBg.optDouble("width");
			bg.height = (float) jBg.optDouble("height");
			
            float r1 = (float) jBg.getDouble("r_1") / 255;//convert from 0-255 range to 0-1 range
            float r2 = (float) jBg.getDouble("r_2") / 255;
            float g1 = (float) jBg.getDouble("g_1") / 255;
            float g2 = (float) jBg.getDouble("g_2") / 255;
            float b1 = (float) jBg.getDouble("b_1") / 255;
            float b2 = (float) jBg.getDouble("b_2") / 255;

            bg.color1 = new Color(r1, g1, b1, 0f);//color is 0-1 range where 1 = 255
            bg.color2 = new Color(r2, g2, b2, 0f);
			level.background = bg;
        }
        else
        {
            throw new IllegalStateException("level must have \"background\" object");
        }
    }

    private void parsePlayer(JSONObject jPlayer, World world) throws JSONException
    {
        if(levelParsed)return;
        float x = (float) jPlayer.getDouble("posx");
        float y = (float) jPlayer.getDouble("posy");
        level.spanPosition = new Vector3(x, y, Maryo.POSITION_Z);
        Maryo maryo = new Maryo(world, level.spanPosition, new Vector2(0.9f, 0.9f));
        world.maryo = maryo;
        level.gameObjects.add(maryo);
    }

    private void parseSprite(World world, JSONObject jSprite) throws JSONException
    {
        Vector3 position = new Vector3((float) jSprite.getDouble("posx"), (float) jSprite.getDouble("posy"), 0);
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

        Sprite sprite = new Sprite(world, new Vector2((float) jSprite.getDouble("width"), (float) jSprite.getDouble("height")), position);
        sprite.type = sType;

        sprite.textureName = jSprite.getString("texture_name");
        if (sprite.textureName == null || sprite.textureName.isEmpty())
        {
            throw new IllegalArgumentException("texture name is invalid: \"" + sprite.textureName + "\"");
        }

        if(!TXT_NAME_IN_ATLAS.matcher(sprite.textureName).matches())
        {
            Assets.manager.load(sprite.textureName, Texture.class, Assets.textureParameter);
        }
        if (jSprite.has("is_front"))
        {
            sprite.isFront = jSprite.getBoolean("is_front");
        }

        sprite.textureAtlas = jSprite.optString("texture_atlas", null);
        if(sprite.textureAtlas != null)
        {
            Assets.manager.load(sprite.textureAtlas, TextureAtlas.class, Assets.atlasTextureParameter);
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
        if (jEnemy.has("texture_atlas"))
        {
            enemy.textureAtlas = jEnemy.getString("texture_atlas");
            Assets.manager.load(enemy.textureAtlas, TextureAtlas.class, Assets.atlasTextureParameter);
        }
        if (jEnemy.has("texture_name"))
        {
            enemy.textureName = jEnemy.getString("texture_name");
            Assets.manager.load(enemy.textureName, Texture.class, Assets.textureParameter);
        }
        if(!levelParsed)level.gameObjects.add(enemy);
    }

    private void parseEnemyStopper(World world, JSONObject jEnemyStopper) throws JSONException
    {
        if(levelParsed)return;
        Vector3 position = new Vector3((float) jEnemyStopper.getDouble("posx"), (float) jEnemyStopper.getDouble("posy"), 0);
        float width = (float) jEnemyStopper.getDouble("width");
        float height = (float) jEnemyStopper.getDouble("height");

        EnemyStopper stopper = new EnemyStopper(world, new Vector2(width, height), position);

        level.gameObjects.add(stopper);
    }

    private void parseLevelEntry(World world, JSONObject jEntry) throws JSONException
    {
        if(levelParsed)return;
        Vector3 position = new Vector3((float) jEntry.getDouble("posx"), (float) jEntry.getDouble("posy"), 0);
        float width = (float) jEntry.getDouble("width");
        float height = (float) jEntry.getDouble("height");

        LevelEntry entry = new LevelEntry(world, new Vector2(width, height), position);
        entry.direction = jEntry.optString("direction");
        entry.type = jEntry.optInt("type");
        entry.name = jEntry.optString("name");

        level.gameObjects.add(entry);
    }

    private void parseLevelExit(World world, JSONObject jExit) throws JSONException
    {
        if(levelParsed)return;
        Vector3 position = new Vector3((float) jExit.getDouble("posx"), (float) jExit.getDouble("posy"), 0);
        float width = (float) jExit.getDouble("width");
        float height = (float) jExit.getDouble("height");
        LevelExit exit = new LevelExit(world, new Vector2(width, height), position);
        exit.cameraMotion = jExit.optInt("camera_motion");
        exit.type = jExit.optInt("type");
        exit.levelName = jExit.optString("level_name", null);
        exit.entry = jExit.optString("entry");
        exit.direction = jExit.optString("direction");

        level.gameObjects.add(exit);
    }

    private void parseItem(World world, JSONObject jItem) throws JSONException
    {
        Vector3 position = new Vector3((float) jItem.getDouble("posx"), (float) jItem.getDouble("posy"), 0);

        Item item = Item.initObject(world, jItem.getString("type"), new Vector2((float) jItem.getDouble("width"), (float) jItem.getDouble("height")), position);
        if (item == null) return;
        if (jItem.has("texture_atlas"))
        {
            item.textureAtlas = jItem.getString("texture_atlas");
            Assets.manager.load(item.textureAtlas, TextureAtlas.class, Assets.atlasTextureParameter);
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
