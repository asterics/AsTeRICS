package eu.asterics.rest.javaClient.utils;

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
	 * Returns the character from the given code.
	 * In most of the cases, the returned character array will contain
	 * only one character.
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
		for (int i=0;i<characterArray.length;i++) {
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
				for (int i=0;i<characterArray.length;i++) {
					decodedString += characterArray[i];
				}
			} catch (Exception ex) {
				return null;
			}
			
		}
		
		return decodedString;
	}
	
}
