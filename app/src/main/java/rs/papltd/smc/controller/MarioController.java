package rs.papltd.smc.controller;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;

import java.util.*;

import rs.papltd.smc.model.*;

public class MarioController
{

    enum Keys
    {
        LEFT, RIGHT, UP, DOWN, JUMP, FIRE
    }

    private static final long LONG_JUMP_PRESS = 150l;
    private static final float MAX_JUMP_SPEED = 5f;
    private static final float MAX_VEL = 2f;

    private WorldWrapper world;
    private Maryo mario;
    private long jumpPressedTime;
    private boolean jumpingPressed;
    private boolean grounded = false;

    static Map<Keys, Boolean> keys = new HashMap<MarioController.Keys, Boolean>();

    static
    {
        keys.put(Keys.LEFT, false);
        keys.put(Keys.RIGHT, false);
        keys.put(Keys.JUMP, false);
        keys.put(Keys.FIRE, false);
        keys.put(Keys.UP, false);
        keys.put(Keys.DOWN, false);
    }

    public MarioController(WorldWrapper world)
    {
        this.world = world;
        this.mario = world.getMario();
        /*rbg = new ParallaxBackground(new ParallaxLayer[]{
                new ParallaxLayer(new TextureRegion(new Texture(Gdx.files.internal("data/background/green_junglehills.png"))), new Vector2(), new Vector2(0, 0))
        }, 10, 7, new Vector2(150, 0));*/
    }

    // ** Key presses and touches **************** //

    public void leftPressed()
    {
        keys.put(Keys.LEFT, true);
    }

    public void rightPressed()
    {
        keys.put(Keys.RIGHT, true);
    }

    public void upPressed()
    {
        keys.put(Keys.UP, true);
    }

    public void downPressed()
    {
        keys.put(Keys.DOWN, true);
    }

    public void jumpPressed()
    {
        keys.put(Keys.JUMP, true);
    }

    public void firePressed()
    {
        keys.put(Keys.FIRE, false);
    }

    public void leftReleased()
    {
        keys.put(Keys.LEFT, false);
    }

    public void rightReleased()
    {
        keys.put(Keys.RIGHT, false);
    }

    public void upReleased()
    {
        keys.put(Keys.UP, false);
    }

    public void downReleased()
    {
        keys.put(Keys.DOWN, false);
    }

    public void jumpReleased()
    {
        keys.put(Keys.JUMP, false);
        jumpingPressed = false;
    }

    public void fireReleased()
    {
        keys.put(Keys.FIRE, false);
    }

    /**
     * The main update method *
     */
    public void update(float delta)
    {
        grounded = isPlayerGrounded();
		if(!grounded)
		{
			mario.setWorldState(Maryo.WorldState.JUMPING);
		}
        System.out.println("grounded: " + grounded);
        processInput();
        if (grounded && mario.getWorldState().equals(Maryo.WorldState.JUMPING))
        {
            mario.setWorldState(Maryo.WorldState.IDLE);
        }
        // simply updates the state time
        mario.update(delta);
        //world.getLevel().getPb().moveX(delta);
		
    }

    /**
     * Change Mario's state and parameters based on input controls *
     */
    private boolean processInput()
    {
        Vector2 vel = mario.getBody().getLinearVelocity();
        Vector2 pos = mario.getBody().getPosition();
        if (keys.get(Keys.JUMP))
        {
            if (!mario.getWorldState().equals(Maryo.WorldState.JUMPING))
            {
                jumpingPressed = true;
                jumpPressedTime = System.currentTimeMillis();
                mario.setWorldState(Maryo.WorldState.JUMPING);
                //mario.getVelocity().y = MAX_JUMP_SPEED;
                grounded = false;
            }
            else
            {
                if (jumpingPressed && ((System.currentTimeMillis() - jumpPressedTime) >= LONG_JUMP_PRESS))
                {
                    jumpingPressed = false;
                }
                else
                {
                    if (jumpingPressed && vel.y < MAX_JUMP_SPEED)
                    {
                        //mario.getVelocity().y = MAX_JUMP_SPEED;
                        mario.getBody().setTransform(pos.x, pos.y + 0.01f, 0);
                        mario.getBody().setLinearVelocity(vel.x, vel.y = +10f);
                    }
                }
            }
        }
        if (keys.get(Keys.LEFT))
        {
            // left is pressed
            mario.setFacingLeft(true);
            if (!mario.getWorldState().equals(Maryo.WorldState.JUMPING))
            {
                mario.setWorldState(Maryo.WorldState.WALKING);
            }
            //if (vel.x > -MAX_VEL)
            //{
                //mario.getBody().applyLinearImpulse(-1.2f, 0, 0, 0, true);
                mario.getBody().setLinearVelocity(vel.x = -4f, vel.y);
            //}
        }
        else if (keys.get(Keys.RIGHT))
        {
            // left is pressed
            mario.setFacingLeft(false);
            if (!mario.getWorldState().equals(Maryo.WorldState.JUMPING))
            {
                mario.setWorldState(Maryo.WorldState.WALKING);
            }
            //if (vel.x < MAX_VEL)
            //{
                //mario.getBody().applyLinearImpulse(1.2f, 0, 0, 0, true);
                mario.getBody().setLinearVelocity(vel.x = +4f, vel.y);
            //}
        }
        else if (keys.get(Keys.DOWN))
        {
            if (!mario.getWorldState().equals(Maryo.WorldState.JUMPING))
            {
                mario.setWorldState(Maryo.WorldState.DUCKING);
            }
        }
        else
        {
            if (!mario.getWorldState().equals(Maryo.WorldState.JUMPING))
            {
                mario.setWorldState(Maryo.WorldState.IDLE);
            }
            //slowly decrease linear velocity on x axes
            mario.getBody().setLinearVelocity(vel.x * 0.7f, vel.y);
        }
        return false;
    }

    private boolean isPlayerGrounded()
    {
        Array<Contact> contactList = world.getWorld().getContactList();
        for (int i = 0; i < contactList.size; i++)
        {
            Contact contact = contactList.get(i);
            if (contact.isTouching()
                    && (contact.getFixtureA() == mario.getSensorFixture() || contact.getFixtureB() == mario.getSensorFixture()))
            {
                /*Vector2 pos = mario.getBody().getPosition();
                WorldManifold manifold = contact.getWorldManifold();
                boolean below = true;
                for (int j = 0; j < manifold.getNumberOfContactPoints(); j++)
                {
                    below &= (manifold.getPoints()[j].y < pos.y - 1.5f);
                }*/

                return true;

            }
        }
        return false;
    }

}
