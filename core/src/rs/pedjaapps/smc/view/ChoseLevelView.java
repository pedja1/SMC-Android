package rs.pedjaapps.smc.view;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.assets.FontAwesome;
import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.screen.LoadingScreen;
import rs.pedjaapps.smc.screen.MainMenuScreen;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.Level;

public class ChoseLevelView extends Group {
    private MainMenuScreen mainMenuScreen;
    private Skin skin;
    private ScrollPane levelScrollPane;
    private Label numLives;
    private Group statusgroup;
    private Table levelStatusGroup;
    private TextButton backButton;
    private LevelButton currentSelectedButton;
    private TextButton leaderBoardButton;
    private Array<LevelButton> allLevelButtons;

    public ChoseLevelView(MainMenuScreen mainMenuScreen, Skin skin) {
        super();

        this.mainMenuScreen = mainMenuScreen;
        this.skin = skin;
    }

    public void inflateWidgets(TextureAtlas dynAtlas, Array<Actor> focussableActors) {
        Table levelTable = new Table();
        LevelButton preselected = null;

        Label lblChoose = new Label("Choose challenge!", skin, Assets.LABEL_BORDER60);
        lblChoose.setFontScale(.6f);
        levelTable.add(lblChoose).minHeight(getHeight() * .35f);

        allLevelButtons = new Array<>();

        int world = 1;
        for (final String levelId : Level.getLevelList()) {
            levelTable.row();
            Level level = Level.getLevel(levelId);

            if (world != level.world) {
                world = level.world;
                levelTable.add(new Label("World " + String.valueOf(world), skin, Assets.LABEL_BORDER25))
                        .center().padTop(getHeight() * .1f);
                levelTable.row();
            }

            LevelButton levelButton = new LevelButton(skin, dynAtlas) {
                @Override
                protected void onChosen() {
                    if (!isMarked())
                        selectLevelButton(this);
                    else if (isUnlocked() || MaryoGame.GAME_DEVMODE)
                        ChoseLevelView.this.mainMenuScreen.game.setScreen(
                                new LoadingScreen(new GameScreen(ChoseLevelView.this.mainMenuScreen.game, true,
                                        getLevel().levelId), false));

                }
            };
            levelButton.setLevel(level, GameSave.isUnlocked(levelId));
            levelTable.add(levelButton).fill().uniform().pad(15);
            allLevelButtons.add(levelButton);

            if (levelButton.isUnlocked() && (level.number == 1 || level.currentScore == 0))
                preselected = levelButton;
        }

        levelTable.row();
        Label lbl = new Label("And there is even more to come!\nAlso check out the level editor!", skin, Assets
                .LABEL_BORDER25);
        lbl.setFontScale(.8f);
        lbl.setWrap(true);
        lbl.setAlignment(Align.center);
        levelTable.add(lbl).fill().minHeight(getHeight() * .35f);

        backButton = new ColorableTextButton(FontAwesome.LEFT_ARROW, skin, Assets.BUTTON_FA);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                goBack();
            }
        });
        backButton.setPosition(10, getHeight() - 10, Align.topLeft);
        this.addActor(backButton);

        final TextButton downButton = new TextButton(FontAwesome.CIRCLE_DOWN, skin, Assets.BUTTON_FA_FRAMELESS);
        downButton.setPosition(getWidth() - 5, 5, Align.bottomRight);
        this.addActor(downButton);

        final TextButton upButton = new TextButton(FontAwesome.CIRCLE_UP, skin, Assets.BUTTON_FA_FRAMELESS);
        upButton.setPosition(getWidth() - 5, getHeight() - 5, Align.topRight);
        this.addActor(upButton);

        levelScrollPane = new ScrollPane(levelTable) {
            @Override
            public void act(float delta) {
                int forceScroll = (downButton.isOver() ? 1 : upButton.isOver() ? -1 : 0);

                if (forceScroll != 0)
                    setScrollY(getScrollY() + forceScroll * getMouseWheelY() * delta * 6);

                super.act(delta);
            }
        };
        levelScrollPane.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                int navigate = 0;
                switch (keycode) {
                    case Input.Keys.UP:
                        navigate--;
                        break;
                    case Input.Keys.DOWN:
                        navigate++;
                        break;
                    case Input.Keys.HOME:
                        navigate -= 100;
                        break;
                    case Input.Keys.END:
                        navigate += 100;
                        break;
                    case Input.Keys.PAGE_DOWN:
                        navigate = navigate + 3;
                        break;
                    case Input.Keys.PAGE_UP:
                        navigate = navigate - 3;
                        break;
                }

                if (navigate != 0) {
                    int goToLevel = currentSelectedButton.getLevel().number + navigate;

                    if (goToLevel < 1)
                        goToLevel = 1;
                    else if (goToLevel > allLevelButtons.size)
                        goToLevel = allLevelButtons.size;

                    if (goToLevel != currentSelectedButton.getLevel().number)
                        selectLevelButton(allLevelButtons.get(goToLevel - 1));
                    return true;

                }
                if (keycode == Input.Keys.ENTER) {
                    currentSelectedButton.getClickListener().clicked(event, 0, 0);
                    return true;

                } else {
                    return super.keyDown(event, keycode);
                }
            }
        });
        levelScrollPane.setScrollingDisabled(true, false);
        levelScrollPane.setWidth(getWidth() * .42f);
        levelScrollPane.setHeight(getHeight());
        levelScrollPane.setPosition(upButton.getX() - 5, getHeight() / 2, Align.right);
        this.addActor(levelScrollPane);

        // Statusinfos zu Maryo
        statusgroup = new Group();

        if (GameSave.getItem() != 0) {
            Image imItem = new Image(dynAtlas.findRegion(Item.getSavedItemTextureName(GameSave.getItem())));
            imItem.setScale(.7f);
            imItem.setPosition(165, 380);
            statusgroup.addActor(imItem);
        }

        numLives = new Label("x" + String.valueOf(GameSave.getLifes() - 1), skin, Assets.LABEL_BORDER60);
        numLives.setFontScale(.5f);
        // die Zahlen sind die von Maryo
        numLives.setPosition(175, 390);
        statusgroup.addActor(numLives);

        Label numCoins = new Label(String.valueOf(GameSave.getCoins()), skin, Assets.LABEL_BORDER60);
        numCoins.setFontScale(.5f);
        numCoins.setHeight(numCoins.getPrefHeight());
        numCoins.setPosition(numLives.getX(), numLives.getY() - 20, Align.topLeft);
        statusgroup.addActor(numCoins);

        Image imCoins = new Image(skin, HUD.IMAGE_WAFFLES);
        imCoins.setSize((numCoins.getPrefHeight() / imCoins.getPrefHeight()) * imCoins.getPrefWidth(), numCoins
                .getPrefHeight());
        imCoins.setPosition(numCoins.getX() - 5, numCoins.getY(), Align.bottomRight);
        statusgroup.addActor(imCoins);

        Label lblTotal = getScaledLabel("SCORE", .5f);
        lblTotal.setPosition(numCoins.getX() - 10, getHeight() / 2, Align.right);
        statusgroup.addActor(lblTotal);

        Label totalScore = getScaledLabel(String.valueOf(GameSave.getTotalScore()), .5f);
        totalScore.setPosition(numCoins.getX(), getHeight() / 2, Align.left);
        statusgroup.addActor(totalScore);

        addActor(statusgroup);

        levelScrollPane.validate();

        selectLevelButton(preselected);

        focussableActors.add(levelScrollPane);
        focussableActors.add(backButton);

        //kommt erst später in die Anzeige, hier hinzufügen wegen focussableActors
        leaderBoardButton = new ColorableTextButton("SHOW LEADER", skin, Assets.BUTTON_SMALL_FRAMELESS);
        focussableActors.add(leaderBoardButton);
    }

    private void selectLevelButton(LevelButton levelButton) {
        if (currentSelectedButton != levelButton && currentSelectedButton != null)
            currentSelectedButton.setMarked(false);

        currentSelectedButton = levelButton;
        levelButton.setMarked(true);

        if (levelStatusGroup != null)
            removeActor(levelStatusGroup);

        final Level level = currentSelectedButton.getLevel();

        levelStatusGroup = new Table();
        levelStatusGroup.add().minWidth(numLives.getX());
        levelStatusGroup.add().minWidth(levelScrollPane.getX() - numLives.getX());
        levelStatusGroup.row();
        if (levelButton.isUnlocked()) {
            levelStatusGroup.add();
            levelStatusGroup.add(getScaledLabel("LEVEL " + level.number + " SCORES", .5f)).padBottom(5).left();

            levelStatusGroup.row();
            if (level.bestScore == 0 && level.currentScore == 0) {
                levelStatusGroup.add();
                levelStatusGroup.add(getScaledLabel("- NO SCORES YET -", .5f)).left();
            } else {
                levelStatusGroup.add(new Label("CURRENT", skin, Assets.LABEL_BORDER25)).right().bottom()
                        .padRight(10);
                levelStatusGroup.add(getScaledLabel(String.valueOf(level.currentScore), .5f)).left();

                levelStatusGroup.row();
                levelStatusGroup.add(new Label("BEST", skin, Assets.LABEL_BORDER25)).right().bottom()
                        .padRight(10);
                levelStatusGroup.add(getScaledLabel(String.valueOf(level.bestScore), .5f)).left();
            }
        } else {
            Label levelTitle = getScaledLabel("LEVEL " + level.number + " LOCKED", .5f);
            levelStatusGroup.add(levelTitle).colspan(2).padBottom(5);

            levelStatusGroup.row();
            Label unlockHint = getScaledLabel("CLEAR LEVEL " + String.valueOf(level.number - 1)
                    + " TO UNLOCK", .5f);
            levelStatusGroup.add(unlockHint).colspan(2);
            unlockHint.addAction(HUD.getForeverFade());
            levelTitle.addAction(HUD.getForeverFade());
        }


        levelStatusGroup.row();
        levelStatusGroup.add(leaderBoardButton).colspan(2)
                .padTop(5).minHeight(backButton.getPrefHeight() * .75f);
        levelStatusGroup.validate();
        levelStatusGroup.setPosition(levelScrollPane.getX() / 2, levelStatusGroup.getPrefHeight() / 2 + 10);
        addActor(levelStatusGroup);
    }

    private Label getScaledLabel(String text, float scale) {
        Label newLabelActor = new Label(text, skin, Assets.LABEL_BORDER60);
        newLabelActor.setFontScale(scale);
        newLabelActor.setHeight(newLabelActor.getPrefHeight());
        newLabelActor.setWidth(newLabelActor.getPrefWidth());
        return newLabelActor;
    }

    protected void goBack() {
        //overriden
    }

    /**
     * informs that the Group is shown on a stage
     *
     * @param stage
     */
    public void onShow(MenuStage stage) {
        //statusgroup.addAction(Actions.sequence(Actions.alpha(0),
        //        Actions.delay(MainMenuScreen.DURATION_TRANSITION), Actions.fadeIn(1f)));
        stage.setFocussedActor(levelScrollPane);
        if (currentSelectedButton != null)
            currentSelectedButton.scrollTo();
        stage.setEscapeActor(backButton);
    }
}
