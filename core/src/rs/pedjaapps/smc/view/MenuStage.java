package rs.pedjaapps.smc.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.golfgl.gdxcontroller.ControllerMenuStage;

/**
 * Created by Benjamin Schulte on 03.11.2017.
 */

public class MenuStage extends ControllerMenuStage {

    private Color oldColor;
    private Color emphColor;
    private Action fadeAction;

    public MenuStage(Viewport viewport) {
        super(viewport);
    }

    public Color getEmphColor() {
        return emphColor;
    }

    public void setEmphColor(Color emphColor) {
        this.emphColor = emphColor;
    }

    @Override
    protected void onFocusGained(Actor focussedActor) {
        oldColor = new Color(focussedActor.getColor());
        fadeAction = Actions.forever(Actions.sequence(Actions.color(emphColor, .5f, Interpolation.circle),
                Actions.color(oldColor, .5f, Interpolation.fade), Actions.delay(1f)));
        focussedActor.addAction(fadeAction);
        //focussedActor.setColor(emphColor);
        super.onFocusGained(focussedActor);
    }

    @Override
    protected void onFocusLost(Actor focussedActor) {
        focussedActor.removeAction(fadeAction);
        fadeAction = null;
        focussedActor.setColor(oldColor);
        super.onFocusLost(focussedActor);
    }

    @Override
    protected boolean fireEventOnActor(Actor actor, InputEvent.Type type) {
        // Die ScrollPane mag touchDown und touchUp Fakes nicht und soll die auch nicht kriegen
        if ((type == InputEvent.Type.touchDown || type == InputEvent.Type.touchUp)
                && actor != null && actor instanceof ScrollPane)
            return false;

        return super.fireEventOnActor(actor, type);
    }
}
