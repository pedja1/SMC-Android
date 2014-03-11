package rs.papltd.smc.utility;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import org.json.*;

import rs.papltd.smc.Assets;
import rs.papltd.smc.model.*;

/**
 * Created by pedja on 2/2/14.
 */

/**
 * This class loads level from json
 */
public class LevelLoader
{
    Level level;

    private enum KEYS
    {
        sprites, posx, posy, width, height, texture_atlas, texture_name, info, player, level_width,
        level_height, collision_bodies, flip_data, flip_x, flip_y, is_front, atlases, atlas_path,
        regions
    }
	
	private enum DATA_KEY
	{
		txt, atl
	}

    public LevelLoader()
    {
        level = new Level();
    }
	
	public void parseLevel(String jsonString, World world)
	{
		JSONObject jLevel;
        try
        {
            jLevel = new JSONObject(jsonString);
            parseInfo(jLevel);
            parseSprites(jLevel, world);
            parsePlayer(jLevel);
            parseBg(jLevel);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Unable to load level! " + e.getMessage());
        }
	}
	
	public Array<String[]> parseLeveData(String levelData)
	{
		Array<String[]> data = new Array<String[]>();
		String[] lines = levelData.split("\n");
		for(String line : lines)
		{
			if(line.startsWith("#"))continue;//skip coments
			String[] item = line.split(":");
			if(item[0] == null || item[1] == null)
			{
				throw new IllegalArgumentException("failed to read data, null");
			}
			else if(!isDataKeyValid(item[0]))
			{
				throw new IllegalArgumentException("invalid key found in data: " + item[0]);
		 	}
			else if(item[1].trim().isEmpty())
			{
				throw new IllegalArgumentException("value with key:" + item[0] + "is invalid.");
			}
			data.add(item);
		}
		return data;
	}
	
	private boolean isDataKeyValid(String key)
	{
		for(DATA_KEY dk : DATA_KEY.values())
		{
			if(key.equals(dk.toString()))
			{
				return true;
			}
		}
		return false;
	}

    private void parseInfo(JSONObject jLevel) throws JSONException
    {
        JSONObject jInfo = jLevel.getJSONObject(KEYS.info.toString());
        float width = (float) jInfo.getDouble(KEYS.level_width.toString());
        float height = (float) jInfo.getDouble(KEYS.level_height.toString());
        level.setWidth(width);
        level.setHeight(height);
    }

    private void parseBg(JSONObject jLevel)
    {
        //TODO implement this method
        Texture bgTexture = new Texture(Gdx.files.absolute(Assets.mountedObbPath + "/game/background/green-junglehills.png"));
        Background bg = new Background(new Vector2(0, 0), bgTexture);
        level.setBg1(bg);
        bg = new Background(new Vector2(Background.WIDTH, 0), bgTexture);
        level.setBg2(bg);
        BackgroundColor bgColor = new BackgroundColor();
        bgColor.color1 = new Color(.117f, 0.705f, .05f, 0f);//color is 0-1 range where 1 = 255
        bgColor.color2 = new Color(0f, 0.392f, 0.039f, 0f);
        level.setBgColor(bgColor);
    }

    private void parsePlayer(JSONObject jLevel) throws JSONException
    {
        JSONObject jPlayer = jLevel.getJSONObject(KEYS.player.toString());
        float x = (float) jPlayer.getDouble(KEYS.posx.toString());
        float y = (float) jPlayer.getDouble(KEYS.posy.toString());
        level.setSpanPosition(new Vector2(x, y));
    }

    private void parseSprites(JSONObject jLevel, World world) throws JSONException
    {
        JSONArray jSprites = jLevel.getJSONArray(KEYS.sprites.toString());
        Array<Sprite> sprites = new Array<Sprite>();

        for (int i = 0; i < jSprites.length(); i++)
        {
            JSONObject jSprite = jSprites.getJSONObject(i);
            Vector2 position = new Vector2((float) jSprite.getDouble(KEYS.posx.toString()), (float) jSprite.getDouble(KEYS.posy.toString()));

            Sprite sprite = new Sprite(position, (float) jSprite.getDouble(KEYS.width.toString()), (float) jSprite.getDouble(KEYS.height.toString()));

            sprite.setTextureName(jSprite.getString(KEYS.texture_name.toString()));
            if (jSprite.has(KEYS.is_front.toString()))
            {
                sprite.setFront(jSprite.getBoolean(KEYS.is_front.toString()));
            }

            //load all assets
            TextureAtlas atlas = null;
            if (jSprite.has(KEYS.texture_atlas.toString()))
            {
                sprite.setTextureAtlas(jSprite.getString(KEYS.texture_atlas.toString()));
                /*if (Assets.loadedAtlases.get(sprite.getTextureAtlas()) == null)
                {
                    atlas = new TextureAtlas(Gdx.files.absolute(Assets.mountedObbPath + sprite.getTextureAtlas()));
                    Assets.loadedAtlases.put(sprite.getTextureAtlas(), atlas);
                }
                else
                {
                    atlas = Assets.loadedAtlases.get(sprite.getTextureAtlas());
                }*/
            }
            boolean hasFlipData = jSprite.has(KEYS.flip_data.toString());

            if (hasFlipData)
            {
                JSONObject flipData = jSprite.getJSONObject(KEYS.flip_data.toString());
                boolean flipX = flipData.getBoolean(KEYS.flip_x.toString());
                boolean flipY = flipData.getBoolean(KEYS.flip_y.toString());
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
                            orig = new TextureRegion(new Texture(Gdx.files.absolute(Assets.mountedObbPath + sprite.getTextureName())));
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
                        textureRegion = new TextureRegion(new Texture(Gdx.files.absolute(Assets.mountedObbPath + sprite.getTextureName())));
                    }
                    else
                    {
                        textureRegion = atlas.findRegion(sprite.getTextureName().split(":")[1]);
                    }
                    Assets.loadedRegions.put(sprite.getTextureName(), textureRegion);
                }
            }

            sprites.add(sprite);
        }
        level.setSprites(sprites);
        JSONArray jCollisionBodies = jLevel.getJSONArray(KEYS.collision_bodies.toString());
        for (int i = 0; i < jCollisionBodies.length(); i++)
        {
            JSONObject jBody = jCollisionBodies.getJSONObject(i);
            Vector2 position = new Vector2((float) jBody.getDouble(KEYS.posx.toString()), (float) jBody.getDouble(KEYS.posy.toString()));
            createBody(world, position, (float) jBody.getDouble(KEYS.width.toString()), (float) jBody.getDouble(KEYS.height.toString()));
        }
    }

    public Body createBody(World world, Vector2 position, float width, float height)
    {
        // Create our body definition
        BodyDef groundBodyDef = new BodyDef();
        // Set its world position
        groundBodyDef.position.set(position.x + width / 2, position.y + height / 2);

        // Create a body from the defintion and add it to the world
        Body body = world.createBody(groundBodyDef);
        // Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
        // Set the polygon shape as a box which is twice the size of our view port and 20 high
        // (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(width / 2, height / 2);
        // Create a fixture from our polygon shape and add it to our ground body

        body.createFixture(groundBox, 0.0f);
        // Clean up after ourselves
        groundBox.dispose();
        return body;
    }

    public Level getLevel()
    {
        return level;
    }
}
