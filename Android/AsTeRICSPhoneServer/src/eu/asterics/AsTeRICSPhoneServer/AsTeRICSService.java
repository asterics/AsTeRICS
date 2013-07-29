package eu.asterics.AsTeRICSPhoneServer;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/*
public class AsTeRICSService extends IntentService {

	public static final String Finish_operation="FINISH_OPERATION";
	
	public AsTeRICSService() {
		super("AsTeRICSService");
	}
	
	private boolean finish=false;

	@Override
	protected void onHandleIntent(Intent intent) {
		finish=false;
		do
		{
			long endTime = System.currentTimeMillis() + 15*1000;
			Log.d("AsTeRICSService", "start");
		      while (System.currentTimeMillis() < endTime) {
		          synchronized (this) {
		              try {
		                  wait(endTime - System.currentTimeMillis());
		              } catch (Exception e) {
		              }
		          }
		      }
		      Log.d("AsTeRICSService", "finish");
		      
		}while(!finish);
		
	}
	
	int notificationNumber=0;
	
	private void displayNotification(String msg)
	{
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(R.drawable.ic_launcher, msg, System.currentTimeMillis());

		// The PendingIntent will launch activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, AsTeRICSPhoneServerActivity.class), 0);

		notification.setLatestEventInfo(this, "AsTeRICS server", "Notification: " + notificationNumber, contentIntent);
		notificationNumber++;

		manager.notify(1, notification);

	}
	

	
	public void finish()
	{
		finish=true;
	}

}*/
