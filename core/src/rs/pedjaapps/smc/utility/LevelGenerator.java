package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.object.Collider;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.items.Coin;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.view.Background;
import rs.pedjaapps.smc.view.Parallax;

/**
 * Created by pedja on 2/2/14.
 */

/**
 * This class loads level from json
 */
public class LevelGenerator
{
	private boolean preLoaded;
	public World world;

	float groundBlockY = -0.265625f;

	//RandomUtils random = new RandomUtils();

	int currentCloudSetIndex = 1;

	static final Array<Array<String>> clouds = new Array<>();
	public static final Array<SpriteDescriptor> groundDecorationNoParallax = new Array<>();
	public static final Array<SpriteDescriptor> groundDecorationParallaxLevel1 = new Array<>();

	static
	{
		Array<String> mDefault = new Array<String>();
		mDefault.add("default-1");
		mDefault.add("default-2");
		mDefault.add("default-3");
		clouds.add(mDefault);

		Array<String> lightYellow = new Array<String>();
		lightYellow.add("yellow-1");
		lightYellow.add("yellow-2");
		lightYellow.add("yellow-3");
		lightYellow.add("yellow-4");
		lightYellow.add("yellow-5");
		lightYellow.add("yellow-6");
		lightYellow.add("yellow-7");
		lightYellow.add("yellow-8");
		lightYellow.add("yellow-9");
		lightYellow.add("yellow-10");
		clouds.add(lightYellow);

		groundDecorationNoParallax.add(new SpriteDescriptor("gras-middle", .25f));
		groundDecorationNoParallax.add(new SpriteDescriptor("plant-m", .25f));
		groundDecorationNoParallax.add(new SpriteDescriptor("plant-r", .25f));

		groundDecorationParallaxLevel1.add(new SpriteDescriptor("cactus-1", .5f));
		groundDecorationParallaxLevel1.add(new SpriteDescriptor("cactus-2", .5f));
		groundDecorationParallaxLevel1.add(new SpriteDescriptor("cactus-3", .7f));
		groundDecorationParallaxLevel1.add(new SpriteDescriptor("cactus-4", .8f));
		groundDecorationParallaxLevel1.add(new SpriteDescriptor("big-plant-2", 1f));
		groundDecorationParallaxLevel1.add(new SpriteDescriptor("big-plant-1", 1f));

	}

	private static final float m_pos_z_passive_start = 0.01f;
	private static final  float m_pos_z_massive_start = 0.08f;
	private static final  float m_pos_z_front_passive_start = 0.1f;
	private static final  float m_pos_z_halfmassive_start = 0.04f;

    public LevelGenerator(World world)
    {
		this.world = world;
    }

	/**
	 * Preload level
	 * Loads all starting data for level*/
	public void preLoad(OrthographicCamera cam)
	{
		if(preLoaded)
			return;
		//load player at starting position

		Maryo maryo = new Maryo(world, new Vector3(2, 2, 0.0999f), 0.9f, 0.9f);
		maryo.initAssets();
		world.maryo = maryo;

		//load first music
		world.level.music = "data/music/land/land_5.mp3";

		//load first background
		world.level.background = new Background(new Vector2(), new Vector2(.1f, .1f), "data/environment/background/forest_2.png");
		world.level.background2 = new Background(new Vector2(), new Vector2(.2f, .2f), "data/environment/background/forest_1.png");

		world.level.backgroundColor = new Background(null, null, null);
		Color color1 = World.COLOR_POOL.obtain();
		color1.set(0.392156863f, 0.745098039f, 0.980392157f, 0);
		world.level.backgroundColor.color1 = color1;
		Color color2 = World.COLOR_POOL.obtain();
		color2.set(0.039215686f, 0.588235294f, 0.784313725f, 0);
		world.level.backgroundColor.color2 = color2;

		world.level.parallaxClouds = new Parallax(new Vector2(.5f, .5f));
		world.level.parallaxGround1 = new Parallax(new Vector2(.1f, .1f));

        world.level.parallaxClouds.nextViewportCallback = new Parallax.NextViewportCallback()
        {
            @Override
            public void onNextViewport(float viewportStartX, float viewportWidth, float viewportHeight)
            {
                addClouds(viewportStartX, viewportWidth, viewportHeight);
            }
        };

        world.level.parallaxGround1.nextViewportCallback = new Parallax.NextViewportCallback()
        {
            @Override
            public void onNextViewport(float viewportStartX, float viewportWidth, float viewportHeight)
            {
                addGroundDecorationLevel1(viewportStartX, viewportWidth);
            }
        };

		//load ground for visible area(screen width)
		float groundStartX = -0.328125f;
		loadGround(groundStartX, cam.viewportWidth);
		addGroundCollider(groundStartX);
		addClouds(cam.position.x - cam.viewportWidth / 2, cam.viewportWidth, cam.viewportHeight);
		addGroundDecorationNoParallax(cam.position.x - cam.viewportWidth / 2, cam.viewportWidth);
		addGroundDecorationLevel1(cam.position.x - cam.viewportWidth / 2, cam.viewportWidth);
		preLoaded = true;
	}

