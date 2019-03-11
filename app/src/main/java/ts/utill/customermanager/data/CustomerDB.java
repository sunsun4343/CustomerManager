package ts.utill.customermanager.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import ts.utill.customermanager.CustomerListActivity;
import ts.utill.customermanager.R;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class CustomerDB {

	//to Singleton
	private static CustomerDB  customerdb = new CustomerDB();
	private CustomerDB(){	}
	public static CustomerDB getInstence(){
		return customerdb;
	}

	//to SQLite
	private static CustomerDBHelper mHelper;

	//to CSV 
	final static String itemlist_split = ";";
	final static String itemlist_split_amount = ":";
	public final static String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/customermanager/";
	//public final static String filepath = Environment.getDataDirectory().getAbsolutePath() + "/data/ts.utill.customermanager/files/customermanager/";

	//to Image
	int[] imageSex = { R.drawable.male , R.drawable.famale };
	int[] imageAge = { R.drawable.age10, R.drawable.age20, R.drawable.age30, R.drawable.age40, R.drawable.age50, R.drawable.age60 };
	int[] imageMemo = { R.drawable.nomemo, R.drawable.memo };
	int[] color = { R.color.chartcolor1, R.color.chartcolor2, R.color.chartcolor3, R.color.chartcolor4, R.color.chartcolor5, R.color.chartcolor6, R.color.chartcolor7, R.color.chartcolor8 };

	//to Customer
	ArrayList<Customer> CustomersList;

	//to Item
	ArrayList<Item> ItemList = null;
	ArrayList<ItemGroup> ItemGroupList = null;


	//to About
	private final int version = 2; //db버전


	//Method DB
	public void InitDB(Context context) {
		mHelper = CustomerDBHelper.getInstance(context);
		Loading_All_DB();
	}

	public void ConvertDB(Context context){


		mHelper = CustomerDBHelper.getInstance(context);

		SQLiteDatabase db = mHelper.getReadableDatabase();

		Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type ='table' AND name ='itemgroups'", null);
		boolean isTable = false;
		if(c.moveToFirst()){
			for(;;){
				isTable = true;
				if(!c.moveToNext())
					break;
			}
		}

		if (isTable) {
			InitDB(context);
			return;
		}

		Toast.makeText(context, "DB 업데이트를 시작합니다.", Toast.LENGTH_SHORT).show();

		AlertDialog builder = new AlertDialog.Builder(context)
				.setTitle("DB Update")
				.setMessage("Now DB Update...")
				.show();

		Toast.makeText(context, "DB를 불러옵니다.", Toast.LENGTH_SHORT).show();

		try {

			//DB Load


			//ItemList
			Cursor cursor_item = db.rawQuery("SELECT * FROM items",null);

			ItemList =  new ArrayList<Item>();
			while(cursor_item.moveToNext()){
				int idx_item = cursor_item.getInt(0);
				String name = cursor_item.getString(1);
				long price = Long.parseLong(cursor_item.getString(2));

				ItemList.add(new Item(idx_item, name, price));
			}
			cursor_item.close();

			ItemGroupList =  new ArrayList<ItemGroup>();

			//CustomerList
			Cursor cursor_customer = db.rawQuery("SELECT * FROM customer ORDER BY name",null);

			CustomersList =  new ArrayList<Customer>();
			while(cursor_customer.moveToNext()){
				int idx_customer = cursor_customer.getInt(0);
				String name = cursor_customer.getString(1);
				String hp = cursor_customer.getString(2);
				boolean sex = cursor_customer.getInt(3) == 0?false:true;
				int age = cursor_customer.getInt(4);
				String memo = cursor_customer.getString(5);

				Cursor cursor_sale = db.rawQuery("SELECT idx_sale, date, salelist, memo FROM sales WHERE idx_customer = " + idx_customer ,null);

				ArrayList<Sale> saleList =  new ArrayList<Sale>();
				while(cursor_sale.moveToNext()){
					int idx_sale = cursor_sale.getInt(0);
					String datetime = cursor_sale.getString(1);
					ArrayList<ItemSet> saleitem = ToSaleItem(cursor_sale.getString(2));
					String memo_sale = cursor_sale.getString(3);

					saleList.add(new Sale(idx_sale, idx_customer, getDateTime(datetime), saleitem, memo_sale));
				}

				cursor_sale.close();

				//sort sale
				Object[] Obj_date = saleList.toArray();
				Arrays.sort(Obj_date, new DataComparator_SaleDate());
				ArrayList<Sale> arr_date = new ArrayList<Sale>();
				for (Object o : Obj_date) {
					arr_date.add((Sale) o);
				}
				saleList = arr_date;

				CustomersList.add(new Customer(idx_customer, name, hp, sex, age, memo, saleList));
			}

			cursor_customer.close();

		} catch (Exception e) {
			Toast.makeText(context, "Err DB Load " + " " + e, Toast.LENGTH_LONG).show();
			return;
		}


		//DB Ver 1 BackUp

		try {
			Toast.makeText(context, "불러온 DB를  CSV파일로 백업합니다.", Toast.LENGTH_SHORT).show();
			CSVExport(context, "AUTO-LowVersion1");
		} catch (Exception e) {
			Toast.makeText(context, "Err DB CSV BackUp " + " " + e, Toast.LENGTH_LONG).show();
			return;
		}

		db = mHelper.getWritableDatabase();

		//DB Reset
		db.execSQL("DROP TABLE IF EXISTS customer");
		db.execSQL("DROP TABLE IF EXISTS items");
		db.execSQL("DROP TABLE IF EXISTS sales");
		mHelper.onCreate(db);

		//Convert Data

		Toast.makeText(context, "DB를 변환합니다.", Toast.LENGTH_SHORT).show();

		//item
		for (Item item : ItemList) {
			DB_Insert_Item(item.getIdx_item(),item.getIdx_Group(), item.getName(), item.getPrice(), item.getUnvisible());
		}

		ItemGroupList =  new ArrayList<ItemGroup>();

		//customer
		for (Customer customer : CustomersList) {

			DB_Insert_Customer(customer.getIdx_customer(), customer.getName().toString(), customer.getHp().toString(), customer.getSexToInt(), customer.getAge(), customer.getMemo().toString(), customer.getPoint());

			for (Sale sale : customer.getSaleList()) {
				DB_Insert_Sale(sale.getIdx_Sale(), sale.getIdx_customer(), sale.getDate(), sale.getItemList(), sale.getMemo());
			}

		}

		builder.setMessage("DB 변환 완료");

		Toast.makeText(context, "DB업데이트가 완료되었습니다.", Toast.LENGTH_SHORT).show();
	}

	public void CSVExport(Context context, String filename) {
		try {
			//OutputStreamWriter out = new OutputStreamWriter(openFileOutput(filename +  ".csv", 0));
			File directory = new File(CustomerDB.filepath);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			File fileCacheItem = new File(CustomerDB.filepath + filename + ".csv");
			fileCacheItem.createNewFile();

			SimpleDateFormat dateForm = new SimpleDateFormat("yyMMdd");
			Calendar calendar = Calendar.getInstance();


			OutputStream out = new FileOutputStream(fileCacheItem);

			StringBuffer buf = new StringBuffer();
			buf.append(CustomerDBHelper.DATABASE_VERSION);
			buf.append(",");
			buf.append(dateForm.format(calendar.getTime()));
			buf.append(",");
			buf.append(CustomerListActivity.VERSION_CODE);
			buf.append(",,,,");
			buf.append("\n");
			buf.append("customer,,,,,,");
			buf.append("\n");
			ArrayList<Customer> customerlist = getCustomersList();
			for (Customer customer : customerlist) {
				StringBuffer buf_customer = new StringBuffer();
				buf_customer.append(customer.getIdx_customer());
				buf_customer.append(",");
				buf_customer.append(BanJumCheck(customer.getName().toString()));
				buf_customer.append(",");
				buf_customer.append(customer.getHp());
				buf_customer.append(",");
				buf_customer.append(customer.getSexToInt());
				buf_customer.append(",");
				buf_customer.append(customer.getAge());
				buf_customer.append(",");
				buf_customer.append(EnterCheck(BanJumCheck(customer.getMemo().toString())));
				buf_customer.append(",");
				buf_customer.append(customer.getPoint());


				buf.append(buf_customer);
				buf.append("\n");
			}

			buf.append("item,,,,,,");
			buf.append("\n");
			ArrayList<Item> itemlist = getItemList();
			for (Item item : itemlist) {
				StringBuffer buf_item = new StringBuffer();
				buf_item.append(item.getIdx_item());
				buf_item.append(",");
				buf_item.append(item.getIdx_Group());
				buf_item.append(",");
				buf_item.append(BanJumCheck(item.getName().toString()));
				buf_item.append(",");
				buf_item.append(item.getPrice());
				buf_item.append(",");
				buf_item.append(item.getUnVisibleToInt());
				buf_item.append(",");
				buf_item.append(",");

				buf.append(buf_item);
				buf.append("\n");
			}

			buf.append("group,,,,,,");
			buf.append("\n");
			ArrayList<ItemGroup> grouplist = getItemGroupList();
			if(grouplist != null){
				for (ItemGroup group : grouplist) {
					StringBuffer buf_item = new StringBuffer();
					buf_item.append(group.getIdx_Group());
					buf_item.append(",");
					buf_item.append(BanJumCheck(group.getName().toString()));
					buf_item.append(",");
					buf_item.append(",");
					buf_item.append(",");
					buf_item.append(",");
					buf_item.append(",");

					buf.append(buf_item);
					buf.append("\n");
				}
			}

			buf.append("sale,,,,,,");
			buf.append("\n");
			ArrayList<Sale> salelist = getAllSaleList();
			for (Sale sale : salelist) {
				StringBuffer buf_sale = new StringBuffer();
				buf_sale.append(sale.getIdx_Sale());
				buf_sale.append(",");
				buf_sale.append(sale.getIdx_customer());
				buf_sale.append(",");
				buf_sale.append(ToDateTime(sale.getDate()));
				buf_sale.append(",");
				buf_sale.append(sale.getItemList());
				buf_sale.append(",");
				buf_sale.append(EnterCheck(BanJumCheck(sale.getMemo().toString())));
				buf_sale.append(",");
				buf_sale.append(",");

				buf.append(buf_sale);
				buf.append("\n");
			}

			out.write(buf.toString().getBytes());
			out.close();



			Toast.makeText(context, "Save " + CustomerDB.filepath + filename + ".csv\n"
					+"고객 " + customerlist.size() + "건 "
					+"상품 " + itemlist.size() + "건 "
					+"분류" + grouplist.size() + "건 "
					+"매출 " + salelist.size() + "건이 백업되었습니다.", Toast.LENGTH_LONG).show();


		} catch (Throwable t) {
			Toast.makeText(context, "오류가 발생하여 추출하지 못했습니다. 개발자에게 문의 하세요. \n" + "Err " + t.toString(), Toast.LENGTH_LONG).show();
			Log.d("TestG", "Err " + t.toString());
		}
	}

	public  void JsonExport(Context context, String filename){
		try {
			//OutputStreamWriter out = new OutputStreamWriter(openFileOutput(filename +  ".csv", 0));
			File directory = new File(CustomerDB.filepath);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			CustomerDBJson customerDBJson = new CustomerDBJson();
			customerDBJson.DATABASE_VERSION = CustomerDBHelper.DATABASE_VERSION;
			customerDBJson.CustomersList = CustomersList;
			customerDBJson.ItemList = ItemList;
			customerDBJson.ItemGroupList = ItemGroupList;

			Gson gson = new GsonBuilder().create();

			String json = gson.toJson(customerDBJson);

			File fileCacheItem = new File(CustomerDB.filepath + filename + ".json");
			fileCacheItem.createNewFile();

			SimpleDateFormat dateForm = new SimpleDateFormat("yyMMdd");
			Calendar calendar = Calendar.getInstance();

			OutputStream out = new FileOutputStream(fileCacheItem);

			StringBuffer buf = new StringBuffer();
			out.write(json.getBytes());
			out.close();

			Toast.makeText(context, "Save " + CustomerDB.filepath + filename + ".json\n"
					+"이 백업되었습니다.", Toast.LENGTH_LONG).show();


		} catch (Throwable t) {
			Toast.makeText(context, "오류가 발생하여 추출하지 못했습니다. 개발자에게 문의 하세요. \n" + "Err " + t.toString(), Toast.LENGTH_LONG).show();
			Log.d("TestG", "Err " + t.toString());
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

	private void Loading_All_DB() {

		SQLiteDatabase db = mHelper.getReadableDatabase();

		//ItemList
		Cursor cursor_item = db.rawQuery("SELECT * FROM items",null);

		ItemList =  new ArrayList<Item>();

		while(cursor_item.moveToNext()){
			int idx_item = cursor_item.getInt(0);
			int idx_group = cursor_item.getInt(1); //ver.3
			String name = cursor_item.getString(2);
			long price = Long.parseLong(cursor_item.getString(3));
			boolean unvisible = cursor_item.getInt(4) == 0?false:true; //ver.3

			ItemList.add(new Item(idx_item, idx_group, name, price, unvisible));

		}

		cursor_item.close();

		//ItemGroupList
		Cursor cursor_itemgroup = db.rawQuery("SELECT * FROM itemgroups",null);

		ItemGroupList =  new ArrayList<ItemGroup>();

		while(cursor_itemgroup.moveToNext()){
			int idx_group = cursor_itemgroup.getInt(0);
			String name = cursor_itemgroup.getString(1);

			ItemGroupList.add(new ItemGroup(idx_group, name));
		}
		cursor_itemgroup.close();

		//CustomerList
		Cursor cursor_customer = db.rawQuery("SELECT * FROM customer ORDER BY name",null);

		CustomersList =  new ArrayList<Customer>();

		while(cursor_customer.moveToNext()){
			int idx_customer = cursor_customer.getInt(0);
			String name = cursor_customer.getString(1);
			String hp = cursor_customer.getString(2);
			boolean sex = cursor_customer.getInt(3) == 0?false:true;
			int age = cursor_customer.getInt(4);
			String memo = cursor_customer.getString(5);
			long point = Long.parseLong(cursor_customer.getString(6));

			Cursor cursor_sale = db.rawQuery("SELECT idx_sale, date, salelist, memo FROM sales WHERE idx_customer = " + idx_customer ,null);

			ArrayList<Sale> saleList =  new ArrayList<Sale>();


			while(cursor_sale.moveToNext()){
				int idx_sale = cursor_sale.getInt(0);
				String datetime = cursor_sale.getString(1);
				ArrayList<ItemSet> saleitem = ToSaleItem(cursor_sale.getString(2));
				String memo_sale = cursor_sale.getString(3);

				saleList.add(new Sale(idx_sale, idx_customer, getDateTime(datetime), saleitem, memo_sale));
			}

			cursor_sale.close();

			//sort sale
			Object[] Obj_date = saleList.toArray();
			Arrays.sort(Obj_date, new DataComparator_SaleDate());
			ArrayList<Sale> arr_date = new ArrayList<Sale>();
			for (Object o : Obj_date) {
				arr_date.add((Sale) o);
			}
			saleList = arr_date;

			CustomersList.add(new Customer(idx_customer, name, hp, sex, age, memo, point, saleList));
		}

		cursor_customer.close();

	}

	//Method Tool
	public Calendar getDateTime(String datetime){
		String[] str = datetime.split(" ");
		String datestr = str[0];
		String timestr = str[1];
		String[] date = datestr.split("-");
		String[] time = timestr.split(":");

		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));

		return calendar;
	}

	public Calendar getDateTimeM(String datetime){
		String[] str = datetime.split(" ");
		String datestr = str[0];
		String timestr = str[1];
		String[] date = datestr.split("-");
		String[] time = timestr.split(":");

		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(date[0]), Integer.parseInt(date[1])-1, Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));

		return calendar;
	}

	public ArrayList<ItemSet> ToSaleItem(String str) {
		String[] idx_items = str.split(itemlist_split);

		ArrayList<ItemSet> saleitem = new ArrayList<ItemSet>();

		for (String idx_item : idx_items) {

			if(!idx_item.equals("")){
				String[] itemset = idx_item.split(":");

				if(itemset.length > 1){
					saleitem.add(new ItemSet(getItem(Integer.parseInt(itemset[0])), Integer.parseInt(itemset[1])));
				}else{
					saleitem.add(new ItemSet(getItem(Integer.parseInt(idx_item)), 1));
				}
			}
		}

		return saleitem;

	}

	public static String toThousand(long l){
		DecimalFormat df = new DecimalFormat("#,##0");
		return df.format(l);
	}

	public int getDDay(Calendar date) {
		//Calendar calendar = Calendar.getInstance();

		Calendar cal1 = new GregorianCalendar();
		//cal1.setTime(calendar.getTime());

		long dday = cal1.getTimeInMillis() - date.getTimeInMillis();
		dday= dday/(24*60*60*1000);

		return (int) dday;
	}

	public String ToDateTime(Calendar c) {
		return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " "
				+ c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
	}

	public String ToDate(Calendar c) {
		return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DAY_OF_MONTH);
	}

	private String ToDateTimeDB(Calendar c) {
		return c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH) + " "
				+ c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
	}

	public String ToTrimHp(String hp) {
		String[] hps = hp.split("-");
		StringBuffer newhp= new StringBuffer();
		for (String h : hps) {
			newhp.append(h);
		}
		return newhp.toString();
	}

	public String getNow(){
		Calendar calendar = new GregorianCalendar();

		StringBuffer value = new StringBuffer();

		value.append(calendar.get(Calendar.YEAR));

		if(calendar.get(Calendar.MONTH) <10){
			value.append("0"+calendar.get(Calendar.MONTH));
		}else{
			value.append(calendar.get(Calendar.MONTH));
		}

		if(calendar.get(Calendar.DAY_OF_MONTH) <10){
			value.append("0"+calendar.get(Calendar.DAY_OF_MONTH));
		}else{
			value.append(calendar.get(Calendar.DAY_OF_MONTH));
		}

		return value.toString();
	}

	//Get and Set
	public ArrayList<Customer> getCustomersList() {
		return CustomersList;
	}

	public ArrayList<Customer> getView_CustomersList() {
		return (ArrayList<Customer>) CustomersList.clone();
	}

	public ArrayList<Item> getItemList() {
		return this.ItemList;
	}

	public ArrayList<ItemGroup> getItemGroupList(){
		return ItemGroupList;
	}

	public int getImageSex(int index){
		return this.imageSex[index];
	}

	public int getImageAge(int index){
		return this.imageAge[index];
	}

	public int getImageMemo(int index) {
		return this.imageMemo[index];
	}

	public int getColor(int index){
		return this.color[index];
	}

	public int getCustomerCnt() {
		if (CustomersList == null) {
			return 0;
		}
		return CustomersList.size();
	}

	public int getVersion() {
		return this.version ;
	}


	public void setCustomersList(ArrayList<Customer> CustomersList)
	{
		this.CustomersList = CustomersList;
	}

	public void setItemList(ArrayList<Item> ItemList)
	{
		this.ItemList = ItemList;
	}

	public void setItemGroupList(ArrayList<ItemGroup> ItemGroupList)
	{
		this.ItemGroupList = ItemGroupList;
	}

	//find Get
	public Customer getCustomer(int idx_customer){
		for (Customer c : CustomersList) {
			if(c.getIdx_customer() == idx_customer){
				return c;
			}
		}
		return null;
	}

	public Item getItem(int idx_item) {
		for (Item item : ItemList) {
			if(item.getIdx_item() == idx_item){
				return item;
			}
		}
		return null;
	}

	public Sale getSale(int idx_customer, int idx_sale) {
		Customer customer = getCustomer(idx_customer);
		for (Sale sale : customer.getSaleList()) {
			if(sale.getIdx_Sale() == idx_sale){
				return sale;
			}
		}
		return null;
	}

	public ArrayList<Sale> getAllSaleList() {
		SQLiteDatabase db = mHelper.getReadableDatabase();

		Cursor cursor_sale = db.rawQuery("SELECT * FROM sales",null);
		ArrayList<Sale> saleList =  new ArrayList<Sale>();

		while(cursor_sale.moveToNext()){
			int idx_sale = cursor_sale.getInt(0);

			int idx_customer = cursor_sale.getInt(1);
			String datetime = cursor_sale.getString(2);
			ArrayList<ItemSet> saleitem = ToSaleItem(cursor_sale.getString(3));
			String memo_sale = cursor_sale.getString(4);

			saleList.add(new Sale(idx_sale, idx_customer, getDateTime(datetime), saleitem, memo_sale));
		}

		cursor_sale.close();

		return saleList;
	}

	public Customer getCutomerData(Context context, String incomNumber) {
		mHelper = CustomerDBHelper.getInstance(context);
		SQLiteDatabase db = mHelper.getReadableDatabase();

		Cursor cursor_customer = db.rawQuery("SELECT idx_customer, name, sex, age, memo FROM customer WHERE hp = '" + incomNumber + "'",null);

		Customer customer = null;
		while(cursor_customer.moveToNext()){
			int idx_customer = cursor_customer.getInt(0);
			String name = cursor_customer.getString(1);
			int sex = cursor_customer.getInt(2);
			int age = cursor_customer.getInt(3);
			String memo = cursor_customer.getString(4);

			ArrayList<Sale> salelist = new ArrayList<Sale>();
			Cursor cursor_sale = db.rawQuery("SELECT idx_sale, date, salelist, memo FROM sales WHERE idx_customer = " + idx_customer ,null);
			while(cursor_sale.moveToNext()){
				int idx_sale = cursor_sale.getInt(0);
				String datetime = cursor_sale.getString(1);
				ArrayList<ItemSet> saleitem = ToSaleItem(cursor_sale.getString(2));
				String memo_sale = cursor_sale.getString(3);

				salelist.add(new Sale(idx_sale, idx_customer, getDateTime(datetime), saleitem, memo_sale));
			}

			customer =  new Customer(idx_customer, name, incomNumber, sex==1?true:false, age, memo, salelist);
		}

		cursor_customer.close();

		return customer;
	}

	//DB Customer
	public void Insert_Customer(String name, String hp, boolean sex, int age, String memo, long point, ArrayList<Sale> salelist) {
		//idx_customer
		int idx_customer = DB_Select_Next_Idx_Customer();

		//DB
		DB_Insert_Customer(idx_customer, name, hp, sex?1:0, age, memo, point);
		for (Sale sale : salelist) {
			int idx_sale = DB_Select_Next_Idx_Sale();
			DB_Insert_Sale(idx_sale, idx_customer, sale.getDate(), sale.getItemList(), sale.getMemo());
			sale.setIdx_Sale(idx_sale);
		}

		//Memory
		CustomersList.add(new Customer(idx_customer, name, hp, sex, age, memo, salelist));
	}

	public void Insert_Customer(int idx_customer, String name, String hp, int sex, int age, String memo, long point) {
		//DB
		DB_Insert_Customer(idx_customer, name, hp, sex, age, memo, point);

		ArrayList<Sale> salelist = new ArrayList<Sale>();

		//Memory
		CustomersList.add(new Customer(idx_customer, name, hp, sex==1?true:false, age, memo, point, salelist));
	}

	private void DB_Insert_Customer(int idx_customer, String name, String hp, int sex, int age, String memo, long point) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		ContentValues row = new ContentValues();

		row.put("idx_customer",idx_customer);
		row.put("name",name);
		row.put("hp",hp);
		row.put("sex",sex);
		row.put("age",age);
		row.put("memo",memo);
		row.put("point", point);
		db.insert("customer",null,row);

		//mHelper.close();
	}

	private int DB_Select_Next_Idx_Customer() {
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor_customer = db.rawQuery("SELECT MAX(idx_customer) FROM customer",null);

		while(cursor_customer.moveToNext()){
			return cursor_customer.getInt(0) + 1;
		}

		cursor_customer.close();
		return 0;
	}

	public void Update_Customer(Customer customer, Customer view_customer) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		ContentValues row;

		int idx_customer = customer.getIdx_customer();
		if(!view_customer.getName().toString().equals(customer.getName().toString())){
			row = new ContentValues();
			row.put("name", view_customer.getName().toString());
			db.update("customer", row, "idx_customer =" + idx_customer, null);

			customer.setName(view_customer.getName().toString());
		}
		if(!view_customer.getHp().toString().equals(customer.getHp().toString())){
			row = new ContentValues();
			row.put("hp", view_customer.getHp().toString());
			db.update("customer", row, "idx_customer =" + idx_customer, null);

			customer.setHp(view_customer.getHp().toString());
		}
		if(view_customer.getSex() != customer.getSex()){
			row = new ContentValues();
			row.put("sex", view_customer.getSexToInt());
			db.update("customer", row, "idx_customer =" + idx_customer, null);

			customer.setSex(view_customer.getSex());
		}
		if(view_customer.getAge() != customer.getAge()){
			row = new ContentValues();
			row.put("age", view_customer.getAge());
			db.update("customer", row, "idx_customer =" + idx_customer, null);

			customer.setAge(view_customer.getAge());
		}
		if(!view_customer.getMemo().toString().equals(customer.getMemo().toString())){
			row = new ContentValues();
			row.put("memo", view_customer.getMemo().toString());
			db.update("customer", row, "idx_customer =" + idx_customer, null);

			customer.setMemo(view_customer.getMemo().toString());
		}
	}

	public void Delete_Customer(Customer customer) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		db.delete("sales", "idx_customer = " + customer.getIdx_customer(), null);
		db.delete("customer", "idx_customer = " + customer.getIdx_customer(), null);

		//photo
		deletePhoto(customer.getIdx_customer());

		CustomersList.remove(customer);
	}

	//DB Sale
	private void DB_Insert_Sale(int idx_sale, int idx_customer, Calendar calendar, CharSequence itemList, CharSequence memo) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		ContentValues row = new ContentValues();

		row.put("idx_sale",idx_sale);
		row.put("idx_customer",idx_customer);
		row.put("date",ToDateTimeDB(calendar));
		row.put("salelist",itemList.toString());
		row.put("memo",memo.toString());
		db.insert("sales",null,row);

		//mHelper.close();
	}

	private int DB_Select_Next_Idx_Sale() {
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor_customer = db.rawQuery("SELECT MAX(idx_sale) FROM sales",null);

		while(cursor_customer.moveToNext()){
			return cursor_customer.getInt(0) + 1;
		}

		cursor_customer.close();
		return 0;
	}

	public void Insert_Sale(int idx_customer, Calendar date, ArrayList<ItemSet> itemList, String memo) {
		//idx_sale
		int idx_sale = DB_Select_Next_Idx_Sale();

		//DB
		DB_Insert_Sale(idx_sale, idx_customer, date, Sale.getItemList(itemList), memo);

		//Memory
		getCustomer(idx_customer).getSaleList().add(new Sale(idx_sale,idx_customer,date,itemList,memo));
	}

	public void Insert_Sale(int idx_sale, int idx_customer, String date, String itemlist, String memo) {
		//DB
		DB_Insert_Sale(idx_sale, idx_customer, getDateTimeM(date), itemlist, memo);

		//Memory
		getCustomer(idx_customer).getSaleList().add(new Sale(idx_sale,idx_customer,getDateTimeM(date),ToSaleItem(itemlist),memo));
	}

	public void Insert_Sale(int idx_sale, int idx_customer, Calendar date, String itemlist, String memo) {
		//DB
		DB_Insert_Sale(idx_sale, idx_customer, date, itemlist, memo);

		//Memory
		getCustomer(idx_customer).getSaleList().add(new Sale(idx_sale,idx_customer,date,ToSaleItem(itemlist),memo));
	}

	public void Update_Sale(Sale sale, Sale view_sale) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		ContentValues row;

		int idx_sale = sale.getIdx_Sale();
		if(!ToDateTime(view_sale.getDate()).equals(ToDateTime(sale.getDate()))){
			row = new ContentValues();
			row.put("date", ToDateTimeDB(view_sale.getDate()));
			db.update("sales", row, "idx_sale =" + idx_sale, null);

			sale.setDate(view_sale.getDate());
		}
		if(!view_sale.getMemo().equals(sale.getMemo())){
			row = new ContentValues();
			row.put("memo", view_sale.getMemo().toString());
			db.update("sales", row, "idx_sale =" + idx_sale, null);

			sale.setMemo(view_sale.getMemo().toString());
		}
		if(!view_sale.getItemListName().equals(sale.getItemList())){
			row = new ContentValues();
			row.put("salelist", view_sale.getItemList().toString());
			db.update("sales", row, "idx_sale =" + idx_sale, null);

			sale.setItemList(view_sale.getSaleItem());
		}
	}

	public void Delete_Sale(Sale sale, int idx_customer) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		db.delete("sales", "idx_sale = " + sale.getIdx_Sale(), null);

		getCustomer(idx_customer).getSaleList().remove(sale);
	}


	//DB Item

	public void Insert_Item(int idx_group, String name, long price, boolean unvisible) {
		//idx_item
		int idx_item = DB_Select_Next_Idx_Item();

		//DB
		DB_Insert_Item(idx_item, idx_group, name, price, unvisible);

		//Memory
		ItemList.add(new Item(idx_item, idx_group, name, price, unvisible));
	}

	private void DB_Insert_Item(int idx_item, int idx_group, String name, long price, boolean bool) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		ContentValues row = new ContentValues();

		int unvisible = bool ? 1:0;

		row.put("idx_item",idx_item);
		row.put("idx_group", idx_group);
		row.put("name",name);
		row.put("price",price);
		row.put("unvisible", unvisible);
		db.insert("items",null,row);
	}

	public void Insert_Item(int idx_item, int idx_group, String name, long price, boolean unvisible) {

		//DB
		DB_Insert_Item(idx_item, idx_group, name, price, unvisible);

		//Memory
		ItemList.add(new Item(idx_item, name, price));
	}

	private int DB_Select_Next_Idx_Item() {
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor_customer = db.rawQuery("SELECT MAX(idx_item) FROM items",null);

		while(cursor_customer.moveToNext()){
			return cursor_customer.getInt(0) + 1;
		}

		cursor_customer.close();
		return 0;
	}

	public void Update_Item(Item item, Item view_item) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		ContentValues row;

		int idx_item = item.getIdx_item();
		if(!view_item.getName().equals(item.getName())){
			row = new ContentValues();
			row.put("name", view_item.getName());
			db.update("items", row, "idx_item =" + idx_item, null);

			item.setName(view_item.getName());
		}
		if(view_item.getIdx_Group() != item.getIdx_Group()){
			row = new ContentValues();
			row.put("idx_group", view_item.getIdx_Group());
			db.update("items", row, "idx_item =" + idx_item, null);

			item.setIdx_Group(view_item.getIdx_Group());
		}
		if(view_item.getPrice() != item.getPrice()){
			row = new ContentValues();
			row.put("price", view_item.getPrice());
			db.update("items", row, "idx_item =" + idx_item, null);

			item.setPrice(view_item.getPrice());
		}
		if(view_item.getUnvisible() != item.getUnvisible()){
			row = new ContentValues();
			row.put("unvisible", view_item.getUnvisible()?1:0);
			db.update("items", row, "idx_item =" + idx_item, null);

			item.setUnvisible(view_item.getUnvisible());
		}

	}

	//DB ItemGroup
	public void Insert_ItemGroup(String name) {
		//idx_group
		int idx_group = DB_Select_Next_Idx_Group();

		//DB
		DB_Insert_ItemGroup(idx_group, name);

		//Memory
		ItemGroupList.add(new ItemGroup(idx_group, name));
	}

	public void Insert_ItemGroup(int idx_group, String name) {
		//DB
		DB_Insert_ItemGroup(idx_group, name);

		//Memory
		ItemGroupList.add(new ItemGroup(idx_group, name));
	}

	private void DB_Insert_ItemGroup(int idx_group, String name) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		ContentValues row = new ContentValues();

		row.put("idx_group",idx_group);
		row.put("name",name);
		db.insert("itemgroups",null,row);

		//mHelper.close();
	}

	private int DB_Select_Next_Idx_Group() {
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT MAX(idx_group) FROM itemgroups",null);

		while(cursor.moveToNext()){
			return cursor.getInt(0) + 1;
		}

		cursor.close();
		return 1;
	}

	public void Update_ItemGroup(ItemGroup group, ItemGroup view_group) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		ContentValues row;

		int idx_group = group.getIdx_Group();
		if(!view_group.getName().equals(group.getName())){
			row = new ContentValues();
			row.put("name", view_group.getName());
			db.update("itemgroups", row, "idx_group =" + idx_group, null);

			group.setName(view_group.getName());
		}
	}

	public void Delete_ItemGroup(ItemGroup group) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		db.delete("itemgroups", "idx_group = " + group.getIdx_Group(), null);

		ItemGroupList.remove(group);
	}


	//DB File
	private void deletePhoto(int idx_customer) {
		File file = new File(filepath + idx_customer + ".jpg");
		file.delete();
		file = new File(filepath + idx_customer + "_high.jpg");
		file.delete();
		deleteFolder(filepath + "gallery" + idx_customer);
	}

	public void deleteFolder(String parentPath) {
		File file = new File(parentPath);
		String[] fnameList = file.list();

		if(fnameList != null){
			int fCnt = fnameList.length;
			String childPath = "";

			for(int i = 0; i < fCnt; i++) {
				childPath = parentPath+"/"+fnameList[i];
				File f = new File(childPath);
				if( ! f.isDirectory()) {
					f.delete();   //파일이면 바로 삭제
				}
				else {
					deleteFolder(childPath);
				}
			}

			File f = new File(parentPath);
			f.delete();   //폴더는 맨 나중에 삭제
		}


	}

	public void ResetDB() {
		CustomersList = new ArrayList<Customer>();
		ItemList = new ArrayList<Item>();
		ItemGroupList = new ArrayList<ItemGroup>();

		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.delete("customer", null, null);
		db.delete("items", null, null);
		db.delete("itemgroups", null, null);
		db.delete("sales", null, null);
		db.delete("schedule", null, null);
	}

	public void ResetPhoto() {
		for (Customer c : CustomersList) {
			deletePhoto(c.getIdx_customer());
		}
	}
	public ItemGroup getGroup(int idx_group) {

		for (ItemGroup gr : ItemGroupList) {
			if (gr.idx_group == idx_group) {
				return gr;
			}
		}

		return null;
	}















}
