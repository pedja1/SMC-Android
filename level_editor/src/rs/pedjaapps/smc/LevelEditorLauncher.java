package rs.pedjaapps.smc;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class LevelEditorLauncher {
    public static void main(String[] arg) {

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1280;
        config.height = 720;
        config.resizable = (false);
        config.vSyncEnabled = true;
        new LwjglApplication(new LevelEditor(arg), config);
    }
}