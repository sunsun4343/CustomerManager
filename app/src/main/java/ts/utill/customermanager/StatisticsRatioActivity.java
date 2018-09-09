package ts.utill.customermanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import ts.utill.customermanager.adapter.StatisticsRatio_RecordListAdapter;
import ts.utill.customermanager.data.Customer;
import ts.utill.customermanager.data.CustomerDB;
import ts.utill.customermanager.data.Item;
import ts.utill.customermanager.data.ItemSet;
import ts.utill.customermanager.data.Sale;
import ts.utill.customermanager.data.Statistics_Ratio_Record;

public class StatisticsRatioActivity extends AppCompatActivity {
	
	CustomerDB customerDB = CustomerDB.getInstence();
	
	Spinner spinner_statistics_ratio_article;
	ArrayAdapter<CharSequence> spin_article;
	
	Spinner spinner_statistics_ratio_option;
	ArrayAdapter<CharSequence> spin_option;
	
	StatisticsRatio_RecordListAdapter statisticsRatio_RecordListAdapter;
	
	LinearLayout layout_statistics_ratio__chartrange;
	
	long totalValue = 0;
	int selected_article = 0;
	int selected_option = 0;
	
	boolean term_all = true;
	Calendar startDate = null;
	Calendar endDate = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics_ratio);
		
		//spinner connect
		spinner_statistics_ratio_article = (Spinner)findViewById(R.id.spinner_statistics_ratio_article);
		spinner_statistics_ratio_article.setPrompt("article");
		spin_article = ArrayAdapter.createFromResource(this, R.array.ratio_article, android.R.layout.simple_list_item_checked);
		spin_article.setDropDownViewResource(android.R.layout.simple_list_item_checked);
		spinner_statistics_ratio_article.setAdapter(spin_article);
		
		spinner_statistics_ratio_option = (Spinner)findViewById(R.id.spinner_statistics_ratio_option);
		spinner_statistics_ratio_option.setPrompt("option");
		spin_option = ArrayAdapter.createFromResource(this, R.array.ratio_option1, android.R.layout.simple_list_item_checked);
		spin_option.setDropDownViewResource(android.R.layout.simple_list_item_checked);
		spinner_statistics_ratio_option.setAdapter(spin_option);
		
		final TextView textView_statistics_ratio_preiod = (TextView)findViewById(R.id.textView_statistics_ratio_preiod);
		
		textView_statistics_ratio_preiod.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {		
				LinearLayout layout = (LinearLayout)View.inflate(StatisticsRatioActivity.this, R.layout.statistics_dialog_term_ratio, null);

				final DatePicker startPicker = (DatePicker)layout.findViewById(R.id.datePicker_statistics_dialog_trem_ratio_startDate);
				final DatePicker endPicker = (DatePicker)layout.findViewById(R.id.datePicker_statistics_dialog_trem_ratio_endDate);
					
				Calendar startCal = Calendar.getInstance();
					
				startCal.add(Calendar.MONTH, -1);
					
				startPicker.updateDate(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH));
					
					
				final Button button_statistics_dialog_term_ratio_1 = (Button)layout.findViewById(R.id.button_statistics_dialog_term_ratio_1);
				button_statistics_dialog_term_ratio_1.setOnClickListener(new OnClickListener(){
					public void onClick(View arg0) {
						Calendar startCal = Calendar.getInstance();
						Calendar endCal = Calendar.getInstance();
						
						startCal.add(Calendar.MONTH, -1);
						
						startPicker.updateDate(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH));
						endPicker.updateDate(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH));
					}
				});
				
				final Button button_statistics_dialog_term_ratio_2 = (Button)layout.findViewById(R.id.button_statistics_dialog_term_ratio_2);
				button_statistics_dialog_term_ratio_2.setOnClickListener(new OnClickListener(){
					public void onClick(View arg0) {
						Calendar startCal = Calendar.getInstance();
						Calendar endCal = Calendar.getInstance();
						
						startCal.add(Calendar.MONTH, -3);
						
						startPicker.updateDate(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH));
						endPicker.updateDate(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH));
					}
				});
				
				final Button button_statistics_dialog_term_ratio_3 = (Button)layout.findViewById(R.id.button_statistics_dialog_term_ratio_3);					
				button_statistics_dialog_term_ratio_3.setOnClickListener(new OnClickListener(){
					public void onClick(View arg0) {
						Calendar startCal = Calendar.getInstance();
						Calendar endCal = Calendar.getInstance();
						
						startCal.add(Calendar.MONTH, -6);
						
						startPicker.updateDate(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH));
						endPicker.updateDate(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH));
					}
				});
				
				final Button button_statistics_dialog_term_ratio_4 = (Button)layout.findViewById(R.id.button_statistics_dialog_term_ratio_4);
				button_statistics_dialog_term_ratio_4.setOnClickListener(new OnClickListener(){
					public void onClick(View arg0) {
						Calendar startCal = Calendar.getInstance();
						Calendar endCal = Calendar.getInstance();
						
						startCal.add(Calendar.YEAR, -1);
						
						startPicker.updateDate(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH));
						endPicker.updateDate(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH));
					}
				});
				
				
				final CheckBox checkBox_statitics_dialog_term_ratio_all = (CheckBox)layout.findViewById(R.id.checkBox_statitics_dialog_term_ratio_all);
				checkBox_statitics_dialog_term_ratio_all.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						if (arg1) {
							startPicker.setEnabled(false);
							endPicker.setEnabled(false);
							button_statistics_dialog_term_ratio_1.setEnabled(false);
							button_statistics_dialog_term_ratio_2.setEnabled(false);
							button_statistics_dialog_term_ratio_3.setEnabled(false);
							button_statistics_dialog_term_ratio_4.setEnabled(false);
						}else{
							startPicker.setEnabled(true);
							endPicker.setEnabled(true);
							button_statistics_dialog_term_ratio_1.setEnabled(true);
							button_statistics_dialog_term_ratio_2.setEnabled(true);
							button_statistics_dialog_term_ratio_3.setEnabled(true);
							button_statistics_dialog_term_ratio_4.setEnabled(true);
						}
					}
				});
				
				
				new AlertDialog.Builder(StatisticsRatioActivity.this)
				.setView(layout)
				.setPositiveButton(R.string.setting,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
						if (checkBox_statitics_dialog_term_ratio_all.isChecked()) {
							startDate = null;
							endDate = null;
							
							textView_statistics_ratio_preiod.setText("��ü");
							
						}else{
							Calendar startcal = Calendar.getInstance();
							startcal.set(startPicker.getYear(), startPicker.getMonth(), startPicker.getDayOfMonth());
							
							Calendar endcal = Calendar.getInstance();
							endcal.set(endPicker.getYear(), endPicker.getMonth(), endPicker.getDayOfMonth());
							
							if(startcal.getTimeInMillis() - endcal.getTimeInMillis() > 0){
								Toast.makeText(StatisticsRatioActivity.this, "�Ⱓ������ �߸��Ǿ����ϴ�.", Toast.LENGTH_SHORT).show();
							}else{
								startcal.set(Calendar.HOUR, 0);
								startcal.set(Calendar.MINUTE, 0);
								startcal.set(Calendar.SECOND, 0);
								
								startDate = startcal;
								
								endcal.set(Calendar.HOUR, 23);
								endcal.set(Calendar.MINUTE, 59);
								endcal.set(Calendar.SECOND, 59);
								endDate = endcal;
								
								SimpleDateFormat dateForm = new SimpleDateFormat("yy.MM.dd");
								
								textView_statistics_ratio_preiod.setText(dateForm.format(startDate.getTime()) + "~" + dateForm.format(endDate.getTime()));
							}
						}
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.show();						
			}
		});
		
		//layout
		layout_statistics_ratio__chartrange = (LinearLayout)findViewById(R.id.layout_statistics_ratio__chartrange);		
		
		//init statistics View
		View_Statistics(getStatistics_Item_Amount(startDate, endDate));
		
		TextView textView_statistics_ratio_title = (TextView)findViewById(R.id.textView_statistics_ratio_title);
		textView_statistics_ratio_title.setText(spinner_statistics_ratio_article.getSelectedItem().toString() + "�� " + spinner_statistics_ratio_option.getSelectedItem().toString());
		
		
		//listener
		OnItemSelectedListener listener_article = new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selected_article = position;
				switch (position) {
				case 0:	case 3:	case 5:	setSpinner_arr(R.array.ratio_option1);break;
				case 1:	case 2:	case 4:	setSpinner_arr(R.array.ratio_option2);break;
				}
				spinner_statistics_ratio_option.setSelection(0);
				
				//all
				textView_statistics_ratio_preiod.setVisibility(View.VISIBLE);
				textView_statistics_ratio_preiod.setText("��ü");
				startDate = null;
				endDate = null;
			}
			public void onNothingSelected(AdapterView<?> parent) {}
		};
		spinner_statistics_ratio_article.setOnItemSelectedListener(listener_article);
		
		OnItemSelectedListener listener_option = new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selected_option = position;
				
				if (selected_article == 1 || selected_article == 2){
					if (selected_option == 1) {
						textView_statistics_ratio_preiod.setVisibility(View.GONE);
					}
					
				}
			}
			public void onNothingSelected(AdapterView<?> parent) {}
		};
		spinner_statistics_ratio_option.setOnItemSelectedListener(listener_option);
		
		ImageView imageView_statistics_ratio_search = (ImageView)findViewById(R.id.imageView_statistics_ratio_search);
		imageView_statistics_ratio_search.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				//layout child clear
				layout_statistics_ratio__chartrange.removeAllViewsInLayout();
				
				TextView textView_statistics_ratio_title = (TextView)findViewById(R.id.textView_statistics_ratio_title);
				textView_statistics_ratio_title.setText(spinner_statistics_ratio_article.getSelectedItem().toString() + "�� " + spinner_statistics_ratio_option.getSelectedItem().toString());
				
				switch (selected_article) {
				case 0:
					if(selected_option == 0){
						if (term_all) {View_Statistics(getStatistics_Item_Amount(startDate, endDate));
						}else{}
					}else if(selected_option == 1){
						if (term_all) {View_Statistics(getStatistics_Item_Count(startDate, endDate));
						}else{}
					}
					break;
				case 1:
					if(selected_option == 0){
						if (term_all) {View_Statistics(getStatistics_Sex_Amount(startDate, endDate));
						}else{}
					}else if(selected_option == 1){
						if (term_all) {View_Statistics(getStatistics_Sex_Count());
						}else{}
					}
					break;
				case 2:
					if(selected_option == 0){
						if (term_all) {View_Statistics(getStatistics_Age_Amount(startDate, endDate));
						}else{}
					}else if(selected_option == 1){
						if (term_all) {View_Statistics(getStatistics_Age_Count());
						}else{}
					}
					break;
				case 3:
					if(selected_option == 0){
						if (term_all) {View_Statistics(getStatistics_Week_Amount(startDate, endDate));
						}else{}
					}else if(selected_option == 1){
						if (term_all) {View_Statistics(getStatistics_Week_Count(startDate, endDate));
						}else{}
					}
					break;
				case 4:
					if(selected_option == 0){
						if (term_all) {
							
						}else{
							
						}
					}else if(selected_option == 1){
						if (term_all) {
							
						}else{
							
						}
					}
					break;
				case 5:
					if(selected_option == 0){
						if (term_all) {
							
						}else{
							
						}
					}else if(selected_option == 1){
						if (term_all) {
							
						}else{
							
						}
					}
					break;
				}
			}
		});
		
	}
	


	private void setSpinner_arr(int ratioOption){
		spin_option = ArrayAdapter.createFromResource(this, ratioOption, android.R.layout.simple_list_item_checked);
		spin_option.setDropDownViewResource(android.R.layout.simple_list_item_checked);
		spinner_statistics_ratio_option.setAdapter(spin_option);
	}
	
	private void View_Statistics(ArrayList<Statistics_Ratio_Record> arr) {
		int index = 0;
		for (Statistics_Ratio_Record statistics_Ratio_Record : arr) {
			if(statistics_Ratio_Record.getValue() > 0 ){
				View view = View.inflate(this, R.layout.statistics_rodchart, null);
				
				TextView TextView_statistics_rodchart_name = (TextView)view.findViewById(R.id.TextView_statistics_rodchart_name);
				TextView textView_statistics_rodchart_value = (TextView)view.findViewById(R.id.textView_statistics_rodchart_value);
				LinearLayout graph_statistics_rodchart_rod = (LinearLayout)view.findViewById(R.id.graph_statistics_rodchart_rod);
				LinearLayout graph_statistics_rodchart_space = (LinearLayout)view.findViewById(R.id.graph_statistics_rodchart_space);
				
				float persent = statistics_Ratio_Record.getValue()/(float)totalValue;
				float weighsum = 1.0f/persent;
				graph_statistics_rodchart_space.setWeightSum(weighsum);
				
				switch (selected_article) {
				case 0: case 3: case 5:
					graph_statistics_rodchart_rod.setBackgroundColor(getResources().getColor(customerDB.getColor(index % 6)));
					break;
				case 1:
					if(statistics_Ratio_Record.getName() == "����"){
						graph_statistics_rodchart_rod.setBackgroundColor(getResources().getColor(customerDB.getColor(6)));
					}else{
						graph_statistics_rodchart_rod.setBackgroundColor(getResources().getColor(customerDB.getColor(7)));
					}
					break;
				case 2:
					int age = Integer.parseInt(statistics_Ratio_Record.getName().substring(0,1))-1;
					graph_statistics_rodchart_rod.setBackgroundColor(getResources().getColor(customerDB.getColor(age)));
					break;
				case 4:
					
					break;
				}
				index++;
				
				TextView_statistics_rodchart_name.setText(statistics_Ratio_Record.getName());
				textView_statistics_rodchart_value.setText(customerDB.toThousand(statistics_Ratio_Record.getValue()));

				layout_statistics_ratio__chartrange.addView(view);
			}
		}
		
		//list connect
		statisticsRatio_RecordListAdapter = new StatisticsRatio_RecordListAdapter(this, Trim(arr));
		ListView listView_statistics_ratio_recorde = (ListView)findViewById(R.id.listView_statistics_ratio_recorde);
		listView_statistics_ratio_recorde.setAdapter(statisticsRatio_RecordListAdapter);
		
		
	}
	
	private ArrayList<Statistics_Ratio_Record> Trim(ArrayList<Statistics_Ratio_Record> recordlist) {
		ArrayList<Statistics_Ratio_Record> newrecordlist = new ArrayList<Statistics_Ratio_Record>();
		for (Statistics_Ratio_Record statistics_Ratio_Record : recordlist) {
			if (statistics_Ratio_Record.getValue() > 0) {
				newrecordlist.add(statistics_Ratio_Record);
			}
		}
		return newrecordlist;
	}

	private ArrayList<Statistics_Ratio_Record> getStatistics_Item_Amount(Calendar firstDate, Calendar lastDate){
		
		boolean isAllData = firstDate == null?true:false;
		
		//statistics list
		int[] stat_amount = new int[customerDB.getItemList().size()];
		for (int i = 0; i < stat_amount.length; i++) {
			stat_amount[i] = 0;
		}
		
		for (Sale sale : customerDB.getAllSaleList()) {
			boolean isOk= false;
			if(isAllData){
				isOk = true;
			}else{
				if(sale.getDate().getTimeInMillis() > firstDate.getTimeInMillis() && sale.getDate().getTimeInMillis() < lastDate.getTimeInMillis()){
					isOk = true;
				}
			}
			if (isOk) {
				for (ItemSet item : customerDB.ToSaleItem(sale.getItemList().toString())) {
					stat_amount[item.getItem().getIdx_item()-1]+=(item.getItem().getPrice() * item.getAmount());
				}				
			}
		}
		
		int totalAmount = 0;
		
		ArrayList<Statistics_Item_Amount> stat_itemlist = new ArrayList<Statistics_Item_Amount>();
		for (Item item : customerDB.getItemList()) {
			stat_itemlist.add(new Statistics_Item_Amount(item, stat_amount[item.getIdx_item()-1]));
			totalAmount += stat_amount[item.getIdx_item()-1];
		}			
		
		this.totalValue = totalAmount;
		
		//sort
		Object[] oa = stat_itemlist.toArray();
		Arrays.sort(oa, new DataComparator_Statistics_Item_Amount());
		ArrayList<Statistics_Item_Amount> arr = new ArrayList<Statistics_Item_Amount>();
		for (Object o : oa) {
			arr.add((Statistics_Item_Amount) o);
		}
		
		ArrayList<Statistics_Ratio_Record> array = new ArrayList<Statistics_Ratio_Record>();
		for (Statistics_Item_Amount i : arr) {
			array.add(new Statistics_Ratio_Record(i.getItem().getName().toString(), i.getAmount()));
		}
		return array;		
	}

	private ArrayList<Statistics_Ratio_Record> getStatistics_Item_Count(Calendar firstDate, Calendar lastDate){
		
		boolean isAllData = firstDate == null?true:false;
		
		//statistics list
		int[] stat_count = new int[customerDB.getItemList().size()];
		for (int i = 0; i < stat_count.length; i++) {
			stat_count[i] = 0;
		}
		
		for (Sale sale : customerDB.getAllSaleList()) {
			boolean isOk= false;
			if(isAllData){
				isOk = true;
			}else{
				if(sale.getDate().getTimeInMillis() > firstDate.getTimeInMillis() && sale.getDate().getTimeInMillis() < lastDate.getTimeInMillis()){
					isOk = true;
				}
			}
			if (isOk) {
				for (ItemSet item : customerDB.ToSaleItem(sale.getItemList().toString())) {
					stat_count[item.getItem().getIdx_item()-1]+= item.getAmount();
				}
			}
		}
		
		int totalCount = 0;
		
		ArrayList<Statistics_Item_Count> stat_itemlist = new ArrayList<Statistics_Item_Count>();
		for (Item item : customerDB.getItemList()) {
			stat_itemlist.add(new Statistics_Item_Count(item, stat_count[item.getIdx_item()-1]));
			totalCount += stat_count[item.getIdx_item()-1];
		}			
		
		this.totalValue = totalCount;
		
		//sort
		Object[] oa = stat_itemlist.toArray();
		Arrays.sort(oa, new DataComparator_Statistics_Item_Count());
		ArrayList<Statistics_Item_Count> arr = new ArrayList<Statistics_Item_Count>();
		for (Object o : oa) {
			arr.add((Statistics_Item_Count) o);
		}
		
		ArrayList<Statistics_Ratio_Record> array = new ArrayList<Statistics_Ratio_Record>();
		for (Statistics_Item_Count i : arr) {
			array.add(new Statistics_Ratio_Record(i.getItem().getName().toString(), i.getCount()));
		}
		return array;		
	}
	
	private ArrayList<Statistics_Ratio_Record> getStatistics_Sex_Amount(Calendar firstDate, Calendar lastDate){
		
		boolean isAllData = firstDate == null?true:false;
		
		//statistics list
		int[] stat_amount = new int[2];
		for (int i = 0; i < stat_amount.length; i++) {
			stat_amount[i] = 0;
		}
		
		for (Sale sale : customerDB.getAllSaleList()) {
			boolean isOk= false;
			if(isAllData){
				isOk = true;
			}else{
				if(sale.getDate().getTimeInMillis() > firstDate.getTimeInMillis() && sale.getDate().getTimeInMillis() < lastDate.getTimeInMillis()){
					isOk = true;
				}
			}
			if (isOk) {
				for (ItemSet item : customerDB.ToSaleItem(sale.getItemList().toString())) {
					stat_amount[customerDB.getCustomer(sale.getIdx_customer()).getSexToInt()]+=(item.getItem().getPrice() * item.getAmount());
				}	
			}
			
		}
		
		this.totalValue = stat_amount[0] + stat_amount[1];
		
		ArrayList<Statistics_Ratio_Record> array = new ArrayList<Statistics_Ratio_Record>();
		
		//sort
		if (stat_amount[0] > stat_amount[1]) {
			array.add(new Statistics_Ratio_Record("����", stat_amount[0]));
			array.add(new Statistics_Ratio_Record("����", stat_amount[1]));
		}else{
			array.add(new Statistics_Ratio_Record("����", stat_amount[1]));
			array.add(new Statistics_Ratio_Record("����", stat_amount[0]));
		}
		
		return array;		
	}
	
	private ArrayList<Statistics_Ratio_Record> getStatistics_Sex_Count(){
		
		//statistics list
		int[] stat_value = new int[2];
		for (int i = 0; i < stat_value.length; i++) {
			stat_value[i] = 0;
		}
		
		for (Customer customer : customerDB.getCustomersList()) {
			stat_value[customer.getSexToInt()]++;
		}
		
		this.totalValue = stat_value[0] + stat_value[1];
		
		ArrayList<Statistics_Ratio_Record> array = new ArrayList<Statistics_Ratio_Record>();
		
		//sort
		if (stat_value[0] > stat_value[1]) {
			array.add(new Statistics_Ratio_Record("����", stat_value[0]));
			array.add(new Statistics_Ratio_Record("����", stat_value[1]));
		}else{
			array.add(new Statistics_Ratio_Record("����", stat_value[1]));
			array.add(new Statistics_Ratio_Record("����", stat_value[0]));
		}
		
		return array;		
	}
	
	private ArrayList<Statistics_Ratio_Record> getStatistics_Age_Amount(Calendar firstDate, Calendar lastDate){
		
		boolean isAllData = firstDate == null?true:false;
		
		//statistics list
		int[] stat_amount = new int[6];
		for (int i = 0; i < stat_amount.length; i++) {
			stat_amount[i] = 0;
		}
		
		for (Sale sale : customerDB.getAllSaleList()) {
			boolean isOk= false;
			if(isAllData){
				isOk = true;
			}else{
				if(sale.getDate().getTimeInMillis() > firstDate.getTimeInMillis() && sale.getDate().getTimeInMillis() < lastDate.getTimeInMillis()){
					isOk = true;
				}
			}
			if (isOk) {
				for (ItemSet item : customerDB.ToSaleItem(sale.getItemList().toString())) {
					stat_amount[customerDB.getCustomer(sale.getIdx_customer()).getAge()]+=(item.getItem().getPrice()*item.getAmount());
				}
			}
			
		}
		
		long totalValue = 0;
		for (int i : stat_amount) {
			totalValue+=i;
		}
		this.totalValue = totalValue;
		
		ArrayList<Statistics_Ratio_Record> array = new ArrayList<Statistics_Ratio_Record>();
		
		//sort
		int[] index = new int[6];
		for (int j = 0; j < index.length; j++) {
			index[j] = j;
			for (int i = j; i > 0 ; i--) {
				if(stat_amount[index[i]] > stat_amount[index[i-1]]){
					int temp = index[i-1];
					index[i-1] = index[i];
					index[i] = temp;
				}else{
					break;
				}
			}			
		}
		
		for (int i : index) {
			array.add(new Statistics_Ratio_Record(i*10+10+"��", stat_amount[i]));
		}
		
		return array;		
	}
	
	private ArrayList<Statistics_Ratio_Record> getStatistics_Age_Count(){
		
		//statistics list
		int[] stat_value = new int[6];
		for (int i = 0; i < stat_value.length; i++) {
			stat_value[i] = 0;
		}
		
		for (Customer customer : customerDB.getCustomersList()) {
			stat_value[customer.getAge()]++;
		}
		
		long totalValue = 0;
		for (int i : stat_value) {
			totalValue+=i;
		}
		this.totalValue = totalValue;
		
		ArrayList<Statistics_Ratio_Record> array = new ArrayList<Statistics_Ratio_Record>();
		
		//sort
		int[] index = new int[6];
		for (int j = 0; j < index.length; j++) {
			index[j] = j;
			for (int i = j; i > 0 ; i--) {
				if(stat_value[index[i]] > stat_value[index[i-1]]){
					int temp = index[i-1];
					index[i-1] = index[i];
					index[i] = temp;
				}else{
					break;
				}
			}			
		}
		
		for (int i : index) {
			array.add(new Statistics_Ratio_Record(i*10+10+"��", stat_value[i]));
		}
		
		return array;		
	}
	
	protected ArrayList<Statistics_Ratio_Record> getStatistics_Week_Amount(Calendar firstDate, Calendar lastDate) {
		
		boolean isAllData = firstDate == null?true:false;
		
		//statistics list
		int[] stat_amount = new int[7];
		for (int i = 0; i < stat_amount.length; i++) {
			stat_amount[i] = 0;
		}
		
		for (Sale sale : customerDB.getAllSaleList()) {
			boolean isOk= false;
			if(isAllData){
				isOk = true;
			}else{
				if(sale.getDate().getTimeInMillis() > firstDate.getTimeInMillis() && sale.getDate().getTimeInMillis() < lastDate.getTimeInMillis()){
					isOk = true;
				}
			}
			if (isOk) {
				for (ItemSet item : customerDB.ToSaleItem(sale.getItemList().toString())) {
					stat_amount[sale.getDate().get(Calendar.DAY_OF_WEEK)-1]+=(item.getItem().getPrice()*item.getAmount());
				}				
			}

		}
		
		long totalValue = 0;
		for (int i : stat_amount) {
			totalValue+=i;
		}
		this.totalValue = totalValue;
		
		ArrayList<Statistics_Ratio_Record> array = new ArrayList<Statistics_Ratio_Record>();
		
		//sort
		int[] index = new int[6];
		for (int j = 0; j < index.length; j++) {
			index[j] = j;
			for (int i = j; i > 0 ; i--) {
				if(stat_amount[index[i]] > stat_amount[index[i-1]]){
					int temp = index[i-1];
					index[i-1] = index[i];
					index[i] = temp;
				}else{
					break;
				}
			}			
		}
		
		String[] week = getResources().getStringArray(R.array.week);
		
		for (int i : index) {
			array.add(new Statistics_Ratio_Record(week[i], stat_amount[i]));
		}
		
		return array;		
	}
	
	protected ArrayList<Statistics_Ratio_Record> getStatistics_Week_Count(Calendar firstDate, Calendar lastDate) {
		
		boolean isAllData = firstDate == null?true:false;
		
		
		//statistics list
		int[] stat_amount = new int[7];
		for (int i = 0; i < stat_amount.length; i++) {
			stat_amount[i] = 0;
		}
		
		for (Sale sale : customerDB.getAllSaleList()) {
			boolean isOk= false;
			if(isAllData){
				isOk = true;
			}else{
				if(sale.getDate().getTimeInMillis() > firstDate.getTimeInMillis() && sale.getDate().getTimeInMillis() < lastDate.getTimeInMillis()){
					isOk = true;
				}
			}
			if (isOk) {
				stat_amount[sale.getDate().get(Calendar.DAY_OF_WEEK)-1]++;
			}
		}
		
		long totalValue = 0;
		for (int i : stat_amount) {
			totalValue+=i;
		}
		this.totalValue = totalValue;
		
		ArrayList<Statistics_Ratio_Record> array = new ArrayList<Statistics_Ratio_Record>();
		
		//sort
		int[] index = new int[6];
		for (int j = 0; j < index.length; j++) {
			index[j] = j;
			for (int i = j; i > 0 ; i--) {
				if(stat_amount[index[i]] > stat_amount[index[i-1]]){
					int temp = index[i-1];
					index[i-1] = index[i];
					index[i] = temp;
				}else{
					break;
				}
			}			
		}
		
		String[] week = getResources().getStringArray(R.array.week);
		
		for (int i : index) {
			array.add(new Statistics_Ratio_Record(week[i], stat_amount[i]));
		}
		
		return array;		
	}
}

class Statistics_Item_Amount{
	Item item;
	long amount;
	public Statistics_Item_Amount(Item item, int amount){
		this.item = item;
		this.amount = amount;
	}
	long getAmount(){
		return this.amount;
	}
	Item getItem(){
		return this.item;
	}
}

class DataComparator_Statistics_Item_Amount  implements java.util.Comparator{
	public int compare(Object lhs, Object rhs) {
		return (int) (((Statistics_Item_Amount) rhs).getAmount() - ((Statistics_Item_Amount) lhs).getAmount());
	}
}

class Statistics_Item_Count{
Item item;
int count;
public Statistics_Item_Count(Item item, int count){
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

class DataComparator_Statistics_Item_Count  implements java.util.Comparator{
public int compare(Object lhs, Object rhs) {
	return ((Statistics_Item_Count) rhs).getCount() - ((Statistics_Item_Count) lhs).getCount();
}
}
