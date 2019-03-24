package rs.pedjaapps.smc.assets;

public enum TextureKey {
    stand_right("stand_right"),
    walk_right_1("walk_right-1"),
    walk_right_2("walk_right-2"),
    jump_right("jump_right"),
    fall_right("fall_right"),
    dead_right("dead_right"),
    duck_right("duck_right"),
    climb_left("climb_left"),
    climb_right("climb_right"),
    throw_right_1("throw_right_1"),
    throw_right_2("throw_right_2"),
    one("1"),
    two("2"),
    three("3"),
    ;

    String mValue;

    TextureKey(String value) {
        mValue = value;
    }

    @Override
    public String toString() {
        return mValue;
    }
}