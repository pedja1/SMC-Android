package rs.papltd.smc;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.*;
import rs.papltd.smc.screen.*;

public class MaryoGame extends Game
{
	
    @Override
	public void create()
	{
		setScreen(new SplashScreen(this));
	}

    @Override
    public void dispose()
    {
        super.dispose();
        Assets.dispose();
    }

    public boolean onBackPressed()
    {
        return ((AbstractScreen)getScreen()).onBackPressed();
    }
}
