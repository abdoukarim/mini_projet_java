package mini_projet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JFrame implements ActionListener {

    private JTextField loginText;
    private JPasswordField passwordField;
    private JButton loginButton;

    LoginPage(){
        super();
        this.setTitle("Login Page");
        this.setSize(new Dimension(300, 300));

        this.setResizable(false);
        Container contenu = this.getContentPane();
        contenu.setLayout(new FlowLayout(FlowLayout.CENTER));

        ImageIcon image = new ImageIcon("image/hospital-icon128x128.png");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setPreferredSize(new Dimension(250,128));
        contenu.add(imageLabel);

        contenu.add(new JLabel("Login: ", SwingConstants.CENTER));
        loginText = new JTextField(20);
        contenu.add(loginText);

        contenu.add(new JLabel("Mot de passe: ", SwingConstants.LEADING));
        passwordField = new JPasswordField(20);
        contenu.add(passwordField);
        loginButton = new JButton("Se connecter");
        loginButton.addActionListener(this);
        contenu.add(loginButton);

        // CENTRER L'ECRAN
        setLocationRelativeTo(null);

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == loginButton){
            String login = loginText.getText();
            String password = String.valueOf(passwordField.getPassword());
            if(login.equals("user") && password.equals("passer")) {
                this.setVisible(false);
                HomePage hp = new HomePage();
                hp.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                hp.setVisible(true);
            }
            else {
                JOptionPane.showMessageDialog(this,"Login ou mot de passe invalide",
                        "Erreur", JOptionPane.ERROR_MESSAGE, null);
            }
        }
    }
}
