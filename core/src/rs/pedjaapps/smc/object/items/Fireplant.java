package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 29.3.15..
 */
public class Fireplant extends Item {
    public static final int POINTS = 700;
    public static final float VELOCITY_POP = 1.6f;
    public static final float DEF_SIZE = 0.65625f;
    private Animation<TextureRegion> animation;

    public Fireplant(float x, float y, float z, float width, float height) {
        super(x, y, z, width, height);
        position.z = 0.051f;
    }

    @Override
    public int getType() {
        return TYPE_FIREPLANT;
    }

    @Override
    public void initAssets() {
        TextureAtlas atlas = MaryoGame.game.assets.get(Assets.ATLAS_DYNAMIC);
        animation = new Animation(1f, atlas.findRegion("game_items_fireplant_left"),
                atlas.findRegion("game_items_fireplant"),
                atlas.findRegion("game_items_fireplant_right"));
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
        ParticleEffect effect = MaryoGame.game.assets.get("data/animation/particles/fireplant_emitter.p", ParticleEffect.class);
        effect.setPosition(position.x + drawRect.width / 2, position.y + drawRect.height / 2);
        effect.start();
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (!visible) return;
        TextureRegion frame = animation.getKeyFrame(stateTime, true);
        Utility.draw(spriteBatch, frame, position.x, position.y, drawRect.height);

        ParticleEffect effect = MaryoGame.game.assets.get("data/animation/particles/fireplant_emitter.p", ParticleEffect.class);
        effect.setPosition(position.x + drawRect.width / 2, position.y + drawRect.height / 2);
        effect.draw(spriteBatch, Gdx.graphics.getDeltaTime());
    }

    @Override
    public void hitPlayer() {
        if (isInBox) return;
        playerHit = true;
        MaryoGame.game.currentScreen.world.maryo.upgrade(Maryo.MaryoState.fire, this, false);
        trashThisObject();
        GameSave.addScore(POINTS);
        MaryoGame.game.addKillPoints(POINTS, position.x, position.y + drawRect.height);
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
