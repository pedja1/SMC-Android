package rs.pedjaapps.smc.view;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.golfgl.gdx.controllers.ControllerMenuDialog;
import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.assets.FontAwesome;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.screen.MainMenuScreen;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.HUDTimeText;
import rs.pedjaapps.smc.utility.Level;
import rs.pedjaapps.smc.utility.NAHudText;
import rs.pedjaapps.smc.utility.NATypeConverter;
import rs.pedjaapps.smc.utility.PrefsManager;

import static com.badlogic.gdx.Gdx.gl;

public class HUD {
    public static final float TOUCHPAD_DEAD_RADIUS = .33f;
    public static final String IMAGE_WAFFLES = "game_gold_m";
    private static final float UPDATE_FREQ = .15f;
    private static boolean keyboardF1HintShown;
    private final NATypeConverter<Integer> coins = new NATypeConverter<>();
    private final NAHudText<Integer> lives = new NAHudText<>(null, "x");
    private final HUDTimeText time = new HUDTimeText();
    public MenuStage stage;
    public boolean updateTimer = true;
    public boolean jumpPressed;
    public boolean firePressed;
    public boolean upPressed, downPressed, rightPressed, leftPressed;
    private float noUpdateDuration = UPDATE_FREQ;
    private World world;
    private GameScreen gameScreen;
    private TextButton pauseButton, playButton, musicButton;
    private Button fire, jump;
    private Touchpad touchpad;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private float stateTime;
    private int points;
    private String pointsText;
    private Label readyLbl;
    private Label scoreLabel;
    private Label coinsLabel;
    private Label timeLabel;
    private Label livesLabel;
    private Label hintLabel;
    private Image imItemBox;
    private int shownItemInBox;
    private Image imWaffles;
    private Image imMaryoL;
    private Skin skin;
    private Dialog popupBox;
    private Image imGameLogo;
    private boolean hasKeyboardOrController;
    private Image imHelp;
    private boolean showFps;
    private Image imItemInBox;
    private TextButton cancelButton;
    private TextureAtlas dynAtlas;
    private Label levelNamePaused;
    private Level gamescreenlevel;
    private TextButton gamePadSettings;

    public HUD(World world, GameScreen gameScreen) {
        this.world = world;
        this.gameScreen = gameScreen;
        stage = new MenuStage(new FitViewport(MaryoGame.NATIVE_WIDTH, MaryoGame.NATIVE_HEIGHT));
    }

    public static RepeatAction getForeverFade() {
        return Actions.forever(Actions.sequence(Actions.alpha(.3f, 1f), Actions.fadeIn(1f)));
    }

    public boolean isHasKeyboardOrController() {
        return hasKeyboardOrController;
    }

