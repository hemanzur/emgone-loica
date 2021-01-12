package com.nursoft.emgone.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nursoft.emgone.main.MainGame;
import com.nursoft.emgone.util.Constants;
import com.nursoft.emgone.util.GamePreferences;
import com.nursoft.emgone.util.Gamification.Gamification;
import com.nursoft.emgone.util.UserData;

public class ProfileScreen implements Screen {

    private Stage stage;

    private Image imgBackground;

    private TextButton btnToMenu;
    private ImageButton btnEdit;

    private UserData userData;


    private GamePreferences prefs = GamePreferences.instance;
    private int currentScore;
    private int currentLevel;

    private Window windowProfile;
    private Window windowResetConfirmation;
    private Window windowLogOutConfirmation;

    private Label lblNivel;
    private Label lblScore;
    public MainGame game;
    private Skin emgoneSkin;
    private Skin emgoneImages;

    public ProfileScreen(MainGame game){
        this.game = game;
        userData = new UserData();
        userData.load();
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(deltaTime);
        stage.draw();

        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(new InitialScreen(game));
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
//        layerControlButtons.debugAll();
        Table layerResetConfirmationWindow = buildResetConfirmationWindow();
        Table layerLogOutConfirmationWindow = buildLogOutConfirmationWindow();
        //stage.setDebugAll(true);

        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerControlButtons);
        stack.add(layerResetConfirmationWindow);
        stack.add(layerLogOutConfirmationWindow);
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
        layer.pad(50, 20, 100, 20).top();

        lblNivel = new Label("" + currentLevel, emgoneSkin, "level");
        lblNivel.setAlignment(Align.center);

        lblScore = new Label(currentScore+" ", emgoneSkin, "max-score");
        lblScore.setAlignment(Align.center);

        //Back to menu and edit button
        btnToMenu = new TextButton("IR A MENÚ", emgoneSkin, "btn-backto");
        btnEdit = new ImageButton(emgoneSkin, "edit");

        layer.add(btnToMenu).colspan(2).left();
        btnToMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new InitialScreen(game));
            }
        });
        layer.add(btnEdit).right().row();
        btnEdit.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new EditProfileScreen(game));
            }
        }));


        // edit profile picture
        Image profilePicture = new Image(emgoneImages, "avatar");

        layer.add(lblNivel).expandX().width(130);
        layer.add(profilePicture).expandX().fill().width(profilePicture.getWidth()).pad(20);
        layer.add(lblScore).expandX().width(130).row();

        Label nameField = new Label(userData.name.toUpperCase(), emgoneSkin, "big-title");
        layer.add(nameField).colspan(3).height(Constants.VIEWPORT_HEIGHT*0.1f).row();
        nameField.getStyle().fontColor = Color.WHITE;
        nameField.setAlignment(Align.center);

        layer.add(buildProfileInfoWindow()).colspan(3).top().height(Constants.VIEWPORT_HEIGHT*0.4f).row();

        TextButton btnSignOut = new TextButton("CERRAR SESIÓN", emgoneSkin, "btn-black");
        layer.add(btnSignOut).expand().bottom().colspan(3).row();

        btnSignOut.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                windowLogOutConfirmation.setVisible(true);
                windowProfile.setVisible(false);
                windowResetConfirmation.setVisible(false);

            }
        });

        TextButton btnResetPoints = new TextButton("RESTAURAR PUNTAJE", emgoneSkin, "btn-black");
        layer.add(btnResetPoints).expand().bottom().colspan(3);

        btnResetPoints.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                windowResetConfirmation.setVisible(true);
                windowLogOutConfirmation.setVisible(false);
                windowProfile.setVisible(false);
            }
        });

        return layer;
    }

    private Table buildProfileInfoWindow() {
        windowProfile = new Window("", emgoneSkin);
        windowProfile.setMovable(false);

        windowProfile.pad(50, 80, 50, 80);
//
//        TextButton btnTaskEx = new TextButton("TAREAS Y EJERCICIOS", emgoneSkin, "profile-btn");
//        btnTaskEx.getLabel().setAlignment(Align.left);
//        windowProfile.add(btnTaskEx).width(Constants.VIEWPORT_WIDTH*0.65f).padBottom(50).row();
//
//        TextButton btnReminder = new TextButton("PROGRAMAR RECORDATORIO", emgoneSkin, "profile-btn");
//        btnReminder.getLabel().setAlignment(Align.left);
//        windowProfile.add(btnReminder).width(Constants.VIEWPORT_WIDTH*0.65f).padBottom(50).row();

        TextButton btnExRecord= new TextButton("HISTORIAL DE EJERCICIOS", emgoneSkin, "profile-btn");
        btnExRecord.getLabel().setAlignment(Align.left);
        windowProfile.add(btnExRecord).width(Constants.VIEWPORT_WIDTH*0.65f).padBottom(50).row();
        btnExRecord.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new HistoryScreen(game));
            }
        });

        TextButton btnExStats = new TextButton("LISTA DE LOGROS", emgoneSkin, "achievement-btn");
        btnExStats.getLabel().setAlignment(Align.left);
