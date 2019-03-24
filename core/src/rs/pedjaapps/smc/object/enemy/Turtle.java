package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.Box;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 * Copyright pedja
 */
public class Turtle extends Enemy {
    private static final float SHELL_TIMEOUT_SEC = 5;
    private final float mVelocity;
    private static final float VELOCITY_TURN = 0.75f;
    private final float mVelocityShell;
    static final float POS_Z = 0.091f;

    private float mShellRotation, mShelledTime;

    public boolean isShell = false, isShellMoving = false;
    private Animation<TextureRegion> walkAnimation;
    private TextureRegion tTurn, tShell, tDead;

    public Turtle(float x, float y, float z, float width, float height, String color) {
        super(x, y, POS_Z, width, height);
        if (!"green".equals(color)) {
            mKillPoints = 50;
            mVelocity = 2f;
            mVelocityShell = 5.8f;
        } else {
            mKillPoints = 150;
            mVelocity = 2.5f;
            mVelocityShell = 7.1f;
        }
        setupBoundingBox();
        MaryoGame.game.assets.load(Assets.SOUND_ENEMY_TURTLE_SHELL_HIT, Sound.class);
    }

    @Override
    public void initAssets() {
        TextureAtlas atlas = MaryoGame.game.assets.get(Assets.ATLAS_DYNAMIC);
        Array<TextureRegion> walkFrames = new Array<>();

        for (int i = 1; i <= 9; i++) {
            TextureRegion region = atlas.findRegion("enemy_turtle_walk_" + String.valueOf(i));
            walkFrames.add(region);
        }

        walkAnimation = new Animation<>(0.07f, walkFrames);
        tTurn = atlas.findRegion("enemy_turtle_turn");
        tShell = atlas.findRegion("enemy_turtle_shell");
        tDead = atlas.findRegion("enemy_turtle_roll");
    }

    @Override
    public void dispose() {
        walkAnimation = null;
        tDead = null;
        tTurn = null;
        tShell = null;
    }

    @Override
    public boolean isBullet() {
        return true;
    }

    @Override
    public void _render(SpriteBatch spriteBatch) {
        TextureRegion frame;
        if (!isShell && turn) {
            frame = tTurn;
        } else {
            if (isShell) {
                frame = tShell;
            } else {
                frame = walkAnimation.getKeyFrame(stateTime, true);
            }
        }
        if (frame != null) {
            float width = Utility.getWidth(frame, drawRect.height);
            float originX = width * 0.5f;
            float originY = drawRect.height * 0.5f;
            frame.flip(!isShell && !turn && direction == Direction.right, false);
            spriteBatch.draw(frame, drawRect.x, drawRect.y, originX, originY, width, drawRect.height, 1, 1, mShellRotation);
            frame.flip(!isShell && !turn && direction == Direction.right, false);
        }
    }

    @Override
    public boolean canBeKilledByJumpingOnTop() {
        return true;
    }

    private float getRotation(float delta) {
        if (isShellMoving) {
            float circumference = (float) Math.PI * (colRect.width);
            float deltaVelocity = mVelocityShell * delta;

            float step = circumference / deltaVelocity;


            float frameRotation = 360 / step;//degrees
            mShellRotation += frameRotation;
            if (mShellRotation > 360) mShellRotation = mShellRotation - 360;
        } else {
            mShellRotation = 0;
        }
        return direction == Direction.left ? mShellRotation : -mShellRotation;
    }

    public void _update(float deltaTime) {
        stateTime += deltaTime;

        // Setting initial vertical acceleration
        acceleration.y = Constants.GRAVITY;

        // Convert acceleration to frame time
        acceleration.scl(deltaTime);

        // apply acceleration to change velocity
        velocity.add(acceleration);

        checkCollisionWithBlocks(deltaTime, !deadByBullet, !deadByBullet);

        if (stateTime - turnStartTime > 0.15f) {
            turnStartTime = 0;
            turn = false;
        }

        /*if (isShell && !isShellMoving)
        {
            mShelledTime -= deltaTime;
            if(mShelledTime <= 0)
            {
                isShell = false;
                isShellMoving = false;

                drawRect.height = drawRect.height / 0.60f;
                drawRect.width = drawRect.width / 0.60f;
                colRect.height = drawRect.height;
                colRect.width = drawRect.width;
            }
        }*/

        if (!deadByBullet) {
            switch (direction) {
                case right:
                    velocity.set(velocity.x = +getVelocityX(), velocity.y, velocity.z);
                    break;
                case left:
                    velocity.set(velocity.x = -getVelocityX(), velocity.y, velocity.z);
                    break;
            }
        }
        turned = false;
        mShellRotation = getRotation(deltaTime);
    }

