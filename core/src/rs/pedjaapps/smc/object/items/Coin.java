package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

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
    public int points;
    public boolean isRed;

    /**
     * Coin will move out of the screen when collected
     */
    private boolean scrollOut;

    private Animation animation;


    public Coin(float x, float y, float width, float height, boolean isRed)
    {
        super(x, y, width, height);
        this.isRed = isRed;
    }

    public Coin()
    {

    }

    @Override
    public void write(Json json)
    {
        super.write(json);
        json.writeValue("points", points);
        json.writeValue("isRed", isRed);
    }

    @Override
    public void read(Json json, JsonValue jsonMap)
    {
        super.read(json, jsonMap);
        points = json.readValue(int.class, jsonMap.get("points"));
        isRed = json.readValue(boolean.class, jsonMap.get("isRed"));
    }

    @Override
    public float maxVelocity()
    {
        return 0;
    }

    @Override
    public void initAssets()
    {
        if (animation == null)
        {
            TextureAtlas atlas = Assets.manager.get(Assets.DEFAULT_ATLAS);
            TextureRegion[] frames = new TextureRegion[6];

            if (isRed)
            {
                frames[0] = atlas.findRegion("environment/coins/red/1");
                frames[1] = atlas.findRegion("environment/coins/red/2");
                frames[2] = atlas.findRegion("environment/coins/red/3");
                frames[3] = atlas.findRegion("environment/coins/red/4");
                frames[4] = atlas.findRegion("environment/coins/red/5");
                frames[5] = atlas.findRegion("environment/coins/red/6");
            }
            else
            {
                frames[0] = atlas.findRegion("environment/coins/yellow/1");
                frames[1] = atlas.findRegion("environment/coins/yellow/2");
                frames[2] = atlas.findRegion("environment/coins/yellow/3");
                frames[3] = atlas.findRegion("environment/coins/yellow/4");
                frames[4] = atlas.findRegion("environment/coins/yellow/5");
                frames[5] = atlas.findRegion("environment/coins/yellow/6");
            }

            animation = new Animation(0.10f, frames);
        }
        if(isRed)
        {
            points = 100;
        }
        else
        {
            points = 5;
        }

        stateTime = MathUtils.random(1.2f);
    }

    @Override
    public void _render(SpriteBatch spriteBatch)
    {
        if (!visible) return;

        TextureRegion frame = animation.getKeyFrame(stateTime, true);
        Utility.draw(spriteBatch, frame, bounds.x, bounds.y, bounds.height);
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

            // un-scale velocity (not in frame time)
            velocity.scl(1 / delta);
            OrthographicCamera cam = ((GameScreen) World.getInstance().screen).cam;
            float camLeft = cam.position.x - cam.viewportWidth / 2;
            float camTop = cam.position.y + cam.viewportHeight / 2;
            float objRight = position.x + bounds.width;
            float objBottom = position.y;
            if (objRight < camLeft || objBottom > camTop)//is visible
            {
                if(World.getInstance().level.gameObjects.removeValue(this, true))
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
        ((GameScreen) World.getInstance().screen).killPointsTextHandler.add(points, position.x, position.y + bounds.height);
    }

    @Override
    public void hitPlayer()
    {
        if (!collectible) return;
        playerHit = true;
        Sound sound;
        if (!isRed)
        {
            sound = Assets.manager.get("data/sounds/item/coin.mp3");
        }
        else
        {
            sound = Assets.manager.get("data/sounds/item/coin_red.mp3");
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
        World.getInstance().COIN_POOL.free(this);
    }

    @Override
    public void reset()
    {
        super.reset();
        scrollOut = false;
        collectible = true;
        velocity.set(0, 0);
    }
}
