package fi.tuni.tiko;

import com.badlogic.gdx.Gdx;
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

class GameUtil {
	public TiledMap tiledMap;
	public World world;

	// Bodies to clear
	Array<Body> bodiesToBeCleared = new Array<Body>();

	public double accumulator = 0;
	public float TIME_STEP = 1 / 60f;

	private int gameStep = 0;

	public GameUtil() {
		world = new World(new Vector2(0, -9.8f), true);

		// Load map
		tiledMap = new TmxMapLoader().load("Taso1.tmx");



		// Transform tiled walls to box2d bodies
		transformWallsToBodies("world-wall-rectangles", "wall");
		transformWallsToBodies("tools-rectangles", "tool");
		transformWallsToBodies("edibles-rectangles", "collectible");


		// Create ground to the world
		createGround();

	}

	public World getWorld() {
		return world;
	}

	public Array<Body> getBodiesToBeCleared() {
		return bodiesToBeCleared;
	}

	public TiledMap getTiledMap() {
		return tiledMap;
	}

	public void transformWallsToBodies(String layer, String userData) {
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

			createStaticBody(rectangle, userData);
		}
	}

	private BodyDef getGroundBodyDef() {
		// Body Definition
		BodyDef myBodyDef = new BodyDef();

		// This body won't move
		myBodyDef.type = BodyDef.BodyType.StaticBody;

		// Initial position is centered up
		// This position is the CENTER of the shape!
		myBodyDef.position.set(GameClass.WORLD_WIDTH / 2, 0.25f);

		return myBodyDef;
	}

	private PolygonShape getGroundShape() {
		// Create shape
		PolygonShape groundBox = new PolygonShape();

		// Real width and height is 2 X this!
		groundBox.setAsBox( GameClass.WORLD_WIDTH/2 , 0.25f);

		return groundBox;
	}

	public void doPhysicsStep(float deltaTime) {

		float frameTime = deltaTime;

		// If it took ages (over 4 fps, then use 4 fps)
		// Avoid of "spiral of death"
		if(deltaTime > 1 / 4f) {
			frameTime = 1 / 4f;
		}

		accumulator += frameTime;

		while (accumulator >= TIME_STEP) {
			// It's fixed time step!
			world.step(TIME_STEP, 6, 2);
			accumulator -= TIME_STEP;
		}
	}


	public void createGround() {
		Body groundBody = world.createBody(getGroundBodyDef());

		// Add shape to fixture, 0.0f is density.
		// Using method createFixture(Shape, density) no need
		// to create FixtureDef object as on createPlayer!
		groundBody.createFixture(getGroundShape(), 0.0f);
	}

	public void createStaticBody(Rectangle rect, String userData) {
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

	public void clearBodies() {
		for (Body body : bodiesToBeCleared) {
			if(body != null) {
				Object userData = body.getUserData();
				GameObjectInfo data = (GameObjectInfo) userData;
				TiledMapTileLayer wallCells = (TiledMapTileLayer) tiledMap.getLayers().get("edibles-layer");

				if(data != null) {
					if(data.type == GameObjectType.TOOL) {
						wallCells = (TiledMapTileLayer) tiledMap.getLayers().get("tools-layer");
					}


					float xPos = data.x / 32 * 100;
					float yPos = data.y / 32 * 100;
					Gdx.app.log("DEBUG", "xpos: " + (int) Math.round(xPos));
					Gdx.app.log("DEBUG", "ypos: " + (int) Math.round(yPos));

					wallCells.setCell((int) Math.round(xPos), (int) Math.round(yPos), null);
					wallCells.setCell((int) Math.round(xPos) + 1, (int) Math.round(yPos), null);

					world.destroyBody(body);
				}

			}
		}
		bodiesToBeCleared = new Array<Body>();
	}

	public int getGameStep() {
		return gameStep;
	}

	public void setGameStep(int gameStep) {
		this.gameStep = gameStep;
	}
}
