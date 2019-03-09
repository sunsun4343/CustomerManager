package ts.utill.customermanager.data;

import java.util.ArrayList;

public class CustomerDBJson {

    public int DATABASE_VERSION = 1;

    //to Customer
    public ArrayList<Customer> CustomersList;

    //to Item
    public ArrayList<Item> ItemList = null;
    public ArrayList<ItemGroup> ItemGroupList = null;

}
