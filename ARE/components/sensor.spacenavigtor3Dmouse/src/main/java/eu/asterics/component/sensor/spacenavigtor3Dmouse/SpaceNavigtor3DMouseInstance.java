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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.sensor.spacenavigtor3Dmouse;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;


/**
 * Implements the Mouse 3D plugin.
 *    
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Feb 11, 2011
 *         Time: 4:27:47 PM
 */
public class SpaceNavigtor3DMouseInstance extends AbstractRuntimeComponentInstance
{
  private final String OP_MOUSE_X="mouseX";
  private final String OP_MOUSE_Y="mouseY";
  private final String OP_MOUSE_Z="mouseZ";
  private final String OP_MOUSE_RX="mouseRx";
  private final String OP_MOUSE_RY="mouseRy";
  private final String OP_MOUSE_RZ="mouseRz";
  private final String OP_BUTTONS="buttons";
  private final String PROP_INTERVAL="interval";
  
  private final OutputPort opMouseX = new OutputPort();
  private final OutputPort opMouseY = new OutputPort();
  private final OutputPort opMouseZ = new OutputPort();
  private final OutputPort opMouseRx = new OutputPort();
  private final OutputPort opMouseRy = new OutputPort();
  private final OutputPort opMouseRz = new OutputPort();
  private final OutputPort opButtons = new OutputPort();
  
  private final SpaceNavigtor3DMouseBridge bridge = new SpaceNavigtor3DMouseBridge(this, opMouseX, opMouseY, opMouseZ, opMouseRx,opMouseRy,opMouseRz,opButtons);

  /**
   * The class constructor.
   */
  public SpaceNavigtor3DMouseInstance()
  {
  }
  
  /**
   * Returns an Input Port.
   * @param portID   the name of the port
   * @return         the input port or null if not found
   */
  public IRuntimeInputPort getInputPort(String portID)
  {
    return null;
  }

  /**
   * Returns an Output Port.
   * @param portID   the name of the port
   * @return         the output port
   */ 
  public IRuntimeOutputPort getOutputPort(String portID)
  {
    if(OP_MOUSE_X.equalsIgnoreCase(portID))
    {
      return opMouseX;
    }
    else if(OP_MOUSE_Y.equalsIgnoreCase(portID))
    {
      return opMouseY;
    }
    else if(OP_MOUSE_Z.equalsIgnoreCase(portID))
    {
      return opMouseZ;
    }
    else if(OP_MOUSE_RX.equalsIgnoreCase(portID))
    {
      return opMouseRx;
    }
    else if(OP_MOUSE_RY.equalsIgnoreCase(portID))
    {
      return opMouseRy;
    }
    else if(OP_MOUSE_RZ.equalsIgnoreCase(portID))
    {
      return opMouseRz;
    }
    else if(OP_BUTTONS.equalsIgnoreCase(portID))
    {
      return opButtons;
    }

    return null;
  }

  /**
   * Returns the value of the given property.
   * @param propertyName   the name of the property
   * @return               the property value or null if not found
   */
  public Object getRuntimePropertyValue(String propertyName)
  {
    if(PROP_INTERVAL.equalsIgnoreCase(propertyName))
    {
      return bridge.getInterval();
    }

    return null;
  }

  /**
   * Sets a new value for the given property.
   * @param propertyName   the name of the property
   * @param newValue       the desired property value
   * @return old property  value
   */
  public Object setRuntimePropertyValue(String propertyName, Object newValue)
  {
    if(PROP_INTERVAL.equalsIgnoreCase(propertyName))
    {
      final Integer oldValue = bridge.getInterval();

      int interval = Integer.parseInt((String) newValue);
      
      bridge.setInterval(interval);
      
      return oldValue;
    }

    return null;  
  }

  /**
   * Called when model is started.
   */
  @Override
  public void start()
  {
    bridge.start();
    super.start();
  }

  /**
   * Called when model is paused.
   */
  @Override
  public void pause()
  {
    bridge.pause();
    super.pause();
  }

  /**
   * Called when model is resumed
   */
  @Override
  public void resume()
  {
    bridge.resume();
    super.resume();
  }

  /**
   * Called when model is stopped.
   */
  @Override
  public void stop()
  {
    bridge.stop();
    super.stop();
  }

  /**
   * Plugin output port class.
   */
  public class OutputPort extends DefaultRuntimeOutputPort
  {
    public void sendData(int data)
    {
      super.sendData(ConversionUtils.intToByteArray(data));
    }
  }
}