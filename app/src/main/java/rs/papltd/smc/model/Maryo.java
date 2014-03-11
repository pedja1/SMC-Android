package rs.papltd.smc.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import rs.papltd.smc.Assets;

public class Maryo
{
    public enum WorldState
    {
        IDLE, WALKING, JUMPING, DYING, DUCKING
	}

    public enum MarioState
    {
        SMALL, BIG, FIRE, ICE, GHOST, FLYING
    }

    public static final float HEIGHT = 0.85f;
    public static final float WIDTH = 0.75f;
    private static final float RUNNING_FRAME_DURATION = 0.06f;

    Rectangle bounds = new Rectangle();
    WorldState worldState = WorldState.IDLE;
    MarioState marioState = MarioState.SMALL;
    boolean facingLeft = true;
    boolean longJump = false;
	float stateTime;

	Body body;
    Fixture sensorFixture;

    TextureHolder textureHolder;

    public Maryo(Vector2 position, World world)
    {
        this.bounds.x = position.x;
        this.bounds.y = position.y;
        this.bounds.height = HEIGHT;
        this.bounds.width = WIDTH;
		body = createBody(world, position);
        loadTextures();
    }

	private Body createBody(World world, Vector2 position)
	{
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
        vertices[0] = new Vector2((WIDTH * -0.4f)/2, (HEIGHT * -0.65f)/2);
        vertices[1] = new Vector2((WIDTH * -0.15f)/2, (HEIGHT * -0.875f)/2);
        vertices[2] = new Vector2((WIDTH * 0.15f)/2, (HEIGHT * -0.875f)/2);
        vertices[3] = new Vector2((WIDTH * 0.4f)/2, (HEIGHT * -0.65f)/2);
        vertices[4] = new Vector2((WIDTH * 0.4f)/2, (HEIGHT * 0.8f)/2);
        vertices[5] = new Vector2((WIDTH * 0.3f)/2, (HEIGHT * 0.875f)/2);
        vertices[6] = new Vector2((WIDTH * -0.3f)/2, (HEIGHT * 0.875f)/2);
        vertices[7] = new Vector2((WIDTH * -0.4f)/2, (HEIGHT * 0.8f)/2);
        polygonShape.set(vertices);

        FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;
		fixtureDef.density = 10.0f;
		fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.5f;
		//fixtureDef.restitution = 1;

        body.createFixture(fixtureDef);

        polygonShape = new PolygonShape();
        //polygonShape.setAsBox(bounds.width/4, .1f);
        vertices = new Vector2[4];
        vertices[0] = new Vector2((WIDTH * -0.3f)/2, (HEIGHT * -0.65f)/2);
        vertices[1] = new Vector2((WIDTH * -0.3f)/2, (-HEIGHT)/2);
        vertices[2] = new Vector2((WIDTH * 0.3f)/2, (-HEIGHT)/2);
        vertices[3] = new Vector2((WIDTH * 0.3f)/2, (HEIGHT * -0.65f)/2);
        polygonShape.set(vertices);
        sensorFixture = body.createFixture(polygonShape, 0);

        body.setBullet(true);

        polygonShape.dispose();
		return body;
	}

    private void loadTextures()
    {
        textureHolder = new TextureHolder();
        loadTexturesSmall();
        loadTexturesBig();
    }

