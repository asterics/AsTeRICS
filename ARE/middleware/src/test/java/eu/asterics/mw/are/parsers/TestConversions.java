package eu.asterics.mw.are.parsers;

import junit.framework.TestCase;

/**
 * User: Nearchos Paspallis
 * Date: 1/13/11
 * Time: 2:18 PM
 */
public class TestConversions extends TestCase
{
    public void test()
    {
        byte b = 0x000a;
        char c = 'a';
        int i = b;
        int j = c;
        double d1 = b;
        double d2 = c;
        System.out.println("b: " + b + ", i: " + i + ", d1: " + d1);
        System.out.println("c: " + c + ", j: " + j + ", d2: " + d2);
    }
}
