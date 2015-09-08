package rs.pedjaapps.smc.view;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.*;
import rs.pedjaapps.smc.*;
import rs.pedjaapps.smc.utility.*;

public class Background
{
	public static final float WIDTH = Constants.CAMERA_WIDTH;
	public static final float HEIGHT = Constants.CAMERA_HEIGHT;
	public Vector2 position;
	public Texture texture;
	public String textureName;
	public float width;
	public float height;

	public Color color1;
    public Color color2;
	ShapeRenderer renderer = new ShapeRenderer();
	public OrthographicCamera bgCam;

	public Background(Vector2 position, String textureName)
	{
		this.position = position;
		this.textureName = textureName;
		width = WIDTH;
		height = HEIGHT;
		bgCam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        bgCam.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        //bgCam.position.set(cam.position.x, cam.position.y, 0);
        bgCam.update();
	}

	public void resize()
	{
		bgCam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        bgCam.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        //bgCam.position.set(cam.position.x, cam.position.y, 0);
        bgCam.update();
	}

	public void render(OrthographicCamera gameCam, SpriteBatch spriteBatch)
	{
		renderer.setProjectionMatrix(gameCam.combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.rect(gameCam.position.x - Constants.CAMERA_WIDTH / 2, gameCam.position.y - Constants.CAMERA_HEIGHT / 2,
					  Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT, color2,
					  color2, color1, color1);
		renderer.end();

		if (texture != null)
		{
			bgCam.position.set(gameCam.position.x * Constants.BACKGROUND_SCROLL_SPEED + gameCam.viewportWidth * 0.44f,
							   gameCam.position.y * Constants.BACKGROUND_SCROLL_SPEED + gameCam.viewportHeight * 0.44f, 0);
			bgCam.update();

			spriteBatch.setProjectionMatrix(bgCam.combined);
			spriteBatch.begin();

			spriteBatch.draw(texture, position.x, position.y, width, height);
			if(position.x + width < bgCam.position.x + bgCam.viewportWidth * .5f)
			{
				spriteBatch.draw(texture, position.x + width, position.y, width, height);
			}
			if(position.x + width < bgCam.position.x - bgCam.viewportWidth * .5f)
			{
				position.x = position.x + width;
			}

			spriteBatch.end();
		}
	}
	
	public void onAssetsLoaded()
	{
		texture = Assets.manager.get(textureName);
	}

	public void dispose()
	{
		renderer.dispose();
		renderer = null;
		if(texture != null)texture.dispose();
		texture = null;
	}

}
