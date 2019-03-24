package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
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
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Gee extends Enemy {
    public static final float POSITION_Z = 0.088f;
    public String direction;
    public float flyDistance, flySpeed;
    private Vector3 mOriginPosition;
    private boolean forward, dirForward, staying = true;
    private float currWaitTime, waitTime;
    private Color color;
    private String colorString;
    private boolean flipx;
    private ParticleEffect effect, deadEffect;
    private boolean canStartParticle;
    private boolean dying;
    private Animation<TextureRegion> animation;
    private TextureRegion deadRegion;

    public Gee(float x, float y, float z, float width, float height, float flyDistance, String color, String direction, float waitTime) {
        super(x, y, z, width, height);
        this.direction = direction;
        colorString = color;
        mOriginPosition = new Vector3(position);
        this.flyDistance = flyDistance;
        this.waitTime = waitTime;
        switch (color) {
            case "yellow":
                this.color = new Color(1, 0.960784314f, 0.039215686f, 1);
                flySpeed = 2.881f;
                mKillPoints = 50;
                mFireResistant = 0;
                break;
            case "red":
                this.color = new Color(1, 0.156862745f, 0.078431373f, 1);
                flySpeed = 3.865f;
                mKillPoints = 100;
                mFireResistant = 1;
                break;
            case "green":
                this.color = new Color(0.078431373f, 0.992156863f, 0.078431373f, 1);
                flySpeed = 4.831f;
                mKillPoints = 200;
                mFireResistant = 0;
                break;
        }
        position.z = POSITION_Z;
        if ("vertical".equals(direction)) {
            MaryoGame.game.assets.load("data/animation/particles/gee_vertical_emitter.p", ParticleEffect.class, Assets.PARTICLE_EFFECT_PARAMETER);
        } else {
            MaryoGame.game.assets.load("data/animation/particles/gee_horizontal_emitter.p", ParticleEffect.class, Assets.PARTICLE_EFFECT_PARAMETER);
        }
        MaryoGame.game.assets.load("data/animation/particles/gee_dead_emitter.p", ParticleEffect.class, Assets.PARTICLE_EFFECT_PARAMETER);
        MaryoGame.game.assets.load(Assets.SOUND_ENEMY_DIE_GEE, Sound.class);
        ppEnabled = false;
    }

    @Override
    protected TextureRegion getDeadTextureRegion() {
        return deadRegion;
    }

    @Override
    public void initAssets() {
        TextureAtlas atlas = MaryoGame.game.assets.get(Assets.ATLAS_DYNAMIC);
        Array<TextureAtlas.AtlasRegion> frames = new Array<>(10);
        for (int i = 1; i <= 10; i++)
            frames.add(atlas.findRegion("enemy_gee_" + colorString + "_" + String.valueOf(i)));

        deadRegion = frames.get(4);
        animation = new Animation<TextureRegion>(0.14f, frames);
        if ("vertical".equals(direction)) {
            effect = new ParticleEffect(MaryoGame.game.assets.get("data/animation/particles/gee_vertical_emitter.p", ParticleEffect.class));
        } else {
            effect = new ParticleEffect(MaryoGame.game.assets.get("data/animation/particles/gee_horizontal_emitter.p", ParticleEffect.class));
        }
        deadEffect = new ParticleEffect(MaryoGame.game.assets.get("data/animation/particles/gee_dead_emitter.p", ParticleEffect.class));
        for (ParticleEmitter pe : effect.getEmitters()) {
            pe.getTint().setColors(new float[]{color.r, color.g, color.b});
        }
        for (ParticleEmitter pe : deadEffect.getEmitters()) {
            pe.getTint().setColors(new float[]{color.r, color.g, color.b});
        }
    }

    @Override
    public void dispose() {
        animation = null;
        effect.dispose();
        deadEffect.dispose();
        effect = null;
        deadEffect = null;
    }

    @Override
    public void _render(SpriteBatch spriteBatch) {
        if (!dying) {
            effect.setPosition(position.x + drawRect.width / 2, position.y + drawRect.height / 2);
            if (canStartParticle) effect.draw(spriteBatch/*, Gdx.graphics.getDeltaTime()*/);

            TextureRegion frame = animation.getKeyFrame(stateTime, true);
            float width = Utility.getWidth(frame, drawRect.height);
            frame.flip(flipx, false);//flip
            spriteBatch.draw(frame, drawRect.x, drawRect.y, width, drawRect.height);
            frame.flip(flipx, false);//return
        } else {
            deadEffect.setPosition(position.x + drawRect.width / 2, position.y + drawRect.height / 2);
            deadEffect.draw(spriteBatch/*, Gdx.graphics.getDeltaTime()*/);
        }
    }

    @Override
    public boolean canBeKilledByJumpingOnTop() {
        return true;
    }

    public void _update(float deltaTime) {
        if (deadByBullet) {
            // Setting initial vertical acceleration
            acceleration.y = Constants.GRAVITY;

            // Convert acceleration to frame time
            acceleration.scl(deltaTime);

            // apply acceleration to change velocity
            velocity.add(acceleration);

            checkCollisionWithBlocks(deltaTime, false, false);
            return;
        }


        stateTime += deltaTime;

        if (dying) {
            deadEffect.update(deltaTime);
            if (effect.isComplete()) {
                MaryoGame.game.trashObject(this);
                MaryoGame.game.addKillPoints(mKillPoints, position.x, position.y + drawRect.height);
            }
            return;
        }

        effect.update(deltaTime);

        if (staying) {
            currWaitTime += deltaTime;
            if (currWaitTime >= waitTime) {
                effect.start();
                canStartParticle = true;
                staying = false;
                currWaitTime = 0;
                if (forward) {
                    forward = false;
                } else {
                    forward = true;
                    dirForward = MathUtils.randomBoolean();
                }
            }
        } else if ("horizontal".equals(direction)) {
            if (dirForward)//right
            {
                float remainingDistance = (mOriginPosition.x + flyDistance) - position.x;
                if (forward) {
                    flipx = true;
                    if (remainingDistance <= 0) {
                        staying = true;
                        effect.allowCompletion();
                        velocity.x = 0;
                    } else {
                        velocity.x = flySpeed;
                    }

                } else {
                    flipx = false;
                    if (remainingDistance >= flyDistance) {
                        staying = true;
                        effect.allowCompletion();
                        velocity.x = 0;
                    } else {
                        velocity.x = -flySpeed;
                    }
                }
            } else//left
            {
                float remainingDistance = position.x - (mOriginPosition.x - flyDistance);
                if (forward) {
                    flipx = false;
                    if (remainingDistance <= 0) {
                        staying = true;
                        effect.allowCompletion();
                        velocity.x = 0;
                    } else {
                        velocity.x = -flySpeed;
                    }
                } else {
                    flipx = true;
                    if (remainingDistance >= flyDistance) {
                        staying = true;
                        effect.allowCompletion();
                        velocity.x = 0;
                    } else {
                        velocity.x = flySpeed;
                    }
                }
            }
        } else if ("vertical".equals(direction)) {
            if (dirForward)//up
            {
                float remainingDistance = mOriginPosition.y + flyDistance - position.y;
                if (forward) {
                    flipx = true;
                    if (remainingDistance <= 0) {
                        staying = true;
                        effect.allowCompletion();
                        velocity.y = 0;
                    } else {
                        velocity.y = flySpeed;
                    }
                } else {
                    flipx = false;
                    if (remainingDistance >= flyDistance) {
                        staying = true;
                        effect.allowCompletion();
                        velocity.y = 0;
                    } else {
                        velocity.y = -flySpeed;
                    }
                }
            } else//down
            {
                float remainingDistance = flyDistance - (mOriginPosition.y - position.y);
                if (forward) {
                    flipx = false;
                    if (remainingDistance <= 0) {
                        staying = true;
                        effect.allowCompletion();
                        velocity.y = 0;
                    } else {
                        velocity.y = -flySpeed;
                    }
                } else {
                    flipx = true;
                    if (remainingDistance >= flyDistance) {
                        staying = true;
                        effect.allowCompletion();
                        velocity.y = 0;
                    } else {
                        velocity.y = flySpeed;
                    }
                }
            }
        }

        updatePosition(deltaTime);
    }

    @Override
    public int hitByPlayer(Maryo maryo, boolean vertical) {
        if (maryo.velocity.y < velocity.y && vertical && maryo.colRect.y >= colRect.y)//enemy death from above
        {
            MaryoGame.game.addKillPoints(mKillPoints, position.x, position.y + drawRect.height);
            dying = true;
            deadEffect.start();
            stateTime = 0;
            handleCollision = false;
            Sound sound = MaryoGame.game.assets.get(Assets.SOUND_ENEMY_DIE_GEE);
            SoundManager.play(sound);
            return HIT_RESOLUTION_ENEMY_DIED;
        } else {
            return HIT_RESOLUTION_PLAYER_DIED;
        }
    }

    @Override
    protected String getDeadSound() {
        return Assets.SOUND_ENEMY_DIE_GEE;
    }
}
