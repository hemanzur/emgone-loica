package com.hachimanzur.loica.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
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
import com.hachimanzur.loica.util.Gamification.Gamification;

import java.text.DecimalFormat;


public class SummaryScreen implements Screen {

    private Skin emgoneSkin;
    private Skin emgoneImages;
    private Skin emgoneSelect;

    private GamePreferences prefs;

    private AbstractEMGStage selectedStage;
    private AbstractLoicaCostume selectedCostume;
    private boolean isNight;

    private TextButton editConfig;
    private TextButton editCustomize;
    private TextButton goPlay;
    private TextButton goRecalibrate;

    private Stage stage;

    public MainGame game;
    private boolean isPractice;

    public SummaryScreen(MainGame game, boolean isPractice) {
        this.game = game;
        this.isPractice = isPractice;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);
        rebuildStage();
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(deltaTime);
        stage.draw();

        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(new InitialScreen(game));
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
        emgoneSelect.dispose();
    }

    @Override
    public void dispose() {
    }

    private void loadSettings() {
        prefs = GamePreferences.instance;
        prefs.load();

        switch (prefs.costume) {
            case Constants.NORMAL_LOICA:
                selectedCostume = new NormalLoica();
                break;
            case Constants.SUPER_LOICA:
                selectedCostume = new SuperLoica();
                break;
            case Constants.SPARTAN_LOICA:
                selectedCostume = new SpartanLoica();
                break;
            case Constants.QUEEN_LOICA:
                selectedCostume = new QueenLoica();
                break;
            case Constants.WIZARD_LOICA:
                selectedCostume = new WizardLoica();
                break;
        }

        switch (prefs.scenario) {
            case Constants.DESERT:
                selectedStage = new DesertStage();
                break;
            case Constants.CITY:
                selectedStage = new CityStage();
                break;
            case Constants.FOREST:
                selectedStage = new ForestStage();
                break;
        }

        isNight = prefs.isNight;

        // Si esto es fuerza/resistencia, ajustar tiempo mínimo si es necesario
        String gameModeKey = Constants.OBSTACLE_MODES.get(prefs.obstacleMode);
        if (gameModeKey.equals("force")) {
            float distanceForce = prefs.distanceBetweenObstaclesFactor;
            if (!isPractice) {
                distanceForce = Gamification.getForceDistance(Gamification.getCurrentLevel());
            }
            float periodForce = Constants.calculateForcePeriod(distanceForce);
            if (prefs.gameTime < periodForce) {
                prefs.gameTime = Math.round(periodForce);
                prefs.save();
            }
        }

    }

    private void rebuildStage() {
        emgoneSkin = new Skin(
                Gdx.files.internal(Constants.EMGONE_SKIN),
                new TextureAtlas(Constants.EMGONE_ATLAS)
        );

        emgoneImages = new Skin(new TextureAtlas(Constants.EMGONE_IMAGES_ATLAS));
        emgoneSelect = new Skin(new TextureAtlas(Constants.SELECT_COSTUME_ATLAS));

        loadSettings();

        Table layerBackground = buildBackgroundLayer();
        Table layerControls = buildControlsLayer();
        //layerControls.debug();

        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerControls);
    }

    private Table buildBackgroundLayer() {
        Table layer = new Table();
        Image imgBackground = new Image(emgoneSelect, "bg_summary");
        layer.add(imgBackground).width(Constants.VIEWPORT_WIDTH).height(Constants.VIEWPORT_HEIGHT);
        Image imgBackgroundBottom = new Image(emgoneImages, "Fondobottom_configuracion");
        layer.addActor(imgBackgroundBottom);
        imgBackgroundBottom.setSize(stage.getWidth(), stage.getHeight()/4);
        return layer;
    }

    private Table buildControlsLayer() {
        Table layer = new Table();
        layer.pad(50, 20, 50, 20);
        // layer.debug();

        TextButton btnBackToMenu = new TextButton("IR A MENÚ", emgoneSkin, "btn-backto");
        layer.add(btnBackToMenu).left().row();
        btnBackToMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new InitialScreen(game));
            }
        });

        Label title = new Label("RESUMEN", emgoneSkin, "big-title");
        layer.add(title).center().colspan(2).expand().row();

        layer.add(buildCalibrationLayer()).colspan(2).width(Constants.VIEWPORT_WIDTH*0.8f).height(Constants.VIEWPORT_HEIGHT*0.7f).expand().fillY().row();

        goRecalibrate = new TextButton("RECALIBRAR", emgoneSkin, "btn-black-big");
        if (game.isCalibrated) {
            layer.add(goRecalibrate);
            goRecalibrate.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setScreen(new SensorCalibrationScreen(game, isPractice));
                }
            });
        }

        goPlay = new TextButton(game.isCalibrated ? "¡A JUGAR!" : "CALIBRAR", emgoneSkin, "btn-black-big");
        layer.add(goPlay).colspan(game.isCalibrated ? 1 : 2);
        goPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.isCalibrated) {
                    game.setScreen(new EmgOneGame(game, selectedStage, selectedCostume, isNight, isPractice));
                } else {
                    game.setScreen(new SensorCalibrationScreen(game, isPractice));
                }
            }
        });

        return layer;

    }

    private Table buildCalibrationLayer() {
        Window window = new Window("", emgoneSkin);
        window.setMovable(false);
        // Graphic has a left padding of 14 and bottom padding of 15, because of the shadows
        window.pad(10, 14 + 20, 15 + 10, 20);
        // window.debug();

        String gameMode = prefs.obstacleMode;
        if (gameMode.isEmpty()) gameMode = Constants.OBSTACLE_MODES_NAMES.first();
        String gameModeKey = Constants.OBSTACLE_MODES.get(gameMode);
        int obstacleSize = 25 + (25 * prefs.obstacleMaxHeightFactor);
        float obstaclePeriod = prefs.periodFactor;
        float nearness = prefs.distanceBetweenObstaclesFactor;
        if (!isPractice) {
            int playerLevel = Gamification.getCurrentLevel();
            float velocity = Gamification.velocityForLevel(playerLevel) * 400;
            if (gameModeKey.equals("force")) {
                nearness = Gamification.getForceDistance(playerLevel);
            } else {
                nearness = Gamification.getNearness(playerLevel);
            }
            obstaclePeriod = (float)Constants.calculatePeriod(velocity, nearness);
        }
        String bodyPart = prefs.bodyPart;
        int gameTime = prefs.gameTime;
        float difficultyPercentage = 1/prefs.strengthThreshold;

        int loicaCostume = prefs.costume;
        int scenario = prefs.scenario;

        Label configTitle = new Label("Configuración", emgoneSkin, "summary-title");
        window.add(configTitle).center().colspan(2).expandY();

        Float spacingLines = 15.0f;

        window.row();
        Table gameModeLine = new Table();
        String gmType = isPractice ? "Modo de práctica: " : "Modo de juego: ";
        Label gmTitle = new Label(gmType, emgoneSkin, "config-header");
        Label gmValue = new Label(gameMode, emgoneSkin, "config-value");
        gameModeLine.add(gmTitle);
        gameModeLine.add(gmValue);
        window.add(gameModeLine).colspan(2).left().spaceBottom(spacingLines);

        window.row();
        Table obstacleSizeLine = new Table();
        Label osTitle = new Label("Tamaño de obstáculos: ", emgoneSkin, "config-header");
        String osDescription = "Normal";
        if (obstacleSize < 100) osDescription = "Pequeño";
        else if (obstacleSize > 100) osDescription = "Grande";
        Label osValue = new Label(osDescription + " (" + Integer.toString(obstacleSize) + " cm)", emgoneSkin, "config-value");
        obstacleSizeLine.add(osTitle);
        obstacleSizeLine.add(osValue);
        window.add(obstacleSizeLine).colspan(2).left().spaceBottom(spacingLines);

        window.row();
        Table obstaclePeriodLine = new Table();
        String opTitleString = "Periodicidad de obstáculos: ";
        String opDescription = isPractice ? "Cada " : "Aprox. ";
        DecimalFormat opFormat = new DecimalFormat("#.#");
        if (gameModeKey.equals("force")) {
            opTitleString = "Duración de obstáculos: ";
            opDescription = "Aprox. ";
            obstaclePeriod = Constants.calculateForcePeriod(nearness);
            opFormat.applyPattern("#");
        }
        Label opTitle = new Label(opTitleString, emgoneSkin, "config-header");
        Label opValue = new Label(opDescription + opFormat.format(obstaclePeriod) + " segs.", emgoneSkin, "config-value");
        obstaclePeriodLine.add(opTitle);
        obstaclePeriodLine.add(opValue);
        window.add(obstaclePeriodLine).colspan(2).left().spaceBottom(spacingLines);

        if (gameModeKey.equals("force")) {
            window.row();
            Table restTimeLine = new Table();
            Label rtTitle = new Label("Proporción de descanso: ", emgoneSkin, "config-header");
            float restMultiplier;
            if (isPractice) {
                restMultiplier = Constants.getRestMultiplier(prefs.forceRest);
            } else {
                restMultiplier = Gamification.getRestForLevel(Gamification.getCurrentLevel());
            }
            Label rtValue = new Label(new DecimalFormat("#.##").format(restMultiplier) + "x", emgoneSkin, "config-value");
            restTimeLine.add(rtTitle);
            restTimeLine.add(rtValue);
            window.add(restTimeLine).colspan(2).left().spaceBottom(spacingLines);
        }

        window.row();
        Table bodyPartLine = new Table();
        Label bpTitle = new Label("Zona del cuerpo a ejercitar: ", emgoneSkin, "config-header");
        Label bpValue = new Label(Constants.shortBodyPart(bodyPart), emgoneSkin, "config-value");
        bodyPartLine.add(bpTitle);
        bodyPartLine.add(bpValue);
        window.add(bodyPartLine).colspan(2).left().spaceBottom(spacingLines);

        window.row();
        Table gameTimeLine = new Table();
        Label gtTitle = new Label("Duración de ejercicio: ", emgoneSkin, "config-header");
        Label gtValue = new Label(Integer.toString(gameTime) + " segs.", emgoneSkin, "config-value");
        gameTimeLine.add(gtTitle);
        gameTimeLine.add(gtValue);
        window.add(gameTimeLine).colspan(2).left().spaceBottom(spacingLines);

        window.row();
        Table difficultyLine = new Table();
        Label dpTitle = new Label("Fuerza de gravedad: ", emgoneSkin, "config-header");
        String dpDescription = "Normal";
        if (difficultyPercentage < 1) dpDescription = "Baja";
        else if (difficultyPercentage > 1.5 && difficultyPercentage < 2) dpDescription = "Alta";
        else if (difficultyPercentage == 2) dpDescription = "Muy Alta";
        Label dpValue = new Label(dpDescription + " (" + new DecimalFormat("#.##").format(difficultyPercentage) + "x)", emgoneSkin, "config-value");
        difficultyLine.add(dpTitle);
        difficultyLine.add(dpValue);
        window.add(difficultyLine).colspan(2).left().spaceBottom(spacingLines);

        window.row();
        editConfig = new TextButton("EDITAR", emgoneSkin, "start-calibration");
        window.add(editConfig).colspan(2).expandY().width(200);
        editConfig.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isPractice) {
                    game.setScreen(new CalibrateScreen(game));
                } else {
                    game.setScreen(new GameCalibrationScreen(game));
                }

            }
        });

        window.row();
        window.add(new SplitPane(null, null, true, emgoneSkin)).expandX().fillX().colspan(2);

        window.row();
        Label customizeTitle = new Label("Personalización", emgoneSkin, "summary-title");
        window.add(customizeTitle).center().colspan(2).expandY();

        Image selectedLoicaImage = new Image(emgoneSelect, "loica" + Integer.toString(loicaCostume) + "_bg");
        Image selectedScenarioImage = new Image(emgoneSelect, "scene_" + Integer.toString(scenario) + "_big");
        Label selectedLoicaText = new Label(Constants.LOICA_NAMES.get(loicaCostume), emgoneSkin, "selected-costume-small");
        Label selectedScenarioText = new Label(Constants.SCENARIO_NAMES.get(scenario), emgoneSkin, "selected-costume-small");
        selectedLoicaImage.setScaling(Scaling.fit);
        selectedScenarioImage.setScaling(Scaling.fit);

        window.row().height(150);
        window.add(selectedLoicaImage).width(200);
        window.add(selectedScenarioImage).width(200);

        window.row();
        window.add(selectedLoicaText).uniform();
        window.add(selectedScenarioText).uniform();

        window.row();
        editCustomize = new TextButton("EDITAR", emgoneSkin, "start-calibration");
        window.add(editCustomize).colspan(2).expandY().width(200);
        editCustomize.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CustomizationScreen(game, isPractice));
            }
        });
        return window;
    }
}
