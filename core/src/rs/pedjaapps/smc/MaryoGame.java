package rs.pedjaapps.smc;

import com.badlogic.gdx.*;

import rs.pedjaapps.smc.screen.AbstractScreen;
import rs.pedjaapps.smc.screen.SplashScreen;

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
