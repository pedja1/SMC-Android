package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Spika extends Enemy {
    private static final float POS_Z = 0.09f;
    private static final float ACCELERATION = 2;

    private float mSpeed;
    private float mRotation, mDetectionSize;
    private TextureRegion texture;
    private String color;
    private Rectangle tmpRect = new Rectangle();

    public Spika(float x, float y, float z, float width, float height, String color) {
        super(x, y, z, width, height);
        this.color = color;
        if ("orange".equals(color)) {
            mSpeed = 2;
            mDetectionSize = 2.5f;
            mKillPoints = 50;
            mFireResistant = 0;
            mIceResistance = 0;
        } else if ("green".equals(color)) {
            mSpeed = 2.66f;
            mDetectionSize = 3.4375f;
            mKillPoints = 200;
            mFireResistant = 0;
            mIceResistance = 0.1f;
        } else if ("grey".equals(color)) {
            mSpeed = 4.66f;
            mDetectionSize = 5.15625f;
            mKillPoints = 500;
            mFireResistant = 1;
            mIceResistance = 0.5f;
        }
        position.z = POS_Z;
    }

    @Override
    public void initAssets() {
        texture = MaryoGame.game.assets.get(Assets.ATLAS_DYNAMIC, TextureAtlas.class)
                .findRegion("enemy_spika_" + color);
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
                    1, 1, -mRotation);
        }
    }

    @Override
    public boolean canBeKilledByJumpingOnTop() {
        return false;
    }

    private float getRotation() {
        float circumference = (float) Math.PI * (colRect.width);
        float deltaVelocity = velocity.x * Gdx.graphics.getDeltaTime();

        float step = circumference / deltaVelocity;

        float frameRotation = 360 / step;//degrees
        mRotation += frameRotation;
        if (mRotation > 360) mRotation = mRotation - 360;

        return mRotation;
    }

    public void _update(float deltaTime) {
        boolean playerInFront = checkMaryoInFront();

        stateTime += deltaTime;

        // Setting initial vertical acceleration
        acceleration.y = Constants.GRAVITY;

        if (!deadByBullet && playerInFront) {
            if (MaryoGame.game.currentScreen.world.maryo.colRect.x < colRect.x) {
                acceleration.x = -ACCELERATION;
            } else {
                acceleration.x = ACCELERATION;
            }
        }

        // Convert acceleration to frame time
        acceleration.scl(deltaTime);

        // apply acceleration to change velocity
        velocity.add(acceleration);

        checkCollisionWithBlocks(deltaTime, !deadByBullet, !deadByBullet);

        velocity.x *= 0.99f;

        mRotation = getRotation();
        if (velocity.x > mSpeed) {
            velocity.x = mSpeed;
        }
        if (velocity.x < -mSpeed) {
            velocity.x = -mSpeed;
        }
    }

    @Override
    protected boolean handleCollision(GameObject object, boolean vertical) {
        super.handleCollision(object, vertical);
        if (object instanceof Enemy && object != this && ((Enemy) object).handleCollision && (velocity.x > 0.5f || velocity.x < 0.5f)) {
            ((Enemy) object).downgradeOrDie(this, false, false);
        }
        return false;
    }

    @Override
    public void handleCollision(ContactType contactType) {
        switch (contactType) {
            case stopper:
                break;
            case player:
                break;
        }
    }

    @Override
    protected TextureRegion getDeadTextureRegion() {
        return texture;
    }

    private boolean checkMaryoInFront() {
        Maryo maryo = MaryoGame.game.currentScreen.world.maryo;//TODO create shortcut for getting Maryo object
        if (maryo == null) return false;
        tmpRect.set(colRect.x - mDetectionSize, colRect.y, mDetectionSize * 2 + colRect.width, colRect.height);
        return maryo.colRect.overlaps(tmpRect);
    }

}
