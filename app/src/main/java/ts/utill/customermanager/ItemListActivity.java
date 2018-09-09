package ts.utill.customermanager;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import ts.utill.customermanager.adapter.ItemListAdapter;
import ts.utill.customermanager.data.CustomerDB;
import ts.utill.customermanager.data.Item;
import ts.utill.customermanager.data.ItemGroup;
import ts.utill.customermanager.data.ItemView;

public class ItemListActivity extends AppCompatActivity {

	ItemListAdapter itemListAdapter;
	CustomerDB customerDB = CustomerDB.getInstence();
	
	ArrayList<ItemView> ItemViewList = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);
		
		Refesh_ItemViewList();
		
		//list connect
		itemListAdapter = new ItemListAdapter(this);
		ListView listView_itmeList;
		listView_itmeList = (ListView)findViewById(R.id.listView_itmeList);
		listView_itmeList.setAdapter(itemListAdapter);
		
	}
	
	public void Refesh_ItemViewList() {
		
		ItemViewList = new ArrayList<ItemView>();
		for (ItemGroup group : customerDB.getItemGroupList()) {
			ItemViewList.add(new ItemView(group));
			
			for (Item item : customerDB.getItemList()) {
				if(item.getIdx_Group() == group.getIdx_Group()){
					ItemViewList.add(new ItemView(item));
				}
			}
		}
		ItemViewList.add(new ItemView(new ItemGroup(0, "�̺з�")));
		for (Item item : customerDB.getItemList()) {
			if(item.getIdx_Group() == 0){
				ItemViewList.add(new ItemView(item));
			}
		}
	}

	public ArrayList<ItemView> getItemViewList(){
		return ItemViewList;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.item_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_newItem) {
			final RelativeLayout layout = (RelativeLayout)View.inflate(ItemListActivity.this, R.layout.itemlist_dialog_newitem, null);
			
			final TextView textView_itemList_dialog_number = (TextView)layout.findViewById(R.id.textView_itemList_dialog_number);
			final EditText editText_itemList_dialog_name = (EditText)layout.findViewById(R.id.editText_itemList_dialog_name);
			final EditText editText_itemList_dialog_price = (EditText)layout.findViewById(R.id.editText_itemList_dialog_price);
			
			textView_itemList_dialog_number.setText("");
			
			final Spinner spinner_itemList_dialog_group = (Spinner)layout.findViewById(R.id.spinner_itemList_dialog_group);
			
			ArrayList<ItemGroup> ItemGroupList = customerDB.getItemGroupList();
			String groupname[]  = new String[ItemGroupList.size() + 1];
			
			groupname[0] = new String("�̺з�");
			for (int i = 0; i < ItemGroupList.size(); i++) {
				groupname[i+1] = ItemGroupList.get(i).getName();
			}
			
			ArrayAdapter<String> adapter_group = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupname);
			adapter_group.setDropDownViewResource(android.R.layout.simple_list_item_1);
			spinner_itemList_dialog_group.setAdapter(adapter_group);
			
			final CheckBox checkBox_itemList_dialog_unvisible = (CheckBox)layout.findViewById(R.id.checkBox_itemList_dialog_unvisible);
			
			new AlertDialog.Builder(ItemListActivity.this)
			.setTitle(R.string.newitem)
			.setIcon(R.drawable.newitem)
			.setView(layout)
			.setPositiveButton(R.string.register,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					long price;
					if(editText_itemList_dialog_price.getText().toString().equals("")){
						price =0;
					}else{
						price = Long.parseLong(editText_itemList_dialog_price.getText().toString());
					}
					
					int idx_group = 0;
					int position = spinner_itemList_dialog_group.getSelectedItemPosition();
					if(position > 0){
						idx_group = customerDB.getItemGroupList().get(spinner_itemList_dialog_group.getSelectedItemPosition() -1).getIdx_Group();
					}
					
					customerDB.Insert_Item(idx_group, editText_itemList_dialog_name.getText().toString(), price, checkBox_itemList_dialog_unvisible.isChecked());
					
					//Refresh
					itemListAdapter.resetArSrc();
					itemListAdapter.NotifyDataSetChanged();
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();	
			return true;
		}else if(id == R.id.action_newGroup){
			final RelativeLayout layout = (RelativeLayout)View.inflate(ItemListActivity.this, R.layout.itemlist_dialog_newgroup, null);
			
			final TextView textView_itemList_dialog_groupNumber = (TextView)layout.findViewById(R.id.textView_itemList_dialog_groupNumber);
			final EditText editText_itemList_dialog_groupName = (EditText)layout.findViewById(R.id.editText_itemList_dialog_groupName);
			
			textView_itemList_dialog_groupNumber.setText("");
			
			new AlertDialog.Builder(ItemListActivity.this)
			.setTitle(R.string.action_newGroup)
			.setIcon(R.drawable.newgroup)
			.setView(layout)
			.setPositiveButton(R.string.register,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
					customerDB.Insert_ItemGroup(editText_itemList_dialog_groupName.getText().toString());

					//Refresh
					itemListAdapter.resetArSrc();
					itemListAdapter.NotifyDataSetChanged();
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
}
