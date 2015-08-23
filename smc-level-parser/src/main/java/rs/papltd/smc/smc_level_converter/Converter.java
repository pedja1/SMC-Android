package rs.papltd.smc.smc_level_converter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import rs.papltd.smc.smc_level_converter.objects.Background;
import rs.papltd.smc.smc_level_converter.objects.Box;
import rs.papltd.smc.smc_level_converter.objects.Enemy;
import rs.papltd.smc.smc_level_converter.objects.EnemyStopper;
import rs.papltd.smc.smc_level_converter.objects.Item;
import rs.papltd.smc.smc_level_converter.objects.LevelEntry;
import rs.papltd.smc.smc_level_converter.objects.LevelExit;
import rs.papltd.smc.smc_level_converter.objects.MovingPlatform;
import rs.papltd.smc.smc_level_converter.objects.Player;
import rs.papltd.smc.smc_level_converter.objects.Sprite;

/**
 * Created by pedja on 22.6.14..
 */
public class Converter
{
    public static void main(String[] args) throws JSONException
    {
        try
        {
            File levelsFolder = new File("/home/pedja/workspace/SMC-Android/levels/levels_smc_original/levels");
            //File levelsFolder = new File("/sdcard/.AppProjects/SMC-Android/levels/levels_smc_original/levels");
			File[] files = levelsFolder.listFiles();
            for(File file : files)
            {
				if(file.isDirectory())continue;
                System.out.println("Processing: " + file.getName());
                XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
                // create a SAXXMLHandler
                SAXXMLHandler saxHandler = new SAXXMLHandler();
                // store handler in XMLReader
                xmlReader.setContentHandler(saxHandler);
                // the process starts
                FileInputStream fis = new FileInputStream(file);
                xmlReader.parse(new InputSource(fis));
                Level level = saxHandler.level;

                String levelJson = convertToJson(level);
                //System.out.println(levelJson);
                PrintWriter writer = new PrintWriter("/home/pedja/workspace/SMC-Android/android/assets/data/levels/" + file.getName(), "UTF-8");
                //PrintWriter writer = new PrintWriter("/sdcard/.AppProjects/SMC-Android/android/assets/data/levels/" + file.getName(), "UTF-8");
                writer.print(levelJson);
                writer.flush();
                writer.close();
                fis.close();
            }
			System.out.println("done");
        }
        catch (SAXException | ParserConfigurationException | IOException e)
        {
            e.printStackTrace();
        }
    }


