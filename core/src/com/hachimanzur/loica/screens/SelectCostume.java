package com.hachimanzur.loica.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hachimanzur.loica.loicas.AbstractLoicaCostume;
import com.hachimanzur.loica.loicas.NormalLoica;
import com.hachimanzur.loica.loicas.QueenLoica;
import com.hachimanzur.loica.loicas.SpartanLoica;
import com.hachimanzur.loica.loicas.SuperLoica;
import com.hachimanzur.loica.loicas.WizardLoica;
import com.hachimanzur.loica.main.MainGame;
import com.hachimanzur.loica.util.Constants;
import com.hachimanzur.loica.util.GamePreferences;
import com.hachimanzur.loica.util.Gamification.Achievements.Achievement;
import com.hachimanzur.loica.util.Gamification.Gamification;

import static com.hachimanzur.loica.util.Gamification.Achievements.Achievement.Available.QUEEN;
import static com.hachimanzur.loica.util.Gamification.Achievements.Achievement.Available.SPARTAN;
import static com.hachimanzur.loica.util.Gamification.Achievements.Achievement.Available.SUPER;
import static com.hachimanzur.loica.util.Gamification.Achievements.Achievement.Available.WIZARD;


public class SelectCostume implements Screen {

    private MainGame game;
    private Stage stage;
    private GamePreferences prefs;
    private Skin emgoneSkin;
    private Skin emgoneImages;

    private Image normalBigLoica;
    private Image superBigLoica;
    private Image spartanBigLoica;
    private Image queenBigLoica;
    private Image wizardBigLoica;
    private Image currentBigLoica;

    private NormalLoica normalLoica;
    private SuperLoica superLoica;
    private SpartanLoica spartanLoica;
    private QueenLoica queenLoica;
    private WizardLoica wizardLoica;
    private AbstractLoicaCostume selectedCostume;

    private ImageButton normalLoicaImageButton;
    private ImageButton superLoicaImageButton;
    private ImageButton spartanLoicaImageButton;
    private ImageButton queenLoicaImageButton;
    private ImageButton wizardLoicaImageButton;
    private ImageButton currentLoicaImageButton;

    private int currentCostumeTag;

    private Label selectedLoica;

    private Cell loicaPreviewCell;

    public boolean isPractice;

    public SelectCostume(MainGame g, boolean isPractice) {
        this.game = g;
        this.isPractice = isPractice;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);
        loadSettings();
        rebuildStage();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            if (isPractice) {
                game.setScreen(new com.hachimanzur.loica.screens.CalibrateScreen(game));
            }
            else {
                game.setScreen(new com.hachimanzur.loica.screens.GameCalibrationScreen(game));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        stage.dispose();
        emgoneSkin.dispose();
        emgoneImages.dispose();
    }

    @Override
    public void dispose() {

    }

