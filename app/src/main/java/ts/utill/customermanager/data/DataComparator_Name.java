package ts.utill.customermanager.data;


public class DataComparator_Name implements java.util.Comparator{

	@Override
	public int compare(Object lhs, Object rhs) {
		return ((Customer) lhs).getName().toString().compareToIgnoreCase(((Customer) rhs).getName().toString());
	}

}
