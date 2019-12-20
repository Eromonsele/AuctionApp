import net.jini.core.entry.*;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The EOKUser objects represents the information of the physical user of the auction application
 */
public class EOKUser implements Entry {

    //Fields
    public String firstName;  // The First Name of the user
    public String secondName; // The Second Name of the User
    public String password; // The password of the User's account
    public String emailAddress; // The email address of the user
    public String userId; // The User identification of the user
    public Boolean loggedIn; // true if user is logged in false if user is logged out
    public ArrayList<EO2Lot> lots; // a collection of lots posted by the user
    public ArrayList<Bid> bids; // a collection of bids posted by the user
    public ArrayList<Message> messages; // a collection of messages by the user

    // Creates a new user
    public EOKUser() {

    }

    //Creates a new user with the specified user id
    public EOKUser(String usrId){
        userId = usrId;
    }

    // Creates a new user with the specified user id and password
    public EOKUser(String usrId, String pswd) {
        userId = usrId;
        password = pswd;
    }

    @Override
    public String toString() {
        return firstName+" "+ secondName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EOKUser eokUser = (EOKUser) o;
        return firstName.equals(eokUser.firstName) &&
                secondName.equals(eokUser.secondName) &&
                password.equals(eokUser.password) &&
                emailAddress.equals(eokUser.emailAddress) &&
                userId.equals(eokUser.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, secondName, password, emailAddress, userId);
    }
}