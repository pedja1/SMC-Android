package rs.pedjaapps.smc.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import java.util.Collections;
import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.object.enemy.Eato;
import rs.pedjaapps.smc.object.enemy.Enemy;
import rs.pedjaapps.smc.object.enemy.Flyon;
import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.screen.LoadingScreen;
import rs.pedjaapps.smc.utility.GameSaveUtility;
import rs.pedjaapps.smc.utility.LevelLoader;

public class Maryo extends DynamicObject
{
    public enum MaryoState
    {
        small, big, fire, ice, ghost, flying
    }

    //this could be all done dynamically, but this way we minimize allocation in game loop
    //omg, this is a lot of constants :D
    private static final String KEY_WALKING_LEFT_SMALL = AKey.walk_left + ":" + MaryoState.small;
    private static final String KEY_WALKING_RIGHT_SMALL = AKey.walk_right + ":" + MaryoState.small;
    private static final String KEY_WALKING_LEFT_BIG = AKey.walk_left + ":" + MaryoState.big;
    private static final String KEY_WALKING_RIGHT_BIG = AKey.walk_right + ":" + MaryoState.big;
    private static final String KEY_WALKING_LEFT_FIRE = AKey.walk_left + ":" + MaryoState.fire;
    private static final String KEY_WALKING_RIGHT_FIRE = AKey.walk_right + ":" + MaryoState.fire;
    private static final String KEY_WALKING_LEFT_FLYING = AKey.walk_left + ":" + MaryoState.flying;
    private static final String KEY_WALKING_RIGHT_FLYING = AKey.walk_right + ":" + MaryoState.flying;
    private static final String KEY_WALKING_LEFT_GHOST = AKey.walk_left + ":" + MaryoState.ghost;
    private static final String KEY_WALKING_RIGHT_GHOST = AKey.walk_right + ":" + MaryoState.ghost;
    private static final String KEY_WALKING_LEFT_ICE = AKey.walk_left + ":" + MaryoState.ice;
    private static final String KEY_WALKING_RIGHT_ICE = AKey.walk_right + ":" + MaryoState.ice;

    private static final String KEY_DUCK_LEFT_SMALL = TKey.duck_left + ":" + MaryoState.small;
    private static final String KEY_DUCK_RIGHT_SMALL = TKey.duck_right + ":" + MaryoState.small;
    private static final String KEY_JUMP_LEFT_SMALL = TKey.jump_left + ":" + MaryoState.small;
    private static final String KEY_JUMP_RIGHT_SMALL = TKey.jump_right + ":" + MaryoState.small;
    private static final String KEY_FALL_LEFT_SMALL = TKey.fall_left + ":" + MaryoState.small;
    private static final String KEY_FALL_RIGHT_SMALL = TKey.fall_right + ":" + MaryoState.small;
    private static final String KEY_DEAD_LEFT_SMALL = TKey.dead_left + ":" + MaryoState.small;
    private static final String KEY_DEAD_RIGHT_SMALL = TKey.dead_right + ":" + MaryoState.small;
    private static final String KEY_STAND_LEFT_SMALL = TKey.stand_left + ":" + MaryoState.small;
    private static final String KEY_STAND_RIGHT_SMALL = TKey.stand_right + ":" + MaryoState.small;

    private static final String KEY_DUCK_LEFT_BIG = TKey.duck_left + ":" + MaryoState.big;
    private static final String KEY_DUCK_RIGHT_BIG = TKey.duck_right + ":" + MaryoState.big;
    private static final String KEY_JUMP_LEFT_BIG = TKey.jump_left + ":" + MaryoState.big;
    private static final String KEY_JUMP_RIGHT_BIG = TKey.jump_right + ":" + MaryoState.big;
    private static final String KEY_FALL_LEFT_BIG = TKey.fall_left + ":" + MaryoState.big;
    private static final String KEY_FALL_RIGHT_BIG = TKey.fall_right + ":" + MaryoState.big;
    private static final String KEY_DEAD_LEFT_BIG = TKey.dead_left + ":" + MaryoState.big;
    private static final String KEY_DEAD_RIGHT_BIG = TKey.dead_right + ":" + MaryoState.big;
    private static final String KEY_STAND_LEFT_BIG = TKey.stand_left + ":" + MaryoState.big;
    private static final String KEY_STAND_RIGHT_BIG = TKey.stand_right + ":" + MaryoState.big;

