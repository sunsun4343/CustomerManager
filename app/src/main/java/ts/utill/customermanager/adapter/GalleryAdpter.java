package ts.utill.customermanager.adapter;

import java.io.File;

import ts.utill.customermanager.GalleryActivity;
import ts.utill.customermanager.data.CustomerDB;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

public class GalleryAdpter extends BaseAdapter {

	private Context mContext;
	GalleryActivity galleryActivity;
	
	public GalleryAdpter(Context c) {
		this.mContext = c;
		this.galleryActivity = (GalleryActivity)mContext;
	}

	@Override
	public int getCount() {
		return galleryActivity.getImageList().length;
	}

	@Override
	public Object getItem(int position) {
		return  galleryActivity.getImageList()[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		
		if(convertView == null){
			imageView = new ImageView(mContext);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setLayoutParams(new Gallery.LayoutParams(300,400));
		}else{
			imageView = (ImageView)convertView;
		}
		
		
		imageView.setImageBitmap(loadPicture(position));
		return imageView;
	}
	
	private Bitmap loadPicture(int i) {
  		File file = new File(CustomerDB.filepath + "gallery" + galleryActivity.getIdx_customer() + "/" + galleryActivity.getImageList()[i]);
  		BitmapFactory.Options option = new BitmapFactory.Options();
  		option.inSampleSize = 16;
  		return BitmapFactory.decodeFile(file.getAbsolutePath(), option);
	}


}
