package rs.pedjaapps.smc.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import rs.pedjaapps.smc.MaryoGame;

public class DesktopLauncher
{
    public static void main(String[] arg)
    {
        //BasicConfigurator.configure();
        /*LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1024;
        config.height = 576;
        config.resizable = false;
        config.allowSoftwareMode = false;
        //config.vSyncEnabled = false;
        //config.foregroundFPS = 0;
        //config.backgroundFPS = 0;
        //config.addIcon("data/icons/desktop_mac.png", Files.FileType.Internal);
        //config.addIcon("data/icons/desktop_win_lin.png", Files.FileType.Internal);
        //config.addIcon("data/icons/desktop_win.png", Files.FileType.Internal);
        new LwjglApplication(new MaryoGame(), config);*/

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(MaryoGame.NATIVE_WIDTH, MaryoGame.NATIVE_HEIGHT);
        //config.setResizable(false);
        config.useVsync(false);
        new Lwjgl3Application(new MaryoGame(null), config);
    }
}
