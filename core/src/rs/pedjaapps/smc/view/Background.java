package rs.pedjaapps.smc.view;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.*;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.utility.*;

public class Background
{
	public static final float WIDTH = Constants.CAMERA_WIDTH;
	public static final float HEIGHT = Constants.CAMERA_HEIGHT;
	public Vector2 position, speed;
	public Texture texture;
	public String textureName;
	public float width;
	public float height;
	public Vector3 oldGameCamPos = new Vector3();

	public Color color1;
    public Color color2;
	ShapeRenderer renderer = new ShapeRenderer();
	public OrthographicCamera bgCam;

	public Background(Vector2 position, Vector2 speed, String textureName)
	{
		this.speed = speed;
		this.position = position;
		this.textureName = textureName;
		width = WIDTH;
		height = HEIGHT;
		bgCam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        bgCam.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        bgCam.update();
	}

	public void resize(OrthographicCamera gameCam)
	{
		bgCam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        bgCam.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        bgCam.position.set(gameCam.position.x, gameCam.position.y, 0);
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
			bgCam.position.add((gameCam.position.x - oldGameCamPos.x) * speed.x, (gameCam.position.y - oldGameCamPos.y) * speed.y, 0);
			if(bgCam.position.x < bgCam.viewportWidth * .5f)
			{
				bgCam.position.x = bgCam.viewportWidth * .5f;
			}
			if(bgCam.position.y < bgCam.viewportHeight * .5f)
			{
				bgCam.position.y = bgCam.viewportHeight * .5f;
			}
			bgCam.update();
			oldGameCamPos.set(gameCam.position);

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
	
	public void onAssetsLoaded(OrthographicCamera gameCam, Assets assets)
	{
		if(textureName != null)
		{
			texture = assets.manager.get(textureName);
			bgCam.position.set(gameCam.position.x, gameCam.position.y, 0);
        	oldGameCamPos.set(gameCam.position);
		}
	}

	public void dispose()
	{
		renderer.dispose();
		renderer = null;
		if(texture != null)texture.dispose();
		texture = null;
	}

}
