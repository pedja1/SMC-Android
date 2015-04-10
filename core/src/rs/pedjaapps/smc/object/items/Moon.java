package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.GameSaveUtility;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 29.3.15..
 */
public class Moon extends Item
{
    public static final float VELOCITY_POP = 1.6f;
    public static final float DEF_SIZE = 0.59375f;
    public Moon(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        textureAtlas = "data/game/items/moon.pack";
    }

    @Override
    public void loadTextures()
    {
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Animation animation = new Animation(2f, atlas.getRegions());
        animation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);//TODO how to set frame time to each frame
        Assets.animations.put(textureAtlas, animation);
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);
        if(popFromBox)
        {
            // scale velocity to frame units
            velocity.scl(delta);

            // update position
            position.add(velocity);
            body.y = position.y;
            updateBounds();

            // un-scale velocity (not in frame time)
            velocity.scl(1 / delta);

            if(position.y >= popTargetPosY)
            {
                popFromBox = false;
            }
        }
    }

    @Override
    public void popOutFromBox(float popTargetPosY)
    {
        super.popOutFromBox(popTargetPosY);
        visible = true;
        popFromBox = true;
        velocity.y = VELOCITY_POP;
        originalPosY = position.y;
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        if(!visible)return;
        TextureRegion frame = Assets.animations.get(textureAtlas).getKeyFrame(stateTime, true);
        Utility.draw(spriteBatch, frame, position.x, position.y, bounds.height);
    }

    @Override
    public void hitPlayer()
    {
        playerHit = true;
        position.set(-1, -1, -1);//has the effect of removing item (if its not on screen it wont be drawn)
        GameSaveUtility.getInstance().save.lifes += 3;

        Sound sound = Assets.manager.get("data/sounds/item/moon.ogg");
        if(sound != null && Assets.playSounds)sound.play();
    }
}
