package fi.tuni.tiko;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.annotations.Expose;

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

    public GameObject(float x, float y, float width, float height, String userData, World world) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.userData = userData;
        GameUtil.createStaticBody(new Rectangle(x, y, width, height), userData, world);
//        Gdx.app.log("DEBUG", "created gameobject to" + x);
    }
    public GameObject(float x, float y, float width, float height, String userData) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.userData = userData;
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

    public String getUserData() {
        return userData;
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
