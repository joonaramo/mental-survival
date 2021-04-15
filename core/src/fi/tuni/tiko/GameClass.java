package fi.tuni.tiko;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
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
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.w3c.dom.Text;
import org.w3c.dom.css.Rect;

import java.sql.Time;


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


	private GameObject shelterObject;
	private GameObject fireObject;

	private Texture noFireTexture;
	private Texture fireTexture;
	private Texture shelterTexture;
	private Texture sleepingTexture;

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

		// Create SpriteBatch
		batch = new SpriteBatch();

		GameUtil.loadGame(game);

		GameUtil.clearOldBodies(game);

		hud = new Hud(batch, game);

		hud.getActionButton().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				hud.setActionButtonPressed(true);
			};
		});

		// Create camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

		tiledMapRenderer = new OrthogonalTiledMapRenderer(game.getTiledMap(), 1 / 100f);
		debugRenderer = new Box2DDebugRenderer();

//		hud.showDialog("Nyt on kyllä ikävä tilanne! Ei auta muu kuin pitää pää kylmänä.", this);
		/* Game Step 1 - Tutorial
		*  Player needs to move a little using the game pad.
		*/
//		hud.showDialog("Liikkuaksesi saarella, liikuta peukalollasi \n vasemmalla puolla ruutua olevaa joystickiä.");


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

				}

			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				Object userData1 = contact.getFixtureA().getBody().getUserData();
				Object userData2 = contact.getFixtureB().getBody().getUserData();
				if(userData1 != null && userData2 != null) {
					GameObjectInfo data1 = (GameObjectInfo) userData1;
					GameObjectInfo data2 = (GameObjectInfo) userData2;
//					Gdx.app.log("DEBUG", String.valueOf(data1.type));
//					Gdx.app.log("DEBUG", String.valueOf(data2.type));

					if(data1.type == GameObjectType.COLLECTIBLE) {
						contact.setEnabled(false);
						game.getBodiesToBeCleared().add(contact.getFixtureA().getBody());
						game.getClearedBodies().add(contact.getFixtureA().getBody());
						game.getPlayer().setSanityLevel(game.getPlayer().getSanityLevel() + 25);
						game.getPlayer().setSpeed(game.getPlayer().getSpeed() + 0.003f);
					}
					if(data2.type == GameObjectType.COLLECTIBLE) {
						contact.setEnabled(false);
						game.getBodiesToBeCleared().add(contact.getFixtureB().getBody());
						game.getClearedBodies().add(contact.getFixtureB().getBody());
						game.getPlayer().setSanityLevel(game.getPlayer().getSanityLevel() + 25);
						game.getPlayer().setSpeed(game.getPlayer().getSpeed() + 0.003f);
					}
					if(data1.type == GameObjectType.TOOL) {
						if(game.getPlayer().isBackpackCollected()) {
							contact.setEnabled(false);
							game.getBodiesToBeCleared().add(contact.getFixtureA().getBody());
							game.getClearedBodies().add(contact.getFixtureA().getBody());
							game.getPlayer().setWoodCount(game.getPlayer().getWoodCount() + 1);
						}
					}
					if(data2.type == GameObjectType.TOOL) {
						if(game.getPlayer().isBackpackCollected()) {
							contact.setEnabled(false);
							game.getBodiesToBeCleared().add(contact.getFixtureA().getBody());
							game.getClearedBodies().add(contact.getFixtureA().getBody());
							game.getPlayer().setWoodCount(game.getPlayer().getWoodCount() + 1);
						}
					}
					if(data1.type == GameObjectType.BACKPACK) {
						contact.setEnabled(false);
						game.getBodiesToBeCleared().add(contact.getFixtureA().getBody());
						game.getClearedBodies().add(contact.getFixtureA().getBody());
						game.getPlayer().setBackpackCollected(true);
					}
					if(data2.type == GameObjectType.BACKPACK) {
						contact.setEnabled(false);
						game.getBodiesToBeCleared().add(contact.getFixtureB().getBody());
						game.getClearedBodies().add(contact.getFixtureB().getBody());
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
			debugRenderer.render(game.getWorld(), camera.combined);
		}

		tiledMapRenderer.setView(camera);

		// Move player and camera
		game.getPlayer().movePlayer(hud.getJoystickControl());
		moveCamera();
		camera.update();

		hud.updateSanityBar(game.getPlayer().getSanityLevel());
		hud.updateBackpack(game.getPlayer().getWoodCount(), game.getPlayer().getMatchCount(), game.getPlayer().hasWater());

//		Gdx.app.log("DEBUG", "game step: " + gameStep);
		showGameStep(game.getGameStep());

		// Render tiled map
		tiledMapRenderer.render();

		// Draw player texture
		batch.begin();
		game.getPlayer().draw(batch);

		if(shelterObject != null) {
			if(game.getPlayer().isSleeping()) {
				batch.draw(sleepingTexture, shelterObject.getX(), shelterObject.getY(), shelterObject.getWidth(), shelterObject.getHeight());
			} else {
				batch.draw(shelterTexture, shelterObject.getX(), shelterObject.getY(), shelterObject.getWidth(), shelterObject.getHeight());
			}
		}
		if(fireObject != null) {
			if(fireObject.isActive()) {
				batch.draw(fireTexture, fireObject.getX(), fireObject.getY(), fireObject.getWidth(), fireObject.getHeight());
			} else {
				batch.draw(noFireTexture, fireObject.getX(), fireObject.getY(), fireObject.getWidth(), fireObject.getHeight());
			}
		}
		batch.end();

		// Draw hud
		batch.setProjectionMatrix(hud.getStage().getCamera().combined);
		hud.getStage().act(delta);
		hud.getStage().draw();

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
				hud.showDialog("Onpas kiperä tilanne! Nyt täytyy pitää pää kylmänä.");
				//Let's say you want to create a body in the center of your screen
				nextGameStep();
				break;
			case 1:
				hud.showDialog("Liikkuaksesi saarella, liikuta peukalollasi \nvasemmalla puolella ruutua olevaa joystickiä.");
				nextGameStep();
				break;
			case 2:
				delayNextGameStep(5);
				break;
			case 3:
				hud.showDialog("Juuri noin! Etsi seuraavaksi itsellesi reppu, jotta voit kerätä saarelta tarvikkeita.");
				nextGameStep();
				break;
			case 4:
				if(game.getPlayer().isBackpackCollected()) {
					hud.showBackpack();
					game.getPlayer().createWalkAnimation();
					game.getPlayer().changeDirection(true);
					game.setGameStep(game.getGameStep() + 1);
				}
				break;
			case 5:
				hud.showDialog("Nyt sinulta löytyy reppu! Sen sisälläkin näyttäisi jo olevan jotakin!");
				nextGameStep();
				break;
			case 6:
				hud.showDialog("Voit nyt vapaasti kiertää saarta ympäri ja etsiä tarpeellisia esineitä!");
				nextGameStep();
				break;
			case 7:
				delayNextGameStep(20);
				break;
			case 8:
				hud.showDialog("Alkaa olla myöhä ja sinua väsyttää. Rakenna itsellesi yöpymispaikka, jotta saat tarpeeksi lepoa!");
				nextGameStep();
				break;
			case 9:
				if(game.getPlayer().getWoodCount() >= 3) {
					hud.showDialog("Olet nyt kerännyt tarpeeksi materiaalia majan rakentamiseen!");
					nextGameStep();
				}
				break;
			case 10:
				hud.getActionButton().getLabel().setText("BUILD");
				hud.getActionButton().setVisible(true);
				if(hud.isActionButtonPressed()) {
					shelterObject = new GameObject(game.getPlayer().getBody().getPosition().x + 0.4f, game.getPlayer().getBody().getPosition().y, shelterTexture.getWidth() / 100f / 2, shelterTexture.getHeight() / 100f / 2, "shelter", game.getWorld());
					hud.getActionButton().setVisible(false);
					hud.setActionButtonPressed(false);
					game.getPlayer().setWoodCount(game.getPlayer().getWoodCount() - 3);
					game.setGameStep(game.getGameStep() + 1);
				}
				break;
			case 11:
				hud.getActionButton().getLabel().setText("SLEEP");
				hud.getActionButton().setVisible(true);
				if(hud.isActionButtonPressed()) {
					hud.getActionButton().setVisible(false);
					hud.setActionButtonPressed(false);
					game.getPlayer().setSleeping(true);
					game.setGameStep(game.getGameStep() + 1);
				}
				break;
			case 12:
				delayNextGameStep(10);
				break;
			case 13:
				game.getPlayer().setSleeping(false);
				hud.showDialog("Tulipas yö nukuttua huonosti. Hirveä nälkäkin iski! Täytyy etsiä jotakin syötävää.");
				nextGameStep();
				break;
			case 14:
				if(game.getPlayer().canFish()) {
					hud.getActionButton().getLabel().setText("FISH");
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
			case 15:
				hud.showDialog("Raaka kala ei ole hyväksi, täytyy laittaa erätaidot kehiin ja tehdä tuli.\n Kerää materiaalia tulentekoon.");
				nextGameStep();
				break;
			case 16:
				if(game.getPlayer().getWoodCount() >= 3) {
					hud.showDialog("Olet nyt kerännyt tarpeeksi materiaalia tulentekoon!");
					nextGameStep();
				}
				break;
			case 17:
				hud.getActionButton().getLabel().setText("USE WOOD");
				hud.getActionButton().setWidth(hud.getActionButton().getPrefWidth());
				hud.getActionButton().setPosition(hud.getActionButton().getX(), hud.getActionButton().getY());
				hud.getActionButton().setVisible(true);
				if(hud.isActionButtonPressed()) {
					fireObject = new GameObject(game.getPlayer().getBody().getPosition().x + 0.4f, game.getPlayer().getBody().getPosition().y, noFireTexture.getWidth() / 100f / 2, noFireTexture.getHeight() / 100f / 2, "fire", game.getWorld());
					hud.getActionButton().setVisible(false);
					hud.setActionButtonPressed(false);
					game.getPlayer().setWoodCount(game.getPlayer().getWoodCount() - 3);
					game.setGameStep(game.getGameStep() + 1);
				}
				break;
			case 18:
				hud.getActionButton().getLabel().setText("SET FIRE");
				hud.getActionButton().setVisible(true);
				if(hud.isActionButtonPressed()) {
					fireObject.setActive(true);
					hud.getActionButton().setVisible(false);
					hud.setActionButtonPressed(false);
					game.setGameStep(game.getGameStep() + 1);
				}
				break;
			case 19:
				hud.getActionButton().getLabel().setText("COOK AND EAT");
				hud.getActionButton().setVisible(true);
				if(hud.isActionButtonPressed()) {
					game.getPlayer().setSanityLevel(game.getPlayer().getSanityLevel() + 20);
					hud.getActionButton().setVisible(false);
					hud.setActionButtonPressed(false);
					game.setGameStep(game.getGameStep() + 1);
				}
				break;
			case 20:
				fireObject.setActive(false);
				hud.showDialog("Tekipäs syöminen hyvää! Nyt kyllä janottaa. Näissä olosuhteissa täytyy \npitää huoli nestetasapainosta \nEtsi juoksevan veden lähde.");
				nextGameStep();
				break;
			case 21:
				if(game.getPlayer().canFish()) {
					hud.getActionButton().getLabel().setText("GET WATER");
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
					hud.getActionButton().getLabel().setText("DRINK WATER");
					hud.getActionButton().setVisible(true);
					if(hud.isActionButtonPressed()) {
						game.getPlayer().setSanityLevel(game.getPlayer().getSanityLevel() + 20);
						game.getPlayer().setHasWater(false);
						hud.getActionButton().setVisible(false);
						hud.setActionButtonPressed(false);
						game.setGameStep(game.getGameStep() + 1);
					}
				}
				break;
			default:
				break;
		}
	}


	public void dispose() {
		game.getWorld().dispose();
		game.getPlayer().getTexture().dispose();
		game.getPlayer().getBackpackTexture().dispose();
		shelterTexture.dispose();
		sleepingTexture.dispose();
		fireTexture.dispose();
		noFireTexture.dispose();
	}
}