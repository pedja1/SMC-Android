package rs.pedjaapps.smc.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by pedja on 28.8.15..
 */
public class Shader
{
    public static ShaderProgram FREEZE_SHADER = new ShaderProgram(Gdx.files.internal("data/shaders/freeze.vert"), Gdx.files.internal("data/shaders/freeze.frag"));
    public static ShaderProgram STAR_GLOW_SHADER = new ShaderProgram(Gdx.files.internal("data/shaders/star_glow.vert"), Gdx.files.internal("data/shaders/star_glow.frag"));

    static
    {
        ShaderProgram.pedantic = false;
    }
}
