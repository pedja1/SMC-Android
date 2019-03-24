package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.utility.Constants;


public class World {
    /**
     * Our player controlled hero *
     */
    public Maryo maryo;
    /**
     * A world has a level through which Mario needs to go through *
     */
    public Level level;
    public Array<String> visitedSubLevels = new Array<>();
    private Array<GameObject> visibleObjects = new Array<>(50);
    private Rectangle worldBounds = new Rectangle();

    /**
     *
     */
    public final Array<GameObject> trashObjects = new Array<>();

    // --------------------

    /**
     * Return only the blocks that need to be drawn *
     */
    public void drawVisibleObjects(OrthographicCamera cam, SpriteBatch batch) {
        visibleObjects.clear();
        setRectToVisibleCamArea(worldBounds, cam);
        for (int i = 0, size = level.gameObjects.size(); i < size; i++) {
            GameObject object = level.gameObjects.get(i);
            Rectangle bounds = object.drawRect;
            if (bounds.overlaps(worldBounds)) {
                visibleObjects.add(object);
                object.render(batch);
            }
        }
    }

    public static void setRectToVisibleCamArea(Rectangle worldBounds, OrthographicCamera cam) {
        float camX = cam.position.x;
        float camY = cam.position.y;
        float camWidth = (cam.viewportWidth * cam.zoom);
        float camHeight = (cam.viewportHeight * cam.zoom);
        float wX = camX - camWidth * 0.5f - 1;
        float wY = camY - camHeight * 0.5f - 1;
        float wW = camWidth + 1;
        float wH = camHeight + 1;
        worldBounds.set(wX, wY, wW, wH);
    }

    public void createMaryoRectWithOffset(Rectangle offsetBounds, float offset) {
        float offsetX = Math.max(offset, Constants.CAMERA_WIDTH);
        float offsetY = Math.max(offset * 0.5f, Constants.CAMERA_HEIGHT);
        float wX = maryo.colRect.x - offsetX;
        float wY = maryo.colRect.y - offsetY;
        float wW = maryo.colRect.x + maryo.colRect.width + offsetX * 2;
        float wH = maryo.colRect.y + maryo.colRect.height + offsetY * 2;
        offsetBounds.set(wX, wY, wW, wH);
    }

    public Array<GameObject> getVisibleObjects() {
        return visibleObjects == null ? new Array<GameObject>() : visibleObjects;
    }

    /**
     * Check if obejct is visible in current camera bounds
     */
    public boolean isObjectVisible(GameObject object, OrthographicCamera cam) {
        setRectToVisibleCamArea(worldBounds, cam);
        return object.drawRect.overlaps(worldBounds);
    }

    public void dispose() {
        level.dispose();
        maryo.dispose();
        maryo = null;
    }
}
