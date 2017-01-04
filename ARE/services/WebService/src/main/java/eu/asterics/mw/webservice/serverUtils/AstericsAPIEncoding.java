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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.mw.webservice.serverUtils;

import java.util.StringTokenizer;

/**
 * Class that provides encoding-decoding capabilities
 *
 * The encoding/decoding functionality is based on the UTF-16 encoding table.
 * 
 * @author Marios Komodromos
 *
 */
public class AstericsAPIEncoding {

    private static final String delimiter = "-";

    public AstericsAPIEncoding() {

    }

    /**
     * Returns the decimal code for the given character
     * 
     * @param character
     * @return
     */
    public int convertCharacterToCode(char character) {
        return (int) character;
    }

    /**
     * Returns the character from the given code. In most of the cases, the
     * returned character array will contain only one character.
     * 
     * @param code
     * @return
     */
    public char[] convertCodeToCharacter(int code) {
        return Character.toChars(code);
    }

    /**
     * Encodes the given string
     * 
     * @return
     */
    public String encodeString(String originalString) {

        String encodedString = "";
        char[] characterArray = originalString.toCharArray();
        for (int i = 0; i < characterArray.length; i++) {
            encodedString += convertCharacterToCode(characterArray[i]) + AstericsAPIEncoding.delimiter;
        }

        return encodedString;
    }

    /**
     * Decodes the given string
     * 
     * @return
     */
    public String decodeString(String originalString) {

        String decodedString = "";
        StringTokenizer tokenizer = new StringTokenizer(originalString, AstericsAPIEncoding.delimiter);

        while (tokenizer.hasMoreTokens()) {

            String code = tokenizer.nextToken();
            try {
                char[] characterArray = convertCodeToCharacter(Integer.parseInt(code));
                for (int i = 0; i < characterArray.length; i++) {
                    decodedString += characterArray[i];
                }
            } catch (Exception ex) {
                return null;
            }

        }

        return decodedString;
    }

}
