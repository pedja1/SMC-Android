package rs.pedjaapps.smc.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import org.json.JSONException;
import org.json.JSONObject;
import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.model.items.Item;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.LevelLoader;
import rs.pedjaapps.smc.utility.Utility;

public class Box extends Sprite
{
	//visibility type
	public static final int
	// always visible
	BOX_VISIBLE = 0,
	// visible after activation
	BOX_INVISIBLE_MASSIVE = 1,
	// only visible in ghost mode
	BOX_GHOST = 2,
	// visible after activation and only touchable in the activation direction
	BOX_INVISIBLE_SEMI_MASSIVE = 3;
	
	
	
	public static final float SIZE = 0.67f;
	String goldColor, animation, boxType, text;
	boolean forceBestItem, invisible;
	int usableCount, item;
	
	protected float stateTime;
	
	TextureRegion txSpinDisabled;
	
	boolean hitByPlayer;
	float originalPosY;
	
	//item that pops out when box is hit by player
	Item itemObject;
	
	public Box(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
		type = Type.massive;
		originalPosY = position.y;
    }

	@Override
	public void loadTextures()
	{
		System.out.println("box:::tname" + textureName);
		System.out.println("box:::anim" + animation);
		
		if(animation == null || "default".equalsIgnoreCase(animation))
		{
			if(textureName == null)
			{
				textureName = "data/game/box/yellow/default.png";
			}
			return;
		}
		TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureRegion> frames = new Array<TextureRegion>();
		float animSpeed = 0;
		if("bonus".equalsIgnoreCase(animation))
		{
			frames.add(atlas.findRegion("1"));
			frames.add(atlas.findRegion("2"));
			frames.add(atlas.findRegion("3"));
			frames.add(atlas.findRegion("4"));
			frames.add(atlas.findRegion("5"));
			frames.add(atlas.findRegion("6"));
			animSpeed = 0.15f;
		}
		if("power".equalsIgnoreCase(animation))
		{
			frames.add(atlas.findRegion("power-1"));
			frames.add(atlas.findRegion("power-2"));
			frames.add(atlas.findRegion("power-3"));
			frames.add(atlas.findRegion("power-4"));
			animSpeed = 0.2f;
		}
		if("spin".equalsIgnoreCase(boxType) || "spin".equalsIgnoreCase(animation))
		{
			frames.add(new TextureRegion(Assets.manager.get("data/game/box/yellow/default.png", Texture.class)));
			frames.add(atlas.findRegion("1"));
			frames.add(atlas.findRegion("2"));
			frames.add(atlas.findRegion("3"));
			frames.add(atlas.findRegion("4"));
			frames.add(atlas.findRegion("5"));
			txSpinDisabled = atlas.findRegion("6");
			animSpeed = 0.13f;
		}
		Assets.animations.put(textureAtlas, new Animation(animSpeed, frames));
	}

	@Override
	public void render(SpriteBatch spriteBatch)
	{
		if(invisible)
		{
			return;
		}
		if(textureName != null)
		{
			System.out.println("tn");
			Texture tx = Assets.manager.get(textureName);
			Utility.draw(spriteBatch, tx, position.x, position.y, bounds.height);
		}
		System.out.println("ta " + textureAtlas);
		if(textureAtlas != null && animation != null && !"default".equalsIgnoreCase(animation))
		{
			TextureRegion frame = Assets.animations.get(textureAtlas).getKeyFrame(stateTime, true);
            Utility.draw(spriteBatch, frame, position.x, position.y, bounds.height);
		}
	}
	
	public static Box initBox(World world, JSONObject jBox, LevelLoader loader) throws JSONException
	{
		Vector3 position = new Vector3((float) jBox.getDouble(LevelLoader.KEY.posx.toString()), (float) jBox.getDouble(LevelLoader.KEY.posy.toString()), 0);
		Vector2 size = new Vector2(SIZE, SIZE/*(float) jBox.getDouble(LevelLoader.KEY.width.toString()), (float) jBox.getDouble(LevelLoader.KEY.height.toString())*/);
		
		Box box = new Box(world, size, position);
		
		box.goldColor = jBox.optString(LevelLoader.KEY.gold_color.toString());
		box.animation = jBox.optString(LevelLoader.KEY.animation.toString(), null);
		box.boxType = jBox.optString(LevelLoader.KEY.type.toString());
		box.text = jBox.optString(LevelLoader.KEY.text.toString());
		box.forceBestItem = jBox.optInt(LevelLoader.KEY.force_best_item.toString(), 0) == 1;
		box.invisible = jBox.optInt(LevelLoader.KEY.invisible.toString(), 0) == 1;
		box.usableCount = jBox.optInt(LevelLoader.KEY.usable_count.toString(), -1);
		box.item = jBox.optInt(LevelLoader.KEY.item.toString(), 0);
		
		box.textureName = jBox.optString(LevelLoader.KEY.texture_name.toString(), null);
		if (jBox.has(LevelLoader.KEY.texture_atlas.toString()))
		{
			box.setTextureAtlas(jBox.getString(LevelLoader.KEY.texture_atlas.toString()));
			if (Assets.manager.isLoaded(box.getTextureAtlas()))
			{
				box.loadTextures();
			}
			else
			{
				throw new IllegalArgumentException("Atlas not found in AssetManager. Every TextureAtlas used"
												   + "in [level].smclvl must also be included in [level].data (" + box.getTextureAtlas() + ")");
			}
		}
		//create item contained in box
		switch(box.item)
		{
			case Item.TYPE_GOLDPIECE:
				createCoin(box, loader);
				break;
		}
		return box;
	}
	
	public static void createCoin(Box box, LevelLoader loader)
	{
		Coin coin = new Coin(box.world, new Vector2(Coin.DEF_SIZE, Coin.DEF_SIZE), new Vector3(box.position));
		String ta = Coin.DEF_ATL;
		coin.setTextureAtlas(ta);
		if (Assets.manager.isLoaded(coin.getTextureAtlas()))
		{
			coin.loadTextures();
		}
		else
		{
			throw new IllegalArgumentException("Atlas not found in AssetManager. Every TextureAtlas used"
											   + "in [level].smclvl must also be included in [level].data (" + coin.getTextureAtlas() + ")");
		}
		coin.collectable = false;
		coin.visible = false;
		
		box.itemObject = coin;
		System.out.println("level: " + box.world.level);
		loader.getLevel().getGameObjects().add(coin);
	}
	
	public void handleHitByPlayer()
	{
		if(!hitByPlayer)
		{
			hitByPlayer = true;
			velocity.y = 3f;
			if(itemObject != null)itemObject.popOutFromBox();
		}
	}
	
	@Override
    public void update(float delta)
    {
		if(hitByPlayer)
		{
			// Setting initial vertical acceleration 
			acceleration.y = Constants.GRAVITY;

			// Convert acceleration to frame time
			acceleration.scl(delta);

			// apply acceleration to change velocity
			velocity.add(acceleration);

			// scale velocity to frame units 
			velocity.scl(delta);
			
			// update position
			position.add(velocity);
			body.y = position.y;
			updateBounds();

			// un-scale velocity (not in frame time)
			velocity.scl(1 / delta);
			
			if(position.y <= originalPosY)
			{
				hitByPlayer = false;
				position.y = originalPosY;
				body.y = position.y;
				updateBounds();
			}
		}
        stateTime += delta;
    }
}
