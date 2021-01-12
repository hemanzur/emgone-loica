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


public class CalibrateScreen implements Screen {

    private Slider sldObsHeight;
    //private Slider sldXVelocity;
    //private Slider sldObsDistance;
    private Slider sldPeriod;
    private TextField tfGameTime;
    private TextField tfForceDuration;
    private Slider sldForceRest;
    private SelectBox bodyPartSelectBox;
    private SelectBox obsModeSelectBox;
    private Slider sldDifficulty;

    private Skin emgoneSkin;
    private Skin emgoneImages;

    private Label obsPeriod;
    private Table forceIndicators;
    private Table coordIndicators;
    private Cell periodIndicatorCell;

    private GamePreferences prefs;

    private boolean showPracticeConfig = true;
    private Table practicePage;
    private Table exercisePage;
    private Table currentPage;
    private Cell currentPageCell;

    private float maxCalibrationHeight;
    private float minCalibrationHeight;

    private TextButton btnSave;

    private Stage stage;

    public MainGame game;


    public CalibrateScreen(MainGame game) {
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
            game.setScreen(new SummaryScreen(game, true));
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
        sldObsHeight.setValue(prefs.obstacleMaxHeightFactor);
        sldPeriod.setValue(6 - prefs.periodFactor);
        sldForceRest.setValue(prefs.forceRest);
        float forceDuration = Constants.calculateForcePeriod(prefs.distanceBetweenObstaclesFactor);
        Gdx.app.log("distanceObstaclesIn", String.valueOf(prefs.distanceBetweenObstaclesFactor));
        Gdx.app.log("forceDurationIn", String.valueOf(forceDuration));
        tfForceDuration.setText(String.valueOf((int)forceDuration));

        bodyPartSelectBox.setSelected(prefs.bodyPart);
        tfGameTime.setText(String.valueOf(prefs.gameTime));
        sldDifficulty.setValue((1/prefs.strengthThreshold));

        minCalibrationHeight = prefs.minCalibrationHeight;
        maxCalibrationHeight = prefs.maxCalibrationHeight;
    }

    private void saveSettings() {
        prefs = GamePreferences.instance;
        int period = (int)(6 - sldPeriod.getValue());
        prefs.periodFactor = period;
        if (Constants.OBSTACLE_MODES.get(obsModeSelectBox.getSelected()).equals("force")) {
            // Note: THIS IS DISTANCE FORCE - DO NOT PROCESS FURTHER ON EMGOneGame!!
            prefs.distanceBetweenObstaclesFactor = Constants.calculateForceDistance(Integer.parseInt(tfForceDuration.getText()));
            Gdx.app.log("forceDurationOut", tfForceDuration.getText());
            Gdx.app.log("distanceObstaclesOut", String.valueOf(prefs.distanceBetweenObstaclesFactor));
            prefs.forceDuration = Integer.parseInt(tfForceDuration.getText());
        } else {
            prefs.distanceBetweenObstaclesFactor = 200 + (150 * period);
        }
        prefs.forceRest = (int)sldForceRest.getValue();
        prefs.loicaXVelocity = 1.125f - (0.125f * period);
        prefs.obstacleMaxHeightFactor = (int)sldObsHeight.getValue();
        prefs.obstacleMode = (String)obsModeSelectBox.getSelected();

        prefs.gameTime = Integer.parseInt(tfGameTime.getText());
        prefs.bodyPart = (String)bodyPartSelectBox.getSelected();
        prefs.strengthThreshold = 1/(sldDifficulty.getValue());

        prefs.maxCalibrationHeight = maxCalibrationHeight;
        prefs.minCalibrationHeight = minCalibrationHeight;
        prefs.save();
    }

