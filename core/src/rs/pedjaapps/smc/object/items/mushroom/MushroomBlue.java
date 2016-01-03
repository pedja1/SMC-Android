package rs.pedjaapps.smc.object.items.mushroom;

import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.GameSave;

/**
 * Created by pedja on 29.3.15..
 * <p/>
 * This file is part of SMC-Android
 * Copyright Predrag Čokulov 2015
 */
public class MushroomBlue extends Mushroom
{
    public MushroomBlue(World world, Vector3 position, float width, float height)
    {
        super(world, position, width, height);
        textureName = "data/game/items/mushroom_blue.png";
        mPickPoints = 700;
    }

    @Override
    protected void performCollisionAction()
    {
        playerHit = true;
        world.level.gameObjects.removeValue(this, true);
        GameSave.save.points += mPickPoints;
    }
}
