package eu.asterics.mw.jnativehook;

import java.lang.reflect.Field;

import org.jnativehook.*;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.osgi.framework.BundleActivator;

import eu.asterics.mw.are.AREProperties;
import eu.asterics.mw.are.AsapiSupport;
import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Helper class to init the jnativehook classes. This is needed because otherwise each plugin using jnativehook would redundantly call
 * registerNativeHook.
 * Also implements a NativeKeyListener and registers hotkeys to start, pause or stop a model.
 * @author mad
 *
 */
public class NativeHookServices implements NativeKeyListener {
	private static final String ARE_HOT_KEY_START_MODEL = "ARE.hotKey.startModel";
	private static final String ARE_HOT_KEY_PAUSE_MODEL = "ARE.hotKey.pauseModel";
	private static final String ARE_HOT_KEY_STOP_MODEL = "ARE.hotKey.stopModel";
	
	private int keyCodeStartModel=-1;
	private int keyCodePauseModel=-1;
	private int keyCodeStopModel=-1;	

	public static NativeHookServices instance=null;
	
	private AsapiSupport as;	

	private NativeHookServices() {
		AstericsErrorHandling.instance.getLogger().fine("Registering native hooks...");
		try 
		{
			GlobalScreen.getInstance().setEventDispatcher(new VoidExecutorService());
			
			GlobalScreen.getInstance().removeNativeKeyListener(this);
			GlobalScreen.unregisterNativeHook();		

			
			GlobalScreen.registerNativeHook();
			GlobalScreen.getInstance().addNativeKeyListener(this);
			
			as=new AsapiSupport();
			
			storeDefaultProperties();
			initHotKeys();
			AstericsErrorHandling.instance.getLogger().fine("Registered native hooks");
		} catch (NativeHookException ne){
			AstericsErrorHandling.instance.getLogger().warning("Could not register native hooks: "+ne.getMessage());
			ne.printStackTrace();
		}
	}
	
	private void initHotKeys() {
		keyCodeStartModel=initHotKey(ARE_HOT_KEY_START_MODEL);
		keyCodePauseModel=initHotKey(ARE_HOT_KEY_PAUSE_MODEL);
		keyCodeStopModel=initHotKey(ARE_HOT_KEY_STOP_MODEL);
	}
	
	private int initHotKey(String key) {
		AREProperties props=AREProperties.instance;
		Field f;
		try {
			String val=props.getProperty(key);
			f = NativeKeyEvent.class.getField(val);
			Class<?> t = f.getType();
			if(t == int.class){	
				try {
					AstericsErrorHandling.instance.getLogger().fine("Hotkey for "+key+"="+val);
					switch(key) {
					case ARE_HOT_KEY_START_MODEL:
						DeploymentManager.instance.getGUI().setStartKeyName(val);
						break;
					case ARE_HOT_KEY_PAUSE_MODEL:
						DeploymentManager.instance.getGUI().setPauseKeyName(val);
						break;
					case ARE_HOT_KEY_STOP_MODEL:
						DeploymentManager.instance.getGUI().setStopKeyName(val);
						break;
					}
					return f.getInt(null);
				} catch (IllegalArgumentException | IllegalAccessException e) {
				}
			}
		} catch (NoSuchFieldException | SecurityException e1) {
			AstericsErrorHandling.instance.getLogger().fine("Ignoring hotkey for "+key);
		}
		return -1;
	}
	
	private void storeDefaultProperties() {
		AREProperties props=AREProperties.instance;
		
		if(!props.containsKey(ARE_HOT_KEY_START_MODEL)) {
			props.setProperty(ARE_HOT_KEY_START_MODEL, "VC_F5");
		}
		if(!props.containsKey(ARE_HOT_KEY_PAUSE_MODEL)) {
			props.setProperty(ARE_HOT_KEY_PAUSE_MODEL, "VC_F6");
		}
		if(!props.containsKey(ARE_HOT_KEY_STOP_MODEL)) {
			props.setProperty(ARE_HOT_KEY_STOP_MODEL, "VC_F7");
		}
		props.storeProperties();
	}

	public static NativeHookServices getInstance() {
		if(instance==null) {
			instance=new NativeHookServices();
		}
		return instance;
	}
	
	public static void init() {
		getInstance();
	}
	
	public static void unInit() {
		System.out.println("Unregistering native hook...");
		if(instance!=null) {
			try {
				getInstance().finalize();
			} catch (Throwable e) {
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		GlobalScreen.getInstance().removeNativeKeyListener(this);
		GlobalScreen.unregisterNativeHook();		
		super.finalize();
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nke) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nke) {
		// TODO Auto-generated method stub
		//System.out.println("Native key released: "+nke.getKeyText(nke.getKeyCode()));
		if(nke.getKeyCode()==keyCodeStartModel) {
			try {
				as.runModel();
			} catch (AREAsapiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if(nke.getKeyCode()==keyCodePauseModel) {
			try {
				as.pauseModel();
			} catch (AREAsapiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}else if(nke.getKeyCode()==keyCodeStopModel) {
			try {
				as.stopModel();
			} catch (AREAsapiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent nke) {
		// TODO Auto-generated method stub
		//System.out.println("Native key typed: "+nke);
	}
}
