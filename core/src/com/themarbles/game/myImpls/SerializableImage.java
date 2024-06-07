package com.themarbles.game.myImpls;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.io.Serializable;

/**
 * represents com.badlogic.gdx.scenes.scene2d.ui.Image class which can be serialised
 * **/

public class SerializableImage extends Image implements Serializable {

    public static final long serialVersionUID = 2222222222L;

    public SerializableImage (Texture texture){
        super(texture);
    }

    //serializable image object with no texture
    public SerializableImage() {}

}

