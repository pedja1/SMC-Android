package de.golfgl.gdx.controllers.mapping;

import com.badlogic.gdx.controllers.Controller;

import java.util.HashMap;

/**
 * Created by Benjamin Schulte on 04.11.2017.
 */

public class ControllerMappings {
    public static final String LOG_TAG = "CONTROLLERMAPPING";
    public float analogToDigitalTreshold = .5f;
    /**
     * this holds all inputs defined by the game
     */
    private HashMap<Integer, ConfiguredInput> configuredInputs;
    /**
     * this holds defined input mappings for every Controller (String is its name)
     */
    private HashMap<String, MappedInputs> mappedInputs;
    private boolean initialized;
    private int waitingForReverseButtonAxisId = -1;
    private int waitingForReverseButtonFirstIdx = -1;

    private static int findHighAxisValue(Controller controller, float analogToDigitalTreshold) {
        // Cycle through axis indexes to check if there is a high value
        float highestValue = 0;
        int axisWithHighestValue = -1;

        for (int i = 0; i <= 500; i++) {
            float abs = Math.abs(controller.getAxis(i));
            if (abs > highestValue && abs >= analogToDigitalTreshold) {
                highestValue = abs;
                axisWithHighestValue = i;
            }
        }

        return axisWithHighestValue;
    }

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

    /**
     * Record a mapping. Don't call this in every render call, although it should make no problems users won't be so
     * fast.
     *
     * @param controller        controller to listen to
     * @param configuredInputId configured button or axis to record
     * @return {@link RecordResult#nothing_done} if nothing was done, {@link RecordResult#not_added} if buttons were
     * pressed but could not be added, {@link RecordResult#need_second_button} if next call must be for an axis
     * reverse button, {@link RecordResult#recorded} if a button mapping was added
     */
    public RecordResult recordMapping(Controller controller, int configuredInputId) {
        if (!initialized)
            throw new IllegalStateException("Recording not allowed before commit() is called");
        ConfiguredInput configuredInput = configuredInputs.get(configuredInputId);


        // initialize mapping map and controller information if not already present
        if (mappedInputs == null)
            mappedInputs = new HashMap<>();

        if (!mappedInputs.containsKey(controller.getName()))
            mappedInputs.put(controller.getName(), new MappedInputs(controller.getName()));

        MappedInputs mappedInput = getControllerMapping(controller);

        switch (configuredInput.inputType) {
            case button:
                int buttonIndex = findPressedButton(controller);

                if (buttonIndex >= 0) {
                    // we found our button, hopefully
                    boolean added = mappedInput.putMapping(new MappedInput(configuredInputId,
                            new ControllerButton(buttonIndex)));

                    return (added ? RecordResult.recorded : RecordResult.not_added);
                } else
                    return RecordResult.nothing_done;
            case axis:
            case axisDigital:
                // check if a button is already there, then we need to set the reverse button
                int foundButtonIndex = findPressedButton(controller);
                if (foundButtonIndex >= 0) {
                    if (waitingForReverseButtonAxisId == configuredInputId) {
                        //this is the reverse button
                        boolean added = mappedInput.putMapping(new MappedInput(configuredInputId,
                                new ControllerButton(waitingForReverseButtonFirstIdx),
                                new ControllerButton(foundButtonIndex)));
                        if (added) {
                            waitingForReverseButtonAxisId = -1;
                            waitingForReverseButtonFirstIdx = -1;
                            return RecordResult.recorded;
                        } else
                            return RecordResult.need_second_button;
                    } else {
                        // this is the first button, so remember state for next call
                        waitingForReverseButtonAxisId = configuredInputId;
                        waitingForReverseButtonFirstIdx = foundButtonIndex;
                        return RecordResult.need_second_button;
                    }
                } else if (waitingForReverseButtonAxisId == configuredInputId)
                    return RecordResult.need_second_button;

                // TODO pov

            case axisAnalog:
                int axisIndex = findHighAxisValue(controller, analogToDigitalTreshold);

                if (axisIndex >= 0) {
                    boolean added = mappedInput.putMapping(new MappedInput(configuredInputId,
                            new ControllerAxis(axisIndex)));

                    return (added ? RecordResult.recorded : RecordResult.not_added);
                } else
                    return RecordResult.nothing_done;

            default:
                return RecordResult.nothing_done;
        }

    }

