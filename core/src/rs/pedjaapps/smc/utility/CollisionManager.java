package rs.pedjaapps.smc.utility;

import rs.pedjaapps.smc.model.GameObject;
import com.badlogic.gdx.math.Rectangle;

public class CollisionManager
{
	static boolean collides_by_axis(float o1Pos, float o1Size, float o2Pos, float o2Size)
	{
		return ((o1Pos + o1Size) > o2Pos &&
	        (o2Pos + o2Size) > o1Pos);
	}

	public static boolean collides(GameObject o1, GameObject o2)
	{
		return (collides_by_axis(o1.getBounds().x, o1.getBounds().width, o2.getBounds().x, o2.getBounds().width) &&
	        collides_by_axis(o1.getBounds().y, o1.getBounds().height, o2.getBounds().y, o2.getBounds().height));
	}

	static float get_overlap(float o1pos, float o1size, float o2pos, float o2size) 
	{
		if (o1pos > o2pos) 
		{
			return -((o2pos + o2size) - o1pos);
		} 
		else
		{
			return (o1pos + o1size) - o2pos;
		}
	}

	public static void resolve_objects(GameObject o1, GameObject o2, boolean x)
	{
		float o1pos = x ? o1.getBody().x : o1.getBody().y;
		float o2pos = x ? o2.getBody().x : o2.getBody().y;
		float o1size = x ? o1.getBody().width : o1.getBody().height;
		float o2size = x ? o2.getBody().width : o2.getBody().height;
		
		float overlap = get_overlap(o1pos, o1size, o2pos, o2size);
		if(o1pos > o2pos)
		{
			if(x)o1.position.x -= overlap;
			else o1.position.y -= overlap;
		}
		else
		{
			if(x)o1.position.x += overlap;
			else o1.position.y += overlap;
		}
	}
}
