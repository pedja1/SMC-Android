package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

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

        commit();

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

}
