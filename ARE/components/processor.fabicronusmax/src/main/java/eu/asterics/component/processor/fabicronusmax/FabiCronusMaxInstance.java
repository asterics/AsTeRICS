
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
 *       This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.processor.fabicronusmax;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 * 
 * @author <your name> [<your email address>] Date: Time:
 */
public class FabiCronusMaxInstance extends AbstractRuntimeComponentInstance {
	final IRuntimeOutputPort opOutConsole = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOutGame = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOutMode = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOutButtons = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOutModel = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.:
	// opMyOutPort.sendData(ConversionUtils.intToBytes(10));

	final IRuntimeEventTriggererPort etpBusy = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpReady = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etploadModel = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	int propComPort = 1;
	String propModeFilePath = ".\\data\\file.csv";

	Map<String, Map<String, ArrayList<ArrayList<String>>>> modes;
	Map<String, List<String>> games;
	List<String> consoles;
	CIMPortController portController = null;
	private boolean running = false;
	private static boolean messageReceived = false;
	private static int timeout = 0;

	String selectedConsole, selectedGame;
	String noData = "";
	String receivedMessage = "";
	boolean errorEEPROM = false;
	boolean busy = false;

	int maxModeCount = 0;
	private int modeCounter = 1;

	private InputStream in = null;
	private OutputStream out = null;
	Thread readThread=null;

	// declare member variables here

	/**
	 * The class constructor.
	 */
	public FabiCronusMaxInstance() {
		// empty constructor
	}

	/**
	 * returns an Input Port.
	 * 
	 * @param portID
	 *            the name of the port
	 * @return the input port or null if not found
	 */
	public IRuntimeInputPort getInputPort(String portID) {
		if ("inConsole".equalsIgnoreCase(portID)) {
			return ipInConsole;
		}
		if ("inGame".equalsIgnoreCase(portID)) {
			return ipInGame;
		}
		if ("inMode".equalsIgnoreCase(portID)) {
			return ipInMode;
		}

		return null;
	}

	/**
	 * returns an Output Port.
	 * 
	 * @param portID
	 *            the name of the port
	 * @return the output port or null if not found
	 */
	public IRuntimeOutputPort getOutputPort(String portID) {
		if ("outConsole".equalsIgnoreCase(portID)) {
			return opOutConsole;
		}
		if ("outGame".equalsIgnoreCase(portID)) {
			return opOutGame;
		}
		if ("outMode".equalsIgnoreCase(portID)) {
			return opOutMode;
		}
		if ("outButtons".equalsIgnoreCase(portID)) {
			return opOutButtons;
		}
		if ("outModel".equalsIgnoreCase(portID)) {
			return opOutModel;
		}

		return null;
	}

	/**
	 * returns an Event Listener Port.
	 * 
	 * @param eventPortID
	 *            the name of the port
	 * @return the EventListener port or null if not found
	 */
	public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
		if ("modeSwitcher".equalsIgnoreCase(eventPortID)) {
			return elpModeSwitcher;
		}
		if ("gameSwitcher".equalsIgnoreCase(eventPortID)) {
			return elpGameSwitcher;
		}
		if ("consoleSwitcher".equalsIgnoreCase(eventPortID)) {
			return elpConsoleSwitcher;
		}

