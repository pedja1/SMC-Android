package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by pedja on 19.9.15..
 */
public class MovingPlatform extends Sprite
{
    private Vector3 mOriginPosition;
    public float max_distance;//max distance until it starts going back
    public float speed;//moving speed
    public float touch_time;// time when touched until it starts shaking (in ms)
    public float shake_time;// time it's shaking until falling
    public float touch_move_time;// time when touched until it starts moving
    public int move_type, middle_img_count, platformState = -1;
    public String direction, image_top_left, image_top_middle, image_top_right;
    private TextureRegion rLeft, rMiddle, rRight;
    boolean touched, forward = true;

    public static final int MOVING_PLATFORM_TYPE_LINE = 0;//left, right, up, down
    public static final int MOVING_PLATFORM_TYPE_CIRCLE = 1;//circle
    public static final int MOVING_PLATFORM_TYPE_PATH = 2;
    public static final int MOVING_PLATFORM_TYPE_PATH_BACKWARDS = 3;

    public static final int MOVING_PLATFORM_STAY = 0;
    public static final int MOVING_PLATFORM_TOUCHED = 1;
    public static final int MOVING_PLATFORM_SHAKE = 2;
    public static final int MOVING_PLATFORM_FALL = 3;

    public MovingPlatform(World world, Vector2 size, Vector3 position, Rectangle colRect)
    {
        super(world, size, position, colRect);
        mOriginPosition = new Vector3(position);
    }

    @Override
    public void initAssets()
    {
        if (textureAtlas != null)
        {
            TextureAtlas atlas = world.screen.game.assets.manager.get(textureAtlas);
            rLeft = atlas.findRegion(image_top_left);
            rMiddle = atlas.findRegion(image_top_middle);
            rRight = atlas.findRegion(image_top_right);
        }
        if (platformState == -1)
        {
            speed /= 2;
            if (touch_time > 0)
            {
                platformState = MOVING_PLATFORM_STAY;
            }
            else
            {
                platformState = MOVING_PLATFORM_TOUCHED;
            }
            if(type == Type.halfmassive)
                type = Type.massive;
        }
    }

    @Override
    public void _render(SpriteBatch spriteBatch)
    {
        float singlePeaceWidth = mDrawRect.width / (2 + middle_img_count);
        spriteBatch.draw(rLeft, mDrawRect.x, mDrawRect.y, singlePeaceWidth, mDrawRect.height);

        for (int i = 0; i < middle_img_count; i++)
        {
            spriteBatch.draw(rMiddle, mDrawRect.x + singlePeaceWidth * (i + 1), mDrawRect.y, singlePeaceWidth, mDrawRect.height);
        }

        spriteBatch.draw(rRight, mDrawRect.x + singlePeaceWidth * (middle_img_count + 1), mDrawRect.y, singlePeaceWidth, mDrawRect.height);
    }

