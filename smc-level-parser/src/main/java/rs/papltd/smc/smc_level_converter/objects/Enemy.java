package rs.papltd.smc.smc_level_converter.objects;


import com.badlogic.gdx.math.Rectangle;

import org.xml.sax.Attributes;

/**
 * Created by pedja on 22.6.14..
 */
public class Enemy
{
    public String type, color, direction, image_dir, texture_atlas;
    public float posx, posy, speed, width, height;
    public float max_distance;
    public Rectangle colRect = new Rectangle();
    public int max_downgrade_count;

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
        else if("image_dir".equals(name))
        {
            image_dir = value;
        }
        else if("direction".equals(name))
        {
            direction = value;
        }
        else if("color".equals(name))
        {
            color = value;
        }
        else if("speed".equals(name))
        {
            speed = Float.parseFloat(value);
        }
        else if("max_distance".equals(name))
        {
            max_distance = Integer.parseInt(value);
        }
        else if("max_downgrade_count".equals(name))
        {
            max_downgrade_count = Integer.parseInt(value);
        }
    }

}
