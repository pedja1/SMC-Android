package rs.pedjaapps.smc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

import com.badlogic.gdx.assets.*;

import rs.pedjaapps.smc.utility.MyFileHandleResolver;
import rs.pedjaapps.smc.utility.PrefsManager;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 2/15/14.
 */
public class Assets
{
    public static AssetManager manager;
    public static TextureLoader.TextureParameter textureParameter;
    public static HashMap<String, TextureRegion> loadedRegions;
    public static HashMap<String, Animation> animations;
    public static boolean playMusic;
    public static boolean playSounds;

    static
    {

        textureParameter = new TextureLoader.TextureParameter();
        //textureParameter.genMipMaps = true;
        //textureParameter.magFilter = Texture.TextureFilter.Linear;
        //textureParameter.minFilter = Texture.TextureFilter.Linear;
        manager = new AssetManager(new MyFileHandleResolver());
        loadedRegions = new HashMap<String, TextureRegion>();
        animations = new HashMap<String, Animation>();

        playMusic = PrefsManager.isPlayMusic();
        playSounds = PrefsManager.isPlaySounds();
    }

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
