package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 29.3.15..
 */
public class Moon extends Item
{
    public static final float VELOCITY_POP = 1.6f;
    public static final float DEF_SIZE = 0.65625f;
    private Animation<TextureRegion> animation;

    public Moon(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        textureAtlas = "data/game/items/moon.pack";
        position.z = 0.052f;
    }

    @Override
    public int getType() {
        return TYPE_MOON;
    }

    @Override
    public void initAssets()
    {
        TextureAtlas atlas = world.screen.game.assets.manager.get(textureAtlas);
        animation = new Animation(2f, atlas.getRegions());
        animation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
    }

    @Override
    public void updateItem(float delta)
    {
        super.updateItem(delta);
        if (popFromBox)
        {
            // scale velocity to frame units
            velocity.scl(delta);

            // update position
            position.add(velocity);
            mColRect.y = position.y;
            updateBounds();

            // un-scale velocity (not in frame time)
            velocity.scl(1 / delta);

            if (position.y >= popTargetPosY)
            {
                popFromBox = false;
                isInBox = false;
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
    public void _render(SpriteBatch spriteBatch)
    {
        if (!visible) return;
        TextureRegion frame = animation.getKeyFrame(stateTime, true);
        Utility.draw(spriteBatch, frame, position.x, position.y, mDrawRect.height);
    }

    @Override
    protected boolean handleDroppedBelowWorld()
    {
        world.trashObjects.add(this);
        return false;
    }

    @Override
    public void hitPlayer()
    {
        if (isInBox) return;
        playerHit = true;
        world.trashObjects.add(this);
        GameSave.save.lifes += 3;
        GameSave.save.points += 4000;

        Sound sound = world.screen.game.assets.manager.get("data/sounds/item/moon.mp3");
        SoundManager.play(sound);
        ((GameScreen) world.screen).killPointsTextHandler.add(4000, position.x, position.y + mDrawRect.height);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        animation = null;
    }

    @Override
    public float maxVelocity()
    {
        return 0;
    }
}
