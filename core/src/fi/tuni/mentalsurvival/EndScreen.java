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
 * This class contains game end screen, which is shown when the player has finished the game.
 */
public class EndScreen extends ScreenAdapter {
    private MentalSurvival game;
    private Texture background;
    private OrthographicCamera camera;
    private Stage stage;
    private Table table;

    /**
     * Constructor which is called to assign the game instance to it's variable so it can be accessed.
     * @param game the game object
     */
    public EndScreen(MentalSurvival game) {
        this.game = game;
    }

    /**
     * This method is used to show the end screen and form the layout of it.
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
        background = new Texture(Gdx.files.internal("images/Endscreen.jpg"));

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



        TextButton textButton = new TextButton(game.getString("exit"), skin);
        textButton.getLabel().setStyle(labelStyle);

        table.add(textButton).size(textButton.getPrefWidth(), textButton.getPrefHeight()).expand().bottom().padBottom(60);

        textButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            };
        });

    }

    /**
     * Render the created layout.
     * @param delta delta value which is based on player's FPS.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(background, 0, 0, 1920, 1080);
        game.batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    /**
     * Disable input processor when the screen is changed to other one.
     */
    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }
}
