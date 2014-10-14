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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
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
import rs.pedjaapps.smc.utility.PrefsManager;
import rs.pedjaapps.smc.utility.Utility;
import rs.pedjaapps.smc.view.ConfirmDialog;
import rs.pedjaapps.smc.view.HUD;
import rs.pedjaapps.smc.utility.GameSaveUtility;

public class GameScreen extends AbstractScreen implements InputProcessor
{

    private World world;
    public OrthographicCamera cam;
    private OrthographicCamera pCamera;
    public OrthographicCamera guiCam;
	private OrthographicCamera bgCam;

    ShapeRenderer shapeRenderer = new ShapeRenderer();

    /**
     * Textures *
     */
    private ParticleEffect leafEffect;

    private SpriteBatch spriteBatch;
    private boolean debug = PrefsManager.isDebug();

    private BitmapFont debugFont;

    Vector2 camMin = new Vector2();
    Vector2 camMax = new Vector2();
    private MarioController controller;
    
    public HUD hud;

    private int width, height;

	public void setGameState(GAME_STATE gameState)
	{
		this.gameState = gameState;
		if(gameState == GAME_STATE.PLAYER_DEAD)hud.updateTimer = false;
	}

	public GAME_STATE getGameState()
	{
		return gameState;
	}

    public enum GAME_STATE
    {
        GAME_READY, GAME_RUNNING, GAME_PAUSED, GAME_LEVEL_END, GAME_OVER, PLAYER_DEAD,
		NO_UPDATE
	}

    private GAME_STATE gameState;

    private HashMap<Integer, TouchInfo> touches = new HashMap<Integer, TouchInfo>();
    LevelLoader loader;
	
	Sound audioOn;
	Music music;
	
	float goAlpha = 0.0f;
	boolean goTouched = false;

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

    ConfirmDialog exitDialog;

    public GameScreen(MaryoGame game, boolean fromMenu)
    {
		super(game);
        gameState = GAME_STATE.GAME_READY;
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        world = new World(this);
        hud = new HUD(world);
        this.cam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        this.cam.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        this.cam.update();

        pCamera = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        pCamera.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
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

        loadTextures();
        controller = new MarioController(world);
		Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(this);

        for (int i = 0; i < 5; i++) //handle max 4 touches
        {
            touches.put(i, new TouchInfo());
        }
        loader = new LevelLoader();
        //Gdx.graphics.setContinuousRendering(false);
		if(fromMenu)GameSaveUtility.getInstance().startLevelFresh();

        exitDialog = new ConfirmDialog(this, guiCam);
    }

    @Override
    public void show()
    {
		music = Assets.manager.get(loader.getLevel().getMusic().first());
        if(Assets.playMusic)music.play();
    }

    @Override
    public void render(float delta)
    {
		if (delta > 0.1f) delta = 0.1f;
        //debug
        //delta = 0.0666f;
        //debug end
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
        if (gameState == GAME_STATE.GAME_RUNNING)controller.update(delta);
        moveCamera(cam, world.getMario().getPosition().x, world.getMario().getPosition().y);
        drawBackground();
        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        drawObjects(delta);
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
		
		if(gameState == GAME_STATE.GAME_OVER)
		{
			handleGameOver(delta);
		}

        exitDialog.render(spriteBatch);

		//cleanup
		for(GameObject obj : world.trashObjects)
		{
			world.getLevel().getGameObjects().remove(obj);
		}
    }

