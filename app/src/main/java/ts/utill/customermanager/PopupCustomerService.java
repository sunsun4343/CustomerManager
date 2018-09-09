package ts.utill.customermanager;

import java.io.File;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ts.utill.customermanager.data.Customer;
import ts.utill.customermanager.data.CustomerDB;

public class PopupCustomerService extends Service {

	private View mPopupView;
	private WindowManager.LayoutParams mParams;  //layout params ��ü. ���� ��ġ �� ũ��
    private WindowManager mWindowManager;          //������ �Ŵ���
	
    CustomerDB customerDB = CustomerDB.getInstence();
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.d("TestG", "onStartCommand");
		
		int idx_customer = intent.getIntExtra("idx_customer", 0);
		
		Customer customer = customerDB.getCustomer(idx_customer);
		mPopupView = View.inflate(this, R.layout.popupwindow_customerview, null);
		
		TextView textView_popwindow_customerview_name = (TextView)mPopupView.findViewById(R.id.textView_popwindow_customerview_name);
		TextView textView_popwindow_customerview_vistDate = (TextView)mPopupView.findViewById(R.id.textView_popwindow_customerview_vistDate);
		TextView textView_popwindow_customerview_memo = (TextView)mPopupView.findViewById(R.id.textView_popwindow_customerview_memo);
		
		textView_popwindow_customerview_name.setText(customer.getName());
		
		Log.d("TestG", "customer.getName() " + customer.getName());
		
		Log.d("TestG", "customer.getLastVistDate() " + customer.getLastVistDate());
		
		textView_popwindow_customerview_vistDate.setText(customerDB.ToDate(customer.getLastVistDate()));
		textView_popwindow_customerview_memo.setText(customer.getMemo());
		
		ImageView imageView_popwindow_customerview_face = (ImageView)mPopupView.findViewById(R.id.imageView_popwindow_customerview_face);
		ImageView imageView_popwindow_customerview_sex = (ImageView)mPopupView.findViewById(R.id.imageView_popwindow_customerview_sex);
		ImageView imageView_popwindow_customerview_age = (ImageView)mPopupView.findViewById(R.id.imageView_popwindow_customerview_age);
		
		String fileChk = CustomerDB.filepath +idx_customer + ".jpg";
		File file = new File(fileChk);
		if (file.exists()) {
			imageView_popwindow_customerview_face.setImageBitmap(loadPicture(idx_customer));
		}else{
			imageView_popwindow_customerview_face.setImageResource(R.drawable.nopace);
		}
		
		imageView_popwindow_customerview_sex.setImageResource(customerDB.getImageSex(customer.getSexToInt()));
		imageView_popwindow_customerview_age.setImageResource(customerDB.getImageAge(customer.getAge()));
		
		mParams = new WindowManager.LayoutParams(
		            WindowManager.LayoutParams.WRAP_CONTENT,
		            WindowManager.LayoutParams.WRAP_CONTENT,
		            WindowManager.LayoutParams.TYPE_PHONE,//�׻� �� ����. ��ġ �̺�Ʈ ���� �� ����.
		            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  //��Ŀ���� ������ ����
		            PixelFormat.OPAQUE);                                        //����
		        mParams.gravity = Gravity.CENTER | Gravity.TOP;                   //���� ��ܿ� ��ġ�ϰ� ��.
		        
		        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);  //������ �Ŵ���
		        mWindowManager.addView(mPopupView, mParams);      //�����쿡 �� �ֱ�. permission �ʿ�.
		
		return super.onStartCommand(intent, flags, startId);
	}

	private Bitmap loadPicture(int idx_customer) {
  		File file = new File(CustomerDB.filepath + idx_customer + ".jpg");
  		BitmapFactory.Options option = new BitmapFactory.Options();
  		option.inSampleSize = 2;
  		option.inScaled = true;
  		option.inPurgeable = true;
  		return BitmapFactory.decodeFile(file.getAbsolutePath(), option);
	}

	@Override
	public void onDestroy() {
        if(mWindowManager != null) { 
            if(mPopupView != null) mWindowManager.removeView(mPopupView);
        }
		super.onDestroy();
	}

}
