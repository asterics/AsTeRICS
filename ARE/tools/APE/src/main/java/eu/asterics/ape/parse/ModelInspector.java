package eu.asterics.ape.parse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import eu.asterics.ape.main.APEProperties;
import eu.asterics.ape.main.Notifier;
import eu.asterics.mw.are.BundleManager;
import eu.asterics.mw.are.ComponentRepository;
import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.are.exceptions.BundleManagementException;
import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.are.parsers.DefaultDeploymentModelParser;
import eu.asterics.mw.are.parsers.ModelValidator;
import eu.asterics.mw.model.bundle.IComponentType;
import eu.asterics.mw.model.deployment.IComponentInstance;
import eu.asterics.mw.model.deployment.IRuntimeModel;
import eu.asterics.mw.services.ResourceRegistry;
import eu.asterics.mw.services.ResourceRegistry.RES_TYPE;

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
 * This class is responsible for inspecting a model file and returning a list of
 * used componentTypes and componentTypeJarURIs.
 * 
 * Author: martin.deinhofer@technikum-wien.at Date: Oct 30, 2015 Time: 14:30:00
 * PM
 */

public class ModelInspector {
    private static final String MODELS_PROP_SEPERATOR = ";";
    ModelValidator modelValidator = null;
    DefaultDeploymentModelParser deploymentModelParser = null;
    BundleManager bundleManager = null;
    APEProperties apeProperties = null;

    private static final RES_TYPE[] CHECK_RES_TYPE_ORDER = { RES_TYPE.DATA, RES_TYPE.ANY, RES_TYPE.MODEL,
            RES_TYPE.STORAGE, RES_TYPE.IMAGE };

    public ModelInspector(APEProperties apeProperties) throws IOException, ParseException, URISyntaxException {
        this.apeProperties = apeProperties;

        modelValidator = new ModelValidator();
        deploymentModelParser = DefaultDeploymentModelParser.create(modelValidator);
        bundleManager = new BundleManager(modelValidator);

        // bundleManager.createComponentListCache();
        DeploymentManager.instance.setBundleManager(bundleManager);
        bundleManager.start();
    }

    /**
     * Parse the given InputStream object expecting model xml data as content.
     * 
     * @param modelStream
     * @return
     * @throws ParseException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TransformerException
     * @throws BundleManagementException
     */
    public IRuntimeModel parseModel(InputStream modelStream) throws ParseException, ParserConfigurationException,
            SAXException, IOException, TransformerException, BundleManagementException {
        IRuntimeModel runtimeModel=deploymentModelParser.parseModel(modelStream);
        return runtimeModel;
    }

    /**
     * Returns a Set of .jar URIs corresponding to the existing componentTypes
     * in the given IRuntimeModel model.
     * 
     * @param model
     * @return
     */
    public Set<URI> getComponentTypeJarURIsOfModel(IRuntimeModel model) {
        Set<URI> modelComponentJarURIs = new HashSet<URI>();
        for (IComponentInstance compInstance : model.getComponentInstances()) {
            URI absoluteURI;
            try {
                absoluteURI = ResourceRegistry.getInstance()
                        .toAbsolute(bundleManager.getJarNameFromComponentTypeId(compInstance.getComponentTypeID()));
                modelComponentJarURIs.add(absoluteURI);
            } catch (BundleManagementException e) {
                Notifier.warning("Ignoring componentType: " + compInstance.getInstanceID() + " ("
                        + compInstance.getComponentTypeID() + "), model: " + model.getModelName(), e);
            }
        }
        // System.out.println("Model: "+model.getModelName()+",
        // comoponentTypeJarURIs:\n"+modelComponentJarURIs);
        return modelComponentJarURIs;
    }

