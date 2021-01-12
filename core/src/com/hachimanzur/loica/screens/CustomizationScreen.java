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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hachimanzur.loica.loicas.AbstractLoicaCostume;
import com.hachimanzur.loica.loicas.NormalLoica;
import com.hachimanzur.loica.loicas.QueenLoica;
import com.hachimanzur.loica.loicas.SpartanLoica;
import com.hachimanzur.loica.loicas.SuperLoica;
import com.hachimanzur.loica.loicas.WizardLoica;
import com.hachimanzur.loica.main.MainGame;
import com.hachimanzur.loica.stages.AbstractEMGStage;
import com.hachimanzur.loica.stages.CityStage;
import com.hachimanzur.loica.stages.DesertStage;
import com.hachimanzur.loica.stages.ForestStage;
import com.hachimanzur.loica.util.Constants;
import com.hachimanzur.loica.util.GamePreferences;
import com.hachimanzur.loica.util.Gamification.Achievements.Achievement;
import com.hachimanzur.loica.util.Gamification.Gamification;

import static com.hachimanzur.loica.util.Gamification.Achievements.Achievement.Available.CITY;
import static com.hachimanzur.loica.util.Gamification.Achievements.Achievement.Available.FOREST;
import static com.hachimanzur.loica.util.Gamification.Achievements.Achievement.Available.QUEEN;
import static com.hachimanzur.loica.util.Gamification.Achievements.Achievement.Available.SPARTAN;
import static com.hachimanzur.loica.util.Gamification.Achievements.Achievement.Available.SUPER;
import static com.hachimanzur.loica.util.Gamification.Achievements.Achievement.Available.WIZARD;

public class CustomizationScreen implements Screen {

    private MainGame game;
    private Stage stage;
    private GamePreferences prefs;
    private Skin emgoneSkin;
    private Skin emgoneImages;

    private Image desertBigScenario;
    private Image cityBigScenario;
    private Image forestBigScenario;
    private Image currentBigScenario;

    private ImageButton desertScenarioImageButton;
    private ImageButton cityScenarioImageButton;
    private ImageButton forestScenarioImageButton;
    private ImageButton currentScenarioImageButton;

    private Image normalBigLoica;
    private Image superBigLoica;
    private Image spartanBigLoica;
    private Image queenBigLoica;
    private Image wizardBigLoica;
    private Image currentBigLoica;

    private ImageButton normalLoicaImageButton;
    private ImageButton superLoicaImageButton;
    private ImageButton spartanLoicaImageButton;
    private ImageButton queenLoicaImageButton;
    private ImageButton wizardLoicaImageButton;
    private ImageButton currentLoicaImageButton;

    private int currentCostumeTag;
    private int currentScenarioTag;
    private Label selectedLoica;
    private Label selectedScenario;

    private Cell scenarioPreviewCell;
    private Cell loicaPreviewCell;

    private boolean showLoicaPage = true;
    private Table loicaPage;
    private Table scenarioPage;
    private Table currentPage;
    private Cell currentPageCell;

    private boolean isNight;
    private boolean isPractice;


    public CustomizationScreen(MainGame g, boolean isPractice) {
        this.game = g;
        this.isNight = true;
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
            game.setScreen(new SummaryScreen(game, isPractice));
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
        currentScenarioTag = prefs.scenario;
        currentCostumeTag = prefs.costume;
    }

