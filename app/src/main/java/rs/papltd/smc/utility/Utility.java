package rs.papltd.smc.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.Toast;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.File;
import java.util.Vector;

import rs.papltd.smc.Assets;

/**
 * Created by pedja on 2/27/14.
 */
public class Utility
{

    /**
     * This class can not bi instantiated, it will throw exception if you try to instantiate it
     * @throws java.lang.IllegalStateException*/
    public Utility()
    {
        throw new IllegalStateException("Class " + this.getClass().getName() + " is not instantiable!");
    }

    public static File getAPKExpansionFile(Context ctx)
    {
        String packageName = ctx.getPackageName();
        PackageInfo pInfo;
        try
        {
            pInfo = ctx.getPackageManager().getPackageInfo(packageName, 0);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
        int version = pInfo.versionCode;
        // Build the full path to the app's expansion files
        File root = Environment.getExternalStorageDirectory();
        File expPath = new File(root.toString() + Constants.EXP_PATH + packageName);

        // Check that expansion file path exists
        String strMainPath = expPath + File.separator + "main." +
                version + "." + packageName + ".obb";
        File main = new File(strMainPath);
        if (main.isFile())
        {
            return main;
        }
        return null;
    }

    /**
     * General Purpose AlertDialog*/
    public static AlertDialog showMessageAlertDialog(Context context, String message,
                                              String title, DialogInterface.OnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, listener);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    /**
     * General Purpose AlertDialog*/
    public static AlertDialog showMessageAlertDialog(Context context, int message,
                                              int title, DialogInterface.OnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, listener);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    /**
     * General Purpose Toast*/
    public static void showToast(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * General Purpose Toast*/
    public static void showToast(Context context, int resId)
    {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    public static float getHeight(float width, TextureRegion region)
    {
        return width*region.getRegionHeight()/region.getRegionWidth();
    }

    public static float getHeight(float width, Texture texture)
    {
        return width*texture.getHeight()/texture.getWidth();
    }

    public static float getHeight(float newWidth, float origWidth, float origHeight)
    {
        return newWidth * origHeight / origWidth;
    }

    public enum PrefsKey
    {
        sound, music
    }

    /**Checks current sound state(on/off) and toggles it
     * @return new state of the sound(true = on, false = off)*/
    public static boolean toggleSound()
    {
        boolean currentState = Assets.prefs.getBoolean(PrefsKey.sound.toString(), true);
        SharedPreferences.Editor editor = Assets.prefs.edit();
        editor.putBoolean(PrefsKey.sound.toString(), !currentState);
        editor.apply();//apply will make changes to memory immediately, and write to disk asynchronously
        Assets.playSounds = !currentState;
        return !currentState;
    }


    /**Checks current music state(on/off) and toggles it
     * @return new state of the music(true = on, false = off)*/
    public static boolean toggleMusic()
    {
        boolean currentState = Assets.prefs.getBoolean(PrefsKey.music.toString(), true);
        SharedPreferences.Editor editor = Assets.prefs.edit();
        editor.putBoolean(PrefsKey.music.toString(), !currentState);
        editor.apply();//apply will make changes to memory immediately, and write to disk asynchronously
        Assets.playMusic = !currentState;
        return !currentState;
    }

    public static void draw(SpriteBatch batch, Texture texture, float x, float y, float height)
    {
        batch.draw(texture, x, y, height * texture.getWidth()/texture.getHeight(), height);
    }

    public static void draw(SpriteBatch batch, TextureRegion region, float x, float y, float height)
    {
        batch.draw(region, x, y, height * region.getRegionWidth()/region.getRegionHeight(), height);
    }
    /*(+x\y-height)/64*/
}
