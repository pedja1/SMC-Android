package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Static extends Enemy
{
    public static final float POS_Z = 0.094f;

    public Static(World world, Vector2 size, Vector3 position, float speed, int fireResistance, float iceResistance)
    {
        super(world, size, position);
        mSpeed = speed;
        mCanBeHitFromShell = 0;
        mFireResistant = fireResistance;
        mIceResistance = iceResistance;
        position.z = POS_Z;
    }

    @Override
    public void initAssets()
    {

    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        Texture texture = Assets.manager.get(textureName);
        if (texture != null)
        {
            float width = Utility.getWidth(texture, mDrawRect.height);
            float originX = width * 0.5f;
            float originY = mDrawRect.height * 0.5f;
            spriteBatch.draw(texture, mDrawRect.x, mDrawRect.y, originX, originY, width, mDrawRect.height,
                    1, 1, -mRotationZ, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
        }
    }

    private float getRotation()
    {
        float circumference = (float) Math.PI * (mColRect.width);
        float deltaVelocity = mSpeed * Gdx.graphics.getDeltaTime();

        float step = circumference / deltaVelocity;

        float frameRotation = 360 / step;//degrees
        mRotationZ += frameRotation;
        if (mRotationZ > 360) mRotationZ = mRotationZ - 360;

        return mRotationZ;
    }

    public void update(float deltaTime)
    {
        stateTime += deltaTime;
        mRotationZ = getRotation();
    }

    @Override
    protected TextureRegion getDeadTextureRegion()
    {
        return Assets.manager.get(textureName);
    }

}
