package rs.pedjaapps.smc.android;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.badlogic.gdx.Input;
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

        game.isRunningOn = Build.MODEL;

        input.addKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == Input.Keys.MEDIA_FAST_FORWARD) {
                    input.onKey(v, Input.Keys.SPACE, event);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void levelStart(String levelName) {
    }

    @Override
    public void levelEnd(String levelName, boolean success) {
    }
}
