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

	private Player player;
	private GameUtil gameUtil;

	private GameObject shelterObject;
	private GameObject fireObject;

	private Texture noFireTexture;
	private Texture fireTexture;
	private Texture shelterTexture;


	private Hud hud;

	public int gameStep = 0;

	private Timer timer = new Timer();

	public GameClass(MentalSurvival game) {
		this.game = game;
	}

	@Override
	public void show() {
		gameUtil = new GameUtil();
		player = new Player(gameUtil.getWorld());

		shelterTexture = new Texture(Gdx.files.internal("fire.png"));
		fireTexture = new Texture(Gdx.files.internal("fire.png"));
		noFireTexture = new Texture(Gdx.files.internal("no_fire.png"));

		// Create SpriteBatch
		batch = new SpriteBatch();

		hud = new Hud(batch);

		hud.getActionButton().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				hud.setActionButtonPressed(true);
			};
		});

		// Create camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

		tiledMapRenderer = new OrthogonalTiledMapRenderer(gameUtil.getTiledMap(), 1 / 100f);
		debugRenderer = new Box2DDebugRenderer();

//		hud.showDialog("Nyt on kyllä ikävä tilanne! Ei auta muu kuin pitää pää kylmänä.", this);
		/* Game Step 1 - Tutorial
		*  Player needs to move a little using the game pad.
		*/
