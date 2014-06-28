package rs.papltd.smc.smc_level_converter;

/**
 * Created by pedja on 22.6.14..
 */
public class Background
{
    public enum Type
    {
        BG_NONE/*=0*/,            // nothing
        BG_IMG_TOP/*=3*/,            // tiles only horizontal and is on the top
        BG_IMG_BOTTOM/*=1*/,        // tiles only horizontal and is on the bottom
        BG_IMG_ALL/*=2*/,            // tiles into all directions
        BG_GR_VER/*=103*/,        // vertical gradient
        BG_GR_HOR/*=104*/            // horizontal gradient*/
    }

    public int type = 0;
    public int color1_red, color2_red, color1_green, color2_green, color1_blue, color2_blue;
    public float posx, posy, speedx, speedy, const_vely, const_velx;
    public String image;
}
