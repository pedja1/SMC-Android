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
    private Maryo maryo;
    private long jumpPressedTime;
    private boolean jumpingPressed;
    private boolean grounded = false;

    static Set<Keys> keys = new HashSet<Keys>();

    static
    {
        keys.add(Keys.LEFT);
        keys.add(Keys.RIGHT);
        keys.add(Keys.JUMP);
        keys.add(Keys.FIRE);
        keys.add(Keys.UP);
        keys.add(Keys.DOWN);
    }

    public MarioController(WorldWrapper world)
    {
        this.world = world;
        this.maryo = world.getMario();
        /*rbg = new ParallaxBackground(new ParallaxLayer[]{
                new ParallaxLayer(new TextureRegion(new Texture(Gdx.files.internal("data/background/green_junglehills.png"))), new Vector2(), new Vector2(0, 0))
        }, 10, 7, new Vector2(150, 0));*/
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
        keys.add(Keys.JUMP);
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
        jumpingPressed = false;
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
        grounded = isPlayerGrounded();
		if(!grounded)
		{
			maryo.setWorldState(Maryo.WorldState.JUMPING);
		}
        processInput();
        if (grounded && maryo.getWorldState().equals(Maryo.WorldState.JUMPING))
        {
            maryo.setWorldState(Maryo.WorldState.IDLE);
        }
        // simply updates the state time
        maryo.updateStateTime(delta);
        //world.getLevel().getPb().moveX(delta);
		
    }

    /**
     * Change Mario's state and parameters based on input controls *
     */
    private boolean processInput()
    {
        Vector2 vel = maryo.getBody().getLinearVelocity();
        Vector2 pos = maryo.getBody().getPosition();
        if (keys.contains(Keys.JUMP))
        {
            System.out.println("jump");
            if (!maryo.getWorldState().equals(Maryo.WorldState.JUMPING))
            {
                jumpingPressed = true;
                jumpPressedTime = System.currentTimeMillis();
                maryo.setWorldState(Maryo.WorldState.JUMPING);
                //maryo.getVelocity().y = MAX_JUMP_SPEED;
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
                        //maryo.getVelocity().y = MAX_JUMP_SPEED;
                        maryo.getBody().setTransform(pos.x, pos.y + 0.01f, 0);
                        maryo.getBody().setLinearVelocity(vel.x, vel.y = +10f);
                    }
                }
            }
        }
        if (keys.contains(Keys.LEFT))
        {
            System.out.println("left");
            // left is pressed
            maryo.setFacingLeft(true);
            if (!maryo.getWorldState().equals(Maryo.WorldState.JUMPING))
            {
                maryo.setWorldState(Maryo.WorldState.WALKING);
            }
            //if (vel.x > -MAX_VEL)
            //{
                //maryo.getBody().applyLinearImpulse(-1.2f, 0, 0, 0, true);
                maryo.getBody().setLinearVelocity(vel.x = -4f, vel.y);
            //}
        }
        else if (keys.contains(Keys.RIGHT))
        {
            System.out.println("right");
            // right is pressed
            maryo.setFacingLeft(false);
            if (!maryo.getWorldState().equals(Maryo.WorldState.JUMPING))
            {
                maryo.setWorldState(Maryo.WorldState.WALKING);
            }
            //if (vel.x < MAX_VEL)
            //{
                //maryo.getBody().applyLinearImpulse(1.2f, 0, 0, 0, true);
                maryo.getBody().setLinearVelocity(vel.x = +4f, vel.y);
            //}
        }
        else if (keys.contains(Keys.DOWN))
        {
            System.out.println("down");
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
            maryo.getBody().setLinearVelocity(vel.x * 0.7f, vel.y);
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
                    && (contact.getFixtureA() == maryo.getSensorFixture() || contact.getFixtureB() == maryo.getSensorFixture()))
            {
                /*Vector2 pos = maryo.getBody().getPosition();
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

    public void setMaryo(Maryo mario)
    {
        this.maryo = mario;
    }
}
