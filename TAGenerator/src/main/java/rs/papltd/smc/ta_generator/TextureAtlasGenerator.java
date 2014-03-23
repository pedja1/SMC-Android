package rs.papltd.smc.ta_generator;

import com.badlogic.gdx.tools.imagepacker.TexturePacker2;

/**
 * Created by pedja on 1/31/14.
 */

public class TextureAtlasGenerator
{
    public static void main(String[] args)
    {
        String path = "/home/pedja/workspace/SMC-Android/data/temp";
        TexturePacker2.process(path, path, "controls.pack");
    }
}


