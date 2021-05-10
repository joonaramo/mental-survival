package fi.tuni.mentalsurvival;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;

/**
 * The game's on screen joystick controller made with scene2d's Touchpad.
 */
public class JoystickControl {

    private Touchpad touchpad;
    private TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;

    /**
     * Constructor that creates the joystick from given textures and values
     * @param background joystick background texture
     * @param knob joystick knob texture
     * @param deadZoneRadius dead zone radius (default: 10)
     * @param x x position
     * @param y y position
     * @param width width
     * @param height height
     */
    public JoystickControl(Texture background, Texture knob, float deadZoneRadius, float x, float y, float width, float height) {
        touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.add("touchBackground", background);
        //Set knob image
        touchpadSkin.add("touchKnob", knob);
        //Create TouchPad Style
        touchpadStyle = new TouchpadStyle();
        //Apply the Drawables to the TouchPad Style
        touchpadStyle.background = touchpadSkin.getDrawable("touchBackground");
        touchpadStyle.knob = touchpadSkin.getDrawable("touchKnob");
        //Create new TouchPad with the created  style
        touchpad = new Touchpad(deadZoneRadius, touchpadStyle);
        //setBounds(x,y,width,height)
        touchpad.setBounds(x, y, width, height);
    }

    /**
     * Get the touchpad object
     * @return touchpad object
     */
    public Touchpad getTouchpad() {
        return touchpad;
    }

}
