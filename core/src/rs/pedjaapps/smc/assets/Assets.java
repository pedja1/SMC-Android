package rs.pedjaapps.smc.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

/**
 * Created by pedja on 2/15/14.
 */
public class Assets {
    public static final String SKIN_HUD = "data/hud/smcskin.json";
    public static final String LOGO_GAME = "data/game/logo/smc_big_1.png";
    public static final String LOGO_LOADING = "data/game/logo/loading.png";

    public static final String FONT_SIMPLE25 = "grobold25";
    public static final String LABEL_SIMPLE25 = "small";
    public static final String LABEL_BORDER25 = "outline_small";
    public static final String LABEL_BORDER60 = "outline";

    public static final String WINDOW_NOFRAME = "frameless";

    public static final String BUTTON_FA = "fa45";
    public static final String BUTTON_SMALL = "small";

    public static final String DATA_MUSHROOM_RED = "data/game/items/mushroom_red.png";

    public static final String SOUND_ITEM_LIVE_UP_2 = "data/sounds/item/live_up_2.ogg";
    public static final String SOUND_ITEM_LIVE_UP = "data/sounds/item/live_up.ogg";
    public static final String SOUND_PLAYER_DEAD = "data/sounds/player/dead.ogg";
    public static final String SOUND_JUMP_BIG = "data/sounds/player/jump_big.ogg";
    public static final String SOUND_JUMP_BIG_POWER = "data/sounds/player/jump_big_power.ogg";
    public static final String SOUND_JUMP_SMALL = "data/sounds/player/jump_small.ogg";
    public static final String SOUND_PLAYER_POWERDOWN = "data/sounds/player/powerdown.ogg";
    public static final String SOUND_JUMP_SMALL_POWER = "data/sounds/player/jump_small_power.ogg";
    public static final String SOUND_AUDIO_ON = "data/sounds/audio_on.ogg";
    public static final String SOUND_ENTER_PIPE = "data/sounds/enter_pipe.ogg";
    public static final String SOUND_LEAVE_PIPE = "data/sounds/leave_pipe.ogg";
    public static final String SOUND_SPROUT = "data/sounds/sprout_1.ogg";
    //???? unused
    public static final String SOUND_STOMP1 = "data/sounds/stomp_1.ogg";
    public static final String SOUND_STOMP4 = "data/sounds/stomp_4.ogg";
    public static final String SOUND_WALL_HIT = "data/sounds/wall_hit.ogg";
    public static final String SOUND_BOSS_FURBALL_HIT_FAILED = "data/sounds/enemy/boss/furball/hit_failed.ogg";
    public static final String SOUND_BOSS_FURBALL_HIT = "data/sounds/enemy/boss/furball/hit.ogg";
    public static final String SOUND_ENEMY_DIE_EATO = "data/sounds/enemy/eato/die.ogg";
    public static final String SOUND_ENEMY_DIE_FLYON = "data/sounds/enemy/flyon/die.ogg";
    public static final String SOUND_ENEMY_DIE_FURBALL = "data/sounds/enemy/furball/die.ogg";
    public static final String SOUND_ENEMY_DIE_GEE = "data/sounds/enemy/gee/die.ogg";
    public static final String SOUND_ENEMY_DIE_KRUSH = "data/sounds/enemy/krush/die.ogg";
    public static final String SOUND_ENEMY_ROKKO_HIT = "data/sounds/enemy/rokko/hit.ogg";
    public static final String SOUND_ENEMY_TURTLE_SHELL_HIT = "data/sounds/enemy/turtle/shell/hit.ogg";
    public static final String SOUND_ENEMY_DIE_THROMP = "data/sounds/enemy/thromp/die.ogg";
    public static final String SOUND_ITEM_FIREBALL = "data/sounds/item/fireball.ogg";
    public static final String SOUND_ITEM_FIREBALL_EXPLOSION = "data/sounds/item/fireball_explosion.ogg";
    public static final String SOUND_ITEM_ICEBALL_HIT = "data/sounds/item/iceball.ogg";
    public static final String SOUND_ITEM_FIREBALL_REPELLED = "data/sounds/item/fireball_repelled.ogg";
    public static final String SOUND_ITEM_FIREPLANT = "data/sounds/item/fireplant.ogg";
    public static final String SOUND_ITEM_GOLDPIECE1 = "data/sounds/item/goldpiece_1.ogg";
    public static final String SOUND_ITEM_GOLDPIECE_RED = "data/sounds/item/goldpiece_red.ogg";
    public static final String SOUND_ITEM_ICE_KILL = "data/sounds/item/ice_kill.ogg";
    public static final String SOUND_ITEM_MOON = "data/sounds/item/moon.ogg";
    public static final String SOUND_ITEM_MUSHROOM = "data/sounds/item/mushroom.ogg";
    public static final String SOUND_ITEM_MUSHROOM_BLUE = "data/sounds/item/mushroom_blue.ogg";
    public static final String SOUND_ITEM_STAR_KILL = "data/sounds/item/star_kill.ogg";

    public static final String MUSIC_COURSECLEAR = "data/music/game/courseclear.ogg";
    public static final String MUSIC_INVINCIBLE = "data/music/game/star.ogg";

    public static final String ATLAS_STATIC = "data/static.pack";
    public static final String ATLAS_DYNAMIC = "data/dynamic.pack";

    public AssetManager manager;
    public TextureLoader.TextureParameter textureParameter;
    public ParticleEffectLoader.ParticleEffectParameter particleEffectParameter;

    public Assets() {
        textureParameter = new TextureLoader.TextureParameter();
        //textureParameter.genMipMaps = true;
        textureParameter.magFilter = Texture.TextureFilter.Linear;
        textureParameter.minFilter = Texture.TextureFilter.Linear;

        manager = new AssetManager();

        particleEffectParameter = new ParticleEffectLoader.ParticleEffectParameter();
        particleEffectParameter.imagesDir = Gdx.files.internal("data/animation/particles");

        // set the loaders for the generator and the fonts themselves
        manager.setLoader(ParticleEffect.class, ".p", new ParticleEffectLoader(new InternalFileHandleResolver()));
        manager.setLoader(Sound.class, ".ogg", new SoundLoader(new InternalFileHandleResolver()));
        manager.setLoader(Music.class, ".ogg", new MusicLoader(new InternalFileHandleResolver()));
    }

    public void dispose() {
        //do not clear. This is just annoying because it needs to be loaded again
        //TODO: große Klopper müssen wieder weggemacht werden (Background, Musik)
        //manager.clear();
    }

}
