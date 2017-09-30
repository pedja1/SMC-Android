package rs.pedjaapps.smc.view;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.HashSet;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.shader.Shader;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.HUDTimeText;
import rs.pedjaapps.smc.utility.MyMathUtils;
import rs.pedjaapps.smc.utility.NAHudText;
import rs.pedjaapps.smc.utility.NATypeConverter;
import rs.pedjaapps.smc.utility.PrefsManager;

import static com.badlogic.gdx.Gdx.gl;

public class HUD
{
	private static final float UPDATE_FREQ = .15f;
	private float noUpdateDuration;

	private World world;
	
	private TextureRegion pause, play, fire, jump, up, down, right,
		left, soundOn, soundOff, musicOn, musicOff, fireP, jumpP, upP,
		downP, leftP, rightP;
    public Rectangle pauseR, playR, fireR, jumpR, upR, downR, rightR,
		leftR, soundR, musicR, fireRT, jumpRT;
	private Texture itemBox, maryoL, goldM;
	private Rectangle itemBoxR, maryoLR;
    public Array<Vector2> leftPolygon = new Array<Vector2>(5);
    public Array<Vector2> rightPolygon = new Array<Vector2>(5);
    public Array<Vector2> upPolygon = new Array<Vector2>(5);
    public Array<Vector2> downPolygon = new Array<Vector2>(5);

	private SpriteBatch batch;
	public Stage stage;

	private ShapeRenderer shapeRenderer = new ShapeRenderer();

	public enum Key
	{
		none, pause, fire, jump, left, right, up, down, play, sound, music
	}

	private HashSet<Key> pressedKeys = new HashSet<>(Key.values().length);
	
	private float stateTime;
	public boolean updateTimer = true;

	private int points;
	private String pointsText;
	private final NATypeConverter<Integer> coins = new NATypeConverter<>();
	private final NAHudText<Integer> lives = new NAHudText<>(null, "x");
	private final HUDTimeText time = new HUDTimeText();

	private Label ttsLabel;
	private Label pauseLabel;
	private Label scoreLabel;
	private Label coinsLabel;
	private Label timeLabel;
	private Label livesLabel;
	private Image imItemBox;
	private Image imWaffles;
	private Image imMaryoL;

	public HUD(World world)
	{
		this.world = world;
		batch = new SpriteBatch();
		stage = new Stage(new FitViewport(MaryoGame.NATIVE_WIDTH, MaryoGame.NATIVE_HEIGHT));
		setBounds();
	}

	private void setBounds()
	{
		float width = MaryoGame.NATIVE_HEIGHT / 10f;
		float height = width;
		float x = MaryoGame.NATIVE_WIDTH - width * 1.5f;
		float y = MaryoGame.NATIVE_HEIGHT - height * 1.5f;
		pauseR = new Rectangle(x, y, width, height);

		width = MaryoGame.NATIVE_HEIGHT / 7f;
		height = width;
		x = MaryoGame.NATIVE_WIDTH - width * 1.5f;
		y = MaryoGame.NATIVE_HEIGHT - height * 5f;
		fireR = new Rectangle(x, y, width, height);
		float bX = x - width * .25f;
		float bY = y - height * .25f;
		float bW = width + width *.5f;
		float bH = height + height * 0.5f;
		fireRT = new Rectangle(bX, bY, bW, bH);

		x = bX - width * 1.25f;
		y = bY - height;
		bY = bY + bH * .25f;
		jumpR = new Rectangle(x, y, width, height);
		jumpRT = new Rectangle(bX - bW, bY - bH, bW, bH);

		x = width / 2f;
		y = height * 1.5f;
		width = width * 1.24f;
		leftR = new Rectangle(x, y, width, height);
		leftPolygon.clear();
		leftPolygon.add(new Vector2(x, y + height));
		leftPolygon.add(new Vector2(x + width - x / 100 * 23.25f, y + height));
		leftPolygon.add(new Vector2(x + width, y + height / 2));
		leftPolygon.add(new Vector2(x + width - x / 100 * 23.25f, y));
		leftPolygon.add(new Vector2(x, y));

		x = x + width + width / 4f;
		rightR = new Rectangle(x, y, width, height);
		rightPolygon.clear();
		rightPolygon.add(new Vector2(x, y + height / 2));
		rightPolygon.add(new Vector2(x + x / 100 * 23.25f, y + height));//x / 100 * 23.25%
		rightPolygon.add(new Vector2(x + width, y + height));
		rightPolygon.add(new Vector2(x + width, y));
		rightPolygon.add(new Vector2(x + x / 100 * 23.25f, y));

		width = MaryoGame.NATIVE_HEIGHT / 7f;
		height = width * 1.24f;
		x = x - width / 2f - width / 8f;
		y = y + height / 2f;
		upR = new Rectangle(x, y, width, height);
		upPolygon.clear();
		upPolygon.add(new Vector2(x, y + height));
		upPolygon.add(new Vector2(x + width, y + height));
		upPolygon.add(new Vector2(x + width, y + y / 100 * 23.25f));
		upPolygon.add(new Vector2(x + width / 2, y));
		upPolygon.add(new Vector2(x, y + y / 100 * 23.25f));

		y = y - height - height / 4f;
		downR = new Rectangle(x, y, width, height);
		downPolygon.clear();
		downPolygon.add(new Vector2(x + width / 2, y + width));
		downPolygon.add(new Vector2(x + width, y + height - y / 100 * 23.25f));
		downPolygon.add(new Vector2(x + width, y));
		downPolygon.add(new Vector2(x, y));
		downPolygon.add(new Vector2(x, y + height - y / 100 * 23.25f));

		width = MaryoGame.NATIVE_HEIGHT / 10f;
		height = width;
		x = MaryoGame.NATIVE_WIDTH / 2 - width / 2;
		y = height * 2.1f + height;
		soundR = new Rectangle(x, y, width, height);

		x = x * 1.20f;
		musicR = new Rectangle(x, y, width, height);

		x = soundR.x * 0.80f;
		playR = new Rectangle(x, y, width, height);

		float ibSize = MaryoGame.NATIVE_WIDTH / 14;
		itemBoxR = new Rectangle(MaryoGame.NATIVE_WIDTH / 2 - ibSize, MaryoGame.NATIVE_HEIGHT - ibSize - ibSize / 5, ibSize, ibSize);

		float maryoLSize = itemBoxR.height / 2.5f;
		maryoLR = new Rectangle(pauseR.x - maryoLSize * 3, itemBoxR.y + itemBoxR.height - maryoLSize - maryoLSize / 2, maryoLSize * 2, maryoLSize);
	}

