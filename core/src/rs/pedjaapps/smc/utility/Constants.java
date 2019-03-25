package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.Gdx;

/**
 * Created by pedja on 2/15/14.
 */
public class Constants {
    public static float CAMERA_WIDTH/* = 10f*/;
    public static float MENU_CAMERA_WIDTH/* = 10f*/;

    public static final float MENU_CAMERA_HEIGHT = 7f;
    public static final float CAMERA_HEIGHT = 9f;

    public static final float MENU_DRAW_WIDTH = 12.444444444f;
    public static final float DRAW_WIDTH = 16f;

    public static final int GRAVITY = -20;

    static {
        initCamera();
    }

    public static void initCamera() {
        float aspectRatio = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        CAMERA_WIDTH = CAMERA_HEIGHT * aspectRatio;
        MENU_CAMERA_WIDTH = MENU_CAMERA_HEIGHT * aspectRatio;
    }
}
