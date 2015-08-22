package rs.pedjaapps.smc.ga;

import com.badlogic.gdx.Gdx;
import com.brsanthu.googleanalytics.AppViewHit;
import com.brsanthu.googleanalytics.EventHit;
import com.brsanthu.googleanalytics.GoogleAnalytics;

/**
 * Created by pedja on 22.8.15..
 */
public class GA
{
    public static final String APP_VERSION = String.valueOf(Gdx.app.getVersion());
    public static GoogleAnalytics ga = new GoogleAnalytics("UA-35780603-2");
    private static long gameStartTime;

    public static void sendGameStarted()
    {
        gameStartTime = System.currentTimeMillis();
        ga.postAsync(new EventHit("game", "started"));
    }

    public static void sendGameEnded()
    {
        StringBuilder builder = new StringBuilder("ended, time: ");
        builder.append(System.currentTimeMillis() - gameStartTime);
        ga.postAsync(new EventHit("game", builder.toString()));
    }

    public static void sendLevelStarted(String levelName)
    {
        StringBuilder builder = new StringBuilder("Level Started: ");
        builder.append(levelName);
        ga.postAsync(new AppViewHit("Secret Maryo Chronicles", APP_VERSION, builder.toString()));
    }

    public static void sendLevelEnded(String levelName, float time)
    {
        StringBuilder builder = new StringBuilder("Level Started: ");
        builder.append(levelName);
        builder.append(", time: ");
        builder.append(time);
        ga.postAsync(new AppViewHit("Secret Maryo Chronicles", APP_VERSION, builder.toString()));
    }

    public static void dispose()
    {
        gameStartTime = 0;
    }
}
