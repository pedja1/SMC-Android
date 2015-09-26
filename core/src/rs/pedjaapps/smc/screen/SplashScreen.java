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
    private static final long splashDuration = 2000;

    @Override
    public void show()
    {

        splashStartTime = System.currentTimeMillis();

        float width = 1;
        float height = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        batch = new SpriteBatch();

        Texture libgdxSplashTexture = new Texture(marioGame.assets.resolver.resolve("data/game/logo/libgdx.jpg"));
        libgdxSplashTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Texture gameSplashTexture = new Texture(marioGame.assets.resolver.resolve("data/game/logo/smc_big_1.png"));
        gameSplashTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Texture afLogoTexture = new Texture(marioGame.assets.resolver.resolve("data/game/logo/af_logo.png"));
        afLogoTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        libgdxSplashSprite = new Sprite( new TextureRegion(libgdxSplashTexture));
        libgdxSplashSprite.setSize(width * 0.4f, (width * 0.4f) * (libgdxSplashSprite.getHeight() / libgdxSplashSprite.getWidth()));
        libgdxSplashSprite.setPosition(width * .75f - libgdxSplashSprite.getWidth() * .5f, height * .25f - libgdxSplashSprite.getHeight() * .5f);

        gameSplashSprite = new Sprite(gameSplashTexture);
        gameSplashSprite.setSize(width * 0.7f, (width * 0.7f) * (gameSplashSprite.getHeight() / gameSplashSprite.getWidth()));
        gameSplashSprite.setPosition(width * .5f - gameSplashSprite.getWidth() * .5f, height * .5f + height * 0.05f);

        afLogoSprite = new Sprite(afLogoTexture);
        afLogoSprite.setSize(width * 0.4f, (width * 0.4f) * (afLogoSprite.getHeight() / afLogoSprite.getWidth()));
        afLogoSprite.setPosition(width * .25f - afLogoSprite.getWidth() * .5f, height * .25f - afLogoSprite.getHeight() * .5f);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        libgdxSplashSprite.draw(batch);
        afLogoSprite.draw(batch);
        gameSplashSprite.draw(batch);

        batch.end();

        if(System.currentTimeMillis() > splashStartTime + splashDuration)
        {
            marioGame.setScreen(new LoadingScreen(new MainMenuScreen(marioGame), false));
        }

    }

    @Override
    public void resize(int width, int height)
    {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1, (float)height / (float)width);
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
