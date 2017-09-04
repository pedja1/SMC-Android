package rs.pedjaapps.smc.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import rs.pedjaapps.smc.MaryoGame;

/**
 * Created by pedja on 2/27/14.
 */
public class AndroidLauncher extends AndroidApplication implements MaryoGame.Event {
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6294976772687752/6322846426");
        requestNewInterstitial();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
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

        MaryoGame game = new MaryoGame(this);
        initialize(game, config);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showInterestitialAd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
        });
    }

    @Override
    public void levelStart(String levelName) {
    }

    @Override
    public void levelEnd(String levelName, boolean success) {
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}
