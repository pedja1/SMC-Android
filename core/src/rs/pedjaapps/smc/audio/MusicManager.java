package rs.pedjaapps.smc.audio;

import com.badlogic.gdx.audio.Music;

import rs.pedjaapps.smc.utility.PrefsManager;

/**
 * Created by pedja on 6.9.15..
 */
public class MusicManager {
    private static Music main, temporary;
    private static boolean isPaused;
    private static Music wasPlayingBeforePause;

    public static void pause() {
        if (isPaused)
            return;

        isPaused = true;

        if (temporary != null && temporary.isPlaying())
            wasPlayingBeforePause = temporary;
        else if (main != null && main.isPlaying())
            wasPlayingBeforePause = main;
        else
            wasPlayingBeforePause = null;

        if (wasPlayingBeforePause != null)
            wasPlayingBeforePause.pause();
    }

    public static void resume() {
        if (!isPaused)
            return;

        if (wasPlayingBeforePause != null)
            wasPlayingBeforePause.play();

        isPaused = false;
        wasPlayingBeforePause = null;
    }

    public static void play(Music music) {
        play(music, PrefsManager.getMusicVolume(), true);
    }

    public static void play(Music music, boolean main) {
        play(music, PrefsManager.getMusicVolume(), main);
    }

    public static void play(Music music, float volume) {
        play(music, volume, true);
    }

    private static void play(Music music, float volume, boolean isMain) {
        wasPlayingBeforePause = null;
        if (music == null || !PrefsManager.isPlayMusic() && isMain) return;
        if (isMain) {
            if (main == music && music.isPlaying())
                return;
            if (main != null) {
                main.stop();
                main = null;
            }
            main = music;
            music.setVolume(volume);
            music.play();
        } else {
            if (main != null) {
                main.pause();
            }
            if (temporary != null) {
                temporary.stop();
            }
            temporary = music;
            temporary.setVolume(volume);
            temporary.play();
            temporary.setOnCompletionListener(new Music.OnCompletionListener() {
                @Override
                public void onCompletion(Music music) {
                    if (main != null) {
                        main.play();
                    }
                }
            });
        }
    }

    public static void stop(boolean isMain) {
        wasPlayingBeforePause = null;
        if (isMain) {
            if (main != null) {
                main.stop();
                main = null;
            }
        } else {
            if (temporary != null) {
                temporary.stop();
                temporary = null;
                if (main != null && PrefsManager.isPlayMusic()) {
                    main.play();
                }
            }
        }
    }
}
