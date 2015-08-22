package rs.pedjaapps.smc.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import org.apache.log4j.BasicConfigurator;

import rs.pedjaapps.smc.MaryoGame;

public class DesktopLauncher
{
    public static void main(String[] arg)
    {
        BasicConfigurator.configure();
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1024;
        config.height = 576;
        config.resizable = false;
        config.allowSoftwareMode = false;
        //config.vSyncEnabled = false;
        //config.foregroundFPS = 0;
        //config.backgroundFPS = 0;
        config.addIcon("data/game/icons/desktop_mac.png", Files.FileType.Internal);
        config.addIcon("data/game/icons/desktop_win_lin.png", Files.FileType.Internal);
        config.addIcon("data/game/icons/desktop_win.png", Files.FileType.Internal);
        new LwjglApplication(new MaryoGame(), config);
    }
}
