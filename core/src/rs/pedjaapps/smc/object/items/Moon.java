package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.assets.Assets;
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
    private Animation animation;

    public Moon(World world, Vector3 position, float width, float height)
    {
        super(world, position, width, height);
        textureAtlas = "data/game/items/moon.pack";
        position.z = 0.052f;
    }

    @Override
    public void initAssets()
    {
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        animation = new Animation(2f, atlas.getRegions());
        animation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);//TODO how to set frame time to each frame
    }

    @Override
    public void _render(SpriteBatch spriteBatch)
    {
        if(!visible)return;
        TextureRegion frame = animation.getKeyFrame(stateTime, true);
        Utility.draw(spriteBatch, frame, position.x, position.y, mDrawRect.height);
    }

    @Override
    public void hitPlayer()
    {
        playerHit = true;
        world.level.gameObjects.removeValue(this, true);
        GameSave.save.points += 4000;

        Sound sound = Assets.manager.get("data/sounds/item/moon.mp3");
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