    private static final String KEY_DUCK_LEFT_FIRE = TKey.duck_left + ":" + MaryoState.fire;
    private static final String KEY_DUCK_RIGHT_FIRE = TKey.duck_right + ":" + MaryoState.fire;
    private static final String KEY_JUMP_LEFT_FIRE = TKey.jump_left + ":" + MaryoState.fire;
    private static final String KEY_JUMP_RIGHT_FIRE = TKey.jump_right + ":" + MaryoState.fire;
    private static final String KEY_FALL_LEFT_FIRE = TKey.fall_left + ":" + MaryoState.fire;
    private static final String KEY_FALL_RIGHT_FIRE = TKey.fall_right + ":" + MaryoState.fire;
    private static final String KEY_DEAD_LEFT_FIRE = TKey.dead_left + ":" + MaryoState.fire;
    private static final String KEY_DEAD_RIGHT_FIRE = TKey.dead_right + ":" + MaryoState.fire;
    private static final String KEY_STAND_LEFT_FIRE = TKey.stand_left + ":" + MaryoState.fire;
    private static final String KEY_STAND_RIGHT_FIRE = TKey.stand_right + ":" + MaryoState.fire;

    private static final String KEY_DUCK_LEFT_FLYING = TKey.duck_left + ":" + MaryoState.flying;
    private static final String KEY_DUCK_RIGHT_FLYING = TKey.duck_right + ":" + MaryoState.flying;
    private static final String KEY_JUMP_LEFT_FLYING = TKey.jump_left + ":" + MaryoState.flying;
    private static final String KEY_JUMP_RIGHT_FLYING = TKey.jump_right + ":" + MaryoState.flying;
    private static final String KEY_FALL_LEFT_FLYING = TKey.fall_left + ":" + MaryoState.flying;
    private static final String KEY_FALL_RIGHT_FLYING = TKey.fall_right + ":" + MaryoState.flying;
    private static final String KEY_DEAD_LEFT_FLYING = TKey.dead_left + ":" + MaryoState.flying;
    private static final String KEY_DEAD_RIGHT_FLYING = TKey.dead_right + ":" + MaryoState.flying;
    private static final String KEY_STAND_LEFT_FLYING = TKey.stand_left + ":" + MaryoState.flying;
    private static final String KEY_STAND_RIGHT_FLYING = TKey.stand_right + ":" + MaryoState.flying;

    private static final String KEY_DUCK_LEFT_GHOST = TKey.duck_left + ":" + MaryoState.ghost;
    private static final String KEY_DUCK_RIGHT_GHOST = TKey.duck_right + ":" + MaryoState.ghost;
    private static final String KEY_JUMP_LEFT_GHOST = TKey.jump_left + ":" + MaryoState.ghost;
    private static final String KEY_JUMP_RIGHT_GHOST = TKey.jump_right + ":" + MaryoState.ghost;
    private static final String KEY_FALL_LEFT_GHOST = TKey.fall_left + ":" + MaryoState.ghost;
    private static final String KEY_FALL_RIGHT_GHOST = TKey.fall_right + ":" + MaryoState.ghost;
    private static final String KEY_DEAD_LEFT_GHOST = TKey.dead_left + ":" + MaryoState.ghost;
    private static final String KEY_DEAD_RIGHT_GHOST = TKey.dead_right + ":" + MaryoState.ghost;
    private static final String KEY_STAND_LEFT_GHOST = TKey.stand_left + ":" + MaryoState.ghost;
    private static final String KEY_STAND_RIGHT_GHOST = TKey.stand_right + ":" + MaryoState.ghost;

    private static final String KEY_DUCK_LEFT_ICE = TKey.duck_left + ":" + MaryoState.ice;
    private static final String KEY_DUCK_RIGHT_ICE = TKey.duck_right + ":" + MaryoState.ice;
    private static final String KEY_JUMP_LEFT_ICE = TKey.jump_left + ":" + MaryoState.ice;
    private static final String KEY_JUMP_RIGHT_ICE = TKey.jump_right + ":" + MaryoState.ice;
    private static final String KEY_FALL_LEFT_ICE = TKey.fall_left + ":" + MaryoState.ice;
    private static final String KEY_FALL_RIGHT_ICE = TKey.fall_right + ":" + MaryoState.ice;
    private static final String KEY_DEAD_LEFT_ICE = TKey.dead_left + ":" + MaryoState.ice;
    private static final String KEY_DEAD_RIGHT_ICE = TKey.dead_right + ":" + MaryoState.ice;
    private static final String KEY_STAND_LEFT_ICE = TKey.stand_left + ":" + MaryoState.ice;
    private static final String KEY_STAND_RIGHT_ICE = TKey.stand_right + ":" + MaryoState.ice;

