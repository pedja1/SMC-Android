package rs.papltd.smc.smc_level_converter.objects;

import com.badlogic.gdx.math.Rectangle;

import org.xml.sax.Attributes;

/**
 * Created by pedja on 22.6.14..
 */
public class Sprite
{
    public enum Type
    {
        MASS_PASSIVE/* = 0*/,
        MASS_MASSIVE/* = 1*/,
        MASS_HALFMASSIVE/* = 2*/,
        MASS_CLIMBABLE/* = 3*/
    }

    public enum Ground
    {
        GROUND_NORMAL("normal", 0),
        GROUND_EARTH("earth", 1),
        GROUND_ICE("ice", 2),
        GROUND_SAND("sand", 3),
        GROUND_STONE("stone", 4),
        GROUND_PLASTIC("plastic", 5);

        String type;
        int id;

        Ground(String type, int id)
        {
            this.type = type;
            this.id = id;
        }

        public static int idFromType(String type)
        {
            if(type == null)
                return GROUND_NORMAL.id;
            for(Ground ground : values())
            {
                if(ground.type.equals(type))
                {
                    return ground.id;
                }
            }
            return GROUND_NORMAL.id;
        }
    }

    public float posx, posy, width, height;
    public String type, texture_atlas, texture_name;
    public String image;
    public int rotationX, rotationY, rotationZ;//degrees
    public Rectangle colRect;
    public int groundType;

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
        else if("image".equals(name))
        {
            image = value;
        }
    }

}
