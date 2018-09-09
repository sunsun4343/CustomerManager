package ts.utill.customermanager.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import ts.utill.customermanager.CustomerListActivity;
import ts.utill.customermanager.R;
import ts.utill.customermanager.data.Customer;
import ts.utill.customermanager.data.CustomerDB;
import ts.utill.customermanager.data.DataComparator_Amount;
import ts.utill.customermanager.data.DataComparator_Date;
import ts.utill.customermanager.data.DataComparator_Name;
import ts.utill.customermanager.data.DataComparator_Vist;
import ts.utill.customermanager.data.UserSetting;

public class CustomerListAdapter extends BaseAdapter {

	Context maincon;
	CustomerListActivity customerListActivity;
	LayoutInflater Inflater;
	CustomerDB customerDB;
	ArrayList<Customer> arSrc;
	
	UserSetting userSetting = UserSetting.getInstance();
	
	public CustomerListAdapter(Context context) {
		this.maincon = context;
		this.customerListActivity  = (CustomerListActivity) context;
		customerDB = CustomerDB.getInstence();
		this.arSrc = customerDB.getView_CustomersList();
		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if(arSrc == null){
			return 0;
		}
		return arSrc.size();
	}

	@Override
	public Customer getItem(int position) {
		return arSrc.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getViewTypeCount(){
		return 1;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			int res =0;
			res = ts.utill.customermanager.R.layout.customerlist_customerview;
			convertView = Inflater.inflate(res, parent, false);
		}
		
		//findView
		RelativeLayout layout_customerList_customer = (RelativeLayout)convertView.findViewById(R.id.layout_customerList_customer);
		
		ImageView imageView_customerList_face = (ImageView)convertView.findViewById(R.id.imageView_customerList_face);
		ImageView imageView_customerList_sex = (ImageView)convertView.findViewById(R.id.imageView_customerList_sex);
		ImageView imageView_customerList_age = (ImageView)convertView.findViewById(R.id.imageView_customerList_age);
		
		TextView textView_customerList_name = (TextView)convertView.findViewById(R.id.textView_customerList_name);
		TextView textView_customerList_hp = (TextView)convertView.findViewById(R.id.textView_customerList_hp);
		TextView textView_customerList_vistCnt = (TextView)convertView.findViewById(R.id.textView_customerList_vistCnt);
		TextView textView_customerList_vistDate = (TextView)convertView.findViewById(R.id.textView_customerList_vistDate);
		TextView textView_customerList_amount = (TextView)convertView.findViewById(R.id.textView_customerList_amount);
		TextView textView_customerList_dday = (TextView)convertView.findViewById(R.id.textView_customerList_dday);
		
		//setContent
		
		String fileChk = CustomerDB.filepath + arSrc.get(position).getIdx_customer() + ".jpg";
		File file = new File(fileChk);
		if (file.exists()) {
			imageView_customerList_face.setImageBitmap(loadPicture( arSrc.get(position).getIdx_customer()));
		}else{
			imageView_customerList_face.setImageResource(R.drawable.nopace);
		}
		
		imageView_customerList_sex.setImageResource(customerDB.getImageSex(arSrc.get(position).getSexToInt()));
		if (userSetting._AgeNogView) {
			imageView_customerList_age.setVisibility(View.GONE);
		}else
		{
			imageView_customerList_age.setVisibility(View.VISIBLE);
			imageView_customerList_age.setImageResource(customerDB.getImageAge(arSrc.get(position).getAge()));
		}
		
		
		
		textView_customerList_name.setText(arSrc.get(position).getName());
		textView_customerList_hp.setText(arSrc.get(position).getHpView());
		textView_customerList_vistCnt.setText(arSrc.get(position).getVistCnt() + "");
		if(arSrc.get(position).getLastVistDate() == null){
			textView_customerList_vistDate.setText("no data");
			textView_customerList_dday.setText("");
		}else{
			textView_customerList_vistDate.setText(customerDB.ToDate(arSrc.get(position).getLastVistDate()));
			textView_customerList_dday.setText(customerDB.getDDay(arSrc.get(position).getLastVistDate()) + "����");
		}
		textView_customerList_amount.setText(customerDB.toThousand(arSrc.get(position).getAmount()));
		
		//setOnListener
		layout_customerList_customer.setOnClickListener(new RelativeLayout.OnClickListener(){
			public void onClick(View v){
				customerListActivity.Intent_CustomerViewActivity(arSrc.get(position).getIdx_customer());
			}
		});

		return convertView;
	}

