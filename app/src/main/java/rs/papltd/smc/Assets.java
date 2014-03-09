package rs.papltd.smc;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

import com.badlogic.gdx.assets.*;

import rs.papltd.smc.utility.AbsoluteFileHandleResolver;

/**
 * Created by pedja on 2/15/14.
 */
public class Assets
{
	public static AssetManager manager = new AssetManager(new AbsoluteFileHandleResolver());
    public static HashMap<String, TextureRegion> loadedRegions = new HashMap<String, TextureRegion>();
    public static String mountedObbPath = null;

    public static void dispose()
    {
        loadedRegions.clear();
		manager.clear();
    }

    public enum TR
    {

    }

    public enum Asset
    {

    }

}