    public void resize(int width, int height)
    {
		stage.getViewport().update(width, height, true);
    }
	
	public void loadAssets()
	{
		world.screen.game.assets.manager.load("data/sounds/item/live_up_2.mp3", Sound.class);

		world.screen.game.assets.manager.load("data/hud/controls.pack", TextureAtlas.class);
		world.screen.game.assets.manager.load("data/hud/hud.pack", TextureAtlas.class);
		world.screen.game.assets.manager.load("data/game/itembox.png", Texture.class, world.screen.game.assets.textureParameter);
        world.screen.game.assets.manager.load("data/game/maryo_l.png", Texture.class, world.screen.game.assets.textureParameter);
        world.screen.game.assets.manager.load("data/game/gold_m.png", Texture.class, world.screen.game.assets.textureParameter);
		world.screen.game.assets.manager.load("data/game/game_over.png", Texture.class, world.screen.game.assets.textureParameter);
	}

    public void initAssets()
	{
		// already initialized
		if (pauseLabel != null)
			return;

		TextureAtlas atlas = world.screen.game.assets.manager.get("data/hud/controls.pack", TextureAtlas.class);
		TextureAtlas hud = world.screen.game.assets.manager.get("data/hud/hud.pack", TextureAtlas.class);
		pause = hud.findRegion("pause");
		play = hud.findRegion("play");
		musicOn = hud.findRegion("music");
        musicOff = hud.findRegion("music_off");
        
		soundOn = hud.findRegion("sound");
        soundOff = hud.findRegion("sound_off");

		if (MaryoGame.showOnScreenControls())
		{
			fire = atlas.findRegion("fire");
			fireP = atlas.findRegion("fire-pressed");
			jump = atlas.findRegion("jump");
			jumpP = atlas.findRegion("jump-pressed");
			left = atlas.findRegion("dpad-left");
			leftP = atlas.findRegion("dpad-left-pressed");
			right = new TextureRegion(left);
			right.flip(true, false);
			rightP = new TextureRegion(leftP);
			rightP.flip(true, false);
			up = atlas.findRegion("dpad-up");
			upP = atlas.findRegion("dpad-up-pressed");
			down = new TextureRegion(up);
			down.flip(false, true);
			downP = new TextureRegion(upP);
			downP.flip(false, true);
		}

		itemBox = world.screen.game.assets.manager.get("data/game/itembox.png");
		maryoL = world.screen.game.assets.manager.get("data/game/maryo_l.png");
		goldM = world.screen.game.assets.manager.get("data/game/gold_m.png");
		
		Texture.TextureFilter filter = Texture.TextureFilter.Linear;
		itemBox.setFilter(filter, filter);
		maryoL.setFilter(filter, filter);
		goldM.setFilter(filter, filter);

		Skin skin = world.screen.game.assets.manager.get(Assets.SKIN_HUD, Skin.class);

		ttsLabel = new Label("TOUCH ANYWHERE TO START", skin, Assets.LABEL_BORDER60);
		ttsLabel.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
		ttsLabel.addAction(Actions.forever(Actions.sequence(Actions.alpha(.3f, 1f), Actions.fadeIn(1f))));
		stage.addActor(ttsLabel);

		pauseLabel = new Label("PAUSE", skin, Assets.LABEL_BORDER60);
		pauseLabel.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
		pauseLabel.addAction(Actions.forever(Actions.sequence(Actions.alpha(.3f, 1f), Actions.fadeIn(1f))));
		stage.addActor(pauseLabel);

		scoreLabel = new Label(formatPointsString(0), skin, Assets.LABEL_BORDER25);
		float padX = stage.getWidth() * 0.03f;
		scoreLabel.setPosition(padX, maryoLR.y);
		stage.addActor(scoreLabel);

		imItemBox = new Image(itemBox);
		imItemBox.setPosition(itemBoxR.x, itemBoxR.y);
		imItemBox.setSize(itemBoxR.width, itemBoxR.height);
		stage.addActor(imItemBox);

		imWaffles = new Image(goldM);
		imWaffles.setPosition(padX * 2 + scoreLabel.getWidth(), scoreLabel.getY());
		stage.addActor(imWaffles);

		coinsLabel = new Label(" ", skin, Assets.LABEL_BORDER25);
		coinsLabel.setPosition(imWaffles.getX() + imWaffles.getWidth(), scoreLabel.getY());
		stage.addActor(coinsLabel);

		imMaryoL = new Image(maryoL);
		imMaryoL.setPosition(maryoLR.x, maryoLR.y);
		imMaryoL.setSize(maryoLR.width, maryoLR.height);
		stage.addActor(imMaryoL);

		livesLabel = new Label("0x", skin, Assets.LABEL_BORDER25);
		livesLabel.setPosition(maryoLR.x, scoreLabel.getY(), Align.bottomRight);
		stage.addActor(livesLabel);

		time.update(0);
		timeLabel = new Label(new String(time.getChars()), skin, Assets.LABEL_BORDER25);
		timeLabel.setPosition(livesLabel.getX() - padX, scoreLabel.getY(), Align.bottomRight);
		stage.addActor(timeLabel);

		//TODO popuptextbox
	}

