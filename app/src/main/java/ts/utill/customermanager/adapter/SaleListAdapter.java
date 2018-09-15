package ts.utill.customermanager.adapter;

import java.util.ArrayList;
import java.util.Arrays;

import ts.utill.customermanager.CustomerListActivity;
import ts.utill.customermanager.CustomerViewActivity;
import ts.utill.customermanager.R;
import ts.utill.customermanager.data.Customer;
import ts.utill.customermanager.data.CustomerDB;
import ts.utill.customermanager.data.DataComparator_SaleDate;
import ts.utill.customermanager.data.Sale;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SaleListAdapter extends BaseAdapter {

	Context maincon;
	CustomerViewActivity customerViewActivity;
	LayoutInflater Inflater;
	CustomerDB customerDB;
	Customer customer;
	ArrayList<Sale> arSrc;

	public SaleListAdapter(Context context, Customer customer) {
		this.maincon = context;
		this.customerViewActivity  = (CustomerViewActivity) context;
		this.customerDB = CustomerDB.getInstence();
		this.customer = customer;
		this.arSrc = customer.getSaleList();
		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		arSrcRefresh();
	}

	@Override
	public int getCount() {
		return arSrc.size();
	}

	@Override
	public Sale getItem(int position) {
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
			res = ts.utill.customermanager.R.layout.customerview_salelist;
			convertView = Inflater.inflate(res, parent, false);
		}

		TextView textView_customerView_date = (TextView)convertView.findViewById(R.id.textView_customerView_date);
		TextView textView_customerView_dday = (TextView)convertView.findViewById(R.id.textView_customerView_dday);
		TextView textView_customerView_itemList = (TextView)convertView.findViewById(R.id.textView_customerView_itemList);
		TextView textView_customerView_amount = (TextView)convertView.findViewById(R.id.textView_customerView_amount);

		ImageView imageView_customerView_memo = (ImageView)convertView.findViewById(R.id.imageView_customerView_memo);

		textView_customerView_date.setText(customerDB.ToDate(arSrc.get(position).getDate()));
		textView_customerView_dday.setText(customerDB.getDDay(arSrc.get(position).getDate()) + "일전");

		textView_customerView_itemList.setText(arSrc.get(position).getItemListName());

		textView_customerView_amount.setText(customerDB.toThousand(arSrc.get(position).getTotalPrice()));

		imageView_customerView_memo.setImageResource(customerDB.getImageMemo(arSrc.get(position).getIsMemo()));

		imageView_customerView_memo.setOnClickListener(new ImageView.OnClickListener(){
			public void onClick(View v){
				if(arSrc.get(position).getIsMemo() == 1){
					new AlertDialog.Builder(customerViewActivity)
							.setTitle(R.string.memo)
							.setMessage(arSrc.get(position).getMemo())
							.show();
				}
			}
		});

		RelativeLayout layout_customerView_saleList = (RelativeLayout)convertView.findViewById(R.id.layout_customerView_saleList);
		layout_customerView_saleList.setOnClickListener(new RelativeLayout.OnClickListener(){
			public void onClick(View v){
				customerViewActivity.Intent_NewSaleActivity(arSrc.get(position).getIdx_Sale());
			}
		});

		return convertView;
	}

	public void arSrcRefresh() {

		//arSrc = customer.getSaleList();
		//date
		Object[] Obj_date = customer.getSaleList().toArray();
		Arrays.sort(Obj_date, new DataComparator_SaleDate());

		ArrayList<Sale> arr_date = new ArrayList<Sale>();
		for (Object o : Obj_date) {
			arr_date.add((Sale) o);
		}

		arSrc = arr_date;

		customer.setSaleList(arSrc);

		this.notifyDataSetChanged();
	}

}
