package rs.pedjaapps.smc.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import rs.pedjaapps.smc.MaryoGame;

/**
 * Created by pedja on 2/27/14.
 */
public class AndroidLauncher extends AndroidApplication implements MaryoGame.Event {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            }
        });
    }

    @Override
    public void levelStart(String levelName) {
    }

    @Override
    public void levelEnd(String levelName, boolean success) {
    }
}
