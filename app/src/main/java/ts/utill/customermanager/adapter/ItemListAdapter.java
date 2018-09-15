package ts.utill.customermanager.adapter;

import java.util.ArrayList;

import ts.utill.customermanager.ItemListActivity;
import ts.utill.customermanager.R;
import ts.utill.customermanager.data.CustomerDB;
import ts.utill.customermanager.data.Item;
import ts.utill.customermanager.data.ItemGroup;
import ts.utill.customermanager.data.ItemView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ItemListAdapter  extends BaseAdapter {
	Context maincon;
	ItemListActivity itemListActivity;
	LayoutInflater Inflater;

	CustomerDB customerDB;
	ArrayList<ItemView> arSrc;

	ArrayAdapter<String> adapter_group;

	public ItemListAdapter(Context context) {
		this.maincon = context;
		this.itemListActivity  = (ItemListActivity) context;
		this.customerDB = CustomerDB.getInstence();
		this.arSrc = Refresh_arSrc();
		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return arSrc.size();
	}

	@Override
	public ItemView getItem(int position) {
		return arSrc.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getViewTypeCount(){
		return 2;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		//if(convertView == null)
		{
			int res =0;
			if (arSrc.get(position).getIsGroup()) {
				res = ts.utill.customermanager.R.layout.item_list_group;
			}else{
				res = ts.utill.customermanager.R.layout.item_list_item;
			}
			convertView = Inflater.inflate(res, parent, false);
		}

		if (arSrc.get(position).getIsGroup()) {

			final ItemGroup arGroup = arSrc.get(position).getGroup();

			ImageView imageView_itemList_group_state = (ImageView)convertView.findViewById(R.id.imageView_itemList_group_state);

			if (arSrc.get(position).getIsState()) {
				imageView_itemList_group_state.setImageResource(R.drawable.open);
			}else{
				imageView_itemList_group_state.setImageResource(R.drawable.close);
			}

			TextView textView_itemList_group_name = (TextView)convertView.findViewById(R.id.textView_itemList_group_name);

			textView_itemList_group_name.setText(arGroup.getName());

			ImageView imageView_itemList_group_edit = (ImageView)convertView.findViewById(R.id.imageView_itemList_group_edit);
			imageView_itemList_group_edit.setOnClickListener(new ImageView.OnClickListener(){
				public void onClick(View arg0) {
					final RelativeLayout layout = (RelativeLayout)View.inflate(itemListActivity, R.layout.itemlist_dialog_newgroup, null);

					final TextView textView_itemList_dialog_groupNumber = (TextView)layout.findViewById(R.id.textView_itemList_dialog_groupNumber);
					final EditText editText_itemList_dialog_groupName = (EditText)layout.findViewById(R.id.editText_itemList_dialog_groupName);

					textView_itemList_dialog_groupNumber.setText(arGroup.getIdx_Group()+"");
					editText_itemList_dialog_groupName.setText(arGroup.getName());


					new AlertDialog.Builder(itemListActivity)
							.setTitle(R.string.editgroup)
							.setIcon(R.drawable.group)
							.setView(layout)
							.setPositiveButton(R.string.edit,new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {

									String newName = editText_itemList_dialog_groupName.getText().toString();

									customerDB.Update_ItemGroup(arGroup, new ItemGroup(arGroup.getIdx_Group(), newName));

									//Refresh
									NotifyDataSetChanged();
								}
							})
							.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							})
							.show();
				}
			});

			ImageView imageView_itemList_group_delete = (ImageView)convertView.findViewById(R.id.imageView_itemList_group_delete);
			imageView_itemList_group_delete.setOnClickListener(new ImageView.OnClickListener(){
				public void onClick(View arg0) {
					new AlertDialog.Builder(itemListActivity)
							.setTitle(R.string.warning)
							.setIcon(R.mipmap.warning)
							.setMessage(R.string.groupdel_Q)
							.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {

									for (int i = position+1; i < arSrc.size(); i++) {
										if(arSrc.get(i).getIsGroup()){
											break;
										}

										Item t = arSrc.get(i).getItem();
										customerDB.Update_Item(t, new Item(t.getIdx_item(), 0 , t.getName(), t.getPrice(), t.getUnvisible()));
									}

									customerDB.Delete_ItemGroup(arSrc.get(position).getGroup());

									//Refresh
									resetArSrc();
									NotifyDataSetChanged();

								}

							})
							.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							})
							.show();
				}
			});


			if (arGroup.getIdx_Group() == 0) {
				imageView_itemList_group_edit.setVisibility(View.GONE);
				imageView_itemList_group_delete.setVisibility(View.GONE);
			}


			LinearLayout layout_itemList_group = (LinearLayout)convertView.findViewById(R.id.layout_itemList_group);
			layout_itemList_group.setOnClickListener(new LinearLayout.OnClickListener(){
				public void onClick(View arg0) {

					arSrc.get(position).turnState();
					for (int i = position+1; i < arSrc.size(); i++) {
						if(arSrc.get(i).getIsGroup()){
							break;
						}
						arSrc.get(i).turnState();
					}

					//Refresh
					NotifyDataSetChanged();
				}
			});


		}else{

			final Item arItem = arSrc.get(position).getItem();

			TextView textView_itemList_number = (TextView)convertView.findViewById(R.id.textView_itemList_number);
			TextView textView_itemList_name = (TextView)convertView.findViewById(R.id.textView_itemList_name);
			TextView textView_itemList_price = (TextView)convertView.findViewById(R.id.textView_itemList_price);

			textView_itemList_number.setText(arItem.getIdx_item() + "");
			textView_itemList_name.setText(arItem.getName());
			textView_itemList_price.setText(CustomerDB.toThousand(arItem.getPrice()));

			if (arItem.getUnvisible()) {
				textView_itemList_name.setTextColor(itemListActivity.getResources().getColor(R.color.gray));
				textView_itemList_price.setTextColor(itemListActivity.getResources().getColor(R.color.gray));
			}else {
				textView_itemList_name.setTextColor(itemListActivity.getResources().getColor(R.color.black));
				textView_itemList_price.setTextColor(itemListActivity.getResources().getColor(R.color.blue));
			}

			ArrayList<ItemGroup> ItemGroupList = customerDB.getItemGroupList();
			String groupname[]  = new String[ItemGroupList.size() + 1];

			groupname[0] = new String("미분류");
			for (int i = 0; i < ItemGroupList.size(); i++) {
				groupname[i+1] = ItemGroupList.get(i).getName();
			}

			adapter_group = new ArrayAdapter<String>(itemListActivity, android.R.layout.simple_spinner_item, groupname);


			RelativeLayout layout_itemList_item = (RelativeLayout)convertView.findViewById(R.id.layout_itemList_item);
			layout_itemList_item.setOnClickListener(new RelativeLayout.OnClickListener(){
				public void onClick(View v){
					//final Item item = customerDB.getItem(arItem.getIdx_item());

					final RelativeLayout layout = (RelativeLayout)View.inflate(itemListActivity, R.layout.itemlist_dialog_newitem, null);

					final TextView textView_itemList_dialog_number = (TextView)layout.findViewById(R.id.textView_itemList_dialog_number);
					final EditText editText_itemList_dialog_name = (EditText)layout.findViewById(R.id.editText_itemList_dialog_name);
					final EditText editText_itemList_dialog_price = (EditText)layout.findViewById(R.id.editText_itemList_dialog_price);

					textView_itemList_dialog_number.setText(arItem.getIdx_item() + "");
					editText_itemList_dialog_name.setText(arItem.getName());
					editText_itemList_dialog_price.setText(arItem.getPrice() + "");

					final Spinner spinner_itemList_dialog_group = (Spinner)layout.findViewById(R.id.spinner_itemList_dialog_group);

					adapter_group.setDropDownViewResource(android.R.layout.simple_list_item_1);
					spinner_itemList_dialog_group.setAdapter(adapter_group);

					int selection = 0;
					for (int i = 0; i < customerDB.getItemGroupList().size(); i++) {
						if(customerDB.getItemGroupList().get(i).getIdx_Group() == arItem.getIdx_Group()){
							selection = i +1;
							break;
						}
					}

					spinner_itemList_dialog_group.setSelection(selection);

					final CheckBox checkBox_itemList_dialog_unvisible = (CheckBox)layout.findViewById(R.id.checkBox_itemList_dialog_unvisible);

					checkBox_itemList_dialog_unvisible.setChecked(arItem.getUnvisible());


					new AlertDialog.Builder(itemListActivity)
							.setTitle(R.string.edititem)
							.setIcon(R.drawable.item)
							.setView(layout)
							.setPositiveButton(R.string.edit,new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									String name = editText_itemList_dialog_name.getText().toString();

									long price;
									if(editText_itemList_dialog_price.getText().toString().equals("")){
										price =0;
									}else{
										price = Long.parseLong(editText_itemList_dialog_price.getText().toString());
									}

									int idx_group = 0;
									int selection = spinner_itemList_dialog_group.getSelectedItemPosition();
									if(selection > 0){
										idx_group = customerDB.getItemGroupList().get(spinner_itemList_dialog_group.getSelectedItemPosition() -1).getIdx_Group();
									}

									customerDB.Update_Item(arItem,new Item(arItem.getIdx_item(), idx_group, name, price, checkBox_itemList_dialog_unvisible.isChecked()));

									//Refresh
									resetArSrc();
									NotifyDataSetChanged();
								}
							})
							.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							})
							.show();
				}
			});

			if (arSrc.get(position).getIsState()) {
				layout_itemList_item.setVisibility(View.VISIBLE);
				textView_itemList_name.setVisibility(View.VISIBLE);
				textView_itemList_number.setVisibility(View.VISIBLE);
				textView_itemList_price.setVisibility(View.VISIBLE);
			}else{
				layout_itemList_item.setVisibility(View.GONE);
				textView_itemList_name.setVisibility(View.GONE);
				textView_itemList_number.setVisibility(View.GONE);
				textView_itemList_price.setVisibility(View.GONE);
			}
		}


		return convertView;
	}

	public void NotifyDataSetChanged(){
		this.notifyDataSetChanged();
	}

	public void resetArSrc(){
		arSrc = Refresh_arSrc();
	}

	ArrayList<ItemView> Refresh_arSrc() {

		ArrayList<ItemView> ItemViewList = new ArrayList<ItemView>();
		for (ItemGroup group : customerDB.getItemGroupList()) {
			ItemViewList.add(new ItemView(group));

			for (Item item : customerDB.getItemList()) {
				if(item.getIdx_Group() == group.getIdx_Group()){
					ItemViewList.add(new ItemView(item));
				}
			}
		}
		ItemViewList.add(new ItemView(new ItemGroup(0, "미분류")));
		for (Item item : customerDB.getItemList()) {
			if(item.getIdx_Group() == 0){
				ItemViewList.add(new ItemView(item));
			}
		}
		return ItemViewList;
	}
}
