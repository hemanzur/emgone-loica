package com.nursoft.emgone.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nursoft.emgone.main.MainGame;
import com.nursoft.emgone.util.Constants;
import com.nursoft.emgone.util.GamePreferences;
import com.nursoft.emgone.util.Gamification.Gamification;
import com.nursoft.emgone.util.UserData;


public class AchievementsScreen implements Screen {

    private Stage stage;


    TextButton btnToMenu;
    ImageButton btnEdit;
    ImageButton btnPreviousPage;
    ImageButton btnNextPage;

    private UserData userData;

    private GamePreferences prefs = GamePreferences.instance;
    private int currentScore;
    private int currentLevel;

    private Window window;

    private Label lblNivel;
    private Label lblScore;
    public MainGame game;
    private Skin emgoneSkin;
    private Skin emgoneImages;
    private Skin emgoneImages2;

    private TextButton btnBack;


    private JsonValue achievementsJson;
    private int achievementsCount;
    private int doneCount;
    private int totalPages;
    private int currentPage;
    private boolean isDataLoaded;
    private Table dataTable;
    private Cell dataTableCell;
    private Label lblError;
    private Table windowTitle;


    public AchievementsScreen(MainGame game){
        this.game = game;
        userData = new UserData();
        userData.load();
        currentPage = 1;

        isDataLoaded = false;

        emgoneSkin = new Skin(
                Gdx.files.internal(Constants.EMGONE_SKIN),
                new TextureAtlas(Constants.EMGONE_ATLAS)
        );

        emgoneImages = new Skin(new TextureAtlas(Constants.EMGONE_IMAGES_ATLAS));
        emgoneImages2 = new Skin(new TextureAtlas(Constants.EMGONE_IMAGES_ATLAS_2));

    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(deltaTime);
        stage.draw();

        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(new ProfileScreen(game));
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
        emgoneImages2.dispose();
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
        getAchievementsFromPage(currentPage);
    }

