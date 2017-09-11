package rs.pedjaapps.smc.screen;

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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.assets.FontAwesome;
import rs.pedjaapps.smc.audio.MusicManager;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.LevelLoader;
import rs.pedjaapps.smc.utility.PrefsManager;
import rs.pedjaapps.smc.utility.Utility;
import rs.pedjaapps.smc.view.Background;
import rs.pedjaapps.smc.view.ConfirmDialog;
import rs.pedjaapps.smc.view.SelectionAdapter;

/**
 * Created by pedja on 2/17/14.
 */
public class MainMenuScreen extends AbstractScreen implements InputProcessor {
    public MaryoGame game;
    public boolean isSelection;
    private Texture gameLogo;
    private OrthographicCamera drawCam, hudCam;
    private SpriteBatch batch;
    private Background background;
    private Background backgroundColor;
    private LevelLoader loader;
    private BitmapFont debugFont;
    private int screenWidth = Gdx.graphics.getWidth();
    private int screenHeight = Gdx.graphics.getHeight();
    private Music music;
    private Sound audioOn;
    private World world;
    private ParticleEffect cloudsPEffect;
    private Stage stage;
    private SelectionAdapter selectionAdapter;
    private ConfirmDialog exitDialog;
    private Viewport viewPort;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private TextureRegion marioFrame;

    public MainMenuScreen(MaryoGame game) {
        super(game);
        this.game = game;
        batch = new SpriteBatch();
        drawCam = new OrthographicCamera();
        viewPort = new FitViewport(Constants.MENU_CAMERA_WIDTH, Constants.MENU_CAMERA_HEIGHT);
        viewPort.setCamera(drawCam);
        drawCam.position.set(Constants.MENU_CAMERA_WIDTH / 2 + (Constants.MENU_DRAW_WIDTH - Constants.MENU_CAMERA_WIDTH) / 2, Constants.MENU_CAMERA_HEIGHT / 2, 0);
        drawCam.update();

        hudCam = new OrthographicCamera(screenWidth, screenHeight);
        hudCam.position.set(screenWidth / 2, screenHeight / 2, 0);
        hudCam.update();

        loader = new LevelLoader("main_menu");
        debugFont = new BitmapFont();
        debugFont.setColor(Color.RED);
        debugFont.getData().setScale(1.3f);
        world = new World(this);

        exitDialog = new ConfirmDialog(this, hudCam);


        stage = new Stage(new FitViewport(MaryoGame.NATIVE_WIDTH, MaryoGame.NATIVE_HEIGHT));
    }

    private Array<SelectionAdapter.Level> loadSelectionItems() {
        Array<SelectionAdapter.Level> items = new Array<SelectionAdapter.Level>();
        for (int i = 0; i < GameSave.LEVELS.size(); i++) {
            SelectionAdapter.Level level = new SelectionAdapter.Level();
            if (i < GameSave.LEVELS.size()) level.levelId = GameSave.LEVELS.get(i);
            level.isUnlocked = i == 0 || GameSave.isUnlocked(level.levelId);
            items.add(level);
        }
        return items;
    }

    @Override
    public void show() {
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(stage);
        music = world.screen.game.assets.manager.get(loader.level.music.first());
        music.setLooping(true);
        MusicManager.play(music);
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 1/30f);
        Gdx.gl20.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewPort.apply();
        backgroundColor.render(drawCam, batch);
        background.render(drawCam, batch);

        batch.setProjectionMatrix(drawCam.combined);
        batch.begin();

        cloudsPEffect.draw(batch, delta);

        drawObjects(delta);

        Utility.draw(batch, gameLogo, 2f, 5f, 2f);

        batch.draw(marioFrame, 2, 4.609375f, 0.85f, 0.85f);

        batch.end();

        if (isSelection) {
            selectionAdapter.render(delta);
        } else {
            stage.getViewport().apply();
            stage.act(delta);
            stage.draw();
        }

