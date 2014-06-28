package rs.papltd.smc.smc_level_converter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by pedja on 22.6.14..
 */
public class Converter
{
    private static final String dataRoot = "/home/pedja/workspace/SMC/smc/data/pixmaps/";
    public static void main(String[] args)
    {
        try
        {
            Level level = new Level();
            File fXmlFile = new File("/home/pedja/workspace/SMC-Android/app/src/main/assets/data/levels/lvl_1.smclvl");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            parseSettings(level, doc);
            parseBoxes(level, doc);
            parseEnemies(level, doc);
            parseEnemyStoppers(level, doc);
            parseItems(level, doc);
            parseLevelExits(level, doc);
            parsePlayer(level, doc);
            parseSprite(level, doc);
            parseBackgrounds(level, doc);

            fixSettings(level.settings);
            fixBoxes(level.boxes);
            fixEnemies(level.enemies);
            //fixEnemyStoppers(level.enemyStoppers);
            fixItems(level.items);
            //fixLevelExits(level.lelveExits);
            fixPlayer(level.player);
            fixSprites(level.sprites);
            fixBackground(level.backgrounds);
            //TODO fix background and everithing elese

            String levelJson = convertToJson(level);
            System.out.println("");

        }
        catch (SAXException | ParserConfigurationException | IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void fixBackground(List<Background> backgrounds)
    {
        for(Background background : backgrounds)
        {
            if(background.image != null)
                background.image = "data/" + background.image;
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

        JSONObject player = new JSONObject();
        player.put("posx", level.player.posx);
        player.put("posy", level.player.posy);
        jsonLevel.put("player", player);

        JSONArray sprites = new JSONArray();
        for(Sprite sprite : level.sprites)
        {
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
            sprites.put(jSprite);
        }
        jsonLevel.put("sprites", sprites);

        JSONArray enemies = new JSONArray();
        for(Enemy enemy : level.enemies)
        {
            JSONObject jEnemy = new JSONObject();
            jEnemy.put("posx", enemy.posx);
            jEnemy.put("posy", enemy.posy);
            jEnemy.put("width", enemy.width);
            jEnemy.put("height", enemy.height);
            jEnemy.put("texture_atlas", enemy.texture_atlas);
            jEnemy.put("enemy_class", enemy.type);
            enemies.put(jEnemy);
        }
        jsonLevel.put("enemies", enemies);

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

    private static void fixSprites(List<Sprite> sprites)
    {
        for(Sprite sprite : sprites)
        {
            sprite.posx = sprite.posx / 64f;
            if(sprite.image.startsWith("pipes"))
            {
                if(sprite.image.contains("blue"))
                {
                    sprite.texture_atlas = sprite.image.contains("small") ? "data/pipes/blue/small.pack" : "data/pipes/blue/blue.pack";
                }
                else if(sprite.image.contains("green"))
                {
                    sprite.texture_atlas = sprite.image.contains("small") ? "data/pipes/green/small.pack" : "data/pipes/green/green.pack";
                }
                else if(sprite.image.contains("grey"))
                {
                    sprite.texture_atlas = sprite.image.contains("small") ? "data/pipes/grey/small.pack" : "data/pipes/grey/grey.pack";
                }
                else if(sprite.image.contains("orange"))
                {
                    sprite.texture_atlas = sprite.image.contains("small") ? "data/pipes/orange/small.pack" : "data/pipes/orange/orange.pack";
                }
                else if(sprite.image.contains("yellow"))
                {
                    sprite.texture_atlas = sprite.image.contains("small") ? "data/pipes/orange/yellow.pack" : "data/pipes/yellow/yellow.pack";
                }
                sprite.texture_name = sprite.texture_atlas + ":" + sprite.image.substring(sprite.image.lastIndexOf("/") + 1, sprite.image.lastIndexOf("."));
            }
            else if(sprite.image.contains("box/yellow"))
            {
                //TODO fix this and everything else
            }
            else if(sprite.image.contains("plant_l"))
            {
                sprite.texture_name = "data/" + sprite.image.replace("plant_l", "plant_r");
                sprite.hasFlipData = true;
                sprite.flipX = true;
            }
            else if(sprite.image.contains("top/right") || sprite.image.contains("middle/right"))
            {
                sprite.texture_name = "data/" + sprite.image.replace("right", "left");
                sprite.hasFlipData = true;
                sprite.flipX = true;
            }
            else if(sprite.image.contains("1_ending_left"))
            {
                sprite.texture_name = "data/" + sprite.image.replace("_left", "");
                sprite.hasFlipData = true;
                sprite.flipX = true;
            }
            else
            {
                sprite.texture_name = "data/" + sprite.image;
            }
            setSpriteSettings(sprite);
        }
    }

    private static void fixPlayer(Player player)
    {
        player.posx = player.posx / 64f;
        player.posy = convertY(player.posy, 64f);
    }

    private static void fixItems(List<Item> items)
    {
        for(Item item : items)
        {
            item.posx = item.posx / 64f;
            item.posy = Math.abs(item.posy / 64f);
            if("goldpiece".equals(item.type))
            {
                item.width = item.height = 0.59375f;
                item.texture_atlas = "data/game/items/goldpiece/" + item.color + ".pack";
            }
            //TODO other item types
        }
    }

    private static void fixEnemies(List<Enemy> enemies)
    {
        for (Enemy enemy : enemies)
        {
            if("eato".equals(enemy.type))
            {
                enemy.texture_atlas = "data/" + enemy.image_dir.substring(0, enemy.image_dir.length() - 1) + ".pack";
                enemy.posx = enemy.posx / 64;
                setEnemySettings(enemy, "1.settings");
            }
            else if("flyon".equals(enemy.type))
            {
                enemy.texture_atlas = "data/" + enemy.image_dir.substring(0, enemy.image_dir.length() - 1) + ".pack";
                enemy.posx = enemy.posx / 64;
                setEnemySettings(enemy, "closed_1.settings");
                enemy.max_distance = enemy.max_distance / 64;
                enemy.speed = enemy.speed / 64f;
            }
            else if("furball".equals(enemy.type))
            {
                enemy.texture_atlas = "data/enemy/furball/" + enemy.color + ".pack";
                enemy.image_dir = "enemy/furball/" + enemy.color + "/";
                enemy.posx = enemy.posx / 64;
                setEnemySettings(enemy, "dead.settings");
            }
            else if("gee".equals(enemy.type))
            {
                //TODO do the rest of the enemies
            }
            else if("krush".equals(enemy.type))
            {

            }
            else if("rokko".equals(enemy.type))
            {

            }
            else if("spika".equals(enemy.type))
            {

            }
            else if("spikeball".equals(enemy.type))
            {

            }
            else if("thromp".equals(enemy.type))
            {

            }
            else if("turtle".equals(enemy.type))
            {
                enemy.texture_atlas = "data/enemy/turtle/" + enemy.color + ".pack";
                enemy.image_dir = "enemy/turtle/" + enemy.color + "/";
                enemy.posx = enemy.posx / 64;
                setEnemySettings(enemy, "walk_1.settings");
            }
        }
    }

    private static void setEnemySettings(Enemy enemy, String fileName)
    {
        File settings = new File(dataRoot, enemy.image_dir);
        settings = new File(settings, fileName);
        String settingsData = readFileContents(settings);
        String[] lines = settingsData.split("\n");
        float origHeight = 1;
        for(String s : lines)
        {
            String[] data = s.split(" ");
            if("width".equals(data[0]))
            {
                enemy.width = Float.parseFloat(data[1]) / 64f;
            }
            else if("height".equals(data[0]))
            {
                origHeight = Float.parseFloat(data[1]);
                enemy.height =  origHeight / 64f;
            }
        }
        enemy.posy = convertY(enemy.posy, origHeight);
        System.out.println("");
    }

    private static void setSpriteSettings(Sprite sprite)
    {
        String fileName = sprite.image.replaceAll("png", "settings");
        File settings = new File(dataRoot, fileName);
        String settingsData = readFileContents(settings);
        String[] lines = settingsData.split("\n");
        float origHeight = 1;
        for(String s : lines)
        {
            String[] data = s.split(" ");
            if("width".equals(data[0]))
            {
                sprite.width = Float.parseFloat(data[1]) / 64f;
            }
            else if("height".equals(data[0]))
            {
                origHeight = Float.parseFloat(data[1]);
                sprite.height = origHeight / 64f;
            }
        }
        sprite.posy = convertY(sprite.posy, origHeight);
        System.out.println("");
    }


    private static String readFileContents(File file)
    {
        try(BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null)
            {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static void fixBoxes(List<Box> boxes)
    {
        for(Box box : boxes)
        {
            box.posx = box.posx / 64;
            box.posy = convertY(box.posy, 43f);
        }
    }

    private static void fixSettings(Settings settings)
    {
        settings.height = Math.abs(settings.height / 64);
        settings.width = settings.width / 64;
        settings.music = "data/music/" + settings.music;
    }

    private static void parseSettings(Level level, Document doc)
    {
        NodeList nlSettings = doc.getElementsByTagName("settings");
        Element eSettings = (Element) nlSettings.item(0);
        NodeList nlProperty = eSettings.getElementsByTagName("property");
        for(int i = 0; i < nlProperty.getLength(); i++)
        {
            Element element = (Element)nlProperty.item(i);
            String name = element.getAttribute("name");
            String value = element.getAttribute("value");
            if("lvl_music".equals(name))
            {
                level.settings.music = value;
            }
            else if("cam_limit_w".equals(name))
            {
                level.settings.width = Integer.parseInt(value);
            }
            else if("cam_limit_h".equals(name))
            {
                level.settings.height = Integer.parseInt(value);
            }
        }
    }

    private static void parsePlayer(Level level, Document doc)
    {
        NodeList nlSettings = doc.getElementsByTagName("player");
        Element eSettings = (Element) nlSettings.item(0);
        NodeList nlProperty = eSettings.getElementsByTagName("property");
        for(int i = 0; i < nlProperty.getLength(); i++)
        {
            Element element = (Element)nlProperty.item(i);
            String name = element.getAttribute("name");
            String value = element.getAttribute("value");
            if("posx".equals(name))
            {
                level.player.posx = Float.parseFloat(value);
            }
            else if("posy".equals(name))
            {
                level.player.posy = Float.parseFloat(value);
            }
            else if("direction".equals(name))
            {
                level.player.direction = value;
            }
        }
    }


    private static void parseBoxes(Level level, Document doc)
    {
        NodeList nodeList = doc.getElementsByTagName("box");
        for (int a= 0; a< nodeList.getLength(); a++)
        {
            Element eSettings = (Element) nodeList.item(a);
            NodeList nlProperty = eSettings.getElementsByTagName("property");
            Box box = new Box();
            for(int i = 0; i < nlProperty.getLength(); i++)
            {
                Element element = (Element)nlProperty.item(i);
                String name = element.getAttribute("name");
                String value = element.getAttribute("value");
                if("posx".equals(name))
                {
                    box.posx = Float.parseFloat(value);
                }
                else if("posy".equals(name))
                {
                    box.posy = Float.parseFloat(value);
                }
                else if("type".equals(name))
                {
                    box.type = value;
                }
                else if("animation".equals(name))
                {
                    box.animation = value;
                }
                else if("item".equals(name))
                {
                    box.item = Integer.parseInt(value);
                }
                else if("invisible".equals(name))
                {
                    box.invisible = Integer.parseInt(value);
                }
                else if("useable_count".equals(name))
                {
                    box.useable_count = Integer.parseInt(value);
                }
                else if("force_best_item".equals(name))
                {
                    box.force_best_item = Integer.parseInt(value);
                }
                else if("gold_color".equals(name))
                {
                    box.gold_color = value;
                }
            }
            level.boxes.add(box);
        }
    }

    private static void parseEnemies(Level level, Document doc)
    {
        NodeList nodeList = doc.getElementsByTagName("enemy");
        for (int a= 0; a< nodeList.getLength(); a++)
        {
            Element eSettings = (Element) nodeList.item(a);
            NodeList nlProperty = eSettings.getElementsByTagName("property");
            Enemy enemy = new Enemy();
            for(int i = 0; i < nlProperty.getLength(); i++)
            {
                Element element = (Element)nlProperty.item(i);
                String name = element.getAttribute("name");
                String value = element.getAttribute("value");
                if("posx".equals(name))
                {
                    enemy.posx = Float.parseFloat(value);
                }
                else if("posy".equals(name))
                {
                    enemy.posy = Float.parseFloat(value);
                }
                else if("type".equals(name))
                {
                    enemy.type = value;
                }
                else if("image_dir".equals(name))
                {
                    enemy.image_dir = value;
                }
                else if("direction".equals(name))
                {
                    enemy.direction = value;
                }
                else if("color".equals(name))
                {
                    enemy.color = value;
                }
                else if("speed".equals(name))
                {
                    enemy.speed = Float.parseFloat(value);
                }
                else if("max_distance".equals(name))
                {
                    enemy.max_distance = Integer.parseInt(value);
                }
            }
            level.enemies.add(enemy);
        }
    }

    private static void parseItems(Level level, Document doc)
    {
        NodeList nodeList = doc.getElementsByTagName("item");
        for (int a= 0; a< nodeList.getLength(); a++)
        {
            Element eSettings = (Element) nodeList.item(a);
            NodeList nlProperty = eSettings.getElementsByTagName("property");
            Item item = new Item();
            for(int i = 0; i < nlProperty.getLength(); i++)
            {
                Element element = (Element)nlProperty.item(i);
                String name = element.getAttribute("name");
                String value = element.getAttribute("value");
                if("posx".equals(name))
                {
                    item.posx = Float.parseFloat(value);
                }
                else if("posy".equals(name))
                {
                    item.posy = Float.parseFloat(value);
                }
                else if("type".equals(name))
                {
                    item.type = value;
                }
                else if("color".equals(name))
                {
                    item.color = value;
                }
            }
            level.items.add(item);
        }
    }

    private static void parseEnemyStoppers(Level level, Document doc)
    {
        NodeList nodeList = doc.getElementsByTagName("enemystopper");
        for (int a= 0; a< nodeList.getLength(); a++)
        {
            Element eSettings = (Element) nodeList.item(a);
            NodeList nlProperty = eSettings.getElementsByTagName("property");
            EnemyStopper enemy = new EnemyStopper();
            for(int i = 0; i < nlProperty.getLength(); i++)
            {
                Element element = (Element)nlProperty.item(i);
                String name = element.getAttribute("name");
                String value = element.getAttribute("value");
                if("posx".equals(name))
                {
                    enemy.posx = Float.parseFloat(value);
                }
                else if("posy".equals(name))
                {
                    enemy.posy = Float.parseFloat(value);
                }
            }
            level.enemyStoppers.add(enemy);
        }
    }

    private static void parseLevelExits(Level level, Document doc)
    {
        NodeList nodeList = doc.getElementsByTagName("levelexit");
        for (int a= 0; a< nodeList.getLength(); a++)
        {
            Element eSettings = (Element) nodeList.item(a);
            NodeList nlProperty = eSettings.getElementsByTagName("property");
            LelveExit lelveExit = new LelveExit();
            for(int i = 0; i < nlProperty.getLength(); i++)
            {
                Element element = (Element)nlProperty.item(i);
                String name = element.getAttribute("name");
                String value = element.getAttribute("value");
                if("posx".equals(name))
                {
                    lelveExit.posx = Float.parseFloat(value);
                }
                else if("posy".equals(name))
                {
                    lelveExit.posy = Float.parseFloat(value);
                }
                else if("type".equals(name))
                {
                    lelveExit.type = Integer.parseInt(value);
                }
                else if("camera_motion".equals(name))
                {
                    lelveExit.camera_motion = Integer.parseInt(value);
                }
                else if("direction".equals(name))
                {
                    lelveExit.direction = value;
                }
                else if("level_name".equals(name))
                {
                    lelveExit.level_name = value;
                }
                else if("entry".equals(name))
                {
                    lelveExit.entry = value;
                }
            }
            level.lelveExits.add(lelveExit);
        }
    }

    private static void parseSprite(Level level, Document doc)
    {
        NodeList nodeList = doc.getElementsByTagName("sprite");
        for (int a= 0; a< nodeList.getLength(); a++)
        {
            Element eSettings = (Element) nodeList.item(a);
            NodeList nlProperty = eSettings.getElementsByTagName("property");
            Sprite sprite = new Sprite();
            for(int i = 0; i < nlProperty.getLength(); i++)
            {
                Element element = (Element)nlProperty.item(i);
                String name = element.getAttribute("name");
                String value = element.getAttribute("value");
                if("posx".equals(name))
                {
                    sprite.posx = Float.parseFloat(value);
                }
                else if("posy".equals(name))
                {
                    sprite.posy = Float.parseFloat(value);
                }
                else if("type".equals(name))
                {
                    sprite.type = value;
                }
                else if("image".equals(name))
                {
                    sprite.image = value;
                }
            }
            level.sprites.add(sprite);
        }
    }

    private static void parseBackgrounds(Level level, Document doc)
    {
        NodeList nodeList = doc.getElementsByTagName("background");
        for (int a= 0; a< nodeList.getLength(); a++)
        {
            Element eSettings = (Element) nodeList.item(a);
            NodeList nlProperty = eSettings.getElementsByTagName("property");
            Background background = new Background();
            for(int i = 0; i < nlProperty.getLength(); i++)
            {
                Element element = (Element)nlProperty.item(i);
                String name = element.getAttribute("name");
                String value = element.getAttribute("value");
                if("bg_color_1_red".equals(name))
                {
                    background.color1_red = Integer.parseInt(value);
                }
                else if("bg_color_2_red".equals(name))
                {
                    background.color2_red = Integer.parseInt(value);
                }
                else if("bg_color_1_blue".equals(name))
                {
                    background.color1_blue = Integer.parseInt(value);
                }
                else if("bg_color_2_blur".equals(name))
                {
                    background.color2_blue = Integer.parseInt(value);
                }
                if("bg_color_1_green".equals(name))
                {
                    background.color1_green = Integer.parseInt(value);
                }
                else if("bg_color_2_green".equals(name))
                {
                    background.color2_green = Integer.parseInt(value);
                }
                else if("type".equals(name))
                {
                    background.type = Integer.parseInt(value);
                }
                else if("posx".equals(name))
                {
                    background.posx = Integer.parseInt(value);
                }
                else if("posy".equals(name))
                {
                    background.posy = Integer.parseInt(value);
                }
                else if("speedx".equals(name))
                {
                    background.speedx = Float.parseFloat(value);
                }
                else if("speedy".equals(name))
                {
                    background.speedy = Float.parseFloat(value);
                }
                else if("image".equals(name))
                {
                    background.image = value;
                }
                else if("const_velx".equals(name))
                {
                    background.const_velx = Integer.parseInt(value);
                }
                else if("const_vely".equals(name))
                {
                    background.const_vely = Integer.parseInt(value);
                }
            }
            level.backgrounds.add(background);
        }
    }

    private static float convertY(float posy, float height)
    {
        return (Math.abs(posy) - height) / 64f;
    }

}
