package rs.pedjaapps.smc.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.*;

import rs.pedjaapps.smc.Assets;

/**
 * @author Mats Svensson
 */
public class LoadingScreen extends AbstractScreen
{
    private float percent;

    BitmapFont font;
    SpriteBatch batch;
    OrthographicCamera cam;

    private Sprite bgSprite;

    private AbstractScreen screenToLoadAfter;
	private boolean resume = false;

    public LoadingScreen(AbstractScreen screenToLoadAfter, boolean resume)
    {
        super(screenToLoadAfter.game);
        this.screenToLoadAfter = screenToLoadAfter;
		this.resume = resume;
    }

    @Override
    public void show()
    {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        float camWidth = 800;
        float camHeight = height/(width/camWidth);
        cam = new OrthographicCamera(camWidth, camHeight);

        Texture fontTexture = new Texture(Gdx.files.internal("data/fonts/dejavu_sans.png"));
        fontTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font = new BitmapFont(Gdx.files.internal("data/fonts/dejavu_sans.fnt"), new TextureRegion(fontTexture), false);
        font.setColor(Color.WHITE);
        font.setScale(0.25f);
        batch = new SpriteBatch();
        Texture bgTexture = new Texture(Gdx.files.internal("data/loading/loading_bg.jpg"));
        bgTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        bgSprite = new Sprite(bgTexture);
        bgSprite.setSize(camWidth, bgSprite.getHeight() / (bgSprite.getWidth() / camWidth));
        bgSprite.setOrigin(bgSprite.getWidth()/2, bgSprite.getHeight()/2);
        bgSprite.setPosition(-bgSprite.getWidth()/2, -camHeight/2);
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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Assets.manager.update())
        {
            // Load some, will return true if done loading
            if(!resume)screenToLoadAfter.onAssetsLoaded();
            game.setScreen(screenToLoadAfter);
        }
        // Interpolate the percentage to make it more smooth
        percent = Interpolation.linear.apply(percent, Assets.manager.getProgress(), 0.1f);

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        bgSprite.draw(batch);

        font.drawMultiLine(batch, "Loading, please wait... " + (int) (percent * 100) + "%", -cam.viewportWidth/2+(0.07f * cam.viewportWidth), -cam.viewportHeight/2+(0.08f * cam.viewportHeight), 0, BitmapFont.HAlignment.LEFT);

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
    public void onAssetsLoaded()
    {
        //do nothing
    }

    @Override
    public void dispose()
    {
        font.dispose();
        batch.dispose();
        bgSprite.getTexture().dispose();
        //empty.getTexture().dispose();
        //full.getTexture().dispose();
    }
}
