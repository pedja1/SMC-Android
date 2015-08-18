package rs.papltd.smc.smc_level_converter.objects;


import com.badlogic.gdx.math.Rectangle;

import org.xml.sax.Attributes;

/**
 * Created by pedja on 22.6.14..
 */
public class Player
{
    public float posx, posy;
    public String direction;
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
        else if("direction".equals(name))
        {
            direction = value;
        }
    }

}
