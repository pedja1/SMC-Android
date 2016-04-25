package rs.papltd.smc.smc_level_converter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import java.io.IOException;

/**
 * Created by pedja on 1/31/14.
 */

public class TextureAtlasGenerator
{
    public static void main(String[] args) throws IOException
    {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 2048;
        settings.maxHeight = 2048;
        settings.duplicatePadding = true;
        settings.alias = false;
        settings.filterMag = Texture.TextureFilter.Linear;
        settings.filterMin = Texture.TextureFilter.Linear;
        settings.combineSubdirectories = true;

        String[] resolutions = new String[]{"_480", "_720", "_1080", "_1440"};

        for (String res : resolutions)
        {
            String path = "/home/pedja/workspace/SMC-Android/android/assets/data" + res;
            TexturePacker.process(settings, path, path, "assets");
        }
    }
}


