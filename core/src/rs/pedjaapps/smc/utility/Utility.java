package rs.pedjaapps.smc.utility;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Base64Coder;

import rs.pedjaapps.smc.screen.GameScreen;

/**
 * Created by pedja on 2/27/14.
 */
public class Utility {

    /**
     * This class can not bi instantiated, it will throw exception if you try to instantiate it
     *
     * @throws java.lang.IllegalStateException
     */
    public Utility() {
        throw new IllegalStateException("Class " + this.getClass().getName() + " is not instantiable!");
    }

    public static float getHeight(float width, TextureRegion region) {
        return width * region.getRegionHeight() / region.getRegionWidth();
    }

    public static float getHeight(float width, Texture texture) {
        return width * texture.getHeight() / texture.getWidth();
    }

    public static float getHeight(float newWidth, float origWidth, float origHeight) {
        return newWidth * origHeight / origWidth;
    }

    /**
     * Checks current sound state(on/off) and toggles it
     *
     * @return new state of the sound(true = on, false = off)
     */
    public static boolean toggleSound() {
        boolean currentState = PrefsManager.isPlaySounds();
        PrefsManager.setPlaySounds(!currentState);
        return !currentState;
    }


    /**
     * Checks current music state(on/off) and toggles it
     *
     * @return new state of the music(true = on, false = off)
     */
    public static boolean toggleMusic() {
        boolean currentState = PrefsManager.isPlayMusic();
        PrefsManager.setPlayMusic(!currentState);
        return !currentState;
    }

    public static void draw(SpriteBatch batch, Texture texture, float x, float y, float height) {
        batch.draw(texture, x, y, height * texture.getWidth() / texture.getHeight(), height);
    }

    public static void draw(SpriteBatch batch, TextureRegion region, float x, float y, float height) {
        batch.draw(region, x, y, height * region.getRegionWidth() / region.getRegionHeight(), height);
    }

    public static float getWidth(TextureRegion region, float height) {
        return height * region.getRegionWidth() / region.getRegionHeight();
    }

    public static float getWidth(Texture texture, float height) {
        return height * texture.getWidth() / texture.getHeight();
    }

    /*(+x\y-height)/64*/

    public static int parseInt(String input, int defValue) {
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static String millisToString(float millis) {
        int s = (int) millis % 60;
        int m = ((int) ((millis / 60) % 60));

        return (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "") + s;
    }

    public static void guiPositionToGamePosition(float positionX, float positionY, GameScreen gameScreen, Vector2 point) {
        OrthographicCamera cam = gameScreen.cam;
        OrthographicCamera guiCam = gameScreen.guiCam;

        float camViewX = cam.position.x - (cam.viewportWidth * cam.zoom) * 0.5f;
        float camViewY = cam.position.y - (cam.viewportHeight * cam.zoom) * 0.5f;
        float widthMul = guiCam.viewportWidth / (cam.viewportWidth * cam.zoom);
        float heightMul = guiCam.viewportHeight / (cam.viewportHeight * cam.zoom);
        point.x = camViewX + positionX / widthMul;
        point.y = camViewY + positionY / heightMul;
    }

    public static String encode(String s, String key) {
        return new String(Base64Coder.encode(xorWithKey(s.getBytes(), key.getBytes())));
    }

    public static String decode(String s, String key) {
        return new String(xorWithKey(Base64Coder.decode(s), key.getBytes()));
    }

    private static byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[i % key.length]);
        }

        return out;
    }


}
