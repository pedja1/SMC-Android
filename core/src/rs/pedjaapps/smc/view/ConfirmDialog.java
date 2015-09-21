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

import rs.pedjaapps.smc.screen.AbstractScreen;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.screen.MainMenuScreen;
import rs.pedjaapps.smc.shader.Shader;

/**
 * Created by pedja on 11.10.14..
 */
public class ConfirmDialog
{
    OrthographicCamera cam;

    AbstractScreen screen;

    ShapeRenderer shapeRenderer = new ShapeRenderer();

    public boolean visible = false;

    Texture back;

    Button buttonNo, buttonYes;

    float dialogX, dialogY;
    float dialogWidth, dialogHeight;

    BitmapFont font;
    GlyphLayout fontGlyph;

    String title = "Quit?";
    String text;

    Rectangle titleR, textR, contentRect;

    public ConfirmDialog(AbstractScreen screen, OrthographicCamera cam)
    {
        this.screen = screen;
        this.cam = cam;

        setupDialogBounds();

        text = (screen instanceof GameScreen)
                ? "Are you sure you want to quit?\nAll unsaved progress will be lost."
                : "Do you really want to leave?";
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
        screen.game.assets.manager.load("data/hud/hud.pack", TextureAtlas.class);

		screen.game.assets.manager.load("data/hud/dialog_background.png", Texture.class, screen.game.assets.textureParameter);
        FreetypeFontLoader.FreeTypeFontLoaderParameter params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        params.fontFileName = "data/fonts/GROBOLD.ttf";//Constants.DEFAULT_FONT_FILE_NAME;
        params.fontParameters.magFilter = Texture.TextureFilter.Linear;
        params.fontParameters.minFilter = Texture.TextureFilter.Linear;
        params.fontParameters.size = (int) (cam.viewportHeight / 23);
        params.fontParameters.characters = "YesNoOESAryuwantqi?lvdpgb.DQ";
        params.fontParameters.borderWidth = 2f;
        screen.game.assets.manager.load("confirm_dialog.ttf", BitmapFont.class, params);
    }

    public void initAssets()
    {
        TextureAtlas atlas = screen.game.assets.manager.get("data/hud/hud.pack");
        back = screen.game.assets.manager.get("data/hud/dialog_background.png");
        
		TextureRegion btnCancel = atlas.findRegion("cancel");
		TextureRegion btnAccept = atlas.findRegion("accept");
		
		font = screen.game.assets.manager.get("confirm_dialog.ttf");
        fontGlyph = new GlyphLayout();

        buttonNo = new Button();
        buttonNo.rect.width = dialogWidth * .15f;
        buttonNo.rect.height = buttonNo.rect.width;
        buttonNo.rect.x = dialogX + dialogWidth * .35f - buttonNo.rect.width * .5f;
        buttonNo.rect.y = dialogY - buttonNo.rect.height * .5f;
        buttonNo.texture = btnCancel;

        buttonYes = new Button(buttonNo);
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
                centerVer + fontGlyph.height * .5f,
                fontGlyph.width, fontGlyph.height);

    }

    public void show()
    {
        visible = true;
        if(screen instanceof GameScreen)((GameScreen)screen).setGameState(GameScreen.GAME_STATE.GAME_PAUSED);
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
            batch.end();

            /*shapeRenderer.setProjectionMatrix(cam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 0, 0, 1);
            shapeRenderer.rect(dialogX, dialogY, dialogWidth, dialogHeight);
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

    public static class Button
    {
        Rectangle rect = new Rectangle();
        TextureRegion texture;

        boolean pressed;

        public void render(SpriteBatch batch)
        {
            if(pressed)batch.setShader(Shader.GLOW_SHADER);
            batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
            batch.setShader(null);
        }

        public Button(Button button)
        {
            rect = new Rectangle(button.rect);
            texture = button.texture;
        }

        public Button()
        {
            
        }
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
    }

    public void touchUp(float x, float y)
    {
        if(buttonNo.rect.contains(x, y))
        {
            visible = false;
        }
        else if(buttonYes.rect.contains(x, y))
        {
            if(screen instanceof GameScreen) screen.exitToMenu();
            else if(screen instanceof MainMenuScreen) screen.quit();
        }
    }

    public void touchDragged(float x, float y)
    {
        buttonNo.pressed = buttonNo.rect.contains(x, y);
        buttonYes.pressed = buttonYes.rect.contains(x, y);
    }
}
