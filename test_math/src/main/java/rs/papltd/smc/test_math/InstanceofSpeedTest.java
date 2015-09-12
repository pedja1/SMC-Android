package rs.papltd.smc.test_math;

/**
 * Created by pedja on 12.9.15..
 */
public class InstanceofSpeedTest
{
    public static void main(String[] args)
    {
        test test = new test();
        //dry run
        for(int i = 0; i < 100000000; i++)
        {
            measureIf(i);
            measureInstanceOf(i, test);
        }

        long ioTotal = 0, ifTotal = 0;
        //measure
        for(int i = 0; i < 100000000; i++)
        {
            ifTotal += measureIf(i);
            ioTotal += measureInstanceOf(i, test);
        }

        System.out.println("instanceof: " + ioTotal / 100000000);
        System.out.println("if: " + ifTotal / 100000000);
    }

    public static long measureInstanceOf(int i, InstanceofSpeedTest obj)
    {
        long start = System.nanoTime();
        int offset = 0;
        if(obj instanceof test)
        {
            offset += i;
        }
        return System.nanoTime() - start;
    }

    public static long measureIf(int i)
    {
        long start = System.nanoTime();
        int offset = 0;
        if(i == 10)
        {
            offset += i;
        }
        return System.nanoTime() - start;
    }

    static class test extends InstanceofSpeedTest
    {

    }
}
