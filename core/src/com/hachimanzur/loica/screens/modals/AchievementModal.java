package com.hachimanzur.loica.screens.modals;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.hachimanzur.loica.util.Gamification.Achievements.Achievement;


public class AchievementModal extends AbstractModal {
    public AchievementModal() {}

    public Table build(Achievement.Available ach, Skin skin, ClickListener continueListener) {
        table = new Table();
        Window window = new Window("", skin);

        window.pad(30);

        window.add(new Label("¡FELICIDADES!", skin, "pause-window")).row();
        window.add(new Label("TU PROGRESO HA DESBLOQUEADO", skin, "calib-labels")).padBottom(20).row();
        window.add(new Label(ach.getName(), skin, "achievement-modal-name")).row();

        Image achImage = new Image(emgoneImages, ach.getAssetFilename());
        window.add(achImage).expand().pad(30,100,30,100).row();

        TextButton continueBtn = new TextButton("CONTINUAR", skin, "save");
        continueBtn.addListener(continueListener);
        window.add(continueBtn).padBottom(20);
        window.setMovable(false);
        table.add(window);

        return table;
    }
}
