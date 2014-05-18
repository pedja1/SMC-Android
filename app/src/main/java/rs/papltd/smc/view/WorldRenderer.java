package rs.papltd.smc.view;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.*;
import rs.papltd.smc.*;
import rs.papltd.smc.model.*;
import rs.papltd.smc.screen.*;
import rs.papltd.smc.utility.*;

import rs.papltd.smc.model.Sprite;

public class WorldRenderer
{

    private WorldWrapper world;
    private OrthographicCamera cam;
    private OrthographicCamera pCamera;
    private OrthographicCamera guiCam;
	private OrthographicCamera bgCam;

    /**
     * for debug rendering *
     */
    ShapeRenderer bgRenderer = new ShapeRenderer();

    /**
     * Textures *
     */
    private ParticleEffect leafEffect;

    private SpriteBatch spriteBatch;
    private boolean debug = false;
    private int width;
    private int height;

    private BitmapFont debugFont;

    public void setSize(int w, int h)
    {
        this.width = w;
        this.height = h;
    }

    public boolean isDebug()
    {
        return debug;
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    public WorldRenderer(GameScreen gameScreen)
    {
        this.world = gameScreen.getWorldWrapper();
        this.width = gameScreen.getWidth();
        this.height = gameScreen.getHeight();
        this.cam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        this.cam.setToOrtho(false,Constants.CAMERA_WIDTH,Constants.CAMERA_HEIGHT);
        //this.cam.position.set(world.getMario().getPosition().x, world.getMario().getPosition().y, 0);
        this.cam.update();

        pCamera = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        this.cam.setToOrtho(false,Constants.CAMERA_WIDTH,Constants.CAMERA_HEIGHT);
        pCamera.position.set(Constants.CAMERA_WIDTH / 2f, Constants.CAMERA_HEIGHT / 2f, 0);
        pCamera.update();

        guiCam = new OrthographicCamera(width, height);
        guiCam.position.set(width / 2f, height / 2f, 0);
        guiCam.update();
		
		bgCam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        bgCam.setToOrtho(false,Constants.CAMERA_WIDTH,Constants.CAMERA_HEIGHT);
        bgCam.position.set(cam.position.x, cam.position.y, 0);
        bgCam.update();

        spriteBatch = new SpriteBatch();
        debugFont = new BitmapFont();
        debugFont.setColor(Color.RED);
        debugFont.setScale(1.3f);

        BitmapFont guiFont = new BitmapFont(Gdx.files.absolute(Assets.mountedObbPath + "/fonts/default.fnt"));
        guiFont.setColor(Color.WHITE);
        guiFont.setScale(1f);

        BitmapFont guiFontBold = new BitmapFont(Gdx.files.absolute(Assets.mountedObbPath + "/fonts/default.fnt"));
        guiFontBold.setColor(Color.WHITE);
        guiFontBold.setScale(1f);

        loadTextures();
    }

    private void loadTextures()
    {
        leafEffect = new ParticleEffect();
        leafEffect.load(Gdx.files.absolute(Assets.mountedObbPath + "/animation/particles/leaf_emitter.p"), Gdx.files.absolute(Assets.mountedObbPath + "/animation/particles"));
        leafEffect.setPosition(Constants.CAMERA_WIDTH / 2, Constants.CAMERA_HEIGHT);
        leafEffect.start();
    }

    public void render(float delta)
    {
        moveCamera(cam, world.getMario().getBody().getPosition().x, world.getMario().getBody().getPosition().y);
        drawBackground();
        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        drawSprites(false);
        world.getMario().render(spriteBatch);
        drawSprites(true);
        spriteBatch.end();

        spriteBatch.setProjectionMatrix(pCamera.combined);
        spriteBatch.begin();
        leafEffect.draw(spriteBatch, delta);
        spriteBatch.end();

        spriteBatch.setProjectionMatrix(guiCam.combined);
        spriteBatch.begin();
        leafEffect.draw(spriteBatch, delta);
        if(debug)drawDebugText();
        drawHud();
        spriteBatch.end();
		if(debug)world.getDebugRenderer().render(world.getWorld(), cam.combined);
		world.getWorld().step(Gdx.graphics.getDeltaTime(), 6, 2);
    }

	private void drawBackground()
	{
        BackgroundColor bgColor = world.getLevel().getBgColor();
        bgColor.render(bgCam);
        bgCam.position.set(cam.position.x * Constants.BACKGROUND_SCROLL_SPEED + cam.viewportWidth * 0.44f,
                cam.position.y * Constants.BACKGROUND_SCROLL_SPEED + cam.viewportHeight * 0.44f, 0);
        bgCam.update();
        spriteBatch.setProjectionMatrix(bgCam.combined);
        spriteBatch.begin();
		world.getLevel().getBg1().render(spriteBatch);
		world.getLevel().getBg2().render(spriteBatch);
        spriteBatch.end();
	}

    public void moveCamera(OrthographicCamera cam, float x, float y)
    {
        cam.position.set(x, y, 0);
        cam.update();
        keepCameraInBounds(cam);
    }

    private void keepCameraInBounds(OrthographicCamera cam)
    {
        float camX = cam.position.x;
        float camY = cam.position.y;

        Vector2 camMin = new Vector2(cam.viewportWidth, cam.viewportHeight);
        camMin.scl(cam.zoom/2); //bring to center and scale by the zoom level
        Vector2 camMax = new Vector2(world.getLevel().getWidth(), world.getLevel().getHeight());
        camMax.sub(camMin); //bring to center

        //keep camera within borders
        camX = Math.min(camMax.x, Math.max(camX, camMin.x));
        camY = Math.min(camMax.y, Math.max(camY, camMin.y));

        cam.position.set(camX, camY, cam.position.z);
        cam.update();
    }

    private void drawSprites(boolean front)
    {
        for (Sprite sprite : world.getDrawableSprites(cam.position.x, cam.position.y, front))
        {
            TextureRegion region = Assets.loadedRegions.get(sprite.getTextureName());
            //spriteBatch.draw(region, sprite.getPosition().x, sprite.getPosition().y, sprite.getBounds().width, sprite.getBounds().height);
            Utility.draw(spriteBatch, region, sprite.getPosition().x, sprite.getPosition().y, sprite.getBounds().height);
        }
    }

    private void drawHud()
    {
        /*guiFont.drawMultiLine(spriteBatch, "Points 00000000", 40, height - 20);
        if(dPad.getClickedArea() == DPad.CLICKED_AREA.NONE)
        {
            spriteBatch.draw(dPadTexture, dPad.getPosition().x, dPad.getPosition().y, dPad.getBounds().width, dPad.getBounds().height);
        }
        else if(dPad.getClickedArea() == DPad.CLICKED_AREA.RIGHT)
        {
            spriteBatch.draw(dPadRightTexture, dPad.getPosition().x, dPad.getPosition().y, dPad.getBounds().width, dPad.getBounds().height);
        }
        else if(dPad.getClickedArea() == DPad.CLICKED_AREA.LEFT)
        {
            spriteBatch.draw(dPadLeftTexture, dPad.getPosition().x, dPad.getPosition().y, dPad.getBounds().width, dPad.getBounds().height);
        }
        else if(dPad.getClickedArea() == DPad.CLICKED_AREA.BOTTOM)
        {
            spriteBatch.draw(dPadBottomTexture, dPad.getPosition().x, dPad.getPosition().y, dPad.getBounds().width, dPad.getBounds().height);
        }
        else if(dPad.getClickedArea() == DPad.CLICKED_AREA.TOP)
        {
            spriteBatch.draw(dPadTopTexture, dPad.getPosition().x, dPad.getPosition().y, dPad.getBounds().width, dPad.getBounds().height);
        }
        if(btnJump.isClicked())
        {
            spriteBatch.draw(btnJumpTextureSelected, btnJump.getPosition().x, btnJump.getPosition().y, btnJump.getBounds().width, btnJump.getBounds().height);
        }
        else
        {
            spriteBatch.draw(btnJumpTexture, btnJump.getPosition().x, btnJump.getPosition().y, btnJump.getBounds().width, btnJump.getBounds().height);
        }*/
        //System.out.println(dPad.getPosition().x + " " + dPad.getPosition().y + " " + dPad.getBounds().height);
    }

    private void drawDebugText()
    {
        String debugMessage = generateDebugMessage();
        BitmapFont.TextBounds tb = debugFont.getBounds(debugMessage);
        debugFont.drawMultiLine(spriteBatch, debugMessage, 20, height - 20);
    }

    private String generateDebugMessage()
    {
        return "Level: width=" + world.getLevel().getWidth() + ", height=" + world.getLevel().getHeight()
			+ "\n" + "Player: x=" + world.getMario().getBody().getPosition().x + ", y=" + world.getMario().getBody().getPosition().y
            + "\n" + "World Camera: x=" + cam.position.x + ", y=" + cam.position.y
            + "\n" + "BG Camera: x=" + bgCam.position.x + ", y=" + bgCam.position.y
            + "\n" + "FPS: " + Gdx.graphics.getFramesPerSecond();
    }
}
