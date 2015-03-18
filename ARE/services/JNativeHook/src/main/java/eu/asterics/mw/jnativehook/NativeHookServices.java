package eu.asterics.mw.jnativehook;

import org.jnativehook.*;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Helper class to init the jnativehook classes. This is needed because otherwise each plugin using jnativehook would redundantly call
 * registerNativeHook.
 * @author mad
 *
 */
public class NativeHookServices {
	public static NativeHookServices instance=null;
	
	private NativeHookServices() {
		AstericsErrorHandling.instance.getLogger().fine("Registering native hooks...");
		try 
		{
			GlobalScreen.getInstance().setEventDispatcher(new VoidExecutorService());
			GlobalScreen.registerNativeHook();
			AstericsErrorHandling.instance.getLogger().fine("Registered native hooks");
		} catch (NativeHookException ne){
			AstericsErrorHandling.instance.getLogger().warning("Could not register native hooks: "+ne.getMessage());
			ne.printStackTrace();
		}
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
	
	@Override
	protected void finalize() throws Throwable {
		System.out.println("Unregistering native hook...");
		GlobalScreen.unregisterNativeHook();
		super.finalize();
	}
}
