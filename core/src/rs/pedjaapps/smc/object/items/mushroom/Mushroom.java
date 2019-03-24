package rs.pedjaapps.smc.object.items.mushroom;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 29.3.15..
 * <p/>
 * This file is part of SMC-Android
 * Copyright Predrag Čokulov 2015
 */
public abstract class Mushroom extends Item {
    private static final float VELOCITY = 1.5f;
    private static final float VELOCITY_POP = 1.6f;
    public static final float DEF_SIZE = 0.546875f;

    private boolean grounded = false;
    protected int mPickPoints;

    public boolean moving;

    public enum Direction {
        right, left
    }

    private Direction direction = Direction.right;

    public Mushroom(float x, float y, float z, float width, float height) {
        super(x, y, z, width, height);
    }

    @Override
    public void initAssets() {
        texture = MaryoGame.game.assets.get(Assets.ATLAS_DYNAMIC, TextureAtlas.class).findRegion(textureName);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (!visible) return;
        Utility.draw(spriteBatch, texture, position.x, position.y, drawRect.height);
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
                isInBox = false;
                popFromBox = false;
                moving = true;
                velocity.x = direction == Direction.right ? VELOCITY : -VELOCITY;
            }
        } else if (moving) {
            // Setting initial vertical acceleration
            acceleration.y = Constants.GRAVITY;

            // Convert acceleration to frame time
            acceleration.scl(delta);

            // apply acceleration to change velocity
            velocity.add(acceleration);

            checkCollisionWithBlocks(delta);

            switch (direction) {
                case right:
                    velocity.x = VELOCITY;
                    break;
                case left:
                    velocity.x = -VELOCITY;
                    break;
            }
        }
    }

    @Override
    protected boolean handleDroppedBelowWorld() {
        MaryoGame.game.trashObject(this);
        return false;
    }

    @Override
    protected boolean handleCollision(GameObject object, boolean vertical) {
        if (object instanceof Sprite
                && ((Sprite) object).type == Sprite.Type.massive) {
            if (vertical) {
                if (velocity.y < 0) {
                    grounded = true;
                }
                velocity.y = 0;
            } else {
                if (object.position.y + object.drawRect.height / 2 > position.y) {
                    direction = direction == Direction.right ? Direction.left : Direction.right;
                    velocity.x = velocity.x > 0 ? -velocity.x : Math.abs(velocity.x);
                }
            }
        }
        return false;
    }

    @Override
    public void hitPlayer() {
        if (isInBox) return;
        playerHit = true;
        performCollisionAction();
        if (mPickPoints > 0)
            MaryoGame.game.addKillPoints(mPickPoints, position.x, position.y + drawRect.height);
    }

    @Override
    public void popOutFromBox(float popTargetPositionY) {
        super.popOutFromBox(popTargetPositionY);
        visible = true;
        popFromBox = true;
        velocity.y = VELOCITY_POP;
        originalPosY = position.y;
    }

    @Override
    public float maxVelocity() {
        return VELOCITY;
    }

    protected abstract void performCollisionAction();
}
