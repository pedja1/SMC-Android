package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.object.Box;
import rs.pedjaapps.smc.object.World;

/**
 * Created by pedja on 23.8.15..
 */
public abstract class BoxItem extends Item
{
    protected Box box;

    public BoxItem(World world, Vector2 size, Vector3 position, Box box)
    {
        super(world, size, position);
        this.box = box;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        box.dispose();
        box = null;
    }
}