	public void render(GameScreen.GAME_STATE gameState, float deltaTime)
	{
		ttsLabel.setVisible(gameState == GameScreen.GAME_STATE.GAME_READY);
		pauseLabel.setVisible(gameState == GameScreen.GAME_STATE.GAME_PAUSED);

		boolean isInGame = !(gameState == GameScreen.GAME_STATE.GAME_READY
				|| gameState == GameScreen.GAME_STATE.GAME_PAUSED);
		scoreLabel.setVisible(isInGame);
		imItemBox.setVisible(isInGame);
		coinsLabel.setVisible(isInGame);
		imWaffles.setVisible(isInGame);
		timeLabel.setVisible(isInGame);
		imMaryoL.setVisible(isInGame);
		livesLabel.setVisible(isInGame);

		if (gameState == GameScreen.GAME_STATE.GAME_PAUSED)
		{
			drawPauseOverlay();
		}
		else
		{
			if(updateTimer)stateTime += deltaTime;
			batch.setProjectionMatrix(stage.getCamera().combined);
			batch.begin();
			if(pressedKeys.contains(Key.pause))batch.setShader(Shader.GLOW_SHADER);
			batch.draw(pause, pauseR.x, pauseR.y, pauseR.width, pauseR.height);
			batch.setShader(null);
			if (MaryoGame.showOnScreenControls())
			{
				batch.draw(pressedKeys.contains(Key.fire) ? fireP : fire, fireR.x, fireR.y , fireR.width, fireR.height);
				batch.draw(pressedKeys.contains(Key.jump) ? jumpP : jump, jumpR.x, jumpR.y , jumpR.width, jumpR.height);
				batch.draw(pressedKeys.contains(Key.left) ? leftP : left, leftR.x, leftR.y , leftR.width, leftR.height);
				batch.draw(pressedKeys.contains(Key.right) ? rightP : right, rightR.x, rightR.y , rightR.width, rightR.height);
				batch.draw(pressedKeys.contains(Key.up) ? upP : up, upR.x, upR.y, upR.width, upR.height);
				batch.draw(pressedKeys.contains(Key.down) ? downP : down, downR.x, downR.y, downR.width, downR.height);
			}
			//if(GameSave.getItem() != null)
			//	batch.setColor(Color.RED);

			noUpdateDuration = noUpdateDuration + deltaTime;

			if (noUpdateDuration >= UPDATE_FREQ) {
				noUpdateDuration = 0;
				// points
				//TODO nicht jedes Mal ändern
				pointsText = formatPointsString(GameSave.save.points);
				scoreLabel.setText(pointsText);

				//coins
				//TODO nicht jedes Mal ändern
				String coins = this.coins.toString(GameSave.getCoins());
				coinsLabel.setText(coins);

				//time
				//TODO nicht jedes Mal ändern und sowieso besser!
				time.update(stateTime);
				timeLabel.setText(new String(time.getChars()));

				//lives
				//TODO nicht jedes Mal ändern und sowieso besser!
				livesLabel.setText(this.lives.toString(MyMathUtils.max(GameSave.save.lifes, 0)));
			}

            //draw item if any
            if(GameSave.getItem() != null)
            {
                float w = imItemBox.getWidth() * 0.5f;
                float h = imItemBox.getHeight() * 0.5f;
                float x = imItemBox.getX() + w * .5f;
                float y = imItemBox.getY() + h * .5f;
                batch.draw(GameSave.getItem().texture, x, y, w, h);
            }

			batch.end();
		}
		if(PrefsManager.isDebug())drawDebug();

		stage.getViewport().apply();
		stage.act(deltaTime);
		stage.draw();
	}
	
