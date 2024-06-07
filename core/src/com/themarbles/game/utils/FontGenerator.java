package com.themarbles.game.utils;

import static com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.*;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontGenerator {

    public static BitmapFont generateFont(FileHandle pathToFont, int size, Color color){

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(pathToFont);
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        BitmapFont font;

        parameter.size = size;
        parameter.color = color;

        font = generator.generateFont(parameter);

        return font;

    }

}
