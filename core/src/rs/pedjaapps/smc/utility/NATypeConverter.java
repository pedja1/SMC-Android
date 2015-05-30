package rs.pedjaapps.smc.utility;

/**
 * Created by pedja on 28.5.15..
 *
 * Converting data types by avoiding allocations as much as possible (by storing previous value)
 */
public class NATypeConverter<T>
{
    private T oldValue;
    private String toString;

    @Override
    public String toString()
    {
        throw new RuntimeException("Use toString(T) instead");
    }

    public String toString(T value)
    {
        if(value == null)return null;
        if(oldValue != null && oldValue.equals(value))
        {
            return toString;
        }
        else
        {
            oldValue = value;
            toString = oldValue.toString();
            return toString;
        }
    }
}
