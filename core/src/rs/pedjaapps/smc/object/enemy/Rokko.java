package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Rokko extends Enemy {
    public static final float POSITION_Z = 0.03f;
    public String direction;
    private float speed;
    private boolean staying = true, dying;
    private float rotation;
    private boolean flipX;
    private float mMinDistanceFront, mMaxDistanceFront, mMaxDistanceSides;
    private ParticleEffect effect;
    private TextureRegion texture;
    private Rectangle tmpRect = new Rectangle();

    public Rokko(float x, float y, float z, float width, float height, String direction) {
        super(x, y, z, width, height);
        this.speed = 3.5f;//speed;
        this.direction = direction;
        if ("left".equals(direction)) {
            flipX = true;
        } else if ("up".equals(direction)) {
            rotation = 90f;
        } else if ("down".equals(direction)) {
            rotation = 270f;
        }
        position.z = POSITION_Z;
        mKillPoints = 250;
        mMinDistanceFront = 3.125f;
        mMaxDistanceFront = 15.625f;
        mMaxDistanceSides = 6.25f;
        mFireResistant = 1;
        mIceResistance = 1;
        MaryoGame.game.assets.load("data/animation/particles/rokko_trail_emitter.p", ParticleEffect.class, Assets.PARTICLE_EFFECT_PARAMETER);
        ppEnabled = false;
    }

    @Override
    protected TextureRegion getDeadTextureRegion() {
        return texture;
    }

    @Override
    public void initAssets() {
        texture = MaryoGame.game.assets.get(Assets.ATLAS_DYNAMIC, TextureAtlas.class)
                .findRegion("enemy_rokko_r");

        if (flipX) {
            texture = new TextureRegion(texture);
            texture.flip(flipX, false);
        }

        effect = new ParticleEffect(MaryoGame.game.assets.get("data/animation/particles/rokko_trail_emitter.p", ParticleEffect.class));
        effect.start();
    }

    @Override
    public void dispose() {
        effect.dispose();
        effect = null;
        texture = null;
    }

    @Override
    public void _render(SpriteBatch spriteBatch) {
        float width = Utility.getWidth(texture, drawRect.height);
        float originX = width * 0.5f;
        float originY = drawRect.height * 0.5f;
        spriteBatch.draw(texture, drawRect.x, drawRect.y, originX, originY, width, drawRect.height, 1, 1, rotation);

        if (!staying) {
            if ("up".equals(direction) || "right".equals(direction)) {
                effect.setPosition(position.x, position.y);
            } else if ("down".equals(direction)) {
                effect.setPosition(position.x, position.y + drawRect.height);
            } else if ("left".equals(direction)) {
                effect.setPosition(position.x + drawRect.width, position.y);
            }
            effect.draw(spriteBatch/*, Gdx.graphics.getDeltaTime()*/);
        }
    }

    @Override
    public boolean canBeKilledByJumpingOnTop() {
        return true;
    }

    public void _update(float deltaTime) {
        if (deadByBullet || dying) {
            if (velocity.x != 0) {
                if ("right".equals(direction)) {
                    rotation -= (20 * deltaTime);
                } else if ("left".equals(direction)) {
                    rotation += (20 * deltaTime);
                }
            }
            // Setting initial vertical acceleration
            acceleration.y = Constants.GRAVITY;

            // Convert acceleration to frame time
            acceleration.scl(deltaTime);

            // apply acceleration to change velocity
            velocity.add(acceleration);

            checkCollisionWithBlocks(deltaTime, false, false);
            effect.update(deltaTime);
            return;
        }

        if (!staying)
            effect.update(deltaTime);

        stateTime += deltaTime;
        if (staying) {
            if (checkMaryoInFront()) {
                staying = false;
            }
        } else if ("up".equals(direction)) {
            velocity.y = speed;
        } else if ("down".equals(direction)) {
            velocity.y = -speed;
        } else if ("right".equals(direction)) {
            velocity.x = speed;
        } else if ("left".equals(direction)) {
            velocity.x = -speed;
        }

        updatePosition(deltaTime);
    }

    private boolean checkMaryoInFront() {
        Maryo maryo = MaryoGame.game.currentScreen.world.maryo;
        if (maryo == null) return false;
        float x = 0, y = 0, w = 0, h = 0;
        if ("up".equals(direction)) {
            x = colRect.x - (mMaxDistanceSides - colRect.width) * 0.5f;
            y = colRect.y + colRect.height + mMinDistanceFront;
            w = mMaxDistanceSides;
            h = mMaxDistanceFront;
        } else if ("down".equals(direction)) {
            x = colRect.x - (mMaxDistanceSides - colRect.width) * 0.5f;
            y = colRect.y - mMaxDistanceFront - mMaxDistanceFront;
            w = mMaxDistanceSides;
            h = mMaxDistanceFront;
        } else if ("left".equals(direction)) {
            x = colRect.x - mMinDistanceFront - mMaxDistanceFront;
            y = colRect.y - (mMaxDistanceSides - colRect.height) * 0.5f;
            w = mMaxDistanceFront;
            h = mMaxDistanceSides;
        } else if ("right".equals(direction)) {
            x = colRect.x + colRect.width + mMaxDistanceSides;
            y = colRect.y - (mMaxDistanceSides - colRect.height) * 0.5f;
            w = mMaxDistanceFront;
            h = mMaxDistanceSides;
        }
        tmpRect.set(x, y, w, h);
        return maryo.colRect.overlaps(tmpRect);
    }

    @Override
    public int hitByPlayer(Maryo maryo, boolean vertical) {
        if (maryo.velocity.y < 0 && vertical && maryo.colRect.y > colRect.y)//enemy death from above
        {
            if ("up".equals(direction)) {
                rotation = 90f;
            }
            MaryoGame.game.addKillPoints(mKillPoints, position.x, position.y + drawRect.height);
            stateTime = 0;
            handleCollision = false;
            dying = true;
            playDeadSound(maryo.mInvincibleStar);
            return HIT_RESOLUTION_ENEMY_DIED;
        } else {
            return HIT_RESOLUTION_PLAYER_DIED;
        }
    }

    @Override
    protected String getDeadSound() {
        return Assets.SOUND_ENEMY_ROKKO_HIT;
    }
}
