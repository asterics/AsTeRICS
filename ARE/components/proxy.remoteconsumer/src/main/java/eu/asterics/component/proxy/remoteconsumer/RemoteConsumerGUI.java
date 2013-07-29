package eu.asterics.component.proxy.remoteconsumer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class RemoteConsumerGUI extends JInternalFrame
{
	private final JButton openButton = new JButton("Open Socket");
	private final JButton closeButton = new JButton("close Socket");
	private final JButton closeConnButton = new JButton("close Connection");
	

	public RemoteConsumerGUI(final RemoteConsumerInstance instance)
	{
		super("OSKA RemoteConsumer GUI");
	
		openButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			//	instance.connectRemoteComponent();
				openButton.setEnabled(false);
				closeButton.setEnabled(false);
				
			}
		});
		closeButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
			//	instance.disconnectRemoteComponent();
				closeButton.setEnabled(false);
				openButton.setEnabled(true);
			}
			
		});
		
		closeConnButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
			instance.closeConnection();
			
				
			}
			
		});
		
		closeButton.setEnabled(false);
		final JPanel panel = new JPanel();
		final BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
		panel.setLayout(boxLayout);
		panel.add(closeConnButton);
		add(panel);
		pack();
	}


	public void disableOpenSocket() {
		openButton.setEnabled(false);
		
	}


	public boolean isClosedSocketEnabled() {
		
		return closeButton.isEnabled();
	}


	public void enableClosedSocket(boolean b) {
		if(!openButton.isEnabled())
			closeButton.setEnabled(b);	
	}

}