package rs.papltd.smc.smc_level_converter.objects;

import com.badlogic.gdx.math.Vector2;

import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pedja on 26.11.15..
 */
public class Path
{
    /**<Property name="posx" value="317" />
     <Property name="posy" value="-865" />
     <Property name="identifier" value="1" />
     <Property name="rewind" value="0" />
     <Property name="segment_0_x1" value="0" />
     <Property name="segment_0_y1" value="0" />
     <Property name="segment_0_x2" value="0" />
     <Property name="segment_0_y2" value="700" />
     <Property name="segment_1_x1" value="0" />
     <Property name="segment_1_y1" value="700" />
     <Property name="segment_1_x2" value="720" />
     <Property name="segment_1_y2" value="700" />*/
    public float posx, posy;
    public int rewind;
    public String id;
    public List<Segment> segments;

    {
        segments = new ArrayList<>();
    }

    public static class Segment
    {
        public Vector2 start, end;

        public Segment()
        {
            start = new Vector2();
            end = new Vector2();
        }
    }

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
        else if("identifier".equals(name))
        {
            id = value;
        }
        else if("rewind".equals(name))
        {
            rewind = Integer.parseInt(value);
        }
        else if(name.contains("segment"))
        {
            if(name.contains("x1"))
            {
                Segment segment = new Segment();
                segment.start.x = Float.parseFloat(value);
                segments.add(segment);
            }
            else if(name.contains("y1"))
            {
                segments.get(segments.size() - 1).start.y = Float.parseFloat(value);
            }
            else if(name.contains("x2"))
            {
                segments.get(segments.size() - 1).end.x = Float.parseFloat(value);
            }
            else if(name.contains("y2"))
            {
                segments.get(segments.size() - 1).end.y = Float.parseFloat(value);
            }
        }
    }

}
