package rs.papltd.smc.smc_level_converter.objects;

import com.badlogic.gdx.math.Rectangle;

import org.xml.sax.Attributes;

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
    public String image;
    public int rotationX, rotationY, rotationZ;//degrees
    public Rectangle colRect;

    public void setFromAttributes(Attributes attributes)
    {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        if("posx".equals(name))
        {
            posx = Float.parseFloat(value);
        }
        else if("posy".equals(name))
        {
            posy = Float.parseFloat(value);
        }
        else if("type".equals(name))
        {
            type = value;
        }
        else if("image".equals(name))
        {
            image = value;
        }
    }

}
