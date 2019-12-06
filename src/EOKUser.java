import net.jini.core.entry.*;

import java.util.ArrayList;
import java.util.Map;

public class EOKUser implements Entry {

    public String firstName;
    public String secondName;
    public String password;
    public String emailAddress;
    public String userId;
    public boolean loggedIn;

    public EOKUser() {
    }

    public EOKUser(String usrId){
        userId = usrId;
    }

    public EOKUser(String usrId, String pswd) {
        userId = usrId;
        password = pswd;
    }

}