    private static final float RUNNING_FRAME_DURATION = 0.08f;
    private static final float RESIZE_ANIMATION_DURATION = 0.977f;
    private static final float RESIZE_ANIMATION_FRAME_DURATION = RESIZE_ANIMATION_DURATION / 8f;

	protected static final float MAX_VEL          = 4f;
	private static final float GOD_MOD_TIMEOUT = 3000;//3 sec
	
    WorldState worldState = WorldState.JUMPING;
    private MaryoState maryoState = GameSaveUtility.getInstance().save.playerState;
    public boolean facingLeft = false;
    public boolean longJump = false;

    public float groundY = 0;

	private boolean handleCollision = true;
	DyingAnimation dyingAnim = new DyingAnimation();

    public Sound jumpSound = null;

    public Rectangle debugRayRect = new Rectangle();
	
	/**
	 * Makes player invincible and transparent for all enemies
	 * Used (for limited time) when player is downgraded (or if you hack the game :D
	 */
	boolean godMode = false;
	long godModeActivatedTime;

    Animation resizingAnimation;
    float resizeAnimStartTime;
    private MaryoState newState;//used with resize animation
    private MaryoState oldState;//used with resize animation
	
	//exit, enter
	private boolean exiting, entering;
	private LevelExit exit;
	private Vector3 exitStartPosition = new Vector3();
	private float exitVelocity = 0.5f;
	private int rotation = 0;
    
    public Maryo(World world, Vector3 position, Vector2 size)
    {
        super(world, size, position);
        setupBoundingBox();
		
		position.y = body.y = bounds.y += 0.5f;
    }

    private void setupBoundingBox()
    {
        float centerX = position.x + body.width / 2;
		switch(maryoState)
		{
			case small:
				bounds.width = 0.9f;
				bounds.height = 0.9f;
				break;
			case big:
			case fire:
			case ghost:
			case ice:
				bounds.height = 1.09f;
				bounds.width = 1.09f;
				break;
			case flying:
				break;
		}
		body.x = bounds.x + bounds.width / 4;
		body.width = bounds.width / 2;
		position.x = body.x;
		
        if(worldState == WorldState.DUCKING)
		{
			body.height = bounds.height / 2;
		}
		else
		{
			body.height = bounds.height * 0.9f;
		}

        position.x = body.x = centerX - body.width / 2;
    }

	@Override
    public void updateBounds()
    {
        bounds.x = body.x - bounds.width / 4;
        bounds.y = body.y;
    }

    public void initAssets()
    {
        MaryoState[] states = new MaryoState[]{MaryoState.small, MaryoState.big, MaryoState.fire, MaryoState.ghost, MaryoState.ice};
        for(MaryoState ms : states)
        {
            loadTextures(ms.toString());
        }
        setJumpSound();
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

        if (MaryoState.small.toString().equals(state))
        {
            Assets.loadedRegions.put(TKey.dead_right + ":" + state, atlas.findRegion(TKey.dead_right.toString()));
            tmp = new TextureRegion(Assets.loadedRegions.get(TKey.dead_right.toString() + ":" + state));
            tmp.flip(true, false);
            Assets.loadedRegions.put(TKey.dead_left + ":" + state, tmp);
        }

        Assets.loadedRegions.put(TKey.duck_right + ":" + state, atlas.findRegion(TKey.duck_right.toString()));
        tmp = new TextureRegion(Assets.loadedRegions.get(TKey.duck_right.toString() + ":" + state));
        tmp.flip(true, false);
        Assets.loadedRegions.put(TKey.duck_left + ":" + state, tmp);
    }

    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion marioFrame;
		if(exiting)
		{
			marioFrame = facingLeft ? Assets.loadedRegions.get(getTextureKey(TKey.stand_left)) : Assets.loadedRegions.get(getTextureKey(TKey.stand_right));
			
            float originX = bounds.width * 0.5f;
            float originY = bounds.height * 0.5f;
            spriteBatch.draw(marioFrame, bounds.x, bounds.y, originX, originY, bounds.width, bounds.height, 1, 1, rotation);
			
			return;
		}
        if(resizingAnimation != null && stateTime > resizeAnimStartTime + RESIZE_ANIMATION_DURATION)
        {
            resizeAnimStartTime = 0;
            resizingAnimation = null;
            ((GameScreen)world.screen).setGameState(GameScreen.GAME_STATE.GAME_RUNNING);
            godMode = false;
            maryoState = newState;
            newState = null;
            oldState = null;
            setupBoundingBox();
            GameSaveUtility.getInstance().save.playerState = maryoState;
        }
        if(resizingAnimation != null)
        {
            int index = resizingAnimation.getKeyFrameIndex(stateTime);
            marioFrame = resizingAnimation.getKeyFrames()[index];
            if(index == 0)
            {
                maryoState = oldState;
                setupBoundingBox();
            }
            else
            {
                maryoState = newState;
                setupBoundingBox();
            }
        }
        else if (worldState.equals(WorldState.WALKING))
        {
            marioFrame = facingLeft ? Assets.animations.get(getAnimationKey(AKey.walk_left)).getKeyFrame(stateTime, true) : Assets.animations.get(getAnimationKey(AKey.walk_right)).getKeyFrame(stateTime, true);
        }
        else if(worldState == WorldState.DUCKING)
        {
            marioFrame = facingLeft ? Assets.loadedRegions.get(getTextureKey(TKey.duck_left)) : Assets.loadedRegions.get(getTextureKey(TKey.duck_right));
        }
        else if (getWorldState().equals(WorldState.JUMPING))
        {
            if (velocity.y > 0)
            {
                marioFrame = facingLeft ? Assets.loadedRegions.get(getTextureKey(TKey.jump_left)) : Assets.loadedRegions.get(getTextureKey(TKey.jump_right));
            }
            else
            {
                marioFrame = facingLeft ? Assets.loadedRegions.get(getTextureKey(TKey.fall_left)) : Assets.loadedRegions.get(getTextureKey(TKey.fall_right));
            }
        }
		else if(worldState == WorldState.DYING)
		{
			marioFrame = facingLeft ? Assets.loadedRegions.get(getTextureKey(TKey.dead_left)) : Assets.loadedRegions.get(getTextureKey(TKey.dead_right));
		}
        else
        {
            marioFrame = facingLeft ? Assets.loadedRegions.get(getTextureKey(TKey.stand_left)) : Assets.loadedRegions.get(getTextureKey(TKey.stand_right));
        }
		
