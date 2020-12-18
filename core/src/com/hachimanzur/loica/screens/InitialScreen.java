package com.hachimanzur.loica.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hachimanzur.loica.util.Constants;
import com.hachimanzur.loica.main.MainGame;


public class InitialScreen implements Screen {

    private Stage stage;

    private Skin emgoneSkin;
    private Skin emgoneImages;

    private ImageButton btnPlay;
    private ImageButton btnPractice;

    private TextButton btnGoToProfile;

    public com.hachimanzur.loica.main.MainGame game;

    public InitialScreen(MainGame game){
        this.game = game;
    }

    @Override
    public void render(float deltaTime) {
        //System.out.println(Gdx.graphics.getFramesPerSecond());
        // 1)Clear the screen
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(deltaTime);
        stage.draw();
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
        emgoneSkin.dispose();
        emgoneImages.dispose();
    }

    @Override
    public void dispose() {
    }

    public void show() {
        stage = new Stage(new FitViewport(com.hachimanzur.loica.util.Constants.VIEWPORT_WIDTH, com.hachimanzur.loica.util.Constants.VIEWPORT_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(false);
        rebuildStage();
    }

    private void rebuildStage() {
        emgoneSkin = new Skin(
                Gdx.files.internal(com.hachimanzur.loica.util.Constants.EMGONE_SKIN),
                new TextureAtlas(com.hachimanzur.loica.util.Constants.EMGONE_ATLAS)
        );

        emgoneImages = new Skin(new TextureAtlas(com.hachimanzur.loica.util.Constants.EMGONE_IMAGES_ATLAS_2));

        Table layerBackground = buildBackgroundLayer();
        Table layerControlButtons = buildControlsLayer();

        // assemble stage for menu screen
        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(com.hachimanzur.loica.util.Constants.VIEWPORT_WIDTH, com.hachimanzur.loica.util.Constants.VIEWPORT_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerControlButtons);
    }

    private Table buildBackgroundLayer() {
        Table layer = new Table();
        Image imgBackground = new Image(emgoneImages, "orange-bg");
        Image imgBackgroundBottom = new Image(emgoneImages, "orange-bg-mountains");
        layer.add(imgBackground).width(com.hachimanzur.loica.util.Constants.VIEWPORT_WIDTH).height(Constants.VIEWPORT_HEIGHT);
        layer.addActor(imgBackgroundBottom);
        imgBackgroundBottom.setSize(stage.getWidth(), stage.getHeight()/6);
        return layer;
    }

    private Table buildControlsLayer() {
        Table layer = new Table();
        layer.top().pad(50, 20, 200, 20);

//        layer.setDebug(true);

        // Go to profile button
        btnGoToProfile = new TextButton("IR A PERFIL", emgoneSkin, "btn-goto");
        layer.add(btnGoToProfile).expandX().right();
        layer.row();
        btnGoToProfile.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ProfileScreen(game));
            }
        });

        // Play button
        btnPlay = new ImageButton(emgoneSkin);
        layer.add(btnPlay).expandY().padTop(80).row();
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SummaryScreen(game, false));
            }
        });


        // Practice button
        btnPractice = new ImageButton(emgoneSkin, "practice-btn");
        layer.add(btnPractice).expandY();
        layer.row();
        btnPractice.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SummaryScreen(game, true));
            }
        });

        return layer;

    }
}

