package rs.pedjaapps.smc.model.items.mushroom;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.model.World;

/**
 * Created by pedja on 29.3.15..
 * <p/>
 * This file is part of SMC-Android
 * Copyright Predrag Čokulov 2015
 */
public class MushroomDefault extends Mushroom
{
    public MushroomDefault(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        textureName = "data/game/items/mushroom_red.png";
    }

    @Override
    protected void performCollisionAction()
    {
        world.getMario().upgrade();
        position.set(-1, -1 ,-1);//has the effect of removing item (if its not on screen it wont be drawn)
    }

    @Override
    protected Sound getCollisionSound()
    {
        return Assets.manager.get("data/sounds/item/mushroom.ogg", Sound.class);
    }
}
