package rs.pedjaapps.smc.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.screen.AbstractScreen;
import rs.pedjaapps.smc.shader.Shader;
import rs.pedjaapps.smc.utility.PrefsManager;

/**
 * Created by pedja on 11.10.14..
 */
public class SettingsDialog
{
    OrthographicCamera cam;

    AbstractScreen screen;

    ShapeRenderer shapeRenderer = new ShapeRenderer();

    public boolean visible = false;

    Texture back;

    ConfirmDialog.Button buttonNo, buttonYes;

    float dialogX, dialogY;
    float dialogWidth, dialogHeight;

    BitmapFont font;
    GlyphLayout fontGlyph;

    String title = "Settings";
    String text;

    Rectangle titleR, textR, contentRect;
    Check low, medium, high;

    public SettingsDialog(AbstractScreen screen, OrthographicCamera cam)
    {
        this.screen = screen;
        this.cam = cam;

        setupDialogBounds();

        text = "Texture Quality";
    }

    private void setupDialogBounds()
    {
        dialogWidth = cam.viewportWidth * .45f;
        dialogHeight = dialogWidth * 0.5f;

        dialogX = cam.position.x - dialogWidth * .5f;
        dialogY = cam.position.y - dialogHeight * .5f;

        contentRect = new Rectangle();
        contentRect.width = dialogWidth * .7f;
        contentRect.height = dialogHeight * .6f;
        contentRect.x = dialogX + dialogWidth * .5f - contentRect.width * .5f;
        contentRect.y = dialogY + dialogHeight * .5f - contentRect.height * .5f;
    }

    public void loadAssets()
    {
        Assets.manager.load("data/hud/hud.pack", TextureAtlas.class);

		Assets.manager.load("data/hud/dialog_background.png", Texture.class, Assets.textureParameter);
        FreetypeFontLoader.FreeTypeFontLoaderParameter params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        params.fontFileName = "data/fonts/GROBOLD.ttf";//Constants.DEFAULT_FONT_FILE_NAME;
        params.fontParameters.magFilter = Texture.TextureFilter.Linear;
        params.fontParameters.minFilter = Texture.TextureFilter.Linear;
        params.fontParameters.size = (int) (cam.viewportHeight / 30);
        params.fontParameters.characters = "YesNoStingTxurQaly";
        params.fontParameters.borderWidth = 2f;
        Assets.manager.load("settings_dialog.ttf", BitmapFont.class, params);

        params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        params.fontFileName = "data/fonts/GROBOLD.ttf";//Constants.DEFAULT_FONT_FILE_NAME;
        params.fontParameters.magFilter = Texture.TextureFilter.Linear;
        params.fontParameters.minFilter = Texture.TextureFilter.Linear;
        params.fontParameters.characters = "LOWHIGMEDU";
        params.fontParameters.size = (int) (cam.viewportHeight / 40);
        params.fontParameters.borderWidth = 2f;
        Assets.manager.load("texture_quality.ttf", BitmapFont.class, params);
    }

    public void initAssets()
    {
        TextureAtlas atlas = Assets.manager.get("data/hud/hud.pack");
        back = Assets.manager.get("data/hud/dialog_background.png");
        TextureRegion check = atlas.findRegion("check");
        TextureRegion checkBackground = atlas.findRegion("check_background");

		TextureRegion btnCancel = atlas.findRegion("cancel");
		TextureRegion btnAccept = atlas.findRegion("accept");
		
		font = Assets.manager.get("settings_dialog.ttf");
        fontGlyph = new GlyphLayout();

        buttonNo = new ConfirmDialog.Button();
        buttonNo.rect.width = dialogWidth * .15f;
        buttonNo.rect.height = buttonNo.rect.width;
        buttonNo.rect.x = dialogX + dialogWidth * .35f - buttonNo.rect.width * .5f;
        buttonNo.rect.y = dialogY - buttonNo.rect.height * .5f;
        buttonNo.texture = btnCancel;

        buttonYes = new ConfirmDialog.Button(buttonNo);
        buttonYes.rect.x = dialogX + dialogWidth * .65f - buttonNo.rect.width * .5f;
        buttonYes.texture = btnAccept;

        fontGlyph.setText(font, title);
        titleR = new Rectangle(contentRect.x + contentRect.width * .5f - fontGlyph.width * .5f,
                contentRect.y + contentRect.height,
                fontGlyph.width, fontGlyph.height);

        fontGlyph.setText(font, text, font.getColor(), contentRect.width, Align.center, true);
        //bounds = font.getWrappedBounds(text, dialogWidth - dialogPadding * 2);
        float top = titleR.y - titleR.height;
        float bottom = buttonNo.rect.y + buttonNo.rect.height;
        float centerVer = top - ((top - bottom) * .5f);
        textR = new Rectangle(dialogX + dialogWidth * .5f - fontGlyph.width * .5f,
                centerVer + fontGlyph.height * 2,
                fontGlyph.width, fontGlyph.height);

        top = textR.y - textR.height;


        BitmapFont ftn = Assets.manager.get("texture_quality.ttf");
        Rectangle rectangle = new Rectangle();
        rectangle.width = contentRect.width * .33f;
        rectangle.height = top - bottom;
        rectangle.x = contentRect.x;
        rectangle.y = bottom;
        low = new Check(ftn, rectangle, checkBackground, check, "LOW");

        medium = new Check(low);
        medium.text = "MEDIUM";
        medium.rect.x = contentRect.x + medium.rect.width;

        high = new Check(low);
        high.text = "HIGH";
        high.rect.x = contentRect.x + medium.rect.width * 2;
    }

