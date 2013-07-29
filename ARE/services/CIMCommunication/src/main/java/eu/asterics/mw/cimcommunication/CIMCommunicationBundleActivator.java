package eu.asterics.mw.cimcommunication;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class CIMCommunicationBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext arg0) throws Exception 
	{  
		System.out.println("cim start");
		CIMPortManager.getInstance();
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		CIMPortManager.uninitialize(); 		
	}

}
