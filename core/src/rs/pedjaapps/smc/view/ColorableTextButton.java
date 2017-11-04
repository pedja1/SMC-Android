package rs.pedjaapps.smc.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import rs.pedjaapps.smc.shader.Shader;

/**
 * Created by Benjamin Schulte on 04.11.2017.
 */

public class ColorableTextButton extends TextButton {
    public ColorableTextButton(String text, Skin skin) {
        super(text, skin);
    }

    public ColorableTextButton(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public ColorableTextButton(String text, TextButtonStyle style) {
        super(text, style);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color oldColor = getStyle().fontColor;
        getStyle().fontColor = getColor();
        super.draw(batch, parentAlpha);
        getStyle().fontColor = oldColor;
    }
}
