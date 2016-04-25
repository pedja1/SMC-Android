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
    private static final boolean GA_ENABLED = false;
    public static final String APP_VERSION = String.valueOf(Gdx.app.getVersion());
    public static GoogleAnalytics ga = new GoogleAnalytics("UA-35780603-2");
    private static long gameStartTime;

    public static void sendGameStarted()
    {
        if(!GA_ENABLED)
            return;
        gameStartTime = System.currentTimeMillis();
        ga.postAsync(new EventHit("game", "started"));
    }

    public static void sendGameEnded()
    {
        if(!GA_ENABLED)
            return;
        StringBuilder builder = new StringBuilder("ended, time: ");
        builder.append(System.currentTimeMillis() - gameStartTime);
        ga.postAsync(new EventHit("game", builder.toString()));
    }

    public static void sendLevelStarted()
    {
        if(!GA_ENABLED)
            return;
        StringBuilder builder = new StringBuilder("Level Started: ");
        ga.postAsync(new AppViewHit("Secret Maryo Chronicles", APP_VERSION, builder.toString()));
    }

    public static void sendLevelEnded(float time)
    {
        if(!GA_ENABLED)
            return;
        StringBuilder builder = new StringBuilder("Level Started: ");
        builder.append(", time: ");
        builder.append(time);
        ga.postAsync(new AppViewHit("Secret Maryo Chronicles", APP_VERSION, builder.toString()));
    }

    public static void dispose()
    {
        gameStartTime = 0;
    }
}