        exitDialog.render(batch);

    }

    private void drawObjects(float deltaTime) {
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, size = loader.level.gameObjects.size(); i < size; i++)
        //for (GameObject gameObject : loader.level.gameObjects)
        {
            GameObject gameObject = loader.level.gameObjects.get(i);
            gameObject._update(deltaTime);
            gameObject._render(batch);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewPort.update(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        music.stop();
    }

    @Override
    public void pause() {
        music.stop();
    }

    @Override
    public void resume() {
        MusicManager.play(music);
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
        game.assets.dispose();
        background.dispose();
        backgroundColor.dispose();
        batch.dispose();
        exitDialog.dispose();
        music.stop();
        stage.dispose();
    }

    @Override
    public void loadAssets() {
        loader.parseLevel(world);
        game.assets.manager.load(Assets.ASSET_HUDSKIN, Skin.class);
        game.assets.manager.load("data/hud/hud.pack", TextureAtlas.class);
        game.assets.manager.load("data/hud/controls.pack", TextureAtlas.class);
        game.assets.manager.load("data/maryo/small.pack", TextureAtlas.class);
        game.assets.manager.load("data/game/logo/smc_big_1.png", Texture.class, game.assets.textureParameter);
        game.assets.manager.load("data/game/background/more_hills.png", Texture.class, game.assets.textureParameter);
        game.assets.manager.load("data/sounds/audio_on.mp3", Sound.class);
        cloudsPEffect = new ParticleEffect();
        cloudsPEffect.load(Gdx.files.internal("data/animation/particles/clouds_emitter.p"), Gdx.files.internal("data/clouds/default_1/"));
        cloudsPEffect.setPosition(Constants.MENU_CAMERA_WIDTH / 2, Constants.MENU_CAMERA_HEIGHT);
        cloudsPEffect.start();

        game.assets.manager.load("data/hud/lock.png", Texture.class, game.assets.textureParameter);
        exitDialog.loadAssets();

    }

    @Override
    public void onAssetsLoaded() {
        TextureAtlas hud = game.assets.manager.get("data/hud/hud.pack");

        background = new Background(new Vector2(0, 0), new Vector2(), "data/game/background/more_hills.png",
                Constants.MENU_CAMERA_WIDTH, Constants.MENU_CAMERA_HEIGHT, Constants.MENU_CAMERA_WIDTH * 2, Constants
                .MENU_CAMERA_HEIGHT, Background.BG_IMG_BOTTOM);
        background.onAssetsLoaded(drawCam, game.assets);

        backgroundColor = new Background(Background.BG_GR_VER);
        backgroundColor.setColors(new Color(.117f, 0.705f, .05f, 0f), new Color(0f, 0.392f, 0.039f, 0f));//color is
        // 0-1 range where 1 = 255

        gameLogo = game.assets.manager.get("data/game/logo/smc_big_1.png");
        gameLogo.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        //gdxLogo = Assets.manager.get("/game/logo/libgdx.png");
        //gdxLogo.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        world.level = loader.level;

        TextureAtlas atlas = game.assets.manager.get("data/maryo/small.pack");
        marioFrame = atlas.findRegion(GameObject.TKey.stand_right.toString());

        audioOn = game.assets.manager.get("data/sounds/audio_on.mp3", Sound.class);

        exitDialog.initAssets();

        for (GameObject go : loader.level.gameObjects)
            go.initAssets();

        Skin skin = game.assets.manager.get(Assets.ASSET_HUDSKIN, Skin.class);
        selectionAdapter = new SelectionAdapter(loadSelectionItems(), this, skin);
        selectionAdapter.initAssets();

        TextButton play = new TextButton("Play", skin);
        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isSelection = true;
            }
        });
        TextButton sound = new TextButton(getSoundStateIcon(), skin, "fa45");
        sound.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!PrefsManager.isPlaySounds()) {
                    PrefsManager.setPlayMusic(true);
                    PrefsManager.setPlaySounds(true);
                    MusicManager.play(music);
                } else if (PrefsManager.isPlayMusic()) {
                    PrefsManager.setPlayMusic(false);
                    PrefsManager.setPlaySounds(true);
                    music.pause();
                    SoundManager.play(audioOn);
                } else {
                    PrefsManager.setPlayMusic(false);
                    PrefsManager.setPlaySounds(false);
                }
                ((TextButton) actor).setText(getSoundStateIcon());
            }
        });

        // Key Events
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.ENTER:
                        isSelection = true;
                        return true;
                    case Input.Keys.BACK:
                    case Input.Keys.ESCAPE:
                        if (exitDialog.visible) exitDialog.hide();
                        else exitDialog.show();
                        return true;
                    default:
                        return false;
                }
            }

        });

        stage.addActor(play);
        play.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        stage.addActor(sound);
        sound.setPosition(stage.getWidth() - 10, 10, Align.bottomRight);

    }

    private String getSoundStateIcon() {
        if (PrefsManager.isPlayMusic())
            return FontAwesome.SETTINGS_MUSIC;
        else if (PrefsManager.isPlaySounds())
            return FontAwesome.SETTINGS_SPEAKER_ON;
        else
            return FontAwesome.SETTINGS_SPEAKER_OFF;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        float x = screenX;//screenX / (screenWidth / Constants.CAMERA_WIDTH);
        float y = screenHeight - screenY;

        if (exitDialog.visible) {
            exitDialog.touchDown(x, y);
            return true;
        }

        if (isSelection) {
            selectionAdapter.touchDown(x, y);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        float x = screenX;// / (screenWidth / Constants.CAMERA_WIDTH);
        float y = screenHeight - screenY;

        if (exitDialog.visible) {
            exitDialog.touchUp(x, y);
            return true;
        }

        if (isSelection) {
            selectionAdapter.touchUp(x, y);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float x = screenX;// / (screenWidth / Constants.CAMERA_WIDTH);
        float y = screenHeight - screenY;

        if (exitDialog.visible) {
            exitDialog.touchDragged(x, y);
            return true;
        }

        if (isSelection) {
            selectionAdapter.touchDragged(x, y);
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
