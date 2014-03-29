package rs.papltd.smc.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.*;
import rs.papltd.smc.*;
import rs.papltd.smc.model.*;
import rs.papltd.smc.utility.*;

import rs.papltd.smc.model.Sprite;

/**
 * Created by pedja on 2/17/14.
 */
public class MainMenuScreen extends AbstractScreen implements InputProcessor
{
    Texture gameLogo;
    Texture gdxLogo;
    TextureRegion play, playP, musicOn, musicOff, musicOnP, musicOffP, soundOn, soundOff, soundOnP, soundOffP;
    Rectangle playR, musicR, soundR, viewport;
    OrthographicCamera cam;
    OrthographicCamera debugCam;
    SpriteBatch batch;
    MaryoGame game;
	Background bgr1;
	BackgroundColor bgColor;
    LevelLoader loader;
    World world;
    private BitmapFont debugFont;
    private boolean playT = false, musicT = false, soundT = false, renderFps = false, playMusic, playSound;

    int screenWidth = Gdx.graphics.getWidth();
    int screenHeight = Gdx.graphics.getHeight();
    Music music;

    public MainMenuScreen(MaryoGame game)
    {
		super(game);
        this.game = game;
        batch = new SpriteBatch();
        cam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        cam.position.set(Constants.CAMERA_WIDTH/2, Constants.CAMERA_HEIGHT/2, 0);
        cam.update();
        debugCam = new OrthographicCamera(1280, 720);
        debugCam.position.set(1280/2, 720/2, 0);
        debugCam.update();
        loader = new LevelLoader();
        world = new World(new Vector2(0, Constants.GRAVITY), true);
        debugFont = new BitmapFont();
        debugFont.setColor(Color.RED);
        debugFont.setScale(1.3f);
    }

    @Override
    public void show()
    {
        Gdx.input.setInputProcessor(this);
        music = Assets.manager.get(loader.getLevel().getMusic().first());
    }

    @Override
    public void render(float delta)
    {
        // set viewport
        Gdx.gl.glViewport((int) viewport.x, (int) viewport.y,
                (int) viewport.width, (int) viewport.height);
		Gdx.gl20.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		bgColor.render(cam);

        batch.setProjectionMatrix(cam.combined);
        batch.begin();

		bgr1.render(batch);

		drawSprites();

        draw(batch, gameLogo, 2f, 5f, 6f);
        draw(batch, gdxLogo, 0.2f, 0.1f, 1.2f);

        draw(batch, playT ? playP : play, playR.x, playR.y, playR.width);
        draw(batch, musicT ? (Assets.playMusic ? musicOnP : musicOffP) : (Assets.playMusic ? musicOn : musicOff), musicR.x, musicR.y, musicR.width);
        draw(batch, soundT ? soundOnP : soundOn, soundR.x, soundR.y, soundR.width);

        batch.end();

        if (renderFps)
        {
            batch.setProjectionMatrix(debugCam.combined);
            batch.begin();
            debugFont.drawMultiLine(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 50f, 670f);
            batch.end();
        }
        if(Assets.playMusic)
        {
            music.play();
        }
        else
        {
            music.pause();
        }

    }

	private void drawSprites()
    {
        for (Sprite sprite : loader.getLevel().getSprites())
        {
            TextureRegion region = Assets.loadedRegions.get(sprite.getTextureName());
            region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            draw(batch, region, sprite.getPosition().x, sprite.getPosition().y, sprite.getBounds().width);
        }
    }

    @Override
    public void resize(int width, int height)
    {
        // calculate new viewport
        float aspectRatio = (float)width/(float)height;
        float scale = 1f;
        Vector2 crop = new Vector2(0f, 0f);
        if(aspectRatio > Constants.ASPECT_RATIO)
        {
            scale = (float)height/(float)Constants.CAMERA_HEIGHT;
            crop.x = (width - Constants.CAMERA_WIDTH*scale)/2f;
        }
        else if(aspectRatio < Constants.ASPECT_RATIO)
        {
            scale = (float)width/(float)Constants.CAMERA_WIDTH;
            crop.y = (height - Constants.CAMERA_HEIGHT*scale)/2f;
        }
        else
        {
            scale = (float)width/(float)Constants.CAMERA_WIDTH;
        }

        float w = (float)Constants.CAMERA_WIDTH*scale;
        float h = (float)Constants.CAMERA_HEIGHT*scale;
        viewport = new Rectangle(crop.x, crop.y, w, h);
    }

