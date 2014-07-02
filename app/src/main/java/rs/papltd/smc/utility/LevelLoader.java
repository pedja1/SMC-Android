package rs.papltd.smc.utility;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import org.json.*;

import rs.papltd.smc.Assets;
import rs.papltd.smc.model.*;
import rs.papltd.smc.model.custom_objects.CustomObject;
import rs.papltd.smc.model.enemy.Enemy;

/**
 * Created by pedja on 2/2/14.
 */

/**
 * This class loads level from json
 */
public class LevelLoader
{
    Level level;
	World world;

    private enum KEY
    {
        sprites, posx, posy, width, height, texture_atlas, texture_name, info, player, level_width,
        level_height, collision_bodies, flip_data, flip_x, flip_y, is_front, background, r_1, r_2,
        g_1, g_2, b_1, b_2, level_music, enemies, enemy_class, objects, object_class, obj_class
		}

	private enum DATA_KEY
	{
		txt, atl, mus, snd
		}

	private enum ObjectClass
	{
		sprite, item, box, player, enemy, moving_platform, enemy_stopper, level_entry, level_exit,
	}

    public LevelLoader()
    {
        level = new Level();
    }

	public void parseLevel(String jsonString, World world)
	{
		this.world = world;
		JSONObject jLevel;
        try
        {
            jLevel = new JSONObject(jsonString);
            parseInfo(jLevel);
            //parseSprites(jLevel, world);//not movable objects
            //parseObjects(jLevel, world);//movable/animated objects(have their own class)
            //parseEnemies(jLevel, world);
            //parsePlayer(jLevel);
			parseColBoxes(jLevel);
            parseBg(jLevel);
			parseGameObjects(jLevel);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Unable to load level! " + e.getMessage());
        }
	}

	private void parseColBoxes(JSONObject jLevel) throws JSONException
	{
		JSONArray jCollisionBodies = jLevel.getJSONArray(KEY.collision_bodies.toString());
		for (int i = 0; i < jCollisionBodies.length(); i++)
		{
			JSONObject jBody = jCollisionBodies.getJSONObject(i);
			Vector2 position = new Vector2((float) jBody.getDouble(KEY.posx.toString()), (float) jBody.getDouble(KEY.posy.toString()));
			createBody(world, position, (float) jBody.getDouble(KEY.width.toString()), (float) jBody.getDouble(KEY.height.toString()));
		}
	}

