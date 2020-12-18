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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hachimanzur.loica.util.Constants;
import com.hachimanzur.loica.main.MainGame;

public class PreLoginScreen implements Screen {

    private String btnFBImg = "login/facebook.png";
    private String btnGoogleImg = "login/google.png";
    private Stage stage;

    private Skin emgoneSkin;
    private Skin emgoneImages;

    Drawable btnFBrawable;
    Drawable btnGoogleDrawable;
    TextButton btnLogin;
    ImageButton btnFB;
    ImageButton btnGoogle;

    public com.hachimanzur.loica.main.MainGame game;
    public PreLoginScreen(MainGame game){
        this.game = game;
    }

    @Override
    public void render(float deltaTime) {
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
    }

    @Override
    public void dispose() {
        emgoneSkin.dispose();
        emgoneImages.dispose();
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

        emgoneImages = new Skin(new TextureAtlas(com.hachimanzur.loica.util.Constants.EMGONE_IMAGES_ATLAS));

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
        Image imgBackground = new Image(emgoneImages, "initial-bg");
        layer.add(imgBackground).width(com.hachimanzur.loica.util.Constants.VIEWPORT_WIDTH).height(Constants.VIEWPORT_HEIGHT);
        return layer;
    }

    private Table buildControlsLayer() {
        Table layer = new Table();

        // Login logo
        Image btnLogo = new Image(emgoneImages, "logologin");
        layer.addActor(btnLogo);
        btnLogo.setPosition(stage.getWidth()/2 - btnLogo.getWidth()/2, stage.getHeight()*0.6f);

        // Login button
        btnLogin = new TextButton("INICIAR SESIÓN CON USUARIO", emgoneSkin);
        layer.addActor(btnLogin);
        btnLogin.setWidth(stage.getWidth()*0.7f);
        btnLogin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LoginScreen(game));
            }
        });
        btnLogin.setPosition(stage.getWidth()/2 - btnLogin.getWidth()/2, stage.getHeight()*0.45f);
//
//        // Facebook button
//        btnFBrawable = new TextureRegionDrawable(new TextureRegion(new Texture(btnFBImg)));
//        btnFB = new ImageButton(btnFBrawable);
//        //layer.addActor(btnFB);
//        btnFB.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                //game.setScreen(new CalibrateScreen(game));
//                System.out.println("LOGIN FACEBOOK");
//            }
//        });
//        btnFB.setPosition(stage.getWidth()/2 - btnFB.getWidth()/2, 190);
//
//        // Google button
//        btnGoogleDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(btnGoogleImg)));
//        btnGoogle = new ImageButton(btnGoogleDrawable);
//        //layer.addActor(btnGoogle);
//        btnGoogle.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                //game.setScreen(new CalibrateScreen(game));
//                System.out.println("LOGIN GOOGLE");
//            }
//        });
//        btnGoogle.setPosition(stage.getWidth()/2 - btnGoogle.getWidth()/2, 100);

        return layer;



    }
}

