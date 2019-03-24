package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.MusicManager;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.shader.Shader;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 29.3.15..
 * <p/>
 * This file is part of SMC-Android
 * Copyright Predrag Čokulov 2015
 */
public class Star extends Item {
    public static final float GLIM_COLOR_START_ALPHA = 0f;
    public static final float GLIM_COLOR_MAX_ALPHA = 0.95f;
    public static final float POSITION_Z = 0.053f;
    public static final float VELOCITY_X = 3f;
    public static final float VELOCITY_Y = 10f;
    public static final float DEF_SIZE = 0.65625f;

    public boolean moving;
    public float velY = -1;

    private Direction direction = Direction.right;

    private final Color glimColor = new Color(0.160784314f, 0.654901961f, 1f, GLIM_COLOR_START_ALPHA);
    private float glimCounter;
    private boolean glimMode = true;
    ParticleEffect trail;

    public Star(float x, float y, float z, float width, float height) {
        super(x, y, z, width, height);
        position.z = POSITION_Z;
        textureName = "game_items_star";
    }

    @Override
    public int getType() {
        return TYPE_STAR;
    }

    @Override
    public void initAssets() {
        texture = MaryoGame.game.assets.get(Assets.ATLAS_DYNAMIC, TextureAtlas.class)
                .findRegion(textureName);
        trail = new ParticleEffect(MaryoGame.game.assets.get("data/animation/particles/star_trail.p", ParticleEffect.class));
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (!visible) return;
        trail.setPosition(colRect.x + colRect.width * 0.5f, colRect.y + colRect.height * 0.5f);
        trail.draw(spriteBatch);
        spriteBatch.setShader(Shader.NORMAL_BLEND_SHADER);

        if (glimMode) {
            glimColor.a = glimCounter;
            if (glimCounter > GLIM_COLOR_MAX_ALPHA) {
                glimMode = false;
                glimCounter = GLIM_COLOR_MAX_ALPHA;
            }
        } else {
            glimColor.a = glimCounter;
            if (glimCounter < GLIM_COLOR_START_ALPHA) {
                glimMode = true;
                glimCounter = GLIM_COLOR_START_ALPHA;
            }
        }
        spriteBatch.setColor(glimColor);

        float width = Utility.getWidth(texture, drawRect.height);
        float originX = width * 0.5f;
        float originY = drawRect.height * 0.5f;
        spriteBatch.draw(texture, drawRect.x, drawRect.y, originX, originY, width, drawRect.height, 1, 1, rotationZ);

        spriteBatch.setShader(null);
        spriteBatch.setColor(Color.WHITE);
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
                velocity.x = direction == Direction.right ? VELOCITY_X : -VELOCITY_X;
            }
        } else if (moving) {
            trail.update(delta);
            // Setting initial vertical acceleration
            acceleration.y = Constants.GRAVITY;

            // Convert acceleration to frame time
            acceleration.scl(delta);

            // apply acceleration to change velocity
            velocity.add(acceleration);

            checkCollisionWithBlocks(delta, true, true, false, false);

            switch (direction) {
                case right:
                    velocity.x = VELOCITY_X;
                    break;
                case left:
                    velocity.x = -VELOCITY_X;
                    break;
            }
            if (velY != -1) {
                velocity.y = velY;
                velY = -1;
            }
        }
        if (glimMode) {
            glimCounter += (delta * 3f);
        } else {
            glimCounter -= (delta * 3f);
        }
        getRotation(delta);
    }

    private void getRotation(float delta) {
        float circumference = (float) Math.PI * (colRect.width);
        float deltaVelocity = VELOCITY_X * delta;
        float step = (circumference / deltaVelocity);
        float frameRotation = 360 / step;//degrees
        frameRotation *= 0.5f;

        if (velocity.y > 0.0f) {
            rotationZ += frameRotation;
        }
        // rotate back to 0 if falling
        else {
            frameRotation *= 0.9f;
            if (rotationZ > 5.0f && rotationZ <= 175.0f) {
                rotationZ -= frameRotation;
            } else if (rotationZ < 355 && rotationZ > 185) {
                rotationZ += frameRotation;
            }
        }
        if (rotationZ > 360) {
            rotationZ = rotationZ - 360;
        }
        if (rotationZ < -360) {
            rotationZ = 0 - rotationZ;
        }
    }

    @Override
    protected boolean handleCollision(GameObject object, boolean vertical) {
        if (object instanceof Sprite) {
            if (((Sprite) object).type == Sprite.Type.massive) {
                if (vertical) {
                    velY = colRect.y < object.colRect.y ? 0 : VELOCITY_Y;
                    return true;
                } else if (colRect.y > groundY) {
                    if (velocity.x < 0) {
                        direction = Direction.right;
                    } else {
                        direction = Direction.left;
                    }
                    return true;
                }
            } else if (((Sprite) object).type == Sprite.Type.halfmassive) {
                if (vertical && colRect.y + colRect.height > object.colRect.y + object.colRect.height) {
                    velY = VELOCITY_Y;
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public float maxVelocity() {
        return VELOCITY_X;
    }

    @Override
    public void hitPlayer() {
        if (isInBox) return;
        playerHit = true;
        if (!MaryoGame.game.assets.isLoaded(Assets.MUSIC_INVINCIBLE)) {
            MaryoGame.game.assets.load(Assets.MUSIC_INVINCIBLE, Music.class);
            MaryoGame.game.assets.finishLoading();
        }
        MusicManager.play(MaryoGame.game.assets.get(Assets.MUSIC_INVINCIBLE, Music.class), false);
        GameSave.addScore(1000);
        MaryoGame.game.addKillPoints(1000, position.x, position.y + drawRect.height);
        MaryoGame.game.currentScreen.world.maryo.starPicked();
        trashThisObject();
    }

    @Override
    public void popOutFromBox(float popTargetPositionY) {
        super.popOutFromBox(popTargetPositionY);
        visible = true;
        popFromBox = true;
        velocity.y = VELOCITY_Y;
        originalPosY = position.y;
    }

    @Override
    public void dispose() {
        super.dispose();
        trail.dispose();
        trail = null;
    }
}
