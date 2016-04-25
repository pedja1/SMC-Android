package rs.pedjaapps.smc.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PerformanceCounter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.MusicManager;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.ga.GA;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Player;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.LevelGenerator;
import rs.pedjaapps.smc.utility.NAHudText;
import rs.pedjaapps.smc.utility.PrefsManager;
import rs.pedjaapps.smc.utility.Utility;
import rs.pedjaapps.smc.view.ConfirmDialog;
import rs.pedjaapps.smc.view.HUD;

public class GameScreen extends AbstractScreen implements InputProcessor
{
    public OrthographicCamera cam;
    private OrthographicCamera pCamera;
    public OrthographicCamera guiCam;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private ParticleEffect leafEffect;

    private SpriteBatch spriteBatch;
    private boolean debug = PrefsManager.isDebug();

    private BitmapFont debugFont;
    private GlyphLayout debugGlyph;

    public HUD hud;

    private float width, height;

    public void setGameState(GAME_STATE gameState)
    {
        this.gameState = gameState;
        hud.updateTimer = !(gameState == GAME_STATE.PLAYER_DEAD || gameState == GAME_STATE.PLAYER_UPDATING || gameState == GAME_STATE.NO_UPDATE);
    }

    public GAME_STATE getGameState()
    {
        return gameState;
    }

    public enum GAME_STATE
    {
        GAME_READY, GAME_RUNNING, GAME_PAUSED, GAME_OVER, PLAYER_DEAD,
        NO_UPDATE, PLAYER_UPDATING
    }

    private GAME_STATE gameState;

    private HashMap<Integer, TouchInfo> touches = new HashMap<>();
    private LevelGenerator generator;

    private Sound audioOn;
    private Music music;

    private float goAlpha = 0.0f;
    private boolean goTouched = false;

