package rs.papltd.smc.smc_level_converter;


import com.badlogic.gdx.math.Rectangle;

/**
 * Created by pedja on 22.6.14..
 */
public class Enemy
{
    public String type, color, direction, image_dir, texture_atlas;
    public float posx, posy, speed, width, height;
    public int max_distance;
    public Rectangle colRect = new Rectangle();
    public int max_downgrade_count;
}
