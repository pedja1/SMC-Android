package rs.pedjaapps.smc.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by pedja on 28.8.15..
 */
public class Shader
{
    public static ShaderProgram FREEZE_SHADER;
    public static ShaderProgram SHAKE_SHADER;
    public static ShaderProgram NORMAL_BLEND_SHADER;
    public static ShaderProgram GLOW_SHADER;
    public static ShaderProgram GS_SHADER;

    public static void init()
    {
        ShaderProgram.pedantic = false;
        FREEZE_SHADER = new ShaderProgram(Gdx.files.internal("data/shaders/freeze.vert"), Gdx.files.internal("data/shaders/freeze.frag"));
        SHAKE_SHADER = new ShaderProgram(Gdx.files.internal("data/shaders/shake.vert"), Gdx.files.internal("data/shaders/shake.frag"));
        GS_SHADER = new ShaderProgram(Gdx.files.internal("data/shaders/gs.vert"), Gdx.files.internal("data/shaders/gs.frag"));
        GLOW_SHADER = new ShaderProgram(Gdx.files.internal("data/shaders/glow.vert"), Gdx.files.internal("data/shaders/glow.frag"));
        NORMAL_BLEND_SHADER = new ShaderProgram(Gdx.files.internal("data/shaders/normal_blend.vert"), Gdx.files.internal("data/shaders/normal_blend.frag"));
    }

    public static void dispose()
    {
        FREEZE_SHADER.dispose();
        NORMAL_BLEND_SHADER.dispose();
        GLOW_SHADER.dispose();
        GS_SHADER.dispose();
    }
}
