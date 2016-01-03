package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 24.5.14..
 */
public class Coin extends Item
{
    public static final float DEF_SIZE = 0.59375f;
    public static final String DEF_ATL = "data/game/items/goldpiece/yellow.pack";
    public int points = 5;
    private Animation animation;

    /**
     * Coin will move out of the screen when collected
     */
    boolean scrollOut;


    public Coin(World world, Vector3 position, float width, float height)
    {
        super(world, position, width, height);
        position.z = 0.041f;
    }

    @Override
    public float maxVelocity()
    {
        return 0;
    }

    @Override
    public void initAssets()
    {
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureAtlas.AtlasRegion> frames = new Array<TextureAtlas.AtlasRegion>();

        for (int i = 1; i < 11; i++)
        {
            frames.add(atlas.findRegion(i + ""));
        }

        animation = new Animation(0.10f, frames);

        if (textureAtlas.contains("yellow"))
        {
            points = 5;
        }
        else
        {
            points = 100;
        }

        stateTime = MathUtils.random(1.2f);
    }

    @Override
    public void _render(SpriteBatch spriteBatch)
    {
        if (!visible) return;

        TextureRegion frame = animation.getKeyFrame(stateTime, true);
        Utility.draw(spriteBatch, frame, position.x, position.y, mDrawRect.height);
    }

    @Override
    public void updateItem(float delta)
    {
        super.updateItem(delta);
        //pointsTextPosition.y += 3f * delta;
        if (scrollOut)
        {
            velocity.scl(delta);

            position.add(velocity);
            mColRect.y = position.y;
            mColRect.x = position.x;
            updateBounds();

            // un-scale velocity (not in frame time)
            velocity.scl(1 / delta);
            OrthographicCamera cam = ((GameScreen) world.screen).cam;
            float camLeft = cam.position.x - cam.viewportWidth / 2;
            float camTop = cam.position.y + cam.viewportHeight / 2;
            float objRight = position.x + mDrawRect.width;
            float objBottom = position.y;
            if (objRight < camLeft || objBottom > camTop)//is visible
            {
                if(world.level.gameObjects.removeValue(this, true))
                {
                    dispose();
                }
            }

        }
    }

    private void collect()
    {
        scrollOut = true;
        velocity.x = -9f;
        velocity.y = 2f;
        ((GameScreen) world.screen).killPointsTextHandler.add(points, position.x, position.y + mDrawRect.height);
    }

    @Override
    public void hitPlayer()
    {
        if (!collectible) return;
        playerHit = true;
        Sound sound;
        if (textureAtlas.contains("yellow"))
        {
            sound = Assets.manager.get("data/sounds/item/goldpiece_1.mp3");
        }
        else
        {
            sound = Assets.manager.get("data/sounds/item/goldpiece_red.mp3");
        }
        SoundManager.play(sound);
        GameSave.save.coins++;
        GameSave.save.points += points;

        collect();
    }

    @Override
    public void dispose()
    {
        super.dispose();
        animation = null;
        world.COIN_POOL.free(this);
    }

    @Override
    public void reset()
    {
        super.reset();
        scrollOut = false;
        collectible = true;
        velocity.set(0, 0, 0);
    }
}
