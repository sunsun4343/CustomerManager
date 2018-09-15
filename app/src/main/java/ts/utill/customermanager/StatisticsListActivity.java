package ts.utill.customermanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import ts.utill.customermanager.data.CustomerDB;

public class StatisticsListActivity extends AppCompatActivity{
	CustomerDB customerDB = CustomerDB.getInstence();
	
	ArrayAdapter<String> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statisticslist);
		
        ListView listView_settingList = (ListView)findViewById(R.id.listView_statistics);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.statistics));
        listView_settingList.setAdapter(adapter);
        
        listView_settingList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					Intent_StatisticsVariation();
					break;
				case 1:
					Intent_StatisticsRatio();
					break;
				}
			}


        });
	}
	
	private void Intent_StatisticsRatio(){
		Intent intent = new Intent(StatisticsListActivity.this, StatisticsRatioActivity.class);
		this.startActivity(intent);		
	}
	
	private void Intent_StatisticsVariation() {
		Intent intent = new Intent(StatisticsListActivity.this, StatisticsVariationActivity.class);
		this.startActivity(intent);	
	}	
	
}