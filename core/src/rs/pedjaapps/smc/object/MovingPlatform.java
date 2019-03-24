package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.shader.Shader;

/**
 * Created by pedja on 19.9.15..
 */
public class MovingPlatform extends Sprite {
    private Vector3 mOriginPosition;
    public float max_distance;//max distance until it starts going back
    public float speed;//moving speed
    public float touch_time;// time when touched until it starts shaking (in ms)
    public float shake_time;// time it's shaking until falling
    public float touch_move_time;// time when touched until it starts moving
    public int move_type, middle_img_count, platformState = -1;
    public String direction, image_top_left, image_top_middle, image_top_right;
    private TextureRegion rLeft, rMiddle, rRight;
    private boolean forward = true, canFall;
    public boolean touched, canAttachTo = true;
    private float movingAngle, fallingRotation;
    private Vector2 origin = new Vector2();

    public static final int MOVING_PLATFORM_TYPE_LINE = 0;//left, right, up, down
    public static final int MOVING_PLATFORM_TYPE_CIRCLE = 1;//circle
    public static final int MOVING_PLATFORM_TYPE_PATH = 2;

    public static final int MOVING_PLATFORM_STAY = 0;
    public static final int MOVING_PLATFORM_TOUCHED = 1;
    public static final int MOVING_PLATFORM_SHAKE = 2;
    public static final int MOVING_PLATFORM_FALL = 3;
    public Path path;

    public MovingPlatform(float x, float y, float z, float width, float height, Rectangle colRect) {
        super(x, y, z, width, height, colRect);
        mOriginPosition = new Vector3(position);
    }

    @Override
    public void initAssets() {
        if (textureAtlas != null && !textureAtlas.trim().isEmpty()) {
            TextureAtlas atlas = MaryoGame.game.assets.get(textureAtlas);
            rLeft = atlas.findRegion(image_top_left);
            rMiddle = atlas.findRegion(image_top_middle);
            rRight = atlas.findRegion(image_top_right);
            if (rLeft == null)
                throw new GdxRuntimeException(image_top_left + " not found in atlas: " + textureAtlas);
            if (rMiddle == null)
                throw new GdxRuntimeException(image_top_middle + " not found in atlas: " + textureAtlas);
            if (rRight == null)
                throw new GdxRuntimeException(image_top_right + " not found in atlas: " + textureAtlas);
        } else {
            rLeft = new TextureRegion(MaryoGame.game.assets.get(image_top_left, Texture.class));
            rMiddle = new TextureRegion(MaryoGame.game.assets.get(image_top_middle, Texture.class));
            rRight = new TextureRegion(MaryoGame.game.assets.get(image_top_right, Texture.class));
        }
        if (platformState == -1) {
            speed /= 2.5f;
            if (touch_move_time > 0) {
                platformState = MOVING_PLATFORM_STAY;
            } else {
                platformState = MOVING_PLATFORM_TOUCHED;
            }
            if (type == Type.halfmassive)
                type = Type.massive;
            canFall = touch_time > 0;
        }
        origin.set(mOriginPosition.x, mOriginPosition.y + max_distance);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (platformState == MOVING_PLATFORM_SHAKE) {
            spriteBatch.setShader(Shader.SHAKE_SHADER);
            spriteBatch.getShader().setUniformf("u_distort", MathUtils.random(.05f), MathUtils.random(.05f), 0);
        }

        float singlePeaceWidth = drawRect.width / (2 + middle_img_count);

        float originX = singlePeaceWidth * 0.5f;
        float originY = drawRect.height * 0.5f;
        spriteBatch.draw(rLeft, drawRect.x, drawRect.y, originX, originY, singlePeaceWidth, drawRect.height, 1, 1, fallingRotation);

        for (int i = 0; i < middle_img_count; i++) {
            spriteBatch.draw(rMiddle, drawRect.x + singlePeaceWidth * (i + 1), drawRect.y, originX, originY, singlePeaceWidth, drawRect.height, 1, 1, fallingRotation);
        }

        spriteBatch.draw(rRight, drawRect.x + singlePeaceWidth * (middle_img_count + 1), drawRect.y, originX, originY, singlePeaceWidth, drawRect.height, 1, 1, fallingRotation);

        if (platformState == MOVING_PLATFORM_SHAKE)
            spriteBatch.setShader(null);
    }

