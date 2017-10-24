package rs.pedjaapps.smc.screen;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.assets.FontAwesome;
import rs.pedjaapps.smc.audio.MusicManager;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.LevelLoader;
import rs.pedjaapps.smc.view.AboutDialog;
import rs.pedjaapps.smc.view.Background;
import rs.pedjaapps.smc.view.MusicButton;
import rs.pedjaapps.smc.view.SelectionAdapter;

/**
 * Created by pedja on 2/17/14.
 */
public class MainMenuScreen extends AbstractScreen {
    public static final float DURATION_TRANSITION = .5f;
    public MaryoGame game;
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
    private SelectionAdapter selectionAdapter;
    private Viewport viewPort;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private TextureRegion marioFrame;
    private Group startMenu;
    private Skin skin;

    public MainMenuScreen(MaryoGame game) {
        super(game);
        this.game = game;
        batch = new SpriteBatch();
        drawCam = new OrthographicCamera();
        viewPort = new FitViewport(Constants.MENU_CAMERA_WIDTH, Constants.MENU_CAMERA_HEIGHT);
        viewPort.setCamera(drawCam);
        drawCam.position.set(Constants.MENU_CAMERA_WIDTH / 2 + (Constants.MENU_DRAW_WIDTH - Constants
                .MENU_CAMERA_WIDTH) / 2, Constants.MENU_CAMERA_HEIGHT / 2, 0);
        drawCam.update();

        hudCam = new OrthographicCamera(screenWidth, screenHeight);
        hudCam.position.set(screenWidth / 2, screenHeight / 2, 0);
        hudCam.update();

        loader = new LevelLoader("main_menu");
        debugFont = new BitmapFont();
        debugFont.setColor(Color.RED);
        debugFont.getData().setScale(1.3f);
        world = new World(this);
    }

    public static Image createLogoImage(MaryoGame game) {
        TextureRegion gameLogo = game.assets.manager.get(Assets.SKIN_HUD, Skin.class)
                .getAtlas().findRegion(Assets.LOGO_GAME);
        Image imGameLogo = new Image(gameLogo);
        imGameLogo.setSize(imGameLogo.getWidth() * .9f, imGameLogo.getHeight() * .9f);
        return imGameLogo;
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
        Gdx.input.setCatchBackKey(false);
        Gdx.input.setInputProcessor(stage);
        music = world.screen.game.assets.manager.get(loader.level.music.first());
        music.setLooping(true);
        MusicManager.play(music);
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 1 / 30f);
        if (delta <= 0)
            return;

        Gdx.gl20.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewPort.apply();
        backgroundColor.render(drawCam, batch);
        background.render(drawCam, batch);

        batch.setProjectionMatrix(drawCam.combined);
        batch.begin();

        cloudsPEffect.draw(batch, delta);

        drawObjects(delta);

        batch.draw(marioFrame, 1.4f, 4.609375f, 0.85f, 0.85f);