		return null;
	}

	/**
	 * returns an Event Triggerer Port.
	 * 
	 * @param eventPortID
	 *            the name of the port
	 * @return the EventTriggerer port or null if not found
	 */
	public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
		if ("busy".equalsIgnoreCase(eventPortID)) {
			return etpBusy;
		}
		if ("ready".equalsIgnoreCase(eventPortID)) {
			return etpReady;
		}
		if ("loadModel".equalsIgnoreCase(eventPortID)) {
			return etploadModel;
		}

		return null;
	}

	/**
	 * returns the value of the given property.
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @return the property value or null if not found
	 */
	public Object getRuntimePropertyValue(String propertyName) {
		if ("comPort".equalsIgnoreCase(propertyName)) {
			return propComPort;
		}
		if ("modeFilePath".equalsIgnoreCase(propertyName)) {
			return propModeFilePath;
		}

		return null;
	}

	/**
	 * sets a new value for the given property.
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param newValue
	 *            the desired property value or null if not found
	 */
	public Object setRuntimePropertyValue(String propertyName, Object newValue) {
		if ("comPort".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propComPort;
			propComPort = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("modeFilePath".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propModeFilePath;
			propModeFilePath = (String) newValue;
			return oldValue;
		}
		return null;
	}

	public void SetBusyEvent() {
		/*
		 * Thread busyThread = new Thread(new Runnable() {
		 * 
		 * @Override public void run() { etpBusy.raiseEvent(); } });
		 * busyThread.start();
		 */
	}

	public void SetReadyEvent() {
		/*
		 * Thread readyThread = new Thread(new Runnable() {
		 * 
		 * @Override public void run() { //etpReady.raiseEvent(); } });
		 * readyThread.start();
		 */
	}

	public void OutputConsole(String text) {
		System.out
				.print("################################################################################");
		System.out.println("Load Console " + text);
		opOutConsole.sendData(ConversionUtils.stringToBytes(text));
	}

	public void OutputGame(String text) {
		System.out
				.print("################################################################################");
		System.out.println("Load Game " + text);
		opOutGame.sendData(ConversionUtils.stringToBytes(text));
	}

	public void OutputMode(int text) {
		System.out
				.print("################################################################################");
		System.out.println("Load Mode " + text);
		opOutMode.sendData(toByteArray(text));
	}

	public void loadNewModel(String model) {
		opOutModel.sendData(ConversionUtils.stringToBytes(model + ".acs"));
		etploadModel.raiseEvent();
	}

	/**
	 * Input Ports for receiving values.
	 */
	private final IRuntimeInputPort ipInConsole = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			selectedConsole = ConversionUtils.stringFromBytes(data)
					.toUpperCase();

			if (consoles == null || consoles.isEmpty() || portController == null || selectedConsole == null)
				return;
			
			if (consoles.indexOf(selectedConsole) < 0) {
				System.out.println("Console " + selectedConsole
						+ " does not exist in config file!");
				if (consoles.get(0) != null) {
					selectedConsole = consoles.get(0); 
				}
			}
			OutputConsole(selectedConsole);

			loadNewModel(selectedConsole);
		}
	};
	private final IRuntimeInputPort ipInGame = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			SetBusyEvent();
			selectedGame = ConversionUtils.stringFromBytes(data).toUpperCase();
			
			if (games == null || games.isEmpty() || portController == null || selectedConsole == null || selectedGame == null)
				return;

			int index = games.get(selectedConsole).indexOf(selectedGame);
			if (index < 0) {
				selectedGame = games.get(selectedConsole).get(0);
				SetReadyEvent();
				return;
			}

			if (games.get(selectedConsole).get(index) != null) {
				setMode(selectedConsole, games.get(selectedConsole).get(index));
				selectedGame = games.get(selectedConsole).get(index);
			} else if (games.get(selectedConsole).get(0) != null) {
				setMode(selectedConsole, games.get(selectedConsole).get(0));
				selectedGame = games.get(selectedConsole).get(0);
			}

			OutputGame(selectedGame);
			SetReadyEvent();

		}
	};

	public byte[] toByteArray(int value) {
		return new byte[] { (byte) (value >> 24), (byte) (value >> 16),
				(byte) (value >> 8), (byte) value };
	}

	public int byteArrayToInt(byte[] b) {
		return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16
				| (b[0] & 0xFF) << 24;
	}

	private final IRuntimeInputPort ipInMode = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			SetBusyEvent();
			
			if (portController == null)
				return;
			
			int getMode = byteArrayToInt(data);
			switchMode(getMode);
			SetReadyEvent();
		}
	};

	/**
	 * Event Listerner Ports.
	 */
	final IRuntimeEventListenerPort elpModeSwitcher = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			// Call next Mode (AT N)
			switchMode();
		}
	};
	final IRuntimeEventListenerPort elpGameSwitcher = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			SetBusyEvent();
			deleteEEPROM();
			if (selectedConsole == null) {
				SetReadyEvent();
				return;
			}

			if (selectedGame == null) {
				selectedGame = games.get(selectedConsole).get(0);
				OutputGame(selectedGame);
				SetReadyEvent();
				return;
			}

			int index = games.get(selectedConsole).indexOf(selectedGame);

			if ((games.get(selectedConsole).size() - 1) >= (index + 1)) {
				if (games.get(selectedConsole).get(index + 1) != null) {
					selectedGame = games.get(selectedConsole).get(index + 1);
					setMode(selectedConsole,
							games.get(selectedConsole).get(index + 1));
				}
			} else if (games.get(selectedConsole).get(0) != null) {
				selectedGame = games.get(selectedConsole).get(0);
				setMode(selectedConsole, games.get(selectedConsole).get(0));
			}

			OutputGame(selectedGame);
			SetReadyEvent();

			loadNewModel(selectedGame);
		}
	};
	final IRuntimeEventListenerPort elpConsoleSwitcher = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			SetBusyEvent();
			deleteEEPROM();

			if (selectedConsole == null) {
				selectedConsole = consoles.get(0);
				OutputConsole(selectedConsole);
				SetReadyEvent();
				return;
			}

			int index = consoles.indexOf(selectedConsole);

			if (index < 0) {
				selectedConsole = consoles.get(0);
				OutputConsole(selectedConsole);
				SetReadyEvent();
				return;
			}

			if ((consoles.size() - 1) >= (index + 1)) {
				if (consoles.get(index + 1) != null) {
					selectedConsole = consoles.get(index + 1);

					// setMode(consoles.get(index+1),
					// games.get(index+1).get(0));
				}
			} else if (consoles.get(0) != null) {
				selectedConsole = consoles.get(0);
				// setMode(consoles.get(0), games.get(consoles.get(0)).get(0));
			}

			OutputConsole(selectedConsole);
			opOutGame.sendData(ConversionUtils.stringToBytes(""));
			opOutMode.sendData(ConversionUtils.stringToBytes(""));
			SetReadyEvent();

			loadNewModel(selectedConsole);
		}
	};

	/**
	 * called when model is started.
	 */
	@Override
	public void start() {
		SetBusyEvent();
		if (!openCOMPort()) {
			SetReadyEvent();
			super.start();
			return;
		}

		// load csv
		try {
			loadCsv();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SetReadyEvent();
		}

		// delete EEPROM
		deleteEEPROM();

		// set Mode in EEPROM for first Game
		String firstKey = modes.keySet().iterator().next();
		// setMode(firstKey, modes.get(firstKey).keySet().iterator().next());

		try {
			SetCommand("AT ID");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SetReadyEvent();
		super.start();
	}

	private String KeyMouseIdentifier(String button) {
		if (button.contains("KEY_")) {
			button = "AT KP " + button;
		} else {
			button = "AT " + button;
		}
		return button;
	}

	private void setMode(String console, String game) {
		ArrayList<ArrayList<String>> eachMode;
		Map<String, ArrayList<ArrayList<String>>> gameMode;

		selectedConsole = console.toUpperCase();
		selectedGame = game.toUpperCase();

		gameMode = modes.get(console.toUpperCase());
		eachMode = gameMode.get(game.toUpperCase());
		maxModeCount = 1;

		int indexButton = 1;
		busy = true;
		for (ArrayList<String> mode : eachMode) {
			for (String config : mode) {
				try {
					if (errorEEPROM)
						return;
					SetModeCommand("AT BM " + indexButton,
							KeyMouseIdentifier(config));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				indexButton++;
			}
			indexButton = 1;
			// Speichern ins EEPROM
			saveToEEPROM(maxModeCount);
			switchModeForSaving(maxModeCount);

			maxModeCount++;
		}
		busy = false;
		modeCounter = 1;
		switchMode();
		System.out.println("Ready!");
	}

	private void saveToEEPROM(int modeToLoad) {
		try {
			System.out.println("save mode to EEPROM");
			out.write(ConversionUtils.stringToBytes("AT SAVE " + modeToLoad
					+ "\n"));

			checkMessageReceived();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void switchMode() {
		switchMode(modeCounter);
		modeCounter++;

		if (modeCounter >= maxModeCount)
			modeCounter = 1;

	}

	private void switchModeForSaving(int modeToLoad) {
		try {
			out.write(ConversionUtils.stringToBytes("AT NEXT\n"));

			checkMessageReceived();
			checkMessageReceived();

			out.write(ConversionUtils.stringToBytes("AT LOAD " + modeToLoad
					+ "\n"));

			checkMessageReceived();
			checkMessageReceived();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void ShowButtons(int modeToLoad)
	{
		String buttons = "";
		ArrayList<ArrayList<String>> eachMode;
		Map<String, ArrayList<ArrayList<String>>> gameMode;

		gameMode = modes.get(selectedConsole.toUpperCase());
		eachMode = gameMode.get(selectedGame.toUpperCase());

		ArrayList<String> mode = eachMode.get(modeToLoad-1);
		for (String config : mode) {
			if(config.contains("KEY_CTRL KEY_ESC"))
			{
				config = "Modus/Zurueck";
			}
			else if(config.contains("KEY_"))
			{
				config = config.replace("KEY_", "");
			}
			buttons += config +  ",";
		}
		System.out.println("Buttons : " + buttons);
		opOutButtons.sendData(ConversionUtils.stringToBytes(buttons));
	}
	
	private void switchMode(int modeToLoad) {
		ShowButtons(modeToLoad);
		ShowButtons(modeToLoad);
		try {
			OutputMode(modeToLoad);
			System.out.println("switch Mode");

			out.write(ConversionUtils.stringToBytes("AT LOAD " + modeToLoad
					+ "\n"));

			checkMessageReceived();
			checkMessageReceived();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void SetModeCommand(String command, String modeButton)
			throws IOException {
		System.out.println(command);
		out.write(ConversionUtils.stringToBytes(command + "\n"));

		checkMessageReceived();

		System.out.println(modeButton);
		out.write(ConversionUtils.stringToBytes(modeButton + "\n"));

		checkMessageReceived();
	}

	public void SetCommand(String command) throws IOException {
		System.out.println(command);
		out.write(ConversionUtils.stringToBytes(command + "\n"));
		String message = checkMessageReceived();
//		System.out.println(message);
		if (message.contains("2.0")) {
			System.out.println("Fabi2.0 is detected!");
		} else {
			System.out.println("#ERROR: Only Fabi2.0 is supported!");
		}
	}

	private boolean openCOMPort() {
		portController = CIMPortManager.getInstance().getRawConnection(
				"COM" + propComPort, 9600, true);
		
		if (portController == null) {
			System.out
					.println("Fabi: Could not construct raw port controller, please verify that the COM port is valid.");
			return false;
		} else {
			System.out.println("COM" + propComPort + " Port open!");
			in = portController.getInputStream();
			out = portController.getOutputStream();
			readThread = new Thread(new Runnable() {
				@Override
				public void run() {
					running = true;	
					while (running) {										
						try {
							
							if (in.available() > 0) {
								handlePacketReceived((byte)in.read());
							}
							else
							{
								Thread.sleep(1);
							}
						} catch (IOException | InterruptedException io) {
							io.printStackTrace();
						}
					}
				}
			});

			readThread.start();
			
			
		}
		return true;
	}

	public void handlePacketReceived(byte data) {
		System.out.print((char) data);
		noData += (char) data;
		
		if ((char) data == '\n') {
			receivedMessage = noData;
			if (noData.contains("no data found")) {
				errorEEPROM = true;
			}
			messageReceived = true;
			noData = "";
		}

	}

	private void deleteEEPROM() {

		try {
			if (out == null) {
				System.out.println("Problem with COM Port!");
				return;
			}
			System.out.println("AT CLEAR");
			out.write(ConversionUtils.stringToBytes("AT CLEAR\n"));
			checkMessageReceived();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String checkMessageReceived() {
		if (messageReceived) {
			messageReceived = false;
			return null;
		}

		while (!messageReceived) {
			timeout++;
			if (timeout == 50) {
				System.out.println("no data received within half a second");
				timeout = 0;
				messageReceived = true;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		timeout = 0;
		messageReceived = false;
		return receivedMessage;
	}

	private void loadCsv() throws FileNotFoundException, IOException {
		checkPath();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		modes = new HashMap<String, Map<String, ArrayList<ArrayList<String>>>>();
		games = new HashMap<String, List<String>>();
		consoles = new ArrayList<String>();
		System.out.println("Open configuartion file at path "
				+ propModeFilePath);
		try {

			br = new BufferedReader(new FileReader(propModeFilePath));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				//line = line.replace(" ", "");
				
				String[] gameMode = line.split(cvsSplitBy);
				String consoleNameUp = gameMode[0].toUpperCase();
				String gameNameUp = gameMode[1].toUpperCase();

				System.out.println("Game-Mode " + consoleNameUp + ", game "
						+ gameNameUp);
				Map<String, ArrayList<ArrayList<String>>> gameName = new HashMap<String, ArrayList<ArrayList<String>>>();
				ArrayList<ArrayList<String>> eachMode = new ArrayList<ArrayList<String>>();

				ArrayList<String> config = null;

				for (String configInFile : gameMode) {
					if (configInFile == null)
						continue;

					if (configInFile.toUpperCase().equals("MODE")) {
						if (config != null)
							eachMode.add(config);

						config = new ArrayList<String>();
					} else {
						if (config == null)
							continue;

						config.add(configInFile);
					}
				}
				eachMode.add(config);
				if (modes.containsKey(consoleNameUp)) {
					modes.get(consoleNameUp).put(gameNameUp, eachMode);
					games.get(consoleNameUp).add(gameNameUp);
				} else {
					gameName.put(gameNameUp, eachMode);
					modes.put(consoleNameUp, gameName);

					consoles.add(consoleNameUp);
					List<String> gameTemp = new ArrayList<String>();
					gameTemp.add(gameNameUp);
					games.put(consoleNameUp, gameTemp);
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void checkPath() {
		if (propModeFilePath.contains("/"))
			return;

		if (propModeFilePath.contains("\\\\"))
			return;

		propModeFilePath.replace("\\", "\\\\");
	}

	/**
	 * called when model is paused.
	 */
	@Override
	public void pause() {
		
		waitUnitlEverythingIsFinished();
		
		super.pause();
	}

	/**
	 * called when model is resumed.
	 */
	@Override
	public void resume() {
		openCOMPort();
		super.resume();
	}

	/**
	 * called when model is stopped.
	 */
	@Override
	public void stop() {
		
		waitUnitlEverythingIsFinished();
		
		if(readThread != null)		
		{
			running = false;
			try {
				readThread.join(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		super.stop();
	}
	
	
	private void waitUnitlEverythingIsFinished()
	{
		int timeout = 0;
		while(busy || timeout < 20)
		{
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			timeout ++;			
		}
		
		if(portController!=null)
		{
			CIMPortManager.getInstance().closeRawConnection("COM" + propComPort);
		}
	}
}