	private void handleGameOver(float delta)
	{
		if(GameSaveUtility.getInstance().save.lifes < 0)
		{
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			Gdx.gl.glEnable(GL20.GL_BLEND);

			shapeRenderer.setProjectionMatrix(guiCam.combined);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(0, 0, 0, 0.5f);
			shapeRenderer.rect(0, 0, width, height);
			shapeRenderer.end();
			
			spriteBatch.setProjectionMatrix(guiCam.combined);
			spriteBatch.begin();
			
			Texture go = Assets.manager.get("data/hud/game_over.png");
			go.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			float width = this.width * 0.8f;
			float height = width / 4;
			
			float x = this.width / 2 - width / 2;
			float y = this.height / 2 - height / 2;
			spriteBatch.draw(go, x, y, width, height);
			
			spriteBatch.end();
			if(!goTouched)return;
		}
		
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glEnable(GL20.GL_BLEND);

		shapeRenderer.setProjectionMatrix(guiCam.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0, 0, 0, goAlpha += 0.033f);
		shapeRenderer.rect(0, 0, width, height);
		shapeRenderer.end();

        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        spriteBatch.end();
        //background changes to black if i don't add this after blend
		
		if(goAlpha >= 1)
		{
			if(GameSaveUtility.getInstance().save.lifes < 0)
			{
				game.setScreen(new LoadingScreen(new MainMenuScreen(game), false));
			}
			else
			{
				game.setScreen(new LoadingScreen(new GameScreen(game, false), false));
			}
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
				if (gameState == GAME_STATE.GAME_RUNNING || (gameState == GAME_STATE.PLAYER_DEAD && go instanceof Maryo))
				{
					go.update(delta);
				}
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
        shapeRenderer.setColor(new Color(0, 0, 1, 1));
        shapeRenderer.rect(maryo.debugRayRect.x, maryo.debugRayRect.y, maryo.debugRayRect.width, maryo.debugRayRect.height);
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
            + "\n" + "Player Vel: x=" + world.getMario().getVelocity().x + ", y=" + world.getMario().getVelocity().y
            + "\n" + "World Camera: x=" + cam.position.x + ", y=" + cam.position.y
            + "\n" + "BG Camera: x=" + bgCam.position.x + ", y=" + bgCam.position.y
            + "\n" + "JavaHeap: " + Gdx.app.getJavaHeap() / 1000000 + "MB"
            + "\n" + "NativeHeap: " + Gdx.app.getNativeHeap() / 1000000 + "MB"
            + "\n" + "FPS: " + Gdx.graphics.getFramesPerSecond();
    }

    @Override
    public void resize(int width, int height)
    {
		this.width = width;
        this.height = height;

        Constants.initCamera();

        cam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        cam.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        cam.update();

        pCamera = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        pCamera.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        pCamera.position.set(Constants.CAMERA_WIDTH / 2f, Constants.CAMERA_HEIGHT / 2f, 0);
        pCamera.update();

        guiCam = new OrthographicCamera(width, height);
        guiCam.position.set(width / 2f, height / 2f, 0);
        guiCam.update();

        bgCam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        bgCam.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        bgCam.position.set(cam.position.x, cam.position.y, 0);
        bgCam.update();
        exitDialog.resize();
        hud.resize(width, height);
    }

    @Override
    public void hide()
    {
        //Gdx.input.setInputProcessor(null);
        music.stop();
    }

    @Override
    public void pause()
    {
		gameState = GAME_STATE.GAME_PAUSED;
        music.stop();
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
        exitDialog.dispose();
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
        for(Maryo.MaryoState ms : Maryo.MaryoState.values())
        {
            Assets.manager.load("data/maryo/" + ms.toString() + ".pack", TextureAtlas.class);
        }
		hud.loadAssets();
		
        //audio
        Assets.manager.load("data/sounds/audio_on.ogg", Sound.class);
		Assets.manager.load("data/sounds/item/goldpiece_1.ogg", Sound.class);
        Assets.manager.load("data/sounds/item/goldpiece_red.wav", Sound.class);
        Assets.manager.load("data/sounds/player/dead.ogg", Sound.class);
        Assets.manager.load("data/sounds/player/jump_big.ogg", Sound.class);
        Assets.manager.load("data/sounds/player/jump_big_power.ogg", Sound.class);
        Assets.manager.load("data/sounds/player/jump_small.ogg", Sound.class);
        Assets.manager.load("data/sounds/player/jump_small_power.ogg", Sound.class);
        Assets.manager.load("data/sounds/player/jump_ghost.ogg", Sound.class);
        Assets.manager.load("data/sounds/player/ghost_end.ogg", Sound.class);
        Assets.manager.load("data/sounds/player/pickup_item.wav", Sound.class);
        Assets.manager.load("data/sounds/player/powerdown.ogg", Sound.class);
        Assets.manager.load("data/sounds/player/run_stop.ogg", Sound.class);
        Assets.manager.load("data/sounds/wall_hit.wav", Sound.class);
        Assets.manager.load("data/sounds/enemy/furball/die.ogg", Sound.class);


        /*FreetypeFontLoader.FreeTypeFontLoaderParameter coinSize = Constants.defaultFontParams;
        coinSize.fontParameters.size = 10;
        coinSize.fontParameters.characters = "0123456789";
        Assets.manager.load("coin.ttf", BitmapFont.class, coinSize);*/

        FreetypeFontLoader.FreeTypeFontLoaderParameter debugFontParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        debugFontParams.fontFileName = Constants.DEFAULT_FONT_FILE_NAME;
        debugFontParams.fontParameters.size = height / 20;
        debugFontParams.fontParameters.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
        Assets.manager.load("debug.ttf", BitmapFont.class, debugFontParams);

        exitDialog.loadAssets();
        
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
        loader.parseLevel(world, controller, Gdx.files.internal("data/levels/test_lvl.smclvl").readString());
        hud.afterLoadAssets();
        world.setLevel(loader.getLevel());
		audioOn = Assets.manager.get("data/sounds/audio_on.ogg", Sound.class);
        exitDialog.afterLoadAssets();

        debugFont = Assets.manager.get("debug.ttf");
        debugFont.setColor(1, 0, 0, 1);
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
		if(keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE)
		{
            if(exitDialog.visible)exitDialog.hide();
            else exitDialog.show();
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
        if(exitDialog.visible)
        {
            exitDialog.touchDown(x, invertY(y));
            return true;
        }
		if (gameState == GAME_STATE.GAME_READY)gameState = GAME_STATE.GAME_RUNNING;
		if(gameState == GAME_STATE.GAME_OVER)goTouched = true;
        if (!Gdx.app.getType().equals(Application.ApplicationType.Android))
            return false;
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
        if(exitDialog.visible)
        {
            exitDialog.touchUp(x, invertY(y));
            return true;
        }
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
        if(exitDialog.visible)
        {
            exitDialog.touchDragged(x, invertY(y));
            return true;
        }
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
