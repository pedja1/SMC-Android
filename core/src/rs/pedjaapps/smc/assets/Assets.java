package rs.pedjaapps.smc.assets;

import com.badlogic.gdx.Application;
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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * Created by pedja on 2/15/14.
 */
public class Assets
{
    public AssetManager manager;
    public TextureLoader.TextureParameter textureParameter;
    public ParticleEffectLoader.ParticleEffectParameter particleEffectParameter;
    public ZipFile archive;
    public ArchiveFileHandleResolver resolver;

    public Assets(String androidAssetsZipFilePath)
    {
        textureParameter = new TextureLoader.TextureParameter();
        //textureParameter.genMipMaps = true;
        textureParameter.magFilter = Texture.TextureFilter.Linear;
        textureParameter.minFilter = Texture.TextureFilter.Linear;

        try
        {
            File file;
            if(Gdx.app.getType() == Application.ApplicationType.iOS || Gdx.app.getType() == Application.ApplicationType.Desktop || Gdx.app.getType() == Application.ApplicationType.WebGL)
            {
                file = Gdx.files.internal("assets.zip").file();
            }
            else if(Gdx.app.getType() == Application.ApplicationType.Android)
            {
                file = Gdx.files.external(androidAssetsZipFilePath).file();
            }
            else
            {
                throw new IllegalStateException("Platform " + Gdx.app.getType() + " is not supported");
            }
            archive = new ZipFile(file);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Unable to load assets: " + e.getMessage());
        }
        resolver = new ArchiveFileHandleResolver(archive);
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
