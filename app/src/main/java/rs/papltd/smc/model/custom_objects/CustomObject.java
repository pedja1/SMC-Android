package rs.papltd.smc.model.custom_objects;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import rs.papltd.smc.model.*;

/**
 * Created by pedja on 24.5.14..
 */
public abstract class CustomObject extends Sprite
{
    protected float stateTime;
    enum CLASS
    {
        coin
    }
    WorldState worldState = WorldState.IDLE;
    protected Body body;
    protected World world;

    public CustomObject(World world, Vector3 position, float width, float height)
    {
        super(position, width, height);
        this.world = world;
        body = createBody(world, position, width, height);
    }

    public Body createBody(World world, Vector3 position, float width, float height)
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
    }


    public static CustomObject initObject(String objectClassString, World world, Vector3 position, float width, float height)
    {
        CLASS enemyClass = CLASS.valueOf(objectClassString);
        CustomObject object = null;
        switch (enemyClass)
        {
            case coin:
                object = new Coin(world, position, width, height);
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
