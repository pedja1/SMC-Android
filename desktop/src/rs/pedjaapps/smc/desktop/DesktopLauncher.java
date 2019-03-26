package rs.pedjaapps.smc.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.Array;

import de.golfgl.gdxgamesvcs.MockGameServiceClient;
import de.golfgl.gdxgamesvcs.NoGameServiceClient;
import de.golfgl.gdxgamesvcs.achievement.IAchievement;
import de.golfgl.gdxgamesvcs.leaderboard.ILeaderBoardEntry;
import rs.pedjaapps.smc.MaryoGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        //BasicConfigurator.configure();
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1024;
        config.height = 472;//576//472
        //config.resizable = false;
        //config.allowSoftwareMode = false;
        config.vSyncEnabled = false;
        //config.foregroundFPS = 0;
        //config.backgroundFPS = 0;
        //config.addIcon("data/icons/desktop_mac.png", Files.FileType.Internal);
        //config.addIcon("data/icons/desktop_win_lin.png", Files.FileType.Internal);
        //config.addIcon("data/icons/desktop_win.png", Files.FileType.Internal);
        MaryoGame listener = new MaryoGame(null);
        listener.gpgsClient = new MockGameServiceClient(.8f) {
            @Override
            protected Array<ILeaderBoardEntry> getLeaderboardEntries() {
                return null;
            }

            @Override
            protected Array<String> getGameStates() {
                return null;
            }

            @Override
            protected byte[] getGameState() {
                return new byte[0];
            }

            @Override
            protected Array<IAchievement> getAchievements() {
                return null;
            }

            @Override
            protected String getPlayerName() {
                return "testnamevolllangundso";
            }
        };
        new LwjglApplication(listener, config);
    }
}
