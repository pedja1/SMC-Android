package rs.pedjaapps.smc.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;

import rs.pedjaapps.smc.MaryoGame;


public class SplashScreen implements Screen
{
    private float width, height;
    private Texture libgdxSplashTexture;
    private Sprite libgdxSplashSprite;
    private Texture gameSplashTexture;
    private Sprite gameSplashSprite;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    ShapeRenderer bgRenderer;

    MaryoGame marioGame;

    public SplashScreen(MaryoGame marioGame)
    {
        this.marioGame = marioGame;
    }

    private long splashStartTime;
    private static final long splashDuration = 2 * 1000;//2 sec
    int screenToDisplay = 0;

    @Override
    public void show()
    {

        splashStartTime = System.currentTimeMillis();
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(1, height/width);
        batch = new SpriteBatch();
        bgRenderer = new ShapeRenderer();

        libgdxSplashTexture = new Texture(Gdx.files.internal("data/game/logo/libgdx.jpg"));
        libgdxSplashTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        gameSplashTexture = new Texture(Gdx.files.internal("data/game/logo/smc_big_1.png"));
        gameSplashTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        libgdxSplashSprite = new Sprite( new TextureRegion(libgdxSplashTexture));
        libgdxSplashSprite.setSize(0.9f, 0.9f * libgdxSplashSprite.getHeight() / libgdxSplashSprite.getWidth());
        libgdxSplashSprite.setOrigin(libgdxSplashSprite.getWidth()/2, libgdxSplashSprite.getHeight()/2);
        libgdxSplashSprite.setPosition(-libgdxSplashSprite.getWidth()/2, -libgdxSplashSprite.getHeight()/2);


        gameSplashSprite = new Sprite(gameSplashTexture);
        gameSplashSprite.setSize(0.9f, 0.9f * gameSplashSprite.getHeight() / gameSplashSprite.getWidth());
        gameSplashSprite.setOrigin(gameSplashSprite.getWidth()/2, gameSplashSprite.getHeight()/2);
        gameSplashSprite.setPosition(-gameSplashSprite.getWidth()/2, -gameSplashSprite.getHeight()/2);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        bgRenderer.begin(ShapeRenderer.ShapeType.Filled);
        bgRenderer.setColor(new Color(255, 255, 255, 1));
        bgRenderer.rect(0, 0, width, height);
        bgRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if(screenToDisplay == 0)
        {
            libgdxSplashSprite.draw(batch);
        }
        else if(screenToDisplay == 1)
        {
            gameSplashSprite.draw(batch);
        }
        else
        {
            marioGame.setScreen(new LoadingScreen(new MainMenuScreen(marioGame), false));
        }

        batch.end();

        if(System.currentTimeMillis() > splashStartTime + splashDuration)
        {
            screenToDisplay++;
            splashStartTime = System.currentTimeMillis();
        }

    }

    @Override
    public void resize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    @Override
    public void hide()
    {
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
        batch.dispose();
        libgdxSplashTexture.dispose();
        gameSplashTexture.dispose();
        bgRenderer.dispose();
    }

}
