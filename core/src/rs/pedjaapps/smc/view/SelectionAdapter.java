package rs.pedjaapps.smc.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.screen.LoadingScreen;
import rs.pedjaapps.smc.screen.MainMenuScreen;

public class SelectionAdapter extends Dialog {
    public static final int ITEMS_COL_CNT = 10;
    private MainMenuScreen mainMenuScreen;

    public SelectionAdapter(Array<Level> items, MainMenuScreen mainMenuScreen, Skin skin) {
        super("", skin);

        this.mainMenuScreen = mainMenuScreen;

        Table mainTable = getContentTable();
        mainTable.row();
        mainTable.add(new Label("Select Level", skin, "outline")).colspan(ITEMS_COL_CNT);

        int col = 0;
        int offset = 0;
        for (Level level : items) {
            if (col == 0)
                mainTable.row();
            col++;
            offset++;

            final String levelId = level.levelId;
            TextButton levelButton = new TextButton(String.valueOf(offset), skin, "small");
            levelButton.setDisabled(!level.isUnlocked && !MaryoGame.GAME_DEVMODE);
            levelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    SelectionAdapter.this.mainMenuScreen.game.setScreen(
                            new LoadingScreen(new GameScreen(SelectionAdapter.this.mainMenuScreen.game, true,
                                    levelId), false));
                }
            });

            mainTable.add(levelButton);

            if (col == ITEMS_COL_CNT)
                col = 0;

        }

        TextButton backButton = new TextButton("Back", skin, "small");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectionAdapter.this.hide();
            }
        });
        getButtonTable().add(backButton);
    }

    public static class Level {
        public boolean isUnlocked;
        public String levelId, levelNumber;
    }
}
