package ru.kbuearpov.themarbles.utils;

import static com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.*;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/** Util, using to generate custom {@link com.badlogic.gdx.graphics.g2d.BitmapFont}.
 * @see BitmapFont
 */

public class FontGenerator {

    public static BitmapFont generateFont(FileHandle pathToFont, int size, Color color, String characters){

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(pathToFont);
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        BitmapFont font;

        parameter.size = size;
        parameter.color = color;
        parameter.characters = characters;

        font = generator.generateFont(parameter);

        return font;

    }

}
