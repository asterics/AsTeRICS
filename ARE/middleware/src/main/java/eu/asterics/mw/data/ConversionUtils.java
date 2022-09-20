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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */

package eu.asterics.mw.data;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import eu.asterics.mw.model.DataType;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy] Date: Aug 20, 2010 Time:
 *         3:18:29 PM
 */
public class ConversionUtils {
    public static final String STRING_TO_BOOLEAN = "stringToBoolean";
    public static final String STRING_TO_CHAR = "stringToChar";
    public static final String STRING_TO_DOUBLE = "stringToDouble";
    public static final String STRING_TO_INTEGER = "stringToInteger";
    public static final String STRING_TO_BYTE = "stringToByte";

    public static final String BOOLEAN_TO_STRING = "booleanToString";
    public static final String BOOLEAN_TO_BYTE = "booleanToByte";
    public static final String BOOLEAN_TO_INTEGER = "booleanToInteger";
    public static final String BOOLEAN_TO_DOUBLE = "booleanToDouble";
    public static final String BOOLEAN_TO_CHAR = "booleanToChar";

    public static final String CHAR_TO_STRING = "charToString";
    public static final String CHAR_TO_INTEGER = "charToInteger";
    public static final String CHAR_TO_DOUBLE = "charToDouble";
    public static final String CHAR_TO_BYTE = "charToByte";
    public static final String CHAR_TO_BOOLEAN = "charToBoolean";

    public static final String DOUBLE_TO_STRING = "doubleToString";
    public static final String DOUBLE_TO_INTEGER = "doubleToInteger";
    public static final String DOUBLE_TO_BOOLEAN = "doubleToBoolean";
    public static final String DOUBLE_TO_BYTE = "doubleToByte";
    public static final String DOUBLE_TO_CHAR = "doubleToChar";

    public static final String INTEGER_TO_DOUBLE = "integerToDouble";
    public static final String INTEGER_TO_STRING = "integerToString";
    public static final String INTEGER_TO_BOOLEAN = "integerToBoolean";
    public static final String INTEGER_TO_BYTE = "integerToByte";
    public static final String INTEGER_TO_CHAR = "integerToChar";

    public static final String BYTE_TO_INTEGER = "byteToInteger";
    public static final String BYTE_TO_DOUBLE = "byteToDouble";
    public static final String BYTE_TO_STRING = "byteToString";
    public static final String BYTE_TO_BOOLEAN = "byteToBoolean";
    public static final String BYTE_TO_CHAR = "byteToChar";

    public static Logger logger = AstericsErrorHandling.instance.getLogger();

    /**
     * @deprecated use {@link #intToBytes(int)} instead
     */
    @Deprecated
    public static byte[] intToByteArray(int value) {
        return intToBytes(value);
    }

    /**
     * @deprecated use {@link #intFromBytes(byte[])} instead
     */
    @Deprecated
    public static int byteArrayToInt(byte[] b) {
        return intFromBytes(b);
    }

    public static final byte FALSE_BITS = 0x00;
    public static final byte TRUE_BITS = 0x01;

    static public boolean booleanFromByte(final byte bits) {
        return bits != FALSE_BITS;
    }

    static public byte booleanToByte(final boolean b) {
        return b ? TRUE_BITS : FALSE_BITS;
    }

    static public boolean booleanFromBytes(final byte[] bytes) {
        if (bytes == null || bytes.length != 1) {
            throw new IllegalArgumentException("The input bytes must be non-null and of size 1");
        }

        return booleanFromByte(bytes[0]);
    }

    static public byte[] booleanToBytes(final boolean b) {
        return b ? new byte[] { TRUE_BITS } : new byte[] { FALSE_BITS };
    }

    static public short shortFromBytes(final byte[] bytes) {
        return ByteBuffer.wrap(bytes).getShort();
    }

    static public byte[] shortToBytes(final short s) {
        return new byte[] { (byte) (s >>> 8), (byte) s };
    }

    static public byte[] shortToBytesLittleEndian(final short s) {
        return new byte[] { (byte) s, (byte) (s >>> 8) };
    }

