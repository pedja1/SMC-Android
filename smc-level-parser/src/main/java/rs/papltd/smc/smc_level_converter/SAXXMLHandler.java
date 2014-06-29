package rs.papltd.smc.smc_level_converter;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by pedja on 11/1/13.
 */
public class SAXXMLHandler extends DefaultHandler
{

    public Level level;
    private Background tmpBackground;
    private Box tmpBox;
    private Enemy tmpEnemy;
    private EnemyStopper tmpStopper;
    private Item tmpItem;
    private LevelExit tmpExit;
    private LevelEntry tmpEntry;
    private Player tmpPlayer;
    private Settings tmpSettings;
    private Sprite tmpSprite;
    private MovingPlatform tmpPlatform;

    public SAXXMLHandler()
    {
        level = new Level();
    }

    // Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        switch (qName)
        {
            case "background":
                tmpBackground = new Background();
                break;
            case "box":
                tmpBox = new Box();
                break;
            case "enemy":
                tmpEnemy = new Enemy();
                break;
            case "enemystopper":
                tmpStopper = new EnemyStopper();
                break;
            case "item":
                tmpItem = new Item();
                break;
            case "levelexit":
                tmpExit = new LevelExit();
                break;
            case "level_entry":
                tmpEntry = new LevelEntry();
                break;
            case "player":
                tmpPlayer = new Player();
                break;
            case "settings":
                tmpSettings = new Settings();
                break;
            case "sprite":
                tmpSprite = new Sprite();
                break;
            case "moving_platform":
                tmpPlatform = new MovingPlatform();
                break;
            case "property":
                setPropertyToElement(attributes);
                break;
            case "level":
                //do nothing, level is our main tag
            case "information":
            case "particle_emitter":
            case "path":
                //we don't need this elements
                break;
            default:
                throw new IllegalArgumentException("Unknown element found (" + qName + ")");
        }
    }

    private void setPropertyToElement(Attributes attributes)
    {
        if(tmpBackground != null)
        {
            setBackgroundAttributes(attributes);
        }
        else if(tmpSprite != null)
        {
            setSpriteAttributes(attributes);
        }
        else if(tmpSettings != null)
        {
            setSettingsAttributes(attributes);
        }
        else if(tmpPlayer != null)
        {
            setPlayerAttributes(attributes);
        }
        else if(tmpExit != null)
        {
            setExitAttributes(attributes);
        }
        else if(tmpEntry != null)
        {
            setEntryAttributes(attributes);
        }
        else if(tmpItem != null)
        {
            setItemAttributes(attributes);
        }
        else if(tmpStopper != null)
        {
            setStopperAttributes(attributes);
        }
        else if(tmpBox != null)
        {
            setBoxAttributes(attributes);
        }
        else if(tmpEnemy != null)
        {
            setEnemyAttributes(attributes);
        }
        else if(tmpPlatform != null)
        {
            setPlatformAttributes(attributes);
        }
        else
        {
            //throw new IllegalArgumentException("All objects are null");
            //System.out.println("Warning: all objects are null, probably skipped element");
        }
    }

    private void setPlatformAttributes(Attributes attributes)
    {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        switch (name)
        {
            case "image_top_middle":
                tmpPlatform.image_top_middle = value;
                break;
            case "image_top_right":
                tmpPlatform.image_top_right = value;
                break;
            case "image_top_left":
                tmpPlatform.image_top_left = value;
                break;
            case "direction":
                tmpPlatform.direction = value;
                break;
            case "massive_type":
                tmpPlatform.massive_type = value;
                break;
            case "middle_img_count":
                tmpPlatform.middle_img_count = Integer.parseInt(value);
                break;
            case "move_type":
                tmpPlatform.move_type = Integer.parseInt(value);
                break;
            case "posx":
                tmpPlatform.posx = Float.parseFloat(value);
                break;
            case "posy":
                tmpPlatform.posy = Float.parseFloat(value);
                break;
            case "max_distance":
                tmpPlatform.max_distance = Float.parseFloat(value);
                break;
            case "speed":
                tmpPlatform.speed = Float.parseFloat(value);
                break;
            case "touch_time":
                tmpPlatform.touch_time = Float.parseFloat(value);
                break;
            case "shake_time":
                tmpPlatform.shake_time = Float.parseFloat(value);
                break;
            case "touch_move_time":
                tmpPlatform.touch_move_time = Float.parseFloat(value);
                break;
        }
    }

    private void setEntryAttributes(Attributes attributes)
    {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        if("posx".equals(name))
        {
            tmpEntry.posx = Float.parseFloat(value);
        }
        else if("posy".equals(name))
        {
            tmpEntry.posy = Float.parseFloat(value);
        }
        else if("type".equals(name))
        {
            tmpEntry.type = Integer.parseInt(value);
        }
        else if("direction".equals(name))
        {
            tmpEntry.direction = value;
        }
        else if("name".equals(name))
        {
            tmpEntry.name = value;
        }
    }


    private void setExitAttributes(Attributes attributes)
    {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        if("posx".equals(name))
        {
            tmpExit.posx = Float.parseFloat(value);
        }
        else if("posy".equals(name))
        {
            tmpExit.posy = Float.parseFloat(value);
        }
        else if("type".equals(name))
        {
            tmpExit.type = Integer.parseInt(value);
        }
        else if("camera_motion".equals(name))
        {
            tmpExit.camera_motion = Integer.parseInt(value);
        }
        else if("direction".equals(name))
        {
            tmpExit.direction = value;
        }
        else if("level_name".equals(name))
        {
            tmpExit.level_name = value;
        }
        else if("entry".equals(name))
        {
            tmpExit.entry = value;
        }
    }

    private void setStopperAttributes(Attributes attributes)
    {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        if("posx".equals(name))
        {
            tmpStopper.posx = Float.parseFloat(value);
        }
        else if("posy".equals(name))
        {
            tmpStopper.posy = Float.parseFloat(value);
        }
    }

    private void setItemAttributes(Attributes attributes)
    {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        if("posx".equals(name))
        {
            tmpItem.posx = Float.parseFloat(value);
        }
        else if("posy".equals(name))
        {
            tmpItem.posy = Float.parseFloat(value);
        }
        else if("type".equals(name))
        {
            tmpItem.type = value;
        }
        else if("color".equals(name))
        {
            tmpItem.color = value;
        }
        else if("mushroom_type".equals(name))
        {
            tmpItem.mushroom_type = Integer.parseInt(value);
        }
    }

    private void setEnemyAttributes(Attributes attributes)
    {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        if("posx".equals(name))
        {
            tmpEnemy.posx = Float.parseFloat(value);
        }
        else if("posy".equals(name))
        {
            tmpEnemy.posy = Float.parseFloat(value);
        }
        else if("type".equals(name))
        {
            tmpEnemy.type = value;
        }
        else if("image_dir".equals(name))
        {
            tmpEnemy.image_dir = value;
        }
        else if("direction".equals(name))
        {
            tmpEnemy.direction = value;
        }
        else if("color".equals(name))
        {
            tmpEnemy.color = value;
        }
        else if("speed".equals(name))
        {
            tmpEnemy.speed = Float.parseFloat(value);
        }
        else if("max_distance".equals(name))
        {
            tmpEnemy.max_distance = Integer.parseInt(value);
        }
    }

    private void setBoxAttributes(Attributes attributes)
    {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        if("posx".equals(name))
        {
            tmpBox.posx = Float.parseFloat(value);
        }
        else if("posy".equals(name))
        {
            tmpBox.posy = Float.parseFloat(value);
        }
        else if("type".equals(name))
        {
            tmpBox.type = value;
        }
        else if("animation".equals(name))
        {
            tmpBox.animation = value;
        }
        else if("item".equals(name))
        {
            tmpBox.item = Integer.parseInt(value);
        }
        else if("invisible".equals(name))
        {
            tmpBox.invisible = Integer.parseInt(value);
        }
        else if("useable_count".equals(name))
        {
            tmpBox.useable_count = Integer.parseInt(value);
        }
        else if("force_best_item".equals(name))
        {
            tmpBox.force_best_item = Integer.parseInt(value);
        }
        else if("gold_color".equals(name))
        {
            tmpBox.gold_color = value;
        }
        else if("text".equals(name))
        {
            tmpBox.text = value;
        }
    }


    private void setPlayerAttributes(Attributes attributes)
    {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        if("posx".equals(name))
        {
            tmpPlayer.posx = Float.parseFloat(value);
        }
        else if("posy".equals(name))
        {
            tmpPlayer.posy = Float.parseFloat(value);
        }
        else if("direction".equals(name))
        {
            tmpPlayer.direction = value;
        }
    }

    private void setSettingsAttributes(Attributes attributes)
    {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        if("lvl_music".equals(name))
        {
            tmpSettings.music = value;
        }
        else if("cam_limit_w".equals(name))
        {
            tmpSettings.width = Integer.parseInt(value);
        }
        else if("cam_limit_h".equals(name))
        {
            tmpSettings.height = Integer.parseInt(value);
        }
    }

    private void setSpriteAttributes(Attributes attributes)
    {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        if("posx".equals(name))
        {
            tmpSprite.posx = Float.parseFloat(value);
        }
        else if("posy".equals(name))
        {
            tmpSprite.posy = Float.parseFloat(value);
        }
        else if("type".equals(name))
        {
            tmpSprite.type = value;
        }
        else if("image".equals(name))
        {
            tmpSprite.image = value;
        }
    }

    private void setBackgroundAttributes(Attributes attributes)
    {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        if("bg_color_1_red".equals(name))
        {
            tmpBackground.color1_red = Integer.parseInt(value);
        }
        else if("bg_color_2_red".equals(name))
        {
            tmpBackground.color2_red = Integer.parseInt(value);
        }
        else if("bg_color_1_blue".equals(name))
        {
            tmpBackground.color1_blue = Integer.parseInt(value);
        }
        else if("bg_color_2_blur".equals(name))
        {
            tmpBackground.color2_blue = Integer.parseInt(value);
        }
        if("bg_color_1_green".equals(name))
        {
            tmpBackground.color1_green = Integer.parseInt(value);
        }
        else if("bg_color_2_green".equals(name))
        {
            tmpBackground.color2_green = Integer.parseInt(value);
        }
        else if("type".equals(name))
        {
            tmpBackground.type = Integer.parseInt(value);
        }
        else if("posx".equals(name))
        {
            tmpBackground.posx = Integer.parseInt(value);
        }
        else if("posy".equals(name))
        {
            tmpBackground.posy = Integer.parseInt(value);
        }
        else if("speedx".equals(name))
        {
            tmpBackground.speedx = Float.parseFloat(value);
        }
        else if("speedy".equals(name))
        {
            tmpBackground.speedy = Float.parseFloat(value);
        }
        else if("image".equals(name))
        {
            tmpBackground.image = value;
        }
        else if("const_velx".equals(name))
        {
            tmpBackground.const_velx = Integer.parseInt(value);
        }
        else if("const_vely".equals(name))
        {
            tmpBackground.const_vely = Integer.parseInt(value);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        switch (qName)
        {
            case "background":
                fixBackground(tmpBackground);
                level.backgrounds.add(tmpBackground);
                tmpBackground = null;
                break;
            case "box":
                fixBox(tmpBox);
                level.objects.add(tmpBox);
                tmpBox = null;
                break;
            case "enemy":
                fixEnemy(tmpEnemy);
                level.objects.add(tmpEnemy);
                tmpEnemy = null;
                break;
            case "enemystopper":
                //TODO fix enemy stopper
                level.objects.add(tmpStopper);
                tmpStopper = null;
                break;
            case "item":
                fixItem(tmpItem);
                level.objects.add(tmpItem);
                tmpItem = null;
                break;
            case "levelexit":
                //TODO fix level exit
                level.objects.add(tmpExit);
                tmpExit = null;
                break;
            case "level_entry":
                //TODO fix level entry
                level.objects.add(tmpEntry);
                tmpEntry = null;
                break;
            case "player":
                fixPlayer(tmpPlayer);
                level.objects.add(tmpPlayer);
                tmpPlayer = null;
                break;
            case "settings":
                tmpSettings.height = Math.abs(tmpSettings.height / 64);
                tmpSettings.width = tmpSettings.width / 64;
                tmpSettings.music = "data/music/" + tmpSettings.music;
                level.settings = tmpSettings;
                tmpSettings = null;
                break;
            case "sprite":
                fixSprite(tmpSprite);
                level.objects.add(tmpSprite);
                tmpSprite = null;
                break;
            case "moving_platform":
                fixPlatform(tmpPlatform);
                level.objects.add(tmpPlatform);
                tmpPlatform = null;
                break;
        }

    }

    private void fixPlatform(MovingPlatform tmpPlatform)
    {
        /*public float posx, posy, max_distance, speed, touch_time, shake_time, touch_move_time;
    public int move_type, middle_img_count;
    public String massive_type, direction, image_top_left, image_top_middle, image_top_right;*/
        tmpPlatform.posx = tmpPlatform.posx / 64f;
        if(tmpPlatform.image_top_left.contains("green_1"))
        {
            tmpPlatform.posy = convertY(tmpPlatform.posy, 18);
            tmpPlatform.texture_atlas = "data/ground/green_1/slider/brown.pack";
            tmpPlatform.height = (18 / 64f) * (2 + tmpPlatform.middle_img_count);
            tmpPlatform.width = 18 / 64f;
        }
        else if(tmpPlatform.image_top_left.contains("jungle_1"))
        {
            tmpPlatform.posy = convertY(tmpPlatform.posy, 22);
            if(tmpPlatform.image_top_left.contains("brown"))
            {
                tmpPlatform.texture_atlas = "data/ground/jungle_1/slider/brown.pack";
            }
            else if(tmpPlatform.image_top_left.contains("blue"))
            {
                tmpPlatform.texture_atlas = "data/ground/jungle_1/slider/blue.pack";
            }
            else if(tmpPlatform.image_top_left.contains("green"))
            {
                tmpPlatform.texture_atlas = "data/ground/jungle_1/slider/green.pack";
            }

            tmpPlatform.height = (22 / 64f) * (2 + tmpPlatform.middle_img_count);
            tmpPlatform.width = 22 / 64f;
        }
        //TODO other maybe


    }

    private void fixBackground(Background background)
    {
            if(background.image != null)
                background.image = "data/" + background.image;
    }

    private void fixBox(Box box)
    {
        box.posx = box.posx / 64;
        box.posy = convertY(box.posy, 43f);
        if(box.animation != null)
        {
            switch (box.animation)
            {
                case "Bonus":
                    box.texture_atlas = "data/game/box/yellow/bonus.pack";
                    break;
                case "Default":
                    box.texture_name = "data/game/box/yellow/default.png";
                    break;
                case "Power":
                    box.texture_atlas = "data/game/box/yellow/power.pack";
                    break;
                case "Spin":
                    box.texture_atlas = "data/game/box/yellow/spin.pack";
                    break;
            }
        }
    }

    private void fixEnemy(Enemy enemy)
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

    private void setEnemySettings(Enemy enemy, String fileName)
    {
        File settings = new File(Converter.dataRoot, enemy.image_dir);
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


    private void fixItem(Item item)
    {
        item.posx = item.posx / 64f;
        item.posy = Math.abs(item.posy / 64f);
        switch (item.type)
        {
            case "goldpiece":
                item.width = item.height = 0.59375f;
                item.texture_atlas = "data/game/items/goldpiece/" + item.color + ".pack";
                break;
            case "mushroom":
                switch (item.mushroom_type)
                {
/*TYPE_MUSHROOM_DEFAULT = 25,
	TYPE_MUSHROOM_LIVE_1 = 35,
	TYPE_MUSHROOM_POISON = 49,
	TYPE_MUSHROOM_BLUE = 51,
	TYPE_MUSHROOM_GHOST = 52,*/
                    case 25:
                        item.texture_name = "data/game/item/mushroom_red.png";
                        break;
                    case 35:
                        item.texture_name = "data/game/item/mushroom_green.png";
                        break;
                    case 49:
                        item.texture_name = "data/game/item/mushroom_poison.png";
                        break;
                    case 51:
                        item.texture_name = "data/game/item/mushroom_blue.png";
                        break;
                    case 52:
                        item.texture_name = "data/game/item/mushroom_ghost.png";
                        break;
                }
                break;
            case "fireplant":
                item.texture_atlas = "data/game/item/fireplant.pack";
                break;
            case "jstar":
                item.texture_name = "data/game/item/star.png";
                break;
            case "moon":
                item.texture_name = "data/game/item/moon.pack";
                break;
        }
    }

    private void fixPlayer(Player player)
    {
        player.posx = player.posx / 64f;
        player.posy = convertY(player.posy, 64f);
    }

    private void fixSprite(Sprite sprite)
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

    private void setSpriteSettings(Sprite sprite)
    {
        String fileName = sprite.image.replaceAll("png", "settings");
        File settings = new File(Converter.dataRoot, fileName);
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

    private String readFileContents(File file)
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

    private static float convertY(float posy, float height)
    {
        return (Math.abs(posy) - height) / 64f;
    }

}