    private void rebuildStage() {

        Table layerBackground = buildBackgroundLayer();
        Table layerControlButtons = buildControlsLayer();
        //stage.setDebugAll(true);

        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerControlButtons);
    }

    private void loadSettings() {
        prefs = GamePreferences.instance;
        prefs.load();
        currentScore = prefs.score;
        currentLevel = Gamification.getCurrentLevel(currentScore);
    }

    private Table buildBackgroundLayer() {
        Table layer = new Table();
        Image imgBackground = new Image(emgoneImages2, "purple-bg");
        layer.add(imgBackground).width(Constants.VIEWPORT_WIDTH).height(Constants.VIEWPORT_HEIGHT);

        return layer;
    }

    private Table buildControlsLayer() {
        Table layer = new Table();
        layer.pad(50, 20, 100, 20).top();

        lblNivel = new Label(currentLevel+"", emgoneSkin, "level");
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
        Image profilePicture = new Image(emgoneImages2, "avatar");

        layer.add(lblNivel).expandX().width(130);
        layer.add(profilePicture).expandX().fill().width(profilePicture.getWidth()).height(profilePicture.getHeight()).pad(20);
        layer.add(lblScore).expandX().width(130).row();

        Label nameField = new Label(userData.name.toUpperCase(), emgoneSkin, "big-title");
        layer.add(nameField).colspan(3).height(Constants.VIEWPORT_HEIGHT*0.1f).row();
        nameField.getStyle().fontColor = Color.WHITE;
        nameField.setAlignment(Align.center);

        layer.add(buildAchievementsList()).colspan(3).top().width(Constants.VIEWPORT_WIDTH*0.95f).row();

        return layer;
    }

    private Table buildAchievementsList() {
        window = new Window("", emgoneSkin);
//        window.debugAll();
        window.setMovable(false);

        window.pad(20, 30, 40, 30);
        dataTable = new Table();
        window.add(dataTable).colspan(2).expandX().fillX().row();
        dataTableCell = window.getCell(dataTable);

        lblError = new Label("", emgoneSkin, "error");
        lblError.setAlignment(Align.center);

        btnPreviousPage = new ImageButton(emgoneSkin, "previous-page");
        btnNextPage = new ImageButton(emgoneSkin, "next-page");

        window.add(btnPreviousPage).expandY().left().pad(40, 40, 10, 10);
        btnPreviousPage.setVisible(false);
        btnPreviousPage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentPage > 1) {
                    currentPage -= 1;
                    isDataLoaded = false;
                    getAchievementsFromPage(currentPage);
                }
            }
        });

        window.add(btnNextPage).right().pad(40, 10, 10, 40).row();
        btnNextPage.setVisible(false);
        btnNextPage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentPage < totalPages) {
                    currentPage += 1;
                    isDataLoaded = false;
                    getAchievementsFromPage(currentPage);
                }
            }
        });

        btnBack = new TextButton("VOLVER", emgoneSkin, "back-history-button");
        window.add(btnBack).width(Constants.VIEWPORT_WIDTH*0.25f).colspan(2).pad(10).row();
        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ProfileScreen(game));
            }
        });

        btnBack.setVisible(false);

        window.pack();
        return window;
    }

    private Table buildWindowTitle(int unlockedAchievements, int totalAchievements) {
        Table table = new Table();
//        table.debug();
        table.add(new Image(emgoneImages, "cup")).expandX().right();
        table.add(new Label("LISTA DE LOGROS (" + unlockedAchievements + "/" + totalAchievements + ")", emgoneSkin, "window-title")).pad(15, 10, 15, 10);
        table.add(new Image(emgoneImages, "cup")).expand().left();
        return table;
    }

    private void getAchievementsFromPage(int page) {
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setUrl(Constants.ACHIEVEMENTS_PAGE_URL+page);
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
                storeAchievementsAndTotalPages(response);
                dataTableCell.setActor(buildDataTable(true));
                isDataLoaded = true;
                btnBack.setVisible(true);
            }
            @Override
            public void failed(Throwable t) {
                System.out.println("Failed " + t.getMessage());
//                dataTableCell.setActor(buildDataTable(false));
            }

            @Override
            public void cancelled() {
                System.out.println("Cancelled");
            }
        };

        Gdx.net.sendHttpRequest(request, listener);
        dataTableCell.setActor(lblError);
        lblError.setText("Cargando datos...");
        btnBack.setVisible(false);
    }

    private void storeAchievementsAndTotalPages(String result) {
        JsonValue json = new JsonReader().parse(result);
        achievementsJson = json.get("achievements");
        totalPages = json.getInt("total_pages");
        achievementsCount = json.getInt("achievements_count");
        doneCount = json.getInt("done_count");

    }

    private Actor buildDataTable(boolean gotData) {

        if (gotData) {
            Table table = new Table();
//            table.debug();
            windowTitle = buildWindowTitle(doneCount, achievementsCount);
            table.add(windowTitle).expandX().colspan(2).padBottom(30).row();

            for (JsonValue achievement : achievementsJson.iterator()) {
                ImageButton cup = new ImageButton(emgoneSkin, "cup");
                boolean isUnlocked = achievement.getBoolean("done");
                cup.setDisabled(!isUnlocked);
                table.add(cup);
                table.add(new Label(achievement.getString("description"), emgoneSkin, isUnlocked? "achievement-unlocked" : "achievement-locked")).left().row();
                table.add(new SplitPane(null, null, true, emgoneSkin)).expandX().fillX().colspan(2).padTop(10).padBottom(10).row();
            }

            btnPreviousPage.setVisible(currentPage > 1);
            btnNextPage.setVisible(currentPage < totalPages);

            return table;
        }

        else {
            dataTableCell.setActor(lblError);
            lblError.setText("Error de conexión. Intente más tarde.");

            return lblError;
        }
    }
}