	private void addGroundCollider(float groundStartX)
	{
		Collider collider = new Collider(world, new Vector3(groundStartX, groundBlockY, 0), Integer.MAX_VALUE, 1);
        collider.mColRect = new Rectangle(collider.mDrawRect);
		world.level.gameObjects.add(collider);
	}

	/**
	 * Load ground for one camera viewport width*/
	private void loadGround(float groundStartX, float width)
	{
		for(int i = 0; i < width; i++)
		{
			float posx = groundStartX + i;
			Sprite sprite = world.SPRITE_POOL.obtain();
            sprite.textureAtlas = null;
			sprite.position.set(posx, groundBlockY, m_pos_z_massive_start);
			sprite.mDrawRect.set(posx, groundBlockY, 1, 1);
			sprite.updateBounds();
			sprite.textureName = "data/environment/ground/green.png";
			sprite.type = Sprite.Type.massive;
			sprite.initAssets();
			world.level.gameObjects.add(sprite);
		}
	}

	/**
	 * Called from screens render method to generate more data for level*/
	public void update(OrthographicCamera cam)
	{
		float camWidth = cam.viewportWidth;
		float camHeight = cam.viewportHeight;
		float camStartX = cam.position.x - camWidth / 2;
		float camStartY = cam.position.y;

		Rectangle rect = World.RECT_POOL.obtain();
		rect.set(camStartX + camWidth - 0.1f, 0, 0.1f, camHeight);

		boolean foundGroundAtEndOfViewport = false;
		float lastGroundBlockEnd = 0;
		for(int i = 0; i < world.level.gameObjects.size; i++)
		{
			GameObject go = world.level.gameObjects.get(i);
			if(!(go instanceof Collider) && go instanceof Sprite && go.position.y == groundBlockY)
			{
				if(rect.overlaps(go.mDrawRect))
				{
					foundGroundAtEndOfViewport = true;
				}
				if(go.position.x + go.mDrawRect.width > lastGroundBlockEnd)
				{
					lastGroundBlockEnd = go.position.x + go.mDrawRect.width;
				}
			}
		}

		if(!foundGroundAtEndOfViewport)
		{
			if(lastGroundBlockEnd != 0)
			{
				loadGround(lastGroundBlockEnd, camWidth);
			}

			addGroundDecorationNoParallax(lastGroundBlockEnd, camWidth);
            addCoins(lastGroundBlockEnd, camWidth);
            addPlatforms(lastGroundBlockEnd, camWidth);
		}
		World.RECT_POOL.free(rect);
    }

	private void addClouds(float camStartX, float camWidth, float camHeight)
	{
		//add 1-5 clouds on each screen on random elevations not too low
		int cloudNum = MathUtils.random(3, 5);
		Array<String> currentCloudSet = clouds.get(currentCloudSetIndex);
		for(int i = 0; i < cloudNum; i++)
		{
			String cloudTxt = currentCloudSet.get(MathUtils.random(currentCloudSet.size - 1));
			float height = MathUtils.random(0.5f, 1.1f);
			float x = MathUtils.random(camStartX, camStartX + camWidth + 2);
			float y = MathUtils.random(3.5f, camHeight);
			Sprite sprite = world.SPRITE_POOL.obtain();
			sprite.position.set(x, y, m_pos_z_massive_start);
			sprite.mDrawRect.set(x, y, 0, height);
			sprite.updateBounds();
			sprite.type = Sprite.Type.passive;
			sprite.textureAtlas = "data/environment/clouds/clouds.pack";
			sprite.textureName = sprite.textureAtlas + ":" + cloudTxt;
			sprite.initAssets();
			world.level.parallaxClouds.objects.add(sprite);
		}
	}


