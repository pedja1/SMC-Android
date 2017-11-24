package rs.pedjaapps.smc.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import de.golfgl.gdx.controllers.ControllerMenuDialog;
import de.golfgl.gdxgamesvcs.IGameServiceListener;
import de.golfgl.gdxgamesvcs.gamestate.ISaveGameStateResponseListener;
import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.utility.GameSave;

/**
 * Created by Benjamin Schulte on 24.11.2017.
 */

public class GpgsDialog extends ControllerMenuDialog implements IGameServiceListener {
    private final ColorableTextButton loginButton;
    private final ColorableTextButton saveNowButton;
    protected MaryoGame game;
    boolean alreadySaved;

    public GpgsDialog(Skin skin, MaryoGame game) {
        super("", skin, Assets.WINDOW_SMALL);

        this.game = game;

        getButtonTable().defaults().pad(20, 40, 0, 40);
        ColorableTextButton closeButton = new ColorableTextButton("Close", skin, Assets.BUTTON_SMALL);
        button(closeButton);

        Table contentTable = getContentTable();
        contentTable.clear();

        contentTable.add(new Label("Cloud save", getSkin(), Assets.LABEL_SIMPLE25)).padBottom(30);

        contentTable.row();
        Label hint = new Label("If you log in to your Google Account, Secret Chronicles will automatically save your " +
                "gamestate to your Google Drive.", getSkin(), Assets.LABEL_SIMPLE25);
        hint.setFontScale(.8f);
        hint.setWrap(true);
        hint.setAlignment(Align.center);
        contentTable.add(hint).padBottom(30).fill().minWidth(MaryoGame.NATIVE_WIDTH * .7f);

        contentTable.row();
        loginButton = new ColorableTextButton("", getSkin(), Assets.BUTTON_SMALL);
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                logInOurOut();
            }
        });
        contentTable.add(loginButton);
        buttonsToAdd.add(loginButton);

        contentTable.row();
        saveNowButton = new ColorableTextButton("Save gamestate now", getSkin(),
                Assets.BUTTON_SMALL_FRAMELESS);
        saveNowButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (alreadySaved)
                    return;

                alreadySaved = true;
                saveNowButton.setText("Saving...");
                GameSave.save(new ISaveGameStateResponseListener() {
                    @Override
                    public void onGameStateSaved(boolean success, String errorCode) {
                        saveNowButton.setText(success ? "Gamestate saved." : "Saving gamestate failed");
                        alreadySaved = success;
                    }
                });
            }
        });
        contentTable.add(saveNowButton).minHeight(saveNowButton.getPrefHeight() * 2);
        buttonsToAdd.add(saveNowButton);

        refreshState();
    }

    private void refreshState() {
        if (game.gpgsClient.isSessionActive()) {
            String playerDisplayName = game.gpgsClient.getPlayerDisplayName();
            loginButton.setText("Sign out to deactivate cloud save" +
                    (playerDisplayName == null ? "" : "\n" + playerDisplayName));

        } else if (game.gpgsClient.isConnectionPending())
            loginButton.setText("Signing in, please wait...");
        else
            loginButton.setText("Sign in and activate cloud save");

        saveNowButton.setVisible(game.gpgsClient.isSessionActive());
    }

    private void logInOurOut() {
        if (game.gpgsClient.isConnectionPending())
            return;

        if (!game.gpgsClient.isSessionActive())
            game.gpgsClient.logIn();
        else
            game.gpgsClient.logOff();
    }

    @Override
    public void gsOnSessionActive() {
        refreshState();
    }

    @Override
    public void gsOnSessionInactive() {
        refreshState();
    }

    @Override
    public void gsShowErrorToUser(IGameServiceListener.GsErrorType et, String msg, Throwable t) {
        // GPGS Error auf aktuellem Bildschirm oder in Log anzeigen
        Gdx.app.error("GPGS", msg);
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        game.gpgsClient.setListener(this);
        return super.show(stage, action);
    }

    @Override
    public boolean remove() {
        game.gpgsClient.setListener(null);
        return super.remove();
    }
}
