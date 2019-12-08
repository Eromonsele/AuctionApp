import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.plaf.basic.BasicLookAndFeel;

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
    private JPanel UserPanel;
    private JLabel welcomeMessage;
    private JTabbedPane tabbedPane1;
    private JButton logOutButton;
    private JList featuredList;
    private JButton addItemsButton;
    private JButton addItemBtn;
    private JPanel lotListPanel;
    private SessionManager sessionManager;
    private JPanel addItemPanel;
    private AddItemForm addItemForm;
    private DefaultListModel<String> lotItems;

    public AuctionGUI() {

        LoginPanel.setVisible(true);
        RegisterPanel.setVisible(false);
        UserPanel.setVisible(false);

        sessionManager= new SessionManager();
        sessionManager.preLoad();
        if (sessionManager.loginUser("admin","root")){
            welcomeMessage.setText("Welcome " + sessionManager.sessionUser.firstName);
            LoginPanel.setVisible(false);
            RegisterPanel.setVisible(false);
            UserPanel.setVisible(true);
        }
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
                welcomeMessage.setText("Welcome " + sessionManager.sessionUser.firstName);
                LoginPanel.setVisible(false);
                RegisterPanel.setVisible(false);
                UserPanel.setVisible(true);
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
                UserPanel.setVisible(false);
            }
        });

        rtnToLoginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                userTxtField.setText("");
                passTxtField.setText("");
                LoginPanel.setVisible(true);
                RegisterPanel.setVisible(false);
                UserPanel.setVisible(false);
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

//        additemButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                addItemForm = new AddItemForm(sessionManager);
//                addItemForm.setLocationByPlatform(false);
//                addItemForm.setLocationRelativeTo(WindowsPanel);
//                addItemForm.setVisible(true);
//            }
//        });

//        lotItems = new DefaultListModel<String>();
//        lotItems.addElement("Drake");
//        lotItems.addElement("Jane Doe");
//        lotItems.addElement("John Smith");
//        lotItems.addElement("Kathy Green");
//        if(sessionManager.getAllLots().size() > 0){
//            lotItems.addElement(sessionManager.getAllLots().get(0).lotName);
//        }

        featuredList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        featuredList.setLayoutOrientation(JList.VERTICAL);
        featuredList.setVisibleRowCount(-1);
        featuredList.setModel(sessionManager.getAllLots());


        addItemsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItemForm = new AddItemForm(sessionManager);
                addItemForm.setLocationByPlatform(false);
                addItemForm.setLocationRelativeTo(WindowsPanel);
                addItemForm.setVisible(true);

                addItemForm.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        featuredList.setModel(sessionManager.getAllLots());
                        System.out.println("closed");
                    }
                });
            }
        });

        new PopulateTableNotify(sessionManager);
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
     * @param  email an email address
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

    public void updateList(){

    }
    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Auction Application");
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        };

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
