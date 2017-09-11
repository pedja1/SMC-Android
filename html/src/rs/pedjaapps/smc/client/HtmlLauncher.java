package rs.pedjaapps.smc.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import rs.pedjaapps.smc.MaryoGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(MaryoGame.NATIVE_WIDTH, MaryoGame.NATIVE_HEIGHT);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new MaryoGame(null);
        }
}