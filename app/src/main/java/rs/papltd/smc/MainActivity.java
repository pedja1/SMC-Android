package rs.papltd.smc;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

/**
 * Created by pedja on 2/27/14.
 */
public class MainActivity extends AndroidApplication
{
    MaryoGame game;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useWakelock = true;
        //config.useGL20 = true;
        game = new MaryoGame();
        initialize(game, config);
    }

    @Override
    public void onBackPressed()
    {
        if(game.onBackPressed())
        {
            super.onBackPressed();
        }
    }
}
