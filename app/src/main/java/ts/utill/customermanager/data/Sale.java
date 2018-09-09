package ts.utill.customermanager.data;

import java.util.ArrayList;
import java.util.Calendar;

import android.util.Log;

public class Sale implements Cloneable {
	private int idx_sale;
	private int idx_customer;
	private Calendar saledate;
	
	//private ArrayList<Item> SaleItem;
	private ArrayList<ItemSet> SaleItem;
	
	private String memo;
	
	public Sale(int idx_sale,int idx_customer, Calendar saledate, ArrayList<ItemSet> saleitem, String memo) {
		this.idx_sale = idx_sale;
		this.idx_customer = idx_customer;
		this.saledate = saledate;
		this.SaleItem = saleitem;
		this.memo = memo;
	}
	
	public Sale(Calendar saledate, ArrayList<ItemSet> saleitem, String memo) {
		this.saledate = saledate;
		this.SaleItem = saleitem;
		this.memo = memo;
	}
	
	public void setIdx_Sale(int idx_sale){
		this.idx_sale = idx_sale;
	}

	public long getTotalPrice(){
		long sum = 0;
		for (ItemSet t : SaleItem) {
			sum += t.getItem().getPrice() * t.getAmount();
		}
		return sum;
	}

	public Calendar getDate() {
		return saledate;
	}

	public CharSequence getItemList() {
		String temp = "";
		for (ItemSet itemset : SaleItem) {
			if(itemset.getAmount() > 0){
				temp += itemset.getItem().getIdx_item();
				temp+=CustomerDB.itemlist_split_amount;
				temp+=itemset.getAmount();
				
				if(itemset.getItem().getIdx_item() != SaleItem.get(SaleItem.size()-1).getItem().getIdx_item()){
					temp+=CustomerDB.itemlist_split;
				}	
			}
		}
		
		Log.d("TestG", "getItemList() " + temp);
		
		return temp;
	}
	
	public static CharSequence getItemList(ArrayList<ItemSet> saleitem) {
		String temp = "";
		
		for (ItemSet itemset : saleitem) {
			if(itemset.getAmount() > 0){
				temp += itemset.getItem().getIdx_item();
				temp+=CustomerDB.itemlist_split_amount;
				temp+=itemset.getAmount();
				
				if(itemset.getItem().getIdx_item() != saleitem.get(saleitem.size()-1).getItem().getIdx_item()){
					temp+=CustomerDB.itemlist_split;
				}		
			}
		}
		
		Log.d("TestG", "getItemList(saleitem) " + temp);
		
		return temp;
	}

	public int getIsMemo() {
		if(this.memo.equals(""))
			return 0;
		return 1;
	}

	public CharSequence getMemo() {
		return this.memo;
	}

	public int getIdx_Sale() {
		return this.idx_sale;
	}

	public CharSequence getItemListName() {
		
		
		
		String temp = "";
		UserSetting setting = UserSetting.getInstance();
		CustomerDB customeDb = CustomerDB.getInstence();
		
		for (ItemSet itemset : SaleItem) {
			if(itemset.getAmount() > 0){
				
				if (setting._GroupNoView) {
					
					if (itemset.getItem().idx_group == 0) {
						temp += "[�̺з�]";
					}else{
						temp += "[";
						temp += customeDb.getGroup(itemset.getItem().idx_group).name;
						temp += "]";
					}
					
					
					
				}
				
				
				temp += itemset.getItem().getName();
				if(itemset.getAmount() > 1){
					temp+="x";
					temp+=itemset.getAmount();
				}
				if(itemset.getItem().getIdx_item() != SaleItem.get(SaleItem.size()-1).getItem().getIdx_item()){
					temp+=", ";
				}				
			}
		}
		
		//Log.d("TestG", "getItemListName() " + temp);
		
		return temp;
	}
	
	public ArrayList<ItemSet> getSaleItem(){
		return this.SaleItem;
	}

	public void setDate(Calendar calendar) {
		this.saledate = calendar;
	}

	public void setMemo(String string) {
		this.memo = string;
	}

	public void setItemList(ArrayList<ItemSet> itemList) {
		this.SaleItem = itemList;
	}
	
	public Object clone(){
		try{
		   return super.clone();
		}catch(CloneNotSupportedException e){
		   return null;
		}
	}

	public int getIdx_customer() {
		return this.idx_customer;
	}
	
}
