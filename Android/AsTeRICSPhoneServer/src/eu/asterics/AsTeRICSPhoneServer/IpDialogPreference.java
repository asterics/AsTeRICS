package eu.asterics.AsTeRICSPhoneServer;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
//import java.util.Formatter;
//import java.util.Formatter;
import java.util.List;
//import java.util.logging.Formatter;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class IpDialogPreference extends DialogPreference {

	EditText edit;
	TextView text;
	
	public IpDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setDialogLayoutResource(R.layout.ip_dialog);
	}
	
	public IpDialogPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	/*
	@Override
	protected View onCreateDialogView() {
		//this.edit = new EditText(this.getContext());
		//this.edit.setText(getPersistedString("default"));
		return View.(R.layout.ip_dialog);
	}*/
	
	@Override
	protected void onBindDialogView(View view) {
		
		text = (TextView)view.findViewById(R.id.ipInformation);
		
		foundInterfaces();
		
		text.setText("");
		
		for(int i=0;i<interfacesFound.size();i++)
		{
			text.append(interfacesFound.get(i).name +":\n" + interfacesFound.get(i).IP +"\n");
		}
		
		super.onBindDialogView(view);
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
	}
	
	List<InterfaceData> interfacesFound = new ArrayList<InterfaceData>();
	
	
	class InterfaceData
	{
		public InterfaceData(String name,String IP)
		{
			this.name=name;
			this.IP=IP;
		}
		public String name;
		public String IP;
	}
	
	private void foundInterfaces()
	{
		interfacesFound.clear();
		try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            String interfaceName=intf.getName();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
	                	
	                    String address= inetAddress.getHostAddress();
	                    InterfaceData interfaceData=new InterfaceData(interfaceName,address);
	                    interfacesFound.add(interfaceData);
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        //ex.printStackTrace();
	    }
	}


}