    private static String convertToJson(Level level) throws JSONException
    {
        JSONObject jsonLevel = new JSONObject();

        JSONObject info = new JSONObject();
        info.put("level_width", level.settings.width);
        info.put("level_height", level.settings.height);
        JSONArray levelMusic = new JSONArray();
        levelMusic.put(level.settings.music);//TODO option to put more than one music
        info.put("level_music", levelMusic);
        //TODO put sounds
        jsonLevel.put("info", info);

        JSONObject background = new JSONObject();
        for(Background bg : level.backgrounds)
        {
            if(bg.type == 103)
            {
                background.put("r_1", bg.color1_red);
                background.put("g_1", bg.color1_green);
                background.put("b_1", bg.color1_blue);
                background.put("r_2", bg.color2_red);
                background.put("g_2", bg.color2_green);
                background.put("b_2", bg.color2_blue);
            }
            else if(bg.type == 1)
            {
                background.put("texture_name", bg.image);
            }
        }


        jsonLevel.put("background", background);

        JSONArray objects = new JSONArray();
        for(Object obj : level.objects)
        {
            if(obj instanceof Player)
            {
                Player player = (Player)obj;
                JSONObject jPlayer = new JSONObject();
                jPlayer.put("posx", player.posx);
                jPlayer.put("posy", player.posy);
                jPlayer.put("obj_class", "player");
                objects.put(jPlayer);
            }
            else if(obj instanceof Sprite)
            {
                Sprite sprite = (Sprite)obj;
                JSONObject jSprite = new JSONObject();
                jSprite.put("posx", sprite.posx);
                jSprite.put("posy", sprite.posy);
                jSprite.put("width", sprite.width);
                jSprite.put("height", sprite.height);
                if(sprite.texture_atlas != null)
                {
                    jSprite.put("texture_atlas", sprite.texture_atlas);
                }
                jSprite.put("texture_name", sprite.texture_name);

                jSprite.put("rotationX", sprite.rotationX);
                jSprite.put("rotationY", sprite.rotationY);
                jSprite.put("rotationZ", sprite.rotationZ);

                jSprite.put("obj_class", "sprite");
				jSprite.put("massive_type", sprite.type);
                objects.put(jSprite);
            }
            else if(obj instanceof Enemy)
            {
                Enemy enemy = (Enemy)obj;
                JSONObject jEnemy = new JSONObject();
                jEnemy.put("posx", enemy.posx);
                jEnemy.put("posy", enemy.posy);
                jEnemy.put("width", enemy.width);
                jEnemy.put("height", enemy.height);
                jEnemy.put("texture_atlas", enemy.texture_atlas);
                jEnemy.put("enemy_class", enemy.type);
                jEnemy.put("color", enemy.color);
                jEnemy.put("obj_class", "enemy");
                if("eato".equals(enemy.type))
                {
                    jEnemy.put("direction", enemy.direction);
                }
                if("furball".equals(enemy.type))
                {
                    jEnemy.put("max_downgrade_count", enemy.max_downgrade_count);
                }
                if("flyon".equals(enemy.type))
                {
                    jEnemy.put("max_distance", enemy.max_distance);
                    jEnemy.put("speed", enemy.speed);
                    jEnemy.put("direction", enemy.direction);
                }
                if("gee".equals(enemy.type))
                {
                    jEnemy.put("max_distance", enemy.max_distance);
                    jEnemy.put("fly_distance", enemy.flyDistance);
                    jEnemy.put("wait_time", enemy.waitTime);
                    jEnemy.put("direction", enemy.direction);
                }
                if("thromp".equals(enemy.type))
                {
                    jEnemy.put("max_distance", enemy.max_distance);
                    jEnemy.put("speed", enemy.speed);
                    jEnemy.put("direction", enemy.direction);
                }
                if("spika".equals(enemy.type))
                {
                    jEnemy.put("texture_name", enemy.texture_name);
                }
                if("rokko".equals(enemy.type))
                {
                    jEnemy.put("texture_name", enemy.texture_name);
                    jEnemy.put("direction", enemy.direction);
                }
                if("static".equals(enemy.type))
                {
                    jEnemy.put("texture_name", enemy.texture_name);
                    jEnemy.put("rotation_speed", enemy.rotationSpeed);
                    jEnemy.put("fire_resistance", enemy.fireResistance);
                    jEnemy.put("ice_resistance", enemy.iceResistance);
                }
                /*if(enemy.colRect != null)
                {
                    jEnemy.put("col_x", enemy.colRect.x);
                    jEnemy.put("col_y", enemy.colRect.y);
                    jEnemy.put("col_width", enemy.colRect.width);
                    jEnemy.put("col_height", enemy.colRect.height);
                }*/
                objects.put(jEnemy);
            }
            else if(obj instanceof Box)
            {
                Box box = (Box)obj;
                JSONObject jBox = new JSONObject();
                jBox.put("posx", box.posx);
                jBox.put("posy", box.posy);
                jBox.put("type", box.type);
                jBox.put("animation", box.animation);
                jBox.put("gold_color", box.gold_color);
                jBox.put("text", box.text);
                jBox.put("texture_name", box.texture_name);
                jBox.put("texture_atlas", box.texture_atlas);
                jBox.put("item", box.item);
                jBox.put("invisible", box.invisible);
                jBox.put("useable_count", box.useable_count);
                jBox.put("force_best_item", box.force_best_item);
                jBox.put("obj_class", "box");
                /*if(box.colRect != null)
                {
                    jBox.put("col_x", box.colRect.x);
                    jBox.put("col_y", box.colRect.y);
                    jBox.put("col_width", box.colRect.width);
                    jBox.put("col_height", box.colRect.height);
                }*/
                objects.put(jBox);
            }
            else if(obj instanceof EnemyStopper)
            {
                EnemyStopper stopper = (EnemyStopper)obj;
                JSONObject jStopper = new JSONObject();
                jStopper.put("posx", stopper.posx);
                jStopper.put("posy", stopper.posy);
                jStopper.put("width", stopper.width);
                jStopper.put("height", stopper.height);
                jStopper.put("obj_class", "enemy_stopper");
                objects.put(jStopper);
            }
            else if(obj instanceof Item)
            {
                Item item = (Item)obj;
                JSONObject jItem = new JSONObject();
                jItem.put("type", item.type);
                jItem.put("color", item.color);
                jItem.put("texture_atlas", item.texture_atlas);
                jItem.put("texture_name", item.texture_name);
                jItem.put("posx", item.posx);
                jItem.put("posy", item.posy);
                jItem.put("width", item.width);
                jItem.put("height", item.height);
                jItem.put("mushroom_type", item.mushroom_type);
                jItem.put("obj_class", "item");
                /*if(item.colRect != null)
                {
                    jItem.put("col_x", item.colRect.x);
                    jItem.put("col_y", item.colRect.y);
                    jItem.put("col_width", item.colRect.width);
                    jItem.put("col_height", item.colRect.height);
                }*/
                objects.put(jItem);
            }
            else if(obj instanceof LevelEntry)
            {
/*LEVEL_ENTRY_BEAM		= 0,	// no animation ( f.e. a door or hole )
	LEVEL_ENTRY_WARP		= 1		// rotated player moves slowly into the destination direction*/
                LevelEntry entry = (LevelEntry)obj;
                JSONObject jEntry = new JSONObject();
                jEntry.put("posx", entry.posx);
                jEntry.put("posy", entry.posy);
                jEntry.put("width", entry.w);
                jEntry.put("height", entry.h);
                jEntry.put("type", entry.type);
                jEntry.put("name", entry.name);
                jEntry.put("direction", entry.direction);
                jEntry.put("obj_class", "level_entry");
                objects.put(jEntry);
            }
            else if(obj instanceof LevelExit)
            {
                /*LEVEL_EXIT_BEAM = 0,	// no animation ( f.e. a door or hole )
	LEVEL_EXIT_WARP = 1		// rotated player moves slowly into the destination direction*/
                LevelExit exit = (LevelExit)obj;
                JSONObject jExit = new JSONObject();
                jExit.put("posx", exit.posx);
                jExit.put("posy", exit.posy);
                jExit.put("width", exit.width);
                jExit.put("height", exit.height);
                jExit.put("type", exit.type);
                jExit.put("camera_motion", exit.camera_motion);
                jExit.put("level_name", exit.level_name);
                jExit.put("entry", exit.entry);
                jExit.put("direction", exit.direction);
                jExit.put("obj_class", "level_exit");
                objects.put(jExit);
            }
            else if(obj instanceof MovingPlatform)
            {
                MovingPlatform platform = (MovingPlatform)obj;
                JSONObject jPlatform = new JSONObject();
                /*public float posx, posy, max_distance, speed, touch_time, shake_time, touch_move_time, width, height;
    public int move_type, middle_img_count;
    public String massive_type, direction, image_top_left, image_top_middle, image_top_right, texture_atlas;*/
                jPlatform.put("posx", platform.posx);
                jPlatform.put("posy", platform.posy);
                jPlatform.put("max_distance", platform.max_distance);
                jPlatform.put("speed", platform.speed);
                jPlatform.put("touch_time", platform.touch_time);
                jPlatform.put("shake_time", platform.shake_time);
                jPlatform.put("touch_move_time", platform.touch_move_time);
                jPlatform.put("width", platform.width);
                jPlatform.put("height", platform.height);
                jPlatform.put("move_type", platform.move_type);
                jPlatform.put("middle_img_count", platform.middle_img_count);
                jPlatform.put("massive_type", platform.massive_type);
                jPlatform.put("direction", platform.direction);
                //jPlatform.put("image_top_left", platform.image_top_left);
                //jPlatform.put("image_top_middle", platform.image_top_middle);
                //jPlatform.put("image_top_right", platform.image_top_right);
                jPlatform.put("texture_atlas", platform.texture_atlas);
                jPlatform.put("obj_class", "moving_platform");
                objects.put(jPlatform);
            }
            //TODO other objects
        }
        jsonLevel.put("objects", objects);

        return jsonLevel.toString();
    }
}