        batch.end();

        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();
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
        super.resize(width, height);
        viewPort.update(width, height);
    }

    @Override
    public void hide() {
        super.hide();
        music.stop();
    }

    @Override
    public void pause() {
        MusicManager.pause();
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
        MusicManager.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
        Gdx.input.setInputProcessor(null);
        game.assets.dispose();
        background.dispose();
        backgroundColor.dispose();
        batch.dispose();
        music.stop();
    }

    @Override
    public void loadAssets() {
        loader.parseLevel(world);
        game.assets.manager.load(Assets.ATLAS_STATIC, TextureAtlas.class);
        game.assets.manager.load(Assets.ATLAS_DYNAMIC, TextureAtlas.class);
        game.assets.manager.load("data/game/background/more_hills.png", Texture.class, game.assets.textureParameter);
        game.assets.manager.load(Assets.SOUND_AUDIO_ON, Sound.class);
        cloudsPEffect = new ParticleEffect();
        cloudsPEffect.loadEmitters(Gdx.files.internal("data/animation/particles/clouds_emitter.p"));
    }

    @Override
    public void onAssetsLoaded() {
        background = new Background(new Vector2(0, 0), new Vector2(), "data/game/background/more_hills.png",
                Constants.MENU_CAMERA_WIDTH, Constants.MENU_CAMERA_HEIGHT, Constants.MENU_CAMERA_WIDTH * 2, Constants
                .MENU_CAMERA_HEIGHT, Background.BG_IMG_BOTTOM);
        background.onAssetsLoaded(drawCam, game.assets);

        cloudsPEffect.loadEmitterImages(game.assets.manager.get(Assets.ATLAS_STATIC, TextureAtlas.class),
                "clouds_default_1_");
        cloudsPEffect.setPosition(Constants.MENU_CAMERA_WIDTH / 2, Constants.MENU_CAMERA_HEIGHT);
        cloudsPEffect.start();


        backgroundColor = new Background(Background.BG_GR_VER);
        backgroundColor.setColors(new Color(.117f, 0.705f, .05f, 0f), new Color(0f, 0.392f, 0.039f, 0f));//color is
        // 0-1 range where 1 = 255

        world.level = loader.level;

        TextureAtlas atlas = game.assets.manager.get(Assets.ATLAS_DYNAMIC);
        marioFrame = atlas.findRegion("maryo_small_" + GameObject.TKey.stand_right.toString());

        audioOn = game.assets.manager.get(Assets.SOUND_AUDIO_ON, Sound.class);

        for (GameObject go : loader.level.gameObjects)
            go.initAssets();

        skin = game.assets.manager.get(Assets.SKIN_HUD, Skin.class);
        selectionAdapter = new SelectionAdapter(this, skin) {
            @Override
            protected void goBack() {
                hideLevels();
            }
        };

        initStartMenu();

        selectionAdapter.setSize(stage.getWidth(), stage.getHeight());
        selectionAdapter.inflateWidgets(loadSelectionItems());
    }

    private void initStartMenu() {
        startMenu = new Group();
        startMenu.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(startMenu);
        TextButton play = new TextButton(FontAwesome.BIG_PLAY, skin, Assets.BUTTON_FA);
        play.setWidth(200);
        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showLevels();
            }
        });

        TextButton sound = new MusicButton(skin, audioOn) {
            @Override
            protected Music getMusic() {
                return music;
            }
        };

        startMenu.addActor(play);
        play.setPosition(startMenu.getWidth() / 2, startMenu.getHeight() / 2 - 50, Align.center);
        startMenu.addActor(sound);
        sound.setPosition(startMenu.getWidth() - 10, 10, Align.bottomRight);

        Image imGameLogo = createLogoImage(game);
        imGameLogo.setPosition(startMenu.getWidth() / 2, (startMenu.getHeight() + play.getY() + play.getHeight()) /
                2, Align.center);
        startMenu.addActor(imGameLogo);

        Label gameVersion = new Label("v" + MaryoGame.GAME_VERSION + " by Benjamin Schulte", skin, Assets
                .LABEL_BORDER25);
        if (MaryoGame.GAME_DEVMODE)
            gameVersion.setColor(Color.RED);
        gameVersion.setPosition(startMenu.getWidth() / 2, 10, Align.bottom);
        gameVersion.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                new AboutDialog(skin).show(stage);
                return true;
            }
        });
        startMenu.addActor(gameVersion);
    }

    private void showLevels() {
        if (startMenu.hasActions())
            return;

        startMenu.addAction(Actions.moveTo(0, stage.getHeight(), DURATION_TRANSITION, Interpolation.circle));
        selectionAdapter.setPosition(0, -selectionAdapter.getHeight());
        selectionAdapter.clearActions();
        stage.addActor(selectionAdapter);
        selectionAdapter.addAction(Actions.moveTo(0, 0, DURATION_TRANSITION, Interpolation.circle));
    }

    private void hideLevels() {
        if (startMenu.hasActions())
            return;

        selectionAdapter.clearActions();
        selectionAdapter.addAction(Actions.sequence(Actions.moveTo(0, -selectionAdapter.getHeight(),
                DURATION_TRANSITION, Interpolation.circle),
                Actions.removeActor()));
        startMenu.addAction(Actions.moveTo(0, 0, DURATION_TRANSITION, Interpolation.circle));
    }

}
