package rs.pedjaapps.smc.controller;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.*;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.LevelExit;
import rs.pedjaapps.smc.object.Maryo;
import rs.pedjaapps.smc.object.World;

public class MarioController
{
    private static final long LONG_JUMP_PRESS = 150l;
    private static final int POWER_JUMP_DELTA = 1000;
    private static final float MAX_JUMP_SPEED = 1f;

    private float mMaxJumpVelocity = 9.5f;
    private float mJumpSpeed = MAX_JUMP_SPEED;

    private World world;
    private Maryo maryo;
    private boolean mJumped;
    private long jumpClickTime;

    private long downPressTime;

    enum Key
    {
        LEFT, RIGHT, UP, DOWN, JUMP, FIRE
    }

    static Set<Key> keys = new HashSet<>(Key.values().length);

    public MarioController(World world)
    {
        this.world = world;
        this.maryo = world.maryo;
    }

    // ** Key presses and touches **************** //

    public void leftPressed()
    {
        keys.add(Key.LEFT);
		checkLeave("left");
    }

    public void rightPressed()
    {
        keys.add(Key.RIGHT);
		checkLeave("right");
		//TODO this is called only when key is pressed, not continusly
		//if player holds the key and walks to the exit, he will have to press it again to exit
    }

    public void upPressed()
    {
        keys.add(Key.UP);
        checkLeave("up");
    }

	private void checkLeave(String dir)
	{
		Array<GameObject> vo = world.getVisibleObjects();
		//for(GameObject go : world.getVisibleObjects())
		for(int i = 0; i < vo.size; i++)
        {
			GameObject go = vo.get(i);
            if(go instanceof LevelExit 
				&& go.mColRect.overlaps(maryo.mColRect)
				&& (((LevelExit)go).type == LevelExit.LEVEL_EXIT_BEAM || (((LevelExit)go).type == LevelExit.LEVEL_EXIT_WARP && dir.equals(((LevelExit)go).direction))))
            {
                /*String nextLevelName = Level.levels[++GameSaveUtility.getInstance().save.currentLevel];
                world.screen.game.setScreen(new LoadingScreen(new GameScreen(world.screen.game, false, nextLevelName), false));*/
                maryo.exitLevel((LevelExit)go);
				return;
            }
        }
	}

    public void downPressed()
    {
        keys.add(Key.DOWN);
		checkLeave("down");
        downPressTime = System.currentTimeMillis();
    }

    public void jumpPressed()
    {
        if(maryo.grounded)
        {
            keys.add(Key.JUMP);

            if(Assets.playSounds)
            {
                Sound sound = maryo.jumpSound;
                if(sound != null)sound.play();
            }
            jumpClickTime = System.currentTimeMillis();
        }
    }

    public void firePressed()
    {
        keys.add(Key.FIRE);
    }

    public void leftReleased()
    {
        keys.remove(Key.LEFT);
    }

    public void rightReleased()
    {
        keys.remove(Key.RIGHT);
    }

    public void upReleased()
    {
        keys.remove(Key.UP);
    }

    public void downReleased()
    {
        keys.remove(Key.DOWN);
        downPressTime = 0;
    }

    public void jumpReleased()
    {
        keys.remove(Key.JUMP);
        mJumped = false;
        mJumpSpeed = MAX_JUMP_SPEED;
    }

    public void fireReleased()
    {
        keys.remove(Key.FIRE);
    }

    /**
     * The main update method *
     */
    public void update(float delta)
    {
        maryo.grounded = maryo.position.y - maryo.groundY < 0.1f;
		if(!maryo.grounded)
		{
			maryo.setWorldState(Maryo.WorldState.JUMPING);
		}
        processInput(delta);
        if (maryo.grounded && maryo.getWorldState().equals(Maryo.WorldState.JUMPING))
        {
            maryo.setWorldState(Maryo.WorldState.IDLE);
        }
        //TODO animation/particles if boost jump
	}

    /**
     * Change Mario's state and parameters based on input controls *
     */
    private boolean processInput(float delta)
    {
        Vector3 vel = maryo.velocity;
        Vector3 pos = maryo.position;
        if (keys.contains(Key.JUMP))
        {
            if (!mJumped && vel.y < mMaxJumpVelocity/* && System.currentTimeMillis() - jumpClickTime < LONG_JUMP_PRESS*/)
            {
                float jumpSpeed;
                if(vel.y + mJumpSpeed >= mMaxJumpVelocity)
                {
                    jumpSpeed = mMaxJumpVelocity - vel.y;
                    mJumped = true;
                }
                else
                {
                    jumpSpeed = mJumpSpeed;
                }
                System.out.println(jumpSpeed);
                System.out.println(vel.y);
                if(jumpSpeed <= 0)
                {
                    mJumped = true;
                }
                else
                {
                    vel.add(0, jumpSpeed, 0);
                }
                mJumpSpeed = mJumpSpeed - MAX_JUMP_SPEED * delta;
                if(mJumpSpeed <= 0)
                {
                    mJumped = true;
                    mJumpSpeed = MAX_JUMP_SPEED;
                }
            }
            else
            {
                mJumped = true;
            }
        }
        if (keys.contains(Key.LEFT))
        {
            // left is pressed
            maryo.facingLeft = true;
            if (maryo.getWorldState() != Maryo.WorldState.JUMPING)
            {
                maryo.setWorldState(Maryo.WorldState.WALKING);
            }
            maryo.velocity.set(vel.x = -4f, vel.y, maryo.velocity.z);
        }
        else if (keys.contains(Key.RIGHT))
        {
            // right is pressed
            maryo.facingLeft  = false;
            if (maryo.getWorldState() != Maryo.WorldState.JUMPING)
            {
                maryo.setWorldState(Maryo.WorldState.WALKING);
            }
            maryo.velocity.set(vel.x = +4f, vel.y, maryo.velocity.z);
        }
        else if (keys.contains(Key.DOWN))
        {
            if (maryo.getWorldState() != Maryo.WorldState.JUMPING)
            {
                maryo.setWorldState(Maryo.WorldState.DUCKING);
            }
        }
        else
        {
            if (maryo.getWorldState() != Maryo.WorldState.JUMPING)
            {
                maryo.setWorldState(Maryo.WorldState.IDLE);
            }
            //slowly decrease linear velocity on x axes
            maryo.velocity.set(vel.x * 0.7f, /*vel.y > 0 ? vel.y * 0.7f : */vel.y, maryo.velocity.z);
        }
        return false;
    }

    public void setMaryo(Maryo mario)
    {
        this.maryo = mario;
    }
}