    /**
     * Returns a set of IRuntimeModel instances for the given set of model URIs.
     * 
     * @param modelURIs
     * @return
     */
    public Set<IRuntimeModel> getIRuntimeModelsOfModelURIs(Set<URI> modelURIs) {
        Set<IRuntimeModel> modelInstances = new HashSet<IRuntimeModel>();
        for (URI modelURI : modelURIs) {
            try (InputStream iStr = modelURI.toURL().openStream();) {
                IRuntimeModel model = parseModel(iStr);
                // The default implementation of IRuntimeModel is
                // DefaultRuntimeModel which does not have a correct
                // equals/hashCode-contract, the same for IComponentInstance and
                // others.
                // This means that the Set can't have unique model instances,
                // which is not a problem because this just means that files are
                // maybe just copied more than once.
                modelInstances.add(model);
            } catch (Exception e) {
                // Catch exceptions and ignore URI, also log the problem.
                Notifier.warning("Ignoring model URI: " + modelURI, e);
            }
        }
        return modelInstances;
    }

    /**
     * Returns a set of IComponentInstances for the given set of model URIs.
     * 
     * @param modelURIs
     * @return
     */
    public Set<IComponentInstance> getIComponentInstancesOfModelURIs(Set<URI> modelURIs) {
        Set<IComponentInstance> componentInstances = new HashSet<IComponentInstance>();
        for (URI modelURI : modelURIs) {
            try (InputStream iStr = modelURI.toURL().openStream();) {
                IRuntimeModel model = parseModel(iStr);

                // The default implementation of IRuntimeModel is
                // DefaultRuntimeModel which does not have a correct
                // equals/hashCode-contract, the same for IComponentInstance and
                // others.
                // This means that the Set can't have unique model instances,
                // which is not a problem because this just means that files are
                // maybe just copied more than once.
                componentInstances.addAll(model.getComponentInstances());
            } catch (Exception e) {
                // Catch exceptions and ignore URI, also log the problem.
                Notifier.warning("Ignoring model URI: " + modelURI, e);
            }
        }
        return componentInstances;
    }

    /**
     * Returns a set of IComponentInstances for the given set of IRuntimeModel
     * instances.
     * 
     * @param modelInstances
     * @return
     */
    public Set<IComponentInstance> getIComponentInstancesOfIRuntimeModels(Set<IRuntimeModel> modelInstances) {
        Set<IComponentInstance> componentInstances = new HashSet<IComponentInstance>();
        for (IRuntimeModel model : modelInstances) {
            // The default implementation of IRuntimeModel is
            // DefaultRuntimeModel which does not have a correct
            // equals/hashCode-contract, the same for IComponentInstance and
            // others.
            // This means that the Set can't have unique model instances, which
            // is not a problem because this just means that files are maybe
            // just copied more than once.
            componentInstances.addAll(model.getComponentInstances());
        }
        return componentInstances;
    }

