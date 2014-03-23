package rs.papltd.smc.screen;

import android.app.ProgressDialog;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import rs.papltd.smc.*;

/**
 * @author Mats Svensson
 */
public abstract class AbstractScreen implements Screen
{

    protected MaryoGame game;

    public AbstractScreen(MaryoGame game)
	{
        this.game = game;
    }

    @Override
    public void pause()
	{
    }

    @Override
    public void resume()
	{
    }

    @Override
    public void dispose()
	{
    }

    protected void draw(SpriteBatch batch, Texture texture, float x, float y, float width)
    {
        batch.draw(texture, x, y, width, width*texture.getHeight()/ texture.getWidth());
    }

    protected void draw(SpriteBatch batch, TextureRegion region, float x, float y, float width)
    {
        batch.draw(region, x, y, width, width*region.getRegionHeight()/region.getRegionWidth());
    }
	
	public abstract void loadAssets();

    /**
     * Called after after all assets has been loaded, use it to find regions from atlases for example*/
    public abstract void afterLoadAssets();
}
