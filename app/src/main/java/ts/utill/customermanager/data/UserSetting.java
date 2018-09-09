package ts.utill.customermanager.data;

public class UserSetting {

    //SingleTone
    public static UserSetting userSetting = new UserSetting();
    private UserSetting(){

    }
    public static UserSetting getInstance(){
        return userSetting;
    }

    //Config
    public static boolean _GroupNoView = false;
    public static boolean _AgeNogView = false;

}
