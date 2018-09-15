package ts.utill.customermanager.data;

public class ItemView {
	
	boolean isGroup;
	boolean isState;
	Item item;
	ItemGroup group;
	
	public ItemView(Item item) {
		this.isGroup = false;
		this.isState = true;
		this.item = item;
		this.group = null;
		
	}
	
	public ItemView(ItemGroup group) {
		this.isGroup = true;
		this.isState = true;
		this.item = null;
		this.group = group;
	}

	public boolean getIsGroup() {
		return this.isGroup;
	}

	public Item getItem() {
		return this.item;
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
	
	
	
}