	private Bitmap loadPicture(int idx_customer) {
  		File file = new File(CustomerDB.filepath + idx_customer + ".jpg");
  		BitmapFactory.Options option = new BitmapFactory.Options();
  		option.inSampleSize = 2;
  		option.inScaled = true;
  		option.inPurgeable = true;
  		return BitmapFactory.decodeFile(file.getAbsolutePath(), option);
	}

	public void Search(String string) {
		if(string.equals("")){
			arSrc = customerDB.getView_CustomersList();
		}else{
			arSrc = customerDB.getView_CustomersList();
			ArrayList<Customer> temp = new ArrayList<Customer>();
			for (Customer customer : arSrc) {
				
				boolean isfind = false;
				
				String name = (String) customer.getName();
				for (int i = 0; i < customer.getName().length()-string.length()+1; i++) {
					if(string.equals(name.substring(i, i+string.length()))){
						temp.add(customer);
						isfind = true;
						break;
					}
				}
				if(!isfind){
					String phone = customer.getHp().toString();
					for (int i = 0; i < phone.length()-string.length()+1; i++) {
						if(string.equals(phone.substring(i, i+string.length()))){
							temp.add(customer);
							break;
						}
					}					
				}
			}
			arSrc = temp;
		}
		this.notifyDataSetChanged();
	}

	public void Sort(int position) {
		switch (position) {
		case 0:
			//name
			//arSrc = customerDB.getView_CustomersList();
			Object[] Obj_name = customerDB.getCustomersList().toArray();
			Arrays.sort(Obj_name, new DataComparator_Name());
			
			ArrayList<Customer> arr_name = new ArrayList<Customer>();
			for (Object o : Obj_name) {
				arr_name.add((Customer) o);
			}
			
			arSrc = arr_name;
			break;
		case 1:
			//vist
			Object[] oa = customerDB.getCustomersList().toArray();
			Arrays.sort(oa, new DataComparator_Vist());
			
			ArrayList<Customer> arr = new ArrayList<Customer>();
			for (Object o : oa) {
				arr.add((Customer) o);
			}
			
			arSrc = arr;
			break;
		case 2:
			//date
			ArrayList<Customer> sortArr = new ArrayList<Customer>();
			for (Customer customer : customerDB.getCustomersList()) {
				if (customer.getLastVistDate() != null) {
					sortArr.add(customer);
				}
			}
			
			Object[] Obj_date = sortArr.toArray();
			Arrays.sort(Obj_date, new DataComparator_Date());
			
			ArrayList<Customer> arr_date = new ArrayList<Customer>();
			for (Object o : Obj_date) {
				arr_date.add((Customer) o);
			}
			for (Customer customer : customerDB.getCustomersList()) {
				if (customer.getLastVistDate() == null) {
					arr_date.add(customer);
				}
			}
			
			arSrc = arr_date;
			break;
		case 3:
			//amount
			Object[] Obj_amount = customerDB.getCustomersList().toArray();
			Arrays.sort(Obj_amount, new DataComparator_Amount());
			
			ArrayList<Customer> arr_amount = new ArrayList<Customer>();
			for (Object o : Obj_amount) {
				arr_amount.add((Customer) o);
			}
			
			arSrc = arr_amount;
			break;
		}
		
		this.notifyDataSetChanged();
	}

	public void arSrcRefresh(int position) {
		//arSrc = customerDB.getView_CustomersList();
		Sort(position);
	}

}