		//if god mode, make player half-transparent
		if(godMode)
		{
			Color color = spriteBatch.getColor();
			float oldA = color.a;
			
			color.a = 0.5f;
			spriteBatch.setColor(color);
			
			spriteBatch.draw(marioFrame, bounds.x, bounds.y, bounds.width, bounds.height);
			
			color.a = oldA;
			spriteBatch.setColor(color);
		}
		else
		{
        	spriteBatch.draw(marioFrame, bounds.x, bounds.y, bounds.width, bounds.height);
		}
    }

    private String getTextureKey(TKey tkey)
    {
        //and another OMG, can this be done differently?
        switch (tkey)
        {
            case stand_right:
                switch (maryoState)
                {
                    case small:
                        return KEY_STAND_RIGHT_SMALL;
                    case big:
                        return KEY_STAND_RIGHT_BIG;
                    case fire:
                        return KEY_STAND_RIGHT_FIRE;
                    case ice:
                        return KEY_STAND_RIGHT_ICE;
                    case ghost:
                        return KEY_STAND_RIGHT_GHOST;
                    case flying:
                        return KEY_STAND_RIGHT_FLYING;
                }
                break;
            case stand_left:
                switch (maryoState)
                {
                    case small:
                        return KEY_STAND_LEFT_SMALL;
                    case big:
                        return KEY_STAND_LEFT_BIG;
                    case fire:
                        return KEY_STAND_LEFT_FIRE;
                    case ice:
                        return KEY_STAND_LEFT_ICE;
                    case ghost:
                        return KEY_STAND_LEFT_GHOST;
                    case flying:
                        return KEY_STAND_LEFT_FLYING;
                }
                break;
            case jump_right:
                switch (maryoState)
                {
                    case small:
                        return KEY_JUMP_RIGHT_SMALL;
                    case big:
                        return KEY_JUMP_RIGHT_BIG;
                    case fire:
                        return KEY_JUMP_RIGHT_FIRE;
                    case ice:
                        return KEY_JUMP_RIGHT_ICE;
                    case ghost:
                        return KEY_JUMP_RIGHT_GHOST;
                    case flying:
                        return KEY_JUMP_RIGHT_FLYING;
                }
                break;
            case jump_left:
                switch (maryoState)
                {
                    case small:
                        return KEY_JUMP_LEFT_SMALL;
                    case big:
                        return KEY_JUMP_LEFT_BIG;
                    case fire:
                        return KEY_JUMP_LEFT_FIRE;
                    case ice:
                        return KEY_JUMP_LEFT_ICE;
                    case ghost:
                        return KEY_JUMP_LEFT_GHOST;
                    case flying:
                        return KEY_JUMP_LEFT_FLYING;
                }
                break;
            case fall_right:
                switch (maryoState)
                {
                    case small:
                        return KEY_FALL_RIGHT_SMALL;
                    case big:
                        return KEY_FALL_RIGHT_BIG;
                    case fire:
                        return KEY_FALL_RIGHT_FIRE;
                    case ice:
                        return KEY_FALL_RIGHT_ICE;
                    case ghost:
                        return KEY_FALL_RIGHT_GHOST;
                    case flying:
                        return KEY_FALL_RIGHT_FLYING;
                }
                break;
            case fall_left:
                switch (maryoState)
                {
                    case small:
                        return KEY_FALL_LEFT_SMALL;
                    case big:
                        return KEY_FALL_LEFT_BIG;
                    case fire:
                        return KEY_FALL_LEFT_FIRE;
                    case ice:
                        return KEY_FALL_LEFT_ICE;
                    case ghost:
                        return KEY_FALL_LEFT_GHOST;
                    case flying:
                        return KEY_FALL_LEFT_FLYING;
                }
                break;
            case dead_right:
                switch (maryoState)
                {
                    case small:
                        return KEY_DEAD_RIGHT_SMALL;
                    case big:
                        return KEY_DEAD_RIGHT_BIG;
                    case fire:
                        return KEY_DEAD_RIGHT_FIRE;
                    case ice:
                        return KEY_DEAD_RIGHT_ICE;
                    case ghost:
                        return KEY_DEAD_RIGHT_GHOST;
                    case flying:
                        return KEY_DEAD_RIGHT_FLYING;
                }
                break;
            case dead_left:
                switch (maryoState)
                {
                    case small:
                        return KEY_DEAD_LEFT_SMALL;
                    case big:
                        return KEY_DEAD_LEFT_BIG;
                    case fire:
                        return KEY_DEAD_LEFT_FIRE;
                    case ice:
                        return KEY_DEAD_LEFT_ICE;
                    case ghost:
                        return KEY_DEAD_LEFT_GHOST;
                    case flying:
                        return KEY_DEAD_LEFT_FLYING;
                }
                break;
            case duck_right:
                switch (maryoState)
                {
                    case small:
                        return KEY_DUCK_RIGHT_SMALL;
                    case big:
                        return KEY_DUCK_RIGHT_BIG;
                    case fire:
                        return KEY_DUCK_RIGHT_FIRE;
                    case ice:
                        return KEY_DUCK_RIGHT_ICE;
                    case ghost:
                        return KEY_DUCK_RIGHT_GHOST;
                    case flying:
                        return KEY_DUCK_RIGHT_FLYING;
                }
                break;
            case duck_left:
                switch (maryoState)
                {
                    case small:
                        return KEY_DUCK_LEFT_SMALL;
                    case big:
                        return KEY_DUCK_LEFT_BIG;
                    case fire:
                        return KEY_DUCK_LEFT_FIRE;
                    case ice:
                        return KEY_DUCK_LEFT_ICE;
                    case ghost:
                        return KEY_DUCK_LEFT_GHOST;
                    case flying:
                        return KEY_DUCK_LEFT_FLYING;
                }
                break;
        }
        throw new IllegalArgumentException("Unknown texture key '" + tkey + "' or maryoState '" + maryoState + "'");
    }

    private String getAnimationKey(AKey akey)
    {
        switch (maryoState)
        {
            case small:
                switch (akey)
                {
                    case walk_left:
                        return KEY_WALKING_LEFT_SMALL;
                    case walk_right:
                        return KEY_WALKING_RIGHT_SMALL;
                }
                break;
            case big:
                switch (akey)
                {
                    case walk_left:
                        return KEY_WALKING_LEFT_BIG;
                    case walk_right:
                        return KEY_WALKING_RIGHT_BIG;
                }
                break;
            case fire:
                switch (akey)
                {
                    case walk_left:
                        return KEY_WALKING_LEFT_FIRE;
                    case walk_right:
                        return KEY_WALKING_RIGHT_FIRE;
                }
                break;
            case ice:
                switch (akey)
                {
                    case walk_left:
                        return KEY_WALKING_LEFT_ICE;
                    case walk_right:
                        return KEY_WALKING_RIGHT_ICE;
                }
                break;
            case ghost:
                switch (akey)
                {
                    case walk_left:
                        return KEY_WALKING_LEFT_GHOST;
                    case walk_right:
                        return KEY_WALKING_RIGHT_GHOST;
                }
                break;
            case flying:
                switch (akey)
                {
                    case walk_left:
                        return KEY_WALKING_LEFT_FLYING;
                    case walk_right:
                        return KEY_WALKING_RIGHT_FLYING;
                }
                break;
        }
        throw new IllegalArgumentException("Unknown animation key '" + akey + "' or maryoState '" + maryoState + "'");
    }

	@Override
	public void update(float delta)
	{
		if(exiting)
		{
			boolean isDone = false;
			float velDelta = exitVelocity * delta;
			if("up".equals(exit.direction))
			{
				if(position.y >= exitStartPosition.y + bounds.height)
				{
					isDone = true;
				}
				else
				{
					body.y = position.y += bounds.height * velDelta;
				}
			}
			else if("down".equals(exit.direction))
			{
				if(position.y <= exitStartPosition.y - bounds.height)
				{
					isDone = true;
				}
				else
				{
					body.y = position.y -= bounds.height * velDelta;
				}
			}
			else if("right".equals(exit.direction))
			{
				if(position.x >= exitStartPosition.x + bounds.width)
				{
					isDone = true;
				}
				else
				{
					rotation = -90;
					body.x = position.x += bounds.width * velDelta;
				}
			}
			else if("left".equals(exit.direction))
			{
				if(position.x <= exitStartPosition.x + bounds.width)
				{
					isDone = true;
				}
				else
				{
					rotation = 90;
					body.x = position.x -= bounds.width * velDelta;
				}
			}
			if(isDone)
			{
				//exiting = false;
				//((GameScreen)world.screen).setGameState(GameScreen.GAME_STATE.GAME_RUNNING);
				
				String nextLevelName;
				if(exit.levelName == null)
				{
					nextLevelName = Level.levels[++GameSaveUtility.getInstance().save.currentLevel];
				}
				else
				{
					nextLevelName = exit.levelName;
				}
				world.screen.game.setScreen(new LoadingScreen(new GameScreen(world.screen.game, false, nextLevelName), false));
			}
			else
			{
				updateBounds();
			}
			return;
		}
		//disable godmod after timeot
		if(godMode && System.currentTimeMillis() - godModeActivatedTime > GOD_MOD_TIMEOUT)
		{
			godMode = false;
		}
		if(worldState == WorldState.DYING)
		{
			stateTime += delta;
			if(dyingAnim.update(delta))super.update(delta);
		}
		else if(resizingAnimation != null)
        {
            stateTime += delta;
        }
        else
		{
			super.update(delta);
            //check where ground is
            Array<GameObject> objects = world.getVisibleObjects();
            Rectangle rect = world.rectPool.obtain();
            debugRayRect = rect;
            rect.set(position.x, 0, body.width, position.y);
            float tmpGroundY = 0;
            float distance = body.y;
			GameObject closestObject = null;
            //for(GameObject go : objects)
            for(int i = 0; i < objects.size; i++)
            {
                GameObject go = objects.get(i);
                if(go == null)continue;
                if(go instanceof Sprite
                        && (((Sprite)go).type == Sprite.Type.massive || ((Sprite)go).type == Sprite.Type.halfmassive)
                        && rect.overlaps(go.body))
                {
					if(((Sprite)go).type == Sprite.Type.halfmassive && body.y < go.body.y + go.body.height)
					{
						continue;
					}
                    float tmpDistance = body.y - (go.body.y + go.body.height);
                    if(tmpDistance < distance)
                    {
                        distance = tmpDistance;
                        tmpGroundY = go.body.y + go.body.height;
						closestObject = go;
                    }
                }
            }
            groundY = tmpGroundY;
			if(closestObject != null 
				&& closestObject instanceof Sprite 
				&& ((Sprite)closestObject).type == Sprite.Type.halfmassive
				&& worldState == WorldState.DUCKING)
			{
				position.y -= 0.1f;
			}
            world.rectPool.free(rect);
		}
	}

	@Override
	protected void handleCollision(GameObject object, boolean vertical)
	{
		if(!handleCollision)return;
		super.handleCollision(object, vertical);
		if(object instanceof Item)
		{
            Item item = (Item)object;
			if(!item.playerHit)item.hitPlayer();
		}
		else if(object instanceof Enemy && ((Enemy)object).handleCollision)
		{
            if(!godMode)
            {
                boolean deadAnyway = isDeadByJumpingOnTopOfEnemy(object);
                if (deadAnyway)
                {
                    downgradeOrDie(false);
                }
                else
                {
                    int resolution = ((Enemy) object).hitByPlayer(this, vertical);
                    if (resolution == Enemy.HIT_RESOLUTION_ENEMY_DIED)
                    {
                        velocity.y = 5f * Gdx.graphics.getDeltaTime();
                    }
                    else if(resolution == Enemy.HIT_RESOLUTION_PLAYER_DIED)
                    {
                        downgradeOrDie(false);
                    }
                    else
                    {
                        //TODO handle this here or in enemy???????
                    }
                }
            }
		}
		else if(object instanceof Box && position.y + body.height <= object.position.y)
		{
			((Box)object).handleHitByPlayer();
		}
	}

    private boolean isDeadByJumpingOnTopOfEnemy(GameObject object)
    {
        //TODO update this when you add new enemy classes
        return object instanceof Flyon || object instanceof Eato;
    }

    public WorldState getWorldState()
    {
        return worldState;
    }

    public void setWorldState(WorldState newWorldState)
    {
		if(worldState == WorldState.DYING)return;
        this.worldState = newWorldState;
		if(worldState == WorldState.DUCKING)
		{
			body.height = bounds.height / 2;
		}
		else
		{
			body.height = bounds.height * 0.9f;
		}
    }

	@Override
	public float maxVelocity()
	{
		return MAX_VEL;
	}
	
	public void downgradeOrDie(boolean forceDie)
	{
		if(maryoState == MaryoState.small || forceDie)
		{
			worldState = WorldState.DYING;
			dyingAnim.start();
		}
		else
		{
			godMode = true;
			godModeActivatedTime = System.currentTimeMillis();
			//for now only make it small no matter the current state
			maryoState = MaryoState.small;
			GameSaveUtility.getInstance().save.playerState = maryoState;
			setupBoundingBox();
		}
	}

    /*
    * Level up*/
    public void upgrade(MaryoState newState)
    {
        if(maryoState == newState)return;
        this.newState = newState;
        oldState = maryoState;
        Array<TextureRegion> frames = generateResizeAnimationFrames(maryoState, newState);
        resizingAnimation = new Animation(RESIZE_ANIMATION_FRAME_DURATION, frames);
        resizingAnimation.setPlayMode(Animation.PlayMode.LOOP);
        resizeAnimStartTime = stateTime;
        godMode = true;
        //TODO handle if screen isnt GameScreen
        ((GameScreen)world.screen).setGameState(GameScreen.GAME_STATE.PLAYER_UPDATING);

        //play new state sound
        Sound sound = upgradeSound(newState);
        if(sound != null && Assets.playSounds)sound.play();
    }

    private Sound upgradeSound(MaryoState newState)
    {
        switch (newState)
        {
            case big:
                return Assets.manager.get("data/sounds/item/mushroom.ogg");
            case fire:
                return Assets.manager.get("data/sounds/item/fireplant.ogg");
            case ice:
                return Assets.manager.get("data/sounds/item/mushroom_blue.ogg");
            case ghost:
                return Assets.manager.get("data/sounds/item/mushroom_ghost.ogg");
            case flying:
                //TODO this asset is missing somehow
                //return Assets.manager.get("data/sounds/item/feather.ogg");
        }
        return null;
    }

    private Array<TextureRegion> generateResizeAnimationFrames(MaryoState stateFrom, MaryoState stateTo)
    {
        Array<TextureRegion> regions = new Array<>();
        if (worldState.equals(WorldState.WALKING))
        {
            regions.add(facingLeft ? Assets.animations.get(AKey.walk_left + ":" + stateFrom).getKeyFrame(stateTime, true) : Assets.animations.get(AKey.walk_right + ":" + stateFrom).getKeyFrame(stateTime, true));
            regions.add(facingLeft ? Assets.animations.get(AKey.walk_left + ":" + stateTo).getKeyFrame(stateTime, true) : Assets.animations.get(AKey.walk_right + ":" + stateTo).getKeyFrame(stateTime, true));
        }
        else if(worldState == WorldState.DUCKING)
        {
            regions.add(facingLeft ? Assets.loadedRegions.get(TKey.duck_left + ":" + stateFrom) : Assets.loadedRegions.get(TKey.duck_right + ":" + stateFrom));
            regions.add(facingLeft ? Assets.loadedRegions.get(TKey.duck_left + ":" + stateTo) : Assets.loadedRegions.get(TKey.duck_right + ":" + stateTo));
        }
        else if (getWorldState().equals(WorldState.JUMPING))
        {
            if (velocity.y > 0)
            {
                regions.add(facingLeft ? Assets.loadedRegions.get(TKey.jump_left + ":" + stateFrom) : Assets.loadedRegions.get(TKey.jump_right + ":" + stateFrom));
                regions.add(facingLeft ? Assets.loadedRegions.get(TKey.jump_left + ":" + stateTo) : Assets.loadedRegions.get(TKey.jump_right + ":" + stateTo));
            }
            else
            {
                regions.add(facingLeft ? Assets.loadedRegions.get(TKey.fall_left + ":" + stateFrom) : Assets.loadedRegions.get(TKey.fall_right + ":" + stateFrom));
                regions.add(facingLeft ? Assets.loadedRegions.get(TKey.fall_left + ":" + stateTo) : Assets.loadedRegions.get(TKey.fall_right + ":" + stateTo));
            }
        }
        else if(worldState == WorldState.DYING)
        {
            regions.add(facingLeft ? Assets.loadedRegions.get(TKey.dead_left + ":" + stateFrom) : Assets.loadedRegions.get(TKey.dead_right + ":" + stateFrom));
            regions.add(facingLeft ? Assets.loadedRegions.get(TKey.dead_left + ":" + stateTo) : Assets.loadedRegions.get(TKey.dead_right + ":" + stateTo));
        }
        else
        {
            regions.add(facingLeft ? Assets.loadedRegions.get(TKey.stand_left + ":" + stateFrom) : Assets.loadedRegions.get(TKey.stand_right + ":" + stateFrom));
            regions.add(facingLeft ? Assets.loadedRegions.get(TKey.stand_left + ":" + stateTo) : Assets.loadedRegions.get(TKey.stand_right + ":" + stateTo));
        }
        return regions;
    }

    public class DyingAnimation
	{
		private float diedTime;
		boolean upAnimFinished, dyedReset, firstDelayFinished;
		Vector3 diedPosition;
		boolean upBoost;
		
		public void start()
		{
			diedTime = stateTime;
			handleCollision = false;
			diedPosition = new Vector3(position);
            if(Assets.playSounds)
            {
                Sound sound = Assets.manager.get("data/sounds/player/dead.ogg");
                sound.play();
            }
			((GameScreen)world.screen).setGameState(GameScreen.GAME_STATE.PLAYER_DEAD);
			GameSaveUtility.getInstance().save.lifes--;
		}
		
		public boolean update(float delat)
		{
			velocity.x = 0;
			position.x = diedPosition.x;
			if(bounds.y + bounds.height < 0)//first check if player is visible
			{
				((GameScreen)world.screen).setGameState(GameScreen.GAME_STATE.GAME_OVER);
				world.trashObjects.add(Maryo.this);
				return false;
			}
			
			if(!firstDelayFinished && stateTime - diedTime < 0.5f)//delay 500ms
			{
				return false;
			}
            else
            {
                firstDelayFinished = true;
            }

            if (!upBoost)
            {
                //animate player up a bit
				velocity.y = 8f;
				upBoost = true;
            }
           
			return true;
		}
	}

    @Override
    protected void handleDroppedBelowWorld()
    {
		if(worldState != WorldState.DYING)
		{
        	downgradeOrDie(true);
		}
    }

    private void setJumpSound()
    {
        switch (maryoState)
        {
            case small:
                jumpSound = Assets.manager.get("data/sounds/player/jump_small.ogg");
                break;
            case big:
            case fire:
            case ice:
                jumpSound = Assets.manager.get("data/sounds/player/jump_big.ogg");
                break;
            case ghost:
                jumpSound = Assets.manager.get("data/sounds/player/jump_ghost.ogg");
                break;
        }
    }

    public MaryoState getMarioState()
    {
        return maryoState;
    }

    public void setMarioState(MaryoState marioState)
    {
        this.maryoState = marioState;
        setJumpSound();
    }
	
	public void enterLevel()
	{
		
	}
	
	public void exitLevel(LevelExit exit)
	{
		System.out.println("level: " + exit.levelName);
		
		switch(exit.type)
		{
			case LevelExit.LEVEL_EXIT_BEAM:
				//just change level
				String nextLevelName;
				if(exit.levelName == null)
				{
					nextLevelName = Level.levels[++GameSaveUtility.getInstance().save.currentLevel];
				}
				else
				{
					nextLevelName = exit.levelName;
				}
				world.screen.game.setScreen(new LoadingScreen(new GameScreen(world.screen.game, false, nextLevelName), false));
				break;
			case LevelExit.LEVEL_EXIT_WARP:
				if(exiting)return;
				((GameScreen)world.screen).setGameState(GameScreen.GAME_STATE.PLAYER_UPDATING);
				exiting = true;
				this.exit = exit;
				exitStartPosition.set(position);
				position.z = LevelLoader.m_pos_z_passive_start;
				Collections.sort(world.level.gameObjects, new LevelLoader.ZSpriteComparator());
				
				//todo sound
				break;
		}
	}
}
