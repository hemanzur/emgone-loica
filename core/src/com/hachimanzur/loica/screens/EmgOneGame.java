package com.hachimanzur.loica.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hachimanzur.loica.util.GamePreferences;
import com.hachimanzur.loica.util.Constants;
import com.hachimanzur.loica.util.Gamification.Achievements.UserAchievements;
import com.hachimanzur.loica.util.Gamification.Gamification;
import com.hachimanzur.loica.screens.modals.GameOverModal;
import com.hachimanzur.loica.loicas.AbstractLoicaCostume;
import com.hachimanzur.loica.main.MainGame;
import com.hachimanzur.loica.stages.AbstractEMGStage;
import com.hachimanzur.loica.util.MicProcessor;
import com.hachimanzur.loica.util.Obstacle;
import com.hachimanzur.loica.util.UserData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class EmgOneGame implements Screen {

    private com.hachimanzur.loica.main.MainGame game;

    // HUD stuff
    com.hachimanzur.loica.stages.AbstractEMGStage stage;
    private Stage uiStage;
    private Skin emgoneSkin;
    private TextureAtlas atlas = new TextureAtlas(com.hachimanzur.loica.util.Constants.EMGONE_IMAGES_ATLAS);
    private Table initialLayer;
    private Table playLayer;
    private Table pauseLayer;
//    private Table gameOverLayer;
    private TextButton infoBtn;
    private String dodgedObs = "ESQUIVADOS: ";
    private String time = "     TIEMPO: ";
    private Label pauseScore;
    private Label gameOverScore;

    // loica stuff
    Animation<TextureRegion> loica;
    Animation<TextureRegion> loica_crash;
    boolean loicaCrashing = false;
    Vector2 loicaPosition = new Vector2();
    Vector2 loicaVelocity = new Vector2();
    float loicaStateTime = 0;

    //background staff
    private float groundOffsetX = 0;
    private float mountainsOffsetX = 0;
    private float starOffset1 = 0;
    private float starOffset2;
    TextureRegion ceiling;
    Vector2 starPosition = new Vector2();
    Vector2 starVelocity = new Vector2();

    // camera stuff
    SpriteBatch batch;
    OrthographicCamera camera;

    // Obstacles logic
    LinkedList<com.hachimanzur.loica.util.Obstacle> obstacles = new LinkedList<com.hachimanzur.loica.util.Obstacle>();
    ArrayList<Integer> heightFactorsOfDodged = new ArrayList<Integer>();
    private boolean showTopObstacles;
    private boolean showBottomObstacles;
    private boolean alternateObstacles;
    private String obstacleMode;
    private float forceFirstObs;
    private float forceLastObs;
    private String obstacles_position;

    // General game logic
    private boolean isPractice;
    private int playerLevel;
    private GameState gameState = GameState.Start;
    private boolean startGame = false;

    private int obstaclesDodged = 0;
    private int numberOfObstaclesCollided = 0;
    private double obstaclesDodgedForce = 0;
    private double obstaclesCollidedForce = 0;
    private double correctionFactor = 1;
    private float period;

    private Rectangle loicaRectangle = new Rectangle();
    private Rectangle obstacleRectangle = new Rectangle();
    private float movingAverage = 0;
    private com.hachimanzur.loica.util.MicProcessor recorder;
    private final float totalTime = com.hachimanzur.loica.util.GamePreferences.instance.gameTime;
    private float gameTime;
    private boolean paused = false;
    private com.hachimanzur.loica.util.GamePreferences prefs;
    private Random rnd;


    /// Gamificaton logic (some values change with player level)
    private float nearnessFactor;
    private float forceRest;
    private int heightMaximumFactor;
    private float strengthThreshold;
    private float horizontalVelocity;
    private int thisGameScore;
    private int totalScore = 0;

    // Modals
    Stack uiStack = new Stack();
    GameOverModal gameOverModal;

    public EmgOneGame (MainGame game, AbstractEMGStage st, AbstractLoicaCostume loicaCostume, boolean isNight, boolean isPractice) {
        this.game = game;

        // HUD stuff
        this.stage = st;
        stage.init();

        // loica stuff
        loicaCostume.init();
        loica = loicaCostume.getAnimation(isNight);
        loica_crash = loicaCostume.getCrashAnimation(isNight);
        loica.setPlayMode(Animation.PlayMode.LOOP);
        loica_crash.setPlayMode(Animation.PlayMode.LOOP);

        // background stuff
        ceiling = new TextureRegion(stage.ground);
        ceiling.flip(true, true);

        // camera stuff
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_HEIGHT, com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_WIDTH);
        camera.rotate(-90);
        camera.translate(0, (camera.viewportWidth - camera.viewportHeight)/2);

        // obstacles stuff
        obstacleMode = com.hachimanzur.loica.util.Constants.OBSTACLE_MODES.get(com.hachimanzur.loica.util.GamePreferences.instance.obstacleMode);
        // default mode: coordination
        showBottomObstacles = true;
        showTopObstacles = true;
        alternateObstacles = false;
        if (obstacleMode.equals("speed")) {
            alternateObstacles = true;
        } else if (obstacleMode.equals("coordUp")) {
            showBottomObstacles = false;
        } else if (obstacleMode.equals("coordDn") || obstacleMode.equals("force")) {
            showTopObstacles = false;
        }

        // game stuff
        this.isPractice = isPractice;
        this.playerLevel = com.hachimanzur.loica.util.Gamification.Gamification.getCurrentLevel();

        recorder = new MicProcessor();
        strengthThreshold = com.hachimanzur.loica.util.GamePreferences.instance.strengthThreshold;
        Gdx.app.log("strengthThreshold", String.valueOf(strengthThreshold));

        if (isPractice) {
            nearnessFactor = com.hachimanzur.loica.util.GamePreferences.instance.distanceBetweenObstaclesFactor;
            heightMaximumFactor = com.hachimanzur.loica.util.GamePreferences.instance.obstacleMaxHeightFactor;
            period = com.hachimanzur.loica.util.GamePreferences.instance.periodFactor;
            horizontalVelocity = com.hachimanzur.loica.util.GamePreferences.instance.loicaXVelocity * 400;
            Gdx.app.log("horizontalVelocity", String.valueOf(horizontalVelocity));
            forceRest = com.hachimanzur.loica.util.Constants.getRestLevel(com.hachimanzur.loica.util.GamePreferences.instance.forceRest);
        }

        else {
            nearnessFactor = com.hachimanzur.loica.util.Gamification.Gamification.getNearness(playerLevel);
            heightMaximumFactor = com.hachimanzur.loica.util.Gamification.Gamification.getHeightFactor(playerLevel);
            period = (float) com.hachimanzur.loica.util.Constants.calculatePeriod((com.hachimanzur.loica.util.Gamification.Gamification.velocityForLevel(playerLevel) * 400),  nearnessFactor);
            horizontalVelocity = com.hachimanzur.loica.util.Gamification.Gamification.velocityForLevel(playerLevel) * 400;
            forceRest = com.hachimanzur.loica.util.Gamification.Gamification.getRestForLevel(playerLevel);
        }
        if (obstacleMode.equals("force")) {
            TextureRegion randomObstacle = stage.getRandomGroundObs(heightMaximumFactor - 1);
            if (!isPractice)
                nearnessFactor = com.hachimanzur.loica.util.Gamification.Gamification.getForceDistance(playerLevel);
            period = com.hachimanzur.loica.util.Constants.calculateForcePeriod(nearnessFactor);
            forceRest = com.hachimanzur.loica.util.Constants.getRestMultiplier(com.hachimanzur.loica.util.GamePreferences.instance.forceRest);
            correctionFactor = com.hachimanzur.loica.util.Constants.calculateForceCorrection(period, nearnessFactor, randomObstacle.getRegionWidth());
            horizontalVelocity = 300.0f;
            Gdx.app.log("obstacleWidth", String.valueOf(randomObstacle.getRegionWidth()));
        }
        rnd = new Random();
        Gdx.app.log("forceRest", String.valueOf(forceRest));
        Gdx.app.log("distance", String.valueOf(nearnessFactor));
        resetWorld();
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(!paused) {
            updateWorld();
        }
        drawWorld();

        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            //Do nothing
        }

        uiStage.act(delta);
        uiStage.draw();
    }

    enum GameState {
        Start, Running, GameOver
    }

    @Override
    public void show() {
        Gdx.input.setCatchBackKey(true);
        uiStage = new Stage(new FitViewport(com.hachimanzur.loica.util.Constants.VIEWPORT_WIDTH, com.hachimanzur.loica.util.Constants.VIEWPORT_HEIGHT));
        ((OrthographicCamera)uiStage.getCamera()).rotate(-90);
        Gdx.input.setInputProcessor(uiStage);
        buildUIStage();
        com.hachimanzur.loica.util.Gamification.Achievements.UserAchievements.instance.getAchievementsFromServer();
    }

    private void buildUIStage() {
        emgoneSkin = new Skin(
                Gdx.files.internal(com.hachimanzur.loica.util.Constants.EMGONE_SKIN),
                new TextureAtlas(com.hachimanzur.loica.util.Constants.EMGONE_ATLAS)
        );

        initialLayer = buildInitialLayer();
        playLayer = buildPlayLayer();
        pauseLayer = buildPauseLayer();

        uiStage.clear();
        uiStage.addActor(uiStack);
        uiStack.setPosition(-com.hachimanzur.loica.util.Constants.VIEWPORT_HEIGHT/5, com.hachimanzur.loica.util.Constants.VIEWPORT_WIDTH/3);
        uiStack.setSize(com.hachimanzur.loica.util.Constants.VIEWPORT_HEIGHT, com.hachimanzur.loica.util.Constants.VIEWPORT_WIDTH);
        uiStack.add(initialLayer);
        uiStack.add(playLayer);
        playLayer.setVisible(false);
        uiStack.add(pauseLayer);
        pauseLayer.setVisible(false);

    }

    private Table buildInitialLayer() {
        Table layer = new Table();
        TextButton btn = new TextButton(" ¡A VOLAR! ", emgoneSkin, "avolar");
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startGame = true;
            }
        });
        layer.add(btn);
        return layer;
    }

    private Table buildPlayLayer() {
        Table layer = new Table();
        layer.pad(30);

        TextButton pauseBtn = new TextButton("PAUSA", emgoneSkin);
        pauseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                paused = !paused;
                pauseLayer.setVisible(paused);
                pauseScore.setText("     " + obstaclesDodged + "/" + (obstaclesDodged + numberOfObstaclesCollided) + "     ");
            }
        });
        infoBtn = new TextButton("", emgoneSkin, "btn-game-info");

        layer.add(pauseBtn).expand().top().left();
        layer.add(infoBtn).width(com.hachimanzur.loica.util.Constants.VIEWPORT_HEIGHT/3).fillX().top();
        layer.add().width(com.hachimanzur.loica.util.Constants.VIEWPORT_HEIGHT/3 - 30);

        return layer;
    }

    private Table buildPauseLayer() {
        Table layer = new Table();
        Window window = new Window("", emgoneSkin);
        window.pad(30);

        window.add(new Label("PAUSA", emgoneSkin, "pause-window")).colspan(3).row();
        window.add(new Image(stage.endGameObs)).expand();
        pauseScore = new Label("     " + obstaclesDodged + "/" + (obstaclesDodged + numberOfObstaclesCollided) + "     ", emgoneSkin, "score");
        window.add(pauseScore).expand();
        window.add(new Image(atlas.findRegion("profile-01"))).expand().row();


        TextButton btnBackToGame = new TextButton("VOLVER A JUEGO", emgoneSkin, "save");
        btnBackToGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                paused = !paused;
                pauseLayer.setVisible(paused);
                pauseScore.setText("     " + obstaclesDodged + "/" + (obstaclesDodged + numberOfObstaclesCollided) + "     ");
            }
        });

        TextButton btnBackToMenu = new TextButton("VOLVER A MENÚ", emgoneSkin, "save");
        btnBackToMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new com.hachimanzur.loica.screens.SummaryScreen(game, isPractice));
            }
        });

        window.add(btnBackToGame).padTop(30).padBottom(15);
        window.add();
        window.add(btnBackToMenu).padTop(30).padBottom(15);

        window.setMovable(false);

        layer.add(window);

        return layer;
    }

    private GameOverModal buildGameOverModal() {
        GameOverModal gameOverModal = new GameOverModal();
        gameOverModal.build(obstaclesDodged, numberOfObstaclesCollided, emgoneSkin, stage.endGameObs, atlas,
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        postData();
                        game.setScreen(new SummaryScreen(game, isPractice));
                    }
                }
        );
        gameOverModal.setVisible(false);
        return gameOverModal;
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        recorder.dispose();
        stage.dispose();
        uiStage.dispose();
        emgoneSkin.dispose();
    }

    @Override
    public void dispose() {
    }


    public void endGame() {
        gameState = GameState.GameOver;

        playLayer.setVisible(false);

        Gdx.app.log("period", String.valueOf(period));
        Gdx.app.log("correction", String.valueOf(correctionFactor));

        // Apply obstacle correction for force mode
        if (obstacleMode.equals("force")) {
            obstaclesDodged = (int)Math.round(obstaclesDodged * correctionFactor);
            numberOfObstaclesCollided = (int)Math.round(numberOfObstaclesCollided * correctionFactor);
            Gdx.app.log("correction", String.valueOf(correctionFactor));
            Gdx.app.log("period", String.valueOf(period));

        }

        // Add game over modal
        gameOverModal = buildGameOverModal();
        uiStack.add(gameOverModal.getTable());
        gameOverModal.setVisible(true);

        // Clear loica velocity
        loicaVelocity.x = 0;
        starVelocity.x = 0;

        // Update obstaclesDodged
        boolean perfectScore = (numberOfObstaclesCollided == 0);
        thisGameScore = com.hachimanzur.loica.util.Gamification.Gamification.getThisGameScore(heightFactorsOfDodged, perfectScore);
        if (!isPractice) {
            int oldLevel = com.hachimanzur.loica.util.Gamification.Gamification.getCurrentLevel();
            totalScore = com.hachimanzur.loica.util.Gamification.Gamification.updateScore(thisGameScore);
            int newLevel = com.hachimanzur.loica.util.Gamification.Gamification.getCurrentLevel();
            com.hachimanzur.loica.util.Gamification.Achievements.UserAchievements.instance.checkForAchievements(uiStack, oldLevel, newLevel, emgoneSkin);
            UserAchievements.instance.sendAchievementsToServer();
        }
    }

    private void resetWorld() {
        starOffset2 = stage.backgroundMoving.getRegionWidth();
        gameTime = totalTime;
        loicaPosition.set(com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_WIDTH*0.06f, com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_HEIGHT/2);
        starPosition.set(0, 0);
        loicaVelocity.set(0, 0);
        starVelocity.set(0, 0);
        camera.position.x = 400;
        boolean isUp = getObsPosition();
        int heightFactor = rnd.nextInt(heightMaximumFactor);
        if (obstacleMode.equals("force")) {
            forceFirstObs = camera.position.x + com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_WIDTH/2;
            forceLastObs = -1;
            heightFactor = heightMaximumFactor - 1;
        }
        TextureRegion random_obs = isUp ? stage.getRandomCeilingObs(heightFactor) : stage.getRandomGroundObs(heightFactor);
        obstacles.offer(
                new com.hachimanzur.loica.util.Obstacle(
                        camera.position.x + com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_WIDTH/2,
                        com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_HEIGHT-random_obs.getRegionHeight(),
                        random_obs,
                        heightFactor + 1,
                        isUp)
        );
    }

    private void updateWorld() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Update game state
        updateGameState(deltaTime);
        calculateMovingAverage();

        // Just for animation purposes
        loicaStateTime += deltaTime;

        if (gameState == GameState.Running) {
            loicaPosition.y = MathUtils.clamp(movingAverage*strengthThreshold, 0, com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_HEIGHT - com.hachimanzur.loica.util.Constants.GROUND_HEIGHT);
            loicaVelocity.set(horizontalVelocity, 0);
            starVelocity.set(horizontalVelocity * 0.2f, 0);
        }

        loicaPosition.mulAdd(loicaVelocity, deltaTime);
        starPosition.mulAdd(starVelocity,deltaTime);
        camera.position.x = loicaPosition.x + 350;

        if(camera.position.x - groundOffsetX > stage.ground.getRegionWidth() + 400) {
            groundOffsetX += stage.ground.getRegionWidth();
        }

        if(camera.position.x - mountainsOffsetX > stage.backgroundLandscape.getRegionWidth() + 400) {
            mountainsOffsetX += stage.backgroundLandscape.getRegionWidth();
        }

        if ( starOffset1 + stage.backgroundMoving.getRegionWidth() - (camera.position.x-starPosition.x-398) < 0 )
        {
            starOffset1 += (2 * stage.backgroundMoving.getRegionWidth());
        }
        if ( starOffset2 + stage.backgroundMoving.getRegionWidth() - (camera.position.x-starPosition.x-398) < 0 )
        {
            starOffset2 += (2 * stage.backgroundMoving.getRegionWidth());
        }

        // Since the stage is moving, we need to take care of on-screens obstacles
        addObstacleToGameIfRequired();
        removeObstacleFromGameIfRequired();
        // Moves loica's collision rectangle
        updateLoicaRectangle();
        // Collision code for obstacles
        checkCollisions();
    }

    private void calculateMovingAverage() {
        movingAverage = map(recorder.getMovingAverage(), com.hachimanzur.loica.util.GamePreferences.instance.minCalibrationHeight, com.hachimanzur.loica.util.GamePreferences.instance.maxCalibrationHeight, 1, com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_HEIGHT);
    }

    private void updateGameState(float deltaTime) {
        if(gameState == GameState.Running) {
            gameTime -= deltaTime;
            infoBtn.setText(dodgedObs + obstaclesDodged + time + String.format ("%.0f", gameTime));
            if (gameTime < 0) {
                gameTime = 0;
                endGame();
            }
        }
    }
    private void updateLoicaRectangle() {
        loicaRectangle.set(loicaPosition.x + 20, loicaPosition.y, loica.getKeyFrames()[0].getRegionWidth() - 20, loica.getKeyFrames()[0].getRegionHeight());
    }

    private void trackForceObstacles(float distanceForce) {
        // hasta ahora sólo hay descanso mínimo seteado
        float distanceClear = 600;
        if ((distanceForce * forceRest) > distanceClear) {
            distanceClear = (distanceForce * forceRest);
        }
        // averiguar si hay algún descanso máximo, just in case
        if (forceFirstObs != -1 && camera.position.x + com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_WIDTH/2 - forceFirstObs >= distanceForce) {
            forceFirstObs = -1;
            forceLastObs = camera.position.x + com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_WIDTH/2;
        } else if (forceLastObs != -1 && camera.position.x + com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_WIDTH/2 - forceLastObs >= distanceClear) {
            forceFirstObs = camera.position.x + com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_WIDTH/2;
            forceLastObs = -1;
        }
    }

    private void addObstacleToGameIfRequired() {
        com.hachimanzur.loica.util.Obstacle lastObstacle = obstacles.peekLast();
        int randomHeightFactor = rnd.nextInt(heightMaximumFactor);
        float distanceBetweenObstacles = nearnessFactor;
        /*if (isPractice) {
            distanceBetweenObstacles =
                    nearnessFactor *
                            Constants.DISTANCE_BETWEEN_OBSTACLES_SLOPE +
                            Constants.DISTANCE_BETWEEN_OBSTACLES_OFFSET;
        }
        else {
            distanceBetweenObstacles = nearnessFactor;
        }*/
        if (obstacleMode.equals("force")) {
            // la distancia de fuerza se setea en base a lo que le digan en la configuración
            // usar sin adulterar
            trackForceObstacles(distanceBetweenObstacles);
            if (forceLastObs > 0) return;
            TextureRegion randomObstacle = stage.getRandomGroundObs(heightMaximumFactor - 1);

            if(lastObstacle.position.x < camera.position.x + com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_WIDTH/2 - randomObstacle.getRegionWidth()) {
                obstacles.offer(new com.hachimanzur.loica.util.Obstacle(
                        camera.position.x + com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_WIDTH/2,
                        com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_HEIGHT - randomObstacle.getRegionHeight(),
                        randomObstacle,
                        heightMaximumFactor,
                        false));
            }
        } else {
            // si es alguno de los modos de coordinación, sumar un random gaussiano al distancebetweenobstacles
            if (!obstacleMode.equals("speed")) {
                double addRandom = rnd.nextGaussian() * com.hachimanzur.loica.util.Constants.DISTANCE_BETWEEN_OBSTACLES_STDEV;
                if (addRandom < com.hachimanzur.loica.util.Constants.DISTANCE_BETWEEN_OBSTACLES_STDEV && addRandom > -com.hachimanzur.loica.util.Constants.DISTANCE_BETWEEN_OBSTACLES_STDEV) {
                    distanceBetweenObstacles += addRandom;
                }
            }
            if(lastObstacle.position.x < camera.position.x + com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_WIDTH/2 - distanceBetweenObstacles) {
                Gdx.app.log("distance", Float.toString(distanceBetweenObstacles));
                boolean isUp;
                if (alternateObstacles) {
                    isUp = !lastObstacle.isUp;
                }
                else {
                    isUp = getObsPosition();
                }
                TextureRegion randomCeilingObstacle = stage.getRandomCeilingObs(randomHeightFactor);

                obstacles.offer(new com.hachimanzur.loica.util.Obstacle(
                        camera.position.x + com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_WIDTH/2,
                        com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_HEIGHT - randomCeilingObstacle.getRegionHeight(),
                        isUp ? randomCeilingObstacle : stage.getRandomGroundObs(randomHeightFactor),
                        randomHeightFactor + 1,
                        isUp));
            }
        }
    }

    private void removeObstacleFromGameIfRequired() {
        com.hachimanzur.loica.util.Obstacle firstObstacle = obstacles.peek();
        if (obstacles.size() > 1 && camera.position.x - firstObstacle.position.x > 400 + firstObstacle.image.getRegionWidth()) {
            obstacles.poll();
        }
    }

    private void checkCollisions() {
        for(com.hachimanzur.loica.util.Obstacle obs : obstacles) {
            obstacleRectangle.set(obs.position.x + (obs.image.getRegionWidth() - 30) / 2 + 20, obs.position.y, 20, obs.getRealHeight() - 10);
            if(loicaRectangle.overlaps(obstacleRectangle)) {
                if(!obs.overlaping){
                    if(gameState != GameState.GameOver){
                        obstaclesCollidedForce++;
                        obs.overlaping = true;
                        loicaCrashing = true;
                    }
                }
            }
            else{
                obs.overlaping = false;
            }
            if(obs.position.x < loicaPosition.x && !obs.counted) {
                if(!obs.overlaping) {
                    obstaclesDodgedForce++;
                    heightFactorsOfDodged.add(obs.getHeightFactor());
                }
                obs.counted = true;
                loicaCrashing = false;

            }
        }
        obstaclesDodged = (int)Math.round(obstaclesDodgedForce * correctionFactor);
        numberOfObstaclesCollided = (int)Math.round(obstaclesCollidedForce * correctionFactor);
    }

    private void drawWorld() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(stage.background, camera.position.x - stage.background.getRegionWidth() / 2, 0);

        batch.draw(stage.backgroundMoving, starPosition.x+starOffset1, com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_HEIGHT - stage.backgroundMoving.getRegionHeight());

        batch.draw(stage.backgroundMoving2, starPosition.x+starOffset2, com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_HEIGHT - stage.backgroundMoving2.getRegionHeight());

        batch.draw(stage.backgroundLandscape, mountainsOffsetX, 0);
        batch.draw(stage.backgroundLandscape, mountainsOffsetX-1 + stage.backgroundLandscape.getRegionWidth(), 0);

        batch.draw(stage.ground, groundOffsetX, 0);
        //el -2 s para que no haya un pequeño espacio entre las repeticiones del fondo, es traslape
        batch.draw(stage.ground, groundOffsetX-2 + stage.ground.getRegionWidth(), 0);
        batch.draw(ceiling, groundOffsetX, com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_HEIGHT - ceiling.getRegionHeight());
        batch.draw(ceiling, groundOffsetX-2 + ceiling.getRegionWidth(), com.hachimanzur.loica.util.Constants.GAME_VIEWPORT_HEIGHT - ceiling.getRegionHeight());

        // Draw the obstacles
        for(Obstacle obs: obstacles) {
            obs.draw(batch);
        }

        if(loicaCrashing) {
            batch.draw(loica_crash.getKeyFrame(loicaStateTime), loicaPosition.x, loicaPosition.y);
        }
        else {
            batch.draw(loica.getKeyFrame(loicaStateTime), loicaPosition.x, loicaPosition.y);
        }
        batch.end();

        if(gameState == GameState.Start && startGame) {
            gameState = GameState.Running;
            initialLayer.setVisible(false);
            playLayer.setVisible(true);
        }

    }

    private boolean getObsPosition() {
        if (showBottomObstacles) {

            // show obstacles on both sides
            if (showTopObstacles) {
                obstacles_position = "both";
                return MathUtils.randomBoolean();
            }

                // show only bottom obstacles
            else {
                obstacles_position = "only_bottom";
                return false;
            }
        }
        // show only top obstacles
        else {
            obstacles_position = "only_top";
            return true;
        }

    }

    private void saveScore(int score) {
        prefs = com.hachimanzur.loica.util.GamePreferences.instance;
        prefs.score += score;
        prefs.save();
    }

    private void postData() {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);

        HttpRequest request = new HttpRequest(HttpMethods.POST);
        request.setUrl(com.hachimanzur.loica.util.Constants.POST_GAME_DATA_URL);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Token token=" + UserData.getToken());
        request.setTimeOut(com.hachimanzur.loica.util.Constants.TIMEOUT);

        float restMultiplier = 0;
        if (isPractice) {
            restMultiplier = com.hachimanzur.loica.util.Constants.getRestMultiplier(com.hachimanzur.loica.util.GamePreferences.instance.forceRest);
        } else {
            restMultiplier = com.hachimanzur.loica.util.Gamification.Gamification.getRestForLevel(Gamification.getCurrentLevel());
        }

        String content = "{" +
            "\"exercise\": {" +
                "\"dodged\": " + obstaclesDodged + ", " +
                "\"collisions\": " + numberOfObstaclesCollided + ", " +
                "\"obstacles\": \"" + obstacles_position + "\", " +
                "\"game_time\": " + com.hachimanzur.loica.util.GamePreferences.instance.gameTime + " , " +
                "\"speed\": " + com.hachimanzur.loica.util.GamePreferences.instance.loicaXVelocity + " , " +
                "\"obstacle_distance\": " + com.hachimanzur.loica.util.GamePreferences.instance.distanceBetweenObstaclesFactor + ", " +
                "\"obstacles_size\": " + com.hachimanzur.loica.util.GamePreferences.instance.obstacleMaxHeightFactor + ", " +
                "\"min_calibration_amp\": " + com.hachimanzur.loica.util.GamePreferences.instance.minCalibrationHeight + ", " +
                "\"max_calibration_amp\": " + com.hachimanzur.loica.util.GamePreferences.instance.maxCalibrationHeight + ", " +
                "\"kind\": \"" + gameType(isPractice) + "\", " +
                "\"body_part\": \"" + com.hachimanzur.loica.util.GamePreferences.instance.bodyPart + "\", " +
                "\"points\": " + thisGameScore + ", " +
                "\"score\": " + totalScore + " ," +
                "\"exercise_type\": " + String.valueOf(getObstacleModeEnum()) +"," +
                "\"gravity\": " + 1/ com.hachimanzur.loica.util.GamePreferences.instance.strengthThreshold +"," +
                "\"obstacle_size\": " + com.hachimanzur.loica.util.GamePreferences.instance.obstacleMaxHeightFactor +"," +
                "\"obstacle_periodicity\":" + ((getObstacleModeEnum() != 2) ? com.hachimanzur.loica.util.GamePreferences.instance.periodFactor : "0") +"," +
                "\"obstacle_duration\": " + ((getObstacleModeEnum() == 2) ? com.hachimanzur.loica.util.GamePreferences.instance.forceDuration : "0") +"," +
                "\"obstacle_rest\": " + ((getObstacleModeEnum() == 2) ? restMultiplier : "0") +
            "}" +
        "}";

        System.out.println(content);
        request.setContent(content);

        Net.HttpResponseListener listener = new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                System.out.println("Status code " + statusCode);
                System.out.println("Result: " + httpResponse.getResultAsString());
