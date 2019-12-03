import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.regex.Pattern;

public class AuctionGUI {
    private JPanel WindowsPanel;
    private JPanel LoginPanel;
    private JTextField userTxtField;
    private JButton loginButton;
    private JButton registerButton;
    private JPasswordField passTxtField;
    private JPanel RegisterPanel;
    private JTextField firstNameTxtField;
    private JTextField secNameTxtField;
    private JTextField EmailTxtField;
    private JTextField passTextField;
    private JTextField confirmPasswordTxtField;
    private JButton usrRegisterBtn;
    private JButton rtnToLoginBtn;
    private JTextField userNameTxtField;
    private JTabbedPane tabbedPane1;
    private JTabbedPane tabbedPane2;
    private JList list1;
    private SessionManager sessionManager;

    public AuctionGUI() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sessionManager = new SessionManager();
                Boolean result = sessionManager.loginUser(userTxtField.getText(),passTxtField.getText());
                if (result == false){
                  JOptionPane.showMessageDialog(LoginPanel,"Login not working");
                  return;
                 }
                //open user GUI interface, with a welcome user on top
                //
                JOptionPane.showMessageDialog(LoginPanel,"Login working");

            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                firstNameTxtField.setText("");
                secNameTxtField.setText("");
                EmailTxtField.setText("");
                passTextField.setText("");
                userNameTxtField.setText("");
                confirmPasswordTxtField.setText("");
                LoginPanel.setVisible(false);
                RegisterPanel.setVisible(true);
            }
        });

        rtnToLoginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                userTxtField.setText("");
                passTxtField.setText("");
                LoginPanel.setVisible(true);
                RegisterPanel.setVisible(false);
            }
        });

        usrRegisterBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sessionManager= new SessionManager();
                String promptMessage = checkRegistrationForm();
                if (promptMessage != "Registration completed"){
                    JOptionPane.showMessageDialog(RegisterPanel, promptMessage);
                    return;
                }
                HashMap<String,String> userInfo = new HashMap<String, String>();
                userInfo.put("firstName",firstNameTxtField.getText().toString());
                userInfo.put("secondName",secNameTxtField.getText().toString());
                userInfo.put("userName",userNameTxtField.getText().toString());
                userInfo.put("email",EmailTxtField.getText().toString());
                userInfo.put("password",passTextField.getText().toString());

                if (!sessionManager.registerUser(userInfo)){
                    if (sessionManager.errorMessage.isEmpty()){
                        sessionManager.errorMessage = "Registration not complete";
                    }
                    JOptionPane.showMessageDialog(RegisterPanel, sessionManager.errorMessage);
                    return;
                }
            }
        });
    }

    /**
     * checkRegistrationForm: Checks the form information before it is submitted to the space
     *
     * @return String Error message if error occurs
     *
     */
    private String checkRegistrationForm(){
        if(firstNameTxtField.getText().isEmpty()){
            return "First Name is blank";
        }
        
        if (secNameTxtField.getText().isEmpty()){
            return "Second/Sur Name is blank";
        }

        if (userNameTxtField.getText().isEmpty()){
            return "UserName is blank";
        }
        
        if (EmailTxtField.getText().isEmpty()){
            return "Email Address is blank";
        }

        if (passTextField.getText().isEmpty()){
            return "Password Field is blank";
        }

        if(passTextField.getText().length() < 6){
            return "Password needs to be more 6 characters, please retype";
        }

        if (!confirmPasswordTxtField.getText().equals(passTextField.getText())){
            return "Password and confirm password input doesn't match";
        }

        if(!isValid(EmailTxtField.getText())){
            return "Email Address is not valid, Please retype";
        }

        return "Registration completed";
    }

    /**
     *  isValid : Check if email address is a valid email address
     *
     * @param  String email an email address
     * @return Boolean
     */
    public boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Auction Application");

        frame.setResizable(false);

        Dimension size = new Dimension();
        size.setSize(800, 800);

        frame.setPreferredSize(size);

        frame.setContentPane(new AuctionGUI().WindowsPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }


}
