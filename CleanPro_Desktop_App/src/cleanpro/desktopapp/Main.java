package cleanpro.desktopapp;

import cleanpro.desktopapp.view.Login;
import javax.swing.SwingUtilities;

public class Main{
    public static void main(String [] args){
        SwingUtilities.invokeLater(() -> {
            Login loginScreen = new Login();
            loginScreen.setVisible(true);
        });
    }
}