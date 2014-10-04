package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

/**
 * Created by pedja on 2/15/14.
 */
public class Constants
{
    public final static String EXP_PATH = "/Android/obb/";
    public static final String OBB_KEY = "s3cr3tm@r10chr0n1cl3s";

    public static final FreetypeFontLoader.FreeTypeFontLoaderParameter defaultFontParams;
    public static final FreetypeFontLoader.FreeTypeFontLoaderParameter defaultFontParamBold;
    static
    {
        defaultFontParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        defaultFontParams.fontFileName = "data/fonts/Roboto-Regular.ttf";
        defaultFontParams.fontParameters.size = 14;
        defaultFontParams.fontParameters.magFilter = Texture.TextureFilter.Linear;
        defaultFontParams.fontParameters.minFilter = Texture.TextureFilter.Linear;

        defaultFontParamBold = defaultFontParams;
        defaultFontParamBold.fontFileName = "data/fonts/Roboto-Bold.ttf";
    }

    public static float CAMERA_WIDTH/* = 10f*/;
    public static final float CAMERA_HEIGHT = 7f;
    public static float ASPECT_RATIO;
    public static final float DRAW_WIDTH = 12.444f;
    public static final float BACKGROUND_SCROLL_SPEED = 0.12f;
    public static final int GRAVITY = -20;
    static
    {
        initCamera();
    }

    public static void initCamera()
    {
        ASPECT_RATIO = (float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
        CAMERA_WIDTH = CAMERA_HEIGHT * ASPECT_RATIO;
    }
}
