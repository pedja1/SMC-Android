package rs.papltd.smc.model;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;

import java.util.*;

import rs.papltd.smc.Assets;
import rs.papltd.smc.utility.*;


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
     */
    public List<Sprite> getDrawableSprites(float camX, float camY, boolean getFront)
    {
        //TODO we only check if x and y of the sprite is in the world, but if sprite is large enough
        // its x and y could be outside of the world while rest of the sprite is in bounds, this will cause
        // sprite to suddenly disappear
        // this can be solved by checking if any part of the sprite is in view, not just x and y

        //TODO Issue fixed, not tested, test and remove todos
        List<Sprite> sprites = new ArrayList<Sprite>();
        float wX = camX - Constants.CAMERA_WIDTH / 2 - 1;
        float wY = camY - Constants.CAMERA_HEIGHT / 2 - 1;
        float wW = Constants.CAMERA_WIDTH + 1;
        float wH = Constants.CAMERA_HEIGHT + 1;
        Rectangle worldBounds = new Rectangle(wX, wY, wW, wH);
        for (Sprite sprite : level.getSprites())
        {
            Rectangle bounds = sprite.getBounds();
            if (bounds.overlaps(worldBounds))
            {
                if (getFront)
                {
                    if (sprite.isFront())
                    {
                        sprites.add(sprite);
                    }
                }
                else
                {
                    sprites.add(sprite);
                }
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
                // TODO: Implement this method
                System.out.println(contact.toString());
                //throw new RuntimeException();
            }

            @Override
            public void endContact(Contact contact)
            {
                // TODO: Implement this method
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold)
            {
                // TODO: Implement this method
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse)
            {
                // TODO: Implement this method
            }
        });
    }

}
