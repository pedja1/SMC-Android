package rs.papltd.smc.screen;

import android.util.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import javax.microedition.khronos.opengles.*;
import rs.papltd.smc.*;
import rs.papltd.smc.controller.*;
import rs.papltd.smc.model.*;
import rs.papltd.smc.view.*;

public class GameScreen extends AbstractScreen implements InputProcessor
{

    private WorldWrapper world;
    private WorldRenderer renderer;
    private MarioController controller;
    DPad dPad;
    BtnJump jump;
    HUD hud;

    private int width, height;

    enum CONTROL_CLICK_AREA
    {
        NONE, DPAD_RIGHT, DPAD_LEFT, DPAD_TOP, DPAD_BOTTOM, JUMP, FIRE
    }

    enum GAME_STATE
    {
        GAME_READY, GAME_RUNNING, GAME_PAUSED, GAME_LEVEL_END, GAME_OVER
    }

    private GAME_STATE gameState;

    private SparseArray<TouchInfo> touches = new SparseArray<TouchInfo>();
    boolean update = false;

    public GameScreen(MaryoGame game)
    {
		super(game);
        gameState = GAME_STATE.GAME_READY;
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        world = new WorldWrapper();
        hud = new HUD();
        dPad = new DPad(0.3f * width);
        jump = new BtnJump(0.20f * height, new Vector2(width - 0.25f * width, 0.05f * height));
        renderer = new WorldRenderer(this, true);
        controller = new MarioController(world);
        Gdx.input.setInputProcessor(this);

        for (int i = 0; i < 5; i++) //handle max 4 touches
        {
            touches.put(i, new TouchInfo());
        }
        //Gdx.graphics.setContinuousRendering(false);
    }

    @Override
    public void show()
    {

    }

    /*public void update(float deltaTime)
    {
        if (deltaTime > 0.1f) deltaTime = 0.1f;

        switch (gameState)
        {
            case GAME_READY:
                updateReady();
                break;
            case GAME_RUNNING:
                updateRunning(deltaTime);
                break;
            case GAME_PAUSED:
                updatePaused();
                break;
            case GAME_LEVEL_END:
                updateLevelEnd();
                break;
            case GAME_OVER:
                updateGameOver();
                break;
        }
    }*/

    private void updateReady()
    {
        if (Gdx.input.justTouched())
        {
            gameState = GAME_STATE.GAME_RUNNING;
        }
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        if (update) controller.update(delta);
        renderer.render(delta);
        hud.render();
    }

    @Override
    public void resize(int width, int height)
    {
        renderer.setSize(width, height);
        this.width = width;
        this.height = height;
    }

