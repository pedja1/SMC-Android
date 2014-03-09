package rs.papltd.smc.model;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g2d.*;

import rs.papltd.smc.utility.Constants;

public class Background
{
	public static final float WIDTH = Constants.CAMERA_WIDTH;
	public static final float HEIGHT = Constants.CAMERA_HEIGHT;
	public Vector2 position;
	public Texture texture;
	public float width;
	public float height;

	public Background(Vector2 position, Texture texture)
	{
		this.position = position;
		this.texture = texture;
		width = WIDTH;
		height = HEIGHT;
	}
	
	public Background(Background bgr)
	{
		position = bgr.position;
		texture = bgr.texture;
		width = bgr.width;
		height = bgr.height;
	}
	
	public void render(SpriteBatch spriteBatch)
	{
		spriteBatch.draw(texture, position.x, position.y, width, height);
	}

    public void dispose()
    {
        texture.dispose();
    }
	
}
