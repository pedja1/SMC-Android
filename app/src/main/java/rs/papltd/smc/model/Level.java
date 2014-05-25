package rs.papltd.smc.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import rs.papltd.smc.model.custom_objects.CustomObject;
import rs.papltd.smc.model.enemy.Enemy;

/**
 * Created by pedja on 1/31/14.
 */
public class Level
{
    private float width;
    private float height;
    private Array<Sprite> sprites;
    private Array<CustomObject> objects;
    private Array<Enemy> enemies;
    private Vector2 spanPosition;//i think its a camera position
	private Background bg1;
	private Background bg2;
    private BackgroundColor bgColor;
    private Array<String> music;

	public void setBg1(Background bg1)
	{
		this.bg1 = bg1;
	}

	public Background getBg1()
	{
		return bg1;
	}

	public void setBg2(Background bg2)
	{
		this.bg2 = bg2;
	}

	public Background getBg2()
	{
		return bg2;
	}

    public float getWidth()
    {
        return width;
    }

    public void setWidth(float width)
    {
        this.width = width;
    }

    public float getHeight()
    {
        return height;
    }

    public void setHeight(float height)
    {
        this.height = height;
    }

    public Array<Sprite> getSprites()
    {
        return sprites;
    }

    public void setSprites(Array<Sprite> sprites)
    {
        this.sprites = sprites;
    }

    public Vector2 getSpanPosition()
    {
        return spanPosition;
    }

    public void setSpanPosition(Vector2 spanPosition)
    {
        this.spanPosition = spanPosition;
    }

    public BackgroundColor getBgColor()
    {
        return bgColor;
    }

    public void setBgColor(BackgroundColor bgColor)
    {
        this.bgColor = bgColor;
    }

    public Array<String> getMusic()
    {
        return music;
    }

    public void setMusic(Array<String> music)
    {
        this.music = music;
    }

    public Array<Enemy> getEnemies()
    {
        return enemies;
    }

    public void setEnemies(Array<Enemy> enemies)
    {
        this.enemies = enemies;
    }

    public Array<CustomObject> getObjects()
    {
        return objects;
    }

    public void setObjects(Array<CustomObject> objects)
    {
        this.objects = objects;
    }
}
