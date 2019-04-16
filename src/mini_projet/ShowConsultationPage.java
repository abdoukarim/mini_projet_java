package mini_projet;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class ShowConsultationPage extends JDialog implements ActionListener, ListSelectionListener {
    JScrollPane scrollPanel;
    JTextArea texteConsultation;
    private static SQLiteJDBCDriverConnection sqlConn = new SQLiteJDBCDriverConnection();
    DefaultTableModel model;

    /*
     * Constructeur qui affiche les détails de la consultation
     *
     */
    ShowConsultationPage(JFrame owner, DefaultTableModel model, int selectedID){
        super(owner, true);
        this.model = model;
        this.setTitle("Détail de la consultation");
        this.setSize(new Dimension(350, 350));
        this.setResizable(false);
        Container contenu = this.getContentPane();
        texteConsultation = new JTextArea(15, 40);
        scrollPanel = new JScrollPane(texteConsultation);
        scrollPanel.setPreferredSize(new Dimension(300, 400));
        scrollPanel.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        contenu.add(scrollPanel);

        Vector<Vector<Object>> consultationDataList = new Vector<>();
        consultationDataList = sqlConn.getConsultationDetail(selectedID);

        texteConsultation.setText("Detail consultation \n");
        String[] medicaments = new String[consultationDataList.size()];
        String medecin = "";
        String patient = "";
        String date = "";
        String nombre_jours = "";
        String numero = "";
        int i=0;
        int j=0;
        for (Vector<Object> c: consultationDataList) {

            medecin = c.get(j++).toString();
            patient = c.get(j++).toString();
            date = c.get(j++).toString();
            numero = c.get(j++).toString();
            medicaments[i]=c.get(j++).toString() + " (indications: " + c.get(j++).toString()+", posologie: "+c.get(j++).toString()+")";
            nombre_jours = c.get(j++).toString();
            i++;
        }
        StringBuilder presciptions = new StringBuilder();
        for (String p:medicaments) {
            presciptions.append(p).append("\n");
        }
        texteConsultation.append("Medecin : "+medecin);
        texteConsultation.append("\n");
        texteConsultation.append("Patient : "+patient);
        texteConsultation.append("\n");
        texteConsultation.append("Date consultation : "+date);
        texteConsultation.append("\n");
        texteConsultation.append("Numero consultation : "+numero);
        texteConsultation.append("\n");
        texteConsultation.append("Presciptions : ");
        texteConsultation.append("\n");
        texteConsultation.append(String.valueOf(presciptions));
        texteConsultation.append("Nombre de jours : ");
        texteConsultation.append(nombre_jours+" jour(s).");
        texteConsultation.setLineWrap(true);
        texteConsultation.setWrapStyleWord(true);

        // CENTRER L'ECRAN
        setLocationRelativeTo(null);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }
}
