package rs.pedjaapps.smc.view;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.golfgl.gdxcontroller.ControllerMenuDialog;
import rs.pedjaapps.smc.assets.Assets;

/**
 * Created by Benjamin Schulte on 24.10.2017.
 */

public class ScrollDialog extends ControllerMenuDialog {

    protected Skin skin;
    protected float percentHeight;
    protected float percentWidth;
    protected Actor scrollActor;

    public ScrollDialog(Skin skin, float percentWidth, float percentHeight) {
        super("", skin, Assets.WINDOW_SMALL);
        this.skin = skin;
        this.percentHeight = percentHeight;
        this.percentWidth = percentWidth;
    }

    public Actor getScrollActor() {
        return scrollActor;
    }

    public void setScrollActor(Actor scrollActor) {
        this.scrollActor = scrollActor;
    }

    private ScrollPane addScrollWidget(Stage stage) {
        ScrollPane scrollPane = new ScrollPane(scrollActor, skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        getContentTable().add(scrollPane).minWidth(stage.getWidth() * percentWidth)
                .height(stage.getHeight() * percentHeight);
        return scrollPane;
    }

    @Override
    public Dialog show(Stage stage) {
        ScrollPane scrollPane = addScrollWidget(stage);
        Dialog dialog = super.show(stage);
        stage.setScrollFocus(scrollPane);
        return dialog;
    }
}