	private void drawDebug()
	{
	}

    private String formatPointsString(int points)
    {
        if(pointsText != null && this.points == points)
		{
			return pointsText;
		}
		else
		{
			this.points = points;
			String pointsPrefix = "Points ";
			String pointsString = points + "";
			int zeroCount = 8 - pointsString.length();
			for (int i = 0; i < zeroCount; i++)
			{
				pointsPrefix += "0";
			}
			return (pointsText = pointsPrefix + pointsString);
		}
    }

    private void drawPauseOverlay()
	{
		gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL20.GL_BLEND);

		shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0, 0, 0, 0.5f);
		shapeRenderer.rect(0, 0, MaryoGame.NATIVE_WIDTH, MaryoGame.NATIVE_HEIGHT);
		shapeRenderer.end();

		batch.setProjectionMatrix(stage.getCamera().combined);
		batch.begin();

		if(pressedKeys.contains(Key.play))batch.setShader(Shader.GLOW_SHADER);
		batch.draw(play, playR.x, playR.y, playR.width, playR.height);
		batch.setShader(null);

		if(pressedKeys.contains(Key.sound))batch.setShader(Shader.GLOW_SHADER);
		batch.draw((PrefsManager.isPlaySounds() ? soundOn : soundOff), soundR.x, soundR.y, soundR.width, soundR.height);
		batch.setShader(null);

		if(pressedKeys.contains(Key.music))batch.setShader(Shader.GLOW_SHADER);
        batch.draw((PrefsManager.isPlayMusic() ? musicOn : musicOff), musicR.x, musicR.y, musicR.width, musicR.height);
		batch.setShader(null);
		

		batch.end();
	}

    public void leftPressed()
    {
        pressedKeys.add(Key.left);
    }

    public void leftReleased()
    {
        pressedKeys.remove(Key.left);
    }

    public void rightPressed()
    {
        pressedKeys.add(Key.right);
    }

    public void rightReleased()
    {
        pressedKeys.remove(Key.right);
    }

    public void upPressed()
    {
        pressedKeys.add(Key.up);
    }

    public void upReleased()
    {
        pressedKeys.remove(Key.up);
    }

    public void downPressed()
    {
        pressedKeys.add(Key.down);
    }

    public void downReleased()
    {
        pressedKeys.remove(Key.down);
    }

    public void firePressed()
    {
        pressedKeys.add(Key.fire);
    }

    public void fireReleased()
    {
        pressedKeys.remove(Key.fire);
    }

    public void jumpPressed()
    {
        pressedKeys.add(Key.jump);
    }

    public void jumpReleased()
    {
        pressedKeys.remove(Key.jump);
    }

    public void pausePressed()
    {
        pressedKeys.add(Key.pause);
    }

    public void pauseReleased()
    {
        pressedKeys.remove(Key.pause);
    }
	
	public void playPressed()
    {
        pressedKeys.add(Key.play);
    }

    public void playReleased()
    {
        pressedKeys.remove(Key.play);
    }
	
	public void soundPressed()
    {
        pressedKeys.add(Key.sound);
    }

    public void soundReleased()
    {
        pressedKeys.remove(Key.sound);
    }
	
	public void musicPressed()
    {
        pressedKeys.add(Key.music);
    }

    public void musicReleased()
    {
        pressedKeys.remove(Key.music);
    }

	public void dispose() {
		stage.dispose();
	}

}
