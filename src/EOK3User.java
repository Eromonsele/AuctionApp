import net.jini.core.entry.*;

import java.util.ArrayList;

public class EOK3User implements Entry {

    public String firstName;
    public String secondName;
    public String password;
    public String emailAddress;
    public String userId;
    public Boolean loggedIn;
    public ArrayList<EO2Lot> EO2Lots;
    public ArrayList<Bid> bids;

    public EOK3User() {

    }

    public EOK3User(String usrId){
        userId = usrId;
    }

    public EOK3User(String usrId, String pswd) {
        userId = usrId;
        password = pswd;
    }

    @Override
    public String toString() {
        return firstName+" "+ secondName;
    }
}