    @Override
    public void update(float delta) {
        if (platformState == MOVING_PLATFORM_TOUCHED) {
            if (touch_move_time <= 0) {
                if (move_type == MOVING_PLATFORM_TYPE_LINE) {
                    if ("right".equals(direction))//right
                    {
                        float remainingDistance = (mOriginPosition.x + max_distance) - position.x;
                        if (forward) {
                            if (remainingDistance <= 0) {
                                forward = false;
                                velocity.x = 0;
                            } else {
                                velocity.x = speed;
                            }
                        } else {
                            if (remainingDistance >= max_distance) {
                                forward = true;
                                velocity.x = 0;
                            } else {
                                velocity.x = -speed;
                            }
                        }
                    } else if ("left".equals(direction))//left
                    {
                        float remainingDistance = position.x - (mOriginPosition.x - max_distance);
                        if (forward) {
                            if (remainingDistance <= 0) {
                                forward = false;
                                velocity.x = 0;
                            } else {
                                velocity.x = -speed;
                            }
                        } else {
                            if (remainingDistance >= max_distance) {
                                forward = true;
                                velocity.x = 0;
                            } else {
                                velocity.x = speed;
                            }
                        }
                    } else if ("up".equals(direction))//up
                    {
                        float remainingDistance = mOriginPosition.y + max_distance - position.y;
                        if (forward) {
                            if (remainingDistance <= 0) {
                                forward = false;
                                velocity.y = 0;
                            } else {
                                velocity.y = speed;
                            }
                        } else {
                            if (remainingDistance >= max_distance) {
                                forward = true;
                                velocity.y = 0;
                            } else {
                                velocity.y = -speed;
                            }
                        }
                    } else//down
                    {
                        float remainingDistance = max_distance - (mOriginPosition.y - position.y);
                        if (forward) {
                            if (remainingDistance <= 0) {
                                forward = false;
                                velocity.y = 0;
                            } else {
                                velocity.y = -speed;
                            }
                        } else {
                            if (remainingDistance >= max_distance) {
                                forward = true;
                                velocity.y = 0;
                            } else {
                                velocity.y = speed;
                            }
                        }
                    }
                } else if (move_type == MOVING_PLATFORM_TYPE_CIRCLE) {
                    if ("right".equals(direction)) {
                        movingAngle += speed * delta;

                        if (movingAngle > 360.0f) {
                            movingAngle -= 360.0f;
                        }
                    } else {
                        movingAngle -= speed * delta;

                        if (movingAngle < 0.0f) {
                            movingAngle += 360.0f;
                        }
                    }
                    velocity.x = position.x - (MathUtils.cosDeg(movingAngle) * (position.x - origin.x) - MathUtils.sinDeg(movingAngle) * (position.y - origin.y) + origin.x);
                    velocity.y = position.y - (MathUtils.sinDeg(movingAngle) * (position.x - origin.x) + MathUtils.cosDeg(movingAngle) * (position.y - origin.y) + origin.y);

                    position.add(velocity);
                    colRect.x = position.x;
                    colRect.y = position.y;
                    updateBounds();
                } else if (move_type == MOVING_PLATFORM_TYPE_PATH) {
                    if (path.currentSegmentIndex < path.segments.size()) {
                        Path.Segment segment = path.segments.get(path.currentSegmentIndex);
                        float targetX = path.posx + segment.end.x;
                        float targetY = path.posy + segment.end.y;
                        float distanceX = targetX - position.x;
                        float distanceY = targetY - position.y;
                        float directionX = segment.end.x - path.posx;
                        float directionY = segment.end.y - path.posy;

                        float distance = (float) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

                        float time = distance / speed;

                        //calculate velocity on x
                        velocity.x = distanceX / time;
                        //v = s/t;
                        //3 = 6/2

                        //calculate velocity on y
                        velocity.y = distanceY / time;

                        boolean x = false, y = false;
                        if (directionX > 0) {
                            if (position.x <= targetX) {
                                x = true;
                            }
                        } else {
                            if (position.x >= targetX) {
                                x = true;
                            }
                        }
                        if (directionY > 0) {
                            if (position.y >= targetY) {
                                y = true;
                            }
                        } else {
                            if (position.y <= targetY) {
                                y = true;
                            }
                        }
                        if (x && y) {
                            path.currentSegmentIndex++;
                        }
                    } else if (path.rewind == 1) {
                        path.currentSegmentIndex = 0;
                        Collections.reverse(path.segments);
                    } else {
                        velocity.set(0, 0, 0);
                    }
                }
            } else {
                touch_move_time -= delta;
            }
            if (canFall && touched) {
                touch_time -= delta;
                if (touch_time <= 0) {
                    platformState = MOVING_PLATFORM_SHAKE;
                }
            }
        } else if (platformState == MOVING_PLATFORM_SHAKE) {
            shake_time -= delta;
            if (shake_time <= 0) {
                platformState = MOVING_PLATFORM_FALL;
                type = Type.passive;
                canAttachTo = false;
            }
        } else if (platformState == MOVING_PLATFORM_FALL) {
            fallingRotation += 45 * delta;//20 deg per sec
            // Setting initial vertical acceleration
            acceleration.y = -30;

            // Convert acceleration to frame time
            acceleration.scl(delta);

            // apply acceleration to change velocity
            velocity.add(acceleration);
        } else //if(platformState == MOVING_PLATFORM_STAY)
        {
            //do nothing
            velocity.set(0, 0, 0);
        }
        if (move_type != MOVING_PLATFORM_TYPE_CIRCLE) {
            velocity.scl(delta);
            // update position
            position.add(velocity);
            colRect.x = position.x;
            colRect.y = position.y;
            updateBounds();

            // un-scale velocity (not in frame time)
            velocity.scl(1 / delta);
        }


        /*if (velocity.x < 0)//moving left
        {
            if (world.maryo.colRect.overlaps(colRect) && world.maryo.colRect.x + world.maryo.colRect.width > colRect.x)
            {
                world.maryo.colRect.x = world.maryo.position.x = colRect.x - world.maryo.colRect.width;
                world.maryo.updateBounds();
            }
        }
        else if (velocity.x > 0)//moving right
        {
            if (world.maryo.colRect.overlaps(colRect) && colRect.x + colRect.width > world.maryo.colRect.x)
            {
                world.maryo.colRect.x = world.maryo.position.x = colRect.x + colRect.width;
                world.maryo.updateBounds();
            }
        }
        if (velocity.y < 0)//down
        {
            if (world.maryo.colRect.overlaps(colRect) && world.maryo.colRect.y + world.maryo.colRect.height > colRect.y)
            {
                world.maryo.colRect.y = world.maryo.position.y = colRect.y - world.maryo.colRect.height;
                world.maryo.updateBounds();
            }
        }
        else **/
        if (velocity.y > 0)//up
        {
            if (MaryoGame.game.currentScreen.world.maryo.colRect.overlaps(colRect) && MaryoGame.game.currentScreen.world.maryo.colRect.y < colRect.x + colRect.height) {
                MaryoGame.game.currentScreen.world.maryo.colRect.y = MaryoGame.game.currentScreen.world.maryo.position.y = colRect.y + colRect.height;
                MaryoGame.game.currentScreen.world.maryo.updateBounds();
            }
        }

    }

    @Override
    public String toString() {
        return "MovingPlatform{" +
                "max_distance=" + max_distance +
                "\n speed=" + speed +
                "\n touch_time=" + touch_time +
                "\n shake_time=" + shake_time +
                "\n touch_move_time=" + touch_move_time +
                "\n move_type=" + move_type +
                "\n middle_img_count=" + middle_img_count +
                "\n direction='" + direction + '\'' +
                "\n image_top_left='" + image_top_left + '\'' +
                "\n image_top_middle='" + image_top_middle + '\'' +
                "\n image_top_right='" + image_top_right + '\'' +
                "\n rLeft=" + rLeft +
                "\n rMiddle=" + rMiddle +
                "\n rRight=" + rRight +
                "}\n\n" + super.toString();
    }

    public static class Path {
        public float posx, posy;
        public int rewind, currentSegmentIndex;
        public List<Segment> segments;

        {
            segments = new ArrayList<>();
        }

        public static class Segment {
            public Vector2 start, end;

            public Segment() {
                start = new Vector2();
                end = new Vector2();
            }
        }
    }
}
