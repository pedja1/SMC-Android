package rs.pedjaapps.smc.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by pedja on 28.8.15..
 */
public class Shader
{
    public static ShaderProgram FREEZE_SHADER = new ShaderProgram(Gdx.files.internal("data/shaders/freeze.vert"), Gdx.files.internal("data/shaders/freeze.frag"));

    static
    {
        /*FREEZE_SHADER.begin();
        FREEZE_SHADER.setUniformf("u_contrast", 1.0f);
        FREEZE_SHADER.setUniformf("u_brightness", 0.0f);
        FREEZE_SHADER.end();*/
        ShaderProgram.pedantic = false;
    }
}
