package rs.pedjaapps.smc.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

import java.util.zip.ZipFile;

public class ArchiveFileHandleResolver implements FileHandleResolver
{
    private static final int[] ASSETS_RESOLUTIONS = new int[]{1080, 768, 540};

    private int resolution;
    private final ZipFile archive;

    public ArchiveFileHandleResolver(ZipFile archive)
    {
        this.archive = archive;
        //TODO also allow user to override in settings
        int height = Gdx.graphics.getHeight();

        int tmpRes = 768;
        int diff = Integer.MAX_VALUE;
        for(int res : ASSETS_RESOLUTIONS)
        {
            int tmp;
            if((tmp = Math.abs(res - height)) < diff)
            {
                diff = tmp;
                tmpRes = res;
            }
        }
        resolution = tmpRes;
    }

    @Override
    public FileHandle resolve(String fileName)
    {
        fileName = getFileNameForResolution(fileName);
        return new ArchiveFileHandle(archive, fileName);
    }

    private String getFileNameForResolution(String fileName)
    {
        String newFileName = fileName.replaceFirst("data", "data_" + resolution);
        FileHandle handle = new ArchiveFileHandle(archive, newFileName);
        if(handle.exists())
        {
            return newFileName;
        }
        else
        {
            return fileName;
        }
    }


}