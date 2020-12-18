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

public class SelectScenario implements Screen {

    private MainGame game;
    private Stage stage;
    private GamePreferences prefs;
    private Skin emgoneSkin;
    private Skin emgoneImages;

    private Image desertBigScenario;
    private Image cityBigScenario;
    private Image forestBigScenario;
    private Image currentBigScenario;

    private DesertStage desertStage;
    private CityStage cityStage;
    private ForestStage forestStage;
    private AbstractEMGStage selectedStage;

    private ImageButton desertScenarioImageButton;
    private ImageButton cityScenarioImageButton;
    private ImageButton forestScenarioImageButton;
    private ImageButton currentScenarioImageButton;

    private int currentScenarioTag;

    private Label selectedScenario;

    private Cell scenarioPreviewCell;

    private AbstractLoicaCostume loicaCostume;

    private boolean isNight;

    public boolean isPractice;

    public SelectScenario(MainGame g, AbstractLoicaCostume loicaCostume, boolean isPractice) {
        this.game = g;
        this.loicaCostume = loicaCostume;
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
            game.setScreen(new com.hachimanzur.loica.screens.SelectCostume(game, isPractice));
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
    }

    private void saveSettings() {
        prefs = GamePreferences.instance;
        prefs.scenario = currentScenarioTag;
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
                game.setScreen(new SelectCostume(game, isPractice));
            }
        });

        Label conf = new Label("ELIGE ESCENARIO", emgoneSkin, "big-title");
        layer.add(conf).expand().row();

        layer.add(buildChooseScenarioLayer()).width(Constants.VIEWPORT_WIDTH*0.85f).height(Constants.VIEWPORT_HEIGHT*0.7f).expand().fillY().row();

        TextButton btnPlay = new TextButton("  ¡A JUGAR!  ", emgoneSkin, "btn-black");
        layer.add(btnPlay).expand();
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveSettings();
                game.setScreen(new EmgOneGame(game, selectedStage, loicaCostume, isNight, isPractice));
            }
        });

        return layer;
    }

    private Table buildChooseScenarioLayer() {
        Window window = new Window("", emgoneSkin);
        window.setMovable(false);
        window.pad(90, 20, 20, 20);
//        window.debugAll();

        desertBigScenario = new Image(emgoneImages, "scene_1_big");
        cityBigScenario = new Image(emgoneImages, "scene_2_big");
        forestBigScenario = new Image(emgoneImages, "scene_3_big");

        desertStage = new DesertStage();
        forestStage = new ForestStage();
        cityStage = new CityStage();

        desertScenarioImageButton = new ImageButton(emgoneSkin, "desert-scenario");
        cityScenarioImageButton = new ImageButton(emgoneSkin, "city-scenario");
        forestScenarioImageButton = new ImageButton(emgoneSkin, "forest-scenario");

        switch (currentScenarioTag) {
            case Constants.DESERT:
                currentBigScenario = desertBigScenario;
                selectedStage = desertStage;
                currentScenarioImageButton = desertScenarioImageButton;
                isNight = false;
                break;
            case Constants.CITY:
                currentBigScenario = cityBigScenario;
                selectedStage = cityStage;
                currentScenarioImageButton = cityScenarioImageButton;
                isNight = true;
                break;
            case Constants.FOREST:
                currentBigScenario = forestBigScenario;
                selectedStage = forestStage;
                currentScenarioImageButton = forestScenarioImageButton;
                isNight = false;
                break;
        }

        currentScenarioImageButton.setChecked(true);


        window.add(currentBigScenario).expandX().row();
        scenarioPreviewCell = window.getCell(currentBigScenario);

        selectedScenario = new Label(Constants.SCENARIO_NAMES.get(currentScenarioTag), emgoneSkin, "selected-costume");
        window.add(selectedScenario).colspan(3).top().pad(50, 0, 80, 0).row();

        window.add(new SplitPane(null, null, true, emgoneSkin)).expand().fillX().colspan(3).top().row();

        window.add(possibleScenarios(Gamification.getCurrentLevel())).expand().colspan(3).row();


        return window;
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
                    selectedStage = desertStage;
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
                    selectedStage = cityStage;
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
                    selectedStage = forestStage;
                    currentScenarioTag = 3;
                    isNight = false;
                }
            }
        });

        table.add(desertScenarioImageButton).expandX().top();
        cityScenarioImageButton.setDisabled(
                Achievement.isLocked(CITY, playerLevel));
        table.add(cityScenarioImageButton).padBottom(50).expandX();
        forestScenarioImageButton.setDisabled(
                Achievement.isLocked(FOREST, playerLevel));
        table.add(forestScenarioImageButton).expandX().top();

        return table;
    }

}
