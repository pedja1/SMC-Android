package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import rs.pedjaapps.smc.object.DynamicObject;
import rs.pedjaapps.smc.object.World;

/**
 * Created by pedja on 18.5.14..
 */
public abstract class Enemy extends DynamicObject
{
    public String textureAtlas;
    public String textureName;//name of texture from pack
    protected Direction direction = Direction.right;
    public boolean handleCollision = true;

    public void setDirection(Direction direction)
    {
        this.direction = direction;
    }

    public Direction getDirection()
    {
        return direction;
    }

    protected Enemy(float x, float y, float width, float height)
    {
        super(x, y, width, height);
    }

    public Enemy()
    {
    }

    @Override
    public void write(Json json)
    {
        super.write(json);
        json.writeValue("direction", direction);
        json.writeValue("textureAtlas", textureAtlas);
        json.writeValue("textureName", textureName);
    }

    @Override
    public void read(Json json, JsonValue jsonMap)
    {
        super.read(json, jsonMap);
        direction = json.readValue(Direction.class, jsonMap.get("direction"));
        textureAtlas = json.readValue(String.class, jsonMap.get("textureAtlas"));
        textureName = json.readValue(String.class, jsonMap.get("textureName"));
    }

    @Override
    public final void _render(SpriteBatch spriteBatch)
    {
        doRender(spriteBatch);
    }

    private void doRender(SpriteBatch batch)
    {
        render(batch);
    }

    @Override
    public float maxVelocity()
    {
        return DEF_MAX_VEL;
    }

    @Override
    protected boolean handleDroppedBelowWorld()
    {
        World.getInstance().level.gameObjects.removeValue(this, true);
        return true;
    }
}
