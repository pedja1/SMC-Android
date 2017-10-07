package rs.pedjaapps.smc.view;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.assets.FontAwesome;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.screen.MainMenuScreen;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.HUDTimeText;
import rs.pedjaapps.smc.utility.NAHudText;
import rs.pedjaapps.smc.utility.NATypeConverter;

import static com.badlogic.gdx.Gdx.gl;

public class HUD {
    public static final float TOUCHPAD_DEAD_RADIUS = .33f;
    private static final float UPDATE_FREQ = .15f;
    private static boolean keyboardF1HintShown;
    private final NATypeConverter<Integer> coins = new NATypeConverter<>();
    private final NAHudText<Integer> lives = new NAHudText<>(null, "x");
    private final HUDTimeText time = new HUDTimeText();
    public Stage stage;
    public boolean updateTimer = true;
    public boolean jumpPressed;
    public boolean firePressed;
    public boolean upPressed, downPressed, rightPressed, leftPressed;
    private float noUpdateDuration;
    private World world;
    private GameScreen gameScreen;
    private TextButton pauseButton, play, musicButton;
    private Button fire, jump;
    private Touchpad touchpad;
    private Texture itemBox, maryoL, goldM;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private Table buttonsTable;
    private float stateTime;
    private int points;
    private String pointsText;
    private Label readyLbl;
    private Label pauseLabel;
    private Label scoreLabel;
    private Label coinsLabel;
    private Label timeLabel;
    private Label livesLabel;
    private Label hintLabel;
    private Image imItemBox;
    private Image imWaffles;
    private Image imMaryoL;
    private Skin skin;
    private Dialog popupBox;
    private Image imGameLogo;
    private Label gameOverLabel;
    private boolean hasKeyboardOrController;
    private Image imHelp;

    public HUD(World world, GameScreen gameScreen) {
        this.world = world;
        this.gameScreen = gameScreen;
        batch = new SpriteBatch();
        stage = new Stage(new FitViewport(MaryoGame.NATIVE_WIDTH, MaryoGame.NATIVE_HEIGHT));
    }

