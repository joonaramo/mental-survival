package fi.tuni.tiko;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MentalSurvival extends Game {
    SpriteBatch batch;
    BitmapFont font;

    @Override
    public void create () {
        batch = new SpriteBatch();
        font = new BitmapFont();
        setScreen(new SplashScreen(this));
    }

    @Override
    public void dispose () {
        batch.dispose();
        font.dispose();
    }
}
