package fi.tuni.mentalsurvival;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.Color;

/**
 * This class contains the on-screen HUD including e.g sanity bar, joystick, story dialogs.
 */
public class Hud {
    private Stage stage;
    private Skin skin;
    private Table root;
    private Table missionTable;
    private JoystickControl joystickControl;
    private Label.LabelStyle labelStyle;
    private BitmapFont font64;
    private BitmapFont font132;
    private Label woodLabel;
    private Label ropeLabel;
    private Label matchLabel;
    private Label waterBottleLabel;
    private Label missionLabel;
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

    private Texture woodTexture;
    private Texture ropeTexture;
    private Image missionImage;

    private Image woodImage;
    private Image ropeImage;
    private Image matchesImage;
    private Image waterBottleImage;

    /**
     * Create the HUD layout-
     * @param game game object
     */
    public Hud(final MentalSurvival game) {
        initialize();

        labelStyle.font = font132;

        // Background for top bar
        Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.RGB565);
        bgPixmap.setColor(Color.WHITE);
        bgPixmap.fill();
        TextureRegionDrawable textureRegionDrawableBg = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));

        // Mission
        missionTable = new Table();
        missionTable.setBackground(textureRegionDrawableBg);

        missionLabel = new Label("COLLECT 10 X", labelStyle);
        missionImage = new Image(woodTexture);
        missionTable.add(missionLabel).size(missionLabel.getPrefWidth(), missionLabel.getPrefHeight()).padLeft(20);
        missionTable.add(missionImage).size(200, 200);
        root.add(missionTable).width((missionLabel.getPrefWidth() + missionImage.getPrefWidth()) * 1.25f).expandX().left().padLeft(80).padTop(35).padRight(190);

        // Sanity bar

        Table table = new Table();
        table.add(currentSanity).size(currentSanity.getPrefWidth() * 8, currentSanity.getPrefHeight() * 8).left();
        root.add(table).width(Gdx.graphics.getWidth() / 3f - currentSanity.getPrefWidth() * 8).expandX();


        table = new Table();
        // Menu button
        TextButton menuButton = new TextButton("||", skin);
        menuButton.getLabel().setStyle(labelStyle);
        menuButton.padLeft(40);
        menuButton.padRight(40);
        table.add(menuButton).size(menuButton.getPrefWidth(), menuButton.getPrefHeight()).expandX().right();
        root.add(table).width(Gdx.graphics.getWidth() / 3f - menuButton.getPrefWidth()).expandX().padRight(0);


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
        actionButton.pad(60);
        actionButton.setVisible(false);
        table.add(actionButton).size(actionButton.getPrefWidth(), actionButton.getPrefHeight()).expandX().align(Align.bottomRight).padTop(500).padRight(-1200);

        root.row();

        // Joystick
        table = new Table();
        root.add(table).growX();
        table.add(joystickControl.getTouchpad()).size(joystickControl.getTouchpad().getWidth(), joystickControl.getTouchpad().getHeight()).expandX().left().padLeft(100).padBottom(80);

        // Backpack
        toolsButton = new TextButton(game.getString("backpack"), skin);
        toolsButton.getLabel().setStyle(labelStyle);
        toolsButton.pad(60);
        if(!game.getPlayer().isBackpackCollected()) {
            toolsButton.setVisible(false);
        }
        table.add(toolsButton).size(toolsButton.getPrefWidth(), toolsButton.getPrefHeight()).padRight(-1200 - toolsButton.getPrefWidth() / 2);

        toolsButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openBackpack();
            };
        });
    }

    /**
     * Initialize the HUD's stage, table, skin, textures etc.
     */
    public void initialize() {
        // Init stage, skin and root table
        stage = new Stage(new FitViewport(2560, 1440));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/pixthulhu-ui.json"));
        root = new Table();
        root.setFillParent(true);
        root.setTransform(true);
        stage.addActor(root);


        // Generate fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 98;
        font132 = generator.generateFont(parameter);
        parameter.size = 64;
        font64 = generator.generateFont(parameter);
        generator.dispose();

        labelStyle = new Label.LabelStyle();

        // Create joystick
        Texture joystickBg = new Texture(Gdx.files.internal("images/joystickBg.png"));
        Texture joystickKnob = new Texture(Gdx.files.internal("images/joystickKnob.png"));
        joystickControl = new JoystickControl(joystickBg, joystickKnob, 10, 0, 0, 400, 400);


        // Create sanity bars
        sanityBar0 = new Texture(Gdx.files.internal("images/Sanitybar_0.png"));
        sanityBar20 = new Texture(Gdx.files.internal("images/Sanitybar_20.png"));
        sanityBar40 = new Texture(Gdx.files.internal("images/Sanitybar_40.png"));
        sanityBar60 = new Texture(Gdx.files.internal("images/Sanitybar_60.png"));
        sanityBar80 = new Texture(Gdx.files.internal("images/Sanitybar_80.png"));
        sanityBar100 = new Texture(Gdx.files.internal("images/Sanitybar_100.png"));

        currentSanity = new Image(sanityBar0);

        // Backpack
        woodTexture = new Texture(Gdx.files.internal("images/wood.png"));
        ropeTexture = new Texture(Gdx.files.internal("images/Rope.png"));
        Texture matchesTexture = new Texture(Gdx.files.internal("images/matches.png"));
        Texture waterBottleTexture = new Texture(Gdx.files.internal("images/water_bottle.png"));

        woodImage = new Image(woodTexture);
        ropeImage = new Image(ropeTexture);
        matchesImage = new Image(matchesTexture);
        waterBottleImage = new Image(waterBottleTexture);

        labelStyle.font = font64;
        woodLabel = new Label("999", labelStyle);
        ropeLabel = new Label("999", labelStyle);
        matchLabel = new Label("999", labelStyle);
        waterBottleLabel = new Label("EMPTY", labelStyle);
    }

    /**
     * Show a on-screen dialog with the given text.
     * @param dialogText text to be shown on the dialog.
     */
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
        plotDialog.text(dialogLabel).pad(40);
        plotDialog.getButtonTable().row();
        plotDialog.button(okButton).pad(40);

        if(!dialogOpen) {
            dialogOpen = true;
            plotDialog.show(stage).pad(40);
        }
    }

    /**
     * Show the player's, backpack on screen.
     */
    public void openBackpack() {
        // Dialog label and button styling


        okButton = new TextButton("OK", skin);
        okButton.getLabel().setStyle(labelStyle);

        Table imageTable = new Table();
        imageTable.add(woodImage).width(200).height(200);
        imageTable.add(woodLabel);
        imageTable.row();
        imageTable.add(ropeImage).width(200).height(200);
        imageTable.add(ropeLabel);
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

    /**
     * Update the mission that is shown on the top left on screen. Calculates the value from given integers.
     * @param woodToCollect amount of wood to be collected
     * @param ropesToCollect amount of ropes to be collected
     * @param woodCount amount of wood in backpack
     * @param ropeCount amount of ropes in backpack
     * @param collectString the string to show (COLLECT/KERÄÄ)
     */
    public void updateMission(int woodToCollect, int ropesToCollect, int woodCount, int ropeCount, String collectString) {
        if(woodToCollect - woodCount > 0) {
            missionTable.setVisible(true);
            missionLabel.setText(collectString + " " + (woodToCollect - woodCount)  + " x");
            missionLabel.setSize(missionLabel.getPrefWidth(), missionLabel.getPrefHeight());
        } else if(ropesToCollect - ropeCount > 0) {
            missionTable.setVisible(true);
            missionLabel.setText(collectString + " " + (ropesToCollect - ropeCount)  + " x");
            missionLabel.setSize(missionLabel.getPrefWidth(), missionLabel.getPrefHeight());
            missionImage.setDrawable(new SpriteDrawable(new Sprite(ropeTexture)));
        }
        else {
            missionTable.setVisible(false);
        }

    }

    /**
     * Update the sanity bar's drawable based on the player's sanity level.
     * @param sanityLevel
     */
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

    /**
     * Update the backpack contents based on the player's values
     * @param woodAmount amount of wood in backpack
     * @param ropeAmount amount of ropes in backpack
     * @param matchAmount amount of matches in backpack
     * @param isWaterBottleFull true if water bottle full, false if empty
     * @param emptyString string to be shown if the bottle is empty
     * @param fullString string to be shown if the bottle is full
     */
    public void updateBackpack(int woodAmount, int ropeAmount, int matchAmount, boolean isWaterBottleFull, String emptyString, String fullString) {
        woodLabel.setText(String.valueOf(woodAmount));
        ropeLabel.setText(String.valueOf(ropeAmount));
        matchLabel.setText(String.valueOf(matchAmount));
        if(isWaterBottleFull) {
            waterBottleLabel.setText(fullString);
        } else {
            waterBottleLabel.setText(emptyString);
        }
    }

    /**
     * Set the backpack's visibility state
     */
    public void showBackpack() {
        toolsButton.setVisible(true);
    }

    /**
     * Check if dialog is currently open
     * @return true if open, false if not
     */
    public Boolean getDialogOpen() {
        return dialogOpen;
    }

    /**
     * Check if action button has been pressed
     * @return true if pressed, false if not
     */
    public boolean isActionButtonPressed() {
        return actionButtonPressed;
    }

    /**
     * Change the action button's pressed-state
     * @param actionButtonPressed true if pressed, false if not
     */
    public void setActionButtonPressed(boolean actionButtonPressed) {
        this.actionButtonPressed = actionButtonPressed;
    }

    /**
     * Set the dialog as open or closed
     * @param dialogOpen true if open, false if closed
     */
    public void setDialogOpen(Boolean dialogOpen) {
        this.dialogOpen = dialogOpen;
    }

    /**
     * Get the plot dialog object
     * @return plot dialog object
     */
    public Dialog getPlotDialog() {
        return plotDialog;
    }

    /**
     * Get the OK button which is used to hide the dialog.
     * @return OK button
     */
    public TextButton getOkButton() {
        return okButton;
    }

    /**
     * Get the action button which is used for some action  (eat, sleep, etc.)
     * @return action button
     */
    public TextButton getActionButton() {
        return actionButton;
    }

    /**
     * Get the stage that has all the HUD's actors.
     * @return stage
     */
    public Stage getStage() { return stage; }

    /**
     * Get the on-screen joystick controller
     * @return joystick control
     */
    public JoystickControl getJoystickControl() {
        return joystickControl;
    }

    /**
     * Method that is called when screen is resized, updates the stage's viewport so it doesn't get messed up.
     * @param width new width
     * @param height new height
     */
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Dispose the textures and fonts to prevent memory leaks.
     */
    public void dispose(){
        stage.dispose();
        ropeTexture.dispose();
        woodTexture.dispose();
        sanityBar0.dispose();
        sanityBar20.dispose();
        sanityBar40.dispose();
        sanityBar60.dispose();
        sanityBar80.dispose();
        sanityBar100.dispose();
        font64.dispose();
        font132.dispose();
    }
}