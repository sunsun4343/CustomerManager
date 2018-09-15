package ts.utill.customermanager.data;

public class Item {
	int idx_item;
	int idx_group;
	String name;
	long price;
	boolean unvisible;
	
	public Item(int idx_item){
		this.idx_item = idx_item;
		this.idx_group = 0;
		this.name = "";
		this.price = 0;
		this.unvisible = false;
	}
	
	public Item(int idx_item, String name, long price) { // later delete
		this.idx_item = idx_item;
		this.name = name;
		this.price = price;
		this.idx_group = 0;
		this.unvisible = false;
	}
	
	public Item(int idx_item, int idx_group, String name, long price, boolean unvisible) {
		this.idx_item = idx_item;
		this.idx_group = idx_group;
		this.name = name;
		this.price = price;
		this.unvisible =unvisible;
	}

	public int getIdx_item(){
		return this.idx_item;
	}

	public String getName() {
		return this.name;
	}

	public long getPrice() {
		return this.price;
	}	
	

	public void setName(String string) {
		this.name = string;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public boolean getUnvisible() {
		return unvisible;
	}

	public int getIdx_Group() {
		return this.idx_group;
	}

	public void setIdx_Group(int idx_Group) {
		this.idx_group = idx_Group;
	}

	public void setUnvisible(boolean unvisible) {
		this.unvisible = unvisible;
	}

	public int getUnVisibleToInt() {
		if(this.unvisible)
			return 1;
		return 0;
	}
}
