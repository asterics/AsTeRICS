

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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.processor.hrvanalysis;


import java.awt.List;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;
import java.util.ArrayList;

import java.text.DecimalFormat;

import eu.asterics.mw.cimcommunication.CIMPortManager;
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


//,programm uebersichtlicher gestalten

//   ausreisser  als property

// lebensfeuer in arbeit erwaehnen


public class HRVAnalysisInstance extends AbstractRuntimeComponentInstance
{	
	final IRuntimeOutputPort opRuntime = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opPulserate = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opSDNN = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opRMSSD = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opSDSD = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opPNN50 = new DefaultRuntimeOutputPort();	
	final IRuntimeOutputPort opPNN20 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opDD = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	
	public static final int LEERLAUFPHASE = 0;
	public static final int INITPHASE = 1;
	public static final int AUSWAHL= 2;
	public static final int BERG = 3;
	public static final int TAL= 4;
	public static final int BERECHNUNG_POS = 5;
	public static final int BERECHNUNG_NEG = 6;
	
	double propSamplerate = 512;
	double propOutlierRange = 1.5;
	
	double runtime = 0;
	double runtime_i = 0;
	double maxvalue_init = 0;
	double maxvalue_border = 0;
	double minvalue_init = 0;
	double minvalue_border = 0;
	double middle = 0;
	double data_in = 0;
	double peak_mean_pos = 0;
	double peak_mean_neg = 0;
	double RR_Int_Sum = 0;
	double RR_Int_Sum2_RMSSD = 0;
	double RR_Int_Sum2_SDNN = 0;
	double RR_Int_Pulse_Sum = 0;
	double RR_Int_Mean_Sum = 0;
	double DD_mean_sum = 0;
	double DD_mean = 0;
	double DD_Sum2_SDSD = 0;
	double Heartbeat_duration_sum = 0;
	double RR_Int_Mean = 0;
	double peak_pos = 0;
	double peak_neg = 0;
	double SDNN = 0;
	double SDSD = 0;
	double RMSSD = 0;
	double NN50 = 0;
	double PNN50 = 0;
	double NN20 = 0;
	double PNN20 = 0;
	double DD = 0;
	
//	double in_data_array 
	
	
	
	ArrayList<Double> data_peaks_pos = new ArrayList<Double>();
	ArrayList<Double> data_peaks_neg = new ArrayList<Double>();	
	ArrayList<Double> RR_Zeiten = new ArrayList<Double>();
	ArrayList<Double> RR_Intervall = new ArrayList<Double>();
	ArrayList<Double> Heartbeat_duration = new ArrayList<Double>();
	ArrayList<Double> DD_array = new ArrayList<Double>();
	double heartbeat_duration = 0;
	double Heartbeat_duration_mean = 0;
	int fl_twist_data_in = 0;
	int fl_initphase = 1;
	int fl_cap_pos = 0;
	int fl_cap_neg = 0;
	int fl_mvb_beginn = 0;
	int fl_mvb_end = 0;
	int RR_Counter = 0;
	int state = LEERLAUFPHASE;
	int i = 0;
	int j = 0;
	double l = 0;
	int RR_Zeiten_size = 0;
	double acttime = 0;
	double leerlauf_timer = 0;
	double init_timer = 0;
	double peak_time_neg  = 0;
	double peak_time_pos = 0;
	double RR_Time1 = 0;
	double RR_Time2 = 0;
	int leerlaufphase = 1;
	int initphase = 0;
	
	double time_mvb_beginn = 0;
	double time_mvb_end = 0;
	double Pulsrate_60sek = 0;
	double Pulsrate = 0;
	
	DecimalFormat f = new DecimalFormat("#0.000");
	// declare member variables here

  
    