//		hud.showDialog("Liikkuaksesi saarella, liikuta peukalollasi \n vasemmalla puolla ruutua olevaa joystickiä.");


		gameUtil.getWorld().setContactListener(new ContactListener() {
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
						player.setCanFish(false);
					}
					if(data2.type == GameObjectType.FISHING) {
						player.setCanFish(false);
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
						player.setWoodCount(player.getWoodCount() + 1);
						player.setBackpackCollected(true);
					}
					if(data2.type == GameObjectType.TOOL) {
						contact.setEnabled(false);
						gameUtil.getBodiesToBeCleared().add(contact.getFixtureB().getBody());
						player.setWoodCount(player.getWoodCount() + 1);
						player.setBackpackCollected(true);
					}
					if(data1.type == GameObjectType.FISHING) {
						contact.setEnabled(false);
						player.setCanFish(true);
					}
					if(data2.type == GameObjectType.FISHING) {
						contact.setEnabled(false);
						player.setCanFish(true);
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

		hud.updateSanityBar(player.getSanityLevel());
		hud.updateBackpack(player.getWoodCount(), player.getMatchCount(), player.hasWater());

//		Gdx.app.log("DEBUG", "game step: " + gameStep);
		showGameStep(gameStep);

		// Render tiled map
		tiledMapRenderer.render();

		// Draw player texture
		batch.begin();
		batch.draw(player.getCurrentFrameTexture(), player.getBody().getPosition().x - player.getRadius(), player.getBody().getPosition().y - player.getRadius(), player.getRadius() * 2, player.getRadius() * 2);
		if(shelterObject != null) {
			batch.draw(shelterTexture, shelterObject.getX(), shelterObject.getY(), shelterObject.getWidth(), shelterObject.getHeight());
		}
		if(fireObject != null) {
			if(fireObject.isOnFire()) {
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

	public void setGameStep(int gameStep) {
		this.gameStep = gameStep;
	}

	public int getGameStep() {
		return gameStep;
	}

	public void nextGameStep() {
		hud.getOkButton().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setGameStep(getGameStep() + 1);
			};
		});
	}

	public void delayNextGameStep(float delay) {
		Timer.schedule(new Timer.Task(){
			@Override
			public void run() {
				setGameStep(getGameStep() + 1);
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
				if(player.isBackpackCollected()) {
					hud.showBackpack();
					setGameStep(getGameStep() + 1);
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
				if(player.getWoodCount() >= 3) {
					hud.showDialog("Olet nyt kerännyt tarpeeksi materiaalia majan rakentamiseen!");
					nextGameStep();
				}
				break;
			case 10:
				hud.getActionButton().getLabel().setText("BUILD");
				hud.getActionButton().setVisible(true);
				if(hud.isActionButtonPressed()) {
					shelterObject = new GameObject(gameUtil, player.getBody().getPosition().x + 0.4f, player.getBody().getPosition().y, shelterTexture.getWidth() / 100f / 2, shelterTexture.getHeight() / 100f / 2, "shelter");
					hud.getActionButton().setVisible(false);
					hud.setActionButtonPressed(false);
					player.setWoodCount(player.getWoodCount() - 3);
					setGameStep(getGameStep() + 1);
				}
				break;
			case 11:
				hud.getActionButton().getLabel().setText("SLEEP");
				hud.getActionButton().setVisible(true);
				if(hud.isActionButtonPressed()) {
					hud.getActionButton().setVisible(false);
					hud.setActionButtonPressed(false);
					setGameStep(getGameStep() + 1);
				}
				break;
			case 12:
				hud.showDialog("Tulipas yö nukuttua huonosti. Hirveä nälkäkin iski! Täytyy etsiä jotakin syötävää.");
				nextGameStep();
				break;
			case 13:
				if(player.canFish()) {
					hud.getActionButton().getLabel().setText("FISH");
					hud.getActionButton().setVisible(true);
					if(hud.isActionButtonPressed()) {
						hud.getActionButton().setVisible(false);
						hud.setActionButtonPressed(false);
						setGameStep(getGameStep() + 1);
					}
				} else {
					hud.getActionButton().setVisible(false);
				}
				break;
			case 14:
				hud.showDialog("Raaka kala ei ole hyväksi, täytyy laittaa erätaidot kehiin ja tehdä tuli.\n Kerää materiaalia tulentekoon.");
				nextGameStep();
				break;
			case 15:
				if(player.getWoodCount() >= 3) {
					hud.showDialog("Olet nyt kerännyt tarpeeksi materiaalia tulentekoon!");
					nextGameStep();
				}
				break;
			case 16:
				hud.getActionButton().getLabel().setText("USE WOOD");
				hud.getActionButton().setWidth(hud.getActionButton().getPrefWidth());
				hud.getActionButton().setPosition(hud.getActionButton().getX(), hud.getActionButton().getY());
				hud.getActionButton().setVisible(true);
				if(hud.isActionButtonPressed()) {
					fireObject = new GameObject(gameUtil, player.getBody().getPosition().x + 0.4f, player.getBody().getPosition().y, noFireTexture.getWidth() / 100f / 2, noFireTexture.getHeight() / 100f / 2, "fire");
					hud.getActionButton().setVisible(false);
					hud.setActionButtonPressed(false);
					player.setWoodCount(player.getWoodCount() - 3);
					setGameStep(getGameStep() + 1);
				}
				break;
			case 17:
				hud.getActionButton().getLabel().setText("SET FIRE");
				hud.getActionButton().setVisible(true);
				if(hud.isActionButtonPressed()) {
					fireObject.setOnFire(true);
					hud.getActionButton().setVisible(false);
					hud.setActionButtonPressed(false);
					setGameStep(getGameStep() + 1);
				}
				break;
			case 18:
				hud.getActionButton().getLabel().setText("COOK AND EAT");
				hud.getActionButton().setVisible(true);
				if(hud.isActionButtonPressed()) {
					player.setSanityLevel(player.getSanityLevel() + 20);
					hud.getActionButton().setVisible(false);
					hud.setActionButtonPressed(false);
					setGameStep(getGameStep() + 1);
				}
				break;
			case 19:
				hud.showDialog("Tekipäs syöminen hyvää! Nyt kyllä janottaa. Näissä olosuhteissa täytyy \npitää huoli nestetasapainosta \nEtsi juoksevan veden lähde.");
				nextGameStep();
			default:
				break;
		}
	}


	public void dispose() {
		gameUtil.getWorld().dispose();
		player.getTexture().dispose();
	}
}