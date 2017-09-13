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
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.PrefsManager;
import rs.pedjaapps.smc.view.Background;

/**
 * @author Mats Svensson
 */
public class LoadingScreen extends AbstractScreen {
    private float percent;

    private AbstractScreen screenToLoadAfter;
    private boolean resume = false;
    private final ProgressBar progressBar;

    public LoadingScreen(AbstractScreen screenToLoadAfter, boolean resume) {
        super(screenToLoadAfter.game);
        screenToLoadAfter.game.assets.manager.finishLoadingAsset(Assets.ASSET_HUDSKIN);

        this.screenToLoadAfter = screenToLoadAfter;
        this.resume = resume;

        progressBar = new ProgressBar(-5, 100, 1, false,
                screenToLoadAfter.game.assets.manager.get(Assets.ASSET_HUDSKIN, Skin.class));

        progressBar.setSize(stage.getWidth() * .75f, 30);
        progressBar.setPosition(stage.getWidth() / 2, stage.getHeight() / 4, Align.center);

        stage.addActor(progressBar);
    }

    @Override
    public void show() {
        screenToLoadAfter.loadAssets();
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(.117f, 0.705f, .05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (game.assets.manager.update()) {
            // Load some, will return true if done loading
            /*if(!resume)*/
            screenToLoadAfter.onAssetsLoaded();
            if (screenToLoadAfter instanceof GameScreen) {
                ((GameScreen) screenToLoadAfter).resumed = resume;
            }
            game.setScreen(screenToLoadAfter);
        }
        // Interpolate the percentage to make it more smooth
        percent = Interpolation.linear.apply(percent, game.assets.manager.getProgress(), 0.1f);

        progressBar.setValue(game.assets.manager.getProgress() * 100);

        stage.act();
        stage.draw();

        //backgroundColor.render(stage.getCamera(), stage.getSp));

        //async loading is just for show, since loading takes less than a second event for largest levels
        //if debug mode just load it all at once
        if (PrefsManager.isDebug())
            game.assets.manager.finishLoading();
    }

    @Override
    public void loadAssets() {
        //do nothing
    }

    @Override
    public void onAssetsLoaded() {
        //do nothing
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
