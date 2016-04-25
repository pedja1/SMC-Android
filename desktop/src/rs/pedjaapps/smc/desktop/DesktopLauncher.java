package rs.pedjaapps.smc.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import org.apache.log4j.BasicConfigurator;

import rs.pedjaapps.smc.MaryoGame;

public class DesktopLauncher
{
    public static void main(String[] arg)
    {
        BasicConfigurator.configure();
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1280, 720);
        config.setResizable(false);
        config.useVsync(true);
        //config.allowSoftwareMode = false;
        //config.vSyncEnabled = false;
        //config.foregroundFPS = 0;
        //config.backgroundFPS = 0;
        //config.addIcon("data/icons/desktop_mac.png", Files.FileType.Internal);
        //config.addIcon("data/icons/desktop_win_lin.png", Files.FileType.Internal);
        //config.addIcon("data/icons/desktop_win.png", Files.FileType.Internal);
        new Lwjgl3Application(new MaryoGame(), config);
    }
}
