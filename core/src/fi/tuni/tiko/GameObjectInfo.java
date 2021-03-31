package fi.tuni.tiko;

class GameObjectInfo {
	public float x;
	public float y;
	GameObjectType type;

	public GameObjectInfo(GameObjectType type, float x, float y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
}
