package rs.papltd.smc.model;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import rs.papltd.smc.*;
import rs.papltd.smc.utility.*;

public class Sprite extends GameObject
{
    protected String textureAtlas;
    private String textureName;//name of texture from pack
    protected TYPE type = null;
    protected Vector2 position;

    @Override
    public void render(SpriteBatch spriteBatch)
    {
		TextureRegion region = Assets.loadedRegions.get(textureName);
		//spriteBatch.draw(region, sprite.getPosition().x, sprite.getPosition().y, sprite.getBounds().width, sprite.getBounds().height);
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
     * massive = player cant pass by it or stand on it, eg. no collision detection
     * passive = player passes in front of it
     * front_passive = player passes behind it
     * */
    public enum TYPE
    {
        massive, passive, front_passive
    }

    public Sprite(Vector2 position, float width, float height)
    {
        super(new Rectangle(position.x, position.y, width, height));
        this.position = position;
    }

    public Vector2 getPosition()
    {
        return position;
    }

    public Rectangle getBounds()
    {
        return bounds;
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

    public TYPE getType()
    {
        return type;
    }

    public void setType(TYPE type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return textureName;
    }
}