    private void rebuildStage() {
        emgoneSkin = new Skin(
                Gdx.files.internal(Constants.EMGONE_SKIN),
                new TextureAtlas(Constants.EMGONE_ATLAS)
        );

        emgoneImages = new Skin(new TextureAtlas(Constants.EMGONE_IMAGES_ATLAS));

        prefs = GamePreferences.instance;
        prefs.load();

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
                game.setScreen(new SummaryScreen(game, true));
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
                game.setScreen(new SummaryScreen(game, true));
            }
        });

        return layer;

    }

    private Table buildPracticePage(Float padBottom) {
        Table page = new Table();
        Label practiceTab = new Label("Práctica", emgoneSkin, "tab-selected");
        practiceTab.setAlignment(Align.center);
        practiceTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentPageCell.setActor(practicePage);
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
        page.row().padBottom(padBottom);
        obsModeSelectBox = new SelectBox(emgoneSkin);
        obsModeSelectBox.setItems(Constants.OBSTACLE_MODES_NAMES);
        page.add(obsModeSelectBox).width(400).height(60).center().colspan(3).row();
        obsModeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Constants.OBSTACLE_MODES.get(obsModeSelectBox.getSelected()).equals("force")) {
                    obsPeriod.setText("Duración de obstáculos");
                    periodIndicatorCell.setActor(forceIndicators);
                } else {
                    obsPeriod.setText("Periodicidad de obstáculos");
                    periodIndicatorCell.setActor(coordIndicators);
                }
            }
        });

        page.add(new SplitPane(null, null, true, emgoneSkin)).expandX().fillX().colspan(3).row();

        String periodTitle = "Periodicidad de obstáculos";
        if (Constants.OBSTACLE_MODES.get(prefs.obstacleMode).equals("force")) {
            periodTitle = "Duración de obstáculos";
        }
        obsPeriod = new Label(periodTitle, emgoneSkin, "calib-labels-big");
        page.add(obsPeriod).colspan(3).expandY().padBottom(5).row();

        forceIndicators = buildForceIndicators(padBottom);
        coordIndicators = buildCoordIndicators(padBottom);
        Table periodLegend;
        if (Constants.OBSTACLE_MODES.get(prefs.obstacleMode).equals("force")) {
            periodLegend = forceIndicators;
        } else {
            periodLegend = coordIndicators;
        }
        page.add(periodLegend).colspan(3).expand().fill();
        periodIndicatorCell = page.getCell(periodLegend);
        page.row();

        page.add(new SplitPane(null, null, true, emgoneSkin)).expandX().fillX().colspan(3).row();

        Label obsHeight = new Label("Tamaño de obstáculos", emgoneSkin, "calib-labels-big");
        page.add(obsHeight).colspan(3).expandY().padBottom(5).row();
        sldObsHeight = new Slider(1, 7, 1, false, emgoneSkin, "slider-7step-horizontal");
        page.add(sldObsHeight).width(440).colspan(3).center();
        page.row();
        page.add(new Label("Pequeño", emgoneSkin, "slider-knob-title")).uniform();
        page.add(new Label("Normal", emgoneSkin, "slider-knob-title")).uniform();
        page.add(new Label("Grande", emgoneSkin, "slider-knob-title")).uniform();
        page.row().padBottom(padBottom);
        page.add(new Label("50 cm", emgoneSkin, "slider-knob-description")).uniform();
        page.add(new Label("125 cm", emgoneSkin, "slider-knob-description")).uniform();
        page.add(new Label("200 cm", emgoneSkin, "slider-knob-description")).uniform();

        return page;
    }

    private Table buildForceIndicators(float padBottom) {
        Table page = new Table().padBottom(padBottom);
        ImageButton btnLess = new ImageButton(emgoneSkin, "less");
        ImageButton btnMore = new ImageButton(emgoneSkin, "more");
        tfForceDuration = new TextField("", emgoneSkin, "config-value");
        tfForceDuration.setAlignment(Align.center);
        tfForceDuration.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        tfForceDuration.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                // obstacle time shall not be lower than a certain constant
                int currentObstacleTime = "".equals(tfForceDuration.getText()) ? 0 : Integer.parseInt(tfForceDuration.getText());
                if (!focused && currentObstacleTime < Constants.MIN_OBSTACLE_TIME) {
                    tfForceDuration.setText(String.valueOf(Constants.MIN_OBSTACLE_TIME));
                }
            }
        });
        page.add(btnLess).right();
        btnLess.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int currentObstacleTime = Integer.parseInt(tfForceDuration.getText());
                if(currentObstacleTime > Constants.MIN_OBSTACLE_TIME) {
                    tfForceDuration.setText(String.valueOf(
                            currentObstacleTime - Constants.DELTA_OBSTACLE_TIME
                    ));
                }
            }
        });
        page.add(tfForceDuration).width(170);
        page.add(btnMore).left();
        btnMore.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int currentObstacleTime = Integer.parseInt(tfForceDuration.getText());
                tfForceDuration.setText(String.valueOf(
                        currentObstacleTime + Constants.DELTA_OBSTACLE_TIME
                ));
            }
        });

        page.row().padTop(padBottom);
        page.add(new SplitPane(null, null, true, emgoneSkin)).expandX().fillX().colspan(3).row();

        page.add(new Label("Duración de descanso", emgoneSkin, "calib-labels-big")).colspan(3).expandY().padBottom(5).row();

        sldForceRest = new Slider(1, 5, 1, false, emgoneSkin, "slider-darkgreen");
        page.add(sldForceRest).width(400).colspan(3).center();
        page.row();
        page.add(new Label("Mitad", emgoneSkin, "slider-knob-title")).uniform();
        page.add(new Label("Igual", emgoneSkin, "slider-knob-title")).uniform();
        page.add(new Label("Doble", emgoneSkin, "slider-knob-title")).uniform();
        page.row();
        page.add(new Label("0.5x", emgoneSkin, "slider-knob-description")).uniform();
        page.add(new Label("1x", emgoneSkin, "slider-knob-description")).uniform();
        page.add(new Label("2x", emgoneSkin, "slider-knob-description")).uniform();
        return page;
    }

    private Table buildCoordIndicators(float padBottom) {
        Table page = new Table();
        sldPeriod = new Slider(1, 5, 1, false, emgoneSkin, "slider-lightgreen");
        page.add(sldPeriod).width(440).colspan(3).center();
        page.row();
        page.add(new Label("Baja", emgoneSkin, "slider-knob-title")).uniform().expandX();
        page.add(new Label("Normal", emgoneSkin, "slider-knob-title")).uniform().expandX();
        page.add(new Label("Alta", emgoneSkin, "slider-knob-title")).uniform().expandX();
        page.row().padBottom(padBottom);
        page.add(new Label("cada 5 seg.", emgoneSkin, "slider-knob-description")).uniform();
        page.add(new Label("cada 3 seg.", emgoneSkin, "slider-knob-description")).uniform();
        page.add(new Label("cada 1 seg.", emgoneSkin, "slider-knob-description")).uniform();
        return page;
    }

    private Table buildExercisePage(Float padBottom) {
        final int COLSPAN = 4;
        final int HALF_COLSPAN = 2;

        Table page = new Table();
        final Label practiceTab = new Label("Práctica", emgoneSkin, "tab-regular");
        practiceTab.setAlignment(Align.center);
        practiceTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentPageCell.setActor(practicePage);
            }
        });
        final Label exerciseTab = new Label("Ejercicio", emgoneSkin, "tab-selected");
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
        page.row().padBottom(padBottom);
        bodyPartSelectBox = new SelectBox(emgoneSkin);
        bodyPartSelectBox.setItems(Constants.BODY_PARTS_LIST);
        page.add(bodyPartSelectBox).width(400).height(60).center().colspan(COLSPAN).row();

        page.add(new SplitPane(null, null, true, emgoneSkin))
                .expandX().fillX().colspan(COLSPAN).row();

        Label lblGameTime = new Label("Tiempo de ejercicio (segs.)", emgoneSkin, "calib-labels-big");
        page.add(lblGameTime).colspan(COLSPAN).expandY();
        page.row().padBottom(padBottom);
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
                if(currentGameTime - Constants.DELTA_EXCERCISE_TIME < Constants.MIN_EXERCISE_TIME) {
                    tfGameTime.setText(String.valueOf(Constants.MIN_EXERCISE_TIME));
                } else if(currentGameTime > Constants.MIN_EXERCISE_TIME){
                    tfGameTime.setText(String.valueOf(currentGameTime - Constants.DELTA_EXCERCISE_TIME));
                }
            }
        });
        page.add(tfGameTime).colspan(HALF_COLSPAN).fill();
        page.add(btnMore).left();
        btnMore.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int currentGameTime = Integer.parseInt(tfGameTime.getText());
                tfGameTime.setText(String.valueOf(currentGameTime + Constants.DELTA_EXCERCISE_TIME));
            }
        });

        page.row();
        page.add(new SplitPane(null, null, true, emgoneSkin))
                .expandX().fillX().colspan(COLSPAN).row();

        Label difficulty = new Label("Fuerza de gravedad", emgoneSkin, "calib-labels-big");
        page.add(difficulty).colspan(COLSPAN).expandY();
        page.row();
        sldDifficulty = new Slider(0.5f, 2f, 0.25f, false, emgoneSkin, "slider-7step-horizontal");
        page.add(sldDifficulty).width(400).colspan(COLSPAN).center();
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
        page.row().padBottom(padBottom);
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

        Float padBottom = 30.0f;
        practicePage = buildPracticePage(padBottom);
        exercisePage = buildExercisePage(padBottom);

        currentPage = showPracticeConfig ? practicePage : exercisePage;

        window.add(currentPage).expand().fill();
        currentPageCell = window.getCell(currentPage);

        return window;
    }
}
