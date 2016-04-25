package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by pedja on 18.5.14..
 */
public abstract class GameObject implements Pool.Poolable, Json.Serializable
{
    public Rectangle bounds;//used for draw
    protected Rectangle collider;//used for collision detection
    protected Rectangle tempRect;
    public Vector2 position;

    public enum WorldState
    {
        running, jumping, dying, sliding, idle
    }

    public GameObject(float x, float y, float width, float height)
    {
        this.bounds = new Rectangle(x, y, width, height);
        this.collider = new Rectangle(bounds);
        this.position = new Vector2(x, y);
        this.tempRect = new Rectangle();
    }

    public GameObject()
    {
    }

    @Override
    public void write(Json json)
    {
        json.writeValue("bounds", bounds);
        json.writeValue("collider", collider);
        json.writeValue("position", position);
    }

    @Override
    public void read(Json json, JsonValue jsonMap)
    {
        bounds = json.readValue(Rectangle.class, jsonMap.get("bounds"));
        position = json.readValue(Vector2.class, jsonMap.get("position"));
        collider = json.readValue(Rectangle.class, jsonMap.get("collider"));
        tempRect = new Rectangle();
    }

    /**
     * main update method
     */
    public final void update(float delta)
    {
        //update bounds from position
        bounds.setPosition(position);

        //get relative collider, transform it to absolute and set it
        Rectangle tmp = getRelativeCollider();
        collider.set(bounds.x + tmp.x, bounds.y + tmp.y, tmp.width, tmp.height);

        //let implementation do the rest
        _update(delta);
    }

    public void render(SpriteBatch spriteBatch)
    {
        _render(spriteBatch);
    }

    protected abstract void _render(SpriteBatch spriteBatch);

    protected abstract void _update(float delta);

    public abstract void initAssets();

    /**
     * This must return collision rect relative to the bounds
     */
    protected Rectangle getRelativeCollider()
    {
        return tempRect.set(0, 0, bounds.width, bounds.height);
    }

    public void dispose()
    {
    }

    @Override
    public void reset()
    {
        position.set(0, 0);
        bounds.set(0, 0, 0, 0);
        collider.set(0, 0, 0, 0);
    }

    /**
     * DO NOT ytu to modify collider you changes will be overwritten
     */
    public Rectangle getCollider()
    {
        return collider;
    }

    @Override
    public String toString()
    {
        return "GameObject{" +
                "\n\tbounds=" + bounds +
                "\n\t position=" + position +
                "\n}";
    }
}
