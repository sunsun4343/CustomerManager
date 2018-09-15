package ts.utill.customermanager.data;

public class ItemSet {
	Item item;
	int amount;
	
	public ItemSet(Item item, int amount){
		this.item = item;
		this.amount = amount;
	}
	
	public Item getItem(){
		return item;
	}
	
	public int getAmount(){
		return amount;
	}
	
	public void setAmount(int amount){
		this.amount = amount;
	}
}
