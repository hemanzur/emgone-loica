package com.nursoft.emgone.screens.modals;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.nursoft.emgone.util.Constants;


public abstract class AbstractModal implements Disposable {
    private boolean visible = false;
    Skin emgoneImages = new Skin(new TextureAtlas(Constants.SELECT_COSTUME_ATLAS));
    public Table table;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        table.setVisible(this.visible);
    }

    public Table getTable() {
        return table;
    }

    @Override
    public void dispose() {
        emgoneImages.dispose();
    }
}
