package com.hachimanzur.loica.util.Gamification.Achievements;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.hachimanzur.loica.screens.modals.AchievementModal;
import com.hachimanzur.loica.util.Constants;
import com.hachimanzur.loica.util.UserData;

public class UserAchievements {

    public static final UserAchievements instance = new UserAchievements();

    private AchievementModal superLoicaModal;
    private AchievementModal spartanLoicaModal;
    private AchievementModal queenLoicaModal;
    private AchievementModal wizardLoicaModal;
    private AchievementModal cityStageModal;
    private AchievementModal forestStageModal;

    public boolean superLoica;
    public boolean spartanLoica;
    public boolean queenLoica;
    public boolean wizardLoica;

    public boolean cityStage;
    public boolean forestStage;

    private Preferences list;

    private UserAchievements() {
        this.list = Gdx.app.getPreferences(Constants.ACHIEVEMENTS);
    }

    public void load () {
        superLoica = list.getBoolean("superLoica", false);
        spartanLoica = list.getBoolean("spartanLoica", false);
        queenLoica = list.getBoolean("queenLoica", false);
        wizardLoica = list.getBoolean("wizardLoica", false);
        cityStage = list.getBoolean("cityStage", false);
        forestStage = list.getBoolean("forestStage", false);
    }

    public void save () {
        list.putBoolean("superLoica", superLoica);
        list.putBoolean("spartanLoica", spartanLoica);
        list.putBoolean("queenLoica", queenLoica);
        list.putBoolean("wizardLoica", wizardLoica);
        list.putBoolean("cityStage", cityStage);
        list.putBoolean("forestStage", forestStage);

        list.flush();
    }

    public void getAchievementsFromServer() {
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setUrl(Constants.ACHIEVEMENTS_PAGE_URL+0);
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
                storeAchievementsFromResponse(response);
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

    private void storeAchievementsFromResponse(String response) {
        JsonValue json = new JsonReader().parse(response);
        JsonValue achievementsList = json.get("achievements");

        for (JsonValue achievement : achievementsList) {
            if ("Super Loica".equals(achievement.getString("name"))) {
                superLoica = achievement.getBoolean("done");
            }
            if ("Spartan Loica".equals(achievement.getString("name"))) {
                spartanLoica = achievement.getBoolean("done");
            }
            if ("Queen Loica".equals(achievement.getString("name"))) {
                queenLoica = achievement.getBoolean("done");
            }
            if ("Wizard Loica".equals(achievement.getString("name"))) {
                wizardLoica = achievement.getBoolean("done");
            }
            if ("City Stage".equals(achievement.getString("name"))) {
                cityStage = achievement.getBoolean("done");
            }
            if ("Forest Stage".equals(achievement.getString("name"))) {
                forestStage = achievement.getBoolean("done");
            }
        }

        System.out.println("superLoica achievement = " + superLoica + " Saved");
        System.out.println("spartanLoica achievement = " + spartanLoica + " Saved");
        System.out.println("queenLoica achievement = " + queenLoica + " Saved");
        System.out.println("wizardLoica achievement = " + wizardLoica + " Saved");
        System.out.println("cityStage achievement = " + cityStage + " Saved");
        System.out.println("forestStage achievement = " + forestStage + " Saved");
        save();
    }

    public void checkForAchievements(Stack uiStack, int oldLevel, int newLevel, Skin skin) {
        if (oldLevel <= Constants.SUPER_LOICA_LEVEL-1 && newLevel >= Constants.SUPER_LOICA_LEVEL) {
            System.out.println("Superloica unlocked");
            superLoicaModal = new AchievementModal();
            superLoicaModal.build(
                    Achievement.Available.SUPER,
                    skin,
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            superLoicaModal.setVisible(false);
                        }
                    });
            uiStack.add(superLoicaModal.getTable());
            superLoica = true;
        }

        if (oldLevel <= Constants.SPARTAN_LOICA_LEVEL-1 && newLevel >= Constants.SPARTAN_LOICA_LEVEL) {
            System.out.println("Spartan loica unlocked");
            spartanLoicaModal = new AchievementModal();
            spartanLoicaModal.build(
                    Achievement.Available.SPARTAN,
                    skin,
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            spartanLoicaModal.setVisible(false);
                        }
                    });
            uiStack.add(spartanLoicaModal.getTable());
            spartanLoica = true;
        }
        if (oldLevel <= Constants.QUEEN_LOICA_LEVEL-1 && newLevel >= Constants.QUEEN_LOICA_LEVEL) {
            System.out.println("queenLoica unlocked");
            queenLoicaModal = new AchievementModal();
            queenLoicaModal.build(
                    Achievement.Available.QUEEN,
                    skin,
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            queenLoicaModal.setVisible(false);
                        }
                    });
            uiStack.add(queenLoicaModal.getTable());
            queenLoica = true;
        }
        if (oldLevel <= Constants.WIZARD_LOICA_LEVEL-1 && newLevel >= Constants.WIZARD_LOICA_LEVEL) {
            System.out.println("Wizard loica unlocked");
            wizardLoicaModal = new AchievementModal();
            wizardLoicaModal.build(
                    Achievement.Available.WIZARD,
                    skin,
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            wizardLoicaModal.setVisible(false);
                        }
                    });
            uiStack.add(wizardLoicaModal.getTable());
            wizardLoica = true;
        }
        if (oldLevel <= Constants.CITY_STAGE_LEVEL-1 && newLevel >= Constants.CITY_STAGE_LEVEL) {
            System.out.println("City Stage unlocked");
            cityStageModal = new AchievementModal();
            cityStageModal.build(
                    Achievement.Available.CITY,
                    skin,
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            cityStageModal.setVisible(false);
                        }
                    });
            uiStack.add(cityStageModal.getTable());
            cityStage = true;
        }
        if (oldLevel <= Constants.FOREST_STAGE_LEVEL-1 && newLevel >= Constants.FOREST_STAGE_LEVEL) {
            System.out.println("Forest Stage unlocked");
            forestStageModal = new AchievementModal();
            forestStageModal.build(
                    Achievement.Available.FOREST,
                    skin,
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            forestStageModal.setVisible(false);
                        }
                    });
            uiStack.add(forestStageModal.getTable());
            forestStage = true;
        }
        save();
    }

    public void sendAchievementsToServer() {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(Constants.POST_ACHIEVEMENTS_URL);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Token token=" + UserData.getToken());
        request.setTimeOut(Constants.TIMEOUT);

        String content = "{" +
                "\"achievement\": {" +
                "\"name\": \"";

        if (superLoica) {
            System.out.println("sent super loica achievement");
            content += "Super Loica,";
        }

        if (spartanLoica) {
            System.out.println("sent spartan loica achievement");
            content += "Spartan Loica,";
        }

        if (queenLoica) {
            System.out.println("sent queen loica achievement");
            content += "Queen Loica,";
        }

        if (wizardLoica) {
            System.out.println("sent wizard loica achievement");
            content += "Wizard Loica,";
        }
        if (cityStage) {
            System.out.println("sent city stage achievement");
            content += "City Stage,";
        }
        if (forestStage) {
            System.out.println("sent forest stage achievement");
            content += "Forest Stage,";
        }

        content += "\"}}";
        request.setContent(content);

        // si no envío nada o "" : 400,
        // algo que no existe: 202,
        // algo que si existe: 201
        Net.HttpResponseListener listener = new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                System.out.println("Status code " + statusCode);
                System.out.println("Result: " + httpResponse.getResultAsString());
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
}
