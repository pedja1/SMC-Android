package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Eato extends Enemy
{
    public static final float POSITION_Z = 0.087f;
    private String direction;
    private Animation<TextureRegion> animation;
    private TextureRegion tdead;
    private String color;

    public Eato(World world, Vector2 size, Vector3 position, String direction, String color)
    {
        super(world, size, position);
        this.direction = direction;
        this.color = color;
        position.z = POSITION_Z;
        mFireResistant = 1;
        mKillPoints = 150;

        if("top_left".equals(direction))
        {
            mRotationY = 180f;
        }
        else if("left_top".equals(direction))
        {
            mRotationZ = 90f;
            mRotationX = 180f;
        }
        else if("left_bottom".equals(direction))
        {
            mRotationZ = 90f;
        }
        else if("right_top".equals(direction))
        {
            mRotationZ = 90f;
        }
        else if("right_bottom".equals(direction))
        {
            mRotationZ = 270f;
            mRotationX = 180f;
        }
        else if("bottom_left".equals(direction))
        {
            mRotationX = 180f;
        }
        else if("bottom_right".equals(direction))
        {
            mRotationZ = 180f;
        }
        ppEnabled = false;
    }

    @Override
    protected String getDeadSound()
    {
        return Assets.SOUND_ENEMY_DIE_EATO;
    }

    @Override
    protected TextureRegion getDeadTextureRegion() {
        return tdead;
    }

    @Override
    public void initAssets()
    {
        TextureAtlas atlas = world.screen.game.assets.manager.get(Assets.ATLAS_DYNAMIC);

        Array<TextureRegion> frames = new Array<>(3);
        for (int i = 1; i < 4; i++)
            frames.add(atlas.findRegion("enemy_eato_" + color + "_" + String.valueOf(i)));

        tdead = frames.get(0);
        animation = new Animation<>(0.18f, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
    }

    @Override
    public void dispose()
    {
        animation = null;
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion frame = animation.getKeyFrame(stateTime, true);

        float width = Utility.getWidth(frame, mDrawRect.height);
        float originX = width * 0.5f;
        float originY = /*0;//*/mDrawRect.height * 0.5f;
        float rotation = mRotationZ;
        boolean flipX = mRotationY == 180;
        boolean flipY = mRotationX == 180;

        frame.flip(flipX, flipY);//flip it
        spriteBatch.draw(frame, mDrawRect.x, mDrawRect.y, originX, originY, width, mDrawRect.height, 1, 1, rotation);
        frame.flip(flipX, flipY);//return it to original

    }

    @Override
    public boolean canBeKilledByJumpingOnTop()
    {
        return false;
    }

    @Override
	public void update(float delta)
	{
		super.update(delta);
        if(deadByBullet)
        {
            // Setting initial vertical acceleration
            acceleration.y = Constants.GRAVITY;

            // Convert acceleration to frame time
            acceleration.scl(delta);

            // apply acceleration to change velocity
            velocity.add(acceleration);

            checkCollisionWithBlocks(delta, false, false);
        }
	}
}
