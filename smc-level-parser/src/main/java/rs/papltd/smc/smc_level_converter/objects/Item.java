package rs.papltd.smc.smc_level_converter.objects;

import com.badlogic.gdx.math.Rectangle;

import org.xml.sax.Attributes;

/**
 * Created by pedja on 22.6.14..
 */
public class Item
{
    public String type, color, texture_atlas, texture_name;
    public float posx, posy, width, height;
    public int mushroom_type;
	public String image;
    public Rectangle colRect = new Rectangle();

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
        else if("color".equals(name))
        {
            color = value;
        }
        else if("mushroom_type".equals(name))
        {
            mushroom_type = Integer.parseInt(value);
        }
    }

}
