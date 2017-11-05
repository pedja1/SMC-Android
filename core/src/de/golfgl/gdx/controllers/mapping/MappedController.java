package de.golfgl.gdx.controllers.mapping;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;

/**
 * Created by Benjamin Schulte on 05.11.2017.
 */

public class MappedController {
    private Controller controller;
    private ControllerMappings.MappedInputs controllerMapping;
    private float analogToDigitalTreshold;

    public MappedController(Controller controller, ControllerMappings mappings) {
        this.controller = controller;
        this.controllerMapping = mappings.getControllerMapping(controller);
        this.analogToDigitalTreshold = mappings.analogToDigitalTreshold;

        if (!controllerMapping.checkCompleted())
            Gdx.app.log(ControllerMappings.LOG_TAG, "MappedController created with incomplete configuration");
    }

    /**
     * returns whether a mapped button is pressed
     * <p>
     * It is not checked if your configuredId is a button.
     *
     * @param configuredId
     * @return true of button is pressed. false if button is not pressed, configureId is not set, not recorded or not
     * of type button
     */
    public boolean isButtonPressed(int configuredId) {
        ControllerMappings.MappedInput mappedInput = controllerMapping.getMappedInput(configuredId);

        // not configured or not recorded
        if (mappedInput == null)
            return false;

        // A virtual button is always a real button
        int buttonIndex = mappedInput.getButtonIndex();
        if (buttonIndex >= 0)
            return controller.getButton(buttonIndex);
        else
            return false;
    }

    /**
     * returns current value of virtual axis
     * <p>
     * It is not checked if your configuredId is an axis.
     *
     * @param configuredId
     * @return current value
     */
    public float getConfiguredAxisValue(int configuredId) {
        ControllerMappings.MappedInput mappedInput = controllerMapping.getMappedInput(configuredId);

        // not configured or not recorded
        if (mappedInput == null)
            return 0;

        ConfiguredInput.Type configuredInputType = mappedInput.getConfiguredInputType();

        // first check if a real axis is mapped
        int axisId = mappedInput.getAxisIndex();

        if (axisId >= 0) {
            float value = controller.getAxis(axisId);
            if (configuredInputType == ConfiguredInput.Type.axisDigital)
                return (Math.abs(value) < analogToDigitalTreshold ? 0 : 1f * Math.signum(value));
            else
                return value;
        }

        // axisAnalog only accepts real axis, so if not found don't look any further
        if (configuredInputType == ConfiguredInput.Type.axisAnalog)
            return 0;

        // if not a real axis, it could be a set of buttons or pov
        int buttonIndex = mappedInput.getButtonIndex();
        if (buttonIndex >= 0 && controller.getButton(buttonIndex))
            return 1f;
        int reverseButtonIndex = mappedInput.getReverseButtonIndex();
        if (reverseButtonIndex >= 0 && controller.getButton(reverseButtonIndex))
            return -1f;

        //TODO pov

        return 0;
    }

    public String getControllerName() {
        return controller.getName();
    }
}
