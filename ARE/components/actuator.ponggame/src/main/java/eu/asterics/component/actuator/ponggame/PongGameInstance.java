

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
 *       This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.actuator.ponggame;


import java.awt.Canvas;
import java.awt.Dimension;
import java.util.logging.Logger;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsThreadPool;

import javax.swing.JPanel;

import java.awt.Canvas;
import java.awt.Dimension;

//import eu.asterics.games.pong.AstericsPong; 

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author <your name> [<your email address>]
 *         Date: 
 *         Time: 
 */
public class PongGameInstance extends AbstractRuntimeComponentInstance
{
	
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 
	
	final IRuntimeEventTriggererPort etpGameOver = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpGoalPlayerOne = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpGoalPlayerTwo = new DefaultRuntimeEventTriggererPort();
	
	final IRuntimeOutputPort opBallX = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opBallY = new DefaultRuntimeOutputPort();

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

//	String propSoundFilePaddleTouch = "";
//	String propSoundFileBoundsTouch = "";
//	String propSoundFileGoal = "";
//	String propSoundFileEndGame = "";
//	int propGoalsToWin = 5;
//	double propEventsToCaloryMultiplier = 5.0;
//	double propSpeedStep = 0.2;
//	boolean propUseSpeed = true;
//	int propControlMode = PongGameProperties.CONTROLMODE_SPEED;
	
    
   /**
    * The class constructor.
    */
    public PongGameInstance()
    {
//    	LwjglApplicationConfiguration.disableAudio = true;
        // empty constructor
    }

