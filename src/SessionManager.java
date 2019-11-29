import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import java.rmi.RemoteException;

public class SessionManager {
    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
    private static final long TWO_MINUTES = 2 * 1000 * 60;

    private JavaSpace space;

    public SessionManager() {
        this.space = SpaceUtils.getSpace();
    }

    public boolean registerUser(){

        return false;
    }

    public boolean loginUser( String userName, String password){
        if(!userName.isEmpty() && !password.isEmpty() ){
            try {
                User userTemplate = new User(userName,password);
                User userLogin = (User)space.readIfExists(userTemplate,null,TWO_SECONDS);

                if(userLogin != null){
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private String decrypt(String password){
        return "";
    }

    private String encrypt(String password){
        return "";
    }
}
