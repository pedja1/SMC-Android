package rs.pedjaapps.smc.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.preloader.Preloader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import rs.pedjaapps.smc.MaryoGame;

public class HtmlLauncher extends GwtApplication {

    // padding is to avoid scrolling in iframes, set to 20 if you have problems
    private static final int PADDING = 0;
    private GwtApplicationConfiguration cfg;

    @Override
    public GwtApplicationConfiguration getConfig() {
        int w = Window.getClientWidth() - PADDING;
        int h = Window.getClientHeight() - PADDING;
        cfg = new GwtApplicationConfiguration(w, h);
        Window.enableScrolling(false);
        Window.setMargin("0");
        Window.addResizeHandler(new ResizeListener());
        cfg.preferFlash = false;
        return cfg;
    }

    @Override
    public ApplicationListener createApplicationListener() {
        MaryoGame maryoGame = new MaryoGame(null);
        maryoGame.isRunningOn = Window.Navigator.getUserAgent();
        return maryoGame;
    }

    @Override
    public Preloader.PreloaderCallback getPreloaderCallback() {
        final Panel preloaderPanel = new VerticalPanel();
        preloaderPanel.setStyleName("gdx-preloader");
        final Image logo = new Image(GWT.getHostPageBaseURL() + "logo_preload.png");
        logo.setStyleName("preloadlogo");
        preloaderPanel.add(logo);
        final Panel meterPanel = new SimplePanel();
        meterPanel.setStyleName("gdx-meter");
        meterPanel.addStyleName("nostripes ");
        final InlineHTML meter = new InlineHTML();
        final Style meterStyle = meter.getElement().getStyle();
        meterStyle.setWidth(0, Style.Unit.PCT);
        meterPanel.add(meter);
        preloaderPanel.add(meterPanel);
        getRootPanel().add(preloaderPanel);
        return new Preloader.PreloaderCallback() {

            @Override
            public void error(String file) {
                System.out.println("error: " + file);
            }

            @Override
            public void update(Preloader.PreloaderState state) {
                meterStyle.setWidth(100f * state.getProgress(), Style.Unit.PCT);
            }

        };
    }

    class ResizeListener implements ResizeHandler {
        @Override
        public void onResize(ResizeEvent event) {
            int width = event.getWidth() - PADDING;
            int height = event.getHeight() - PADDING;
            getRootPanel().setWidth("" + width + "px");
            getRootPanel().setHeight("" + height + "px");
            getApplicationListener().resize(width, height);
            Gdx.graphics.setWindowedMode(width, height);
        }
    }
}