    @Override
    public void _update(float delta)
    {
        if (platformState == MOVING_PLATFORM_TOUCHED)
        {
            if (move_type == MOVING_PLATFORM_TYPE_LINE)
            {
                if ("right".equals(direction))//right
                {
                    float remainingDistance = (mOriginPosition.x + max_distance) - position.x;
                    if (forward)
                    {
                        if (remainingDistance <= 0)
                        {
                            forward = false;
                            velocity.x = 0;
                        }
                        else
                        {
                            velocity.x = speed;
                        }
                    }
                    else
                    {
                        if (remainingDistance >= max_distance)
                        {
                            forward = true;
                            velocity.x = 0;
                        }
                        else
                        {
                            velocity.x = -speed;
                        }
                    }
                }
                else if ("left".equals(direction))//left
                {
                    float remainingDistance = position.x - (mOriginPosition.x - max_distance);
                    if (forward)
                    {
                        if (remainingDistance <= 0)
                        {
                            forward = false;
                            velocity.x = 0;
                        }
                        else
                        {
                            velocity.x = -speed;
                        }
                    }
                    else
                    {
                        if (remainingDistance >= max_distance)
                        {
                            forward = true;
                            velocity.x = 0;
                        }
                        else
                        {
                            velocity.x = speed;
                        }
                    }
                }
                else if ("up".equals(direction))//up
                {
                    float remainingDistance = mOriginPosition.y + max_distance - position.y;
                    if (forward)
                    {
                        if (remainingDistance <= 0)
                        {
                            forward = false;
                            velocity.y = 0;
                        }
                        else
                        {
                            velocity.y = speed;
                        }
                    }
                    else
                    {
                        if (remainingDistance >= max_distance)
                        {
                            forward = true;
                            velocity.y = 0;
                        }
                        else
                        {
                            velocity.y = -speed;
                        }
                    }
                }
                else//down
                {
                    float remainingDistance = max_distance - (mOriginPosition.y - position.y);
                    if (forward)
                    {
                        if (remainingDistance <= 0)
                        {
                            forward = false;
                            velocity.y = 0;
                        }
                        else
                        {
                            velocity.y = -speed;
                        }
                    }
                    else
                    {
                        if (remainingDistance >= max_distance)
                        {
                            forward = true;
                            velocity.y = 0;
                        }
                        else
                        {
                            velocity.y = speed;
                        }
                    }
                }
            }
        }
        else if (platformState == MOVING_PLATFORM_SHAKE)
        {

        }
        else if (platformState == MOVING_PLATFORM_FALL)
        {

        }
        else //if(platformState == MOVING_PLATFORM_STAY)
        {
            //do nothing
            velocity.set(0, 0, 0);
        }
        velocity.scl(delta);
        // update position
        position.add(velocity);
        mColRect.x = position.x;
        mColRect.y = position.y;
        updateBounds();

        // un-scale velocity (not in frame time)
        velocity.scl(1 / delta);


        if (velocity.x < 0)//moving left
        {
            if (world.maryo.mColRect.overlaps(mColRect) && world.maryo.mColRect.x + world.maryo.mColRect.width > mColRect.x)
            {
                world.maryo.mColRect.x = world.maryo.position.x = mColRect.x - world.maryo.mColRect.width;
                world.maryo.updateBounds();
            }
        }
        else if (velocity.x > 0)//moving right
        {
            if (world.maryo.mColRect.overlaps(mColRect) && mColRect.x + mColRect.width > world.maryo.mColRect.x)
            {
                world.maryo.mColRect.x = world.maryo.position.x = mColRect.x + mColRect.width;
                world.maryo.updateBounds();
            }
        }
        if (velocity.y < 0)//down
        {
            if (world.maryo.mColRect.overlaps(mColRect) && world.maryo.mColRect.y + world.maryo.mColRect.height > mColRect.y)
            {
                world.maryo.mColRect.y = world.maryo.position.y = mColRect.y - world.maryo.mColRect.height;
                world.maryo.updateBounds();
            }
        }
        else if (velocity.y > 0)//up
        {
            if (world.maryo.mColRect.overlaps(mColRect) && world.maryo.mColRect.y < mColRect.x + mColRect.height)
            {
                world.maryo.mColRect.y = world.maryo.position.y = mColRect.y + mColRect.height;
                world.maryo.updateBounds();
            }
        }

    }

    @Override
    public String toString()
    {
        return "MovingPlatform{" +
                "max_distance=" + max_distance +
                "\n speed=" + speed +
                "\n touch_time=" + touch_time +
                "\n shake_time=" + shake_time +
                "\n touch_move_time=" + touch_move_time +
                "\n move_type=" + move_type +
                "\n middle_img_count=" + middle_img_count +
                "\n direction='" + direction + '\'' +
                "\n image_top_left='" + image_top_left + '\'' +
                "\n image_top_middle='" + image_top_middle + '\'' +
                "\n image_top_right='" + image_top_right + '\'' +
                "\n rLeft=" + rLeft +
                "\n rMiddle=" + rMiddle +
                "\n rRight=" + rRight +
                "}\n\n" + super.toString();
    }
}