    public void setHasKeyboardOrController(boolean hasKeyboardOrController, boolean onInit) {
        if (this.hasKeyboardOrController == hasKeyboardOrController)
            return;

        this.hasKeyboardOrController = hasKeyboardOrController;

        if (onInit)
            return;

        if (hasKeyboardOrController && !keyboardF1HintShown) {
            if (PrefsManager.showKeyboardHint())
                showHint("Hold F1 key to see an overview of keys to control the game", 10f);
            keyboardF1HintShown = true;
        }

        onGameStateChange();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void loadAssets() {
        world.screen.game.assets.manager.load(Assets.SOUND_ITEM_LIVE_UP_2, Sound.class);
        world.screen.game.assets.manager.load("data/hud/help.png", Texture.class, world.screen.game.assets
                .textureParameter);
    }

    public void initAssets() {
        // already initialized
        if (skin != null)
            return;

        skin = world.screen.game.assets.manager.get(Assets.SKIN_HUD, Skin.class);
        stage.setEmphColor(skin.getColor(Assets.COLOR_EMPH2));
        float padX = stage.getWidth() * 0.03f;
        float ibSize = MaryoGame.NATIVE_WIDTH / 14;

        fire = new Button(skin, "fire");
        fire.setSize(MaryoGame.NATIVE_HEIGHT * .2f, MaryoGame.NATIVE_HEIGHT * .2f);
        fire.setPosition(MaryoGame.NATIVE_WIDTH - fire.getWidth() - padX, MaryoGame.NATIVE_HEIGHT * .5f - fire
                .getHeight());
        stage.addActor(fire);
        fire.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (firePressed != fire.getClickListener().isPressed()) {
                    firePressed = fire.getClickListener().isPressed();

                    if (firePressed)
                        world.maryo.firePressed();
                    else
                        world.maryo.fireReleased();
                }

                return false;
            }
        });

        jump = new Button(skin, "jump");
        jump.setSize(fire.getWidth(), fire.getHeight());
        jump.setPosition(fire.getX() - fire.getWidth() * 1.5f, (fire.getY() - fire.getHeight()) / 2);
        stage.addActor(jump);
        jump.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (jumpPressed != jump.getClickListener().isPressed()) {
                    jumpPressed = jump.getClickListener().isPressed();

                    if (jumpPressed)
                        world.maryo.jumpPressed();
                    else
                        world.maryo.jumpReleased();
                }

                return false;
            }
        });

        touchpad = new Touchpad(0, skin);
        touchpad.setSize(MaryoGame.NATIVE_HEIGHT * .5f, MaryoGame.NATIVE_HEIGHT * .5f);
        touchpad.setPosition(0, 0);
        stage.addActor(touchpad);
        touchpad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean upNowPressed = touchpad.getKnobPercentY() > TOUCHPAD_DEAD_RADIUS;
                boolean downNowPressed = touchpad.getKnobPercentY() < -TOUCHPAD_DEAD_RADIUS;
                boolean rightNowPressed = touchpad.getKnobPercentX() > TOUCHPAD_DEAD_RADIUS;
                boolean leftNowPressed = touchpad.getKnobPercentX() < -TOUCHPAD_DEAD_RADIUS;

                // zwei Richtungen gleichzeitig: entscheiden welcher wichtiger ist
                if ((upNowPressed || downNowPressed) && (leftNowPressed || rightNowPressed)) {
                    if (Math.abs(touchpad.getKnobPercentY()) >= Math.abs(touchpad.getKnobPercentX())) {
                        rightNowPressed = false;
                        leftNowPressed = false;
                    } else {
                        upNowPressed = false;
                        downNowPressed = false;
                    }

                }

                if (upPressed != upNowPressed) {
                    upPressed = upNowPressed;
                    if (upPressed)
                        world.maryo.upPressed();
                    else
                        world.maryo.upReleased();
                }

                if (downPressed != downNowPressed) {
                    downPressed = downNowPressed;
                    if (downPressed)
                        world.maryo.downPressed();
                    else
                        world.maryo.downReleased();
                }

                if (rightPressed != rightNowPressed) {
                    rightPressed = rightNowPressed;
                    if (rightPressed)
                        world.maryo.rightPressed();
                    else
                        world.maryo.rightReleased();
                }

                if (leftPressed != leftNowPressed) {
                    leftPressed = leftNowPressed;
                    if (leftPressed)
                        world.maryo.leftPressed();
                    else
                        world.maryo.leftReleased();
                }

            }
        });

        dynAtlas = world.screen.game.assets.manager.get(Assets.ATLAS_DYNAMIC, TextureAtlas.class);

        readyLbl = new Label("LET'S GET GOING!", skin, Assets.LABEL_BORDER60);
        readyLbl.setAlignment(Align.center);
        readyLbl.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        readyLbl.addAction(getForeverFade());
        stage.addActor(readyLbl);

        cancelButton = new ColorableTextButton(FontAwesome.MISC_CROSS, skin, Assets.BUTTON_FA);
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.exitToMenu();
            }
        });
        cancelButton.setPosition(10, stage.getHeight() - 10, Align.topLeft);
        stage.addActor(cancelButton);
        stage.addFocussableActor(cancelButton);

        playButton = new ColorableTextButton("RESUME", skin, Assets.BUTTON_BORDER);
        playButton.getLabel().setFontScale(.7f);
        playButton.setSize(playButton.getPrefWidth(), playButton.getPrefHeight());
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.setGameState(GameScreen.GAME_STATE.GAME_RUNNING);
            }
        });
        playButton.setPosition(stage.getWidth() / 2, stage.getHeight() / 4, Align.center);
        stage.addActor(playButton);
        stage.addFocussableActor(playButton);

        gamescreenlevel = Level.getLevel(gameScreen.getMenuLevelname());
        levelNamePaused = getScaledLabel("LEVEL " + gamescreenlevel.number + " PAUSED", .8f);
        levelNamePaused.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.bottom);
        levelNamePaused.addAction(HUD.getForeverFade());
        stage.addActor(levelNamePaused);

        imGameLogo = MainMenuScreen.createLogoImage(world.screen.game);
        imGameLogo.setSize(imGameLogo.getPrefWidth() * .6f, imGameLogo.getPrefHeight() * .6f);
        imGameLogo.setPosition(stage.getWidth() / 2, stage.getHeight() - 10, Align.top);
        stage.addActor(imGameLogo);

        gamePadSettings = new ColorableTextButton(FontAwesome.DEVICE_GAMEPAD, skin, Assets.BUTTON_FA);
        gamePadSettings.setPosition(stage.getWidth() - 10, 10, Align.bottomRight);
        gamePadSettings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new GamepadSettingsDialog(skin, gameScreen.game.controllerMappings).show(stage);
            }
        });
        stage.addActor(gamePadSettings);
        stage.addFocussableActor(gamePadSettings);

        musicButton = new MusicButton(skin, world.screen.game.assets.manager.get(Assets.SOUND_AUDIO_ON, Sound.class)) {
            @Override
            protected Music getMusic() {
                return gameScreen.getMusic();
            }
        };
        musicButton.setPosition(gamePadSettings.getX() - 10, 10, Align.bottomRight);
        stage.addActor(musicButton);
        stage.addFocussableActor(musicButton);

        imItemBox = new Image(skin, "game_itembox");
        imItemBox.setPosition(MaryoGame.NATIVE_WIDTH / 2 - ibSize, MaryoGame.NATIVE_HEIGHT - ibSize - ibSize / 5);
        imItemBox.setSize(ibSize, ibSize);
        stage.addActor(imItemBox);

        imItemInBox = new Image();
        imItemInBox.setSize(imItemBox.getWidth() * 0.5f, imItemBox.getHeight() * 0.5f);
        imItemInBox.setPosition(imItemBox.getX() + imItemBox.getWidth() * .25f,
                imItemBox.getY() + imItemBox.getHeight() * .25f);
        stage.addActor(imItemInBox);

        pauseButton = new TextButton(FontAwesome.CIRCLE_PAUSE, skin, Assets.BUTTON_FA_FRAMELESS);
        //pauseButton.getLabel().setFontScale(.5f);
        pauseButton.setPosition(MaryoGame.NATIVE_WIDTH - padX / 2 - pauseButton.getWidth(),
                imItemBox.getY() + imItemBox.getHeight() - pauseButton.getHeight());
        stage.addActor(pauseButton);
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.setGameState(GameScreen.GAME_STATE.GAME_PAUSED);
            }
        });

        imMaryoL = new Image(skin, "game_maryo_l");
        imMaryoL.setSize(ibSize / 1.25f, ibSize / 2.5f);
        imMaryoL.setPosition(pauseButton.getX() - imMaryoL.getHeight() * 3, imItemBox.getY() + imItemBox.getHeight()
                - imMaryoL.getHeight() - imMaryoL.getHeight() / 2);
        stage.addActor(imMaryoL);

        if (MaryoGame.GAME_DEVMODE)
            imMaryoL.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    gameScreen.setDebug(!gameScreen.isDebug());
                    return true;
                }
            });

        scoreLabel = new Label(formatPointsString(0), skin, Assets.LABEL_BORDER60);
        scoreLabel.setFontScale(.45f);
        scoreLabel.setSize(scoreLabel.getPrefWidth(), scoreLabel.getPrefHeight());
        scoreLabel.setPosition(padX, imMaryoL.getY() + (imMaryoL.getHeight() - scoreLabel.getHeight()) / 2);
        stage.addActor(scoreLabel);

        imWaffles = new Image(skin, IMAGE_WAFFLES);
        imWaffles.setSize(imWaffles.getWidth() / 2, imWaffles.getHeight() / 2);
        imWaffles.setPosition(padX * 2 + scoreLabel.getWidth(), scoreLabel.getY());
        stage.addActor(imWaffles);

        coinsLabel = new Label(" ", skin, Assets.LABEL_BORDER60);
        coinsLabel.setFontScale(.45f);
        coinsLabel.setSize(coinsLabel.getPrefWidth(), coinsLabel.getPrefHeight());
        coinsLabel.setPosition(imWaffles.getX() + imWaffles.getWidth(), scoreLabel.getY());
        stage.addActor(coinsLabel);

        livesLabel = new Label("0x", skin, Assets.LABEL_BORDER60);
        livesLabel.setFontScale(.45f);
        livesLabel.setSize(livesLabel.getPrefWidth(), livesLabel.getPrefHeight());
        livesLabel.setPosition(imMaryoL.getX(), scoreLabel.getY(), Align.bottomRight);
        stage.addActor(livesLabel);

        time.update(GameSave.getLevelPlaytime());
        timeLabel = new Label(new String(time.getChars()), skin, Assets.LABEL_BORDER60);
        timeLabel.setFontScale(.45f);
        timeLabel.setSize(timeLabel.getPrefWidth(), timeLabel.getPrefHeight());
        timeLabel.setPosition(livesLabel.getX() - padX, scoreLabel.getY(), Align.bottomRight);
        stage.addActor(timeLabel);
        if (MaryoGame.GAME_DEVMODE)
            timeLabel.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    showFps = !showFps;
                    return true;
                }
            });

        hintLabel = new Label(" ", skin, Assets.LABEL_BORDER60);
        hintLabel.setFontScale(.5f);
        hintLabel.setWrap(true);
        hintLabel.setWidth(stage.getWidth() - 2 * padX);
        hintLabel.setHeight(2 * hintLabel.getHeight());
        hintLabel.setAlignment(Align.top, Align.center);
        hintLabel.setPosition(padX, imItemBox.getY() - padX - hintLabel.getHeight());
        stage.addActor(hintLabel);

        Texture helpScreen = world.screen.game.assets.manager.get("data/hud/help.png", Texture.class);
        helpScreen.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        imHelp = new Image(helpScreen);
        imHelp.setSize(imHelp.getWidth() * .8f, imHelp.getHeight() * .8f);
        imHelp.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        stage.addActor(imHelp);

        // die on screen buttons müssen ganz vorne sein
        int actorsNum = stage.getActors().size;
        touchpad.setZIndex(actorsNum);
        fire.setZIndex(actorsNum);
        jump.setZIndex(actorsNum);

        onGameStateChange();
    }

    private boolean isInGame(GameScreen.GAME_STATE gameState) {
        return !(gameState == GameScreen.GAME_STATE.GAME_READY
                || gameState == GameScreen.GAME_STATE.GAME_PAUSED
                || gameState == GameScreen.GAME_STATE.GAME_LEVEL_END);
    }

    public void onGameStateChange() {
        GameScreen.GAME_STATE gameState = gameScreen.getGameState();
        boolean isDead = (gameState == GameScreen.GAME_STATE.PLAYER_DIED
                || gameState == GameScreen.GAME_STATE.PLAYER_DEAD);
        boolean isInGame = isInGame(gameState);
        boolean isPaused = (gameState == GameScreen.GAME_STATE.GAME_PAUSED);

        readyLbl.setVisible(gameState == GameScreen.GAME_STATE.GAME_READY);

        if (!isInGame) {
            imHelp.setVisible(false);
            imHelp.getColor().a = 0;
            hintLabel.setVisible(false);
            hintLabel.getColor().a = 0;
            hintLabel.clearActions();
        }

        scoreLabel.setVisible(isInGame);
        imItemBox.setVisible(isInGame);
        refreshItemInBox();
        coinsLabel.setVisible(isInGame);
        imWaffles.setVisible(isInGame);
        timeLabel.setVisible(isInGame || showFps);
        imMaryoL.setVisible(isInGame);
        livesLabel.setVisible(isInGame);
        playButton.setVisible(isPaused);
        levelNamePaused.setVisible(isPaused);
        musicButton.setVisible(isPaused);
        gamePadSettings.setVisible(isPaused);
        cancelButton.setVisible(isPaused);
        imGameLogo.setVisible(isPaused);
        pauseButton.setVisible(isInGame && !isDead);
        jump.setVisible(isInGame && !isDead && !hasKeyboardOrController);
        fire.setVisible(jump.isVisible() && world.maryo.hasFireAbility() && !isDead);
        touchpad.setVisible(jump.isVisible());

        if (!isInGame && popupBox != null && popupBox.hasParent())
            popupBox.hide();

        if (isPaused) {
            stage.setFocussedActor(playButton);
            stage.setEscapeActor(cancelButton);
        }
    }

    public void showKeyboardHelp() {
        imHelp.clearActions();
        imHelp.setVisible(isInGame(gameScreen.getGameState()));
        imHelp.addAction(Actions.fadeIn(.5f));
    }

    public void hideKeyboardHelp() {
        imHelp.clearActions();
        imHelp.addAction(Actions.sequence(Actions.fadeOut(.3f), Actions.visible(false)));
    }

    public void render(GameScreen.GAME_STATE gameState, float deltaTime) {
        if (gameState == GameScreen.GAME_STATE.GAME_PAUSED) {
            drawPauseOverlay();

            // die FPS auch Aktualisieren wenn in Pause
            if (showFps)
                timeLabel.setText(Integer.toString(Gdx.graphics.getFramesPerSecond()));

        } else {
            if (updateTimer) stateTime += deltaTime;

            //if(GameSave.getItem() != null)
            //	batch.setColor(Color.RED);

            noUpdateDuration = noUpdateDuration + deltaTime;

            if (noUpdateDuration >= UPDATE_FREQ) {
                noUpdateDuration = 0;
                // points
                pointsText = formatPointsString(GameSave.getScore());
                scoreLabel.setText(pointsText);

                //coins
                String coins = this.coins.toString(GameSave.getCoins());
                coinsLabel.setText(coins);

                if (stateTime >= 1f) {
                    GameSave.addLevelPlaytime(1);
                    stateTime = stateTime - 1f;
                    time.update(GameSave.getLevelPlaytime());
                }

                //time
                if (showFps)
                    timeLabel.setText(Integer.toString(Gdx.graphics.getFramesPerSecond()));
                else {
                    timeLabel.setText(new String(time.getChars()));
                }

                //lives
                livesLabel.setText(this.lives.toString(GameSave.getLifes()));
                refreshItemInBox();
            }
        }

        stage.getViewport().apply();
        stage.act(deltaTime);
        stage.draw();
    }

    private void refreshItemInBox() {
        int savedItem = GameSave.getItem();
        imItemInBox.setVisible(savedItem != 0 && imItemBox.isVisible());
        if (savedItem != shownItemInBox) {
            imItemInBox.setDrawable(new TextureRegionDrawable(
                    dynAtlas.findRegion(Item.getSavedItemTextureName(savedItem))));
            shownItemInBox = savedItem;
        }
    }

    public void showLevelEndScreen() {
        Actor newDefaultActor = null;
        Actor newEscapeActor = null;

        // für gestorben, game over und level erfolgreich beendet
        popupBox = new ControllerMenuDialog("", skin, Assets.WINDOW_SMALL);

        Table table = popupBox.getContentTable();

        ChangeListener proceed = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.proceedFromPausedOrEnded();
            }
        };
        TextButton abort = new ColorableTextButton(FontAwesome.MISC_CROSS, skin, Assets.BUTTON_FA);
        ChangeListener cancel = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.exitToMenu();
            }
        };
        abort.addListener(cancel);

        popupBox.getButtonTable().defaults().pad(20, 40, 0, 40);
        if (gameScreen.getGameState() == GameScreen.GAME_STATE.GAME_LEVEL_END) {
            // erfolgreich beendet
            table.add(getScaledLabel("LEVEL " + gamescreenlevel.number + " CLEAR", .8f));
            table.row();

            Table scoreTable = new Table();
            scoreTable.add(new Label("YOUR SCORE: ", skin, Assets.LABEL_SIMPLE25)).right();
            scoreTable.add(getScaledLabel(String.valueOf(gamescreenlevel.currentScore), .6f))
                    .right();
            scoreTable.row();
            scoreTable.add(new Label("BEST SCORE: ", skin, Assets.LABEL_SIMPLE25)).right();
            scoreTable.add(getScaledLabel(String.valueOf(gamescreenlevel.bestScore), .6f))
                    .right();

            //TODO Show Leaderboard

            table.add(scoreTable).pad(30);

            TextButton toMenu = new ColorableTextButton(FontAwesome.MENU_SANDWICH, skin, Assets.BUTTON_FA);
            toMenu.addListener(cancel);
            popupBox.button(toMenu);
            TextButton nextLevel = new ColorableTextButton(FontAwesome.BIG_FORWARD, skin, Assets.BUTTON_FA);
            nextLevel.addListener(proceed);
            popupBox.button(nextLevel);
            newEscapeActor = toMenu;
            newDefaultActor = nextLevel;
        } else if (gameScreen.getGameState() == GameScreen.GAME_STATE.PLAYER_DEAD
                || gameScreen.getGameState() == GameScreen.GAME_STATE.PLAYER_DIED) {
            // gestorben - Game over wenn keine Leben mehr über
            table.add(getScaledLabel(GameSave.getLifes() > 0 ? "Retry level?" : "GAME OVER", .8f));
            if (GameSave.getLifes() > 0) {
                TextButton retry = new ColorableTextButton(FontAwesome.ROTATE_RELOAD, skin, Assets.BUTTON_FA);
                retry.addListener(proceed);
                popupBox.button(retry);
                popupBox.button(abort);
                newEscapeActor = abort;
                newDefaultActor = retry;
            } else {
                table.row();
                table.add(new Label("Total score gained: " + GameSave.getTotalScore(),
                        skin, Assets.LABEL_SIMPLE25));

                //TODO Show Leaderboard

                if (Gdx.app.getType() == Application.ApplicationType.WebGL) {
                    table.row();
                    table.add(new Label("Reload the game for new lifes.", skin, Assets.LABEL_SIMPLE25));
                } else {
                    popupBox.button(abort);
                    newEscapeActor = abort;
                    newDefaultActor = abort;
                }
            }
        }

        popupBox.setKeepWithinStage(false);
        popupBox.validate();
        popupBox.setPosition(stage.getWidth() + popupBox.getWidth(), stage.getHeight() / 2, Align.left);
        popupBox.show(stage, Actions.delay(.5f, Actions.moveToAligned(stage.getWidth() / 2, stage.getHeight() / 2,
                Align.center, .5f, Interpolation.circle)));

        stage.setFocussedActor(newDefaultActor);
        stage.setEscapeActor(newEscapeActor);
    }

    public void showPopupBox(String text) {
        popupBox = new Dialog("", skin, Assets.WINDOW_SMALL) {
            @Override
            protected void result(Object object) {
                gameScreen.discardBoxText();
                cancel();
            }
        };

        Label textLabel = new Label(text, skin, Assets.LABEL_BORDER60);
        textLabel.setFontScale(.5f);
        textLabel.setWrap(true);
        popupBox.getContentTable().add(textLabel).prefWidth
                (MaryoGame.NATIVE_WIDTH / 2).pad(10);

        popupBox.show(stage);
    }

    public boolean hidePopupBox() {
        if (popupBox != null && popupBox.hasParent()) {
            // wenn Anzeige noch nicht voll gebracht, dann noch nicht verstecken
            if (popupBox.getActions().size > 0)
                return false;
            else
                popupBox.hide();
        }
        return true;
    }

    public void showHint(String hint, float duration) {
        hintLabel.setText(hint);
        hintLabel.clearActions();
        hintLabel.setVisible(isInGame(gameScreen.getGameState()));
        hintLabel.addAction(Actions.sequence(Actions.fadeIn(.2f), Actions.delay(duration), Actions.fadeOut(.2f),
                Actions.visible(false)));
    }

    private String formatPointsString(int points) {
        if (pointsText != null && this.points == points) {
            return pointsText;
        } else {
            this.points = points;
            String pointsPrefix = "SCORE ";
            String pointsString = points + "";
            int zeroCount = 8 - pointsString.length();
            for (int i = 0; i < zeroCount; i++) {
                pointsPrefix += "0";
            }
            return (pointsText = pointsPrefix + pointsString);
        }
    }

    private void drawPauseOverlay() {
        gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL20.GL_BLEND);

        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.5f);
        shapeRenderer.rect(0, 0, MaryoGame.NATIVE_WIDTH, MaryoGame.NATIVE_HEIGHT);
        shapeRenderer.end();

    }

    public void dispose() {
        stage.dispose();
    }

    private Label getScaledLabel(String text, float scale) {
        Label newLabelActor = new Label(text, skin, Assets.LABEL_BORDER60);
        newLabelActor.setFontScale(scale);
        newLabelActor.setHeight(newLabelActor.getPrefHeight());
        newLabelActor.setWidth(newLabelActor.getPrefWidth());
        return newLabelActor;
    }
}
