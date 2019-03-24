package rs.pedjaapps.smc.utility;

/**
 * Created by pedja on 28.5.15..
 * <p>
 * Converting data types by avoiding allocations as much as possible (by storing previous value)
 */
public class NAHudText<T> {
    private T oldValue;
    private String toString;
    private final String prefix, suffix;

    public NAHudText(String prefix, String suffix) {
        this.prefix = prefix == null ? "" : prefix;
        this.suffix = suffix == null ? "" : suffix;
    }

    @Override
    public String toString() {
        throw new RuntimeException("Use toString(T) instead");
    }

    public String toString(T value) {
        if (value == null) return null;
        if (oldValue != null && oldValue.equals(value)) {
            return toString;
        } else {
            oldValue = value;
            toString = prefix + oldValue.toString() + suffix;
            return toString;
        }
    }
}