//        windowProfile.add(btnExStats).width(Constants.VIEWPORT_WIDTH*0.65f).row();
        btnExStats.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new AchievementsScreen(game));
            }
        });

        windowProfile.pack();
        return windowProfile;
    }

    private Table buildResetConfirmationWindow() {
        Table table = new Table();
        windowResetConfirmation = new Window("", emgoneSkin);
        windowResetConfirmation.setMovable(false);

        windowProfile.pad(50, 80, 50, 80);

        windowResetConfirmation.add(new Label("¿Está seguro de reiniciar su progreso?", emgoneSkin, "calib-labels")).colspan(2).pad(20).row();

        TextButton btnCancel = new TextButton("CANCELAR", emgoneSkin, "save");
        btnCancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                windowProfile.setVisible(true);
                windowResetConfirmation.setVisible(false);
                windowLogOutConfirmation.setMovable(false);
            }
        });

        TextButton btnReset = new TextButton("REINICIAR", emgoneSkin, "save");
        btnReset.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                prefs.score = 0;
                prefs.level = 0;
                prefs.costume = Constants.NORMAL_LOICA;
                prefs.scenario = Constants.DESERT;
                prefs.save();
                resetAchievementsOnServer();
                game.setScreen(new InitialScreen(game));
            }
        });

        windowResetConfirmation.add(btnCancel).pad(20);
        windowResetConfirmation.add(btnReset).pad(20);
        windowResetConfirmation.setVisible(false);

        windowResetConfirmation.pack();

        table.add(windowResetConfirmation).width(windowProfile.getWidth());

        return table;
    }


    private void resetAchievementsOnServer() {
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.DELETE);
        request.setUrl(Constants.POST_ACHIEVEMENTS_URL);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Token token=" + UserData.getToken());
        request.setTimeOut(Constants.TIMEOUT);

        Net.HttpResponseListener listener = new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String response = httpResponse.getResultAsString();
                System.out.println("Status code: " + statusCode);
                System.out.println("Response : " + response);
            }
            @Override
            public void failed(Throwable t) {
                System.out.println("Failed " + t.getMessage());
            }

            @Override
            public void cancelled() {
                System.out.println("Cancelled");
            }
        };

        Gdx.net.sendHttpRequest(request, listener);
    }

    private Table buildLogOutConfirmationWindow() {
        Table table = new Table();
        windowLogOutConfirmation = new Window("", emgoneSkin);
        windowLogOutConfirmation.setMovable(false);

        windowProfile.pad(50, 80, 50, 80);

        windowLogOutConfirmation.add(new Label("¿Está seguro de cerrar sesión?", emgoneSkin, "calib-labels")).colspan(2).pad(20).row();

        TextButton btnCancel = new TextButton("CANCELAR", emgoneSkin, "save");
        btnCancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                windowProfile.setVisible(true);
                windowLogOutConfirmation.setVisible(false);
                windowResetConfirmation.setVisible(false);
            }
        });

        TextButton btnReset = new TextButton("CERRAR SESIÓN", emgoneSkin, "save");
        btnReset.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UserData.resetData();
                prefs.resetPreferences();
                game.setScreen(new PreLoginScreen(game));
            }
        });

        windowLogOutConfirmation.add(btnCancel).pad(20);
        windowLogOutConfirmation.add(btnReset).pad(20);
        windowLogOutConfirmation.setVisible(false);

        windowLogOutConfirmation.pack();

        table.add(windowLogOutConfirmation).width(windowProfile.getWidth());

        return table;
    }
}