    @Override
    public void hide()
    {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose()
    {
        Gdx.input.setInputProcessor(null);
        Assets.dispose();
    }

    @Override
    public void loadAssets()
    {

    }

    @Override
    public void afterLoadAssets()
    {

    }

    // * InputProcessor methods ***************************//

    @Override
    public boolean keyDown(int keycode)
    {
        update = true;
        if (keycode == Input.Keys.LEFT)
        {
            controller.leftPressed();
            hud.leftPressed();
        }
        if (keycode == Input.Keys.RIGHT)
        {
            controller.rightPressed();
            hud.rightPressed();
        }
        if (keycode == Input.Keys.SPACE)
        {
            controller.jumpPressed();
            hud.jumpPressed();
        }
        if (keycode == Input.Keys.X)
        {
            controller.firePressed();
            hud.firePressed();
        }
        if (keycode == Input.Keys.DOWN)
        {
            controller.downPressed();
            hud.downPressed();
        }
        if (keycode == Input.Keys.UP)
        {
            controller.upPressed();
            hud.upPressed();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        if (keycode == Input.Keys.LEFT)
        {
            controller.leftReleased();
            hud.leftReleased();
        }
        if (keycode == Input.Keys.RIGHT)
        {
            controller.rightReleased();
            hud.rightReleased();
        }
        if (keycode == Input.Keys.SPACE)
        {
            controller.jumpReleased();
            hud.jumpReleased();
        }
        if (keycode == Input.Keys.X)
        {
            controller.fireReleased();
            hud.fireReleased();
        }
        if (keycode == Input.Keys.DOWN)
        {
            controller.downReleased();
            hud.downReleased();
        }
        if (keycode == Input.Keys.UP)
        {
            controller.upReleased();
            hud.upReleased();
        }
        if (keycode == Input.Keys.D)
            renderer.setDebug(!renderer.isDebug());
        return true;
    }

    @Override
    public boolean keyTyped(char character)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button)
    {
        update = true;
        System.out.println(x + " " + y);
        if (!Gdx.app.getType().equals(Application.ApplicationType.Android))
            return false;
        if (pointer < 5)
        {
            if (isTouchInBounds(CONTROL_CLICK_AREA.DPAD_RIGHT, x, y))//is right
            {
                controller.rightPressed();
                dPad.setClickedArea(DPad.CLICKED_AREA.RIGHT);
                touches.get(pointer).clickArea = CONTROL_CLICK_AREA.DPAD_RIGHT;
            }
            if (isTouchInBounds(CONTROL_CLICK_AREA.DPAD_LEFT, x, y))//is left
            {
                controller.leftPressed();
                dPad.setClickedArea(DPad.CLICKED_AREA.LEFT);
                touches.get(pointer).clickArea = CONTROL_CLICK_AREA.DPAD_LEFT;
            }
            if (isTouchInBounds(CONTROL_CLICK_AREA.DPAD_TOP, x, y))//is top
            {
                controller.upPressed();
                dPad.setClickedArea(DPad.CLICKED_AREA.TOP);
                touches.get(pointer).clickArea = CONTROL_CLICK_AREA.DPAD_TOP;
            }
            if (isTouchInBounds(CONTROL_CLICK_AREA.DPAD_BOTTOM, x, y))//is bottom
            {
                controller.downPressed();//not implemented yet
                dPad.setClickedArea(DPad.CLICKED_AREA.BOTTOM);
                touches.get(pointer).clickArea = CONTROL_CLICK_AREA.DPAD_BOTTOM;
            }
            if (jump.getBounds().contains(x, height - y))
            {
                controller.jumpPressed();
                jump.setClicked(true);
                touches.get(pointer).clickArea = CONTROL_CLICK_AREA.JUMP;
            }
        }

        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button)
    {
        if (!Gdx.app.getType().equals(Application.ApplicationType.Android))
            return false;
        TouchInfo ti = touches.get(pointer);
        if (ti != null)
        {
            switch (ti.clickArea)
            {
                case DPAD_RIGHT:
                    controller.rightReleased();
                    break;
                case DPAD_LEFT:
                    controller.leftReleased();
                    break;
                case DPAD_TOP:
                    controller.upReleased();
                    break;
                case DPAD_BOTTOM:
                    controller.downReleased();
                    break;
                case JUMP:
                    controller.jumpReleased();
                    jump.setClicked(false);
                    break;
            }
            touches.get(pointer).clickArea = CONTROL_CLICK_AREA.NONE;
            dPad.setClickedArea(DPad.CLICKED_AREA.NONE);
        }
        return true;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean isTouchInBounds(CONTROL_CLICK_AREA area, int x, int y)
    {
        float wh = dPad.getBounds().width;//width and height, they are the same
        float dPadXPos = dPad.getPosition().x;
        float dPadYPos = height - (dPad.getPosition().y + wh);

        float bX = wh / 2, bY = dPadYPos + wh / 2; //b is always center
        float aX = 0, aY = 0;
        float cX = 0, cY = 0;
        switch (area)
        {
            case DPAD_LEFT:
                aX = dPadXPos;
                aY = dPadYPos;
                cX = dPadXPos;
                cY = dPadYPos + wh;
                break;
            case DPAD_RIGHT:
                aX = dPadXPos + wh;
                aY = dPadYPos + wh;
                cX = dPadXPos + wh;
                cY = dPadYPos;
                break;
            case DPAD_BOTTOM:
                aX = dPadXPos;
                aY = dPadYPos + wh;
                cX = dPadXPos + wh;
                cY = dPadYPos + wh;
                break;
            case DPAD_TOP:
                aX = dPadXPos + wh;
                aY = dPadYPos;
                cX = dPadXPos;
                cY = dPadYPos;
                break;
        }

        // no need to divide by 2.0 here, since it is not necessary in the equation
        double ABC = Math.abs(aX * (bY - cY) + bX * (cY - aY) + cX * (aY - bY));
        double ABP = Math.abs(aX * (bY - y) + bX * (y - aY) + x * (aY - bY));
        double APC = Math.abs(aX * (y - cY) + x * (cY - aY) + cX * (aY - y));
        double PBC = Math.abs(x * (bY - cY) + bX * (cY - y) + cX * (y - bY));

        return ABP + APC + PBC == ABC;
    }

    public WorldWrapper getWorld()
    {
        return world;
    }

    public void setWorld(WorldWrapper world)
    {
        this.world = world;
    }

    public DPad getdPad()
    {
        return dPad;
    }

    public void setdPad(DPad dPad)
    {
        this.dPad = dPad;
    }

    public BtnJump getJump()
    {
        return jump;
    }

    public void setJump(BtnJump jump)
    {
        this.jump = jump;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    class TouchInfo
    {
        public float touchX = 0;
        public float touchY = 0;
        public boolean touched = false;
        CONTROL_CLICK_AREA clickArea = CONTROL_CLICK_AREA.NONE;
    }
}
