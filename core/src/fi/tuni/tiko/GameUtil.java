package fi.tuni.tiko;

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
import com.badlogic.gdx.utils.Json;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


class GameUtil {

	public static void createWorld(MentalSurvival game) {
		game.setWorld(new World(new Vector2(0, -9.8f), true));
		// Load map
		game.setTiledMap(new TmxMapLoader().load("Taso1.tmx"));

		// Transform tiled walls to box2d bodies
		transformWallsToBodies("world-wall-rectangles", "wall", game.getTiledMap(), game.getWorld());
		transformWallsToBodies("tools-rectangles", "tool", game.getTiledMap(), game.getWorld());
		transformWallsToBodies("edibles-rectangles", "collectible", game.getTiledMap(), game.getWorld());
		transformWallsToBodies("fishing-rectangles", "fishing-area", game.getTiledMap(), game.getWorld());
		transformWallsToBodies("backpack-rectangle", "backpack", game.getTiledMap(), game.getWorld());

		// Create ground to the world
		createGround(game.getWorld());
	}


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
			Rectangle rectangle = Util.scaleRect(tmp, 1 / 100f);

			createStaticBody(rectangle, userData, world);
		}
	}

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

	private static PolygonShape getGroundShape() {
		// Create shape
		PolygonShape groundBox = new PolygonShape();

		// Real width and height is 2 X this!
		groundBox.setAsBox( GameClass.WORLD_WIDTH/2 , 0.25f);

		return groundBox;
	}


	public static void createGround(World world) {
		Body groundBody = world.createBody(getGroundBodyDef());

		// Add shape to fixture, 0.0f is density.
		// Using method createFixture(Shape, density) no need
		// to create FixtureDef object as on createPlayer!
		groundBody.createFixture(getGroundShape(), 0.0f);
	}

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
		} else  if(userData.equals("backpack")) {
			type = GameObjectType.BACKPACK;
		}

		GameObjectInfo gameObject = new GameObjectInfo(type, x, y);

		Body wall = world.createBody(myBodyDef);

		wall.setUserData(gameObject);
		// Create shape
		PolygonShape groundBox = new PolygonShape();

		// Real width and height is 2 X this!
		groundBox.setAsBox(width / 2 , height / 2 );

		wall.createFixture(groundBox, 0.0f);
	}

	public static void clearBodies(MentalSurvival game) {
		for (Body body : game.getBodiesToBeCleared()) {
			if(body != null) {
				Object userData = body.getUserData();
				GameObjectInfo data = (GameObjectInfo) userData;
				TiledMapTileLayer wallCells = (TiledMapTileLayer) game.getTiledMap().getLayers().get("edibles-layer");

				if(data != null) {
					if(data.type == GameObjectType.TOOL) {
						wallCells = (TiledMapTileLayer) game.getTiledMap().getLayers().get("tools-layer");
					}
					if(data.type == GameObjectType.BACKPACK) {
						wallCells = (TiledMapTileLayer) game.getTiledMap().getLayers().get("backpack-layer");
					}

					float xPos = data.x / 32 * 100;
					float yPos = data.y / 32 * 100;

					wallCells.setCell((int) Math.round(xPos), (int) Math.round(yPos), null);
					wallCells.setCell((int) Math.round(xPos) + 1, (int) Math.round(yPos), null);

					game.getWorld().destroyBody(body);
				}
			}
		}
		game.setBodiesToBeCleared(new Array<Body>());
	}

	public static void clearOldBodies(MentalSurvival game) {
		for (Body body : game.getClearedBodies()) {
			if(body != null) {
				Object userData = body.getUserData();
				GameObjectInfo data = (GameObjectInfo) userData;
				TiledMapTileLayer wallCells = (TiledMapTileLayer) game.getTiledMap().getLayers().get("edibles-layer");

				if(data != null) {
					if(data.type == GameObjectType.TOOL) {
						wallCells = (TiledMapTileLayer) game.getTiledMap().getLayers().get("tools-layer");
					}
					if(data.type == GameObjectType.BACKPACK) {
						wallCells = (TiledMapTileLayer) game.getTiledMap().getLayers().get("backpack-layer");
					}

					float xPos = data.x / 32 * 100;
					float yPos = data.y / 32 * 100;

					wallCells.setCell((int) Math.round(xPos), (int) Math.round(yPos), null);
					wallCells.setCell((int) Math.round(xPos) + 1, (int) Math.round(yPos), null);

					game.getWorld().destroyBody(body);
				}
			}
		}
		game.setClearedBodies(new Array<Body>());
	}

	public static String getJsonString(Player player) {
		// Before converting to GSON check value of id
		Gson gson = null;
		gson = new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.create();

		return gson.toJson(player);
	}


	private static Preferences getPreferences() {
		return Gdx.app.getPreferences("Game_State");
	}

	public static void saveGame(MentalSurvival game) {
		String jsonString = getJsonString(game.getPlayer());
		getPreferences()
	  	.putString("PLAYER", jsonString)
		.putInteger("GAME_STEP", game.getGameStep())
		.flush();
		Gdx.app.log("saved_str", jsonString);
	}

	public static void loadGame(MentalSurvival game) {
		String str = getPreferences().getString("PLAYER", "{\"backpackCollected\":false,\"canFish\":false,\"hasWater\":false,\"matchCount\":5,\"sanityLevel\":0,\"sleeping\":false,\"speed\":0.015,\"woodCount\":0}\n");
		int gameStep = getPreferences().getInteger("GAME_STEP", 0);

		JsonObject jsonObject = (JsonObject) JsonParser.parseString(str);

		boolean backpackCollected = jsonObject.get("backpackCollected").getAsBoolean();
		int woodCount = jsonObject.get("woodCount").getAsInt();
		boolean canFish = jsonObject.get("canFish").getAsBoolean();
		boolean hasWater = jsonObject.get("hasWater").getAsBoolean();
		int matchCount = jsonObject.get("matchCount").getAsInt();
		int sanityLevel = jsonObject.get("sanityLevel").getAsInt();
		boolean sleeping = jsonObject.get("sleeping").getAsBoolean();
		float speed = jsonObject.get("speed").getAsFloat();

		game.setGameStep(gameStep);
		game.getPlayer().setBackpackCollected(backpackCollected);
		game.getPlayer().setWoodCount(woodCount);
		game.getPlayer().setCanFish(canFish);
		game.getPlayer().setHasWater(hasWater);
		game.getPlayer().setMatchCount(matchCount);
		game.getPlayer().setSanityLevel(sanityLevel);
		game.getPlayer().setSleeping(sleeping);
		game.getPlayer().setSpeed(speed);
	}
}
