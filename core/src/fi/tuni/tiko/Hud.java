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
    private TextButton toolsButton;
    private boolean dialogOpen = false;
    private Dialog plotDialog;

    public Hud(SpriteBatch spriteBatch) {
        initialize(spriteBatch);

        // Sanity bar
        labelStyle.font = font132;
        Label sanityLevel = new Label("INSANE", labelStyle);
        table.add(sanityLevel).size(sanityLevel.getWidth(), sanityLevel.getHeight()).expand().top();
        table.row();

        // Joystick
        table.add(joystickControl.getTouchpad()).size(joystickControl.getTouchpad().getWidth(), joystickControl.getTouchpad().getHeight()).expand().left().bottom().padLeft(80);

        // Backpack
        toolsButton = new TextButton("BACKPACK", skin);
        toolsButton.getLabel().setStyle(labelStyle);
        table.row();
        table.add(toolsButton).size(toolsButton.getPrefWidth(), toolsButton.getPrefHeight() * 2).expand().right().padRight(20);
        toolsButton.setVisible(false);
//        showBackpack();



        toolsButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openBackpack("Wood: 0 \nWater bottle: EMPTY \nMatches: 5");
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

        labelStyle = new Label.LabelStyle();

        // Create joystick
        joystickBg = new Texture("joystickBg.png");
        joystickKnob = new Texture("joystickKnob.png");
        joystickControl = new JoystickControl(joystickBg, joystickKnob, 10, 0, 0, 400, 400);
    }



    public void showDialog(String dialogText, final GameClass game) {
        // Dialog label and button styling
        labelStyle.font = font64;
        dialogLabel = new Label(dialogText, labelStyle);
        okButton = new TextButton("OK", skin);
        okButton.getLabel().setStyle(labelStyle);

         plotDialog = new Dialog("", skin) {
            {
            }
            @Override
            protected void result(final Object object) {
//                int gameStep = gameUtil.getGameStep();
//                gameUtil.setGameStep(gameStep + 1);
//                  game.showGameStep(game.getGameStep() + 1);
//                  game.setGameStep(game.getGameStep() + 1);
                dialogOpen = false;
//                game.setGameStep(game.getGameStep() + 1);
            }
        };
        plotDialog.text(dialogLabel);
        plotDialog.getButtonTable().row();
        plotDialog.button(okButton);

        if(!dialogOpen) {
            dialogOpen = true;
            plotDialog.show(stage);
        }
    }

    public void openBackpack(String backpackText) {
        // Dialog label and button styling
        labelStyle.font = font64;
        dialogLabel = new Label(backpackText, labelStyle);
        okButton = new TextButton("OK", skin);
        okButton.getLabel().setStyle(labelStyle);

        Dialog dialog = new Dialog("", skin) {
            {
            }
        };
        dialog.text(dialogLabel);
        dialog.getButtonTable().row();
        dialog.button(okButton);
        dialog.show(stage);
    }

    public void showBackpack() {
        toolsButton.setVisible(true);
    }

    public Boolean getDialogOpen() {
        return dialogOpen;
    }

    public void setDialogOpen(Boolean dialogOpen) {
        this.dialogOpen = dialogOpen;
    }

    public Dialog getPlotDialog() {
        return plotDialog;
    }

    public TextButton getOkButton() {
        return okButton;
    }

    public Stage getStage() { return stage; }

    public JoystickControl getJoystickControl() {
        return joystickControl;
    }

    public void dispose(){
        stage.dispose();
    }
}