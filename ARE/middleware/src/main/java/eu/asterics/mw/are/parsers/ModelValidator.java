package eu.asterics.mw.are.parsers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.osgi.framework.BundleContext;
import org.xml.sax.SAXException;

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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */

/**
 * @author Costas Kakousis [kakousis@cs.ucy.ac.cy]
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy] This class validates the
 *         models Date: Jul 16, 2010 Time: 4:03:23 PM
 */
public class ModelValidator {
    public static final String XML_SCHEMA_URL = "http://www.w3.org/2001/XMLSchema";

    public static final String BUNDLE_DESCRIPTOR_SCHEMA_URL = "/schemas/bundle_model.xsd";

    public static final String DEPLOYMENT_DESCRIPTOR_SCHEMA_URL = "/schemas/deployment_model.xsd";

    private static Logger logger = AstericsErrorHandling.instance.getLogger();

    // Lookup a factory for the W3C XML Schema language
    public static final SchemaFactory SCHEMA_FACTORY = SchemaFactory.newInstance(XML_SCHEMA_URL);

    private Validator bundleDescriptorValidator;
    private Validator deploymentDescriptorValidator;

    private static ModelValidator instance = null;

    public ModelValidator() throws MalformedURLException {
        this(ResourceRegistry
                .toJarInternalURI(ResourceRegistry.getInstance().getAREJarURI(false), BUNDLE_DESCRIPTOR_SCHEMA_URL)
                .toURL(),
                ResourceRegistry.toJarInternalURI(ResourceRegistry.getInstance().getAREJarURI(false),
                        DEPLOYMENT_DESCRIPTOR_SCHEMA_URL).toURL());
    }

    /**
     * Validates the bundleContext's bundle.
     * 
     * @param bundleContext
     *            the bundle to be parsed
     */
    public ModelValidator(final BundleContext bundleContext) {
        this(bundleContext.getBundle().getResource(BUNDLE_DESCRIPTOR_SCHEMA_URL),
                bundleContext.getBundle().getResource(DEPLOYMENT_DESCRIPTOR_SCHEMA_URL));
    }

    /**
     * This constructor can be used for cases where no OSGI BundleContext is
     * available.
     * 
     * @param bundleDescriptorSchemaURL
     * @param deploymentDescriptorSchemaURL
     */
    ModelValidator(URL bundleDescriptorSchemaURL, URL deploymentDescriptorSchemaURL) {
        // Initiate the bundle-descriptor validator
        try {
            synchronized (SCHEMA_FACTORY) {
                final Schema bundleDescriptorSchema = SCHEMA_FACTORY.newSchema(bundleDescriptorSchemaURL);
                bundleDescriptorValidator = bundleDescriptorSchema.newValidator();
            }

        } catch (SAXException saxe) {
            logger.warning(this.getClass().getName() + "." + "ModelValidator: Could not instantiate bundle model -> \n"
                    + saxe.getMessage());
            throw new RuntimeException("Could not instantiate bundle model " + "validator (" + saxe.getMessage() + ")");
        }

        // Initiate the deployment-descriptor validator
        try {
            synchronized (SCHEMA_FACTORY) {
                final Schema deploymentDescriptorSchema = SCHEMA_FACTORY.newSchema(deploymentDescriptorSchemaURL);
                deploymentDescriptorValidator = deploymentDescriptorSchema.newValidator();
            }
        } catch (SAXException saxe) {
            logger.warning(this.getClass().getName() + "." + "ModelValidator: Could not instantiate deployment model"
                    + " -> \n" + saxe.getMessage());
            throw new RuntimeException(
                    "Could not instantiate deployment model " + "validator (" + saxe.getMessage() + ")");
        }
        instance = this;
    }

    /**
     * Returns the ModelValidator instance
     * 
     * @return the ModelValidator instance
     */
    public static ModelValidator getInstance() {
        if (instance == null) {
            logger.warning(ModelValidator.class.getName() + "." + "getInstance: Empty model validator -> \n");
            throw new RuntimeException("Empty model validator");
        } else {
            return instance;
        }
    }

    /**
     * Checks if the input XML file is a valid instance of the given input
     * schema
     * 
     * @param schemaLocation:
     *            The path to the XML schema that defines the structure of the
     *            XML model
     * @param xmlFileName:
     *            The path to the XML file to be checked if it is a valid
     *            instance of the given schema.
     * @return true if the given XML file is valid against the given schema,
     *         false otherwise
     */
    public boolean isValid(String schemaLocation, String xmlFileName) {
        // 1. Compile the schema.
        Schema schema;
        try {
            File schemaFile = new File(schemaLocation);

            schema = SCHEMA_FACTORY.newSchema(schemaFile);
            // 2. Get a validator from the schema.
            Validator validator = schema.newValidator();

            // 3. Parse the document you want to check.
            Source source = new StreamSource(xmlFileName);

            // 4. Check the document
            validator.validate(source);
            return true;
        } catch (SAXException ex) {
            logger.warning(
                    this.getClass().getName() + ".isValid: " + xmlFileName + " not valid -> \n" + ex.getMessage());
            return false;
        } catch (IOException e) {
            logger.warning(this.getClass().getName() + ".isValid: Could not " + "read from source -> " + xmlFileName);
            return false;
        }
    }

