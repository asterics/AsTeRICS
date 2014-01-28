/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 *
 *
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b.
 *       d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
 *      d88P888          888           888    888   888  888    888 Y88b.    
 *     d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b. 
 *    d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
 *   d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
 *  d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
 * d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P"
 *
 *
 *                    homepage: http://www.asterics.org
 *
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 * 
 * 
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */

package eu.asterics.mw.data;

import java.nio.ByteBuffer;

/**
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 *         Date: Aug 20, 2010
 *         Time: 3:18:29 PM
 */
public class ConversionUtils
{
    /**
     * @deprecated use {@link #intToBytes(int)} instead
     */
    public static byte[] intToByteArray(int value)
    {
        return intToBytes(value);
    }

    /**
     * @deprecated use {@link #intFromBytes(byte[])} instead
     */
    public static int byteArrayToInt(byte [] b)
    {
        return intFromBytes(b);
    }

    public static final byte FALSE_BITS = 0x00;
    public static final byte TRUE_BITS = 0x01;

    static public boolean booleanFromByte(final byte bits)
    {
        return bits != FALSE_BITS;
    }

    static public byte booleanToByte(final boolean b)
    {
        return b ? TRUE_BITS : FALSE_BITS;
    }
    static public boolean booleanFromBytes(final byte [] bytes)
    {
        if(bytes == null || bytes.length != 1)
        {
            throw new IllegalArgumentException("The input bytes must be non-null and of size 1");
        }

        return booleanFromByte(bytes[0]);
    }

    static public byte [] booleanToBytes(final boolean b)
    {
        return b ? new byte [] {TRUE_BITS} : new byte [] {FALSE_BITS};
    }


    static public short shortFromBytes(final byte [] bytes)
    {
        return ByteBuffer.wrap(bytes).getShort();
    }

    static public byte [] shortToBytes(final short s)
    {
        return new byte [] {
                (byte)(s >>> 8),
                (byte) s};
    }

    static public byte [] shortToBytesLittleEndian(final short s)
    {
        return new byte [] {
        		(byte) s,
        		(byte)(s >>> 8)};
    }

    static public int intFromBytes(final byte [] bytes)
    {
        return ByteBuffer.wrap(bytes).getInt();
    }

    static public byte [] intToBytes(final int i)
    {
        return new byte [] {
                (byte)(i >>> 24),
                (byte)(i >>> 16),
                (byte)(i >>> 8),
                (byte) i};
    }
    static public byte [] intToBytesLittleEndian(final int i)
    {
        return new byte [] {
        		(byte) i,
        		(byte)(i >>> 8),
                (byte)(i >>> 16),
        		(byte)(i >>> 24)};
    }

    static public long longFromBytes(final byte [] bytes)
    {
        return ByteBuffer.wrap(bytes).getLong();
    }

    static public byte [] longToBytes(final long l)
    {
        return new byte [] {
                (byte)(l >>> 56),
                (byte)(l >>> 48),
                (byte)(l >>> 40),
                (byte)(l >>> 32),
                (byte)(l >>> 24),
                (byte)(l >>> 16),
                (byte)(l >>> 8),
                (byte) l};
    }

    static public float floatFromBytes(byte [] bytes)
    {
        final int bits = intFromBytes(bytes);
        return Float.intBitsToFloat(bits);
    }

    static public byte [] floatToBytes(final float f)
    {
        int i = Float.floatToRawIntBits(f);
        return intToBytes(i);
    }

    static public byte [] doubleToBytes(final double d)
    {
        long l = Double.doubleToRawLongBits(d);
        return longToBytes(l);
    }

    static public double doubleFromBytes(byte [] bytes)
    {
        final long bits = longFromBytes(bytes);
        return Double.longBitsToDouble(bits);
    }

    static public byte [] stringToBytes(final String s)
    {
        return s.getBytes();
    }

    static public String stringFromBytes(byte [] bytes)
    {
        return new String(bytes);
    }

}