    private float getVelocityX() {
        if (isShell) {
            if (isShellMoving) {
                return mVelocityShell;
            } else {
                return 0;
            }
        } else {
            if (turn) {
                return VELOCITY_TURN;
            } else {
                return mVelocity;
            }
        }
    }

    @Override
    protected boolean handleCollision(GameObject object, boolean vertical) {

        super.handleCollision(object, vertical);
        if (!vertical) {
            if (((object instanceof Sprite && ((Sprite) object).type == Sprite.Type.massive
                    && object.colRect.y + object.colRect.height > colRect.y + 0.1f)
                    || (object instanceof EnemyStopper && !isShellMoving))
                    && !turned) {
                //CollisionManager.resolve_objects(this, object, true);
                handleCollision(ContactType.stopper);
            } else if (object instanceof Enemy && object != this && isShell && isShellMoving && ((Enemy) object).handleCollision) {
                ((Enemy) object).downgradeOrDie(this, false, false);
            } else if (object instanceof Enemy && object != this && !isShell && !(object instanceof Flyon)) {
                turn();
            }

            if (object instanceof Box && isShell && isShellMoving) {
                ((Box) object).activate();
            }
        }
        return false;
    }

    @Override
    public void handleCollision(ContactType contactType) {
        switch (contactType) {
            case stopper:
                turn();
                break;
            case player:

                break;
        }
    }

    private void setupBoundingBox() {
        if (!isShell) colRect.height = colRect.height - 0.2f;
    }

    @Override
    public void updateBounds() {
        if (!isShell) {
            drawRect.height = colRect.height + 0.2f;
            super.updateBounds();
        } else {
            drawRect.x = (colRect.x) - ((drawRect.width - colRect.width) - colRect.width / 2);
            drawRect.y = (colRect.y) - ((drawRect.height - colRect.height) - colRect.height / 2);
        }
    }

    @Override
    public int hitByPlayer(Maryo maryo, boolean vertical) {
        if (isShell && !isShellMoving) {
            isShellMoving = true;
            if (colRect.x > maryo.colRect.x) {
                setDirection(Direction.right);
                colRect.x = position.x = maryo.colRect.x + maryo.colRect.width + 0.1f;
            } else {
                setDirection(Direction.left);
                colRect.x = position.x = maryo.colRect.x - maryo.colRect.width - 0.1f;
            }
            updateBounds();
            String soundFile = Assets.SOUND_ENEMY_TURTLE_SHELL_HIT;
            if (MaryoGame.game.assets.isLoaded(soundFile))
                SoundManager.play(MaryoGame.game.assets.get(soundFile, Sound.class));
            return HIT_RESOLUTION_CUSTOM;
        } else if (maryo.velocity.y < 0 && vertical && maryo.colRect.y > colRect.y)//enemy death from above
        {
            //transform to shell if not shell
            //if shell make it move
            // if shell and moving make it stop
            if (!isShell) {
                mShelledTime = SHELL_TIMEOUT_SEC;
                isShell = true;
                velocity.x = 0;
                drawRect.height = drawRect.height * 0.60f;
                drawRect.width = drawRect.width * 0.60f;
                colRect.height = drawRect.height / 2;
                colRect.width = drawRect.width / 2;
                return HIT_RESOLUTION_ENEMY_DIED;
            } else {
                isShellMoving = !isShellMoving;
            }
            return HIT_RESOLUTION_CUSTOM;
        } else {
            // wenn sich die Schildkröte als Panzer bewegt, dann nur töten wenn auf Maryo draufgelaufen wird
            if (isShell) {
                float maryoMiddle = maryo.colRect.x + maryo.colRect.width / 2;
                float shellMiddle = colRect.x + colRect.width / 2;

                if (maryoMiddle > shellMiddle && direction == Direction.right
                        || maryoMiddle < shellMiddle && direction == Direction.left)
                    return HIT_RESOLUTION_PLAYER_DIED;
                else
                    return HIT_RESOLUTION_CUSTOM;

            } else
                return HIT_RESOLUTION_PLAYER_DIED;
        }
    }

    @Override
    protected TextureRegion getDeadTextureRegion() {
        return tDead;
    }

    @Override
    protected String getDeadSound() {
        return Assets.SOUND_STOMP4;
    }
}
