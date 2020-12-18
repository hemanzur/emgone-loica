package com.hachimanzur.loica.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Obstacle {
    public Vector2 position = new Vector2();
    public TextureRegion image;
    public boolean counted;
    public boolean overlaping;
    private int heightFactor;
    public boolean isUp;
    private static Random rnd = new Random();

    public Obstacle(float x, float y, TextureRegion image, int heightFactor, boolean isUp) {
        this.position.x = x;
        if (isUp) {
            this.position.y = y;
        } else {
            this.position.y = 0;
        }
        this.image = image;
        this.overlaping = false;
        this.heightFactor = heightFactor;
        this.isUp = isUp;
    }

    public void draw(SpriteBatch batch){
        if(this.isUp){
            batch.draw(this.image, this.position.x,this.position.y-Constants.GROUND_HEIGHT,this.image.getRegionWidth(),this.getRealHeight());
        }
        else{
            batch.draw(this.image, this.position.x,this.position.y+Constants.GROUND_HEIGHT,this.image.getRegionWidth(),this.getRealHeight());
        }

    }
    public float getRealHeight(){
        return this.image.getRegionHeight();
    }

    public int getHeightFactor() {
        return heightFactor;
    }

    public void setHeightFactor(int heightFactor) {
        this.heightFactor = heightFactor;
    }
}
