import net.jini.core.entry.*;

import java.util.ArrayList;
import java.util.Map;

public class EOUser implements Entry {

    public String firstName;
    public String secondName;
    public String password;
    public String emailAddress;
    public String userId;
    public Boolean loggedIn;
    public ArrayList<EOLot> lots;
    public ArrayList<Bid> bids;

    public EOUser() {

    }

    public EOUser(String usrId){
        userId = usrId;
    }

    public EOUser(String usrId, String pswd) {
        userId = usrId;
        password = pswd;
    }

}