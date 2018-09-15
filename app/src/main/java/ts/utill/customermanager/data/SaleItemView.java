package ts.utill.customermanager.data;

public class SaleItemView {
	
	boolean isGroup;
	boolean isState;
	ItemSet itemSet;
	ItemGroup group;
	
	public SaleItemView(ItemSet itemSet) {
		this.isGroup = false;
		this.isState = true;
		this.itemSet = itemSet;
		this.group = null;
		
	}
	
	public SaleItemView(ItemGroup group) {
		this.isGroup = true;
		this.isState = true;
		this.itemSet = null;
		this.group = group;
	}

	public boolean getIsGroup() {
		return this.isGroup;
	}

	public ItemSet getItemSet() {
		return this.itemSet;
	}

	public ItemGroup getGroup() {
		return this.group;
	}

	public boolean getIsState() {
		return this.isState;
	}

	public void turnState() {
		this.isState = !isState;
	}

	public void setSate(boolean b) {
		this.isState =b;
	}
	
	
}
