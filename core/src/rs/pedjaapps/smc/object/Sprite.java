package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.utility.Utility;

public class Sprite extends GameObject
{
    public String textureAtlas;
    public String textureName;//name of texture from pack or png
    public Type type = null;
    private Texture txt = null;
    public TextureRegion region = null;

    public Sprite(float x, float y, float width, float height)
    {
        super(x, y, width, height);
    }

    public Sprite()
    {
    }

    @Override
    public void write(Json json)
    {
        super.write(json);
        json.writeValue("type", type);
        json.writeValue("textureAtlas", textureAtlas);
        json.writeValue("textureName", textureName);
    }

    @Override
    public void read(Json json, JsonValue jsonMap)
    {
        super.read(json, jsonMap);
        type = json.readValue(Type.class, jsonMap.get("type"));
        textureAtlas = json.readValue(String.class, jsonMap.get("textureAtlas"));
        textureName = json.readValue(String.class, jsonMap.get("textureName"));
    }

    @Override
    protected void _render(SpriteBatch spriteBatch)
    {
        if (txt != null || region != null)
        {
            float width = txt == null ? Utility.getWidth(region, bounds.height) : Utility.getWidth(txt, bounds.height);

            if (txt != null)
            {
                spriteBatch.draw(txt, bounds.x, bounds.y, width, bounds.getHeight());
            }
            else
            {
                spriteBatch.draw(region, bounds.x, bounds.y, width, bounds.height);
            }

        }
        else
        {
            throw new IllegalStateException("both Texture and TextureRegion are null");
        }
    }

    @Override
    protected void _update(float delta)
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
            region = atlas.findRegion(textureName);
            if(region == null)System.out.println("not found");
        }
        else
        {
            txt = Assets.manager.get(textureName);
        }
        if(bounds.width == 0)
        {
            float width;
            if(region == null)
            {
                width = Utility.getWidth(txt, bounds.height);
            }
            else
            {
                width = Utility.getWidth(region, bounds.height);
            }
            bounds.width = width;
        }
    }

    @Override
    public void dispose()
    {
        System.out.println("dispose");
        super.dispose();
        txt = null;
        region = null;
        World.getInstance().SPRITE_POOL.free(this);
    }



    /**
     * Type of the block
     * massive = player cant pass by it
     * passive = player passes in front of it
     * front_passive = player passes behind it
     */
    public enum Type
    {
        massive, passive, halfmassive
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
