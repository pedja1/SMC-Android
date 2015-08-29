package rs.pedjaapps.smc.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import rs.pedjaapps.smc.MaryoGame;


public class SplashScreen implements Screen
{
    private float width, height;
    private Sprite libgdxSplashSprite;
    private Sprite gameSplashSprite;
    private Sprite afLogoSprite;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    MaryoGame marioGame;

    public SplashScreen(MaryoGame marioGame)
    {
        this.marioGame = marioGame;
    }

    private long splashStartTime;
    private static final long splashDuration = 1500;
    int screenToDisplay = 0;

    @Override
    public void show()
    {

        splashStartTime = System.currentTimeMillis();
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(1, height/width);
        batch = new SpriteBatch();

        Texture libgdxSplashTexture = new Texture(Gdx.files.internal("data/game/logo/libgdx.jpg"));
        libgdxSplashTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Texture gameSplashTexture = new Texture(Gdx.files.internal("data/game/logo/smc_big_1.png"));
        gameSplashTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Texture afLogoTexture = new Texture(Gdx.files.internal("data/game/logo/af_logo.png"));
        afLogoTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        libgdxSplashSprite = new Sprite( new TextureRegion(libgdxSplashTexture));
        libgdxSplashSprite.setSize(0.9f, 0.9f * libgdxSplashSprite.getHeight() / libgdxSplashSprite.getWidth());
        libgdxSplashSprite.setOrigin(libgdxSplashSprite.getWidth()/2, libgdxSplashSprite.getHeight()/2);
        libgdxSplashSprite.setPosition(-libgdxSplashSprite.getWidth()/2, -libgdxSplashSprite.getHeight()/2);

        gameSplashSprite = new Sprite(gameSplashTexture);
        gameSplashSprite.setSize(0.9f, 0.9f * gameSplashSprite.getHeight() / gameSplashSprite.getWidth());
        gameSplashSprite.setOrigin(gameSplashSprite.getWidth()/2, gameSplashSprite.getHeight()/2);
        gameSplashSprite.setPosition(-gameSplashSprite.getWidth()/2, -gameSplashSprite.getHeight()/2);

        afLogoSprite = new Sprite(afLogoTexture);
        afLogoSprite.setSize(0.9f, 0.9f * afLogoSprite.getHeight() / afLogoSprite.getWidth());
        afLogoSprite.setOrigin(afLogoSprite.getWidth()/2, afLogoSprite.getHeight()/2);
        afLogoSprite.setPosition(-afLogoSprite.getWidth()/2, -afLogoSprite.getHeight()/2);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if(screenToDisplay == 0)
        {
            libgdxSplashSprite.draw(batch);
        }
        else if(screenToDisplay == 1)
        {
            afLogoSprite.draw(batch);
        }
        else if(screenToDisplay == 2)
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
        libgdxSplashSprite.getTexture().dispose();
        gameSplashSprite.getTexture().dispose();
        batch = null;
        libgdxSplashSprite = null;
        gameSplashSprite = null;
    }

}
