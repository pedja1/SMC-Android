package rs.papltd.smc.smc_level_converter;

import com.badlogic.gdx.tools.imagepacker.TexturePacker2;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by pedja on 1/31/14.
 */

public class TextureAtlasGenerator
{
    static boolean delete = true;
    static TexturePacker2.Settings settings;
    public static void main(String[] args) throws IOException
    {
        settings = new TexturePacker2.Settings();
        settings.maxWidth = 1024;
        settings.maxHeight = 1024;
        settings.duplicatePadding = true;
        int[] resolutionHeights = new int[]{1080, 768, 540};

        for(int resolutionHeight : resolutionHeights)
        {
            /*fireball(resolutionHeight);
            light(resolutionHeight);
            pipe(resolutionHeight);
            clouds(resolutionHeight);
            box(resolutionHeight);
            gold(resolutionHeight);
            fp_moon(resolutionHeight);
            desert_bones(resolutionHeight);
            chest(resolutionHeight);
            brown_slider(resolutionHeight);
            baloon_tree(resolutionHeight);
            tendril(resolutionHeight);
            jungle_slider(resolutionHeight);
            jungle(resolutionHeight);
            mushroom(resolutionHeight);
            plastic(resolutionHeight);
            tendril_sand(resolutionHeight);
            snow_platform(resolutionHeight);
            snow_baloon(resolutionHeight);
            underground(resolutionHeight);
            hills(resolutionHeight);
            maryo(resolutionHeight);
            pipes(resolutionHeight);
            turtleboss(resolutionHeight);
            eato(resolutionHeight);
            flyon(resolutionHeight);
            thromp(resolutionHeight);
            furball(resolutionHeight);
            gee(resolutionHeight);
            krush(resolutionHeight);
            spikeball(resolutionHeight);
            kplant(resolutionHeight);*/
            turtle(resolutionHeight);
        }

    }

