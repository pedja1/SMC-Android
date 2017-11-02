package rs.pedjaapps.smc.object.items.mushroom;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.GameSave;

/**
 * Created by Benjamin Schulte on 02.11.2017.
 */

public class MushroomGhost extends Mushroom {
    public MushroomGhost(World world, Vector2 size, Vector3 position) {
        super(world, size, position);
        textureName = "game_items_mushroom_ghost";
        mPickPoints = 800;
    }

    @Override
    public int getType() {
        return TYPE_MUSHROOM_GHOST;
    }

    @Override
    protected void performCollisionAction() {
        playerHit = true;
        world.maryo.enableGhostMode();
        Sound sound = world.screen.game.assets.manager.get(Assets.SOUND_ITEM_MUSHROOM_GHOST);
        SoundManager.play(sound);
        world.trashObjects.add(this);
        GameSave.addScore(mPickPoints);
    }
}
