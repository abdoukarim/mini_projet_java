package mini_projet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePage extends JFrame implements ActionListener {
    JButton btnListePatients, btnHistorique, btnDoctor, btnDrugs;
    HomePage(){
        super();
        this.setTitle("Home Page");
        this.setSize(new Dimension(550, 750));
        this.setResizable(false);
        Container contenu = this.getContentPane();
        contenu.setLayout(new FlowLayout());
        ImageIcon image = new ImageIcon("image/hospital-icon.png");
        contenu.add(new JLabel(image));
        btnListePatients = new JButton("Liste des Patients");
        btnListePatients.setPreferredSize(new Dimension(200, 80));
        btnListePatients.setFont(new Font(Font.SERIF, Font.ITALIC, 15));
        btnListePatients.addActionListener(this);
        contenu.add(btnListePatients);
        btnHistorique = new JButton("Historique des consultations");
        btnHistorique.setPreferredSize(new Dimension(200, 80));
        btnHistorique.setFont(new Font(Font.SERIF, Font.ITALIC, 15));
        btnHistorique.addActionListener(this);
        contenu.add(btnHistorique);
        btnDoctor = new JButton("Liste des docteurs");
        btnDoctor.setPreferredSize(new Dimension(200, 80));
        btnDoctor.setFont(new Font(Font.SERIF, Font.ITALIC, 15));
        btnDoctor.addActionListener(this);
        contenu.add(btnDoctor);
        btnDrugs = new JButton("Liste des médicaments");
        btnDrugs.setPreferredSize(new Dimension(200, 80));
        btnDrugs.setFont(new Font(Font.SERIF, Font.ITALIC, 15));
        btnDrugs.addActionListener(this);
        contenu.add(btnDrugs);
        // CENTRER L'ECRAN
        setLocationRelativeTo(null);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==btnListePatients){
            this.setVisible(false);
            ListePatientsPage lp = new ListePatientsPage();
            lp.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            lp.setVisible(true);
        }
        if(e.getSource()==btnHistorique){
            this.setVisible(false);
            ListeConsultationPage lc = new ListeConsultationPage();
            lc.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            lc.setVisible(true);
        }
        if(e.getSource()==btnDoctor){
            this.setVisible(false);
            ListeDoctorsPage ld = new ListeDoctorsPage();
            ld.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            ld.setVisible(true);
        }
        if(e.getSource()==btnDrugs){
            this.setVisible(false);
            ListeMedicamentsPage lm = new ListeMedicamentsPage();
            lm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            lm.setVisible(true);
        }
    }

}
