package ts.utill.customermanager.data;

public class ItemGroup {

	int idx_group;
	String name;
	
	public ItemGroup(int idx_group, String name) {
		this.idx_group = idx_group;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getIdx_Group() {
		return idx_group;
	}

	public void setName(String name) {
		this.name = name;
	}

}