    public void show()
    {
        visible = true;

        int tq = PrefsManager.getTextureQuality();
        low.selected = tq == 0;
        medium.selected = tq == 1;
        high.selected = tq == 2;
        low.pressed = false;
        medium.pressed = false;
        high.pressed = false;
        buttonNo.pressed = false;
        buttonYes.pressed = false;
    }

    public void hide()
    {
        visible = false;
    }

    public void render(SpriteBatch batch)
    {
        if(visible)
        {
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            Gdx.gl.glEnable(GL20.GL_BLEND);

            shapeRenderer.setProjectionMatrix(cam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.5f);
            shapeRenderer.rect(0, 0, cam.viewportWidth, cam.viewportHeight);
            shapeRenderer.end();

            batch.setProjectionMatrix(cam.combined);
            batch.begin();
            batch.draw(back, dialogX, dialogY, dialogWidth, dialogHeight);
            buttonNo.render(batch);
            buttonYes.render(batch);
            font.setColor(1, 1, 1, 1);
            font.draw(batch, title, titleR.x, titleR.y);
            font.draw(batch, text, textR.x, textR.y, textR.width, Align.center, true);
            low.render(batch);
            medium.render(batch);
            high.render(batch);
            batch.end();

            /*shapeRenderer.setProjectionMatrix(cam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 0, 0, 1);
            shapeRenderer.rect(low.rect.x, low.rect.y, low.rect.width, low.rect.height);
            shapeRenderer.rect(medium.rect.x, medium.rect.y, medium.rect.width, medium.rect.height);
            shapeRenderer.rect(high.rect.x, high.rect.y, high.rect.width, high.rect.height);
            shapeRenderer.end();*/
        }
    }

    public void resize()
    {
        setupDialogBounds();
    }

    public void dispose()
    {
    }

    public void touchDown(float x, float y)
    {
        if(buttonNo.rect.contains(x, y))
        {
            buttonNo.pressed = true;
        }
        else if(buttonYes.rect.contains(x, y))
        {
            buttonYes.pressed = true;
        }
        else if(low.rect.contains(x, y))
        {
            low.pressed = true;
        }
        else if(medium.rect.contains(x, y))
        {
            medium.pressed = true;
        }
        else if(high.rect.contains(x, y))
        {
            high.pressed = true;
        }
    }

    public void touchUp(float x, float y)
    {
        if(buttonNo.rect.contains(x, y))
        {
            visible = false;
        }
        else if(buttonYes.rect.contains(x, y))
        {
            int tq = low.selected ? 0 : (medium.selected ? 1 : 2);
            PrefsManager.setTextureQuality(tq);
            screen.game.restart();
        }
        else if(low.rect.contains(x, y))
        {
            low.pressed = false;
            low.selected = true;
            medium.selected = false;
            high.selected = false;
        }
        else if(medium.rect.contains(x, y))
        {
            medium.pressed = false;
            low.selected = false;
            medium.selected = true;
            high.selected = false;
        }
        else if(high.rect.contains(x, y))
        {
            high.pressed = false;
            low.selected = false;
            medium.selected = false;
            high.selected = true;
        }
    }

    public void touchDragged(float x, float y)
    {
        buttonNo.pressed = buttonNo.rect.contains(x, y);
        buttonYes.pressed = buttonYes.rect.contains(x, y);
        low.pressed = low.rect.contains(x, y);
        medium.pressed = medium.rect.contains(x, y);
        high.pressed = high.rect.contains(x, y);
    }

    private static class Check
    {
        BitmapFont font;
        Rectangle rect;
        TextureRegion background, checked;
        String text;
        boolean selected, pressed;
        GlyphLayout glyphLayout = new GlyphLayout();

        public Check(BitmapFont font, Rectangle rect, TextureRegion background,TextureRegion check, String text)
        {
            this.font = font;
            this.rect = rect;
            this.background = background;
            this.checked = check;
            this.text = text;
        }

        public Check(Check check)
        {
            font = check.font;
            rect = new Rectangle(check.rect);
            text = check.text;
            background = check.background;
            checked = check.checked;
        }

        public void render(SpriteBatch batch)
        {
            glyphLayout.setText(font, text);
            float bgSize = rect.height * .4f;
            float checkSize = bgSize * 0.9f;
            float totalHeight = bgSize + glyphLayout.height;
            float top = rect.y + totalHeight + (rect.height - totalHeight) * .5f;
            float bottom = rect.y + (rect.height - totalHeight) * .5f - glyphLayout.height * .5f;
            font.draw(batch, glyphLayout, rect.x + rect.width * .5f - glyphLayout.width * .5f, top);
            if(pressed)batch.setShader(Shader.GLOW_SHADER);
            batch.draw(background, rect.x + rect.width * .5f - bgSize * .5f, bottom, bgSize, bgSize);
            if(selected)
            {
                batch.draw(checked, rect.x + rect.width * .5f - checkSize * .5f, bottom + (bgSize - checkSize), checkSize, checkSize);
            }
            batch.setShader(null);
        }
    }
}
