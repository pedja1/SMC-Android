package rs.papltd.smc.model.enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import rs.papltd.smc.Assets;
import rs.papltd.smc.model.Sprite;

/**
 * Created by pedja on 18.5.14..
 */
public abstract class Enemy extends Sprite
{
    enum CLASS
    {
        eato
    }

    WorldState worldState = WorldState.IDLE;
    protected Body body;

    protected Enemy(World world, Vector2 position, float width, float height)
    {
        super(position, width, height);
        body = createBody(world, position, width, height);
    }

    public Body createBody(World world, Vector2 position, float width, float height)
    {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(position.x + width / 2, position.y + height / 2);

        Body body = world.createBody(groundBodyDef);

        PolygonShape groundBox = new PolygonShape();

        groundBox.setAsBox(width / 2, height / 2);

        body.createFixture(groundBox, 0.0f);

        groundBox.dispose();
        return body;
    }

    public abstract void loadTextures();
    public abstract void render(SpriteBatch spriteBatch, float deltaTime);

    public static Enemy initEnemy(String enemyClassString, World world, Vector2 position, float width, float height)
    {
        CLASS enemyClass = CLASS.valueOf(enemyClassString);
        Enemy enemy = null;
        switch (enemyClass)
        {
            case eato:
                enemy = new Eato(world, position, width, height);
                break;
        }
        return enemy;
    }

}
