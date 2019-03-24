package rs.pedjaapps.smc;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Pool;

import java.util.Collections;

import de.golfgl.gdxgamesvcs.IGameServiceClient;
import de.golfgl.gdxgamesvcs.IGameServiceListener;
import de.golfgl.gdxgamesvcs.NoGameServiceClient;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.enemy.Enemy;
import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.object.maryo.Fireball;
import rs.pedjaapps.smc.object.maryo.Iceball;
import rs.pedjaapps.smc.screen.AbstractScreen;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.screen.LoadingScreen;
import rs.pedjaapps.smc.screen.MainMenuScreen;
import rs.pedjaapps.smc.shader.Shader;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.LevelLoader;
import rs.pedjaapps.smc.utility.MyControllerMapping;
import rs.pedjaapps.smc.utility.PrefsManager;

public class MaryoGame extends Game implements IGameServiceListener {
    public static final int NATIVE_WIDTH = 1024;
    public static final int NATIVE_HEIGHT = 576;

    public static final String GAME_VERSION = "2.0.0";
    public static final boolean GAME_DEVMODE = true;
    public static final String GAME_STOREURL = "https://play.google.com/store/apps/details?id=rs.pedjaapps.smc.android";
    public static final String GAME_WEBURL = "https://skynetspftware.org/smc";

    public static final String GAME_SOURCEURL = "https://github.com/pedja1/SMC-Android";

    // This is the rectangle pool used in collision detection
    // Good to avoid instantiation each frame
    public static Pool<Rectangle> RECT_POOL = new Pool<Rectangle>()
    {
        @Override
        protected Rectangle newObject()
        {
            return new Rectangle();
        }
    };

    public static Pool<Polygon> POLY_POOL = new Pool<Polygon>()
    {
        @Override
        protected Polygon newObject()
        {
            return new Polygon();
        }
    };

    public static Pool<Vector3> VECTOR3_POOL = new Pool<Vector3>()
    {
        @Override
        protected Vector3 newObject()
        {
            return new Vector3();
        }
    };

    public static Pool<Vector2> VECTOR2_POOL = new Pool<Vector2>()
    {
        @Override
        protected Vector2 newObject()
        {
            return new Vector2();
        }
    };

    public static Pool<Fireball> FIREBALL_POOL = new Pool<Fireball>()
    {
        @Override
        protected Fireball newObject()
        {
            Fireball fb = new Fireball(0, 0);
            fb.initAssets();
            return fb;
        }
    };

    public static Pool<Iceball> ICEBALL_POOL = new Pool<Iceball>()
    {
        @Override
        protected Iceball newObject()
        {
            Iceball fb = new Iceball(0, 0);
            fb.initAssets();
            return fb;
        }
    };

    public static MaryoGame game;

    public MyControllerMapping controllerMappings;
    public String isRunningOn = "";
    public IGameServiceClient gsClient;
    public IGameServiceClient gpgsClient;
    public Assets assets;
    public AbstractScreen currentScreen;
    private Event event;

    public MaryoGame(Event event) {
        this.event = event;
    }

    @Override
    public void create() {
        game = this;
        if (!GAME_DEVMODE)
            Gdx.app.setLogLevel(Application.LOG_ERROR);

        assets = new Assets();
        Shader.init();
        GameSave.init();

        assets.load(Assets.SKIN_HUD, Skin.class);

        try {
            controllerMappings = new MyControllerMapping();
            Controllers.addListener(controllerMappings.controllerToInputAdapter);
        } catch (Throwable t) {
            Gdx.app.error("Application", "Controllers not instantiated", t);
        }

        if (gsClient == null)
            gsClient = new NoGameServiceClient();
        gsClient.resumeSession();

        if (gpgsClient != null) {
            gpgsClient.setListener(this);
            gpgsClient.resumeSession();
            GameSave.cloudSaveClient = gpgsClient;
        }

        changeScreen(new LoadingScreen(new MainMenuScreen(), false));
    }

