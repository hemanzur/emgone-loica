package com.hachimanzur.loica.loicas;


import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public abstract class AbstractLoicaCostume implements Disposable {

    public TextureRegion frame1;
    public TextureRegion frame2;
    public TextureRegion frame3;
    public TextureRegion crash1;
    public TextureRegion crash2;

    public TextureRegion frame1Night;
    public TextureRegion frame2Night;
    public TextureRegion frame3Night;
    public TextureRegion crash1Night;
    public TextureRegion crash2Night;

    TextureAtlas atlas;

    public abstract void init();

    @Override
    public void dispose() {
        atlas.dispose();
    }

    public Animation<TextureRegion> getAnimation(boolean isNight) {
        if (isNight)
            return new Animation<TextureRegion>(0.08f, frame1Night, frame2Night, frame3Night, frame2Night);
        else
            return new Animation<TextureRegion>(0.08f, frame1, frame2, frame3, frame2);

    }

    public Animation<TextureRegion> getCrashAnimation(boolean isNight) {
        if (isNight)
            return new Animation<TextureRegion>(0.06f, crash1Night, crash2Night);
        else
            return new Animation<TextureRegion>(0.06f, crash1, crash2);
    }
}
