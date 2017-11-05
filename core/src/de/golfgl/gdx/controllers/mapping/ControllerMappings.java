package de.golfgl.gdx.controllers.mapping;

import com.badlogic.gdx.controllers.Controller;

import java.util.HashMap;

/**
 * Created by Benjamin Schulte on 04.11.2017.
 */

public class ControllerMappings {
    public static final String LOG_TAG = "CONTROLLERMAPPING";

    /**
     * this holds all inputs defined by the game
     */
    private HashMap<Integer, ConfiguredInput> configuredInputs;
    /**
     * this holds defined input mappings for every Controller (String is its name)
     */
    private HashMap<String, MappedInputs> mappedInputs;
    private boolean initialized;

    private static int findPressedButton(Controller controller) {
        // Cycle through button indexes to check if a button is pressed
        // Some gamepads report buttons from 90 to 107, so we check up to index 500
        // this should be moved into controller implementation which knows it better
        for (int i = 0; i <= 500; i++)
            if (controller.getButton(i))
                return i;

        return -1;
    }

    protected MappedInputs getControllerMapping(Controller controller) {
        MappedInputs retVal = mappedInputs.get(controller.getName());

        if (retVal == null) {
            //TODO fire event and initialize with default values
        }

        return retVal;
    }

    public void addConfiguredInput(ConfiguredInput configuredInput) {
        if (initialized)
            throw new IllegalStateException("Changing config not allowed after commit() is called");

        if (configuredInputs == null)
            configuredInputs = new HashMap<>();

        configuredInputs.put(configuredInput.inputId, configuredInput);
    }

    /**
     * call this when configuration is done
     */
    public void commit() {
        initialized = true;
    }

    public boolean recordMapping(Controller controller, int configuredInputId) {
        if (!initialized)
            throw new IllegalStateException("Recording not allowed before commit() is called");
        ConfiguredInput configuredInput = configuredInputs.get(configuredInputId);


        // initialize mapping map and controller information if not already present
        if (mappedInputs == null)
            mappedInputs = new HashMap<>();

        if (!mappedInputs.containsKey(controller.getName()))
            mappedInputs.put(controller.getName(), new MappedInputs(controller.getName()));

        switch (configuredInput.inputType) {
            case button:
                int buttonIndex = findPressedButton(controller);

                if (buttonIndex >= 0) {
                    // we found our button, hopefully
                    MappedInputs mappedInput = getControllerMapping(controller);
                    boolean added = mappedInput.putMapping(new MappedInput(configuredInputId,
                            new ControllerButton(buttonIndex)));

                    //TODO fire event when not added

                    return added;
                } else
                    return false;
            default:
                //TODO
                return false;
        }

    }

    public static abstract class ControllerInput {
        public static final char PREFIX_BUTTON = 'B';
        public static final char PREFIX_AXIS = 'A';
        public static final char PREFIX_POV = 'P';

        public static ControllerInput deserialize(String serializedInput) {
            char p = serializedInput.charAt(0);

            switch (p) {
                case PREFIX_BUTTON:
                    return ControllerButton.deserialize(serializedInput);
                case PREFIX_AXIS:
                    return ControllerAxis.deserialize(serializedInput);
                case PREFIX_POV:
                    return ControllerPovButton.deserialize(serializedInput);
                default:
                    throw new IllegalArgumentException("Unknown prefix " + p);
            }
        }

        public abstract String serialize();
    }

    public static class ControllerButton extends ControllerInput {
        public int buttonIndex;

        public ControllerButton(int buttonIndex) {
            this.buttonIndex = buttonIndex;
        }

        public static ControllerButton deserialize(String serializedInput) {
            ControllerButton button = new ControllerButton(Integer.valueOf(serializedInput.substring(1)));
            return button;
        }

        public static String serializeButton(int buttonIndex) {
            return PREFIX_BUTTON + String.valueOf(buttonIndex);
        }

        @Override
        public String serialize() {
            return serializeButton(buttonIndex);
        }
    }

    public static class ControllerAxis extends ControllerInput {
        public int axisIndex;

        public static ControllerAxis deserialize(String serializedInput) {
            ControllerAxis axis = new ControllerAxis();
            axis.axisIndex = Integer.valueOf(serializedInput.substring(1));
            return axis;
        }

        public static String serializeAxis(int axisIndex) {
            return PREFIX_AXIS + String.valueOf(axisIndex);
        }

