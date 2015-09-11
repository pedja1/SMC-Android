package rs.papltd.smc.smc_level_converter.objects;

import org.xml.sax.Attributes;

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
    public float posx, posy, speedx, speedy, const_vely, const_velx, width, height;
    public String image;

    public void setFromAttributes(Attributes attributes)
    {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        if("bg_color_1_red".equals(name))
        {
            color1_red = Integer.parseInt(value);
        }
        else if("bg_color_2_red".equals(name))
        {
            color2_red = Integer.parseInt(value);
        }
        else if("bg_color_1_blue".equals(name))
        {
            color1_blue = Integer.parseInt(value);
        }
        else if("bg_color_2_blur".equals(name))
        {
            color2_blue = Integer.parseInt(value);
        }
        if("bg_color_1_green".equals(name))
        {
            color1_green = Integer.parseInt(value);
        }
        else if("bg_color_2_green".equals(name))
        {
            color2_green = Integer.parseInt(value);
        }
        else if("type".equals(name))
        {
            type = Integer.parseInt(value);
        }
        else if("posx".equals(name))
        {
            posx = Float.parseFloat(value);
        }
        else if("posy".equals(name))
        {
            posy = Float.parseFloat(value);
        }
        else if("speedx".equals(name))
        {
            speedx = Float.parseFloat(value);
        }
        else if("speedy".equals(name))
        {
            speedy = Float.parseFloat(value);
        }
        else if("image".equals(name))
        {
            image = value;
        }
        else if("const_velx".equals(name))
        {
            const_velx = Float.parseFloat(value);
        }
        else if("const_vely".equals(name))
        {
            const_vely = Float.parseFloat(value);
        }
    }

}
