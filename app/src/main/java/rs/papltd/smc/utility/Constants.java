package rs.papltd.smc.utility;

import com.badlogic.gdx.Gdx;

/**
 * Created by pedja on 2/15/14.
 */
public class Constants
{
    public final static String EXP_PATH = "/Android/obb/";
    public static final String OBB_KEY = "s3cr3tm@r10chr0n1cl3s";

    public static final float CAMERA_WIDTH/* = 10f*/;
    public static final float CAMERA_HEIGHT = 7f;
    public static final float ASPECT_RATIO;
    public static final float DRAW_WIDTH = 12.444f;
    public static final float BACKGROUND_SCROLL_SPEED = 0.12f;
    public static final int GRAVITY = -20;
    static
    {
        ASPECT_RATIO = (float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
        CAMERA_WIDTH = CAMERA_HEIGHT * ASPECT_RATIO;
    }
}
