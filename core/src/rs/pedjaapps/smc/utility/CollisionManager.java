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

	public static boolean resolve_objects(GameObject o1, GameObject o2)
	{
		float overlap_x = get_overlap(o1.getPosition().x, o1.getBounds().width, o2.getPosition().x, o2.getBounds().width);
		float overlap_y = get_overlap(o1.getPosition().y, o1.getBounds().height, o2.getPosition().y, o2.getBounds().height);
		System.out.println("y before: " + o1.getPosition().y);
		
		// First method, resolve both axises
		//o1.getBounds().x -= overlap_x;
		//o1.getBounds().y -= overlap_y;
		// Second method, resolve the shortest axis
		boolean y;
		if (overlap_x < overlap_y)
		{
			y = false;
			/*if(o1.getPosition().x > o2.getPosition().x)
			{
				o1.position.x += overlap_x;
			}
			else
			{
				o1.position.x -= overlap_x;
			}*/
		}
		else
		{
			y = true;
			if(o1.getPosition().y > o2.getPosition().y)
			{
				o1.position.y -= overlap_y;
			}
			else
			{
				o1.position.y += overlap_y;
			}
		}
		o1.getBounds().x = o1.position.x;
		o1.getBounds().y = o1.position.y;
		System.out.println("y after: " + o1.position.y);
		return y;
	}
}
