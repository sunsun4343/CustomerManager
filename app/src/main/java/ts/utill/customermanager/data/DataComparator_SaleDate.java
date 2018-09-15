package ts.utill.customermanager.data;


public class DataComparator_SaleDate implements java.util.Comparator{

	CustomerDB customerDB = CustomerDB.getInstence();
	
	@Override
	public int compare(Object lhs, Object rhs) {
		long lt, rt;
		lt = customerDB.getDDay(((Sale) lhs).getDate());
		rt= customerDB.getDDay(((Sale) rhs).getDate());
		return (int) (lt - rt);
	}

}