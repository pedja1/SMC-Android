package rs.papltd.smc.model;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import rs.papltd.smc.*;
import rs.papltd.smc.model.custom_objects.*;
import rs.papltd.smc.utility.*;

/**
 * Created by pedja on 24.5.14..
 */
public class Coin extends CustomObject
{

    public Coin(World world, Vector3 position, float width, float height)
    {
        super(world, position, width, height);
    }

    @Override
    public void loadTextures()
    {
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureAtlas.AtlasRegion> frames = atlas.getRegions();
        frames.add(frames.removeIndex(1));


        Assets.animations.put(textureAtlas, new Animation(0.10f, frames));
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion frame = Assets.animations.get(textureAtlas).getKeyFrame(stateTime, true);

        //spriteBatch.draw(frame, body.getPosition().x - getBounds().width/2, body.getPosition().y - getBounds().height/2, bounds.width, bounds.height);
        Utility.draw(spriteBatch, frame, body.getPosition().x - getBounds().width / 2, body.getPosition().y - getBounds().height / 2, bounds.height);
    }
}
