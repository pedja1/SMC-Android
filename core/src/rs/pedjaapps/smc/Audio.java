package rs.pedjaapps.smc;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import rs.pedjaapps.smc.utility.PrefsManager;

/**
 * Created by pedja on 6.9.15..
 */
public class Audio
{
    public static long play(Sound sound)
    {
        return play(sound, PrefsManager.getSoundVolume());
    }

    public static long play(Sound sound, float volume)
    {
        if(sound == null || !PrefsManager.isPlaySounds())return -1;
        return sound.play(volume);
    }

    public static void play(Music music)
    {
        play(music, PrefsManager.getMusicVolume());
    }

    public static void play(Music music, float volume)
    {
        if(music == null || !PrefsManager.isPlayMusic())return;
        music.setVolume(volume);
        music.play();
    }
}
