package rs.pedjaapps.smc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

import rs.pedjaapps.smc.utility.MyFileHandleResolver;
import rs.pedjaapps.smc.utility.PrefsManager;

/**
 * Created by pedja on 2/15/14.
 */
public class Assets
{
    public static AssetManager manager;
    public static TextureLoader.TextureParameter textureParameter;
    public static SMCTextureAtlasLoader.TextureAtlasParameter atlasTextureParameter;
    public static ParticleEffectLoader.ParticleEffectParameter particleEffectParameter;
    public static boolean playMusic;
    public static boolean playSounds;

    static
    {
        textureParameter = new TextureLoader.TextureParameter();
        //textureParameter.genMipMaps = true;
        textureParameter.magFilter = Texture.TextureFilter.Nearest;
        textureParameter.minFilter = Texture.TextureFilter.Nearest;

        atlasTextureParameter = new SMCTextureAtlasLoader.TextureAtlasParameter(true);
        atlasTextureParameter.magFilter = Texture.TextureFilter.Nearest;
        atlasTextureParameter.minFilter = Texture.TextureFilter.Nearest;
        //atlasTextureParameter.genMipMaps = true;

        particleEffectParameter = new ParticleEffectLoader.ParticleEffectParameter();
        particleEffectParameter.imagesDir = Gdx.files.internal("data/animation/particles");

        MyFileHandleResolver resolver = new MyFileHandleResolver();
        manager = new AssetManager(resolver);

        // set the loaders for the generator and the fonts themselves
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        manager.setLoader(ParticleEffect.class, ".p", new ParticleEffectLoader(resolver));
        manager.setLoader(TextureAtlas.class, ".pack", new SMCTextureAtlasLoader(resolver));

        playMusic = PrefsManager.isPlayMusic();
        playSounds = PrefsManager.isPlaySounds();
    }

    public static void dispose()
    {
		manager.clear();
    }

}
