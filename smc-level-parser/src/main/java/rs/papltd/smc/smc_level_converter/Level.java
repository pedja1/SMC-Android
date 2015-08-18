package rs.papltd.smc.smc_level_converter;

import java.util.ArrayList;
import java.util.List;

import rs.papltd.smc.smc_level_converter.objects.Background;
import rs.papltd.smc.smc_level_converter.objects.Information;
import rs.papltd.smc.smc_level_converter.objects.Settings;

/**
 * Created by pedja on 22.6.14..
 */
public class Level
{
    public Settings settings;
    public Information information;
    public List<Background> backgrounds;
    //public List<Box> boxes;
    //public List<Enemy> enemies;
    //public List<EnemyStopper> enemyStoppers;
   // public List<Item> items;
    //public List<LevelExit> levelExits;
    //public Player player;
    //public List<Sprite> sprites;

    public List<Object> objects;

    public Level()
    {
        settings = new Settings();
        backgrounds = new ArrayList<Background>();
        //boxes = new ArrayList<Box>();
        //enemies = new ArrayList<Enemy>();
        //enemyStoppers = new ArrayList<EnemyStopper>();
        //items= new ArrayList<Item>();
        //levelExits = new ArrayList<LevelExit>();
        //player = new Player();
        //sprites = new ArrayList<Sprite>();
        objects = new ArrayList<>();
    }
}
