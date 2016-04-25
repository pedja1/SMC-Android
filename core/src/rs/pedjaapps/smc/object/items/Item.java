package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.graphics.Texture;

import rs.pedjaapps.smc.object.DynamicObject;

/**
 * Created by pedja on 24.5.14..
 */
public abstract class Item extends DynamicObject
{
    public boolean playerHit;

    //is drawn
    public boolean visible = true;

    //collectible by player
    public boolean collectible = true;
    public Texture texture;

    private boolean dropping;

    public Item(float x, float y, float widht, float height)
    {
        super(x, y, widht, height);
    }

    public Item()
    {
        super();
    }

    @Override
    protected final void _update(float delta)
    {
        if(dropping)
        {
            stateTime += delta;
            velocity.y = -1f;
            velocity.x = 0;

            velocity.scl(delta);

            position.add(velocity);

            velocity.scl(1 / delta);
        }
        else
        {
            updateItem(delta);
        }
    }

    public abstract void hitPlayer();

    public void updateItem(float delta)
    {
        stateTime += delta;
    }

    public void drop()
    {
        dropping = true;
        playerHit = false;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        if(texture != null)texture = null;
    }

    @Override
    public void reset()
    {
        super.reset();
        playerHit = false;
    }
}
