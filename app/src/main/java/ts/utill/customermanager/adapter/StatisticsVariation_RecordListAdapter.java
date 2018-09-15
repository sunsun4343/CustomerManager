package ts.utill.customermanager.adapter;

import java.util.ArrayList;

import ts.utill.customermanager.R;
import ts.utill.customermanager.StatisticsRatioActivity;
import ts.utill.customermanager.StatisticsVariationActivity;
import ts.utill.customermanager.data.CustomerDB;
import ts.utill.customermanager.data.Statistics_Ratio_Record;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StatisticsVariation_RecordListAdapter  extends BaseAdapter {
	Context maincon;
	StatisticsVariationActivity statisticsVariationActivity;
	LayoutInflater Inflater;
	
	CustomerDB customerDB = CustomerDB.getInstence();
	
	ArrayList<Statistics_Ratio_Record> arSrc;
	
	public StatisticsVariation_RecordListAdapter(Context context, ArrayList<Statistics_Ratio_Record> recordlist) {
		this.maincon = context;
		this.statisticsVariationActivity  = (StatisticsVariationActivity) context;
		this.arSrc = recordlist;
		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return arSrc.size();
	}

	@Override
	public Statistics_Ratio_Record getItem(int position) {
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
			res = R.layout.statistics_variation_recordlist;
			convertView = Inflater.inflate(res, parent, false);
		}	

		TextView textView_statistics_variation_recordlist_date = (TextView)convertView.findViewById(R.id.textView_statistics_variation_recordlist_date);
		TextView textView_statistics_variation_recordlist_value = (TextView)convertView.findViewById(R.id.textView_statistics_variation_recordlist_value);
		
		textView_statistics_variation_recordlist_date.setText(arSrc.get(position).getName());
		textView_statistics_variation_recordlist_value.setText(customerDB.toThousand(arSrc.get(position).getValue()));
	
		return convertView;
	}
}
