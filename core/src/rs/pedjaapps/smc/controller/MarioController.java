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

    enum Keys
    {
        LEFT, RIGHT, UP, DOWN, JUMP, FIRE
    }
    private static final int POWER_JUMP_DELTA = 1000;

    private static final long LONG_JUMP_PRESS = 150l;
    private static final float MAX_JUMP_SPEED = 9f;
    private static final float POWER_MAX_JUMP_SPEED = 11f;
    private float mMaxJumpSpeed = MAX_JUMP_SPEED;
    
    private World world;
    private Maryo maryo;
    private boolean jumped;

    private long downPressTime;

    private long jumpClickTime;

    static Set<Keys> keys = new HashSet<>(Keys.values().length);

    public MarioController(World world)
    {
        this.world = world;
        this.maryo = world.maryo;
    }

    // ** Key presses and touches **************** //

    public void leftPressed()
    {
        keys.add(Keys.LEFT);
		checkLeave("left");
    }

    public void rightPressed()
    {
        keys.add(Keys.RIGHT);
		checkLeave("right");
		//TODO this is called only when key is pressed, not continusly
		//if player holds the key and walks to the exit, he will have to press it again to exit
    }

    public void upPressed()
    {
        keys.add(Keys.UP);
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
        keys.add(Keys.DOWN);
		checkLeave("down");
        downPressTime = System.currentTimeMillis();
    }

    public void jumpPressed()
    {
        if(maryo.grounded)
        {
            keys.add(Keys.JUMP);

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
        keys.add(Keys.FIRE);
    }

    public void leftReleased()
    {
        keys.remove(Keys.LEFT);
    }

    public void rightReleased()
    {
        keys.remove(Keys.RIGHT);
    }

    public void upReleased()
    {
        keys.remove(Keys.UP);
    }

    public void downReleased()
    {
        keys.remove(Keys.DOWN);
        downPressTime = 0;
    }

    public void jumpReleased()
    {
        keys.remove(Keys.JUMP);
        jumped = false;
    }

    public void fireReleased()
    {
        keys.remove(Keys.FIRE);
    }

    /**
     * The main update method *
     */
    public void update(float delta)
    {
        if(downPressTime != 0 && System.currentTimeMillis() - downPressTime > POWER_JUMP_DELTA)
        {
            mMaxJumpSpeed = POWER_MAX_JUMP_SPEED;
        }
        else
        {
            mMaxJumpSpeed = MAX_JUMP_SPEED;
        }
        maryo.grounded = maryo.position.y - maryo.groundY < 0.1f;
		if(!maryo.grounded)
		{
			maryo.setWorldState(Maryo.WorldState.JUMPING);
		}
        processInput();
        if (maryo.grounded && maryo.getWorldState().equals(Maryo.WorldState.JUMPING))
        {
            maryo.setWorldState(Maryo.WorldState.IDLE);
        }
	}

    /**
     * Change Mario's state and parameters based on input controls *
     */
    private boolean processInput()
    {
        Vector3 vel = maryo.velocity;
        Vector3 pos = maryo.position;
        if (keys.contains(Keys.JUMP))
        {
            if (!jumped && vel.y < mMaxJumpSpeed && System.currentTimeMillis() - jumpClickTime < LONG_JUMP_PRESS)
            {
                maryo.velocity.set(vel.x, vel.y += 2f, maryo.velocity.z);
            }
            else
            {
                jumped = true;
            }
        }
        if (keys.contains(Keys.LEFT))
        {
            // left is pressed
            maryo.facingLeft = true;
            if (maryo.getWorldState() != Maryo.WorldState.JUMPING)
            {
                maryo.setWorldState(Maryo.WorldState.WALKING);
            }
            maryo.velocity.set(vel.x = -4f, vel.y, maryo.velocity.z);
        }
        else if (keys.contains(Keys.RIGHT))
        {
            // right is pressed
            maryo.facingLeft  = false;
            if (maryo.getWorldState() != Maryo.WorldState.JUMPING)
            {
                maryo.setWorldState(Maryo.WorldState.WALKING);
            }
            maryo.velocity.set(vel.x = +4f, vel.y, maryo.velocity.z);
        }
        else if (keys.contains(Keys.DOWN))
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
