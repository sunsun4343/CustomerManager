package ts.utill.customermanager.data;


public class DataComparator_Amount implements java.util.Comparator{

	@Override
	public int compare(Object lhs, Object rhs) {
		return (int) (((Customer) rhs).getAmount() - ((Customer) lhs).getAmount());
		
	}

}
