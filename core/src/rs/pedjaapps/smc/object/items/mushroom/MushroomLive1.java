package rs.pedjaapps.smc.object.items.mushroom;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.utility.GameSave;

/**
 * Created by pedja on 29.3.15..
 * <p/>
 * This file is part of SMC-Android
 * Copyright Predrag Čokulov 2015
 */
public class MushroomLive1 extends Mushroom {

    public MushroomLive1(float x, float y, float z, float width, float height) {
        super(x, y, z, width, height);
        textureName = "game_items_mushroom_green";
        mPickPoints = 1000;
    }

    @Override
    public int getType() {
        return TYPE_MUSHROOM_LIVE_1;
    }

    @Override
    protected void performCollisionAction() {
        playerHit = true;
        GameSave.addLifes(1);
        Sound sound = MaryoGame.game.assets.get(Assets.SOUND_ITEM_LIVE_UP);
        SoundManager.play(sound);
        trashThisObject();
        GameSave.addScore(mPickPoints);
    }
}
