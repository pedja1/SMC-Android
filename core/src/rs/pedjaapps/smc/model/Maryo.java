package rs.pedjaapps.smc.model;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.utility.GameSaveUtility;
import rs.pedjaapps.smc.model.enemy.Enemy;

public class Maryo extends DynamicObject
{

    public enum MarioState
    {
        small, big, fire, ice, ghost, flying
    }

    private static final float RUNNING_FRAME_DURATION = 0.08f;
	
	protected static final float MAX_VEL          = 4f;
	
    WorldState worldState = WorldState.JUMPING;
    MarioState marioState = MarioState.small;
    boolean facingLeft = false;
    boolean longJump = false;

    Array<MarioState> usedStates = new Array<MarioState>();

	private boolean handleCollision = true;
	DyingAnimation dyingAnim = new DyingAnimation();
    
    public Maryo(World world, Vector3 position, Vector2 size, Array<MarioState> usedStates)
    {
        super(world, size, position);
        this.usedStates = usedStates;
        setupBoundingBox();
    }

    private void setupBoundingBox()
    {
        body.x = bounds.x + bounds.width / 4;
        body.width = bounds.width / 2;
        position.x = body.x;

        position.y = body.y = bounds.y += 0.5f;
    }

	@Override
    public void updateBounds()
    {
        bounds.x = body.x - bounds.width / 4;
        bounds.y = body.y;
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
            if (velocity.y > 0)
            {
                marioFrame = isFacingLeft() ? Assets.loadedRegions.get(TKey.jump_left + ":" + marioState) : Assets.loadedRegions.get(TKey.jump_right + ":" + marioState);
            }
            else
            {
                marioFrame = isFacingLeft() ? Assets.loadedRegions.get(TKey.fall_left + ":" + marioState) : Assets.loadedRegions.get(TKey.fall_right + ":" + marioState);
            }
        }
		else if(worldState == WorldState.DYING)
		{
			marioFrame = isFacingLeft() ? Assets.loadedRegions.get(TKey.dead_left + ":" + marioState) : Assets.loadedRegions.get(TKey.dead_right + ":" + marioState);
		}
        spriteBatch.draw(marioFrame, bounds.x, bounds.y, bounds.width, bounds.height);
    }

	@Override
	public void update(float delta)
	{
		if(worldState == WorldState.DYING)
		{
			stateTime += delta;
			dyingAnim.update(delta);
		}
		else
		{
			super.update(delta);
		}
	}

	@Override
	protected void handleCollision(GameObject object, boolean vertical)
	{
		if(!handleCollision)return;
		super.handleCollision(object, vertical);
		if(object instanceof Coin)
		{
			Coin coin = (Coin)object;
			Sound sound = null;
			if(coin.textureAtlas.contains("yellow"))
			{
				sound = Assets.manager.get("data/sounds/item/goldpiece_1.ogg");
			}
			else
			{
				sound = Assets.manager.get("data/sounds/item/goldpiece_red.wav");
			}
			if(sound != null)sound.play();
			GameSaveUtility.getInstance().save.coins++;
			
			world.trashObjects.add(object);
		}
		else if(object instanceof Enemy)
		{
			worldState = WorldState.DYING;
			dyingAnim.start();
		}
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
		if(worldState == WorldState.DYING)return;
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

    public void setStateTime(float stateTime)
    {
        this.stateTime = stateTime;
    }


    public float getStateTime()
    {
        return stateTime;
    }
	
	public boolean checkGrounded()
    {
        grounded = velocity.y == 0;
		return grounded;
    }
	
	public void setGrounded(boolean grounded)
	{
		this.grounded = grounded;
	}
	
	public boolean isGrounded()
	{
		return grounded;
	}

	@Override
	public float maxVelocity()
	{
		return MAX_VEL;
	}
	
	public class DyingAnimation
	{
		private float diedTime;
		boolean firstDelayDone, secondDelayDone;
		Vector3 diedPosition;
		
		public void start()
		{
			diedTime = stateTime;
			handleCollision = false;
			diedPosition = new Vector3(position);
		}
		
		public void update(float delat)
		{
			velocity.x = 0;
			if(bounds.y + bounds.height < 0)//first check if player is visible
			{
				return;
			}
			
			if(stateTime - diedTime < 0.5f)//delay 500ms
			{
				return;
			}

			//animate player up a bit
			position.y += 13f * delat;
			body.y = position.y;
			updateBounds();
			/*if()
			{
				
			}*/
			
		}
	}
}
