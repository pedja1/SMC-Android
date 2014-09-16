package rs.pedjaapps.smc.model;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.utility.Constants;

public class Maryo extends GameObject
{
    protected float stateTime;

    public enum MarioState
    {
        small, big, fire, ice, ghost, flying
    }

    private static final float RUNNING_FRAME_DURATION = 0.08f;

    WorldState worldState = WorldState.IDLE;
    MarioState marioState = MarioState.small;
    boolean facingLeft = false;
    boolean longJump = false;

    Array<MarioState> usedStates = new Array<MarioState>();

    private static final long LONG_JUMP_PRESS   = 150l;
    private static final float ACCELERATION     = 20f;
    private static final float GRAVITY          = -20f;
    private static final float MAX_JUMP_SPEED   = 7f;
    private static final float DAMP             = 0.90f;
    private static final float MAX_VEL          = 4f;

    public Maryo(World world, Vector3 position, Vector2 size, Array<MarioState> usedStates)
    {
        super(world, size, position);
        this.usedStates = usedStates;

    }

	/*private Body createBody(World world, Vector3 position)
	{
        //TODO body should be resized dynamically depending on maryo's state
		BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x + bounds.width / 2, position.y + bounds.height / 2);
        bodyDef.fixedRotation = true;

        Body body = world.createBody(bodyDef);

        MassData massData = new MassData();
        massData.mass = 90;
        body.setMassData(massData);
        body.setUserData(this);
        //body.setLinearDamping(2.0f);

		PolygonShape polygonShape = new PolygonShape();
        Vector2[] vertices = new Vector2[8];
        vertices[0] = new Vector2((bounds.width * -0.4f)/2, (bounds.height * -0.65f)/2);
        vertices[1] = new Vector2((bounds.width * -0.15f)/2, (bounds.height * -0.875f)/2);
        vertices[2] = new Vector2((bounds.width * 0.15f)/2, (bounds.height * -0.875f)/2);
        vertices[3] = new Vector2((bounds.width * 0.4f)/2, (bounds.height * -0.65f)/2);
        vertices[4] = new Vector2((bounds.width * 0.4f)/2, (bounds.height * 0.8f)/2);
        vertices[5] = new Vector2((bounds.width * 0.3f)/2, (bounds.height * 0.875f)/2);
        vertices[6] = new Vector2((bounds.width * -0.3f)/2, (bounds.height * 0.875f)/2);
        vertices[7] = new Vector2((bounds.width * -0.4f)/2, (bounds.height * 0.8f)/2);
        polygonShape.set(vertices);

        FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;
		fixtureDef.density = 1062;
		fixtureDef.friction = 0;
        fixtureDef.restitution = 0.5f;
		//fixtureDef.restitution = 1;

        body.createFixture(fixtureDef);

        polygonShape = new PolygonShape();
        //polygonShape.setAsBox(bounds.width/4, .1f);
        vertices = new Vector2[4];
        vertices[0] = new Vector2((bounds.width * -0.3f)/2, (bounds.height * -0.65f)/2);
        vertices[1] = new Vector2((bounds.width * -0.3f)/2, (-bounds.height)/2);
        vertices[2] = new Vector2((bounds.width * 0.3f)/2, (-bounds.height)/2);
        vertices[3] = new Vector2((bounds.width * 0.3f)/2, (bounds.height * -0.65f)/2);
        polygonShape.set(vertices);
        sensorFixture = body.createFixture(polygonShape, 0);

        body.setBullet(true);

        polygonShape.dispose();
		return body;
	}*/

    public void loadTextures()
    {
        for(MarioState ms : usedStates)
        {
            loadTextures(ms.toString());
        }
    }

