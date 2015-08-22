package rs.papltd.smc.smc_level_converter;

import com.badlogic.gdx.tools.imagepacker.TexturePacker2;

/**
 * Created by pedja on 1/31/14.
 */

public class TextureAtlasGenerator
{
    public static void main(String[] args)
    {
        TexturePacker2.Settings settings = new TexturePacker2.Settings();
        settings.maxWidth = 1024;
        settings.maxHeight = 1024;
        settings.duplicatePadding = true;
        String path = "/home/pedja/workspace/SMC-Android/android/assets/data/pipes/yellow/tmp";
        TexturePacker2.process(settings, path, path, "small.pack");
    }
}


