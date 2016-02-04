package eu.asterics.ape.main;

import java.net.URI;
import java.util.Properties;

/**
 * This class provides property keys and manages the property values of a running APE instance.
 * It can be instantiated with an instance of a Property object providing default values for each property.
 * @author mad
 *
 */
public class APEProperties extends Properties {
	public static final String P_APE_BASE_URI="APE.baseURI";
	public static final String P_ARE_BASE_URI="ARE.baseURI";
	public static final String P_APE_MODELS="APE.models";
	public static final String P_APE_PROJECT_DIR="APE.projectDir";
	public static final String P_APE_BUILD_DIR="APE.buildDir";
	public static final String P_APE_DATA_COPY_MODE="APE.dataCopyMode";
	public static final String P_APE_LOG_LEVEL="APE.logLevel";
	
	public static final String APE_PROP_PREFIX="APE.";
	public static final String ARE_PROP_PREFIX = "ARE.";
	public static final String FX_PROP_PREFIX = "fx.";
	
	public static String DEFAULT_PROJECT_DIR="defProjectDir/";
	public static String DEFAULT_BUILD_DIR="build/";
	public static String DEFAULT_APE_LOG_LEVEL="INFO";
	
	public static URI APE_BASE_URI=null;
	public static URI APE_PROJECT_DIR_URI=null;
	//public static URI APE_PROP_FILE_BASE_URI=null;

	/**
	 * Allowed modes for property {@link APEProperties#P_APE_DATA_COPY_MODE}
	 * @author mad
	 *
	 */
	public enum DATA_COPY_MODE {
		ALL,
		FOLDER,
		SINGLE,
		NONE
	}
	
	public APEProperties() {
	}

	public APEProperties(Properties defaults) {
		super(defaults);
	}

}
