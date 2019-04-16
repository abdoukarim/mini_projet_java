package mini_projet;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddDoctorPage extends JDialog implements ActionListener {
    private JLabel nameLabel;
    private JTextField nameField;
    private JPanel formPanel;
    private JButton addButton;
    private SQLiteJDBCDriverConnection sql = new SQLiteJDBCDriverConnection();
    DefaultTableModel model;

    AddDoctorPage(JFrame owner, DefaultTableModel model){
        super(owner, true);
        this.model = model;
        this.setTitle("Ajouter un nouveau docteur");
        this.setSize(new Dimension(350, 150));
        this.setResizable(false);
        Container contenu = this.getContentPane();
        formPanel = new JPanel();
        formPanel.setLayout(new FlowLayout());
        nameLabel = new JLabel("Nom");
        formPanel.add(nameLabel);
        nameField = new JTextField(20);
        formPanel.add(nameField);
        addButton = new JButton("ajouter le docteur");
        addButton.addActionListener(this);
        formPanel.add(addButton);
        formPanel.setBorder(new TitledBorder("Création d'un nouveau docteur"));

        contenu.add(formPanel);
        // CENTRER L'ECRAN
        setLocationRelativeTo(null);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==addButton){
            if(!nameField.getText().isBlank() && nameField.getText().length()<=45) {

                String nom = nameField.getText().strip();
                // SQL CREATE NEW PATIENT
                sql.createMedecin(nom);
                this.dispose();
                // add new data to the table
                Object[] rowData = new Object[2];
                rowData[1] = nom;
                model.addRow(rowData);
                model.fireTableDataChanged();
            }
            else if(nameField.getText().length()>45){
                JOptionPane.showMessageDialog(this,
                        "Le nom du patient dépasse 45 caratères",
                        "Erreur", JOptionPane.ERROR_MESSAGE, null);
            }
            else {
                JOptionPane.showMessageDialog(this,
                        "Veuillez renseigner le nom du docteur",
                        "Erreur", JOptionPane.ERROR_MESSAGE, null);
            }
        }
    }
}