    static public char charFromBytes(final byte[] bytes) {
        // return ByteBuffer.wrap(bytes).getChar();
        return new String(bytes).charAt(0);
    }

    static public byte[] charToBytes(final char i) {
        return stringToBytes(Character.toString(i));
    }

    static public byte byteFromBytes(final byte[] bytes) {
        return bytes[0];
    }

    static public byte[] byteToBytes(final byte i) {
        return new byte[] { i };
    }

    static public int intFromBytes(final byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    static public byte[] intToBytes(final int i) {
        return new byte[] { (byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i };
    }

    static public byte[] intToBytesLittleEndian(final int i) {
        return new byte[] { (byte) i, (byte) (i >>> 8), (byte) (i >>> 16), (byte) (i >>> 24) };
    }

    static public long longFromBytes(final byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    static public byte[] longToBytes(final long l) {
        return new byte[] { (byte) (l >>> 56), (byte) (l >>> 48), (byte) (l >>> 40), (byte) (l >>> 32),
                (byte) (l >>> 24), (byte) (l >>> 16), (byte) (l >>> 8), (byte) l };
    }

    static public float floatFromBytes(byte[] bytes) {
        final int bits = intFromBytes(bytes);
        return Float.intBitsToFloat(bits);
    }

    static public byte[] floatToBytes(final float f) {
        int i = Float.floatToRawIntBits(f);
        return intToBytes(i);
    }

    static public byte[] doubleToBytes(final double d) {
        long l = Double.doubleToRawLongBits(d);
        return longToBytes(l);
    }

    static public double doubleFromBytes(byte[] bytes) {
        final long bits = longFromBytes(bytes);
        return Double.longBitsToDouble(bits);
    }

    static public byte[] stringToBytes(final String s) {
        return s.getBytes();
    }

    static public String stringFromBytes(byte[] bytes) {
        return new String(bytes);
    }

    static public byte[] convertData(byte[] data, String conversion) {
        try {
            switch (conversion) {
            case BYTE_TO_DOUBLE:
                return ConversionUtils.doubleToBytes(ConversionUtils.byteFromBytes(data));
            case BYTE_TO_INTEGER:
                return ConversionUtils.intToBytes(ConversionUtils.byteFromBytes(data));
            case BYTE_TO_STRING:
                return ConversionUtils.stringToBytes(Byte.toString(ConversionUtils.byteFromBytes(data)));
            case BYTE_TO_BOOLEAN:
                return ConversionUtils
                        .booleanToBytes(ConversionUtils.booleanFromByte(ConversionUtils.byteFromBytes(data)));
            case BYTE_TO_CHAR:
                return ConversionUtils.charToBytes((char) ConversionUtils.byteFromBytes(data));

            case CHAR_TO_DOUBLE:
                return ConversionUtils.doubleToBytes(ConversionUtils.charFromBytes(data));
            case CHAR_TO_INTEGER:
                return ConversionUtils.intToBytes(ConversionUtils.charFromBytes(data));
            case CHAR_TO_STRING:
                return ConversionUtils.stringToBytes(new Character(ConversionUtils.charFromBytes(data)).toString());
            case CHAR_TO_BYTE:
                return ConversionUtils.byteToBytes((byte) ConversionUtils.charFromBytes(data));
            case CHAR_TO_BOOLEAN:
                return ConversionUtils.booleanToBytes(ConversionUtils.charFromBytes(data) != 0 ? true : false);

            case INTEGER_TO_DOUBLE:
                return ConversionUtils.doubleToBytes(ConversionUtils.intFromBytes(data));
            case INTEGER_TO_STRING:
                return ConversionUtils.stringToBytes(Integer.toString(ConversionUtils.intFromBytes(data)));
            case INTEGER_TO_BOOLEAN:
                return ConversionUtils
                        .booleanToBytes(ConversionUtils.booleanFromByte((byte) ConversionUtils.intFromBytes(data)));
            case INTEGER_TO_BYTE:
                return ConversionUtils.byteToBytes((byte) ConversionUtils.intFromBytes(data));
            case INTEGER_TO_CHAR:
                return ConversionUtils.charToBytes((char) ConversionUtils.intFromBytes(data));

            case DOUBLE_TO_INTEGER:
                return ConversionUtils.intToBytes((int) ConversionUtils.doubleFromBytes(data));
            case DOUBLE_TO_STRING:
                return ConversionUtils.stringToBytes(Double.toString(ConversionUtils.doubleFromBytes(data)));
            case DOUBLE_TO_BOOLEAN:
                return ConversionUtils
                        .booleanToBytes(ConversionUtils.booleanFromByte((byte) ConversionUtils.doubleFromBytes(data)));
            case DOUBLE_TO_BYTE:
                return ConversionUtils.byteToBytes((byte) ConversionUtils.doubleFromBytes(data));
            case DOUBLE_TO_CHAR:
                return ConversionUtils.charToBytes((char) ConversionUtils.doubleFromBytes(data));

            case BOOLEAN_TO_STRING:
                return ConversionUtils.stringToBytes(Boolean.toString(ConversionUtils.booleanFromBytes(data)));
            case BOOLEAN_TO_BYTE:
                return ConversionUtils.byteToBytes((ConversionUtils.booleanFromBytes(data) ? (byte) 1 : (byte) 0));
            case BOOLEAN_TO_INTEGER:
                return ConversionUtils.intToBytes((ConversionUtils.booleanFromBytes(data) ? (int) 1 : (int) 0));
            case BOOLEAN_TO_DOUBLE:
                return ConversionUtils
                        .doubleToBytes((ConversionUtils.booleanFromBytes(data) ? (double) 1 : (double) 0));
            case BOOLEAN_TO_CHAR:
                return ConversionUtils
                        .charToBytes((char) (ConversionUtils.booleanFromBytes(data) ? (char) 1 : (char) 0));

            // String to any type conversion
            case STRING_TO_BYTE:
                return ConversionUtils.byteToBytes((byte) Double.parseDouble(ConversionUtils.stringFromBytes(data)));
            case STRING_TO_INTEGER:
                return ConversionUtils.intToBytes((int) Double.parseDouble(ConversionUtils.stringFromBytes(data)));
            case STRING_TO_DOUBLE:
                return ConversionUtils.doubleToBytes(Double.valueOf(ConversionUtils.stringFromBytes(data)));
            case STRING_TO_CHAR:
                // take first element of string
                return ConversionUtils.stringToBytes(ConversionUtils.stringFromBytes(data).substring(0, 1));
            case STRING_TO_BOOLEAN:
                boolean val = Boolean.valueOf(ConversionUtils.stringFromBytes(data));
                if (!val) {
                    // if it is not a string containing "true" in any case then
                    // try to parse a number
                    // an integer number != 0 will be interpreted as true
                    try {
                        val = ConversionUtils
                                .booleanFromByte((byte) Double.parseDouble(ConversionUtils.stringFromBytes(data)));
                    } catch (Exception ne) {
                        val = false;
                    }
                }
                return ConversionUtils.booleanToBytes(val);
            default:
                break;
            }
        } catch (Exception e) {
            logger.warning(
                    "Conversion of data failed, conversionString: " + conversion + ", message: " + e.getMessage());
        }
        return null;
    }

    public static String getDataTypeConversionString(DataType sourceDataType, DataType targetDataType) {
        String conversion = "";

        switch (sourceDataType) {
        case BYTE:
            switch (targetDataType) {
            case INTEGER:
                conversion = BYTE_TO_INTEGER;
                break;
            case DOUBLE:
                conversion = BYTE_TO_DOUBLE;
                break;
            case STRING:
                conversion = BYTE_TO_STRING;
                break;
            case BOOLEAN:
                conversion = BYTE_TO_BOOLEAN;
                break;
            case CHAR:
                conversion = BYTE_TO_CHAR;
                break;

            default:
                logger.warning("Incompatible conversion " + "from: " + sourceDataType + " to: " + targetDataType);
                throw new RuntimeException(
                        "Incompatible conversion " + "from: " + sourceDataType + " to: " + targetDataType);
            }
            break;
        case CHAR:
            switch (targetDataType) {
            case INTEGER:
                conversion = CHAR_TO_INTEGER;
                break;
            case DOUBLE:
                conversion = CHAR_TO_DOUBLE;
                break;
            case STRING:
                conversion = CHAR_TO_STRING;
                break;
            case BYTE:
                conversion = CHAR_TO_BYTE;
                break;
            case BOOLEAN:
                conversion = CHAR_TO_BOOLEAN;
                break;

            default:
                logger.warning("Incompatible conversion " + "from: " + sourceDataType + " to: " + targetDataType);
                throw new RuntimeException(
                        "Incompatible conversion " + "from: " + sourceDataType + " to: " + targetDataType);
            }
            break;
        case INTEGER:
            switch (targetDataType) {
            case DOUBLE:
                conversion = INTEGER_TO_DOUBLE;
                break;
            case STRING:
                conversion = INTEGER_TO_STRING;
                break;
            case BOOLEAN:
                conversion = INTEGER_TO_BOOLEAN;
                break;
            case BYTE:
                conversion = INTEGER_TO_BYTE;
                break;
            case CHAR:
                conversion = INTEGER_TO_CHAR;
                break;

            default:
                logger.warning("Incompatible conversion " + "from: " + sourceDataType + " to: " + targetDataType);
                throw new RuntimeException(
                        "Incompatible conversion " + "from: " + sourceDataType + " to: " + targetDataType);
            }
            break;
        case DOUBLE:
            switch (targetDataType) {
            case INTEGER:
                conversion = DOUBLE_TO_INTEGER;
                break;
            case STRING:
                conversion = DOUBLE_TO_STRING;
                break;
            case CHAR:
                conversion = DOUBLE_TO_CHAR;
                break;
            case BOOLEAN:
                conversion = DOUBLE_TO_BOOLEAN;
                break;
            case BYTE:
                conversion = DOUBLE_TO_BYTE;
                break;

            default:
                logger.warning("Incompatible conversion " + "from: " + sourceDataType + " to: " + targetDataType);
                throw new RuntimeException(
                        "Incompatible conversion " + "from: " + sourceDataType + " to: " + targetDataType);
            }
            break;
        case BOOLEAN:
            switch (targetDataType) {
            case STRING:
                conversion = BOOLEAN_TO_STRING;
                break;
            case BYTE:
                conversion = BOOLEAN_TO_BYTE;
                break;
            case INTEGER:
                conversion = BOOLEAN_TO_INTEGER;
                break;
            case CHAR:
                conversion = BOOLEAN_TO_CHAR;
                break;
            case DOUBLE:
                conversion = BOOLEAN_TO_DOUBLE;
                break;

            default:
                logger.warning("Incompatible conversion " + "from: " + sourceDataType + " to: " + targetDataType);
                throw new RuntimeException(
                        "Incompatible conversion " + "from: " + sourceDataType + " to: " + targetDataType);
            }
            break;
        case STRING:
            switch (targetDataType) {
            case BYTE:
                conversion = STRING_TO_BYTE;
                break;
            case INTEGER:
                conversion = STRING_TO_INTEGER;
                break;
            case DOUBLE:
                conversion = STRING_TO_DOUBLE;
                break;
            case CHAR:
                conversion = STRING_TO_CHAR;
                break;
            case BOOLEAN:
                conversion = STRING_TO_BOOLEAN;
                break;
            default:
                logger.warning("Incompatible conversion " + "from: " + sourceDataType + " to: " + targetDataType);
                throw new RuntimeException(
                        "Incompatible conversion " + "from: " + sourceDataType + " to: " + targetDataType);
            }
            break;

        case UNKNOWN:
            conversion = "";
            logger.severe("Invalid enum type for source data type: " + sourceDataType);
            throw new RuntimeException("Invalid enum type for source " + "data type: " + sourceDataType);
        default:
            logger.severe("Invalid enum type for source data type: " + sourceDataType);
            throw new RuntimeException("Invalid enum type for source" + " data type: " + sourceDataType);
        }
        return conversion;
    }
}