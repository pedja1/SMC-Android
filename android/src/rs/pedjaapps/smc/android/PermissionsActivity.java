package rs.pedjaapps.smc.android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Copyright (c) 2016 "Predrag Čokulov,"
 * pedjaapps [https://pedjaapps.net]
 * <p>
 * This file is part of SMC-Android.
 * <p>
 * SMC-Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class PermissionsActivity extends Activity
{
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_CONTACTS = 1001;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            int permissionCheck = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED)
            {
                init();
            }
            else
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_READ_EXTERNAL_CONTACTS);
            }
        }
        else
        {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_READ_EXTERNAL_CONTACTS:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    init();
                }
                else
                {
                    finish();
                }
            }
        }
    }

    private void init()
    {
        finish();
        startActivity(new Intent(this, AndroidLauncher.class));
    }
}
