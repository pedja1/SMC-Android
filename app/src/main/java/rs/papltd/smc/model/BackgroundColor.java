package rs.papltd.smc.model;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.*;
import rs.papltd.smc.utility.*;

/**
 * Created by pedja on 2/15/14.
 */
public class BackgroundColor
{
    public Color color1;
    public Color color2;
	ShapeRenderer renderer = new ShapeRenderer();
	
	public void render(Camera cam)
	{
		renderer.setProjectionMatrix(cam.combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.rect(cam.position.x - Constants.CAMERA_WIDTH / 2, cam.position.y - Constants.CAMERA_HEIGHT / 2,
                Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT, color2,
                color2, color1, color1);
       renderer.end();
	}
    
    public void dispose()
    {
        renderer.dispose();
    }
}
