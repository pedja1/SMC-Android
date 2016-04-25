package rs.pedjaapps.smc.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;


public class SplashScreen implements Screen
{
    private float width, height;
    private Sprite libgdxSplashSprite;
    private Sprite robovmSprite;
    private Sprite lwjglSprite;
    private Sprite afLogoSprite;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private MaryoGame marioGame;

    public SplashScreen(MaryoGame marioGame)
    {
        this.marioGame = marioGame;
    }

    private long splashStartTime;
    private static final long splashDuration = 1200;
    private int screenToDisplay = 0;

    @Override
    public void show()
    {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(1, height / width);
        batch = new SpriteBatch();


        Texture libgdxSplashTexture = new Texture(Assets.resolver.resolve("data/logo/libgdx_logo.png"));
        libgdxSplashTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Texture robovmTexture = new Texture(Assets.resolver.resolve("data/logo/robovm_logo.png"));
        robovmTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Texture lvjglTexture = new Texture(Assets.resolver.resolve("data/logo/lwjgl_logo.png"));
        lvjglTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Texture afLogoTexture = new Texture(Assets.resolver.resolve("data/logo/af_logo.png"));
        afLogoTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        libgdxSplashSprite = new Sprite(libgdxSplashTexture);
        libgdxSplashSprite.setSize(0.9f, 0.9f * libgdxSplashSprite.getHeight() / libgdxSplashSprite.getWidth());
        libgdxSplashSprite.setOrigin(libgdxSplashSprite.getWidth() / 2, libgdxSplashSprite.getHeight() / 2);
        libgdxSplashSprite.setPosition(-libgdxSplashSprite.getWidth() / 2, -libgdxSplashSprite.getHeight() / 2);

        robovmSprite = new Sprite(robovmTexture);
        robovmSprite.setSize(0.9f, 0.9f * robovmSprite.getHeight() / robovmSprite.getWidth());
        robovmSprite.setOrigin(robovmSprite.getWidth() / 2, robovmSprite.getHeight() / 2);
        robovmSprite.setPosition(-robovmSprite.getWidth() / 2, -robovmSprite.getHeight() / 2);

        afLogoSprite = new Sprite(afLogoTexture);
        afLogoSprite.setSize(0.9f, 0.9f * afLogoSprite.getHeight() / afLogoSprite.getWidth());
        afLogoSprite.setOrigin(afLogoSprite.getWidth() / 2, afLogoSprite.getHeight() / 2);
        afLogoSprite.setPosition(-afLogoSprite.getWidth() / 2, -afLogoSprite.getHeight() / 2);

        lwjglSprite = new Sprite(lvjglTexture);
        lwjglSprite.setSize(0.9f, 0.9f * lwjglSprite.getHeight() / lwjglSprite.getWidth());
        lwjglSprite.setOrigin(lwjglSprite.getWidth() / 2, afLogoSprite.getHeight() / 2);
        lwjglSprite.setPosition(-lwjglSprite.getWidth() / 2, -lwjglSprite.getHeight() / 2);

        splashStartTime = System.currentTimeMillis();
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (screenToDisplay == 0)
        {
            libgdxSplashSprite.draw(batch);
        }
        else if (screenToDisplay == 1)
        {
            lwjglSprite.draw(batch);
        }
        else if (screenToDisplay == 2)
        {
            robovmSprite.draw(batch);
        }
        else if (screenToDisplay == 3)
        {
            afLogoSprite.draw(batch);
        }
        else
        {
            //marioGame.setScreen(new LoadingScreen(new MainMenuScreen(marioGame), false));
            marioGame.setScreen(new LoadingScreen(new GameScreen(marioGame), false));
        }

        batch.end();

        if (System.currentTimeMillis() > splashStartTime + splashDuration)
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

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void dispose()
    {
        batch.dispose();
        libgdxSplashSprite.getTexture().dispose();
        robovmSprite.getTexture().dispose();
        lwjglSprite.getTexture().dispose();
        batch = null;
        libgdxSplashSprite = null;
        robovmSprite = null;
        lwjglSprite = null;
    }

}
