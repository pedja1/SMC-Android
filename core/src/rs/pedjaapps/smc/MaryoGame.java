package rs.pedjaapps.smc;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.golfgl.gdxgamesvcs.IGameServiceClient;
import de.golfgl.gdxgamesvcs.IGameServiceListener;
import de.golfgl.gdxgamesvcs.NoGameServiceClient;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.screen.LoadingScreen;
import rs.pedjaapps.smc.screen.MainMenuScreen;
import rs.pedjaapps.smc.shader.Shader;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.MyControllerMapping;
import rs.pedjaapps.smc.utility.PrefsManager;

public class MaryoGame extends Game implements IGameServiceListener {
	public static final int NATIVE_WIDTH = 1024;
	public static final int NATIVE_HEIGHT = 576;

	public static final String GAME_VERSION = "2.1.1710";
    public static final boolean GAME_DEVMODE = true;
	public static final String GAME_STOREURL = "https://play.google.com/store/apps/details?id=de.golfgl.smc.android";
	public static final String GAME_WEBURL = "https://www.golfgl.de/sccplf/";

	public static final String GAME_SOURCEURL = "https://www.golfgl.de/sccplf/sccsrc.zip";
	public static final boolean GAME_DEVMODE = true;


	public MyControllerMapping controllerMappings;
	public String isRunningOn = "";
	public IGameServiceClient gsClient;
	public IGameServiceClient gpgsClient;
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

		try {
			controllerMappings = new MyControllerMapping();
			Controllers.addListener(controllerMappings.controllerToInputAdapter);
		} catch (Throwable t) {
			Gdx.app.error("Application", "Controllers not instantiated", t);
		}

		if (gsClient == null)
			gsClient = new NoGameServiceClient();
		gsClient.resumeSession();

		if (gpgsClient != null) {
			gpgsClient.setListener(this);
			gpgsClient.resumeSession();
		}
	}

	@Override
	public void pause()	{
		super.pause();
		// kann null sein wenn preloader versteckt wird
		if (Gdx.app != null) {
			PrefsManager.flush();
			gsClient.pauseSession();
			if (gpgsClient != null)
				gpgsClient.pauseSession();
		}
	}

	@Override
	public void resume() {
		super.resume();

		if (gsClient != null)
			gsClient.resumeSession();
		if (gpgsClient != null)
			gpgsClient.resumeSession();
	}

	@Override
    public void dispose()
    {
        super.dispose();
        assets.dispose();
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

		gsClient.submitEvent(GameSave.EVENT_LEVEL_STARTED, 1);
		if (gpgsClient != null)
			gpgsClient.submitEvent(GameSave.EVENT_LEVEL_STARTED, 1);
	}

	public void levelEnd(String levelName, boolean success)
	{
		if(event != null)
			event.levelEnd(levelName, success);

		if (success) {
			gsClient.submitEvent(GameSave.EVENT_LEVEL_CLEARED, 1);
			gsClient.submitToLeaderboard(GameSave.LEADERBOARD_TOTAL, GameSave.getTotalScore(), null);
			if (gpgsClient != null) {
				gpgsClient.unlockAchievement(levelName + "_CLEAR");
				gpgsClient.submitEvent(GameSave.EVENT_LEVEL_CLEARED, 1);
				gpgsClient.submitToLeaderboard(GameSave.LEADERBOARD_TOTAL, GameSave.getTotalScore(), null);
			}
		}
	}

	@Override
	public void gsOnSessionActive() {

	}

	@Override
	public void gsOnSessionInactive() {

	}

	@Override
	public void gsShowErrorToUser(GsErrorType et, String msg, Throwable t) {
		// GPGS Error auf aktuellem Bildschirm oder in Log anzeigen
		Gdx.app.error("GPGS", msg);
	}

	public interface Event
	{
		void showInterestitialAd();
		void levelStart(String levelName);
		void levelEnd(String levelName, boolean success);
	}
}
