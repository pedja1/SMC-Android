package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.Gdx;

import rs.pedjaapps.smc.MaryoGame;

/**
 * Created by pedja on 2/15/14.
 */
public class Constants
{
    public static final String DEFAULT_FONT_FILE_NAME = "data/fonts/GROBOLD.ttf";


    public static float CAMERA_WIDTH/* = 10f*/;
    public static float MENU_CAMERA_WIDTH/* = 10f*/;

    public static final float MENU_CAMERA_HEIGHT = 7f;
    public static final float CAMERA_HEIGHT = 9f;

    public static float ASPECT_RATIO;

    public static final float MENU_DRAW_WIDTH = 12.444444444f;
    public static final float DRAW_WIDTH = 16f;

    public static final int GRAVITY = -20;

    public static boolean PHYSICS_PP = PrefsManager.isPhysicsPP();

    static
    {
        initCamera();
    }

    public static void initCamera()
    {
        ASPECT_RATIO = (float)MaryoGame.NATIVE_WIDTH/(float)MaryoGame.NATIVE_HEIGHT;
        CAMERA_WIDTH = CAMERA_HEIGHT * ASPECT_RATIO;
        MENU_CAMERA_WIDTH = MENU_CAMERA_HEIGHT * ASPECT_RATIO;
    }
}
