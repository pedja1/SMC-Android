package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Eato extends Enemy
{
    public static String DEAD_KEY;
    public Eato(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
    }

    @Override
    protected TextureRegion getDeadTextureRegion()
    {
        return Assets.loadedRegions.get(DEAD_KEY);
    }

    @Override
    public void initAssets()
    {
        DEAD_KEY = textureAtlas + ":dead";
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureAtlas.AtlasRegion> frames = new Array<>();//atlas.getRegions();
        		frames.add(atlas.findRegion(TKey.one.toString()));
        		frames.add(atlas.findRegion(TKey.two.toString()));
        		frames.add(atlas.findRegion(TKey.three.toString()));
        frames.add(atlas.findRegion(TKey.two.toString()));
        Assets.loadedRegions.put(DEAD_KEY, frames.get(0));

        Assets.animations.put(textureAtlas, new Animation(0.18f, frames));
    }

    @Override
    public void draw(SpriteBatch spriteBatch)
    {
        TextureRegion frame = Assets.animations.get(textureAtlas).getKeyFrame(stateTime, true);
        Utility.draw(spriteBatch, frame, position.x, position.y, mDrawRect.height);
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
