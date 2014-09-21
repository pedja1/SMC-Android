package rs.pedjaapps.smc.utility;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.File;

import rs.pedjaapps.smc.Assets;

/**
 * Created by pedja on 2/27/14.
 */
public class Utility
{

    /**
     * This class can not bi instantiated, it will throw exception if you try to instantiate it
     * @throws java.lang.IllegalStateException*/
    public Utility()
    {
        throw new IllegalStateException("Class " + this.getClass().getName() + " is not instantiable!");
    }

    public static float getHeight(float width, TextureRegion region)
    {
        return width*region.getRegionHeight()/region.getRegionWidth();
    }

    public static float getHeight(float width, Texture texture)
    {
        return width*texture.getHeight()/texture.getWidth();
    }

    public static float getHeight(float newWidth, float origWidth, float origHeight)
    {
        return newWidth * origHeight / origWidth;
    }

    /**Checks current sound state(on/off) and toggles it
     * @return new state of the sound(true = on, false = off)*/
    public static boolean toggleSound()
    {
        boolean currentState = PrefsManager.isPlaySounds();
        PrefsManager.setPlaySounds(!currentState);
        Assets.playSounds = !currentState;
        return !currentState;
    }


    /**Checks current music state(on/off) and toggles it
     * @return new state of the music(true = on, false = off)*/
    public static boolean toggleMusic()
    {
        boolean currentState = PrefsManager.isPlayMusic();
        PrefsManager.setPlayMusic(!currentState);
        Assets.playMusic = !currentState;
        return !currentState;
    }

    public static void draw(SpriteBatch batch, Texture texture, float x, float y, float height)
    {
        batch.draw(texture, x, y, height * texture.getWidth()/texture.getHeight(), height);
    }

    public static void draw(SpriteBatch batch, TextureRegion region, float x, float y, float height)
    {
        batch.draw(region, x, y, height * region.getRegionWidth()/region.getRegionHeight(), height);
    }
    /*(+x\y-height)/64*/
}
