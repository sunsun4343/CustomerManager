package ts.utill.customermanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import ts.utill.customermanager.R.string;
import ts.utill.customermanager.data.Customer;
import ts.utill.customermanager.data.CustomerDB;
import ts.utill.customermanager.data.CustomerDBJson;
import ts.utill.customermanager.data.Item;
import ts.utill.customermanager.data.ItemGroup;
import ts.utill.customermanager.data.Sale;

import android.Manifest;
import android.R.integer;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.AndroidCharacter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SettingActivity extends AppCompatActivity{
	
	ArrayAdapter<String> adapter;
	
	CustomerDB customerDB = CustomerDB.getInstence();

	SettingActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		activity = this;

        ListView listView_settingList = (ListView)findViewById(R.id.listView_settingList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.setting));
        listView_settingList.setAdapter(adapter);
        
        listView_settingList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0: //상품설정
					Intent_ItemListActivity();
					break;
				case 1: //등급설정
					new AlertDialog.Builder(SettingActivity.this)
					.setTitle(string.ready)
					.setMessage(string.ready_context)
					.show();
					break;
				case 2: //기타설정
					Intent_OtherSettingActivity();
					break;
				case 3: //DB 초기화
					new AlertDialog.Builder(SettingActivity.this)
					.setTitle(string.warning)
					.setIcon(R.mipmap.warning)
					.setMessage(string.reset_Q)
					.setPositiveButton(string.action_delete, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							customerDB.ResetDB();

							//Finish
							finish();
						}
					})
					.setNegativeButton(string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.show();
					break;
				case 4: //사진 삭제
					new AlertDialog.Builder(SettingActivity.this)
					.setTitle(string.warning)
					.setIcon(R.mipmap.warning)
					.setMessage(string.resetphoto_Q)
					.setPositiveButton(string.action_delete, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							customerDB.ResetPhoto();

							//Finish
							finish();
						}
					})
					.setNegativeButton(string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.show();
					break;
				case 5: //csv가져오기
					if (ActivityCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
						ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
					}else{

						if(customerDB.getCustomerCnt() == 0 && customerDB.getItemList().size() == 0){
							new AlertDialog.Builder(SettingActivity.this)
									.setIcon(R.drawable.item)
									.setTitle("CSV List")
									.setSingleChoiceItems(getCSVFileList(), -1, new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											CSVImport(getCSVFileList()[which]);
											dialog.cancel();
										}
									})
									.create()
									.show();
						}else{
							new AlertDialog.Builder(SettingActivity.this)
									.setTitle(string.warning)
									.setIcon(R.mipmap.warning)
									.setMessage(string.needreset)
									.show();
						}

					}
					break;
