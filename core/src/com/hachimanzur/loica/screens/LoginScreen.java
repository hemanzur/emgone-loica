package com.hachimanzur.loica.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hachimanzur.loica.util.Constants;
import com.hachimanzur.loica.main.MainGame;
import com.hachimanzur.loica.util.UserData;

import java.util.regex.Pattern;


public class LoginScreen implements Screen {

    private Stage stage;

    // authentication goodies
    private TextField emailField;
    private TextField passwordField;
    private Label loginLabel;
    private boolean isAuthenticated;
    private Pattern pattern;

    TextButton btnLogin;
    TextButton btnRegister;
    TextButton btnRecover;

    public com.hachimanzur.loica.main.MainGame game;
    private Skin emgoneSkin;
    private Skin emgoneImages;

    public LoginScreen(MainGame g){
        this.game = g;
        pattern = Pattern.compile(com.hachimanzur.loica.util.Constants.EMAIL_PATTERN);
    }

    class User {
        private String email;
        private String password;

        User(String e, String p) {
            email = e;
            password = p;
        }
    }

    class Data {
        private User user;

        Data(User u){
            user = u;
        }
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(com.hachimanzur.loica.util.Constants.VIEWPORT_WIDTH, com.hachimanzur.loica.util.Constants.VIEWPORT_HEIGHT));
        isAuthenticated = false;
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

