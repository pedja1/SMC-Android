package rs.pedjaapps.smc.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.object.World;

/**
 * Created by Benjamin Schulte on 05.10.2017.
 */

public class GameScreenInput implements InputProcessor {
    private GameScreen gameScreen;
    private World world;

    GameScreenInput(GameScreen gameScreen, World world) {
        this.gameScreen = gameScreen;
        this.world = world;
    }

    @Override
    public boolean keyDown(int keycode) {
        gameScreen.hud.setHasKeyboardOrController(true);

        if (touchDown(0, 0, 0, 0))
            return true;

        GameScreen.GAME_STATE gameState = gameScreen.getGameState();

        if (gameState == GameScreen.GAME_STATE.GAME_PAUSED && keycode == Input.Keys.ENTER)
            gameScreen.setGameState(GameScreen.GAME_STATE.GAME_RUNNING);
        else if (gameState == GameScreen.GAME_STATE.GAME_PAUSED)
            return false;

        switch (keycode) {
            case Input.Keys.LEFT:
                world.maryo.leftPressed();
                break;

            case Input.Keys.RIGHT:
                world.maryo.rightPressed();
                break;

            case Input.Keys.SPACE:
                world.maryo.jumpPressed();
                break;

            case Input.Keys.ALT_LEFT:
            case Input.Keys.X:
                world.maryo.firePressed();
                break;

            case Input.Keys.DOWN:
                world.maryo.downPressed();
                break;

            case Input.Keys.UP:
                world.maryo.upPressed();
                break;
        }

        if (keycode == Input.Keys.F8 && MaryoGame.GAME_DEVMODE) {
            if (gameState == GameScreen.GAME_STATE.GAME_EDIT_MODE) {
                gameScreen.setGameState(GameScreen.GAME_STATE.GAME_RUNNING);
                gameScreen.cam.zoom = 1;
            } else {
                gameScreen.cameraEditModeTranslate.set(gameScreen.cam.position);
                gameScreen.setGameState(GameScreen.GAME_STATE.GAME_EDIT_MODE);
            }
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        GameScreen.GAME_STATE gameState = gameScreen.getGameState();

        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            if (gameState == GameScreen.GAME_STATE.GAME_PAUSED) {
                gameScreen.exitToMenu();
            } else {
                gameScreen.setGameState(GameScreen.GAME_STATE.GAME_PAUSED);
            }
            return true;
        }

        if (gameState == GameScreen.GAME_STATE.GAME_PAUSED)
            return false;

        switch (keycode) {
            case Input.Keys.LEFT:
                world.maryo.leftReleased();
                break;

            case Input.Keys.RIGHT:
                world.maryo.rightReleased();
                break;

            case Input.Keys.SPACE:
                world.maryo.jumpReleased();
                break;

            case Input.Keys.ALT_LEFT:
            case Input.Keys.X:
                world.maryo.fireReleased();
                break;

            case Input.Keys.DOWN:
                world.maryo.downReleased();
                break;

            case Input.Keys.UP:
                world.maryo.upReleased();
                break;
        }

        if (keycode == Input.Keys.D && MaryoGame.GAME_DEVMODE)
            gameScreen.debug = !gameScreen.debug;

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        GameScreen.GAME_STATE gameState = gameScreen.getGameState();

        if (gameState == GameScreen.GAME_STATE.GAME_READY)
            gameScreen.setGameState(GameScreen.GAME_STATE.GAME_RUNNING);
        else if (gameState == GameScreen.GAME_STATE.SHOW_BOX)
            gameScreen.discardBoxText();
        else if (gameState == GameScreen.GAME_STATE.PLAYER_DIED)
            gameScreen.goTouched = true;
        else
            return false;

        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if (gameScreen.getGameState() == GameScreen.GAME_STATE.GAME_EDIT_MODE || gameScreen.debug) {
            gameScreen.cam.zoom += amount * 0.1f;
            gameScreen.cam.update();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

}
