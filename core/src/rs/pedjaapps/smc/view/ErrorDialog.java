package rs.pedjaapps.smc.view;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.assets.FontAwesome;

/**
 * Created by Benjamin Schulte on 25.11.2017.
 */

public class ErrorDialog extends ScrollDialog {
    public ErrorDialog(String msg, Skin skin, float percentWidth, float percentHeight) {
        super(skin, percentWidth, percentHeight);

        Label textLabel = new Label(msg, skin, Assets.LABEL_SIMPLE25);
        textLabel.setWrap(true);

        setScrollActor(textLabel);
        button(new ColorableTextButton(FontAwesome.CIRCLE_CHECK, skin, Assets.BUTTON_FA_FRAMELESS));
    }
}
