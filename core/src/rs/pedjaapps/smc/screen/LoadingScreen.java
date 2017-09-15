package rs.pedjaapps.smc.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.utility.PrefsManager;

/**
 * @author Mats Svensson
 */
public class LoadingScreen extends AbstractScreen {
    private final ProgressBar progressBar;
    private final Color color1;
    private final Color color2;
    private final ShapeRenderer renderer;
    private float percent;
    private AbstractScreen screenToLoadAfter;
    private boolean resume = false;

    public LoadingScreen(AbstractScreen screenToLoadAfter, boolean resume) {
        super(screenToLoadAfter.game);
        game.assets.manager.load(Assets.LOGO_GAME, Texture.class, game.assets.textureParameter);
        game.assets.manager.load(Assets.LOGO_LOADING, Texture.class, game.assets.textureParameter);
        game.assets.manager.finishLoading();

        this.screenToLoadAfter = screenToLoadAfter;
        this.resume = resume;

        Skin skin = game.assets.manager.get(Assets.SKIN_HUD, Skin.class);
        progressBar = new ProgressBar(0, 100, 1, false, skin);

        progressBar.setSize(stage.getWidth() * .75f, 30);
        progressBar.setPosition(stage.getWidth() / 2, 20, Align.bottom);

        stage.addActor(progressBar);

        Image imGameLogo = MainMenuScreen.createLogoImage(game);
        imGameLogo.setPosition(stage.getWidth() / 2, stage.getHeight() - 10f, Align.top);
        stage.addActor(imGameLogo);

        Label loading = new Label("Loading...", skin, "outline");
        loading.setPosition(stage.getWidth() / 2, progressBar.getY() + progressBar.getHeight() + 10, Align.bottom);
        loading.addAction(Actions.forever(Actions.sequence(Actions.alpha(.3f, 1f), Actions.fadeIn(1f))));
        stage.addActor(loading);

        Texture txtLoadingLogo = game.assets.manager.get(Assets.LOGO_LOADING, Texture.class);
        txtLoadingLogo.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image imLoadingLogo = new Image(txtLoadingLogo);
        imLoadingLogo.setSize(imLoadingLogo.getWidth() * .33f, imLoadingLogo.getHeight() * .33f);
        imLoadingLogo.setPosition(stage.getWidth() / 2, loading.getY() + loading.getHeight() + (imGameLogo.getY() -
                loading.getY() - loading.getHeight()) / 2, Align.center);
        stage.addActor(imLoadingLogo);

        color1 = new Color(.117f, 0.705f, .05f, 0f);
        color2 = new Color(0f, 0.392f, 0.039f, 0f);
        renderer = new ShapeRenderer();
    }

    @Override
    public void show() {
        screenToLoadAfter.loadAssets();
    }

    @Override
    public void render(float delta) {
        // Clear the screen
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

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setProjectionMatrix(stage.getCamera().combined);
        renderer.rect(0, 0, stage.getWidth(), stage.getHeight(), color2, color1, color1, color2);
        renderer.end();

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
        renderer.dispose();
        ;
        super.dispose();
    }
}
