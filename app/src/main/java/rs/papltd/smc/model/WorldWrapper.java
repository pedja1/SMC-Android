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

    /**
     * Return only the blocks that need to be drawn *
     */
    public List<Sprite> getDrawableSprites(float camX, float camY, boolean getFront)
    {
        //TODO we only check if x and y of the sprite is in the world, but if sprite is large enough
        // its x and y could be outside of the world while rest of the sprite is in bounds, this will cause
        // sprite to suddenly disappear
        // this can be solved by checking if any part of the sprite is in view, not just x and y
        List<Sprite> sprites = new ArrayList<Sprite>();
        float wX = camX - Constants.CAMERA_WIDTH / 2 - 1;
        float wY = camY - Constants.CAMERA_HEIGHT / 2 - 1;
        float wX2 = wX + Constants.CAMERA_WIDTH + 1;
        float wY2 = wY + Constants.CAMERA_HEIGHT + 1;
        for(Sprite sprite : level.getSprites())
        {
            Vector2 position = sprite.getPosition();
            if(position.x >= wX && position.x <= wX2 && position.y >= wY && position.y <= wY2)
            {
                if(getFront)
                {
                    if(sprite.isFront())
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
        createWorld();
		world.setContactListener(new ContactListener(){

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

    private void createWorld()//TODO needs to be done differently, level loading will also load all textures for each object and it should be done on different screen and asynchronously
    {
        LevelLoader levelLoader = new LevelLoader(Gdx.files.absolute(Assets.mountedObbPath + "/levels/level_1.smclvl").readString(), world);
        level = levelLoader.getLevel();//LevelLoader.loadLevel(1);
        mario = new Maryo(level.getSpanPosition(), world);
    }

}
