package rs.papltd.smc.smc_level_converter.objects;

import org.xml.sax.Attributes;

/**
 * Created by pedja on 29.6.14..
 */
public class MovingPlatform
{
    public float posx, posy, max_distance, speed, touch_time, shake_time, touch_move_time, width, height;
    public int move_type, middle_img_count;
    public String massive_type, direction, image_top_left, image_top_middle, image_top_right,
            texture_atlas, path_identifier;

    public void setFromAttributes(Attributes attributes)
    {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        switch (name)
        {
            case "image_top_middle":
                image_top_middle = value;
                break;
            case "image_top_right":
                image_top_right = value;
                break;
            case "image_top_left":
                image_top_left = value;
                break;
            case "direction":
                direction = value;
                break;
            case "massive_type":
                massive_type = value;
                break;
            case "middle_img_count":
                middle_img_count = Integer.parseInt(value);
                break;
            case "move_type":
                move_type = Integer.parseInt(value);
                break;
            case "posx":
                posx = Float.parseFloat(value);
                break;
            case "posy":
                posy = Float.parseFloat(value);
                break;
            case "max_distance":
                max_distance = Float.parseFloat(value);
                break;
            case "speed":
                speed = Float.parseFloat(value);
                break;
            case "touch_time":
                touch_time = Float.parseFloat(value);
                break;
            case "shake_time":
                shake_time = Float.parseFloat(value);
                break;
            case "touch_move_time":
                touch_move_time = Float.parseFloat(value);
                break;
            case "path_identifier":
                path_identifier = value;
                break;
        }
    }

}
