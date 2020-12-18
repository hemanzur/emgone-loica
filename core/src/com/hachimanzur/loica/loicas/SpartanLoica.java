package com.hachimanzur.loica.loicas;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.hachimanzur.loica.util.Constants;

public class SpartanLoica extends AbstractLoicaCostume{

    public SpartanLoica(){
    }

    @Override
    public void init() {
        atlas = new TextureAtlas(Constants.SPARTAN_LOICA_ATLAS);

        frame1 = atlas.findRegion("loica_up_spartan");
        frame2 = atlas.findRegion("loica_middle_spartan");
        frame3 = atlas.findRegion("loica_down_spartan");
        crash1 = atlas.findRegion("loica_damage_spartan");
        crash2 = atlas.findRegion("loica_damage_spartan_2");

        frame1Night = atlas.findRegion("loica_night_up_spartan");
        frame2Night = atlas.findRegion("loica_night_middle_spartan");
        frame3Night = atlas.findRegion("loica_night_down_spartan");
        crash1Night = atlas.findRegion("loica_night_damage_spartan");
        crash2Night = atlas.findRegion("loica_night_damage_spartan_2");
    }
}
