package rs.papltd.smc.smc_level_converter.objects;

import org.xml.sax.Attributes;

/**
 * Created by pedja on 3.7.14. 13.06.
 * This class is part of the .smc
 * Copyright © 2014 ${OWNER}
 */
public class Information
{
    public int engine_version;
    public int yOffset = 0;

    public void setFromAttributes(Attributes attributes)
    {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        if("engine_version".equals(name))
        {
            if(value.contains("."))
            {
                engine_version = (int) (Float.parseFloat(value) * 10);
            }
            else
            {
                engine_version = Integer.parseInt(value);
            }
            if(engine_version < 35)
            {
                yOffset = -600;
            }
        }
    }

}
