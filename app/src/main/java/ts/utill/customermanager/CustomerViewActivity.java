package ts.utill.customermanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import ts.utill.customermanager.adapter.SaleListAdapter;
import ts.utill.customermanager.data.Customer;
import ts.utill.customermanager.data.CustomerDB;
import ts.utill.customermanager.data.ItemSet;
import ts.utill.customermanager.data.Sale;
import ts.utill.customermanager.data.UserSetting;

public class CustomerViewActivity extends AppCompatActivity{
	
	static int REQUEST_PICTURE = 3;
	static int REQUEST_PHOTO_ALBUM = 4;
	
	boolean isModeNew;
	
	CustomerDB customerDB = CustomerDB.getInstence();
	UserSetting userSetting = UserSetting.getInstance();
	Customer customer;
	Customer view_customer;
	
	SaleListAdapter saleListAdapter;
	
	EditText editText_customerView_name;
	EditText editText_customerView_hp;
	EditText editText_customerView_memo;
	
	TextView textView_customerView_vistCnt;
	TextView textView_customerView_totalAmount;
	
	ImageView imageView_customerView_face;

	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_view);

		context = this;

		Intent intent = getIntent();
		isModeNew = intent.getBooleanExtra("modeNew", true);
		
		imageView_customerView_face = (ImageView)findViewById(R.id.imageView_customerView_face);
		
		final ImageView imageView_customerView_sex = (ImageView)findViewById(R.id.imageView_customerView_sex);
		final ImageView imageView_customerView_age = (ImageView)findViewById(R.id.imageView_customerView_age);
		final ImageView imageView_customerView_newSale =  (ImageView)findViewById(R.id.imageView_customerView_newSale);

		editText_customerView_name = (EditText)findViewById(R.id.editText_customerView_name);
		editText_customerView_hp = (EditText)findViewById(R.id.editText_customerView_hp);
		editText_customerView_memo = (EditText)findViewById(R.id.editText_customerView_memo);
		
		if(isModeNew){
			customer =  new Customer();
			view_customer = (Customer)customer.clone();
			
			LinearLayout layout_customerView_vistandamount = (LinearLayout)findViewById(R.id.layout_customerView_vistandamount);
			layout_customerView_vistandamount.setVisibility(View.GONE);
		}else{
			int idx_customer = intent.getIntExtra("idx_customer", -1);
			
			customer = customerDB.getCustomer(idx_customer);
			view_customer = (Customer)customer.clone();
			
			this.setTitle(customer.getName() + " 고객님");
			
			String fileChk =CustomerDB.filepath + customer.getIdx_customer() + ".jpg";
			File file = new File(fileChk);
			if (file.exists()) {
				imageView_customerView_face.setImageBitmap(loadPicture(customer.getIdx_customer()));
			}
			
			textView_customerView_vistCnt = (TextView)findViewById(R.id.textView_customerView_vistCnt);
			textView_customerView_totalAmount = (TextView)findViewById(R.id.textView_customerView_totalAmount);
			
			editText_customerView_name.setText(customer.getName());
			editText_customerView_hp.setText(customer.getHp());
			editText_customerView_memo.setText(customer.getMemo());
			
			TextView label_customerView_age = (TextView)findViewById(R.id.label_customerView_age);
			
			imageView_customerView_sex.setImageResource(customerDB.getImageSex(customer.getSexToInt()));
			if (userSetting._AgeNogView) {
				label_customerView_age.setVisibility(View.GONE);
				imageView_customerView_age.setVisibility(View.GONE);
			}else
			{
				label_customerView_age.setVisibility(View.VISIBLE);
				imageView_customerView_age.setVisibility(View.VISIBLE);
				imageView_customerView_age.setImageResource(customerDB.getImageAge(customer.getAge()));
			}
		
			textView_customerView_vistCnt.setText(customer.getVistCnt() + "");
			textView_customerView_totalAmount.setText(customerDB.toThousand(customer.getAmount()));
			
			
			//list connect
			saleListAdapter = new SaleListAdapter(this, customer);
			ListView customerView_saleList;
			customerView_saleList = (ListView)findViewById(R.id.listView_customerView_slaeList);
			customerView_saleList.setAdapter(saleListAdapter);
			
			//Listnenr
			imageView_customerView_face.setOnClickListener(new ImageView.OnClickListener(){
				public void onClick(View v){
					new AlertDialog.Builder(CustomerViewActivity.this)
					.setTitle("")
					.setIcon(null)
					.setItems(R.array.command_photo,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch (which) {
								case 0: viewphoto(); break;
								case 1: gallery(); break;
								case 2:	takePicture();	break;
								case 3:	photoAlbum();	break;
								case 4:	deletePhoto();	break;									
								}
							}
						})
					.show();
				}
				private void viewphoto() {
					Intent_PhotoViewActivity();
				}				
				private void deletePhoto() {
					File file = new File(CustomerDB.filepath + customer.getIdx_customer() + ".jpg");
					file.delete();
					file = new File(CustomerDB.filepath + customer.getIdx_customer() + "_high.jpg");
					file.delete();
					imageView_customerView_face.setImageResource(R.drawable.nopace);
				}

                private static final int PICK_FROM_GALLERY = 4;

				private void photoAlbum() {
			        File file = new File(CustomerDB.filepath);
			        File file_gallery = new File(CustomerDB.filepath + "gallery" + customer.getIdx_customer() + "/");
			        
			        // If no folders
			        if (!file.exists()) {
			            file.mkdirs();
			        }
			        if (!file_gallery.exists()) {
			            file.mkdirs();
			        }

                    try {
                        if (ActivityCompat.checkSelfPermission(CustomerViewActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(CustomerViewActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                        } else {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//
//			  		Intent intent = new Intent(Intent.ACTION_PICK);
//			  		intent.setType(Images.Media.CONTENT_TYPE);
//			  		//intent.putExtra("crop","true");
//			  		intent.setData(Images.Media.EXTERNAL_CONTENT_URI);
//			  		startActivityForResult(intent, REQUEST_PHOTO_ALBUM);
				}

                public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
                {
                    switch (requestCode) {
                        case PICK_FROM_GALLERY:
                            // If request is cancelled, the result arrays are empty.
                            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                            } else {
                                //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                            }
                            break;
                    }
                }
				
				private void takePicture() {

					if (ActivityCompat.checkSelfPermission(CustomerViewActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
						ActivityCompat.requestPermissions(CustomerViewActivity.this, new String[]{Manifest.permission.CAMERA}, 4);
					}else{

						File file = new File(CustomerDB.filepath);
						File file_gallery = new File(CustomerDB.filepath + "gallery" + customer.getIdx_customer() + "/");

						// If no folders
						if (!file.exists()) {
							file.mkdirs();
						}
						if (!file_gallery.exists()) {
							file.mkdirs();
						}

						Uri uri;
						if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
							uri = Uri.fromFile(file);
						}else{
							uri = FileProvider.getUriForFile(context, "ts.utill.customermanager.fileprovider", file);
						}

						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						file = new File(CustomerDB.filepath +  customer.getIdx_customer() + "_high.jpg");
						intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			  		/*
			  		file_gallery = new File(CustomerDB.filepath + "gallery" + customer.getIdx_customer() + "/" +  getNewGalleryNumber() +  ".jpg");
			  		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file_gallery));
			  		*/
						startActivityForResult(intent, REQUEST_PICTURE);

					}
				}
			
				private void gallery() {
					Intent_GallerywActivity();
				}
			});			
		}
		

		
			imageView_customerView_sex.setOnClickListener(new ImageView.OnClickListener(){
				public void onClick(View v){
					view_customer.ChangeSex();
					imageView_customerView_sex.setImageResource(customerDB.getImageSex(view_customer.getSexToInt()));
				}
			});
			
			imageView_customerView_age.setOnClickListener(new ImageView.OnClickListener(){
				public void onClick(View v){
					view_customer.ChangeAge();
					imageView_customerView_age.setImageResource(customerDB.getImageAge(view_customer.getAge()));
				}
			});		
			
			imageView_customerView_newSale.setOnClickListener(new ImageView.OnClickListener(){
				public void onClick(View v){
					//Chek Item List
					if(customerDB.getItemList().size() == 0){
						new AlertDialog.Builder(CustomerViewActivity.this)
						.setTitle(R.string.itemsetting)
						.setIcon(R.drawable.item)
						.setMessage(R.string.needitemsetting)
						.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Intent_ItemListActivity();
							}
						})
						.setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						})
						.show();
					}else{
						if(isModeNew){
							//Intent_NewSaleActivity(true);
						}else{
							Intent_NewSaleActivity();
						}
					}
				}
			});
			
		
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (isModeNew) {
			getMenuInflater().inflate(R.menu.customer_view, menu);
		}else{
			getMenuInflater().inflate(R.menu.customer_view2, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_chek) {
			if(isModeNew){
				//New Customer
				String name = editText_customerView_name.getText().toString();
				String hp = editText_customerView_hp.getText().toString();
				String memo = editText_customerView_memo.getText().toString();
				boolean sex = view_customer.getSex();
				int age = view_customer.getAge();
				
				customerDB.Insert_Customer(name, customerDB.ToTrimHp(hp), sex, age, memo, 0, view_customer.getSaleList());

				//Refresh & Finish
				finish();
			}else{
				//Edit Customer
				view_customer.setName(editText_customerView_name.getText().toString());
				view_customer.setHp(customerDB.ToTrimHp(editText_customerView_hp.getText().toString()));
				view_customer.setMemo(editText_customerView_memo.getText().toString());
				
				customerDB.Update_Customer(customer, view_customer);
				
				//Refresh & Finish
				finish();
			}
			return true;
		}else if(id == R.id.action_delete){
			
			new AlertDialog.Builder(CustomerViewActivity.this)
			.setTitle(R.string.action_delete)
			.setIcon(R.drawable.delcustomer)
			.setMessage(R.string.delete_Q)
			.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
					customerDB.Delete_Customer(customer);
					
					//Finish
					finish();
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
			
			return true;
		}else if(id == R.id.action_statistics){
			Intent_CustomerStatisticsActivity();
		}
		
		return super.onOptionsItemSelected(item);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  		super.onActivityResult(requestCode, resultCode, data);
  		
  		Log.d("TestG", " Data " +data);



   		if (requestCode == REQUEST_PICTURE) {
   			if(data == null){
	  			String imagePath = CustomerDB.filepath + customer.getIdx_customer() + "_high.jpg";
	  			Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
	  			
				String filename_gallery =  "gallery" + customer.getIdx_customer() + "/" +  getNewGalleryNumber() +  ".jpg";
				SaveBitmapToFileCache(bitmap, filename_gallery);
	  			
				float width = 200.0f;
				float height = width / bitmap.getWidth() * bitmap.getHeight();

				Bitmap bitmap_Low = Bitmap.createScaledBitmap(bitmap, (int)width, (int)height, true);
				String filename =  customer.getIdx_customer() + ".jpg";
				SaveBitmapToFileCache(bitmap_Low, filename);
	  			
				imageView_customerView_face.setImageBitmap(loadPicture(customer.getIdx_customer()));	   				
   			}
		}else if (requestCode == REQUEST_PHOTO_ALBUM) {
			if(data != null){
				imageView_customerView_face.setImageURI(data.getData());
				
				String imagePath = getImageRealPathFromURI(this.getContentResolver(), data.getData()); // path ���
				Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
				
				String filename =  customer.getIdx_customer() + "_high.jpg";
				SaveBitmapToFileCache(bitmap, filename);
				
				String filename_gallery =  "gallery" + customer.getIdx_customer() + "/" +  getNewGalleryNumber() +  ".jpg";
				SaveBitmapToFileCache(bitmap, filename_gallery);
				
				float width = 200.0f;
				float height = width / bitmap.getWidth() * bitmap.getHeight();
				
				Bitmap bitmap_Low = Bitmap.createScaledBitmap(bitmap, (int)width, (int)height, true);
				filename =  customer.getIdx_customer() + ".jpg";
				SaveBitmapToFileCache(bitmap_Low, filename);					
			}
		}

  		
		switch (requestCode) {
		case 0:
			if(resultCode == RESULT_OK){	
				
				Calendar date =customerDB.getDateTime(data.getStringExtra("datetime"));
				ArrayList<ItemSet> itemlist = customerDB.ToSaleItem(data.getStringExtra("itemlist"));
				String memo = data.getStringExtra("memo");
				
				view_customer.getSaleList().add(new Sale(date, itemlist, memo));
				
				
				//Refresh
				saleListAdapter.notifyDataSetChanged();
			}
			break;
		case 1:
			if(resultCode == RESULT_OK){
				//Refresh
				
				saleListAdapter.arSrcRefresh();
				//saleListAdapter.notifyDataSetChanged();
				
				textView_customerView_vistCnt.setText(customer.getVistCnt() + "");
				textView_customerView_totalAmount.setText(customerDB.toThousand(customer.getAmount()));
			}
			break;
		case 2:
			if(resultCode == RESULT_OK){
				//Refresh
				saleListAdapter.arSrcRefresh();
				//saleListAdapter.notifyDataSetChanged();
				
				textView_customerView_vistCnt.setText(customer.getVistCnt() + "");
				textView_customerView_totalAmount.setText(customerDB.toThousand(customer.getAmount()));
			}
			break;
		}
	}
	
	private int getNewGalleryNumber() {
		
			File file = new File(CustomerDB.filepath + "gallery" + customer.getIdx_customer() + "/" );
			
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
			
			if(temp.size() > 0){
				String value = temp.get(temp.size()-1);
				return Integer.parseInt(value.substring(0, value.length() - 4)) +1;					
			}else{
				return 1;
			}
	}
	
    public String getImageRealPathFromURI(ContentResolver cr, Uri contentUri) {
        // can post image
        String[] proj = { Images.Media.DATA };
 
        Cursor cursor = cr.query(contentUri, proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
 
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            int path = cursor
                    .getColumnIndexOrThrow(Images.Media.DATA);
            cursor.moveToFirst();
 
            String tmp = cursor.getString(path);
            cursor.close();
            return tmp;
        }
    }

	// Bitmap to File
    public  void SaveBitmapToFileCache(Bitmap bitmap, String filename) {
 
        File file = new File(CustomerDB.filepath);
 
        // If no folders
        if (!file.exists()) {
            file.mkdirs();
        }
 
        File fileCacheItem = new File(CustomerDB.filepath + filename);
        OutputStream out = null;
        
        try {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);
 
            bitmap.compress(CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	private Bitmap loadPicture(int i) {
  		File file = new File(CustomerDB.filepath + i + ".jpg");
  		BitmapFactory.Options option = new BitmapFactory.Options();
  		option.inSampleSize = 1;
  		return BitmapFactory.decodeFile(file.getAbsolutePath(), option);
	}

	@Override
	public void finish() {
		Intent intent = new Intent();
		setResult(RESULT_OK,intent);		
		super.finish();
	}

	void Intent_NewSaleActivity(){
		Intent intent = new Intent(CustomerViewActivity.this, NewSaleActivity.class);
		intent.putExtra("modeNew",true);
		intent.putExtra("modeNotCustomer",false);
		intent.putExtra("idx_customer",customer.getIdx_customer());
		//this.startActivity(intent);
		this.startActivityForResult(intent, 1);
	}
	/*
	protected void Intent_NewSaleActivity(boolean ismodenew) {
		Intent intent = new Intent(CustomerViewActivity.this, NewSaleActivity.class);
		intent.putExtra("modeNew",true);
		intent.putExtra("modeNotCustomer",true);
		this.startActivityForResult(intent, 0);
	}
	*/
	
	void Intent_ItemListActivity(){
		Intent intent = new Intent(CustomerViewActivity.this, ItemListActivity.class);
		this.startActivity(intent);
	}

	public void Intent_NewSaleActivity(int idx_Sale) {
		Intent intent = new Intent(CustomerViewActivity.this, NewSaleActivity.class);
		intent.putExtra("modeNew",false);
		intent.putExtra("idx_customer",customer.getIdx_customer());
		intent.putExtra("idx_Sale",idx_Sale);
		this.startActivityForResult(intent, 2);
	}
	
	protected void Intent_PhotoViewActivity() {
		Intent intent = new Intent(CustomerViewActivity.this, PhotoViewActivity.class);
		intent.putExtra("idx_customer",customer.getIdx_customer());
		this.startActivity(intent);
	}
	
	private void Intent_GallerywActivity() {
		Intent intent = new Intent(CustomerViewActivity.this, GalleryActivity.class);
		intent.putExtra("idx_customer",customer.getIdx_customer());
		this.startActivity(intent);
	}
	
	private void Intent_CustomerStatisticsActivity() {
		Intent intent = new Intent(CustomerViewActivity.this, CustomerStatisticsActivity.class);
		intent.putExtra("idx_customer",customer.getIdx_customer());
		this.startActivity(intent);
	}
}