    /**
     * Checks if the input XML file is a valid instance of the given input
     * schema
     * 
     * @param schemaLocation:
     *            The path to the XML schema that defines the structure of the
     *            XML model
     * @param xmlFileName:
     *            The path to the XML file to be checked if it is a valid
     *            instance of the given schema.
     * @return true if the given XML file is valid against the given schema,
     *         false otherwise
     */
    public boolean isValid(URL schemaLocation, String xmlFileName) {
        // 1. Compile the schema.
        Schema schema;
        try {
            schema = SCHEMA_FACTORY.newSchema(schemaLocation);

            // 2. Get a validator from the schema.
            Validator validator = schema.newValidator();

            // 3. Parse the document you want to check.
            Source source = new StreamSource(xmlFileName);

            // 4. Check the document
            validator.validate(source);
            return true;
        } catch (SAXException ex) {
            logger.warning(
                    this.getClass().getName() + ".isValid: " + xmlFileName + " not valid -> \n" + ex.getMessage());
            return false;
        } catch (IOException e) {
            logger.warning(this.getClass().getName() + ".isValid: Could not " + "read from source -> " + xmlFileName);
            return false;
        }
    }

    /**
     * Tests if the given input stream contains a valid bundle descriptor according to {@link ModelValidator#BUNDLE_DESCRIPTOR_SCHEMA_URLL}.
     * 
     * @param inputStream
     * @return true if the file encoded in the inputStream is valid with respect
     *         to the bundle format XSD, false otherwise
     * @throws ParseException
     * @throws IOException
     */
    public boolean isValidBundleDescriptor(final InputStream inputStream) throws ParseException, IOException {
        // Parse the document you want to check.
        Source source = new StreamSource(inputStream);
        return isValidBundleDescriptor(source);
    }
    
    /**
     * Tests if the given input stream contains a valid bundle descriptor according to {@link ModelValidator#BUNDLE_DESCRIPTOR_SCHEMA_URLL}. 
     * @param xmlSource
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public boolean isValidBundleDescriptor(final Source xmlSource) throws ParseException, IOException {    
        try {
            // Check the document
            bundleDescriptorValidator.validate(xmlSource);
            return true;
        } catch (SAXException ex) {
            logger.warning(this.getClass().getName() + "." + "isValidBundleDescriptor: input stream not a valid "
                    + "bundle descriptor -> \n" + ex.getMessage());
            throw new ParseException("Bundle descriptor could not be parsed: " + ex.getMessage());
        } catch (IOException e) {
            logger.warning(this.getClass().getName() + ".isValidBundleDescriptor: "
                    + "Could not read from source input stream \n");
            throw e;
        }
    }
    
    /**
     * Tests if the given input stream contains a valid bundle descriptor according to {@link ModelValidator#BUNDLE_DESCRIPTOR_SCHEMA_URLL}. 
     * @param bundleDescriptorAsXMLString
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public boolean isValidBundleDescriptor(final String bundleDescriptorAsXMLString) throws ParseException, IOException {
        Source xmlSource = new StreamSource(new StringReader(bundleDescriptorAsXMLString));
        return isValidBundleDescriptor(xmlSource);
    }
    /**
     * Tests if the given input stream contains a valid model according to {@link ModelValidator#DEPLOYMENT_DESCRIPTOR_SCHEMA_URL}.  
     * 
     * @param inputStream
     * @return true if the file encoded in the inputStream is valid, false otherwise
     * @throws IOException
     * @throws ParseException
     */
    public boolean isValidDeploymentDescriptor(final InputStream inputStream) throws IOException, ParseException {
        return isValidDeploymentDescriptor(new StreamSource(inputStream));
    }
    
    /**
     * Tests if the given input stream contains a valid model according to {@link ModelValidator#DEPLOYMENT_DESCRIPTOR_SCHEMA_URL}. 
     * @param xmlSource
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public boolean isValidDeploymentDescriptor(final Source xmlSource) throws IOException, ParseException {
        try {
            // Check the document
            deploymentDescriptorValidator.validate(xmlSource);
            return true;
        } catch (SAXException ex) {
            logger.warning(this.getClass().getName() + "." + "isValidDeploymentDescriptor: input stream not a valid "
                    + "deployment descriptor -> \n" + ex.getMessage());
            throw new ParseException("Deployment descriptor could not be parsed: " + ex.getMessage());
        } catch (IOException e) {
            logger.warning(this.getClass().getName() + ".isValidBundleDescriptor: "
                    + "Could not read from source input stream \n");
            throw e;
        }        
    }
    
    /**
     * Tests if the given input stream contains a valid model according to {@link ModelValidator#DEPLOYMENT_DESCRIPTOR_SCHEMA_URL}. 
     * @param modelInXML
     * @return
     * @throws IOException
     * @throws ParseException
     * @throws SAXException
     */
    public boolean isValidDeploymentDescriptor(String modelInXML) throws IOException, ParseException, SAXException {        
        Source xmlSource = new StreamSource(new StringReader(modelInXML));
        return isValidDeploymentDescriptor(xmlSource);
    }    
}
