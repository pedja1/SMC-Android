package rs.pedjaapps.smc.audio;

import com.badlogic.gdx.audio.Sound;

import rs.pedjaapps.smc.utility.PrefsManager;

/**
 * Created by pedja on 6.9.15..
 */
public class SoundManager
{
    public static long play(Sound sound)
    {
        return play(sound, 1f);
    }

    public static long play(Sound sound, float volume)
    {
        if(sound == null || !PrefsManager.isPlaySounds())return -1;
        return sound.play(volume * PrefsManager.getSoundVolume());
    }
}
