package tester;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.glassfish.jersey.media.sse.EventListener;
import org.glassfish.jersey.media.sse.InboundEvent;

import eu.asterics.rest.javaClient.ARECommunicator;
import eu.asterics.rest.javaClient.serialization.RestFunction;
import eu.asterics.rest.javaClient.utils.SseCommunicator;

public class JavaClient {

	public static void main(String[] args) throws Exception {
		ARECommunicator areCommunicator = new ARECommunicator("http://localhost:8081/rest/");

		String localModel = JavaClient.readModel("JavaLibrary/models/demomenu.acs");
		String areModel = "";
		String filename = "myModel.acs";
		String response = "";
		String[] arrayResponse = null;
		String componentId = "";
		String componentPorpertyKey = "";
		
		System.out.println("=======================================================");
		System.out.println("ARE Restfull API - Framework Tester (JAVA)");
		System.out.println("=======================================================\n");
		
		System.out.println("-------------------");
		System.out.println("ARE REPOSITORY");
		System.out.println("-------------------");
		System.out.println("*** Type 'run' to continue...\n");
		JavaClient.pause();
		
		//list stored models
		System.out.println("\nList stored models\n-------------------------------------");
		arrayResponse = areCommunicator.listStoredModels();
		for (String storedModel: arrayResponse) {
			System.out.println(storedModel);
		}
		System.out.println("\n");
		
		//download model from file
		System.out.println("\nDownload model from file\n-------------------------------------");
		response = areCommunicator.downloadModelFromFile("autostart.acs");
		System.out.println(response + "\n");
		areModel = response;
		
		//store model
		System.out.println("\nStore model to file\n-------------------------------------");
		response = areCommunicator.storeModel(filename, localModel);
		System.out.println(response + "\n");
		
		//delete from file
		System.out.println("\nDelete model from file\n-------------------------------------");
		response = areCommunicator.deleteModelFromFile(filename);
		System.out.println(response + "\n");
		
		//another two calls for descriptors
		
		System.out.println("-------------------");
		System.out.println("ARE RUNTIME HANDLING");
		System.out.println("-------------------");
		System.out.println("*** Type 'run' to continue...\n");
		JavaClient.pause();
		
		//download current deployed model
		System.out.println("\nDownload deployed model\n-------------------------------------");
		response = areCommunicator.downloadDeployedModel();
		System.out.println(response + "\n");

		//upload model
		System.out.println("\nUpload model\n-------------------------------------");
		response = areCommunicator.uploadModel(areModel);
		System.out.println(response + "\n");

		//autorun
		System.out.println("\nAutorun\n-------------------------------------");
		response = areCommunicator.autorun("autostart.acs");
		System.out.println(response + "\n");
		
		//deploy from file
		System.out.println("\nDeploy model from file\n-------------------------------------");
		response = areCommunicator.deployModelFromFile("HeadSound.acs");
		System.out.println(response + "\n");
		
		//get Runtime Component Ids
		System.out.println("\nGet runtime component Ids\n-------------------------------------");
		arrayResponse = areCommunicator.getRuntimeComponentIds();
		for (String component: arrayResponse) {
			System.out.println(component);
			componentId = component;
		}
		System.out.println("\n");
		
		//get component property keys
		System.out.println("\nGet runtime component property keys\n-------------------------------------");
		arrayResponse = areCommunicator.getRuntimeComponentPropertyKeys(componentId);
		for (String propertyKey: arrayResponse) {
			System.out.println(propertyKey);
			componentPorpertyKey = propertyKey;
		}
		System.out.println("\n\n");

		//get component property
		System.out.println("\nGet runtime component property (" + componentId +": " + componentPorpertyKey + ")\n-------------------------------------");
		response = areCommunicator.getRuntimeComponentProperty(componentId, componentPorpertyKey);
		System.out.println(response + "\n");
		
        //get component dynamic property
        System.out.println("\nGet runtime component dynamic property (" + componentId +": " + componentPorpertyKey + ")\n-------------------------------------");
        String[] responseList = areCommunicator.getRuntimeComponentPropertyDynamic("MidiPlayer.1", "toneScale");
        System.out.println(Arrays.toString(responseList) + "\n");		
		
		//set component property
		System.out.println("\nSet runtime component property (" + componentId +": " + componentPorpertyKey + " = 15)\n-------------------------------------");
		response = areCommunicator.setRuntimeComponentProperty(componentId, componentPorpertyKey, 15+"");
		System.out.println(response + "\n");
		
		
		System.out.println("-------------------");
		System.out.println("ARE MODEL STATE");
		System.out.println("-------------------");
		System.out.println("*** Type 'run' to continue...\n");
		JavaClient.pause();
		
		//pause model
		System.out.println("\nPause model\n-------------------------------------");
		response = areCommunicator.pauseModel();
		System.out.println(response + "\n");
		
		//stop model
		System.out.println("\nStop model\n-------------------------------------");
		response = areCommunicator.stopModel();
		System.out.println(response + "\n");
		
		//start model
		System.out.println("\nStart model\n-------------------------------------");
		response = areCommunicator.startModel();
		System.out.println(response + "\n");
		
		//get model statel
		System.out.println("\nGet model state\n-------------------------------------");
		response = areCommunicator.getModelState();
		System.out.println(response + "\n");

		
		System.out.println("-------------------");
		System.out.println("ARE SERVER SENT EVENTS");
		System.out.println("-------------------");
		System.out.println("*** Type 'run' to continue...\n");
		JavaClient.pause();
		
		System.out.println("\nSubscribe to MODEL_STATE_CHANGED events\n-------------------------------------");
		areCommunicator.subscribe(SseCommunicator.MODEL_STATE_CHANGED, 
			new EventListener() {
		        @Override
		        public void onEvent(InboundEvent inboundEvent) {
		        	System.out.println("Event Received:");
		            System.out.println("eventName: " + inboundEvent.getName() + ", data: " + inboundEvent.readData(String.class) + "\n");
		        }
	    	}
		);
		System.out.println("Try firing a MODEL_STATE_CHANGED event... Type 'run' to unsubscribe");
		JavaClient.pause();
		
		System.out.println("\nUnsubscribe from MODEL_STATE_CHANGED events\n-------------------------------------");
		System.out.println(areCommunicator.unsubscribe(SseCommunicator.MODEL_STATE_CHANGED) + "\n");
		
		System.out.println("-------------------");
		System.out.println("ARE INFORMATION");
		System.out.println("-------------------");
		System.out.println("*** Type 'run' to continue...\n");
		JavaClient.pause();
		
		//get rest functions
		System.out.println("\nGet Rest functions\n-------------------------------------");
		ArrayList<RestFunction> functions = areCommunicator.getRestFunctions();
		for (RestFunction rf: functions) {
			System.out.println(rf.getHttpRequestType() + " " + rf.getPath());
		}
		
		System.out.println("\nPROGRAM TERMINATED");
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
