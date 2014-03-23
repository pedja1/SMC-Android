package rs.papltd.smc;

import android.app.Application;
import android.content.Context;

/**
 * Created by pedja on 23.3.14..
 */
public class MainApp extends Application
{
    private static Context context;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext()
    {
        return context;
    }
}
