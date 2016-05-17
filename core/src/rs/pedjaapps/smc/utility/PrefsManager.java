package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import rs.pedjaapps.smc.assets.ArchiveFileHandleResolver;

/**
 * Created by pedja on 21.9.14..
 */
public class PrefsManager
{
    public static Preferences prefs = Gdx.app.getPreferences("main_prefs");

    public enum PrefsKey
    {
        sound, music, sg, debug, sound_volume, music_volume, texture_quality, phisycs_post_processing
    }

    public static boolean isPlayMusic()
    {
        return prefs.getBoolean(PrefsKey.music.toString(), true);
    }

    public static void setPlayMusic(boolean playMusic)
    {
        prefs.putBoolean(PrefsKey.music.toString(), playMusic);
        flush();
    }

    public static boolean isPlaySounds()
    {
        return prefs.getBoolean(PrefsKey.sound.toString(), true);
    }

    public static void setPlaySounds(boolean playSounds)
    {
        prefs.putBoolean(PrefsKey.sound.toString(), playSounds);
		flush();
    }

    public static boolean isPhysicsPP()
    {
        return prefs.getBoolean(PrefsKey.phisycs_post_processing.toString(), true);
    }

    public static void setPhysicsPP(boolean pp)
    {
        prefs.putBoolean(PrefsKey.phisycs_post_processing.toString(), pp);
		flush();
    }
	
	public static String getSaveGame()
    {
        return prefs.getString(PrefsKey.sg.toString(), null);
    }

    public static void setSaveGame(String saveGame)
    {
        prefs.putString(PrefsKey.sg.toString(), saveGame);
		flush();
    }

	public static float getSoundVolume()
    {
        return prefs.getFloat(PrefsKey.sound_volume.toString(), 0.6f);
    }

    public static void setSoundVolume(float volume)
    {
        prefs.putFloat(PrefsKey.sound_volume.toString(), volume);
		flush();
    }

	public static float getMusicVolume()
    {
        return prefs.getFloat(PrefsKey.music_volume.toString(), 0.4f);
    }

    public static void setMusicVolume(float volume)
    {
        prefs.putFloat(PrefsKey.music_volume.toString(), volume);
		flush();
    }

    public static boolean isDebug()
    {
        return prefs.getBoolean(PrefsKey.debug.toString(), false);
    }

    public static void setDebug(boolean debug)
    {
        prefs.putBoolean(PrefsKey.debug.toString(), debug);
        flush();
    }

    public static int getTextureQuality()
    {
        return prefs.getInteger(PrefsKey.texture_quality.toString(), ArchiveFileHandleResolver.DEFAULT_TEXTURE_QUALITY);
    }

    public static void setTextureQuality(int quality)
    {
        prefs.putInteger(PrefsKey.texture_quality.toString(), quality);
        flush();
    }
	
	public static void flush()
	{
		prefs.flush();
	}
}
