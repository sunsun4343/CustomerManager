package ts.utill.customermanager;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TooManyListenersException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import ts.utill.customermanager.data.UserSetting;

public class OtherSettingActivity extends Activity {

	UserSetting userSetting = UserSetting.getInstance();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_othersetting);
	
		ToggleButton toggleButton_otherSetting_ageView = (ToggleButton)findViewById(R.id.toggleButton_otherSetting_ageView);
		toggleButton_otherSetting_ageView.setChecked(userSetting._AgeNogView);
		
		toggleButton_otherSetting_ageView.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				userSetting._AgeNogView = isChecked;
				SharedPreferences pref = getSharedPreferences("Pref_CustomerManager",0);
				SharedPreferences.Editor edit = pref.edit();
				edit.putBoolean("agenotview", isChecked);
				edit.commit();
			}
		});
		
		
		ToggleButton toggleButton_otherSetting_groupView = (ToggleButton)findViewById(R.id.toggleButton_otherSetting_groupView);
		toggleButton_otherSetting_groupView.setChecked(userSetting._GroupNoView);
		
		toggleButton_otherSetting_groupView.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				userSetting._GroupNoView = isChecked;
				SharedPreferences pref = getSharedPreferences("Pref_CustomerManager",0);
				SharedPreferences.Editor edit = pref.edit();
				edit.putBoolean("groupnotview", isChecked);
				edit.commit();
			}
		});
		
	}
	
	
	
	
	
}
