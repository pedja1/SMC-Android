package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.LevelRegion;
import rs.pedjaapps.smc.object.Player;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
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

	private static final float m_pos_z_passive_start = 0.01f;
	private static final  float m_pos_z_massive_start = 0.08f;
	private static final  float m_pos_z_front_passive_start = 0.1f;
	private static final  float m_pos_z_halfmassive_start = 0.04f;

	private static String[] CLOUDS = new String[]{"environment/clouds/cloud-1", "environment/clouds/cloud-2", "environment/clouds/cloud-3"};

	private TextureRegion tileBase;

	private int viewPortIndex = 0;

	private Json json;

    public LevelGenerator()
    {
		this.world = World.getInstance();
		json = new Json();
    }

	/**
	 * Preload level
	 * Loads all starting data for level*/
	public void preLoad(OrthographicCamera cam)
	{
		if(preLoaded)
			return;
		//load player at starting position

		tileBase = ((TextureAtlas)Assets.manager.get(Assets.DEFAULT_ATLAS)).findRegion("environment/tiles/1");

		Player player = new Player(2, 2, 1.182656827f, 1);
		player.initAssets();
		world.player = player;

		//load first music
		//world.level.music = "data/music/land/land_5.mp3";

		//load first background
		world.level.background = new Background(new Vector2(), new Vector2(.1f, .1f), "environment/backgrounds/forest_small", Assets.DEFAULT_ATLAS);
		world.level.background2 = new Background(new Vector2(), new Vector2(.2f, .2f), "environment/backgrounds/forest_big", Assets.DEFAULT_ATLAS);
		world.level.background3 = new Background(new Vector2(), new Vector2(.05f, .05f), "environment/backgrounds/jungle_mountains", Assets.DEFAULT_ATLAS);

		world.level.backgroundColor = new Background(null, null, null, null);
		Color color1 = World.COLOR_POOL.obtain();
		color1.set(0.392156863f, 0.745098039f, 0.980392157f, 0);
		world.level.backgroundColor.color1 = color1;
		Color color2 = World.COLOR_POOL.obtain();
		color2.set(0.039215686f, 0.588235294f, 0.784313725f, 0);
		world.level.backgroundColor.color2 = color2;

		world.level.parallaxClouds = new Parallax(new Vector2(.5f, .5f));
		world.level.parallaxGround = new Parallax(new Vector2(.1f, .1f));

        world.level.parallaxClouds.nextViewportCallback = new Parallax.NextViewportCallback()
        {
            @Override
            public void onNextViewport(float viewportStartX, float viewportWidth, float viewportHeight)
            {
                addClouds(viewportStartX, viewportWidth, viewportHeight);
            }
        };

        world.level.parallaxGround.nextViewportCallback = new Parallax.NextViewportCallback()
        {
            @Override
            public void onNextViewport(float viewportStartX, float viewportWidth, float viewportHeight)
            {
                //addGroundDecorationLevel1(viewportStartX, viewportWidth);
            }
        };

		//load ground for visible area(screen width)
		float groundStartX = -0.328125f;
		addClouds(cam.position.x - cam.viewportWidth / 2, cam.viewportWidth, cam.viewportHeight);
		addGround(cam);
		//addGroundDecorationLevel1(cam.position.x - cam.viewportWidth / 2, cam.viewportWidth);
		preLoaded = true;
	}

	private void addGround(OrthographicCamera cam)
	{
		float camPosX = cam.position.x - cam.viewportWidth / 2;
		if(camPosX + cam.viewportWidth >= cam.viewportWidth * viewPortIndex)
		{
			float nextViewportX = cam.viewportWidth * viewPortIndex;
			LevelRegion region = json.fromJson(LevelRegion.class, Gdx.files.internal("data/levels/e1.json").readString());
			System.out.println(region.gameObjects.size);
			for(GameObject go : region.gameObjects)
			{
				go.position.x += nextViewportX;
				go.initAssets();
			}
			world.level.gameObjects.addAll(region.gameObjects);
			addClouds(camPosX, cam.viewportWidth, cam.viewportHeight);
			viewPortIndex++;
		}
	}


	/**
	 * Called from screens render method to generate more data for level*/
	public void update(OrthographicCamera cam)
	{
		addGround(cam);
    }

	private void addClouds(float camStartX, float camWidth, float camHeight)
	{
		//add 1-5 clouds on each screen on random elevations not too low
		int cloudNum = MathUtils.random(3, 5);
		for(int i = 0; i < cloudNum; i++)
		{
			String cloudTxt = CLOUDS[MathUtils.random(CLOUDS.length - 1)];
			float x = MathUtils.random(camStartX, camStartX + camWidth + 2);
			float y = MathUtils.random(3.5f, camHeight);
			Sprite sprite = world.SPRITE_POOL.obtain();
			sprite.position.set(x, y);
			sprite.type = Sprite.Type.passive;
			sprite.textureAtlas = Assets.DEFAULT_ATLAS;
			sprite.textureName = cloudTxt;
			sprite.initAssets();

			float tileBaseWidth = tileBase.getRegionWidth();

			float width = 1 / (tileBaseWidth / sprite.region.getRegionWidth());

			sprite.bounds.set(x, y, width, width / ((float) sprite.region.getRegionWidth() / (float) sprite.region.getRegionHeight()));
			world.level.parallaxClouds.objects.add(sprite);
		}
	}

}