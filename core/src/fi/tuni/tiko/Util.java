package fi.tuni.tiko;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

class Util {
	public static Rectangle scaleRect(Rectangle r, float scale) {
		Rectangle rectangle = new Rectangle();
		rectangle.x      = r.x * scale;
		rectangle.y      = r.y * scale;
		rectangle.width  = r.width * scale;
		rectangle.height = r.height * scale;
		return rectangle;
	}

	public static void flip(Animation<TextureRegion> animation) {
		TextureRegion[] regions = animation.getKeyFrames();
		for(TextureRegion r : regions) {
			r.flip(true, false);
		}
	}

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
