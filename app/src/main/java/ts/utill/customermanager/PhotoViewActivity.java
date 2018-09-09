package ts.utill.customermanager;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

import ts.utill.customermanager.data.CustomerDB;


public class PhotoViewActivity  extends Activity {
	
	int idx_customer;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photoview);
		
		ImageView imageView_photoView = (ImageView)findViewById(R.id.imageView_photoView);
		
		Intent intent = getIntent();
		idx_customer = intent.getIntExtra("idx_customer", 0);
		
		if(idx_customer == 0){
			imageView_photoView.setImageResource(R.drawable.nopace);
		}else{
			String fileChk = CustomerDB.filepath + idx_customer + "_high.jpg";
			File file = new File(fileChk);
			if (file.exists()) {
				imageView_photoView.setImageBitmap(loadPicture(idx_customer));
			}
		}
	}
	
	private Bitmap loadPicture(int i) {
  		File file = new File(CustomerDB.filepath + i + "_high.jpg");
  		BitmapFactory.Options option = new BitmapFactory.Options();
  		option.inSampleSize = 1;
  		return BitmapFactory.decodeFile(file.getAbsolutePath(), option);
	}
}