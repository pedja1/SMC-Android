package rs.pedjaapps.smc.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.utility.Level;

/**
 * Created by Benjamin Schulte on 26.10.2017.
 */

public class LevelButton extends Button {

    private Level level;
    private boolean unlocked;
    private TextureAtlas dynAtlas;
    private Skin skin;
    private Label statusLabel;
    private boolean marked;
    private Actor image;
    private Label title;
    private ScrollPane scrollPane;

    public LevelButton(Skin skin, TextureAtlas dynAtlas) {
        super(skin, Assets.BUTTON_BORDER);

        this.dynAtlas = dynAtlas;
        this.skin = skin;
    }

    @Override
    public void act(float delta) {
        boolean hasFocus;

        if (getStage() instanceof MenuStage && scrollPane != null)
            // prüfen ob der Fokus auf der ScrollPane liegt
            hasFocus = (((MenuStage) getStage()).getFocussedActor() == scrollPane);
        else
            //stimmt nicht ganz, aber Levelbutton ist eh immer in ScrollPane
            hasFocus = true;

        // Animationen nur wenn Fokus drauf ist
        if (hasFocus)
            super.act(delta);

    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        if (this.marked == marked)
            return;

        this.marked = marked;
        setColor(getStatusColor());
        statusLabel.setText(getStatusLabel());
        statusLabel.clearActions();

        if (marked) {
            scrollTo();
            if (unlocked) {
                statusLabel.addAction(HUD.getForeverFade());
                statusLabel.setColor(skin.getColor(Assets.COLOR_EMPH2));
            }
        } else
            statusLabel.addAction(Actions.fadeIn(.5f));
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level, boolean unlocked) {

        this.clearChildren();

        this.unlocked = unlocked;
        this.level = level;

        image = null;
        if (level.textureName != null) {
            Image imPreview = new Image(dynAtlas.findRegion(level.textureName));
            imPreview.setScaling(Scaling.fit);
            image = imPreview;
        } else {
            Label levelNum = new Label(String.valueOf(level.number), skin, Assets.LABEL_BORDER60);
            levelNum.setAlignment(Align.center);
            image = levelNum;
        }

        Table facts = new Table();
        title = new Label(level.levelName, skin, Assets.LABEL_BORDER60);
        title.setFontScale(.6f);
        facts.add(title).fill();
        facts.row();
        statusLabel = new Label(getStatusLabel(), skin, Assets.LABEL_BORDER25);
        facts.add(statusLabel).fill().expandX();

        add(image).width(100).height(facts.getPrefHeight()).center().padRight(10);
        add(facts).fill().expandX().left();

        Color color = getStatusColor();
        setColor(color);
        if (!unlocked)
            image.setColor(color);

        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onChosen();
            }
        });
    }

    private Color getStatusColor() {
        if (!unlocked && marked) {
            Color color = new Color(skin.getColor(Assets.COLOR_PRESSED));
            color.add(color);
            return color;
        } else if (!unlocked) {
            return skin.getColor(Assets.COLOR_PRESSED);
        } else if (marked)
            return skin.getColor(Assets.COLOR_EMPH1);
        else
            return Color.WHITE;
    }

    private String getStatusLabel() {
        return !unlocked ? "LOCKED" : marked ? "PRESS TO PLAY" : level.currentScore > 0 ?
                "SCORE " + String.valueOf(level.currentScore) : "UNLOCKED";
    }

    protected void onChosen() {

    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        statusLabel.setColor(color);
        title.setColor(color);
    }

    public void scrollTo() {
        if (scrollPane != null) {
            Vector2 localCoords = localToParentCoordinates(new Vector2(getWidth() / 2, getHeight() / 2));
            scrollPane.setSmoothScrolling(false);
            scrollPane.scrollTo(localCoords.x, localCoords.y, 1, 1, false, true);
            scrollPane.setSmoothScrolling(true);
        }
    }

    @Override
    protected void setParent(Group parent) {
        super.setParent(parent);

        findScrollPane(parent);
    }

    private void findScrollPane(Group parent) {
        Actor parentActor = parent;

        if (parentActor != null)
            parentActor = parentActor.getParent();

        if (parentActor != null && parentActor instanceof ScrollPane)
            scrollPane = (ScrollPane) parentActor;
        else
            scrollPane = null;
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        findScrollPane(getParent());

    }
}
