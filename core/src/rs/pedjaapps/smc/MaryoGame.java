package rs.pedjaapps.smc;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

import rs.pedjaapps.smc.ga.GA;
import rs.pedjaapps.smc.screen.AbstractScreen;
import rs.pedjaapps.smc.screen.LoadingScreen;
import rs.pedjaapps.smc.screen.SplashScreen;
import rs.pedjaapps.smc.utility.GameSaveUtility;
import rs.pedjaapps.smc.utility.PrefsManager;

public class MaryoGame extends Game
{


    @Override
	public void create()
	{
		setScreen(new SplashScreen(this));
		GA.sendGameStarted();
	}

	@Override
	public void resume()
	{
		Screen currentScreen = getScreen();
		if(currentScreen instanceof SplashScreen)return;
		Texture.setAssetManager(Assets.manager);
		setScreen(new LoadingScreen((AbstractScreen)currentScreen, true));
	}

	@Override
	public void pause()
	{
		PrefsManager.flush();
	}

    @Override
    public void dispose()
    {
        super.dispose();
        Assets.dispose();
		GA.sendGameEnded();
		GA.dispose();
		GameSaveUtility.getInstance().dispose();

    }

    public void exit()
    {
        Gdx.app.exit();
    }

	public static boolean showOnScreenControls()
	{
		return Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS;
	}
}
