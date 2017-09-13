package rs.pedjaapps.smc.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

import rs.pedjaapps.smc.MaryoGame;

/**
 * @author Mats Svensson
 */
public abstract class AbstractScreen implements Screen {
    public MaryoGame game;
    protected Stage stage;


    public AbstractScreen(MaryoGame game) {
        this.game = game;
        stage = new Stage(new FitViewport(MaryoGame.NATIVE_WIDTH, MaryoGame.NATIVE_HEIGHT));
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void exitToMenu() {
        game.setScreen(new LoadingScreen(new MainMenuScreen(game), false));
    }

    public void quit() {
        game.exit();
    }

    /**
     * Override this method to add assets to loading queue using AssetManager.load()
     */
    public abstract void loadAssets();

    /**
     * Called after after all assets has been loaded, use it to find regions from atlases for example
     */
    public abstract void onAssetsLoaded();
}
