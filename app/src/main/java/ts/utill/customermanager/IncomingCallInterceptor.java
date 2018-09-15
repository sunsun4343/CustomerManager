package ts.utill.customermanager;

import java.util.zip.Inflater;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import ts.utill.customermanager.data.Customer;
import ts.utill.customermanager.data.CustomerDB;

public class IncomingCallInterceptor extends BroadcastReceiver {

	CustomerDB customerDB = CustomerDB.getInstence();

	@Override
	public void onReceive(Context context, Intent intent) {
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		
		if(TelephonyManager.EXTRA_STATE_RINGING.equals(state)){
			String incomNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			//ok
			
			Customer customer = customerDB.getCutomerData(context, incomNumber);
			
			if(customer != null){
				/*
				Intent intent2 = new Intent(context, PopupCustomerService.class);
				intent2.putExtra("idx_customer", customer.getIdx_customer());
				context.startService(intent2);    //���� ����
				*/
			}
			
		}else if(TelephonyManager.EXTRA_STATE_IDLE.equals(state)){
			context.stopService(new Intent(context, PopupCustomerService.class));    //���� ����
		}
	}
}
