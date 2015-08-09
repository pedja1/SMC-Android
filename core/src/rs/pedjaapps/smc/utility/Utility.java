package rs.pedjaapps.smc.utility;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Base64Coder;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.screen.GameScreen;

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
        batch.draw(region, x, y, height * region.getRegionWidth() / region.getRegionHeight(), height);
    }

    public static float getWidth(TextureRegion region, float height)
    {
        return height * region.getRegionWidth() / region.getRegionHeight();
    }

    public static float getWidth(Texture texture, float height)
    {
        return height * texture.getWidth() / texture.getHeight();
    }

    /*(+x\y-height)/64*/
	
	public static String base64Encode(final String input) 
	{
		return Base64Coder.encodeString(input);
	}
	
	public static String base64Decode(final String input) 
	{
		return Base64Coder.decodeString(input);
	}
	
	public static int parseInt(String input, int defValue)
	{
		try
		{
			return Integer.parseInt(input);
		}
		catch(Exception e)
		{
			return defValue;
		}
	}
	
	public static String millisToString(float millis)
	{
        int s = (int) millis % 60;
        int m = ((int) ((millis / 60) % 60));

		return (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "") + s;
	}
	
	public static void scaleObjectToGuiCam(GameObject gameObject, GameScreen gameScreen)
	{
		OrthographicCamera cam = gameScreen.cam;
		OrthographicCamera guiCam = gameScreen.hud.cam;
		float widthMul = guiCam.viewportWidth / cam.viewportWidth;
		float heightMul = guiCam.viewportHeight / cam.viewportHeight;
		
		float objGuiX = (gameObject.position.x - (cam.position.x - cam.viewportWidth / 2)) * widthMul;
		float objGuiY = (gameObject.position.y - (cam.position.y - cam.viewportHeight / 2)) * heightMul;
		
		gameObject.body.y = gameObject.bounds.y = gameObject.position.y = objGuiY;
		gameObject.body.x = gameObject.bounds.x = gameObject.position.x = objGuiX;
		
		gameObject.body.width = gameObject.bounds.width = gameObject.bounds.width * widthMul;
		gameObject.body.height = gameObject.bounds.height = gameObject.bounds.height * heightMul;
	}
}
