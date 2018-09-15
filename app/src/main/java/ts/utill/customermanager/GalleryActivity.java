package ts.utill.customermanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.TextView;

import ts.utill.customermanager.adapter.GalleryAdpter;
import ts.utill.customermanager.data.CustomerDB;


public class GalleryActivity extends Activity {
	
	CustomerDB customerDB = CustomerDB.getInstence();
	
	int idx_customer;
	
	String[] ImageList;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
	
		Intent intent = getIntent();
		idx_customer = intent.getIntExtra("idx_customer", 0);
		
		ImageList = initImageList();
		
		final TextView textView_gallery_date = (TextView)findViewById(R.id.textView_gallery_date);
		
		final ImageView imageView_gallery_select =(ImageView)findViewById(R.id.imageView_gallery_select);
		
		Gallery gallery = (Gallery)findViewById(R.id.gallery_gallery_list);
		gallery.setAdapter(new GalleryAdpter(this));
		
		gallery.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				imageView_gallery_select.setImageBitmap(loadPicture(position));
			}
		});
		
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				textView_gallery_date.setText(getDate(position));
				imageView_gallery_select.setImageBitmap(loadPicture(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}	
		});
		
	}
	
	protected String getDate(int position) {
		File file = new File(CustomerDB.filepath + "gallery" + idx_customer + "/" + ImageList[position]);
		Calendar c = new GregorianCalendar();
		c.setTime(new Date(file.lastModified()));
		return customerDB.ToDate(c);
	}

	public String[] getImageList(){
		return this.ImageList;
	}
	
	public int getIdx_customer(){
		return this.idx_customer;
	}
	
	private String[] initImageList() {
		File file = new File(CustomerDB.filepath + "gallery" + idx_customer + "/");
		
        if (!file.exists()) {
            file.mkdirs();
        }
		
		File[] fileList = file.listFiles();
		
		ArrayList<String> temp = new ArrayList<String>();
		
		for (int i = 0; i < fileList.length; i++) {
			if (!fileList[i].isDirectory()) {
				String name = fileList[i].getName();
				
				if(name.substring(name.length()-4, name.length()).equals(".jpg")){
					temp.add(fileList[i].getName());
					
				}
			}
		}
		
		String[] str = new String[temp.size()];
		for (int i = 0; i < temp.size(); i++) {
			str[i] = temp.get(i);
		}
		return str;
	}
	
	private Bitmap loadPicture(int i) {
  		File file = new File(CustomerDB.filepath + "gallery" + idx_customer + "/" + ImageList[i]);
  		BitmapFactory.Options option = new BitmapFactory.Options();
  		option.inSampleSize = 4;
  		return BitmapFactory.decodeFile(file.getAbsolutePath(), option);
	}
}
