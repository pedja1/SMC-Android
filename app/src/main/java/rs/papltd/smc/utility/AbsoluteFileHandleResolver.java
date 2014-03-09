package rs.papltd.smc.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

import rs.papltd.smc.Assets;

/**
 * Created by pedja on 3/8/14.
 */
public class AbsoluteFileHandleResolver implements FileHandleResolver
{
    @Override
    public FileHandle resolve(String fileName)
    {
        return Gdx.files.absolute(Assets.mountedObbPath + fileName);
    }
}
