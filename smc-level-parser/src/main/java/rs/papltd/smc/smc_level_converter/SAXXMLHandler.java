package rs.papltd.smc.smc_level_converter;


import com.badlogic.gdx.math.Rectangle;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rs.papltd.smc.smc_level_converter.objects.Background;
import rs.papltd.smc.smc_level_converter.objects.Box;
import rs.papltd.smc.smc_level_converter.objects.Enemy;
import rs.papltd.smc.smc_level_converter.objects.EnemyStopper;
import rs.papltd.smc.smc_level_converter.objects.Information;
import rs.papltd.smc.smc_level_converter.objects.Item;
import rs.papltd.smc.smc_level_converter.objects.LevelEntry;
import rs.papltd.smc.smc_level_converter.objects.LevelExit;
import rs.papltd.smc.smc_level_converter.objects.MovingPlatform;
import rs.papltd.smc.smc_level_converter.objects.ParticleEffect;
import rs.papltd.smc.smc_level_converter.objects.Path;
import rs.papltd.smc.smc_level_converter.objects.Player;
import rs.papltd.smc.smc_level_converter.objects.Settings;
import rs.papltd.smc.smc_level_converter.objects.Sprite;

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
    private Information tmpInformation;
    private Path tmpPath;
    private ParticleEffect tmpParticleEffect;

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
            case "particle_effect":
                tmpParticleEffect = new ParticleEffect();
                break;
            case "information":
                tmpInformation = new Information();
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
            case "path":
                tmpPath = new Path();
                break;
            case "Property":
            case "property":
                setPropertyToElement(attributes);
                break;
            case "level":
                //do nothing, level is our main tag
            case "particle_emitter":
            case "global_effect":
                //we don't need this elements
                break;
            case "sound":
                System.out.println("skipped 'sound' element from level");
                break;
            default:
                throw new IllegalArgumentException("Unknown element found (" + qName + ")");
        }
    }

    private void setPropertyToElement(Attributes attributes)
    {
        if (tmpBackground != null)
        {
            tmpBackground.setFromAttributes(attributes);
        }
        else if (tmpSprite != null)
        {
            tmpSprite.setFromAttributes(attributes);
        }
        else if (tmpSettings != null)
        {
            tmpSettings.setFromAttributes(attributes);
        }
        else if (tmpPlayer != null)
        {
            tmpPlayer.setFromAttributes(attributes);
        }
        else if (tmpExit != null)
        {
            tmpExit.setFromAttributes(attributes);
        }
        else if (tmpEntry != null)
        {
            tmpEntry.setFromAttributes(attributes);
        }
        else if (tmpItem != null)
        {
            tmpItem.setFromAttributes(attributes);
        }
        else if (tmpStopper != null)
        {
            tmpStopper.setFromAttributes(attributes);
        }
        else if (tmpBox != null)
        {
            tmpBox.setFromAttributes(attributes);
        }
        else if (tmpEnemy != null)
        {
            tmpEnemy.setFromAttributes(attributes);
        }
        else if (tmpPlatform != null)
        {
            tmpPlatform.setFromAttributes(attributes);
        }
        else if (tmpInformation != null)
        {
            tmpInformation.setFromAttributes(attributes);
        }
        else if (tmpPath != null)
        {
            tmpPath.setFromAttributes(attributes);
        }
        else if (tmpParticleEffect != null)
        {
            tmpParticleEffect.setFromAttributes(attributes);
        }
        else
        {
            //throw new IllegalArgumentException("All objects are null");
            //System.out.println("Warning: all objects are null, probably skipped element");
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
            case "particle_effect":
                level.particleEffect = (tmpParticleEffect);
                tmpParticleEffect = null;
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
                fixEnemyStopper(tmpStopper);
                level.objects.add(tmpStopper);
                tmpStopper = null;
                break;
            case "item":
                fixItem(tmpItem);
                level.objects.add(tmpItem);
                tmpItem = null;
                break;
            case "levelexit":
                fixLevelExit(tmpExit);
                level.objects.add(tmpExit);
                tmpExit = null;
                break;
            case "level_entry":
                fixLevelEntry(tmpEntry);
                level.objects.add(tmpEntry);
                tmpEntry = null;
                break;
            case "player":
                fixPlayer(tmpPlayer);
                level.objects.add(tmpPlayer);
                tmpPlayer = null;
                break;
            case "information":
                level.information = tmpInformation;
                tmpInformation = null;
                break;
            case "settings":
                tmpSettings.height = Math.abs((tmpSettings.height - level.information.yOffset) / 64f);
                tmpSettings.width = tmpSettings.width / 64f;
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
            case "path":
                fixPath(tmpPath);
                level.objects.add(tmpPath);
                tmpPath = null;
                break;
        }

    }

    private void fixPath(Path tmpPath)
    {
        tmpPath.posx = tmpPath.posx / 64f;
        tmpPath.posy = convertY(tmpPath.posy, 0);
        for(Path.Segment segment : tmpPath.segments)
        {
            segment.start.x = segment.start.x / 64f;
            segment.start.y = -segment.start.y / 64f;
            segment.end.x = segment.end.x / 64f;
            segment.end.y = -segment.end.y / 64f;
        }
    }

    private void fixPlatform(MovingPlatform tmpPlatform)
    {
        /*public float posx, posy, max_distance, speed, touch_time, shake_time, touch_move_time;
    public int move_type, middle_img_count;
    public String massive_type, direction, image_top_left, image_top_middle, image_top_right;*/
        tmpPlatform.posx = tmpPlatform.posx / 64f;
        if (tmpPlatform.image_top_left.contains("green_1"))
        {
            tmpPlatform.posy = convertY(tmpPlatform.posy, 18);
            tmpPlatform.width = (18 / 64f) * (2 + tmpPlatform.middle_img_count);
            tmpPlatform.height = 18 / 64f;
        }
        else if (tmpPlatform.image_top_left.contains("jungle_1"))
        {
            tmpPlatform.posy = convertY(tmpPlatform.posy, 22);
            String color = "brown";
            if (tmpPlatform.image_top_left.contains("brown"))
                color = "brown";
            else if (tmpPlatform.image_top_left.contains("blue"))
                color = "blue";
            else if (tmpPlatform.image_top_left.contains("green"))
                color = "green";

            if (tmpPlatform.image_top_right.contains("1") && tmpPlatform.image_top_right.contains("right"))
            {
                tmpPlatform.image_top_right = "ground/jungle_1/slider/2_" + color + "_right.png";
            }

            tmpPlatform.width = (22 / 64f) * (2 + tmpPlatform.middle_img_count);
            tmpPlatform.height = 22 / 64f;
        }
        else if (tmpPlatform.image_top_left.contains("mushroom_1"))
        {
            tmpPlatform.posy = convertY(tmpPlatform.posy, 64);
            tmpPlatform.width = (64 / 64f) * (2 + tmpPlatform.middle_img_count);
            tmpPlatform.height = 64 / 64f;
        }
        else if (tmpPlatform.image_top_left.contains("blocks"))
        {
            tmpPlatform.posy = convertY(tmpPlatform.posy, 43);
            tmpPlatform.width = (43 / 64f) * (2 + tmpPlatform.middle_img_count);
            tmpPlatform.height = 43 / 64f;
        }
        else if (tmpPlatform.image_top_left.contains("white1_1"))
        {
            tmpPlatform.posy = convertY(tmpPlatform.posy, 43);
            tmpPlatform.width = (43 / 64f) * (2 + tmpPlatform.middle_img_count);
            tmpPlatform.height = 43 / 64f;
        }
        tmpPlatform.max_distance = tmpPlatform.max_distance / 64f;
        tmpPlatform.touch_move_time /= 25;
        tmpPlatform.touch_time /= 25;
        tmpPlatform.shake_time /= 25;

        setPlatformImagesForAtlas(tmpPlatform);

    }

    private void setPlatformImagesForAtlas(MovingPlatform tmpPlatform)
    {
        if (tmpPlatform.texture_atlas == null) {
            String nameInAtlas = getNameInAtlas(tmpPlatform.image_top_left);

            File f = new File(Const.UNPACKED_PATH + "/static/" + nameInAtlas);
            if (f.exists() && !f.isDirectory()) {
                tmpPlatform.texture_atlas = "data/static.pack";
                tmpPlatform.image_top_left = nameInAtlas.substring(0, nameInAtlas.lastIndexOf("."));
                nameInAtlas = getNameInAtlas(tmpPlatform.image_top_middle);
                tmpPlatform.image_top_middle = nameInAtlas.substring(0, nameInAtlas.lastIndexOf("."));
                nameInAtlas = getNameInAtlas(tmpPlatform.image_top_right);
                tmpPlatform.image_top_right = nameInAtlas.substring(0, nameInAtlas.lastIndexOf("."));
            } else {
                System.out.println("! Not in atlas: " + nameInAtlas);
            }
        } else
            System.out.println("Has own atlas? " + tmpPlatform.texture_atlas);


    }

    private String getNameInAtlas(String name) {
        return name.replace("data/", "").replace("/", "_");
    }

    private void fixBackground(Background background)
    {
        if (background.image == null)
            return;
        setBackgroundSettings(background);
        background.image = "data/" + background.image;
        background.posx = background.posx / 64f;
    }

    private void setBackgroundSettings(Background item)
    {
        String fileName = item.image.replaceAll("png", "settings");
        File settings = new File(Const.dataRoot, fileName);
        String settingsData = readFileContents(settings);
        String[] lines = settingsData.split("\n");
        float origHeight = 0;
        for (String s : lines)
        {
            String[] data = s.split(" ");
            if ("width".equals(data[0]))
            {
                item.width = Float.parseFloat(data[1]) / 64f;
            }
            else if ("height".equals(data[0]))
            {
                origHeight = Float.parseFloat(data[1]);
                item.height = origHeight / 64f;
            }
        }
        item.posy = Math.abs(item.posy) / 64f;
    }

    private void fixBox(Box box)
    {
        box.posx = box.posx / 64;
        box.posy = convertY(box.posy, 43f);
        String settingsFileName;
        if (box.texture_name != null)
            System.out.println("Box has own texture!");

        if (box.animation != null)
        {
            switch (box.animation)
            {
                case "Bonus":
                    settingsFileName = "game/box/yellow/bonus/1.settings";
                    break;
                case "Default":
                    settingsFileName = "game/box/yellow/default.settings";
                    break;
                case "Power":
                    settingsFileName = "game/box/yellow/power_1.settings";
                    break;
                case "Spin":
                    settingsFileName = "game/box/yellow/spin/1.settings";
                    break;
                default:
                    settingsFileName = "game/box/yellow/default.settings";
                    break;
            }
        }
        else
            settingsFileName = "game/box/yellow/default.settings";

        File settings = new File(Const.dataRoot, settingsFileName);
        String settingsData = readFileContents(settings);
        box.colRect = getCollisionRectangle(settingsData);
    }

    private void fixEnemyStopper(EnemyStopper enemyStopper)
    {
        enemyStopper.posx = enemyStopper.posx / 64;
        enemyStopper.posy = convertY(enemyStopper.posy, enemyStopper.height);
        enemyStopper.height = enemyStopper.height / 64;
        enemyStopper.width = enemyStopper.width / 64;
    }

    private void fixLevelExit(LevelExit exit)
    {
        exit.posx = exit.posx / 64;
        exit.posy = convertY(exit.posy, exit.height);
        exit.height = exit.height / 64;
        exit.width = exit.width / 64;
    }

    private void fixLevelEntry(LevelEntry entry)
    {
        entry.posx = entry.posx / 64;
        entry.posy = convertY(entry.posy, 12f);
        entry.h = entry.h / 64;
        entry.w = entry.w / 64;
    }

    private void fixEnemy(Enemy enemy)
    {
        if ("eato".equals(enemy.type))
        {
            enemy.color = (enemy.image_dir.contains("green") ? "green" : "brown");
            enemy.posx = enemy.posx / 64;
            setEnemySettings(enemy, "1.settings");
        }
        else if ("flyon".equals(enemy.type) || "jpiranha".equals(enemy.type))
        {
            if ("jpiranha".equals(enemy.type))
            {
                if (enemy.image_dir == null)
                {
                    enemy.image_dir = "enemy/flyon/orange/";
                }
                else
                {
                    enemy.image_dir = enemy.image_dir.replace("jpiranha", "flyon");
                }
                enemy.type = "flyon";
            }
            enemy.posx = enemy.posx / 64;
            setEnemySettings(enemy, "closed_1.settings");
            enemy.max_distance = enemy.max_distance / 64f;
            enemy.speed = enemy.speed / 64f;
        }
        else if ("furball".equals(enemy.type) || "gumba".equals(enemy.type))
        {
            if ("gumba".equals(enemy.type))
            {
                enemy.type = "furball";
                if ("red".equals(enemy.color))
                    enemy.color = "blue";
            }
            if ("black".equals(enemy.color))
                enemy.color = "boss";
            enemy.image_dir = "enemy/furball/" + enemy.color + "/";
            enemy.posx = enemy.posx / 64;
            setEnemySettings(enemy, "dead.settings");
        }
        else if ("gee".equals(enemy.type))
        {
            enemy.posx = enemy.posx / 64;
            switch (enemy.color)
            {
                case "yellow":
                    enemy.image_dir = "enemy/gee/electro";
                    break;
                case "red":
                    enemy.image_dir = "enemy/gee/venom";
                    break;
                case "green":
                    enemy.image_dir = "enemy/gee/lava";
                    break;
            }
            setEnemySettings(enemy, "1.settings");
            enemy.max_distance = enemy.max_distance / 64f;
            enemy.flyDistance = enemy.flyDistance / 64f;
        }
        else if ("krush".equals(enemy.type))
        {
            enemy.image_dir = "enemy/krush/";
            enemy.posx = enemy.posx / 64;
            setEnemySettings(enemy, "big_1.settings");
        }
        else if ("rokko".equals(enemy.type))
        {
            enemy.image_dir = "enemy/rokko/";
            enemy.posx = enemy.posx / 64;
            setEnemySettings(enemy, "r.settings");
        }
        else if ("spika".equals(enemy.type))
        {
            enemy.image_dir = "enemy/spika/";
            enemy.posx = enemy.posx / 64;
            setEnemySettings(enemy, enemy.color + ".settings");
        }
        else if ("spikeball".equals(enemy.type))
        {
            enemy.image_dir = "enemy/spikeball/grey/";
            enemy.posx = enemy.posx / 64;
            setEnemySettings(enemy, "walk_1.settings");
        }
        else if ("thromp".equals(enemy.type))
        {
            enemy.posx = enemy.posx / 64;
            setEnemySettings(enemy, "down.settings");
            enemy.max_distance = enemy.max_distance / 64f;
            enemy.speed = enemy.speed / 64f;
        }
        else if ("turtle".equals(enemy.type))
        {
            enemy.image_dir = "enemy/turtle/green/";
            enemy.posx = enemy.posx / 64;
            setEnemySettings(enemy, "walk_1.settings");
        }
        else if ("turtleboss".equals(enemy.type))
        {
            System.out.println("turtleboss");
            enemy.texture_atlas = "data/enemy/bosses/turtle.pack";
            enemy.image_dir = "enemy/bosses/turtle/";
            enemy.posx = enemy.posx / 64;
            setEnemySettings(enemy, "walk_1.settings");
        }
        else if ("static".equals(enemy.type))
        {
            enemy.posx = enemy.posx / 64;
            if (enemy.image == null) enemy.image = "enemy/static/blocks/spike_1/2_grey.png";

            String nameInAtlas = getNameInAtlas(enemy.image);
            File f = new File(Const.UNPACKED_PATH + "/dynamic/" + nameInAtlas);
            if (f.exists() && !f.isDirectory())
                enemy.texture_name = nameInAtlas.substring(0, nameInAtlas.lastIndexOf("."));
            else
                System.out.println("Not in atlas: " + nameInAtlas);

            enemy.image_dir = enemy.image.substring(0, enemy.image.lastIndexOf('/'));
            enemy.rotationSpeed = enemy.rotationSpeed * 0.5f;
            setEnemySettings(enemy, enemy.image.substring(enemy.image.lastIndexOf('/') + 1, enemy.image.length()).replaceAll("png", "settings"));
        }
        else
        {
            System.out.println(String.format("skipped enemy '%s'", enemy.type));
        }
    }

    private void setEnemySettings(Enemy enemy, String fileName)
    {
        File settings = new File(Const.dataRoot, enemy.image_dir);
        settings = new File(settings, fileName);
        String settingsData = readFileContents(settings);
        String[] lines = settingsData.split("\n");
        float origHeight = 1;
        for (String s : lines)
        {
            String[] data = s.split(" ");
            if ("width".equals(data[0]))
            {
                enemy.width = Float.parseFloat(data[1]) / 64f;
            }
            else if ("height".equals(data[0]))
            {
                origHeight = Float.parseFloat(data[1]);
                enemy.height = origHeight / 64f;
            }
        }
        enemy.posy = convertY(enemy.posy, origHeight);
        if (!"eato".equals(enemy.type))
        {
            enemy.posy += 0.1f;//TODO for collision
        }
        enemy.colRect = getCollisionRectangle(settingsData);
    }


    private void fixItem(Item item)
    {
        item.posx = item.posx / 64f;
        switch (item.type)
        {
            case "goldpiece":
                item.width = item.height = 0.59375f;
                item.image = "game/items/goldpiece/" + item.color + "/1.png";
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
                        item.texture_name = "data/game/items/mushroom_red.png";
                        break;
                    case 35:
                        item.texture_name = "data/game/items/mushroom_green.png";
                        break;
                    case 49:
                        item.texture_name = "data/game/items/mushroom_poison.png";
                        break;
                    case 51:
                        item.texture_name = "data/game/items/mushroom_blue.png";
                        break;
                    case 52:
                        item.texture_name = "data/game/items/mushroom_ghost.png";
                        break;

                }
                item.image = item.texture_name.substring(item.texture_name.indexOf("/"), item.texture_name.length());
                break;
            case "fireplant":
                item.image = "game/items/fireplant.png";
                break;
            case "jstar":
                item.texture_name = "data/game/items/star.png";
                item.image = item.texture_name.substring(item.texture_name.indexOf("/") - 1, item.texture_name.length() - 1);
                break;
            case "moon":
                item.image = "game/items/moon_1.png";
                break;
            default:
                System.out.println(String.format("skipped item '%s'", item.type));
                break;
        }
        setItemSettings(item);
    }

    private void fixPlayer(Player player)
    {
        player.posx = player.posx / 64f;
        player.posy = convertY(player.posy, 64f);
    }

    private void fixSprite(Sprite sprite)
    {
        sprite.posx = sprite.posx / 64f;
        if (sprite.image.contains("box/yellow"))
        {
            //TODO fix this and everything else
        }
        else if (sprite.image.contains("plant_l"))
        {
            String tmp = sprite.image.replace("plant_l", "plant_r");
            sprite.texture_name = "data/" + tmp;
        }
        else if (sprite.image.contains("1_ending_left") && !sprite.image.contains("1_ending_left_up"))
        {
            String tmp = sprite.image.replace("_left", "");
            sprite.texture_name = "data/" + tmp;

        }
        else if (sprite.image.contains("ground/green_3/ground") && sprite.image.contains("right"))
        {
            sprite.texture_name = "data/" + sprite.image;
        }
        else
        {
            sprite.texture_name = "data/" + sprite.image;
        }
        setSpriteSettings(sprite);
    }

    private void setItemSettings(Item item)
    {
        String fileName = item.image.replaceAll("png", "settings");
        File settings = new File(Const.dataRoot, fileName);
        String settingsData = readFileContents(settings);
        String[] lines = settingsData.split("\n");
        float origHeight = 1;
        for (String s : lines)
        {
            String[] data = s.split(" ");
            if ("width".equals(data[0]))
            {
                item.width = Float.parseFloat(data[1]) / 64f;
            }
            else if ("height".equals(data[0]))
            {
                origHeight = Float.parseFloat(data[1]);
                item.height = origHeight / 64f;
            }
        }
        item.posy = convertY(item.posy, origHeight);
        item.colRect = getCollisionRectangle(settingsData);
    }

    private void setSpriteSettings(Sprite sprite)
    {
        String fileName = sprite.image.replaceAll("png", "settings");
        File settings = new File(Const.dataRoot, fileName);
        String settingsData = readFileContents(settings);
        String[] lines = settingsData.split("\n");

        if (lines.length > 0)
        {
            String[] data = lines[0].split(" ");
            if ("base".equals(data[0]))
            {
                String baseFileName = fileName.replaceAll(fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf(".")), data[1].replaceAll(".png", ""));
                File baseFile = new File(Const.dataRoot, baseFileName);
                if (baseFile.exists())
                {
                    String baseSettingsData = readFileContents(baseFile);
                    String[] baseLines = baseSettingsData.split("\n");
                    String[] tmp = new String[lines.length + baseLines.length];
                    System.arraycopy(lines, 0, tmp, 0, lines.length);
                    System.arraycopy(baseLines, 0, tmp, lines.length, baseLines.length);

                    lines = tmp;

                    sprite.texture_name = sprite.texture_name.replaceAll(sprite.texture_name.substring(sprite.texture_name.lastIndexOf('/') + 1, sprite.texture_name.lastIndexOf('.')), baseFileName.substring(baseFileName.lastIndexOf("/") + 1, baseFileName.lastIndexOf(".")));
                }
                else
                    sprite.texture_name = sprite.texture_name.replaceAll(sprite.texture_name.substring(sprite.texture_name.lastIndexOf('/') + 1, sprite.texture_name.lastIndexOf('.')), baseFileName.substring(baseFileName.lastIndexOf("/") + 1, baseFileName.lastIndexOf(".")));

            }
        }


        if (sprite.texture_atlas == null) {
            String nameInAtlas = getNameInAtlas(sprite.texture_name);

            File f = new File(Const.UNPACKED_PATH + "/static/" + nameInAtlas);
            if (f.exists() && !f.isDirectory()) {
                sprite.texture_atlas = "data/static.pack";
                sprite.texture_name = sprite.texture_atlas + ":" + nameInAtlas.substring(0, nameInAtlas.lastIndexOf("."));
            } else {
                System.out.println("Not in atlas: " + nameInAtlas);
            }
        } else
            System.out.println("Has own atlas? " + sprite.texture_name);


        float origHeight = 1;
        float origWidth = 1;
        for (String s : lines)
        {
            s =  s.replaceAll("\r","");
            String[] data = s.split(" ");
            if ("width".equals(data[0]))
            {
                origWidth = Float.parseFloat(data[1]);
                sprite.width = Float.parseFloat(data[1]) / 64f;
            }
            else if ("height".equals(data[0]))
            {
                origHeight = Float.parseFloat(data[1]);
                sprite.height = origHeight / 64f;
            }
            else if ("rotation".equals(data[0]))
            {
                sprite.rotationX = Integer.parseInt(data[1]);
                sprite.rotationY = Integer.parseInt(data[2]);
                sprite.rotationZ = Integer.parseInt(data[3]);
                //System.out.println("rotation: " + Arrays.toString(data));
            }
            else if ("col_rect".equals(data[0]))
            {
                sprite.colRect = new Rectangle();
                sprite.colRect.width = Float.parseFloat(data[3]) / 64f;
                sprite.colRect.height = Float.parseFloat(data[4]) / 64f;
                sprite.colRect.x = Float.parseFloat(data[1]) / 64f;
                sprite.colRect.y = (origHeight / 64f) - (sprite.colRect.height + (Float.parseFloat(data[2]) / 64f));
            }
            else if("ground_type".equals(data[0]))
            {
                sprite.groundType = Sprite.Ground.idFromType(data[1]);
            }
        }
        /*if(sprite.rotationZ == 90 || sprite.rotationZ == 270)
        {
            sprite.posy = convertY(sprite.posy, origWidth);
            //sprite.posy += (sprite.width - sprite.height) / 2;
        }
        else
        {
            sprite.posy = convertY(sprite.posy, origHeight);
        }*/
        sprite.posy = convertY(sprite.posy, origHeight);
    }

    private String readFileContents(File file)
    {
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(file));
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
        finally
        {
            try
            {
                if (br != null)
                {
                    br.close();
                }
            }
            catch (IOException ignore)
            {
            }
        }
        return null;
    }

    private float convertY(float posy, float height)
    {
        posy = posy + level.information.yOffset;
        return (Math.abs(posy) - height) / 64f;
    }


    public Rectangle getCollisionRectangle(String content)
    {
        Pattern pattern = Pattern.compile("col_rect\\s*([0-9])\\s*([0-9])\\s*([0-9])\\s*([0-9])");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find())
        {
            Rectangle rectangle = new Rectangle();
            rectangle.set(Float.parseFloat(matcher.group(1)), Float.parseFloat(matcher.group(2)), Float.parseFloat(matcher.group(3)), Float.parseFloat(matcher.group(4)));
            return rectangle;
        }
        return null;
    }
}