    private void loadTexturesSmall()
    {
        TextureAtlas marioAtlasSmall = new TextureAtlas(Gdx.files.absolute(Assets.mountedObbPath + "/maryo/small.pack"));
        textureHolder.idleRightSmall = marioAtlasSmall.findRegion("stand-right");
        textureHolder.idleLeftSmall = new TextureRegion(textureHolder.idleRightSmall);
        textureHolder.idleLeftSmall.flip(true, false);

        TextureRegion[] walkRightFramesSmall = new TextureRegion[3];
        walkRightFramesSmall[0] = textureHolder.idleRightSmall;
        walkRightFramesSmall[1] = marioAtlasSmall.findRegion("walk-right-1");
        walkRightFramesSmall[2] = marioAtlasSmall.findRegion("walk-right-2");
        textureHolder.walkRightAnimationSmall = new Animation(RUNNING_FRAME_DURATION, walkRightFramesSmall);

        TextureRegion[] walkLeftFramesSmall = new TextureRegion[3];
        for (int i = 0; i < 3; i++)
        {
            walkLeftFramesSmall[i] = new TextureRegion(walkRightFramesSmall[i]);
            walkLeftFramesSmall[i].flip(true, false);
        }
        textureHolder.walkLeftAnimationSmall = new Animation(RUNNING_FRAME_DURATION, walkLeftFramesSmall);

        textureHolder.jumpRightSmall = marioAtlasSmall.findRegion("jump-right");
        textureHolder.jumpLeftSmall = new TextureRegion(textureHolder.jumpRightSmall);
        textureHolder.jumpLeftSmall.flip(true, false);

        textureHolder.fallRightSmall = marioAtlasSmall.findRegion("fall-right");
        textureHolder.fallLeftSmall = new TextureRegion(textureHolder.fallRightSmall);
        textureHolder.fallLeftSmall.flip(true, false);

        textureHolder.deadRightSmall = marioAtlasSmall.findRegion("dead-right");
        textureHolder.deadLeftSmall = new TextureRegion(textureHolder.deadRightSmall);
        textureHolder.deadLeftSmall.flip(true, false);

        textureHolder.duckRightSmall = marioAtlasSmall.findRegion("duck-right");
        textureHolder.duckLeftSmall = new TextureRegion(textureHolder.duckRightSmall);
        textureHolder.duckLeftSmall.flip(true, false);
    }

