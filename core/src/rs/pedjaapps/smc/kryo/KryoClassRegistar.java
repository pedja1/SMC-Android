package rs.pedjaapps.smc.kryo;

import com.esotericsoftware.kryo.Kryo;

import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.maryo.Maryo;

/**
 * Created by pedja on 9.11.15. 16.10.
 * This class is part of the p-net
 * Copyright © 2015 ${OWNER}
 */
public class KryoClassRegistar
{
    public static void registerClasses(Kryo kryo)
    {
        kryo.register(GameObject.WorldState.class);
        kryo.register(Maryo.MaryoState.class);
        kryo.register(Data.class);
        kryo.register(OpponentLeft.class);
        kryo.register(CancelMatchmaking.class);
        kryo.register(MatchmakingSuccess.class);
        kryo.register(MatchmakingFailed.class);
        kryo.register(ServerFull.class);
    }
}