    public static RepeatAction getForeverFade() {
        return Actions.forever(Actions.sequence(Actions.alpha(.3f, 1f), Actions.fadeIn(1f)));
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void loadAssets() {
        world.screen.game.assets.manager.load("data/sounds/item/live_up_2.mp3", Sound.class);

        world.screen.game.assets.manager.load("data/game/itembox.png", Texture.class, world.screen.game.assets
                .textureParameter);
        world.screen.game.assets.manager.load("data/game/maryo_l.png", Texture.class, world.screen.game.assets
                .textureParameter);
        world.screen.game.assets.manager.load("data/game/gold_m.png", Texture.class, world.screen.game.assets
                .textureParameter);
        world.screen.game.assets.manager.load("data/hud/help.png", Texture.class, world.screen.game.assets
                .textureParameter);
    }

    public void initAssets() {
        // already initialized
        if (skin != null)
            return;

        skin = world.screen.game.assets.manager.get(Assets.SKIN_HUD, Skin.class);
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

        itemBox = world.screen.game.assets.manager.get("data/game/itembox.png");
        maryoL = world.screen.game.assets.manager.get("data/game/maryo_l.png");
        goldM = world.screen.game.assets.manager.get("data/game/gold_m.png");

        Texture.TextureFilter filter = Texture.TextureFilter.Linear;
        itemBox.setFilter(filter, filter);
        maryoL.setFilter(filter, filter);
        goldM.setFilter(filter, filter);

        readyLbl = new Label("LET'S GET GOING!", skin, Assets.LABEL_BORDER60);
        readyLbl.setAlignment(Align.center);
        readyLbl.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        readyLbl.addAction(getForeverFade());
        stage.addActor(readyLbl);

        imGameLogo = MainMenuScreen.createLogoImage(world.screen.game);
        imGameLogo.setSize(imGameLogo.getWidth() * .6f, imGameLogo.getHeight() * .6f);
        imGameLogo.setPosition(stage.getWidth() / 2, padX / 2, Align.bottom);
        imGameLogo.getColor().a = .8f;
        stage.addActor(imGameLogo);

        gameOverLabel = new Label("GAME OVER", skin, Assets.LABEL_BORDER60);
        gameOverLabel.setPosition(stage.getWidth() / 2,
                (stage.getHeight() + imGameLogo.getY() + imGameLogo.getHeight()) / 2, Align.center);
        gameOverLabel.addAction(Actions.forever(
                Actions.sequence(Actions.color(Color.SALMON, 1f), Actions.color(Color.WHITE, 1f))));
        stage.addActor(gameOverLabel);

        pauseLabel = new Label("PAUSE", skin, Assets.LABEL_BORDER60);
        pauseLabel.setPosition(stage.getWidth() / 2, stage.getHeight() * .75f, Align.center);
        pauseLabel.addAction(getForeverFade());
        stage.addActor(pauseLabel);

        TextButton cancelButton = new TextButton(FontAwesome.MISC_CROSS, skin, Assets.BUTTON_FA);
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.exitToMenu();
            }
        });

        play = new TextButton("Resume", skin, Assets.BUTTON_SMALL);
        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.setGameState(GameScreen.GAME_STATE.GAME_RUNNING);
            }
        });

        musicButton = new MusicButton(skin, world.screen.game.assets.manager.get("data/sounds/audio_on.mp3", Sound
                .class)) {
            @Override
            protected Music getMusic() {
                return gameScreen.getMusic();
            }
        };

        buttonsTable = new Table();
        buttonsTable.defaults().uniform().pad(padX / 2).fill();
        buttonsTable.add(cancelButton);
        buttonsTable.add(play).uniform(false, true);
        buttonsTable.add(musicButton);
        buttonsTable.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.top);
        stage.addActor(buttonsTable);

        imItemBox = new Image(itemBox);
        imItemBox.setPosition(MaryoGame.NATIVE_WIDTH / 2 - ibSize, MaryoGame.NATIVE_HEIGHT - ibSize - ibSize / 5);
        imItemBox.setSize(ibSize, ibSize);
        stage.addActor(imItemBox);

        pauseButton = new TextButton(FontAwesome.BIG_PAUSE, skin, Assets.BUTTON_FA);
        pauseButton.getLabel().setFontScale(.5f);
        pauseButton.setSize(MaryoGame.NATIVE_HEIGHT / 10f, MaryoGame.NATIVE_HEIGHT / 10f);
        pauseButton.setPosition(MaryoGame.NATIVE_WIDTH - padX / 2 - pauseButton.getWidth(),
                imItemBox.getY() + imItemBox.getHeight() - pauseButton.getHeight());
        stage.addActor(pauseButton);
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.setGameState(GameScreen.GAME_STATE.GAME_PAUSED);
            }
        });

        imMaryoL = new Image(maryoL);
        imMaryoL.setSize(ibSize / 1.25f, ibSize / 2.5f);
        imMaryoL.setPosition(pauseButton.getX() - imMaryoL.getHeight() * 3, imItemBox.getY() + imItemBox.getHeight()
                - imMaryoL.getHeight() - imMaryoL.getHeight() / 2);
        stage.addActor(imMaryoL);

        scoreLabel = new Label(formatPointsString(0), skin, Assets.LABEL_BORDER60);
        scoreLabel.setFontScale(.45f);
        scoreLabel.setSize(scoreLabel.getPrefWidth(), scoreLabel.getPrefHeight());
        scoreLabel.setPosition(padX, imMaryoL.getY() + (imMaryoL.getHeight() - scoreLabel.getHeight()) / 2);
        stage.addActor(scoreLabel);

        imWaffles = new Image(goldM);
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

        time.update(0);
        timeLabel = new Label(new String(time.getChars()), skin, Assets.LABEL_BORDER60);
        timeLabel.setFontScale(.45f);
        timeLabel.setSize(timeLabel.getPrefWidth(), timeLabel.getPrefHeight());
        timeLabel.setPosition(livesLabel.getX() - padX, scoreLabel.getY(), Align.bottomRight);
        stage.addActor(timeLabel);

        Texture helpScreen = world.screen.game.assets.manager.get("data/hud/help.png", Texture.class);
        helpScreen.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        imHelp = new Image(helpScreen);
        imHelp.setSize(imHelp.getWidth() * .8f, imHelp.getHeight() * .8f);
        imHelp.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        stage.addActor(imHelp);

        hintLabel = new Label(" ", skin, Assets.LABEL_BORDER60);
        hintLabel.setFontScale(.5f);
        hintLabel.setWrap(true);
        hintLabel.setWidth(stage.getWidth() - 2 * padX);
        hintLabel.setHeight(2 * hintLabel.getHeight());
        hintLabel.setAlignment(Align.top, Align.center);
        hintLabel.setPosition(padX, imItemBox.getY() - padX - hintLabel.getHeight());
        stage.addActor(hintLabel);

        // die on screen buttons müssen ganz vorne sein
        int actorsNum = stage.getActors().size;
        touchpad.setZIndex(actorsNum);
        fire.setZIndex(actorsNum);
        jump.setZIndex(actorsNum);

        onGameStateChange();
    }

    public void onGameStateChange() {
        GameScreen.GAME_STATE gameState = gameScreen.getGameState();
        boolean isGameOver = (gameState == GameScreen.GAME_STATE.PLAYER_DIED && GameSave.save.lifes < 0);
        boolean isInGame = !(gameState == GameScreen.GAME_STATE.GAME_READY
                || gameState == GameScreen.GAME_STATE.GAME_PAUSED);

        readyLbl.setVisible(gameState == GameScreen.GAME_STATE.GAME_READY);

        gameOverLabel.setVisible(isGameOver);
        pauseLabel.setVisible(gameState == GameScreen.GAME_STATE.GAME_PAUSED);

        imHelp.setVisible(isInGame);
        if (!imHelp.isVisible())
            imHelp.getColor().a = 0;
        hintLabel.setVisible(isInGame);
        if (!hintLabel.isVisible()) {
            hintLabel.getColor().a = 0;
            hintLabel.clearActions();
        }

        scoreLabel.setVisible(isInGame);
        imItemBox.setVisible(isInGame);
        coinsLabel.setVisible(isInGame);
        imWaffles.setVisible(isInGame);
        timeLabel.setVisible(isInGame);
        imMaryoL.setVisible(isInGame);
        livesLabel.setVisible(isInGame);
        buttonsTable.setVisible(gameState == GameScreen.GAME_STATE.GAME_PAUSED);
        imGameLogo.setVisible(gameState == GameScreen.GAME_STATE.GAME_PAUSED || isGameOver);
        pauseButton.setVisible(isInGame && !isGameOver);
        jump.setVisible(isInGame && !isGameOver && !hasKeyboardOrController);
        fire.setVisible(jump.isVisible() && world.maryo.hasFireAbility());
        touchpad.setVisible(jump.isVisible());

        if (!isInGame && popupBox != null && popupBox.hasParent())
            popupBox.hide();
    }

    public void showKeyboardHelp() {
        imHelp.clearActions();
        imHelp.addAction(Actions.fadeIn(.5f));

        if (hintLabel.getColor().a > 0) {
            hintLabel.clearActions();
            hintLabel.getColor().a = 0;
        }
    }

    public void hideKeyboardHelp() {
        imHelp.clearActions();
        imHelp.addAction(Actions.fadeOut(.3f));
    }

    public void render(GameScreen.GAME_STATE gameState, float deltaTime) {
        if (gameState == GameScreen.GAME_STATE.GAME_PAUSED)
            drawPauseOverlay();

        else {
            if (updateTimer) stateTime += deltaTime;
            batch.setProjectionMatrix(stage.getCamera().combined);
            batch.begin();

            //if(GameSave.getItem() != null)
            //	batch.setColor(Color.RED);

            noUpdateDuration = noUpdateDuration + deltaTime;

            if (noUpdateDuration >= UPDATE_FREQ) {
                noUpdateDuration = 0;
                // points
                pointsText = formatPointsString(GameSave.save.points);
                scoreLabel.setText(pointsText);

                //coins
                String coins = this.coins.toString(GameSave.getCoins());
                coinsLabel.setText(coins);

                //time
                time.update(stateTime);
                timeLabel.setText(new String(time.getChars()));

                //lives
                int lifesToShow = GameSave.save.lifes;
                //während Sterbens-Animation wurde Leben schon abgezogen, für Anzeige aber noch dazuzählen
                if (gameScreen.getGameState() == GameScreen.GAME_STATE.PLAYER_DEAD ||
                        gameScreen.getGameState() == GameScreen.GAME_STATE.PLAYER_DIED)
                    lifesToShow++;
                livesLabel.setText(this.lives.toString(lifesToShow));
            }

            //draw item if any
            if (GameSave.getItem() != null) {
                float w = imItemBox.getWidth() * 0.5f;
                float h = imItemBox.getHeight() * 0.5f;
                float x = imItemBox.getX() + w * .5f;
                float y = imItemBox.getY() + h * .5f;
                batch.draw(GameSave.getItem().texture, x, y, w, h);
            }

            batch.end();
        }

        stage.getViewport().apply();
        stage.act(deltaTime);
        stage.draw();
    }

    public void showPopupBox(String text) {
        popupBox = new Dialog("", skin, Assets.WINDOW_NOFRAME) {
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
        hintLabel.addAction(Actions.sequence(Actions.fadeIn(.2f), Actions.delay(duration), Actions.fadeOut(.2f)));
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

    public void setHasKeyboardOrController(boolean hasKeyboardOrController) {
        if (this.hasKeyboardOrController == hasKeyboardOrController)
            return;

        if (hasKeyboardOrController && !keyboardF1HintShown) {
            showHint("Hold F1 key to see an overview of keys to control the game", 10f);
            keyboardF1HintShown = true;
        }

        this.hasKeyboardOrController = hasKeyboardOrController;
        onGameStateChange();
    }
}
