package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.utility.Utility;

public class Sprite extends GameObject
{
    public String textureAtlas;
    public String textureName;//name of texture from pack or png
    public Type type = null;
    public int rotationX, rotationY, rotationZ;//degrees

    @Override
    public void render(SpriteBatch spriteBatch)
    {
		Texture txt = null;
        TextureRegion region = null;
        if(textureAtlas == null)
        {
            txt = Assets.manager.get(textureName);
        }
        else
        {
            region = Assets.loadedRegions.get(textureName);
        }
		if (txt != null || region != null)
        {
            float width = txt == null ? Utility.getWidth(region, bounds.height) : Utility.getWidth(txt, bounds.height);
            float originX = bounds.x + width / 2;
            float originY = bounds.y + bounds.height / 2;
            float rotation = rotationZ;
            boolean flipX = rotationY == 180;
            boolean flipY = rotationX == 180;

            if(txt != null)
            {
                spriteBatch.draw(txt, position.x, position.y, originX, originY, width, bounds.height, 1, 1, rotation, 0, 0, txt.getWidth(), txt.getHeight(), flipX, flipY);
            }
            else
            {
                region.flip(flipX, flipY);//flip it
                spriteBatch.draw(region, position.x ,position.y, originX, originY, width, bounds.height, 1, 1, rotation);
                region.flip(flipX, flipY);//return it to original
            }

        }
    }

    @Override
    public void update(float delta)
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

		if(atlas != null)
		{
			TextureRegion region = Assets.loadedRegions.get(textureName);
			if(region == null)
			{
				Assets.loadedRegions.put(textureName, atlas.findRegion(textureName.split(":")[1]));
			}
		}
    }

    /**
     * Type of the block
     * massive = player cant pass by it
     * passive = player passes in front of it
     * front_passive = player passes behind it
     * */
    public enum Type
    {
        massive, passive, front_passive, halfmassive, climbable
		}

    public Sprite(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        this.position = position;
    }

    @Override
    public String toString()
    {
        return textureName;
    }
}
