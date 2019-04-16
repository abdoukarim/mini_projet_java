package mini_projet;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class AddPatientPage extends JDialog implements ActionListener {
    private JLabel nameLabel;
    private JTextField nameField;
    private JPanel formPanel;
    private JButton addButton;
    private SQLiteJDBCDriverConnection sql = new SQLiteJDBCDriverConnection();
    DefaultTableModel model;

    AddPatientPage(JFrame owner, DefaultTableModel model){
        super(owner, true);
        this.model = model;
        this.setTitle("Ajouter un nouveau patient");
        this.setSize(new Dimension(350, 150));
        this.setResizable(false);
        Container contenu = this.getContentPane();
        formPanel = new JPanel();
        formPanel.setLayout(new FlowLayout());
        nameLabel = new JLabel("Nom");
        formPanel.add(nameLabel);
        nameField = new JTextField(20);
        formPanel.add(nameField);
        addButton = new JButton("ajouter le patient");
        addButton.addActionListener(this);
        formPanel.add(addButton);
        formPanel.setBorder(new TitledBorder("Création d'un nouveau patient"));

        contenu.add(formPanel);
        // CENTRER L'ECRAN
        setLocationRelativeTo(null);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==addButton){
            if(!nameField.getText().isBlank() && nameField.getText().length()<=45) {
                System.out.println(nameField.getText().strip());
                String nom = nameField.getText().strip();
                // SQL CREATE NEW PATIENT
                sql.createPatient(nom);
                this.dispose();
                // add new data to the table
                Object[] rowData = new Object[2];
                rowData[0] = model.getRowCount()+1;
                rowData[1] = nom;
                model.addRow(rowData);

            }
            else if(nameField.getText().length()>45){
                JOptionPane.showMessageDialog(this,
                        "Le nom du patient dépasse 45 caratères",
                        "Erreur", JOptionPane.ERROR_MESSAGE, null);
            }
            else {
                JOptionPane.showMessageDialog(this,
                        "Veuillez renseigner le nom du patient",
                        "Erreur", JOptionPane.ERROR_MESSAGE, null);
            }
        }
    }
}
