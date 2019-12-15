import net.jini.core.entry.*;

import java.util.Objects;

public class EOKUser implements Entry {

    public String firstName;
    public String secondName;
    public String password;
    public String emailAddress;
    public String userId;
    public Boolean loggedIn;

    public EOKUser() {

    }

    public EOKUser(String usrId){
        userId = usrId;
    }

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