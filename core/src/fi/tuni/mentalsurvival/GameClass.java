package fi.tuni.mentalsurvival;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;

/**
 * This class contains rendering of the game, HUD, sounds, etc. Also has the game progression logic in it.
 */
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

	private double accumulator = 0;
	private float TIME_STEP = 1 / 60f;

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
	private Sound eatingSound;
	private Sound drinkingSound;

	private float currentAlpha = 0;

	private Hud hud;

	/**
	 * Constructor which is called to assign the game instance to it's variable so it can be accessed.
	 * @param game the game object
	 */
	public GameClass(MentalSurvival game) {
		this.game = game;
	}

	/**
	 * Method which is called when the gameplay screen is opened, creates the textures, HUD, and camera for the game.
	 */
	@Override
	public void show() {
		shelterTexture = new Texture(Gdx.files.internal("images/shelter.png"));
		sleepingTexture = new Texture(Gdx.files.internal("images/shelter_player.png"));
		fireTexture = new Texture(Gdx.files.internal("images/fire.png"));
		noFireTexture = new Texture(Gdx.files.internal("images/no_fire.png"));
		ferryTexture = new Texture(Gdx.files.internal("images/ferry.png"));

		walkingSound = Gdx.audio.newSound(Gdx.files.internal("audio/walking.mp3"));
		eatingSound = Gdx.audio.newSound(Gdx.files.internal("audio/eating.mp3"));
		drinkingSound = Gdx.audio.newSound(Gdx.files.internal("audio/drinking.mp3"));

		// Create SpriteBatch
		batch = new SpriteBatch();

		// Add game objects to world when game is resumed
		for(GameObject gameObject: game.getGameObjects()) {
			if(gameObject.getUserData() == "shelter") {
				shelterObject = new fi.tuni.mentalsurvival.GameObject(gameObject.getX(), gameObject.getY(), gameObject.getWidth(), gameObject.getHeight(), gameObject.getUserData(), game.getWorld());
			}
			if(gameObject.getUserData() == "fire") {
				fireObject = new fi.tuni.mentalsurvival.GameObject(gameObject.getX(), gameObject.getY(), gameObject.getWidth(), gameObject.getHeight(), gameObject.getUserData(), game.getWorld());
			}
			if(gameObject.getUserData() == "ferry") {
				ferryObject = new fi.tuni.mentalsurvival.GameObject(gameObject.getX(), gameObject.getY(), gameObject.getWidth(), gameObject.getHeight(), gameObject.getUserData(), game.getWorld());
			}
		}


		// Create camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

		// Create HUD
		hud = new Hud(game);

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

	/**
	 * Give a background color that fills the screen.
	 * @param r red color value
	 * @param g green color value
	 * @param b blue color value
	 */
	private void clearScreen(float r, float g, float b) {
		Gdx.gl.glClearColor(r, g, b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	/**
	 * Render the game including textures, tiled map, hud. Also update the HUD.
	 * @param delta delta value which is based on player's FPS.
	 */
	@Override
	public void render(float delta) {
		batch.setProjectionMatrix(camera.combined);

		clearScreen(0, 0, 0);

		if(DEBUG_PHYSICS) {
			debugRenderer.render(game.getWorld(), camera.combined);
		}

		tiledMapRenderer.setView(camera);

		// Move player and camera, play sound when moving/eating/drinking
		game.getPlayer().movePlayer(hud.getJoystickControl());
		game.getPlayer().playWalkingSound(walkingSound);
		game.getPlayer().playEatingsound(eatingSound);
		game.getPlayer().playDrinkingSound(drinkingSound);
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

		// Draw objects the player has built
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

		// Draw a fullscreen rectangle that dims, representing player sleeping
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
		fi.tuni.mentalsurvival.GameUtil.clearBodies(game);

		doPhysicsStep(Gdx.graphics.getDeltaTime());
	}

	/**
	 * Make box2d world's physics step forward
	 * @param deltaTime delta value which is based on player's FPS.
	 */
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

	/**
	 * Move camera so the player is always centered on the screen.
	 */
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

	/**
	 * Go to the next game step weh button is clicked.
	 */
	public void nextGameStep() {
		hud.getOkButton().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setGameStep(game.getGameStep() + 1);
			};
		});
	}

	/**
	 * Go to the next game step after specified delay
	 * @param delay delay before next game step (in seconds)
	 */
	public void delayNextGameStep(float delay) {
		Timer.schedule(new Timer.Task(){
			@Override
			public void run() {
				game.setGameStep(game.getGameStep() + 1);
				Timer.instance().clear();
			}
		}, delay);
	}

	/**
	 * This method includes the game progression logic. Everytime player has made the correct play and can move on, next game step is shown.
	 * @param gameStep the current game step to be shown
	 */
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
				Timer.schedule(new Timer.Task(){
					@Override
					public void run() {
						if(!game.getPlayer().isBackpackCollected()) {
							hud.showDialog(game.getString("step4"));
						}
						Timer.instance().clear();
					}
				}, 30);
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
					shelterObject = new fi.tuni.mentalsurvival.GameObject(game.getPlayer().getBody().getPosition().x + 0.4f, game.getPlayer().getBody().getPosition().y, shelterTexture.getWidth() / 100f / 2, shelterTexture.getHeight() / 100f / 2, "shelter", game.getWorld());
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
					fireObject = new fi.tuni.mentalsurvival.GameObject(game.getPlayer().getBody().getPosition().x + 0.4f, game.getPlayer().getBody().getPosition().y, noFireTexture.getWidth() / 100f / 2, noFireTexture.getHeight() / 100f / 2, "fire", game.getWorld());
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
				if(!game.getPlayer().isEating()) {
					hud.getActionButton().getLabel().setText(game.getString("cook_and_eat"));
					hud.getActionButton().setWidth(hud.getActionButton().getPrefWidth());
					hud.getActionButton().setVisible(true);
					if(hud.isActionButtonPressed()) {
						game.getPlayer().setSanityLevel(game.getPlayer().getSanityLevel() + 15);
						hud.getActionButton().setVisible(false);
						hud.setActionButtonPressed(false);
						game.getPlayer().setEating(true);
						delayNextGameStep(7);
					}
				}
				break;
			case 20:
				game.getPlayer().setCanMove(true);
				game.getPlayer().setEating(false);
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
				if(game.getPlayer().hasWater() && !game.getPlayer().isDrinking()) {
					hud.getActionButton().getLabel().setText(game.getString("drink_water"));
					hud.getActionButton().setWidth(hud.getActionButton().getPrefWidth());
					hud.getActionButton().setVisible(true);
					if(hud.isActionButtonPressed()) {
						hud.getActionButton().setVisible(false);
						hud.setActionButtonPressed(false);
						game.getPlayer().setSanityLevel(game.getPlayer().getSanityLevel() + 15);
						game.getPlayer().setHasWater(false);
						game.getPlayer().setCanMove(false);
						game.getPlayer().setDrinking(true);
						delayNextGameStep(7);
					}
				}
				break;
			case 23:
				game.getPlayer().setCanMove(true);
				game.getPlayer().setDrinking(false);
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
				if(game.getPlayer().getSanityLevel() > 80) {
					hud.showDialog(game.getFormattedString("step27_good", game.getPlayer().getSanityLevel()));
				} else if(game.getPlayer().getSanityLevel() > 60) {
					hud.showDialog(game.getFormattedString("step27_neutral", game.getPlayer().getSanityLevel()));
				} else {
					hud.showDialog(game.getFormattedString("step27_bad", game.getPlayer().getSanityLevel()));
				}
				nextGameStep();
				break;
			case 28:
				game.setScreen(new EndScreen(game));
				break;
			default:
				break;
		}
	}

	/**
	 * This method handles the contact between two box2d bodies in the game.
	 */
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
					fi.tuni.mentalsurvival.GameObjectInfo data1 = (fi.tuni.mentalsurvival.GameObjectInfo) userData1;
					fi.tuni.mentalsurvival.GameObjectInfo data2 = (fi.tuni.mentalsurvival.GameObjectInfo) userData2;

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
					fi.tuni.mentalsurvival.GameObjectInfo data1 = (fi.tuni.mentalsurvival.GameObjectInfo) userData1;
					fi.tuni.mentalsurvival.GameObjectInfo data2 = (fi.tuni.mentalsurvival.GameObjectInfo) userData2;
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

	/**
	 * When screen is resized, resize the HUD as well so it looks normal again.
	 * @param width
	 * @param height
	 */
	@Override
	public void resize(int width, int height) {
		hud.resize(width, height);
	}

	/**
	 * Dispose all used textures to prevent memory leaks.
	 */
	@Override
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