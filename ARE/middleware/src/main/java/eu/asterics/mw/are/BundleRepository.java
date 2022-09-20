package eu.asterics.mw.are;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import eu.asterics.mw.are.exceptions.BundleManagementException;
import eu.asterics.mw.model.DataType;
import eu.asterics.mw.model.bundle.IComponentType;
import eu.asterics.mw.model.bundle.IOutputPortType;
import eu.asterics.mw.services.AstericsErrorHandling;

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
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 * @author Costas Kakousis [kakousis@cs.ucy.ac.cy] Date: Jul 14, 2010 Time:
 *         4:11:34 PM TODO delete this class (replaced by ComponentRepository)
 */
public class BundleRepository {
    public static final BundleRepository instance = new BundleRepository();
    private Logger logger = null;

    private BundleRepository() {
        super();
        logger = AstericsErrorHandling.instance.getLogger();
    }

    private final Map<String, IComponentType> repository = new HashMap<String, IComponentType>();

    public void install(final IComponentType componentType) throws BundleManagementException {
        if (componentType == null) {
            logger.severe(this.getClass().getName() + ": install-> Illegal null argument");
            throw new NullPointerException("Illegal null argument");
        }

        final String componentTypeID = componentType.getID();

        if (repository.containsKey(componentTypeID)) {
            logger.severe("The specified componentTypeID" + " is already installed in the repository.");
            throw new BundleManagementException(
                    "The specified componentTypeID" + " is already installed in the repository.");
        }

        repository.put(componentTypeID, componentType);
    }

    void uninstall(final IComponentType componentType) throws BundleManagementException {
        if (componentType == null) {

            logger.severe(this.getClass().getName() + ": install-> Illegal null argument");
            throw new NullPointerException("Illegal null argument");
        }

        uninstall(componentType.getID());
    }

    void uninstall(final String componentTypeID) throws BundleManagementException {
        if (!repository.containsKey(componentTypeID)) {
            logger.severe("The specified componentTypeID" + " is already installed in the repository.");
            throw new BundleManagementException(
                    "The specified componentTypeID: " + componentTypeID + " is not found in the repository.");
        }

        repository.remove(componentTypeID);
    }

    public Set<IComponentType> getInstalledComponentTypes() {
        final Set<IComponentType> installedComponentTypes = new LinkedHashSet<IComponentType>();

        installedComponentTypes.addAll(repository.values());

        return installedComponentTypes;
    }

    public IComponentType getComponentType(final String componentTypeID) {
        return repository.get(componentTypeID);
    }

    // todo remove this debug method
    public void printAll() {
        System.out.println("  Bundle repository:");
        for (final String key : repository.keySet()) {
            System.out.println("    " + key + "\t --> " + repository.get(key));
        }
    }

    /**
     * @deprecated
     * @param sourceComponentTypeID
     * @param sourcePortID
     * @return
     */
    @Deprecated
    public String getPortType(String sourceComponentTypeID, String sourcePortID) {
        Set<String> x = repository.keySet();
        Set<IOutputPortType> outPorts;
        IComponentType componentType;
        for (String ct : x) {
            if (ct.equals(sourceComponentTypeID)) {
                componentType = repository.get(ct);
                outPorts = componentType.getOutputPorts();
                for (IOutputPortType opt : outPorts) {
                    return opt.getDataType().toString();
                }
            }
        }
        return null;
    }

    public DataType getPortDataType(final String componentTypeID, final String portID) {
        final IComponentType componentType = repository.get(componentTypeID);
        return componentType == null ? DataType.UNKNOWN : componentType.getDataType(portID);
    }
}
