package com.hachimanzur.loica.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.hachimanzur.loica.util.Gamification.Gamification;

public class UserData {
    // TO DO pls refactor dis
    int id;
    public String email;
    public String name;
    public String phone;
    public String rut;
    public String address;
    String token;
    private String password;
    public int score;

    public static Preferences file = Gdx.app.getPreferences(Constants.PERSONAL_DATA);

    public UserData() {

    }

    public UserData(String n, String r, String ph, String e, String a, String p, String t, int score) {
        name = n;
        rut = r;
        phone = ph;
        email = e;
        address = a;
        password = p;
        token = t;
        this.score = score;
    }

    public UserData(String n, String r, String ph, String e, String a, String p, String t) {
        name = n;
        rut = r;
        phone = ph;
        email = e;
        address = a;
        password = p;
        token = t;
    }

    public void save() {
        file.putInteger("id", id);
        file.putString("email", email);
        file.putString("name", name);
        file.putString("phone", phone);
        file.putString("rut", rut);
        file.putString("address", address);
        file.putString("token", token);
        file.putBoolean("loggedIn", true);
        file.flush();

        // Save score to GamePreferences
        GamePreferences prefs = GamePreferences.instance;
        prefs.load();
        prefs.score = score;
        prefs.level = Gamification.getCurrentLevel(score);
        prefs.save();
    }

    public static String getToken() {
        return file.getString("token");
    }

    public static void setToken(String token) {
        file.putString("token", token);
        file.flush();
    }

    public void load() {
        id = file.getInteger("id");
        email = file.getString("email");
        name = file.getString("name");
        phone = file.getString("phone");
        rut = file.getString("rut");
        address = file.getString("address");
        token = file.getString("token");
    }

    public static boolean isLoggedIn() {
        return file.getBoolean("loggedIn", false);
    }

    public static void resetData() {
        file.putInteger("id", -1);
        file.putString("email", "");
        file.putString("name", "");
        file.putString("phone", "");
        file.putString("rut", "");
        file.putString("address", "");
        file.putString("token", "");
        file.putBoolean("loggedIn", false);
        file.flush();
    }
}
