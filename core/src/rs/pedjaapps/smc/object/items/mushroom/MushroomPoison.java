package rs.pedjaapps.smc.object.items.mushroom;

import rs.pedjaapps.smc.MaryoGame;

/**
 * Created by pedja on 29.3.15..
 * <p/>
 * This file is part of SMC-Android
 * Copyright Predrag Čokulov 2015
 */
public class MushroomPoison extends Mushroom {
    public MushroomPoison(float x, float y, float z, float width, float height) {
        super(x, y, z, width, height);
        textureName = "game_items_mushroom_poison";
    }

    @Override
    public int getType() {
        return TYPE_MUSHROOM_POISON;
    }

    @Override
    protected void performCollisionAction() {
        playerHit = true;
        if (!MaryoGame.game.currentScreen.world.maryo.mInvincibleStar)
            MaryoGame.game.currentScreen.world.maryo.downgradeOrDie(false);
        trashThisObject();
    }
}
