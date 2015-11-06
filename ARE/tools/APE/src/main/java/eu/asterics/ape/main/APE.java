package eu.asterics.ape.main;

import java.awt.EventQueue;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import com.sun.org.apache.xalan.internal.utils.XMLSecurityPropertyManager.Property;

import eu.asterics.ape.packaging.Packager;
import eu.asterics.ape.parse.ModelInspector;
import eu.asterics.mw.are.exceptions.BundleManagementException;
import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.ResourceRegistry;

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
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 * 
 * 
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */

/**
 * 
 *         Author: martin.deinhofer@technikum-wien.at
 *         Date: Oct 30, 2015
 *         Time: 14:30:00 PM
 */

public class APE {
	/**
	 * The properties of APE
	 */
	private APEProperties apeProperties=null;
	/**
	 * Reference to the ModelInspector instance.
	 */
	private ModelInspector modelInspector=null;
	/**
	 * Reference to the Packager instance.
	 */
	private Packager packager=null;
	
	private static APE instance=null;
	
	static {
		URI defaultAPEBaseURI=Paths.get(".").toUri();
		try {
			defaultAPEBaseURI = ResourceRegistry.toPath(APE.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toUri();
		} catch (URISyntaxException e) {
		}
		
		APEProperties.APE_BASE_URI=URI.create(System.getProperty("eu.asterics.APE.baseURI", defaultAPEBaseURI.toString()));
		System.out.println("Setting APE base URI to <"+APEProperties.APE_BASE_URI+">");
	}
	
	/**
	 * Launch the application.
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws TransformerException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws URISyntaxException 
	 * @throws BundleManagementException 
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws IOException, ParseException, URISyntaxException, ParserConfigurationException, SAXException, TransformerException, BundleManagementException {
		APE.getInstance().start();
	}
	
	/**
	 * Returns the singleton instance of APE.
	 * @return
	 */
	public static APE getInstance() {
		if(instance==null) {
			instance=new APE();
		}
		return instance;
	}

	/**
	 * Initializes and starts APE.
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws TransformerException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws URISyntaxException 
	 * @throws BundleManagementException 
	 */
	public void start() throws IOException, ParseException, URISyntaxException, ParserConfigurationException, SAXException, TransformerException, BundleManagementException {
		
		AstericsErrorHandling.instance.getLogger().setLevel(Level.FINE);
		ResourceRegistry.getInstance().setOSGIMode(false);
		ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").toURI());
		setAPEBaseURI(new File("../bin/APE").toURI());

		initProperties();

		modelInspector=new ModelInspector(apeProperties);
		packager=new Packager(apeProperties, modelInspector);
		packager.makeAll();
	}

	private void initProperties() {
		Properties defaultProperties=new Properties();
		try {
			defaultProperties.load(new BufferedReader(new InputStreamReader(APEProperties.APE_BASE_URI.resolve("APE.properties").toURL().openStream())));
			apeProperties=new APEProperties(defaultProperties);
			for(Entry<Object, Object> entry : System.getProperties().entrySet()) {
				if(entry.getKey().toString().startsWith(APEProperties.APE_PROP_PREFIX)||entry.getKey().toString().startsWith(APEProperties.ARE_PROP_PREFIX)) {
					apeProperties.setProperty(entry.getKey().toString(), entry.getValue().toString());
				}
			}
		} catch (IOException e) {
		}
	}
	public void exit() {
		
	}

	public APEProperties getApeProperties() {
		return apeProperties;
	}

	public void setApeProperties(APEProperties apeProperties) {
		this.apeProperties = apeProperties;
	}

	public static URI getAPEBaseURI() {
		return APEProperties.APE_BASE_URI;
	}

	public static void setAPEBaseURI(URI APEBaseURI) {
		APEProperties.APE_BASE_URI = APEBaseURI;
		System.out.println("Setting APE base URI to: "+APEBaseURI);		
	}
	
	public ModelInspector getModelInspector() {
		return modelInspector;
	}

	public void setModelInspector(ModelInspector modelInspector) {
		this.modelInspector = modelInspector;
	}

	public Packager getPackager() {
		return packager;
	}

	public void setPackager(Packager packager) {
		this.packager = packager;
	}
}
