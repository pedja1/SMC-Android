package rs.papltd.smc.model.enemy;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import rs.papltd.smc.model.GameObject;

/**
 * Created by pedja on 18.5.14..
 */
public abstract class Enemy extends GameObject
{
    protected float stateTime;
    protected Vector2 velocity;
    protected String textureAtlas;
    private String textureName;//name of texture from pack
    protected Vector2 position;
    enum CLASS
    {
        eato, flyon, furball, turtle
    }

    WorldState worldState = WorldState.IDLE;
    protected Body body;
    protected World world;

    protected Enemy(World world, Vector2 position, float width, float height)
    {
        super(new Rectangle(position.x, position.y, width, height));
        this.position = position;
        this.world = world;
        velocity = new Vector2();
        body = createBody(world, position, width, height);
    }

    public Body createBody(World world, Vector2 position, float width, float height)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x + width / 2, position.y + height / 2);

        Body body = world.createBody(bodyDef);

        /*MassData massData = new MassData();
        massData.mass = 0.0000001f;
        body.setMassData(massData);
        body.setUserData(this);*/

        PolygonShape polygonShape = new PolygonShape();

        polygonShape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1062;
        fixtureDef.friction = /*0.5f*/0;
        fixtureDef.restitution = 0.5f;

        body.createFixture(fixtureDef);

        polygonShape.dispose();
        return body;
    }

    public static Enemy initEnemy(String enemyClassString, World world, Vector2 position, float width, float height)
    {
        CLASS enemyClass = CLASS.valueOf(enemyClassString);
        Enemy enemy = null;
        switch (enemyClass)
        {
            case eato:
                enemy = new Eato(world, position, width, height);
                break;
            case flyon:
                enemy = new Flyon(world, position, width, height);
                break;
        }
        return enemy;
    }

    @Override
    public void update(float delta)
    {
        stateTime += delta;
    }
}
