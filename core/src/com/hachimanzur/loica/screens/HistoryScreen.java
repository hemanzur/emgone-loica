package com.hachimanzur.loica.screens;

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
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hachimanzur.loica.main.MainGame;
import com.hachimanzur.loica.util.Constants;
import com.hachimanzur.loica.util.GamePreferences;
import com.hachimanzur.loica.util.Gamification.Gamification;
import com.hachimanzur.loica.util.UserData;


public class HistoryScreen implements Screen {

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

    private TextButton btnBack;


    private JsonValue exercisesJson;
    private int totalPages;
    private int currentPage;
    private boolean isDataLoaded;
    private Table dataTable;
    private Cell dataTableCell;
    private Label lblError;
    private int ROWS_PER_PAGE;


    public HistoryScreen(MainGame game){
        this.game = game;
        userData = new UserData();
        userData.load();
        currentPage = 1;

        isDataLoaded = false;
        ROWS_PER_PAGE = 10;
        emgoneSkin = new Skin(
                Gdx.files.internal(Constants.EMGONE_SKIN),
                new TextureAtlas(Constants.EMGONE_ATLAS)
        );

        emgoneImages = new Skin(new TextureAtlas(Constants.EMGONE_IMAGES_ATLAS_2));

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
        getExercises(currentPage);
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
        Image imgBackground = new Image(emgoneImages, "purple-bg");
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
        Image profilePicture = new Image(emgoneImages, "avatar");

        layer.add(lblNivel).expandX().width(130);
        layer.add(profilePicture).expandX().fill().width(profilePicture.getWidth()).height(profilePicture.getHeight()).pad(20);
        layer.add(lblScore).expandX().width(130).row();

        Label nameField = new Label(userData.name.toUpperCase(), emgoneSkin, "big-title");
        layer.add(nameField).colspan(3).height(Constants.VIEWPORT_HEIGHT*0.1f).row();
        nameField.getStyle().fontColor = Color.WHITE;
        nameField.setAlignment(Align.center);

        layer.add(buildInfoWindow()).colspan(3).top().width(Constants.VIEWPORT_WIDTH*0.95f).row();

        return layer;
    }

    private Table buildInfoWindow() {
        window = new Window("HISTORIAL DE EJERCICIOS", emgoneSkin);
        window.getTitleLabel().setAlignment(Align.center);
        window.setMovable(false);

        window.pad(80, 30, 40, 30);

        dataTable = new Table();
        window.add(dataTable).colspan(2).expandX().fillX().row();
        dataTableCell = window.getCell(dataTable);

        lblError = new Label("", emgoneSkin, "error");
        lblError.setAlignment(Align.center);

        btnPreviousPage = new ImageButton(emgoneSkin, "previous-page");
        btnNextPage = new ImageButton(emgoneSkin, "next-page");

        window.add(btnPreviousPage).expandY().left().pad(10, 40, 10, 10);
        btnPreviousPage.setVisible(false);
        btnPreviousPage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentPage > 1) {
                    currentPage -= 1;
                    isDataLoaded = false;
                    getExercises(currentPage);
                }
            }
        });

        window.add(btnNextPage).right().pad(10, 10, 10, 40).row();
        btnNextPage.setVisible(false);
        btnNextPage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentPage < totalPages) {
                    currentPage += 1;
                    System.out.println("Current page: " + currentPage);
                    isDataLoaded = false;
                    getExercises(currentPage);
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

    class GetExercisesGraphql {
        private String variables;
        private String query;
        private String operationName;

        GetExercisesGraphql(String id, int start, int limit) {
            operationName = "obtenerEj";
            query = "query obtenerEj(\n" +
                    "  $id:ID!\n" +
                    "  $start: Int\n" +
                    "  $limit: Int\n" +
                    ") {\n" +
                    "  ejercicios (\n" +
                    "    start: $start\n" +
                    "    limit: $limit\n" +
                    "    where : {\n" +
                    "      user: $id\n" +
                    "    }\n" +
                    "  ) {\n" +
                    "    tipo\n" +
                    "    esquivados\n" +
                    "    colisionados\n" +
                    "    createdAt\n" +
                    "  }\n" +
                    "}";
            variables = "{\n" +
                    "  \"id\": \""+ id +"\",\n" +
                    "  \"start\": "+ Integer.toString(start) +",\n" +
                    "  \"limit\": "+  Integer.toString(limit) +"\n" +
                    "}";;
        }
    }

    private void getExercises(int page) {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        String jwt = UserData.getToken();
        String id = UserData.getId();
        String bearer = "Bearer " + jwt;
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(Constants.GRAPHQL_URL);
        int start = ROWS_PER_PAGE * (page - 1);
        GetExercisesGraphql contentRequest = new GetExercisesGraphql(id, start, ROWS_PER_PAGE + 1);
        request.setContent(json.prettyPrint(contentRequest));
        System.out.println(json.prettyPrint(contentRequest));
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", bearer);
        request.setTimeOut(Constants.TIMEOUT);

        Net.HttpResponseListener listener = new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                System.out.println("Status code " + httpResponse.getStatus().getStatusCode());
                String responseString = httpResponse.getResultAsString();
                JsonValue jsonResponse = new JsonReader().parse(responseString);
                JsonValue data = jsonResponse.get("data");
                System.out.println("Response: " + responseString);
                if ( statusCode != 200 || data.isNull()) {
                    System.out.println("Failed " );
                    lblError.setText(Constants.CONNECTION_ERROR_MSG);
                }
                else {
                    storeExercisesAndTotalPages(data.get("ejercicios"));
                    lblError.setText("");
                    dataTableCell.setActor(buildDataTable(true));
                    isDataLoaded = true;
                    btnBack.setVisible(true);
                }
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
        dataTableCell.setActor(lblError);
        lblError.setText("Cargando datos...");
        btnBack.setVisible(false);
    }

    private void storeExercisesAndTotalPages(JsonValue ejercicios) {
        exercisesJson = ejercicios;
        totalPages = exercisesJson.size > ROWS_PER_PAGE ? currentPage + 1 : currentPage;
    }

    private String parseTimeStampString(String timestamp) {
        String[] aux = timestamp.split("T");
        aux = aux[0].split("-");
        return aux[2] + "/" + aux[1] + "/" + aux[0];
    }

    private Actor buildDataTable(boolean gotData) {

        if (gotData) {
            Table table = new Table();

            table.add(new Label("TIPO", emgoneSkin, "history-header")).expandX().padBottom(15);
            table.add(new Label("ESQUIVADOS", emgoneSkin, "history-header")).expandX().padBottom(15);
            table.add(new Label("OBSTÁCULOS", emgoneSkin, "history-header")).expandX().padBottom(15);
            table.add(new Label("FECHA", emgoneSkin, "history-header")).expandX().padBottom(15).row();

            int i = 0;
            for (JsonValue exerciseJson : exercisesJson.iterator()) {
                if (i == ROWS_PER_PAGE) break;
                table.add(new Label(exerciseJson.getString("tipo"), emgoneSkin, "history-data")).pad(10);
                table.add(new Label("" + exerciseJson.getInt("esquivados"), emgoneSkin, "history-data")).pad(10);
                table.add(new Label("" + (exerciseJson.getInt("esquivados") + exerciseJson.getInt("colisionados")), emgoneSkin, "history-data")).pad(10);
                table.add(new Label(parseTimeStampString(exerciseJson.getString("createdAt")), emgoneSkin, "history-data")).pad(10).row();
                i++;
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

