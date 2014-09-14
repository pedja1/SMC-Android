package rs.pedjaapps.smc.model.enemy;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Eato extends Enemy
{
    public Eato(Vector3 position, float width, float height)
    {
        super(position, width, height);
    }

    @Override
    public void loadTextures()
    {
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureAtlas.AtlasRegion> frames = new Array<TextureAtlas.AtlasRegion>();//atlas.getRegions();
        		frames.add(atlas.findRegion(TKey.one.toString()));
        		frames.add(atlas.findRegion(TKey.two.toString()));
        		frames.add(atlas.findRegion(TKey.three.toString()));
        frames.add(atlas.findRegion(TKey.two.toString()));

        Assets.animations.put(textureAtlas, new Animation(0.18f, frames));
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion frame = Assets.animations.get(textureAtlas).getKeyFrame(stateTime, true);

        //spriteBatch.draw(frame, body.getPosition().x - getBounds().width/2, body.getPosition().y - getBounds().height/2, bounds.width, bounds.height);
        Utility.draw(spriteBatch, frame, position.x - bounds.width / 2, position.y - bounds.height / 2, bounds.height);
    }
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
	}
}
