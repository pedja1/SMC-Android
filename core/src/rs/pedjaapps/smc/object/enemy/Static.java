package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Static extends Enemy {
    public static final float POS_Z = 0.094f;
    private TextureRegion texture;

    public Static(float x, float y, float z, float width, float height, float speed, int fireResistance, float iceResistance) {
        super(x, y, z, width, height);
        mSpeed = speed;
        mCanBeHitFromShell = 0;
        mFireResistant = fireResistance;
        mIceResistance = iceResistance;
        position.z = POS_Z;
        ppEnabled = false;
    }

    @Override
    public void initAssets() {
        texture = MaryoGame.game.assets.get(Assets.ATLAS_DYNAMIC, TextureAtlas.class)
                .findRegion(textureName);
    }

    @Override
    public void dispose() {
        texture = null;
    }

    @Override
    public boolean isBullet() {
        return true;
    }

    @Override
    public void _render(SpriteBatch spriteBatch) {
        if (texture != null) {
            float width = Utility.getWidth(texture, drawRect.height);
            float originX = width * 0.5f;
            float originY = drawRect.height * 0.5f;
            spriteBatch.draw(texture, drawRect.x, drawRect.y, originX, originY, width, drawRect.height,
                    1, 1, -mRotationZ);
        }
    }

    @Override
    public boolean canBeKilledByJumpingOnTop() {
        return false;
    }

    private float getRotation() {
        float circumference = (float) Math.PI * (colRect.width);
        float deltaVelocity = mSpeed * Gdx.graphics.getDeltaTime();

        float step = circumference / deltaVelocity;

        float frameRotation = 360 / step;//degrees
        mRotationZ += frameRotation;
        if (mRotationZ > 360) mRotationZ = mRotationZ - 360;

        return mRotationZ;
    }

    public void _update(float deltaTime) {
        stateTime += deltaTime;
        mRotationZ = getRotation();
        if (deadByBullet) {
            // Setting initial vertical acceleration
            acceleration.y = Constants.GRAVITY;

            // Convert acceleration to frame time
            acceleration.scl(deltaTime);

            // apply acceleration to change velocity
            velocity.add(acceleration);

            checkCollisionWithBlocks(deltaTime, false, false);
        }
    }

    @Override
    protected TextureRegion getDeadTextureRegion() {
        return texture;
    }

    @Override
    protected boolean handleCollision(GameObject object, boolean vertical) {
        if (object instanceof Enemy && ((Enemy) object).handleCollision) {
            GameSave.addScore(((Enemy) object).mKillPoints);
            ((Enemy) object).downgradeOrDie(this, true, false);
        }
        return true;
    }
}
