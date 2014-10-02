package rs.pedjaapps.smc.screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;
import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.controller.MarioController;
import rs.pedjaapps.smc.model.BackgroundColor;
import rs.pedjaapps.smc.model.GameObject;
import rs.pedjaapps.smc.model.Maryo;
import rs.pedjaapps.smc.model.World;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.LevelLoader;
import rs.pedjaapps.smc.utility.Utility;
import rs.pedjaapps.smc.view.HUD;
import rs.pedjaapps.smc.utility.GameSaveUtility;

public class GameScreen extends AbstractScreen implements InputProcessor
{

    private World world;
    private OrthographicCamera cam;
    private OrthographicCamera pCamera;
    private OrthographicCamera guiCam;
	private OrthographicCamera bgCam;

    ShapeRenderer shapeRenderer = new ShapeRenderer();

    /**
     * Textures *
     */
    private ParticleEffect leafEffect;

    private SpriteBatch spriteBatch;
    private boolean debug = true;

    private BitmapFont debugFont;

    Vector2 camMin = new Vector2();
    Vector2 camMax = new Vector2();
    private MarioController controller;
    
    HUD hud;

    private int width, height;

    public enum GAME_STATE
    {
        GAME_READY, GAME_RUNNING, GAME_PAUSED, GAME_LEVEL_END, GAME_OVER
	}

    private GAME_STATE gameState;

    private HashMap<Integer, TouchInfo> touches = new HashMap<Integer, TouchInfo>();
    LevelLoader loader;
	
	Sound audioOn;
	Music music;

	public void setSize(int w, int h)
    {
        this.width = w;
        this.height = h;
    }

