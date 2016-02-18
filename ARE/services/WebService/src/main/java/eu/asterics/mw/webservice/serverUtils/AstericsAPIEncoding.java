package eu.asterics.mw.webservice.serverUtils;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Class that provides encoding-decoding capabilities 
 *
 * The character-code pairs are proprietary and are not 
 * based in any known encoding table.
 * 
 * @author Marios Komodromos
 *
 */
public class AstericsAPIEncoding {
	private HashMap<String, String> encodeMap;
	private HashMap<String, String> decodeMap;
	
	public AstericsAPIEncoding() {
		this.encodeMap = new HashMap<String, String>();
		this.decodeMap = new HashMap<String, String>();
		
		addEncodingPair("/", "_aae_slash_");
		addEncodingPair(" ", "_aae_space_");
	}
	
	private void addEncodingPair(String symbol, String code) {
		this.encodeMap.put(symbol, code);
		this.decodeMap.put(code, symbol);
	}
	
	/**
	 * Returns the code for the given character
	 * 
	 * @param character
	 * @return
	 */
	public String convertCharacterToCode(String character) {
		if (this.encodeMap.containsKey(character)) {
			return this.encodeMap.get(character);
		}
		else {
			return character;
		}
	}
	
	/**
	 * Returns the character from the given code
	 * 
	 * @param code
	 * @return
	 */
	public String convertCodeToCharacter(String code) {
		if (this.decodeMap.containsKey(code)) {
			return this.decodeMap.get(code);
		}
		else {
			return code;
		}
	}
	
	/**
	 * Encodes the given string
	 * 
	 * @return
	 */
	public String encodeString(String originalString) {
		
		String encodedString = originalString;
		for (String character: this.encodeMap.keySet()) {
			String code = this.encodeMap.get(character);
			encodedString = encodedString.replaceAll(Pattern.quote(character), code);
		}
		
		return encodedString;
	}
	
	/**
	 * Decodes the given string
	 * 
	 * @return
	 */
	public String decodeString(String originalString) {
		
		String decodedString = originalString;
		for (String code: this.decodeMap.keySet()) {
			String character = this.decodeMap.get(code);
			decodedString = decodedString.replaceAll(Pattern.quote(code), character);
		}
		
		return decodedString;
	}
	
}
