package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.utility.Utility;

public class Sprite extends GameObject
{
    public String textureAtlas;
    public String textureName;//name of texture from pack or png
    public Type type = null;
    Texture txt = null;
    TextureRegion region = null;

    public Sprite(World world, Vector2 size, Vector3 position, Rectangle colRect)
    {
        super(world, size, position);
        this.position = position;
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
        if (txt != null || region != null)
        {
            float width = txt == null ? Utility.getWidth(region, mDrawRect.height) : Utility.getWidth(txt, mDrawRect.height);

            if (txt != null)
            {
                spriteBatch.draw(txt, mDrawRect.x, mDrawRect.y, width, mDrawRect.getHeight());
            }
            else
            {
                spriteBatch.draw(region, mDrawRect.x, mDrawRect.y, width, mDrawRect.height);
            }

        }
        else
        {
            throw new IllegalStateException("both Texture and TextureRegion are null");
        }
    }

    @Override
    public void _update(float delta)
    {

    }

    @Override
    public void initAssets()
    {
        //load all assets
        TextureAtlas atlas = null;
        if (textureAtlas != null && textureAtlas.length() > 0)
        {
            atlas = Assets.manager.get(textureAtlas);
        }

        if (atlas != null)
        {
            String[] split = textureName.split(":");
            region = atlas.findRegion(split.length == 2 ? split[1] : textureName);
        }
        else
        {
            txt = Assets.manager.get(textureName);
        }
        if(mDrawRect.width == 0)
        {
            float width;
            if(region == null)
            {
                width = Utility.getWidth(txt, mDrawRect.height);
            }
            else
            {
                width = Utility.getWidth(region, mDrawRect.height);
            }
            mDrawRect.width = width;
            updateBounds();
        }

    }

    @Override
    public void dispose()
    {
        super.dispose();
        txt = null;
        region = null;
        world.SPRITE_POOL.free(this);
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
                "\n textureAtlas='" + textureAtlas + '\'' +
                "\n textureName='" + textureName + '\'' +
                "\n type=" + type +
                "\n txt=" + txt +
                "\n region=" + region +
                "\n} \n" + super.toString();
    }
}
