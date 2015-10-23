package eu.asterics.ape.parse;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.osgi.framework.internal.core.BundleContextImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.xml.sax.SAXException;

import eu.asterics.mw.are.BundleManager;
import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.model.deployment.IComponentInstance;
import eu.asterics.mw.model.deployment.IRuntimeModel;
import eu.asterics.mw.services.AstericsErrorHandling;

public class TestModelInspector {
	ModelInspector modelInspector;

	@Before
	public void setUp() throws Exception {
		modelInspector=new ModelInspector();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParseModel() {
		
		try {
			Path testModel = Paths.get("tools/APE/src/test/resources/models/test_deployment_model.acs");
			InputStream iStr=testModel.toUri().toURL().openStream();
			IRuntimeModel runtimeModel=modelInspector.parseModel(iStr);
			System.out.println("instanceIds: "+Arrays.toString(runtimeModel.getComponentInstancesIDs()));
			
			System.out.println("Componenttypes used: ");
			for(IComponentInstance compInstance : runtimeModel.getComponentInstances()) {
				System.out.println(compInstance.getComponentTypeID()+" [");

				String [] propKeys=runtimeModel.getComponentPropertyKeys(compInstance.getInstanceID());
				for(String propKey : propKeys) {
					System.out.println(propKey+"="+runtimeModel.getComponentProperty(compInstance.getInstanceID(), propKey));
				}
				System.out.println("]");				
			}
			
			List<URI> modelComponentJarList=modelInspector.getComponentJarURIListOfModel(runtimeModel);
			for(URI componentJarURI : modelComponentJarList) {
				System.out.println(componentJarURI);
			}
		} catch (ParseException | IOException | ParserConfigurationException | SAXException | TransformerException e) {
			// TODO Auto-generated catch block
			fail(e.getMessage());
		}

	}
	
	@Test
	public void testGenerateComponentListCache() {
		try {
			modelInspector.generateComponentListCache(new File("loader_componentlist.ini"));
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testRegisterBundle() {
	}

	class EmptyBundleContext implements BundleContext {
		private List<Bundle> bundles=new ArrayList<Bundle>();

		@Override
		public void addBundleListener(BundleListener arg0) {
			// TODO Auto-generated method stub
			AstericsErrorHandling.instance.getLogger().entering("EmptyBundleContext", "addBundleListener");
		}

		@Override
		public void addFrameworkListener(FrameworkListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addServiceListener(ServiceListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addServiceListener(ServiceListener arg0, String arg1) throws InvalidSyntaxException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Filter createFilter(String arg0) throws InvalidSyntaxException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ServiceReference[] getAllServiceReferences(String arg0, String arg1) throws InvalidSyntaxException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Bundle getBundle() {
			// TODO Auto-generated method stub
			AstericsErrorHandling.instance.getLogger().entering("EmptyBundleContext", "getBundle");
			return new EmptyBundle();
		}

		@Override
		public Bundle getBundle(long arg0) {
			// TODO Auto-generated method stub
			AstericsErrorHandling.instance.getLogger().entering("EmptyBundleContext", "getBundle(long)");
			return new EmptyBundle();
		}

		@Override
		public Bundle[] getBundles() {
			return bundles.toArray(new Bundle[bundles.size()]);
		}

		@Override
		public File getDataFile(String arg0) {
			AstericsErrorHandling.instance.getLogger().entering("EmptyBundleContext", "getDataFile(String arg0)");
			return null;
		}

		@Override
		public String getProperty(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getService(ServiceReference arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ServiceReference getServiceReference(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ServiceReference[] getServiceReferences(String arg0, String arg1) throws InvalidSyntaxException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Bundle installBundle(String arg0) throws BundleException {
			Bundle bundle=new EmptyBundle();
			bundles.add(bundle);
			return bundle;
		}

		@Override
		public Bundle installBundle(String arg0, InputStream arg1) throws BundleException {
			// TODO Auto-generated method stub
			return installBundle(arg0);
		}

		@Override
		public ServiceRegistration registerService(String[] arg0, Object arg1, Dictionary arg2) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ServiceRegistration registerService(String arg0, Object arg1, Dictionary arg2) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void removeBundleListener(BundleListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeFrameworkListener(FrameworkListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeServiceListener(ServiceListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean ungetService(ServiceReference arg0) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	class EmptyBundle implements Bundle {

		@Override
		public Enumeration findEntries(String arg0, String arg1, boolean arg2) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public BundleContext getBundleContext() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getBundleId() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public URL getEntry(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Enumeration getEntryPaths(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Dictionary getHeaders() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Dictionary getHeaders(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getLastModified() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getLocation() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ServiceReference[] getRegisteredServices() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public URL getResource(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Enumeration getResources(String arg0) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ServiceReference[] getServicesInUse() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map getSignerCertificates(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getState() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getSymbolicName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Version getVersion() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasPermission(Object arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Class loadClass(String arg0) throws ClassNotFoundException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void start() throws BundleException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void start(int arg0) throws BundleException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void stop() throws BundleException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void stop(int arg0) throws BundleException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void uninstall() throws BundleException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void update() throws BundleException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void update(InputStream arg0) throws BundleException {
			// TODO Auto-generated method stub
			
		}
		
	}
}
