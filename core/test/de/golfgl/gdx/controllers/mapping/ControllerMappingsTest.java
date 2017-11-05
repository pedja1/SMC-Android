package de.golfgl.gdx.controllers.mapping;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Benjamin Schulte on 05.11.2017.
 */
public class ControllerMappingsTest {
    @Test
    public void testButtonMapping() {
        ControllerMappings mappings = new ControllerMappings();

        // We test 4 buttons
        ConfiguredInput button1 = new ConfiguredInput(ConfiguredInput.Type.button, 1);
        ConfiguredInput button2 = new ConfiguredInput(ConfiguredInput.Type.button, 2);
        ConfiguredInput button3 = new ConfiguredInput(ConfiguredInput.Type.button, 3);
        ConfiguredInput button4 = new ConfiguredInput(ConfiguredInput.Type.button, 4);

        mappings.addConfiguredInput(button1);
        mappings.addConfiguredInput(button2);
        mappings.addConfiguredInput(button3);
        mappings.addConfiguredInput(button4);

        // ok, configuration done...
        mappings.commit();

        // now we connect a Controller... and map
        MockedController controller = new MockedController();
        controller.pressedButton = 107;
        assertTrue(mappings.recordMapping(controller, 1));
        controller.pressedButton = 108;
        assertTrue(mappings.recordMapping(controller, 2));
        controller.pressedButton = 1;
        assertTrue(mappings.recordMapping(controller, 3));
        //TODO add assertion for check if record is complete
        controller.pressedButton = 4;
        assertTrue(mappings.recordMapping(controller, 4));
        controller.pressedButton = -1;

        // now check
        TestControllerAdapter controllerAdapter = new TestControllerAdapter(mappings);

        assertTrue(controllerAdapter.buttonDown(controller, 108));
        assertEquals(2, controllerAdapter.lastEventId);
        assertTrue(controllerAdapter.buttonDown(controller, 4));
        assertEquals(4, controllerAdapter.lastEventId);
        assertFalse(controllerAdapter.buttonDown(controller, 2));
        assertEquals(4, controllerAdapter.lastEventId);
    }

    public class TestControllerAdapter extends MappedControllerAdapter {
        public int lastEventId;

        public TestControllerAdapter(ControllerMappings mappings) {
            super(mappings);
        }

        @Override
        public boolean configuredButtonDown(Controller controller, int buttonId) {
            lastEventId = buttonId;
            System.out.println("Button down: " + controller.getName() + ":" + buttonId);
            return true;
        }

        @Override
        public boolean configuredAxisMoved(Controller controller, int axisId, float value) {
            System.out.println("Axis moved: " + controller.getName() + ":" + axisId + " " + String.valueOf(value));
            return true;
        }
    }

    public class MockedController implements Controller {

        public int pressedButton;

        @Override
        public boolean getButton(int buttonCode) {
            return (pressedButton == buttonCode);
        }

        @Override
        public float getAxis(int axisCode) {
            return 0;
        }

        @Override
        public PovDirection getPov(int povCode) {
            return null;
        }

        @Override
        public boolean getSliderX(int sliderCode) {
            return false;
        }

        @Override
        public boolean getSliderY(int sliderCode) {
            return false;
        }

        @Override
        public Vector3 getAccelerometer(int accelerometerCode) {
            return null;
        }

        @Override
        public void setAccelerometerSensitivity(float sensitivity) {

        }

        @Override
        public String getName() {
            return "TEST";
        }

        @Override
        public void addListener(ControllerListener listener) {

        }

        @Override
        public void removeListener(ControllerListener listener) {

        }
    }
}