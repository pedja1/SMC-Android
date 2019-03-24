package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;

/**
 * Created by pedja on 10.4.15..
 */
public class LevelExit extends GameObject {
    public static final int LEVEL_EXIT_BEAM = 0;    // no animation ( f.e. a door or hole )
    public static final int LEVEL_EXIT_WARP = 1;    // rotated player moves slowly into the destination direction

    public final int type;
    public int cameraMotion;
    public final String direction;
    public String levelName, entry;
    private ParticleEffect effect;

    public LevelExit(float x, float y, float z, float width, float height, int type, String direction) {
        super(x, y, z, width, height);
        this.type = type;
        this.direction = direction;
        if (showParticles()) {
            MaryoGame.game.assets.load("data/animation/particles/pipe_star.p", ParticleEffect.class, Assets.PARTICLE_EFFECT_PARAMETER);
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (showParticles()) {
            effect.setPosition(position.x - drawRect.width / 2 - .1f, position.y);
            effect.draw(spriteBatch);
        }
    }

    @Override
    public void update(float delta) {
        if (showParticles()) {
            effect.update(delta);
        }
    }

    @Override
    public void initAssets() {
        if (showParticles()) {
            effect = new ParticleEffect(MaryoGame.game.assets.get("data/animation/particles/pipe_star.p", ParticleEffect.class));
            effect.start();
        }
    }

    @Override
    public void dispose() {
        if (showParticles()) {
            effect.dispose();
            effect = null;
        }
    }

    private boolean showParticles() {
        return type == LEVEL_EXIT_WARP && "down".equals(direction);
    }
}
