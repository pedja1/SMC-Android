package rs.papltd.smc.model.enemy;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import rs.papltd.smc.model.*;

/**
 * Created by pedja on 18.5.14..
 */
public abstract class Enemy extends GameObject
{
    protected float stateTime;
    protected String textureAtlas;
    private String textureName;//name of texture from pack
	protected Direction direction = Direction.right;

	public void setDirection(Direction direction)
	{
		this.direction = direction;
	}

	public Direction getDirection()
	{
		return direction;
	}
	
	public enum Direction
	{
		right, left
	}

	public void setTextureAtlas(String textureAtlas)
	{
		this.textureAtlas = textureAtlas;
	}

	public String getTextureAtlas()
	{
		return textureAtlas;
	}

	public void setTextureName(String textureName)
	{
		this.textureName = textureName;
	}

	public String getTextureName()
	{
		return textureName;
	}
	
    enum CLASS
    {
        eato, flyon, furball, turtle, gee, krush, rokko, spika, spikeball, thromp, turtleboss
    }
	
	public enum ContactType
	{
		stopper, player, enemy
	}

    WorldState worldState = WorldState.IDLE;
    protected Body body;
    protected World world;

    protected Enemy(World world, Vector3 position, float width, float height)
    {
        super(new Rectangle(position.x, position.y, width, height), position);
        this.world = world;
        body = createBody(world, position, width, height);
    }

    public Body createBody(World world, Vector3 position, float width, float height)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = getBodyType();
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
		body.setUserData(this);

        polygonShape.dispose();
        return body;
    }

    public static Enemy initEnemy(String enemyClassString, World world, Vector3 position, float width, float height)
    {
		System.out.println(enemyClassString);
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
			case furball:
                enemy = new Furball(world, position, width, height);
                break;
        }
        return enemy;
    }

    @Override
    public void update(float delta)
    {
        stateTime += delta;
    }
	
	public abstract BodyDef.BodyType getBodyType();
	public void handleCollision(ContactType ContactType)
	{
		// subclasses should implement this
	}
}