	private void addCoins(float camStartX, float camWidth)
	{
		//add 4-15 coins on each screen on random elevations not too low
		int coinNum = MathUtils.random(4, 10);
		float xStart = MathUtils.random(camStartX, camStartX + camWidth + 2);
		float y = MathUtils.random(groundBlockY + 1f, groundBlockY + 1f + world.maryo.mDrawRect.height * 2.5f);
		for(int i = 0; i < coinNum; i++)
		{
			xStart += Coin.DEF_SIZE + Coin.DEF_SIZE * .25f;
			Coin coin = world.COIN_POOL.obtain();
			coin.position.set(xStart, y, m_pos_z_massive_start);
			coin.mDrawRect.set(xStart, y, Coin.DEF_SIZE, Coin.DEF_SIZE);
            coin.mColRect = new Rectangle();
			coin.mColRect.set(coin.mDrawRect);
			coin.updateBounds();
			int chance = MathUtils.random(0, 100);
			if(chance >= 90)//10%
			{
				coin.textureAtlas = "data/game/items/goldpiece/red.pack";
			}
			else
			{
				coin.textureAtlas = "data/game/items/goldpiece/yellow.pack";
			}

			coin.initAssets();
			world.level.gameObjects.add(coin);
		}
	}

	private void addPlatforms(float camStartX, float camWidth)
	{
		final float platformWidth = 0.67f;
		//add 4-15 coins on each screen on random elevations not too low
		int coinNum = MathUtils.random(4, 10);
		float xStart = MathUtils.random(camStartX, camStartX + camWidth + 2);
		float y = MathUtils.random(groundBlockY + 2f, groundBlockY + 1f + world.maryo.mDrawRect.height * 2.5f);

        Collider collider = new Collider(world, new Vector3(xStart, y, 0), platformWidth * coinNum, platformWidth);
        collider.mColRect = new Rectangle(collider.mDrawRect);
        collider.type = Sprite.Type.halfmassive;
        world.level.gameObjects.add(collider);

		for(int i = 0; i < coinNum; i++)
		{
			xStart += platformWidth;
			Sprite sprite = world.SPRITE_POOL.obtain();
			sprite.position.set(xStart, y, m_pos_z_massive_start);
			sprite.mDrawRect.set(xStart, y, 0, platformWidth);
			sprite.updateBounds();
			sprite.type = Sprite.Type.massive;
			sprite.textureAtlas = null;
			sprite.textureName = "data/game/box/brown.png";
			sprite.initAssets();
			world.level.gameObjects.add(sprite);
		}

	}

	private void addGroundDecorationNoParallax(float camStartX, float camWidth)
	{
		//add 1-5 bushes on each screen
		int num = MathUtils.random(5, 10);
		for(int i = 0; i < num; i++)
		{
			SpriteDescriptor sd = groundDecorationNoParallax.get(MathUtils.random(groundDecorationNoParallax.size - 1));
			float height = sd.height;
			float x = MathUtils.random(camStartX, camStartX + camWidth + 2);
			float y = groundBlockY + 1;
			Sprite sprite = world.SPRITE_POOL.obtain();
            sprite.textureAtlas = "data/environment/decoration/decoration.pack";
			sprite.position.set(x, y, m_pos_z_massive_start);
			sprite.mDrawRect.set(x, y, 0, height);
			sprite.updateBounds();
			sprite.type = Sprite.Type.passive;
			sprite.textureName = sd.texture;
			sprite.initAssets();
			world.level.gameObjects.add(sprite);
		}
	}

	private void addGroundDecorationLevel1(float camStartX, float camWidth)
	{
		//add 1-5 bushes on each screen
		int num = MathUtils.random(1, 5);
		for(int i = 0; i < num; i++)
		{
			SpriteDescriptor sd = groundDecorationParallaxLevel1.get(MathUtils.random(groundDecorationParallaxLevel1.size - 1));
			float height = sd.height;
			float x = MathUtils.random(camStartX, camStartX + camWidth + 2);
			float y = groundBlockY + 1;
			Sprite sprite = world.SPRITE_POOL.obtain();
			sprite.textureAtlas = "data/environment/decoration/decoration.pack";
			sprite.position.set(x, y, m_pos_z_massive_start);
			sprite.mDrawRect.set(x, y, 0, height);
			sprite.updateBounds();
			sprite.type = Sprite.Type.passive;
			sprite.textureName = sd.texture;
			sprite.initAssets();
			world.level.parallaxGround1.objects.add(sprite);
		}
	}

	public static class SpriteDescriptor
	{
		public String texture, textureAtlas;
		float height;

		public SpriteDescriptor(String texture, float height)
		{
			this.texture = texture;
			this.height = height;
		}
	}

}