package fi.tuni.tiko;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen extends ScreenAdapter {
    private MentalSurvival game;
    private Stage stage;
    private Skin skin;
    private Texture background;
    private OrthographicCamera camera;

    private boolean audioOn = true;
    private String language = "FI";

    public MenuScreen(MentalSurvival game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 8, 4.8f);
        background = new Texture(Gdx.files.internal("menu_background.jpg"));


        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("pixthulhu-ui.json"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 84;
        BitmapFont font64 = generator.generateFont(parameter);
        generator.dispose();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font64;

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Table table = new Table();
        table.defaults().left();
        root.add(table);

        Label label = new Label("- M E N U -", labelStyle);
        table.add(label).padTop(50.0f).center();

        table.row();
        TextButton textButton = new TextButton("PLAY", skin);
        textButton.getLabel().setStyle(labelStyle);
        table.add(textButton).padTop(35.0f);

        textButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameClass(game));
            };
        });

        table.row();
        textButton = new TextButton("LEADERBOARDS", skin);
        textButton.getLabel().setStyle(labelStyle);
        table.add(textButton).padTop(35.0f);

        table.row();
        textButton = new TextButton("SAVE GAME", skin);
        textButton.getLabel().setStyle(labelStyle);
        table.add(textButton).padTop(35.0f);

        textButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameUtil.saveGame(game);
            };
        });

        table.row();
        textButton = new TextButton("AUDIO: ON", skin);
        textButton.getLabel().setStyle(labelStyle);
        table.add(textButton).padTop(35.0f);

//        final TextButton finalTextButton = textButton;
//        textButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                finalTextButton.setText("AUDIO: OFF");
//            };
//        });

        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if(audioOn) {
                    ((TextButton) actor).setText("AUDIO: OFF");
                    audioOn = false;
                } else {
                    ((TextButton) actor).setText("AUDIO: ON");
                    audioOn = true;
                }
            }
        });

        table.row();
        textButton = new TextButton("LANGUAGE: FI", skin);
        textButton.getLabel().setStyle(labelStyle);
        table.add(textButton).padTop(35.0f);

        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if(language == "FI") {
                    ((TextButton) actor).setText("LANGUAGE: EN");
                    language = "EN";
                } else {
                    ((TextButton) actor).setText("LANGUAGE: FI");
                    language = "FI";
                }
            }
        });

        root.row();
        table = new Table();
        root.add(table).expandY().top().padTop(50.0f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(background, 0, 0, 8f, 4.8f);
        game.batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}