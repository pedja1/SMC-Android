package rs.pedjaapps.smc.android;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;

import io.fabric.sdk.android.Fabric;
import rs.pedjaapps.smc.MaryoGame;

/**
 * Created by pedja on 2/27/14.
 */
public class AndroidLauncher extends AndroidApplication implements MaryoGame.AdLoader
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
                    requestNewInterstitial();
                }
            }
        });
    }

    private void requestNewInterstitial()
    {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}
