package rs.papltd.smc.model;

import com.badlogic.gdx.math.*;

public class Sprite extends GameObject
{
    protected Rectangle bounds = new Rectangle();
    protected String textureAtlas;
    private String textureName;//name of texture from pack
    protected TYPE type = null;
    private Vector2 position;
    boolean isFront = false; // is sprite drawn after player, so that it appears like player walks behind it

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
        this.position = position;
        bounds.x = position.x;
        bounds.y = position.y;
        bounds.width = width;
        bounds.height = height;
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

    public boolean isFront()
    {
        return isFront;
    }

    public void setFront(boolean isFront)
    {
        this.isFront = isFront;
    }

    @Override
    public String toString()
    {
        return textureName;
    }
}
