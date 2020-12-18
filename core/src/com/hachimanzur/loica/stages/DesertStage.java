package com.hachimanzur.loica.stages;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hachimanzur.loica.util.Constants;

public class DesertStage extends AbstractEMGStage {

    public DesertStage() {}

    @Override
    public void init() {
        atlas = new TextureAtlas(Constants.DESERT_STAGE_ATLAS);

        background = atlas.findRegion("back");
        backgroundLandscape = atlas.findRegion("middle");
        backgroundMoving = atlas.findRegion("floating");
        backgroundMoving2 = atlas.findRegion("floating");
        ground = atlas.findRegion("band");
        endGameObs = atlas.findRegion("small-obstacle");
        groundObs = new TextureRegion[] {
                atlas.findRegion("bottom1"),
                atlas.findRegion("bottom2"),
                atlas.findRegion("bottom3"),
                atlas.findRegion("bottom4"),
                atlas.findRegion("bottom5"),
                atlas.findRegion("bottom6"),
                atlas.findRegion("bottom7")
        };
        ceilingObs = new TextureRegion[] {
                atlas.findRegion("top1"),
                atlas.findRegion("top2"),
                atlas.findRegion("top3"),
                atlas.findRegion("top4"),
                atlas.findRegion("top5"),
                atlas.findRegion("top6"),
                atlas.findRegion("top7")
        };
    }
}
