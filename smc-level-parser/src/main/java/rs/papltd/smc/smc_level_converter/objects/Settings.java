package rs.papltd.smc.smc_level_converter.objects;

import org.xml.sax.Attributes;

/**
 * Created by pedja on 22.6.14..
 */
public class Settings
{
    public String music;
    public float width, height;

    public void setFromAttributes(Attributes attributes)
    {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        if("lvl_music".equals(name))
        {
            music = value.substring(0, value.lastIndexOf(".")) + ".ogg";
        }
        else if("cam_limit_w".equals(name))
        {
            width = Integer.parseInt(value);
        }
        else if("cam_limit_h".equals(name))
        {
            height = Integer.parseInt(value);
        }
    }

}
