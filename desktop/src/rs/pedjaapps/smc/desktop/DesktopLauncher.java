package rs.pedjaapps.smc.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import rs.pedjaapps.smc.MaryoGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 640;
        config.height = 360;
		new LwjglApplication(new MaryoGame(), config);
	}
}