        @Override
        public String serialize() {
            return serializeAxis(axisIndex);
        }
    }

    public static class ControllerPovButton extends ControllerInput {
        public static final String VERTICAL = "V";
        public int povIndex;
        public boolean povDirectionVertical;

        public static ControllerPovButton deserialize(String serializedInput) {
            ControllerPovButton pov = new ControllerPovButton();
            pov.povIndex = Integer.valueOf(serializedInput.substring(2));
            pov.povDirectionVertical = (serializedInput.substring(1, 1).equals(VERTICAL));
            return pov;
        }

        public static String serializePov(boolean povDirectionVertical, int povIndex) {
            return PREFIX_POV + (povDirectionVertical ? VERTICAL : "H") + String.valueOf(povIndex);
        }

        @Override
        public String serialize() {
            return serializePov(povDirectionVertical, povIndex);
        }
    }

    /**
     * A single input mapping definition for one ConfiguredInput and one Controller
     */
    private static class MappedInput {
        /**
         * the configured input this mapping is referring to
         */
        private int configuredInputId;
        private ControllerInput controllerInput;
        // if an axis is simulated by two buttons, second one is needed
        private ControllerButton secondButtonForAxis;

        public MappedInput(int configuredInputId, ControllerInput controllerInput) {
            this.configuredInputId = configuredInputId;
            this.controllerInput = controllerInput;
        }
    }

    /**
     * An input mappings for a single controller
     */
    protected class MappedInputs {
        private String controllerName;
        private boolean isComplete;
        private HashMap<Integer, MappedInput> mappingsByConfigured;
        private HashMap<Integer, MappedInput> mappingsByButton;

        private MappedInputs(String controllerName) {
            this.controllerName = controllerName;
            mappingsByConfigured = new HashMap<>(mappedInputs.size());
            mappingsByButton = new HashMap<>(mappedInputs.size());
        }

        public boolean checkCompleted() {
            //TODO check of mappingsByConfigured contains all configuredInputs
            return false;
        }

        public String getControllerName() {
            return controllerName;
        }

        /**
         * add a new mapping
         *
         * @param mapping
         * @return true if this was possible, false if mapping could not be added
         */
        public boolean putMapping(MappedInput mapping) {
            if (mappingsByConfigured.containsKey(mapping.configuredInputId))
                return false;

            if (mapping.controllerInput instanceof ControllerButton) {
                ControllerButton controllerButton = (ControllerButton) mapping.controllerInput;
                if (mappingsByButton.containsKey(controllerButton.buttonIndex))
                    return false;

                if (mapping.secondButtonForAxis != null &&
                        mappingsByButton.containsKey((mapping.secondButtonForAxis).buttonIndex))
                    return false;

                mappingsByButton.put(controllerButton.buttonIndex, mapping);
                if (mapping.secondButtonForAxis != null)
                    mappingsByButton.put(controllerButton.buttonIndex, mapping);

            } else if (mapping.controllerInput instanceof ControllerAxis) {
                //TODO
                return false;
            } else if (mapping.controllerInput instanceof ControllerPovButton) {
                //TODO
                return false;
            } else
                return false;

            mappingsByConfigured.put(mapping.configuredInputId, mapping);

            return true;
        }

        public void reset() {
            mappingsByButton.clear();
            mappingsByConfigured.clear();
        }

        public String save() {
            //TODO
            return null;
        }

        public void load(String json) {
            //TODO
        }

        public ConfiguredInput getConfiguredFromButton(int buttonIndex) {
            MappedInput mappedInput = mappingsByButton.get(buttonIndex);

            // if hit, check if it is not the reverse button
            if (mappedInput != null && (mappedInput.secondButtonForAxis == null ||
                    mappedInput.secondButtonForAxis.buttonIndex != buttonIndex))
                return configuredInputs.get(mappedInput.configuredInputId);
            else
                return null;
        }

        public ConfiguredInput getConfiguredFromReverseButton(int buttonIndex) {
            MappedInput mappedInput = mappingsByButton.get(buttonIndex);

            // if hit, check if it is the reverse button
            if (mappedInput != null && mappedInput.secondButtonForAxis != null &&
                    mappedInput.secondButtonForAxis.buttonIndex == buttonIndex)
                return configuredInputs.get(mappedInput.configuredInputId);
            else
                return null;
        }
    }

    //TODO vordefinierte XBox und (S)NES Definitionen
}
