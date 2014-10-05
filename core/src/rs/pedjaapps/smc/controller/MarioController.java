package rs.pedjaapps.smc.controller;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.*;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.model.GameObject;
import rs.pedjaapps.smc.model.Maryo;
import rs.pedjaapps.smc.model.World;

public class MarioController
{

    enum Keys
    {
        LEFT, RIGHT, UP, DOWN, JUMP, FIRE
    }

    private static final long LONG_JUMP_PRESS = 150l;
    private static final float MAX_JUMP_SPEED = 9f;
    
    private World world;
    private Maryo maryo;
    private boolean jumped;

    static Set<Keys> keys = new HashSet<Keys>();

    public MarioController(World world)
    {
        this.world = world;
        this.maryo = world.getMario();
    }

    // ** Key presses and touches **************** //

    public void leftPressed()
    {
        keys.add(Keys.LEFT);
    }

    public void rightPressed()
    {
        keys.add(Keys.RIGHT);
    }

    public void upPressed()
    {
        keys.add(Keys.UP);
    }

    public void downPressed()
    {
        keys.add(Keys.DOWN);
    }

    public void jumpPressed()
    {
        if(maryo.isGrounded())
        {
            keys.add(Keys.JUMP);

            if(Assets.playSounds)
            {
                Sound sound = maryo.jumpSound;
                if(sound != null)sound.play();
            }
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
        maryo.setGrounded(maryo.position.y - maryo.groundY < 0.1f);
		if(!maryo.isGrounded())
		{
			maryo.setWorldState(Maryo.WorldState.JUMPING);
		}
        processInput();
        if (maryo.isGrounded() && maryo.getWorldState().equals(Maryo.WorldState.JUMPING))
        {
            maryo.setWorldState(Maryo.WorldState.IDLE);
        }
	}

    /**
     * Change Mario's state and parameters based on input controls *
     */
    private boolean processInput()
    {
        Vector3 vel = maryo.getVelocity();
        Vector3 pos = maryo.getPosition();
        if (keys.contains(Keys.JUMP))
        {
            if (!jumped && vel.y < MAX_JUMP_SPEED)
            {
                maryo.setVelocity(vel.x, vel.y += 2f);
            }
            else
            {
                jumped = true;
            }
        }
        if (keys.contains(Keys.LEFT))
        {
            // left is pressed
            maryo.setFacingLeft(true);
            if (!maryo.getWorldState().equals(Maryo.WorldState.JUMPING))
            {
                maryo.setWorldState(Maryo.WorldState.WALKING);
            }
            maryo.setVelocity(vel.x = -4f, vel.y);
        }
        else if (keys.contains(Keys.RIGHT))
        {
            // right is pressed
            maryo.setFacingLeft(false);
            if (!maryo.getWorldState().equals(Maryo.WorldState.JUMPING))
            {
                maryo.setWorldState(Maryo.WorldState.WALKING);
            }
            maryo.setVelocity(vel.x = +4f, vel.y);
        }
        else if (keys.contains(Keys.DOWN))
        {
            if (!maryo.getWorldState().equals(Maryo.WorldState.JUMPING))
            {
                maryo.setWorldState(Maryo.WorldState.DUCKING);
            }
        }
        else
        {
            if (!maryo.getWorldState().equals(Maryo.WorldState.JUMPING))
            {
                maryo.setWorldState(Maryo.WorldState.IDLE);
            }
            //slowly decrease linear velocity on x axes
            maryo.setVelocity(vel.x * 0.7f, /*vel.y > 0 ? vel.y * 0.7f : */vel.y);
        }
        return false;
    }

    public void setMaryo(Maryo mario)
    {
        this.maryo = mario;
    }
}
