package rs.pedjaapps.smc.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.*;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;

import java.util.*;

import rs.pedjaapps.smc.Assets;

public class HUD
{
	TextureRegion pause, fire, jump, up, down, right,
		left, pauseP, fireP, jumpP, upP, downP, leftP, 
		rightP;
    public Rectangle pauseR, fireR, jumpR, upR, downR, rightR, leftR;
    public Array<Vector2> leftPolygon = new Array<Vector2>(5);
    public Array<Vector2> rightPolygon = new Array<Vector2>(5);
    public Array<Vector2> upPolygon = new Array<Vector2>(5);
    public Array<Vector2> downPolygon = new Array<Vector2>(5);
	
	OrthographicCamera cam;
	SpriteBatch batch;
	
	public static final float C_W = Gdx.graphics.getWidth();
	public static final float C_H = Gdx.graphics.getHeight();
	
	public enum Key
	{
		pause, fire, jump, left, right, up, down
	}
	
	public HashSet<Key> pressedKeys = new HashSet<Key>();
	
	public HUD()
	{
		cam = new OrthographicCamera(C_W, C_H);
		cam.position.set(new Vector2(C_W/2, C_H/2), 0);
		cam.update();
		batch = new SpriteBatch();
        setBounds();
	}

    private void setBounds()
    {
        float width = C_H/10f;
        float height = width;
        float x = C_W - width*1.5f;
        float y = C_H - height*1.5f;
        pauseR = new Rectangle(x, y, width, height);

        width = C_H/7f;
        height = width;
        x = C_W - width*1.5f;
        y = C_H - height*5f;
        fireR = new Rectangle(x, y, width, height);

        x = x - width;
        y = y - height;
        jumpR = new Rectangle(x, y, width, height);

        x = width/2f;
        y = height*1.5f;
        width = width * 1.24f;
        leftR = new Rectangle(x, y, width, height);
        leftPolygon.add(new Vector2(x, y + height));
        leftPolygon.add(new Vector2(x + width - x / 100 * 23.25f, y + height));
        leftPolygon.add(new Vector2(x + width, y + height / 2));
        leftPolygon.add(new Vector2(x + width - x / 100 * 23.25f, y));
        leftPolygon.add(new Vector2(x, y));

        x = x + width + width/4f;
        rightR = new Rectangle(x, y, width, height);
        rightPolygon.add(new Vector2(x, y + height / 2));
        rightPolygon.add(new Vector2(x + x / 100 * 23.25f, y + height));//x / 100 * 23.25%
        rightPolygon.add(new Vector2(x + width, y + height));
        rightPolygon.add(new Vector2(x + width, y));
        rightPolygon.add(new Vector2(x + x / 100 * 23.25f, y));

        width = C_H/7f;
        height = width*1.24f;
        x = x - width/2f - width/8f;
        y = y + height/2f;
        upR = new Rectangle(x, y, width, height);
        upPolygon.add(new Vector2(x, y + height));
        upPolygon.add(new Vector2(x + width, y + height));
        upPolygon.add(new Vector2(x + width, y + y / 100 * 23.25f));
        upPolygon.add(new Vector2(x + width / 2, y));
        upPolygon.add(new Vector2(x, y + y / 100 * 23.25f));

        y = y - height - height/4f;
        downR = new Rectangle(x, y, width, height);
        downPolygon.add(new Vector2(x + width / 2, y + width));
        downPolygon.add(new Vector2(x + width, y + height - y / 100 * 23.25f));
        downPolygon.add(new Vector2(x + width, y));
        downPolygon.add(new Vector2(x, y));
        downPolygon.add(new Vector2(x, y + height - y / 100 * 23.25f));

    }

    public void loadAssets()
	{
		TextureAtlas atlas = Assets.manager.get("data/hud/controls.pack", TextureAtlas.class);
		pause = atlas.findRegion("pause");
		pauseP = atlas.findRegion("pause-pressed");
		fire = atlas.findRegion("fire");
		fireP = atlas.findRegion("fire-pressed");
		jump = atlas.findRegion("jump");
		jumpP = atlas.findRegion("jump-pressed");
	    left = atlas.findRegion("dpad-left");
		leftP = atlas.findRegion("dpad-left-pressed");
		right = new TextureRegion(left);
		right.flip(true, false);
		rightP = new TextureRegion(leftP);
		rightP.flip(true, false);
		up = atlas.findRegion("dpad-up");
		upP = atlas.findRegion("dpad-up-pressed");
		down = new TextureRegion(up);
		down.flip(false, true);
		downP = new TextureRegion(upP);
		downP.flip(false, true);
	}
	
	public void render()
	{
		batch.setProjectionMatrix(cam.combined);
		batch.begin();

		batch.draw(pressedKeys.contains(Key.pause) ? pauseP : pause, pauseR.x, pauseR.y, pauseR.width, pauseR.height);
        batch.draw(pressedKeys.contains(Key.fire) ? fireP : fire, fireR.x, fireR.y ,fireR.width, fireR.height);
        batch.draw(pressedKeys.contains(Key.jump) ? jumpP : jump, jumpR.x, jumpR.y ,jumpR.width, jumpR.height);
        batch.draw(pressedKeys.contains(Key.left) ? leftP : left, leftR.x, leftR.y ,leftR.width, leftR.height);
        batch.draw(pressedKeys.contains(Key.right) ? rightP : right, rightR.x, rightR.y ,rightR.width, rightR.height);
        batch.draw(pressedKeys.contains(Key.up) ? upP : up, upR.x, upR.y, upR.width, upR.height);
        batch.draw(pressedKeys.contains(Key.down) ? downP : down, downR.x, downR.y, downR.width, downR.height);
		
		batch.end();
	}

    public void leftPressed()
    {
        pressedKeys.add(Key.left);
    }

    public void leftReleased()
    {
        pressedKeys.remove(Key.left);
    }

    public void rightPressed()
    {
        pressedKeys.add(Key.right);
    }

    public void rightReleased()
    {
        pressedKeys.remove(Key.right);
    }

    public void upPressed()
    {
        pressedKeys.add(Key.up);
    }

    public void upReleased()
    {
        pressedKeys.remove(Key.up);
    }

    public void downPressed()
    {
        pressedKeys.add(Key.down);
    }

    public void downReleased()
    {
        pressedKeys.remove(Key.down);
    }

    public void firePressed()
    {
        pressedKeys.add(Key.fire);
    }

    public void fireReleased()
    {
        pressedKeys.remove(Key.fire);
    }

    public void jumpPressed()
    {
        pressedKeys.add(Key.jump);
    }

    public void jumpReleased()
    {
        pressedKeys.remove(Key.jump);
    }

    public void pausePressed()
    {
        pressedKeys.add(Key.pause);
    }

    public void pauseReleased()
    {
        pressedKeys.remove(Key.pause);
    }
}