    private void loadTexturesBig()
    {
        TextureAtlas marioAtlasBig = new TextureAtlas(Gdx.files.absolute(Assets.mountedObbPath + "/maryo/big.pack"));
        textureHolder.idleRightBig = marioAtlasBig.findRegion("stand-right");
        textureHolder.idleLeftBig = new TextureRegion(textureHolder.idleRightBig);
        textureHolder.idleLeftBig.flip(true, false);

        TextureRegion[] walkRightFramesBig = new TextureRegion[3];
        walkRightFramesBig[0] = textureHolder.idleRightBig;
        walkRightFramesBig[1] = marioAtlasBig.findRegion("walk-right-1");
        walkRightFramesBig[2] = marioAtlasBig.findRegion("walk-right-2");
        textureHolder.walkRightAnimationBig = new Animation(RUNNING_FRAME_DURATION, walkRightFramesBig);

        TextureRegion[] walkLeftFramesBig = new TextureRegion[3];
        for (int i = 0; i < 3; i++)
        {
            walkLeftFramesBig[i] = new TextureRegion(walkRightFramesBig[i]);
            walkLeftFramesBig[i].flip(true, false);
        }
        textureHolder.walkLeftAnimationBig = new Animation(RUNNING_FRAME_DURATION, walkLeftFramesBig);

        textureHolder.jumpRightBig = marioAtlasBig.findRegion("jump-right");
        textureHolder.jumpLeftBig = new TextureRegion(textureHolder.jumpRightBig);
        textureHolder.jumpLeftBig.flip(true, false);

        textureHolder.fallRightBig = marioAtlasBig.findRegion("fall-right");
        textureHolder.fallLeftBig = new TextureRegion(textureHolder.fallRightBig);
        textureHolder.fallLeftBig.flip(true, false);

        textureHolder.duckRightBig = marioAtlasBig.findRegion("duck-right");
        textureHolder.duckLeftBig = new TextureRegion(textureHolder.duckRightBig);
        textureHolder.duckLeftBig.flip(true, false);
    }

    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion marioFrame = isFacingLeft() ? textureHolder.getIdleLeft() : textureHolder.getIdleRight();
        if (worldState.equals(WorldState.WALKING))
        {
            marioFrame = isFacingLeft() ? textureHolder.getWalkLeftAnimation().getKeyFrame(getStateTime(), true) : textureHolder.getWalkRightAnimation().getKeyFrame(getStateTime(), true);
        }
        else if(worldState == WorldState.DUCKING)
        {
            marioFrame = isFacingLeft() ? textureHolder.getDuckLeft() : textureHolder.getDuckRight();
        }
        else if (getWorldState().equals(WorldState.JUMPING))
        {
            if (getBody().getLinearVelocity().y > 0)
            {
                marioFrame = isFacingLeft() ? textureHolder.getJumpLeft() : textureHolder.getJumpRight();
            }
            else
            {
                marioFrame = isFacingLeft() ? textureHolder.getFallLeft() : textureHolder.getFallRight();
            }
        }
        spriteBatch.draw(marioFrame, getBody().getPosition().x - getBounds().width/2, getBody().getPosition().y - getBounds().height/2, WIDTH, HEIGHT);
    }

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

    public Vector2 getPosition()
    {
        return body.getPosition();
    }

    public Rectangle getBounds()
    {
        return bounds;
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

    class TextureHolder
    {
        private TextureRegion idleLeftSmall, idleLeftBig;
        private TextureRegion jumpLeftSmall, jumpLeftBig;
        private TextureRegion fallLeftSmall, fallLeftBig;
        private TextureRegion deadLeftSmall;
        private TextureRegion duckLeftSmall, duckLeftBig;

        private TextureRegion idleRightSmall, idleRightBig;
        private TextureRegion jumpRightSmall, jumpRightBig;
        private TextureRegion fallRightSmall, fallRightBig;
        private TextureRegion deadRightSmall;
        private TextureRegion duckRightSmall, duckRightBig;

        /**
         * Animations *
         */
        private Animation walkLeftAnimationSmall;
        private Animation walkRightAnimationSmall;
        private Animation walkLeftAnimationBig;
        private Animation walkRightAnimationBig;

        public TextureRegion getIdleLeft()
        {
            switch (marioState)
            {
                case SMALL:
                    return idleLeftSmall;
                case BIG:
                    return idleLeftBig;
            }
            return null;
        }

        public TextureRegion getIdleRight()
        {
            switch (marioState)
            {
                case SMALL:
                    return idleRightSmall;
                case BIG:
                    return idleRightBig;
            }
            return null;
        }

        public TextureRegion getFallRight()
        {
            switch (marioState)
            {
                case SMALL:
                    return fallRightSmall;
                case BIG:
                    return fallRightBig;
            }
            return null;
        }

        public TextureRegion getFallLeft()
        {
            switch (marioState)
            {
                case SMALL:
                    return fallLeftSmall;
                case BIG:
                    return fallLeftBig;
            }
            return null;
        }

        public TextureRegion getJumpRight()
        {
            switch (marioState)
            {
                case SMALL:
                    return jumpRightSmall;
                case BIG:
                    return jumpRightBig;
            }
            return null;
        }

        public TextureRegion getJumpLeft()
        {
            switch (marioState)
            {
                case SMALL:
                    return jumpLeftSmall;
                case BIG:
                    return jumpLeftBig;
            }
            return null;
        }

        public TextureRegion getDuckRight()
        {
            switch (marioState)
            {
                case SMALL:
                    return duckRightSmall;
                case BIG:
                    return duckRightBig;
            }
            return null;
        }

        public TextureRegion getDuckLeft()
        {
            switch (marioState)
            {
                case SMALL:
                    return duckLeftSmall;
                case BIG:
                    return duckLeftBig;
            }
            return null;
        }

        public Animation getWalkLeftAnimation()
        {
            switch (marioState)
            {
                case SMALL:
                    return walkLeftAnimationSmall;
                case BIG:
                    return walkLeftAnimationBig;
            }
            return null;
        }

        public Animation getWalkRightAnimation()
        {
            switch (marioState)
            {
                case SMALL:
                    return walkRightAnimationSmall;
                case BIG:
                    return walkRightAnimationBig;
            }
            return null;
        }
    }

}
