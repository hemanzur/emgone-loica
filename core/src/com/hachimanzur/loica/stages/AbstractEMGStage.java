package com.hachimanzur.loica.stages;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public abstract class AbstractEMGStage implements Disposable {

    public TextureRegion background;
    public TextureRegion backgroundLandscape;
    public TextureRegion backgroundMoving;
    public TextureRegion backgroundMoving2;
    public TextureRegion ground;
    public TextureRegion endGameObs;
    public TextureRegion [] groundObs;
    public TextureRegion [] ceilingObs;

    TextureAtlas atlas;

    public abstract void init();

    public TextureRegion getRandomCeilingObs(int heightFactor){
        return ceilingObs[heightFactor];
    }

    public TextureRegion getRandomGroundObs(int heightFactor){
        return groundObs[heightFactor];
    }

    @Override
    public void dispose() {
        atlas.dispose();

    }
}
