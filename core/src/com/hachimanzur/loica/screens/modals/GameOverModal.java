package com.nursoft.emgone.screens.modals;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.nursoft.emgone.util.Constants;


public class GameOverModal extends AbstractModal {

    public GameOverModal() {
    }

    public Table build(int obstaclesDodged,
                       int obstaclesCollided,
                       TextField userEmailField,
                       Skin skin,
                       TextureRegion region,
                       TextureAtlas atlas,
                       ClickListener btnSendListener) {
        table = new Table();
        Window window = new Window("", skin);
        window.pad(30);

        window.add(new Label("FIN DEL JUEGO", skin, "pause-window")).colspan(3).row();
        window.add(new Image(region)).expand();
        Label gameOverScore = new Label("     " + obstaclesDodged + "/" + (obstaclesDodged + obstaclesCollided) + "     ", skin, "score");
        window.add(gameOverScore).expand();
        window.add(new Image(atlas.findRegion("profile-01"))).expand().row();

        // Input address
        userEmailField.setMessageText("Comentario");
        float textFieldWidth = Constants.VIEWPORT_WIDTH*0.7f;
        userEmailField.setAlignment(Align.center);
        window.add(userEmailField).width(textFieldWidth).pad(15, 15, 0, 15).colspan(3).row();
        //userEmailField.addListener(focusListener);

        TextButton btnSendData = new TextButton("FINALIZAR", skin, "save");
        btnSendData.addListener(btnSendListener);

        window.add().width(btnSendData.getWidth());
        window.add(btnSendData).pad(30, 15, 15, 15);
        window.add().width(btnSendData.getWidth());

        window.setMovable(false);

        getTable().add(window);

        return getTable();
    }
}