	private void parseGameObjects(JSONObject level) throws JSONException
	{
		JSONArray jObjects =  level.getJSONArray(KEY.objects.toString());
		for (int i = 0; i < jObjects.length(); i++)
		{
			JSONObject jObject = jObjects.getJSONObject(i);
			switch (ObjectClass.valueOf(jObject.getString(KEY.obj_class.toString())))
			{
				case sprite:
					parseSprite(jObject, world);
					break;
				case player:
					parsePlayer(jObject);
					break;
			}
		}
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
        level.setWidth(width);
        level.setHeight(height);
        if (jInfo.has(KEY.level_music.toString()))
        {
            JSONArray jMusic = jInfo.getJSONArray(KEY.level_music.toString());
            Array<String> music = new Array<String>();
            for (int i = 0; i < jMusic.length(); i++)
            {
                music.add(jMusic.getString(i));
            }
            level.setMusic(music);
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
				level.setBg1(bg);
				bg = new Background(new Vector2(Background.WIDTH, 0), bgTexture);
				level.setBg2(bg);
				//TODO this is stupid, we should dinamically repeat background
			}
			else
			{
				throw new IllegalArgumentException("Texture not found in AssetManager. Every Texture used" 
												   + "in [level].smclvl must also be included in [level].data (" + textureName + ")");
			}
			float r1 = (float) jBg.getDouble(KEY.r_1.toString());
			float r2 = (float) jBg.getDouble(KEY.r_2.toString());
			float g1 = (float) jBg.getDouble(KEY.g_1.toString());
			float g2 = (float) jBg.getDouble(KEY.g_2.toString());
			float b1 = (float) jBg.getDouble(KEY.b_1.toString());
			float b2 = (float) jBg.getDouble(KEY.b_2.toString());

			BackgroundColor bgColor = new BackgroundColor();
			bgColor.color1 = new Color(r1, g1, b1, 0f);//color is 0-1 range where 1 = 255
			bgColor.color2 = new Color(r2, g2, b2, 0f);
			level.setBgColor(bgColor);
		}
		else
		{
			throw new IllegalStateException("level must have \"background\" object");
		}
    }

    private void parsePlayer(JSONObject jPlayer) throws JSONException
    {
        float x = (float) jPlayer.getDouble(KEY.posx.toString());
        float y = (float) jPlayer.getDouble(KEY.posy.toString());
        level.setSpanPosition(new Vector2(x, y));
    }

    private void parseSprite(JSONObject jSprite, World world) throws JSONException
    {
		Vector2 position = new Vector2((float) jSprite.getDouble(KEY.posx.toString()), (float) jSprite.getDouble(KEY.posy.toString()));

		Sprite sprite = new Sprite(position, (float) jSprite.getDouble(KEY.width.toString()), (float) jSprite.getDouble(KEY.height.toString()));

		sprite.setTextureName(jSprite.getString(KEY.texture_name.toString()));
		if (sprite.getTextureName() == null || sprite.getTextureName().isEmpty())
		{
			throw new IllegalArgumentException("texture name is invalid: \"" + sprite.getTextureName() + "\"");
		}
		if (jSprite.has(KEY.is_front.toString()))
		{
			sprite.setFront(jSprite.getBoolean(KEY.is_front.toString()));
		}

		//load all assets
		TextureAtlas atlas = null;
		if (jSprite.has(KEY.texture_atlas.toString()))
		{
			sprite.setTextureAtlas(jSprite.getString(KEY.texture_atlas.toString()));
			if (Assets.manager.isLoaded(sprite.getTextureAtlas()))
			{
				atlas = Assets.manager.get(sprite.getTextureAtlas());
			}
			else
			{
				throw new IllegalArgumentException("Atlas not found in AssetManager. Every TextureAtlas used" 
												   + "in [level].smclvl must also be included in [level].data ("
												   + sprite.getTextureAtlas() + ")");
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
				newTextureName = sprite.getTextureName() + "-flip_x";
			}
			else if (flipY && !flipX)
			{
				newTextureName = sprite.getTextureName() + "-flip_y";
			}
			else if (flipY && flipX)
			{
				newTextureName = sprite.getTextureName() + "-flip_xy";
			}

			if (newTextureName != null && Assets.loadedRegions.get(newTextureName) == null)
			{
				TextureRegion orig;
				if (Assets.loadedRegions.get(sprite.getTextureName()) == null)
				{
					if (atlas == null)
					{
						if (Assets.manager.isLoaded(sprite.getTextureName()))
						{
							orig = new TextureRegion(Assets.manager.get(sprite.getTextureName(), Texture.class));
						}
						else
						{
							throw new IllegalArgumentException("Texture(" + sprite.getTextureName() + ") not found in AssetManager. Every Texture used" 
															   + "in [level].smclvl must also be included in [level].data (" + sprite.getTextureName() + ")");
						}
					}
					else
					{
						orig = atlas.findRegion(sprite.getTextureName().split(":")[1]);
					}
					Assets.loadedRegions.put(sprite.getTextureName(), orig);
				}
				else
				{
					orig = Assets.loadedRegions.get(sprite.getTextureName());
				}
				TextureRegion flipped = new TextureRegion(orig);
				orig.flip(flipX, flipY);
				sprite.setTextureName(newTextureName);
				Assets.loadedRegions.put(newTextureName, flipped);
			}
		}
		else
		{
			if (Assets.loadedRegions.get(sprite.getTextureName()) == null)
			{
				TextureRegion textureRegion;
				if (atlas == null)
				{
					if (Assets.manager.isLoaded(sprite.getTextureName()))
					{
						textureRegion = new TextureRegion(Assets.manager.get(sprite.getTextureName(), Texture.class));
					}
					else
					{
						throw new IllegalArgumentException("Texture (" + sprite.getTextureName() + ") not found in AssetManager. Every Texture used"
														   + "in [level].smclvl must also be included in [level].data ( " + sprite.getTextureName() + " )");
					}
				}
				else
				{
					textureRegion = atlas.findRegion(sprite.getTextureName().split(":")[1]);
				}
				Assets.loadedRegions.put(sprite.getTextureName(), textureRegion);
			}
		}

		level.getGameObjects().add(sprite);

    }

    public Body createBody(World world, Vector2 position, float width, float height)
    {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(position.x + width / 2, position.y + height / 2);

		Body body = world.createBody(groundBodyDef);

		PolygonShape groundBox = new PolygonShape();

		groundBox.setAsBox(width / 2, height / 2);

		body.createFixture(groundBox, 0.0f);

		groundBox.dispose();
        return body;
    }

    public Level getLevel()
    {
        return level;
    }

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

    private void parseEnemies(JSONObject jLevel, World world) throws JSONException
    {
        if (!jLevel.has(KEY.enemies.toString()))
        {
            return;
        }
        JSONArray jEnemies = jLevel.getJSONArray(KEY.enemies.toString());
        Array<Enemy> enemies = new Array<Enemy>();

        for (int i = 0; i < jEnemies.length(); i++)
        {
            JSONObject jEnemy = jEnemies.getJSONObject(i);
            Vector2 position = new Vector2((float) jEnemy.getDouble(KEY.posx.toString()), (float) jEnemy.getDouble(KEY.posy.toString()));

            Enemy enemy = Enemy.initEnemy(jEnemy.getString(KEY.enemy_class.toString()), world, position, (float) jEnemy.getDouble(KEY.width.toString()), (float) jEnemy.getDouble(KEY.height.toString()));
            if (enemy == null)continue;//TODO this has to go aways after levels are fixed
            if (jEnemy.has(KEY.texture_atlas.toString()))
            {
                enemy.setTextureAtlas(jEnemy.getString(KEY.texture_atlas.toString()));
                if (Assets.manager.isLoaded(enemy.getTextureAtlas()))
                {
                    enemy.loadTextures();
                }
                else
                {
                    throw new IllegalArgumentException("Atlas not found in AssetManager. Every TextureAtlas used"
													   + "in [level].smclvl must also be included in [level].data (" + enemy.getTextureAtlas() + ")");
                }
            }
            enemies.add(enemy);
        }
		// level.setEnemies(enemies);
    }

    private void parseObjects(JSONObject jLevel, World world) throws JSONException
    {
        if (!jLevel.has(KEY.objects.toString()))
        {
            return;
        }
        JSONArray jObjects = jLevel.getJSONArray(KEY.objects.toString());
        Array<CustomObject> objects = new Array<CustomObject>();

        for (int i = 0; i < jObjects.length(); i++)
        {
            JSONObject jObject = jObjects.getJSONObject(i);
            Vector2 position = new Vector2((float) jObject.getDouble(KEY.posx.toString()), (float) jObject.getDouble(KEY.posy.toString()));

            CustomObject object = CustomObject.initObject(jObject.getString(KEY.object_class.toString()), world, position, (float) jObject.getDouble(KEY.width.toString()), (float) jObject.getDouble(KEY.height.toString()));

            if (jObject.has(KEY.texture_atlas.toString()))
            {
                object.setTextureAtlas(jObject.getString(KEY.texture_atlas.toString()));
                if (Assets.manager.isLoaded(object.getTextureAtlas()))
                {
                    object.loadTextures();
                }
                else
                {
                    throw new IllegalArgumentException("Atlas not found in AssetManager. Every TextureAtlas used"
													   + "in [level].smclvl must also be included in [level].data (" + object.getTextureAtlas() + ")");
                }
            }
            objects.add(object);
        }
        //level.setObjects(objects);
    }


}
