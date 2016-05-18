package rs.pedjaapps.smc.android;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LevelEndEvent;
import com.crashlytics.android.answers.LevelStartEvent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;

import io.fabric.sdk.android.Fabric;
import rs.pedjaapps.smc.MaryoGame;

/**
 * Created by pedja on 2/27/14.
 */
public class AndroidLauncher extends AndroidApplication implements MaryoGame.Event
{
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Fabric.with(this, new Crashlytics());
        super.onCreate(savedInstanceState);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6294976772687752/6322846426");
        requestNewInterstitial();
        mInterstitialAd.setAdListener(new AdListener()
        {
            @Override
            public void onAdClosed()
            {
                requestNewInterstitial();
            }

            @Override
            public void onAdFailedToLoad(int errorCode)
            {
                requestNewInterstitial();
            }
        });

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useWakelock = true;
        config.hideStatusBar = true;
        config.useImmersiveMode = true;
        config.useGLSurfaceView20API18 = true;
        //config.useGL20 = true;

        String dir = "/Android/obb/" + getPackageName();
        String obbName = "main." + getVersionCode() + "." + getPackageName() + ".obb";
        String androidZipPath = new File(dir, obbName).getAbsolutePath();
        MaryoGame game = new MaryoGame(androidZipPath, this);
        initialize(game, config);
    }

    private int getVersionCode()
    {
        try
        {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            throw new IllegalStateException("huston we have a problem");
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void showInterestitialAd()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if(mInterstitialAd.isLoaded())
                {
                    mInterstitialAd.show();
                }
            }
        });
    }

    @Override
    public void levelStart(String levelName)
    {
        Answers.getInstance().logLevelStart(new LevelStartEvent()
                .putLevelName(levelName));
    }

    @Override
    public void levelEnd(String levelName, boolean success)
    {
        Answers.getInstance().logLevelEnd(new LevelEndEvent()
                .putLevelName(levelName)
                .putSuccess(success));
    }

    private void requestNewInterstitial()
    {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}
