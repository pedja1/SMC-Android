package rs.pedjaapps.smc.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.utility.Constants;

public class Background {
    public static final int BG_NONE = 0;// nothing
    public static final int BG_IMG_TOP = 3;// tiles only horizontal and is on the top
    public static final int BG_IMG_BOTTOM = 1;// tiles only horizontal and is on the bottom
    public static final int BG_IMG_ALL = 2;// tiles into all directions
    public static final int BG_GR_VER = 103;// vertical gradient
    public static final int BG_GR_HOR = 104;// horizontal gradient

    private boolean cameraPositioned;
    public Vector2 position, speed;
    public Texture texture;
    public String textureName;
    public float width;
    public float height;
    private Vector3 oldGameCamPos = new Vector3();

    private Color[] colors;
    private ShapeRenderer renderer;
    public OrthographicCamera bgCam;

    private float widthMul;
    private float heightMul;
    private int type;

    public Background(int type) {
        this(null, null, null, 0, 0, 0, 0, type);
    }

    public Background(Vector2 position, Vector2 speed, String textureName, float width, float height, float levelWidth, float levelHeight, int type) {
        this.speed = speed;
        this.position = position;
        this.textureName = textureName;
        this.type = type;
        this.width = width;
        this.height = height;
        bgCam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        bgCam.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        bgCam.update();

        widthMul = levelWidth / width;
        heightMul = getVerticalWarp() == Texture.TextureWrap.Repeat ? levelHeight / height : 1;
        if (isColor()) {
            renderer = new ShapeRenderer();
        }
    }

    public void setColors(Color color1, Color color2) {
        if (type == BG_GR_HOR) {
            colors = new Color[]{color2, color2, color1, color1};
        } else {
            colors = new Color[]{color2, color1, color1, color2};
        }
    }

    public void resize(OrthographicCamera gameCam) {
        bgCam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        bgCam.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        bgCam.position.set(gameCam.position.x, gameCam.position.y, 0);
        bgCam.update();
    }

    public void render(OrthographicCamera gameCam, SpriteBatch spriteBatch) {
        if (isColor()) {
            renderer.setProjectionMatrix(gameCam.combined);
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.rect(gameCam.position.x - Constants.CAMERA_WIDTH / 2, gameCam.position.y - Constants.CAMERA_HEIGHT / 2,
                    Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT, colors[0],
                    colors[1], colors[2], colors[3]);
            renderer.end();
        }

        if (texture != null && isTexture()) {
            bgCam.position.add((gameCam.position.x - oldGameCamPos.x) * speed.x, (gameCam.position.y - oldGameCamPos.y) * speed.y, 0);
            if (bgCam.position.x < bgCam.viewportWidth * .5f) {
                bgCam.position.x = bgCam.viewportWidth * .5f;
            }
            if (bgCam.position.y < bgCam.viewportHeight * .5f) {
                bgCam.position.y = bgCam.viewportHeight * .5f;
            }
            bgCam.update();
            oldGameCamPos.set(gameCam.position);

            spriteBatch.setProjectionMatrix(bgCam.combined);
            spriteBatch.begin();

            spriteBatch.draw(texture, position.x, position.y, width * widthMul, height * heightMul, 0, 0, MathUtils.ceil(texture.getWidth() * widthMul), MathUtils.ceil(texture.getHeight() * heightMul), false, false);

            spriteBatch.end();
        }
    }

    public void onAssetsLoaded(OrthographicCamera gameCam) {
        if (textureName != null && isTexture()) {
            if (cameraPositioned)
                return;
            texture = MaryoGame.game.assets.get(textureName);
            texture.setWrap(getHorizontalWrap(), getVerticalWarp());
            bgCam.position.set(gameCam.position.x, gameCam.position.y, 0);
            oldGameCamPos.set(gameCam.position);
            cameraPositioned = true;
        }
    }

    public void dispose() {
        if (renderer != null) {
            renderer.dispose();
            renderer = null;
        }
    }

    private Texture.TextureWrap getHorizontalWrap() {
        if (type == BG_IMG_ALL || type == BG_IMG_BOTTOM || type == BG_IMG_TOP)
            return Texture.TextureWrap.Repeat;
        return Texture.TextureWrap.ClampToEdge;
    }

    private Texture.TextureWrap getVerticalWarp() {
        if (type == BG_IMG_ALL)
            return Texture.TextureWrap.Repeat;
        return Texture.TextureWrap.ClampToEdge;
    }

    private boolean isTexture() {
        return type == BG_IMG_ALL || type == BG_IMG_BOTTOM || type == BG_IMG_TOP;
    }

    private boolean isColor() {
        return type == BG_GR_VER || type == BG_GR_HOR;
    }
}
