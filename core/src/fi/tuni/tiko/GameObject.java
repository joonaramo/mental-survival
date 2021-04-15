package fi.tuni.tiko;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class GameObject {
    private Texture texture;
    private float x;
    private float y;
    private float width;
    private float height;
    private boolean visible = false;
    private boolean active = false;

    public GameObject(float x, float y, float width, float height, String userData, World world) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        GameUtil.createStaticBody(new Rectangle(x, y, width, height), userData, world);
//        Gdx.app.log("DEBUG", "created gameobject to" + x);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
