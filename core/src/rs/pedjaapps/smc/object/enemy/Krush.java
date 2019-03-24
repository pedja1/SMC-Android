package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Krush extends Enemy {
    private static final float VELOCITY_SMALL = 2.75f;
    private static final float VELOCITY_BIG = 1.5f;
    private static final int KP_SMALL = 20;
    private static final int KP_BIG = 40;
    private static final float POS_Z = 0.09f;

    private boolean dying = false;

    private boolean isSmall;

    private Animation<TextureRegion> aBig, aSmall;
    private TextureRegion tDead;

    public Krush(float x, float y, float z, float width, float height) {
        super(x, y, z, width, height);
        setupBoundingBox();
        position.z = POS_Z;
    }

    @Override
    public void initAssets() {
        TextureAtlas atlas = MaryoGame.game.assets.get(Assets.ATLAS_DYNAMIC);

        Array<TextureRegion> smallFrames = new Array<>();
        Array<TextureRegion> bigFrames = new Array<>();

        for (int i = 1; i <= 4; i++)
            bigFrames.add(atlas.findRegion("enemy_krush_big_" + String.valueOf(i)));

        for (int i = 1; i <= 4; i++)
            smallFrames.add(atlas.findRegion("enemy_krush_small_" + String.valueOf(i)));

        aSmall = new Animation(0.07f, smallFrames);
        aBig = new Animation(0.12f, bigFrames);

        tDead = atlas.findRegion("enemy_krush_small_1");
    }

    @Override
    public void dispose() {
        aBig = null;
        aSmall = null;
        tDead = null;
    }

    @Override
    public void _render(SpriteBatch spriteBatch) {
        TextureRegion frame;
        if (!dying) {
            frame = isSmall ? aSmall.getKeyFrame(stateTime, true) : aBig.getKeyFrame(stateTime, true);
            frame.flip(direction == Direction.left, false);
            Utility.draw(spriteBatch, frame, drawRect.x, drawRect.y, drawRect.height);
            frame.flip(direction == Direction.left, false);
        } else {
            frame = aSmall.getKeyFrame(0);
            frame.flip(direction == Direction.left, false);
            spriteBatch.draw(frame, drawRect.x, drawRect.y, drawRect.width, drawRect.height);
            frame.flip(direction == Direction.left, false);
        }
    }

    @Override
    public boolean canBeKilledByJumpingOnTop() {
        return true;
    }

    @Override
    public void _update(float deltaTime) {
        stateTime += deltaTime;
        if (dying) {
            //resize it by state time
            drawRect.height -= 1.26f * deltaTime;
            drawRect.width -= 0.63f * deltaTime;
            if (drawRect.height < 0) {
                MaryoGame.game.trashObject(this);
            }
            return;
        }

        // Setting initial vertical acceleration
        acceleration.y = Constants.GRAVITY;

        // Convert acceleration to frame time
        acceleration.scl(deltaTime);

        // apply acceleration to change velocity
        velocity.add(acceleration);

        checkCollisionWithBlocks(deltaTime, !deadByBullet, !deadByBullet);

        if (!deadByBullet) {
            switch (direction) {
                case right:
                    velocity.set(velocity.x = -(isSmall ? VELOCITY_SMALL : VELOCITY_BIG), velocity.y, velocity.z);
                    break;
                case left:
                    velocity.set(velocity.x = (isSmall ? VELOCITY_SMALL : VELOCITY_BIG), velocity.y, velocity.z);
                    break;
            }
        }
    }

    @Override
    protected boolean handleCollision(GameObject object, boolean vertical) {
        super.handleCollision(object, vertical);
        if (!vertical) {
            if (((object instanceof Sprite && ((Sprite) object).type == Sprite.Type.massive
                    && object.colRect.y + object.colRect.height > colRect.y + 0.1f)
                    || object instanceof EnemyStopper
                    || (object instanceof Enemy && this != object && !(object instanceof Flyon)))) {
                //CollisionManager.resolve_objects(this, object, true);
                handleCollision(ContactType.stopper);
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
        }
    }

    @Override
    public void turn() {
        if (turned) return;
        direction = direction == Direction.right ? Direction.left : Direction.right;
        velocity.x = velocity.x > 0 ? -velocity.x : Math.abs(velocity.x);
        turned = true;
    }

    private void setupBoundingBox() {
        colRect.height = colRect.height - 0.2f;
    }

    @Override
    public void updateBounds() {
        drawRect.height = colRect.height + 0.2f;
        super.updateBounds();
    }

    @Override
    public int hitByPlayer(Maryo maryo, boolean vertical) {
        if (maryo.velocity.y < 0 && vertical && maryo.colRect.y > colRect.y)//enemy death from above
        {
            if (isSmall) {
                //TODO should get points even if big
                MaryoGame.game.addKillPoints(isSmall ? KP_SMALL : KP_BIG, position.x, position.y + drawRect.height);
                stateTime = 0;
                handleCollision = false;
                dying = true;
                playDeadSound(maryo.mInvincibleStar);
                return HIT_RESOLUTION_ENEMY_DIED;
            } else {
                isSmall = true;
                return HIT_RESOLUTION_CUSTOM;
            }
        } else {
            return HIT_RESOLUTION_PLAYER_DIED;
        }
    }

    @Override
    protected TextureRegion getDeadTextureRegion() {
        return tDead;
    }

    @Override
    public void downgradeOrDie(GameObject killedBy, boolean forceBulletKill, boolean isStarKill) {
        mKillPoints = isSmall ? KP_SMALL : KP_BIG;
        super.downgradeOrDie(killedBy, forceBulletKill, isStarKill);
    }

    @Override
    protected String getDeadSound() {
        return Assets.SOUND_ENEMY_DIE_KRUSH;
    }
}
