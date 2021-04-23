package fi.tuni.tiko;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.w3c.dom.Text;
import org.w3c.dom.css.Rect;

import java.sql.Time;
import java.util.Locale;


public class GameClass extends ScreenAdapter {
	MentalSurvival game;

	private SpriteBatch batch;

	public static final boolean DEBUG_PHYSICS = false;
	public static final float WORLD_WIDTH     = 8.0f;
	public static final float WORLD_HEIGHT    = 4.8f;

	public static int TILES_AMOUNT_WIDTH  = 110;
	public static int TILES_AMOUNT_HEIGHT = 53;

	public static int TILE_WIDTH          = 32;
	public static int TILE_HEIGHT         = 32;

	public static int WORLD_HEIGHT_PIXELS = TILES_AMOUNT_HEIGHT * TILE_HEIGHT;
	public static int WORLD_WIDTH_PIXELS  = TILES_AMOUNT_WIDTH  * TILE_WIDTH;

	private OrthographicCamera camera;

	private TiledMapRenderer tiledMapRenderer;
	private Box2DDebugRenderer debugRenderer;

	private ShapeRenderer shapeRenderer;
	private Color shapeColor;

	private GameObject shelterObject;
	private GameObject fireObject;
	private GameObject ferryObject;

	private Texture noFireTexture;
	private Texture fireTexture;
	private Texture shelterTexture;
	private Texture sleepingTexture;
	private Texture ferryTexture;

	private Sound walkingSound;

	private float currentAlpha = 0;

	private Hud hud;

	public GameClass(MentalSurvival game) {
		this.game = game;
	}

