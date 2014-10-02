package rs.pedjaapps.smc.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import android.view.View;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.utility.GameSaveUtility;
import rs.pedjaapps.smc.model.Maryo;

/**
 * Created by pedja on 2/27/14.
 */
public class AndroidLauncher extends AndroidApplication
{
    MaryoGame game;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		/*View decorView = getWindow().getDecorView(); 
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		decorView.setSystemUiVisibility(uiOptions);
*/
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useWakelock = true;
		config.hideStatusBar = true;
		config.useImmersiveMode = true;
        //config.useGL20 = true;
		
		game = new MaryoGame();
        initialize(game, config);
		
		GameSaveUtility.getInstance(); //initialize save game
		//debug
		System.out.println(GameSaveUtility.getInstance().save.toString());
		//debug end
    }

    @Override
    public void onBackPressed()
    {
        if(game.onBackPressed())
        {
            super.onBackPressed();
        }
    }

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//debug
		GameSaveUtility.getInstance().save.currentLevel = 5;
		GameSaveUtility.getInstance().save.playerState = Maryo.MarioState.big;
		//debug end
		GameSaveUtility.getInstance().dispose();
	}
}
