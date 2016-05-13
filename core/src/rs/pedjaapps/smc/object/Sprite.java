package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

import rs.pedjaapps.smc.utility.Utility;

public class Sprite extends GameObject
{
    public static final int GROUND_NORMAL = 0;
    public static final int GROUND_EARTH = 1;
    public static final int GROUND_ICE = 2;
    public static final int GROUND_SAND = 3;
    public static final int GROUND_STONE = 4;
    public static final int GROUND_PLASTIC = 5;

    private boolean rotationAplied = false;
    public String textureAtlas;
    public String textureName;//name of texture from pack or png
    public Type type = null;
    private Rectangle mOrigDrawRect;
    private Texture txt = null;
    private TextureRegion region = null;
    public int groundType = GROUND_NORMAL;

    public Sprite(World world, Vector2 size, Vector3 position, Rectangle colRect)
    {
        super(world, size, position);
        this.position = position;
        mOrigDrawRect = new Rectangle(mDrawRect);
        if (colRect != null)
        {
            mColRect.x = mDrawRect.x + Math.abs(colRect.x);
            mColRect.y = mDrawRect.y + Math.abs(colRect.y);
            mColRect.width = colRect.width;
            mColRect.height = colRect.height;
        }
    }

    @Override
    public void _render(SpriteBatch spriteBatch)
    {
        float width = txt == null ? Utility.getWidth(region, mOrigDrawRect.height) : Utility.getWidth(txt, mOrigDrawRect.height);
        float originX = width * 0.5f;
        float originY = getOriginY();
        float rotation = mRotationZ;
        boolean flipX = mRotationY == 180;
        boolean flipY = mRotationX == 180;

        if (txt != null)
        {
            spriteBatch.draw(txt, mOrigDrawRect.x, mOrigDrawRect.y, originX, originY, width, mOrigDrawRect.height, 1, 1, rotation, 0, 0, txt.getWidth(), txt.getHeight(), flipX, flipY);
        }
        else
        {
            region.flip(flipX, flipY);//flip it
            spriteBatch.draw(region, mOrigDrawRect.x, mOrigDrawRect.y, originX, originY, width, mOrigDrawRect.height, 1, 1, rotation);
            region.flip(flipX, flipY);//return it to original
        }
    }

    @Override
    public void _update(float delta)
    {

    }

    @Override
    public void initAssets()
    {
        if (mRotationZ == 90 && mRotationX == 0 && mRotationY == 0)
        {
            mRotationY = 180;
            mRotationX = 180;
        }
        //load all assets
        TextureAtlas atlas = null;
        if (textureAtlas != null && textureAtlas.length() > 0)
        {
            atlas = world.screen.game.assets.manager.get(textureAtlas);
        }

        if (atlas != null)
        {
            region = atlas.findRegion(textureName.split(":")[1]);
        }
        else
        {
            txt = world.screen.game.assets.manager.get(textureName);
        }

        if (txt == null && region == null)
        {
            throw new GdxRuntimeException("both Texture and TextureRegion are null");
        }

        if (!rotationAplied)
        {
            applyRotation();
            rotationAplied = true;
        }

    }

    @Override
    public void dispose()
    {
        txt = null;
        region = null;
    }

    private void applyRotation()
    {
        //apply rotation
        if (mRotationX == 180.0)
        {
            mColRect.y = mDrawRect.y + ((mDrawRect.y + mDrawRect.height) - (mColRect.y + mColRect.height));
        }

        if (mRotationY == 180.0)
        {
            mColRect.x = mDrawRect.x + ((mDrawRect.x + mDrawRect.width) - (mColRect.x + mColRect.width));
        }

        if (mRotationZ != 0)
        {
            float originY = getOriginY();
            rotate2(mOrigDrawRect, mDrawRect, mOrigDrawRect.width / 2, getOriginY(), mRotationZ);
            rotate2(mColRect, mColRect, mColRect.width / 2, originY, mRotationZ);
        }
    }

    private float getOriginY()
    {
        float originY = 0;
        if (mDrawRect.width == mDrawRect.height)
        {
            originY = mOrigDrawRect.height / 2;
        }
        return originY;
    }

    /**
     * @param originX , originY, rotation point relative to self
     */
    private void rotate2(Rectangle sourceRect, Rectangle destRect, float originX, float originY, float rotate)
    {
        float x = sourceRect.x;
        float y = sourceRect.y;
        float centerX = sourceRect.x + originX;
        float centerY = sourceRect.y + originY;
        float w = sourceRect.width;
        float h = sourceRect.height;

        Polygon polygon = new Polygon(new float[]{
                x, y,
                x, y + h,
                x + w, y + h,
                x + w, y
        });

        polygon.setOrigin(centerX, centerY);
        polygon.setRotation(rotate);
        destRect.set(polygon.getBoundingRectangle());
    }

    /**
     * Type of the block
     * massive = player cant pass by it
     * passive = player passes in front of it
     * front_passive = player passes behind it
     */
    public enum Type
    {
        massive, passive, front_passive, halfmassive, climbable
    }

    @Override
    public String toString()
    {
        return "Sprite{" +
                "\nrotationAplied=" + rotationAplied +
                "\n textureAtlas='" + textureAtlas + '\'' +
                "\n textureName='" + textureName + '\'' +
                "\n type=" + type +
                "\n mOrigDrawRect=" + mOrigDrawRect +
                "\n txt=" + txt +
                "\n region=" + region +
                "\n} \n" + super.toString();
    }
}
