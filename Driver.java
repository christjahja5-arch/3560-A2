import javax.swing.SwingUtilities;

//starts mini twitter

public class Driver {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> AdminControlPanel.getInstance().setVisible(true));
    }
}
