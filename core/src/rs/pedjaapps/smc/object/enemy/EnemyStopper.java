package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.object.GameObject;

public class EnemyStopper extends GameObject {

    public EnemyStopper(float x, float y, float z, float width, float height) {
        super(x, y, z, width, height);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        // this object is invisible
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void initAssets() {

    }

    @Override
    public void dispose() {

    }

}
