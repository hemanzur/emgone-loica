package com.hachimanzur.loica.loicas;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.hachimanzur.loica.util.Constants;

public class WizardLoica extends AbstractLoicaCostume{

    public WizardLoica() {
    }

    @Override
    public void init() {
        atlas = new TextureAtlas(Constants.WIZARD_LOICA_ATLAS);

        frame1 = atlas.findRegion("loica_up_mage");
        frame2 = atlas.findRegion("loica_middle_mage");
        frame3 = atlas.findRegion("loica_down_mage");
        crash1 = atlas.findRegion("loica_damage_mage");
        crash2 = atlas.findRegion("loica_damage_mage_2");

        frame1Night = atlas.findRegion("loica_night_up_mage");
        frame2Night = atlas.findRegion("loica_night_middle_mage");
        frame3Night = atlas.findRegion("loica_night_down_mage");
        crash1Night = atlas.findRegion("loica_night_damage_mage");
        crash2Night = atlas.findRegion("loica_night_damage_mage_2");
    }
}
