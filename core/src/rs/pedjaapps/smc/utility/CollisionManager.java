package rs.pedjaapps.smc.utility;

import rs.pedjaapps.smc.object.GameObject;

public class CollisionManager
{
	static boolean collides_by_axis(float o1Pos, float o1Size, float o2Pos, float o2Size)
	{
		return ((o1Pos + o1Size) > o2Pos &&
	        (o2Pos + o2Size) > o1Pos);
	}

	public static boolean collides(GameObject o1, GameObject o2)
	{
		return (collides_by_axis(o1.mDrawRect.x, o1.mDrawRect.width, o2.mDrawRect.x, o2.mDrawRect.width) &&
	        collides_by_axis(o1.mDrawRect.y, o1.mDrawRect.height, o2.mDrawRect.y, o2.mDrawRect.height));
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
		float o1pos = x ? o1.mColRect.x : o1.mColRect.y;
		float o2pos = x ? o2.mColRect.x : o2.mColRect.y;
		float o1size = x ? o1.mColRect.width : o1.mColRect.height;
		float o2size = x ? o2.mColRect.width : o2.mColRect.height;
		
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
