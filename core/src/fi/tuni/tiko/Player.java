package fi.tuni.tiko;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import static fi.tuni.tiko.GameUtil.getPreferences;

class Player {
	private float radius = 0.25f;
	@Expose
	private int sanityLevel = 0;
	@Expose
	private int woodCount = 0;
	@Expose
	private int ropeCount = 0;
	@Expose
	private int matchCount = 5;
	@Expose
	private boolean hasWater = false;
	@Expose
	private boolean sleeping = false;
	@Expose
	private float speed = 0.015f;
	@Expose
	private boolean backpackCollected = false;
	@Expose
	private boolean canFish = false;
	@Expose
	private boolean canMove = true;
	@Expose
	private boolean canGetWater = false;
	@Expose
	private float positionX;
	@Expose
	private float positionY;

	private boolean hasMoved = false;

	private boolean isWalking = false;
	private float soundPlayed = 0;
	private long soundId;

	private Texture texture;
	private Texture backpackTexture;
	private Texture fishingTexture;
	private Animation<TextureRegion> walkAnimation;
	private float stateTime;
	private TextureRegion currentFrameTexture;

	private Body body;

	public static boolean RIGHT = true;
	public static boolean LEFT = false;
	private boolean direction = RIGHT;

	private boolean walking = false;
	private boolean fishing = false;



	public Player(World world) {
		texture = new Texture(Gdx.files.internal("walking_animation.png"));
		backpackTexture = new Texture(Gdx.files.internal("walking_animation_backpack.png"));
		fishingTexture = new Texture(Gdx.files.internal("fishing.png"));

		createWalkAnimation();

		BodyDef myBodyDef = new BodyDef();
		myBodyDef.type = BodyDef.BodyType.DynamicBody;

		myBodyDef.position.set(7.55f + radius, 32 / 100f + radius);

		GameObjectInfo gameObject = new GameObjectInfo(GameObjectType.PLAYER, 32 / 100f + radius, 32 / 100f + radius);

		body = world.createBody(myBodyDef);
		body.setGravityScale(0);
		body.setUserData(gameObject);
		FixtureDef playerFixtureDef = new FixtureDef();

		playerFixtureDef.density     = 2;
		playerFixtureDef.restitution = 0.5f;
		playerFixtureDef.friction    = 0.5f;

		CircleShape circleshape = new CircleShape();
		circleshape.setRadius(radius);

		playerFixtureDef.shape = circleshape;
		body.createFixture(playerFixtureDef);
	}

	public Texture getTexture() {
		return texture;
	}

	public Texture getBackpackTexture() {
		return backpackTexture;
	}

	public Texture getCurrentTexture() {
		if(backpackCollected) {
			return backpackTexture;
		}
		return texture;
	}

	public TextureRegion getCurrentFrameTexture() {
		return currentFrameTexture;
	}

	public Body getBody() {
		return body;
	}

	public float getSpeed() {
		return speed;
	}

	public float getRadius() {
		return radius;
	}

	public int getWoodCount() {
		return woodCount;
	}

	public int getRopeCount() {
		return ropeCount;
	}

	public int getSanityLevel() {
		return sanityLevel;
	}

	public int getMatchCount() {
		return matchCount;
	}

	public boolean isBackpackCollected() {
		return backpackCollected;
	}

	public boolean canFish() {
		return canFish;
	}

	public boolean canGetWater() {
		return canGetWater;
	}

	public boolean hasWater() {
		return hasWater;
	}

	public boolean isSleeping() {
		return sleeping;
	}

	public boolean isFishing() {
		return fishing;
	}

	public boolean hasMoved() {
		return hasMoved;
	}

	public void setSleeping(boolean sleeping) {
		this.sleeping = sleeping;
	}

