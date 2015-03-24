package eu.asterics.mw.jnativehook;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class NativeHookActivator implements BundleActivator {

	@Override
	public void start(BundleContext arg0) throws Exception {
		// TODO Auto-generated method stub
		NativeHookServices.init();
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		// TODO Auto-generated method stub
		NativeHookServices.unInit();
	}

	
}
