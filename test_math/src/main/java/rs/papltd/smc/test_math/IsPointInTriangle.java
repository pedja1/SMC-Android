package rs.papltd.smc.test_math;

/**
 * Created by pedja on 2/1/14.
 */
public class IsPointInTriangle
{
    public static void main(String[] args)
    {
        int y = 100, x = 100;
        int x1 = 0, y1 = 0;
        int x2 = 128, y2 = 128;
        int x3 = 256, y3 = 0;

        // no need to divide by 2.0 here, since it is not necessary in the equation
        double ABC = Math.abs (x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2));
        double ABP = Math.abs (x1 * (y2 - y) + x2 * (y - y1) + x * (y1 - y2));
        double APC = Math.abs (x1 * (y - y3) + x * (y3 - y1) + x3 * (y1 - y));
        double PBC = Math.abs (x * (y2 - y3) + x2 * (y3 - y) + x3 * (y - y2));

        boolean isInTriangle = ABP + APC + PBC == ABC;
        System.out.println(isInTriangle);
    }
}