    @Override
    public void pause() {
        super.pause();
        // kann null sein wenn preloader versteckt wird
        if (Gdx.app != null) {
            PrefsManager.flush();
            gsClient.pauseSession();
            if (gpgsClient != null)
                gpgsClient.pauseSession();
        }
    }

    @Override
    public void resume() {
        super.resume();

        if (gsClient != null)
            gsClient.resumeSession();
        if (gpgsClient != null)
            gpgsClient.resumeSession();
    }

    @Override
    public void dispose() {
        super.dispose();
        assets.dispose();
        assets = null;
        Shader.dispose();
        game = null;
    }

    public void exit() {
        Gdx.app.exit();
    }

    public void levelStart(String levelName) {
        if (event != null)
            event.levelStart(levelName);

        gsClient.submitEvent(GameSave.EVENT_LEVEL_STARTED, 1);
        if (gpgsClient != null)
            gpgsClient.submitEvent(GameSave.EVENT_LEVEL_STARTED, 1);
    }

    public void levelEnd(String levelName, boolean success) {
        if (event != null)
            event.levelEnd(levelName, success);

        if (success) {
            gsClient.submitEvent(GameSave.EVENT_LEVEL_CLEARED, 1);
            gsClient.submitToLeaderboard(GameSave.LEADERBOARD_TOTAL, GameSave.getTotalScore(), null);
            if (gpgsClient != null) {
                gpgsClient.unlockAchievement(levelName + "_CLEAR");
                gpgsClient.submitEvent(GameSave.EVENT_LEVEL_CLEARED, 1);
                gpgsClient.submitToLeaderboard(GameSave.LEADERBOARD_TOTAL, GameSave.getTotalScore(), null);
            }
        }
    }

    @Override
    public void gsOnSessionActive() {
        GameSave.loadFromCloudIfApplicable();
    }

    @Override
    public void gsOnSessionInactive() {

    }

    @Override
    public void gsShowErrorToUser(GsErrorType et, String msg, Throwable t) {
        Gdx.app.error("GS", msg, t);
    }

    public void onChangedStateFromCloud() {
        // aus der Cloud wurde ein Spielstand geladen, der abweichend war. Wenn noch im Ladebildschirm, dann ist eh
        // alles super. Wenn schon auf Menü, dann benachrichtigen

        if (getScreen() instanceof MainMenuScreen)
            ((MainMenuScreen) getScreen()).onChangedStateFromCloud();
    }

    public void changeScreen(AbstractScreen screen) {
        super.setScreen(screen);
        if(screen instanceof LoadingScreen) {
            currentScreen = ((LoadingScreen) screen).getScreenToLoadAfter();
        } else {
            currentScreen = screen;
        }
    }

    public void sortLevel() {
        Collections.sort(currentScreen.world.level.gameObjects, new LevelLoader.ZSpriteComparator());
    }

    public void trashObject(GameObject object) {
        currentScreen.world.trashObjects.add(object);
    }

    public void addKillPoints(int killPoints, float positionX, float positionY) {
        if(currentScreen instanceof GameScreen) {
            ((GameScreen)currentScreen).killPointsTextHandler.add(killPoints, positionX, positionY);
        }
    }

    public void endLevel() {
        if(currentScreen instanceof GameScreen)
        ((GameScreen) currentScreen).endLevel();
    }

    public void setGameState(GameScreen.GAME_STATE gameState) {
        if(currentScreen instanceof GameScreen) {
            ((GameScreen) currentScreen).setGameState(gameState);
        }
    }

    public GameScreen.GAME_STATE getGameState() {
        if(currentScreen instanceof GameScreen) {
            return ((GameScreen) currentScreen).getGameState();
        }
        return null;
    }

    public void addObject(GameObject item) {
        currentScreen.world.level.gameObjects.add(item);
    }

    public interface Event {
        void levelStart(String levelName);

        void levelEnd(String levelName, boolean success);
    }
}
