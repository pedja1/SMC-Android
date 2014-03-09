package rs.papltd.smc.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import rs.papltd.smc.*;
import rs.papltd.smc.model.*;
import rs.papltd.smc.utility.*;

import rs.papltd.smc.model.Sprite;

/**
 * Created by pedja on 2/17/14.
 */
public class MainMenuScreen extends AbstractScreen implements InputProcessor
{
    Texture gameLogo;
    Texture gdxLogo;
    TextureRegion start, load, save;
    Rectangle startR, loadR, saveR;
    OrthographicCamera cam;
    SpriteBatch batch;
    MaryoGame game;
	Background bgr1, bgr2;
	BackgroundColor bgColor;
	Array<Sprite> sprites = new Array<Sprite>();

    public MainMenuScreen(MaryoGame game)
    {
		super(game);
        this.game = game;
        batch = new SpriteBatch();
        cam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        cam.position.set(Constants.CAMERA_WIDTH/2, Constants.CAMERA_HEIGHT/2, 0);
        cam.update();
		System.out.println("MMS : const");
		/*loadAssets(new ProgressListener(){

				@Override
				public void onProgressUpdate(int progress)
				{
					// TODO: Implement this method
				}

				@Override
				public void onLoadingFinished()
				{
					// TODO: Implement this method
				}
			});*/
    }
	
	private void generateSprites()
	{
		TextureAtlas pipesAtlas = Assets.manager.get("/pipes/orange/orange.pack");
        TextureAtlas sliderAtlas = Assets.manager.get("/ground/jungle-1/slider/green.pack");

		TextureRegion green_3_top = new TextureRegion(Assets.manager.get("/ground/green-3/ground/top/1.png", Texture.class));

		TextureRegion green_1_hedge_wild_medium = new TextureRegion(Assets.manager.get("/ground/green-1/hedges/wild-medium.png", Texture.class));

		TextureRegion orange_up = pipesAtlas.findRegion("up");
		TextureRegion orange_ver = pipesAtlas.findRegion("ver");
        TextureRegion jungle_1_big_plant_1 = new TextureRegion(Assets.manager.get("/ground/jungle-1/big-plant-1.png", Texture.class));

        TextureRegion sliderLeft = sliderAtlas.findRegion("1-green-left");
        TextureRegion sliderMiddle = sliderAtlas.findRegion("1-green-middle");
        TextureRegion sliderRight = new TextureRegion(sliderLeft);
        sliderRight.flip(true, false);

		Assets.loadedRegions.put("/ground/green-3/ground/top/1.png", green_3_top);
		Assets.loadedRegions.put("/pipes/orange/orange.pack:ver", orange_ver);
		Assets.loadedRegions.put("/pipes/orange/orange.pack:up", orange_up);
		Assets.loadedRegions.put("/ground/green-1/hedges/wild-medium.png", green_1_hedge_wild_medium);
        Assets.loadedRegions.put("/ground/jungle-1/big-plant-1.png", jungle_1_big_plant_1);
        Assets.loadedRegions.put("/ground/jungle-1/slider/green.pack:1-green-left", sliderLeft);
        Assets.loadedRegions.put("/ground/jungle-1/slider/green.pack:1-green-middle", sliderMiddle);
        Assets.loadedRegions.put("/ground/jungle-1/slider/green.pack:1-green-left-flipx", sliderRight);

		sprites.clear();
		for(int i = 0; i < 10; i++)
		{
			Vector2 position = new Vector2(i, 0);
            Sprite sprite = new Sprite(position, 1, 1);
			sprite.setTextureName("/ground/green-3/ground/top/1.png");
			sprites.add(sprite);
		}
		
		Sprite sprite = new Sprite(new Vector2(7.5f, 1), 1.2f, 0.6f);
		sprite.setTextureName("/pipes/orange/orange.pack:ver");
		sprites.add(sprite);
		
		sprite = new Sprite(new Vector2(7.5f, 1.6f), 1.2f, 0.6f);
		sprite.setTextureName("/pipes/orange/orange.pack:ver");
		sprites.add(sprite);
		
		sprite = new Sprite(new Vector2(7.5f, 2.2f), 1.2f, 0.6f);
		sprite.setTextureName("/pipes/orange/orange.pack:up");
		sprites.add(sprite);
		
		sprite = new Sprite(new Vector2(7.55f, 2.5f), 1.15f, 0.3f);
		sprite.setTextureName("/ground/green-1/hedges/wild-medium.png");
		sprites.add(sprite);

        sprite = new Sprite(new Vector2(1f, 1f), 2.4f, 3.4f);
        sprite.setTextureName("/ground/jungle-1/big-plant-1.png");
        sprites.add(sprite);

        sprite = new Sprite(new Vector2(1.5f, 3.8f), 0.25f, 0.35f);
        sprite.setTextureName("/ground/jungle-1/slider/green.pack:1-green-left");
        sprites.add(sprite);

        sprite = new Sprite(new Vector2(1.75f, 3.8f), 0.25f, 0.35f);
        sprite.setTextureName("/ground/jungle-1/slider/green.pack:1-green-middle");
        sprites.add(sprite);

        sprite = new Sprite(new Vector2(2f, 3.8f), 0.25f, 0.35f);
        sprite.setTextureName("/ground/jungle-1/slider/green.pack:1-green-middle");
        sprites.add(sprite);

        sprite = new Sprite(new Vector2(2.25f, 3.8f), 0.25f, 0.35f);
        sprite.setTextureName("/ground/jungle-1/slider/green.pack:1-green-middle");
        sprites.add(sprite);

        sprite = new Sprite(new Vector2(2.5f, 3.8f), 0.25f, 0.35f);
        sprite.setTextureName("/ground/jungle-1/slider/green.pack:1-green-left-flipx");
        sprites.add(sprite);

	}

