package rs.pedjaapps.smc.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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

    public LevelButton(Skin skin, TextureAtlas dynAtlas) {
        super(skin, Assets.BUTTON_BORDER);

        this.dynAtlas = dynAtlas;
        this.skin = skin;
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

        Actor prev = null;
        if (level.textureName != null) {
            Image imPreview = new Image(dynAtlas.findRegion(level.textureName));
            imPreview.setScaling(Scaling.fit);
            prev = imPreview;
        } else {
            Label levelNum = new Label(String.valueOf(level.number), skin, Assets.LABEL_BORDER60);
            levelNum.setAlignment(Align.center);
            prev = levelNum;
        }

        Table facts = new Table();
        Label labelLevelName = new Label(level.levelName, skin, Assets.LABEL_BORDER60);
        labelLevelName.setFontScale(.6f);
        facts.add(labelLevelName).fill();
        facts.row();
        statusLabel = new Label(getStatusLabel(level), skin, Assets.LABEL_BORDER25);
        facts.add(statusLabel).fill().expandX();

        add(prev).width(100).height(facts.getPrefHeight()).center().padRight(10);
        add(facts).fill().expandX().left();

        if (!unlocked) {
            Color disabledColor = skin.getColor(Assets.COLOR_PRESSED);
            statusLabel.setColor(disabledColor);
            labelLevelName.setColor(disabledColor);
            prev.setColor(disabledColor);
            setColor(disabledColor);
        }

        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onChosen();;
            }
        });
    }

    private String getStatusLabel(Level level) {
        return !unlocked ? "LOCKED" : level.currentScore > 0 ?
                "SCORE " + String.valueOf(level.currentScore) : "UNLOCKED";
    }

    protected void onChosen() {

    }
}
