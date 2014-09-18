package rs.pedjaapps.smc.model;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public abstract class DynamicObject extends GameObject
{
	public DynamicObject(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
    }
	
	protected void updateBounds()
    {
        bounds.x = body.x;
        bounds.y = body.y;
    }
}
