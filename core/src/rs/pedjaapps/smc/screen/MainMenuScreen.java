package rs.pedjaapps.smc.screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import rs.pedjaapps.smc.view.ChoseLevelView;
import rs.pedjaapps.smc.view.ColorableTextButton;
import rs.pedjaapps.smc.view.GamepadMappingDialog;
import rs.pedjaapps.smc.view.GamepadSettingsDialog;
import rs.pedjaapps.smc.view.GpgsDialog;
import rs.pedjaapps.smc.view.MusicButton;

/**
 * Created by pedja on 2/17/14.
 */
public class MainMenuScreen extends AbstractScreen {
    public static final float DURATION_TRANSITION = .5f;
    private static boolean firstStartDone;
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
    private ChoseLevelView choseLevelView;
    private Viewport viewPort;
    private Image shadeBackground;
    private Image maryo;
    private Group startMenu;
    private Skin skin;
    private TextButton playButton;
    private TextButton exitButton;

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

        if (GameSave.getLifes() <= 0)
            GameSave.resetGameOver();
    }

    public static Image createLogoImage(MaryoGame game) {
        Image imGameLogo = new Image(game.assets.manager.get(Assets.SKIN_HUD, Skin.class), Assets.LOGO_GAME);
        imGameLogo.setSize(imGameLogo.getWidth() * .9f, imGameLogo.getHeight() * .9f);
        return imGameLogo;
    }

    @Override
    public void show() {
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setCatchMenuKey(true);
        Gdx.input.setInputProcessor(stage);
        game.controllerMappings.setInputProcessor(stage);
        music = world.screen.game.assets.manager.get(loader.level.music.first());
        music.setLooping(true);
        MusicManager.play(music);
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 1 / 30f);
        if (delta <= 0)
            return;

        Gdx.gl20.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewPort.apply();
        backgroundColor.render(drawCam, batch);
        background.render(drawCam, batch);

        batch.setProjectionMatrix(drawCam.combined);
        batch.begin();

        cloudsPEffect.draw(batch, delta);

        drawObjects(delta);

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
        game.controllerMappings.setInputProcessor(null);
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

        TextureAtlas dynAtlas = game.assets.manager.get(Assets.ATLAS_DYNAMIC);

        audioOn = game.assets.manager.get(Assets.SOUND_AUDIO_ON, Sound.class);

        for (GameObject go : loader.level.gameObjects)
            go.initAssets();

        skin = game.assets.manager.get(Assets.SKIN_HUD, Skin.class);
        choseLevelView = new ChoseLevelView(this, skin) {
            @Override
            protected void goBack() {
                hideLevels();
            }
        };

        initStartMenu();

        choseLevelView.setSize(stage.getWidth(), stage.getHeight());
        choseLevelView.inflateWidgets(dynAtlas, stage.getFocussableActors());

        maryo = new Image(dynAtlas.findRegion("maryo_" + GameSave.getPersistentMaryoState().toString()
                + "_" + GameObject.TKey.stand_right.toString()));
        maryo.setSize(maryo.getPrefWidth() * .55f, maryo.getPrefHeight() * .55f);
        maryo.setPosition(100, 378);
        stage.addActor(maryo);

        // für FireTV: falls direkt bei Start schon Controller da, dann gleich in Konfig springen
        if (Gdx.app.getType() == Application.ApplicationType.Android &&
                !Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen)
                && !Gdx.input.isPeripheralAvailable(Input.Peripheral.HardwareKeyboard)
                && !firstStartDone
                && Controllers.getControllers().size >= 1
                && !game.controllerMappings.loadedSavedSettings)
            new GamepadMappingDialog(skin, Controllers.getControllers().get(0), game.controllerMappings).show(stage);

        firstStartDone = true;
    }

    private void initStartMenu() {
        shadeBackground = new Image(skin, "stagebackground");
        shadeBackground.setFillParent(true);
        shadeBackground.getColor().a = 0;
        stage.addActor(shadeBackground);

        startMenu = new Group();
        startMenu.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(startMenu);
        playButton = new ColorableTextButton("Play", skin, Assets.BUTTON_BORDER) {
//            private float time;
//
//            @Override
//            public void act(float delta) {
//                super.act(delta);
//                time += delta;
//            }
//
//            @Override
//            public void draw(Batch batch, float parentAlpha) {
//                stage.getBatch().setShader(Shader.GLOW_SHADER);
//                Shader.GLOW_SHADER.setUniformf("u_time", time);
//                super.draw(batch, parentAlpha);
//                stage.getBatch().setShader(null);
//            }
        };
        stage.setEmphColor(skin.getColor(Assets.COLOR_EMPH2));
        playButton.getLabel().setFontScale(.8f);
        playButton.setSize(playButton.getPrefWidth() * 1.2f, playButton.getPrefHeight());
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showLevels();
            }
        });

        TextButton soundButton = new MusicButton(skin, audioOn) {
            @Override
            protected Music getMusic() {
                return music;
            }
        };

        startMenu.addActor(playButton);
        stage.addFocussableActor(playButton);
        playButton.setPosition(startMenu.getWidth() / 2, startMenu.getHeight() / 2 - 50, Align.center);
        startMenu.addActor(soundButton);
        stage.addFocussableActor(soundButton);

        Image imGameLogo = createLogoImage(game);
        imGameLogo.setPosition(startMenu.getWidth() / 2, (startMenu.getHeight() + playButton.getY() + playButton
                .getHeight()) /
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
        stage.addFocussableActor(gameVersion);

        exitButton = new ColorableTextButton(FontAwesome.CIRCLE_CROSS, skin, Assets.BUTTON_FA_FRAMELESS);
        exitButton.setPosition(10, startMenu.getHeight() - 10, Align.topLeft);
        exitButton.setVisible(Gdx.app.getType() != Application.ApplicationType.WebGL);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        startMenu.addActor(exitButton);
        stage.addFocussableActor(exitButton);

        TextButton gamePadSettings = new ColorableTextButton(FontAwesome.DEVICE_GAMEPAD, skin, Assets.BUTTON_FA);
        gamePadSettings.setPosition(startMenu.getWidth() - 10, 10, Align.bottomRight);
        gamePadSettings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new GamepadSettingsDialog(skin, game.controllerMappings, game.isRunningOn).show(stage);
            }
        });
        startMenu.addActor(gamePadSettings);
        stage.addFocussableActor(gamePadSettings);

        soundButton.setPosition(gamePadSettings.getX(), gamePadSettings.getY() + gamePadSettings.getHeight() + 20);

        if (game.gpgsClient != null) {
            TextButton gpgsLogin = new ColorableTextButton(FontAwesome.NET_CLOUDSAVE, skin, Assets.BUTTON_FA) {
                private boolean isConnected = true;

                @Override
                public void act(float delta) {
                    super.act(delta);
                    if (game.gpgsClient.isSessionActive() != isConnected) {
                        isConnected = !isConnected;
                        getLabel().setColor(isConnected ? Color.WHITE : Color.SALMON);
                    }
                }
            };
            gpgsLogin.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    new GpgsDialog(skin, game).show(stage);
                }
            });
            gpgsLogin.setPosition(10, 10, Align.bottomLeft);
            startMenu.addActor(gpgsLogin);
            stage.addFocussableActor(gpgsLogin);
        }

        focusOnMain();
    }

    private void focusOnMain() {
        stage.setFocussedActor(playButton);
        stage.setEscapeActor(exitButton);
    }

    private void showLevels() {
        if (startMenu.hasActions())
            return;

        shadeBackground.addAction(Actions.alpha(.8f, DURATION_TRANSITION));

        startMenu.addAction(Actions.moveTo(0, stage.getHeight(), DURATION_TRANSITION, Interpolation.circle));
        choseLevelView.setPosition(0, -choseLevelView.getHeight());
        choseLevelView.clearActions();
        stage.addActor(choseLevelView);
        choseLevelView.onShow(stage);
        choseLevelView.addAction(Actions.moveTo(0, 0, DURATION_TRANSITION, Interpolation.circle));
    }

    private void hideLevels() {
        if (startMenu.hasActions())
            return;

        shadeBackground.addAction(Actions.alpha(0, DURATION_TRANSITION));
        choseLevelView.clearActions();
        choseLevelView.addAction(Actions.sequence(Actions.moveTo(0, -choseLevelView.getHeight(),
                DURATION_TRANSITION, Interpolation.circle),
                Actions.removeActor()));
        startMenu.addAction(Actions.moveTo(0, 0, DURATION_TRANSITION, Interpolation.circle));
        focusOnMain();
    }

}