    public boolean isDebug()
    {
        return debug;
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    public GameScreen(MaryoGame game)
    {
		super(game);
        gameState = GAME_STATE.GAME_READY;
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        world = new World();
        hud = new HUD();
        //dPad = new DPad(0.3f * width);
        //jump = new BtnJump(0.20f * height, new Vector2(width - 0.25f * width, 0.05f * height));
        this.cam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        this.cam.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        //this.cam.position.set(world.getMario().getPosition().x, world.getMario().getPosition().y, 0);
        this.cam.update();

        pCamera = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        this.cam.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        pCamera.position.set(Constants.CAMERA_WIDTH / 2f, Constants.CAMERA_HEIGHT / 2f, 0);
        pCamera.update();

        guiCam = new OrthographicCamera(width, height);
        guiCam.position.set(width / 2f, height / 2f, 0);
        guiCam.update();

		bgCam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        bgCam.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        bgCam.position.set(cam.position.x, cam.position.y, 0);
        bgCam.update();

        spriteBatch = new SpriteBatch();
        debugFont = new BitmapFont();
        debugFont.setColor(Color.RED);
        debugFont.setScale(1.3f);

        BitmapFont guiFont = new BitmapFont(Gdx.files.internal("data/fonts/default.fnt"));
        guiFont.setColor(Color.WHITE);
        guiFont.setScale(1f);

        BitmapFont guiFontBold = new BitmapFont(Gdx.files.internal("data/fonts/default.fnt"));
        guiFontBold.setColor(Color.WHITE);
        guiFontBold.setScale(1f);

        loadTextures();
        controller = new MarioController(world);
		//Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(this);

        for (int i = 0; i < 5; i++) //handle max 4 touches
        {
            touches.put(i, new TouchInfo());
        }
        loader = new LevelLoader();
        //Gdx.graphics.setContinuousRendering(false);
    }

    @Override
    public void show()
    {
		music = Assets.manager.get(loader.getLevel().getMusic().first());
        if(Assets.playMusic)music.play();
		GameSaveUtility.getInstance().nextLevel();
    }

    @Override
    public void render(float delta)
    {
		if (delta > 0.1f) delta = 0.1f;
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
        if (gameState == GAME_STATE.GAME_RUNNING)controller.update(delta);
        moveCamera(cam, world.getMario().getPosition().x, world.getMario().getPosition().y);
        drawBackground();
        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        drawObjects(delta);
        world.getMario().render(spriteBatch);
        spriteBatch.end();

        spriteBatch.setProjectionMatrix(pCamera.combined);
        spriteBatch.begin();
        if (gameState == GAME_STATE.GAME_RUNNING)leafEffect.draw(spriteBatch, delta);
        spriteBatch.end();

        spriteBatch.setProjectionMatrix(guiCam.combined);
        spriteBatch.begin();
        if (debug)drawDebugText();
        spriteBatch.end();
		if (debug)drawDebug();
		
        hud.render(gameState, delta);

		//cleanup
		for(GameObject obj : world.trashObjects)
		{
			world.getLevel().getGameObjects().remove(obj);
		}
    }

	

	private void drawBackground()
	{
        BackgroundColor bgColor = world.getLevel().getBgColor();
        bgColor.render(bgCam);
        bgCam.position.set(cam.position.x * Constants.BACKGROUND_SCROLL_SPEED + cam.viewportWidth * 0.44f,
						   cam.position.y * Constants.BACKGROUND_SCROLL_SPEED + cam.viewportHeight * 0.44f, 0);
        bgCam.update();
        spriteBatch.setProjectionMatrix(bgCam.combined);
        spriteBatch.begin();
		world.getLevel().getBg1().render(spriteBatch);
		world.getLevel().getBg2().render(spriteBatch);
        spriteBatch.end();
	}

    public void moveCamera(OrthographicCamera cam, float x, float y)
    {
        cam.position.set(x, y, 0);
        cam.update();
        keepCameraInBounds(cam);
    }

    private void keepCameraInBounds(OrthographicCamera cam)
    {
        float camX = cam.position.x;
        float camY = cam.position.y;

        camMin.set(cam.viewportWidth, cam.viewportHeight);
        camMin.scl(cam.zoom / 2); //bring to center and scale by the zoom level
        camMax.set(world.getLevel().getWidth(), world.getLevel().getHeight());
        camMax.sub(camMin); //bring to center

        //keep camera within borders
        camX = Math.min(camMax.x, Math.max(camX, camMin.x));
        camY = Math.min(camMax.y, Math.max(camY, camMin.y));

        cam.position.set(camX, camY, cam.position.z);
        cam.update();
    }

    private void drawObjects(float delta)
    {
		Rectangle maryoBWO = world.createMaryoRectWithOffset(10);
		for (GameObject go : world.getLevel().getGameObjects())
		{
			if (maryoBWO.overlaps(go.getBody()))
			{
				if (gameState == GAME_STATE.GAME_RUNNING)go.update(delta);
			}
		}
		for (GameObject object : world.getDrawableObjects(cam.position.x, cam.position.y))
        {
            object.render(spriteBatch);
        }
    }

	private void drawDebug() 
	{
		// render blocks
		shapeRenderer.setProjectionMatrix(cam.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		for (GameObject go : world.getVisibleObjects()) 
		{
            Rectangle body = go.getBody();
            Rectangle bounds = go.getBounds();
            shapeRenderer.setColor(new Color(0, 1, 0, 1));
            shapeRenderer.rect(body.x, body.y, body.width, body.height);
            shapeRenderer.setColor(new Color(1, 0, 0, 1));
            shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		// render maryo
		Maryo maryo = world.getMario();
		Rectangle body = maryo.getBody();
        Rectangle bounds = maryo.getBounds();
		shapeRenderer.setColor(new Color(0, 1, 0, 1));
		shapeRenderer.rect(body.x, body.y, body.width, body.height);
        shapeRenderer.setColor(new Color(1, 0, 0, 1));
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
		shapeRenderer.end();
	}

    private void drawDebugText()
    {
        String debugMessage = generateDebugMessage();
        BitmapFont.TextBounds tb = debugFont.getBounds(debugMessage);
        debugFont.drawMultiLine(spriteBatch, debugMessage, 20, height - 20);
    }

    private String generateDebugMessage()
    {
        return "Level: width=" + world.getLevel().getWidth() + ", height=" + world.getLevel().getHeight()
			+ "\n" + "Player: x=" + world.getMario().getPosition().x + ", y=" + world.getMario().getPosition().y
            + "\n" + "World Camera: x=" + cam.position.x + ", y=" + cam.position.y
            + "\n" + "BG Camera: x=" + bgCam.position.x + ", y=" + bgCam.position.y
            + "\n" + "FPS: " + Gdx.graphics.getFramesPerSecond();
    }

    @Override
    public void resize(int width, int height)
    {
		this.width = width;
        this.height = height;
    }

    @Override
    public void hide()
    {
        //Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause()
    {
		gameState = GAME_STATE.GAME_PAUSED;
    }

    @Override
    public void resume()
    {
		//Gdx.input.setInputProcessor(this);
    }

    @Override
    public void dispose()
    {
		music.stop();
        Gdx.input.setInputProcessor(null);
        Assets.dispose();
    }

    @Override
    public void loadAssets()
    {
        Array<String[]> data = loader.parseLevelData(Gdx.files.internal("data/levels/test_lvl.data").readString());

        for (String[] s : data)
        {
            if (LevelLoader.isTexture(s[0]))
            {
                Assets.manager.load(s[1], Texture.class, Assets.textureParameter);
            }
            else
            {
                Assets.manager.load(s[1], LevelLoader.getTextureClassForKey(s[0]));
            }
        }
        Assets.manager.load("data/hud/controls.pack", TextureAtlas.class);
        Assets.manager.load("data/maryo/small.pack", TextureAtlas.class);//TODO load depending on states
		Assets.manager.load("data/hud/pause.png", Texture.class);
		Assets.manager.load("data/sounds/audio_on.ogg", Sound.class);
		Assets.manager.load("data/hud/itembox.png", Texture.class);
        Assets.manager.load("data/hud/maryo_l.png", Texture.class);
        Assets.manager.load("data/hud/gold_m.png", Texture.class);
		
		Assets.manager.load("data/sounds/item/goldpiece_1.ogg", Sound.class);
        Assets.manager.load("data/sounds/item/goldpiece_red.wav", Sound.class);
        
    }

	private void loadTextures()
    {
        leafEffect = new ParticleEffect();
        leafEffect.load(Gdx.files.internal("data/animation/particles/leaf_emitter.p"), Gdx.files.internal("data/animation/particles"));
        leafEffect.setPosition(Constants.CAMERA_WIDTH / 2, Constants.CAMERA_HEIGHT);
        leafEffect.start();
    }

    @Override
    public void afterLoadAssets()
    {
        loader.parseLevel(world, Gdx.files.internal("data/levels/test_lvl.smclvl").readString());
        hud.loadAssets();
        Array<Maryo.MarioState> states = new Array<Maryo.MarioState>();
        states.add(Maryo.MarioState.small);//TODO load from level
        Maryo maryo = new Maryo(world, loader.getLevel().getSpanPosition(), new Vector2(0.85f, 0.85f), states);
        maryo.loadTextures();
        world.setMario(maryo);
        world.setLevel(loader.getLevel());
        controller.setMaryo(maryo);
		audioOn = Assets.manager.get("data/sounds/audio_on.ogg", Sound.class);
    }

    // * InputProcessor methods ***************************//

    @Override
    public boolean keyDown(int keycode)
    {
        if (gameState == GAME_STATE.GAME_READY)gameState = GAME_STATE.GAME_RUNNING;
        if (keycode == Input.Keys.LEFT)
        {
            controller.leftPressed();
            hud.leftPressed();
        }
        if (keycode == Input.Keys.RIGHT)
        {
            controller.rightPressed();
            hud.rightPressed();
        }
        if (keycode == Input.Keys.SPACE)
        {
            controller.jumpPressed();
            hud.jumpPressed();
        }
        if (keycode == Input.Keys.X)
        {
            controller.firePressed();
            hud.firePressed();
        }
        if (keycode == Input.Keys.DOWN)
        {
            controller.downPressed();
            hud.downPressed();
        }
        if (keycode == Input.Keys.UP)
        {
            controller.upPressed();
            hud.upPressed();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        if (keycode == Input.Keys.LEFT)
        {
            controller.leftReleased();
            hud.leftReleased();
        }
        if (keycode == Input.Keys.RIGHT)
        {
            controller.rightReleased();
            hud.rightReleased();
        }
        if (keycode == Input.Keys.SPACE)
        {
            controller.jumpReleased();
            hud.jumpReleased();
        }
        if (keycode == Input.Keys.X)
        {
            controller.fireReleased();
            hud.fireReleased();
        }
        if (keycode == Input.Keys.DOWN)
        {
            controller.downReleased();
            hud.downReleased();
        }
        if (keycode == Input.Keys.UP)
        {
            controller.upReleased();
            hud.upReleased();
        }
		if(keycode == Input.Keys.BACK)
		{
			
		}
        if (keycode == Input.Keys.D)
            debug = !debug;
        return true;
    }

    @Override
    public boolean keyTyped(char character)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button)
    {
		if (gameState == GAME_STATE.GAME_READY)gameState = GAME_STATE.GAME_RUNNING;
        //System.out.println("Touch point: " + x + "x" + y);
        if (!Gdx.app.getType().equals(Application.ApplicationType.Android))
            return false;
        //float gameX = convertTouchPointToGamePoint(x, true);
        //float gameY = convertTouchPointToGamePoint(y, false);
        //System.out.println("Game point: " + gameX + "x" + gameY);
        //System.out.println("Is touching right: " + Intersector.isPointInPolygon(hud.rightPolygon, new Vector2(x, invertY(y))));
        if (pointer < 5)
        {
            if (Intersector.isPointInPolygon(hud.rightPolygon, new Vector2(x, invertY(y))))//is right
            {
                controller.rightPressed();
                //dPad.setClickedArea(DPad.CLICKED_AREA.RIGHT);
                touches.get(pointer).clickArea = HUD.Key.right;
                hud.rightPressed();
            }
            if (Intersector.isPointInPolygon(hud.leftPolygon, new Vector2(x, invertY(y))))//is left
            {
                controller.leftPressed();
                //dPad.setClickedArea(DPad.CLICKED_AREA.LEFT);
                touches.get(pointer).clickArea = HUD.Key.left;
                hud.leftPressed();
            }
            if (Intersector.isPointInPolygon(hud.upPolygon, new Vector2(x, invertY(y))))//is top
            {
                controller.upPressed();
                touches.get(pointer).clickArea = HUD.Key.up;
                hud.upPressed();
            }
            if (Intersector.isPointInPolygon(hud.downPolygon, new Vector2(x, invertY(y))))//is bottom
            {
                controller.downPressed();
                touches.get(pointer).clickArea = HUD.Key.down;
                hud.downPressed();
            }
            if (hud.jumpR.contains(x, invertY(y)))
            {
                controller.jumpPressed();
                touches.get(pointer).clickArea = HUD.Key.jump;
                hud.jumpPressed();
            }
			if (hud.pauseR.contains(x, invertY(y)))
            {
				touches.get(pointer).clickArea = HUD.Key.pause;
                hud.pausePressed();
            }
			if (gameState == GAME_STATE.GAME_PAUSED && hud.soundR.contains(x, invertY(y)))
            {
				touches.get(pointer).clickArea = HUD.Key.sound;
                hud.soundPressed();
            }
			if (gameState == GAME_STATE.GAME_PAUSED && hud.musicR.contains(x, invertY(y)))
            {
				touches.get(pointer).clickArea = HUD.Key.music;
                hud.musicPressed();
            }
			if (gameState == GAME_STATE.GAME_PAUSED && hud.playR.contains(x, invertY(y)))
            {
				touches.get(pointer).clickArea = HUD.Key.play;
                hud.playPressed();
            }
        }

        return true;
    }

    private float invertY(int y)
    {
        return height - y;
    }

    private float convertTouchPointToGamePoint(int val, boolean isX)
    {
        if (isX)
        {
            return val / ((float)width / Constants.CAMERA_WIDTH);
        }
        else
        {
            return (height - val) / ((float)height / Constants.CAMERA_HEIGHT);
        }
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button)
    {
        if (!Gdx.app.getType().equals(Application.ApplicationType.Android))
            return false;
        TouchInfo ti = touches.get(pointer);
        if (ti != null)
        {
            switch (ti.clickArea)
            {
                case right:
                    controller.rightReleased();
                    hud.rightReleased();
                    break;
                case left:
                    controller.leftReleased();
                    hud.leftReleased();
                    break;
                case up:
                    controller.upReleased();
                    hud.upReleased();
                    break;
                case down:
                    controller.downReleased();
                    hud.downReleased();
                    break;
                case jump:
                    controller.jumpReleased();
                    hud.jumpReleased();
                    break;
				case pause:
					if (gameState == GAME_STATE.GAME_RUNNING)gameState = GAME_STATE.GAME_PAUSED;
                    hud.pauseReleased();
                    break;
				case play:
					gameState = GAME_STATE.GAME_RUNNING;
                    hud.playReleased();
                    break;
				case sound:
					if(Utility.toggleSound())
					{
						audioOn.play();
					}
					hud.soundReleased();
                    break;
				case music:
					if(Utility.toggleMusic())
					{
						music.play();
					}
					else
					{
						music.pause();
					}
					hud.musicReleased();
                    break;
            }
            touches.get(pointer).clickArea = HUD.Key.none;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public World getWorld()
    {
        return world;
    }

    public void setWorld(World world)
    {
        this.world = world;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    class TouchInfo
    {
        public float touchX = 0;
        public float touchY = 0;
        public boolean touched = false;
        HUD.Key clickArea = HUD.Key.none;
    }
}
