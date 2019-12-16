import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ViewBidsForm extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextArea bidsList;

	public ViewBidsForm(EO2Lot list) {

		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		Dimension size = new Dimension();
		size.setSize(400, 600);

		setSize(size);
		bidsList.append(list.lotName + "\n" + "--------------------------------\n");
		if (list.bids.size() > 0){
			for (Object object : list.bids) {
				bidsList.append(object.toString());
			}
		}

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

	private void onCancel() {
		// add your code here if necessary
		dispose();
	}

}
