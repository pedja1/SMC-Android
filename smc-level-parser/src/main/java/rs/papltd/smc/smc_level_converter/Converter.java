package rs.papltd.smc.smc_level_converter;

import org.json.JSONArray;
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

/**
 * Created by pedja on 22.6.14..
 */
public class Converter
{
    public static final String dataRoot = "/home/pedja/workspace/smc/smc/data/pixmaps/";
    public static void main(String[] args)
    {
        try
        {
            File fXmlFile = new File("/home/pedja/workspace/smc/smc/data/levels/menu_green_1.smclvl");

            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            // create a SAXXMLHandler
            SAXXMLHandler saxHandler = new SAXXMLHandler();
            // store handler in XMLReader
            xmlReader.setContentHandler(saxHandler);
            // the process starts
            xmlReader.parse(new InputSource(new FileInputStream(fXmlFile)));
            Level level = saxHandler.level;

            String levelJson = convertToJson(level);
            //System.out.println(levelJson);
			PrintWriter writer = new PrintWriter("/home/pedja/workspace/.smc/app/src/main/assets/data/levels/main_menu.smclvl", "UTF-8");
			writer.print(levelJson);
			writer.close();

        }
        catch (SAXException | ParserConfigurationException | IOException e)
        {
            e.printStackTrace();
        }
    }


    private static String convertToJson(Level level)
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
        background.put("r_1", level.backgrounds.get(0).color1_red);
        background.put("g_1", level.backgrounds.get(0).color1_green);
        background.put("b_1", level.backgrounds.get(0).color1_blue);
        background.put("r_2", level.backgrounds.get(0).color2_red);
        background.put("g_2", level.backgrounds.get(0).color2_green);
        background.put("b_2", level.backgrounds.get(0).color2_blue);
        background.put("texture_name", level.backgrounds.get(1).image);
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
                if(sprite.hasFlipData)
                {
                    JSONObject flipData = new JSONObject();
                    flipData.put("flip_x", sprite.flipX);
                    flipData.put("flip_y", sprite.flipY);
                    jSprite.put("flip_data", flipData);
                }
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
                jEnemy.put("obj_class", "enemy");
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

        JSONArray collBodies = new JSONArray();
        JSONObject body = new JSONObject();
        body.put("posx", 0);
        body.put("posy", -1);
        body.put("width", level.settings.width);
        body.put("height", 1);
        collBodies.put(body);
        body = new JSONObject();
        body.put("posx", 0);
        body.put("posy", level.settings.height + 1);
        body.put("width", level.settings.width);
        body.put("height", 1);
        collBodies.put(body);
        body = new JSONObject();
        body.put("posx", -1);
        body.put("posy", 0);
        body.put("width", 1);
        body.put("height", level.settings.height);
        collBodies.put(body);
        body = new JSONObject();
        body.put("posx", level.settings.width);
        body.put("posy", 0);
        body.put("width", 1);
        body.put("height", level.settings.height);
        collBodies.put(body);

        jsonLevel.put("collision_bodies", collBodies);

        return jsonLevel.toString();
    }
}
