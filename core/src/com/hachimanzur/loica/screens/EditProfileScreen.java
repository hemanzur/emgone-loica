package com.hachimanzur.loica.screens;

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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hachimanzur.loica.main.MainGame;
import com.hachimanzur.loica.util.Constants;
import com.hachimanzur.loica.util.GamePreferences;
import com.hachimanzur.loica.util.Gamification.Gamification;
import com.hachimanzur.loica.util.UserData;

import java.util.regex.Pattern;

public class EditProfileScreen implements Screen {

    private Stage stage;

    private Image imgBackground;

    TextButton btnToMenu;
    TextButton btnSave;

    private GamePreferences prefs = GamePreferences.instance;
    private int currentScore;
    private int currentLevel;

    Window window;
    float textFieldWidth = Constants.VIEWPORT_WIDTH*0.7f;

    private Label lblNivel;
    private Label lblScore;
    public MainGame game;
    private Skin emgoneSkin;
    private Skin emgoneImages;

    // Personal data goodies
    private boolean isAuthenticated;
    private Pattern pattern;
    private UserData userData;
    private UserData newUserData;
    private TextField nameField;
    private TextField rutField;
    private TextField phoneField;
    private TextField addressField;
    private TextField emailField;
    private TextField passField;
    private TextField confirmPassField;
    private Label lblError;

    private int supposedKeyboardHeight;
    private boolean isKeyboardVisible;
    private FocusListener focusListener;

    public EditProfileScreen(MainGame game) {
        this.game = game;
        pattern = Pattern.compile(Constants.EMAIL_PATTERN);
        userData = new UserData();
        userData.load();
        supposedKeyboardHeight = 400;
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

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(deltaTime);
        stage.draw();

        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK) || isAuthenticated) {
                game.setScreen(new com.hachimanzur.loica.screens.ProfileScreen(game));
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
        loadSettings();
        rebuildStage();
    }

    private void rebuildStage() {
        emgoneSkin = new Skin(
                Gdx.files.internal(Constants.EMGONE_SKIN),
                new TextureAtlas(Constants.EMGONE_ATLAS)
        );

        emgoneImages = new Skin(new TextureAtlas(Constants.EMGONE_IMAGES_ATLAS_2));

        Table layerBackground = buildBackgroundLayer();
        Table layerControlButtons = buildControlsLayer();
        //stage.setDebugAll(true);

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
                    isKeyboardVisible = false;
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    stage.getCamera().position.y = Constants.VIEWPORT_HEIGHT/2;
                }
                return false;
            }
        });
    }

    private void loadSettings() {
        prefs = GamePreferences.instance;
        prefs.load();
        currentScore = prefs.score;
        currentLevel = Gamification.getCurrentLevel(currentScore);
    }

    private Table buildBackgroundLayer() {
        Table layer = new Table();
        imgBackground = new Image(emgoneImages, "purple-bg");
        layer.add(imgBackground).width(Constants.VIEWPORT_WIDTH).height(Constants.VIEWPORT_HEIGHT);

        return layer;
    }

    private Table buildControlsLayer() {
        Table layer = new Table();
        layer.pad(50, 20, 20, 20);

        lblNivel = new Label(currentLevel+"", emgoneSkin, "level");
        lblNivel.setAlignment(Align.center);


        lblScore = new Label(currentScore+" ", emgoneSkin, "max-score");
        lblScore.setAlignment(Align.center);

        //Back to menu and edit button
        btnToMenu = new TextButton("IR A MENÚ", emgoneSkin, "btn-backto");

        layer.add(btnToMenu).colspan(3).left().row();
        btnToMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ProfileScreen(game));
            }
        });


        // edit profile picture
        Image profilePicture = new Image(emgoneImages, "avatar");

        layer.add(lblNivel).expandX().width(130);
        layer.add(profilePicture).expandX().fill().width(profilePicture.getWidth()).pad(20);
        layer.add(lblScore).expandX().width(130).row();
