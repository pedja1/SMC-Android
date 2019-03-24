package rs.pedjaapps.smc.object.items.mushroom;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.utility.GameSave;

/**
 * Created by pedja on 29.3.15..
 * <p/>
 * This file is part of SMC-Android
 * Copyright Predrag Čokulov 2015
 */
public class MushroomBlue extends Mushroom {

    public static final String TEXTURE_NAME = "game_items_mushroom_blue";

    public MushroomBlue(float x, float y, float z, float width, float height) {
        super(x, y, z, width, height);
        textureName = TEXTURE_NAME;
        mPickPoints = 700;
    }

    @Override
    public int getType() {
        return TYPE_MUSHROOM_BLUE;
    }

    @Override
    protected void performCollisionAction() {
        playerHit = true;
        MaryoGame.game.currentScreen.world.maryo.upgrade(Maryo.MaryoState.ice, this, false);
        trashThisObject();
        GameSave.addScore(mPickPoints);
    }
}
