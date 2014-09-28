package rs.pedjaapps.smc.view;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import rs.pedjaapps.smc.Assets;

public class SelectionAdapter
{
	public static final float ITEMS_ROW_CNT = 3;
	public static final float ITEMS_COL_CNT = 5;
    public static final float CAM_WIDTH = 1280;
    public static float CAM_HEIGHT;


    OrthographicCamera cam;
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	
	Array<Level> items;
	int page = 0;
	
	Texture txNextEnabled, txNextDisabled,
		txPrevEnabled, txPrevDisabled,
		txItemBg;
    BitmapFont font24;

    private float selectionSize;
    private float cellSize;

    public static class Level
	{
		public Vector2 position = new Vector2();
		public Rectangle bounds = new Rectangle();
		public boolean isUnlocked;
	}
	
	public SelectionAdapter(Array<Level> items)
	{
		this.items = items;
        CAM_HEIGHT = CAM_WIDTH / ((float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight());
		cam = new OrthographicCamera(CAM_WIDTH, CAM_HEIGHT);
		cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
		cam.update();
		
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

        cellSize = CAM_HEIGHT / 7;
        selectionSize = cellSize * 5;
	}
	
	public void loadAssets()
	{
		txItemBg = Assets.manager.get("data/hud/option.png");

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/fonts/Roboto-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 96;
        parameter.characters = "SelctLv";
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;
        font24 = generator.generateFont(parameter);
        generator.dispose();
	}
	
	public void render(float delta)
	{
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glEnable(GL20.GL_BLEND);

		shapeRenderer.setProjectionMatrix(cam.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0, 0, 0, 0.7f);
		shapeRenderer.rect(0, 0, CAM_WIDTH, CAM_HEIGHT);
		shapeRenderer.end();
		
		batch.setProjectionMatrix(cam.combined);
		batch.begin();

        font24.setColor(.5f, .5f, .5f, 1);
        font24.drawMultiLine(batch, "Select Level", CAM_WIDTH/2 + 2, CAM_HEIGHT * 0.9f - 2, 0, BitmapFont.HAlignment.CENTER);
        font24.setColor(1, 1, 1, 1);
        font24.drawMultiLine(batch, "Select Level", CAM_WIDTH/2, CAM_HEIGHT * 0.9f, 0, BitmapFont.HAlignment.CENTER);

        for(int i = page; page < (ITEMS_COL_CNT * ITEMS_ROW_CNT); i++)
		{
			if(i > items.size - 1)break;
			Level level = items.get(i);
			batch.draw(txItemBg, level.bounds.x, level.bounds.y, level.bounds.width, level.bounds.height);
		}

		batch.end();

	}


}
