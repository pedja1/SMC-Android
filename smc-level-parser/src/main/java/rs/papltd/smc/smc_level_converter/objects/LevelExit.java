package rs.papltd.smc.smc_level_converter.objects;

import org.xml.sax.Attributes;

import java.io.File;

/**
 * Created by pedja on 22.6.14..
 */
public class LevelExit
{
    public float posx, posy, width = 20, height = 20;
    public int type, camera_motion;
    public String level_name, entry, direction;

    public void setFromAttributes(Attributes attributes)
    {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        if("posx".equals(name))
        {
            posx = Float.parseFloat(value) - 10;
        }
        else if("posy".equals(name))
        {
            posy = Float.parseFloat(value);
        }
        else if("type".equals(name))
        {
            type = Integer.parseInt(value);
        }
        else if("camera_motion".equals(name))
        {
            camera_motion = Integer.parseInt(value);
        }
        else if("direction".equals(name))
        {
            direction = value;
        }
        else if("level_name".equals(name))
        {
            level_name = value;
            File file;
            if(!(file = new File("/home/pedja/workspace/SMC-Android/levels/levels_smc_original/levels", level_name + ".smclvl")).exists())
            {
                throw new IllegalArgumentException("Sublevel '" + file + "' doesnt exist");
            }
        }
        else if("entry".equals(name))
        {
            entry = value;
        }
    }

}
