package rs.papltd.smc.smc_level_converter;

/**
 * Created by pedja on 22.6.14..
 */
public class Sprite
{
    public enum Type
    {
        MASS_PASSIVE/* = 0*/,
        MASS_MASSIVE/* = 1*/,
        MASS_HALFMASSIVE/* = 2*/,
        MASS_CLIMBABLE/* = 3*/
    }
    public float posx, posy, width, height;
    public String type, texture_atlas, texture_name;
    public String image, imageOriginal;
    public int rotationX, rotationY, rotationZ;//degrees
}
