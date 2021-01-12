package com.nursoft.emgone.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nursoft.emgone.main.MainGame;
import com.nursoft.emgone.util.Constants;

import java.util.regex.Pattern;


public class RecoverScreen implements Screen {

    private String backgroundImg = "login/bg720.png";
    private Stage stage;

    // TO DO: usar skins + json (cap 7 libro de libgdx)
    private Image imgBackground;
    private Image imgLogo;

    private Pattern pattern;



    private Label lblRecover;
    private Label lblError;
    private TextField emailField;
    private Label lblFeedback;
    private TextButton btnRecover;
    private TextButton btnBack;

    private Cell cellLabel;
    private Cell cellEmailField;
    private Cell cellBtn;

    public MainGame game;

    private Skin emgoneSkin;
    private Skin emgoneImages;

    public RecoverScreen(MainGame game){
        this.game = game;
        pattern = Pattern.compile(Constants.EMAIL_PATTERN);
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        stage.act(deltaTime);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(new LoginScreen(game));
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
    public void show() {
        stage = new Stage(new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);
        rebuildStage();
    }

    private void rebuildStage() {
        emgoneSkin = new Skin(
                Gdx.files.internal(Constants.EMGONE_SKIN),
                new TextureAtlas(Constants.EMGONE_ATLAS)
        );

        emgoneImages = new Skin(new TextureAtlas(Constants.EMGONE_IMAGES_ATLAS));

        Table layerBackground = buildBackgroundLayer();
        Table layerControlButtons = buildControlsLayer();

        // assemble stage for menu screen
        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerControlButtons);

        stage.getRoot().addCaptureListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (!(event.getTarget() instanceof TextField)) {
                    stage.setKeyboardFocus(null);
                    Gdx.input.setOnscreenKeyboardVisible(false);
                }
                return false;
            }
        });
        //stage.setDebugAll(true);
    }

    private Table buildBackgroundLayer() {
        Table layer = new Table();
        imgBackground = new Image(emgoneImages, "initial-bg");
        layer.add(imgBackground).width(Constants.VIEWPORT_WIDTH).height(Constants.VIEWPORT_HEIGHT);

        imgLogo = new Image(emgoneImages, "logologin");
        layer.addActor(imgLogo);
        imgLogo.setPosition(Constants.VIEWPORT_WIDTH/2 - imgLogo.getWidth()/2, Constants.VIEWPORT_HEIGHT*0.6f);
        return layer;
    }

    private Table buildControlsLayer() {
        Table layer = new Table();
        layer.padTop(50);

        // Recover Label
        lblRecover = new Label("Recuperar contraseña", emgoneSkin);
        layer.add(lblRecover).row();
        cellLabel = layer.getCell(lblRecover);

        lblError = new Label("", emgoneSkin, "error");
        lblError.setAlignment(Align.center);

        // Input mail
        emailField = new TextField("", emgoneSkin);
        emailField.setMessageText("Email");
        layer.add(emailField).width(Constants.VIEWPORT_WIDTH*0.7f).pad(40).row();
        emailField.setAlignment(Align.center);

        cellEmailField = layer.getCell(emailField);

        // Recover button
        btnRecover = new TextButton("RECUPERAR", emgoneSkin);
        layer.add(btnRecover).row();
        btnRecover.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String email = emailField.getText();
                if (isEmailValid(email)) {
                    sendRecoveryMail(email);
                }
            }
        });

        cellBtn = layer.getCell(btnRecover);

        // Message label
        lblFeedback = new Label("Se ha enviado un mensaje de\ncorreo electrónico a la dirección\nregistrada para tu cuenta", emgoneSkin);
        lblFeedback.setAlignment(Align.center);

        btnBack = new TextButton("VOLVER", emgoneSkin, "btn-backto");
        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LoginScreen(game));
            }
        });

        return layer;

    }

    private boolean isEmailValid(String email) {
        if ("".equals(email)) {
            refuse(Constants.EMPTY_FIELDS);
            return false;
        }
        else if (!pattern.matcher(email).matches()) {
            refuse(Constants.INVALID_EMAIL_FORMAT);
            return false;
        }
        else return true;
    }

    private void sendRecoveryMail(String email) {
        HttpRequest request = new HttpRequest(HttpMethods.POST);
        request.setUrl(Constants.RECOVERY_URL);
        request.setContent("{" +
                "\"user\": {" +
                "\"email\":" +
                "\"" + email + "\"" +
                "}}");
        request.setHeader("Content-Type", "application/json");

        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                System.out.println("Status code " + statusCode);
                System.out.println("Response: " + httpResponse.getResultAsString());
                if (statusCode != 201) {
                    refuse(statusCode);
                }
                else {
                    lblRecover.setText("Recuperar contraseña");
                    cellLabel.setActor(lblRecover);
                    cellEmailField.setActor(lblFeedback);
                    cellBtn.setActor(btnBack);
                }
            }

            @Override
            public void failed(Throwable t) {
                refuse(503);
                System.out.println("Failed: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                System.out.println("Request cancelled");
            }
        };

        Gdx.net.sendHttpRequest(request, listener);
        lblRecover.setText("Enviando solicitud...");
        cellLabel.setActor(lblRecover);
    }

    private void refuse(int statusCode) {
        if (statusCode == Constants.EMPTY_FIELDS) {
            lblError.setText(Constants.EMPTY_FIELD_MSG);
        }
        else if (statusCode == Constants.INVALID_EMAIL_FORMAT) {
            lblError.setText(Constants.INVALID_EMAIL_FORMAT_MSG);
        }

        else if (statusCode == 503) {
            lblError.setText(Constants.CONNECTION_ERROR_MSG);
        }

        else if (statusCode == 400) {
            lblError.setText(Constants.EMAIL_NOT_REGISTERED_MSG);
        }

        else {
            lblRecover.setText("Otro error ocurrió: " + statusCode);
        }

        cellLabel.setActor(lblError);
    }
}

