package com.hachimanzur.loica.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
import com.hachimanzur.loica.main.MainGame;
import com.hachimanzur.loica.util.Constants;
import com.hachimanzur.loica.util.GamePreferences;
import com.hachimanzur.loica.util.MicProcessor;


public class SensorCalibrationScreen implements Screen {

    public MainGame game;
    private Skin emgoneSkin;
    private Skin emgoneImages;
    private Stage stage;
    private Image imgLoicaCalib;
    private Image imgSliderCalib;

    private float initialMaxCalibrationHeight = 0;
    private float initialMinCalibrationHeight = 20000;
    private float minCalibrationHeight;
    private float maxCalibrationHeight;
    private Window window;

    //Loica min and max position inside window
    private float minLoicaHeight = Constants.VIEWPORT_HEIGHT*0.40f;
    private float maxLoicaHeight = Constants.VIEWPORT_HEIGHT*0.69f;

    private MicProcessor recorder;
    private float movingAverage;

    int minOffset;

    private boolean isPractice;

    private float minElapsed;
    private float maxElapsed;
    private boolean minEnabled = false;
    private boolean maxEnabled = false;

    private TextButton btnResetMin;
    private TextButton btnResetMax;


    public SensorCalibrationScreen(MainGame g, boolean isPractice) {
        this.game = g;
        recorder = new MicProcessor();
        this.isPractice = isPractice;
    }


    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);
        rebuildStage();
        maxCalibrationHeight = initialMaxCalibrationHeight;
        minCalibrationHeight = initialMinCalibrationHeight;
        minElapsed = 0;
        maxElapsed = 0;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        movingAverage = recorder.getMovingAverage();
        if(movingAverage < minCalibrationHeight){
            if(minOffset==0){
                minCalibrationHeight = movingAverage;
            }
            else{minOffset--;}
        }
        if(movingAverage > maxCalibrationHeight){
            maxCalibrationHeight = movingAverage;
        }

        imgLoicaCalib.setY(map(movingAverage, minCalibrationHeight, maxCalibrationHeight,  minLoicaHeight, maxLoicaHeight));

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(new com.hachimanzur.loica.screens.SummaryScreen(game, isPractice));
        }

        minElapsed += delta;
        maxElapsed += delta;
        // Check if minimum is disabled and it should be enabled
        if (!minEnabled && minElapsed > Constants.CALIBRATION_ENABLE_TIME) {
            // set min as enabled
            minEnabled = true;
            btnResetMin.setDisabled(false);
            if (maxEnabled || maxElapsed > Constants.CALIBRATION_ENABLE_TIME) {
                // if max is already enabled, or it should be enabled right now, use calibracion_enable
                imgSliderCalib.setDrawable(emgoneImages, "calibracion_enable");
                // enable max if it wasn't
                maxEnabled = true;
                btnResetMax.setDisabled(false);
            } else {
                // if max is not enabled yet, use calibracion_minonly
                imgSliderCalib.setDrawable(emgoneImages, "calibracion_minonly");
            }
        }
        // now, check if maximum is disabled and it should be enabled
        if (!maxEnabled && maxElapsed > Constants.CALIBRATION_ENABLE_TIME) {
            // set max as enabled
            maxEnabled = true;
            btnResetMax.setDisabled(false);
            if (minEnabled || minElapsed > Constants.CALIBRATION_ENABLE_TIME) {
                // if min is already enabled, or it should be enabled now, use calibracion_enable
                imgSliderCalib.setDrawable(emgoneImages, "calibracion_enable");
                // enable min if it wasn't
                minEnabled = true;
                btnResetMin.setDisabled(false);
            } else {
                // if min is not enabled, use calibracion_maxonly
                imgSliderCalib.setDrawable(emgoneImages, "calibracion_maxonly");
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
        recorder.dispose();
        emgoneImages.dispose();
        game.isCalibrated = true;
    }

    @Override
    public void dispose() {
    }

    private void rebuildStage() {
        emgoneSkin = new Skin(
                Gdx.files.internal(Constants.EMGONE_SKIN),
                new TextureAtlas(Constants.EMGONE_ATLAS)
        );

        emgoneImages = new Skin(new TextureAtlas(Constants.EMGONE_IMAGES_ATLAS));

        imgLoicaCalib = new Image(emgoneImages, "loica1");

        Table layerBackground = buildBackgroundLayer();
        Table layerControlButtons = buildControlsLayer();


        // assemble stage for menu screen
        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stage.addActor(imgLoicaCalib);
        imgLoicaCalib.setX(Constants.VIEWPORT_WIDTH/3 - imgLoicaCalib.getWidth()/2);
        imgLoicaCalib.setY(minLoicaHeight);
        stack.setSize(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerControlButtons);
    }

    private Table buildBackgroundLayer() {
        Table layer = new Table();
        Image imgBackground = new Image(emgoneImages, "Fondo_configuracion");
        layer.add(imgBackground).width(Constants.VIEWPORT_WIDTH).height(Constants.VIEWPORT_HEIGHT);
        Image imgBackgroundBottom = new Image(emgoneImages, "Fondobottom_configuracion");
        layer.addActor(imgBackgroundBottom);
        imgBackgroundBottom.setSize(stage.getWidth(), stage.getHeight() / 4);
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
                game.setScreen(new com.hachimanzur.loica.screens.SummaryScreen(game, isPractice));
            }
        });

        Label conf = new Label("CALIBRAR", emgoneSkin, "big-title");
        layer.add(conf).expand().row();

        layer.add(buildLoicaWindow()).width(Constants.VIEWPORT_WIDTH*0.8f).height(Constants.VIEWPORT_HEIGHT*0.7f).expand().fillY().row();

        TextButton btnBack = new TextButton("GUARDAR", emgoneSkin, "btn-black");
        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GamePreferences prefs = GamePreferences.instance;
                prefs.minCalibrationHeight = minCalibrationHeight;
                prefs.maxCalibrationHeight = maxCalibrationHeight;
                prefs.save();
                game.setScreen(new SummaryScreen(game, isPractice));
            }
        });

        layer.add(btnBack).expand();

        return layer;
    }

    private Table buildLoicaWindow() {
        window = new Window("", emgoneSkin);
        window.setMovable(false);
        window.pad(50, 80, 50, 80);
        //window.debug();

        imgSliderCalib = new Image(emgoneImages, "calibracion_disable");
        window.add(imgSliderCalib).height(500).expandX();

        btnResetMax = new TextButton("REESTABLECER", emgoneSkin, "start-calibration");
        btnResetMax.setDisabled(true);
        btnResetMax.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!btnResetMax.isDisabled()) {
                    maxCalibrationHeight = initialMaxCalibrationHeight;
                    maxEnabled = false;
                    maxElapsed = 0.0f;
                    btnResetMax.setDisabled(true);
                    imgSliderCalib.setDrawable(emgoneImages, "calibracion_minonly");
                }
            }
        });

        btnResetMin = new TextButton("REESTABLECER", emgoneSkin, "start-calibration");
        btnResetMin.setDisabled(true);
        btnResetMin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!btnResetMin.isDisabled()) {
                    minCalibrationHeight = initialMinCalibrationHeight;
                    minEnabled = false;
                    minElapsed = 0.0f;
                    btnResetMin.setDisabled(true);
                    imgSliderCalib.setDrawable(emgoneImages, "calibracion_maxonly");
                }
            }
        });

        Table resetButtons = new Table();
        resetButtons.add(btnResetMax).expandY().top();
        resetButtons.row();
        resetButtons.add(btnResetMin).expandY().bottom();

        window.add(resetButtons).fillY();

        window.row().padTop(20);
        window.add(new SplitPane(null, null, true, emgoneSkin)).expand().fillX().colspan(2);

        window.row();
        Label lblTitle = new Label("Instrucciones", emgoneSkin, "sensor-calib-title");
        Label lblInstructions = new Label("Realice repeticiones del ejercicio de\n" +
                "control para calibrar correctamente,\n" +
                "marcando claramente la posición de\n" +
                "reposo y la de tensión máxima", emgoneSkin, "instructions");
        lblInstructions.setAlignment(Align.center);

        Table instructions = new Table();
        instructions.add(lblTitle).row();
        instructions.add(lblInstructions).row();
        window.add(instructions).colspan(2).expandY();

        return window;
    }

    private float map(float x, float a, float b, float c, float d) {
        return ((d - c)*(x-a)/(b-a)) + c;

    }
}