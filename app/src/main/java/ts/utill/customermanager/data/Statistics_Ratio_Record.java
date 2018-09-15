package ts.utill.customermanager.data;

public class Statistics_Ratio_Record {
	String name;
	long value;
	float ration;

	public Statistics_Ratio_Record(String name, long value){
		this.name = name;
		this.value = value;
	}
	
	public long getValue() {
		return this.value;
	}

	public String getName() {
		return this.name;
	}

}