    /**
     * Returns a set of license URIs for the given set of model instances.
     * Currently this method only returns license URIs directly for the involved
     * componentTypes not considering services or the middleware.
     * 
     * @param modelInstances
     * @return
     */
    public Set<URI> getLicenseURIsOfModels(Set<IRuntimeModel> modelInstances) {
        Set<URI> licenseURIs = new HashSet<URI>();
        Set<IComponentInstance> componentInstances = getIComponentInstancesOfIRuntimeModels(modelInstances);
        for (final IComponentInstance componentInstance : componentInstances) {
            try {
                IComponentType compTypeInst = ComponentRepository.instance
                        .getComponentType(componentInstance.getComponentTypeID());
                Notifier.debug("compTypeId: " + compTypeInst.getID() + ", subtype: " + compTypeInst.getType(), null);
                String compTypeId = componentInstance.getComponentTypeID();
                String[] compTypeElems = compTypeId.split("\\.");
                final String compTypePrefixForLicense = compTypeInst.getType() + "." + compTypeElems[1];

                List<URI> compLicenseURIs = ResourceRegistry.getInstance().getLicensesList(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        String[] compTypePrefix = name.split("-");
                        Notifier.debug("compTypePrefix: " + compTypePrefix[0] + ", compType: "
                                + componentInstance.getComponentTypeID(), null);
                        return compTypePrefix[0].equalsIgnoreCase(compTypePrefixForLicense) && name.endsWith(".txt");
                    }

                }, false);
                Notifier.debug(
                        "compType: " + componentInstance.getComponentTypeID() + ", compLicensURIs: " + compLicenseURIs,
                        null);
                licenseURIs.addAll(compLicenseURIs);
            } catch (BundleManagementException e) {
                Notifier.warning("Could not determine componentType/SubType of componentInstance with Id: "
                        + componentInstance.getComponentTypeID(), e);
            }
        }
        return licenseURIs;
    }

    /**
     * Returns a Set of merged .jar URIs corresponding to the existing
     * componentTypes in the given Set of IRuntimeModel model URIs.
     * 
     * @param modelURIs
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * @throws ParseException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     * @throws BundleManagementException
     */
    public Set<URI> getComponentTypeJarURIsOfModelURIs(Set<URI> modelURIs)
            throws MalformedURLException, IOException, ParseException, ParserConfigurationException, SAXException,
            TransformerException, BundleManagementException {
        Set<URI> modelComponentJarURIs = new HashSet<URI>();
        for (URI modelURI : modelURIs) {
            try(InputStream iStr = modelURI.toURL().openStream();) {
                IRuntimeModel model = parseModel(iStr);
                modelComponentJarURIs.addAll(getComponentTypeJarURIsOfModel(model));
            }
        }
        return modelComponentJarURIs;
    }

    /**
     * Returns a Set of merged .jar URIs corresponding to the existing
     * componentTypes in the given Set of IRuntimeModel models.
     * 
     * @param modelInstances
     * @return
     */
    public Set<URI> getComponentTypeJarURIsOfModels(Set<IRuntimeModel> modelInstances) {
        Set<URI> modelComponentJarURIs = new HashSet<URI>();
        for (IRuntimeModel model : modelInstances) {
            modelComponentJarURIs.addAll(getComponentTypeJarURIsOfModel(model));
        }
        return modelComponentJarURIs;
    }

    /**
     * Returns a Set of URIs to model files by analyzing the APE.model property
     * value.
     * 
     * @return
     */
    public Set<URI> getModelURIsFromProperty() {
        Set<URI> modelURIs = new HashSet<URI>();
        String modelsPropVals = apeProperties.getProperty(APEProperties.P_APE_MODELS);
        String projectDirPath = apeProperties.getProperty(APEProperties.P_APE_PROJECT_DIR);
        for (String modelsPropVal : modelsPropVals.split(MODELS_PROP_SEPERATOR)) {
            // do sanity check: ignore leading and trailing whitespace and empty
            // strings
            modelsPropVal = modelsPropVal.trim();
            if ("".equals(modelsPropVal)) {
                continue;
            }

            File testFile = ResourceRegistry.getInstance().resolveRelativeFilePath(new File(projectDirPath), modelsPropVal);
            URI testURI = testFile.toURI();

            if (!testFile.exists()) {
                Notifier.warning("Ignoring URI: " + testFile, null);
                continue;
            }

            List<URI> URIs = new ArrayList();
            if (testFile.isDirectory()) {
                URIs = ResourceRegistry.getInstance().getModelList(testURI, false);
            } else {
                URIs.add(testURI);
            }
            modelURIs.addAll(URIs);
        }

        return modelURIs;
    }

    /**
     * This method checks the values of all component properties found in the
     * given set IRuntimeModel instances. Ths values are tested as resource keys
     * for
     * {@link ResourceRegistry#getResource(String, RES_TYPE, String, String)}
     * and tested for existence. If a value exists it is added to the list of
     * URIs returned.
     * 
     * @param modelInstances
     * @return
     */
    public Collection<URI> getPropertyReferredURIs(Set<IRuntimeModel> modelInstances) {
        // only collect unique URIs
        Collection<URI> dataURIs = new HashSet<URI>();

        for (IRuntimeModel model : modelInstances) {
            // The default implementation of IRuntimeModel is
            // DefaultRuntimeModel which does not have a correct
            // equals/hashCode-contract, the same for IComponentInstance and
            // others.
            // This means that the Set can't have unique model instances, which
            // is not a problem because this just means that files are maybe
            // just copied more than once.
            for (IComponentInstance componentInstance : model.getComponentInstances()) {
                for (Map.Entry<String, Object> property : componentInstance.getPropertyValues().entrySet()) {
                    Notifier.debug("Evaluating property: " + property.getKey() + "=" + property.getValue(), null);
                    URI propValURI = null;

                    try {
                        String propVal = excludeNonURIValues(property);

                        for (RES_TYPE resType : CHECK_RES_TYPE_ORDER) {
                            try {

                                propValURI = ResourceRegistry.getInstance().getResource(propVal, resType,
                                        componentInstance.getComponentTypeID(), null);

                                // Skip URI if it equals AREBaseURI or is not a
                                // sub URI of ARE base URI
                                if (ResourceRegistry.getInstance().equalsAREBaseURI(propValURI)) {
                                    Notifier.warning(
                                            "Skipping property URI, because equals to ARE.baseURI. URI: " + propValURI,
                                            null);
                                    break;
                                }

                                if (!ResourceRegistry.getInstance().isSubURIOfAREBaseURI((propValURI))) {
                                    Notifier.warning(
                                            "Skipping property URI, because not contained in ARE.baseURI. Please copy URI manually if needed. URI: "
                                                    + propValURI,
                                            null);
                                    break;
                                }

                                // if URI is not a file or does not exist
                                // We could also consider trying to open an
                                // InputStream, then it would work generically
                                // for all types of URIs,
                                // also URLs, but actually we only wanna copy
                                // local files.
                                File propValFile = ResourceRegistry.getInstance().toFile(propValURI);

                                if (propValFile.exists()) {
                                    // Ok, got it, File exists so we can copy it
                                    Notifier.debug("Selecting resource of property for copying: " + property.getKey()
                                            + ", URI: " + propValURI, null);
                                    if (resType.equals(RES_TYPE.MODEL) && propValFile.getName().endsWith(".acs")) {
                                        Notifier.warning("The model <" + model.getModelName()
                                                + "> refers to another model at " + componentInstance.getInstanceID()
                                                + "." + property.getKey() + " - Consider adding the model path to "
                                                + APEProperties.P_APE_MODELS + ", URI: " + propValURI, null);
                                    }
                                    dataURIs.add(propValURI);
                                    break;
                                }
                            } catch (Exception e) {
                                Notifier.debug("Ignoring value of property " + property.getKey() + ", message: "
                                        + e.getMessage(), e);
                            }

                        }
                    } catch (URISyntaxException e) {
                        Notifier.debug(
                                "Ignoring value of property " + property.getKey() + ", message: " + e.getMessage(),
                                null);
                    }
                }
            }
        }

        return dataURIs;
    }

    /**
     * Internal method to do sanity checks with component property values.
     * 
     * @param property
     * @return
     * @throws URISyntaxException
     */
    private String excludeNonURIValues(Map.Entry<String, Object> property) throws URISyntaxException {
        Object propValObj = property.getValue();
        if (propValObj == null) {
            throw new URISyntaxException(property.toString(), "Value of property is null");
        }
        String propVal = propValObj.toString();
        if (propVal.equals("")) {
            throw new URISyntaxException(property.toString(), "Value of property is empty");
        }
        if (propVal.startsWith("@") && propVal.indexOf(":") > -1) {
            throw new URISyntaxException(property.toString(),
                    "Value of property most likely an AsTeRICS action command");
        }
        if (isNumber(propVal)) {
            throw new URISyntaxException(property.toString(), "Value of property most likely a numeric property value");
        }

        return propVal;
    }

    /**
     * Checks whether the given String contains a number.
     * 
     * @param propVal
     * @return
     */
    private boolean isNumber(String propVal) {
        try {
            Long.parseLong(propVal);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }
}
