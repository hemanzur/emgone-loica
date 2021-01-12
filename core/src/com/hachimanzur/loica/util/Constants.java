package com.hachimanzur.loica.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Constants {

    public static final float VIEWPORT_WIDTH = 720.0f;
    public static final float VIEWPORT_HEIGHT = 1200.0f;
    public static final float GAME_VIEWPORT_HEIGHT = 480.0f;
    public static final float GAME_VIEWPORT_WIDTH = 800.0f;
    public static final int GROUND_HEIGHT = 28;

    // please do not use negative floats
    public static final float DISTANCE_BETWEEN_OBSTACLES_OFFSET = 200.0f;
    public static final float DISTANCE_BETWEEN_OBSTACLES_SLOPE = 1000.0f;
    public static final float DISTANCE_BETWEEN_OBSTACLES_STDEV = 200.0f;

    public static final int MIN_EXERCISE_TIME = 30;
    public static final int DELTA_EXCERCISE_TIME = 30;
    public static final int MIN_OBSTACLE_TIME = 1;
    public static final int DELTA_OBSTACLE_TIME = 1;
    public static final float CALIBRATION_ENABLE_TIME = 3.0f;

    // endpoints
    //FIXME: AUTOMATIZAR DEPENDIENDO DEL TIPO DE BUILD QUE SE REALIZA
    // private static final String HOST = BuildConfig.HOST;
    // Compsci app endpoint
    public static final String GRAPHQL_URL = "https://compsci.cl/emg-one/graphql";
    // Deprecated Nursoft endpoints
    // private static final String HOST = "http://emgone.cl";
    // private static final String PORT = "80";
    // public static final String REGISTER_URL = HOST + ":" + PORT + "/api/v1/users";
    // public static final String RECOVERY_URL = HOST + ":" + PORT + "/api/v1/users/me/recovery";
    // public static final String ACHIEVEMENTS_PAGE_URL = HOST + ":" + PORT + "/api/v1/achievements?page=";
    // public static final String POST_ACHIEVEMENTS_URL = HOST + ":" + PORT + "/api/v1/achievements";

    public static final int TIMEOUT = 15000;
    // error handlers
    public static final int FORM_WITH_ERRORS = -1;
    public static final int EMPTY_FIELDS = -2;
    public static final int INVALID_EMAIL_FORMAT = -3;
    public static final int PASSWORD_TOO_SHORT = -4;
    public static final int INVALID_RUT = -5;

    public static final int PASSWORDS_DONT_MATCH = -6;
    public static final String EMPTY_FIELD_MSG = "Debe llenar todos los campos requeridos";
    public static final String INVALID_EMAIL_FORMAT_MSG = "Formato de email inválido";
    public static final String PASSWORD_TOO_SHORT_MSG = "La contraseña debe tener 8 o más caracteres";
    public static final String INVALID_RUT_MSG = "RUT inválido";
    public static final String FORM_WITH_ERRORS_MSG = "El formulario contiene errores";
    public static final String CONNECTION_ERROR_MSG = "No se pudo conectar...\nRevise su conexión a internet o intente más tarde";
    public static final String INVALID_USER_PASS_MSG = "Usuario o contraseña inválidos";
    public static final String PASSWORDS_DONT_MATCH_MSG = "Las contraseñas no coinciden";


    public static final String EMAIL_NOT_REGISTERED_MSG = "El correo entregado no se encuentra registrado";

    // regex
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    // skins
    public static final String PREFERENCES = "game-prefs";
    public static final String PERSONAL_DATA = "personal-data";
    public static final String ACHIEVEMENTS = "achievements";
    public static final String EMGONE_SKIN = "emgone-assets/emgone-skin.json";
    public static final String EMGONE_ATLAS = "emgone-assets/emgone-skin.atlas";
    public static final String EMGONE_IMAGES_ATLAS= "emgone-assets/emgone-images.txt";
    public static final String EMGONE_IMAGES_ATLAS_2 = "emgone-assets/emgone-images-2.txt";
    public static final String CITY_STAGE_ATLAS = "emgone-assets/city-assets.txt";
    public static final String DESERT_STAGE_ATLAS = "emgone-assets/desert-assets.txt";
    public static final String FOREST_STAGE_ATLAS = "emgone-assets/forest-assets.txt";

    public static final String SELECT_COSTUME_ATLAS = "emgone-assets/select-costume-images.txt";

    private static String[] bodyParts = {
            " Brazo izquierdo", 
            " Brazo derecho", 
            " Pie izquierdo", 
            " Pie derecho",
            " Cara izquierdo",
            " Cara derecho",
            " Deglución",
            " Hombro izquierdo",
            " Hombro derecho",
            " Bíceps izquierdo",
            " Bíceps derecho",
            " Tríceps izquierdo",
            " Tríceps derecho",
            " Antebrazo flexión izquierdo",
            " Antebrazo flexión derecho",
            " Antebrazo extensión izquierdo",
            " Antebrazo extensión derecho",
            " Mano izquierdo",
            " Mano derecho",
            " Abdominal",
            " Lumbar",
            " Piso pélvico",
            " Glúteo izquierdo",
            " Glúteo derecho",
            " Iliopsoas izquierdo",
            " Iliopsoas derecho",
            " Cuádriceps izquierdo",
            " Cuádriceps derecho",
            " Isquiotibiales izquierdo",
            " Isquiotibiales derecho",
            " Tibial anterior izquierdo",
            " Tibial anterior derecho",
            " Tríceps sural izquierdo",
            " Tríceps sural derecho"
    };
    public static final Array<String> BODY_PARTS_LIST = new Array<String>(bodyParts);

    public static String shortBodyPart(String part) {
        String[] parts = part.split(" ");
        parts = Arrays.copyOfRange(parts, 1, parts.length);
        Gdx.app.log("ShortBodyPart", String.valueOf(parts.length));

        if(parts.length == 1) { return part; }

        StringBuilder builder = new StringBuilder(40);
        if(parts.length == 2) {
            builder.append(parts[0]);
            builder.append(" ");
            builder.append(parts[1].substring(0, 3));
        }
        else if(parts.length == 3) {
            builder.append(parts[0]);
            builder.append(" ");
            builder.append(parts[1].substring(0, 3));
            builder.append(" ");
            builder.append(parts[2].substring(0, 3));
            builder.append(" ");
        }

        return builder.toString();
    }

    // obstacle modes
    public static final Map<String, String> OBSTACLE_MODES;
    static {
        Map<String, String> obstacles = new LinkedHashMap<String, String>();
        obstacles.put("Velocidad", "speed");
        obstacles.put("Fuerza/Resistencia", "force");
        obstacles.put("Coordinación", "coord");
        obstacles.put("Coordinación (arriba)", "coordUp");
        obstacles.put("Coordinación (abajo)", "coordDn");
        OBSTACLE_MODES = Collections.unmodifiableMap(obstacles);
    }
    private static String[] obstacleNames = OBSTACLE_MODES.keySet().toArray(new String[OBSTACLE_MODES.size()]);
    public static final Array<String> OBSTACLE_MODES_NAMES = new Array<String>(obstacleNames);

    // scenarios
    public static final int DESERT = 1;
    public static final int CITY = 2;
    public static final int FOREST = 3;

    // nombres de los escenarios
    public static final Map<Integer, String> SCENARIO_NAMES;
    static {
        Map<Integer, String> names = new LinkedHashMap<Integer, String>();
        names.put(DESERT, "DESIERTO");
        names.put(CITY, "CIUDAD");
        names.put(FOREST, "BOSQUE");
        SCENARIO_NAMES = Collections.unmodifiableMap(names);
    }

    // loicas
    public static final int NORMAL_LOICA = 1;
    public static final int SUPER_LOICA = 2;
    public static final int SPARTAN_LOICA = 3;
    public static final int QUEEN_LOICA = 4;
    public static final int WIZARD_LOICA = 5;

    public static final String NORMAL_LOICA_ATLAS = "emgone-assets/normal-loica-assets.txt";
    public static final String SUPER_LOICA_ATLAS = "emgone-assets/super-loica-assets.txt";
    public static final String SPARTAN_LOICA_ATLAS = "emgone-assets/spartan-loica-assets.txt";
    public static final String QUEEN_LOICA_ATLAS = "emgone-assets/queen-loica-assets.txt";
    public static final String WIZARD_LOICA_ATLAS = "emgone-assets/wizard-loica-assets.txt";

    // nombres de las loicas
    public static final Map<Integer, String> LOICA_NAMES;
    static {
        Map<Integer, String> names = new LinkedHashMap<Integer, String>();
        names.put(NORMAL_LOICA, "LOICA");
        names.put(SUPER_LOICA, "SUPER LOICA");
        names.put(SPARTAN_LOICA, "LOICA ESPARTANA");
        names.put(QUEEN_LOICA, "LOICA REINA");
        names.put(WIZARD_LOICA, "LOICA MÁGICA");
        LOICA_NAMES = Collections.unmodifiableMap(names);
    }

    // achievements levels
    public static final int SUPER_LOICA_LEVEL = 10;
    public static final int SPARTAN_LOICA_LEVEL = 20;
    public static final int QUEEN_LOICA_LEVEL = 40;
    public static final int WIZARD_LOICA_LEVEL = 50;

    public static final int CITY_STAGE_LEVEL = 5;
    public static final int FOREST_STAGE_LEVEL = 30;

    public static double calculatePeriod(float velocity, float nearness) {
        double ratio = velocity / nearness;
        return (-2.360442f * Math.log(ratio)) + 1.270737f;
    }

    public static float calculateForcePeriod(float nearness) {
        return (nearness + 102) / 305;
    }

    public static float calculateForceDistance(float period) {
        return (305 * period) - 102;
    }

    public static double calculateForceCorrection(float period, float distance, int obstacleWidth) {
        return (period * obstacleWidth) / distance;
    }

    public static float getRestMultiplier(int level) {
        switch (level) {
            case 1: return 0.5f;
            case 2: return 0.75f;
            case 3: return 1.0f;
            case 4: return 1.5f;
            case 5: return 2.0f;
            default: return 1.0f;
        }
    }

    public static int getRestLevel(float multiplier) {
        int multSwitch = Math.round(100 * multiplier);
        switch(multSwitch) {
            case 50: return 1;
            case 75: return 2;
            case 100: return 3;
            case 150: return 4;
            case 200: return 5;
            default: return 3;
        }
    }

}
