package ts.utill.customermanager.data;


public class DataComparator_Vist implements java.util.Comparator{

	@Override
	public int compare(Object lhs, Object rhs) {
		return ((Customer) rhs).getVistCnt() - ((Customer) lhs).getVistCnt();
	}

}
