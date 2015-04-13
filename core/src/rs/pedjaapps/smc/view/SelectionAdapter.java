package rs.pedjaapps.smc.view;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.screen.LoadingScreen;
import rs.pedjaapps.smc.screen.MainMenuScreen;

public class SelectionAdapter
{
	public static final float ITEMS_ROW_CNT = 3;
	public static final float ITEMS_COL_CNT = 5;
    public static final float CAM_WIDTH = 1280;
    public static float CAM_HEIGHT;

	MainMenuScreen mainMenuScreen;

    OrthographicCamera cam;
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;

	Array<Level> items;
	int page = 0;

	Texture txNextEnabled, txNextDisabled,
	txPrevEnabled, txPrevDisabled,
	txItemBg, txItemBgSelected, txLock;
    BitmapFont font96, font32;

    private float selectionWidth;
	private float selectionHeight;
    private float cellSize;
	private float cellPadding = 10;
	private float selectionX;
	private float selectionY;
	private float lockSize;

	Rectangle backBounds;

    private Level touchDownLevel;

    public static class Level
	{
		private Rectangle bounds = new Rectangle();
		public boolean isUnlocked, isTouched;
		public String levelId;
	}

	public SelectionAdapter(Array<Level> items, MainMenuScreen mainMenuScreen)
	{
		this.items = items;
		this.mainMenuScreen = mainMenuScreen;
        CAM_HEIGHT = CAM_WIDTH / ((float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight());
		cam = new OrthographicCamera(CAM_WIDTH, CAM_HEIGHT);
		cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
		cam.update();

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		selectionY = CAM_HEIGHT * 0.1f;
		selectionHeight = selectionY + CAM_HEIGHT * 0.55f;
		selectionWidth = selectionHeight * 1.6f;//1.6 is aspect of grid 8x5
		selectionX = CAM_WIDTH / 2 - selectionWidth / 2;

		cellSize = selectionHeight / 5 - cellPadding / 2;// padding of 5 on every side
		lockSize = cellSize / 4;

		backBounds = new Rectangle();
	}

	public void initAssets()
	{
		txItemBg = Assets.manager.get("data/hud/option.png");
		txItemBgSelected = Assets.manager.get("data/hud/option_selected.png");
		txLock = Assets.manager.get("data/hud/lock.png");

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/fonts/Roboto-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 96;
        parameter.characters = "SECTLV";
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;
        font96 = generator.generateFont(parameter);

		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36;
        parameter.characters = "0123456789BACK";
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;
        font32 = generator.generateFont(parameter);
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

        font96.setColor(.5f, .5f, .5f, 1);
        font96.drawMultiLine(batch, "SELECT LEVEL", CAM_WIDTH / 2 + 2, CAM_HEIGHT * 0.9f - 2, 0, BitmapFont.HAlignment.CENTER);
        font96.setColor(1, 1, 1, 1);
        font96.drawMultiLine(batch, "SELECT LEVEL", CAM_WIDTH / 2, CAM_HEIGHT * 0.9f, 0, BitmapFont.HAlignment.CENTER);

		int row = 0;
		int column = 0;
		int offset = 0;

        for (Level level : items)
		{
			if (offset > 40)break;//40 levels, 8x5

			float x = selectionX + column * cellSize + (column == 0 ? cellPadding / 2 : cellPadding / 2 + cellPadding / 2 * column);
			float y = (selectionY + selectionHeight - cellSize) - row * cellSize - (row == 0 ? cellPadding / 2 : cellPadding / 2 + cellPadding / 2 * row);

			level.bounds.set(x, y, cellSize, cellSize);

			batch.draw(level.isTouched ? txItemBgSelected : txItemBg, x, y, cellSize, cellSize);

			if (!level.isUnlocked)
			{
				batch.draw(txLock, x + cellSize - lockSize, y, lockSize, lockSize);
			}

			BitmapFont.TextBounds bounds = font32.getBounds(offset + 1 + "");
			font32.draw(batch, offset + 1 + "", x + cellSize / 2 - bounds.width / 2, y + cellSize / 2 + bounds.height / 2);

			if (column == 7)
			{
				row++;
				column = 0;
			}
			else
			{
				column++;
			}
			offset++;
		}

		BitmapFont.TextBounds bounds = font32.getBounds("BACK");
		backBounds.set(20, 20, bounds.width + 40, bounds.height + 40);
		font32.draw(batch, "BACK", 40f, 40 + bounds.height);

		batch.end();

		//debug
		if (mainMenuScreen.debug)
		{
			shapeRenderer.setProjectionMatrix(cam.combined);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

			//TODO ALLOC "new Color"
			shapeRenderer.setColor(0, 1, 0, 1);
			shapeRenderer.rect(backBounds.x, backBounds.y, backBounds.width, backBounds.height);

			for (Level level : items)
			{
				shapeRenderer.rect(level.bounds.x, level.bounds.y, level.bounds.width, level.bounds.height);
			}

			shapeRenderer.end();
		}
		//debug end
	}

	public void touchUp(float x, float y)
	{
		//convert touch point to camera point
		x = x / (Gdx.graphics.getWidth() / CAM_WIDTH);
		y = y / (Gdx.graphics.getHeight() / CAM_HEIGHT);

		if (backBounds.contains(x, y))
		{
			mainMenuScreen.isSelection = false;
			return;
		}
        if(touchDownLevel != null && touchDownLevel.bounds.contains(x, y))
        {
            mainMenuScreen.game.setScreen(new LoadingScreen(new GameScreen(mainMenuScreen.game, true, touchDownLevel.levelId), false));
            touchDownLevel.isTouched = false;
            touchDownLevel = null;
        }
	}

    public void touchDown(float x, float y)
    {
        //convert touch point to camera point
        x = x / (Gdx.graphics.getWidth() / CAM_WIDTH);
        y = y / (Gdx.graphics.getHeight() / CAM_HEIGHT);

        for (Level level : items)
        {
            if (level.bounds.contains(x, y) && level.isUnlocked)
            {
                level.isTouched = true;
                touchDownLevel = level;
                break;
            }
        }
    }

    public void touchDragged(float x, float y)
    {
    	//convert touch point to camera point
		x = x / (Gdx.graphics.getWidth() / CAM_WIDTH);
		y = y / (Gdx.graphics.getHeight() / CAM_HEIGHT);

		if(touchDownLevel != null && !touchDownLevel.bounds.contains(x, y))
		{
			touchDownLevel = null;
			for (Level level : items)
			{
				level.isTouched = false;
			}
		}
    }


}