    static void fireball(int resolutionHeight) throws IOException
    {
        String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/animation/fireball";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/animation/", "fireball.pack");
        if(delete)if(delete)FileUtils.deleteDirectory(new File(path));
    }

    static void light(int resolutionHeight) throws IOException
    {
        String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/animation/light_1";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/animation/", "light.pack");
        if(delete)FileUtils.deleteDirectory(new File(path));
    }

    static void pipe(int resolutionHeight) throws IOException
    {
        String[] states = new String[]{"blue", "green", "orange", "red"};
        for(String state : states)
        {
            String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/blocks/pipe/connection/plastic_1/" + state;
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/blocks/pipe/connection/plastic_1/" + state, state + ".pack");
            if(delete)FileUtils.deleteDirectory(new File(path));
        }
    }

    static void clouds(int resolutionHeight) throws IOException
    {
        String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/clouds/lightblue_1";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/clouds/lightblue_1", "lightblue-1.pack");
        if(delete)FileUtils.deleteDirectory(new File(path));
    }

    static void box(int resolutionHeight) throws IOException
    {
        String[] states = new String[]{"bonus", "spin", "power"};
        for(String state : states)
        {
            String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/game/box/yellow/" + state;
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/game/box/yellow/", state + ".pack");
            if(delete)FileUtils.deleteDirectory(new File(path));
        }
    }

    static void gold(int resolutionHeight) throws IOException
    {
        String[] states = new String[]{"red", "yellow"};
        for(String state : states)
        {
            String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/game/items/goldpiece/" + state;
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/game/items/goldpiece/", state + ".pack");
            if(delete)FileUtils.deleteDirectory(new File(path));
        }
    }

    static void fp_moon(int resolutionHeight) throws IOException
    {
        String[] states = new String[]{"fireplant", "moon"};
        for(String state : states)
        {
            String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/game/items/" + state;
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/game/items/", state + ".pack");
            if(delete)FileUtils.deleteDirectory(new File(path));
        }
    }

    static void desert_bones(int resolutionHeight) throws IOException
    {
        String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/ground/desert_1/bones";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/ground/desert_1/", "bones.pack");
        if(delete)FileUtils.deleteDirectory(new File(path));
    }

    static void chest(int resolutionHeight) throws IOException
    {
        String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/ground/green_1/chest";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/ground/green_1/", "chest.pack");
        if(delete)FileUtils.deleteDirectory(new File(path));
    }

    static void brown_slider(int resolutionHeight) throws IOException
    {
        String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/ground/green_1/slider";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/ground/green_1/slider/", "brown.pack");
        if(delete)FileUtils.deleteDirectory(new File(path));
    }

    static void baloon_tree(int resolutionHeight) throws IOException
    {
        String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/ground/green_2/trees/balloon_tree";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/ground/green_2/", "balloon_tree.pack");
        if(delete)FileUtils.deleteDirectory(new File(path));
    }

    static void tendril(int resolutionHeight) throws IOException
    {
        String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/ground/green_2/tendril";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/ground/green_2/", "tendril.pack");
        if(delete)FileUtils.deleteDirectory(new File(path));
    }

    static void kplant(int resolutionHeight) throws IOException
    {
        String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/ground/green_1/kplant";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/ground/green_1/", "kplant.pack");
        if(delete)FileUtils.deleteDirectory(new File(path));
    }

    static void jungle_slider(int resolutionHeight) throws IOException
    {
        String[] states = new String[]{"blue", "brown", "green"};
        for(String state : states)
        {
            String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/ground/jungle_1/slider/" + state;
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/ground/jungle_1/slider/", state + ".pack");
            if(delete)FileUtils.deleteDirectory(new File(path));
        }
    }

    static void jungle(int resolutionHeight) throws IOException
    {
        String[] states = new String[]{"bridge", "vine", "kplant"};
        for(String state : states)
        {
            String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/ground/jungle_1/" + state;
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/ground/jungle_1/", state + ".pack");
            if(delete)FileUtils.deleteDirectory(new File(path));
        }
    }

    static void mushroom(int resolutionHeight) throws IOException
    {
        String[] states = new String[]{"blue", "gold", "red", "green", "shaft"};
        for(String state : states)
        {
            String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/ground/mushroom_1/platform/" + state;
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/ground/mushroom_1/platform/", state + ".pack");
            if(delete)FileUtils.deleteDirectory(new File(path));
        }
    }

    static void plastic(int resolutionHeight) throws IOException
    {
        String[] states = new String[]{"screw_block_blue", "screw_block_green", "screw_block_grey", "screw_block_red"};
        for(String state : states)
        {
            String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/ground/plastic_1/" + state;
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/ground/plastic_1/", state + ".pack");
            if(delete)FileUtils.deleteDirectory(new File(path));
        }
    }

    static void tendril_sand(int resolutionHeight) throws IOException
    {
        String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/ground/sand_1/tendril";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/ground/sand_1/", "tendril.pack");
        if(delete)FileUtils.deleteDirectory(new File(path));
    }

    static void snow_platform(int resolutionHeight) throws IOException
    {
        String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/ground/snow_1/platform/1_blue";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/ground/snow_1/platform/", "1_blue.pack");
        if(delete)FileUtils.deleteDirectory(new File(path));
    }

    static void snow_baloon(int resolutionHeight) throws IOException
    {
        String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/ground/snow_1/trees/balloon";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/ground/snow_1/trees/", "balloon.pack");
        if(delete)FileUtils.deleteDirectory(new File(path));
    }

    static void underground(int resolutionHeight) throws IOException
    {
        String[] states = new String[]{"cain", "rope"};
        for(String state : states)
        {
            String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/ground/underground/" + state;
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/ground/underground/", state + ".pack");
            if(delete)FileUtils.deleteDirectory(new File(path));
        }
    }

    static void hills(int resolutionHeight) throws IOException
    {
        String[] states = new String[]{"green_1", "light_blue_1"};
        for(String state : states)
        {
            String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/hills/" + state;
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/hills/", state + ".pack");
            if(delete)FileUtils.deleteDirectory(new File(path));
        }
    }

    static void maryo(int resolutionHeight) throws IOException
    {
        String[] states = new String[]{"big", "fire", "ghost", "ice", "small"};
        for(String state : states)
        {
            String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/maryo/" + state;
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/maryo/", state + ".pack");
            if(delete)FileUtils.deleteDirectory(new File(path));
        }
    }

    static void pipes(int resolutionHeight) throws IOException
    {
        String[] states = new String[]{"blue", "green", "grey", "orange", "yellow"};
        for(String state : states)
        {
            String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/pipes/" + state + "/" + state;
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/pipes/" + state, state + ".pack");
            if(delete)FileUtils.deleteDirectory(new File(path));

            path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/pipes/" + state + "/small";
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/pipes/" + state, "small.pack");

            if(delete)FileUtils.deleteDirectory(new File(path));
        }
    }

    static void turtleboss(int resolutionHeight) throws IOException
    {
        String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/enemy/bosses/turtle";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/enemy/bosses/", "turtle.pack");
        if(delete)FileUtils.deleteDirectory(new File(path));
    }

    static void eato(int resolutionHeight) throws IOException
    {
        String[] states = new String[]{"brown", "green"};
        for(String state : states)
        {
            String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/enemy/eato/" + state;
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/enemy/eato/", state + ".pack");
            if(delete)FileUtils.deleteDirectory(new File(path));
        }
    }

    static void flyon(int resolutionHeight) throws IOException
    {
        String[] states = new String[]{"blue", "orange"};
        for(String state : states)
        {
            String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/enemy/flyon/" + state;
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/enemy/flyon/", state + ".pack");
            if(delete)FileUtils.deleteDirectory(new File(path));
        }
    }

    static void furball(int resolutionHeight) throws IOException
    {
        String[] states = new String[]{"blue", "boss", "brown"};
        for(String state : states)
        {
            String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/enemy/furball/" + state;
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/enemy/furball/", state + ".pack");
            if(delete)FileUtils.deleteDirectory(new File(path));
        }
    }

    static void gee(int resolutionHeight) throws IOException
    {
        String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/enemy/gee/electro";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/enemy/gee/", "yellow.pack");
        if(delete)FileUtils.deleteDirectory(new File(path));

        path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/enemy/gee/lava";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/enemy/gee/", "red.pack");
        if(delete)FileUtils.deleteDirectory(new File(path));

        path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/enemy/gee/venom";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/enemy/gee/", "green.pack");
        if(delete)FileUtils.deleteDirectory(new File(path));
    }

    static void krush(int resolutionHeight) throws IOException
    {
        String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/enemy/krush";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/enemy/krush/", "krush.pack");
        if(delete)FileUtils.deleteDirectory(new File(path));
    }

    static void spikeball(int resolutionHeight) throws IOException
    {
        String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/enemy/spikeball/grey";
        TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/enemy/spikeball/", "grey.pack");
        if(delete)FileUtils.deleteDirectory(new File(path));
    }

    static void thromp(int resolutionHeight) throws IOException
    {
        String[] states = new String[]{"thromp", "desert"};
        for(String state : states)
        {
            String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/enemy/thromp/" + state;
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/enemy/thromp/", state + ".pack");
            if(delete)FileUtils.deleteDirectory(new File(path));
        }
    }

    static void turtle(int resolutionHeight) throws IOException
    {
        String[] states = new String[]{"green", "red"};
        for(String state : states)
        {
            String path = "/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps_" + resolutionHeight + "/enemy/turtle/" + state;
            TexturePacker2.process(settings, path, "/home/pedja/workspace/SMC-Android/android/assets/data_" + resolutionHeight + "/enemy/turtle/", state + ".pack");
            if(delete)FileUtils.deleteDirectory(new File(path));
        }
    }
}


