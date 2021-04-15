package fi.tuni.tiko;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;

public class Hud {
    private Stage stage;
    private Skin skin;
    private Table root;
    private JoystickControl joystickControl;
    private Label.LabelStyle labelStyle;
    private BitmapFont font64;
    private BitmapFont font132;
    private Label woodLabel;
    private Label matchLabel;
    private Label waterBottleLabel;
    private TextButton okButton;
    private TextButton toolsButton;
    private TextButton actionButton;
    private boolean dialogOpen = false;
    private boolean actionButtonPressed = false;
    private Dialog plotDialog;

    private Texture sanityBar0;
    private Texture sanityBar20;
    private Texture sanityBar40;
    private Texture sanityBar60;
    private Texture sanityBar80;
    private Texture sanityBar100;
    private Image currentSanity;

    private Image woodImage;
    private Image matchesImage;
    private Image waterBottleImage;

    public Hud(SpriteBatch spriteBatch, final MentalSurvival game) {
        initialize(spriteBatch);

        labelStyle.font = font132;

        // Sanity bar
        Table table = new Table();
        root.add(table).expandY().fillX().top();
        table.add(currentSanity).size(currentSanity.getPrefWidth() * 8, currentSanity.getPrefHeight() * 8).expandX().center();

        // Menu button
        TextButton menuButton = new TextButton("||", skin);
        menuButton.getLabel().setStyle(labelStyle);
        menuButton.padLeft(40);
        menuButton.padRight(40);
        table.add(menuButton).size(menuButton.getPrefWidth(), menuButton.getPrefHeight()).expandX().right();

        menuButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            };
        });

        root.row();

        // Action button
        table = new Table();
        root.add(table).expand().fill();
        actionButton = new TextButton("COOK AND EAT", skin);
        actionButton.getLabel().setStyle(labelStyle);
        actionButton.setVisible(false);
        table.add(actionButton).size(actionButton.getPrefWidth(), actionButton.getPrefHeight()).expandX().align(Align.bottomRight).padTop(500).padRight(20);

        root.row();

        // Joystick
        table = new Table();
        root.add(table).expandX().fillX();
        table.add(joystickControl.getTouchpad()).size(joystickControl.getTouchpad().getWidth(), joystickControl.getTouchpad().getHeight()).expandX().left().padLeft(80).padBottom(80);

        // Backpack
        toolsButton = new TextButton("BACKPACK", skin);
        toolsButton.getLabel().setStyle(labelStyle);
        toolsButton.pad(60);
        if(!game.getPlayer().isBackpackCollected()) {
            toolsButton.setVisible(false);
        }
        table.add(toolsButton).size(toolsButton.getPrefWidth(), toolsButton.getPrefHeight()).padRight(20);

        toolsButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openBackpack();
            };
        });
    }

    public void initialize(SpriteBatch spriteBatch) {
        // Init stage, skin and root table
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("pixthulhu-ui.json"));
        root = new Table();
        root.setFillParent(true);
        root.setTransform(true);
        stage.addActor(root);


        // Generate fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 98;
        font132 = generator.generateFont(parameter);
        parameter.size = 64;
        font64 = generator.generateFont(parameter);
        generator.dispose();

        labelStyle = new Label.LabelStyle();

        // Create joystick
        Texture joystickBg = new Texture(Gdx.files.internal("joystickBg.png"));
        Texture joystickKnob = new Texture(Gdx.files.internal("joystickKnob.png"));
        joystickControl = new JoystickControl(joystickBg, joystickKnob, 10, 0, 0, 400, 400);


        // Create sanity bars
        sanityBar0 = new Texture(Gdx.files.internal("Sanitybar_0.png"));
        sanityBar20 = new Texture(Gdx.files.internal("Sanitybar_20.png"));
        sanityBar40 = new Texture(Gdx.files.internal("Sanitybar_40.png"));
        sanityBar60 = new Texture(Gdx.files.internal("Sanitybar_60.png"));
        sanityBar80 = new Texture(Gdx.files.internal("Sanitybar_80.png"));
        sanityBar100 = new Texture(Gdx.files.internal("Sanitybar_100.png"));

        currentSanity = new Image(sanityBar0);

        // Backpack
        Texture woodTexture = new Texture(Gdx.files.internal("wood.png"));
        Texture matchesTexture = new Texture(Gdx.files.internal("matches.png"));
        Texture waterBottleTexture = new Texture(Gdx.files.internal("water_bottle.png"));

        woodImage = new Image(woodTexture);
        matchesImage = new Image(matchesTexture);
        waterBottleImage = new Image(waterBottleTexture);

        labelStyle.font = font64;
        woodLabel = new Label("999", labelStyle);
        matchLabel = new Label("999", labelStyle);
        waterBottleLabel = new Label("EMPTY", labelStyle);
    }



    public void showDialog(String dialogText) {
        // Dialog label and button styling
        labelStyle.font = font64;
        Label dialogLabel = new Label(dialogText, labelStyle);
        okButton = new TextButton("OK", skin);
        okButton.getLabel().setStyle(labelStyle);

         plotDialog = new Dialog("", skin) {
            {
            }
            @Override
            protected void result(final Object object) {
                dialogOpen = false;
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

    public void openBackpack() {
        // Dialog label and button styling


        okButton = new TextButton("OK", skin);
        okButton.getLabel().setStyle(labelStyle);

        Table imageTable = new Table();
        imageTable.add(woodImage).width(200).height(200);
        imageTable.add(woodLabel);
        imageTable.row();
        imageTable.add(matchesImage).width(200).height(200);
        imageTable.add(matchLabel);
        imageTable.row();
        imageTable.add(waterBottleImage).width(200).height(200);
        imageTable.add(waterBottleLabel);

        Dialog dialog = new Dialog("", skin) {
            {
            }
        };
//        dialog.text(dialogLabel);
        dialog.getButtonTable().row();
        dialog.getContentTable().add(imageTable);
        dialog.button(okButton);
        dialog.padRight(40);
        dialog.show(stage);
    }

    public void updateSanityBar(int sanityLevel) {
        if(sanityLevel > 95) {
            currentSanity.setDrawable(new SpriteDrawable(new Sprite(sanityBar100)));
        } else if(sanityLevel > 75) {
            currentSanity.setDrawable(new SpriteDrawable(new Sprite(sanityBar80)));
        } else if(sanityLevel > 55) {
            currentSanity.setDrawable(new SpriteDrawable(new Sprite(sanityBar60)));
        } else if(sanityLevel > 35) {
            currentSanity.setDrawable(new SpriteDrawable(new Sprite(sanityBar40)));
        } else if(sanityLevel > 15) {
            currentSanity.setDrawable(new SpriteDrawable(new Sprite(sanityBar20)));
        } else {
            currentSanity.setDrawable(new SpriteDrawable(new Sprite(sanityBar0)));
        }
    }

    public void updateBackpack(int woodAmount, int matchAmount, boolean isWaterBottleFull) {
        woodLabel.setText(String.valueOf(woodAmount));
        matchLabel.setText(String.valueOf(matchAmount));
        if(isWaterBottleFull) {
            waterBottleLabel.setText("FULL");
        } else {
            waterBottleLabel.setText("EMPTY");
        }
    }

    public void showBackpack() {
        toolsButton.setVisible(true);
    }

    public Boolean getDialogOpen() {
        return dialogOpen;
    }

    public boolean isActionButtonPressed() {
        return actionButtonPressed;
    }

    public void setActionButtonPressed(boolean actionButtonPressed) {
        this.actionButtonPressed = actionButtonPressed;
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

    public TextButton getActionButton() {
        return actionButton;
    }

    public Stage getStage() { return stage; }

    public JoystickControl getJoystickControl() {
        return joystickControl;
    }

    public void dispose(){
        stage.dispose();
    }
}