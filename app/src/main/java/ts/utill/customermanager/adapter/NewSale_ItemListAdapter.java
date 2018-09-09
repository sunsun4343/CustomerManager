package ts.utill.customermanager.adapter;

import java.util.ArrayList;

import ts.utill.customermanager.ItemListActivity;
import ts.utill.customermanager.NewSaleActivity;
import ts.utill.customermanager.R;
import ts.utill.customermanager.data.CustomerDB;
import ts.utill.customermanager.data.Item;
import ts.utill.customermanager.data.ItemGroup;
import ts.utill.customermanager.data.ItemSet;
import ts.utill.customermanager.data.SaleItemView;

import android.content.Context;
import android.graphics.Point;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewSale_ItemListAdapter extends BaseAdapter {
		Context maincon;
		NewSaleActivity newSaleActivity;
		LayoutInflater Inflater;
		
		boolean isModeNew;
		//-int[] init = null;
		
		CustomerDB customerDB;
		ArrayList<SaleItemView> arSrc;
		
		long UnVisibleAmount = 0;
		
		ImageView imageView_newSale_plus;
		ImageView imageView_newSale_minus;
		TextView textView_newSale_amount;
		
		
		public NewSale_ItemListAdapter(Context context) {
			this.isModeNew = true;
			this.maincon = context;
			this.newSaleActivity  = (NewSaleActivity) context;
			this.customerDB = CustomerDB.getInstence();
			this.arSrc = InitArSrc();
			//this.ItemCheck = new boolean[customerDB.getItemList().size()];
			//this.selectItem = new ArrayList<Item>();
			Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			Log.d("TestG", "make 1");
		}

		public NewSale_ItemListAdapter(Context context, ArrayList<ItemSet> saleSrc) {
			this.isModeNew = false;
			this.maincon = context;
			this.newSaleActivity  = (NewSaleActivity) context;
			this.customerDB = CustomerDB.getInstence();
			this.arSrc = LoadArSrc();
			Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			newSaleActivity.Refresh_TextView_Amount(calcAmount());
			
			Log.d("TestG", "make 2");
			
		}
		


		@Override
		public int getCount() {
			return arSrc.size();
		}

		@Override
		public SaleItemView getItem(int position) {
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
			//Log.d("TestG", "position " + position);
			//Log.d("TestG", "convertView " + convertView);
			
			//if(convertView == null)
			{
				int res =0;
				if(arSrc.get(position).getIsGroup()){
					res = R.layout.newsale_itemlist_group;
				}else{
					res = R.layout.newsale_itemlist;
				}
				
				convertView = Inflater.inflate(res, parent, false);
			}	
			
			if (arSrc.get(position).getIsGroup()) {
				final ItemGroup group = arSrc.get(position).getGroup();
				
				TextView textView_newSale_groupName = (TextView)convertView.findViewById(R.id.textView_newSale_groupName);
				
				textView_newSale_groupName.setText(group.getName());
				
				ImageView imageView_newSale_State = (ImageView)convertView.findViewById(R.id.imageView_newSale_State);
				
				if (arSrc.get(position).getIsState()) {
					imageView_newSale_State.setImageResource(R.drawable.open);
				}else{
					imageView_newSale_State.setImageResource(R.drawable.close);
				}
				
				RelativeLayout layout_newSale_group =(RelativeLayout)convertView.findViewById(R.id.layout_newSale_group);
				layout_newSale_group.setOnClickListener(new RelativeLayout.OnClickListener(){
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
				final ItemSet itemset = arSrc.get(position).getItemSet();
				
				RelativeLayout layout_newSale_item = (RelativeLayout)convertView.findViewById(R.id.layout_newSale_item);
				
				TextView textView_newSale_number = (TextView)convertView.findViewById(R.id.textView_newSale_number);
				TextView textView_newSale_ItemName = (TextView)convertView.findViewById(R.id.textView_newSale_ItemName);
				TextView textView_newSale_price = (TextView)convertView.findViewById(R.id.textView_newSale_price);
				
				imageView_newSale_plus = (ImageView)convertView.findViewById(R.id.imageView_newSale_plus);
				imageView_newSale_minus = (ImageView)convertView.findViewById(R.id.ImageView_newSale_minus);
				textView_newSale_amount = (TextView)convertView.findViewById(R.id.TextView_newSale_amount);
				
				textView_newSale_number.setText(itemset.getItem().getIdx_item()+"");
				textView_newSale_ItemName.setText(itemset.getItem().getName());
				textView_newSale_price.setText(CustomerDB.toThousand(itemset.getItem().getPrice()));		
				
				textView_newSale_amount.setText(itemset.getAmount()+"");
				
				imageView_newSale_plus.setOnClickListener(new ImageView.OnClickListener(){
					public void onClick(View v){
						itemset.setAmount(itemset.getAmount() + 1);
						//ItemAmount[position] = ItemAmount[position] + 1;
					
						newSaleActivity.Refresh_TextView_Amount(calcAmount());
						
						notifyDataSetChanged();
					}
				});
				imageView_newSale_minus.setOnClickListener(new ImageView.OnClickListener(){
					public void onClick(View v){
						if(itemset.getAmount() > 0 ){
							itemset.setAmount(itemset.getAmount() - 1);
							//ItemAmount[position] = ItemAmount[position] - 1;
							
							newSaleActivity.Refresh_TextView_Amount(calcAmount());
							
							notifyDataSetChanged();							
						}
					}
				});	
				
				if (arSrc.get(position).getIsState()) {
					layout_newSale_item.setVisibility(View.VISIBLE);
					textView_newSale_number.setVisibility(View.VISIBLE);
					textView_newSale_ItemName.setVisibility(View.VISIBLE);
					textView_newSale_price.setVisibility(View.VISIBLE);
					imageView_newSale_plus.setVisibility(View.VISIBLE);
					imageView_newSale_minus.setVisibility(View.VISIBLE);
					textView_newSale_amount.setVisibility(View.VISIBLE);
					
					
				}else{
					layout_newSale_item.setVisibility(View.GONE);
					textView_newSale_number.setVisibility(View.GONE);
					textView_newSale_ItemName.setVisibility(View.GONE);
					textView_newSale_price.setVisibility(View.GONE);
					imageView_newSale_plus.setVisibility(View.GONE);
					imageView_newSale_minus.setVisibility(View.GONE);
					textView_newSale_amount.setVisibility(View.GONE);
				}				
				
			}
			

				
			return convertView;
		}

		public long calcAmount() {
			long sum = UnVisibleAmount;
			
			for (SaleItemView view : arSrc) {
				if(!view.getIsGroup()){
					sum+= view.getItemSet().getAmount() * view.getItemSet().getItem().getPrice();
				}
			}
			
			return sum;
		}
		
		public ArrayList<ItemSet> getArSrc(){
			ArrayList<ItemSet> itemSet = new ArrayList<ItemSet>();
			
			for (SaleItemView view : arSrc) {
				if(!view.getIsGroup()){
					itemSet.add(view.getItemSet());
				}
			}
			
			return itemSet;
		}
		
		
		private ArrayList<SaleItemView> InitArSrc(){
			ArrayList<SaleItemView> ItemViewList = new ArrayList<SaleItemView>();
			for (ItemGroup group : customerDB.getItemGroupList()) {
				ItemViewList.add(new SaleItemView(group));
				
				boolean isGroupNull = false;
				for (Item item : customerDB.getItemList()) {
					if(item.getIdx_Group() == group.getIdx_Group()){
						if(!item.getUnvisible()){
							ItemViewList.add(new SaleItemView(new ItemSet(item, 0)));
							isGroupNull = true;
						}
					}
				}
				
				if(!isGroupNull){
					ItemViewList.remove(ItemViewList.size()-1);
				}
			}
			
			if(ItemViewList.size() > 0){
				ItemViewList.add(new SaleItemView(new ItemGroup(0, "�̺з�")));
			}else{
				
			}
			
			boolean isGroupNull = false;
			for (Item item : customerDB.getItemList()) {
				if(item.getIdx_Group() == 0){
					if(!item.getUnvisible()){
						ItemViewList.add(new SaleItemView(new ItemSet(item,0)));
						isGroupNull = true;
					}
				}
			}
			
			if(!isGroupNull){
				ItemViewList.remove(ItemViewList.size()-1);
			}

			
			if(ItemViewList.get(0).getIsGroup()){
				for (SaleItemView saleItemView : ItemViewList) {
					saleItemView.setSate(false);
				}
			}else{
				for (SaleItemView saleItemView : ItemViewList) {
					saleItemView.setSate(true);
				}
			}
			
			return ItemViewList;
		}
		
		private ArrayList<SaleItemView> LoadArSrc(){
			ArrayList<SaleItemView> baseSrc = InitArSrc();
			ArrayList<ItemSet> saleSrc = newSaleActivity.getItemSetList();
			
			
			long amount = 0;
			
			for (ItemSet itemSet : saleSrc) {
				boolean overlap = false;
				for (SaleItemView view : baseSrc) {
					if(!view.getIsGroup()){
						if(view.getItemSet().getItem().getIdx_item() == itemSet.getItem().getIdx_item()){
							overlap = true;
							view.getItemSet().setAmount(itemSet.getAmount());
							break;
						}
					}
				}
				
				if (!overlap) {
					amount += itemSet.getAmount() * itemSet.getItem().getPrice();
				}
			}
			
			UnVisibleAmount = amount;

			return baseSrc;
			
		}
		
		/*
		private ArrayList<ItemSet> LoadArSrc(){
			ArrayList<ItemSet> baseSrc = new ArrayList<ItemSet>();
			ArrayList<ItemSet> saleSrc = newSaleActivity.getItemSetList();
			
			for (Item item : customerDB.getItemList()) {
				boolean overlap = false;
				for (ItemSet itemSet : saleSrc) {
					if(item.getIdx_item() == itemSet.getItem().getIdx_item()){
						overlap = true;
						baseSrc.add(new ItemSet(item,itemSet.getAmount()));
						break;
					}
				}
				if(!overlap){
					baseSrc.add(new ItemSet(item,0));
				}
			}
			
			ItemAmount = new int[customerDB.getItemList().size()];
			for (int i = 0; i < ItemAmount.length; i++) {
				ItemAmount[i] = baseSrc.get(i).getAmount();
			}
			
			return baseSrc;
			
		}
		 * 
		 */
		
		
		void NotifyDataSetChanged(){
			this.notifyDataSetChanged();
		}
		
}
