package fi.tuni.mentalsurvival;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.google.gson.annotations.Expose;

/**
 * Player class contains all the methods and information about the player in the game.
 */
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
	private float walkingSoundPlayed = 0;
	private float eatingSoundPlayed = 0;
	private float drinkingSoundPlayed = 0;
	private long walkingSoundId;
	private long eatingSoundId;
	private long drinkingSoundId;

	private Texture texture;
	private Texture backpackTexture;
	private Texture fishingTexture;
	private Texture eatingTexture;
	private Texture drinkingTexture;
	private Animation<TextureRegion> walkAnimation;
	private float stateTime;
	private TextureRegion currentFrameTexture;

	private Body body;

	public static boolean RIGHT = true;
	public static boolean LEFT = false;
	private boolean direction = RIGHT;

	private boolean walking = false;
	private boolean fishing = false;
	private boolean eating = false;
	private boolean drinking = false;

	/**
	 * Constructor which is called to create the player and it's body to box2d.
	 * @param world the box2d world object
	 */
	public Player(World world) {
		texture = new Texture(Gdx.files.internal("images/walking_animation.png"));
		backpackTexture = new Texture(Gdx.files.internal("images/walking_animation_backpack.png"));
		fishingTexture = new Texture(Gdx.files.internal("images/fishing.png"));
		eatingTexture = new Texture(Gdx.files.internal("images/eating.png"));
		drinkingTexture = new Texture(Gdx.files.internal("images/drinking.png"));

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

	/**
	 * Get the player texture
	 * @return
	 */
	public Texture getTexture() {
		return texture;
	}

	/**
	 * Get player texture with backpack
	 * @return
	 */
	public Texture getBackpackTexture() {
		return backpackTexture;
	}

	/**
	 * Get current player texture
	 * @return texture; normal, with backpack, sleeping.
	 */
	public Texture getCurrentTexture() {
		if(backpackCollected) {
			return backpackTexture;
		}
		return texture;
	}

	/**
	 * Get current frame texture (animation)
	 * @return current frame's texture
	 */
	public TextureRegion getCurrentFrameTexture() {
		return currentFrameTexture;
	}

	/**
	 * Get player's box2d body
	 * @return player body
	 */
	public Body getBody() {
		return body;
	}

	/**
	 * Get player's speed
	 * @return player's speed (float)
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * Get player's radius
	 * @return player's radius (float)
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * Get players' wood count
	 * @return wood count (int)
	 */
	public int getWoodCount() {
		return woodCount;
	}

	/**
	 * Get player's rope count
	 * @return rope count (int)
	 */
	public int getRopeCount() {
		return ropeCount;
	}

	/**
	 * Get player's sanity level
	 * @return sanity level (int)
	 */
	public int getSanityLevel() {
		return sanityLevel;
	}

	/**
	 * Get player's match count
	 * @return match count (int)
	 */
	public int getMatchCount() {
		return matchCount;
	}

	/**
	 * Check if player has collected backpack from map
	 * @return true if collected, false if not
	 */
	public boolean isBackpackCollected() {
		return backpackCollected;
	}

	/**
	 * Check if player can currently fish
	 * @return true if can, false if not
	 */
	public boolean canFish() {
		return canFish;
	}

	/**
	 * Check if player can currently get water to his bottle
	 * @return true if can, false if not
	 */
	public boolean canGetWater() {
		return canGetWater;
	}

	/**
	 * Check if player currently has water in his bottle
	 * @return true if has, false if not
	 */
	public boolean hasWater() {
		return hasWater;
	}

	/**
	 * Check if player is currently sleeping
	 * @return true if is, false if is not
	 */
	public boolean isSleeping() {
		return sleeping;
	}

	/**
	 * Check if player is currently fishing
	 * @return true if is, false if not
	 */
	public boolean isFishing() {
		return fishing;
	}

	/**
	 * Check if player is currently fishing
	 * @return true if is, false if not
	 */
	public boolean isDrinking() {
		return drinking;
	}

	/**
	 * Check if player is currently eating
	 * @return true if is, false if not
	 */
	public boolean isEating() {
		return eating;
	}

	/**
	 * Check if player has moved in the game at all
	 * @return true if has, false if not
	 */
	public boolean hasMoved() {
		return hasMoved;
	}

	/**
	 * Set player sleeping-state
	 * @param sleeping sleeping-state of the player
	 */
	public void setSleeping(boolean sleeping) {
		this.sleeping = sleeping;
	}

	/**
	 * Set player can fish-state
	 * @param canFish can fish state of the player
	 */
	public void setCanFish(boolean canFish) {
		this.canFish = canFish;
	}

	/**
	 * Set the player's speed
	 * @param speed player's new speed
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * Set the player's sanity level
	 * @param sanityLevel player's new sanity level
	 */
	public void setSanityLevel(int sanityLevel) {
		if (sanityLevel < 0) {
			this.sanityLevel = 0;
		} else if (sanityLevel > 100) {
			this.sanityLevel = 100;
		} else {
			this.sanityLevel = sanityLevel;
		}
	}

	/**
	 * Set player's wood count
	 * @param woodCount player's new wood count
	 */
	public void setWoodCount(int woodCount) {
		this.woodCount = woodCount;
	}

	/**
	 * Set player's rope count
	 * @param ropeCount player's new rope count
	 */
	public void setRopeCount(int ropeCount) {
		this.ropeCount = ropeCount;
	}

	/**
	 * Set player's match count
	 * @param matchCount player's new match count
	 */
	public void setMatchCount(int matchCount) {
		this.matchCount = matchCount;
	}

	/**
	 * Set player has water state
	 * @param hasWater has water state of the player
	 */
	public void setHasWater(boolean hasWater) {
		this.hasWater = hasWater;
	}

	/**
	 * Set player backpack collected state
	 * @param backpackCollected backpack collected state of the player
	 */
	public void setBackpackCollected(boolean backpackCollected) {
		this.backpackCollected = backpackCollected;
	}

	/**
	 * Set player can move state
	 * @param canMove can move state of the player
	 */
	public void setCanMove(boolean canMove) {
		this.canMove = canMove;
	}

	/**
	 * Set player fishing-state
	 * @param fishing fishing-state of the player
	 */
	public void setFishing(boolean fishing) {
		this.fishing = fishing;
	}

	/**
	 * Set player eating-state
	 * @param eating eating-state of the player
	 */
	public void setEating(boolean eating) {
		this.eating = eating;
	}

	/**
	 * Set player drinking-state
	 * @param drinking drinking-state of the player
	 */
	public void setDrinking(boolean drinking) {
		this.drinking = drinking;
	}

	/**
	 * Set player can get water state
	 * @param canGetWater can get water state of the player
	 */
	public void setCanGetWater(boolean canGetWater) {
		this.canGetWater = canGetWater;
	}

	/**
	 * Draw the player's texture
	 *
	 * If player is sleeping, draw the sleeping texture, also if sleeping/drinking, use those textures. Use normal walking animation texture if doing nothing of those mentioned.
	 * @param batch sprite batch used to draw
	 */
	public void draw(SpriteBatch batch) {
		if(!sleeping) {
			if(fishing) {
				batch.draw(fishingTexture, body.getPosition().x - radius, body.getPosition().y - radius * 2, radius * 2, radius * 4.5f);
			} else if(eating) {
				batch.draw(eatingTexture, body.getPosition().x - radius, body.getPosition().y - radius, radius * 2, radius * 2);
			} else if(drinking) {
				batch.draw(drinkingTexture, body.getPosition().x - radius, body.getPosition().y - radius, radius * 2, radius * 2);
			} else {
				batch.draw(currentFrameTexture, body.getPosition().x - radius, body.getPosition().y - radius, radius * 2, radius * 2);
			}
		}
	}


	/**
	 * Move the player when the on-screen joystick is used.
	 * @param joystickControl on-screen joystick controller
	 */
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

	/**
	 * Play walking sound whenever player is in walking-state
	 * @param walkingSound
	 */
	public void playWalkingSound(Sound walkingSound) {
		int volume = GameUtil.getPreferences().getInteger("AUDIO_VOLUME", 1);
		if(walkingSoundPlayed > 0) {
			walkingSoundPlayed -= Gdx.graphics.getDeltaTime();
		}
		if(isWalking ) {
			if(walkingSoundPlayed <= 0) {
				walkingSoundId = walkingSound.play(volume);
				walkingSoundPlayed = 7;
			}
		} else {
			walkingSound.stop(walkingSoundId);
			walkingSoundPlayed = 0;
		}
	}

	/**
	 * Play eating sound whenever player is in eating-state
	 * @param eatingSound
	 */
	public void playEatingsound(Sound eatingSound) {
		int volume = GameUtil.getPreferences().getInteger("AUDIO_VOLUME", 1);
		if(eatingSoundPlayed > 0) {
			eatingSoundPlayed -= Gdx.graphics.getDeltaTime();
		}
		if(eating ) {
			if(eatingSoundPlayed <= 0) {
				eatingSoundId = eatingSound.play(volume);
				eatingSoundPlayed = 7;
			}
		} else {
			eatingSound.stop(eatingSoundId);
			eatingSoundPlayed = 0;
		}
	}

	/**
	 * Play drinking sound whenever player is in drinking-state
	 * @param drinkingSound
	 */
	public void playDrinkingSound(Sound drinkingSound) {
		int volume = GameUtil.getPreferences().getInteger("AUDIO_VOLUME", 1);
		if(drinkingSoundPlayed > 0) {
			drinkingSoundPlayed -= Gdx.graphics.getDeltaTime();
		}
		if(drinking ) {
			if(drinkingSoundPlayed <= 0) {
				drinkingSoundId = drinkingSound.play(volume);
				drinkingSoundPlayed = 7;
			}
		} else {
			drinkingSound.stop(drinkingSoundId);
			drinkingSoundPlayed = 0;
		}
	}

	/**
	 * Create the player's walking animation
	 */
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
		TextureRegion[] allFrames = fi.tuni.mentalsurvival.Util.toTextureArray( tmp, FRAME_COLS, FRAME_ROWS );

		walkAnimation = new Animation(12 / 60f, (Object[]) allFrames);

		currentFrameTexture = walkAnimation.getKeyFrame(stateTime, true);
	}

	/**
	 * Use the walk animation (get the correct frame from the walking texture)
	 */
	public void walk() {
		// stateTime was initialized to 0.0f
		stateTime += Gdx.graphics.getDeltaTime();

		// stateTime is used to calculate the next frame
		// frameDuration!
		currentFrameTexture = walkAnimation.getKeyFrame(stateTime, true);
	}

	/**
	 * Change player's direction (flip the texture)
	 * @param dir
	 */
	public void changeDirection(boolean dir) {
		if(dir != direction) {
			direction = dir;

			// Reverse all textureregions in the sheet.
			Util.flip(walkAnimation);
		}
	}


}