	public void setCanFish(boolean canFish) {
		this.canFish = canFish;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void setSanityLevel(int sanityLevel) {
		if (sanityLevel < 0) {
			this.sanityLevel = 0;
		} else if (sanityLevel > 100) {
			this.sanityLevel = 100;
		} else {
			this.sanityLevel = sanityLevel;
		}
	}

	public void setWoodCount(int woodCount) {
		this.woodCount = woodCount;
	}

	public void setRopeCount(int ropeCount) {
		this.ropeCount = ropeCount;
	}

	public void setMatchCount(int matchCount) {
		this.matchCount = matchCount;
	}

	public void setHasWater(boolean hasWater) {
		this.hasWater = hasWater;
	}

	public void setBackpackCollected(boolean backpackCollected) {
		this.backpackCollected = backpackCollected;
	}

	public void setCanMove(boolean canMove) {
		this.canMove = canMove;
	}

	public void setFishing(boolean fishing) {
		this.fishing = fishing;
	}

	public void setCanGetWater(boolean canGetWater) {
		this.canGetWater = canGetWater;
	}

	public void draw(SpriteBatch batch) {
		if(!sleeping) {
			if(fishing) {
				batch.draw(fishingTexture, body.getPosition().x - radius, body.getPosition().y - radius * 2, radius * 2, radius * 4.5f);
			} else {
				batch.draw(currentFrameTexture, body.getPosition().x - radius, body.getPosition().y - radius, radius * 2, radius * 2);
			}
		}
	}


	public void movePlayer(JoystickControl joystickControl) {
		if(canMove) {
			float velX = 0, velY = 0;

			if(joystickControl.getTouchpad().isTouched()) {
				if(!hasMoved) {
					Timer.schedule(new Timer.Task(){
						@Override
						public void run() {
							Timer.instance().clear();
							hasMoved = true;
						}
					}, 3);
				}
				velX = joystickControl.getTouchpad().getKnobPercentX();
				velY = joystickControl.getTouchpad().getKnobPercentY();
				if(velX > 0) {
					changeDirection(RIGHT);
				}
				if(velX < 0) {
					changeDirection(LEFT);
				}
				walk();
				isWalking = true;
			} else {
				isWalking = false;
			}

			getBody().setLinearVelocity(velX, velY);
			positionX = getBody().getPosition().x;
			positionY = getBody().getPosition().y;
		}
	}

	public void playSound(Sound walkingSound) {
		int volume = getPreferences().getInteger("AUDIO_VOLUME", 1);
		if(soundPlayed > 0) {
			soundPlayed -= Gdx.graphics.getDeltaTime();
		}
		if(isWalking) {
			if(soundPlayed <= 0) {
				soundId = walkingSound.play(volume);
				soundPlayed = 7;
			}
		} else {
			walkingSound.stop(soundId);
			soundPlayed = 0;
		}
	}

	public void createWalkAnimation() {
		final int FRAME_COLS = 2;
		final int FRAME_ROWS = 1;

		/** CREATE THE WALK ANIM **/

		// Calculate the tile width from the sheet
		int tileWidth = getTexture().getWidth() / FRAME_COLS;

		// Calculate the tile height from the sheet
		int tileHeight = getTexture().getHeight() / FRAME_ROWS;

		// Create 2D array from the texture (REGIONS of a TEXTURE).
		TextureRegion[][] tmp = TextureRegion.split(getCurrentTexture(), tileWidth, tileHeight);

		// Transform the 2D array to 1D
		TextureRegion[] allFrames = Util.toTextureArray( tmp, FRAME_COLS, FRAME_ROWS );

		walkAnimation = new Animation(12 / 60f, (Object[]) allFrames);

		currentFrameTexture = walkAnimation.getKeyFrame(stateTime, true);
	}

	public void walk() {
		// stateTime was initialized to 0.0f
		stateTime += Gdx.graphics.getDeltaTime();

		// stateTime is used to calculate the next frame
		// frameDuration!
		currentFrameTexture = walkAnimation.getKeyFrame(stateTime, true);
	}

	public void changeDirection(boolean dir) {
		if(dir != direction) {
			direction = dir;

			// Reverse all textureregions in the sheet.
			Util.flip(walkAnimation);
		}
	}


}
