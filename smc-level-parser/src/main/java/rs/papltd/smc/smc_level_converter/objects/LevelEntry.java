package rs.papltd.smc.smc_level_converter.objects;

import org.xml.sax.Attributes;

/**
 * Created by pedja on 22.6.14..
 */
public class LevelEntry
{
    public float posx, posy, w = 10, h = 20;
    public int type;
    public String name, direction;

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
            type = Integer.parseInt(value);
        }
        else if("direction".equals(name))
        {
            direction = value;
        }
        else if("name".equals(name))
        {
            name = value;
        }
    }

}
