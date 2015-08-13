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
		TextureRegion region = Assets.loadedRegions.get(textureName);
		if (region != null) Utility.draw(spriteBatch, region, position.x, position.y, bounds.height);
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

        if (hasFlip)
        {
            String newTextureName = null;
            if (flipX && !flipY)
            {
                newTextureName = textureName + "-flip_x";
            }
            else if (flipY && !flipX)
            {
                newTextureName = textureName + "-flip_y";
            }
            else if (flipY && flipX)
            {
                newTextureName = textureName + "-flip_xy";
            }

            if (newTextureName != null)
            {
				if(Assets.loadedRegions.get(newTextureName) == null)
				{
					TextureRegion orig = Assets.loadedRegions.get(textureName);
					if (orig == null)
					{
						if (atlas == null)
						{
							orig = new TextureRegion(Assets.manager.get(textureName, Texture.class));
						}
						else
						{
							orig = atlas.findRegion(textureName.split(":")[1]);
						}
						Assets.loadedRegions.put(textureName, orig);
					}
					TextureRegion flipped = new TextureRegion(orig);
					flipped.flip(flipX, flipY);
					textureName = newTextureName;
					Assets.loadedRegions.put(newTextureName, flipped);
				}
				else
				{
					textureName = newTextureName;
				}
			}
        }
        else
        {
            if (Assets.loadedRegions.get(textureName) == null)
            {
                TextureRegion textureRegion;
                if (atlas == null)
                {
                    textureRegion = new TextureRegion(Assets.manager.get(textureName, Texture.class));
                }
                else
                {
                    textureRegion = atlas.findRegion(textureName.split(":")[1]);
                }
                Assets.loadedRegions.put(textureName, textureRegion);
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
