package rs.pedjaapps.smc.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import rs.pedjaapps.smc.screen.AbstractScreen;
import rs.pedjaapps.smc.shader.Shader;

/**
 * Created by pedja on 9/10/16.
 */

public class MPMatchmakingView
{
    private static final float SPINNER_SIZE = 1;
    private static final float CAM_WIDTH = 16;
    private static final float ASPECT = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 0.7f);

    private AbstractScreen mScreen;
    private Animation<TextureRegion> mAnimation;
    private OrthographicCamera mCamera;

    private float stateTime;

    private float posX, posY;

    private ShapeRenderer mShapeRender;

    private boolean visible;

    public MPMatchmakingView(AbstractScreen screen)
    {
        this.mScreen = screen;
        mCamera = new OrthographicCamera(CAM_WIDTH, CAM_WIDTH / ASPECT);
        mCamera.setToOrtho(false, mCamera.viewportWidth, mCamera.viewportHeight);
        mCamera.position.set(mCamera.viewportWidth * .5f, mCamera.viewportHeight * .5f, 0);
        mCamera.update();

        posX = CAM_WIDTH * 0.5f - SPINNER_SIZE * 0.5f;
        posY = mCamera.viewportHeight * 0.5f - SPINNER_SIZE * 0.5f;

        mShapeRender = new ShapeRenderer();
    }

    public void onAssetsLoaded()
    {
        if(mAnimation == null)
        {
            TextureAtlas ta = mScreen.game.assets.manager.get("data/animation/spinner.atlas");
            mAnimation = new Animation<TextureRegion>(0.05f, ta.getRegions(), Animation.PlayMode.LOOP);
        }
    }

    public void loadAssets()
    {
        mScreen.game.assets.manager.load("data/animation/spinner.atlas", TextureAtlas.class);
    }

    public void render(SpriteBatch batch, float delta)
    {
        if(!visible)
            return;
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        mShapeRender.begin(ShapeRenderer.ShapeType.Filled);
        mShapeRender.setColor(BACKGROUND_COLOR);
        mShapeRender.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        mShapeRender.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.setProjectionMatrix(mCamera.combined);
        batch.enableBlending();
        batch.setShader(Shader.NORMAL_BLEND_SHADER);
        batch.begin();

        batch.draw(mAnimation.getKeyFrame(stateTime), posX, posY, SPINNER_SIZE, SPINNER_SIZE);

        batch.end();
        batch.setShader(null);

        stateTime += delta;
    }

    public void show()
    {
        visible = true;
    }

    public void hide()
    {
        visible = false;
    }
}
