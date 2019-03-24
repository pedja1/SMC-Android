package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.MaryoGame;


/**
 * Created by pedja on 18.5.14..
 */
public abstract class GameObject {
    public Rectangle drawRect;//used for draw
    public Rectangle colRect;//used for collision detection
    public Vector3 position;
    public Vector3 velocity = new Vector3();
    public Vector3 acceleration = new Vector3();
    public float rotationX, rotationY, rotationZ;//degrees

    public GameObject(float x, float y, float z, float width, float height) {
        this.drawRect = new Rectangle(x, y, width, height);
        colRect = new Rectangle(drawRect);
        this.position = new Vector3(x, y, z);
    }

    public void updateBounds() {
        drawRect.x = colRect.x;
        drawRect.y = colRect.y;
    }

    protected void trashObject(GameObject gameObject) {
        MaryoGame.game.trashObject(gameObject);
    }

    protected void trashThisObject() {
        MaryoGame.game.trashObject(this);
    }

    //TODO only internal method should start with underscore
    public abstract void render(SpriteBatch spriteBatch);

    public abstract void update(float delta);

    public abstract void initAssets();

    public abstract void dispose();

    /**
     * whether this object acts as bullet when hitting other objects (enemies, mario)
     */
    public boolean isBullet() {
        return false;
    }

    @Override
    public String toString() {
        return "GameObject{" +
                "\n\tdrawRect=" + drawRect +
                "\n\t colRect=" + colRect +
                "\n\t position=" + position +
                "\n\t velocity=" + velocity +
                "\n\t acceleration=" + acceleration +
                "\n\t rotationX=" + rotationX +
                "\n\t rotationY=" + rotationY +
                "\n\t rotationZ=" + rotationZ +
                "\n}";
    }
}
