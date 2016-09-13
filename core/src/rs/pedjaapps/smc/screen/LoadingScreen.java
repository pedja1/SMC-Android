package rs.pedjaapps.smc.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Align;
import com.esotericsoftware.kryonet.Connection;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.PrefsManager;
import rs.pedjaapps.smc.view.ConfirmDialog;

/**
 * @author Mats Svensson
 */
public class LoadingScreen extends AbstractScreen
{
    private float percent;

    private BitmapFont font;
    private SpriteBatch batch;
    private OrthographicCamera cam;

    private Sprite bgSprite;

    private AbstractScreen screenToLoadAfter;
    private boolean resume = false;

    private boolean connectToServer;

    private boolean connected, connectionFailed;

    private ConfirmDialog confirmDialog;

    MaryoGame.ConnectionListener mConnectionListener;

    OrthographicCamera dialogCam;

    public LoadingScreen(AbstractScreen screenToLoadAfter, boolean resume, boolean connectToServer)
    {
        super(screenToLoadAfter.game);
        this.screenToLoadAfter = screenToLoadAfter;
        this.resume = resume;
        this.connectToServer = connectToServer;
        if (connectToServer)
        {
            mConnectionListener = new MaryoGame.ConnectionListener()
            {
                @Override
                public void connected(Connection connection)
                {
                    connected = true;
                }

                @Override
                public void connectionFailed()
                {
                    connectionFailed = true;
                }
            };
            game.addConnectionListener(mConnectionListener);
            game.connectToServer();
        }
    }

    @Override
    public void show()
    {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        float camWidth = 800;
        float camHeight = height / (width / camWidth);
        cam = new OrthographicCamera(camWidth, camHeight);

        dialogCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        dialogCam.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
        dialogCam.update();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(game.assets.resolver.resolve(Constants.DEFAULT_FONT_FILE_NAME));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = (int) camHeight / 20;
        fontParams.magFilter = Texture.TextureFilter.Linear;
        fontParams.minFilter = Texture.TextureFilter.Linear;
        if (connectToServer)
        {
            fontParams.characters = "Conectigplasw.";
        }
        else
        {
            fontParams.characters = "Loading,pleswt.0123456789";
        }
        font = generator.generateFont(fontParams);
        font.setColor(new Color(1, 1, 1, 0.75f));

        generator.dispose();

        batch = new SpriteBatch();
        Texture bgTexture = new Texture(game.assets.resolver.resolve("data/loading/loading_bg.jpg"));
        bgTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        bgSprite = new Sprite(bgTexture);
        bgSprite.setSize(camWidth, bgSprite.getHeight() / (bgSprite.getWidth() / camWidth));
        bgSprite.setOrigin(bgSprite.getWidth() / 2, bgSprite.getHeight() / 2);
        bgSprite.setPosition(-bgSprite.getWidth() / 2, -camHeight / 2);
        screenToLoadAfter.loadAssets();

        confirmDialog = new ConfirmDialog(this, dialogCam);
        loadAssets();//confirm dialog
    }

    @Override
    public void resize(int width, int height)
    {

    }

    @Override
    public void render(float delta)
    {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!confirmDialog.visible && game.assets.manager.update() && (!connectToServer || ((connected || connectionFailed))))
        {
            onAssetsLoaded();//for confirm dialog
            if (connectionFailed)
            {
                confirmDialog.show();
            }
            else
            {
                // Load some, will return true if done loading
            /*if(!resume)*/
                screenToLoadAfter.onAssetsLoaded();
                if (screenToLoadAfter instanceof GameScreen)
                {
                    ((GameScreen) screenToLoadAfter).resumed = resume;
                }
                game.setScreen(screenToLoadAfter);
            }
        }

        // Interpolate the percentage to make it more smooth
        percent = Interpolation.linear.apply(percent, game.assets.manager.getProgress(), 0.1f);

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        bgSprite.draw(batch);

        if (connectToServer)
        {
            font.draw(batch, "Connecting, please wait...", -cam.viewportWidth / 2 + (0.07f * cam.viewportWidth), -cam.viewportHeight / 2 + (0.08f * cam.viewportHeight), 0, Align.left, true);
        }
        else
        {
            font.draw(batch, "Loading, please wait... " + (int) (percent * 100) + "%", -cam.viewportWidth / 2 + (0.07f * cam.viewportWidth), -cam.viewportHeight / 2 + (0.08f * cam.viewportHeight), 0, Align.left, true);
        }

        batch.end();

        batch.setProjectionMatrix(dialogCam.combined);
        confirmDialog.render(batch);

        //async loading is just for show, since loading takes less than a second event for largest levels
        //if debug mode just load it all at once
        if (PrefsManager.isDebug())
            game.assets.manager.finishLoading();
    }

    @Override
    public void hide()
    {
        // Dispose the loading assets as we no longer need them
        //atlas.dispose();
        //stage.dispose();
    }

    @Override
    public void loadAssets()
    {
        confirmDialog.loadAssets();
    }

    @Override
    public void onAssetsLoaded()
    {
        confirmDialog.initAssets();
    }

    @Override
    public void dispose()
    {
        font.dispose();
        batch.dispose();
        bgSprite.getTexture().dispose();
        //empty.getTexture().dispose();
        //full.getTexture().dispose();
        if(mConnectionListener != null)game.removeConnectionListener(mConnectionListener);
    }
}
