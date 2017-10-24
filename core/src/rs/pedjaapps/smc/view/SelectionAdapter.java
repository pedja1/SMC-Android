package rs.pedjaapps.smc.view;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.screen.LoadingScreen;
import rs.pedjaapps.smc.screen.MainMenuScreen;

public class SelectionAdapter extends Group {
    private MainMenuScreen mainMenuScreen;
    private Skin skin;

    public SelectionAdapter(MainMenuScreen mainMenuScreen, Skin skin) {
        super();

        this.mainMenuScreen = mainMenuScreen;
        this.skin = skin;
    }

    public void inflateWidgets(Array<Level> items) {
        Table levelTable = new Table();

        Label lblChoose = new Label("Choose your challenge!", skin, Assets.LABEL_BORDER60);
        lblChoose.setFontScale(.7f);
        levelTable.add(lblChoose);

        int number = 0;
        for (Level level : items) {
            levelTable.row();
            number++;

            final String levelId = level.levelId;
            Button levelButton = new Button(skin, Assets.BUTTON_SMALL);
            levelButton.setDisabled(!level.isUnlocked && !MaryoGame.GAME_DEVMODE);
            levelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    SelectionAdapter.this.mainMenuScreen.game.setScreen(
                            new LoadingScreen(new GameScreen(SelectionAdapter.this.mainMenuScreen.game, true,
                                    levelId), false));
                }
            });

            levelButton.add(new Label(String.valueOf(number), skin, Assets.LABEL_BORDER60)).minWidth(100);
            levelButton.add(new Label(level.levelId + "\n" + "Score: " + level.currentScore + "\nBest: " + level
                    .bestScore, skin,
                    Assets.LABEL_BORDER25)).fill().minWidth(300);

            levelTable.add(levelButton).fill().uniform().pad(15);
        }

        levelTable.row();
        Label lbl = new Label("And there is even more to come! Or use the level editor!", skin, Assets.LABEL_BORDER25);
        lbl.setWrap(true);
        lbl.setAlignment(Align.center);
        levelTable.add(lbl).fill();

        ScrollPane scrollPane = new ScrollPane(levelTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setWidth(getWidth() * .5f);
        scrollPane.setHeight(getHeight());
        scrollPane.setPosition(getWidth() / 2, getHeight() / 2, Align.center);
        this.addActor(scrollPane);

        TextButton backButton = new TextButton("Back", skin, Assets.BUTTON_SMALL);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                goBack();
            }
        });
        backButton.setPosition(10, 10, Align.bottomLeft);
        this.addActor(backButton);
    }

    protected void goBack() {
    }

    public static class Level {
        public boolean isUnlocked;
        public String levelId;
        public TextureRegion icon;
        public int currentScore;
        public int bestScore;
    }
}
