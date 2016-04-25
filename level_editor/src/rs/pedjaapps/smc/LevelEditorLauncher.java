package rs.pedjaapps.smc;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import org.apache.log4j.BasicConfigurator;

public class LevelEditorLauncher
{
    public static void main(String[] arg)
    {
        BasicConfigurator.configure();

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1280, 720);
        config.setResizable(false);
        config.useVsync(true);
        new Lwjgl3Application(new LevelEditor(arg), config);
    }
}
