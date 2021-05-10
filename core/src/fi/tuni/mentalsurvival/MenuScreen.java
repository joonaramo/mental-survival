package fi.tuni.mentalsurvival;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * This class contains the main menu screen of the game.
 */
public class MenuScreen extends ScreenAdapter {
    private MentalSurvival game;
    private Stage stage;
    private Skin skin;
    private Texture background;
    private OrthographicCamera camera;

    private boolean audioOn = true;
    private String language = "FI";

    /**
     * Constructor which is called to assign the game instance to it's variable so it can be accessed.
     * @param game
     */
    public MenuScreen(MentalSurvival game) {
        this.game = game;
    }

    /**
     * This method creates the layout of the menu screen.
     */
    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        background = new Texture(Gdx.files.internal("images/menu_background.jpg"));

        float widthRatio = Gdx.graphics.getWidth() / 1920f;
        float heightRatio = Gdx.graphics.getHeight() / 1080f;
        stage = new Stage(new StretchViewport(Gdx.graphics.getWidth() / widthRatio, Gdx.graphics.getHeight() / heightRatio, camera));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/pixthulhu-ui.json"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 60;
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
        TextButton textButton = new TextButton(game.getString("play"), skin);
        textButton.getLabel().setStyle(labelStyle);
        table.add(textButton).padTop(35.0f);

        textButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameClass(game));
            };
        });

        table.row();

        // Audio settings
        String audioButtonText;
        if(GameUtil.getPreferences().getInteger("AUDIO_VOLUME", 1) == 1) {
            audioButtonText = "AUDIO: ON";
            audioOn = true;
        } else {
            audioButtonText = "AUDIO: OFF";
            audioOn = false;
        }

        textButton = new TextButton(audioButtonText, skin);
        textButton.getLabel().setStyle(labelStyle);
        table.add(textButton).padTop(35.0f);

        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if(audioOn) {
                    ((TextButton) actor).setText("AUDIO: OFF");
                    audioOn = false;
                    GameUtil.getPreferences().putInteger("AUDIO_VOLUME", 0).flush();
                } else {
                    ((TextButton) actor).setText("AUDIO: ON");
                    audioOn = true;
                    GameUtil.getPreferences().putInteger("AUDIO_VOLUME", 1).flush();
                }
            }
        });

        table.row();

        String languageButtonText;
        if(GameUtil.getPreferences().getString("LANGUAGE", "fi").equals("fi")) {
            languageButtonText = "KIELI: FI";
            language = "FI";
        } else {
            languageButtonText = "LANGUAGE: EN";
            language = "EN";
        }

        textButton = new TextButton(languageButtonText, skin);
        textButton.getLabel().setStyle(labelStyle);
        table.add(textButton).padTop(35.0f);

        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if(language == "FI") {
                    ((TextButton) actor).setText("LANGUAGE: EN");
                    language = "EN";
                    GameUtil.getPreferences().putString("LANGUAGE", "en").flush();
                    game.setCurrentLocale(game.getEnLocale());
                } else {
                    ((TextButton) actor).setText("KIELI: FI");
                    language = "FI";
                    GameUtil.getPreferences().putString("LANGUAGE", "fi").flush();
                    game.setCurrentLocale(game.getFiLocale());
                }
                game.setScreen(new MenuScreen(game));
            }
        });

        table.row();
        textButton = new TextButton(game.getString("save"), skin);
        textButton.getLabel().setStyle(labelStyle);
        table.add(textButton).padTop(35.0f);

        textButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameUtil.saveGame(game);
            };
        });

        table.row();
        textButton = new TextButton(game.getString("restart"), skin);
        textButton.getLabel().setStyle(labelStyle);
        table.add(textButton).padTop(35.0f);

        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                GameUtil.resetSavedGame();
                game.restart();
                game.setScreen(new GameClass(game));
            }
        });

        table.row();
        textButton = new TextButton(game.getString("exit"), skin);
        textButton.getLabel().setStyle(labelStyle);
        table.add(textButton).padTop(35.0f);

        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        root.row();
        table = new Table();
        root.add(table).expandY().top().padTop(50.0f);
    }

    /**
     * Render the stage created for the menu.
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
     * Handle resizing screen so it does not cause any issues.
     * @param width new width
     * @param height new height
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Dispose stages, skin and texture to prevent memory leaks.
     */
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        background.dispose();
    }
}