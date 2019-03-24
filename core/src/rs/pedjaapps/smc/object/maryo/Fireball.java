package rs.pedjaapps.smc.object.maryo;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.DynamicObject;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.enemy.Enemy;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 19.8.15..
 */
public class Fireball extends DynamicObject {
    public static final float POSITION_Z = 0.095f;
    public static final float VELOCITY_X = 7f;
    public static final float VELOCITY_Y = 5f;
    public static final float MAX_DURATION = 2f;
    private float duration;
    public Direction direction = Direction.right;
    public float velY = -1;
    private ParticleEffect trail, explosion;
    private boolean destroyed;
    private Animation<TextureRegion> animation;

    public Fireball(float x, float y) {
        super(x, y, POSITION_Z, .3125f, .3125f);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        trail.setPosition(colRect.x + colRect.width * 0.5f, colRect.y + colRect.height * 0.5f);
        trail.draw(spriteBatch);
        if (!destroyed) {
            TextureRegion region = animation.getKeyFrame(stateTime, true);
            Utility.draw(spriteBatch, region, drawRect.x, drawRect.y, drawRect.height);
        } else {
            explosion.setPosition(colRect.x + (velocity.x > 0 ? colRect.width : 0), colRect.y + colRect.height * 0.5f);
            explosion.draw(spriteBatch);
        }
    }

    @Override
    public void update(float delta) {
        trail.update(delta);
        if (!destroyed) {
            duration += delta;

            velocity.x = direction == Direction.right ? VELOCITY_X : -VELOCITY_X;
            if (velY != -1) {
                velocity.y = velY;
                velY = -1;
            }

            // Setting initial vertical acceleration
            acceleration.y = Constants.GRAVITY;

            // Convert acceleration to frame time
            acceleration.scl(delta);

            // apply acceleration to change velocity
            velocity.add(acceleration);

            // checking collisions with the surrounding blocks depending on Bob's velocity
            checkCollisionWithBlocks(delta, true, true, false, false);

            // apply damping to halt Maryo nicely
            velocity.x *= velocityDump;

            // ensure terminal velocity is not exceeded
            //x
            if (velocity.x > maxVelocity())
                velocity.x = maxVelocity();
            if (velocity.x < -maxVelocity())
                velocity.x = -maxVelocity();

            if (duration > MAX_DURATION)
                destroy(false);
        } else {
            if (explosion.isComplete()) {
                trashThisObject();
                MaryoGame.FIREBALL_POOL.free(this);
            }
            explosion.update(delta);
        }

        stateTime += delta;
    }

    @Override
    public float maxVelocity() {
        return VELOCITY_X;
    }

    @Override
    protected boolean handleCollision(GameObject object, boolean vertical) {
        if (destroyed) return false;
        if (object instanceof Sprite) {
            if (((Sprite) object).type == Sprite.Type.massive) {
                if (vertical) {
                    velY = colRect.y < object.colRect.y ? 0 : VELOCITY_Y;
                } else {
                    if (colRect.y > groundY) destroy(true);
                }
                return true;
            } else if (((Sprite) object).type == Sprite.Type.halfmassive) {
                if (vertical && colRect.y + colRect.height > object.colRect.y + object.colRect.height) {
                    velY = VELOCITY_Y;
                    return true;
                }
            }
        } else if (object instanceof Enemy) {

            if (((Enemy) object).mFireResistant != 1)
                ((Enemy) object).downgradeOrDie(this, false, false);
            else {
                Sound sound = MaryoGame.game.assets.get(Assets.SOUND_ITEM_FIREBALL_REPELLED);
                SoundManager.play(sound);
                //repelled sound
            }
            destroy(true);
        }
        return false;
    }

    @Override
    protected boolean handleDroppedBelowWorld() {
        destroy(false);
        return true;
    }

    @Override
    public void initAssets() {
        if (animation == null) {
            TextureAtlas atlas = MaryoGame.game.assets.get(Assets.ATLAS_DYNAMIC, TextureAtlas.class);
            Array<TextureAtlas.AtlasRegion> regions = new Array<>(3);
            regions.add(atlas.findRegion("animation_fireball_1"));
            regions.add(atlas.findRegion("animation_fireball_2"));
            regions.add(atlas.findRegion("animation_fireball_3"));

            animation = new Animation<TextureRegion>(0.05f, regions);
        }
        trail = new ParticleEffect(MaryoGame.game.assets.get("data/animation/particles/fireball_emitter_2.p", ParticleEffect.class));
        explosion = new ParticleEffect(MaryoGame.game.assets.get("data/animation/particles/fireball_explosion_emitter.p", ParticleEffect.class));
    }

    @Override
    public void dispose() {
        animation = null;
        trail.dispose();
        trail = null;
        explosion.dispose();
        explosion = null;
    }

    @Override
    public boolean isBullet() {
        return true;
    }

    public void destroy(boolean playSound) {
        destroyed = true;
        trail.allowCompletion();
        explosion.reset();
        explosion.getEmitters().get(0).getAngle().setHighMin(velocity.x > 0 ? 270 : -90);
        explosion.getEmitters().get(0).getAngle().setHighMax(velocity.x > 0 ? 90 : 90);
        if (playSound) {
            Sound sound = MaryoGame.game.assets.get(Assets.SOUND_ITEM_FIREBALL_EXPLOSION);
            // je weiter entfernt, desto leiser. 9 etwa ein Bildschirm, also nach 18 nur noch 1/3
            float distance = Math.abs(position.x - MaryoGame.game.currentScreen.world.maryo.position.x);
            float volume = .3f + .7f * Math.max(0, (18 - distance) / 18);
            SoundManager.play(sound, volume);
        }
    }

    public void reset() {
        velocity.set(0, 0, 0);
        duration = 0;
        destroyed = false;
        trail.reset();
        trail.setPosition(colRect.x, colRect.y + colRect.height * 0.5f);
        explosion.reset();
    }
}
