public class SessionManager {

    public SessionManager() {

    }

    public boolean registerUser(){
        return false;
    }

    public boolean loginUser( String userName, String password){
        if(!userName.isEmpty() && !password.isEmpty() ){
            return true;
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
