package fi.tuni.mentalsurvival;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * This class contains some utility methods that are not directly related to the gameplay itself.
 */
public class Util {
	public static Rectangle scaleRect(Rectangle r, float scale) {
		Rectangle rectangle = new Rectangle();
		rectangle.x      = r.x * scale;
		rectangle.y      = r.y * scale;
		rectangle.width  = r.width * scale;
		rectangle.height = r.height * scale;
		return rectangle;
	}

	/**
	 * This method flips a TextureRegion, so the player animation looks like it changes its' direction
	 * @param animation the TextureRegion to be flipped
	 */
	public static void flip(Animation<TextureRegion> animation) {
		TextureRegion[] regions = animation.getKeyFrames();
		for(TextureRegion r : regions) {
			r.flip(true, false);
		}
	}

	/**
	 * This method changes a 2d TextureRegion array to 1d array of its' frames
	 * @param tr array to transform
	 * @param cols amount of columns on the texture
	 * @param rows amount of rows on the texture
	 * @return array of animation frames
	 */
	public static TextureRegion[] toTextureArray( TextureRegion [][]tr, int cols, int rows ) {
		TextureRegion [] frames
				= new TextureRegion[cols * rows];

		int index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				frames[index++] = tr[i][j];
			}
		}

		return frames;
	}
}