    private void loadTextures(String state)
    {
        TextureAtlas atlas = Assets.manager.get("data/maryo/" + state + ".pack");

        Assets.loadedRegions.put(TKey.stand_right + ":" + state, atlas.findRegion(TKey.stand_right.toString()));
        TextureRegion tmp = new TextureRegion(Assets.loadedRegions.get(TKey.stand_right + ":" + state));
        tmp.flip(true, false);
        Assets.loadedRegions.put(TKey.stand_left + ":" + state, tmp);

        TextureRegion[] walkRightFrames = new TextureRegion[4];
        walkRightFrames[0] = Assets.loadedRegions.get(TKey.stand_right + ":" + state);
        walkRightFrames[1] = atlas.findRegion(TKey.walk_right_1 + "");
        walkRightFrames[2] = atlas.findRegion(TKey.walk_right_2 + "");
        walkRightFrames[3] = walkRightFrames[1];
        Assets.animations.put(AKey.walk_right + ":" + state, new Animation(RUNNING_FRAME_DURATION, walkRightFrames));

        TextureRegion[] walkLeftFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++)
        {
            walkLeftFrames[i] = new TextureRegion(walkRightFrames[i]);
            walkLeftFrames[i].flip(true, false);
        }
        Assets.animations.put(AKey.walk_left + ":" + state, new Animation(RUNNING_FRAME_DURATION, walkLeftFrames));

        Assets.loadedRegions.put(TKey.jump_right + ":" + state, atlas.findRegion(TKey.jump_right.toString()));
        tmp = new TextureRegion(Assets.loadedRegions.get(TKey.jump_right.toString() + ":" + state));
        tmp.flip(true, false);
        Assets.loadedRegions.put(TKey.jump_left + ":" + state, tmp);

        Assets.loadedRegions.put(TKey.fall_right + ":" + state, atlas.findRegion(TKey.fall_right.toString()));
        tmp = new TextureRegion(Assets.loadedRegions.get(TKey.fall_right.toString() + ":" + state));
        tmp.flip(true, false);
        Assets.loadedRegions.put(TKey.fall_left + ":" + state, tmp);

        Assets.loadedRegions.put(TKey.dead_right + ":" + state, atlas.findRegion(TKey.dead_right.toString()));
        tmp = new TextureRegion(Assets.loadedRegions.get(TKey.dead_right.toString() + ":" + state));
        tmp.flip(true, false);
        Assets.loadedRegions.put(TKey.dead_left + ":" + state, tmp);

