package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

/**
 * Created by pedja on 3/8/14.
 */
public class MyFileHandleResolver implements FileHandleResolver
{
    @Override
    public FileHandle resolve(String fileName)
    {
        //return Gdx.files.absolute(Assets.mountedObbPath + fileName);
        return Gdx.files.internal(fileName);
    }
}
