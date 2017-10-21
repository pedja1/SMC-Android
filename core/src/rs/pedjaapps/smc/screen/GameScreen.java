package rs.pedjaapps.smc.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.MusicManager;
import rs.pedjaapps.smc.object.Box;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.maryo.Fireball;
import rs.pedjaapps.smc.object.maryo.Iceball;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.LevelLoader;
import rs.pedjaapps.smc.utility.NAHudText;
import rs.pedjaapps.smc.utility.TextUtils;
import rs.pedjaapps.smc.utility.Utility;
import rs.pedjaapps.smc.view.Background;
import rs.pedjaapps.smc.view.HUD;

import static rs.pedjaapps.smc.utility.GameSave.save;

public class GameScreen extends AbstractScreen {
    private static final float LEVEL_END_ANIMATION_DURATION = .5f;
    private static final String GOD_MOD_TEXT = "god";
    public OrthographicCamera cam;
    public OrthographicCamera guiCam;
    public HUD hud;
    public String levelName;
    public KillPointsTextHandler killPointsTextHandler;
    public String entryName;
    public GameScreen parent;
    public boolean resumed, forceCheckEnter;
    protected Vector3 cameraEditModeTranslate = new Vector3();
    protected boolean goTouched = false;
    private boolean debug;
    private World world;
    private OrthographicCamera pCamera;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    /**
     * Textures *
     */
    private ParticleEffect globalEffect;
    private SpriteBatch spriteBatch;
    private BitmapFont debugFont, debugObjectFont;
    private GlyphLayout debugGlyph;
    private Vector2 camMin = new Vector2();
    private Vector2 camMax = new Vector2();
    private float width, height;
    private Rectangle maryoBWO = new Rectangle();
    private GAME_STATE gameState;
    private LevelLoader loader;
    private Music music;
    private float goAlpha = 0.0f;
    private boolean cameraForceSnap;
    private float levelEndAnimationStateTime;
    private String mNextLevelName;
    private InputProcessor keyboardAndTouch;
    private Array<GameObject> objectsToUpdate = new Array<>(75);
    private static final float FREQ_OTU_REFRESH = .2f;
    private float timeSinceUpdObjRefresh = FREQ_OTU_REFRESH;
    private int objRefreshSize;

    public GameScreen(MaryoGame game, boolean fromMenu, String levelName) {
        this(game, fromMenu, levelName, null);
    }

    public GameScreen(MaryoGame game, boolean fromMenu, String levelName, GameScreen parent) {
        super(game);
        this.parent = parent;
        this.levelName = levelName;
        gameState = GAME_STATE.GAME_READY;
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        world = new World(this);
        hud = new HUD(world, this);
        keyboardAndTouch = new GameScreenInput(this, world);
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

        loader = new LevelLoader(levelName);
        //Gdx.graphics.setContinuousRendering(false);
        if (fromMenu) GameSave.startLevelFresh();
    }

    public GAME_STATE getGameState() {
        return gameState;
    }

    public void setGameState(GAME_STATE gameState) {
        if (gameState == GAME_STATE.GAME_PAUSED && this.gameState == GAME_STATE.PLAYER_DIED)
            return;

        if (gameState == GAME_STATE.GAME_PAUSED && this.gameState != GAME_STATE.GAME_PAUSED)
            MusicManager.pause();
        else if (gameState != GAME_STATE.GAME_PAUSED && this.gameState == GAME_STATE.GAME_PAUSED)
            MusicManager.resume();

        this.gameState = gameState;
        hud.onGameStateChange();
        hud.updateTimer = !(gameState == GAME_STATE.PLAYER_DEAD || gameState == GAME_STATE.PLAYER_UPDATING ||
                gameState == GAME_STATE.SHOW_BOX || gameState == GAME_STATE.PLAYER_DIED);

        if (gameState == GAME_STATE.PLAYER_DIED && save.lifes < 0)
            MusicManager.stop(true);
    }

    public Music getMusic() {
        return music;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
        if (debug)
            GLProfiler.enable();
        else
            GLProfiler.disable();
    }

    @Override
    public void show() {
        GameSave.unlockLevel(levelName);
        music = game.assets.manager.get(loader.level.music.first());
        if (!resumed)
            music.setPosition(0);
        music.setLooping(true);
        MusicManager.play(music);
        if (!resumed || forceCheckEnter) {
            world.maryo.checkLevelEnter(entryName);
            forceCheckEnter = false;
        }
        Gdx.input.setCatchBackKey(true);

        InputMultiplexer multiInput = new InputMultiplexer();
        multiInput.addProcessor(keyboardAndTouch);
        multiInput.addProcessor(hud.stage);

        Gdx.input.setInputProcessor(multiInput);
        if (!resumed) {
            game.levelStart(levelName);
        }
        if (resumed) {
            cameraForceSnap = true;
        }
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 1 / 30f);
        if (delta <= 0)
            return;

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //physics
        updateObjects(delta);
        //physics end

