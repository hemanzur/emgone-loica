package com.hachimanzur.loica.util.Gamification;

import com.hachimanzur.loica.util.Constants;
import com.hachimanzur.loica.util.GamePreferences;
import com.hachimanzur.loica.util.Gamification.Exceptions.ZeroLevelException;

import java.util.ArrayList;


/**
 * This class will handle every method linked with the gamification aspect of the Application,
 * like calculating experience, level, and dinamically setting difficulty values, like velocity, obstacle height
 * and obstacle nearness
 * Created by nursoft on 04-04-17.
 */

public final class Gamification {
    private static GamePreferences prefs = GamePreferences.instance;
    private Gamification () {
        System.out.println("Gamification class initiated");
    }

    public static int getCurrentLevel() {
        int score = prefs.score;
        return getCurrentLevel(score);
    }

    public static int getCurrentScore() {
        return prefs.score;
    }

    public static int getCurrentLevel(int score) {
        double level = Math.log(1 + (score/650.0)) / Math.log(1.04);
        if (level <= 1) return 1;
        if (level >= 50) return 50;
        return (int)level;
    }

    // Handles the updating of the score when the game finishes
    public static int getThisGameScore(ArrayList<Integer> listOfHeightFactors, boolean perfectGame) {
        int tempScore = 0;
        for (Integer factor: listOfHeightFactors) {
            tempScore += factor;
        }
        if (perfectGame) tempScore *= 3;
        return tempScore;
    }

    public static int updateScore(int score) {
        prefs.score += score;
        prefs.save();
        return prefs.score;
    }

    public static double experienceForLevel(int level) {
        return 650 * Math.pow(1.04, level) - 1;
    }

    public static float velocityForLevel(int level) {
        return (float)(0.13 * Math.log(level) + 0.5);
    }

    public static float obstacleNearnessForLevel(int level) throws ZeroLevelException {
        if (level == 0) { throw new ZeroLevelException("Level can't be zero"); }
        return (float)Math.pow(level, -0.3)*1400;
    }

    private static int obstacleHeightFactorForLevel(int level) throws ZeroLevelException {
        if (level == 0) throw new ZeroLevelException("Level can't be zero.");
        if (level == 1) return 1;
        int height = (int) Math.ceil(Math.log(level));
        if (height > 7) return 7;
        return height;
    }

    public static int getHeightFactor(int level) {
        int heightFactor;
        try {
           heightFactor = obstacleHeightFactorForLevel(level);
        } catch (ZeroLevelException ex) {
            heightFactor = 1;
        }
        return heightFactor;
    }

    public static float getNearness(int level) {
        float nearnessFactor;
        try {
            nearnessFactor = obstacleNearnessForLevel(level);
        } catch (ZeroLevelException ex) {
            return Constants.DISTANCE_BETWEEN_OBSTACLES_OFFSET;
        }
        return nearnessFactor;
    }

    public static float getForceDistance(int level) {
        double period = 76.431f * Math.log(level) + 1;
        return Constants.calculateForceDistance((float)period);
    }

    public static float getRestForLevel(int level) {
        return (float)(Math.log(level) * -0.383f) + 2;
    }

}
