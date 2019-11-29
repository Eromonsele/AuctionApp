import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

import java.util.Map;

public class SessionManager {
    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
    private static final long TWO_MINUTES = 2 * 1000 * 60;
    private static final long ONESECOND = 1000;  // one thousand milliseconds

    private JavaSpace space;

    /**
     *
     */
    public SessionManager() {

        space = SpaceUtils.getSpace();
        if (space == null){
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }
    }

    /**
     *
     * @param userInfo
     * @return
     */
    public boolean registerUser(Map<String,String> userInfo){
        if (!userInfo.isEmpty()){
            try {
                EOKUser EOKUserTemplate = new EOKUser(userInfo.get("userName"));
                EOKUser EOKUserRegister = (EOKUser)space.readIfExists(EOKUserTemplate,null,TWO_MINUTES);

                if (EOKUserRegister == null){
                    try{
                        EOKUser EOKUserReg = new EOKUser(userInfo.get("userName"),userInfo.get("password"));
                        EOKUserReg.setFirstName(userInfo.get("firstName"));
                        EOKUserReg.setSecondName(userInfo.get("secondName"));
                        EOKUserReg.setEmailAddress(userInfo.get("email"));
                        space.write(EOKUserReg,null, Lease.FOREVER);
                        return true;
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     *
     * @param userName
     * @param password
     * @return
     */
    public boolean loginUser( String userName, String password){

            try {
                if(!userName.isEmpty() && !password.isEmpty() ) {
                    EOKUser EOKUserTemplate = new EOKUser(userName, password);
                    EOKUser EOKUserLogin = (EOKUser) space.readIfExists(EOKUserTemplate, null, TWO_SECONDS);

                    if (EOKUserLogin != null) {
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        return false;
    }

}
