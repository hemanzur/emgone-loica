package com.nursoft.emgone.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nursoft.emgone.main.MainGame;
import com.nursoft.emgone.util.Constants;

import java.util.regex.Pattern;

public class RegisterScreen implements Screen {

    private String backgroundImg = "login/bg720.png";
    private Stage stage;

    // Register data goodies
    private TextField nameField;
    private TextField rutField;
    private TextField phoneField;
    private TextField emailField;
    private TextField addressField;
    private TextField passField;
    private TextField confirmPassField;
    private Pattern pattern;
    private boolean isAuthenticated;
    // TO DO: usar skins + json (cap 7 libro de libgdx)
    private Image imgBackground;
    private Table layerControlButtons;
    private Table layerSuccess;

    private Label registerLbl;
    private Label errorLbl;

    private Cell mainLblCell;
    private Cell btnCell;

    private TextButton btnRegister;
    private Label feedBackMsg;

    public MainGame game;
    private Skin emgoneSkin;
    private Skin emgoneImages;

    private int supposedKeyboardHeight;
    private boolean isKeyboardVisible;
    private FocusListener focusListener;

    public RegisterScreen(MainGame game){
        this.game = game;
        pattern = Pattern.compile(Constants.EMAIL_PATTERN);
        supposedKeyboardHeight = 500;
        isKeyboardVisible = false;
        focusListener = new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (focused && !isKeyboardVisible) {
                    stage.getCamera().translate(0, -supposedKeyboardHeight, 0);
                    isKeyboardVisible = true;
                }
            }
        };

    }

    class User {
        String name;
        String rut;
        String phone;
        String email;
        String address;
        String password;

        User(String n, String r, String ph, String e, String a, String p) {
            name = n;
            rut = r;
            phone = ph;
            email = e;
            address = a;
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
    public void render(float deltaTime) {
        // 1)Clear the screen
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(deltaTime);
        stage.draw();

        if (isAuthenticated) {
            layerControlButtons.setVisible(false);
            layerSuccess.setVisible(true);
        }

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
        layerControlButtons = buildControlsLayer();
        layerSuccess = buildSuccessLayer();

        // assemble stage for menu screen
        //stage.setDebugAll(true);
        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerControlButtons);
        stack.add(layerSuccess);
        layerSuccess.setVisible(false);
        stage.getRoot().addCaptureListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (!(event.getTarget() instanceof TextField)) {
                    stage.setKeyboardFocus(null);
                    isKeyboardVisible = false;
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    stage.getCamera().position.y = Constants.VIEWPORT_HEIGHT/2;
                }
                return false;
            }
        });
    }

    private Table buildBackgroundLayer() {
        Table layer = new Table();
        imgBackground = new Image(emgoneImages, "initial-bg");
        layer.add(imgBackground).width(Constants.VIEWPORT_WIDTH).height(Constants.VIEWPORT_HEIGHT);
        return layer;
    }


    private Table buildControlsLayer() {
        Table layer = new Table();

        //Recover label
        registerLbl = new Label("Regístrate", emgoneSkin);
        layer.add(registerLbl).padBottom(40).row();
        mainLblCell = layer.getCell(registerLbl);
        registerLbl.setAlignment(Align.center);

        // Input nombre
        nameField = new TextField("",emgoneSkin);
        nameField.setMessageText("Nombre *");
        layer.add(nameField).padBottom(40).width(Constants.VIEWPORT_WIDTH*0.7f).row();
        nameField.setAlignment(Align.center);

        // Input rut
/*
        rutField = new TextField("", emgoneSkin);
        rutField.setMessageText("R.U.T. (e.j. 11111111-1) *");
        layer.add(rutField).padBottom(40).width(Constants.VIEWPORT_WIDTH*0.7f).row();
        rutField.setAlignment(Align.center);
*/

        // Input telefono
        phoneField = new TextField("", emgoneSkin);
        phoneField.setMessageText("Teléfono");
        layer.add(phoneField).padBottom(40).width(Constants.VIEWPORT_WIDTH*0.7f).row();
        phoneField.setAlignment(Align.center);

        // Input email
        emailField = new TextField("", emgoneSkin);
        emailField.setMessageText("Email *");
        layer.add(emailField).padBottom(40).width(Constants.VIEWPORT_WIDTH*0.7f).row();
        emailField.setAlignment(Align.center);

        // Input direccion
        addressField = new TextField("", emgoneSkin);
        addressField.setMessageText("Dirección");

        layer.add(addressField).padBottom(40).width(Constants.VIEWPORT_WIDTH*0.7f).row();
        addressField.setAlignment(Align.center);
        addressField.addListener(focusListener);

        // Input contraseña
        passField = new TextField("", emgoneSkin);
        passField.setMessageText("Contraseña *");
        layer.add(passField).padBottom(40).width(Constants.VIEWPORT_WIDTH*0.7f).row();
        passField.setAlignment(Align.center);
        passField.setPasswordMode(true);
        passField.setPasswordCharacter('*');
        passField.addListener(focusListener);

        // Confirm password
        confirmPassField = new TextField("", emgoneSkin);
        confirmPassField.setMessageText("Confirme contraseña");
        layer.add(confirmPassField).padBottom(20).width(Constants.VIEWPORT_WIDTH*0.7f).row();
        confirmPassField.setAlignment(Align.center);
        confirmPassField.setPasswordMode(true);
        confirmPassField.setPasswordCharacter('*');
        confirmPassField.addListener(focusListener);

        Label lblMandatory = new Label("* Campos obligatorios", emgoneSkin, "constraints");
        layer.add(lblMandatory).padBottom(20).row();


        // Register button
        btnRegister = new TextButton("REGÍSTRATE", emgoneSkin);
        layer.add(btnRegister).height(btnRegister.getHeight()).row();
        btnRegister.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TO DO pls refactor dis
                String name = nameField.getText();
                String rut = rutField.getText();
                String phone = phoneField.getText();
                String email = emailField.getText();
                String address = addressField.getText();
                String password = passField.getText();
                String confirmPass = confirmPassField.getText();

                if (areCredentialsValid(name, rut, email, password, confirmPass)) {
                    postCredentials(name, rut, phone, email, address, password);
                }
            }
        });

        btnCell = layer.getCell(btnRegister);

        feedBackMsg = new Label("Enviando solicitud...", emgoneSkin);
        feedBackMsg.setAlignment(Align.center);

        errorLbl = new Label("", emgoneSkin, "error");
        errorLbl.setAlignment(Align.center);

        return layer;

    }

    private boolean areCredentialsValid(String name, String rut, String email, String password, String confirmPass) {
        if ("".equals(name) ||
                "".equals(rut) ||
                "".equals(email) ||
                "".equals(password) ||
                "".equals(confirmPass)
                ) {
            refuse(Constants.EMPTY_FIELDS);
            return false;
        }

        else if (!validRut(rut)) {
            refuse(Constants.INVALID_RUT);
            return false;
        }

        else if (!pattern.matcher(email).matches()) {
            refuse(Constants.INVALID_EMAIL_FORMAT);
            return false;
        }

        else if (password.length() < 8) {
            refuse(Constants.PASSWORD_TOO_SHORT);
            return false;
        }

        else if (!password.equals(confirmPass)) {
            refuse(Constants.PASSWORDS_DONT_MATCH);
            return false;
        }

        //verificar otras cosas

        else return true;
    }

    private void postCredentials(String name, String rut, String phone, String email, String address, String password) {
        // TODO: Connect post credentials to new app endpoint
        return;
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);

        User user = new User(name, rut, phone, email, address, password);
        Data data = new Data(user);

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(Constants.REGISTER_URL);
        request.setContent(json.prettyPrint(data));
        request.setHeader("Content-Type", "application/json");
        request.setTimeOut(Constants.TIMEOUT);

        Net.HttpResponseListener listener = new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                System.out.println("Status code " + statusCode);
                System.out.println("Result: " + httpResponse.getResultAsString());
                if (statusCode != 201) {
                    refuse(statusCode);
                }
                else {
                    isAuthenticated = true;
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
        btnRegister.setVisible(false);
        mainLblCell.setActor(registerLbl);
        btnCell.setActor(feedBackMsg);
    }

    private void refuse(int statusCode){
        // form validation
        if (statusCode < 0) {
            if (statusCode == Constants.EMPTY_FIELDS) {
                errorLbl.setText(Constants.EMPTY_FIELD_MSG);
            }

            else if (statusCode == Constants.INVALID_RUT) {
                errorLbl.setText(Constants.INVALID_RUT_MSG);
            }

            else if (statusCode == Constants.INVALID_EMAIL_FORMAT) {
                errorLbl.setText(Constants.INVALID_EMAIL_FORMAT_MSG);
            }

            else if (statusCode == Constants.PASSWORD_TOO_SHORT) {
                errorLbl.setText(Constants.PASSWORD_TOO_SHORT_MSG);
            }

            else if (statusCode == Constants.PASSWORDS_DONT_MATCH) {
                errorLbl.setText(Constants.PASSWORD_TOO_SHORT_MSG);
            }

            else {
                errorLbl.setText(Constants.FORM_WITH_ERRORS_MSG);
            }

        }

        // response status code management
        else if (statusCode == 400) {
            errorLbl.setText("La dirección de correo ya está tomada");
        }

        else if (statusCode==503) {
            errorLbl.setText(Constants.CONNECTION_ERROR_MSG);
        }
        else {
            errorLbl.setText("Otro error ocurrió: " + statusCode);
        }

        passField.setText("");
        confirmPassField.setText("");
        mainLblCell.setActor(errorLbl);
        btnCell.setActor(btnRegister);
        btnRegister.setVisible(true);
    }

    private boolean validRut(String rut) {
        boolean valid = false;
        try {
            rut =  rut.toUpperCase();
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

            char dv = rut.charAt(rut.length() - 1);

            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                valid = true;
            }

        } catch (java.lang.NumberFormatException e) {
        } catch (Exception e) {
        }
        return valid;
    }

    private Table buildSuccessLayer() {
        layerSuccess = new Table();
        //layerSuccess.setDebug(true);
        layerSuccess.pad(Constants.VIEWPORT_HEIGHT*0.4f, Constants.VIEWPORT_HEIGHT*0.15f, Constants.VIEWPORT_HEIGHT*0.4f, Constants.VIEWPORT_HEIGHT*0.15f);
        Label successLbl = new Label("¡Registro exitoso!", emgoneSkin);
        layerSuccess.add(successLbl).expand().top().row();

        TextButton btnBack = new TextButton("VOLVER", emgoneSkin, "btn-backto");
        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LoginScreen(game));
            }
        });
        layerSuccess.add(btnBack);


        return layerSuccess;

    }
}