//				case 8: //csv 편집
//					new AlertDialog.Builder(SettingActivity.this)
//					.setIcon(R.drawable.item)
//					.setTitle("CSV List")
//					.setSingleChoiceItems(getCSVFileList(), -1, new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							Intent_CSVEditActivity(getCSVFileList()[which]);
//							dialog.cancel();
//						}
//					})
//					.create()
//					.show();
//					break;

				case 6: //Json 내보내기
				{
					if (ActivityCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
						ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
					}else{
						final LinearLayout layout = (LinearLayout)View.inflate(SettingActivity.this, R.layout.setting_dialog_input, null);

						final EditText editText_setting_dialog = (EditText)layout.findViewById(R.id.editText_setting_dialog);
						final TextView textView_setting_dialog_input = (TextView)layout.findViewById(R.id.textView_setting_dialog_input);

						textView_setting_dialog_input.setText("저장위치 : " + customerDB.filepath);

						new AlertDialog.Builder(SettingActivity.this)
								.setTitle(string.needfilename)
								.setIcon(R.drawable.item)
								.setView(layout)
								.setPositiveButton(string.save,new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										if(editText_setting_dialog.getText().toString().equals("")){
											Toast.makeText(SettingActivity.this, "파일명을 입력해주세요.", Toast.LENGTH_LONG).show();
										}else{
											customerDB.JsonExport(SettingActivity.this,editText_setting_dialog.getText().toString());
										}
										SharedPreferences pref = getSharedPreferences("Pref_CustomerManager",0);
										SharedPreferences.Editor edit = pref.edit();
										Calendar c = new GregorianCalendar();
										edit.putString("Backup_Date", customerDB.ToDateTime(c));
										edit.commit();
									}

								})
								.setNegativeButton(string.cancel, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
									}
								})
								.show();
					}

				}



						break;
				case 7: // Json 가져오기

					if (ActivityCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
						ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
					}else{

						if(customerDB.getCustomerCnt() == 0 && customerDB.getItemList().size() == 0){
							new AlertDialog.Builder(SettingActivity.this)
									.setIcon(R.drawable.item)
									.setTitle("Json List")
									.setSingleChoiceItems(getJsonFileList(), -1, new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											JsonImport(getJsonFileList()[which]);
											dialog.cancel();
										}
									})
									.create()
									.show();
						}else{
							new AlertDialog.Builder(SettingActivity.this)
									.setTitle(string.warning)
									.setIcon(R.mipmap.warning)
									.setMessage(string.needreset)
									.show();
						}
					}

						break;
				case 8: //Json 외부전송
					if (ActivityCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
						ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
					}else{
						new AlertDialog.Builder(SettingActivity.this)
								.setIcon(R.drawable.item)
								.setTitle("Json List")
								.setSingleChoiceItems(getJsonFileList(), -1, new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent_ACTIONSEND(getJsonFileList()[which].toString());
										dialog.cancel();
									}
								})
								.create()
								.show();
					}

					break;
                case 9: // Json 클립보드 복사 및 표시
				{
					final EditText et = new EditText(SettingActivity.this);
					et.setLines(6);
					String json = customerDB.JsonExport(SettingActivity.this);
					et.setText(json);

					ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
					ClipData clip = ClipData.newPlainText("json", json.toString());
					clipboard.setPrimaryClip(clip);

					final AlertDialog.Builder alt_bld = new AlertDialog.Builder(SettingActivity.this);
					alt_bld.setTitle("Json 클립보드 복사 및 표시")
							.setCancelable(false)
							.setView(et)
							.setNegativeButton("취소", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
								}
							});
					AlertDialog alert = alt_bld.create();
					alert.show();

					Toast.makeText(SettingActivity.this, "json 데이터가 복사되었습니다.", Toast.LENGTH_LONG).show();
				}
                    break;
                case 10://Json Text로 가져오기
				{

					if (ActivityCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
						ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
					}else{

						if(customerDB.getCustomerCnt() == 0 && customerDB.getItemList().size() == 0){
							final EditText et = new EditText(SettingActivity.this);
							et.setText("");
							final AlertDialog.Builder alt_bld = new AlertDialog.Builder(SettingActivity.this);
							alt_bld.setTitle("Json Text로 가져오기")
									.setCancelable(false)
									.setView(et)
									.setPositiveButton("가져오기", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											JsonImportFromText(et.getText().toString());
										}
									});
							alt_bld.setNegativeButton("취소", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
								}
							});
							AlertDialog alert = alt_bld.create();
							alert.show();
						}else{
							new AlertDialog.Builder(SettingActivity.this)
									.setTitle(string.warning)
									.setIcon(R.mipmap.warning)
									.setMessage(string.needreset)
									.show();
						}
					}

				}
                    break;
				case 11: // 도움말
					Uri uri = Uri.parse("http://blog.naver.com/sunsun4343/220108349570");
					Intent it  = new Intent(Intent.ACTION_VIEW,uri);
					startActivity(it);
					break;
				case 12: // 제작자 앤 앱
					Intent marketLaunch = new Intent(Intent.ACTION_VIEW); 
					marketLaunch.setData(Uri.parse("market://search?q=pub:이선선")); 
					startActivity(marketLaunch);
					break;
				case 13: // 공지사항
					new AlertDialog.Builder(SettingActivity.this)
					.setTitle(string.notice)
					.setMessage(string.notice_context)
					.show();
					break;
				case 14: // 문의
					new AlertDialog.Builder(SettingActivity.this)
					.setTitle(string.inquiry)
					.setMessage(string.inquiry_context)
					.show();
					break;
				}
			}



        });
	}
	
	
	
	@Override
	public void finish() {
		Intent intent = new Intent();
		setResult(RESULT_OK,intent);	
		super.finish();
	}



	void Intent_ItemListActivity(){
		Intent intent = new Intent(SettingActivity.this, ItemListActivity.class);
		this.startActivity(intent);
	}
	

	private void Intent_OtherSettingActivity() {
		Intent intent = new Intent(SettingActivity.this, OtherSettingActivity.class);
		this.startActivity(intent);
	}
	
	public void Intent_CSVEditActivity(CharSequence charSequence) {
		Intent intent = new Intent(SettingActivity.this, CSVEditActivity.class);
		intent.putExtra("filename", charSequence);
		this.startActivity(intent);
	}
	
	private void Intent_ACTIONSEND(String filename) {
		String type = "mail";
		
	    boolean found = false;
	    Intent share = new Intent(Intent.ACTION_SEND);
	    share.setType("image/jpeg");

	    Uri uri;
	    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
			uri = Uri.parse("file:" + CustomerDB.filepath + filename);
		}else{
			File file = new File(CustomerDB.filepath + filename);
			uri = FileProvider.getUriForFile(this, "ts.utill.customermanager.fileprovider", file);
		}

	    // gets the list of intents that can be loaded.
	    List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share, 0);
	    if (!resInfo.isEmpty()){
	        for (ResolveInfo info : resInfo) {
	            if (info.activityInfo.packageName.toLowerCase().contains(type) || 
	                    info.activityInfo.name.toLowerCase().contains(type) ) {
	                share.putExtra(Intent.EXTRA_SUBJECT,  "고객관리자 백업");
	                share.putExtra(Intent.EXTRA_TEXT,     filename);
	                share.putExtra(Intent.EXTRA_EMAIL, new String[]{" "});  //받는사람
	                share.putExtra(Intent.EXTRA_STREAM, uri); // Optional, just if you wanna share an image.
	                share.setPackage(info.activityInfo.packageName);
	                found = true;
	                break;
	            }
	        }
	        if (!found)
	            return;

	        startActivity(Intent.createChooser(share, "Select"));
	    }
	}
	
	private String BanJumCheck(String str){
		
		String ban = "、";
		
		for (int i = 0; i < str.length(); i++) {
			if(str.substring(i, i+1).equals(",")){
				
				Log.d("TestG", "@" + str);
				
				str = str.substring(0,i) + ban + str.substring(i+1, str.length());
				
				Log.d("TestG", "#" + str);
			}
		}
		
		return str;
	}
	
	private String EnterCheck(String str) {
		String ent = "¿";
		
		for (int i = 0; i < str.length(); i++) {
			if(str.substring(i, i+1).equals("\n")){
				
				Log.d("TestG", "@" + str);
				
				str = str.substring(0,i) + ent + str.substring(i+1, str.length());
				
				Log.d("TestG", "#" + str);
			}
		}
		
		return str;
	}

	
	private String BanJumChange(String str){
		
		String ban = "、";
		
		for (int i = 0; i < str.length(); i++) {
			if(str.substring(i, i+1).equals(ban)){
				str = str.substring(0,i) + "," + str.substring(i+1, str.length());
			}
		}
		
		return str;
	}
	
	private String EnterChange(String str){
		
		Log.d("TestG", "EnterChange");
		
		String ent = "¿";
		
		for (int i = 0; i < str.length(); i++) {
			if(str.substring(i, i+1).equals(ent)){
				
				Log.d("TestG", "¿");
				
				str = str.substring(0,i) + "\n" + str.substring(i+1, str.length());
			}
		}
		
		return str;
	}



	private CharSequence[] getCSVFileList() {
		File file = new File(CustomerDB.filepath);
		File[] fileList = file.listFiles();

		if(fileList == null) fileList = new File[0];

		ArrayList<String> temp = new ArrayList<String>();
		
		for (int i = 0; i < fileList.length; i++) {
			if (!fileList[i].isDirectory()) {
				String name = fileList[i].getName();
				if(name.length() > 5) {
					if (name.substring(name.length() - 4, name.length()).equals(".csv")) {
						temp.add(fileList[i].getName());
					}
				}
			}
		}
		
		String[] str = new String[temp.size()];
		for (int i = 0; i < temp.size(); i++) {
			str[i] = temp.get(i);
		}
		return str;
	}

	private CharSequence[] getJsonFileList() {
		File file = new File(CustomerDB.filepath);
		File[] fileList = file.listFiles();

		if(fileList == null) fileList = new File[0];

		ArrayList<String> temp = new ArrayList<String>();

		for (int i = 0; i < fileList.length; i++) {
			if (!fileList[i].isDirectory()) {
				String name = fileList[i].getName();
				if(name.length() > 6){
					if(name.substring(name.length()-5, name.length()).equals(".json")){
						temp.add(fileList[i].getName());
					}
				}
			}
		}

		String[] str = new String[temp.size()];
		for (int i = 0; i < temp.size(); i++) {
			str[i] = temp.get(i);
		}
		return str;
	}

	//경로의 텍스트 파일읽기
	public String ReadTextFile(String path){
		StringBuffer strBuffer = new StringBuffer();
		try{
			InputStream is = new FileInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line="";
			while((line=reader.readLine())!=null){
				strBuffer.append(line+"\n");
			}

			reader.close();
			is.close();
		}catch (IOException e){
			e.printStackTrace();
			return "";
		}
		return strBuffer.toString();
	}


	private void JsonImport(CharSequence filename) {
		try {
			File directory = new File(CustomerDB.filepath);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			String json = ReadTextFile(CustomerDB.filepath + filename);
			Gson gson = new GsonBuilder().create();

			CustomerDBJson customerDBJson = gson.fromJson(json, CustomerDBJson.class);
			int DBVersion = customerDBJson.DATABASE_VERSION;


			int type = 0;

			switch (DBVersion) {
				case 1:
					for (Customer c : customerDBJson.CustomersList) {
						int idx_customer = c.getIdx_customer();
						String name = c.getName().toString();
						String hp = c.getHp().toString();
						int sex = c.getSexToInt();
						int age = c.getAge();
						String memo = c.getMemo().toString();
						customerDB.Insert_Customer(idx_customer, name, hp, sex, age, memo, 0);
					}

					for (Item c : customerDBJson.ItemList) {
						int idx_item = c.getIdx_item();
						String name = c.getName();
						long price = c.getPrice();
						customerDB.Insert_Item(idx_item, 0, name, price, false);
					}

					for (Customer c : customerDBJson.CustomersList) {

						for (Sale s : c.getSaleList()) {
							int idx_sale = s.getIdx_Sale();
							int idx_customer = s.getIdx_customer();
							String date = s.getDate().toString();
							String itemlist = s.getItemList().toString();
							String memo = s.getMemo().toString();
							customerDB.Insert_Sale(idx_sale, idx_customer, date, itemlist, memo);
						}
					}
					break;
				case 2:

					for (Customer c : customerDBJson.CustomersList) {
						int idx_customer = c.getIdx_customer();
						String name = c.getName().toString();
						String hp = c.getHp().toString();
						int sex = c.getSexToInt();
						int age = c.getAge();
						String memo = c.getMemo().toString();
						long point = c.getPoint();
						customerDB.Insert_Customer(idx_customer, name, hp, sex, age, memo, point);
					}

					for (Item c : customerDBJson.ItemList) {
						int idx_item = c.getIdx_item();
						int idx_group = c.getIdx_Group();
						String name = c.getName();
						long price = c.getPrice();
						boolean unvisible = c.getUnvisible();
						customerDB.Insert_Item(idx_item, idx_group, name, price, unvisible);
					}

					for (ItemGroup c : customerDBJson.ItemGroupList) {
						int idx_group = c.getIdx_Group();
						String name = c.getName();
						customerDB.Insert_ItemGroup(idx_group, name);
					}

					for (Customer c : customerDBJson.CustomersList) {

						for (Sale s : c.getSaleList()) {
							int idx_sale = s.getIdx_Sale();
							int idx_customer = s.getIdx_customer();
							Calendar date = s.getDate();
							String itemlist = s.getItemList().toString();
							String memo = s.getMemo().toString();
							customerDB.Insert_Sale(idx_sale, idx_customer, date, itemlist, memo);
						}
					}
					break;
			}

			Toast.makeText(this, "Load\n"
					+"고객 " + customerDB.getCustomersList().size() + "건 "
					+"상품 " + customerDB.getItemList().size() + "건 "
					+"분류 " + customerDB.getItemGroupList().size() + "건 "
					+"매출 " + customerDB.getAllSaleList().size() + "건이 등록되었습니다.", Toast.LENGTH_LONG).show();
			
			
		} catch (Throwable t) {
			Toast.makeText(this, "오류가 발생하여 로드하지 못했습니다. 개발자에게 문의 하세요. \n" + "Err " + t.toString(), Toast.LENGTH_LONG).show();
			Log.d("TestG", "Err " + t.toString());
		}
	}

	private void JsonImportFromText(String json) {
		try {
			Gson gson = new GsonBuilder().create();

			CustomerDBJson customerDBJson = gson.fromJson(json, CustomerDBJson.class);
			int DBVersion = customerDBJson.DATABASE_VERSION;


			int type = 0;

			switch (DBVersion) {
				case 1:
					for (Customer c : customerDBJson.CustomersList) {
						int idx_customer = c.getIdx_customer();
						String name = c.getName().toString();
						String hp = c.getHp().toString();
						int sex = c.getSexToInt();
						int age = c.getAge();
						String memo = c.getMemo().toString();
						customerDB.Insert_Customer(idx_customer, name, hp, sex, age, memo, 0);
					}

					for (Item c : customerDBJson.ItemList) {
						int idx_item = c.getIdx_item();
						String name = c.getName();
						long price = c.getPrice();
						customerDB.Insert_Item(idx_item, 0, name, price, false);
					}

					for (Customer c : customerDBJson.CustomersList) {

						for (Sale s : c.getSaleList()) {
							int idx_sale = s.getIdx_Sale();
							int idx_customer = s.getIdx_customer();
							String date = s.getDate().toString();
							String itemlist = s.getItemList().toString();
							String memo = s.getMemo().toString();
							customerDB.Insert_Sale(idx_sale, idx_customer, date, itemlist, memo);
						}
					}
					break;
				case 2:

					for (Customer c : customerDBJson.CustomersList) {
						int idx_customer = c.getIdx_customer();
						String name = c.getName().toString();
						String hp = c.getHp().toString();
						int sex = c.getSexToInt();
						int age = c.getAge();
						String memo = c.getMemo().toString();
						long point = c.getPoint();
						customerDB.Insert_Customer(idx_customer, name, hp, sex, age, memo, point);
					}

					for (Item c : customerDBJson.ItemList) {
						int idx_item = c.getIdx_item();
						int idx_group = c.getIdx_Group();
						String name = c.getName();
						long price = c.getPrice();
						boolean unvisible = c.getUnvisible();
						customerDB.Insert_Item(idx_item, idx_group, name, price, unvisible);
					}

					for (ItemGroup c : customerDBJson.ItemGroupList) {
						int idx_group = c.getIdx_Group();
						String name = c.getName();
						customerDB.Insert_ItemGroup(idx_group, name);
					}

					for (Customer c : customerDBJson.CustomersList) {

						for (Sale s : c.getSaleList()) {
							int idx_sale = s.getIdx_Sale();
							int idx_customer = s.getIdx_customer();
							Calendar date = s.getDate();
							String itemlist = s.getItemList().toString();
							String memo = s.getMemo().toString();
							customerDB.Insert_Sale(idx_sale, idx_customer, date, itemlist, memo);
						}
					}
					break;
			}

			Toast.makeText(this, "Load\n"
					+"고객 " + customerDB.getCustomersList().size() + "건 "
					+"상품 " + customerDB.getItemList().size() + "건 "
					+"분류 " + customerDB.getItemGroupList().size() + "건 "
					+"매출 " + customerDB.getAllSaleList().size() + "건이 등록되었습니다.", Toast.LENGTH_LONG).show();


		} catch (Throwable t) {
			Toast.makeText(this, "오류가 발생하여 로드하지 못했습니다. 개발자에게 문의 하세요. \n" + "Err " + t.toString(), Toast.LENGTH_LONG).show();
			Log.d("TestG", "Err " + t.toString());
		}
	}


	private void CSVImport(CharSequence filename) {
		try {
			File directory = new File(CustomerDB.filepath);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			File file = new File(CustomerDB.filepath + filename);
			file.createNewFile();
			InputStream in = new FileInputStream(file);

			byte[] b = new byte[(int) file.length()];
			int x = in.read(b);

			String[] str = new String(b).split("\n");


			String[] csvlog = str[0].split(",");

			int DBVersion;
			try {
				DBVersion = Integer.parseInt(csvlog[0]);
			} catch (Exception e) {
				DBVersion = 1;
			}

			int type = 0;

			switch (DBVersion) {
				case 1:
					for (String string : str) {

						String[] recode = string.split(",");
						if(str[0].equals(string)){
						}else if(recode[0].equals("customer")){
							type = 0;
						}else if(recode[0].equals("item")){
							type = 1;
						}else if(recode[0].equals("sale")){
							type = 2;
						}else if(!recode[0].equals("")){
							switch (type) {
								case 0:
									int idx_customer = Integer.parseInt(recode[0]);
									String name = BanJumChange(recode[1]);
									String hp = recode[2];
									int sex = Integer.parseInt(recode[3]);
									int age = Integer.parseInt(recode[4]);
									String memo;
									if (recode.length > 5) {
										memo = EnterChange(BanJumChange(recode[5]));
									}else{
										memo="";
									}

									customerDB.Insert_Customer(idx_customer, name, hp, sex, age, memo, 0);
									break;
								case 1:
									int idx_item = Integer.parseInt(recode[0]);
									name = BanJumChange(recode[1]);
									long price = Long.parseLong(recode[2]);

									customerDB.Insert_Item(idx_item, 0, name, price, false);
									break;
								case 2:
									int idx_sale = Integer.parseInt(recode[0]);
									idx_customer = Integer.parseInt(recode[1]);
									String date = recode[2];
									String itemlist;

									if (recode.length > 3) {
										itemlist = recode[3];
									}else{
										itemlist="";
									}
									if (recode.length > 4) {
										memo = EnterChange(BanJumChange(recode[4]));
									}else{
										memo="";
									}

									customerDB.Insert_Sale(idx_sale, idx_customer, date, itemlist, memo);
									break;
							}
						}
					}
					break;

				case 2:

					Log.d("TestG", "DB2");

					for (String string : str) {
						String[] recode = string.split(",");

						if(str[0].equals(string)){
						}else if(recode[0].equals("customer")){
							type = 0;
						}else if(recode[0].equals("item")){
							type = 1;
						}else if(recode[0].equals("group")){
							type = 2;
						}else if(recode[0].equals("sale")){
							type = 3;
						}else if(!recode[0].equals("") && !recode[0].equals(" ")){
							switch (type) {
								case 0:
									int idx_customer = Integer.parseInt(recode[0]);
									String name = BanJumChange(recode[1]);
									String hp = recode[2];
									int sex = Integer.parseInt(recode[3]);
									int age = Integer.parseInt(recode[4]);
									String memo = EnterChange(BanJumChange(recode[5]));
									long point = Long.parseLong(recode[6]);

									customerDB.Insert_Customer(idx_customer, name, hp, sex, age, memo, point);
									break;
								case 1:
									Log.d("TestG", "recode[0]" + recode[0]);

									int idx_item = Integer.parseInt(recode[0]);
									int idx_group = Integer.parseInt(recode[1]);
									name = BanJumChange(recode[2]);
									long price = Long.parseLong(recode[3]);
									boolean unvisible = recode[4].equals("1")?true:false;

									customerDB.Insert_Item(idx_item, idx_group, name, price, unvisible);
									break;
								case 2:
									idx_group = Integer.parseInt(recode[0]);
									name = recode[1];

									customerDB.Insert_ItemGroup(idx_group, name);
									break;
								case 3:
									int idx_sale = Integer.parseInt(recode[0]);
									idx_customer = Integer.parseInt(recode[1]);
									String date = recode[2];
									String itemlist;

									if (recode.length > 3) {
										itemlist = recode[3];
									}else{
										itemlist="";
									}
									if (recode.length > 4) {
										memo = EnterChange(BanJumChange(recode[4]));
									}else{
										memo="";
									}

									customerDB.Insert_Sale(idx_sale, idx_customer, date, itemlist, memo);
									break;
							}
						}
					}
					break;
			}

			Toast.makeText(this, "Load\n"
					+"고객 " + customerDB.getCustomersList().size() + "건 "
					+"상품 " + customerDB.getItemList().size() + "건 "
					+"분류 " + customerDB.getItemGroupList().size() + "건 "
					+"매출 " + customerDB.getAllSaleList().size() + "건이 등록되었습니다.", Toast.LENGTH_LONG).show();


		} catch (Throwable t) {
			Toast.makeText(this, "오류가 발생하여 로드하지 못했습니다. 개발자에게 문의 하세요. \n" + "Err " + t.toString(), Toast.LENGTH_LONG).show();
			Log.d("TestG", "Err " + t.toString());
		}
	}



}
