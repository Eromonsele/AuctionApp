import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.regex.Pattern;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
    private JList <EO2Lot> featuredList;
    private JButton addItemsButton;
    private JScrollPane featuredListscrollPane;
    private JPanel itemInfoPanel;
    private JTextArea descTextAreaField;
    private JLabel lotNamelabel;
    private JLabel descLabel;
    private JButton bidButton;
    private JButton buyOutButton;
    private JPanel itemActionsField;
    private JScrollPane descScrollPane;
    private JLabel highestBidValueField;
    private JLabel itemOwnerLabel;
    private JLabel startingBidValueLabel;
    private JLabel buyOutValueLabel;
    private JPanel itemOwnerWrapper;
    private JPanel priceWrapper;
    private JList myItemsList;
    private JButton refreshButton;
    private JButton removeBtn;
    private JTextArea notificationTxtArea;
	private JButton viewBidsButton;
    private JButton acceptBidButton;
    private JPanel adminButtonPanel;
    private JList activeLotsList;
    private JPanel activeLotsWrapper;
    private JPanel notifyTab;
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

                featuredList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                featuredList.setLayoutOrientation(JList.VERTICAL);
                featuredList.setVisibleRowCount(-1);

                // Refresh Item on Add everytime
                new AddItemRefresh(featuredList);

                activeLotsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                activeLotsList.setLayoutOrientation(JList.VERTICAL);
                activeLotsList.setVisibleRowCount(-1);
                activeLotsList.setModel(sessionManager.getActiveLots());

//               new BidNotification(notificationTxtArea);

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
                }else{
                    LoginPanel.setVisible(true);
                    RegisterPanel.setVisible(false);
                    UserPanel.setVisible(false);
                }
            }
        });

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
                        activeLotsList.setModel(sessionManager.getActiveLots());
                    }
                });
            }
        });

        featuredList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (featuredList.getSelectedValue() != null){
                    if (featuredList.getSelectedValue().sold){
                        JOptionPane.showMessageDialog(UserPanel,"This Item has been Purchased");
                        return;
                    }

                    lotNamelabel.setText(featuredList.getSelectedValue().lotName);
                    adminButtonPanel.setVisible(true);
                    if (sessionManager.sessionUser.equals(featuredList.getSelectedValue().lotOwner)){
                        bidButton.setVisible(false);
                        buyOutButton.setVisible(false);
                        removeBtn.setVisible(true);
                        acceptBidButton.setVisible(true);
                    }else{
                        bidButton.setVisible(true);
                        buyOutButton.setVisible(true);
                        removeBtn.setVisible(false);
                        acceptBidButton.setVisible(false);
                    }
                    if (featuredList.getSelectedValue().bids.size() > 0){
                        acceptBidButton.setEnabled(true);
                        viewBidsButton.setEnabled(true);
                    }else{
                        acceptBidButton.setEnabled(false);
                        viewBidsButton.setEnabled(false);
                    }
                    priceWrapper.setVisible(true);
                    itemOwnerWrapper.setVisible(true);
                    highestBidValueField.setText(sessionManager.getHighestBid(featuredList.getSelectedValue()));
                    itemOwnerLabel.setText("Owner:" + featuredList.getSelectedValue().lotOwner.toString());
                    buyOutValueLabel.setText("£"+featuredList.getSelectedValue().lotBuyOutPrice.toString());
                    startingBidValueLabel.setText("£"+featuredList.getSelectedValue().lotStartPrice.toString());
                    lotNamelabel.setVisible(true);
                    descLabel.setVisible(true);
                    descScrollPane.setVisible(true);
                    itemActionsField.setVisible(true);
                    descTextAreaField.setVisible(true);
                    descTextAreaField.setText(null);
                    descTextAreaField.setText(featuredList.getSelectedValue().lotDescription);
                }
            }
        });

        bidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (sessionManager.sessionUser.equals(featuredList.getSelectedValue().lotOwner)){
                    JOptionPane.showMessageDialog(UserPanel, "You can't bid on your own item");
                    return;
                }

                String bidValue = JOptionPane.showInputDialog(UserPanel,"What is your bid?");
                if(sessionManager.isNumeric(bidValue)){
                    Double highestBid =  featuredList.getSelectedValue().lotStartPrice;
                    if (featuredList.getSelectedValue().bids.size() > 0){
                        highestBid = featuredList.getSelectedValue().bids.get(featuredList.getSelectedValue().bids.size() - 1).bidValue;
                    }

                    if (sessionManager.setBid(Double.parseDouble(bidValue),featuredList.getSelectedValue(), highestBid)){
                        JOptionPane.showMessageDialog(UserPanel, "Successful Bid");
                    }else{
                        JOptionPane.showMessageDialog(UserPanel, sessionManager.errorMessage);
                    }
                }else{
                    JOptionPane.showMessageDialog(UserPanel, "Please put in a number");
                }
                resetFeaturedWindow();
                activeLotsList.setModel(sessionManager.getActiveLots());
            }
        });

        buyOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (sessionManager.buyOutItem(featuredList.getSelectedValue())){
                    JOptionPane.showMessageDialog(UserPanel, "Purchase Successful");
                    resetFeaturedWindow();
                    activeLotsList.setModel(sessionManager.getActiveLots());
                }else{
                    JOptionPane.showMessageDialog(UserPanel, "Error");
                }
            }
        });

        removeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sessionManager.removeItem(featuredList.getSelectedValue());
                resetFeaturedWindow();
            }
        });
        acceptBidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sessionManager.buyOutItem(featuredList.getSelectedValue())){
                    JOptionPane.showMessageDialog(UserPanel, "Purchase Successful");
                    resetFeaturedWindow();
                }else{
                    JOptionPane.showMessageDialog(UserPanel, "Error");
                }
            }
        });

        viewBidsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewBidsForm viewBidsForm = new ViewBidsForm(featuredList.getSelectedValue());
                viewBidsForm.setLocationByPlatform(false);
                viewBidsForm.setLocationRelativeTo(WindowsPanel);
                viewBidsForm.setVisible(true);
            }
        });
        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (sessionManager.logOut()){
                    JOptionPane.showMessageDialog(UserPanel, "Bye Bye" + sessionManager.sessionUser .toString());
                    LoginPanel.setVisible(true);
                    RegisterPanel.setVisible(false);
                    UserPanel.setVisible(false);
                }else{
                    JOptionPane.showMessageDialog(UserPanel, "Logout not successful");
                }
            }
        });


        activeLotsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

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
     * @param  email an email address
     * @return Boolean
     */
    public boolean isValid(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    /**
     * resetFeaturedWindow:
     */
    private void resetFeaturedWindow(){
        adminButtonPanel.setVisible(false);
        priceWrapper.setVisible(false);
        itemOwnerWrapper.setVisible(false);
        lotNamelabel.setVisible(false);
        descLabel.setVisible(false);
        descScrollPane.setVisible(false);
        itemActionsField.setVisible(false);
        descTextAreaField.setVisible(false);
    }

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Auction Application");
        frame.setResizable(false);

        Dimension size = new Dimension();
        size.setSize(900, 900);

        frame.setPreferredSize(size);

        frame.setContentPane(new AuctionGUI().WindowsPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

}
