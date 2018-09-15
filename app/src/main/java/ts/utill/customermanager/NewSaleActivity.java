package ts.utill.customermanager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import ts.utill.customermanager.adapter.NewSale_ItemListAdapter;
import ts.utill.customermanager.data.CustomerDB;
import ts.utill.customermanager.data.ItemSet;
import ts.utill.customermanager.data.Sale;

public class NewSaleActivity extends AppCompatActivity {

	boolean isModeNew;
	//boolean isModeNotCustomer;
	
	NewSale_ItemListAdapter newSale_ItemListAdapter;
	CustomerDB customerDB =  CustomerDB.getInstence();
	
	int idx_customer;
	int idx_sale;
	
	Sale sale;
	Sale view_sale;
	
	Calendar calendar;
	ArrayList<ItemSet> itemSetList = new ArrayList<ItemSet>();
	
	EditText editText_newSale_memo;
	
	int Hour = 0;
	int Minute = 0;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_sale);
		
		Intent intent = getIntent();
		isModeNew = intent.getBooleanExtra("modeNew", true);
		idx_customer = intent.getIntExtra("idx_customer", 0);
		//isModeNotCustomer = intent.getBooleanExtra("modeNotCustomer", false);
		
		if (!isModeNew) {
			this.setTitle(R.string.edit);
		}
		/*
		if(!isModeNotCustomer){
			idx_customer = intent.getIntExtra("idx_customer", 0);
		}
*/
		calendar = new GregorianCalendar();
		final TextView textView_newSale_date = (TextView)findViewById(R.id.textView_newSale_date);

		if(isModeNew){
			
			textView_newSale_date.setText(customerDB.ToDateTime(calendar));
			
			//list connect
			newSale_ItemListAdapter = new NewSale_ItemListAdapter(this);
			ListView listView_newSale_itemList;
			listView_newSale_itemList = (ListView)findViewById(R.id.listView_newSale_itemList);
			listView_newSale_itemList.setAdapter(newSale_ItemListAdapter);
			
			/*
			listView_newSale_itemList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					newSale_ItemListAdapter.ItemCheck[position] = !newSale_ItemListAdapter.ItemCheck[position];
					newSale_ItemListAdapter.notifyDataSetChanged();
				}
			});
			*/
			
		}else{
			idx_sale = intent.getIntExtra("idx_Sale", 0);
			sale = customerDB.getSale(idx_customer, idx_sale);
			view_sale = (Sale) sale.clone();
			
			calendar = (Calendar) sale.getDate().clone();
			
			itemSetList = (ArrayList<ItemSet>) sale.getSaleItem().clone();
			
			editText_newSale_memo = (EditText)findViewById(R.id.editText_newSale_memo);
			
			textView_newSale_date.setText(customerDB.ToDateTime(sale.getDate()));
			editText_newSale_memo.setText(sale.getMemo());
			
			//list connect
			newSale_ItemListAdapter = new NewSale_ItemListAdapter(this, itemSetList);
			ListView listView_newSale_itemList;
			listView_newSale_itemList = (ListView)findViewById(R.id.listView_newSale_itemList);
			listView_newSale_itemList.setAdapter(newSale_ItemListAdapter);
			
			//newSale_ItemListAdapter.calcAmount();
			
			/*
			listView_newSale_itemList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					newSale_ItemListAdapter.ItemCheck[position] = !newSale_ItemListAdapter.ItemCheck[position];
					newSale_ItemListAdapter.notifyDataSetChanged();
				}
			});
			*/
		}
		
		textView_newSale_date.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				final LinearLayout linear = (LinearLayout)View.inflate(NewSaleActivity.this, R.layout.newsale_dialog_date, null);
				
				TimePicker timepicker = (TimePicker)linear.findViewById(R.id.timePicker_newSale_dialog);
				timepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
					public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
						Hour = hourOfDay;
						Minute = minute;
					}
				});
				
				new AlertDialog.Builder(NewSaleActivity.this)
				.setTitle("")
				.setView(linear)
				.setPositiveButton(R.string.completion,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						DatePicker datepicker = (DatePicker)linear.findViewById(R.id.datePicker_newSale_dialog);
						calendar.set(datepicker.getYear(), datepicker.getMonth(), datepicker.getDayOfMonth(), Hour, Minute);
						textView_newSale_date.setText(customerDB.ToDateTime(calendar));
					}
				})
				.show();	
			}
		});
		

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(isModeNew){
			getMenuInflater().inflate(R.menu.new_sale, menu);
		}else{
			getMenuInflater().inflate(R.menu.new_sale2, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_chek) {
			
			itemSetList = newSale_ItemListAdapter.getArSrc();
			
			if(isModeNew){
					final EditText editText_newSale_memo = (EditText)findViewById(R.id.editText_newSale_memo);

					//New Sale
					String memo = editText_newSale_memo.getText().toString();
					
					customerDB.Insert_Sale(idx_customer , calendar, itemSetList, memo);

					//Refresh & Finish
					Intent intent = new Intent();
					setResult(RESULT_OK,intent);
					finish();					
				//}
			}else{
				//Edit Customer
				view_sale.setDate(calendar);
				view_sale.setMemo(editText_newSale_memo.getText().toString());
				view_sale.setItemList(itemSetList);
				
				customerDB.Update_Sale(sale, view_sale);
				
				//Refresh & Finish
				Intent intent = new Intent();
				setResult(RESULT_OK,intent);
				finish();
			}
			return true;
		}else if(id == R.id.action_delete){
			
			new AlertDialog.Builder(NewSaleActivity.this)
			.setTitle(R.string.action_delete)
			.setIcon(R.drawable.delsale)
			.setMessage(R.string.delete_Q)
			.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
					customerDB.Delete_Sale(sale, idx_customer);
					
					//Refresh & Finish
					Intent intent = new Intent();
					setResult(RESULT_OK,intent);
					finish();
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public ArrayList<ItemSet> getItemSetList(){
		return itemSetList;
	}
	
	public void Refresh_TextView_Amount(long l){
		final TextView textView_newSale_amount = (TextView)findViewById(R.id.textView_newSale_amount);
		textView_newSale_amount.setText(customerDB.toThousand(l));
	}
}