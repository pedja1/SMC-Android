package rs.papltd.smc.smc_level_converter.objects;

import org.xml.sax.Attributes;

/**
 * Created by pedja on 22.6.14..
 */
public class EnemyStopper
{
    public float posx, posy, width = 15, height = 15f;

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
    }

}
