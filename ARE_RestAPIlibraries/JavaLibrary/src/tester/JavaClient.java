package tester;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.glassfish.jersey.media.sse.EventListener;
import org.glassfish.jersey.media.sse.InboundEvent;

import eu.asterics.rest.javaClient.ARECommunicator;
import eu.asterics.rest.javaClient.serialization.RestFunction;
import eu.asterics.rest.javaClient.utils.SseCommunicator;

public class JavaClient {

	public static void main(String[] args) throws Exception {
		ARECommunicator areCommunicator = new ARECommunicator("http://localhost:8081/rest/");
		System.out.println(areCommunicator.triggerEvent("FS20Sender.1","toggle"));
	}


	/**
	 * Reads the content of a file and returns it as a string.
	 * 
	 * @param filename
	 * @return
	 */
	private static String readModel(String filename) {
		String modelInXML = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = "";
			while ((line = br.readLine()) != null) {
				modelInXML += line + "\n";
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelInXML;
	}

	
	/**
	 * Pauses the program until the word 'ok' is entered.
	 */
	private static void pause() {
		Scanner scanner = new Scanner(System.in);
		String ans = "";
		while (true) {
			ans = scanner.next();
			if (ans.equalsIgnoreCase("run")) {
				break;
			}
		}
	}
	
}
