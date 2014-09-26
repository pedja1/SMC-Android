package rs.pedjaapps.smc.view;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import rs.pedjaapps.smc.Assets;

public class SelectionAdapter
{
	public static final float ITEMS_ROW_CNT = 3;
	public static final float ITEMS_COL_CNT = 5;
	
	OrthographicCamera cam;
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	BitmapFont font;
	
	Array<Level> items;
	int page = 0;
	
	Texture txNextEnabled, txNextDisabled,
		txPrevEnabled, txPrevDisabled,
		txItemBg;
	
	public static class Level
	{
		public Vector2 position = new Vector2();
		public Rectangle bounds = new Rectangle();
		public boolean isUnlocked;
	}
	
	public SelectionAdapter(Array<Level> items)
	{
		this.items = items;
		cam = new OrthographicCamera(ITEMS_COL_CNT+2, ITEMS_ROW_CNT+2);
		cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
		cam.update();
		
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
	}
	
	public void loadAssets()
	{
		txItemBg = Assets.manager.get("data/hud/option.png");
		
		
		Texture fontTexture = Assets.manager.get("data/fonts/dejavu_sans.png");
        fontTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		//font = Assets.manager.get("data/fonts/dejavu_sans.fnt");
        font = new BitmapFont(Gdx.files.internal("data/fonts/dejavu_sans.fnt"), new TextureRegion(fontTexture), false);
		
        font.setColor(Color.WHITE);
        font.setScale(0.007f);
	}
	
	public void render(float delta)
	{
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glEnable(GL20.GL_BLEND);

		shapeRenderer.setProjectionMatrix(cam.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0, 0, 0, 0.5f);
		shapeRenderer.rect(0, 0, ITEMS_COL_CNT+2, ITEMS_ROW_CNT+2);
		shapeRenderer.end();
		
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		
		font.drawMultiLine(batch, "Select Level", 1, 5, 0, BitmapFont.HAlignment.LEFT);
		
		for(int i = page; page < (ITEMS_COL_CNT * ITEMS_ROW_CNT); i++)
		{
			if(i > items.size - 1)break;
			Level level = items.get(i);
			batch.draw(txItemBg, level.bounds.x, level.bounds.y, level.bounds.width, level.bounds.height);
		}
		
		batch.end();
	}
}
