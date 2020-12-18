package com.hachimanzur.loica.loicas;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.hachimanzur.loica.util.Constants;

public class SuperLoica extends AbstractLoicaCostume {

    public SuperLoica() {
    }

    @Override
    public void init() {
        atlas = new TextureAtlas(Constants.SUPER_LOICA_ATLAS);

        frame1 = atlas.findRegion("loica_up_sup");
        frame2 = atlas.findRegion("loica_middle_sup");
        frame3 = atlas.findRegion("loica_down_sup");
        crash1 = atlas.findRegion("loica_damage_sup");
        crash2 = atlas.findRegion("loica_damage_sup_2");

        frame1Night = atlas.findRegion("loica_night_up_sup");
        frame2Night = atlas.findRegion("loica_night_middle_sup");
        frame3Night = atlas.findRegion("loica_night_down_sup");
        crash1Night = atlas.findRegion("loica_night_damage_sup");
        crash2Night = atlas.findRegion("loica_night_damage_sup_2");
    }
}
