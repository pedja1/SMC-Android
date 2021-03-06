package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import de.golfgl.gdxgamesvcs.IGameServiceClient;
import de.golfgl.gdxgamesvcs.gamestate.ILoadGameStateResponseListener;
import de.golfgl.gdxgamesvcs.gamestate.ISaveGameStateResponseListener;
import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.screen.AbstractScreen;

public class GameSave {
    public static final String LEADERBOARD_TOTAL = "TOTAL_SCORE";
    public static final String EVENT_LEVEL_STARTED = "EVENT_LEVEL_STARTED";
    public static final String EVENT_LEVEL_CLEARED = "EVENT_LEVEL_CLEARED";
    public static final String CLOUD_FILE_NAME = "gamestate";
    public static IGameServiceClient cloudSaveClient;
    // der aktuelle Stand, der gerade gespielt wird
    private static int levelScore;
    private static long levelPlaytime;
    private static int levelStartedNum;
    private static int maryoState;
    private static int persistentMaryoState;
    private static int lifes;
    private static long totalPlaytime;
    private static int coins;
    private static int item;
    private static int persistentItem;
    private static int totalScore;
    private static int bestTotal;
    private static int gameOverNum;
    //cloud save
    private static boolean loadedFromCloud;
    private static boolean loadingFromCloud;

    public static boolean isLoadingFromCloud() {
        return loadingFromCloud || cloudSaveClient != null && cloudSaveClient.isConnectionPending();
    }

    public static long getLevelPlaytime() {
        return levelPlaytime;
    }

    public static void addLevelPlaytime(long timeToAdd) {
        levelPlaytime += timeToAdd;
        totalPlaytime += timeToAdd;
    }

    public static void init() {
        // gibt es bereits einen gespeicherten Stand?
        boolean didRead = false;
        try {
            String savedGame = PrefsManager.getSaveGame();
            if (savedGame != null) {
                readFromJson(savedGame);
                didRead = true;
            }
        } catch (Throwable t) {
            Gdx.app.error("GameSave", "Error loading saved state", t);
        }

        if (!didRead || lifes <= 0)
            resetGameOver();
    }

    /**
     * read saved game, if gameovernum and levelstarts are greater than current state
     *
     * @param savedgame json
     * @return true if savegame was read, false otherwise
     */
    private static boolean readFromJson(String savedgame) {
        JsonValue savegame = new JsonReader().parse(savedgame);
        int readLevelStartedNum = savegame.getInt("levelstarts", 0);
        int readGameOverNum = savegame.getInt("gameovers", 1);

        boolean readStateIsNewer = (readGameOverNum > gameOverNum
                || readGameOverNum == gameOverNum && readLevelStartedNum > levelStartedNum);

        if (readStateIsNewer) {
            lifes = savegame.getInt("lifes");
            coins = savegame.getInt("coins");
            persistentItem = savegame.getInt("item");
            persistentMaryoState = savegame.getInt("state");
            totalPlaytime = savegame.getLong("playtime");
            levelStartedNum = readLevelStartedNum;
            gameOverNum = readGameOverNum;
            item = persistentItem;
            maryoState = persistentMaryoState;
        }

        // following
        bestTotal = Math.max(bestTotal, savegame.getInt("bestTotal", 0));

        JsonValue levelList = savegame.get("levels");
        for (JsonValue jsonlevel = levelList.child; jsonlevel != null; jsonlevel = jsonlevel.next) {
            String levelId = jsonlevel.getString("id");
            Level level = Level.getLevel(levelId);
            if (level != null) {
                if (readStateIsNewer)
                    level.currentScore = jsonlevel.getInt("score");
                level.bestScore = Math.max(level.bestScore, jsonlevel.getInt("best"));
            }
        }

        recalcTotalScore();

        return readStateIsNewer;
    }

    private static JsonValue toJson() {
        JsonValue json = new JsonValue(JsonValue.ValueType.object);
        json.addChild("lifes", new JsonValue(lifes));
        json.addChild("coins", new JsonValue(coins));
        json.addChild("item", new JsonValue(persistentItem));
        json.addChild("state", new JsonValue(persistentMaryoState));
        json.addChild("playtime", new JsonValue(totalPlaytime));
        json.addChild("levelstarts", new JsonValue(levelStartedNum));
        json.addChild("gameovers", new JsonValue(gameOverNum));
        json.addChild("bestTotal", new JsonValue(bestTotal));

        JsonValue levelArray = new JsonValue(JsonValue.ValueType.array);
        for (String levelId : Level.getLevelList()) {
            Level level = Level.getLevel(levelId);

            if (level.bestScore > 0) {
                JsonValue levelJson = new JsonValue(JsonValue.ValueType.object);
                levelJson.addChild("id", new JsonValue(levelId));
                levelJson.addChild("score", new JsonValue(level.currentScore));
                levelJson.addChild("best", new JsonValue(level.bestScore));
                levelArray.addChild(levelJson);
            }
        }
        json.addChild("levels", levelArray);

        return json;
    }

