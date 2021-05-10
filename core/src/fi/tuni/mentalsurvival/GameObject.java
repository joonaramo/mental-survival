package fi.tuni.mentalsurvival;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.annotations.Expose;

/**
 * This class is used to create game objects such as shelter.
 */
public class GameObject {
    @Expose
    private float x;
    @Expose
    private float y;
    @Expose
    private float width;
    @Expose
    private float height;
    @Expose
    private String userData;

    private boolean visible = false;
    private boolean active = false;

    /**
     * Constructor, which is used to create the game object with the given params.
     * @param x x position of the object
     * @param y y position of the object
     * @param width width of the object
     * @param height height of the object
     * @param userData data, which contains the type of the object (e.g shelter)
     * @param world the game's world object, used so we can add the body to it
     */
    public GameObject(float x, float y, float width, float height, String userData, World world) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.userData = userData;
        GameUtil.createStaticBody(new Rectangle(x, y, width, height), userData, world);
    }

    /**
     * Get the x position of the object
     * @return x position
     */
    public float getX() {
        return x;
    }

    /**
     * Get the y position of the object
     * @return y position
     */
    public float getY() {
        return y;
    }

    /**
     * Get the width of the object
     * @return width
     */
    public float getWidth() {
        return width;
    }

    /**
     * Get the height of the object
     * @return height
     */
    public float getHeight() {
        return height;
    }

    /**
     * Get the userdata of the object
     * @return String userData (contains type)
     */
    public String getUserData() {
        return userData;
    }

    /**
     * Check if object is visible
     * @return true if visible, false if not
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Check if object is active/being used.
     * @return true if active, false if not
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set the object as active
     * @param active the activity state of the object
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Set the object as visible
     * @param visible the visibility state of the object
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