    public boolean isDebug()
    {
        return debug;
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    private ConfirmDialog exitDialog;

    public KillPointsTextHandler killPointsTextHandler;

    boolean resumed;
    private float stateTime;

    private double accumulator;
    private float step = 1.0f / 30.0f;
    private String objectDebugText;
    PerformanceCounter performanceCounter = new PerformanceCounter("pc");

    public GameScreen(MaryoGame game)
    {
        super(game);
        gameState = GAME_STATE.GAME_READY;
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        World.create(this);
        hud = new HUD();
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

        spriteBatch = new SpriteBatch();

        loadTextures();

        for (int i = 0; i < 5; i++) //handle max 4 touches
        {
            touches.put(i, new TouchInfo());
        }
        generator = new LevelGenerator();
        //Gdx.graphics.setContinuousRendering(false);
        GameSave.startLevelFresh();

        exitDialog = new ConfirmDialog(this, guiCam);
    }

    @Override
    public void show()
    {
        music = World.getInstance().level.music != null ? (Music) Assets.manager.get(World.getInstance().level.music) : null;
        MusicManager.play(music);
        if (debug) GLProfiler.enable();
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(this);
        if (!resumed)
        {
            GA.sendLevelStarted();
        }
    }

    private int physicsAccumulatorIterations;

    @Override
    public void render(float delta)
    {
        World world = World.getInstance();
        //debug
        //long now = System.currentTimeMillis();
        //debug

        //delta = 1f / 60f;

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //physics
        updateObjects(delta);
        //physics end

        if (gameState == GAME_STATE.GAME_RUNNING) generator.update(cam);

        moveCamera(cam, world.player.position);

        world.level.backgroundColor.render(cam, spriteBatch);
        world.level.parallaxGround.render(cam, spriteBatch);
        world.level.background3.render(cam, spriteBatch);
        world.level.background.render(cam, spriteBatch);
        world.level.background2.render(cam, spriteBatch);

        world.level.parallaxClouds.render(cam, spriteBatch);

        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        drawObjects();
        killPointsTextHandler.render(spriteBatch, delta);
        spriteBatch.end();

        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        world.player.render(spriteBatch);
        spriteBatch.end();

        spriteBatch.setProjectionMatrix(pCamera.combined);
        spriteBatch.begin();
        leafEffect.draw(spriteBatch);

        if (gameState == GAME_STATE.GAME_RUNNING) leafEffect.update(delta);
        spriteBatch.end();

        spriteBatch.setProjectionMatrix(guiCam.combined);
        spriteBatch.begin();
        if (debug) drawDebugText();
        spriteBatch.end();
        if (debug) drawDebug();

        hud.render(gameState, delta);

        if (gameState == GAME_STATE.GAME_OVER)
        {
            handleGameOver(delta);
        }

        exitDialog.render(spriteBatch);

        /*//cleanup
        for (int i = 0; i < world.level.gameObjects.size; i++)
        {
            GameObject go = world.level.gameObjects.get(i);
            removeObject(world.level.gameObjects, cam, go);
        }
        for (int i = 0; i < world.level.parallaxClouds.objects.size; i++)
        {
            GameObject go = world.level.parallaxClouds.objects.get(i);
            removeObject(world.level.parallaxClouds.objects, world.level.parallaxClouds.cam, go);
        }
        for (int i = 0; i < world.level.parallaxGround.objects.size; i++)
        {
            GameObject go = world.level.parallaxGround.objects.get(i);
            removeObject(world.level.parallaxGround.objects, world.level.parallaxGround.cam, go);
        }
*/
        if (debug) GLProfiler.reset();
        stateTime += delta;

        //debug
        //long end = System.currentTimeMillis() - now;
        //System.ou t.println("render time total: " + end);
        //debug
    }

    private void removeObject(Array<GameObject> objects, OrthographicCamera cam, GameObject go)
    {
        if (go.bounds.x + go.bounds.width < cam.position.x - cam.viewportWidth * .5f)
        {
            if (objects.removeValue(go, true))
            {
                go.dispose();
            }
        }
    }

    private void handleGameOver(float delta)
    {

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

            /*Texture go = Assets.manager.get("data/game/game_over.png");
            go.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            float width = this.width * 0.8f;
            float height = width / 4;

            float x = this.width / 2 - width / 2;
            float y = this.height / 2 - height / 2;
            spriteBatch.draw(go, x, y, width, height);*/
            //TODO drawgame over

            spriteBatch.end();
            if (!goTouched) return;
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

        if (goAlpha >= 1)
        {
            game.setScreen(new LoadingScreen(new MainMenuScreen(game), false));
        }
    }

    private void moveCamera(OrthographicCamera cam, Vector2 pos)
    {
        if ((gameState == GAME_STATE.PLAYER_UPDATING))
            return;
        pos = World.VECTOR2_POOL.obtain().set(pos);
        pos.x = pos.x + cam.viewportWidth * .25f;
        if (pos.x - cam.viewportWidth / 2 < 0)
        {
            pos.x = cam.viewportWidth / 2;
        }

        if (pos.y - cam.viewportHeight / 2 < 0)
        {
            pos.y = cam.viewportHeight / 2;
        }
        cam.position.set(pos, 0);
        cam.update();
        World.VECTOR2_POOL.free(pos);
    }

    private void updateObjects(float delta)
    {
        World world = World.getInstance();
        //performanceCounter.tick();
        //performanceCounter.start();
        //int count = 0;
        Rectangle maryoBWO = world.createMaryoRectWithOffset(cam, 8);
        for (int i = 0; i < world.level.gameObjects.size; i++)
        //for (GameObject go : world.level.gameObjects)
        {
            GameObject go = world.level.gameObjects.get(i);
            if (go.bounds != null && maryoBWO.overlaps(go.bounds))
            {
                if (gameState == GAME_STATE.GAME_RUNNING)
                {
                    go.update(delta);
                    /*if(go instanceof Enemy)*/
                    //count++;
                }
            }
        }
        World.RECT_POOL.free(maryoBWO);
        if (gameState == GAME_STATE.GAME_RUNNING || gameState == GAME_STATE.PLAYER_DEAD || gameState == GAME_STATE.PLAYER_UPDATING)
        {
            world.player.update(delta);
        }
        //System.out.println(count);
        //performanceCounter.stop();
        //System.out.println(performanceCounter.time.average);
    }

    private void drawObjects()
    {
        World world = World.getInstance();
        for (int i = 0; i < world.level.gameObjects.size; i++)
        {
            world.level.gameObjects.get(i).render(spriteBatch);
        }
    }

    private void drawDebug()
    {
        World world = World.getInstance();
        // render blocks
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (int i = 0; i < world.level.gameObjects.size; i++)
        {
            GameObject go = world.level.gameObjects.get(i);
            Rectangle colRect = go.getCollider();
            Rectangle drawRect = go.bounds;
            shapeRenderer.setColor(0, 1, 0, 1);
            if(colRect != null)shapeRenderer.rect(colRect.x, colRect.y, colRect.width, colRect.height);
            shapeRenderer.setColor(1, 0, 0, 1);
            shapeRenderer.rect(drawRect.x, drawRect.y, drawRect.width, drawRect.height);
        }

        // render maryo
        Player player = world.player;
        Rectangle body = player.getCollider();
        Rectangle bounds = player.bounds;
        Rectangle maryoBWO = world.createMaryoRectWithOffset(cam, 8);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.rect(body.x, body.y, body.width, body.height);
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.setColor(0, 0, 1, 1);
        shapeRenderer.rect(player.debugRayRect.x, player.debugRayRect.y, player.debugRayRect.width, player.debugRayRect.height);
        shapeRenderer.setColor(0.3f, 0.9f, 0, 0);
        shapeRenderer.rect(maryoBWO.x, maryoBWO.y, maryoBWO.width, maryoBWO.height);
        shapeRenderer.end();
    }

    private void drawDebugText()
    {
        String debugMessage = generateDebugMessage();
        debugGlyph.setText(debugFont, debugMessage);
        debugFont.draw(spriteBatch, debugMessage, 20, height - 20);
    }

    private String generateDebugMessage()
    {
        World world = World.getInstance();
        return
                "\n" + "Player: x=" + world.player.position.x + ", y=" + world.player.position.y
                        + "\n" + "Player Vel: x=" + world.player.velocity.x + ", y=" + world.player.velocity.y
                        + "\n" + "World Camera: x=" + cam.position.x + ", y=" + cam.position.y
                        + "\n" + "BG Camera: x=" + world.level.background.bgCam.position.x + ", y=" + world.level.background.bgCam.position.y
                        + "\n" + "JavaHeap: " + Gdx.app.getJavaHeap() / 1000000 + "MB"
                        + "\n" + "NativeHeap: " + Gdx.app.getNativeHeap() / 1000000 + "MB"
                        + "\n" + "OGL Draw Calls: " + GLProfiler.drawCalls
                        + "\n" + "OGL TextureBindings: " + GLProfiler.textureBindings
                        + "\n" + "Object Count: " + (world.level.gameObjects.size + world.level.parallaxClouds.objects.size + world.level.parallaxGround.objects.size)
                        + "\n" + "Render/Physics: 1/" + physicsAccumulatorIterations
                        + "\n" + "Screen w=" + width + "h=" + height
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

        World world = World.getInstance();

        world.level.backgroundColor.resize(cam);
        world.level.background.resize(cam);
        world.level.background2.resize(cam);
        world.level.background3.resize(cam);
        world.level.parallaxClouds.resize(cam);
        world.level.parallaxGround.resize(cam);
        exitDialog.resize();
        hud.resize(width, height);
    }

    @Override
    public void hide()
    {
        gameState = GAME_STATE.GAME_PAUSED;
        //Gdx.input.setInputProcessor(null);
        if(music != null)music.stop();
    }

    @Override
    public void pause()
    {
        gameState = GAME_STATE.GAME_PAUSED;
        if(music != null)music.stop();
    }

    @Override
    public void resume()
    {
        //Gdx.input.setInputProcessor(this);
    }

    @Override
    public void dispose()
    {
        if(music != null)music.stop();
        Gdx.input.setInputProcessor(null);
        Assets.dispose();
        exitDialog.dispose();
        World.getInstance().dispose();
        GA.sendLevelEnded(stateTime);
    }

    @Override
    public void loadAssets()
    {
        Assets.manager.load("data/assets.atlas", TextureAtlas.class);

        Assets.manager.load("data/animation/particles/star.p", ParticleEffect.class, Assets.particleEffectParameter);

        hud.loadAssets();

        //audio
        Assets.manager.load("data/sounds/audio_on.mp3", Sound.class);
        Assets.manager.load("data/sounds/item/coin.mp3", Sound.class);
        Assets.manager.load("data/sounds/item/coin_red.mp3", Sound.class);
        Assets.manager.load("data/sounds/player/jump.mp3", Sound.class);
        Assets.manager.load("data/sounds/player/pickup_item.mp3", Sound.class);
        Assets.manager.load("data/sounds/wall_hit.mp3", Sound.class);


        FreetypeFontLoader.FreeTypeFontLoaderParameter debugFontParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        debugFontParams.fontFileName = "data/fonts/MyriadPro-Regular.otf";
        debugFontParams.fontParameters.size = (int) (height / 25f);
        debugFontParams.fontParameters.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
        Assets.manager.load("debug.ttf", BitmapFont.class, debugFontParams);

        debugFontParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        debugFontParams.fontFileName = Constants.DEFAULT_FONT_FILE_NAME;
        debugFontParams.fontParameters.size = (int) (height / 40f);
        debugFontParams.fontParameters.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
        Assets.manager.load("debug_object.ttf", BitmapFont.class, debugFontParams);

        exitDialog.loadAssets();

        FreetypeFontLoader.FreeTypeFontLoaderParameter pointsParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        pointsParams.fontFileName = Constants.DEFAULT_FONT_FILE_NAME;
        pointsParams.fontParameters.size = (int) HUD.C_H / 35;
        pointsParams.fontParameters.characters = "0123456789";
        Assets.manager.load("kill-points.ttf", BitmapFont.class, pointsParams);

    }

    private void loadTextures()
    {
        leafEffect = new ParticleEffect();
        leafEffect.load(Gdx.files.internal("data/animation/particles/leaf_emitter.p"), Assets.resolver.resolve("data/animation/particles"));
        leafEffect.setPosition(Constants.CAMERA_WIDTH / 2, Constants.CAMERA_HEIGHT);
        leafEffect.start();
    }

    @Override
    public void onAssetsLoaded()
    {
        generator.preLoad(cam);
        hud.initAssets();
        audioOn = Assets.manager.get("data/sounds/audio_on.mp3", Sound.class);
        exitDialog.initAssets();

        debugFont = Assets.manager.get("debug.ttf");
        debugFont.setColor(1, 0, 0, 1);

        debugGlyph = new GlyphLayout();

        World world = World.getInstance();

        for (GameObject go : world.level.gameObjects)
            go.initAssets();

        BitmapFont pointsFont = Assets.manager.get("kill-points.ttf");
        pointsFont.setColor(1, 1, 1, 1);
        killPointsTextHandler = new KillPointsTextHandler(pointsFont);
        world.level.backgroundColor.onAssetsLoaded(cam);
        world.level.background.onAssetsLoaded(cam);
        world.level.background2.onAssetsLoaded(cam);
        world.level.background3.onAssetsLoaded(cam);
        world.level.parallaxClouds.onAssetsLoaded(cam);
        world.level.parallaxGround.onAssetsLoaded(cam);
    }

    // * InputProcessor methods ***************************//

    @Override
    public boolean keyDown(int keycode)
    {
        World world = World.getInstance();
        if (gameState == GAME_STATE.GAME_READY) gameState = GAME_STATE.GAME_RUNNING;
        if (keycode == Input.Keys.SPACE)
        {
            world.player.jumpPressed();
            hud.jumpPressed();
        }
        if (keycode == Input.Keys.DOWN)
        {
            world.player.downPressed();
            hud.downPressed();
        }
        if (keycode == Input.Keys.UP)
        {
            world.player.upPressed();
            hud.upPressed();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        World world = World.getInstance();
        if (keycode == Input.Keys.SPACE)
        {
            world.player.jumpReleased();
            hud.jumpReleased();
        }
        if (keycode == Input.Keys.DOWN)
        {
            world.player.downReleased();
            hud.downReleased();
        }
        if (keycode == Input.Keys.UP)
        {
            world.player.upReleased();
            hud.upReleased();
        }
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE)
        {
            if (gameState == GAME_STATE.GAME_PAUSED)
            {
                if (exitDialog.visible) exitDialog.hide();
                else exitDialog.show();
            }
            else
            {
                gameState = GAME_STATE.GAME_PAUSED;
            }
        }
        if (keycode == Input.Keys.D)
            debug = !debug;

        if (keycode == Input.Keys.ENTER)
        {
            setGameState(GAME_STATE.GAME_RUNNING);
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button)
    {
        if (exitDialog.visible)
        {
            exitDialog.touchDown(x, invertY(y));
            return true;
        }
        if (gameState == GAME_STATE.GAME_READY) gameState = GAME_STATE.GAME_RUNNING;
        if (gameState == GAME_STATE.GAME_OVER) goTouched = true;
        if (pointer < 5)
        {
            Vector2 vect = World.VECTOR2_POOL.obtain();
            /*if (MaryoGame.showOnScreenControls())
            {
                if (Intersector.isPointInPolygon(hud.upPolygon, vect.set(x, invertY(y))))//is top
                {
                    world.player.upPressed();
                    touches.get(pointer).clickArea = HUD.Key.up;
                    hud.upPressed();
                }
                if (Intersector.isPointInPolygon(hud.downPolygon, vect.set(x, invertY(y))))//is bottom
                {
                    world.player.downPressed();
                    touches.get(pointer).clickArea = HUD.Key.down;
                    hud.downPressed();
                }
                if (hud.jumpRT.contains(x, invertY(y)))
                {
                    world.player.jumpPressed();
                    touches.get(pointer).clickArea = HUD.Key.jump;
                    hud.jumpPressed();
                }
            }*/
            //TODO controls
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
            World.VECTOR2_POOL.free(vect);
        }
        return true;
    }

    private float invertY(float y)
    {
        return height - y;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button)
    {
        if (exitDialog.visible)
        {
            exitDialog.touchUp(x, invertY(y));
            return true;
        }
        TouchInfo ti = touches.get(pointer);
        if (ti != null)
        {
            switch (ti.clickArea)
            {
                /*case up:
                    if (MaryoGame.showOnScreenControls())
                    {
                        world.player.upReleased();
                        hud.upReleased();
                    }
                    break;
                case down:
                    if (MaryoGame.showOnScreenControls())
                    {
                        world.player.downReleased();
                        hud.downReleased();
                    }
                    break;
                case jump:
                    if (MaryoGame.showOnScreenControls())
                    {
                        world.player.jumpReleased();
                        hud.jumpReleased();
                    }
                    break;*/
                //TODO controls
                case pause:
                    if (gameState == GAME_STATE.GAME_RUNNING) gameState = GAME_STATE.GAME_PAUSED;
                    hud.pauseReleased();
                    break;
                case play:
                    gameState = GAME_STATE.GAME_RUNNING;
                    hud.playReleased();
                    break;
                case sound:
                    if (Utility.toggleSound())
                    {
                        SoundManager.play(audioOn);
                    }
                    hud.soundReleased();
                    break;
                case music:
                    if (Utility.toggleMusic())
                    {
                        MusicManager.play(music);
                    }
                    else
                    {
                        if(music != null)music.pause();
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
        if (exitDialog.visible)
        {
            exitDialog.touchDragged(x, invertY(y));
            return true;
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;
    }

    public float getWidth()
    {
        return width;
    }

    public void setWidth(float width)
    {
        this.width = width;
    }

    public float getHeight()
    {
        return height;
    }

    public void setHeight(float height)
    {
        this.height = height;
    }

    private class TouchInfo
    {
        HUD.Key clickArea = HUD.Key.none;
    }

    public static class KillPointsTextHandler
    {
        private final List<KillPoint> pointsTextPool = new ArrayList<>(10);
        private BitmapFont font;
        NAHudText<Integer> text = new NAHudText<>(null, null);

        KillPointsTextHandler(BitmapFont font)
        {
            this.font = font;
            font.getData().setScale(0.01f);
        }

        public void add(int points, float positionX, float positionY)
        {
            for (KillPoint point : pointsTextPool)
            {
                if (point.recycled)
                {
                    point.reset(positionX, positionY, points);
                    return;
                }
            }
            KillPoint point = new KillPoint(points, positionX, positionY);
            pointsTextPool.add(point);
        }

        public void render(SpriteBatch batch, float deltaTime)
        {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, size = pointsTextPool.size(); i < size; i++)
            {
                KillPoint point = pointsTextPool.get(i);
                if (!point.recycled)
                {
                    point.draw(batch, deltaTime, font, text);
                }
            }
        }

        private static class KillPoint
        {
            static final float velocity = 0.9f;
            static final float maxDistance = 0.4f;
            private boolean recycled = false;
            private int points;
            private float positionX, positionY, origPosY;
            private float alpha = 1;

            KillPoint(int points, float positionX, float positionY)
            {
                this.points = points;
                this.positionX = positionX;
                this.positionY = positionY;
                this.origPosY = positionY;
            }

            public void draw(SpriteBatch spriteBatch, float deltaTime, BitmapFont font, NAHudText<Integer> text)
            {
                if (positionY >= origPosY + maxDistance)
                {
                    recycled = true;
                    return;
                }
                float velDelta = velocity * deltaTime;
                positionY += maxDistance * velDelta;
                alpha -= 1 / (maxDistance / (maxDistance * velDelta));
                font.getColor().set(1, 1, 1, alpha);
                font.draw(spriteBatch, text.toString(points), positionX, positionY);
            }

            void reset(float posX, float posY, int points)
            {
                recycled = false;
                positionX = posX;
                positionY = posY;
                this.points = points;
                origPosY = posY;
                alpha = 1;
            }
        }
    }
}
