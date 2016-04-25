package rs.pedjaapps.smc.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

import rs.pedjaapps.smc.utility.PrefsManager;

public class MaryoFileHandleResolver implements FileHandleResolver
{
    private static final int[] ASSETS_RESOLUTIONS = new int[]{1440, 1080, 720, 480};

    public static int DEFAULT_TEXTURE_QUALITY = -1;

    private int resolution;

    public MaryoFileHandleResolver()
    {
        int prefResolution = PrefsManager.getTextureQuality();

        if(prefResolution >= 0 && prefResolution < ASSETS_RESOLUTIONS.length)
        {
            resolution = ASSETS_RESOLUTIONS[ASSETS_RESOLUTIONS.length - 1 - prefResolution];
        }
        else
        {
            int height = Gdx.graphics.getHeight();

            int tmpRes = 720;
            int diff = Integer.MAX_VALUE;
            int offset = ASSETS_RESOLUTIONS.length;
            for (int res : ASSETS_RESOLUTIONS)
            {
                int tmp;
                if ((tmp = Math.abs(res - height)) < diff)
                {
                    diff = tmp;
                    tmpRes = res;
                    offset--;
                }
            }
            resolution = tmpRes;
            DEFAULT_TEXTURE_QUALITY = offset;
            PrefsManager.setTextureQuality(DEFAULT_TEXTURE_QUALITY);
        }
        System.out.println("resolution: " + resolution);
    }

    @Override
    public FileHandle resolve(String fileName)
    {
        fileName = getFileNameForResolution(fileName);
        return Gdx.files.internal(fileName);
    }

    private String getFileNameForResolution(String fileName)
    {
        String newFileName = fileName.replaceFirst("data", "data_" + resolution);
        FileHandle handle = new FileHandle(newFileName);
        if(handle.exists())
        {
            return newFileName;
        }
        else
        {
            int resolutionIndex = finIndexForResolution(resolution);
            if(resolutionIndex < 0)
                return fileName;

            //try smaller resolutions first
            for(int i = resolutionIndex; i >= 0; i--)
            {
                int resolution = ASSETS_RESOLUTIONS[i];
                newFileName = fileName.replaceFirst("data", "data_" + resolution);
                handle = new FileHandle(newFileName);
                if(handle.exists())
                {
                    return newFileName;
                }
            }

            //try larger
            for(int i = resolutionIndex; i < ASSETS_RESOLUTIONS.length; i++)
            {
                int resolution = ASSETS_RESOLUTIONS[i];
                newFileName = fileName.replaceFirst("data", "data_" + resolution);
                handle = new FileHandle(newFileName);
                if(handle.exists())
                {
                    return newFileName;
                }
            }
        }
        return fileName;
    }

    private int finIndexForResolution(int resolution)
    {
        for(int i = 0; i < ASSETS_RESOLUTIONS.length; i++)
        {
            if(resolution == ASSETS_RESOLUTIONS[i])
                return i;
        }
        return -1;
    }


}