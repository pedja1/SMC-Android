package rs.pedjaapps.smc.utility;

/**
 * Created by pedja on 29.5.15..
 */
public class HUDTimeText extends FLString
{
    private static final String TIME_PREFIX = "TIME ";
    private static final int LENGTH = TIME_PREFIX.length() + 5;/*Time 00:05*/
    private static final char[] USED_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', };

    public HUDTimeText()
    {
        super(LENGTH);
    }

    public void update(float millis)
    {
        for(int i = 0, size = TIME_PREFIX.length(); i < size; i++)
        {
            chars[i] = TIME_PREFIX.charAt(i);
        }

        int next = TIME_PREFIX.length();

        int s = (int) millis % 60;
        int m = ((int) ((millis / 60) % 60));

        if(m < 10)
        {
            chars[next] = USED_CHARS[0];
            chars[next + 1] = getFirstDigit(m);
        }
        else
        {
            chars[next] = getFirstDigit(m);
            chars[next + 1] = getSecondDigit(m);
        }
        chars[next + 2] = USED_CHARS[10];
        if(s < 10)
        {
            chars[next + 3] = USED_CHARS[0];
            chars[next + 4] = getFirstDigit(s);
        }
        else
        {
            chars[next + 3] = getFirstDigit(s);
            chars[next + 4] = getSecondDigit(s);
        }

    }

    private char getSecondDigit(int m)
    {
        int secondDigit =  Math.abs(m) % 10;
        return getCharFromDigit(secondDigit);
    }

    private char getFirstDigit(int m)
    {
        if (m == 0) return '0';
        m = Math.abs(m);
        m = (int) Math.floor(m / Math.pow(10, Math.floor(Math.log10(m))));
        return getCharFromDigit(m);
    }

    private char getCharFromDigit(int number)
    {
        switch (number)
        {
            case 1:
                return USED_CHARS[1];
            case 2:
                return USED_CHARS[2];
            case 3:
                return USED_CHARS[3];
            case 4:
                return USED_CHARS[4];
            case 5:
                return USED_CHARS[5];
            case 6:
                return USED_CHARS[6];
            case 7:
                return USED_CHARS[7];
            case 8:
                return USED_CHARS[8];
            case 9:
                return USED_CHARS[9];
            case 0:
            default:
                return USED_CHARS[0];
        }
    }
}
