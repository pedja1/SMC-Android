package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import de.golfgl.gdx.controllers.mapping.ConfiguredInput;
import de.golfgl.gdx.controllers.mapping.ControllerMappings;
import de.golfgl.gdx.controllers.mapping.ControllerToInputAdapter;

/**
 * Created by Benjamin Schulte on 05.11.2017.
 */

public class MyControllerMapping extends ControllerMappings {

    public static final int BUTTON_JUMP = 0;
    public static final int BUTTON_FIRE = 1;
    public static final int AXIS_VERTICAL = 2;
    public static final int AXIS_HORIZONTAL = 3;
    public static final int BUTTON_START = 4;
    public static final int BUTTON_CANCEL = 5;
    public ControllerToInputAdapter controllerToInputAdapter;

    public MyControllerMapping() {
        super();

        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, BUTTON_JUMP));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, BUTTON_FIRE));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, BUTTON_START));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, BUTTON_CANCEL));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axisDigital, AXIS_VERTICAL));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axisDigital, AXIS_HORIZONTAL));

        commitConfig();

        try {
            String json = PrefsManager.loadControllerMappings();
            JsonValue jsonValue = new JsonReader().parse(json);
            fillFromJson(jsonValue);
        } catch (Throwable t) {
            Gdx.app.error("Prefs", "Error reading saved controller mappings", t);
        }

        controllerToInputAdapter = new ControllerToInputAdapter(this);

        controllerToInputAdapter.addButtonMapping(BUTTON_JUMP, Input.Keys.SPACE);
        controllerToInputAdapter.addButtonMapping(BUTTON_FIRE, Input.Keys.X);
        controllerToInputAdapter.addButtonMapping(BUTTON_START, Input.Keys.ENTER);
        controllerToInputAdapter.addButtonMapping(BUTTON_CANCEL, Input.Keys.ESCAPE);
        controllerToInputAdapter.addAxisMapping(AXIS_HORIZONTAL, Input.Keys.LEFT, Input.Keys.RIGHT);
        controllerToInputAdapter.addAxisMapping(AXIS_VERTICAL, Input.Keys.UP, Input.Keys.DOWN);
    }

    public void setInputProcessor(InputProcessor input) {
        controllerToInputAdapter.setInputProcessor(input);
    }

    @Override
    public boolean getDefaultMapping(MappedInputs defaultMapping) {
        defaultMapping.putMapping(new MappedInput(AXIS_VERTICAL, new ControllerAxis(1)));
        defaultMapping.putMapping(new MappedInput(AXIS_HORIZONTAL, new ControllerAxis(0)));
        defaultMapping.putMapping(new MappedInput(BUTTON_JUMP, new ControllerButton(0)));
        defaultMapping.putMapping(new MappedInput(BUTTON_START, new ControllerButton(9)));
        defaultMapping.putMapping(new MappedInput(BUTTON_CANCEL, new ControllerButton(8)));

        return true;
    }
}
