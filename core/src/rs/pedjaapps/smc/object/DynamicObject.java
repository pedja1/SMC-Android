package rs.pedjaapps.smc.object;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.List;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.utility.Constants;

public abstract class DynamicObject extends GameObject
{
	public float stateTime;
	
	private static final float ACCELERATION     = 20f;
    protected static final float DAMP             = 0.90f;
    protected static final float DEF_MAX_VEL = 4f;
	
	public boolean grounded = false;

    long lasHitSoundPlayed;
	
	public DynamicObject(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
    }
	
	protected void updatePosition(float deltaTime)
	{
		velocity.scl(deltaTime);

		position.add(velocity);
        body.x = position.x;
        body.y = position.y;
        updateBounds();

		velocity.scl(1 / deltaTime);
	}
	
	@Override
    public void update(float delta)
    {
        // Setting initial vertical acceleration 
        acceleration.y = Constants.GRAVITY;

        // Convert acceleration to frame time
        acceleration.scl(delta);

        // apply acceleration to change velocity
        velocity.add(acceleration);

        // checking collisions with the surrounding blocks depending on Bob's velocity
        checkCollisionWithBlocks(delta);

        // apply damping to halt Maryo nicely 
        velocity.x *= DAMP;

        // ensure terminal velocity is not exceeded
        if (velocity.x > maxVelocity()) {
            velocity.x = maxVelocity();
        }
        if (velocity.x < -maxVelocity()) {
            velocity.x = -maxVelocity();
        }

        stateTime += delta;
    }

    /** Collision checking **/
    protected void checkCollisionWithBlocks(float delta) 
    {
        // scale velocity to frame units 
        velocity.scl(delta);

        // we first check the movement on the horizontal X axis

        // simulate maryos's movement on the X
        body.x += velocity.x;

		List<GameObject> surroundingObjects = world.level.gameObjects;//world.getSurroundingObjects(this, 1);
        // if m collides, make his horizontal velocity 0
        //noinspection ForLoopReplaceableByForEach
        for(int i = 0; i < surroundingObjects.size(); i++)
        //for (GameObject object : surroundingObjects)
		{
            GameObject object = surroundingObjects.get(i);
            if (object == null) continue;
            if (body.overlaps(object.body))
			{
				handleCollision(object, false);
            }
            else if((object instanceof Box && ((Box) object).itemObject != null && body.overlaps(((Box) object).itemObject.body)))
            {
                handleCollision(((Box) object).itemObject, false);
            }
        }
        if(body.x < 0 || body.x + body.width > world.level.width)
        {
            velocity.x = 0;
        }

        // reset the x position of the collision box
        body.x = position.x;

        // the same thing but on the vertical Y axis

        body.y += velocity.y;

        //noinspection ForLoopReplaceableByForEach
        for(int i = 0; i < surroundingObjects.size(); i++)
        //for (GameObject object : surroundingObjects)
		{
            GameObject object = surroundingObjects.get(i);
            if (object == null) continue;
            if (body.overlaps(object.body))
			{
				handleCollision(object, true);
            }
            else if((object instanceof Box && ((Box) object).itemObject != null && body.overlaps(((Box) object).itemObject.body)))
            {
                handleCollision(((Box) object).itemObject, true);
            }
        }
        if(body.y < 0)
        {
            handleDroppedBelowWorld();
        }

        // reset the collision box's position on Y
        body.y = position.y;

        // update position
        position.add(velocity);
        body.x = position.x;
        body.y = position.y;
        updateBounds();

        // un-scale velocity (not in frame time)
        velocity.scl(1 / delta);
    }

    protected void handleDroppedBelowWorld()
    {
        //TODO for now only prevent it from dropping below
        if (velocity.y < 0)
        {
            grounded = true;
        }
        velocity.y = 0;
    }

    protected void handleCollision(GameObject object, boolean vertical)
	{
		if(object instanceof Sprite && ((Sprite)object).type == Sprite.Type.massive)
		{
			if(vertical)
			{
                if(velocity.y > 0 && this instanceof Maryo && !(object instanceof Box))
                {
                    if(System.currentTimeMillis() - lasHitSoundPlayed > 200)
                    {
                        Sound sound = Assets.manager.get("data/sounds/wall_hit.wav");
                        if (sound != null && Assets.playSounds)
                        {
                            sound.play();
                            lasHitSoundPlayed = System.currentTimeMillis();
                        }
                    }
                }
				if (velocity.y < 0) 
				{
					grounded = true;
				}
                velocity.y = 0;
			}
			else
			{
				velocity.x = 0;
			}
		}
		else if(object instanceof Sprite && ((Sprite)object).type == Sprite.Type.halfmassive)
		{
			if(velocity.y < 0 && position.y > object.position.y + object.body.height)
			{
				grounded = true;
				velocity.y = 0;
			}
		}
	}
	
	public abstract float maxVelocity();
}