//
//        ImageButton btnEdit = new ImageButton(emgoneSkin, "edit-picture");
//        layer.addActor(btnEdit);
//        btnEdit.setPosition(Constants.VIEWPORT_WIDTH/2-btnEdit.getWidth()/2, Constants.VIEWPORT_HEIGHT-220);

        nameField = new TextField(userData.name, emgoneSkin);
        layer.add(nameField).width(textFieldWidth).colspan(3).bottom().row();
        nameField.setAlignment(Align.center);

        layer.add(buildProfileInfoWindow()).colspan(3).expand().row();

        lblError = new Label("", emgoneSkin, "error");
        lblError.setAlignment(Align.center);
        layer.add(lblError).colspan(3);
        lblError.setVisible(false);

        return layer;
    }

    private Table buildProfileInfoWindow() {
        window = new Window("", emgoneSkin);
        window.setMovable(false);
        window.pad(50, 80, 50, 80);
        //window.debug();

        // Input rut
        rutField = new TextField(userData.rut, emgoneSkin);
        rutField.setMessageText("R.U.T");
        window.add(rutField).width(textFieldWidth).padBottom(25).row();
        rutField.setAlignment(Align.center);

        // Input phone
        phoneField = new TextField(userData.phone, emgoneSkin);
        phoneField.setMessageText("Teléfono");
        window.add(phoneField).width(textFieldWidth).padBottom(25).row();
        phoneField.setAlignment(Align.center);

        // Input email
        emailField = new TextField(userData.email, emgoneSkin);
        emailField.setMessageText("Email");
        window.add(emailField).width(textFieldWidth).padBottom(25).row();
        emailField.setAlignment(Align.center);
        emailField.addListener(focusListener);

        // Input address
        addressField = new TextField(userData.address, emgoneSkin);
        addressField.setMessageText("Dirección");
        window.add(addressField).width(textFieldWidth).padBottom(25).row();
        addressField.setAlignment(Align.center);
        addressField.addListener(focusListener);

        // Input contraseña
        passField = new TextField("", emgoneSkin);
        passField.setMessageText("Nueva contraseña");
        window.add(passField).width(textFieldWidth).padBottom(35).row();
        passField.setAlignment(Align.center);
        passField.setPasswordMode(true);
        passField.setPasswordCharacter('*');
        passField.addListener(focusListener);

        confirmPassField = new TextField("", emgoneSkin);
        confirmPassField.setMessageText("Confirme nueva contraseña");
        window.add(confirmPassField).width(textFieldWidth).padBottom(35).row();
        confirmPassField.setAlignment(Align.center);
        confirmPassField.setPasswordMode(true);
        confirmPassField.setPasswordCharacter('*');
        confirmPassField.addListener(focusListener);

        // Save button
        btnSave = new TextButton("GUARDAR", emgoneSkin, "save");
        window.add(btnSave).width(Constants.VIEWPORT_WIDTH*0.25f).row();
        btnSave.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String email = emailField.getText();
                String rut = rutField.getText();
                String newPassword = passField.getText();
                String confirmPassword = confirmPassField.getText();
                if (areCredentialsValid(email, rut, newPassword, confirmPassword)) {
                    postCredentials(nameField.getText(), rutField.getText(), phoneField.getText(), email, addressField.getText(), newPassword);
                }
            }
        });

        window.pack();
        return window;
    }

    private boolean areCredentialsValid(String email, String rut, String newPassword, String confirmNewPass) {
        if ("".equals(email)) {
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

        else if (newPassword.length() > 0) {
            if (newPassword.length() < 8) {
                refuse(Constants.PASSWORD_TOO_SHORT);
                return false;
            }

            else if (!newPassword.equals(confirmNewPass)) {
                refuse(Constants.PASSWORDS_DONT_MATCH);
                return false;
            }

            else return true;
        }

        //verificar otras cosas

        else return true;
    }

    class Data {
        UserData user;

        Data(UserData u) {
            user = u;
        }
    }

    private void postCredentials(String name, String rut, String phone, String email, String address, String password) {

        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);

        newUserData = new UserData(name, rut, phone, email, address, password, UserData.getToken());
        Data data = new Data(newUserData);

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.PUT);
        request.setUrl(Constants.EDIT_PROFILE_URL);
        request.setContent(json.prettyPrint(data));
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Token token=" + UserData.getToken());
        request.setTimeOut(Constants.TIMEOUT);

        Net.HttpResponseListener listener = new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                System.out.println("Status code " + httpResponse.getStatus().getStatusCode());
                System.out.println("Result: " + httpResponse.getResultAsString());
                if ( statusCode != 200) {
                    refuse(statusCode);
                }
                else {
                    isAuthenticated = true;
                    newUserData.save();
                }
            }
            @Override
            public void failed(Throwable t) {
                refuse(503);
                System.out.println("Failed " + t.getMessage());            }

            @Override
            public void cancelled() {
                System.out.println("Cancelled");
            }
        };

        Gdx.net.sendHttpRequest(request, listener);
        lblError.setText("Enviando solicitud...");
        lblError.setVisible(true);
        btnSave.setVisible(false);
    }
    private void refuse(int statusCode){

        // form validation
        if (statusCode < 0) {
            if (statusCode == Constants.EMPTY_FIELDS) {
                lblError.setText(Constants.EMPTY_FIELD_MSG);
            }

            else if (statusCode == Constants.INVALID_RUT) {
                lblError.setText(Constants.INVALID_RUT_MSG);
            }

            else if (statusCode == Constants.INVALID_EMAIL_FORMAT) {
                lblError.setText(Constants.INVALID_EMAIL_FORMAT_MSG);
            }

            else if (statusCode == Constants.PASSWORD_TOO_SHORT) {
                lblError.setText(Constants.PASSWORD_TOO_SHORT_MSG);
            }

            else if (statusCode == Constants.PASSWORDS_DONT_MATCH) {
                lblError.setText(Constants.PASSWORDS_DONT_MATCH_MSG);
            }

            else {
                lblError.setText(Constants.FORM_WITH_ERRORS_MSG);
            }

        }

        // response status code managment
        else if (statusCode == 400) {
            lblError.setText("La dirección de correo ya está tomada");
        }

        else if (statusCode == 503) {
            lblError.setText(Constants.CONNECTION_ERROR_MSG);
        }
        else {
            lblError.setText("Otro error ocurrió: " + statusCode);
        }

        passField.setText("");
        lblError.setVisible(true);
        btnSave.setVisible(true);
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
}

