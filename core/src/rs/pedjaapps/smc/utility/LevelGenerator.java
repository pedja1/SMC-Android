package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

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
	public static final Array<SpriteDescriptor> groundDecorationParallaxLevel2 = new Array<>();

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

		groundDecorationNoParallax.add(new SpriteDescriptor("data/environment/decoration/cactus_1.png", .5f));
		groundDecorationNoParallax.add(new SpriteDescriptor("data/environment/decoration/gras_middle.png", .25f));
		groundDecorationNoParallax.add(new SpriteDescriptor("data/environment/decoration/plant_m.png", .25f));
		groundDecorationNoParallax.add(new SpriteDescriptor("data/environment/decoration/plant_r.png", .25f));
		groundDecorationNoParallax.add(new SpriteDescriptor("data/environment/decoration/small_2.png", .25f));
		groundDecorationNoParallax.add(new SpriteDescriptor("data/environment/decoration/small_4.png", .25f));
		groundDecorationNoParallax.add(new SpriteDescriptor("data/environment/decoration/small_5.png", .5f));
		groundDecorationNoParallax.add(new SpriteDescriptor("data/environment/decoration/small_6.png", .5f));

		groundDecorationParallaxLevel1.add(new SpriteDescriptor("data/environment/decoration/cactus_2.png", 1f));
		groundDecorationParallaxLevel1.add(new SpriteDescriptor("data/environment/decoration/cactus_3.png", 2f));
		groundDecorationParallaxLevel1.add(new SpriteDescriptor("data/environment/decoration/cactus_4.png", 2f));
		groundDecorationParallaxLevel1.add(new SpriteDescriptor("data/environment/decoration/big_1.png", 2f));
		groundDecorationParallaxLevel1.add(new SpriteDescriptor("data/environment/decoration/big_2.png", 2f));
		groundDecorationParallaxLevel1.add(new SpriteDescriptor("data/environment/decoration/big_4.png", 2f));
		groundDecorationParallaxLevel1.add(new SpriteDescriptor("data/environment/decoration/big_5.png", 2f));
		groundDecorationParallaxLevel1.add(new SpriteDescriptor("data/environment/decoration/big_6.png", 2f));
		groundDecorationParallaxLevel1.add(new SpriteDescriptor("data/environment/decoration/big_plant_2.png", 2f));
		groundDecorationParallaxLevel1.add(new SpriteDescriptor("data/environment/decoration/medium_1.png", .5f));
		groundDecorationParallaxLevel1.add(new SpriteDescriptor("data/environment/decoration/medium_2.png", .5f));

		groundDecorationParallaxLevel2.add(new SpriteDescriptor("data/environment/decoration/1.png", 4f));
		groundDecorationParallaxLevel2.add(new SpriteDescriptor("data/environment/decoration/1_front.png", 4f));
		groundDecorationParallaxLevel2.add(new SpriteDescriptor("data/environment/decoration/2.png", 4f));
		groundDecorationParallaxLevel2.add(new SpriteDescriptor("data/environment/decoration/2_front.png", 4f));
		groundDecorationParallaxLevel2.add(new SpriteDescriptor("data/environment/decoration/beanstalk.png", 4f));
		groundDecorationParallaxLevel2.add(new SpriteDescriptor("data/environment/decoration/beanstalk_2.png", 4f));
		groundDecorationParallaxLevel2.add(new SpriteDescriptor("data/environment/decoration/big_plant_1.png", 4f));
		groundDecorationParallaxLevel2.add(new SpriteDescriptor("data/environment/decoration/lights.png", 4f));
		groundDecorationParallaxLevel2.add(new SpriteDescriptor("data/environment/decoration/normal.png", 4f));
		groundDecorationParallaxLevel2.add(new SpriteDescriptor("data/environment/decoration/snow.png", 4f));

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

		Maryo maryo = new Maryo(world, new Vector3(2, 2, 0.0999f), new Vector2(0.9f, 0.9f));
		maryo.initAssets();
		world.maryo = maryo;

		//load first music
		world.level.music = "data/music/land/land_5.mp3";

		//load first background
		Background background = new Background(new Vector2(), new Vector2(.2f, .2f), "data/environment/background/green_junglehills.png");
		Color color1 = World.COLOR_POOL.obtain();
		color1.set(0.117647059f, 0.705882353f, 0.050980392f, 0);
		background.color1 = color1;
		Color color2 = World.COLOR_POOL.obtain();
		color2.set(0, 0.392156863f, 0, 0);
		background.color2 = color2;
		world.level.background = background;

		world.level.parallaxClouds = new Parallax(new Vector2(.6f, .6f));
		world.level.parallaxGround1 = new Parallax(new Vector2(.9f, .9f));
		world.level.parallaxGround2 = new Parallax(new Vector2(.4f, .4f));

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

        world.level.parallaxGround2.nextViewportCallback = new Parallax.NextViewportCallback()
        {
            @Override
            public void onNextViewport(float viewportStartX, float viewportWidth, float viewportHeight)
            {
                addGroundDecorationLevel2(viewportStartX, viewportWidth);
            }
        };

		//load ground for visible area(screen width)
		float groundStartX = -0.328125f;
		loadGround(groundStartX, cam.viewportWidth);
		addClouds(cam.position.x - cam.viewportWidth / 2, cam.viewportWidth, cam.viewportHeight);
		addGroundDecorationNoParallax(cam.position.x - cam.viewportWidth / 2, cam.viewportWidth);
		addGroundDecorationLevel1(cam.position.x - cam.viewportWidth / 2, cam.viewportWidth);
		addGroundDecorationLevel2(cam.position.x - cam.viewportWidth / 2, cam.viewportWidth);
		preLoaded = true;
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
			sprite.mColRect.set(sprite.mDrawRect);
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
			if(go instanceof Sprite && go.position.y == groundBlockY)
			{
				if(rect.overlaps(go.mColRect))
				{
					foundGroundAtEndOfViewport = true;
				}
				if(go.position.x + go.mColRect.width > lastGroundBlockEnd)
				{
					lastGroundBlockEnd = go.position.x + go.mColRect.width;
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
			sprite.mColRect.set(sprite.mDrawRect);
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
            sprite.textureAtlas = null;
			sprite.position.set(x, y, m_pos_z_massive_start);
			sprite.mDrawRect.set(x, y, 0, height);
			sprite.mColRect.set(sprite.mDrawRect);
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
            sprite.textureAtlas = null;
			sprite.position.set(x, y, m_pos_z_massive_start);
			sprite.mDrawRect.set(x, y, 0, height);
			sprite.mColRect.set(sprite.mDrawRect);
			sprite.updateBounds();
			sprite.type = Sprite.Type.passive;
			sprite.textureName = sd.texture;
			sprite.initAssets();
			world.level.parallaxGround1.objects.add(sprite);
		}
	}

	private void addGroundDecorationLevel2(float camStartX, float camWidth)
	{
		//add 1-5 bushes on each screen
		int num = MathUtils.random(1, 3);
		for(int i = 0; i < num; i++)
		{
			SpriteDescriptor sd = groundDecorationParallaxLevel2.get(MathUtils.random(groundDecorationParallaxLevel2.size - 1));
			float height = sd.height;
			float x = MathUtils.random(camStartX, camStartX + camWidth + 2);
			float y = groundBlockY + 1;
			Sprite sprite = world.SPRITE_POOL.obtain();
            sprite.textureAtlas = null;
			sprite.position.set(x, y, m_pos_z_massive_start);
			sprite.mDrawRect.set(x, y, 0, height);
			sprite.mColRect.set(sprite.mDrawRect);
			sprite.updateBounds();
			sprite.type = Sprite.Type.passive;
			sprite.textureName = sd.texture;
			sprite.initAssets();
			world.level.parallaxGround2.objects.add(sprite);
		}
	}

	public static class SpriteDescriptor
	{
		public String texture;
		float height;

		public SpriteDescriptor(String texture, float height)
		{
			this.texture = texture;
			this.height = height;
		}
	}

}