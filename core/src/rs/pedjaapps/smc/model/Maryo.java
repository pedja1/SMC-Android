package rs.pedjaapps.smc.model;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;

import rs.pedjaapps.smc.Assets;

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

	Body body;
    Fixture sensorFixture;

    Array<MarioState> usedStates = new Array<MarioState>();

    public Maryo(Vector3 position, Vector2 size, World world, Array<MarioState> usedStates)
    {
        super(new Rectangle(position.x, position.y, size.x, size.y), position);
        this.usedStates = usedStates;
		body = createBody(world, position);
    }

	private Body createBody(World world, Vector3 position)
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
		fixtureDef.friction = /*0.5f*/0;
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
	}

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
            if (getBody().getLinearVelocity().y > 0)
            {
                marioFrame = isFacingLeft() ? Assets.loadedRegions.get(TKey.jump_left + ":" + marioState) : Assets.loadedRegions.get(TKey.jump_right + ":" + marioState);
            }
            else
            {
                marioFrame = isFacingLeft() ? Assets.loadedRegions.get(TKey.fall_left + ":" + marioState) : Assets.loadedRegions.get(TKey.fall_right + ":" + marioState);
            }
        }
        spriteBatch.draw(marioFrame, getBody().getPosition().x - getBounds().width/2, getBody().getPosition().y - getBounds().height/2, bounds.width, bounds.height);
    }

    @Override
    public void update(float delta)
    {
        stateTime += delta;
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

    public void setBody(Body body)
    {
        this.body = body;
    }

    public Body getBody()
    {
        return body;
    }

    public Fixture getSensorFixture()
    {
        return sensorFixture;
    }

}
