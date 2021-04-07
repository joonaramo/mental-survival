package fi.tuni.tiko;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

class Player {
	private float radius = 0.15f;
	private int sanityLevel = 0;
	private int toolsCount = 0;
	private float speed = 0.015f;
	private boolean backpackCollected = false;

	private Texture texture;
	private Animation<TextureRegion> walkAnimation;
	private float stateTime;
	private TextureRegion currentFrameTexture;
	private Body body;
	public static boolean RIGHT = true;
	public static boolean LEFT = false;
	private boolean direction = RIGHT;



	public Player(World world) {
		texture = new Texture(Gdx.files.internal("walking_animation.png"));

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

	public int getToolsCount() {
		return toolsCount;
	}

	public int getSanityLevel() {
		return sanityLevel;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void setSanityLevel(int sanityLevel) {
		this.sanityLevel = sanityLevel;
	}

	public void setToolsCount(int toolsCount) {
		this.toolsCount = toolsCount;
	}

	public void setBackpackCollected(boolean backpackCollected) {
		this.backpackCollected = backpackCollected;
	}

	public boolean isBackpackCollected() {
		return backpackCollected;
	}

	public void movePlayer(JoystickControl joystickControl) {
//		Gdx.app.log("X", String.valueOf(playerBody.getPosition().x));
//		Gdx.app.log("SPEED", String.valueOf(PLAYER_SPEED));

//		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
//			changeDirection(LEFT);
//			walk();
//			getBody().setTransform(getBody().getPosition().x - getSpeed(), getBody().getPosition().y, getBody().getAngle());
//		}
//		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
//			changeDirection(RIGHT);
//			walk();
//			getBody().setTransform(getBody().getPosition().x + getSpeed(), getBody().getPosition().y, getBody().getAngle());
//		}
//		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
//			walk();
//			getBody().setTransform(getBody().getPosition().x, getBody().getPosition().y + getSpeed(), getBody().getAngle());
//		}
//		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
//			walk();
//			getBody().setTransform(getBody().getPosition().x, getBody().getPosition().y - getSpeed(), getBody().getAngle());
//		}
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
		TextureRegion[][] tmp = TextureRegion.split(getTexture(), tileWidth, tileHeight);

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
