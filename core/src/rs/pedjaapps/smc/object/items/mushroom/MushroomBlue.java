package rs.pedjaapps.smc.object.items.mushroom;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.object.Box;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.GameSaveUtility;

/**
 * Created by pedja on 29.3.15..
 * <p/>
 * This file is part of SMC-Android
 * Copyright Predrag Čokulov 2015
 */
public class MushroomBlue extends Mushroom
{
    public MushroomBlue(World world, Vector2 size, Vector3 position, Box box)
    {
        super(world, size, position, box);
        textureName = "data/game/items/mushroom_blue.png";
    }

    @Override
    protected void performCollisionAction()
    {
        playerHit = true;
        world.maryo.upgrade(Maryo.MaryoState.ice, false, this);
        box.itemObject = null;
        GameSaveUtility.getInstance().save.points += 700;
    }
}
