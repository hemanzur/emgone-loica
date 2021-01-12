package com.hachimanzur.loica.util;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.hachimanzur.loica.screens.PreLoginScreen;
import com.hachimanzur.loica.util.Gamification.Gamification;

public class UserData {
    // TO DO pls refactor dis
    private String id;
    public String email;
    public String name;
    public String phone;
    public String rut;
    public String address;
    private String token;
    private String password;
    public int score;

    public static Preferences file = Gdx.app.getPreferences(Constants.PERSONAL_DATA);

    public UserData() {

    }

    public UserData(String id,
                    String name,
                    String phone,
                    String email,
                    String address,
                    String jwt) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.token = jwt;
    }

    public UserData(String id,
                    String name,
                    String phone,
                    String email,
                    String address,
                    int score,
                    String jwt) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.score = score;
        this.token = jwt;
    }

    public UserData(String id,
                    String name,
                    String phone,
                    String email,
                    String address,
                    int score,
                    String jwt,
                    String password) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.score = score;
        this.token = jwt;
        this.password = password;
    }

    public void save() {
        file.putString("id", id);
        file.putString("email", email);
        file.putString("name", name);
        file.putString("phone", phone);
        file.putString("rut", rut);
        file.putString("address", address);
        file.putString("token", token);
        file.putString("password", password);
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

    public static String getId() {  return file.getString("id"); }

    public static String getEmail() {  return file.getString("email"); }

    public static String getPassword() {  return file.getString("password"); }

    public static void setToken(String token) {
        file.putString("token", token);
        file.flush();
    }

    public void load() {
        id = file.getString("id");
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
        file.putString("id", "");
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