        moveCamera(cam, gameState == GAME_STATE.GAME_EDIT_MODE ? cameraEditModeTranslate : world.maryo.position,
                gameState == GAME_STATE.GAME_EDIT_MODE || (gameState != GAME_STATE.GAME_RUNNING && gameState !=
                        GAME_STATE.PLAYER_UPDATING && gameState != GAME_STATE.PLAYER_DEAD));
        drawBackground();
        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        drawObjects();
        killPointsTextHandler.render(spriteBatch, delta);

        if (globalEffect != null) {
            globalEffect.setPosition(cam.position.x - Constants.CAMERA_WIDTH * 0.5f, cam.position.y + 0.5f *
                    Constants.CAMERA_HEIGHT);
            globalEffect.draw(spriteBatch);

            if (gameState == GAME_STATE.GAME_RUNNING) globalEffect.update(delta);
        }

        spriteBatch.end();

        hud.render(gameState, delta);

        if (debug) {
            spriteBatch.setProjectionMatrix(guiCam.combined);
            spriteBatch.begin();
            drawDebugText();
            spriteBatch.end();
            drawDebug();
        }

        if (gameState == GAME_STATE.PLAYER_DIED) {
            handlePlayerDied();
        }

        if (gameState == GAME_STATE.GAME_LEVEL_END) {
            handleLevelEnded(delta);
        }

        //cleanup
        if (world.trashObjects.size > 0) {
            for (int i = 0; i < world.trashObjects.size; i++)
                world.level.gameObjects.remove(world.trashObjects.get(i));
            world.trashObjects.clear();
            // neuaufbau der objectstoupdate auslösen
            objRefreshSize = 0;
        }

