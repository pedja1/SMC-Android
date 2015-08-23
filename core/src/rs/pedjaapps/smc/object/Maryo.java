package rs.pedjaapps.smc.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
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
import rs.pedjaapps.smc.object.enemy.Spika;
import rs.pedjaapps.smc.object.enemy.Thromp;
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
    private static final String KEY_CLIMB_SMALL = AKey.climb + ":" + MaryoState.small;
    private static final String KEY_WALKING_LEFT_BIG = AKey.walk_left + ":" + MaryoState.big;
    private static final String KEY_WALKING_RIGHT_BIG = AKey.walk_right + ":" + MaryoState.big;
    private static final String KEY_CLIMB_BIG = AKey.climb + ":" + MaryoState.big;
    private static final String KEY_WALKING_LEFT_FIRE = AKey.walk_left + ":" + MaryoState.fire;
    private static final String KEY_WALKING_RIGHT_FIRE = AKey.walk_right + ":" + MaryoState.fire;
    private static final String KEY_CLIMB_FIRE = AKey.climb + ":" + MaryoState.fire;
    private static final String KEY_WALKING_LEFT_FLYING = AKey.walk_left + ":" + MaryoState.flying;
    private static final String KEY_WALKING_RIGHT_FLYING = AKey.walk_right + ":" + MaryoState.flying;
    private static final String KEY_CLIMB_FLYING = AKey.climb + ":" + MaryoState.flying;
    private static final String KEY_WALKING_LEFT_GHOST = AKey.walk_left + ":" + MaryoState.ghost;
    private static final String KEY_WALKING_RIGHT_GHOST = AKey.walk_right + ":" + MaryoState.ghost;
    private static final String KEY_CLIMB_GHOST = AKey.climb + ":" + MaryoState.ghost;
    private static final String KEY_WALKING_LEFT_ICE = AKey.walk_left + ":" + MaryoState.ice;
    private static final String KEY_WALKING_RIGHT_ICE = AKey.walk_right + ":" + MaryoState.ice;
    private static final String KEY_CLIMB_ICE = AKey.climb + ":" + MaryoState.ice;

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

    public static final float POSITION_Z = 0.0999f;

    private static final float RUNNING_FRAME_DURATION = 0.08f;
    private static final float CLIMB_FRAME_DURATION = 0.25f;
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
    public float enterStartTime;
	public boolean exiting, entering;
	private LevelExit exit;
	private LevelEntry entry;
	private Vector3 exitEnterStartPosition = new Vector3();
	private static final float exitEnterVelocity = 1.3f;
	private int rotation = 0;
    public ParticleEffect powerJumpEffect;
    public boolean powerJump;
    
    public Maryo(World world, Vector3 position, Vector2 size)
    {
        super(world, size, position);
        setupBoundingBox();
		
		position.y = mColRect.y = mDrawRect.y += 0.5f;
        Assets.manager.load("data/animation/particles/maryo_power_jump_emitter.p", ParticleEffect.class, Assets.particleEffectParameter);
    }

    private void setupBoundingBox()
    {
        float centerX = position.x + mColRect.width / 2;
		switch(maryoState)
		{
			case small:
				mDrawRect.width = 0.9f;
				mDrawRect.height = 0.9f;
				break;
			case big:
			case fire:
			case ghost:
			case ice:
				mDrawRect.height = 1.09f;
				mDrawRect.width = 1.09f;
				break;
			case flying:
				break;
		}
		mColRect.x = mDrawRect.x + mDrawRect.width / 4;
		mColRect.width = mDrawRect.width / 2;
		position.x = mColRect.x;
		
        if(worldState == WorldState.DUCKING)
		{
			mColRect.height = mDrawRect.height / 2;
		}
		else
		{
			mColRect.height = mDrawRect.height * 0.9f;
		}

        position.x = mColRect.x = centerX - mColRect.width / 2;
    }

	@Override
    public void updateBounds()
    {
        mDrawRect.x = mColRect.x - mDrawRect.width / 4;
        mDrawRect.y = mColRect.y;
    }

    public void initAssets()
    {
        MaryoState[] states = new MaryoState[]{MaryoState.small, MaryoState.big, MaryoState.fire, MaryoState.ghost, MaryoState.ice};
        for(MaryoState ms : states)
        {
            loadTextures(ms.toString());
        }
        setJumpSound();
        powerJumpEffect = new ParticleEffect(Assets.manager.get("data/animation/particles/maryo_power_jump_emitter.p", ParticleEffect.class));

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

        TextureRegion[] climbFrames = new TextureRegion[2];
        climbFrames[0] = atlas.findRegion(TKey.climb_left + "");
        climbFrames[1] = atlas.findRegion(TKey.climb_right + "");
        Assets.animations.put(AKey.climb + ":" + state, new Animation(CLIMB_FRAME_DURATION, climbFrames));

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
			
            float originX = mDrawRect.width * 0.5f;
            float originY = mDrawRect.height * 0.5f;
            spriteBatch.draw(marioFrame, mDrawRect.x, mDrawRect.y, originX, originY, mDrawRect.width, mDrawRect.height, 1, 1, rotation);
			
			return;
		}
        if(entering)
        {
            marioFrame = facingLeft ? Assets.loadedRegions.get(getTextureKey(TKey.stand_left)) : Assets.loadedRegions.get(getTextureKey(TKey.stand_right));

            float originX = mDrawRect.width * 0.5f;
            float originY = mDrawRect.height * 0.5f;
            spriteBatch.draw(marioFrame, mDrawRect.x, mDrawRect.y, originX, originY, mDrawRect.width, mDrawRect.height, 1, 1, rotation);

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
        else if(worldState == WorldState.CLIMBING)
        {
            TextureRegion[] frames = Assets.animations.get(getAnimationKey(AKey.climb)).getKeyFrames();
            float distance = position.y - exitEnterStartPosition.y;
            marioFrame = frames[Math.floor(distance / 0.3f) % 2 == 0 ? 0 : 1];
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
			
			spriteBatch.draw(marioFrame, mDrawRect.x, mDrawRect.y, mDrawRect.width, mDrawRect.height);
			
			color.a = oldA;
			spriteBatch.setColor(color);
		}
		else
		{
        	spriteBatch.draw(marioFrame, mDrawRect.x, mDrawRect.y, mDrawRect.width, mDrawRect.height);
		}
        if(worldState == WorldState.DUCKING && powerJump)
        {
            powerJumpEffect.setPosition(position.x, position.y + 0.05f);
            powerJumpEffect.draw(spriteBatch);
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
                    case climb:
                        return KEY_CLIMB_SMALL;
                }
                break;
            case big:
                switch (akey)
                {
                    case walk_left:
                        return KEY_WALKING_LEFT_BIG;
                    case walk_right:
                        return KEY_WALKING_RIGHT_BIG;
                    case climb:
                        return KEY_CLIMB_BIG;
                }
                break;
            case fire:
                switch (akey)
                {
                    case walk_left:
                        return KEY_WALKING_LEFT_FIRE;
                    case walk_right:
                        return KEY_WALKING_RIGHT_FIRE;
                    case climb:
                        return KEY_CLIMB_FIRE;
                }
                break;
            case ice:
                switch (akey)
                {
                    case walk_left:
                        return KEY_WALKING_LEFT_ICE;
                    case walk_right:
                        return KEY_WALKING_RIGHT_ICE;
                    case climb:
                        return KEY_CLIMB_ICE;
                }
                break;
            case ghost:
                switch (akey)
                {
                    case walk_left:
                        return KEY_WALKING_LEFT_GHOST;
                    case walk_right:
                        return KEY_WALKING_RIGHT_GHOST;
                    case climb:
                        return KEY_CLIMB_GHOST;
                }
                break;
            case flying:
                switch (akey)
                {
                    case walk_left:
                        return KEY_WALKING_LEFT_FLYING;
                    case walk_right:
                        return KEY_WALKING_RIGHT_FLYING;
                    case climb:
                        return KEY_CLIMB_FLYING;
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
			float velDelta = exitEnterVelocity * delta;
			if("up".equals(exit.direction))
			{
				if(position.y >= exitEnterStartPosition.y + mDrawRect.height)
				{
					isDone = true;
				}
				else
				{
					mColRect.y = position.y += mDrawRect.height * velDelta;
				}
			}
			else if("down".equals(exit.direction))
			{
				if(position.y <= exitEnterStartPosition.y - mDrawRect.height)
				{
					isDone = true;
				}
				else
				{
					mColRect.y = position.y -= mDrawRect.height * velDelta;
				}
			}
			else if("right".equals(exit.direction))
			{
				if(position.x >= exitEnterStartPosition.x + mDrawRect.width)
				{
					isDone = true;
				}
				else
				{
					rotation = -90;
					mColRect.x = position.x += mDrawRect.width * velDelta;
				}
			}
			else if("left".equals(exit.direction))
			{
				if(exitEnterStartPosition.x - position.x >= mDrawRect.width)
				{
					isDone = true;
				}
				else
				{
					rotation = 90;
					mColRect.x = position.x -= mDrawRect.width * velDelta;
				}
			}
			if(isDone)
			{
				exiting = false;
				//((GameScreen)world.screen).setGameState(GameScreen.GAME_STATE.GAME_RUNNING);
				
				String nextLevelName;
				if(exit.levelName == null)
				{
                    String currentLevel = ((GameScreen)world.screen).parent == null ? ((GameScreen)world.screen).levelName : ((GameScreen)world.screen).parent.levelName;
					nextLevelName = GameSaveUtility.getInstance().getNextLevel(currentLevel);
				}
				else
				{
					nextLevelName = exit.levelName;
				}
                GameScreen parent;
                GameScreen newScreen;
                boolean resume = false;
                if(nextLevelName.contains("sub"))
                {
                    parent = (GameScreen) world.screen;
                    newScreen = new GameScreen(world.screen.game, false, nextLevelName, parent);
                }
                else if(((GameScreen)world.screen).parent != null && nextLevelName.equals(((GameScreen)world.screen).parent.levelName))
                {
                    newScreen = ((GameScreen)world.screen).parent;
                    newScreen.forceCheckEnter = true;
                    resume = true;
                }
                else
                {
                    if(((GameScreen)world.screen).parent != null)
                    {
                        ((GameScreen)world.screen).parent.dispose();
                        ((GameScreen)world.screen).parent = null;
                    }
                    newScreen = new GameScreen(world.screen.game, false, nextLevelName, null);
                }
				world.screen.game.setScreen(new LoadingScreen(newScreen, resume));
			}
			else
			{
				updateBounds();
			}
			return;
		}
        if(entering)
        {
            enterStartTime += delta;
            if(enterStartTime < 1)
            {
                return;
            }
            boolean isDone = false;
            float velDelta = exitEnterVelocity * delta;
            if("up".equals(entry.direction))
            {
                if(position.y > entry.mColRect.y + entry.mColRect.height)
                {
                    isDone = true;
                }
                else
                {
                    mColRect.y = position.y += mDrawRect.height * velDelta;
                }
            }
            else if("down".equals(entry.direction))
            {
                if(position.y + mDrawRect.height < entry.mColRect.y)
                {
                    isDone = true;
                }
                else
                {
                    mColRect.y = position.y -= mDrawRect.height * velDelta;
                }
            }
            else if("right".equals(entry.direction))
            {
                if(position.x > entry.mColRect.x + entry.mColRect.width)
                {
                    isDone = true;
                }
                else
                {
                    rotation = -90;
                    mColRect.x = position.x += mDrawRect.width * velDelta;
                }
            }
            else if("left".equals(entry.direction))
            {
                if(position.x + mDrawRect.width < entry.mColRect.x)
                {
                    isDone = true;
                }
                else
                {
                    rotation = 90;
                    mColRect.x = position.x -= mDrawRect.width * velDelta;
                }
            }
            if(isDone)
            {
                position.z = POSITION_Z;
                Collections.sort(world.level.gameObjects, new LevelLoader.ZSpriteComparator());
                entering = false;
                ((GameScreen)world.screen).setGameState(GameScreen.GAME_STATE.GAME_RUNNING);
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
            if(worldState == WorldState.CLIMBING)
            {
                checkCollisionWithBlocks(delta);
                boolean climbing = false;
                Array<GameObject> vo = world.getVisibleObjects();
                for(int i = 0; i < vo.size; i++)
                {
                    GameObject go = vo.get(i);
                    if(go instanceof Sprite && ((Sprite)go).type == Sprite.Type.climbable && go.mColRect.overlaps(mColRect))
                    {
                        climbing = true;
                        break;
                    }
                }
                if(!climbing)setWorldState(WorldState.JUMPING);
                stateTime += delta;
            }
            else
            {
                super.update(delta);
                //check where ground is
                Array<GameObject> objects = world.getVisibleObjects();
                Rectangle rect = world.RECT_POOL.obtain();
                debugRayRect = rect;
                rect.set(position.x, 0, mColRect.width, position.y);
                float tmpGroundY = 0;
                float distance = mColRect.y;
                GameObject closestObject = null;
                //for(GameObject go : objects)
                for (int i = 0; i < objects.size; i++)
                {
                    GameObject go = objects.get(i);
                    if (go == null) continue;
                    if (go instanceof Sprite
                            && (((Sprite) go).type == Sprite.Type.massive || ((Sprite) go).type == Sprite.Type.halfmassive)
                            && rect.overlaps(go.mColRect))
                    {
                        if (((Sprite) go).type == Sprite.Type.halfmassive && mColRect.y < go.mColRect.y + go.mColRect.height)
                        {
                            continue;
                        }
                        float tmpDistance = mColRect.y - (go.mColRect.y + go.mColRect.height);
                        if (tmpDistance < distance)
                        {
                            distance = tmpDistance;
                            tmpGroundY = go.mColRect.y + go.mColRect.height;
                            closestObject = go;
                        }
                    }
                }
                groundY = tmpGroundY;
                if (closestObject != null
                        && closestObject instanceof Sprite
                        && ((Sprite) closestObject).type == Sprite.Type.halfmassive
                        && worldState == WorldState.DUCKING)
                {
                    position.y -= 0.1f;
                }
                world.RECT_POOL.free(rect);
            }
		}
        if(powerJump)
        {
            powerJumpEffect.update(delta);
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
		else if(object instanceof Box && position.y + mColRect.height <= object.position.y)
		{
			((Box)object).handleHitByPlayer();
		}
	}

    private boolean isDeadByJumpingOnTopOfEnemy(GameObject object)
    {
        //TODO update this when you add new enemy classes
        return object instanceof Flyon || object instanceof Eato || object instanceof Thromp
                || object instanceof Spika;
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
			mColRect.height = mDrawRect.height / 2;
		}
		else
		{
			mColRect.height = mDrawRect.height * 0.9f;
		}
        if(worldState == WorldState.CLIMBING)
        {
            exitEnterStartPosition.set(position);
            velocity.x = 0;
            velocity.y = 0;
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
                return Assets.manager.get("data/sounds/item/mushroom_blue.wav");
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
			if(mDrawRect.y + mDrawRect.height < 0)//first check if player is visible
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

    public void checkLevelEnter()
    {
        //check if maryo is overlapping level entry and if so call enterLevel
        for(GameObject go : world.level.gameObjects)
        {
            if(go instanceof LevelEntry && mColRect.overlaps(go.mColRect) && ((LevelEntry)go).type == LevelExit.LEVEL_EXIT_WARP)
            {
                LevelEntry entry = (LevelEntry) go;
                if( entry.type == LevelExit.LEVEL_EXIT_BEAM )
                {
                    float entryCenter = entry.mColRect.x + entry.mColRect.width  * 0.5f;
                    position.x = mColRect.x = entryCenter - mColRect.width  * 0.5f;
                    position.y = mColRect.y = entry.mColRect.y + entry.mColRect.height + mColRect.height;
                    updateBounds();
                    return;
                }
                else
                {
                    enterLevel((LevelEntry) go);
                    return;
                }
            }
        }
    }
	
	public void enterLevel(LevelEntry entry)
	{
        ((GameScreen)world.screen).setGameState(GameScreen.GAME_STATE.PLAYER_UPDATING);
        entering = true;
        this.entry = entry;
        if( entry.type == LevelExit.LEVEL_EXIT_WARP )
        {
            // left
            if("left".equals(entry.direction))
            {
                position.x = mColRect.x = entry.mColRect.x + entry.mColRect.width;

                float entryCenter = entry.mColRect.y + entry.mColRect.height * 0.5f;
                position.y = mColRect.y = entryCenter - mColRect.height * 0.5f;
            }
            // right
            else if("right".equals(entry.direction))
            {
                position.x = mColRect.x = entry.mColRect.x - mColRect.width;


                float entryCenter = entry.mColRect.y + entry.mColRect.height * 0.5f;
                position.y = mColRect.y = entryCenter - mColRect.height * 0.5f;
            }
            //up
            else if("up".equals(entry.direction))
            {
                position.y = mColRect.y = entry.mColRect.y - mColRect.height;

                float entryCenter = entry.mColRect.x + entry.mColRect.width  * 0.5f;
                position.x = mColRect.x = entryCenter - mColRect.width  * 0.5f;
            }
            // down
            else if("down".equals(entry.direction))
            {
                position.y = mColRect.y = entry.mColRect.y;

                float entryCenter = entry.mColRect.x + entry.mColRect.width  * 0.5f;
                position.x = mColRect.x = entryCenter - mColRect.width  * 0.5f;
            }
        }
        else if( entry.type == LevelExit.LEVEL_EXIT_BEAM )
        {
            float entryCenter = entry.mColRect.x + entry.mColRect.width  * 0.5f;
            position.x = mColRect.x = entryCenter - mColRect.width  * 0.5f;
            position.y = mColRect.y = entry.mColRect.y + entry.mColRect.height + mColRect.height;
        }
        updateBounds();
        exitEnterStartPosition.set(position);
        position.z = LevelLoader.m_pos_z_passive_start;
        Collections.sort(world.level.gameObjects, new LevelLoader.ZSpriteComparator());

        //todo sound
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
                    String currentLevel = ((GameScreen)world.screen).parent == null ? ((GameScreen)world.screen).levelName : ((GameScreen)world.screen).parent.levelName;
                    nextLevelName = GameSaveUtility.getInstance().getNextLevel(currentLevel);
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
                if("up".equals(exit.direction) || "down".equals(exit.direction))
                {
                    float exitCenter = exit.mColRect.x + exit.mColRect.width  * 0.5f;
                    position.x = mColRect.x = exitCenter - mColRect.width  * 0.5f;
                }
                else
                {
                    float exitCenter = exit.mColRect.y + exit.mColRect.height * 0.5f;
                    position.y = mColRect.y = exitCenter - mColRect.height * 0.5f;
                }
                updateBounds();
				exitEnterStartPosition.set(position);
				position.z = LevelLoader.m_pos_z_passive_start;
				Collections.sort(world.level.gameObjects, new LevelLoader.ZSpriteComparator());
				
				//todo sound
				break;
		}
	}
}
