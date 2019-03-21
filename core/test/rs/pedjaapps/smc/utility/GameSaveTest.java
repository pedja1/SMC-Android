package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import rs.pedjaapps.smc.object.maryo.Maryo;

import static org.junit.Assert.assertEquals;

/**
 * Created by Benjamin Schulte on 31.10.2017.
 */
public class GameSaveTest {

    @BeforeClass
    public static void init() {
        // Note that we don't need to implement any of the listener's methods
        Gdx.app = new HeadlessApplication(new ApplicationListener() {
            @Override
            public void create() {
            }

            @Override
            public void resize(int width, int height) {
            }

            @Override
            public void render() {
            }

            @Override
            public void pause() {
            }

            @Override
            public void resume() {
            }

            @Override
            public void dispose() {
            }
        });

        // Use Mockito to mock the OpenGL methods since we are running headlessly
        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl = Gdx.gl20;
    }

    @Test
    public void initTest() {
        // wenn kein gespeicherter Stand da, dann alles auf 0
        Preferences prefs = Gdx.app.getPreferences(PrefsManager.SCCPLF);
        prefs.remove("sg");

        GameSave.init();

        assertEquals(3, GameSave.getLifes());
        assertEquals(0, GameSave.getTotalScore());
        assertEquals(0, GameSave.getItem());
        assertEquals(0, GameSave.getCoins());
        assertEquals(Maryo.MaryoState.small, GameSave.getMaryoState());

        // wenn gespeicherter Stand da, dann soll natürlich der gelesen werden
        prefs.putString("sg", Utility.encode("{\"coins\":10,\"lifes\":2,\"state\":1,\"item\":0,\"playtime\":0}", PrefsManager.SCCPLF));
        GameSave.init();

        assertEquals(2, GameSave.getLifes());
        assertEquals(0, GameSave.getTotalScore());
        assertEquals(0, GameSave.getItem());
        assertEquals(10, GameSave.getCoins());
        assertEquals(Maryo.MaryoState.big, GameSave.getMaryoState());

    }

}