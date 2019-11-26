import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private SessionManager sessionManager = new SessionManager();

    public AuctionGUI() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Boolean result = sessionManager.loginUser(userTxtField.getText(),passTxtField.getPassword().toString());
                if (result == false){
                  JOptionPane.showMessageDialog(LoginPanel,"Login not working");
                  return;
                 }
                //open user GUI interface, with a welcome user on top

                JOptionPane.showMessageDialog(LoginPanel,"Login working");

            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                LoginPanel.setVisible(false);
                RegisterPanel.setVisible(true);
            }
        });
        rtnToLoginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                LoginPanel.setVisible(true);
                RegisterPanel.setVisible(false);
            }
        });
        usrRegisterBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //send it in as an array
                Boolean result = sessionManager.registerUser();

                if (result == false){

                    return;
                }
            }
        });
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
