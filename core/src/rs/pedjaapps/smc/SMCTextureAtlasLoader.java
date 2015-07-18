/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package rs.pedjaapps.smc;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Page;
import com.badlogic.gdx.utils.Array;

/** {@link AssetLoader} to load {@link TextureAtlas} instances. Passing a {@link TextureAtlasParameter} to
 * {@link AssetManager#load(String, Class, AssetLoaderParameters)} allows to specify whether the atlas regions should be flipped
 * on the y-axis or not.
 * @author mzechner */
public class SMCTextureAtlasLoader extends SynchronousAssetLoader<TextureAtlas, SMCTextureAtlasLoader.TextureAtlasParameter>
{
    public SMCTextureAtlasLoader(FileHandleResolver resolver)
    {
        super(resolver);
    }

    TextureAtlasData data;

    @Override
    public TextureAtlas load(AssetManager assetManager, String fileName, FileHandle file, TextureAtlasParameter parameter)
    {
        for (Page page : data.getPages())
        {
            page.texture = assetManager.get(page.textureFile.path().replaceAll("\\\\", "/"), Texture.class);
        }

        return new TextureAtlas(data);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle atlasFile, TextureAtlasParameter parameter)
    {
        FileHandle imgDir = atlasFile.parent();

        if (parameter != null)
            data = new TextureAtlasData(atlasFile, imgDir, parameter.flip);
        else
        {
            data = new TextureAtlasData(atlasFile, imgDir, false);
        }

        Array<AssetDescriptor> dependencies = new Array<>();
        for (Page page : data.getPages())
        {
            TextureLoader.TextureParameter params = new TextureLoader.TextureParameter();
            params.format = page.format;
            params.genMipMaps = page.useMipMaps;
            params.minFilter = page.minFilter;
            params.magFilter = page.magFilter;

            if(parameter != null)
            {
                params.format = parameter.format != null ? (parameter.overwrite || params.format == null ? parameter.format : params.format) : params.format;
                params.genMipMaps = parameter.genMipMaps != null ? (parameter.overwrite ? parameter.genMipMaps : params.genMipMaps) : params.genMipMaps;
                params.minFilter = parameter.minFilter != null ? (parameter.overwrite || params.minFilter == null ? parameter.minFilter : params.minFilter) : params.minFilter;
                params.magFilter = parameter.magFilter != null ? (parameter.overwrite || params.magFilter == null ? parameter.magFilter : params.magFilter) : params.magFilter;
            }
            dependencies.add(new AssetDescriptor<>(page.textureFile, Texture.class, params));
        }
        return dependencies;
    }

    static public class TextureAtlasParameter extends TextureAtlasLoader.TextureAtlasParameter
    {
        private final boolean overwrite;
        public Pixmap.Format format = null;
        public Boolean genMipMaps = null;
        public Texture.TextureFilter minFilter = null;
        public Texture.TextureFilter magFilter = null;

        public TextureAtlasParameter(boolean overwrite)
        {
            this.overwrite = overwrite;
        }

        public TextureAtlasParameter(boolean flip, boolean overwrite)
        {
            super(flip);
            this.overwrite = overwrite;
        }
    }

}
