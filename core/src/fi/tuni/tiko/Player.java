package fi.tuni.tiko;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

class Player {
	private float radius = 0.15f;
	@Expose
	private int sanityLevel = 0;
	@Expose
	private int woodCount = 0;
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

	private Texture texture;
	private Texture backpackTexture;
	private Animation<TextureRegion> walkAnimation;
	private float stateTime;
	private TextureRegion currentFrameTexture;

	private Body body;

	public static boolean RIGHT = true;
	public static boolean LEFT = false;
	private boolean direction = RIGHT;



	public Player(World world) {
		texture = new Texture(Gdx.files.internal("walking_animation.png"));
		backpackTexture = new Texture(Gdx.files.internal("walking_animation_backpack.png"));

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

	public boolean hasWater() {
		return hasWater;
	}

	public boolean isSleeping() {
		return sleeping;
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
		this.sanityLevel = sanityLevel;
	}

	public void setWoodCount(int woodCount) {
		this.woodCount = woodCount;
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

	public void draw(SpriteBatch batch) {
		if(!sleeping) {
			batch.draw(currentFrameTexture, body.getPosition().x - radius, body.getPosition().y - radius, radius * 2, radius * 2);
		}
	}


	public void movePlayer(JoystickControl joystickControl) {
		float velX = 0, velY = 0;

		if(joystickControl.getTouchpad().isTouched()) {
			velX = joystickControl.getTouchpad().getKnobPercentX();
			velY = joystickControl.getTouchpad().getKnobPercentY();
			if(velX > 0) {
				changeDirection(RIGHT);
			}
			if(velX < 0) {
				changeDirection(LEFT);
			}
			walk();
		}

		getBody().setLinearVelocity(velX, velY);
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
