package rs.pedjaapps.smc.android;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.io.File;

import rs.pedjaapps.smc.MaryoGame;

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
		config.useGLSurfaceView20API18 = true;
        //config.useGL20 = true;

		String dir = "/Android/obb/" + getPackageName();
		String obbName = "main." + getVersionCode() + "." + getPackageName() + ".obb";
		String androidZipPath = new File(dir, obbName).getAbsolutePath();
		game = new MaryoGame(androidZipPath);
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
}