   /** 
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("playerOnePos".equalsIgnoreCase(portID))
		{
			return ipPlayerOnePos;
		}
		if ("playerTwoPos".equalsIgnoreCase(portID))
		{
			return ipPlayerTwoPos;
		}
		if ("playerOneSpeed".equalsIgnoreCase(portID))
		{
			return ipPlayerOneSpeed;
		}
		if ("playerTwoSpeed".equalsIgnoreCase(portID))
		{
			return ipPlayerTwoSpeed;
		}

		return null;
	}

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{
		if ("ballX".equalsIgnoreCase(portID))
		{
			return opBallX;
		}
		if ("ballY".equalsIgnoreCase(portID))
		{
			return opBallY;
		}

		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
		if ("startGame".equalsIgnoreCase(eventPortID))
		{
			return elpStartGame; 
		}
		if ("playerOneToggleDirection".equalsIgnoreCase(eventPortID))
		{
			return elpPlayerOneToggleDirection;
		}
		if ("playerTwoToggleDirection".equalsIgnoreCase(eventPortID))
		{
			return elpPlayerTwoToggleDirection;
		}
		if ("playerTwoMovement".equalsIgnoreCase(eventPortID))
		{
			return elpPlayerTwoMovement;
		}
		if ("playerOneMovement".equalsIgnoreCase(eventPortID))
		{
			return elpPlayerOneMovement;
		}

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
		if ("gameOver".equalsIgnoreCase(eventPortID))
		{
			return etpGameOver;
		}
		if ("goalPlayerOne".equalsIgnoreCase(eventPortID))
		{
			return etpGoalPlayerOne;
		}
		if ("goalPlayerTwo".equalsIgnoreCase(eventPortID))
		{
			return etpGoalPlayerTwo;
		}

        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if ("controlMode".equalsIgnoreCase(propertyName))
		{
			return PongGameProperties.controlMode;
		}
		if ("speedStep".equalsIgnoreCase(propertyName))
		{
			return PongGameProperties.speedStep ;
		}
		if ("soundFilePaddleTouch".equalsIgnoreCase(propertyName))
		{
			return PongGameProperties.soundFilePaddleTouch;
		}
		if ("soundFileBoundsTouch".equalsIgnoreCase(propertyName))
		{
			return PongGameProperties.soundFileWallTouch;
		}
		if ("soundFileGoal".equalsIgnoreCase(propertyName))
		{
			return PongGameProperties.soundFileGoal;
		}
		if ("soundFileEndGame".equalsIgnoreCase(propertyName))
		{
			return PongGameProperties.soundFileEndGame;
		}
		if ("goalsToWin".equalsIgnoreCase(propertyName))
		{
			return PongGameProperties.goalsToWin;
		}
		if ("eventsToCaloryMultiplier".equalsIgnoreCase(propertyName))
		{
			return PongGameProperties.eventsToCaloryMultiplier;
		}
		if ("goalScoreBase".equalsIgnoreCase(propertyName))
		{
			return PongGameProperties.propGoalScoreBase;
		}
		if ("goalTouchBase".equalsIgnoreCase(propertyName))
		{
			return PongGameProperties.propGoalTouchBase;
		}
		if ("resetWaitTime".equalsIgnoreCase(propertyName))
		{
			return PongGameProperties.propResetWaitTime;
		}
		if ("maxSpeed".equalsIgnoreCase(propertyName))
		{
			return PongGameProperties.propMaxSpeed;
		}
		if ("minXSpeed".equalsIgnoreCase(propertyName))
		{
			return PongGameProperties.propMinXSpeed;
		}
		if ("reflectionYImpulse".equalsIgnoreCase(propertyName))
		{
			return PongGameProperties.propReflectionYImpulse;
		}
        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
		if ("controlMode".equalsIgnoreCase(propertyName))
		{
			final int oldValue = PongGameProperties.controlMode;
			PongGameProperties.controlMode = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("speedStep".equalsIgnoreCase(propertyName))
		{
			final double oldValue = PongGameProperties.speedStep;
			PongGameProperties.speedStep = Double.parseDouble((String)newValue);
			return oldValue;
		}

		if ("soundFilePaddleTouch".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = PongGameProperties.soundFilePaddleTouch;
			PongGameProperties.soundFilePaddleTouch = (String)newValue; 
			return oldValue;
		}
		if ("soundFileBoundsTouch".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = PongGameProperties.soundFileWallTouch;
			PongGameProperties.soundFileWallTouch = (String)newValue;
			return oldValue;
		}
		if ("soundFileGoal".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = PongGameProperties.soundFileGoal;
			PongGameProperties.soundFileGoal = (String)newValue;
			return oldValue;
		}
		if ("soundFileEndGame".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = PongGameProperties.soundFileEndGame;
			PongGameProperties.soundFileEndGame = (String)newValue;;
			return oldValue;
		}
		if ("goalsToWin".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = PongGameProperties.goalsToWin;
			PongGameProperties.goalsToWin = Integer.parseInt(newValue.toString()); 
			return oldValue;
		}
		if ("eventsToCaloryMultiplier".equalsIgnoreCase(propertyName))
		{
			final double oldValue = PongGameProperties.eventsToCaloryMultiplier;
			PongGameProperties.eventsToCaloryMultiplier = Double.parseDouble((String)newValue);
			return oldValue;
		}

		if ("goalScoreBase".equalsIgnoreCase(propertyName))
		{
			final int oldValue = PongGameProperties.propGoalScoreBase;
			PongGameProperties.propGoalScoreBase = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("goalTouchBase".equalsIgnoreCase(propertyName))
		{
			final int oldValue = PongGameProperties.propGoalTouchBase;
			PongGameProperties.propGoalTouchBase = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("resetWaitTime".equalsIgnoreCase(propertyName))
		{
			final int oldValue = (int) PongGameProperties.propResetWaitTime;
			PongGameProperties.propResetWaitTime = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("maxSpeed".equalsIgnoreCase(propertyName))
		{
			final double oldValue = PongGameProperties.propMaxSpeed;
			PongGameProperties.propMaxSpeed = (float) Double.parseDouble(newValue.toString());
			return oldValue;
		}
		if ("minXSpeed".equalsIgnoreCase(propertyName))
		{
			final double oldValue = PongGameProperties.propMinXSpeed;
			PongGameProperties.propMinXSpeed = (float) Double.parseDouble(newValue.toString());
			return oldValue;
		}
		if ("reflectionYImpulse".equalsIgnoreCase(propertyName))
		{
			final double oldValue = PongGameProperties.propReflectionYImpulse;
			PongGameProperties.propReflectionYImpulse = (float) Double.parseDouble(newValue.toString());
			return oldValue;
		}
		return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipPlayerOnePos  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			AstericsPong.instance.playerPosInput(0, ConversionUtils.intFromBytes(data));
		}
	};
	private final IRuntimeInputPort ipPlayerTwoPos  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			AstericsPong.instance.playerPosInput(1, ConversionUtils.intFromBytes(data));
		}
	};
	private final IRuntimeInputPort ipPlayerOneSpeed  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			AstericsPong.instance.playerSpeedInput(0, ConversionUtils.intFromBytes(data));
		}
	};
	private final IRuntimeInputPort ipPlayerTwoSpeed  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			AstericsPong.instance.playerSpeedInput(1, ConversionUtils.intFromBytes(data));
		}
	};


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpStartGame = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			AstericsPong.instance.auxiliaryButtonInput(); 
		}
	};
	final IRuntimeEventListenerPort elpPlayerOneToggleDirection = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			AstericsPong.instance.playerDirectionToggle(0); 
		}
	};
	final IRuntimeEventListenerPort elpPlayerTwoToggleDirection = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			AstericsPong.instance.playerDirectionToggle(1); 
		}
	};
	final IRuntimeEventListenerPort elpPlayerTwoMovement = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data) 
		{
			AstericsPong.instance.playerMovementInput(1);
		}
	};
	final IRuntimeEventListenerPort elpPlayerOneMovement = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			AstericsPong.instance.playerMovementInput(0); 
		}
	};

	LwjglAWTCanvas lcnv;
	JPanel pnl;

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
/*    	  
    	  AstericsThreadPool.instance.execute(
    			  new Runnable()
    			  {
    				  public void run()
    				  {
    						LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
    						cfg.title = "asterics-pong";
    						cfg.useGL20 = false;
    						cfg.width = 980;
    						cfg.height = 320;
    						
    						
    						new LwjglApplication(new AstericsPong(), cfg);
    				  }
    			  }
    	  );
    	  */
          Dimension dim = AREServices.instance.getAvailableSpace(this);
          pnl = new JPanel();

          //LwjglApplicationConfiguration.disableAudio = true;
          
          AstericsPong.reset(dim, opBallX, opBallY);
          AstericsPong.instance.setPongGameInstane(this);
          lcnv= new LwjglAWTCanvas(AstericsPong.instance, false); 
          Canvas cnvs = lcnv.getCanvas();
          cnvs.setPreferredSize(dim);
          pnl.setPreferredSize(dim);
          pnl.add(cnvs);
   	  
          AREServices.instance.displayPanel(pnl, this, true);
          super.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
          super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
          super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
		  AREServices.instance.displayPanel(pnl, this, false);
		  AstericsPong.instance.stopGame();
		  lcnv.stop();
          super.stop();
      }
}