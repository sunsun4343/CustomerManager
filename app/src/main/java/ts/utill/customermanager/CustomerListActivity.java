package ts.utill.customermanager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.adfit.ads.AdListener;
import com.kakao.adfit.ads.ba.BannerAdView;

import ts.utill.customermanager.adapter.CustomerListAdapter;
import ts.utill.customermanager.data.CustomerDB;
import ts.utill.customermanager.data.UserSetting;

public class CustomerListActivity extends AppCompatActivity {

    private static final String LOGTAG = "BannerTypeXML1";
    private BannerAdView adView = null;

    public final static int VERSION_CODE = 18;
    final int NOTICE_NUM = 5;

    CustomerDB customerDB = CustomerDB.getInstence();
    UserSetting userSetting = UserSetting.getInstance();

    private static final int REQUEST_EXTERNAL_STORAGE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        initAdFit();

        Init_Setting();

        Init_View();

        if (ActivityCompat.checkSelfPermission(CustomerListActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerListActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        }
    }

//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
//    {
//        switch (requestCode) {
//            case REQUEST_EXTERNAL_STORAGE:
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                } else {
//                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
//                }
//                break;
//        }
//    }

    void Init_Setting(){
        SharedPreferences pref = getSharedPreferences("Pref_CustomerManager", 0);
        int versioncode = pref.getInt("version", VERSION_CODE-1);
        userSetting._AgeNogView = pref.getBoolean("agenotview", false);

        int notice = pref.getInt("notice", 0);
        if(notice < NOTICE_NUM){
            new AlertDialog.Builder(CustomerListActivity.this)
                    .setTitle(R.string.notice)
                    .setMessage(R.string.notice_context)
                    .setPositiveButton("다시보지 않음", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface arg0, int arg1) {
                            SharedPreferences pref = getSharedPreferences("Pref_CustomerManager", 0);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putInt("notice", NOTICE_NUM);
                            edit.commit();
                        }
                    })
                    .show();
        }


        //DB Seting
        if (versioncode < 13) {
            customerDB.ConvertDB(this);
        }else{
            customerDB.InitDB(this);
        }

        if (versioncode < VERSION_CODE) {
            customerDB.CSVExport(this,"AUTO-VerCode " + VERSION_CODE );
            customerDB.JsonExport(this,"AUTO-VerCode " + VERSION_CODE );
            Toast.makeText(CustomerListActivity.this, "어플리케이션 버전 업데이트로 인해 CSV가 자동 백업되었습니다.", Toast.LENGTH_LONG).show();
        }

        SharedPreferences.Editor edit = pref.edit();
        edit.putInt("version", VERSION_CODE);
        edit.commit();
    }

    TextView textView_customerList_customerCnt;
    Spinner spinner_customerList_sort;
    ArrayAdapter<CharSequence> spin_sort;
    CustomerListAdapter customerListAdapter;

    void Init_View(){
        //customerCnt
        textView_customerList_customerCnt = (TextView)findViewById(R.id.textView_customerList_customerCnt);
        textView_customerList_customerCnt.setText(customerDB.getCustomerCnt()+"");

        //spinner connect
        spinner_customerList_sort = (Spinner)findViewById(R.id.spinner_customerList_sort);
        spinner_customerList_sort.setPrompt("select");
        spin_sort = ArrayAdapter.createFromResource(this, R.array.sort, android.R.layout.simple_list_item_checked);
        spin_sort.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinner_customerList_sort.setAdapter(spin_sort);


        //list connect
        customerListAdapter = new CustomerListAdapter(this);

        ListView customerList_list;
        customerList_list = (ListView)findViewById(R.id.listView_customerList_list);
        //customerList_list.setDivider(new ColorDrawable(R.color.skyblue));
        //customerList_list.setDividerHeight(2);
        customerList_list.setAdapter(customerListAdapter);

        //Search
        final EditText editText_customerList_search = (EditText)findViewById(R.id.editText_customerList_search);
        editText_customerList_search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {		}
            public void onTextChanged(CharSequence s, int start, int before, int count) {		}
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")){
                    customerListAdapter.Search("");
                }else{
                    customerListAdapter.Search(s.toString());
                }
            }
        });

        //Sort
        AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editText_customerList_search.setText("");
                customerListAdapter.Sort(position);
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spinner_customerList_sort.setOnItemSelectedListener(mOnItemSelectedListener);

        //Chek Item List
        if(customerDB.getItemList().size() == 0){
            new AlertDialog.Builder(CustomerListActivity.this)
                    .setTitle(R.string.itemsetting)
                    .setIcon(R.drawable.item)
                    .setMessage(R.string.needitemsetting)
                    .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent_ItemListActivity();
                        }
                    })
                    .setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (adView != null) {
            adView.destroy();
            adView = null;
        }
    }

    private void toast(String message) {
        if (adView == null) return;
        Toast.makeText(adView.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void initAdFit() {
        // AdFit sdk 초기화 시작
        adView = (BannerAdView) findViewById(R.id.adview);
        adView.setClientId("DAN-1h7iozq3woyt4");  // 할당 받은 광고 단위(clientId) 설정
        adView.setAdListener(new AdListener() {  // 광고 수신 리스너 설정

            @Override
            public void onAdLoaded() {
                toast("Banner is loaded");
            }

            @Override
            public void onAdFailed(int errorCode) {
                toast("Failed to load banner :: errorCode = " + errorCode);
            }

            @Override
            public void onAdClicked() {
                toast("Banner is clicked");
            }

        });

        adView.loadAd();
    }


    //ActionBar Icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.customer_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_newCustomer) {
            Intent_CustomerViewActivity();
            return true;
        }else if (id == R.id.action_settings) {
            Intent_SettingActivity();
            return true;
        }else if (id == R.id.action_statistics){
            Intent_StatisticsListActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    //Menu Activity

    public void Intent_CustomerViewActivity(){
        Intent intent = new Intent(CustomerListActivity.this, CustomerViewActivity.class);
        intent.putExtra("modeNew",true);
        this.startActivityForResult(intent, 1);
    }

    private void Intent_SettingActivity() {
        Intent intent = new Intent(CustomerListActivity.this, SettingActivity.class);
        this.startActivityForResult(intent, 2);
    }

    private void Intent_StatisticsListActivity() {
        Intent intent = new Intent(CustomerListActivity.this, StatisticsListActivity.class);
        this.startActivity(intent);
    }

    //Activity

    public void Intent_CustomerViewActivity(int idx_customer){
        Intent intent = new Intent(CustomerListActivity.this, CustomerViewActivity.class);
        intent.putExtra("modeNew",false);
        intent.putExtra("idx_customer",idx_customer);
        this.startActivityForResult(intent, 0);
    }

    void Intent_ItemListActivity(){
        Intent intent = new Intent(CustomerListActivity.this, ItemListActivity.class);
        this.startActivityForResult(intent, 3);
    }

    //Result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    textView_customerList_customerCnt.setText(customerDB.getCustomerCnt()+"");
                    customerListAdapter.arSrcRefresh(spinner_customerList_sort.getSelectedItemPosition());
                    //customerListAdapter.notifyDataSetChanged();
                }
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    textView_customerList_customerCnt.setText(customerDB.getCustomerCnt()+"");
                    customerListAdapter.arSrcRefresh(spinner_customerList_sort.getSelectedItemPosition());
                    //customerListAdapter.notifyDataSetChanged();
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    textView_customerList_customerCnt.setText(customerDB.getCustomerCnt()+"");
                    customerListAdapter.arSrcRefresh(spinner_customerList_sort.getSelectedItemPosition());
                }
                break;
            case 3:
                if(resultCode == RESULT_OK){
                    textView_customerList_customerCnt.setText(customerDB.getCustomerCnt()+"");
                    customerListAdapter.arSrcRefresh(spinner_customerList_sort.getSelectedItemPosition());
                }
                break;
        }
    }




}
