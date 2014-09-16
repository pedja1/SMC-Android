package rs.pedjaapps.smc.model.items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import rs.pedjaapps.smc.model.Coin;
import rs.pedjaapps.smc.model.Sprite;
import rs.pedjaapps.smc.model.World;

/**
 * Created by pedja on 24.5.14..
 */
public abstract class Item extends Sprite
{
    protected float stateTime;
    enum CLASS
    {
        goldpiece, moon, jstar, mushroom, fireplant
    }
    WorldState worldState = WorldState.IDLE;

    public Item(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
    }

    /*public Body createBody(World world, Vector3 position, float width, float height)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(position.x + width / 2, position.y + height / 2);

        Body body = world.createBody(bodyDef);
        body.setGravityScale(0);

        PolygonShape polygonShape = new PolygonShape();

        polygonShape.setAsBox(width / 2, height / 2);

        body.createFixture(polygonShape, 0.0f);

        polygonShape.dispose();
        return body;
    }*/


    public static Item initObject(World world, String objectClassString, Vector2 size, Vector3 position)
    {
        CLASS itemClass = CLASS.valueOf(objectClassString);
        Item object = null;
        switch (itemClass)
        {
            case goldpiece:
                object = new Coin(world, size, position);
                break;
        }
        return object;
    }

    @Override
    public void update(float delta)
    {
        stateTime += delta;
    }
}
