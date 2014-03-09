package rs.papltd.smc.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;

import rs.papltd.smc.*;
import rs.papltd.smc.model.*;
import rs.papltd.smc.shader.DistanceFieldShader;

/**
 * @author Mats Svensson
 */
public class LoadingScreen extends AbstractScreen
{
    private float percent;

    BitmapFont font;
    SpriteBatch batch;
    NinePatch empty;
    NinePatch full;
    OrthographicCamera cam;
    //DistanceFieldShader fontShader;

    private AbstractScreen screenToLoadAfter;

    public LoadingScreen(AbstractScreen screenToLoadAfter)
    {
        super(screenToLoadAfter.game);
        this.screenToLoadAfter = screenToLoadAfter;
    }

    @Override
    public void show()
    {
        cam = new OrthographicCamera(800, 480);
        cam.position.set(new Vector2(400, 240), 0);
        cam.update();

        //fontShader = new DistanceFieldShader();
        //fontShader.setSmoothing(0.125f);

        Texture fontTexture = new Texture(Gdx.files.absolute(Assets.mountedObbPath + "/fonts/dejavu_sans.png"));
        fontTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font = new BitmapFont(Gdx.files.absolute(Assets.mountedObbPath + "/fonts/dejavu_sans.fnt"), new TextureRegion(fontTexture), false);
        font.setColor(Color.WHITE);
        font.setScale(0.25f);
        batch = new SpriteBatch();
        empty = new NinePatch(new TextureRegion(new Texture(Gdx.files.absolute(Assets.mountedObbPath + "/loading/empty.png")), 24, 24), 8, 8, 8, 8);
        full = new NinePatch(new TextureRegion(new Texture(Gdx.files.absolute(Assets.mountedObbPath + "/loading/full.png")), 24, 24), 8, 8, 8, 8);
        screenToLoadAfter.loadAssets();
    }

    @Override
    public void resize(int width, int height)
    {

    }

    @Override
    public void render(float delta)
    {
        // Clear the screen
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        if (Assets.manager.update())
        {
            // Load some, will return true if done loading
            screenToLoadAfter.afterLoadAssets();
            System.out.println("LS : start screen()");
            game.setScreen(screenToLoadAfter);
        }
        // Interpolate the percentage to make it more smooth
        percent = Interpolation.linear.apply(percent, Assets.manager.getProgress(), 0.1f);

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        empty.draw(batch, 140, 228, 520, 24);
        full.draw(batch, 140, 228, percent * 520, 24);

        //batch.setShader(fontShader);
        font.drawMultiLine(batch, (int) (percent * 100) + "% loaded", 400, 247 - 8 * 0.25f, 0, BitmapFont.HAlignment.CENTER);
        //batch.setShader(null);

        batch.end();
    }

    @Override
    public void hide()
    {
        // Dispose the loading assets as we no longer need them
        //atlas.dispose();
        //stage.dispose();
    }

    @Override
    public void loadAssets()
    {
        //do nothing
    }

    @Override
    public void afterLoadAssets()
    {
        //do nothing
    }

    @Override
    public void dispose()
    {
        font.dispose();
        batch.dispose();
        empty.getTexture().dispose();
        full.getTexture().dispose();
    }
}
