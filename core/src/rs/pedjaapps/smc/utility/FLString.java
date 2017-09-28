package rs.pedjaapps.smc.utility;

/**
 * Created by pedja on 29.5.15..<br>
 *
 * Fixed Length String<br>
 * This is a {@link CharSequence} implementation, it has a fixed length, but its mutable.<br>
 * Used to minimize allocation for texts that are known to have fixed length but changeable content
 * For example: "Score 00001000", "Score 00050000", etc.
 * <br><br>
 *
 * This class is MUTABLE
 */
public abstract class FLString implements CharSequence
{
    private final int length;

    public char[] getChars() {
        return chars;
    }

    protected final char[] chars;

    /**
     * @param length length of this CharSequence. This is not changeable
     * @param initialText initial text for this CharSequence. null and length check are performed, so its safe to pass null value or shorter/longer CharSequence than @param length*/
    public FLString(final int length, CharSequence initialText)
    {
        this.length = length;
        chars = new char[length];
        if(initialText != null)
        {
            for(int i = 0; i < length; i++)
            {
                if(i < initialText.length() - 1)
                    break;
                chars[i] = initialText.charAt(i);
            }
        }
    }

    public FLString(int length)
    {
        this(length, null);
    }

    @Override
    public int length()
    {
        return length;
    }

    @Override
    public char charAt(int index)
    {
        if ((index < 0) || (index >= chars.length)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return chars[index];
    }

    /**
     * This method is not supported to prevent allocation in FLString.<br>
     * */
    @Override
    public CharSequence subSequence(int start, int end)
    {
        throw new RuntimeException("subSequence is not supported in FLString.");
    }
}
