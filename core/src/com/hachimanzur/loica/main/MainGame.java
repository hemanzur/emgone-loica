package com.hachimanzur.loica.main;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hachimanzur.loica.screens.SplashScreen;

public class MainGame extends Game {
    public SpriteBatch batch;
    public static int width, height;
    public boolean isCalibrated = false;

    @Override
    public void create () {
        batch = new SpriteBatch();
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        this.setScreen(new SplashScreen(this));
        // this.setScreen(new CalibrateScreen(this));
    }

    @Override
    public void render () {
        super.render();
    }
    public void dispose(){
        batch.dispose();
    }
}
