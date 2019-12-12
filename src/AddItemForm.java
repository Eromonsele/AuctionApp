import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AddItemForm extends JDialog {
    private JPanel contentPane;
    private JButton addItemBtn;
    private JButton buttonCancel;
    private JTextField lotNameTxtField;
    private JTextArea lotDescTxtArea;
    private JTextField lotBidPriceTxtMedia;
    private JTextField lotBuyOutPriceTextField;
    private SessionManager sessionManager;

    public AddItemForm(SessionManager sManager) {
        sessionManager = sManager;
        setContentPane(contentPane);
        setModal(true);
        setResizable(false);

        Dimension size = new Dimension();
        size.setSize(400, 600);

        setSize(size);
        getRootPane().setDefaultButton(addItemBtn);

        addItemBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();

            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        Map<String, String> lotInfo = new HashMap<String, String>();

        if (lotNameTxtField.getText().isEmpty() || lotBuyOutPriceTextField.getText().isEmpty() || lotDescTxtArea.getText().isEmpty() || lotBidPriceTxtMedia.getText().isEmpty()){
            JOptionPane.showMessageDialog(contentPane, "Please all fields are required to be filled, fuck face");
            return;
        }

        lotInfo.put("lotName",lotNameTxtField.getText());
        lotInfo.put("lotBuyoutPrice",lotBuyOutPriceTextField.getText());
        lotInfo.put("lotDescription",lotDescTxtArea.getText());
        lotInfo.put("lotStartPrice",lotBidPriceTxtMedia.getText());
        if (!sessionManager.addItem(lotInfo)){
            if (sessionManager.errorMessage.isEmpty()){
                sessionManager.errorMessage = "Item not added";
            }
            JOptionPane.showMessageDialog(contentPane, sessionManager.errorMessage);
            return;
        }

        JOptionPane.showMessageDialog(contentPane,"Item added");
        dispose();
    }



    private void onCancel() {
        dispose();
     }
}
