package rs.papltd.smc.model.enemy;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import rs.papltd.smc.*;
import rs.papltd.smc.utility.*;

/**
 * Created by pedja on 18.5.14..
 */
public class Eato extends Enemy
{
    public Eato(World world, Vector3 position, float width, float height)
    {
        super(world, position, width, height);
    }

    @Override
    public void loadTextures()
    {
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureAtlas.AtlasRegion> frames = atlas.getRegions();
        frames.add(atlas.findRegion(TKey.two.toString()));

        Assets.animations.put(textureAtlas, new Animation(0.18f, frames));
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion frame = Assets.animations.get(textureAtlas).getKeyFrame(stateTime, true);

        //spriteBatch.draw(frame, body.getPosition().x - getBounds().width/2, body.getPosition().y - getBounds().height/2, bounds.width, bounds.height);
        Utility.draw(spriteBatch, frame, body.getPosition().x - bounds.width / 2, body.getPosition().y - bounds.height / 2, bounds.height);
    }
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		body.getPosition().x = position.x;
		body.getPosition().y = position.y;
	}
	
	@Override
	public BodyDef.BodyType getBodyType()
	{
		return BodyDef.BodyType.KinematicBody;
	}
}
