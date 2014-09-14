package rs.pedjaapps.smc.model;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.model.items.Item;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 24.5.14..
 */
public class Coin extends Item
{

    public Coin(Vector3 position, float width, float height)
    {
        super(position, width, height);
    }

    @Override
    public void loadTextures()
    {
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureAtlas.AtlasRegion> frames = new Array<TextureAtlas.AtlasRegion>();//atlas.getRegions();
		//frames.sort();
        //frames.add(frames.removeIndex(1));

		for(int i = 1; i < 11; i++)
		{
			frames.add(atlas.findRegion(i + ""));
		}

        Assets.animations.put(textureAtlas, new Animation(0.10f, frames));
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion frame = Assets.animations.get(textureAtlas).getKeyFrame(stateTime, true);

        //spriteBatch.draw(frame, body.getPosition().x - getBounds().width/2, body.getPosition().y - getBounds().height/2, bounds.width, bounds.height);
        Utility.draw(spriteBatch, frame, position.x - getBounds().width / 2, position.y - getBounds().height / 2, bounds.height);
    }
}