        //debug
        if (gameState == GAME_STATE.GAME_EDIT_MODE) {
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                cameraEditModeTranslate.x += 0.2f;
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                cameraEditModeTranslate.x -= 0.2f;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                cameraEditModeTranslate.y += 0.2f;
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                cameraEditModeTranslate.y -= 0.2f;
            }
        }
        if (debug) GLProfiler.reset();
    }

    public void endLevel(String nextLevelName) {
        mNextLevelName = nextLevelName;
        levelEndAnimationStateTime = LEVEL_END_ANIMATION_DURATION;
        setGameState(GAME_STATE.GAME_LEVEL_END);
    }

    private void handleLevelEnded(float delta) {
        if (levelEndAnimationStateTime <= 0) {
            world.screen.game.levelEnd(((GameScreen) world.screen).levelName, true);
            game.setScreen(new LoadingScreen(new GameScreen(game, false, mNextLevelName), false));
            mNextLevelName = null;
            return;
        }
        levelEndAnimationStateTime -= delta;

        float percent = 1 - levelEndAnimationStateTime / LEVEL_END_ANIMATION_DURATION;

        float camWidth = hud.stage.getWidth();
        float camHeight = hud.stage.getHeight();

        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        shapeRenderer.setProjectionMatrix(hud.stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0, 0, 0, percent);
        shapeRenderer.rect(0, 0, camWidth, camHeight);

        shapeRenderer.end();
    }

    public void showBoxText(Box box) {
        setGameState(GAME_STATE.SHOW_BOX);
        hud.showPopupBox(box.text);
    }

    public void discardBoxText() {
        if (gameState == GAME_STATE.SHOW_BOX) {
            if (hud.hidePopupBox())
                setGameState(GAME_STATE.GAME_RUNNING);
        }
    }

    private void handlePlayerDied() {
        if (save.lifes < 0 && !goTouched)
            return;

        // Fade out
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

        if (goAlpha >= 1) {
            if (save.lifes < 0) {
                game.setScreen(new LoadingScreen(new MainMenuScreen(game), false));
            } else {
                game.setScreen(new LoadingScreen(new GameScreen(game, false, levelName, parent), false));
            }
            game.levelEnd(levelName, false);
        }
    }

    public void won() {
        music = game.assets.manager.get(Assets.MUSIC_COURSECLEAR);
        //TODO einfache Verzögerung!?
        music.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                game.setScreen(new LoadingScreen(new MainMenuScreen(game), false));
            }
        });
        MusicManager.play(music);
    }

    private void drawBackground() {
        for (Background background : world.level.backgrounds) {
            background.render(cam, spriteBatch);
        }
    }

    private void moveCamera(OrthographicCamera cam, Vector3 pos, boolean snap) {
        //frieren bei größer/kleiner, aber sicherstellen das Maryo sichtbar ist (falls aus Kamerabereich geplumpst)
        if ((gameState == GAME_STATE.PLAYER_UPDATING && !world.maryo.entering && !world.maryo.exiting
                && world.isObjectVisible(world.maryo, cam))) {
            return;
        }
        if (snap || cameraForceSnap) {
            cam.position.set(pos);
            cameraForceSnap = false;
        } else {
            cam.position.lerp(pos, 0.05f);
        }
        cam.update();
        if (gameState != GAME_STATE.GAME_EDIT_MODE) keepCameraInBounds(cam);
    }

    private void keepCameraInBounds(OrthographicCamera cam) {
        float camX = cam.position.x;
        float camY = cam.position.y;

        camMin.set(cam.viewportWidth, cam.viewportHeight);
        camMin.scl(cam.zoom / 2); //bring to center and scale by the zoom level
        camMax.set(world.level.width, world.level.height);
        camMax.sub(camMin); //bring to center

        //keep camera within borders
        camX = Math.min(camMax.x, Math.max(camX, camMin.x));
        camY = Math.min(camMax.y, Math.max(camY, camMin.y));

        cam.position.set(camX, camY, cam.position.z);
        cam.update();
    }

    private void updateObjects(float delta) {
        if (gameState == GAME_STATE.GAME_RUNNING) {
            timeSinceUpdObjRefresh += delta;
            if (timeSinceUpdObjRefresh >= FREQ_OTU_REFRESH || world.level.gameObjects.size() != objRefreshSize) {
                objectsToUpdate.clear();
                timeSinceUpdObjRefresh = 0;
                objRefreshSize = world.level.gameObjects.size();
                world.createMaryoRectWithOffset(maryoBWO, 8);
                for (int i = 0, size = world.level.gameObjects.size(); i < size; i++) {
                    GameObject go = world.level.gameObjects.get(i);
                    if (maryoBWO.overlaps(go.mColRect) || (go instanceof Fireball) || (go instanceof Iceball)) {
                        objectsToUpdate.add(go);
                        go._update(delta);
                    }
                }
            } else {
                for (int i = 0; i < objectsToUpdate.size; i++) {
                    GameObject go = objectsToUpdate.get(i);
                    go._update(delta);
                }
            }

        } else if (gameState == GAME_STATE.PLAYER_DEAD || gameState == GAME_STATE.PLAYER_UPDATING)
            world.maryo._update(delta);
    }

    private void drawObjects() {
        world.drawVisibleObjects(cam, spriteBatch);
    }

    private void drawDebug() {
        // render blocks
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < world.getVisibleObjects().size; i++) {
            GameObject go = world.getVisibleObjects().get(i);
            Rectangle colRect = go.mColRect;
            Rectangle drawRect = go.mDrawRect;
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.rect(colRect.x, colRect.y, colRect.width, colRect.height);
            shapeRenderer.setColor(1, 0, 0, 1);
            shapeRenderer.rect(drawRect.x, drawRect.y, drawRect.width, drawRect.height);
        }
        // render maryo
        Maryo maryo = world.maryo;
        Rectangle body = maryo.mColRect;
        Rectangle bounds = maryo.mDrawRect;
        world.createMaryoRectWithOffset(maryoBWO, 8);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.rect(body.x, body.y, body.width, body.height);
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.setColor(0, 0, 1, 1);
        shapeRenderer.rect(maryo.debugRayRect.x, maryo.debugRayRect.y, maryo.debugRayRect.width, maryo.debugRayRect
                .height);
        shapeRenderer.setColor(0.3f, 0.9f, 0, 0);
        shapeRenderer.rect(maryoBWO.x, maryoBWO.y, maryoBWO.width, maryoBWO.height);
        shapeRenderer.end();
    }

    private void drawDebugText() {
        String debugMessage = generateDebugMessage();
        debugGlyph.setText(debugFont, debugMessage);
        debugFont.draw(spriteBatch, debugMessage, 20, height - 20);

        Vector2 point = World.VECTOR2_POOL.obtain();
        float x = Gdx.input.getX();
        float y = invertY(Gdx.input.getY());
        Utility.guiPositionToGamePosition(x, y, this, point);

        for (GameObject gameObject : world.level.gameObjects) {
            if (gameObject.mDrawRect.contains(point)) {
                String objectDebugText = gameObject.toString();
                float tWidth = width * 0.4f;
                debugGlyph.setText(debugObjectFont, objectDebugText, Color.BLACK, tWidth, Align.left, true);
                float height = debugGlyph.height;
                debugObjectFont.draw(spriteBatch, debugGlyph, x - tWidth, y + height);
                break;
            }
        }

        World.VECTOR2_POOL.free(point);
    }

    private String generateDebugMessage() {
        return "Level: width=" + world.level.width + ", height=" + world.level.height
                + "\n" + "Player: x=" + world.maryo.position.x + ", y=" + world.maryo.position.y
                + "\n" + "LevelName: " + levelName
                + "\n" + "Player Vel: x=" + world.maryo.velocity.x + ", y=" + world.maryo.velocity.y
                + "\n" + "World Camera: x=" + cam.position.x + ", y=" + cam.position.y
                + "\n" + "JavaHeap: " + Gdx.app.getJavaHeap() / 1000000 + "MB"
                + "\n" + "NativeHeap: " + Gdx.app.getNativeHeap() / 1000000 + "MB"
                + "\n" + "OGL Draw Calls: " + GLProfiler.drawCalls
                + "\n" + "OGL TextureBindings: " + GLProfiler.textureBindings
                + "\n" + "Screen w=" + width + "h=" + height
                + "\n" + "FPS: " + Gdx.graphics.getFramesPerSecond();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.width = width;
        this.height = height;
        Vector3 oldPos = cam.position;

        Constants.initCamera();

        cam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        cam.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        cam.update();
        cam.position.x = oldPos.x;
        cam.position.y = oldPos.y;

        pCamera = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        pCamera.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        pCamera.position.set(Constants.CAMERA_WIDTH / 2f, Constants.CAMERA_HEIGHT / 2f, 0);
        pCamera.update();

        guiCam = new OrthographicCamera(width, height);
        guiCam.position.set(width / 2f, height / 2f, 0);
        guiCam.update();

        for (Background background : world.level.backgrounds) {
            background.resize(cam);
        }
        hud.resize(width, height);
    }

    @Override
    public void hide() {
        setGameState(GAME_STATE.GAME_PAUSED);
        music.stop();
    }

    @Override
    public void pause() {
        setGameState(GAME_STATE.GAME_PAUSED);
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
        music.stop();
        hud.dispose();
        Gdx.input.setInputProcessor(null);
        game.assets.dispose();
        world.dispose();
        if (globalEffect != null) {
            globalEffect.dispose();
        }
        if (debug) GLProfiler.disable();
    }

    @Override
    public void loadAssets() {
        loader.parseLevel(world);
        game.assets.manager.load("data/animation/particles/fireball_emitter_2.p", ParticleEffect.class, game.assets
                .particleEffectParameter);
        game.assets.manager.load("data/animation/particles/fireball_explosion_emitter.p", ParticleEffect.class, game
                .assets.particleEffectParameter);
        game.assets.manager.load("data/animation/particles/iceball_emitter.p", ParticleEffect.class, game.assets
                .particleEffectParameter);
        game.assets.manager.load("data/animation/particles/iceball_explosion_emitter.p", ParticleEffect.class, game
                .assets.particleEffectParameter);
        game.assets.manager.load("data/animation/particles/star_trail.p", ParticleEffect.class, game.assets
                .particleEffectParameter);
        game.assets.manager.load("data/animation/particles/maryo_star.p", ParticleEffect.class, game.assets
                .particleEffectParameter);
        hud.loadAssets();

        //audio
        game.assets.manager.load(Assets.SOUND_AUDIO_ON, Sound.class);
        game.assets.manager.load(Assets.SOUND_ITEM_GOLDPIECE1, Sound.class);
        game.assets.manager.load(Assets.SOUND_ITEM_GOLDPIECE_RED, Sound.class);
        game.assets.manager.load(Assets.SOUND_PLAYER_DEAD, Sound.class);
        game.assets.manager.load(Assets.SOUND_JUMP_BIG, Sound.class);
        game.assets.manager.load(Assets.SOUND_JUMP_BIG_POWER, Sound.class);
        game.assets.manager.load(Assets.SOUND_JUMP_SMALL, Sound.class);
        game.assets.manager.load(Assets.SOUND_JUMP_SMALL_POWER, Sound.class);
        game.assets.manager.load(Assets.SOUND_PLAYER_POWERDOWN, Sound.class);
        game.assets.manager.load(Assets.SOUND_WALL_HIT, Sound.class);
        game.assets.manager.load(Assets.SOUND_ITEM_FIREBALL, Sound.class);
        game.assets.manager.load(Assets.SOUND_ITEM_LIVE_UP, Sound.class);

        game.assets.manager.load(Assets.SOUND_SPROUT, Sound.class);
        game.assets.manager.load(Assets.SOUND_ITEM_STAR_KILL, Sound.class);
        game.assets.manager.load(Assets.SOUND_LEAVE_PIPE, Sound.class);
        game.assets.manager.load(Assets.SOUND_ENTER_PIPE, Sound.class);

        game.assets.manager.load(Assets.SOUND_ITEM_FIREBALL_REPELLED, Sound.class);

        game.assets.manager.load(Assets.SOUND_ITEM_ICEBALL_HIT, Sound.class);
        game.assets.manager.load(Assets.SOUND_ITEM_FIREBALL_EXPLOSION, Sound.class);

        game.assets.manager.load(Assets.MUSIC_COURSECLEAR, Music.class);

    }

    @Override
    public void onAssetsLoaded() {
        hud.initAssets();
        world.level = loader.level;

        debugFont = new BitmapFont();
        debugFont.setColor(1, 0, 0, 1);

        debugObjectFont = new BitmapFont();
        debugGlyph = new GlyphLayout();

        for (GameObject go : loader.level.gameObjects) {
            go.initAssets();
        }

        BitmapFont pointsFont = game.assets.manager.get(Assets.SKIN_HUD, Skin.class).getFont(Assets.FONT_SIMPLE25);
        pointsFont.setColor(1, 1, 1, 1);
        killPointsTextHandler = new KillPointsTextHandler(pointsFont);
        for (Background background : world.level.backgrounds) {
            background.onAssetsLoaded(cam, game.assets);
        }

        if (!TextUtils.isEmpty(world.level.particleEffect)) {
            globalEffect = new ParticleEffect(game.assets.manager.get(world.level.particleEffect, ParticleEffect
                    .class));
            globalEffect.start();
        }
    }

    private float invertY(float y) {
        return height - y;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public enum GAME_STATE {
        GAME_READY, GAME_RUNNING, GAME_PAUSED, GAME_LEVEL_END, PLAYER_DIED, PLAYER_DEAD,
        SHOW_BOX, PLAYER_UPDATING, GAME_EDIT_MODE
    }

    public static class KillPointsTextHandler {
        private final List<KillPoint> pointsTextPool = new ArrayList<>(10);
        NAHudText<Integer> text = new NAHudText<>(null, null);
        private BitmapFont font;

        KillPointsTextHandler(BitmapFont font) {
            this.font = font;
        }

        public void add(int points, float positionX, float positionY) {
            for (KillPoint point : pointsTextPool) {
                if (point.recycled) {
                    point.reset(positionX, positionY, points);
                    return;
                }
            }
            KillPoint point = new KillPoint(points, positionX, positionY);
            pointsTextPool.add(point);
        }

        public void render(SpriteBatch batch, float deltaTime) {
            //noinspection ForLoopReplaceableByForEach
            float oldScale = font.getData().scaleX;
            boolean useInt = font.usesIntegerPositions();
            font.getData().setScale(0.015f);
            font.setUseIntegerPositions(false);

            for (int i = 0, size = pointsTextPool.size(); i < size; i++) {
                KillPoint point = pointsTextPool.get(i);
                if (!point.recycled) {
                    point.draw(batch, deltaTime, font, text);
                }
            }

            font.getData().setScale(oldScale);
            font.setUseIntegerPositions(useInt);

        }

        private static class KillPoint {
            static final float velocity = 0.9f;
            static final float maxDistance = 0.4f;
            private boolean recycled = false;
            private int points;
            private float positionX, positionY, origPosY;
            private float alpha = 1;

            KillPoint(int points, float positionX, float positionY) {
                this.points = points;
                this.positionX = positionX;
                this.positionY = positionY;
                this.origPosY = positionY;
            }

            public void draw(SpriteBatch spriteBatch, float deltaTime, BitmapFont font, NAHudText<Integer> text) {
                if (positionY >= origPosY + maxDistance) {
                    recycled = true;
                    return;
                }
                float velDelta = velocity * deltaTime;
                positionY += maxDistance * velDelta;
                alpha -= 1 / (maxDistance / (maxDistance * velDelta));
                font.getColor().set(1, 1, 1, alpha);
                font.draw(spriteBatch, text.toString(points), positionX, positionY);
            }

            void reset(float posX, float posY, int points) {
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
