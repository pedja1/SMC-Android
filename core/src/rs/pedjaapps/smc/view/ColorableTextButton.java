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
        getStyle().fontColor = getColor();
    }

    public ColorableTextButton(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
        getStyle().fontColor = getColor();
    }

    public ColorableTextButton(String text, TextButtonStyle style) {
        super(text, style);
        getStyle().fontColor = getColor();
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        getStyle().fontColor = getColor();
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        super.setColor(r, g, b, a);
        getStyle().fontColor = getColor();
    }

}
