package rs.pedjaapps.smc.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import java.util.ArrayList;
import java.util.List;
import rs.pedjaapps.smc.utility.Constants;


public class World
{

    /**
     * Our player controlled hero *
     */
    Maryo mario;
    /**
     * A world has a level through which Mario needs to go through *
     */
    Level level;
    Array<GameObject> visibleObjects;
	
	// This is the rectangle pool used in collision detection
	// Good to avoid instantiation each frame
	public Pool<Rectangle> rectPool = new Pool<Rectangle>() 
	{
		@Override
		protected Rectangle newObject() 
		{
			return new Rectangle();
		}
	};

    // Getters -----------

    public Maryo getMario()
    {
        return mario;
    }

    public Level getLevel()
    {
        return level;
    }

    public void setMario(Maryo mario)
    {
        this.mario = mario;
    }

    public void setLevel(Level level)
    {
        this.level = level;
    }

    /**
     * Return only the blocks that need to be drawn *
     * All Enemies are always returned
     */
    public Array<GameObject> getDrawableObjects(float camX, float camY/*, boolean getFront*/)
    {
        Array<GameObject> objects = new Array<GameObject>();
        float wX = camX - Constants.CAMERA_WIDTH / 2 - 1;
        float wY = camY - Constants.CAMERA_HEIGHT / 2 - 1;
        float wW = Constants.CAMERA_WIDTH + 1;
        float wH = Constants.CAMERA_HEIGHT + 1;
        Rectangle worldBounds = rectPool.obtain();
		worldBounds.set(wX, wY, wW, wH);
        for (GameObject object : level.getGameObjects())
        {
            Rectangle bounds = object.getBounds();
            if (bounds.overlaps(worldBounds)/* || object instanceof Enemy*/)
            {
                objects.add(object);
                /*if (getFront)
                {
                    if (object.isFront())
                    {
                        sprites.add(object);
                    }
                }
                else
                {
                    sprites.add(object);
                }*/
            }
        }
        visibleObjects = objects;
        return objects;
    }

	public List<GameObject> getSurroundingObjects(GameObject center, float offset)
    {
        List<GameObject> objects = new ArrayList<GameObject>();
        float wX = center.getBody().x - offset;
        float wY = center.getBody().y - offset;
        float wW = center.getBody().x + center.getBody().width + offset * 2;
        float wH = center.getBody().y + center.getBody().height + offset * 2;
        Rectangle offsetBounds = rectPool.obtain();
		offsetBounds.set(wX, wY, wW, wH);
        for (GameObject object : level.getGameObjects())
        {
            Rectangle bounds = object.getBounds();
            if (bounds.overlaps(offsetBounds)/* || object instanceof Enemy*/)
            {
                objects.add(object);
                /*if (getFront)
				 {
				 if (object.isFront())
				 {
				 sprites.add(object);
				 }
				 }
				 else
				 {
				 sprites.add(object);
				 }*/
            }
        }
        return objects;
    }
	
	public Rectangle createMaryoRectWithOffset(float offset)
	{
		float wX = mario.getBody().x - offset;
        float wY = mario.getBody().y - offset;
        float wW = mario.getBody().x + mario.getBody().width + offset * 2;
        float wH = mario.getBody().y + mario.getBody().height + offset * 2;
        Rectangle offsetBounds = rectPool.obtain();
		offsetBounds.set(wX, wY, wW, wH);
     	return offsetBounds;
	}
	
    // --------------------
    public World()
    {
        /*world.setContactListener(new ContactListener()
        {

            @Override
            public void beginContact(Contact contact)
            {
                if(contact.getFixtureA().getBody().getUserData() instanceof Enemy)
				{
					if(contact.getFixtureB().getBody().getUserData() instanceof EnemyStopper || contact.getFixtureB().getBody().getUserData() instanceof Collider)
					{
						((Enemy)contact.getFixtureA().getBody().getUserData()).handleCollision(Enemy.ContactType.stopper);
					}
					else if(contact.getFixtureB().getBody().getUserData() instanceof Maryo)
					{
						((Enemy)contact.getFixtureA().getBody().getUserData()).handleCollision(Enemy.ContactType.player);
					}
				}
                else if(contact.getFixtureA().getBody().getUserData() instanceof EnemyStopper)
                {
                    if(contact.getFixtureB().getBody().getUserData() instanceof Enemy)
                    {
                        ((Enemy)contact.getFixtureB().getBody().getUserData()).handleCollision(Enemy.ContactType.stopper);
                    }
                }
                else if(contact.getFixtureA().getBody().getUserData() instanceof Collider)
                {
                    if(contact.getFixtureB().getBody().getUserData() instanceof Enemy)
                    {
                        ((Enemy)contact.getFixtureB().getBody().getUserData()).handleCollision(Enemy.ContactType.stopper);
                    }
                }
            }

            @Override
            public void endContact(Contact contact)
            {
                // TODO: Implement this method
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold)
            {
                if(contact.getFixtureA().getBody().getUserData() instanceof Enemy)
                {
                    if(contact.getFixtureB().getBody().getUserData() instanceof Enemy)
                    {
                        //TODO i don't know if its a good idea or not to disable contact between 2 enemies
                        //contact.setEnabled(false);
                    }
                }
                else if(contact.getFixtureA().getBody().getUserData() instanceof EnemyStopper)
                {
                    if(contact.getFixtureB().getBody().getUserData() instanceof Maryo)
                    {
                        contact.setEnabled(false);
                    }
                }
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse)
            {
                // TODO: Implement this method
            }
        });*/

    }

    public Array<GameObject> getVisibleObjects()
    {
        return visibleObjects == null ? new Array<GameObject>() : visibleObjects;
    }
}
