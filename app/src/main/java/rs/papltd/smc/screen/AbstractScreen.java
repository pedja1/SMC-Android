package rs.papltd.smc.screen;

import android.app.ProgressDialog;

import com.badlogic.gdx.*;
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
	
	public abstract void loadAssets();

    /**
     * Called after after all assets has been loaded, use it to find regions from atlases for example*/
    public abstract void afterLoadAssets();
}
