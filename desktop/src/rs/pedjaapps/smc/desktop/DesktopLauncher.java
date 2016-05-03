package rs.pedjaapps.smc.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import rs.pedjaapps.smc.MaryoGame;

public class DesktopLauncher
{
    public static void main(String[] arg)
    {
        //BasicConfigurator.configure();
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
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
        new LwjglApplication(new MaryoGame(), config);

        /*Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1024, 576);
        config.setResizable(false);
        config.useVsync(false);
        new Lwjgl3Application(new MaryoGame(), config);*/
    }
}
