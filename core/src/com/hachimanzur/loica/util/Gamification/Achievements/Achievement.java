package com.hachimanzur.loica.util.Gamification.Achievements;

import com.hachimanzur.loica.util.Constants;


public final class Achievement {
    public enum Types {
        COSTUME,
        SCENARIO,
        DODGED,
        TIME
    }
    public enum Available {
        SUPER ("SUPER LOICA", Types.COSTUME, "loica2_bg"),
        SPARTAN ("LOICA ESPARTANA", Types.COSTUME, "loica3_bg"),
        QUEEN ("LOICA REINA", Types.COSTUME, "loica4_bg"),
        WIZARD ("LOICA MÁGICA", Types.COSTUME, "loica5_bg"),
        CITY ("ESCENARIO CIUDAD", Types.SCENARIO, "scene_2_big"),
        FOREST ("ESCENARIO BOSQUE", Types.SCENARIO, "scene_3_big");


        private final String assetFilename;
        private final Types type;
        private final String name;

        Available(String name, Types type, String assetFilename) {
            this.name = name;
            this.type = type;
            this.assetFilename = assetFilename;
        }

        public String getAssetFilename() {
            return assetFilename;
        }

        public String getName() {
            return name;
        }
    }

    public static boolean isLocked(Available ach, int level) {
        switch (ach) {
            case SUPER:
                if (level >= Constants.SUPER_LOICA_LEVEL) return false;
                break;
            case SPARTAN:
                if (level >= Constants.SPARTAN_LOICA_LEVEL) return false;
                break;
            case QUEEN:
                if (level >= Constants.QUEEN_LOICA_LEVEL) return false;
                break;
            case WIZARD:
                if (level >= Constants.WIZARD_LOICA_LEVEL) return false;
                break;
            case CITY:
                if (level >= Constants.CITY_STAGE_LEVEL) return false;
                break;
            case FOREST:
                if (level >= Constants.FOREST_STAGE_LEVEL) return false;
                break;
        }
        return true;
    }
}
