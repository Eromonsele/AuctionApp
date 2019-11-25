import javax.swing.*;

public class AuctionGUI {
    private JPanel panel1;
    private JPanel northPanel;
    private JPanel southPanel;
    private JPanel centrePanel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("AuctionGUI");
        frame.setContentPane(new AuctionGUI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
