package rs.papltd.smc.model;

/**
 * Created by pedja on 18.5.14..
 */
public class GameObject
{
    protected float stateTime;
    public enum WorldState
    {
        IDLE, WALKING, JUMPING, DYING, DUCKING
    }

    public enum TKey
    {
        stand_right("stand-right"),
        walk_right_1("walk-right-1"),
        walk_right_2("walk-right-2"),
        stand_left("stand-left"),
        jump_right("jump-right"),
        jump_left("jump-left"),
        fall_right("fall-right"),
        fall_left("fall-left"),
        dead_right("dead-right"),
        dead_left("dead-left"),
        duck_right("duck-right"),
        duck_left("duck-left"),
        one("1"),
        two("2"),
        three("3"),;

        String mValue;
        TKey(String value)
        {
            mValue = value;
        }

        @Override
        public String toString()
        {
            return mValue;
        }
    }

    public enum AKey
    {
        walk_left, walk_right
    }

    public void updateStateTime(float delta)
    {
        stateTime += delta;
    }
}
