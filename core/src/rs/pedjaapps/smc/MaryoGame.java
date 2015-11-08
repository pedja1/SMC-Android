package rs.pedjaapps.smc;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.ga.GA;
import rs.pedjaapps.smc.screen.AbstractScreen;
import rs.pedjaapps.smc.screen.LoadingScreen;
import rs.pedjaapps.smc.screen.MainMenuScreen;
import rs.pedjaapps.smc.screen.SplashScreen;
import rs.pedjaapps.smc.shader.Shader;
import rs.pedjaapps.smc.utility.GameSaveUtility;
import rs.pedjaapps.smc.utility.PrefsManager;

public class MaryoGame extends Game
{
	public Assets assets;
	private String androidAssetsZipFilePath;

	public MaryoGame(String androidAssetsZipFilePath)
	{
		this.androidAssetsZipFilePath = androidAssetsZipFilePath;
	}

	public MaryoGame()
	{
	}

	@Override
	public void create()
	{
		assets = new Assets(androidAssetsZipFilePath);
		Shader.init();
		setScreen(new SplashScreen(this));
		GA.sendGameStarted();
	}

	@Override
	public void resume()
	{
		Screen currentScreen = getScreen();
		if(currentScreen instanceof SplashScreen)return;
		Texture.setAssetManager(assets.manager);
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
        assets.dispose();
		GA.sendGameEnded();
		GA.dispose();
		GameSaveUtility.getInstance().dispose();
		assets = null;
		Shader.dispose();
    }

    public void exit()
    {
        Gdx.app.exit();
    }

	public void restart()
	{
		assets.dispose();
		assets = null;
		assets = new Assets(androidAssetsZipFilePath);
		setScreen(new LoadingScreen(new MainMenuScreen(this), false));
	}

	public static boolean showOnScreenControls()
	{
		return Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS;
	}
}
