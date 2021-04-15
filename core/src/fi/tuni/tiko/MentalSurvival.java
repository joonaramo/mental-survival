package fi.tuni.tiko;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class MentalSurvival extends Game {
    SpriteBatch batch;
    private int gameStep = 0;
    private Player player;
    private TiledMap tiledMap;
    private World world;
    private Array<Body> bodiesToBeCleared = new Array<Body>();
    private Array<Body> clearedBodies = new Array<Body>();


    @Override
    public void create () {
        batch = new SpriteBatch();
        GameUtil.createWorld(this);
        player = new Player(world);
        setScreen(new SplashScreen(this));
    }


    @Override
    public void dispose () {
        batch.dispose();
    }

    public Player getPlayer() {
        return player;
    }

    public int getGameStep() {
        return gameStep;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public World getWorld() {
        return world;
    }

    public Array<Body> getBodiesToBeCleared() {
        return bodiesToBeCleared;
    }

    public Array<Body> getClearedBodies() {
        return clearedBodies;
    }

    public void setGameStep(int gameStep) {
        this.gameStep = gameStep;
    }

    public void setBodiesToBeCleared(Array<Body> bodiesToBeCleared) {
        this.bodiesToBeCleared = bodiesToBeCleared;
    }

    public void setClearedBodies(Array<Body> clearedBodies) {
        this.clearedBodies = clearedBodies;
    }

    public void setTiledMap(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