    private void saveSettings() {
        prefs = GamePreferences.instance;
        prefs.scenario = currentScenarioTag;
        prefs.costume = currentCostumeTag;
        prefs.isNight = isNight;
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
//        layer.debugAll();

        TextButton btnBackToMenu = new TextButton("VOLVER", emgoneSkin, "btn-backto");
        layer.add(btnBackToMenu).expandX().left().row();
        btnBackToMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SummaryScreen(game, isPractice));
            }
        });

        Label conf = new Label("PERSONALIZAR", emgoneSkin, "big-title");
        layer.add(conf).expand().row();

        layer.add(buildCustomizeLayer()).width(Constants.VIEWPORT_WIDTH*0.85f).height(Constants.VIEWPORT_HEIGHT*0.7f).expand().fillY().row();

        TextButton btnPlay = new TextButton("GUARDAR", emgoneSkin, "btn-black");
        layer.add(btnPlay).expand();
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveSettings();
                game.setScreen(new SummaryScreen(game, isPractice));
            }
        });

        return layer;
    }

    private Table buildLoicaPage() {
        Table page = new Table();
        //page.debug();

        Label loicaTab = new Label("Loica", emgoneSkin, "tab-selected");
        loicaTab.setAlignment(Align.center);
        loicaTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentPageCell.setActor(loicaPage);
            }
        });
        Label scenarioTab = new Label("Escenario", emgoneSkin, "tab-regular");
        scenarioTab.setAlignment(Align.center);
        scenarioTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentPageCell.setActor(scenarioPage);
            }
        });

        page.add(new SplitPane(loicaTab, scenarioTab, false, emgoneSkin)).expandX().fillX();

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
                currentLoicaImageButton = normalLoicaImageButton;
                break;
            case Constants.SUPER_LOICA:
                currentBigLoica = superBigLoica;
                currentLoicaImageButton = superLoicaImageButton;
                break;
            case Constants.SPARTAN_LOICA:
                currentBigLoica = spartanBigLoica;
                currentLoicaImageButton = spartanLoicaImageButton;
                break;
            case Constants.QUEEN_LOICA:
                currentBigLoica = queenBigLoica;
                currentLoicaImageButton = queenLoicaImageButton;
                break;
            case Constants.WIZARD_LOICA:
                currentBigLoica = wizardBigLoica;
                currentLoicaImageButton = wizardLoicaImageButton;
                break;
        }

        currentLoicaImageButton.setChecked(true);

        page.row();

        Table bigLoica = new Table();
        bigLoica.add(currentBigLoica).expandX();
        loicaPreviewCell = bigLoica.getCell(currentBigLoica);

        bigLoica.row().padTop(15);
        selectedLoica = new Label(Constants.LOICA_NAMES.get(currentCostumeTag), emgoneSkin, "selected-costume");
        bigLoica.add(selectedLoica).expandX();

        page.add(bigLoica).expandY();

        page.row();
        page.add(new SplitPane(null, null, true, emgoneSkin)).expandX().fillX();

        Table loicaSelect = new Table();
        int currentLevel = Gamification.getCurrentLevel();
        loicaSelect.add(loicasFirstRow(currentLevel));
        loicaSelect.row().padTop(15);
        loicaSelect.add(loicasSecondRow(currentLevel));

        page.row();
        page.add(loicaSelect).expandY();

        return page;
    }


    private Table buildScenarioPage() {
        Table page = new Table();

        Label loicaTab = new Label("Loica", emgoneSkin, "tab-regular");
        loicaTab.setAlignment(Align.center);
        loicaTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentPageCell.setActor(loicaPage);
            }
        });
        Label scenarioTab = new Label("Escenario", emgoneSkin, "tab-selected");
        scenarioTab.setAlignment(Align.center);
        scenarioTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentPageCell.setActor(scenarioPage);
            }
        });

        page.add(new SplitPane(loicaTab, scenarioTab, false, emgoneSkin)).expandX().fillX();

        desertBigScenario = new Image(emgoneImages, "scene_1_big");
        cityBigScenario = new Image(emgoneImages, "scene_2_big");
        forestBigScenario = new Image(emgoneImages, "scene_3_big");

        desertScenarioImageButton = new ImageButton(emgoneSkin, "desert-scenario");
        cityScenarioImageButton = new ImageButton(emgoneSkin, "city-scenario");
        forestScenarioImageButton = new ImageButton(emgoneSkin, "forest-scenario");

        switch (currentScenarioTag) {
            case Constants.DESERT:
                currentBigScenario = desertBigScenario;
                currentScenarioImageButton = desertScenarioImageButton;
                isNight = false;
                break;
            case Constants.CITY:
                currentBigScenario = cityBigScenario;
                currentScenarioImageButton = cityScenarioImageButton;
                isNight = true;
                break;
            case Constants.FOREST:
                currentBigScenario = forestBigScenario;
                currentScenarioImageButton = forestScenarioImageButton;
                isNight = false;
                break;
        }

        currentScenarioImageButton.setChecked(true);

        page.row();
        Table bigScenario = new Table();
        bigScenario.add(currentBigScenario).expandX();
        scenarioPreviewCell = bigScenario.getCell(currentBigScenario);

        bigScenario.row().padTop(30);
        selectedScenario = new Label(Constants.SCENARIO_NAMES.get(currentScenarioTag), emgoneSkin, "selected-costume");
        bigScenario.add(selectedScenario).expandX();

        page.add(bigScenario).expandY();

        page.row();
        page.add(new SplitPane(null, null, true, emgoneSkin)).expandX().fillX();

        page.row();
        page.add(possibleScenarios(Gamification.getCurrentLevel())).expandY();

        return page;
    }

    private Table buildCustomizeLayer() {
        Window window = new Window("", emgoneSkin);
        window.setMovable(false);
        // window.pad(90, 20, 20, 20);
        // window.debugAll();

        loicaPage = buildLoicaPage();
        scenarioPage = buildScenarioPage();

        currentPage = showLoicaPage ? loicaPage : scenarioPage;
        window.add(currentPage).expand().fill();
        currentPageCell = window.getCell(currentPage);

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

    private Table possibleScenarios(int playerLevel){
        Table table = new Table();

        desertScenarioImageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                if(!desertScenarioImageButton.isDisabled()) {
                    scenarioPreviewCell.setActor(desertBigScenario);
                    selectedScenario.setText("DESIERTO");
                    currentScenarioImageButton.setChecked(false);
                    currentScenarioImageButton = desertScenarioImageButton;
                    currentScenarioImageButton.setChecked(true);
                    currentScenarioTag = 1;
                    isNight = false;
                }
            }
        });
        cityScenarioImageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!cityScenarioImageButton.isDisabled()) {
                    scenarioPreviewCell.setActor(cityBigScenario);
                    selectedScenario.setText("CIUDAD");
                    currentScenarioImageButton.setChecked(false);
                    currentScenarioImageButton = cityScenarioImageButton;
                    currentScenarioImageButton.setChecked(true);
                    currentScenarioTag = 2;
                    isNight = true;
                }
            }
        });
        forestScenarioImageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!forestScenarioImageButton.isDisabled()) {
                    scenarioPreviewCell.setActor(forestBigScenario);
                    selectedScenario.setText("BOSQUE");
                    currentScenarioImageButton.setChecked(false);
                    currentScenarioImageButton = forestScenarioImageButton;
                    currentScenarioImageButton.setChecked(true);
                    currentScenarioTag = 3;
                    isNight = false;
                }
            }
        });

        table.add(desertScenarioImageButton).expandX();
        cityScenarioImageButton.setDisabled(
                Achievement.isLocked(CITY, playerLevel));
        table.add(cityScenarioImageButton).expandX();
        forestScenarioImageButton.setDisabled(
                Achievement.isLocked(FOREST, playerLevel));
        table.add(forestScenarioImageButton).expandX();

        return table;
    }

}
