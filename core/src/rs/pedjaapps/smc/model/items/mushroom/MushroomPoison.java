package rs.pedjaapps.smc.model.items.mushroom;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.model.World;

/**
 * Created by pedja on 29.3.15..
 * <p/>
 * This file is part of SMC-Android
 * Copyright Predrag Čokulov 2015
 */
public class MushroomPoison extends Mushroom
{
    public MushroomPoison(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        textureName = "data/game/items/mushroom_poison.png";
    }

    @Override
    protected void performCollisionAction()
    {

    }

    @Override
    protected Sound getCollisionSound()
    {
        return null;
    }
}
