package com.example.asterics_android;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.util.StringMap;
import org.osgi.framework.BundleContext;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.util.Log;

import org.apache.felix.bundlerepository.*;
import org.apache.felix.gogo.command.*;
import org.apache.felix.gogo.runtime.*;
import org.apache.felix.gogo.shell.*;

public class MainActivity extends Activity {

	private Felix m_felix = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			launchFelix();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	 void launchFelix() {    	
	    	Log.d("info","About to start Felix...");
	    	String cacheDir = null;    	
	    	try {
				cacheDir = File.createTempFile("skifta", ".tmp").getParent();
			}
	    	catch (IOException e){
				Log.d("Felix", "unable to create temp file", e);
				return;
			} 
	        
	    	Map configMap = new HashMap();                    
	        configMap.put("org.osgi.framework.storage", cacheDir);
	        configMap.put("felix.embedded.execution", "true");
	        configMap.put("org.osgi.service.http.port", "9990");
	        configMap.put("org.osgi.framework.startlevel.beginning", "5");
	        try
	        {            
	            m_felix = new Felix(configMap); // Now create an instance of the framework        	
	            m_felix.start();				// Start Felix instance.           
	            Log.d("Felix","Felix is started");	
	            
	            //Load basic Felix bundles
	            m_felix.getBundleContext().installBundle("file:/sdcard/asterics/org.apache.felix.gogo.shell-0.10.0.jar");
	            Log.d("Felix","Gogo shell installed");
	            m_felix.getBundleContext().installBundle("file:/sdcard/asterics/org.apache.felix.gogo.runtime-0.10.0.jar");
	            Log.d("Felix","Gogo runtime installed");
	            m_felix.getBundleContext().installBundle("file:/sdcard/asterics/org.apache.felix.gogo.command-0.12.0.jar");
	            Log.d("Felix","Gogo command installed");
	            m_felix.getBundleContext().installBundle("file:/sdcard/asterics/org.apache.felix.bundlerepository-1.6.6.jar");
	            Log.d("Felix","Bundlerepository installed");
	            
	                        
	            for(org.osgi.framework.Bundle b : m_felix.getBundleContext().getBundles()) 
	            	Log.d("info","Bundle: " + b.getSymbolicName());            	
	        } 
	        catch (Throwable ex) {
	            Log.d("Felix","Could not create framework: " + ex.getMessage(), ex);
	        }    	
	    }     
	 private static final Constructor m_dexFileClassConstructor;
	 private static final Method m_dexFileClassLoadClass;

	 static
	 {
	     Constructor dexFileClassConstructor = null;
	     Method dexFileClassLoadClass = null;
	     try
	     {
	         Class dexFileClass =  Class.forName("android.dalvik.DexFile");
	         dexFileClassConstructor = dexFileClass.getConstructor(
	             new Class[] { java.io.File.class });
	         dexFileClassLoadClass = dexFileClass.getMethod("loadClass",
	             new Class[] { String.class, ClassLoader.class });
	     }
	     catch (Exception ex)
	     {
	         dexFileClassConstructor = null;
	         dexFileClassLoadClass = null;
	     }
	     m_dexFileClassConstructor = dexFileClassConstructor;
	     m_dexFileClassLoadClass = dexFileClassLoadClass;
	 }

	 private Object m_dexFile = null;

	 public synchronized Class getDexFileClass(String name, ClassLoader loader)
	     throws Exception
	 {
	     if (m_dexFile == null)
	     {
	         if ((m_dexFileClassConstructor != null) &&
	               (m_dexFileClassLoadClass != null))
	         {
	             //m_dexFile = m_dexFileClassConstructor.newInstance(
	                 //new Object[] { m_file });
	         }
	         else
	         {
	             return null;
	         }
	     }

	     return (Class) m_dexFileClassLoadClass.invoke(m_dexFile,
	         new Object[] { name.replace('.','/'), loader });
	 }

}
