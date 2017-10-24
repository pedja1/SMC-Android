package rs.pedjaapps.smc;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.screen.LoadingScreen;
import rs.pedjaapps.smc.screen.MainMenuScreen;
import rs.pedjaapps.smc.shader.Shader;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.PrefsManager;

public class MaryoGame extends Game
{
	public static final int NATIVE_WIDTH = 1024;
	public static final int NATIVE_HEIGHT = 576;

	public static final String GAME_VERSION = "1.1.1710";
    public static final boolean GAME_DEVMODE = true;
	public static final String GAME_STOREURL = "https://play.google.com/store/apps/details?id=de.golfgl.smc.android";
	public static final String GAME_WEBURL = "https://www.golfgl.de/smcplf/";
	public static final String GAME_SOURCEURL = "https://www.golfgl.de/smcplf/smcsrc.zip";

    public Assets assets;
	private Event event;

	public MaryoGame(Event event)
	{
		this.event = event;
	}

	@Override
	public void create()
	{
        if (!GAME_DEVMODE)
            Gdx.app.setLogLevel(Application.LOG_ERROR);

		assets = new Assets();
		Shader.init();
		GameSave.init();
        assets.manager.load(Assets.SKIN_HUD, Skin.class);
		setScreen(new LoadingScreen(new MainMenuScreen(this), false));
	}

	@Override
	public void pause()	{
		super.pause();
		PrefsManager.flush();
	}

    @Override
    public void dispose()
    {
        super.dispose();
        assets.dispose();
		GameSave.dispose();
		assets = null;
		Shader.dispose();
    }

    public void exit()
    {
        Gdx.app.exit();
    }

	public void showAd()
	{
		if(event != null)
			event.showInterestitialAd();
	}

	public void levelStart(String levelName)
	{
		if(event != null)
			event.levelStart(levelName);
	}

	public void levelEnd(String levelName, boolean success)
	{
		if(event != null)
			event.levelEnd(levelName, success);
	}

	public interface Event
	{
		void showInterestitialAd();
		void levelStart(String levelName);
		void levelEnd(String levelName, boolean success);
	}
}
