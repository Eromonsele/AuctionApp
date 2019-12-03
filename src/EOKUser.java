import net.jini.core.entry.*;

import java.util.ArrayList;
import java.util.Map;

public class EOKUser implements Entry {

    public String firstName;
    public String secondName;
    public String password;
    public String emailAddress;
    public String userId;
    private ArrayList<Lot> lots;
    private ArrayList<Bid> bids;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void addItem(Map<String,String> itemInfo){

    }
    public void removeItem(){

    }

    public void getAllItems(){

    }

    public void getAllBids(){

    }

}