	@Override
	public void show() {

		shelterTexture = new Texture(Gdx.files.internal("shelter.png"));
		sleepingTexture = new Texture(Gdx.files.internal("shelter_player.png"));
		fireTexture = new Texture(Gdx.files.internal("fire.png"));
		noFireTexture = new Texture(Gdx.files.internal("no_fire.png"));
		ferryTexture = new Texture(Gdx.files.internal("ferry.png"));

		walkingSound = Gdx.audio.newSound(Gdx.files.internal("walking.mp3"));

		// Create SpriteBatch
		batch = new SpriteBatch();

		// Add game objects to world when game is resumed
		for(GameObject gameObject: game.getGameObjects()) {
			if(gameObject.getUserData() == "shelter") {
				shelterObject = new GameObject(gameObject.getX(), gameObject.getY(), gameObject.getWidth(), gameObject.getHeight(), gameObject.getUserData(), game.getWorld());
			}
			if(gameObject.getUserData() == "fire") {
				fireObject = new GameObject(gameObject.getX(), gameObject.getY(), gameObject.getWidth(), gameObject.getHeight(), gameObject.getUserData(), game.getWorld());
			}
			if(gameObject.getUserData() == "ferry") {
				ferryObject = new GameObject(gameObject.getX(), gameObject.getY(), gameObject.getWidth(), gameObject.getHeight(), gameObject.getUserData(), game.getWorld());
			}
		}


		// Create camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

		hud = new Hud(batch, game);

		hud.getActionButton().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				hud.setActionButtonPressed(true);
			};
		});


		tiledMapRenderer = new OrthogonalTiledMapRenderer(game.getTiledMap(), 1 / 100f);
		debugRenderer = new Box2DDebugRenderer();

		shapeRenderer = new ShapeRenderer();

		shapeColor = new Color(0, 0, 0, 0);

		handleContact();
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
			debugRenderer.render(game.getWorld(), camera.combined);
		}

		tiledMapRenderer.setView(camera);

		// Move player and camera, play sound when moving
		game.getPlayer().movePlayer(hud.getJoystickControl());
		game.getPlayer().playSound(walkingSound);
		moveCamera();
		camera.update();

		// Update sanity bar, backpack and mission text
		hud.updateSanityBar(game.getPlayer().getSanityLevel());
		hud.updateBackpack(game.getPlayer().getWoodCount(), game.getPlayer().getRopeCount(), game.getPlayer().getMatchCount(), game.getPlayer().hasWater(), game.getString("empty"), game.getString("full"));
		hud.updateMission(game.getWoodToCollect(), game.getRopesToCollect(), game.getPlayer().getWoodCount(), game.getPlayer().getRopeCount(), game.getString("collect"));

		// Show current game step
		showGameStep(game.getGameStep());

		// Render tiled map
		tiledMapRenderer.render();

		// Draw player texture
		batch.begin();
		game.getPlayer().draw(batch);

		if(shelterObject != null) {
			if(game.getPlayer().isSleeping()) {
				batch.draw(sleepingTexture, shelterObject.getX(), shelterObject.getY(), shelterObject.getWidth() * 2, shelterObject.getHeight() * 2);
			} else {
				batch.draw(shelterTexture, shelterObject.getX(), shelterObject.getY(), shelterObject.getWidth() * 2, shelterObject.getHeight() * 2);
			}
		}
		if(fireObject != null) {
			if(fireObject.isActive()) {
				batch.draw(fireTexture, fireObject.getX(), fireObject.getY(), fireObject.getWidth(), fireObject.getHeight());
			} else {
				batch.draw(noFireTexture, fireObject.getX(), fireObject.getY(), fireObject.getWidth(), fireObject.getHeight());
			}
		}
		if(ferryObject != null) {
			batch.draw(ferryTexture, ferryObject.getX(), ferryObject.getY(), ferryObject.getWidth(), ferryObject.getHeight());
		}
		batch.end();

		// Draw hud
		batch.setProjectionMatrix(hud.getStage().getCamera().combined);
		hud.getStage().act(delta);
		hud.getStage().draw();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		shapeRenderer.setColor(shapeColor);
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

		if(game.getPlayer().isSleeping()) {
			currentAlpha += Gdx.graphics.getDeltaTime() / 10;
			shapeColor.a = currentAlpha;
		} else {
			shapeColor.a = 0;
		}

		// Clear bodies
		GameUtil.clearBodies(game);

		doPhysicsStep(Gdx.graphics.getDeltaTime());
	}

	double accumulator = 0;
	float TIME_STEP = 1 / 60f;

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
			game.getWorld().step(TIME_STEP, 6, 2);
			accumulator -= TIME_STEP;
		}
	}

	public void moveCamera() {
		camera.position.set(game.getPlayer().getBody().getPosition().x,
				game.getPlayer().getBody().getPosition().y,
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

//	public void setGameStep(int gameStep) {
//		this.gameStep = gameStep;
//	}

//	public int getGameStep() {
//		return gameStep;
//	}

	public void nextGameStep() {
		hud.getOkButton().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setGameStep(game.getGameStep() + 1);
			};
		});
	}

	public void delayNextGameStep(float delay) {
		Timer.schedule(new Timer.Task(){
			@Override
			public void run() {
				game.setGameStep(game.getGameStep() + 1);
				Timer.instance().clear();
			}
		}, delay);
	}

	public void showGameStep(int gameStep) {
		switch (gameStep) {
			case 0:
				hud.showDialog(game.getString("step0"));
				nextGameStep();
				break;
			case 1:
				hud.showDialog(game.getString("step1"));
				nextGameStep();
				break;
			case 2:
				delayNextGameStep(5);
				break;
			case 3:
				if(game.getPlayer().hasMoved()) {
					hud.showDialog(game.getString("step3"));
					nextGameStep();
				}
				break;
			case 4:
				if(game.getPlayer().isBackpackCollected()) {
					hud.showBackpack();
					game.getPlayer().changeDirection(true);
					game.getPlayer().createWalkAnimation();
					game.setGameStep(game.getGameStep() + 1);
				}
				break;
			case 5:
				hud.showDialog(game.getString("step5"));
				nextGameStep();
				break;
			case 6:
				hud.showDialog(game.getString("step6"));
				nextGameStep();
				break;
			case 7:
				delayNextGameStep(20);
				break;
			case 8:
				hud.showDialog(game.getString("step8"));
				game.setWoodToCollect(3);
				nextGameStep();
				break;
			case 9:
				if(game.getPlayer().getWoodCount() >= 3) {
					hud.showDialog(game.getString("step9"));
					game.setWoodToCollect(0);
					nextGameStep();
				}
				break;
			case 10:
				hud.getActionButton().getLabel().setText(game.getString("build"));
				hud.getActionButton().setWidth(hud.getActionButton().getPrefWidth());
				hud.getActionButton().setVisible(true);
				if(hud.isActionButtonPressed()) {
					shelterObject = new GameObject(game.getPlayer().getBody().getPosition().x + 0.4f, game.getPlayer().getBody().getPosition().y, shelterTexture.getWidth() / 100f / 2, shelterTexture.getHeight() / 100f / 2, "shelter", game.getWorld());
					game.getGameObjects().add(shelterObject);
					hud.getActionButton().setVisible(false);
					hud.setActionButtonPressed(false);
					game.getPlayer().setCanMove(false);
					game.getPlayer().setWoodCount(game.getPlayer().getWoodCount() - 3);
					game.setGameStep(game.getGameStep() + 1);
				}
				break;
			case 11:
				hud.getActionButton().getLabel().setText(game.getString("sleep"));
				hud.getActionButton().setWidth(hud.getActionButton().getPrefWidth());
				hud.getActionButton().setVisible(true);
				if(hud.isActionButtonPressed()) {
					hud.getActionButton().setVisible(false);
					hud.setActionButtonPressed(false);
					game.getPlayer().setSanityLevel(game.getPlayer().getSanityLevel() + 15);
					game.getPlayer().setSleeping(true);
					game.setGameStep(game.getGameStep() + 1);
				}
				break;
			case 12:
				delayNextGameStep(10);
				break;
			case 13:
				game.getPlayer().setSleeping(false);
				game.getPlayer().setCanMove(true);
				hud.showDialog(game.getString("step13"));
				nextGameStep();
				break;
			case 14:
				if(game.getPlayer().canFish() && !game.getPlayer().isFishing()) {
					hud.getActionButton().getLabel().setText(game.getString("fish"));
					hud.getActionButton().setWidth(hud.getActionButton().getPrefWidth());
					hud.getActionButton().setVisible(true);
					if(hud.isActionButtonPressed()) {
						hud.getActionButton().setVisible(false);
						hud.setActionButtonPressed(false);
						game.getPlayer().setFishing(true);
						game.getPlayer().setCanMove(false);
						delayNextGameStep(7);
					}
				} else {
					hud.getActionButton().setVisible(false);
				}
				break;
			case 15:
				game.getPlayer().setFishing(false);
				game.getPlayer().setCanMove(true);
				hud.showDialog(game.getString("step15"));
				game.setWoodToCollect(3);
				nextGameStep();
				break;
			case 16:
				if(game.getPlayer().getWoodCount() >= 3) {
					hud.showDialog(game.getString("step16"));
					game.setWoodToCollect(0);
					nextGameStep();
				}
				break;
			case 17:
				hud.getActionButton().getLabel().setText(game.getString("use_wood"));
				hud.getActionButton().setWidth(hud.getActionButton().getPrefWidth());
				hud.getActionButton().setPosition(hud.getActionButton().getX(), hud.getActionButton().getY());
				hud.getActionButton().setVisible(true);
				if(hud.isActionButtonPressed()) {
					fireObject = new GameObject(game.getPlayer().getBody().getPosition().x + 0.4f, game.getPlayer().getBody().getPosition().y, noFireTexture.getWidth() / 100f / 2, noFireTexture.getHeight() / 100f / 2, "fire", game.getWorld());
					game.getGameObjects().add(fireObject);
					hud.getActionButton().setVisible(false);
					hud.setActionButtonPressed(false);
					game.getPlayer().setCanMove(false);
					game.getPlayer().setWoodCount(game.getPlayer().getWoodCount() - 3);
					game.setGameStep(game.getGameStep() + 1);
				}
				break;
			case 18:
				hud.getActionButton().getLabel().setText(game.getString("set_fire"));
				hud.getActionButton().setWidth(hud.getActionButton().getPrefWidth());
				hud.getActionButton().setVisible(true);
				if(hud.isActionButtonPressed()) {
					fireObject.setActive(true);
					hud.getActionButton().setVisible(false);
					hud.setActionButtonPressed(false);
					game.getPlayer().setMatchCount(game.getPlayer().getMatchCount() - 1);
					game.setGameStep(game.getGameStep() + 1);
				}
				break;
			case 19:
				hud.getActionButton().getLabel().setText(game.getString("cook_and_eat"));
				hud.getActionButton().setWidth(hud.getActionButton().getPrefWidth());
				hud.getActionButton().setVisible(true);
				if(hud.isActionButtonPressed()) {
					game.getPlayer().setSanityLevel(game.getPlayer().getSanityLevel() + 15);
					game.getPlayer().setCanMove(true);
					hud.getActionButton().setVisible(false);
					hud.setActionButtonPressed(false);
					game.setGameStep(game.getGameStep() + 1);
				}
				break;
			case 20:
				fireObject.setActive(false);
				hud.showDialog(game.getString("step20"));
				nextGameStep();
				break;
			case 21:
				if(game.getPlayer().canGetWater()) {
					hud.getActionButton().getLabel().setText(game.getString("get_water"));
					hud.getActionButton().setWidth(hud.getActionButton().getPrefWidth());
					hud.getActionButton().setVisible(true);
					if(hud.isActionButtonPressed()) {
						hud.getActionButton().setVisible(false);
						hud.setActionButtonPressed(false);
						game.getPlayer().setHasWater(true);
						game.setGameStep(game.getGameStep() + 1);
					}
				} else {
					hud.getActionButton().setVisible(false);
				}
				break;
			case 22:
				if(game.getPlayer().hasWater()) {
					hud.getActionButton().getLabel().setText(game.getString("drink_water"));
					hud.getActionButton().setWidth(hud.getActionButton().getPrefWidth());
					hud.getActionButton().setVisible(true);
					if(hud.isActionButtonPressed()) {
						game.getPlayer().setSanityLevel(game.getPlayer().getSanityLevel() + 15);
						game.getPlayer().setHasWater(false);
						hud.getActionButton().setVisible(false);
						hud.setActionButtonPressed(false);
						game.setGameStep(game.getGameStep() + 1);
					}
				}
				break;
			case 23:
				hud.showDialog(game.getString("step23"));
				game.setWoodToCollect(10);
				game.setRopesToCollect(4);
				nextGameStep();
				break;
			case 24:
				if(game.getPlayer().getWoodCount() >= 10 && game.getPlayer().getRopeCount() >= 4) {
					game.setWoodToCollect(0);
					game.setRopesToCollect(0);
					hud.showDialog(game.getString("step24"));
					nextGameStep();
				}
				break;
			case 25:
				if(game.getPlayer().canFish()) {
					hud.getActionButton().getLabel().setText(game.getString("build"));
					hud.getActionButton().setWidth(hud.getActionButton().getPrefWidth());
					hud.getActionButton().setVisible(true);
					if(hud.isActionButtonPressed()) {
						hud.getActionButton().setVisible(false);
						hud.setActionButtonPressed(false);
						ferryObject = new GameObject(game.getPlayer().getBody().getPosition().x, game.getPlayer().getBody().getPosition().y - ferryTexture.getHeight() / 100f, ferryTexture.getWidth() / 100f / 2, ferryTexture.getHeight() / 100f / 2, "ferry", game.getWorld());
						game.getGameObjects().add(ferryObject);

						game.setGameStep(game.getGameStep() + 1);
					}
				} else {
					hud.getActionButton().setVisible(false);
				}
				break;
			case 26:
				if(game.getPlayer().canFish()) {
					hud.getActionButton().getLabel().setText(game.getString("escape"));
					hud.getActionButton().setWidth(hud.getActionButton().getPrefWidth());
					hud.getActionButton().setVisible(true);
					if(hud.isActionButtonPressed()) {
						hud.getActionButton().setVisible(false);
						hud.setActionButtonPressed(false);
						game.setGameStep(game.getGameStep() + 1);
					}
				} else {
					hud.getActionButton().setVisible(false);
				}
				break;
			case 27:
				hud.showDialog(game.getString("step27"));
				nextGameStep();
				break;
			default:
				break;
		}
	}

	public void handleContact() {
		game.getWorld().setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {

			}

			@Override
			public void endContact(Contact contact) {
				Object userData1 = contact.getFixtureA().getBody().getUserData();
				Object userData2 = contact.getFixtureB().getBody().getUserData();
				if(userData1 != null && userData2 != null) {
					GameObjectInfo data1 = (GameObjectInfo) userData1;
					GameObjectInfo data2 = (GameObjectInfo) userData2;
//					Gdx.app.log("DEBUG", String.valueOf(data1.type));
//					Gdx.app.log("DEBUG", String.valueOf(data2.type));
					if(data1.type == GameObjectType.FISHING) {
						game.getPlayer().setCanFish(false);
					}
					if(data2.type == GameObjectType.FISHING) {
						game.getPlayer().setCanFish(false);
					}
					if(data1.type == GameObjectType.WATER) {
						game.getPlayer().setCanGetWater(false);
					}
					if(data2.type == GameObjectType.WATER) {
						game.getPlayer().setCanGetWater(false);
					}
				}

			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				Object userData1 = contact.getFixtureA().getBody().getUserData();
				Object userData2 = contact.getFixtureB().getBody().getUserData();
				if(userData1 != null && userData2 != null) {
					GameObjectInfo data1 = (GameObjectInfo) userData1;
					GameObjectInfo data2 = (GameObjectInfo) userData2;
					if(data1.type == GameObjectType.COLLECTIBLE) {
						contact.setEnabled(false);
						game.getBodiesToBeCleared().add(contact.getFixtureA().getBody());
						game.getPlayer().setSanityLevel(game.getPlayer().getSanityLevel() + 5);
						game.getPlayer().setSpeed(game.getPlayer().getSpeed() + 0.003f);
					}
					if(data2.type == GameObjectType.COLLECTIBLE) {
						contact.setEnabled(false);
						game.getBodiesToBeCleared().add(contact.getFixtureB().getBody());
						game.getPlayer().setSanityLevel(game.getPlayer().getSanityLevel() + 5);
						game.getPlayer().setSpeed(game.getPlayer().getSpeed() + 0.003f);
					}
					if(data1.type == GameObjectType.TOOL) {
						if(game.getPlayer().isBackpackCollected()) {
							contact.setEnabled(false);
							game.getBodiesToBeCleared().add(contact.getFixtureA().getBody());
							game.getPlayer().setWoodCount(game.getPlayer().getWoodCount() + 1);
						}
					}
					if(data2.type == GameObjectType.TOOL) {
						if(game.getPlayer().isBackpackCollected()) {
							contact.setEnabled(false);
							game.getBodiesToBeCleared().add(contact.getFixtureA().getBody());
							game.getPlayer().setWoodCount(game.getPlayer().getWoodCount() + 1);
						}
					}
					if(data1.type == GameObjectType.ROPE) {
						if(game.getPlayer().isBackpackCollected()) {
							contact.setEnabled(false);
							game.getBodiesToBeCleared().add(contact.getFixtureA().getBody());
							game.getPlayer().setRopeCount(game.getPlayer().getRopeCount() + 1);
						}
					}
					if(data2.type == GameObjectType.ROPE) {
						if(game.getPlayer().isBackpackCollected()) {
							contact.setEnabled(false);
							game.getBodiesToBeCleared().add(contact.getFixtureA().getBody());
							game.getPlayer().setRopeCount(game.getPlayer().getRopeCount() + 1);
						}
					}
					if(data1.type == GameObjectType.BACKPACK) {
						contact.setEnabled(false);
						game.getBodiesToBeCleared().add(contact.getFixtureA().getBody());
						game.getPlayer().setBackpackCollected(true);
					}
					if(data2.type == GameObjectType.BACKPACK) {
						contact.setEnabled(false);
						game.getBodiesToBeCleared().add(contact.getFixtureB().getBody());
						game.getPlayer().setBackpackCollected(true);
					}
					if(data1.type == GameObjectType.FISHING) {
						contact.setEnabled(false);
						game.getPlayer().setCanFish(true);
					}
					if(data2.type == GameObjectType.FISHING) {
						contact.setEnabled(false);
						game.getPlayer().setCanFish(true);
					}
					if(data1.type == GameObjectType.WATER) {
						contact.setEnabled(false);
						game.getPlayer().setCanGetWater(true);
					}
					if(data2.type == GameObjectType.WATER) {
						contact.setEnabled(false);
						game.getPlayer().setCanGetWater(true);
					}
				}
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}
		});
	}


	public void dispose() {
		game.getWorld().dispose();
		game.getPlayer().getTexture().dispose();
		game.getPlayer().getBackpackTexture().dispose();
		shelterTexture.dispose();
		sleepingTexture.dispose();
		fireTexture.dispose();
		noFireTexture.dispose();
		shapeRenderer.dispose();
		hud.dispose();
	}
}