        Assets.loadedRegions.put(TKey.duck_right + ":" + state, atlas.findRegion(TKey.duck_right.toString()));
        tmp = new TextureRegion(Assets.loadedRegions.get(TKey.duck_right.toString() + ":" + state));
        tmp.flip(true, false);
        Assets.loadedRegions.put(TKey.duck_left + ":" + state, tmp);
    }

    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion marioFrame = isFacingLeft() ? Assets.loadedRegions.get(TKey.stand_left + ":" + marioState) : Assets.loadedRegions.get(TKey.stand_right + ":" + marioState);
        if (worldState.equals(WorldState.WALKING))
        {
            marioFrame = isFacingLeft() ? Assets.animations.get(AKey.walk_left + ":" + marioState).getKeyFrame(getStateTime(), true) : Assets.animations.get(AKey.walk_right + ":" + marioState).getKeyFrame(getStateTime(), true);
        }
        else if(worldState == WorldState.DUCKING)
        {
            marioFrame = isFacingLeft() ? Assets.loadedRegions.get(TKey.duck_left + ":" + marioState) : Assets.loadedRegions.get(TKey.duck_right + ":" + marioState);
        }
        else if (getWorldState().equals(WorldState.JUMPING))
        {
            if (velocity.y > 0)
            {
                marioFrame = isFacingLeft() ? Assets.loadedRegions.get(TKey.jump_left + ":" + marioState) : Assets.loadedRegions.get(TKey.jump_right + ":" + marioState);
            }
            else
            {
                marioFrame = isFacingLeft() ? Assets.loadedRegions.get(TKey.fall_left + ":" + marioState) : Assets.loadedRegions.get(TKey.fall_right + ":" + marioState);
            }
        }
        spriteBatch.draw(marioFrame, position.x, position.y, bounds.width, bounds.height);
    }

    @Override
    public void update(float delta)
    {
        // Setting initial vertical acceleration 
        acceleration.y = GRAVITY;

        // Convert acceleration to frame time
        acceleration.scl(delta);

        // apply acceleration to change velocity
        velocity.add(acceleration);

        // checking collisions with the surrounding blocks depending on Bob's velocity
        checkCollisionWithBlocks(delta);

        // apply damping to halt Maryo nicely 
        velocity.x *= DAMP;

        // ensure terminal velocity is not exceeded
        if (velocity.x > MAX_VEL) {
            velocity.x = MAX_VEL;
        }
        if (velocity.x < -MAX_VEL) {
            velocity.x = -MAX_VEL;
        }
        
        stateTime += delta;

    }

    /** Collision checking **/
    private void checkCollisionWithBlocks(float delta) 
    {
        // scale velocity to frame units 
        velocity.scl(delta);


        // we first check the movement on the horizontal X axis
        /*int startX, endX;
        int startY = (int) bounds.y;
        int endY = (int) (bounds.y + bounds.height);
        // if Bob is heading left then we check if he collides with the block on his left
        // we check the block on his right otherwise
        if (velocity.x < 0) {
            startX = endX = (int) Math.floor(bounds.x + velocity.x);
        } else {
            startX = endX = (int) Math.floor(bounds.x + bounds.width + velocity.x);
        }*/

        // get the block(s) maryo can collide with
        //populateCollidableBlocks(startX, startY, endX, endY);

        // simulate maryos's movement on the X
        bounds.x += velocity.x;

        // if bob collides, make his horizontal velocity 0
        for (GameObject object : world.getVisibleObjects()) 
		{
            if (object == null) continue;
            if (object instanceof Sprite && ((Sprite)object).getType() == Sprite.Type.massive && bounds.overlaps(object.getBounds())) 
			{
                velocity.x = 0;
				break;
            }
        }

        // reset the x position of the collision box
        bounds.x = position.x;

        // the same thing but on the vertical Y axis
        /*startX = (int) bounds.x;
        endX = (int) (bounds.x + bounds.width);
        if (velocity.y < 0) {
            startY = endY = (int) Math.floor(bounds.y + velocity.y);
        } else {
            startY = endY = (int) Math.floor(bounds.y + bounds.height + velocity.y);
        }*/

        //populateCollidableBlocks(startX, startY, endX, endY);

        bounds.y += velocity.y;

        for (GameObject object : world.getVisibleObjects()) 
		{
            if (object == null) continue;
            if (object instanceof Sprite && ((Sprite)object).getType() == Sprite.Type.massive && bounds.overlaps(object.getBounds())) 
			{
                velocity.y = 0;
				break;
            }
        }
        // reset the collision box's position on Y
        bounds.y = position.y;

        // update Bob's position
        position.add(velocity);
        bounds.x = position.x;
        bounds.y = position.y;

        // un-scale velocity (not in frame time)
        velocity.scl(1 / delta);

    }

    public boolean isFacingLeft()
    {
        return facingLeft;
    }

    public void setFacingLeft(boolean facingLeft)
    {
        this.facingLeft = facingLeft;
    }

    public WorldState getWorldState()
    {
        return worldState;
    }

    public void setWorldState(WorldState newWorldState)
    {
        this.worldState = newWorldState;
    }

    public boolean isLongJump()
    {
        return longJump;
    }

    public void setLongJump(boolean longJump)
    {
        this.longJump = longJump;
    }


    public void setBounds(Rectangle bounds)
    {
        this.bounds = bounds;
    }


    public void setStateTime(float stateTime)
    {
        this.stateTime = stateTime;
    }


    public float getStateTime()
    {
        return stateTime;
    }
}
