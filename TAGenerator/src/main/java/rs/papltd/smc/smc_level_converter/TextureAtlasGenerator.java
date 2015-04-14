package rs.papltd.smc.smc_level_converter;

import com.badlogic.gdx.tools.imagepacker.TexturePacker2;

/**
 * Created by pedja on 1/31/14.
 */

public class TextureAtlasGenerator
{
    public static void main(String[] args)
    {
        String path = "/home/pedja/workspace/SMC-Android/android/assets/data/ground/mushroom_1/platform/tmp";
        TexturePacker2.process(path, path, "shaft.pack");
    }
}