    public static void resetGameOver() {
        lifes = 4;
        item = 0;
        coins = 0;
        persistentMaryoState = 0;
        maryoState = 0;
        totalPlaytime = 0;
        totalScore = 0;
        gameOverNum++;

        for (String levelId : Level.getLevelList()) {
            Level level = Level.getLevel(levelId);

            level.currentScore = 0;
        }
        save();
    }

    private static void save() {
        save(null);
    }

    public static void save(ISaveGameStateResponseListener cloudResponseListener) {
        JsonValue json = toJson();
        String jsonString = json.toJson(JsonWriter.OutputType.json);
        PrefsManager.setSaveGame(jsonString);

        if (cloudSaveClient != null && loadedFromCloud)
            cloudSaveClient.saveGameState(CLOUD_FILE_NAME,
                    Utility.encode(jsonString, PrefsManager.SCCPLF).getBytes(),
                    levelStartedNum, cloudResponseListener);
    }

    public static void addCoins(AbstractScreen screen, int addCoins) {
        coins += addCoins;
        if (coins >= 100) {
            coins -= 100;
            lifes++;
            AssetManager manager = screen.game.assets.manager;
            if (manager.isLoaded(Assets.SOUND_ITEM_LIVE_UP_2))
                SoundManager.play(manager.get(Assets.SOUND_ITEM_LIVE_UP_2, Sound.class));
        }
    }

    public static int getCoins() {
        return coins;
    }

    public static int getItem() {
        return item;
    }

    public static void setItem(int itemType) {
        item = itemType;
    }

    public static Maryo.MaryoState getMaryoState() {
        return Maryo.MaryoState.fromInt(maryoState);
    }

    public static void setMaryoState(Maryo.MaryoState newState) {
        maryoState = Maryo.MaryoState.toInt(newState);
    }

    public static Maryo.MaryoState getPersistentMaryoState() {
        return Maryo.MaryoState.fromInt(persistentMaryoState);
    }

    public static int getPersistentItem() {
        return persistentItem;
    }

    public static int getLifes() {
        return lifes;
    }

    public static void addScore(int score) {
        levelScore += score;
    }

    public static void addLifes(int addedLifes) {
        lifes += addedLifes;
    }

    public static int getScore() {
        return levelScore;
    }

    public static int getTotalScore() {
        return totalScore;
    }

    /**
     * Starten eines Spiels aus dem Menü heraus
     */
    public static boolean startLevelFresh() {
        item = persistentItem;
        maryoState = persistentMaryoState;
        persistentMaryoState = 0;
        persistentItem = 0;
        levelPlaytime = 0;
        lifes--;
        levelScore = 0;
        levelStartedNum++;
        save();
        return lifes >= 0;
    }

    /**
     * Level erfolgreich beendet => Punkte etc übernehmen
     */
    public static void levelCleared(String levelName) {
        persistentItem = item;
        persistentMaryoState = maryoState;
        lifes++;

        Level level = Level.getLevel(levelName);
        level.currentScore = levelScore;
        if (level.currentScore > level.bestScore)
            level.bestScore = levelScore;

        recalcTotalScore();
        save();
    }

    private static void recalcTotalScore() {
        totalScore = 0;

        for (String levelId : Level.getLevelList()) {
            Level level = Level.getLevel(levelId);

            totalScore += level.currentScore;
        }

        if (totalScore > bestTotal)
            bestTotal = totalScore;
    }

    public static void loadFromCloudIfApplicable(final MaryoGame game) {
        if (!loadedFromCloud && !loadingFromCloud && cloudSaveClient != null && cloudSaveClient.isSessionActive()
                && cloudSaveClient.isFeatureSupported(IGameServiceClient.GameServiceFeature.GameStateStorage)) {
            loadingFromCloud = true;
            cloudSaveClient.loadGameState(GameSave.CLOUD_FILE_NAME, new ILoadGameStateResponseListener() {
                @Override
                public void gsGameStateLoaded(final byte[] gameState) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            // wenn abgebrochen wurde, dann nicht mehr laden
                            if (loadedFromCloud)
                                return;

                            loadingFromCloud = false;
                            loadedFromCloud = true;

                            try {
                                String json = Utility.decode(new String(gameState), PrefsManager.SCCPLF);
                                boolean madeChanges = false;
                                if (json != null)
                                    madeChanges = readFromJson(json);

                                if (madeChanges)
                                    game.onChangedStateFromCloud();
                            } catch (Throwable t) {
                                // eat
                            }
                        }
                    });
                }
            });
        }
    }

    public static void resetLoadedFromCloud() {
        loadingFromCloud = false;
        loadedFromCloud = false;
    }
}
