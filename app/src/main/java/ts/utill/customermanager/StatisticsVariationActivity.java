package ts.utill.customermanager;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.R.integer;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ts.utill.customermanager.adapter.StatisticsVariation_RecordListAdapter;
import ts.utill.customermanager.data.CustomerDB;
import ts.utill.customermanager.data.ItemSet;
import ts.utill.customermanager.data.Sale;
import ts.utill.customermanager.data.Statistics_Ratio_Record;

public class StatisticsVariationActivity extends AppCompatActivity {

	CustomerDB customerDB = CustomerDB.getInstence();

	Spinner spinner_statistics_variation_trem;
	ArrayAdapter<CharSequence> spin_term;

	Spinner spinner_statistics_variation_article;
	ArrayAdapter<CharSequence> spin_article;

	Spinner spinner_statistics_variation_option;
	ArrayAdapter<CharSequence> spin_option;

	StatisticsVariation_RecordListAdapter statisticsVariation_RecordListAdapter;

	LinearLayout layout_statistics_variation_chartrange;

	long totalValue = 0;
	long maxValue = 0;
	int selected_term = 0;
	int selected_article = 0;
	int selected_option = 0;

	boolean term_all = true;
	Calendar startDate = null;
	Calendar endDate = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics_variation);

		//spinner connect
		spinner_statistics_variation_trem = (Spinner)findViewById(R.id.spinner_statistics_variation_trem);
		spinner_statistics_variation_trem.setPrompt("term");
		spin_term = ArrayAdapter.createFromResource(this, R.array.variation_term, android.R.layout.simple_list_item_checked);
		spin_term.setDropDownViewResource(android.R.layout.simple_list_item_checked);
		spinner_statistics_variation_trem.setAdapter(spin_term);

		spinner_statistics_variation_article = (Spinner)findViewById(R.id.spinner_statistics_variation_article);
		spinner_statistics_variation_article.setPrompt("article");
		spin_article = ArrayAdapter.createFromResource(this, R.array.variation_article, android.R.layout.simple_list_item_checked);
		spin_article.setDropDownViewResource(android.R.layout.simple_list_item_checked);
		spinner_statistics_variation_article.setAdapter(spin_article);

		spinner_statistics_variation_option = (Spinner)findViewById(R.id.spinner_statistics_variation_option);
		spinner_statistics_variation_option.setPrompt("option");
		spin_option = ArrayAdapter.createFromResource(this, R.array.variation_option, android.R.layout.simple_list_item_checked);
		spin_option.setDropDownViewResource(android.R.layout.simple_list_item_checked);
		spinner_statistics_variation_option.setAdapter(spin_option);


		final TextView textView_statistics_variation_preiod = (TextView)findViewById(R.id.textView_statistics_variation_preiod);

		//listener
		OnItemSelectedListener listener_term = new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
					case 0:
						startDate = Calendar.getInstance();
						endDate = Calendar.getInstance();

						startDate.add(Calendar.MONTH, -6);

						SimpleDateFormat dateForm = new SimpleDateFormat("yy.MM");
						textView_statistics_variation_preiod.setText(dateForm.format(startDate.getTime()) + "~" + dateForm.format(endDate.getTime()));

						break;
					case 1:
						startDate = Calendar.getInstance();
						endDate = Calendar.getInstance();

						startDate.add(Calendar.MONTH, -1);

						SimpleDateFormat dateForm2 = new SimpleDateFormat("yy.MM.dd");
						textView_statistics_variation_preiod.setText(dateForm2.format(startDate.getTime()) + "~" + dateForm2.format(endDate.getTime()));
						break;
				}
			}
			public void onNothingSelected(AdapterView<?> parent) {}
		};
		spinner_statistics_variation_trem.setOnItemSelectedListener(listener_term);

		textView_statistics_variation_preiod.setOnClickListener(new TextView.OnClickListener(){
			public void onClick(View arg0) {

				LinearLayout layout =null;
				switch (spinner_statistics_variation_trem.getSelectedItemPosition()) {
					case 0:
						layout = (LinearLayout)View.inflate(StatisticsVariationActivity.this, R.layout.statistics_dialog_term_month, null);


					/*
					 try {
				            Field[] datePickerDialogFields = DatePicker.class.getDeclaredFields();
				            for (Field datePickerField : datePickerDialogFields) {
				                if (datePickerField.getName().equals("mDatePicker")) {
				                    datePickerField.setAccessible(true);
				                    DatePicker datePicker = (DatePicker) datePickerField.get(this);
				                    Field datePickerFields[] = datePickerField.getType()
				                            .getDeclaredFields();
				                    for (Field datePickerField1 : datePickerFields) {
				                        if ("mDayPicker".equals(datePickerField1.getName())
				                                || "mDaySpinner".equals(datePickerField1.getName())
				                                || "mMonthPicker".equals(datePickerField1.getName())
				                                || "mMonthSpinner".equals(datePickerField1.getName())) {
				                            datePickerField1.setAccessible(true);
				                            Object dayPicker = new Object();
				                            dayPicker = datePickerField.get(datePicker);
				                            ((View) dayPicker).setVisibility(View.GONE);
				                        }
				                    }
				                }
				            }
				        } catch (Exception ex) {
				        }
					*/


						final DatePicker startPicker_month = (DatePicker)layout.findViewById(R.id.datePicker_statistics_dialog_trem_month_startDate);
						final DatePicker endPicker_month = (DatePicker)layout.findViewById(R.id.datePicker_statistics_dialog_trem_month_endDate);

						Calendar startCal_month = Calendar.getInstance();

						startCal_month.add(Calendar.MONTH, -6);

						startPicker_month.updateDate(startCal_month.get(Calendar.YEAR), startCal_month.get(Calendar.MONTH), 1);

						Button button_statistics_dialog_term_month_1 = (Button)layout.findViewById(R.id.button_statistics_dialog_term_month_1);
						button_statistics_dialog_term_month_1.setOnClickListener(new Button.OnClickListener(){
							public void onClick(View arg0) {
								Calendar startCal = Calendar.getInstance();
								Calendar endCal = Calendar.getInstance();

								startCal.add(Calendar.MONTH, -6);

								startPicker_month.updateDate(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), 1);
								endPicker_month.updateDate(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), 30);
							}
						});

						Button button_statistics_dialog_term_month_2 = (Button)layout.findViewById(R.id.button_statistics_dialog_term_month_2);
						button_statistics_dialog_term_month_2.setOnClickListener(new Button.OnClickListener(){
							public void onClick(View arg0) {
								Calendar startCal = Calendar.getInstance();
								Calendar endCal = Calendar.getInstance();

								startCal.add(Calendar.YEAR, -1);

								startPicker_month.updateDate(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), 1);
								endPicker_month.updateDate(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), 30);
							}
						});

						Button button_statistics_dialog_term_month_3 = (Button)layout.findViewById(R.id.button_statistics_dialog_term_month_3);
						button_statistics_dialog_term_month_3.setOnClickListener(new Button.OnClickListener(){
							public void onClick(View arg0) {
								Calendar startCal = Calendar.getInstance();
								Calendar endCal = Calendar.getInstance();

								startCal.add(Calendar.MONTH, -18);

								startPicker_month.updateDate(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), 1);
								endPicker_month.updateDate(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), 30);
							}
						});

						Button button_statistics_dialog_term_month_4 = (Button)layout.findViewById(R.id.button_statistics_dialog_term_month_4);
						button_statistics_dialog_term_month_4.setOnClickListener(new Button.OnClickListener(){
							public void onClick(View arg0) {
								Calendar startCal = Calendar.getInstance();
								Calendar endCal = Calendar.getInstance();

								startCal.add(Calendar.YEAR, -2);

								startPicker_month.updateDate(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), 1);
								endPicker_month.updateDate(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), 30);
							}
						});


						new AlertDialog.Builder(StatisticsVariationActivity.this)
								.setView(layout)
								.setPositiveButton(R.string.setting,new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {

										Calendar startcal = Calendar.getInstance();
										startcal.set(startPicker_month.getYear(), startPicker_month.getMonth(), startPicker_month.getDayOfMonth());

										Calendar endcal = Calendar.getInstance();
										endcal.set(endPicker_month.getYear(), endPicker_month.getMonth(), endPicker_month.getDayOfMonth());

										if(startcal.getTimeInMillis() - endcal.getTimeInMillis() > 0){
											Toast.makeText(StatisticsVariationActivity.this, "기간설정이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
										}else{
											startDate = startcal;
											endDate = endcal;

											SimpleDateFormat dateForm = new SimpleDateFormat("yy.MM");

											textView_statistics_variation_preiod.setText(dateForm.format(startDate.getTime()) + "~" + dateForm.format(endDate.getTime()));
										}
									}
								})
								.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
									}
								})
								.show();

						break;

					case 1:
						layout = (LinearLayout)View.inflate(StatisticsVariationActivity.this, R.layout.statistics_dialog_term_day, null);

						final DatePicker startPicker = (DatePicker)layout.findViewById(R.id.datePicker_statistics_dialog_trem_day_startDate);
						final DatePicker endPicker = (DatePicker)layout.findViewById(R.id.datePicker_statistics_dialog_trem_day_endDate);

						Calendar startCal = Calendar.getInstance();

						startCal.add(Calendar.MONTH, -1);

						startPicker.updateDate(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH));


						Button button_statistics_dialog_term_day_1 = (Button)layout.findViewById(R.id.button_statistics_dialog_term_day_1);
						button_statistics_dialog_term_day_1.setOnClickListener(new Button.OnClickListener(){
							public void onClick(View arg0) {
								Calendar startCal = Calendar.getInstance();
								Calendar endCal = Calendar.getInstance();

								startCal.add(Calendar.MONTH, -1);

								startPicker.updateDate(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH));
								endPicker.updateDate(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH));
							}
						});

						Button button_statistics_dialog_term_day_2 = (Button)layout.findViewById(R.id.button_statistics_dialog_term_day_2);
						button_statistics_dialog_term_day_2.setOnClickListener(new Button.OnClickListener(){
							public void onClick(View arg0) {
								Calendar startCal = Calendar.getInstance();
								Calendar endCal = Calendar.getInstance();

								startCal.add(Calendar.MONTH, -3);

								startPicker.updateDate(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH));
								endPicker.updateDate(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH));
							}
						});

						Button button_statistics_dialog_term_day_3 = (Button)layout.findViewById(R.id.button_statistics_dialog_term_day_3);
						button_statistics_dialog_term_day_3.setOnClickListener(new Button.OnClickListener(){
							public void onClick(View arg0) {
								Calendar startCal = Calendar.getInstance();
								Calendar endCal = Calendar.getInstance();

								startCal.add(Calendar.MONTH, -6);

								startPicker.updateDate(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH));
								endPicker.updateDate(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH));
							}
						});

						Button button_statistics_dialog_term_day_4 = (Button)layout.findViewById(R.id.button_statistics_dialog_term_day_4);
						button_statistics_dialog_term_day_4.setOnClickListener(new Button.OnClickListener(){
							public void onClick(View arg0) {
								Calendar startCal = Calendar.getInstance();
								Calendar endCal = Calendar.getInstance();

								startCal.add(Calendar.YEAR, -1);

								startPicker.updateDate(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH));
								endPicker.updateDate(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH));
							}
						});


						new AlertDialog.Builder(StatisticsVariationActivity.this)
								.setView(layout)
								.setPositiveButton(R.string.setting,new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {

										Calendar startcal = Calendar.getInstance();
										startcal.set(startPicker.getYear(), startPicker.getMonth(), startPicker.getDayOfMonth());

										Calendar endcal = Calendar.getInstance();
										endcal.set(endPicker.getYear(), endPicker.getMonth(), endPicker.getDayOfMonth());

										if(startcal.getTimeInMillis() - endcal.getTimeInMillis() > 0){
											Toast.makeText(StatisticsVariationActivity.this, "기간설정이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
										}else{
											startDate = startcal;
											endDate = endcal;

											SimpleDateFormat dateForm = new SimpleDateFormat("yy.MM.dd");

											textView_statistics_variation_preiod.setText(dateForm.format(startDate.getTime()) + "~" + dateForm.format(endDate.getTime()));
										}
									}
								})
								.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
									}
								})
								.show();
						break;
				}


			}
		});



		//layout
		layout_statistics_variation_chartrange = (LinearLayout)findViewById(R.id.layout_statistics_variation_chartrange);

		TextView textView_statistics_variation_title = (TextView)findViewById(R.id.textView_statistics_variation_title);
		textView_statistics_variation_title.setText(spinner_statistics_variation_trem.getSelectedItem().toString() + " " + spinner_statistics_variation_article.getSelectedItem().toString() + "(" + spinner_statistics_variation_option.getSelectedItem().toString() + ")");


		ImageView imageView_statistics_variation_search = (ImageView)findViewById(R.id.imageView_statistics_variation_search);
		imageView_statistics_variation_search.setOnClickListener(new ImageView.OnClickListener(){
			@Override
			public void onClick(View v) {

				//layout child clear
				layout_statistics_variation_chartrange.removeAllViewsInLayout();

				TextView textView_statistics_variation_title = (TextView)findViewById(R.id.textView_statistics_variation_title);
				textView_statistics_variation_title.setText(spinner_statistics_variation_trem.getSelectedItem().toString() + " " + spinner_statistics_variation_article.getSelectedItem().toString() + "(" + spinner_statistics_variation_option.getSelectedItem().toString() + ")");

				switch (spinner_statistics_variation_trem.getSelectedItemPosition()) {
					case 0:
						switch (spinner_statistics_variation_article.getSelectedItemPosition()) {
							case 0:	View_Statistics(getStatistics_Month_Amount(startDate, endDate));	break;
							case 1:	break;
						}
						break;
					case 1:
						switch (spinner_statistics_variation_article.getSelectedItemPosition()) {
							case 0: View_Statistics(getStatistics_Day_Amount(startDate, endDate)); break;
							case 1: break;
						}
						break;
				}
			}
		});

		//init statistics View
		startDate = Calendar.getInstance();
		endDate = Calendar.getInstance();

		startDate.add(Calendar.MONTH, -6);

		View_Statistics(getStatistics_Month_Amount(startDate, endDate));

		SimpleDateFormat dateForm = new SimpleDateFormat("yy.MM");
		textView_statistics_variation_preiod.setText(dateForm.format(startDate.getTime()) + "~" + dateForm.format(endDate.getTime()));


	}


	private void View_Statistics(ArrayList<Statistics_Ratio_Record> arr) {
		int index = 0;
		for (Statistics_Ratio_Record statistics_Ratio_Record : arr) {
			View view = View.inflate(this, R.layout.statistics_rodchart, null);

			TextView TextView_statistics_rodchart_name = (TextView)view.findViewById(R.id.TextView_statistics_rodchart_name);
			TextView textView_statistics_rodchart_value = (TextView)view.findViewById(R.id.textView_statistics_rodchart_value);
			LinearLayout graph_statistics_rodchart_rod = (LinearLayout)view.findViewById(R.id.graph_statistics_rodchart_rod);
			LinearLayout graph_statistics_rodchart_space = (LinearLayout)view.findViewById(R.id.graph_statistics_rodchart_space);

			TextView label_statistics_variation_100 = (TextView)findViewById(R.id.label_statistics_variation_100);
			TextView label_statistics_variation_75 = (TextView)findViewById(R.id.label_statistics_variation_75);
			TextView label_statistics_variation_50 = (TextView)findViewById(R.id.label_statistics_variation_50);
			TextView label_statistics_variation_25 = (TextView)findViewById(R.id.label_statistics_variation_25);

			label_statistics_variation_100.setText(customerDB.toThousand(maxValue));
			label_statistics_variation_75.setText(customerDB.toThousand((int)(maxValue*0.75)));
			label_statistics_variation_50.setText(customerDB.toThousand((int)(maxValue*0.5)));
			label_statistics_variation_25.setText(customerDB.toThousand((int)(maxValue*0.25)));

			float persent = statistics_Ratio_Record.getValue()/(float)maxValue;
			float weighsum = 1.0f/persent;
			graph_statistics_rodchart_space.setWeightSum(weighsum);

			graph_statistics_rodchart_rod.setBackgroundColor(getResources().getColor(customerDB.getColor(index % 6)));
			index++;

			TextView_statistics_rodchart_name.setText(statistics_Ratio_Record.getName());
			textView_statistics_rodchart_value.setText(customerDB.toThousand(statistics_Ratio_Record.getValue()));

			layout_statistics_variation_chartrange.addView(view);
		}

		//list connect
		statisticsVariation_RecordListAdapter = new StatisticsVariation_RecordListAdapter(this, arr);
		ListView listView_statistics_variation_recorde = (ListView)findViewById(R.id.listView_statistics_variation_recorde);
		listView_statistics_variation_recorde.setAdapter(statisticsVariation_RecordListAdapter);

	}

	private ArrayList<Statistics_Ratio_Record> getStatistics_Month_Amount(Calendar firstDate, Calendar lastDate) {

		ArrayList<Sale> salelist = customerDB.getAllSaleList();


		if(firstDate == null){
			Calendar[] firstlastDate = getFirstLastDate(salelist);
			firstDate = firstlastDate[0];
			lastDate = firstlastDate[1];
		}

		int record_count = getRecordCount(firstDate, lastDate, 0);

		int[] stat_value = new int[record_count];
		for (int i = 0; i < stat_value.length; i++) {
			stat_value[i] = 0;
		}

		for (Sale sale : salelist) {
			for (ItemSet item : customerDB.ToSaleItem(sale.getItemList().toString())) {
				int index = getIndex(firstDate, sale.getDate(), 0);
				if(index > -1 && index < stat_value.length){
					stat_value[getIndex(firstDate, sale.getDate(), 0)]+=(item.getItem().getPrice()*item.getAmount());
				}
			}
		}

		int totalValue = 0;
		int maxValue = 0;

		ArrayList<Statistics_Ratio_Record> stat_list = new ArrayList<Statistics_Ratio_Record>();

		for (int i = 0; i < stat_value.length; i++) {
			stat_list.add(new Statistics_Ratio_Record(getNowDateName(firstDate, i, 0), stat_value[i]));
			totalValue += stat_value[i];
			if (maxValue < stat_value[i]) {
				maxValue = stat_value[i];
			}
		}
		this.totalValue = totalValue;
		this.maxValue = maxValue;

		return stat_list;
	}

	protected ArrayList<Statistics_Ratio_Record> getStatistics_Day_Amount(Calendar firstDate, Calendar lastDate) {

		ArrayList<Sale> salelist = customerDB.getAllSaleList();

		if(firstDate == null){
			Calendar[] firstlastDate = getFirstLastDate(salelist);
			firstDate = firstlastDate[0];
			lastDate = firstlastDate[1];
		}

		int record_count = getRecordCount(firstDate, lastDate, 1);

		int[] stat_value = new int[record_count];
		for (int i = 0; i < stat_value.length; i++) {
			stat_value[i] = 0;
		}

		for (Sale sale : salelist) {
			for (ItemSet item : customerDB.ToSaleItem(sale.getItemList().toString())) {
				int index = getIndex(firstDate, sale.getDate(), 1);
				if(index > -1 && index < stat_value.length){
					stat_value[getIndex(firstDate, sale.getDate(), 1)]+=(item.getItem().getPrice()*item.getAmount());
				}
			}
		}

		int totalValue = 0;
		int maxValue = 0;

		ArrayList<Statistics_Ratio_Record> stat_list = new ArrayList<Statistics_Ratio_Record>();

		for (int i = 0; i < stat_value.length; i++) {
			stat_list.add(new Statistics_Ratio_Record(getNowDateName(firstDate, i, 1), stat_value[i]));
			totalValue += stat_value[i];
			if (maxValue < stat_value[i]) {
				maxValue = stat_value[i];
			}
		}
		this.totalValue = totalValue;
		this.maxValue = maxValue;

		return stat_list;
	}

	private String getNowDateName(Calendar firstDate, int index, int i) {
		int firstYear = firstDate.get(Calendar.YEAR);
		int firstMonth = firstDate.get(Calendar.MONTH);
		int firstDay = firstDate.get(Calendar.DAY_OF_MONTH);

		if (i == 0) { //Month

			int nowYear = firstYear + (int) (index / 12.0f);
			int nowMonth = firstMonth + index % 12;

			if(nowMonth >= 12){
				nowYear++;
				nowMonth-=12;
			}

			return nowYear + "/" + (nowMonth+1); //Month + 1
		}else{ //Day
			Calendar date = new GregorianCalendar();
			date.set(firstYear, firstMonth, firstDay,0,0,0);
			date.add(Calendar.DAY_OF_MONTH, index);

			int nowYear = Integer.parseInt(new String(date.get(Calendar.YEAR) + "").substring(2,4));
			int nowMonth = date.get(Calendar.MONTH);
			int nowDay = date.get(Calendar.DAY_OF_MONTH);
			return nowYear + "/" + (nowMonth+1) + "/" + nowDay; //Month + 1
		}
	}

	private Calendar[] getFirstLastDate(ArrayList<Sale> salelist) {
		Calendar firstDate = null;
		Calendar lastDate = null;

		if(salelist.size() > 0){
			firstDate = salelist.get(0).getDate();
			lastDate = salelist.get(0).getDate();
		}

		if(salelist.size() > 1){
			for (int i = 1; i < salelist.size(); i++) {
				if (firstDate.getTimeInMillis() > salelist.get(i).getDate().getTimeInMillis()) {
					firstDate = salelist.get(i).getDate();
				}
				if(lastDate.getTimeInMillis() < salelist.get(i).getDate().getTimeInMillis()){
					lastDate = salelist.get(i).getDate();
				}
			}
		}

		Calendar[] result = new Calendar[2];
		result[0] = firstDate;
		result[1] = lastDate;

		return result;
	}

	private int getRecordCount(Calendar firstDate, Calendar lastDate, int i) {
		int firstYear = firstDate.get(Calendar.YEAR);
		int firstMonth = firstDate.get(Calendar.MONTH);
		int firstDay = firstDate.get(Calendar.DAY_OF_MONTH);
		int lastYear = lastDate.get(Calendar.YEAR);
		int lastMonth = lastDate.get(Calendar.MONTH);
		int lastDay = lastDate.get(Calendar.DAY_OF_MONTH);

		firstDate.set(firstYear, firstMonth, firstDay,0,0,0);
		lastDate.set(lastYear, lastMonth, lastDay,0,0,0);

		if (i == 0) { //Month
			return (lastYear - firstYear)*12 + (lastMonth - firstMonth) + 1;
		}else{ //Day
			long dday = lastDate.getTimeInMillis() - firstDate.getTimeInMillis();
			dday= dday/(24*60*60*1000);

			return (int) dday+1;
		}
	}

	private int getIndex(Calendar firstDate, Calendar nowDate, int i) {
		int firstYear = firstDate.get(Calendar.YEAR);
		int firstMonth = firstDate.get(Calendar.MONTH);
		int firstDay = firstDate.get(Calendar.DAY_OF_MONTH);
		int nowYear = nowDate.get(Calendar.YEAR);
		int nowMonth = nowDate.get(Calendar.MONTH);
		int nowDay = nowDate.get(Calendar.DAY_OF_MONTH);

		firstDate.set(firstYear, firstMonth, firstDay,0,0,0);
		nowDate.set(nowYear, nowMonth, nowDay,0,0,0);

		if (i == 0) { //Month
			return (nowYear - firstYear)*12 + (nowMonth - firstMonth);
		}else{ //Day
			long dday = nowDate.getTimeInMillis() - firstDate.getTimeInMillis();
			dday= dday/(24*60*60*1000);

			return (int) dday;
		}
	}


}
