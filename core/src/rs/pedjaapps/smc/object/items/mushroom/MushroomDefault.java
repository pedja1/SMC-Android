package rs.pedjaapps.smc.object.items.mushroom;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.object.Maryo;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.GameSaveUtility;

/**
 * Created by pedja on 29.3.15..
 * <p/>
 * This file is part of SMC-Android
 * Copyright Predrag Čokulov 2015
 */
public class MushroomDefault extends Mushroom
{
    public static final int POINTS = 500;
    public MushroomDefault(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        textureName = "data/game/items/mushroom_red.png";
    }

    @Override
    protected void performCollisionAction()
    {
        world.maryo.upgrade(Maryo.MaryoState.big);
        position.set(-1, -1, -1);//has the effect of removing item (if its not on screen it wont be drawn)
        GameSaveUtility.getInstance().save.points += POINTS;
    }
}
