package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.utils.GdxRuntimeException;

public enum EnemyClass {
    eato, flyon, furball, turtle, gee, krush, rokko, spika, spikeball, thromp, turtleboss, _static("static");

    String mValue;

    EnemyClass(String mValue) {
        this.mValue = mValue;
    }

    EnemyClass() {
    }

    public static EnemyClass fromString(String string) {
        for (EnemyClass cls : values()) {
            if (cls.toString().equals(string))
                return cls;
        }
        throw new GdxRuntimeException("Unknown enemy class: '" + string + "'");
    }


    @Override
    public String toString() {
        return mValue == null ? super.toString() : mValue;
    }
}