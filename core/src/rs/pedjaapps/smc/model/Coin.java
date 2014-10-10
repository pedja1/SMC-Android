package rs.pedjaapps.smc.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.model.items.Item;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.GameSaveUtility;
import rs.pedjaapps.smc.utility.Utility;
import rs.pedjaapps.smc.screen.GameScreen;

/**
 * Created by pedja on 24.5.14..
 */
public class Coin extends Item
{
	public static final float DEF_SIZE = 0.59375f;
	public static final String DEF_ATL = "data/game/items/goldpiece/yellow.pack";
    public boolean playerHit;
    private Vector2 pointsTextPosition = new Vector2();
    private BitmapFont font;
    public int points = 5;
	
	//collectable by player
	public boolean collectable = true;
	
	//is drawn
	public boolean visible = true;
	
	boolean popFromBox;

	float originalPosY;
	
	boolean scrollOut;
	

    public Coin(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
    }

    @Override
    public void loadTextures()
    {
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureAtlas.AtlasRegion> frames = new Array<TextureAtlas.AtlasRegion>();

		for(int i = 1; i < 11; i++)
		{
			frames.add(atlas.findRegion(i + ""));
		}

        Assets.animations.put(textureAtlas, new Animation(0.10f, frames));
        if (Assets.manager.isLoaded("coin.ttf"))
        {
            font = Assets.manager.get("coin.ttf");
            font.scale(Constants.CAMERA_WIDTH / Gdx.graphics.getWidth());
            BitmapFont.TextBounds textBounds;
            if(textureAtlas.contains("yellow"))
            {
                points = 5;
                textBounds = font.getBounds("5");
            }
            else
            {
                points = 100;
                textBounds = font.getBounds("100");
            }

            pointsTextPosition.x = (bounds.x + bounds.width / 2) - textBounds.width / 2;
            pointsTextPosition.y = (bounds.y + bounds.height / 2) + textBounds.height / 2;
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
		if(!visible)return;
        //if (!playerHit)
        //{
            TextureRegion frame = Assets.animations.get(textureAtlas).getKeyFrame(stateTime, true);
            Utility.draw(spriteBatch, frame, position.x, position.y, bounds.height);
			System.out.println("posy: " + position.y + ", boundsy: " + bounds.y);
        //}
        //else
        //{
        //    font.draw(spriteBatch, position + "", pointsTextPosition.x, pointsTextPosition.y);
        //}
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);
        //pointsTextPosition.y += 3f * delta;
		if(popFromBox)
		{
			System.out.println("popfb update");
			// scale velocity to frame units 
			velocity.scl(delta);

			// update position
			position.add(velocity);
			body.y = position.y;
			updateBounds();

			// un-scale velocity (not in frame time)
			velocity.scl(1 / delta);

			if(position.y >= originalPosY + body.height + 0.3f)
			{
				System.out.println("stop updating");
				popFromBox = false;
				collect();
			}
		}
		if(scrollOut)
		{
			velocity.scl(delta);
			
			position.add(velocity);
			body.y = position.y;
			body.x = position.x;
			updateBounds();

			// un-scale velocity (not in frame time)
			velocity.scl(1 / delta);
			if(false)//TODO not in camera viewport
			{
				world.trashObjects.add(this);
			}
			
		}
    }
	
	private void collect()
	{
		scrollOut = true;
		velocity.x = -9f;
		velocity.y = 2f;
	}

    public void hitPlayer()
    {
		if(!collectable)return;
        playerHit = true;
        Sound sound;
        if(textureAtlas.contains("yellow"))
        {
            sound = Assets.manager.get("data/sounds/item/goldpiece_1.ogg");
        }
        else
        {
            sound = Assets.manager.get("data/sounds/item/goldpiece_red.wav");
        }
        if(sound != null && Assets.playSounds)sound.play();
        GameSaveUtility.getInstance().save.coins++;

        collect();
    }
	
	@Override
	public void popOutFromBox()
	{
		visible = true;
		popFromBox = true;
		velocity.y = 4f;
		originalPosY = position.y;
		System.out.println("popOutFromBox");
	}
}
