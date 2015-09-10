package rs.pedjaapps.smc.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import rs.pedjaapps.smc.*;
import rs.pedjaapps.smc.object.*;
import rs.pedjaapps.smc.object.maryo.*;
import rs.pedjaapps.smc.utility.*;
import rs.pedjaapps.smc.view.*;

import rs.pedjaapps.smc.Audio;

/**
 * Created by pedja on 2/17/14.
 */
public class MainMenuScreen extends AbstractScreen implements InputProcessor
{
    private static final String MARIO_TEXTURE_REGION_KEY = GameObject.TKey.stand_right + ":" + Maryo.MaryoState.small;
    Texture gameLogo;
    TextureRegion play, playP, musicOn, musicOff, musicOnP, musicOffP, soundOn, soundOff, soundOnP, soundOffP;
    Rectangle playR, musicR, soundR;
    OrthographicCamera drawCam, debugCam, hudCam;
    SpriteBatch batch;
    public MaryoGame game;
	Background background;
    LevelLoader loader;
    private BitmapFont debugFont;
    private GlyphLayout debugGlyph;
    private boolean playT = false, musicT = false, soundT = false;
	public boolean debug = PrefsManager.isDebug();
    private static final String FPS_STRING = "FPS: ";
    private static final NATypeConverter<Integer> fpsCounter = new NATypeConverter<>();

    int screenWidth = Gdx.graphics.getWidth();
    int screenHeight = Gdx.graphics.getHeight();
    Music music;
    Sound audioOn;
    World world;
    private ParticleEffect cloudsPEffect;

	private SelectionAdapter selectionAdapter;

	public boolean isSelection;

    ConfirmDialog exitDialog;

    ShapeRenderer shapeRenderer = new ShapeRenderer();
    TextureRegion marioFrame;

    public MainMenuScreen(MaryoGame game)
    {
		super(game);
        this.game = game;
        batch = new SpriteBatch();
        drawCam = new OrthographicCamera(Constants.MENU_CAMERA_WIDTH, Constants.MENU_CAMERA_HEIGHT);
        drawCam.position.set(Constants.MENU_CAMERA_WIDTH / 2 + (Constants.MENU_DRAW_WIDTH - Constants.MENU_CAMERA_WIDTH) / 2, Constants.MENU_CAMERA_HEIGHT / 2, 0);
        drawCam.update();
        debugCam = new OrthographicCamera(1280, 720);
        debugCam.position.set(1280 / 2, 720 / 2, 0);
        debugCam.update();
        hudCam = new OrthographicCamera(screenWidth, screenHeight);
        hudCam.position.set(screenWidth / 2, screenHeight / 2, 0);
        hudCam.update();

        loader = new LevelLoader("main_menu");
        debugFont = new BitmapFont();
        debugFont.setColor(Color.RED);
        debugFont.getData().setScale(1.3f);
        debugGlyph = new GlyphLayout();
        world = new World(this);

		selectionAdapter = new SelectionAdapter(loadSelectionItems(), this);
        exitDialog = new ConfirmDialog(this, hudCam);
    }

	public Array<SelectionAdapter.Level> loadSelectionItems()
	{
		Array<SelectionAdapter.Level> items = new Array<SelectionAdapter.Level>();
		for (int i = 0; i < GameSaveUtility.LEVELS.size(); i++)
		{
			SelectionAdapter.Level level = new SelectionAdapter.Level();
            if(i < GameSaveUtility.LEVELS.size())level.levelId = GameSaveUtility.LEVELS.get(i);
            level.isUnlocked = i == 0 || GameSaveUtility.getInstance().isUnlocked(level.levelId);
			items.add(level);
		}
		return items;
	}

    @Override
    public void show()
    {
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(this);
        music = Assets.manager.get(loader.level.music.first());
        Audio.play(music);
    }

    @Override
    public void render(float delta)
    {
		Gdx.gl20.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		background.render(drawCam, batch);
		
        batch.setProjectionMatrix(drawCam.combined);
        batch.begin();

        cloudsPEffect.draw(batch, delta);

        drawObjects(delta);

        Utility.draw(batch, gameLogo, 2f, 5f, 2f);

        batch.draw(marioFrame, 2, 4.609375f, 0.85f, 0.85f);

        batch.end();

        if (isSelection)
		{
			selectionAdapter.render(delta);
		}
		else
		{
			batch.setProjectionMatrix(hudCam.combined);
			batch.begin();

			batch.draw(playT ? playP : play, playR.x, playR.y, playR.width, playR.height);
			batch.draw(soundT ? (Assets.playSounds ? soundOnP : soundOffP) : (Assets.playSounds ? soundOn : soundOff), soundR.x, soundR.y, soundR.width, soundR.height);
			batch.draw(musicT ? (Assets.playMusic ? musicOnP : musicOffP) : (Assets.playMusic ? musicOn : musicOff), musicR.x, musicR.y, musicR.width, musicR.height);

			batch.end();
		}

        if (debug)
        {
            batch.setProjectionMatrix(debugCam.combined);
            batch.begin();
            debugFont.draw(batch, FPS_STRING, 50f, 670f);
            debugGlyph.setText(debugFont, FPS_STRING);
            debugFont.draw(batch, fpsCounter.toString(Gdx.graphics.getFramesPerSecond()), debugGlyph.width + 60f, 670f);
            batch.end();
        }

        exitDialog.render(batch);
        if (debug)drawDebug();
    }

