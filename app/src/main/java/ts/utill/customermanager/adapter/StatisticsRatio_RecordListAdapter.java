package ts.utill.customermanager.adapter;

import java.util.ArrayList;

import ts.utill.customermanager.R;
import ts.utill.customermanager.StatisticsRatioActivity;
import ts.utill.customermanager.data.CustomerDB;
import ts.utill.customermanager.data.Statistics_Ratio_Record;

import android.content.Context;
import android.sax.RootElement;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StatisticsRatio_RecordListAdapter extends BaseAdapter {
	Context maincon;
	StatisticsRatioActivity statisticsRatioActivity;
	LayoutInflater Inflater;
	
	CustomerDB customerDB = CustomerDB.getInstence();
	
	ArrayList<Statistics_Ratio_Record> arSrc;
	
	public StatisticsRatio_RecordListAdapter(Context context, ArrayList<Statistics_Ratio_Record> recordlist) {
		this.maincon = context;
		this.statisticsRatioActivity  = (StatisticsRatioActivity) context;
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
			res = R.layout.statistics_ratio_recordlist;
			convertView = Inflater.inflate(res, parent, false);
		}	

		TextView textView_statistics_ratio_recordlist_rank = (TextView)convertView.findViewById(R.id.textView_statistics_ratio_recordlist_rank);
		TextView textView_statistics_ratio_recordlist_name = (TextView)convertView.findViewById(R.id.textView_statistics_ratio_recordlist_name);
		TextView textView_statistics_ratio_recordlist_value = (TextView)convertView.findViewById(R.id.textView_statistics_ratio_recordlist_value);
		TextView textView_statistics_ratio_recordlist_ration = (TextView)convertView.findViewById(R.id.textView_statistics_ratio_recordlist_ration);
		
		textView_statistics_ratio_recordlist_rank.setText(position+1 +"");
		textView_statistics_ratio_recordlist_name.setText(arSrc.get(position).getName());
		textView_statistics_ratio_recordlist_value.setText(customerDB.toThousand(arSrc.get(position).getValue()));
		textView_statistics_ratio_recordlist_ration.setText(getRation(position) + "%");	
	
		return convertView;
	}
	
	private int getRation(int position){
		long total = 0;
		for (Statistics_Ratio_Record record : arSrc) {
			total+=record.getValue();
		}
		return (int) (arSrc.get(position).getValue()/(float)total * 100);
	}
	
}
