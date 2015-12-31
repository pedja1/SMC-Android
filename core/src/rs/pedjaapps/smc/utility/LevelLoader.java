package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.Level;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.enemy.Enemy;
import rs.pedjaapps.smc.object.enemy.EnemyStopper;
import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.view.Background;

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
    String levelName;

    private enum ObjectClass
    {
        sprite, item, player, enemy, enemy_stopper,
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
        this.levelName = levelName;
        level = new Level();
    }

    public synchronized void parseLevel(World world)
    {
        JSONObject jLevel;
        try
        {
            jLevel = new JSONObject(Gdx.files.internal("data/levels/" + levelName + Level.LEVEL_EXT).readString());
            parseInfo(jLevel, world.screen.game.assets);
            parseBg(jLevel, world.screen.game.assets);
            parseGameObjects(world, jLevel, world.screen.game.assets);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Unable to load level! " + e.getMessage());
        }
        levelParsed = true;
    }

    private void parseGameObjects(World world, JSONObject level, Assets assets) throws JSONException
    {
        JSONArray jObjects = level.getJSONArray("objects");
        for (int i = 0; i < jObjects.length(); i++)
        {
            JSONObject jObject = jObjects.getJSONObject(i);
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
            }
        }
        //this.level.gameObjects.sort(new ZSpriteComparator());
        //Collections.sort(this.level.gameObjects, new ZSpriteComparator());
    }

    private void parseInfo(JSONObject jLevel, Assets assets) throws JSONException
    {
        JSONObject jInfo = jLevel.getJSONObject("info");
        if (jInfo.has("level_music"))
        {
            String music = jInfo.getString("level_music");
            assets.manager.load(music, Music.class);
            if(!levelParsed)level.music = music;
        }
    }

    private void parseBg(JSONObject jLevel, Assets assets) throws JSONException
    {
        if (jLevel.has("background"))
        {
            JSONObject jBg = jLevel.getJSONObject("background");
            String textureName = jBg.optString("texture_name", null);
            if(textureName != null)assets.manager.load(textureName, Texture.class, assets.textureParameter);
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

    private void parseSprite(World world, JSONObject jSprite, Assets assets) throws JSONException
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
        Vector2 size = new Vector2((float) jSprite.getDouble("width"), (float) jSprite.getDouble("height"));

        Rectangle rectangle = new Rectangle();
        rectangle.x = (float) jSprite.optDouble("c_posx", 0);
        rectangle.y = (float) jSprite.optDouble("c_posy", 0);
        rectangle.width = (float) jSprite.optDouble("c_width", size.x);
        rectangle.height = (float) jSprite.optDouble("c_height", size.y);
        Sprite sprite = new Sprite(world, size, position, rectangle);
        sprite.type = sType;

        sprite.textureName = jSprite.getString("texture_name");
        if (sprite.textureName == null || sprite.textureName.isEmpty())
        {
            throw new IllegalArgumentException("texture name is invalid: \"" + sprite.textureName + "\"");
        }

        if(!TXT_NAME_IN_ATLAS.matcher(sprite.textureName).matches())
        {
            assets.manager.load(sprite.textureName, Texture.class, assets.textureParameter);
        }
        if (jSprite.has("is_front"))
        {
            sprite.isFront = jSprite.getBoolean("is_front");
        }

        sprite.textureAtlas = jSprite.optString("texture_atlas", null);
        if(sprite.textureAtlas != null)
        {
            assets.manager.load(sprite.textureAtlas, TextureAtlas.class);
        }
        sprite.mRotationX = jSprite.optInt("rotationX");
        sprite.mRotationY = jSprite.optInt("rotationY");
        sprite.mRotationZ = jSprite.optInt("rotationZ");
        if(sprite.mRotationZ == 270)
        {
            sprite.mRotationZ = -sprite.mRotationZ;
        }
        if(!levelParsed)level.gameObjects.add(sprite);

    }

    private void parseEnemy(World world, JSONObject jEnemy, Assets assets) throws JSONException
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

    private void parseItem(World world, JSONObject jItem, Assets assets) throws JSONException
    {
        Vector3 position = new Vector3((float) jItem.getDouble("posx"), (float) jItem.getDouble("posy"), 0);

        Item item = Item.initObject(world, jItem.getString("type"), new Vector2((float) jItem.getDouble("width"), (float) jItem.getDouble("height")), position);
        if (item == null) return;
        if (jItem.has("texture_atlas"))
        {
            item.textureAtlas = jItem.getString("texture_atlas");
            assets.manager.load(item.textureAtlas, TextureAtlas.class);
        }
        if(!levelParsed)level.gameObjects.add(item);
    }

}
