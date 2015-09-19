package rs.pedjaapps.smc.object.items.mushroom;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.utility.GameSaveUtility;

/**
 * Created by pedja on 29.3.15..
 * <p/>
 * This file is part of SMC-Android
 * Copyright Predrag Čokulov 2015
 */
public class MushroomGhost extends Mushroom
{
    public MushroomGhost(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        textureName = "data/game/items/mushroom_ghost.png";
        mPickPoints = 800;
    }

    @Override
    protected void performCollisionAction()
    {
        playerHit = true;
        world.maryo.upgrade(Maryo.MaryoState.ghost, true, null, false);
        world.trashObjects.add(this);
        GameSaveUtility.getInstance().save.points += mPickPoints;
    }
}
