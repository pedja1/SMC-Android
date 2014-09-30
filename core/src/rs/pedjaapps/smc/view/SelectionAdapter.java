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

    private float selectionWidth;
	private float selectionHeight;
    private float cellSize;
	private float cellPadding = 10;
	private float selectionX;
	private float selectionY;

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
		
		selectionY = CAM_HEIGHT * 0.1f;
		selectionHeight = selectionY + CAM_HEIGHT * 0.55f;
		selectionWidth = selectionHeight * 1.6f;//1.6 is aspect of grid 8x5
		selectionX = CAM_WIDTH / 2 - selectionWidth / 2;
		
		cellSize = selectionHeight / 5 - cellPadding / 2;// padding of 5 on every side
		
		System.out.println("x: " + selectionX + " y: " + selectionY + " width: " + selectionWidth + " height: " + selectionHeight + " cellSize: " + cellSize);
		
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

		int row = 0;
		int column = 0;
		
        for(/*Level level : items*/int i = 0; i < 40; i++)
		{
			//if(column > 40)break;//40 levels, 8x5
			
			float x = selectionX + column * cellSize + (column == 0 ? cellPadding / 2 : cellPadding / 2 + cellPadding / 2 * column);
			float y = (selectionY + selectionHeight - cellSize) - row * cellSize - (row == 0 ? cellPadding / 2 : cellPadding / 2 + cellPadding / 2 * row);
			
			batch.draw(txItemBg, x, y, cellSize, cellSize);
			
			if(column == 7)
			{
				row++;
				column = 0;
			}
			else
			{
				column++;
			}
			
		}

		
		batch.end();
		
		shapeRenderer.setProjectionMatrix(cam.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

		shapeRenderer.setColor(new Color(0, 1, 0, 1));
		shapeRenderer.rect(selectionX, selectionY, selectionWidth, selectionHeight);
		
		shapeRenderer.end();

	}


}
