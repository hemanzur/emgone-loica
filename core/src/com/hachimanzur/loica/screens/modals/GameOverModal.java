package com.hachimanzur.loica.screens.modals;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class GameOverModal extends AbstractModal {

    public GameOverModal() {
    }

    public Table build(int obstaclesDodged, int obstaclesCollided, Skin skin, TextureRegion region, TextureAtlas atlas, ClickListener btnSendListener) {
        table = new Table();
        Window window = new Window("", skin);
        window.pad(30);

        window.add(new Label("FIN DEL JUEGO", skin, "pause-window")).colspan(3).row();
        window.add(new Image(region)).expand();
        Label gameOverScore = new Label("     " + obstaclesDodged + "/" + (obstaclesDodged + obstaclesCollided) + "     ", skin, "score");
        window.add(gameOverScore).expand();
        window.add(new Image(atlas.findRegion("profile-01"))).expand().row();

        TextButton btnSendData = new TextButton("VOLVER A MENÚ", skin, "save");
        btnSendData.addListener(btnSendListener);

        window.add().width(btnSendData.getWidth());
        window.add(btnSendData).pad(30, 15, 15, 15);
        window.add().width(btnSendData.getWidth());

        window.setMovable(false);

        getTable().add(window);

        return getTable();
    }
}
