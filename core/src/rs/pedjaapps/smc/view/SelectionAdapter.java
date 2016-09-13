package rs.pedjaapps.smc.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.screen.LoadingScreen;
import rs.pedjaapps.smc.screen.MainMenuScreen;
import rs.pedjaapps.smc.shader.Shader;

public class SelectionAdapter
{
	public static final float ITEMS_ROW_CNT = 3;
	public static final float ITEMS_COL_CNT = 5;
    public static final float CAM_WIDTH = 1280;
    public static float CAM_HEIGHT;

	private MainMenuScreen mainMenuScreen;

    private OrthographicCamera cam;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;

	private Array<Level> items;
	int page = 0;

	private Texture txNextEnabled, txNextDisabled,
	txPrevEnabled, txPrevDisabled, txLock;
	private TextureRegion txItemBg;
    private BitmapFont font96, font32;
	private GlyphLayout font96Glyph, font32Glyph;

	private float selectionHeight;
    private float cellSize;
	private float cellPadding = 10;
	private float selectionX;
	private float selectionY;
	private float lockSize;

	private Rectangle backBounds;
	private boolean backPressed;

    private Level touchDownLevel;

    public static class Level
	{
		private Rectangle bounds = new Rectangle();
		public boolean isUnlocked, isTouched;
		public String levelId, levelNumber;
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
		float selectionWidth = selectionHeight * 1.6f;
		selectionX = CAM_WIDTH / 2 - selectionWidth / 2;

		cellSize = selectionHeight / 5 - cellPadding / 2;// padding of 5 on every side
		lockSize = cellSize / 4;

		backBounds = new Rectangle();
	}

	public void initAssets()
	{
		TextureAtlas hud = mainMenuScreen.game.assets.manager.get("data/hud/hud.pack");
		txItemBg = hud.findRegion("empty_square_button");
		txLock = mainMenuScreen.game.assets.manager.get("data/hud/lock.png");

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(mainMenuScreen.game.assets.resolver.resolve("data/fonts/GROBOLD.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 90;
        parameter.characters = "SECTLV";
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;
		parameter.borderWidth = 3f;
		parameter.borderColor = new Color(0, .5f, 0, 1);
        font96 = generator.generateFont(parameter);
		font96Glyph = new GlyphLayout();

		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36;
        parameter.characters = "0123456789BACK";
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;
		parameter.borderWidth = 2f;
        font32 = generator.generateFont(parameter);
		font32Glyph = new GlyphLayout();
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

        //font96.setColor(.5f, .5f, .5f, 1);
        //font96.draw(batch, "SELECT LEVEL", CAM_WIDTH / 2 + 2, CAM_HEIGHT * 0.9f - 2, 0, Align.center, false);
        font96.setColor(1, 1, 1, 1);
        font96.draw(batch, "SELECT LEVEL", CAM_WIDTH / 2, CAM_HEIGHT * 0.9f, 0, Align.center, false);

		int row = 0;
		int column = 0;
		int offset = 0;

        for (Level level : items)
		{
			if (offset > 40)break;//40 levels, 8x5

			float x = selectionX + column * cellSize + (column == 0 ? cellPadding / 2 : cellPadding / 2 + cellPadding / 2 * column);
			float y = (selectionY + selectionHeight - cellSize) - row * cellSize - (row == 0 ? cellPadding / 2 : cellPadding / 2 + cellPadding / 2 * row);

			level.bounds.set(x, y, cellSize, cellSize);

			if(!level.isUnlocked)
				batch.setShader(Shader.GS_SHADER);
			else if(level.isTouched)
				batch.setShader(Shader.GLOW_SHADER);

			batch.draw(txItemBg, x, y, cellSize, cellSize);

			batch.setShader(null);

			if(!level.isUnlocked)
			{
				font32.setColor(.7f, .7f, .7f, 1);
			}
			if(level.levelNumber == null)
			{
				level.levelNumber = String.valueOf(offset);
			}
			font32Glyph.setText(font32, level.levelNumber);
			font32.draw(batch, level.levelNumber, x + cellSize * .5f - font32Glyph.width * .5f, y + cellSize * .5f + font32Glyph.height * .5f);
			font32.setColor(Color.WHITE);

			if (!level.isUnlocked)
			{
				batch.draw(txLock, x + cellSize - lockSize * 1.5f, y + lockSize * .5f, lockSize, lockSize);
			}

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

		//BitmapFont.TextBounds bounds = font32.getBounds("BACK");
		font32Glyph.setText(font32, "BACK");
		backBounds.set(20, 20, font32Glyph.width + 40, font32Glyph.height + 40);
		//if(backPressed)batch.setShader(Shader.GLOW_SHADER);
		font32.draw(batch, "BACK", 40f, 40 + font32Glyph.height);
		//batch.setShader(null);

		batch.end();
	}

	public void touchUp(float x, float y)
	{
		//convert touch point to camera point
		x = x / (Gdx.graphics.getWidth() / CAM_WIDTH);
		y = y / (Gdx.graphics.getHeight() / CAM_HEIGHT);

		if (backBounds.contains(x, y))
		{
			mainMenuScreen.isSelection = false;
			backPressed = false;
			return;
		}
        if(touchDownLevel != null && touchDownLevel.bounds.contains(x, y))
        {
            mainMenuScreen.game.setScreen(new LoadingScreen(new GameScreen(mainMenuScreen.game, true, touchDownLevel.levelId), false, false));
            touchDownLevel.isTouched = false;
            touchDownLevel = null;
        }
	}

    public void touchDown(float x, float y)
    {
        //convert touch point to camera point
        x = x / (Gdx.graphics.getWidth() / CAM_WIDTH);
        y = y / (Gdx.graphics.getHeight() / CAM_HEIGHT);

		if (backBounds.contains(x, y))
		{
			backPressed = true;
			return;
		}

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

		if (!backBounds.contains(x, y))
		{
			backPressed = false;
		}

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
