package rs.pedjaapps.smc.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.viewport.FitViewport;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.view.MenuStage;

/**
 * @author Mats Svensson
 */
public abstract class AbstractScreen implements Screen {
    protected MenuStage stage;
    public World world;

    public AbstractScreen() {
        stage = new MenuStage(new FitViewport(MaryoGame.NATIVE_WIDTH, MaryoGame.NATIVE_HEIGHT));
        world = new World();
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
        //TODO direkt in Levelauswahl springen bei Gamescreen
        MaryoGame.game.changeScreen(new LoadingScreen(new MainMenuScreen(), false));
    }

    public void quit() {
        MaryoGame.game.exit();
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