    private void rebuildStage() {
        emgoneSkin = new Skin(
                Gdx.files.internal(Constants.EMGONE_SKIN),
                new TextureAtlas(Constants.EMGONE_ATLAS)
        );

        emgoneImages = new Skin(new TextureAtlas(Constants.SELECT_COSTUME_ATLAS));

        Table layerBackground = buildBackgroundLayer();
        Table layerControls = buildControlsLayer();

        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerControls);

    }

    private void loadSettings(){
        prefs = GamePreferences.instance;
        prefs.load();
        currentCostumeTag = prefs.costume;
    }

    private void saveSettings() {
        prefs = GamePreferences.instance;
        prefs.costume = currentCostumeTag;
        prefs.save();
    }

    private Table buildBackgroundLayer() {
        Table layer = new Table();
        Image imgBackground = new Image(emgoneImages, "bg_choosecharacter");
        layer.add(imgBackground).width(Constants.VIEWPORT_WIDTH).height(Constants.VIEWPORT_HEIGHT);
        return layer;
    }

    private Table buildControlsLayer() {
        Table layer = new Table();
        layer.pad(50, 20, 50, 20);

        TextButton btnBackToMenu = new TextButton("VOLVER", emgoneSkin, "btn-backto");
        layer.add(btnBackToMenu).expandX().left().row();
        btnBackToMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isPractice) {
                    game.setScreen(new CalibrateScreen(game));
                }
                else {
                    game.setScreen(new GameCalibrationScreen(game));
                }
            }
        });

        Label conf = new Label("ELIGE PERSONAJE", emgoneSkin, "big-title");
        layer.add(conf).expand().row();

        layer.add(buildChooseCharacterLayer()).width(Constants.VIEWPORT_WIDTH*0.8f).height(Constants.VIEWPORT_HEIGHT*0.7f).expand().fillY().row();

        TextButton btnPlay = new TextButton("  SIGUIENTE  ", emgoneSkin, "btn-black");
        layer.add(btnPlay).expand();
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveSettings();
                game.setScreen(new SelectScenario(game, selectedCostume, isPractice));
            }
        });

        return layer;
    }

    private Table buildChooseCharacterLayer() {
        Window window = new Window("", emgoneSkin);
        window.setMovable(false);
        window.pad(90, 20, 20, 20);
        window.debugAll();

        normalLoica = new NormalLoica();
        superLoica = new SuperLoica();
        spartanLoica = new SpartanLoica();
        queenLoica = new QueenLoica();
        wizardLoica = new WizardLoica();

        normalBigLoica = new Image(emgoneImages, "loica1_bg");
        superBigLoica = new Image(emgoneImages, "loica2_bg");
        spartanBigLoica = new Image(emgoneImages, "loica3_bg");
        queenBigLoica = new Image(emgoneImages, "loica4_bg");
        wizardBigLoica = new Image(emgoneImages, "loica5_bg");

        normalLoicaImageButton = new ImageButton(emgoneSkin, "normal-loica");
        superLoicaImageButton = new ImageButton(emgoneSkin, "super-loica");
        spartanLoicaImageButton = new ImageButton(emgoneSkin, "spartan-loica");
        queenLoicaImageButton = new ImageButton(emgoneSkin, "queen-loica");
        wizardLoicaImageButton = new ImageButton(emgoneSkin, "wizard-loica");

        switch (currentCostumeTag) {
            case Constants.NORMAL_LOICA:
                currentBigLoica = normalBigLoica;
                selectedCostume = normalLoica;
                currentLoicaImageButton = normalLoicaImageButton;
                break;
            case Constants.SUPER_LOICA:
                currentBigLoica = superBigLoica;
                selectedCostume = superLoica;
                currentLoicaImageButton = superLoicaImageButton;
                break;
            case Constants.SPARTAN_LOICA:
                currentBigLoica = spartanBigLoica;
                selectedCostume = spartanLoica;
                currentLoicaImageButton = spartanLoicaImageButton;
                break;
            case Constants.QUEEN_LOICA:
                currentBigLoica = queenBigLoica;
                selectedCostume = queenLoica;
                currentLoicaImageButton = queenLoicaImageButton;
                break;
            case Constants.WIZARD_LOICA:
                currentBigLoica = wizardBigLoica;
                selectedCostume = wizardLoica;
                currentLoicaImageButton = wizardLoicaImageButton;
                break;
        }

        currentLoicaImageButton.setChecked(true);

        window.add(currentBigLoica).expandX().row();
        loicaPreviewCell = window.getCell(currentBigLoica);
        selectedLoica = new Label(Constants.LOICA_NAMES.get(currentCostumeTag), emgoneSkin, "selected-costume");
        window.add(selectedLoica).pad(15, 0, 40, 0).colspan(3).expandX().top().row();

        window.add(new SplitPane(null, null, true, emgoneSkin)).expand().fillX().colspan(3).row();

        int currentLevel = Gamification.getCurrentLevel();
        window.add(loicasFirstRow(currentLevel)).colspan(3).pad(30).row();
        window.add(loicasSecondRow(currentLevel)).colspan(3).pad(30).padBottom(80).row();


        return window;
    }

    private Table loicasFirstRow(int playerLevel){
        Table table = new Table();

        normalLoicaImageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                if (!normalLoicaImageButton.isDisabled()) {
                    loicaPreviewCell.setActor(normalBigLoica);
                    selectedLoica.setText("LOICA");
                    currentLoicaImageButton.setChecked(false);
                    currentLoicaImageButton = normalLoicaImageButton;
                    currentLoicaImageButton.setChecked(true);
                    currentCostumeTag = Constants.NORMAL_LOICA;
                    selectedCostume = normalLoica;
                }
            }
        });
        superLoicaImageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                if (!superLoicaImageButton.isDisabled()) {
                    loicaPreviewCell.setActor(superBigLoica);
                    selectedLoica.setText("SUPER LOICA");
                    currentLoicaImageButton.setChecked(false);
                    currentLoicaImageButton = superLoicaImageButton;
                    currentLoicaImageButton.setChecked(true);
                    currentCostumeTag = Constants.SUPER_LOICA;
                    selectedCostume = superLoica;
                }
            }
        });

        table.add(normalLoicaImageButton).expandX().padRight(10);
        superLoicaImageButton.setDisabled(Achievement.isLocked(SUPER, playerLevel));
        table.add(superLoicaImageButton).expandX().padRight(10);

        return table;
    }

    private Table loicasSecondRow(int playerLevel) {
        Table table = new Table();

        spartanLoicaImageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                if(!spartanLoicaImageButton.isDisabled()) {
                    loicaPreviewCell.setActor(spartanBigLoica);
                    selectedLoica.setText("LOICA ESPARTANA");
                    currentLoicaImageButton.setChecked(false);
                    currentLoicaImageButton = spartanLoicaImageButton;
                    currentLoicaImageButton.setChecked(true);
                    currentCostumeTag = Constants.SPARTAN_LOICA;
                    selectedCostume = spartanLoica;
                }
            }
        });
        queenLoicaImageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                if(!queenLoicaImageButton.isDisabled()) {
                    loicaPreviewCell.setActor(queenBigLoica);
                    selectedLoica.setText("LOICA REINA");
                    currentLoicaImageButton.setChecked(false);
                    currentLoicaImageButton = queenLoicaImageButton;
                    currentLoicaImageButton.setChecked(true);
                    currentCostumeTag = Constants.QUEEN_LOICA;
                    selectedCostume = queenLoica;
                }
            }
        });
        wizardLoicaImageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                if(!wizardLoicaImageButton.isDisabled()) {
                    loicaPreviewCell.setActor(wizardBigLoica);
                    selectedLoica.setText("LOICA MÁGICA");
                    currentLoicaImageButton.setChecked(false);
                    currentLoicaImageButton = wizardLoicaImageButton;
                    currentLoicaImageButton.setChecked(true);
                    currentCostumeTag = Constants.WIZARD_LOICA;
                    selectedCostume = wizardLoica;
                }
            }
        });

        spartanLoicaImageButton.setDisabled(
                Achievement.isLocked(SPARTAN, playerLevel));
        table.add(spartanLoicaImageButton).expandX();
        queenLoicaImageButton.setDisabled(
                Achievement.isLocked(QUEEN, playerLevel));
        table.add(queenLoicaImageButton).expandX().pad(0, 15, 0, 15);
        wizardLoicaImageButton.setDisabled(
                Achievement.isLocked(WIZARD, playerLevel));
        table.add(wizardLoicaImageButton).expandX();

        return table;
    }
}
