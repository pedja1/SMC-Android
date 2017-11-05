package de.golfgl.gdx.controllers.mapping;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.PovDirection;

/**
 * Created by Benjamin Schulte on 05.11.2017.
 */
public class MappedControllerAdapter extends ControllerAdapter {
    ControllerMappings mappings;
    private float analogToDigitalTreshold;

    public MappedControllerAdapter(ControllerMappings mappings) {
        this.mappings = mappings;
        this.analogToDigitalTreshold = mappings.analogToDigitalTreshold;
    }

    /**
     * @param controller the controller giving this event
     * @param buttonId   your configured button id
     * @return whether you handled the event
     */
    public boolean configuredButtonDown(Controller controller, int buttonId) {
        return false;
    }

    /**
     * @param controller the controller giving this event
     * @param buttonId   your configured button id
     * @return whether you handled the event
     */
    public boolean configuredButtonUp(Controller controller, int buttonId) {
        return false;
    }

    public boolean configuredAxisMoved(Controller controller, int axisId, float value) {
        System.out.println("Axis moved: " + controller.getName() + ":" + axisId + " " + String.valueOf(value));
        return false;
    }

    protected boolean buttonChange(Controller controller, int buttonIndex, boolean isDown) {
        boolean isReverse = false;
        ControllerMappings.MappedInputs mapping = mappings.getControllerMapping(controller);

        if (mapping == null)
            return false;

        ConfiguredInput configuredInput = mapping.getConfiguredFromButton(buttonIndex);

        if (configuredInput == null) {
            configuredInput = mapping.getConfiguredFromReverseButton(buttonIndex);
            isReverse = true;
        }

        if (configuredInput == null)
            return false;

        switch (configuredInput.inputType) {
            case button:
                if (isDown)
                    return configuredButtonDown(controller, configuredInput.inputId);
                else
                    return configuredButtonUp(controller, configuredInput.inputId);
            case axis:
            case axisDigital:
                return configuredAxisMoved(controller, configuredInput.inputId, !isDown ? 0 : isReverse ? -1f : 1f);
            default:
                // axis analog may not happen
                Gdx.app.log(ControllerMappings.LOG_TAG, "Button mapped to analog axis not allowed!");
                return false;
        }
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        return buttonChange(controller, buttonIndex, true);
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonIndex) {
        return buttonChange(controller, buttonIndex, false);
    }

    @Override
    public boolean axisMoved(Controller controller, int axisIndex, float value) {
        //TODO axis fires very often, so cache last controller and last two axis

        ControllerMappings.MappedInputs mapping = mappings.getControllerMapping(controller);

        if (mapping == null)
            return false;

        ConfiguredInput configuredInput = mapping.getConfiguredFromAxis(axisIndex);

        if (configuredInput == null)
            return false;

        switch (configuredInput.inputType) {
            case axis:
            case axisAnalog:
                return configuredAxisMoved(controller, configuredInput.inputId, value);
            case axisDigital:
                return configuredAxisMoved(controller, configuredInput.inputId,
                        Math.abs(value) < analogToDigitalTreshold ? 0 : 1 * Math.signum(value));
            default:
                // button may not happen
                Gdx.app.log(ControllerMappings.LOG_TAG, "Axis mapped to button not allowed!");
                return false;
        }
    }

    @Override
    public boolean povMoved(Controller controller, int povIndex, PovDirection value) {
        return super.povMoved(controller, povIndex, value);
    }
}
