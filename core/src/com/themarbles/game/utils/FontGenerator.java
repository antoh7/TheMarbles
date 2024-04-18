package com.themarbles.game.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontGenerator {

    private static FreeTypeFontGenerator generator;
    private static FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    public static BitmapFont generateFont(FileHandle pathToFont){
        generator = new FreeTypeFontGenerator(pathToFont);
        return generator.generateFont(parameter);
    }

    public static void initParameter(int size, Color color){
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = color;
    }
}