    @Override
    public void hide()
    {

    }

    @Override
    public void pause()
    {
    }

    @Override
    public void resume()
    {
    }

    @Override
    public void dispose()
    {
        Gdx.input.setInputProcessor(null);
        Assets.dispose();
        bgColor.dispose();
        batch.dispose();
        bgr1.dispose();
    }

    @Override
    public void loadAssets()
    {
        Array<String[]> data = loader.parseLevelData(Gdx.files.absolute(Assets.mountedObbPath + "/levels/main_menu.data").readString());

        for(String[] s : data)
        {
            if(LevelLoader.isTexture(s[0]))
            {
                Assets.manager.load(s[1], Texture.class, Assets.textureParameter);
            }
            else
            {
                Assets.manager.load(s[1], LevelLoader.getTextureClassForKey(s[0]));
            }
        }
        Assets.manager.load("/hud/controls.pack", TextureAtlas.class);
    }

    @Override
    public void afterLoadAssets()
    {
        loader.parseLevel(Gdx.files.absolute(Assets.mountedObbPath + "/levels/main_menu.smclvl").readString(), world);

        TextureAtlas controlsAtlas = Assets.manager.get("/hud/controls.pack");
        play = controlsAtlas.findRegion("play");
        playP = controlsAtlas.findRegion("play-pressed");
        playR = new Rectangle(4.5f, 3f, 1f, 1f);

        musicOn = controlsAtlas.findRegion("music-on");
        musicOnP = controlsAtlas.findRegion("music-on-pressed");
        musicOff = controlsAtlas.findRegion("music-off");
        musicOffP = controlsAtlas.findRegion("music-off-pressed");
        musicR = new Rectangle(9.25f, 0.15f, 0.5f, 0.5f);

        soundOn = controlsAtlas.findRegion("sound-on");
        soundOnP = controlsAtlas.findRegion("sound-on-pressed");
        soundOff = controlsAtlas.findRegion("sound-off");
        soundOffP = controlsAtlas.findRegion("sound-off-pressed");
        soundR = new Rectangle(8.675f, 0.15f, 0.5f, 0.5f);

        Texture bgTexture = Assets.manager.get("/game/background/more-hills.png");
        bgTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        bgr1 = new Background(new Vector2(0, 0), bgTexture);
        bgr1.width = 7f;
        bgr1.height = 4.5f;

        bgColor = new BackgroundColor();
        bgColor.color1 = new Color(.117f, 0.705f, .05f, 0f);//color is 0-1 range where 1 = 255
        bgColor.color2 = new Color(0f, 0.392f, 0.039f, 0f);

        gameLogo = Assets.manager.get("/game/logo/smc-big-1.png");
        gameLogo.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        gdxLogo = Assets.manager.get("/game/logo/libgdx.png");
        gdxLogo.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    }

    @Override
    public boolean keyDown(int keycode)
    {
        if(keycode == Input.Keys.ENTER)
        {
            game.setScreen(new GameScreen(game));
        }
        else if(keycode == Input.Keys.D)
        {
            renderFps = !renderFps;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        float x = screenX / (screenWidth / Constants.CAMERA_WIDTH);
        float y = Constants.CAMERA_HEIGHT - (screenY / (screenHeight / Constants.CAMERA_HEIGHT));

        if(playR.contains(x, y))
        {
            playT = true;
        }
        if(musicR.contains(x, y))
        {
            musicT = true;
        }
        if(soundR.contains(x, y))
        {
            soundT = true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        float x = screenX / (screenWidth / Constants.CAMERA_WIDTH);
        float y = Constants.CAMERA_HEIGHT - (screenY / (screenHeight / Constants.CAMERA_HEIGHT));

        if(playR.contains(x, y))
        {
            playT = false;
            music.stop();
            game.setScreen(new GameScreen(game));
        }
        if(musicR.contains(x, y))
        {
            musicT = false;
            Utility.toggleMusic();
        }
        if(soundR.contains(x, y))
        {
            soundT = false;
            Utility.toggleSound();
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        float x = screenX / (screenWidth / Constants.CAMERA_WIDTH);
        float y = Constants.CAMERA_HEIGHT - (screenY / (screenHeight / Constants.CAMERA_HEIGHT));

        playT = playR.contains(x, y);
        musicT = musicR.contains(x, y);
        soundT = soundR.contains(x, y);
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
