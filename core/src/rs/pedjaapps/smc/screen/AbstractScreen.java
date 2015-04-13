package rs.pedjaapps.smc.screen;

import com.badlogic.gdx.*;

import rs.pedjaapps.smc.MaryoGame;

/**
 * @author Mats Svensson
 */
public abstract class AbstractScreen implements Screen
{
    public MaryoGame game;

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

    public void exitToMenu()
    {
        game.setScreen(new LoadingScreen(new MainMenuScreen(game), false));
    }

    public void quit()
    {
        game.exit();
    }

    /**
     * Override this method to add assets to loading queue using AssetManager.load()*/
	public abstract void loadAssets();

    /**
     * Called after after all assets has been loaded, use it to find regions from atlases for example*/
    public abstract void onAssetsLoaded();
}
