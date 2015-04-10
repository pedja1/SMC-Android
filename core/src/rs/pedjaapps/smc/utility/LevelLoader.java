package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import java.util.Collections;
import java.util.Comparator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
    public Level level;

    public enum KEY
    {
        sprites, posx, posy, width, height, texture_atlas, texture_name, info, player, level_width,
        level_height, collision_bodies, flip_data, flip_x, flip_y, is_front, background, r_1, r_2,
        g_1, g_2, b_1, b_2, level_music, enemies, enemy_class, objects, object_class, obj_class, 
		massive_type, type, enemy_filter, gold_color, item, text, useable_count, invisible, animation,
		force_best_item, max_downgrade_count, direction, level_name, name, camera_motion, entry
	}

	private enum DATA_KEY
	{
		/**
		* Texture*/
		txt,
		/**
		 * TextureAtlas*/
		atl,
		/**
		 * Music*/
		mus,
		/**
		 * Sound*/
		snd,
		/**
		 * PartcileEfect*/
		pce
	}

	private enum ObjectClass
	{
		sprite, item, box, player, enemy, moving_platform, enemy_stopper, level_entry, level_exit,
	}
	
	private static final float m_pos_z_passive_start = 0.01f;
	private static final  float m_pos_z_massive_start = 0.08f;
	private static final  float m_pos_z_front_passive_start = 0.1f;
	private static final  float m_pos_z_halfmassive_start = 0.04f;

    public LevelLoader()
    {
        level = new Level();
    }

	public void parseLevel(World world, MarioController controller, String jsonString)
	{
		JSONObject jLevel;
        try
        {
            jLevel = new JSONObject(jsonString);
            parseInfo(jLevel);
            parseBg(jLevel);
			parseGameObjects(world, controller, jLevel);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Unable to load level! " + e.getMessage());
        }
	}

	private void parseGameObjects(World world, MarioController controller, JSONObject level) throws JSONException
	{
		JSONArray jObjects =  level.getJSONArray(KEY.objects.toString());
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

	public Array<String[]> parseLevelData(String levelData)
	{
		if (levelData == null)
		{
			throw new IllegalArgumentException("levelData cannot be null");
		}
		levelData = levelData.trim();//clean spaces and new lines from start and end
		if (levelData.isEmpty())
		{
			throw new IllegalArgumentException("levelData cannot be empty");
		}

		Array<String[]> data = new Array<String[]>();
		String[] lines = levelData.split("\n");
		for (String line : lines)
		{
			if (line.startsWith("#"))continue;//skip comments
			String[] item = line.split(":");
			if (item[0] == null || item[1] == null)// both value and key must not be null
			{
				throw new IllegalArgumentException("failed to read data, null");
			}
			else if (!isDataKeyValid(item[0]))// check if we recognize key
			{
				throw new IllegalArgumentException("invalid key found in data: " + item[0]);
		 	}
			else if (item[1].trim().isEmpty())//check if value is empty string
			{
				throw new IllegalArgumentException("value with key:" + item[0] + "is invalid.");
			}
			data.add(item);
		}
		return data;
	}

	private boolean isDataKeyValid(String key)
	{
		for (DATA_KEY dk : DATA_KEY.values())
		{
			if (key.equals(dk.toString()))
			{
				return true;
			}
		}
		return false;
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
                music.add(jMusic.getString(i));
            }
            level.music = music;
        }
    }

    private void parseBg(JSONObject jLevel) throws JSONException
    {
		if (jLevel.has(KEY.background.toString()))
		{
			JSONObject jBg = jLevel.getJSONObject(KEY.background.toString());
			String textureName = jBg.getString(KEY.texture_name.toString());
			if (Assets.manager.isLoaded(textureName))
			{
				Texture bgTexture = Assets.manager.get(textureName);
				Background bg = new Background(new Vector2(0, 0), bgTexture);
				level.bg1 = bg;
				bg = new Background(new Vector2(Background.WIDTH, 0), bgTexture);
				level.bg2 = bg;
				//TODO this is stupid, we should dinamically repeat background
			}
			else
			{
				throw new IllegalArgumentException("Texture not found in AssetManager. Every Texture used" 
												   + "in [level].smclvl must also be included in [level].data (" + textureName + ")");
			}
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
        float x = (float) jPlayer.getDouble(KEY.posx.toString());
        float y = (float) jPlayer.getDouble(KEY.posy.toString());
        level.spanPosition = new Vector3(x, y, 0.0999f);
        if (controller != null)
        {
            Maryo maryo = new Maryo(world, level.spanPosition, new Vector2(0.9f, 0.9f));
            maryo.loadTextures();
            world.maryo = maryo;
            level.gameObjects.add(maryo);
            controller.setMaryo(maryo);
        }
    }

    private void parseSprite(World world, JSONObject jSprite) throws JSONException
    {
		
		Vector3 position = new Vector3((float) jSprite.getDouble(KEY.posx.toString()), (float) jSprite.getDouble(KEY.posy.toString()), 0);
		Sprite.Type sType = null;
		if(jSprite.has(KEY.massive_type.toString()))
		{
			sType = Sprite.Type.valueOf(jSprite.getString(KEY.massive_type.toString()));
		switch(sType)
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
		if (jSprite.has(KEY.is_front.toString()))
		{
			sprite.isFront = jSprite.getBoolean(KEY.is_front.toString());
		}

		//load all assets
		TextureAtlas atlas = null;
		if (jSprite.has(KEY.texture_atlas.toString()))
		{
			sprite.textureAtlas = jSprite.getString(KEY.texture_atlas.toString());
			if (Assets.manager.isLoaded(sprite.textureAtlas))
			{
				atlas = Assets.manager.get(sprite.textureAtlas);
			}
			else
			{
				throw new IllegalArgumentException("Atlas not found in AssetManager. Every TextureAtlas used" 
												   + "in [level].smclvl must also be included in [level].data ("
												   + sprite.textureAtlas + ")");
			}
		}
		boolean hasFlipData = jSprite.has(KEY.flip_data.toString());

		if (hasFlipData)
		{
			JSONObject flipData = jSprite.getJSONObject(KEY.flip_data.toString());
			boolean flipX = flipData.getBoolean(KEY.flip_x.toString());
			boolean flipY = flipData.getBoolean(KEY.flip_y.toString());
			String newTextureName = null;
			if (flipX && !flipY)
			{
				newTextureName = sprite.textureName + "-flip_x";
			}
			else if (flipY && !flipX)
			{
				newTextureName = sprite.textureName + "-flip_y";
			}
			else if (flipY && flipX)
			{
				newTextureName = sprite.textureName + "-flip_xy";
			}

			if(newTextureName != null)if (Assets.loadedRegions.get(newTextureName) == null)
			{
				TextureRegion orig;
				if (Assets.loadedRegions.get(sprite.textureName) == null)
				{
					if (atlas == null)
					{
						if (Assets.manager.isLoaded(sprite.textureName))
						{
							orig = new TextureRegion(Assets.manager.get(sprite.textureName, Texture.class));
						}
						else
						{
							throw new IllegalArgumentException("Texture(" + sprite.textureName + ") not found in AssetManager. Every Texture used" 
															   + "in [level].smclvl must also be included in [level].data (" + sprite.textureName + ")");
						}
					}
					else
					{
						orig = atlas.findRegion(sprite.textureName.split(":")[1]);
					}
					Assets.loadedRegions.put(sprite.textureName, orig);
				}
				else
				{
					orig = Assets.loadedRegions.get(sprite.textureName);
				}
				TextureRegion flipped = new TextureRegion(orig);
				flipped.flip(flipX, flipY);
				sprite.textureName = newTextureName;
				Assets.loadedRegions.put(newTextureName, flipped);
				
			}
			else
			{
				sprite.textureName = newTextureName;
			}
		}
		else
		{
			if (Assets.loadedRegions.get(sprite.textureName) == null)
			{
				TextureRegion textureRegion;
				if (atlas == null)
				{
					if (Assets.manager.isLoaded(sprite.textureName))
					{
						textureRegion = new TextureRegion(Assets.manager.get(sprite.textureName, Texture.class));
					}
					else
					{
						throw new IllegalArgumentException("Texture (" + sprite.textureName + ") not found in AssetManager. Every Texture used"
														   + "in [level].smclvl must also be included in [level].data ( " + sprite.textureName + " )");
					}
				}
				else
				{
					textureRegion = atlas.findRegion(sprite.textureName.split(":")[1]);
				}
				Assets.loadedRegions.put(sprite.textureName, textureRegion);
			}
		}

		level.gameObjects.add(sprite);

    }

    /*public Body createBody(World world, Vector2 position, float width, float height, boolean enemyFilter)
    {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(position.x + width / 2, position.y + height / 2);

		Body body = world.createBody(groundBodyDef);

		PolygonShape groundBox = new PolygonShape();

		groundBox.setAsBox(width / 2, height / 2);

		body.createFixture(groundBox, 0.0f);
        if(enemyFilter)body.setUserData(new Collider());

		groundBox.dispose();
        return body;
    }*/

    public static Class getTextureClassForKey(String key)
    {
        if (key.equals(DATA_KEY.txt.toString()))
        {
            return Texture.class;
        }
        else if (key.equals(DATA_KEY.atl.toString()))
        {
            return TextureAtlas.class;
        }
        else if (key.equals(DATA_KEY.mus.toString()))
        {
            return Music.class;
        }
        else if (key.equals(DATA_KEY.snd.toString()))
        {
            return Sound.class;
        }
        else
        {
            throw new IllegalArgumentException("Key: " + key + " is invalid!");
        }
    }

    public static boolean isTexture(String key)
    {
        return key.equals(DATA_KEY.txt.toString());
    }

    public static boolean isParticle(String key)
    {
        return key.equals(DATA_KEY.pce.toString());
    }

    private void parseEnemy(World world, JSONObject jEnemy) throws JSONException
    {
        Vector3 position = new Vector3((float) jEnemy.getDouble(KEY.posx.toString()), (float) jEnemy.getDouble(KEY.posy.toString()), 0);

            Enemy enemy = Enemy.initEnemy(world, jEnemy.getString(KEY.enemy_class.toString()), new Vector2((float) jEnemy.getDouble(KEY.width.toString()), (float) jEnemy.getDouble(KEY.height.toString())), position, jEnemy.optInt(KEY.max_downgrade_count.toString()));
            if (enemy == null)return;//TODO this has to go aways after levels are fixed
            if (jEnemy.has(KEY.texture_atlas.toString()))
            {
                enemy.textureAtlas = jEnemy.getString(KEY.texture_atlas.toString());
                if (Assets.manager.isLoaded(enemy.textureAtlas))
                {
                    enemy.loadTextures();
                }
                else
                {
                    throw new IllegalArgumentException("Atlas not found in AssetManager. Every TextureAtlas used"
													   + "in [level].smclvl must also be included in [level].data (" + enemy.textureAtlas + ")");
                }
            }
            level.gameObjects.add(enemy);
    }

	private void parseEnemyStopper(World world, JSONObject jEnemyStopper) throws JSONException
    {
        Vector3 position = new Vector3((float) jEnemyStopper.getDouble(KEY.posx.toString()), (float) jEnemyStopper.getDouble(KEY.posy.toString()), 0);
        float width =  (float) jEnemyStopper.getDouble(KEY.width.toString());
		float height =  (float) jEnemyStopper.getDouble(KEY.height.toString());
		
		EnemyStopper stopper = new EnemyStopper(world, new Vector2(width, height), position);
		
		level.gameObjects.add(stopper);
    }

	private void parseLevelEntry(World world, JSONObject jEntry) throws JSONException
    {
        Vector3 position = new Vector3((float) jEntry.getDouble(KEY.posx.toString()), (float) jEntry.getDouble(KEY.posy.toString()), 0);
        float width =  0.2f;
		float height =  0.2f;

		LevelEntry entry = new LevelEntry(world, new Vector2(width, height), position);
		entry.direction = jEntry.optString(KEY.direction.toString());
		entry.type = jEntry.optInt(KEY.type.toString());
		entry.name = jEntry.optString(KEY.name.toString());

		level.gameObjects.add(entry);
	}

	private void parseLevelExit(World world, JSONObject jExit) throws JSONException
    {
        Vector3 position = new Vector3((float) jExit.getDouble(KEY.posx.toString()), (float) jExit.getDouble(KEY.posy.toString()), 0);
        float width =  (float) jExit.getDouble(KEY.width.toString());
		float height =  (float) jExit.getDouble(KEY.height.toString());

		LevelExit exit = new LevelExit(world, new Vector2(width, height), position);
		exit.cameraMotion = jExit.optInt(KEY.camera_motion.toString());
		exit.type = jExit.optInt(KEY.type.toString());
		exit.levelName = jExit.optString(KEY.level_name.toString());
		exit.entry = jExit.optString(KEY.entry.toString());
		exit.direction = jExit.optString(KEY.direction.toString());

		level.gameObjects.add(exit);
	}

	private void parseItem(World world, JSONObject jItem) throws JSONException
    {
            Vector3 position = new Vector3((float) jItem.getDouble(KEY.posx.toString()), (float) jItem.getDouble(KEY.posy.toString()), 0);

            Item item = Item.initObject(world, jItem.getString(KEY.type.toString()), new Vector2((float) jItem.getDouble(KEY.width.toString()), (float) jItem.getDouble(KEY.height.toString())), position);
            if(item == null) return;
            if (jItem.has(KEY.texture_atlas.toString()))
            {
                item.textureAtlas = jItem.getString(KEY.texture_atlas.toString());
                if (Assets.manager.isLoaded(item.textureAtlas))
                {
                    item.loadTextures();
                }
                else
                {
                    throw new IllegalArgumentException("Atlas not found in AssetManager. Every TextureAtlas used"
													   + "in [level].smclvl must also be included in [level].data (" + item.textureAtlas + ")");
                }
            }
            level.gameObjects.add(item);
    }

	private void parseBox(World world, JSONObject jBox) throws JSONException
    {
		Box box = Box.initBox(world, jBox, this);
		level.gameObjects.add(box);
    }
	
	/** Comparator used for sorting, sorts in ascending order (biggset z to smallest z).
	 * @author mzechner */
	public class ZSpriteComparator implements Comparator<GameObject>
	{
		@Override
		public int compare (GameObject sprite1, GameObject sprite2)
		{
			if(sprite1.position.z > sprite2.position.z) return 1;
			if(sprite1.position.z < sprite2.position.z) return -1;
			return 0;
		}
	}

}
