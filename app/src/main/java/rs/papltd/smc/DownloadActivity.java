package rs.papltd.smc;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.OnObbStateChangeListener;
import android.os.storage.StorageManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.io.File;

import rs.papltd.smc.utility.Constants;
import rs.papltd.smc.utility.Utility;

public class DownloadActivity extends Activity
{
    StorageManager storageManager;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);

        if(!isExternalStoragePresent())
        {
            Utility.showMessageAlertDialog(this, getString(R.string.media_unmounted_error), null, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    finish();
                }
            }).setCancelable(false);
            return;
        }

        File obbFile = Utility.getAPKExpansionFile(this);
        if(obbFile != null && obbFile.exists())
        {
            mountObbFile(obbFile);
        }
        else
        {
            Utility.showMessageAlertDialog(this, "TODO: File not found, start download", null, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    finish();
                }
            }).setCancelable(false);
        }

    }

    private void mountObbFile(final File obbFile)
    {
        if(storageManager.isObbMounted(obbFile.getAbsolutePath()))
        {
            Assets.mountedObbPath = storageManager.getMountedObbPath(obbFile.getAbsolutePath());
            startGame();
        }
        else
        {
            storageManager.mountObb(obbFile.getAbsolutePath(), null/*Constants.OBB_KEY*/, new OnObbStateChangeListener()
            {
                @Override
                public void onObbStateChange(String path, int state)
                {
                    if(state == OnObbStateChangeListener.MOUNTED)
                    {
                        System.out.println("Everything ok, start game");
                        System.out.println("OBB mount path: " + storageManager.getMountedObbPath(obbFile.getAbsolutePath()));
                        Assets.mountedObbPath = storageManager.getMountedObbPath(obbFile.getAbsolutePath());
                        startGame();
                    }
                    else
                    {
                        Utility.showMessageAlertDialog(DownloadActivity.this, getString(R.string.obb_mounting_error), null, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                finish();
                            }
                        }).setCancelable(false);
                    }
                }
            });
        }
    }

    private boolean isExternalStoragePresent()
    {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private void startGame()
    {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