    @Override
    public void show()
    {
		System.out.println("MMS : show()");
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta)
    {
		System.out.println("MMS : render() start");
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		bgColor.render(cam);
		
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
		
		bgr1.render(batch);
		bgr2.render(batch);
		
		drawSprites();
		
		batch.draw(gameLogo, 2, 5, 6, 2);
        batch.draw(gdxLogo, 0.2f, 0.1f, 2f, 0.5f);
		
        batch.draw(start, startR.x, startR.y, startR.width, startR.height);
        //batch.draw(options, optionsR.x, optionsR.y, optionsR.width, optionsR.height);
        batch.draw(load, loadR.x, loadR.y, loadR.width, loadR.height);
        batch.draw(save, saveR.x, saveR.y, saveR.width, saveR.height);
        //batch.draw(quit, quitR.x, quitR.y, quitR.width, quitR.height);

        batch.end();
		System.out.println("MMS : show() end");
    }
	
	private void drawSprites()
    {
        for (Sprite sprite : sprites)
        {
            TextureRegion region = Assets.loadedRegions.get(sprite.getTextureName());
            batch.draw(region, sprite.getPosition().x, sprite.getPosition().y, sprite.getBounds().width, sprite.getBounds().height);
        }
    }

    @Override
    public void resize(int width, int height)
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void dispose()
    {
        Gdx.input.setInputProcessor(null);
        sprites.clear();
        Assets.dispose();
        bgColor.dispose();
        batch.dispose();
        bgr1.dispose();
        bgr2.dispose();
        gameLogo.dispose();
        batch.dispose();
    }

    @Override
    public void loadAssets()
    {
        Assets.manager.load("/menu/menu.pack", TextureAtlas.class);
        Assets.manager.load("/game/background/more-hills.png", Texture.class);
        Assets.manager.load("/game/logo/smc-big-1.png", Texture.class);
        Assets.manager.load("/game/logo/libgdx.png", Texture.class);
        Assets.manager.load("/pipes/orange/orange.pack", TextureAtlas.class);
        Assets.manager.load("/ground/jungle-1/slider/green.pack", TextureAtlas.class);
        Assets.manager.load("/ground/green-3/ground/top/1.png", Texture.class);
        Assets.manager.load("/ground/green-1/hedges/wild-medium.png", Texture.class);
        Assets.manager.load("/ground/jungle-1/big-plant-1.png", Texture.class);
    }

    @Override
    public void afterLoadAssets()
    {
        TextureAtlas menuAtlas = Assets.manager.get("/menu/menu.pack");
        start = menuAtlas.findRegion("start");
        startR = new Rectangle(4.4f, 3.5f, 1.2f, 0.6f);

        //options = menuAtlas.findRegion("options");
        //optionsR = new Rectangle(3.8f, 4.3f, 2.4f, 0.6f);

        load = menuAtlas.findRegion("load");
        loadR = new Rectangle(4.4f, 2.8f, 1.2f, 0.6f);

        save = menuAtlas.findRegion("save");
        saveR = new Rectangle(4.4f, 2.1f, 1.2f, 0.6f);

        //quit = menuAtlas.findRegion("quit");
        //quitR = new Rectangle(4.4f, 1.3f, 1.2f, 0.6f);

        Texture bgTexture = Assets.manager.get("/game/background/more-hills.png");
        bgr1 = new Background(new Vector2(0, 0), bgTexture);
        bgr1.width = 7f;
        bgr1.height = 4.5f;

        bgr2 = new Background(new Vector2(bgr1.width, 0), bgTexture);
        bgr2.width = 7f;
        bgr2.height = 4.5f;

        bgColor = new BackgroundColor();
        bgColor.color1 = new Color(.117f, 0.705f, .05f, 0f);//color is 0-1 range where 1 = 255
        bgColor.color2 = new Color(0f, 0.392f, 0.039f, 0f);

        gameLogo = Assets.manager.get("/game/logo/smc-big-1.png");
        gdxLogo = Assets.manager.get("/game/logo/libgdx.png");

        generateSprites();
    }

    @Override
    public boolean keyDown(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

        float x = screenX / (screenWidth / Constants.CAMERA_WIDTH);
        float y = Constants.CAMERA_HEIGHT - (screenY / (screenHeight / Constants.CAMERA_HEIGHT));

        if(startR.contains(x, y))
        {
            game.setScreen(new GameScreen(game));
        }
        /*else if(optionsR.contains(x, y))
        {
            System.out.println("options");
        }*/
        else if(loadR.contains(x, y))
        {
            System.out.println("load");
        }
        else if(saveR.contains(x, y))
        {
            System.out.println("save");
        }
        /*else if(quitR.contains(x, y))
        {
            System.out.println("quit");
        }*/
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        return false;
    }
}
