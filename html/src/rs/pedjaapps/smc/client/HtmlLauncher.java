package rs.pedjaapps.smc.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;

import rs.pedjaapps.smc.MaryoGame;

public class HtmlLauncher extends GwtApplication {

    private GwtApplicationConfiguration cfg;

    @Override
    public GwtApplicationConfiguration getConfig() {
        // reduce by 20 to avoid scrolling in iframes
        int w = Window.getClientWidth() - 20;
        int h = Window.getClientHeight() - 20;
        cfg = new GwtApplicationConfiguration(w, h);
        Window.enableScrolling(false);
        Window.setMargin("0");
        Window.addResizeHandler(new ResizeListner());
        cfg.preferFlash = false;
        return cfg;
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new MaryoGame(null);
    }

    class ResizeListner implements ResizeHandler {
        @Override
        public void onResize(ResizeEvent event) {
            int width = event.getWidth();
            int height = event.getHeight();
            getApplicationListener().resize(width, height);
            Gdx.graphics.setWindowedMode(width, height);
            Gdx.gl.glViewport(0, 0, width, height);

            Window.scrollTo((cfg.width - width) / 2, (cfg.height - height) / 2);

        }
    }
}