package ts.utill.customermanager;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import ts.utill.customermanager.data.Customer;
import ts.utill.customermanager.data.CustomerDB;
import ts.utill.customermanager.data.Item;
import ts.utill.customermanager.data.ItemSet;
import ts.utill.customermanager.data.Sale;

public class CustomerStatisticsActivity  extends AppCompatActivity {
	
	CustomerDB customerDB =  CustomerDB.getInstence();
	
	Customer customer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customerstatistics);
		
		Intent intent = getIntent();
		int idx_customer = intent.getIntExtra("idx_customer",0);
		customer = customerDB.getCustomer(idx_customer);
		
		//statistics list
		int[] stat_count = new int[customerDB.getItemList().size()];
		for (int i = 0; i < stat_count.length; i++) {
			stat_count[i] = 0;
		}
		
		for (Sale sale : customer.getSaleList()) {
			for (ItemSet item : customerDB.ToSaleItem(sale.getItemList().toString())) {
				stat_count[item.getItem().getIdx_item()-1]+= item.getAmount();
			}
		}
		
		int totalcount = 0;
		
		ArrayList<Statistics_CustomerPreferItem> stat_itemlist = new ArrayList<Statistics_CustomerPreferItem>();
		for (Item item : customerDB.getItemList()) {
			stat_itemlist.add(new Statistics_CustomerPreferItem(item, stat_count[item.getIdx_item()-1]));
			totalcount += stat_count[item.getIdx_item()-1];
		}			
		
		//sort
		Object[] oa = stat_itemlist.toArray();
		Arrays.sort(oa, new DataComparator_Statistics_CustomerPreferItem());
		ArrayList<Statistics_CustomerPreferItem> arr = new ArrayList<Statistics_CustomerPreferItem>();
		for (Object o : oa) {
			arr.add((Statistics_CustomerPreferItem) o);
		}
		
		//layout
		LinearLayout layout_customerstatistics_chartrange = (LinearLayout)findViewById(R.id.layout_customerstatistics_chartrange);
		
		int index = 0;
		for (Statistics_CustomerPreferItem statistics_CustomerPreferItem : arr) {
			if(statistics_CustomerPreferItem.getCount() > 0 ){
				View view = View.inflate(this, R.layout.statistics_rodchart, null);
				
				TextView TextView_statistics_rodchart_name = (TextView)view.findViewById(R.id.TextView_statistics_rodchart_name);
				TextView textView_statistics_rodchart_value = (TextView)view.findViewById(R.id.textView_statistics_rodchart_value);
				LinearLayout graph_statistics_rodchart_rod = (LinearLayout)view.findViewById(R.id.graph_statistics_rodchart_rod);
				LinearLayout graph_statistics_rodchart_space = (LinearLayout)view.findViewById(R.id.graph_statistics_rodchart_space);
				
				float persent = statistics_CustomerPreferItem.getCount()/(float)totalcount;
				float weighsum = 1.0f/persent;
				graph_statistics_rodchart_space.setWeightSum(weighsum);
				
				graph_statistics_rodchart_rod.setBackgroundColor(getResources().getColor(customerDB.getColor(index % 6)));
				index++;
				
				TextView_statistics_rodchart_name.setText(statistics_CustomerPreferItem.getItem().getName());
				textView_statistics_rodchart_value.setText(statistics_CustomerPreferItem.getCount()+"");

				layout_customerstatistics_chartrange.addView(view);				
			}
		}
	}
/*	
	public static int getDpToPixel(Context context, int DP) {
	    float px = 0;
	    try {
	        px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP, context.getResources().getDisplayMetrics());
	    } catch (Exception e) {
	         
	    }
	    return (int) px;
	}
	*/
}



class Statistics_CustomerPreferItem{
	Item item;
	int count;
	public Statistics_CustomerPreferItem(Item item, int count){
		this.item = item;
		this.count = count;
	}
	int getCount(){
		return this.count;
	}
	Item getItem(){
		return this.item;
	}
}

class DataComparator_Statistics_CustomerPreferItem  implements java.util.Comparator{
	public int compare(Object lhs, Object rhs) {
		return ((Statistics_CustomerPreferItem) rhs).getCount() - ((Statistics_CustomerPreferItem) lhs).getCount();
	}
}