    public enum RecordResult {recorded, nothing_done, not_added, need_second_button}

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

        public ControllerAxis(int axisIndex) {
            this.axisIndex = axisIndex;
        }

        public static ControllerAxis deserialize(String serializedInput) {
            ControllerAxis axis = new ControllerAxis(Integer.valueOf(serializedInput.substring(1)));
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
    protected class MappedInput {
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

        public MappedInput(int configuredInputId, ControllerButton controllerInput, ControllerButton
                reverseButton) {
            this.configuredInputId = configuredInputId;
            this.controllerInput = controllerInput;
            this.secondButtonForAxis = reverseButton;
        }

        /**
         * returns the real button index from a configured virtual button or axis
         *
         * @return the real button id, or -1 if this mapping is no button
         */
        public int getButtonIndex() {
            if (controllerInput instanceof ControllerButton)
                return ((ControllerButton) controllerInput).buttonIndex;

            return -1;
        }

        /**
         * returns the real axis from a configured axis.
         *
         * @return real axis id, or -1 if not available
         */
        public int getAxisIndex() {
            if (controllerInput instanceof ControllerAxis)
                return ((ControllerAxis) controllerInput).axisIndex;
            return -1;
        }

        public ConfiguredInput.Type getConfiguredInputType() {
            return configuredInputs.get(configuredInputId).inputType;
        }

        public int getReverseButtonIndex() {
            if (secondButtonForAxis != null)
                return secondButtonForAxis.buttonIndex;

            return -1;
        }
    }

    /**
     * Input mappings for a single controller. Class is protected and not for accessing from outside.
     * Mappings are constructed via {@link #recordMapping(Controller, int)}
     */
    protected class MappedInputs {
        private String controllerName;
        private boolean isComplete;
        private HashMap<Integer, MappedInput> mappingsByConfigured;
        private HashMap<Integer, MappedInput> mappingsByButton;
        private HashMap<Integer, MappedInput> mappingsByAxis;

        private MappedInputs(String controllerName) {
            this.controllerName = controllerName;
            mappingsByConfigured = new HashMap<>(mappedInputs.size());
            mappingsByButton = new HashMap<>(mappedInputs.size());
            mappingsByAxis = new HashMap<>(mappedInputs.size());
        }

        public boolean checkCompleted() {
            boolean completed = true;
            for (Integer configureId : configuredInputs.keySet())
                completed = completed && mappingsByConfigured.containsKey(configureId);

            return completed;
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

                // just in case reverse and first button are the same...
                if (mapping.secondButtonForAxis != null &&
                        controllerButton.buttonIndex == mapping.secondButtonForAxis.buttonIndex)
                    return false;

                mappingsByButton.put(controllerButton.buttonIndex, mapping);
                if (mapping.secondButtonForAxis != null)
                    mappingsByButton.put(mapping.secondButtonForAxis.buttonIndex, mapping);

            } else if (mapping.controllerInput instanceof ControllerAxis) {
                ControllerAxis controllerAxis = (ControllerAxis) mapping.controllerInput;
                if (mappingsByAxis.containsKey(controllerAxis.axisIndex))
                    return false;

                mappingsByAxis.put(controllerAxis.axisIndex, mapping);

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
            mappingsByAxis.clear();
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

        /**
         * returns mapped input for a configuration id, if present
         *
         * @param configuredId configuration id
         * @return MappedInput
         * @throws Exception when no mapping set
         */
        public MappedInput getMappedInput(int configuredId) {
            return mappingsByConfigured.get(configuredId);
        }

        public ConfiguredInput getConfiguredFromAxis(int axisIndex) {
            MappedInput mappedInput = mappingsByAxis.get(axisIndex);

            // if hit, check if it is the reverse button
            if (mappedInput != null)
                return configuredInputs.get(mappedInput.configuredInputId);
            else
                return null;
        }
    }

    //TODO vordefinierte XBox und (S)NES Definitionen
}
