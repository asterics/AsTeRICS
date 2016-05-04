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
 *    This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         License: GPL v3.0
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.processor.mathevaluator;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

import org.cheffo.jeplite.*;

import java.util.*;

/**
 * MathEvaluatorInstance incorporates a component which uses an mathematical 
 * expression parser which allows to specify the combination of the inputs in
 * any way that is understood by the JEPlite library. This makes more 
 * complicated manipulations of the signal easier.
 * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at]
 *         Date: Nov 3, 2010
 *         Time: 02:22:08 PM
 */
public class MathEvaluatorInstance extends AbstractRuntimeComponentInstance
{
	// Constants
	final String PROPERTY_EXPRESSION_KEY = "expression";

	final int NUMBER_OF_INPUTS = 4;
	
	// Properties
	String propExpression = "a";

	// Internals
	JEP jep = new JEP();

	// Inputs
    private IRuntimeInputPort inputPortA = new InputPort("a", this);
    private IRuntimeInputPort inputPortB = new InputPort("b", this);
    private IRuntimeInputPort inputPortC = new InputPort("c", this);
    private IRuntimeInputPort inputPortD = new InputPort("d", this);

    // Outputs
    private IRuntimeOutputPort outputPort1 = new DefaultRuntimeOutputPort();
	
	/**
	 * Constructs the component, sets up JEP and the calculation decision
	 */
    public MathEvaluatorInstance()
    {
    	
        jep.addStandardConstants();
        jep.addStandardFunctions();
        jep.addVariable("a", 0);
        jep.addVariable("b", 0);
        jep.addVariable("c", 0);
        jep.addVariable("d", 0);
       	jep.parseExpression(propExpression);
    }

    /**
     * Starts the component
     */
    public void start()
    {
        super.start();
    }

    /**
     * Pauses the component
     */
    public void pause()
    {
        super.pause();
    }

    /**
     * Resumes the component
     */
    public void resume()
    {
        super.resume();
    }

    /**
     * Stops the component
     */
    public void stop()
    {
        super.stop();
    }
    
    /**
     * Returns a specified  input port instance
     * @param portID the requested input port ID
     * @return the port instance, null if non existant
     */
    public IRuntimeInputPort getInputPort(String portID)
    {
        if("inA".equalsIgnoreCase(portID))
        {
            return inputPortA;
        }
        else if("inB".equalsIgnoreCase(portID))
        {
            return inputPortB;
        }
        else if("inC".equalsIgnoreCase(portID))
        {
            return inputPortC;
        }
        else if("inD".equalsIgnoreCase(portID))
        {
            return inputPortD;
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns a specified  output port instance
     * @param portID the requested output port ID
     * @return the port instance, null if non existant
     */
    public IRuntimeOutputPort getOutputPort(String portID)
    {
        if("out".equalsIgnoreCase(portID))
        {
            return outputPort1;
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the value of a specified component property
     * @param propertyName the name of the requested property
     * @return the value of the property as an Object
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
        if(PROPERTY_EXPRESSION_KEY.equalsIgnoreCase(propertyName))
        {
            return propExpression;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets a new value for a specifed property
     * @param propertyName the name of the requested property
     * @param newValue the new value for the property
     * @return the old value of the property as an Object
    */
    synchronized public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
        if(PROPERTY_EXPRESSION_KEY.equalsIgnoreCase(propertyName))
        {
        	final Object oldValue = propExpression;
            
            propExpression = (String) newValue;
            // AstericsErrorHandling.instance.reportInfo(this, "Set setting expression property to: " + propExpression);
           	jep.parseExpression(propExpression);
           	
            return oldValue;
        }
         return null;
    }
    
    /**
     * Sets a variable for an input of the component 
     * @param varName the variable name ('a' to 'd')
     * @param value the new value for this variable
     */
    synchronized void setVariable(String varName, double value)
    {
    	jep.addVariable(varName, value);
    }
    
    /**
     * Performs the calculations according to the mathematical expression. Will
     * only trigger the output if all necessary inputs have received data.
     * @param sendAlways always triggers a calculation on received data on any
     * input
     */
    synchronized void calculateAndProcessOutput() 
    {
    	double value = 0;
        try
        {
        	value = jep.getValue();
        }
        catch (ParseException e)
        {
        	AstericsErrorHandling.instance.reportInfo(this, "Exception while evaluating output of expression parser");
        	e.printStackTrace();
        }
        
        outputPort1.sendData(ConversionUtils.doubleToBytes(value));
    }

    /**
     * An InputPort implementation which sets its corresponding variable in the
     * evaluator
     * @author weissch
     *
     */
    private class InputPort extends DefaultRuntimeInputPort
    {
    	String varName; 
    	MathEvaluatorInstance owner;
    	
    	/**
    	 * Constructs the input port
    	 * @param varName name of corresponding variable
    	 * @param owner link to owning instance
    	 */
    	public InputPort(String varName, MathEvaluatorInstance owner)
    	{
    		super();
    		this.varName = varName;
    		this.owner = owner;
    	}
    	
    	/**
    	 * Called upon incoming data, will transfer data to evaluator
    	 */
        public void receiveData(byte[] data)
        {
        	double in = ConversionUtils.doubleFromBytes(data);
       
            // convert input to int
            owner.setVariable(varName, in);
            owner.calculateAndProcessOutput();
        }
    }

	@Override
	public void syncedValuesReceived(HashMap<String, byte[]> dataRow) {
	
		for (String s: dataRow.keySet())
		{
			
			byte [] data = dataRow.get(s);
			if (s.equals("inA"))
			{
				setVariable("a", ConversionUtils.doubleFromBytes(data));
			}
			if (s.equals("inB"))
			{
				setVariable("b", ConversionUtils.doubleFromBytes(data));
			}
			if (s.equals("inC"))
			{
				setVariable("c", ConversionUtils.doubleFromBytes(data));
			}
			if (s.equals("inD"))
			{
				setVariable("d", ConversionUtils.doubleFromBytes(data));
			}
		}
        calculateAndProcessOutput();
	}
}