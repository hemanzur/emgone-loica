package com.hachimanzur.loica.loicas;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.hachimanzur.loica.util.Constants;

public class QueenLoica extends AbstractLoicaCostume{

    public QueenLoica() {
    }

    @Override
    public void init() {
        atlas = new TextureAtlas(Constants.QUEEN_LOICA_ATLAS);

        frame1 = atlas.findRegion("loica_up_queen");
        frame2 = atlas.findRegion("loica_middle_queen");
        frame3 = atlas.findRegion("loica_down_queen");
        crash1 = atlas.findRegion("loica_damage_queen");
        crash2 = atlas.findRegion("loica_damage_queen_2");

        frame1Night = atlas.findRegion("loica_night_up_queen");
        frame2Night = atlas.findRegion("loica_night_middle_queen");
        frame3Night = atlas.findRegion("loica_night_down_queen");
        crash1Night = atlas.findRegion("loica_night_damage_queen");
        crash2Night = atlas.findRegion("loica_night_damage_queen_2");
    }
}