        if (isAuthenticated) {
            game.setScreen(new InitialScreen(game));
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(new PreLoginScreen(game));
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
        emgoneSkin.dispose();
        emgoneImages.dispose();

    }
    @Override
    public void dispose() {
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

        // hide keyboard when tap over other place
        stage.getRoot().addCaptureListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (!(event.getTarget() instanceof TextField)) {
                    stage.setKeyboardFocus(null);
                    Gdx.input.setOnscreenKeyboardVisible(false);
                }
                return false;
            }
        });
    }

    private Table buildBackgroundLayer() {
        Table layer = new Table();
        Image imgBackground = new Image(emgoneImages, "initial-bg");
        layer.add(imgBackground).width(com.hachimanzur.loica.util.Constants.VIEWPORT_WIDTH).height(com.hachimanzur.loica.util.Constants.VIEWPORT_HEIGHT);

        Image btnLogo = new Image(emgoneImages, "logologin");

        layer.addActor(btnLogo);
        btnLogo.setPosition(stage.getWidth()/2 - btnLogo.getWidth()/2, stage.getHeight()*0.6f);

        return layer;
    }

    private Table buildControlsLayer() {
        Table layer = new Table();

        // Label login
        loginLabel = new Label("Iniciar sesión con usuario", emgoneSkin);
        loginLabel.setAlignment(Align.center);
        layer.add(loginLabel).padBottom(50).row();
        loginLabel.setPosition(stage.getWidth()/2 - loginLabel.getWidth()/2, 480);

        // Input email
        emailField = new TextField("", emgoneSkin);
        emailField.setMessageText("Email");
        layer.add(emailField).padBottom(15).width(stage.getWidth()*0.7f).row();
        emailField.setAlignment(Align.center);
        emailField.setPosition(stage.getWidth()/2 - emailField.getWidth()/2, 380);

        // Input contraseña
        passwordField = new TextField("", emgoneSkin);
        passwordField.setMessageText("Contraseña");
        layer.add(passwordField).padBottom(50).width(stage.getWidth()*0.7f).row();
        passwordField.setAlignment(Align.center);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        passwordField.setPosition(stage.getWidth()/2 - passwordField
                .getWidth()/2, 300);

        // Login button
        btnLogin = new TextButton("INICIAR SESIÓN", emgoneSkin);
        layer.add(btnLogin).row();
        btnLogin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String email = emailField.getText();
                String password = passwordField.getText();
                if (areCredentialsValid(email, password)) {
                    postCredentials(email, password);
                }
            }
        });

        // Sign up
        btnRegister = new TextButton("Regístrate\n", emgoneSkin, "no-image-btn");
        // won't be used for now
        //layer.addActor(btnRegister);
        btnRegister.setPosition(stage.getWidth()*0.26f- btnRegister.getWidth()/2, stage.getHeight()*0.2f);

        btnRegister.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new RegisterScreen(game));
            }
        });

        // Recover password
        btnRecover = new TextButton("Recuperar contraseña", emgoneSkin, "no-image-btn");
        layer.addActor(btnRecover);
        btnRecover.setPosition(stage.getWidth()*0.5f- btnRecover.getWidth()/2 + 10, stage.getHeight()*0.2f);

        btnRecover.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new RecoverScreen(game));
            }
        });
        return layer;

    }

    private boolean areCredentialsValid(String email, String password) {
        if ("".equals(email) || "".equals(password)) {
            refuse(com.hachimanzur.loica.util.Constants.EMPTY_FIELDS);
            return false;
        }
        else if (!pattern.matcher(email).matches()) {
            refuse(com.hachimanzur.loica.util.Constants.INVALID_EMAIL_FORMAT);
            return false;
        }
        else return true;
    }

    private void postCredentials(String email, String password) {
        Json json = new Json();
        json.setOutputType(OutputType.json);

        User user = new User(email, password);
        Data data = new Data(user);

        HttpRequest request = new HttpRequest(HttpMethods.POST);
        request.setUrl(com.hachimanzur.loica.util.Constants.LOGIN_URL);
        request.setContent(json.prettyPrint(data));
        request.setHeader("Content-Type", "application/json");
        request.setTimeOut(com.hachimanzur.loica.util.Constants.TIMEOUT);

        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                System.out.println("Status code " + statusCode);
                if (statusCode != 200) {
                    refuse(statusCode);
                }
                else {
                    isAuthenticated = true;
                    String response = httpResponse.getResultAsString();
                    System.out.println("Login response");
                    System.out.println(response);
                    Json json = new Json();
                    com.hachimanzur.loica.util.UserData ud = json.fromJson(UserData.class, response);
                    ud.save();
                    System.out.println(response);
                }
            }
            @Override
            public void failed(Throwable t) {
                refuse(503);
                System.out.println("Failed " + t.getMessage());
            }

            @Override
            public void cancelled() {
                System.out.println("Cancelled");
            }
        };

        Gdx.net.sendHttpRequest(request, listener);
        loginLabel.setText("Conectando...");
        loginLabel.getStyle().fontColor = Color.WHITE;
        btnLogin.setVisible(false);
        btnRecover.setVisible(false);
        passwordField.setVisible(false);
        emailField.setVisible(false);
    }

    private void refuse(int statusCode){
        loginLabel.getStyle().fontColor = Color.CORAL;
        if (statusCode < 0) {
            if (statusCode == com.hachimanzur.loica.util.Constants.EMPTY_FIELDS) {
                loginLabel.setText(com.hachimanzur.loica.util.Constants.EMPTY_FIELD_MSG);
            }

            else if (statusCode == com.hachimanzur.loica.util.Constants.INVALID_EMAIL_FORMAT) {
                loginLabel.setText(com.hachimanzur.loica.util.Constants.INVALID_EMAIL_FORMAT_MSG);
            }

            else {
                loginLabel.setText(com.hachimanzur.loica.util.Constants.FORM_WITH_ERRORS_MSG);
            }
        }
        else if (statusCode == 401) {
            loginLabel.setText(com.hachimanzur.loica.util.Constants.INVALID_USER_PASS_MSG);
        }
        else if (statusCode == 503) {
            loginLabel.setText(Constants.CONNECTION_ERROR_MSG);
        }
        else {
            loginLabel.setText("Otro error ocurrió: " + statusCode);
        }

        passwordField.setText("");
        btnLogin.setVisible(true);
        btnRecover.setVisible(true);
        emailField.setVisible(true);
        passwordField.setVisible(true);
    }
}

