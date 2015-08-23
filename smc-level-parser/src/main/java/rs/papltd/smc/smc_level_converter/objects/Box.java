package rs.papltd.smc.smc_level_converter.objects;

import com.badlogic.gdx.math.Rectangle;

import org.xml.sax.Attributes;

/**
 * Created by pedja on 22.6.14..
 */
public class Box
{
    public float posx, posy;
    public String type, animation, gold_color, text, texture_name, texture_atlas;
    public int item, invisible, useable_count, force_best_item;
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
        else if("animation".equals(name))
        {
            animation = value;
        }
        else if("item".equals(name))
        {
            item = Integer.parseInt(value);
        }
        else if("invisible".equals(name))
        {
            invisible = Integer.parseInt(value);
        }
        else if("useable_count".equals(name))
        {
            useable_count = Integer.parseInt(value);
        }
        else if("force_best_item".equals(name))
        {
            force_best_item = Integer.parseInt(value);
        }
        else if("gold_color".equals(name))
        {
            gold_color = value;
        }
        else if("text".equals(name))
        {
            text = value.replaceAll("<br/>", "\n");
        }
    }

}
