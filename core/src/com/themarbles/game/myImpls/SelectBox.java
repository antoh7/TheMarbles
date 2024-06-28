package com.themarbles.game.myImpls;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;

/**
 * Overwrites {@link com.badlogic.gdx.scenes.scene2d.ui.SelectBox}, adds {@link SelectBox#canBeExecuted} flag to avoid
 * unwanted code execution in {@link com.themarbles.game.screens.Room}.
 * @see com.themarbles.game.screens.Room
 * @see com.badlogic.gdx.scenes.scene2d.ui.SelectBox
 * @param <T>
 */

public class SelectBox<T> extends com.badlogic.gdx.scenes.scene2d.ui.SelectBox<T> {

    private boolean canBeExecuted;

    public SelectBox (Skin skin){
        super(skin);
        canBeExecuted = true;
    }

    @Override
    public void setItems (Array<T> newItems) {
        canBeExecuted = false;
        super.setItems(newItems);
    }

    @Override
    public @Null T getSelected () {
        canBeExecuted = false;
        return super.getSelected();
    }

    public boolean getCanBeExecuted(){
        return canBeExecuted;
    }

    public void setCanBeExecuted(boolean canBeExecuted) {
        this.canBeExecuted = canBeExecuted;
    }

}
