package rs.pedjaapps.smc.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.assets.FontAwesome;

/**
 * Created by Benjamin Schulte on 24.10.2017.
 */

public class AboutDialog extends ScrollDialog {
    public final Label.LabelStyle simpleLabel = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
    private final ColorableTextButton scrollDownButton;
    private final ColorableTextButton scrollUpButton;
    private Stage stage;
    private float sinceLastScroll;

    public AboutDialog(Skin skin) {
        super(skin, .8f, .5f);

        // Als allererstes um dieses als Defaulbutton zu setzen
        button(new ColorableTextButton(FontAwesome.CIRCLE_CHECK, skin, Assets.BUTTON_FA_FRAMELESS));

        Runnable gpl3runnable = getLicenseBoxRunnable("data/about/license_gpl3.txt");
        Runnable apache2runnable = getLicenseBoxRunnable("data/about/license_ap2.txt");

        Table aboutTable = new Table();
        aboutTable.defaults().pad(5).align(Align.center);

        aboutTable.add(new Label("Secret Chronicles Classic", skin, Assets.LABEL_BORDER60));
        aboutTable.row();
        aboutTable.add(new Label("Version " + MaryoGame.GAME_VERSION, skin, Assets.LABEL_SIMPLE25));
        aboutTable.row();
        aboutTable.add(new Label("brought to you by Benjamin Schulte", skin, Assets.LABEL_SIMPLE25));
        aboutTable.row().padBottom(40);
        aboutTable.add(getButtonsTable(new String[]{"Website", "License"},
                new Runnable[]{getWebRunnable(MaryoGame.GAME_WEBURL),
                        gpl3runnable}, Assets.BUTTON_SMALL));

        aboutTable.row();
        aboutTable.add(new Label("This game is based on the following projects:", skin, Assets.LABEL_SIMPLE25));

        aboutTable.row().padTop(40);
        aboutTable.add(getCenteredSmallLabel("Graphics, levels, sounds:\nSecret Maryo Chronicles by Florian Richter " +
                "and others")).fill();
        aboutTable.row();
        aboutTable.add(getButtonsTable(new String[]{"Website", "License"},
                new Runnable[]{getWebRunnable("http://www.secretmaryo.org/"),
                        gpl3runnable}));

        aboutTable.row().padTop(40);
        aboutTable.add(getCenteredSmallLabel("Source code:\nSMC-Android by Predrag Cokulov")).fill();
        aboutTable.row();
        aboutTable.add(getButtonsTable(new String[]{"Website", "License"},
                new Runnable[]{getWebRunnable("https://github.com/pedja1/SMC-Android"), gpl3runnable}));

        aboutTable.row().padTop(40);
        aboutTable.add(getCenteredSmallLabel("Source code:\nlibGDX game development framework")).fill();
        aboutTable.row();
        aboutTable.add(getButtonsTable(new String[]{"Website", "License"},
                new Runnable[]{getWebRunnable("http://libgdx.badlogicgames.com/"), apache2runnable}));

        aboutTable.row().padTop(40);
        aboutTable.add(getCenteredSmallLabel("Game service connection:\ngdx-gamesvcs by Benjamin Schulte")).fill();
        aboutTable.row();
        aboutTable.add(getButtonsTable(new String[]{"Website", "License"},
                new Runnable[]{getWebRunnable("https://github.com/MrStahlfelge/gdx-gamesvcs"), apache2runnable}));

        aboutTable.row();
        aboutTable.add(getButtonsTable(new String[]{"Source code"},
                new Runnable[]{getWebRunnable(MaryoGame.GAME_SOURCEURL)}));

        scrollActor = aboutTable;

        scrollDownButton = new ColorableTextButton(FontAwesome.CIRCLE_DOWN, skin, Assets.BUTTON_FA_FRAMELESS);
        scrollUpButton = new ColorableTextButton(FontAwesome.CIRCLE_UP, skin, Assets.BUTTON_FA_FRAMELESS);
        getButtonTable().add(scrollDownButton);
        buttonsToAdd.add(scrollDownButton);
        getButtonTable().add(scrollUpButton);
        buttonsToAdd.add(scrollUpButton);
    }

    private Runnable getLicenseBoxRunnable(final String file) {
        return new Runnable() {
            @Override
            public void run() {
                String license = Gdx.files.internal(file).readString();
                Label textLabel = new Label(license, simpleLabel);
                textLabel.setWrap(true);

                ScrollDialog licenseBox = new ScrollDialog(skin, .5f, .75f);
                licenseBox.setScrollActor(textLabel);
                licenseBox.button(new ColorableTextButton(FontAwesome.CIRCLE_CHECK, skin, Assets.BUTTON_FA_FRAMELESS));
                licenseBox.show(stage);
            }
        };
    }

    private Runnable getWebRunnable(final String url) {
        return new Runnable() {
            @Override
            public void run() {
                Gdx.net.openURI(url);
            }
        };
    }

    private Table getButtonsTable(String[] label, Runnable[] run) {
        return getButtonsTable(label, run, Assets.BUTTON_SMALL_FRAMELESS);
    }

    private Table getButtonsTable(String[] label, final Runnable[] run, String styleName) {
        Table storebuttons = new Table();
        for (int i = 0; i < label.length; i++) {
            TextButton actor = new ColorableTextButton(label[i], skin, styleName);
            final Runnable runnable = run[i];
            if (runnable != null)
                actor.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        runnable.run();
                    }

                });
            storebuttons.add(actor).uniform().fill().pad(5);
            buttonsToAdd.add(actor);
        }
        return storebuttons;
    }

    private Label getCenteredSmallLabel(String text) {
        Label smLabel = new Label(text, skin, Assets.LABEL_SIMPLE25);
        smLabel.setWrap(true);
        smLabel.setAlignment(Align.center);
        return smLabel;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        sinceLastScroll -= delta;
        sinceLastScroll = Math.max(-1, sinceLastScroll);
        int forceScroll = (scrollDownButton.isPressed() ? 1 : scrollUpButton.isPressed() ? -1 : 0);

        if (forceScroll != 0 && sinceLastScroll <= 0) {
            getStage().setScrollFocus(scrollPane);
            getStage().scrolled(forceScroll);
            sinceLastScroll=.1f;
        }
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        this.stage = stage;
        return super.show(stage, action);
    }
}
