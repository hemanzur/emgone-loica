package com.hachimanzur.loica.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hachimanzur.loica.util.Constants;
import com.hachimanzur.loica.main.MainGame;
import com.hachimanzur.loica.util.UserData;

public class SplashScreen implements Screen {

    private Stage stage;

    private Skin emgoneImages;

    public com.hachimanzur.loica.main.MainGame game;
    private float elapsed = 0;
    public SplashScreen(MainGame game){


        this.game = game;
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(deltaTime);
        stage.draw();
        elapsed += deltaTime;
        if(elapsed>=2){
            if (UserData.isLoggedIn()) {
                game.setScreen(new InitialScreen(game));
            }

            else{
                game.setScreen(new PreLoginScreen(game));
            }

        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        stage.dispose();
    }

    @Override
    public void dispose() {
        emgoneImages.dispose();

    }
    public void show() {
        stage = new Stage(new FitViewport(com.hachimanzur.loica.util.Constants.VIEWPORT_WIDTH, com.hachimanzur.loica.util.Constants.VIEWPORT_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        rebuildStage();
    }

    private void rebuildStage() {
        emgoneImages = new Skin(new TextureAtlas(com.hachimanzur.loica.util.Constants.EMGONE_IMAGES_ATLAS));

        Table layerBackground = buildBackgroundLayer();

        // assemble stage for menu screen
        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(com.hachimanzur.loica.util.Constants.VIEWPORT_WIDTH, com.hachimanzur.loica.util.Constants.VIEWPORT_HEIGHT);
        stack.add(layerBackground);
    }

    private Table buildBackgroundLayer() {
        Table layer = new Table();
        Image imgBackground = new Image(emgoneImages, "initial-bg");
        layer.add(imgBackground).width(com.hachimanzur.loica.util.Constants.VIEWPORT_WIDTH).height(com.hachimanzur.loica.util.Constants.VIEWPORT_HEIGHT);
        Image logoSplash = new Image(emgoneImages, "logoSplash");
        layer.addActor(logoSplash);
        logoSplash.setSize(350, 350);
        logoSplash.setPosition(com.hachimanzur.loica.util.Constants.VIEWPORT_WIDTH/2 - logoSplash.getWidth()/2, Constants.VIEWPORT_HEIGHT*0.55f);
        return layer;
    }
}

