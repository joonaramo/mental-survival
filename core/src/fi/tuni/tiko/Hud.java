package fi.tuni.tiko;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;

public class Hud {
    private Stage stage;
    private Skin skin;
    private Table table;
    private Texture joystickBg;
    private Texture joystickKnob;
    private JoystickControl joystickControl;
    private Label.LabelStyle labelStyle;
    private BitmapFont font64;
    private BitmapFont font132;
    private Label dialogLabel;
    private TextButton okButton;

    public Hud(SpriteBatch spriteBatch) {
        initialize(spriteBatch);

        // Sanity bar
        labelStyle.font = font132;
        Label sanityLevel = new Label("INSANE", labelStyle);
        table.add(sanityLevel).size(sanityLevel.getWidth(), sanityLevel.getHeight()).expand().top();
        table.row();

        // Dialog
        showDialog("Nyt on kyllä ikävä tilanne! Ei auta muu kuin pitää pää kylmänä.");

        // Joystick
        table.add(joystickControl.getTouchpad()).size(joystickControl.getTouchpad().getWidth(), joystickControl.getTouchpad().getHeight()).expand().left().bottom().padLeft(80);

        // Backpack
        TextButton toolsButton = new TextButton("BACKPACK", skin);
        toolsButton.getLabel().setStyle(labelStyle);
        table.row();
        table.add(toolsButton).size(toolsButton.getPrefWidth(), toolsButton.getPrefHeight() * 2).expand().right().padRight(20);

        toolsButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Label toolsDialogLabel = new Label("Wood: 0 \nWater bottle: EMPTY \nMatches: 5", labelStyle);
                okButton.getLabel().setStyle(labelStyle);
                table.row();
                Dialog toolsDialog = new Dialog("", skin) {
                    {
                    }
                };
                toolsDialog.text(toolsDialogLabel);
                toolsDialog.getButtonTable().row();
                toolsDialog.button(okButton);
                toolsDialog.show(stage);
            };
        });

    }

    public void initialize(SpriteBatch spriteBatch) {
        // Init stage, skin and root table
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), spriteBatch);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        table = new Table();
        table.setFillParent(true);
        table.setTransform(true);
        stage.addActor(table);

        // Generate fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 132;
        font132 = generator.generateFont(parameter);
        parameter.size = 64;
        font64 = generator.generateFont(parameter);
        generator.dispose();

        // Dialog label and button styling
        labelStyle = new Label.LabelStyle();
        labelStyle.font = font64;

        dialogLabel = new Label("", labelStyle);
        okButton = new TextButton("OK", skin);
        okButton.getLabel().setStyle(labelStyle);

        // Create joystick
        joystickBg = new Texture("joystickBg.png");
        joystickKnob = new Texture("joystickKnob.png");
        joystickControl = new JoystickControl(joystickBg, joystickKnob, 10, 0, 0, 400, 400);
    }

    public void showDialog(String dialogText) {
        Dialog dialog = new Dialog("", skin) {
            {
            }
        };
        dialogLabel.setText(dialogText);
        dialog.text(dialogLabel);
        dialog.getButtonTable().row();
        dialog.button(okButton);
        dialog.show(stage);
    }

    public Stage getStage() { return stage; }

    public JoystickControl getJoystickControl() {
        return joystickControl;
    }

    public void dispose(){
        stage.dispose();
    }
}