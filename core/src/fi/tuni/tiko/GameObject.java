package fi.tuni.tiko;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class GameObject {
    private Texture texture;
    private float x;
    private float y;
    private float width;
    private float height;
    private boolean visible = false;
    private boolean onFire = false;

    public GameObject(GameUtil gameUtil, float x, float y, float width, float height, String userData) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        gameUtil.createStaticBody(new Rectangle(x, y, width, height), userData);
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

    public boolean isOnFire() {
        return onFire;
    }

    public void setOnFire(boolean onFire) {
        this.onFire = onFire;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
