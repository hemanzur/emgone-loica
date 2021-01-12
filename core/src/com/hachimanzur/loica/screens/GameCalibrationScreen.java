package com.nursoft.emgone.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nursoft.emgone.main.MainGame;
import com.nursoft.emgone.util.Constants;
import com.nursoft.emgone.util.GamePreferences;
import com.nursoft.emgone.util.Gamification.Gamification;


public class GameCalibrationScreen implements Screen {

    private TextField tfGameTime;
    private SelectBox obsModeSelectBox;
    private SelectBox bodyPartSelectBox;
    private Slider sldDifficulty;

    private Skin emgoneSkin;
    private Skin emgoneImages;

    private GamePreferences prefs;

    private boolean showGameConfig = true;
    private Table gamePage;
    private Table exercisePage;
    private Table currentPage;
    private Cell currentPageCell;

    private Label periodHeader;
    private Label periodNumber;
    private int playerLevel;
    private float nearnessFactor;
    private float velocity;
    private double period;

    private float maxCalibrationHeight;
    private float minCalibrationHeight;

    private TextButton btnSave;

    private Stage stage;

    public MainGame game;

    public GameCalibrationScreen(MainGame game) {
        this.game = game;
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
            game.setScreen(new SummaryScreen(game, false));
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

    private void loadSettings() {
        obsModeSelectBox.setSelected(prefs.obstacleMode);

        tfGameTime.setText(String.valueOf(prefs.gameTime));
        bodyPartSelectBox.setSelected(prefs.bodyPart);
        sldDifficulty.setValue((1/prefs.strengthThreshold));

        minCalibrationHeight = prefs.minCalibrationHeight;
        maxCalibrationHeight = prefs.maxCalibrationHeight;
    }

    private void saveSettings() {
        prefs = GamePreferences.instance;

        prefs.obstacleMode = (String)obsModeSelectBox.getSelected();
        prefs.gameTime = Integer.parseInt(tfGameTime.getText());
        prefs.bodyPart = (String)bodyPartSelectBox.getSelected();
        prefs.strengthThreshold = 1/(sldDifficulty.getValue());
        if(Constants.OBSTACLE_MODES.get((String)obsModeSelectBox.getSelected()).equals("force")) {
            prefs.forceDuration = (int)period;
        }
        prefs.maxCalibrationHeight = maxCalibrationHeight;
        prefs.minCalibrationHeight = minCalibrationHeight;
        prefs.save();
    }

    private void rebuildStage() {
        prefs = GamePreferences.instance;
        prefs.load();

        playerLevel = Gamification.getCurrentLevel();
        velocity = Gamification.velocityForLevel(playerLevel) * 400;
        if (Constants.OBSTACLE_MODES.get(prefs.obstacleMode).equals("force")) {
            nearnessFactor = Gamification.getForceDistance(playerLevel);
            period = Constants.calculateForcePeriod(nearnessFactor);
        } else {
            nearnessFactor = Gamification.getNearness(playerLevel);
            period = Constants.calculatePeriod(velocity, nearnessFactor);
        }

        emgoneSkin = new Skin(
                Gdx.files.internal(Constants.EMGONE_SKIN),
                new TextureAtlas(Constants.EMGONE_ATLAS)
        );

        emgoneImages = new Skin(new TextureAtlas(Constants.EMGONE_IMAGES_ATLAS));

        Table layerBackground = buildBackgroundLayer();
        Table layerControls = buildControlsLayer();
        //layerControls.debug();

        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerControls);

        stage.getRoot().addCaptureListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (!(event.getTarget() instanceof TextField)) {
                    stage.setKeyboardFocus(null);
                    Gdx.input.setOnscreenKeyboardVisible(false);
                }
                return false;
            }
        });
        loadSettings();
    }

    private Table buildBackgroundLayer() {
        Table layer = new Table();
        Image imgBackground = new Image(emgoneImages, "Fondo_configuracion");
        layer.add(imgBackground).width(Constants.VIEWPORT_WIDTH).height(Constants.VIEWPORT_HEIGHT);
        Image imgBackgroundBottom = new Image(emgoneImages, "Fondobottom_configuracion");
        layer.addActor(imgBackgroundBottom);
        imgBackgroundBottom.setSize(stage.getWidth(), stage.getHeight()/4);
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
                game.setScreen(new SummaryScreen(game, false));
            }
        });

        Label conf = new Label("CONFIGURACIÓN", emgoneSkin, "big-title");
        layer.add(conf).expand().row();

        layer.add(buildCalibrationLayer()).width(Constants.VIEWPORT_WIDTH*0.8f).height(Constants.VIEWPORT_HEIGHT*0.7f).expand().fillY().row();

        btnSave = new TextButton("GUARDAR", emgoneSkin, "btn-black");
        layer.add(btnSave).expand();
        btnSave.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveSettings();
                game.setScreen(new SummaryScreen(game, false));
            }
        });

        return layer;

    }

    private Table buildGamePage(Float padBottomSections, Float padBottomStats) {
        Table page = new Table();
        Label practiceTab = new Label("Juego", emgoneSkin, "tab-selected");
        practiceTab.setAlignment(Align.center);
        practiceTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentPageCell.setActor(gamePage);
            }
        });
        Label exerciseTab = new Label("Ejercicio", emgoneSkin, "tab-regular");
        exerciseTab.setAlignment(Align.center);
        exerciseTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentPageCell.setActor(exercisePage);
            }
        });

        page.add(new SplitPane(practiceTab, exerciseTab, false, emgoneSkin)).expandX().fillX().colspan(3).row();

        Label obsMode = new Label("Modo de juego", emgoneSkin, "calib-labels-big");
        page.add(obsMode).colspan(3).expandY();
        page.row().padBottom(padBottomSections);
        obsModeSelectBox = new SelectBox(emgoneSkin);
        obsModeSelectBox.setItems(Constants.OBSTACLE_MODES_NAMES);
        page.add(obsModeSelectBox).width(400).height(60).center().colspan(3).row();
        obsModeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Constants.OBSTACLE_MODES.get((String)obsModeSelectBox.getSelected()).equals("force")) {
                    nearnessFactor = Gamification.getForceDistance(playerLevel);
                    period = Constants.calculateForcePeriod(nearnessFactor);
                    periodHeader.setText("Duración de los obstáculos:");
                    periodNumber.setText(String.format("%.0f", period) + " segs");
                } else {
                    nearnessFactor = Gamification.getNearness(playerLevel);
                    period = Constants.calculatePeriod(velocity, nearnessFactor);
                    periodHeader.setText("Periodicidad de los obstáculos:");
                    periodNumber.setText(String.format("%.01f", period) + " segs");
                }
            }
        });


        page.add(new SplitPane(null, null, true, emgoneSkin)).expandX().fillX().colspan(3);

        page.row().padTop(padBottomSections);
        // now show the parameters
        int heightMaximumFactor = Gamification.getHeightFactor(playerLevel);
        int actualHeight = 25 + (25 * heightMaximumFactor);

        Label playerLevelHeader = new Label("Estás en el nivel:", emgoneSkin, "stats-header");
        page.add(playerLevelHeader).colspan(3);

        page.row().padBottom(padBottomStats).padTop(-10);
        Label playerLevelNumber = new Label(Integer.toString(playerLevel), emgoneSkin, "stats-level-big");
        page.add(playerLevelNumber).colspan(3);

        page.row();
        Label heightHeader = new Label("Tamaño de los obstáculos:", emgoneSkin, "stats-header");
        page.add(heightHeader).colspan(3);

        page.row().padBottom(padBottomStats);
        Label heightNumber = new Label(Integer.toString(actualHeight) + " cm", emgoneSkin, "stats-parameters");
        page.add(heightNumber).colspan(3);

        page.row();
        String periodString = "Periodicidad de los obstáculos:";
        if (Constants.OBSTACLE_MODES.get(prefs.obstacleMode).equals("force")) {
            periodString = "Duración de los obstáculos:";
        }
        periodHeader = new Label(periodString, emgoneSkin, "stats-header");
        page.add(periodHeader).colspan(3);

        page.row().padBottom(padBottomStats);
        String periodFormat = "%.01f";
        if (Constants.OBSTACLE_MODES.get(prefs.obstacleMode).equals("force")) {
            periodFormat = "%.0f";
        }
        periodNumber = new Label(String.format(periodFormat, period) + " segs", emgoneSkin, "stats-parameters");
        page.add(periodNumber).colspan(3).row();
        return page;
    }

    private Table buildExercisePage(Float padBottomSections) {
        final int COLSPAN = 4;
        final int HALF_COLSPAN = 2;

        Table page = new Table();
        Label practiceTab = new Label("Juego", emgoneSkin, "tab-regular");
        practiceTab.setAlignment(Align.center);
        practiceTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentPageCell.setActor(gamePage);
            }
        });
        Label exerciseTab = new Label("Ejercicio", emgoneSkin, "tab-selected");
        exerciseTab.setAlignment(Align.center);
        exerciseTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentPageCell.setActor(exercisePage);
            }
        });

        page.add(new SplitPane(practiceTab, exerciseTab, false, emgoneSkin))
                .expandX().fillX().colspan(COLSPAN).row();

        Label bodyPart = new Label("Zona del cuerpo a ejercitar", emgoneSkin, "calib-labels-big");
        page.add(bodyPart).colspan(COLSPAN).expandY();
        page.row().padBottom(padBottomSections);
        bodyPartSelectBox = new SelectBox(emgoneSkin);
        bodyPartSelectBox.setItems(Constants.BODY_PARTS_LIST);
        page.add(bodyPartSelectBox).width(400).height(60).center().colspan(COLSPAN).row();

        page.add(new SplitPane(null, null, true, emgoneSkin))
                .expandX().fillX().colspan(COLSPAN).row();

        Label lblGameTime = new Label("Tiempo de ejercicio (segs.)", emgoneSkin, "calib-labels-big");
        page.add(lblGameTime).colspan(COLSPAN).expandY();
        page.row().padBottom(padBottomSections);
        ImageButton btnLess = new ImageButton(emgoneSkin, "less");
        ImageButton btnMore = new ImageButton(emgoneSkin, "more");
        tfGameTime = new TextField("", emgoneSkin, "config-value");
        tfGameTime.setAlignment(Align.center);
        tfGameTime.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        tfGameTime.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                int currentGameTime = "".equals(tfGameTime.getText()) ? 0 : Integer.parseInt(tfGameTime.getText());
                if (!focused && currentGameTime < Constants.MIN_EXERCISE_TIME) {
                    tfGameTime.setText(String.valueOf(Constants.MIN_EXERCISE_TIME));
                }
            }
        });
        page.add(btnLess).right();
        btnLess.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int currentGameTime = Integer.parseInt(tfGameTime.getText());
                if(currentGameTime > Constants.MIN_EXERCISE_TIME){
                    tfGameTime.setText(String.valueOf(currentGameTime-Constants.DELTA_EXCERCISE_TIME));
                }
            }
        });
        page.add(tfGameTime).colspan(HALF_COLSPAN).fill();
        page.add(btnMore).left();
        btnMore.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int currentGameTime = Integer.parseInt(tfGameTime.getText());
                tfGameTime.setText(String.valueOf(currentGameTime+Constants.DELTA_EXCERCISE_TIME));
            }
        });

        page.row();
        page.add(new SplitPane(null, null, true, emgoneSkin)).expandX().fillX()
                .colspan(COLSPAN).row();

        Label difficulty = new Label("Fuerza de gravedad", emgoneSkin, "calib-labels-big");
        page.add(difficulty).colspan(COLSPAN).expandY();
        page.row();
        sldDifficulty = new Slider(0.5f, 2f, 0.25f, false, emgoneSkin, "slider-7step-horizontal");
        page.add(sldDifficulty).width(440).colspan(COLSPAN).center();
        sldDifficulty.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("Difficulty slider", Float.toString(sldDifficulty.getValue()));
                Gdx.app.log("Difficulty calculated", Float.toString(2.0f - (sldDifficulty.getValue())));
            }
        });
        page.row();
        page.add(new Label("Baja", emgoneSkin, "slider-knob-title")).uniform();
        page.add(new Label("Normal", emgoneSkin, "slider-knob-title")).uniform();
        page.add(new Label("Alta", emgoneSkin, "slider-knob-title")).uniform();
        page.add(new Label("Muy Alta", emgoneSkin, "slider-knob-title")).uniform();
        page.row().padBottom(padBottomSections);
        page.add(new Label("0.5x", emgoneSkin, "slider-knob-description")).uniform();
        page.add(new Label("1x", emgoneSkin, "slider-knob-description")).uniform();
        page.add(new Label("1.5x", emgoneSkin, "slider-knob-description")).uniform();
        page.add(new Label("2x", emgoneSkin, "slider-knob-description")).uniform();
        page.row();

        return page;
    }

    private Table buildCalibrationLayer() {
        Window window = new Window("", emgoneSkin);
        window.setMovable(false);
        // window.pad(30, 80, 30, 80);
        // window.debug();

        Float padBottomSections = 50.0f;
        Float padBottomStats = 30.0f;
        gamePage = buildGamePage(padBottomSections, padBottomStats);
        exercisePage = buildExercisePage(padBottomSections);

        currentPage = showGameConfig ? gamePage : exercisePage;

        window.add(currentPage).expand().fill();
        currentPageCell = window.getCell(currentPage);
        return window;
    }
}
