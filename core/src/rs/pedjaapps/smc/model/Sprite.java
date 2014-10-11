package rs.pedjaapps.smc.model;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.utility.Utility;

public class Sprite extends GameObject
{
    protected String textureAtlas;
    protected String textureName;//name of texture from pack or png
    protected Type type = null;

    @Override
    public void render(SpriteBatch spriteBatch)
    {
		TextureRegion region = Assets.loadedRegions.get(textureName);
		Utility.draw(spriteBatch, region, position.x, position.y, bounds.height);
        
    }

    @Override
    public void update(float delta)
    {

    }

    @Override
    public void loadTextures()
    {

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

    

    public String getTextureAtlas()
    {
        return textureAtlas;
    }

    public void setTextureAtlas(String textureAtlas)
    {
        this.textureAtlas = textureAtlas;
    }

    public String getTextureName()
    {
        return textureName;
    }

    public void setTextureName(String textureName)
    {
        this.textureName = textureName;
    }

    public void setBounds(Rectangle bounds)
    {
        this.bounds = bounds;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return textureName;
    }
}
