package mini_projet;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddMedicamentPage extends JDialog implements ActionListener {
    private JLabel libelleLabel, indicationLabel, posologieLabel, codeLabel;
    private JTextField libelleField, codeField;
    private JTextArea indicationField, posologieField;
    private JPanel formPanel;
    private JButton addButton;
    private SQLiteJDBCDriverConnection sql = new SQLiteJDBCDriverConnection();
    DefaultTableModel model;

    AddMedicamentPage(JFrame owner, DefaultTableModel model) {
        super(owner, true);
        this.model = model;
        this.setTitle("Ajouter un nouveau medicament");
        this.setSize(new Dimension(350, 200));
        this.setResizable(false);
        Container contenu = this.getContentPane();
        formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(6, 2));
        codeLabel = new JLabel("Code");
        formPanel.add(codeLabel);
        codeField = new JTextField();
        formPanel.add(codeField);
        libelleLabel = new JLabel("Libelle");
        formPanel.add(libelleLabel);
        libelleField = new JTextField();
        formPanel.add(libelleField);
        indicationLabel = new JLabel("Indications");
        formPanel.add(indicationLabel);
        indicationField = new JTextArea();
        formPanel.add(indicationField);
        formPanel.add(new JSeparator());
        formPanel.add(new JSeparator());
        posologieLabel = new JLabel("Posologie");
        formPanel.add(posologieLabel);
        posologieField = new JTextArea();
        formPanel.add(posologieField);
        addButton = new JButton("ajouter le medicament");
        addButton.addActionListener(this);
        formPanel.add(addButton);
        formPanel.setBorder(new TitledBorder("Création d'un nouveau medicament"));

        contenu.add(formPanel);
        // CENTRER L'ECRAN
        setLocationRelativeTo(null);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            if (!codeField.getText().isBlank()||!libelleField.getText().isBlank() ||
                    !indicationField.getText().isBlank()||
                    !posologieField.getText().isBlank()) {
                String code = codeField.getText().strip();
                String libelle = libelleField.getText().strip();
                String indication = indicationField.getText().strip();
                String posologie = posologieField.getText().strip();

                sql.createMedicament(code, libelle, indication, posologie);
                this.dispose();
                // add new data to the table
                Object[] rowData = new Object[4];
                rowData[0] = code;
                rowData[1] = libelle;
                rowData[2] = indication;
                rowData[3] = posologie;
                model.addRow(rowData);
                model.fireTableDataChanged();
            }
            else {
                JOptionPane.showMessageDialog(this,
                        "Veuillez renseigner le nom du medicament",
                        "Erreur", JOptionPane.ERROR_MESSAGE, null);
            }
        }
    }
}

