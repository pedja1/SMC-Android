package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 29.3.15..
 */
public class Moon extends Item {
    public static final float VELOCITY_POP = 1.6f;
    public static final float DEF_SIZE = 0.65625f;
    private Animation<TextureRegion> animation;

    public Moon(float x, float y, float z, float width, float height) {
        super(x, y, z, width, height);
        position.z = 0.052f;
    }

    @Override
    public int getType() {
        return TYPE_MOON;
    }

    @Override
    public void initAssets() {
        TextureAtlas atlas = MaryoGame.game.assets.get(Assets.ATLAS_DYNAMIC);
        animation = new Animation(1f, atlas.findRegion("game_items_moon_1"),
                atlas.findRegion("game_items_moon_2"));
        animation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
    }

    @Override
    public void updateItem(float delta) {
        super.updateItem(delta);
        if (popFromBox) {
            // scale velocity to frame units
            velocity.scl(delta);

            // update position
            position.add(velocity);
            colRect.y = position.y;
            updateBounds();

            // un-scale velocity (not in frame time)
            velocity.scl(1 / delta);

            if (position.y >= popTargetPosY) {
                popFromBox = false;
                isInBox = false;
            }
        }
    }

    @Override
    public void popOutFromBox(float popTargetPosY) {
        super.popOutFromBox(popTargetPosY);
        visible = true;
        popFromBox = true;
        velocity.y = VELOCITY_POP;
        originalPosY = position.y;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (!visible) return;
        TextureRegion frame = animation.getKeyFrame(stateTime, true);
        Utility.draw(spriteBatch, frame, position.x, position.y, drawRect.height);
    }

    @Override
    protected boolean handleDroppedBelowWorld() {
        trashThisObject();
        return false;
    }

    @Override
    public void hitPlayer() {
        if (isInBox) return;
        playerHit = true;
        trashThisObject();
        GameSave.addLifes(3);
        GameSave.addScore(4000);

        Sound sound = MaryoGame.game.assets.get(Assets.SOUND_ITEM_MOON);
        SoundManager.play(sound);
        MaryoGame.game.addKillPoints(4000, position.x, position.y + drawRect.height);
    }

    @Override
    public void dispose() {
        super.dispose();
        animation = null;
    }

    @Override
    public float maxVelocity() {
        return 0;
    }
}
