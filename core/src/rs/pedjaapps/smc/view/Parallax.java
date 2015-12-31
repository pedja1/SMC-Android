package rs.pedjaapps.smc.view;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.utility.Constants;

public class Parallax
{
    boolean cameraPositioned;
    public Vector2 speed;
    public Vector3 oldGameCamPos = new Vector3();

    public OrthographicCamera cam;

    public float lastViewportRight;

    public Array<GameObject> objects;

    public NextViewportCallback nextViewportCallback;

    public Parallax(Vector2 speed)
    {
        objects = new Array<>(100);
        this.speed = speed;
        cam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        cam.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        cam.update();
    }

    public void resize(OrthographicCamera gameCam)
    {
        /*cam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        cam.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        cam.position.set(gameCam.position.x, gameCam.position.y, 0);
        cam.update();*/
    }

    public void render(OrthographicCamera mainCam, SpriteBatch spriteBatch)
    {
        cam.position.add((mainCam.position.x - oldGameCamPos.x) * speed.x, (mainCam.position.y - oldGameCamPos.y) * speed.y, 0);
        if (cam.position.x < cam.viewportWidth * .5f)
        {
            cam.position.x = cam.viewportWidth * .5f;
        }
        if (cam.position.y < cam.viewportHeight * .5f)
        {
            cam.position.y = cam.viewportHeight * .5f;
        }
        cam.update();
        oldGameCamPos.set(mainCam.position);

        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();

        for (int i = 0, size = objects.size; i < size; i++)
        {
            GameObject object = objects.get(i);
            object._render(spriteBatch);
        }

        spriteBatch.end();

        if(lastViewportRight < cam.position.x + cam.viewportWidth * .5f)
        {
            lastViewportRight += cam.viewportWidth;
            if(nextViewportCallback != null)
                nextViewportCallback.onNextViewport(lastViewportRight, cam.viewportWidth, cam.viewportHeight);
        }

    }

    public void onAssetsLoaded(OrthographicCamera mainCam)
    {
        if(cameraPositioned)
            return;
        cam.position.set(mainCam.position.x, mainCam.position.y, 0);
        lastViewportRight = cam.position.x - cam.viewportWidth * .5f;
        oldGameCamPos.set(mainCam.position);
        cameraPositioned = true;
    }

    public void dispose()
    {
        for(GameObject object : objects)
        {
            object.dispose();
        }
        objects = null;
    }

    public interface NextViewportCallback
    {
        void onNextViewport(float viewportStartX, float viewportWidth, float viewportHeight);
    }

}
