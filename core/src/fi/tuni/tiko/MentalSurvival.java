package fi.tuni.tiko;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

public class MentalSurvival extends Game {
    SpriteBatch batch;
    private int gameStep = 0;
    private int woodToCollect = 0;
    private int ropesToCollect = 0;
    private Player player;
    private TiledMap tiledMap;
    private World world;
    private Array<Body> bodiesToBeCleared = new Array<Body>();
    private Array<Body> clearedBodies = new Array<Body>();
    private Array<Integer[]> clearedPositions = new Array<Integer[]>();
    private Array<GameObject> gameObjects = new Array<GameObject>();
    private I18NBundle myBundle;
    private Locale fiLocale;
    private Locale enLocale;
    private Locale currentLocale;


    @Override
    public void create () {
        batch = new SpriteBatch();
        GameUtil.createWorld(this);
        player = new Player(world);
        GameUtil.loadGame(this);
        fiLocale = new Locale("fi");
        enLocale = new Locale("en");
        if(GameUtil.getPreferences().getString("LANGUAGE", "fi").equals("fi")) {
            currentLocale = fiLocale;
        } else {
            currentLocale = enLocale;
        }
        myBundle = I18NBundle.createBundle(Gdx.files.internal("locales/MyBundle"), currentLocale);
        setScreen(new SplashScreen(this));
    }


    @Override
    public void dispose () {
        batch.dispose();
        world.dispose();
        tiledMap.dispose();
    }

    public void restart() {
        GameUtil.createWorld(this);
        player = new Player(world);
        gameStep = 0;
        woodToCollect = 0;
        ropesToCollect = 0;
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

    public Array<Integer[]> getClearedPositions() {
        return clearedPositions;
    }

    public Array<GameObject> getGameObjects() {
        return gameObjects;
    }

    public int getWoodToCollect() {
        return woodToCollect;
    }

    public int getRopesToCollect() {
        return ropesToCollect;
    }

    public String getString(String key) {
        return myBundle.get(key);
    }

    public Locale getFiLocale() {
        return fiLocale;
    }

    public Locale getEnLocale() {
        return enLocale;
    }

    public void setCurrentLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;
        myBundle = I18NBundle.createBundle(Gdx.files.internal("locales/MyBundle"), currentLocale);
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

    public void setWoodToCollect(int woodToCollect) {
        this.woodToCollect = woodToCollect;
    }

    public void setRopesToCollect(int ropesToCollect) {
        this.ropesToCollect = ropesToCollect;
    }
}
