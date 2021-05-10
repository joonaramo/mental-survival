package fi.tuni.mentalsurvival;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * This class contains the splash screen which is shown to the user first when the game is opened.
 */
public class SplashScreen extends ScreenAdapter {
    private MentalSurvival game;
    private Texture background;
    private OrthographicCamera camera;
    private Stage stage;
    private Table table;

    /**
     * Constructor which is called to assign the game instance to it's variable so it can be accessed.
     * @param game the game object
     */
    public SplashScreen(MentalSurvival game) {
        this.game = game;
    }

    /**
     * This method creates the layout for the splash screen
     */
    @Override
    public void show(){
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        float widthRatio = w / 1920f;
        float heightRatio = h / 1080f;
        stage = new Stage(new StretchViewport(w / widthRatio, h / heightRatio, camera));
        background = new Texture(Gdx.files.internal("images/Titlescreen.jpg"));

        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Skin skin = new Skin(Gdx.files.internal("ui/pixthulhu-ui.json"));


        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 128;
        BitmapFont font64 = generator.generateFont(parameter);
        generator.dispose();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font64;



        TextButton textButton = new TextButton("PLAY", skin);
        textButton.getLabel().setStyle(labelStyle);

        table.add(textButton).size(textButton.getPrefWidth(), textButton.getPrefHeight()).expand().bottom().padBottom(60);

        textButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            };
        });

    }

    /**
     * This method renders the splash screen background and the stage on top of it
     * @param delta delta value which is based on player's FPS.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(background, 0, 0, 1920, 1080);
//        game.font.draw(game.batch, "Title Screen!", Gdx.graphics.getWidth() * .25f, Gdx.graphics.getHeight() * .75f);
//        game.font.draw(game.batch, "Click the circle to win.", Gdx.graphics.getWidth() * .25f, Gdx.graphics.getHeight() * .5f);
//        game.font.draw(game.batch, "Press space to play.", Gdx.graphics.getWidth() * .25f, Gdx.graphics.getHeight() * .25f);
        game.batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    /**
     * Un-assign any input processing when the screen is hidden.
     */
    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }
}
