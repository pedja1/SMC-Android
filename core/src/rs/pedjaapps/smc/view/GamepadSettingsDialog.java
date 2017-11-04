package rs.pedjaapps.smc.view;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import de.golfgl.gdxcontroller.ControllerMenuDialog;
import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;

/**
 * Created by Benjamin Schulte on 04.11.2017.
 */

public class GamepadSettingsDialog extends ControllerMenuDialog {

    private Button refreshButton;
    private RefreshListener controllerListener;

    public GamepadSettingsDialog(Skin skin) {
        super("", skin, Assets.WINDOW_SMALL);

        getButtonTable().defaults().pad(20, 40, 0, 40);
        button(new ColorableTextButton("OK", skin, Assets.BUTTON_SMALL));

        refreshButton = new ColorableTextButton("Refresh", getSkin(), Assets.BUTTON_SMALL);
        refreshButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                refreshShownControllers();
            }
        });

        getButtonTable().add(refreshButton);
        buttonsToAdd.add(refreshButton);

        controllerListener = new RefreshListener();
    }

    private void refreshShownControllers() {
        fillContentTable();
        invalidate();
        pack();
        setPosition(getStage().getWidth() / 2, getStage().getHeight() / 2, Align.center);
    }

    private void fillContentTable() {
        Table contentTable = getContentTable();
        contentTable.clear();

        Array<Controller> controllers = Controllers.getControllers();

        contentTable.add(new Label(controllers.size == 0 ? "No controllers found." : "Controllers found:",
                getSkin(), Assets.LABEL_SIMPLE25)).padBottom(30);
        for (int i = 0; i < controllers.size; i++) {
            contentTable.row().pad(10);
            String shownName = controllers.get(i).getName();
            if (shownName.length() > 30)
                shownName = shownName.substring(0, 30) + "...";
            contentTable.add(new Label(shownName, getSkin(), Assets.LABEL_SIMPLE25)).left();
        }

        contentTable.row().padTop(30);
        Label hint = new Label("If a connected controller does not show up, try pressing a button.",
                getSkin(),  Assets.LABEL_SIMPLE25);
        hint.setWrap(true);
        hint.setAlignment(Align.center);
        contentTable.add(hint).fill().minWidth(MaryoGame.NATIVE_WIDTH * .7f);
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        //getContentTable().setWidth(stage.getWidth() * .7f);
        fillContentTable();

        super.show(stage, action);

        Controllers.addListener(controllerListener);
        return this;
    }

    @Override
    public void hide(Action action) {
        Controllers.removeListener(controllerListener);
        super.hide(action);
    }

    private class RefreshListener extends ControllerAdapter {
        @Override
        public void connected(Controller controller) {
            refreshShownControllers();
        }

        @Override
        public void disconnected(Controller controller) {
            refreshShownControllers();
        }
    }
}
