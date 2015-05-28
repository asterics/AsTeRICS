package eu.asterics.mw.are;

import eu.asterics.mw.are.UDP.UDPThread;
import eu.asterics.mw.are.asapi.Activator;
import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.are.exceptions.DeploymentException;
import eu.asterics.mw.gui.AstericsGUI;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsModelExecutionThreadPool;
import eu.asterics.mw.services.AstericsThreadPool;
import eu.asterics.mw.utils.OSUtils;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


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
 * Starting point for ARE middleware
 *
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 *         Date: Aug 23, 2010
 *         Time: 11:36:14 AM
 */
public class Main implements BundleActivator
{
	private static Logger logger = null;

	private BundleManager bundleManager = null;

	private AstericsGUI astericsGUI=null;
	private JFrame astericsFrame;
	

	private static BundleContext areContext;

	public static BundleContext getAREContext (){
		return areContext;
	}
	public void start(final BundleContext context) throws Exception
	{
		try {

			logger = AstericsErrorHandling.instance.getLogger();
			// Check if not 32bit
			String bits = System.getProperty("sun.arch.data.model");
			if (OSUtils.isWindows() && bits.compareTo("64") == 0) {
				logger.severe("JVM "
						+ bits
						+ " bit detected! ARE needs a 32bit JVM \n ARE will shutdown");
				long start = System.currentTimeMillis();
				long end = start + 5 * 1000; // 60 seconds * 1000 ms/sec
				while (System.currentTimeMillis() < end) {
					;
				}
				System.exit(0);
			}
			logger.info("JVM " + bits + " bit detected");
			final String startModel = context
					.getProperty("eu.asterics.ARE.startModel");
			logger.info("Property eu.asterics.ARE.startModel: " + startModel);

			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						astericsGUI = new AstericsGUI(context);

						astericsFrame = astericsGUI.getFrame();

						DeploymentManager.instance.setGui(astericsGUI);

						DeploymentManager.instance.setStatus(AREStatus.UNKNOWN);
						AstericsErrorHandling.instance.setStatusObject(
								AREStatus.UNKNOWN.toString(), "", "");
						areContext = context;


						bundleManager = new BundleManager(context);
						context.addBundleListener(bundleManager);
						context.addFrameworkListener(bundleManager);
						bundleManager.start();
						
						DeploymentManager.instance
								.setBundleManager(bundleManager);

						DeploymentManager.instance.start(context);


						// Create thread pools and eventually store back
						// properties
						AstericsThreadPool.getInstance();
						AstericsModelExecutionThreadPool.getInstance();
						AREProperties.instance.storeProperties();

						DeploymentManager.instance.setStatus(AREStatus.OK);
						AstericsErrorHandling.instance.setStatusObject(
								AREStatus.OK.toString(), "", "");

						/*
						 * if(AREProperties.instance.checkProperty("iconify",
						 * "1")) {
						 * System.out.println("*** Main: set to SystemTray !!");
						 * 
						 * astericsGUI.setSystemTray();
						 * astericsFrame.setVisible(false); } else {
						 * System.out.println("*** Main: no SystemTray !!");
						 * astericsFrame.setVisible(true);
						 * astericsFrame.setState(JFrame.NORMAL); }
						 */

						astericsFrame.setVisible(true);

						AsapiSupport as = new AsapiSupport();
						// System.out.println("***  starting model !");
						as.autostart(startModel);

						Thread asapiServerThread = new Thread(new Activator());
						asapiServerThread.start();

						Thread udpThread = new Thread(new UDPThread());
						udpThread.start();
					} catch (Throwable e) {
						String reason=e.getMessage()!=null ? "\n"+e.getMessage() : "";
						JOptionPane.showMessageDialog(null,
								"The AsTeRICS Runtime Environment could not be initiated!"+reason
								, "ARE startup error",
								JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
				}
			});
		} catch (Throwable t) {
			// custom title, error icon
			JOptionPane.showMessageDialog(null,
					"The AsTeRICS Runtime Environment could not be initiated!",
					"ARE startup error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

	}


	/**
	 * This method stops the ARE.
	 * @param context the BundleContext
	 * @throws Exception
	 */
	public void stop(BundleContext context) throws Exception
	{
		logger.fine(this.getClass().getName()+".stop: " +
				"removing bundle listener \n");

		context.removeBundleListener(bundleManager);

		bundleManager.stop();

		logger.fine(this.getClass().getName()+".stop: " +
				"destroying the bundle manager \n");

		bundleManager = null;

		logger.fine(this.getClass().getName()+".stop: " +
				"stopping deployment manager \n");

		DeploymentManager.instance.stop();



		logger.fine(this.getClass().getName()+".stop: " +
				"destroying the bundle manager \n");

		logger.fine(this.getClass().getName()+".stop: OK \n");
		astericsFrame.setVisible(false);
	}
}
