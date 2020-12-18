package com.hachimanzur.loica.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;


public class GamePreferences {
    public static final String TAG = GamePreferences.class.getName();

    public static final GamePreferences instance = new GamePreferences();
    public int obstacleMaxHeightFactor;
    public float distanceBetweenObstaclesFactor;
    public float loicaYPosition;
    public float loicaXVelocity;
    public int gameTime;
    public float maxCalibrationHeight;
    public float minCalibrationHeight;

    public int score;
    public int level;

    public String bodyPart;
    public float strengthThreshold;

    public boolean showBottomObstacles;
    public boolean showTopObstacles;
    public boolean alternateObstacles;

    public String obstacleMode;

    public boolean isNight;

    public int costume;
    public int scenario;

    public int periodFactor;
    public int forceRest;
    public int forceDuration;

    private Preferences prefs;

    private GamePreferences () {
        prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
    }

    public void load () {

        gameTime = prefs.getInteger("gameTime", Constants.MIN_EXERCISE_TIME);

        score = prefs.getInteger("score", 0);
        obstacleMaxHeightFactor = prefs.getInteger("obstacleMaxHeightFactor", 3);
        distanceBetweenObstaclesFactor = prefs.getFloat("distanceBetweenObstaclesFactor", 150.0f);
        loicaXVelocity = prefs.getFloat("loicaXVelocity", 0.5f);

        maxCalibrationHeight = prefs.getFloat("maxCalibrationHeight",5000f);
        minCalibrationHeight = prefs.getFloat("minCalibrationHeight",800f);

        showBottomObstacles = prefs.getBoolean("showBottomObstacles", true);
        showTopObstacles = prefs.getBoolean("showTopObstacles", true);
        alternateObstacles = prefs.getBoolean("alternateObstacles", false);


        bodyPart = prefs.getString("bodyPart");
        strengthThreshold = prefs.getFloat("strengthThreshold", 1.0f);
        level = prefs.getInteger("playerLevel", 1);

        costume = prefs.getInteger("costume", Constants.NORMAL_LOICA);
        scenario = prefs.getInteger("scenario", Constants.DESERT);

        obstacleMode = prefs.getString("obstacleMode", Constants.OBSTACLE_MODES_NAMES.first());

        isNight = prefs.getBoolean("isNight", true);

        periodFactor = prefs.getInteger("periodFactor", 3);
        forceRest = prefs.getInteger("forceRest", 3);
        forceDuration = prefs.getInteger("forceDuration", Constants.MIN_OBSTACLE_TIME);

    }

    public void save () {
        prefs.putInteger("gameTime", gameTime);
        prefs.putInteger("score", score);
        prefs.putInteger("obstacleMaxHeightFactor", obstacleMaxHeightFactor);
        prefs.putFloat("distanceBetweenObstaclesFactor", distanceBetweenObstaclesFactor);
        prefs.putFloat("loicaYPosition", loicaYPosition);
        prefs.putFloat("loicaXVelocity", loicaXVelocity );
        prefs.putFloat("maxCalibrationHeight", maxCalibrationHeight);
        prefs.putFloat("minCalibrationHeight", minCalibrationHeight);
        prefs.putBoolean("showBottomObstacles", showBottomObstacles);
        prefs.putBoolean("showTopObstacles", showTopObstacles);
        prefs.putBoolean("alternateObstacles", alternateObstacles);
        prefs.putString("bodyPart", bodyPart);
        prefs.putInteger("playerLevel", level);
        prefs.putFloat("strengthThreshold", strengthThreshold);
        prefs.putInteger("costume", costume);
        prefs.putInteger("scenario", scenario);
        prefs.putString("obstacleMode", obstacleMode);
        prefs.putBoolean("isNight", isNight);
        prefs.putInteger("periodFactor", periodFactor);
        prefs.putInteger("forceRest", forceRest);
        prefs.putInteger("forceDuration", forceDuration);
        prefs.flush();
    }

    public void resetPreferences() {
        prefs.putInteger("gameTime", Constants.MIN_EXERCISE_TIME);
        prefs.putInteger("score", 0);
        prefs.putInteger("obstacleMaxHeightFactor", 4);
        prefs.putFloat("distanceBetweenObstaclesFactor", 150.0f);
        prefs.putFloat("loicaXVelocity", 0.5f);
        prefs.putFloat("maxCalibrationHeight", 5000f);
        prefs.putFloat("minCalibrationHeight", 800f);
        prefs.putBoolean("showBottomObstacles", true);
        prefs.putBoolean("showTopObstacles", true);
        prefs.putBoolean("alternateObstacles", false);
        prefs.putString("bodyPart", "");
        prefs.putInteger("playerLevel", 1);
        prefs.putFloat("strengthThreshold", 0.5f);
        prefs.putInteger("costume", Constants.NORMAL_LOICA);
        prefs.putInteger("scenario", Constants.DESERT);
        prefs.putString("obstacleMode", Constants.OBSTACLE_MODES_NAMES.first());
        prefs.putBoolean("isNight", false);
        prefs.putInteger("periodFactor", 3);
        prefs.putInteger("forceRest", 3);
        prefs.putInteger("forceDuration", Constants.MIN_OBSTACLE_TIME);
        prefs.flush();
    }

}
