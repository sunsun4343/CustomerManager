package ts.utill.customermanager.data;


public class DataComparator_Date implements java.util.Comparator{

	CustomerDB customerDB = CustomerDB.getInstence();
	
	@Override
	public int compare(Object lhs, Object rhs) {
		long lt, rt;
		rt = customerDB.getDDay(((Customer) lhs).getLastVistDate());
		lt= customerDB.getDDay(((Customer) rhs).getLastVistDate());
		return (int) (rt - lt);
	}

}
