package com.hachimanzur.loica.loicas;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.hachimanzur.loica.util.Constants;

public class NormalLoica extends AbstractLoicaCostume {



    public NormalLoica() {
    }

    @Override
    public void init() {
        atlas = new TextureAtlas(Constants.NORMAL_LOICA_ATLAS);

        frame1 = atlas.findRegion("loica_up");
        frame2 = atlas.findRegion("loica_middle");
        frame3 = atlas.findRegion("loica_down");
        crash1 = atlas.findRegion("loica_damage");
        crash2 = atlas.findRegion("loica_damage_2");

        frame1Night = atlas.findRegion("loica_night_up");
        frame2Night = atlas.findRegion("loica_night_middle");
        frame3Night = atlas.findRegion("loica_night_down");
        crash1Night = atlas.findRegion("loica_night_damage");
        crash2Night = atlas.findRegion("loica_night_damage_2");
    }
}
