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

    public static final String DATA_SOUNDS_ITEM_LIVE_UP_2 = "data/sounds/item/live_up_2.mp3";
    public static final String DATA_SOUNDS_ITEM_LIVE_UP = "data/sounds/item/live_up.mp3";

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
        manager.setLoader(Sound.class, ".mp3", new SoundLoader(new InternalFileHandleResolver()));
        manager.setLoader(Music.class, ".mp3", new MusicLoader(new InternalFileHandleResolver()));
    }

    public void dispose() {
        //do not clear. This is just annoying because it needs to be loaded again
        //TODO: große Klopper müssen wieder weggemacht werden (Background, Musik)
        //manager.clear();
    }

}
