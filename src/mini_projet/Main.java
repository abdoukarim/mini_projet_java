package mini_projet;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Main extends JWindow {

    static boolean isRegistered;
    private static JProgressBar progressBar = new JProgressBar();
    private static Main execute;
    private static int count;
    private static Timer timer1;

    public Main() {
        this.setSize(new Dimension(550, 620));
        Container container = getContentPane();
        container.setLayout(new FlowLayout());

        JLabel chargementlabel = new JLabel("Gestion des consultations");
        chargementlabel.setFont(new Font("Verdana", Font.BOLD, 14));
        ImageIcon image = new ImageIcon("image/hospital-icon.png");
        JLabel label = new JLabel(image);
        //label.setBounds(85, 25, 280, 30);
        container.add(chargementlabel);
        container.add(label);

        progressBar.setMaximum(50);
        progressBar.setBounds(55, 180, 500, 20);
        JLabel textChargement = new JLabel("Chargement de l'application...", SwingConstants.CENTER);
        textChargement.setPreferredSize(new Dimension(500, 20));

        container.add(progressBar);
        container.add(textChargement);
        loadProgressBar();
        //setSize(370, 215);
        // CENTRER L'ECRAN
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadProgressBar() {
        ActionListener al = new ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                count++;

                progressBar.setValue(count);


                if (count == 102) {

                    //createFrame();
                    LoginPage lp = new LoginPage();
                    lp.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    lp.setVisible(true);

                    execute.setVisible(false);//swapped this around with timer1.stop()

                    timer1.stop();
                }

            }

            private void createFrame() throws HeadlessException {
                JFrame frame = new JFrame();
                frame.setSize(500, 500);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        };
        timer1 = new Timer(100, al);
        timer1.start();
    }

    public static void main(String[] args) {
        execute = new Main();
    }
};
