import net.jini.core.entry.*;

public class User implements Entry {

    private String firstName;
    private String secondName;
    private String password;
    private String emailAddress;
    private String hint;
    private String userId;

    public User() {
    }

    public User(String usrId, String pswd) {
        this.userId = usrId;
        this.password = pswd;
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


}