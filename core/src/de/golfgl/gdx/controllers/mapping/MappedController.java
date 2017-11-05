package de.golfgl.gdx.controllers.mapping;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;

/**
 * Created by Benjamin Schulte on 05.11.2017.
 */

public class MappedController {
    private Controller controller;
    private ControllerMappings mappings;

    public MappedController(Controller controller, ControllerMappings mappings) {
        this.controller = controller;
        this.mappings = mappings;
    }

    /**
     * returns whether a mapped button is pressed
     *
     * @param configuredId
     * @return true of button is pressed. false if button is not pressed, configureId is not set, not recorded or not
     * of type button
     */
    public boolean isButtonPressed(int configuredId) {
        // A virtual button is always a real button
        try {
            return controller.getButton(mappings.getControllerMapping(controller).getButtonFromConfigured
                    (configuredId));

        } catch (Throwable t) {
            Gdx.app.log(ControllerMappings.LOG_TAG, "Exception when accessing button - not set, or not of type " +
                    "button?", t);
            return false;
        }
    }

    public float getConfiguredAxis(int axisCode) {
        //TODO
        return 0;
    }

    public String getControllerName() {
        return controller.getName();
    }
}
