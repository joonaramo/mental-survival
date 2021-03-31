package fi.tuni.tiko;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.w3c.dom.css.Rect;


public class GameClass extends ScreenAdapter {
	MentalSurvival game;

	private SpriteBatch batch;

	public static final boolean DEBUG_PHYSICS = true;
	public static final float WORLD_WIDTH     = 8.0f;
	public static final float WORLD_HEIGHT    = 4.8f;

	public static int TILES_AMOUNT_WIDTH  = 55;
	public static int TILES_AMOUNT_HEIGHT = 35;

	public static int TILE_WIDTH          = 32;
	public static int TILE_HEIGHT         = 32;

	public static int WORLD_HEIGHT_PIXELS = TILES_AMOUNT_HEIGHT * TILE_HEIGHT;
	public static int WORLD_WIDTH_PIXELS  = TILES_AMOUNT_WIDTH  * TILE_WIDTH;

	private OrthographicCamera camera;

	private TiledMapRenderer tiledMapRenderer;
	private Box2DDebugRenderer debugRenderer;

	private Player player;
	private GameUtil gameUtil;

	private Hud hud;

	public GameClass(MentalSurvival game) {
		this.game = game;
	}

	@Override
	public void show() {
		gameUtil = new GameUtil();
		player = new Player(gameUtil.getWorld());

		// Create SpriteBatch
		batch = new SpriteBatch();

		hud = new Hud(batch);

		// Create camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

		tiledMapRenderer = new OrthogonalTiledMapRenderer(gameUtil.getTiledMap(), 1 / 100f);
		debugRenderer = new Box2DDebugRenderer();

		gameUtil.getWorld().setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {

			}

			@Override
			public void endContact(Contact contact) {

			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				Object userData1 = contact.getFixtureA().getBody().getUserData();
				Object userData2 = contact.getFixtureB().getBody().getUserData();
				if(userData1 != null && userData2 != null) {
					GameObjectInfo data1 = (GameObjectInfo) userData1;
					GameObjectInfo data2 = (GameObjectInfo) userData2;
					Gdx.app.log("DEBUG", String.valueOf(data1.type));
					Gdx.app.log("DEBUG", String.valueOf(data2.type));

					if(data1.type == GameObjectType.COLLECTIBLE) {
						contact.setEnabled(false);
						gameUtil.getBodiesToBeCleared().add(contact.getFixtureA().getBody());
						player.setSanityLevel(player.getSanityLevel() + 25);
						player.setSpeed(player.getSpeed() + 0.003f);
					}
					if(data2.type == GameObjectType.COLLECTIBLE) {
						contact.setEnabled(false);
						gameUtil.getBodiesToBeCleared().add(contact.getFixtureB().getBody());
						player.setSanityLevel(player.getSanityLevel() + 25);
						player.setSpeed(player.getSpeed() + 0.003f);
					}
					if(data1.type == GameObjectType.TOOL) {
						contact.setEnabled(false);
						gameUtil.getBodiesToBeCleared().add(contact.getFixtureA().getBody());
						player.setToolsCount(player.getToolsCount() + 1);
					}
					if(data2.type == GameObjectType.TOOL) {
						contact.setEnabled(false);
						gameUtil.getBodiesToBeCleared().add(contact.getFixtureB().getBody());
						player.setToolsCount(player.getToolsCount() + 1);
					}

				}
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {

			}
		});




	}

	private void clearScreen(float r, float g, float b) {
		Gdx.gl.glClearColor(r, g, b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	@Override
	public void render(float delta) {
		batch.setProjectionMatrix(camera.combined);

		clearScreen(0, 0, 0);

		if(DEBUG_PHYSICS) {
			debugRenderer.render(gameUtil.getWorld(), camera.combined);
		}

		tiledMapRenderer.setView(camera);

		// Move player and camera
		player.movePlayer(hud.getJoystickControl());
		moveCamera();
		camera.update();

		// Render tiled map
		tiledMapRenderer.render();

		// Draw player texture
		batch.begin();
		batch.draw(player.getCurrentFrameTexture(), player.getBody().getPosition().x - player.getRadius(), player.getBody().getPosition().y - player.getRadius(), player.getRadius() * 2, player.getRadius() * 2);
		batch.end();

		// Draw hud
		batch.setProjectionMatrix(hud.getStage().getCamera().combined);
		hud.getStage().act(delta);
		hud.getStage().draw();


		// Clear bodies
		gameUtil.clearBodies();


		gameUtil.doPhysicsStep(Gdx.graphics.getDeltaTime());

	}

	public void moveCamera() {
		camera.position.set(player.getBody().getPosition().x,
				player.getBody().getPosition().y,
				0);


		// Move LEFT if possible
		if(camera.position.x < WORLD_WIDTH / 2){
			camera.position.x = WORLD_WIDTH / 2;
		}

		// UP
		if(camera.position.y > (WORLD_HEIGHT_PIXELS - WORLD_HEIGHT * 100 / 2) / 100) {
			camera.position.y = (WORLD_HEIGHT_PIXELS - WORLD_HEIGHT * 100 / 2) / 100;
		}

		// DOWN
		if(camera.position.y < WORLD_HEIGHT / 2) {
			camera.position.y = WORLD_HEIGHT / 2;
		}

		// RIGHT
		if(camera.position.x > (WORLD_WIDTH_PIXELS - WORLD_WIDTH * 100 / 2) / 100) {
			camera.position.x = (WORLD_WIDTH_PIXELS - WORLD_WIDTH * 100 / 2) / 100;
		}
	}


	public void dispose() {
		gameUtil.getWorld().dispose();
		player.getTexture().dispose();
	}
}