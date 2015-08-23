package rs.pedjaapps.smc.object.items.mushroom;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.object.Box;
import rs.pedjaapps.smc.object.World;

/**
 * Created by pedja on 29.3.15..
 * <p/>
 * This file is part of SMC-Android
 * Copyright Predrag Čokulov 2015
 */
public class MushroomPoison extends Mushroom
{
    public MushroomPoison(World world, Vector2 size, Vector3 position, Box box)
    {
        super(world, size, position, box);
        textureName = "data/game/items/mushroom_poison.png";
    }

    @Override
    protected void performCollisionAction()
    {
        playerHit = true;
        world.maryo.downgradeOrDie(false);
        box.itemObject = null;
    }
}
