package com.themarbles.game.myImpls;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;

/**
 * Overwrites {@link com.badlogic.gdx.scenes.scene2d.ui.SelectBox}, adds {@link SelectBox#canBeFired} flag to avoid
 * unwanted code execution in {@link com.themarbles.game.screens.Room}.
 * @see com.themarbles.game.screens.Room
 * @see com.badlogic.gdx.scenes.scene2d.ui.SelectBox
 * @param <T>
 */

public class SelectBox<T> extends com.badlogic.gdx.scenes.scene2d.ui.SelectBox<T> {

    private boolean canBeFired;

    public SelectBox (Skin skin){
        super(skin);
        canBeFired = true;
    }

    @Override
    public void setItems (Array<T> newItems) {
        canBeFired = false;
        super.setItems(newItems);
    }

    @Override
    public @Null T getSelected () {
        canBeFired = false;
        return super.getSelected();
    }

    public boolean getCanBeFired(){
        return canBeFired;
    }

    public void setCanBeFired(boolean canBeFired) {
        this.canBeFired = canBeFired;
    }

}
