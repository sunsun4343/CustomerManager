package ts.utill.customermanager.data;

import java.util.ArrayList;
import java.util.Calendar;

public class Customer implements Cloneable {
	//public enum Ages {_10,_20,_30,_40,_50,_60};
	
	int idx_customer;
	String name;
	String hp;
	boolean sex;
	int age;
	String memo;
	long point;
	
	ArrayList<Sale> SaleList;
	
	public Customer(int idx_customer, String name, String hp, boolean sex, int age, String memo, ArrayList<Sale> salelist) { //later delete
		this.idx_customer = idx_customer;
		this.name = name;
		this.hp = hp;
		this.sex = sex;
		this.age = age;
		this.memo = memo;
		this.SaleList = salelist;
		this.point = 0;
	}

	public Customer() {
		this.name = "";
		this.hp = "";
		this.sex = false;
		this.age = 0;
		this.memo = "";
		this.SaleList = new ArrayList<Sale>();
	}

	public Customer(int idx_customer, String name, String hp, boolean sex, int age, String memo, long point, ArrayList<Sale> salelist) {
		this.idx_customer = idx_customer;
		this.name = name;
		this.hp = hp;
		this.sex = sex;
		this.age = age;
		this.memo = memo;
		this.point = point;
		this.SaleList = salelist;
	}

	public int getVistCnt(){
		return SaleList.size();
	}
	
	public Calendar getLastVistDate(){
		if(SaleList.size() > 0)
			return SaleList.get(0).getDate();
		return null;
	}
	
	public long getAmount(){
		long amount = 0;
		for (Sale s : SaleList) {
			amount += s.getTotalPrice();
		}
		return amount;
	}

	public CharSequence getName() {
		return this.name;
	}

	public CharSequence getHpView() {
		String newhp = null;
		if (this.hp.length() == 11) {
			newhp = hp.substring(0, 3) + "-" + hp.substring(3, 7) + "-" + hp.substring(7, 11);
		}else if(this.hp.length() == 10){
			newhp = hp.substring(0, 3) + "-" + hp.substring(3, 6) + "-" + hp.substring(6, 10);
		}else if(this.hp.length() == 9){
			newhp = hp.substring(0, 2) + "-" + hp.substring(2, 5) + "-" + hp.substring(5, 9);
		}
		return newhp;
	}
	
	public CharSequence getHp() {
		return this.hp;
	}

	public int getSexToInt() {
		if(this.sex)
			return 1;
		return 0;
	}
	
	public int getAge() {
		return this.age;
	}

	public int getIdx_customer() {
		return this.idx_customer;
	}

	public CharSequence getMemo() {
		return this.memo;
	}

	public ArrayList<Sale> getSaleList() {
		return this.SaleList;
	}

	public void ChangeSex() {
		this.sex = !this.sex;
	}

	public boolean getSex() {
		return this.sex;
	}
	
	public void ChangeAge() {
		this.age ++;
		if(this.age >= 6)
			this.age=0;
	}
	
	public Object clone(){
		try{
		   return super.clone();
		}catch(CloneNotSupportedException e){
		   return null;
		}
	}

	public void setName(String string) {
		this.name = string;
	}

	public void setHp(String string) {
		this.hp = string;
	}

	public void setMemo(String string) {
		this.memo = string;
	}

	public void setSex(boolean sex) {
		this.sex = sex;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setSaleList(ArrayList<Sale> arSrc) {
		this.SaleList = arSrc;
	}

	public long getPoint() {
		return this.point;
	}


}
