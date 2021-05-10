package fi.tuni.mentalsurvival;

/**
 * This class contains info about the bodies created in our tiled map.
 */
class GameObjectInfo {
	public float x;
	public float y;
	GameObjectType type;

	/**
	 * Constructor which creates the game object with info
	 * @param type the type of the object
	 * @param x the x position of the object
	 * @param y the y position of the object
	 */
	public GameObjectInfo(GameObjectType type, float x, float y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
}
