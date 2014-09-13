package rs.pedjaapps.smc.model;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import java.util.*;

import rs.pedjaapps.smc.model.enemy.Enemy;
import rs.pedjaapps.smc.model.enemy.EnemyStopper;
import rs.pedjaapps.smc.utility.Constants;


public class WorldWrapper
{

    World world = new World(new Vector2(0, Constants.GRAVITY), true);

    Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    /**
     * Our player controlled hero *
     */
    Maryo mario;
    /**
     * A world has a level through which Mario needs to go through *
     */
    Level level;

    public void setDebugRenderer(Box2DDebugRenderer debugRenderer)
    {
        this.debugRenderer = debugRenderer;
    }

    public Box2DDebugRenderer getDebugRenderer()
    {
        return debugRenderer;
    }

    public World getWorld()
    {
        return world;
    }

    // Getters -----------

    public Maryo getMario()
    {
        return mario;
    }

    public Level getLevel()
    {
        return level;
    }

    public void setWorld(World world)
    {
        this.world = world;
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
    public List<GameObject> getDrawableObjects(float camX, float camY/*, boolean getFront*/)
    {
        List<GameObject> sprites = new ArrayList<GameObject>();
        float wX = camX - Constants.CAMERA_WIDTH / 2 - 1;
        float wY = camY - Constants.CAMERA_HEIGHT / 2 - 1;
        float wW = Constants.CAMERA_WIDTH + 1;
        float wH = Constants.CAMERA_HEIGHT + 1;
        Rectangle worldBounds = new Rectangle(wX, wY, wW, wH);
        for (GameObject object : level.getGameObjects())
        {
            Rectangle bounds = object.getBounds();
            if (bounds.overlaps(worldBounds) || object instanceof Enemy)
            {
                sprites.add(object);
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
        return sprites;
    }

    // --------------------
    public WorldWrapper()
    {
        world.setContactListener(new ContactListener()
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
        });
    }

}
