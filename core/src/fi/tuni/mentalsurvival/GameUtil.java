package fi.tuni.mentalsurvival;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Game utility class that contains methods for different actions in game (creating the world. transforming tiled map's layers to bodies, saving and loading the game etc.)
 */
public class GameUtil {
	/**
	 * Method that creates our box2d world from tiled map file.
	 * @param game the game object
	 */
	public static void createWorld(MentalSurvival game) {
		game.setWorld(new World(new Vector2(0, -9.8f), true));
		// Load map
		game.setTiledMap(new TmxMapLoader().load("tiled/Taso1_new.tmx"));

//		 Transform tiled walls to box2d bodies
		transformWallsToBodies("world-wall-rectangles", "wall", game.getTiledMap(), game.getWorld());
		transformWallsToBodies("fishing-rectangles", "fishing-area", game.getTiledMap(), game.getWorld());
		transformWallsToBodies("drinking-rectangles", "water-area", game.getTiledMap(), game.getWorld());
		transformWallsToBodies("tools-rectangles", "tool", game.getTiledMap(), game.getWorld());
		transformWallsToBodies("ropes-rectangles", "rope", game.getTiledMap(), game.getWorld());
		transformWallsToBodies("edibles-rectangles", "collectible", game.getTiledMap(), game.getWorld());
		transformWallsToBodies("backpack-rectangle", "backpack", game.getTiledMap(), game.getWorld());

		// Create ground to the world
		createGround(game.getWorld());
	}

	/**
	 * A method that transforms tiled map walls to box2d bodies.
	 * @param layer tiled map layer name
	 * @param userData type of the layer
	 * @param tiledMap tiled map object
	 * @param world world object
	 */
	public static void transformWallsToBodies(String layer, String userData, TiledMap tiledMap, World world) {
		// Let's get the collectable rectangles layer
		MapLayer collisionObjectLayer = tiledMap.getLayers().get(layer);

		// All the rectangles of the layer
		MapObjects mapObjects = collisionObjectLayer.getObjects();

		// Cast it to RectangleObjects array
		Array<RectangleMapObject> rectangleObjects = mapObjects.getByType(RectangleMapObject.class);

		// Iterate all the rectangles
		for (RectangleMapObject rectangleObject : rectangleObjects) {
			Rectangle tmp = rectangleObject.getRectangle();

			// SCALE given rectangle down if using world dimensions!
			Rectangle rectangle = fi.tuni.mentalsurvival.Util.scaleRect(tmp, 1 / 100f);

			createStaticBody(rectangle, userData, world);
		}
	}

	/**
	 * Gwt body definition of the box2d world ground.
	 * @return body definition of the world
	 */
	private static BodyDef getGroundBodyDef() {
		// Body Definition
		BodyDef myBodyDef = new BodyDef();

		// This body won't move
		myBodyDef.type = BodyDef.BodyType.StaticBody;

		// Initial position is centered up
		// This position is the CENTER of the shape!
		myBodyDef.position.set(GameClass.WORLD_WIDTH / 2, 0.25f);

		return myBodyDef;
	}

	/**
	 * Get the ground's polygon shape.
	 * @return ground as PolygonShape
	 */
	private static PolygonShape getGroundShape() {
		// Create shape
		PolygonShape groundBox = new PolygonShape();

		// Real width and height is 2 X this!
		groundBox.setAsBox( GameClass.WORLD_WIDTH/2 , 0.25f);

		return groundBox;
	}

	/**
	 * Create the ground to our world
	 * @param world the world object
	 */
	public static void createGround(World world) {
		Body groundBody = world.createBody(getGroundBodyDef());

		// Add shape to fixture, 0.0f is density.
		// Using method createFixture(Shape, density) no need
		// to create FixtureDef object as on createPlayer!
		groundBody.createFixture(getGroundShape(), 0.0f);
	}

	/**
	 * Create any kind of static body to our box2d world.
	 * @param rect the rectangle which the body is made from (contains x, y, width, height)
	 * @param userData the type of the created body
	 * @param world world object
	 */
	public static void createStaticBody(Rectangle rect, String userData, World world) {
		BodyDef myBodyDef = new BodyDef();
		myBodyDef.type = BodyDef.BodyType.StaticBody;

		float x = rect.getX();
		float y = rect.getY();
		float width = rect.getWidth();
		float height = rect.getHeight();

		float centerX = width/2 + x;
		float centerY = height/2 + y;

		myBodyDef.position.set(centerX, centerY);

		GameObjectType type = null;

		if(userData.equals("wall")) {
			type = GameObjectType.WALL;
		} else if(userData.equals("collectible")) {
			type = GameObjectType.COLLECTIBLE;
		} else if(userData.equals("tool")) {
			type = GameObjectType.TOOL;
		} else if(userData.equals("fishing-area")) {
			type = GameObjectType.FISHING;
		} else if(userData.equals("water-area")) {
			type = GameObjectType.WATER;
		} else if(userData.equals("backpack")) {
			type = GameObjectType.BACKPACK;
		} else if (userData.equals("rope")) {
			type = GameObjectType.ROPE;
		}

		fi.tuni.mentalsurvival.GameObjectInfo gameObject = new fi.tuni.mentalsurvival.GameObjectInfo(type, x, y);

		Body wall = world.createBody(myBodyDef);

		wall.setUserData(gameObject);
		// Create shape
		PolygonShape groundBox = new PolygonShape();

		// Real width and height is 2 X this!
		groundBox.setAsBox(width / 2 , height / 2 );

		wall.createFixture(groundBox, 0.0f);
	}

	/**
	 * Remove the specific bodies from world and empty it's cell so it disappears from the map.
	 * @param game game object
	 */
	public static void clearBodies(MentalSurvival game) {
		for (Body body : game.getBodiesToBeCleared()) {
			if(body != null) {
				Object userData = body.getUserData();
				fi.tuni.mentalsurvival.GameObjectInfo data = (fi.tuni.mentalsurvival.GameObjectInfo) userData;
				TiledMapTileLayer wallCells = (TiledMapTileLayer) game.getTiledMap().getLayers().get("edibles-layer");

				if(data != null) {
					if(data.type == GameObjectType.TOOL) {
						wallCells = (TiledMapTileLayer) game.getTiledMap().getLayers().get("tools-layer");
					}
					if(data.type == GameObjectType.BACKPACK) {
						wallCells = (TiledMapTileLayer) game.getTiledMap().getLayers().get("backpack-layer");
					}
					if(data.type == GameObjectType.ROPE) {
						wallCells = (TiledMapTileLayer) game.getTiledMap().getLayers().get("ropes-layer");
					}
					float xPos = data.x / 32 * 100;
					float yPos = data.y / 32 * 100;

					wallCells.setCell((int) Math.round(xPos) - 1, (int) Math.round(yPos), null);
					wallCells.setCell((int) Math.round(xPos), (int) Math.round(yPos), null);
					wallCells.setCell((int) Math.round(xPos) + 1, (int) Math.round(yPos), null);

					game.getClearedPositions().add(new Integer[]{Math.round(xPos), Math.round(yPos)});
					game.getWorld().destroyBody(body);
				}
			}
		}
		game.setBodiesToBeCleared(new Array<Body>());
	}

	/**
	 * Clear bodies that were removed earlier (this method is used when loading the game from save)
	 * @param game tha game object
	 * @param bodies array of all the bodies on the box2d world
	 * @param positionsArray the positions that has bodies we need to remove
	 */
	public static void clearOldBodies(MentalSurvival game, Array<Body> bodies, JsonArray positionsArray) {
		for (Body body : bodies) {
			for (JsonElement position : positionsArray) {
				if (body != null) {
					Object userData = body.getUserData();
					fi.tuni.mentalsurvival.GameObjectInfo data = (fi.tuni.mentalsurvival.GameObjectInfo) userData;
					if (data != null && !position.isJsonNull()) {
						JsonArray positionArray = position.getAsJsonArray();
						int xPos = (int) (data.x / 32 * 100);
						int yPos = (int) (data.y / 32 * 100);
						int clearedX = positionArray.get(0).getAsInt();
						int clearedY = positionArray.get(1).getAsInt();
						game.getClearedPositions().add(new Integer[]{clearedX, clearedY});
						if((Math.round(xPos) == clearedX || Math.round(xPos) + 1 == clearedX) && Math.round(yPos) == clearedY) {
							if (data.type == GameObjectType.BACKPACK) {
								TiledMapTileLayer wallCells = (TiledMapTileLayer) game.getTiledMap().getLayers().get("backpack-layer");
								wallCells.setCell((int) Math.round(xPos) - 1, (int) Math.round(yPos), null);
								wallCells.setCell((int) Math.round(xPos), (int) Math.round(yPos), null);
								wallCells.setCell((int) Math.round(xPos) + 1, (int) Math.round(yPos), null);
								game.getWorld().destroyBody(body);
							} else if (data.type == GameObjectType.TOOL) {
								TiledMapTileLayer wallCells = (TiledMapTileLayer) game.getTiledMap().getLayers().get("tools-layer");
								wallCells.setCell((int) Math.round(xPos) - 1, (int) Math.round(yPos), null);
								wallCells.setCell((int) Math.round(xPos), (int) Math.round(yPos), null);
								wallCells.setCell((int) Math.round(xPos) + 1, (int) Math.round(yPos), null);
								game.getWorld().destroyBody(body);
							} else if(data.type == GameObjectType.ROPE) {
								TiledMapTileLayer wallCells = (TiledMapTileLayer) game.getTiledMap().getLayers().get("ropes-layer");
								wallCells.setCell((int) Math.round(xPos) - 1, (int) Math.round(yPos), null);
								wallCells.setCell((int) Math.round(xPos), (int) Math.round(yPos), null);
								wallCells.setCell((int) Math.round(xPos) + 1, (int) Math.round(yPos), null);
								game.getWorld().destroyBody(body);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Get player object as a JSON string
	 * @param player the player object
	 * @return player object as JSON string
	 */
	public static String getJsonString(Player player) {
		// Before converting to GSON check value of id
		Gson gson = null;
		gson = new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.create();
		return gson.toJson(player);
	}

	/**
	 * Get game's saved preferences.
	 * @return preferences
	 */
	public static Preferences getPreferences() {
		return Gdx.app.getPreferences("Game_State");
	}

	/**
	 * Save the game using preferences
	 * @param game game object to save
	 */
	public static void saveGame(MentalSurvival game) {
		String jsonString = getJsonString(game.getPlayer());
		Array<Integer[]> clearedPositions = game.getClearedPositions();

		getPreferences()
				.putString("PLAYER", jsonString)
				.putInteger("GAME_STEP", game.getGameStep())
				.putInteger("WOOD_TO_COLLECT", game.getWoodToCollect())
				.putInteger("ROPES_TO_COLLECT", game.getRopesToCollect())
				.putString("CLEARED_POSITIONS",  new Gson().toJson(clearedPositions))
				.flush();
	}

	/**
	 * Remove all the saved preferences to reset the save.
	 */
	public static void resetSavedGame() {
		Preferences prefs = getPreferences();
		prefs.remove("PLAYER");
		prefs.remove("GAME_STEP");
		prefs.remove("WOOD_TO_COLLECT");
		prefs.remove("ROPES_TO_COLLECT");
		prefs.remove("CLEARED_POSITIONS");
		prefs.flush();
	}

	/**
	 * Load the saved game from preferences
	 *
	 * Gets all the data needed for loading as strings, integers and booleans from preferences and then assigns them to the game object.
	 * @param game the game object
	 */
	public static void loadGame(MentalSurvival game) {
		String str = getPreferences().getString("PLAYER", "{\"backpackCollected\":false,\"canFish\":false,\"hasWater\":false,\"matchCount\":5,\"positionX\":7.8,\"positionY\":0.7549997,\"sanityLevel\":0,\"sleeping\":false,\"speed\":0.015,\"woodCount\":0,\"ropeCount\":0}\n");
		String positions = getPreferences().getString("CLEARED_POSITIONS", "{\"items\":[],\"ordered\":true,\"size\":0}");
		int gameStep = getPreferences().getInteger("GAME_STEP", 0);
		int woodToCollect = getPreferences().getInteger("WOOD_TO_COLLECT", 0);
		int ropesToCollect = getPreferences().getInteger("ROPES_TO_COLLECT", 0);

		JsonObject positionsJson = (JsonObject) JsonParser.parseString(positions);
		JsonArray positionsArray = positionsJson.get("items").getAsJsonArray();

		Array<Body> bodies = new Array<Body>();
		game.getWorld().getBodies(bodies);

		clearOldBodies(game, bodies, positionsArray);

		JsonObject jsonObject = (JsonObject) JsonParser.parseString(str);

		boolean backpackCollected = jsonObject.get("backpackCollected").getAsBoolean();
		int woodCount = jsonObject.get("woodCount").getAsInt();
		int ropeCount = jsonObject.get("ropeCount").getAsInt();
		boolean canFish = jsonObject.get("canFish").getAsBoolean();
		boolean hasWater = jsonObject.get("hasWater").getAsBoolean();
		int matchCount = jsonObject.get("matchCount").getAsInt();
		int sanityLevel = jsonObject.get("sanityLevel").getAsInt();
		boolean sleeping = jsonObject.get("sleeping").getAsBoolean();
		float speed = jsonObject.get("speed").getAsFloat();
		float positionX = jsonObject.get("positionX").getAsFloat();
		float positionY = jsonObject.get("positionY").getAsFloat();

		game.setGameStep(gameStep);
		game.setWoodToCollect(woodToCollect);
		game.setRopesToCollect(ropesToCollect);
		game.getPlayer().setBackpackCollected(backpackCollected);
		game.getPlayer().setWoodCount(woodCount);
		game.getPlayer().setRopeCount(ropeCount);
		game.getPlayer().setCanFish(canFish);
		game.getPlayer().setHasWater(hasWater);
		game.getPlayer().setMatchCount(matchCount);
		game.getPlayer().setSanityLevel(sanityLevel);
		game.getPlayer().setSleeping(sleeping);
		game.getPlayer().setSpeed(speed);
		game.getPlayer().createWalkAnimation();
		game.getPlayer().getBody().getPosition().x = positionX;
		game.getPlayer().getBody().setTransform(positionX, positionY, game.getPlayer().getBody().getAngle());
	}
}
