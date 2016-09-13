package rs.pedjaapps.smc;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.kryo.Data;
import rs.pedjaapps.smc.kryo.FindOpponent;
import rs.pedjaapps.smc.kryo.KryoClassRegistar;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.screen.AbstractScreen;
import rs.pedjaapps.smc.screen.LoadingScreen;
import rs.pedjaapps.smc.screen.MainMenuScreen;
import rs.pedjaapps.smc.screen.SplashScreen;
import rs.pedjaapps.smc.shader.Shader;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.PrefsManager;

public class MaryoGame extends Game implements Runnable
{
	public Assets assets;
	private Event event;

	private List<ConnectionListener> listeners;
	private Client mServerConnection;
	private static final FindOpponent FIND_OPPONENT = new FindOpponent();
	private static final Data DATA = new Data();

	private boolean connecting;

	public MaryoGame(Event event)
	{
		this.event = event;

		listeners = new ArrayList<>();

		mServerConnection = new Client();
		KryoClassRegistar.registerClasses(mServerConnection.getKryo());
		mServerConnection.start();
	}

	@Override
	public void create()
	{
		assets = new Assets();
		Shader.init();
		GameSave.init();
		setScreen(new SplashScreen(this));
	}

	@Override
	public void resume()
	{
		Screen currentScreen = getScreen();
		if(currentScreen instanceof SplashScreen)return;
		Texture.setAssetManager(assets.manager);
		setScreen(new LoadingScreen((AbstractScreen)currentScreen, true, false));
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
		GameSave.dispose();
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
		assets = new Assets();
		setScreen(new LoadingScreen(new MainMenuScreen(this), false, false));
	}

	public static boolean showOnScreenControls()
	{
		return Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS;
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

	@Override
	public void run()
	{
		try
		{
			mServerConnection.connect(5000, "localhost", 50591, 50592);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			for(ConnectionListener listener : listeners)
			{
				listener.connectionFailed();
			}
		}
		connecting = false;
	}

	public void connectToServer()
	{
		if(connecting)
			return;
		Thread thread = new Thread(this);
		thread.start();
		connecting = true;
	}

	public void addConnectionListener(ConnectionListener listener)
	{
		listeners.add(listener);
		mServerConnection.addListener(listener);
	}

	public void removeConnectionListener(ConnectionListener listener)
	{
		listeners.remove(listener);
		mServerConnection.addListener(listener);
	}

	public void findOpponent()
	{
		mServerConnection.sendTCP(FIND_OPPONENT);
	}

	public void sendLocation(Maryo maryo)
	{
		DATA.facingLeft = maryo.facingLeft;
		DATA.maryoState = maryo.getMarioState();
		DATA.worldState = maryo.getWorldState();
		DATA.posX = maryo.position.x;
		DATA.posY = maryo.position.y;
		mServerConnection.sendUDP(DATA);
	}

	public interface Event
	{
		void showInterestitialAd();
		void levelStart(String levelName);
		void levelEnd(String levelName, boolean success);
	}

	public static class ConnectionListener extends Listener
	{
		@Override
		public void received(Connection connection, Object object)
		{

		}

		@Override
		public void connected(Connection connection)
		{

		}

		@Override
		public void disconnected(Connection connection)
		{

		}

		public void connectionFailed()
		{

		}
	}
}
