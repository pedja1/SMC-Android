package rs.pedjaapps.smc.object.items.mushroom;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.object.World;

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
        textureName = "game_items_mushroom_poison";
    }

    @Override
    public int getType() {
        return TYPE_MUSHROOM_POISON;
    }

    @Override
    protected void performCollisionAction()
    {
        playerHit = true;
        if (!world.maryo.mInvincibleStar)
            world.maryo.downgradeOrDie(false);
        world.trashObjects.add(this);
    }
}
