package rs.papltd.smc.smc_level_converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pedja on 22.6.14..
 */
public class Level
{
    public Settings settings;
    public List<Background> backgrounds;
    public List<Box> boxes;
    public List<Enemy> enemies;
    public List<EnemyStopper> enemyStoppers;
    public List<Item> items;
    public List<LelveExit> lelveExits;
    public Player player;
    public List<Sprite> sprites;

    public Level()
    {
        settings = new Settings();
        backgrounds = new ArrayList<Background>();
        boxes = new ArrayList<Box>();
        enemies = new ArrayList<Enemy>();
        enemyStoppers = new ArrayList<EnemyStopper>();
        items= new ArrayList<Item>();
        lelveExits = new ArrayList<LelveExit>();
        player = new Player();
        sprites = new ArrayList<Sprite>();
    }
}
