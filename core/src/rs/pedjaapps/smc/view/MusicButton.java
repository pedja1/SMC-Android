package rs.pedjaapps.smc.view;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.assets.FontAwesome;
import rs.pedjaapps.smc.audio.MusicManager;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.utility.PrefsManager;

/**
 * Created by Benjamin Schulte on 02.10.2017.
 */

public class MusicButton extends ColorableTextButton {
    public MusicButton(Skin skin, final Sound audioOn) {
        super(getSoundStateIcon(), skin, Assets.BUTTON_FA);

        this.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Music music = getMusic();

                if (!PrefsManager.isPlaySounds()) {
                    PrefsManager.setPlayMusic(true);
                    PrefsManager.setPlaySounds(true);
                    if (music != null)
                        MusicManager.play(music);
                } else if (PrefsManager.isPlayMusic()) {
                    PrefsManager.setPlayMusic(false);
                    PrefsManager.setPlaySounds(true);

                    if (music != null)
                        music.pause();

                    if (audioOn != null)
                        SoundManager.play(audioOn);
                } else {
                    PrefsManager.setPlayMusic(false);
                    PrefsManager.setPlaySounds(false);
                }
                ((TextButton) actor).setText(getSoundStateIcon());
            }
        });
    }

    private static String getSoundStateIcon() {
        if (PrefsManager.isPlayMusic())
            return FontAwesome.SETTINGS_MUSIC;
        else if (PrefsManager.isPlaySounds())
            return FontAwesome.SETTINGS_SPEAKER_ON;
        else
            return FontAwesome.SETTINGS_SPEAKER_OFF;
    }

    protected Music getMusic() {
        return null;
    }
}
