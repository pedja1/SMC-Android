package rs.pedjaapps.smc.assets;

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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

/**
 * Created by pedja on 2/15/14.
 */
public class Assets
{
    public AssetManager manager;
    public TextureLoader.TextureParameter textureParameter;
    public ParticleEffectLoader.ParticleEffectParameter particleEffectParameter;
    public MaryoFileHandleResolver resolver;

    public Assets()
    {
        textureParameter = new TextureLoader.TextureParameter();
        //textureParameter.genMipMaps = true;
        textureParameter.magFilter = Texture.TextureFilter.Linear;
        textureParameter.minFilter = Texture.TextureFilter.Linear;

        resolver = new MaryoFileHandleResolver();
        manager = new AssetManager(resolver);

        particleEffectParameter = new ParticleEffectLoader.ParticleEffectParameter();
        particleEffectParameter.imagesDir = resolver.resolve("data/animation/particles");

        // set the loaders for the generator and the fonts themselves
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        manager.setLoader(ParticleEffect.class, ".p", new ParticleEffectLoader(resolver));
        manager.setLoader(Sound.class, ".mp3", new SoundLoader(new InternalFileHandleResolver()));
        manager.setLoader(Music.class, ".mp3", new MusicLoader(new InternalFileHandleResolver()));
    }

    public void dispose()
    {
		manager.clear();
    }

}
