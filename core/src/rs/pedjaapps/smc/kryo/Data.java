package rs.pedjaapps.smc.kryo;

import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.maryo.Maryo;

public class Data
{
    public int posX, posY;
    public Maryo.MaryoState maryoState;
    public GameObject.WorldState worldState;
    public boolean facingLeft;
}