//                if (statusCode != 201) {
//                    refuse(statusCode);
//                }
//                else {
//                    isAuthenticated = true;
//                }
            }
            @Override
            public void failed(Throwable t) {
//                String failedToConnect = t.getLocalizedMessage();
//                if (failedToConnect.contains("failed to connect") ||
//                        failedToConnect.contains("timed out") ||
//                        failedToConnect.contains("refused") ||
//                        failedToConnect.contains("unreachable"))
//                if (t.getLocalizedMessage().contains("failed to connect"))
//                    refuse(503);
                System.out.println("Failed " + t.getMessage());
            }

            @Override
            public void cancelled() {
                System.out.println("Cancelled");
            }
        };

        Gdx.net.sendHttpRequest(request, listener);
    }

    private float map(float x, float a, float b, float c, float d) {
        float res = ((d - c)*(x-a)/(b-a)) + c;
        return res;
    }

    //FIXME: Change this to a real enumeration.
    private int getObstacleModeEnum() {
        String obstacleMode = Constants.OBSTACLE_MODES.get(GamePreferences.instance.obstacleMode);
        Gdx.app.log("obstacle_mode", obstacleMode);

        if      (obstacleMode.equals("speed"))      { return 1; }
        else if (obstacleMode.equals("force"))      { return 2; }
        else if (obstacleMode.equals("coord"))      { return 3; }
        else if (obstacleMode.equals("coordUp"))    { return 4; }
        else if (obstacleMode.equals("coordDn"))    { return 5; }
        return 0;
    }

    private String gameType(boolean isPractice) {
        if (isPractice) return "practice";
        return "game";
    }
}