   /**
    * The class constructor.
    */
    public HRVAnalysisInstance()
    {
        // empty constructor
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("hRVInput".equalsIgnoreCase(portID))
		{
			return ipHRVInput;
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
    	if ("runtime".equalsIgnoreCase(portID))
		{
			return opRuntime;
		}
    	
    	
    	if ("pulserate".equalsIgnoreCase(portID))
		{
			return opPulserate;
		}
		if ("sDNN".equalsIgnoreCase(portID))
		{
			return opSDNN;
		}
		if ("rMSSD".equalsIgnoreCase(portID))
		{
			return opRMSSD;
		}
		
		if ("SDSD".equalsIgnoreCase(portID))
		{
			return opSDSD;
		}
		
		if ("pNN50".equalsIgnoreCase(portID))
		{
			return opPNN50;
		}
		
		if ("pNN20".equalsIgnoreCase(portID))
		{
			return opPNN20;
		}
		
		if ("dD".equalsIgnoreCase(portID))
		{
			return opDD;
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
    	if ("start".equalsIgnoreCase(eventPortID))
		{
			return elpStart;
		}

        return null;
    }
	public class OutputPort extends DefaultRuntimeOutputPort
   	{
   		/**
   		 * Sends data to the connected input port 
   		 * @param data a double value to be sent
   		 * 
   		 */
   		public void sendData(double data)
   		{	
   			
   			System.out.println("in SendData vor SendData  :" +data);
   			//TODO change this to a more useful conversion
   			super.sendData(ConversionUtils.doubleToBytes(data));
   			
   			System.out.println("in SendData nach dem senden SendData  :" +data);

   			
   		}
   	}
    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {

        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
    	if("samplerate".equalsIgnoreCase(propertyName))
    	{
        return propSamplerate;
    	}
    	
    	if("outlierrange".equalsIgnoreCase(propertyName))
    	{
        return propOutlierRange;
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

    	if("sameplerate".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propSamplerate;

            propSamplerate=Double.parseDouble(newValue.toString());
            return oldValue;
        }
    	
     	if("outlierrange".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propOutlierRange;

            propOutlierRange=Double.parseDouble(newValue.toString());
            return oldValue;
        }
        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipHRVInput  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			
			data_in = ByteBuffer.wrap(data).getDouble();

			acttime += 1000/propSamplerate;
			calc_Runtime();

			
			if (fl_twist_data_in == 1)
				data_in = data_in*-1;
			
			int fl_zero_output= 0;
			switch(state)
			{
	    	  

		    	  
	      case LEERLAUFPHASE: // Leerlaufphase = 5sek zum richten vom sensor
	    	  
	      
	    	  	if (leerlaufphase == 1)	    	  
		      { 
		    	leerlauf_timer = acttime+5000;//*5.4;
		    	leerlaufphase = 0;
		    	System.out.println("Leerlaufphase Anfang: " + acttime + "Systemzeit: " +System.currentTimeMillis());
		      }  		 		
	    	  	
	    	  if ( acttime <= leerlauf_timer) 	  
	    	  {  	
	    	  break;
	    	  }  
	    	  
	    	  
	    	  else 
	    	   {
	    		
	    		state = INITPHASE;
	    	    System.out.println("Leerlaufphase Ende:" + acttime+ "Systemzeit: " +System.currentTimeMillis());
	    	    init_timer = acttime+5000;//*4.92;
	    	    System.out.println("Initphase Anfang: "+ acttime+ "Systemzeit: " +System.currentTimeMillis());
	    	    
	    	    
	    	   } 
	    	  
	    	  
	      case INITPHASE: // analysephase 5sek zum suchen der hoechsten Peaks und des Grenzwertes
	    	  
	    	  if (acttime <= init_timer) // Liste wird solange gefuellt wie analysephase lauuft
	    	  	{ 
	    		  if ( data_in > maxvalue_init)
	    		  { 
	    			  maxvalue_init = data_in;
	    			  break;
	    			
	    		  }
	    		  if (data_in <  minvalue_init)
	    	  		{
	    	  		 minvalue_init = data_in;
	    	  		break;
	    	  		}
	    	  	}
    		  else 
    		  {
    			  
    			 check_if_reversed();   			
    			 calc_middle_for_init();
    			 calc_maxvalue_border();

        	      state = AUSWAHL; 
    		  	  System.out.println("Initphase Ende"+ acttime+ "Systemzeit: " +System.currentTimeMillis());	  

    		 }
    			
    						
    				
    					
    			      
		  	  
	    	  
	    	  break;
	    	  
	      case AUSWAHL: // Erkennen von Bergen bzw. Ausgabe von message
	    	 
	    	  //System.out.println("Case Auswahl");
	    	 if (data_in >= middle) 
	    	 {
	    		 if (data_in >= maxvalue_border) // Berg erkannt
	    		 	{ 
	    			 System.out.println("Berg erkannt");
	    			 fl_cap_pos = 0;
	    			 state = BERG;         			 
	    		 	}  
	    		 else
		    		  break;
	    	 }
	    	 if (data_in < middle) 
	    	 {	if (data_in <= minvalue_border)
	    	 		{
	    	 		System.out.println("Tal erkannt");
	    	 		fl_cap_neg = 0;
	    	 		state = TAL;    		  
	    	 		}
	    	 	else
	    		  break;
	    	 }
	    	  break;
	    	  
	      case BERG: // Berg
	    	  System.out.println("Case BERG :" + data_in);
	    	  
	    	  if (data_in >= maxvalue_border) // Berg erkannt
	    	  {  if (fl_mvb_beginn == 0)
	    		  {time_mvb_beginn = acttime;
	    	  	  fl_mvb_beginn = 1;
	    		  }

	    		  if (data_in > peak_pos)
	    		  {   
	    			  
	    			  peak_pos = data_in;
	    		     peak_time_pos = acttime;
	    		     
	    		     break;
	    		  
	    		  }
	    	  }
	    	  if (data_in <  maxvalue_border) 
	 		   { 	
	    		  
	    		  
	    		  
	    		  if (fl_mvb_end == 0)
	    		  {
	    			  time_mvb_end = acttime;
	    			  fl_mvb_end = 1;
	    		  }
	    		  heartbeat_duration = time_mvb_end - time_mvb_beginn;
	    		  
	 		   if (Heartbeat_duration.size() > 4)
	 		   {
	 			  if ((heartbeat_duration > Heartbeat_duration_mean*1.5) || (heartbeat_duration < Heartbeat_duration_mean*0.5))
	 			  {	 System.out.println("Heartbeat_durationn zu klein/gross" );
	 				 fl_mvb_end = 0;
	 				 fl_mvb_beginn = 0;
	 			  	 break;
	 			  }
	 			  	 
	 		   }
	 		   
     
	 		time_mvb_end = 0;
	 		time_mvb_beginn = 0;
	 		fl_mvb_beginn = 0;
	 		fl_mvb_end = 0;
	 		
	 		
	    			 if (fl_cap_pos == 0) 
	    			 {  
	    				 
	    				 if(RR_Zeiten.size() > 0)
	    				 { 
	    					 if (data_peaks_pos.size() >= 5)
	    					 {
	    						 if (peak_pos >= peak_mean_pos*propOutlierRange) //
		    				 		{	System.out.println("Ausreisser erkannt" );
		    				 			break;
		    				 		}
	    					    	    						 
	    					 }
	    					 
	    					 
	    					 if (peak_time_pos - RR_Zeiten.get(RR_Zeiten.size()-1) <150) // werte bis 200ms sind moeglich
	    					 {	 System.out.println("Intervall zu klein unter 150ms" );
		    				     break;
	    					 }
	    					 
	    					     					 
	    				 }
	    				 
	    				Heartbeat_duration.add(heartbeat_duration);
	    		 		System.out.println("Heartbeat_durationn :" + Heartbeat_duration.get(Heartbeat_duration.size()-1)); 
	    				fl_cap_pos = 1; 			
 			 			System.out.println("                                                              Pos Spitze gefunden :" +peak_pos);
 			 			System.out.println("                                                              Zeit von pos Spitze :"+f.format(peak_time_pos));
 			 			RR_Zeiten.add(peak_time_pos);
 			 	
 			 			data_peaks_pos.add(peak_pos);
 			 			

 			 		//	fl_zero_output=1;
 			 	
 			 			peak_pos = 0;
 			 			state = BERECHNUNG_POS;

	    						
	    			 	 
	    			 }
	    			 
	    		 }  
	    	     

	    	  break;
	    	  
	      case TAL: // Tal
	    //	  System.out.println("Case TAL :" + data_in);
	    	  
	    	  if (data_in <= minvalue_border) //Tal erkannt, suchen der Spitze in in_data_mount List
	    	  {
	    		 
	    		  if (data_in < peak_neg)
	    		  {  peak_neg = data_in;
	    		     peak_time_neg = acttime;
	    		  
	    		  break;
	    		  }
	    	  }
	    	 if( data_in > minvalue_border)
	    		  {
	    			 if (fl_cap_neg == 0) 
	    			 {  fl_cap_neg = 1;
	    			 	System.out.println("                                                              Neg Spitze gefunden :" +peak_neg);
	    			 	System.out.println("                                                              Zeit von neg Spitze :"+f.format(peak_time_neg));
	    			 	//RR_Zeiten.add(peak_time_pos);
	    			 	data_peaks_neg.add (peak_neg);
	    			 //	opSDNN.sendData(ConversionUtils.doubleToBytes(peak_neg));

	    			 	fl_zero_output=1;
	    			 	
	    			 	peak_neg = 0;
	    			 	state = BERECHNUNG_NEG;
	    			 }
	    			 else
	    			 break;
	    		  }
	    	      
	    	  break;		  	
	    	
		   case BERECHNUNG_POS:// pos_durchschnittswerte berechnen
			   
			   System.out.println("Case BERECHNUNG_POS");
			   
			   
			   calc_peakmean_pos();
			   calc_middle();
			   
			   if (RR_Zeiten.size() > 1)
			   {
				   calc_RRintervals();
				   calc_RRintervals_mean();
				   calc_act_Pulse();
				   calc_heartbeat_duration();
				   
				   if (RR_Intervall.size() > 1)	
					{  
					   calc_SDNN();
					   calc_DD();
					   calc_RMSSD();
					   calc_SDSD();
					   calc_NN50();
					   calc_PNN50();
					   calc_NN20();
					   calc_PNN20();
					   
					}// bis Heartbeat
				   
			   }//bis auswahl
			   
			    state = AUSWAHL;
			    break;


		   case BERECHNUNG_NEG:// neg_durchschnittswerte berechnen
			   
			   System.out.println("Case BERECHNUNG_NEG");
			   
			   
			   calc_peakmean_neg();

			   state = AUSWAHL;
			    break;
			   
			    
		} 
			
		   //	 if (fl_zero_output==0) opPNN50.sendData(ConversionUtils.doubleToBytes(0));

	}
		//**********//
		// Funktionen//
		//**********//
		
	public void calc_maxvalue_border()
	{	 
    			 
    maxvalue_border = ((maxvalue_init/100)*70);
    System.out.println("maxvalue_init: "+maxvalue_init + "maxvalue_border: " +maxvalue_border);
        		  
    minvalue_border = ((minvalue_init/100)*70);
    System.out.println("minvalue_init: "+minvalue_init + "minvalue_border: " +minvalue_border);
	} 		  
	
	
	public void calc_middle_for_init()
	{
		if ((minvalue_init*-1) >= maxvalue_init)
		{	
			middle = (minvalue_init + maxvalue_init)/2;	
			System.out.println("Neg ist groesser minvalue :" +minvalue_init+"   maxvalue :"+maxvalue_init+"   middle :"+middle);
		}
		if ((minvalue_init*-1) < maxvalue_init)
		{	
			middle	= (maxvalue_init + minvalue_init)/2;
			System.out.println("Pos ist groesser minvalue :" +minvalue_init+"   maxvalue :"+maxvalue_init+"   middle :"+middle);
		}
	}
		
		public void check_if_reversed()
		{
			// Maximalwert im positiven oder negativen Bereich, bzw. sensor verkehrt?
			if ((minvalue_init*-1) > maxvalue_init)
			{	
				fl_twist_data_in = 1;;
				maxvalue_init = maxvalue_init*-1;
				minvalue_init = minvalue_init *-1;
				System.out.println("R Zacke ist im negativen Bereich, Signal wird negiert");
			} 
		
		}
		
		public void calc_peakmean_pos()
		{ 
				peak_mean_pos = 0; 
				// mittelwert der peaks
				if (data_peaks_pos.size() > 0)
				{
					if (data_peaks_pos.size() < 5)// wenn peakcounter kleiner als 5
					{  System.out.println("groesse data_peaks_pos: " + data_peaks_pos.size());
		  
		  
						for (i = 0; i<data_peaks_pos.size();i++)			 	
						{
							peak_mean_pos += data_peaks_pos.get(i);				    
						}	
						
						peak_mean_pos = peak_mean_pos/ data_peaks_pos.size();
						
						maxvalue_border = (peak_mean_pos/100)*70;// neue Grenze wird gesetzt
						System.out.println("maxvalue_border nach peakmean : "+maxvalue_border );
	   		
		    
					}
					else // wenn peakcounter > als 4
					{		
						System.out.println("wenn peakcounter groesser als 4    maxsize : " +data_peaks_pos.size() +"maxsize : " +(data_peaks_pos.size()-5)  );
						for (i = data_peaks_pos.size()-5; i<data_peaks_pos.size();i++)
							{
							peak_mean_pos += data_peaks_pos.get(i);
							}
						
						peak_mean_pos = peak_mean_pos/5;
						
						//peak_mean_pos_sum += data_peaks_pos.get(data_peaks_pos.size()-1);
						
						maxvalue_border = (peak_mean_pos/100)*70; // neue Grenze wird gesetzt
						System.out.println("maxvalue_border nach peakmean : "+maxvalue_border );
					}	
		    
		
		
				}
		}
		
		
		public void calc_middle()
		{  
			if ((data_peaks_pos.size() > 4) && (data_peaks_neg.size() > 4))
			{
			  	if ((peak_mean_neg*-1) >= peak_mean_pos)
  					middle = (peak_mean_neg + peak_mean_pos)/2;	
  				
  				if ((peak_mean_neg*-1) < peak_mean_pos)
  					middle	= (peak_mean_pos + peak_mean_neg)/2;
  				System.out.println("middle mean : "+middle );
  						  
			}
		}
		
		public void calc_RRintervals()
		{ // Berechnenen der Intervalle aus den Zeiten
        	
		      
    	System.out.println("RR_Zeiten.size() > 1: " +RR_Zeiten.size() );
    	    	
    	System.out.println("RR_2.Zeit " + f.format(RR_Zeiten.get(RR_Zeiten.size()-2)));
    	System.out.println("RR_1.Zeit " + f.format(RR_Zeiten.get(RR_Zeiten.size()-1)));			    	
    	
    	    
    	RR_Intervall.add(RR_Zeiten.get(RR_Zeiten.size()-1) - RR_Zeiten.get(RR_Zeiten.size()-2));
    	
    	System.out.println("RR_Intervall: "+ f.format(RR_Intervall.get(RR_Intervall.size()-1)));
   
  	
        
			
		}
		
		public void calc_RRintervals_mean()
		{
			//mittelwert der RR Intervalle			    

		    
		    
		    RR_Int_Mean_Sum += RR_Intervall.get(RR_Intervall.size()-1);
		   
		        RR_Int_Mean = RR_Int_Mean_Sum / RR_Intervall.size();
		        
		        System.out.println("RR_Int_Mean :" + f.format(RR_Int_Mean) + "   RR_INT_Anzahl :" + RR_Intervall.size());
		}
		
		
		
		
		
		public void calc_Runtime()
		{			
			
			if (runtime_i == 25)
			{
			runtime_i = 0;		
			runtime = acttime/1000;
			opRuntime.sendData(ConversionUtils.doubleToBytes(runtime));		
			}
			runtime_i++;
		}
		
		public void calc_act_Pulse()
		{
			// Pulssignal Berechnung
			if (RR_Intervall.size() > 4)   
			{
				RR_Int_Pulse_Sum = 0;
				for (i = RR_Intervall.size()-5; i<RR_Intervall.size(); i++)
					{			    	
					RR_Int_Pulse_Sum += RR_Intervall.get(i);			    	
					}
        	
				RR_Int_Sum = RR_Int_Pulse_Sum/5; 	        
				Pulsrate = 60000/RR_Int_Sum;		     
				System.out.println("Pulsrate :" + f.format(Pulsrate));
				opPulserate.sendData(ConversionUtils.doubleToBytes(Pulsrate));
			}
		
		}
		
		public void calc_heartbeat_duration()
		{ //Berechnung Heartbeat_duration_mean
			
			
			
			Heartbeat_duration_sum += Heartbeat_duration.get(Heartbeat_duration.size()-1);
			
			
			Heartbeat_duration_mean = Heartbeat_duration_sum/Heartbeat_duration.size();
			System.out.println("Heartbeat_duration_mean :" + f.format(Heartbeat_duration_mean));
		}
		
		
		public void calc_SDNN()
		{
		   // Berechnung SDNN		        
        
			
			RR_Int_Sum2_SDNN += Math.pow((RR_Intervall.get(RR_Intervall.size()-1) - RR_Int_Mean),2);
			
	        SDNN = Math.sqrt(RR_Int_Sum2_SDNN/RR_Intervall.size());
	        System.out.println("SDNN :" + f.format(SDNN));
    	
	        opSDNN.sendData(ConversionUtils.doubleToBytes(SDNN));
		}
		
		
		public void calc_DD()
		{
			if(RR_Intervall.get(RR_Intervall.size()-2) >= RR_Intervall.get(RR_Intervall.size()-1))
			{
				DD = RR_Intervall.get(RR_Intervall.size()-2) - RR_Intervall.get(RR_Intervall.size()-1);
			}
				
				
			else
			{
				DD = RR_Intervall.get(RR_Intervall.size()-1) - RR_Intervall.get(RR_Intervall.size()-2);
			}
			
			DD_array.add(DD);
			System.out.println("DD :" + f.format(DD));
			opDD.sendData(ConversionUtils.doubleToBytes(DD));
		}
		
		
		
		public void calc_RMSSD()
		{
			
			
			RR_Int_Sum2_RMSSD += Math.pow(DD_array.get(DD_array.size()-1),2);
			
			RMSSD = Math.sqrt((RR_Int_Sum2_RMSSD/(DD_array.size())));
			
			
						System.out.println("RMSSD :" + f.format(RMSSD));
			        
						opRMSSD.sendData(ConversionUtils.doubleToBytes(RMSSD));
			
		}
		
		public void calc_SDSD()
		{
			DD_mean_sum  += DD_array.get(DD_array.size()-1);
			System.out.println("DD_mean_sum :" + f.format(DD_mean_sum));
			
			 DD_mean = DD_mean_sum / DD_array.size();
			 System.out.println("DD_mean :" + f.format(DD_mean));
			 
			 DD_Sum2_SDSD += Math.pow((DD_array.get(DD_array.size()-1) - DD_mean),2);
			 System.out.println("DD_Sum2_SDSD :" + f.format(DD_Sum2_SDSD));
			 
			 SDSD = Math.sqrt((DD_Sum2_SDSD/(DD_array.size()-1)));
		        
		        System.out.println("SDSD :" + f.format(SDSD));
	    	
		        opSDSD.sendData(ConversionUtils.doubleToBytes(SDSD));
		}

		
		
		
		public void calc_NN50()
		 {
			
			if (DD > 50)
				NN50++;
		
		 }
		 
		public void calc_PNN50()
		 {
			//Berechnung PNN50
			    
		    	PNN50 = (NN50 / RR_Intervall.size()) * 100 ;
		    
		    	System.out.println("PNN50 :" + f.format(PNN50)+"%");
		    	opPNN50.sendData(ConversionUtils.doubleToBytes(PNN50));
		    	
		    	
				 
	     }
		 
		public void calc_NN20()
		{

			if (DD > 20)
				NN20++;
		
		}
		
		public void calc_PNN20()
		{
			//Berechnung PNN50
		    
	    	PNN20 = (NN20 / RR_Intervall.size()) * 100 ;
	    
	    	System.out.println("PNN20 :" + f.format(PNN20)+"%");
	    	opPNN20.sendData(ConversionUtils.doubleToBytes(PNN20));
	    	
				 
		}
		
		
		
		

	
		   public void calc_peakmean_neg()
		   {
		 	 // peak_mean nullsetzen -> wird jedes mal neu berechnet
			   peak_mean_neg = 0;
			   if (data_peaks_neg.size() > 0)
			   	{   
				   if (data_peaks_neg.size() < 5)// wenn peakcounter kleiner als 5		   	
			   		{ 
				    	for (i = 0; i<data_peaks_neg.size();i++)			 	
				    	{					    		
				    		peak_mean_neg += data_peaks_neg.get(i);					    
				    	}	
				    		
				    	peak_mean_neg = peak_mean_neg / data_peaks_neg.size();
				    
				    	minvalue_border = (peak_mean_neg/100)*70;// neue Grenze wird gesetzt   		
				   
			   		}
				  else // wenn peakcounter > als 4
				    	{	
					  		System.out.println("wenn peakcounter > als 4");
				    	
				    		for (i = data_peaks_neg.size()-4; i<data_peaks_neg.size();i++)
				    			{
				    			peak_mean_neg += data_peaks_neg.get(i);
				    			}
				    		peak_mean_neg = peak_mean_neg/5;
				    		minvalue_border = (peak_mean_neg/100)*70; // neue Grenze wird gesetzt
				    	}	
			   		
				    
			 	 }
    	
    	
		   }
    	
    	
    
		
		
		
		
		
		
		
		
		
};

     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpStart = new IRuntimeEventListenerPort()
	{
		public void receiveEvent (final String data)
		{
			System.out.println("Event ausgeloest");
			
		}
	};
	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	 
    	System.out.println("HRV_Analysis start");
    	acttime = 0;
    	/* 
    	  
  
    	  
 
    		  	System.out.println("truesense send start");
    		  CIMPortManager.getInstance().sendPacket(portController,turn_on, (short) 0,(short) 0, false); /*public int sendPacket(CIMPortController ctrl, byte [] data, 
																									short featureAddress, short requestCode, boolean crc)*/
    /*	  in = portController.getInputStream();
    		  readerThread = new Thread(new Runnable() {

				@Override
				public void run() {
					running = true;
				  	System.out.println("truesense in read thread");

					while (running) {

						try { 
							while (in.available() > 0) {

								handlePacketReceived((byte) in.read());
								//System.out.println("in.read() "+in.read());
							} 
							System.out.println("truesense request data");
							//Thread.sleep(100);
							CIMPortManager.getInstance().sendPacket(portController,request_data, (short) 0,(short) 0, false); 
							Thread.sleep(30);
						} catch (InterruptedException ie) {
							ie.printStackTrace();
						} catch (IOException io) {
							io.printStackTrace();
						}
					
					}
				}
    			  
    		  });
    		  readerThread.start();
    	  }
    */
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

          super.stop();
      }


}
