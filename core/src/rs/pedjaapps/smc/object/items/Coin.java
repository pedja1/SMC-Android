package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 24.5.14..
 */
public class Coin extends Item {
    public static final float DEF_SIZE = 0.59375f;
    public static final int TYPE_YELLOW = 0;
    public static final int TYPE_RED = 1;
    public int points = 5;
    private Animation<TextureRegion> animation;
    private int coinType;

    /**
     * Coin will move out of the screen when collected
     */
    private boolean scrollOut;


    public Coin(float x, float y, float z, float width, float height, int coinType) {
        super(x, y, z, width, height);
        position.z = 0.041f;
        this.coinType = coinType;
    }

    @Override
    public int getType() {
        return TYPE_GOLDPIECE;
    }

    @Override
    public float maxVelocity() {
        return 0;
    }

    @Override
    public void initAssets() {
        TextureAtlas atlas = MaryoGame.game.assets.get(Assets.ATLAS_DYNAMIC);
        Array<TextureAtlas.AtlasRegion> frames = new Array<TextureAtlas.AtlasRegion>();

        for (int i = 1; i < 11; i++) {
            frames.add(atlas.findRegion("game_items_waffle_" + (coinType == TYPE_YELLOW ? "yellow" : "red")
                    + "_" + String.valueOf(i)));
        }

        animation = new Animation(0.10f, frames);

        if (coinType == TYPE_YELLOW) {
            points = 5;
        } else {
            points = 100;
        }

        stateTime = MathUtils.random(1.2f);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (!visible) return;
        //if (!playerHit)
        //{
        TextureRegion frame = animation.getKeyFrame(stateTime, true);
        Utility.draw(spriteBatch, frame, position.x, position.y, drawRect.height);
        //}
        //else
        //{
        //    font.draw(spriteBatch, position + "", pointsTextPosition.x, pointsTextPosition.y);
        //}
    }

    @Override
    public void updateItem(float delta) {
        super.updateItem(delta);
        //pointsTextPosition.y += 3f * delta;
        if (popFromBox) {
            // scale velocity to frame units
            velocity.scl(delta);

            // update position
            position.add(velocity);
            colRect.y = position.y;
            updateBounds();

            // un-scale velocity (not in frame time)
            velocity.scl(1 / delta);

            if (position.y >= originalPosY + colRect.height + 0.3f) {
                popFromBox = false;
                collect();
            }
        }
        if (scrollOut) {
            velocity.scl(delta);

            position.add(velocity);
            colRect.y = position.y;
            colRect.x = position.x;
            updateBounds();

            // un-scale velocity (not in frame time)
            velocity.scl(1 / delta);
            OrthographicCamera cam = ((GameScreen) MaryoGame.game.currentScreen).cam;
            float camLeft = cam.position.x - cam.viewportWidth / 2;
            float camTop = cam.position.y + cam.viewportHeight / 2;
            float objRight = position.x + drawRect.width;
            float objBottom = position.y;
            if (objRight < camLeft || objBottom > camTop)//is visible
            {
                trashThisObject();
            }

        }
    }

    private void collect() {
        scrollOut = true;
        velocity.x = -9f;
        velocity.y = 2f;
        MaryoGame.game.addKillPoints(points, position.x, position.y + drawRect.height);
    }

    @Override
    public void hitPlayer() {
        if (!collectible) return;
        playerHit = true;
        GameSave.addCoins(MaryoGame.game.currentScreen, coinType == TYPE_YELLOW ? 1 : 5);
        Sound sound = getSound();
        SoundManager.play(sound);
        GameSave.addScore(points);

        collect();
    }

    public Sound getSound() {
        if (coinType == TYPE_YELLOW)
            return MaryoGame.game.assets.get(Assets.SOUND_ITEM_GOLDPIECE1);
        else
            return MaryoGame.game.assets.get(Assets.SOUND_ITEM_GOLDPIECE_RED);
    }

    @Override
    public void popOutFromBox(float popTargetPositionY) {
        super.popOutFromBox(popTargetPositionY);
        visible = true;
        popFromBox = true;
        velocity.y = 4f;
        originalPosY = position.y;
        if (coinType == TYPE_YELLOW) {
            GameSave.addCoins(MaryoGame.game.currentScreen, 1);
        } else {
            GameSave.addCoins(MaryoGame.game.currentScreen, 5);
        }
        GameSave.addScore(points);
    }

    @Override
    public void dispose() {
        super.dispose();
        animation = null;
    }
}
