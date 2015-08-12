package rs.pedjaapps.smc;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import rs.pedjaapps.smc.object.World;

public class Rect extends Rectangle
{
	public Rect() {}

    public Rect(float x, float y, float width, float height) 
	{
		super(x, y, width, height);
	}

    public Rect(Rect rect) 
	{
		super(rect);
	}
	
	public boolean overlaps(Rect r, boolean considerIntersectAreaPercent)
	{
		boolean overlaps = super.overlaps(r);
		return overlaps;
		/*if (overlaps) 
		{
			if(!considerIntersectAreaPercent)return true;
			if(contains(r))
			{
				int perc = (int) (100f / area() * r.area());
				return perc > 30;
			}
			
			Rectangle intersect = World.RECT_POOL.obtain();
			Intersector.intersectRectangles(this, r, intersect);
			
			float maryoArea = area();
			float intersectArea = intersect.area();
			
			int perc = (int) (100f / (maryoArea) * (intersectArea));
			System.out.println("overlaps perc" + perc);
	
			World.RECT_POOL.free(intersect);
			return perc < 10;
		}
		return false;*/
	}
}
