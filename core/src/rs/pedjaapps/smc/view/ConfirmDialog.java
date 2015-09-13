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
import rs.pedjaapps.smc.utility.Constants;

/**
 * Created by pedja on 11.10.14..
 */
public class ConfirmDialog
{
    OrthographicCamera cam;

    AbstractScreen screen;

    ShapeRenderer shapeRenderer = new ShapeRenderer();

    public boolean visible = false;

    TextureRegion back;

    Button buttonNo, buttonYes;

    float dialogX, dialogY;
    float dialogWidth, dialogHeight;
    float dialogPadding;

    BitmapFont font;
    GlyphLayout fontGlyph;

    String title = "Quit?";
    String text;

    Rectangle titleR, textR;

    public ConfirmDialog(AbstractScreen screen, OrthographicCamera cam)
    {
        this.screen = screen;
        this.cam = cam;

        dialogHeight = cam.viewportHeight / 2;
        dialogWidth = cam.viewportWidth / 2;

        dialogPadding = dialogHeight / 15;

        dialogX = cam.position.x - dialogWidth / 2;
        dialogY = cam.position.y - dialogHeight / 2;

        text = (screen instanceof GameScreen)
                ? "Are you sure you want to quit?\nAll unsaved progress will be lost."
                : "Do you really want to leave?";
    }

    public void loadAssets()
    {
        screen.game.assets.manager.load("data/hud/SMCLook512.pack", TextureAtlas.class);

        FreetypeFontLoader.FreeTypeFontLoaderParameter params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        params.fontFileName = Constants.DEFAULT_FONT_FILE_NAME;
        params.fontParameters.magFilter = Texture.TextureFilter.Linear;
        params.fontParameters.minFilter = Texture.TextureFilter.Linear;
        params.fontParameters.size = (int) (cam.viewportHeight / 20);
        params.fontParameters.characters = "YesNoOESAryuwantqi?lvdpgb.DQ";
        screen.game.assets.manager.load("confirm_dialog.ttf", BitmapFont.class, params);
    }

    public void initAssets()
    {
        TextureAtlas atlas = screen.game.assets.manager.get("data/hud/SMCLook512.pack");
        back = atlas.findRegion("ClientBrush");
        TextureRegion buttonNormal = atlas.findRegion("button-normal");
        TextureRegion buttonPressed = atlas.findRegion("button-pressed");

        font = screen.game.assets.manager.get("confirm_dialog.ttf");
        fontGlyph = new GlyphLayout();

        buttonNo = new Button();
        buttonNo.rect.width = dialogWidth / 4;
        buttonNo.rect.height = dialogHeight / 6;
        buttonNo.rect.x = dialogX + dialogWidth - dialogPadding - buttonNo.rect.width;
        buttonNo.rect.y = dialogY + dialogPadding;
        buttonNo.bgPressed = buttonPressed;
        buttonNo.bgNormal = buttonNormal;
        buttonNo.text = "NO";
        buttonNo.font = font;

        buttonYes = new Button(buttonNo);
        buttonYes.rect.x = buttonNo.rect.x - dialogPadding - buttonNo.rect.width;
        buttonYes.text = "YES";

        fontGlyph.setText(font, title);
        titleR = new Rectangle(dialogX + dialogWidth / 2 - fontGlyph.width / 2,
                dialogY + dialogHeight - dialogPadding,
                fontGlyph.width, fontGlyph.height);

        fontGlyph.setText(font, text, font.getColor(), dialogWidth - dialogPadding * 2, Align.center, true);
        //bounds = font.getWrappedBounds(text, dialogWidth - dialogPadding * 2);
        float top = titleR.y - titleR.height - dialogPadding;
        float bottom = buttonNo.rect.y + buttonNo.rect.height + dialogPadding;
        float centerVer = top - ((top - bottom) / 2);
        textR = new Rectangle(dialogX + dialogWidth / 2 - fontGlyph.width / 2,
                centerVer + fontGlyph.height / 2,
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
            shapeRenderer.rect(buttonNo.x, buttonNo.y, buttonNo.width, buttonNo.height);
            shapeRenderer.end();*/
        }
    }

    public void resize()
    {
        dialogHeight = cam.viewportHeight / 2;
        dialogWidth = cam.viewportWidth / 2;
    }

    public void dispose()
    {
    }

    /*class ExitDialog extends Dialog
    {
        float dialogWidth = cam.viewportWidth / 2;
        float dialogHeight = cam.viewportHeight / 2;
        private static final int BUTTON_COUNT = 2;
        public ExitDialog(String title, Skin skin)
        {
            super(title, skin);
        }

        public ExitDialog(String title, Skin skin, String windowStyleName)
        {
            super(title, skin, windowStyleName);
        }

        public ExitDialog(String title, WindowStyle windowStyle)
        {
            super(title, windowStyle);
        }

        {
            setMovable(false);
            setResizable(false);
            padLeft(dialogWidth / 10);
            padRight(dialogWidth / 10);
            padBottom(dialogHeight / 10);
            getButtonTable().defaults().height(dialogHeight / 5).width((dialogWidth * 0.95f) / BUTTON_COUNT);
            if (screen instanceof GameScreen)
            {
                text("Are you sure you want to quit?\nAll unsaved progress will be lost.");
            }
            else
            {
                text("Do you really want to leave?");
            }
            button("YES", true);
            button("NO", false);
        }

        @Override
        public Dialog text(String text)
        {
            super.text(new Label(text, skin));
            return this;
        }

        @Override
        public float getPrefWidth()
        {
            return dialogWidth;
        }

        @Override
        public float getPrefHeight()
        {
            return dialogHeight;
        }

        @Override
        protected void result(Object object)
        {
            ConfirmDialog.this.hide();
            if(((Boolean)object))
            {
                if(screen instanceof GameScreen) screen.exitToMenu();
                else if(screen instanceof MainMenuScreen) screen.quit();
            }
        }
    }*/

    private static class Button
    {
        Rectangle rect = new Rectangle();
        TextureRegion bgPressed, bgNormal;
        String text;
        BitmapFont font;
        GlyphLayout fontGlyph;

        boolean pressed;

        public void render(SpriteBatch batch)
        {
            batch.draw(pressed ? bgPressed : bgNormal, rect.x, rect.y, rect.width, rect.height);
            font.setColor(0.368627451f, 0.141176471f, 0.050980392f, 1);
            fontGlyph.setText(font, text);
            font.draw(batch, text, rect.x + rect.width / 2 - fontGlyph.width / 2, rect.y + rect.height / 2 + fontGlyph.height / 2);
        }

        public Button(Button button)
        {
            rect = new Rectangle(button.rect);
            bgNormal = button.bgNormal;
            bgPressed = button.bgPressed;
            text = button.text;
            font = button.font;
            fontGlyph = new GlyphLayout();
        }

        public Button()
        {
            fontGlyph = new GlyphLayout();
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
