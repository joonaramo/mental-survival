package fi.tuni.mentalsurvival;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

/**
 * The actual game class that is used to create a game object. Contains data that is used by many more classes to make things happen in the game world.
 */
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


    /**
     * Method which is called when the game is opened, creates the game world, the player, and the world.
     */
    @Override
    public void create () {
        batch = new SpriteBatch();
        GameUtil.createWorld(this);
        player = new Player(world);
        GameUtil.loadGame(this);
        fiLocale = new Locale("fi", "FI");
        enLocale = new Locale("en", "US");
        if(GameUtil.getPreferences().getString("LANGUAGE", "fi").equals("fi")) {
            currentLocale = fiLocale;
        } else {
            currentLocale = enLocale;
        }
        myBundle = I18NBundle.createBundle(Gdx.files.internal("locales/MyBundle"), currentLocale);
        setScreen(new SplashScreen(this));
    }

    /**
     * Call the dispose methods to prevent memory leak.
     */
    @Override
    public void dispose () {
        batch.dispose();
        world.dispose();
        tiledMap.dispose();
    }

    /**
     * Restart the game (create world and player again, set gamestep to its' initial value.)
     */
    public void restart() {
        GameUtil.createWorld(this);
        player = new Player(world);
        gameStep = 0;
        woodToCollect = 0;
        ropesToCollect = 0;
    }

    /**
     * Get the player object
     * @return player object
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get current game step
     * @return game step (integer)
     */
    public int getGameStep() {
        return gameStep;
    }

    /**
     * Get tiled map object
     * @return tiled map object
     */
    public TiledMap getTiledMap() {
        return tiledMap;
    }

    /**
     * Get world object
     * @return world object
     */
    public World getWorld() {
        return world;
    }

    /**
     * Get bodies that needs to be cleared
     * @return array of bodies
     */
    public Array<Body> getBodiesToBeCleared() {
        return bodiesToBeCleared;
    }

    /**
     * Get positions of bodies that need to be cleared
     * @return 2d array of integers
     */
    public Array<Integer[]> getClearedPositions() {
        return clearedPositions;
    }

    /**
     * Get the game objects on the world
     * @return array of game objects
     */
    public Array<GameObject> getGameObjects() {
        return gameObjects;
    }

    /**
     * Get amount of wood to be collected
     * @return amount to collect
     */
    public int getWoodToCollect() {
        return woodToCollect;
    }

    /**
     * Get amount of ropes to be collected
     * @return amount to collect
     */
    public int getRopesToCollect() {
        return ropesToCollect;
    }

    /**
     * Get specific string from our localization bundle file
     * @param key the string to find (by key)
     * @return the string in set language
     */
    public String getString(String key) {
        return myBundle.get(key);
    }

    /**
     * Get a string from localization bundle file, but formatted with a given value
     * @param key the string to find (by key)
     * @param value the value we use in the string
     * @return the string in set language
     */
    public String getFormattedString(String key, int value) {
        return myBundle.format(key, value);
    }

    /**
     * Get Finnish language locale
     * @return FI-locale
     */
    public Locale getFiLocale() {
        return fiLocale;
    }

    /**
     * Get English language locale
     * @return EN-locale
     */
    public Locale getEnLocale() {
        return enLocale;
    }

    /**
     * Set currently used locale to change the language of the game
     * @param currentLocale the locale wanted (EN/FI)
     */
    public void setCurrentLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;
        myBundle = I18NBundle.createBundle(Gdx.files.internal("locales/MyBundle"), currentLocale);
    }

    /**
     * Set game step to a new one.
     * @param gameStep new game step
     */
    public void setGameStep(int gameStep) {
        this.gameStep = gameStep;
    }

    /**
     * Set the array of bodies which will be cleared.
     * @param bodiesToBeCleared array of bodies to be cleared
     */
    public void setBodiesToBeCleared(Array<Body> bodiesToBeCleared) {
        this.bodiesToBeCleared = bodiesToBeCleared;
    }

    /**
     * Set the tiled map object
     * @param tiledMap tiled map object
     */
    public void setTiledMap(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
    }

    /**
     * Set the world object
     * @param world world object
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Set amount of wood to be collected.
     * @param woodToCollect amount to be collected
     */
    public void setWoodToCollect(int woodToCollect) {
        this.woodToCollect = woodToCollect;
    }

    /**
     * Set the amount of ropes to be collected
     * @param ropesToCollect amount to be collected
     */
    public void setRopesToCollect(int ropesToCollect) {
        this.ropesToCollect = ropesToCollect;
    }
}
