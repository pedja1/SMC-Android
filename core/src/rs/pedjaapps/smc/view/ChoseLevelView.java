package rs.pedjaapps.smc.view;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.assets.FontAwesome;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.screen.LoadingScreen;
import rs.pedjaapps.smc.screen.MainMenuScreen;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.Level;

public class ChoseLevelView extends Group {
    private MainMenuScreen mainMenuScreen;
    private Skin skin;
    private ScrollPane levelScrollPane;

    public ChoseLevelView(MainMenuScreen mainMenuScreen, Skin skin) {
        super();

        this.mainMenuScreen = mainMenuScreen;
        this.skin = skin;
    }

    public void inflateWidgets(TextureAtlas dynAtlas) {
        Table levelTable = new Table();

        Label lblChoose = new Label("Choose challenge!", skin, Assets.LABEL_BORDER60);
        lblChoose.setFontScale(.6f);
        levelTable.add(lblChoose).minHeight(getHeight() * .35f);

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
                    ChoseLevelView.this.mainMenuScreen.game.setScreen(
                            new LoadingScreen(new GameScreen(ChoseLevelView.this.mainMenuScreen.game, true,
                                    getLevel().levelId), false));
                }
            };
            levelButton.setLevel(level, GameSave.isUnlocked(levelId));
            levelTable.add(levelButton).fill().uniform().pad(15);
        }

        levelTable.row();
        Label lbl = new Label("And there is even more to come!\nAlso check out the level editor!", skin, Assets
                .LABEL_BORDER25);
        lbl.setFontScale(.8f);
        lbl.setWrap(true);
        lbl.setAlignment(Align.center);
        levelTable.add(lbl).fill().minHeight(getHeight() * .35f);

        TextButton backButton = new TextButton(FontAwesome.LEFT_ARROW, skin, Assets.BUTTON_FA);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                goBack();
            }
        });
        backButton.setPosition(10, 10, Align.bottomLeft);
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
        levelScrollPane.setScrollingDisabled(true, false);
        levelScrollPane.setWidth(getWidth() * .42f);
        levelScrollPane.setHeight(getHeight());
        levelScrollPane.setPosition(upButton.getX() - 5, getHeight() / 2, Align.right);
        this.addActor(levelScrollPane);


    }

    protected void goBack() {
    }

    /**
     * informs that the Group is shown on a stage
     *
     * @param stage
     */
    public void onShow(Stage stage) {
        stage.setScrollFocus(levelScrollPane);
    }
}