    private void drawObjects(float deltaTime)
    {
        //noinspection ForLoopReplaceableByForEach
        for(int i = 0, size = loader.level.gameObjects.size(); i < size; i++)
        //for (GameObject gameObject : loader.level.gameObjects)
        {
            GameObject gameObject = loader.level.gameObjects.get(i);
			gameObject._update(deltaTime);
            gameObject._render(batch);
        }
    }

    private void drawDebug()
    {
        // render blocks
        shapeRenderer.setProjectionMatrix(drawCam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for(int i = 0; i < loader.level.gameObjects.size(); i++)
        //for (GameObject go : world.getVisibleObjects())
        {
            GameObject go = loader.level.gameObjects.get(i);
            Rectangle body = go.mColRect;
            Rectangle bounds = go.mDrawRect;
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.rect(body.x, body.y, body.width, body.height);
            /*if(go instanceof rs.pedjaapps.smc.object.Sprite)
            {
                shapeRenderer.setColor(1, 0, 0, 1);
                shapeRenderer.polygon(((rs.pedjaapps.smc.object.Sprite)go).polygon.getTransformedVertices());
            }
            else
            {*/
                shapeRenderer.setColor(1, 0, 0, 1);
                shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            /*}*/
        }
        shapeRenderer.end();
    }


    @Override
    public void resize(int width, int height)
    {
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        Constants.initCamera();
        drawCam = new OrthographicCamera(Constants.MENU_CAMERA_WIDTH, Constants.MENU_CAMERA_HEIGHT);
        drawCam.position.set(Constants.MENU_CAMERA_WIDTH / 2 + (Constants.MENU_DRAW_WIDTH - Constants.MENU_CAMERA_WIDTH) / 2, Constants.MENU_CAMERA_HEIGHT / 2, 0);
        drawCam.update();
        hudCam = new OrthographicCamera(screenWidth, screenHeight);
        hudCam.position.set(screenWidth / 2, screenHeight / 2, 0);
        hudCam.update();
        exitDialog.resize();
    }

    @Override
    public void hide()
    {
        music.stop();
    }

    @Override
    public void pause()
    {
        music.stop();
    }

    @Override
    public void resume()
    {
        Audio.play(music);
    }

    @Override
    public void dispose()
    {
        Gdx.input.setInputProcessor(null);
        Assets.dispose();
        background.dispose();
        batch.dispose();
        exitDialog.dispose();
        music.stop();
    }

    @Override
    public void loadAssets()
    {
        loader.parseLevel(world);
        Assets.manager.load("data/hud/controls.pack", TextureAtlas.class);
        Assets.manager.load("data/maryo/small.pack", TextureAtlas.class, Assets.atlasTextureParameter);
		Assets.manager.load("data/hud/option.png", Texture.class, Assets.textureParameter);
		Assets.manager.load("data/game/logo/smc_big_1.png", Texture.class, Assets.textureParameter);
		Assets.manager.load("data/hud/option_selected.png", Texture.class, Assets.textureParameter);
		Assets.manager.load("data/sounds/audio_on.ogg", Sound.class);
        cloudsPEffect = new ParticleEffect();
        cloudsPEffect.load(Gdx.files.internal("data/animation/particles/clouds_emitter.p"), Gdx.files.internal("data/clouds/default_1/"));
        cloudsPEffect.setPosition(Constants.MENU_CAMERA_WIDTH / 2, Constants.MENU_CAMERA_HEIGHT);
        cloudsPEffect.start();

        Assets.manager.load("data/hud/lock.png", Texture.class, Assets.textureParameter);
        exitDialog.loadAssets();
		
    }

    @Override
    public void onAssetsLoaded()
    {
        TextureAtlas controlsAtlas = Assets.manager.get("data/hud/controls.pack");
        play = controlsAtlas.findRegion("play");
        playP = controlsAtlas.findRegion("play-pressed");
        playR = new Rectangle(screenWidth / 2f - (screenWidth / 10f) / 2,
							  screenHeight / 2f - (screenWidth / 10f) / 2, screenWidth / 10f, screenWidth / 10f);

        musicOn = controlsAtlas.findRegion("music-on");
        musicOnP = controlsAtlas.findRegion("music-on-pressed");
        musicOff = controlsAtlas.findRegion("music-off");
        musicOffP = controlsAtlas.findRegion("music-off-pressed");
        musicR = new Rectangle(screenWidth - (screenWidth / 18f) * 1.25f,
							   (screenWidth / 18f) / 4, screenWidth / 18f, screenWidth / 18f);

        soundOn = controlsAtlas.findRegion("sound-on");
        soundOnP = controlsAtlas.findRegion("sound-on-pressed");
        soundOff = controlsAtlas.findRegion("sound-off");
        soundOffP = controlsAtlas.findRegion("sound-off-pressed");
        soundR = new Rectangle(screenWidth - (screenWidth / 18f) * 2.5f,
							   (screenWidth / 18f) / 4, screenWidth / 18f, screenWidth / 18f);

        background = new Background(new Vector2(0, 0), new Vector2(), "data/game/background/more_hills.png");
        background.width = Constants.MENU_CAMERA_WIDTH;//8.7f;
        background.height = Constants.MENU_CAMERA_HEIGHT;//4.5f;
 
        background.color1 = new Color(.117f, 0.705f, .05f, 0f);//color is 0-1 range where 1 = 255
        background.color2 = new Color(0f, 0.392f, 0.039f, 0f);
		background.onAssetsLoaded(drawCam);

        gameLogo = Assets.manager.get("data/game/logo/smc_big_1.png");
        gameLogo.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        //gdxLogo = Assets.manager.get("/game/logo/libgdx.png");
        //gdxLogo.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        world.level = loader.level;

        TextureAtlas atlas = Assets.manager.get("data/maryo/small.pack");
        marioFrame = atlas.findRegion(GameObject.TKey.stand_right.toString());

        audioOn = Assets.manager.get("data/sounds/audio_on.ogg", Sound.class);

		selectionAdapter.initAssets();
        exitDialog.initAssets();

        for(GameObject go : loader.level.gameObjects)
            go.initAssets();
    }

    @Override
    public int getTimeStep()
    {
        return DINAMYC_TIMESTEP;
    }

    @Override
    public boolean keyDown(int keycode)
    {
        if (keycode == Input.Keys.ENTER)
        {
            //game.setScreen(new LoadingScreen(new GameScreen(game), false));
			isSelection = true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        if(keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE)
        {
            if(exitDialog.visible)exitDialog.hide();
            else exitDialog.show();
        }
        else if(keycode == Input.Keys.D)
        {
            debug = !debug;
            PrefsManager.setDebug(debug);
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        float x = screenX;//screenX / (screenWidth / Constants.CAMERA_WIDTH);
        float y = screenHeight - screenY;

        if(exitDialog.visible)
        {
            exitDialog.touchDown(x, y);
            return true;
        }

		if (isSelection)
		{
			selectionAdapter.touchDown(x, y);
		}
		else
		{
			if (playR.contains(x, y))
			{
				playT = true;
			}
			if (musicR.contains(x, y))
			{
				musicT = true;
			}
			if (soundR.contains(x, y))
			{
				soundT = true;
			}
		}
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        float x = screenX;// / (screenWidth / Constants.CAMERA_WIDTH);
        float y = screenHeight - screenY;

        if(exitDialog.visible)
        {
            exitDialog.touchUp(x, y);
            return true;
        }

        if (isSelection)
        {
            selectionAdapter.touchUp(x, y);
        }
        else
        {
            if (playR.contains(x, y))
            {
                playT = false;
                //music.stop();
                //game.setScreen(new LoadingScreen(new GameScreen(game), false));
                isSelection = true;
            }
            if (musicR.contains(x, y))
            {
                musicT = false;
                if (Utility.toggleMusic())
                {
                    Audio.play(music);
                }
                else
                {
                    music.pause();
                }
            }
            if (soundR.contains(x, y))
            {
                soundT = false;
                if (Utility.toggleSound())
                {
                    rs.pedjaapps.smc.Audio.play(audioOn);
                }
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        float x = screenX;// / (screenWidth / Constants.CAMERA_WIDTH);
        float y = screenHeight - screenY;

        if(exitDialog.visible)
        {
            exitDialog.touchDragged(x, y);
            return true;
        }

        if (isSelection)
        {
            selectionAdapter.touchDragged(x, y);
        }
        else
        {
            playT = playR.contains(x, y);
            musicT = musicR.contains(x, y);
            soundT = soundR.contains(x, y);
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        return false;
    }
}
