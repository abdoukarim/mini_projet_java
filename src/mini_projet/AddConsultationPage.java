package mini_projet;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

public class AddConsultationPage extends JDialog implements ActionListener, ListSelectionListener {
    private JLabel doctorLabel, patientLabel, dateLabel, nbJourLabel, drugListLabel;
    private JComboBox doctorField, patientField;
    private JDatePickerImpl dateField;
    private JTextField nbJourField;
    private JPanel formPanel;
    private JButton addButton, cancelButton;
    private JList drugList;
    private String[] drugs;
    private String[] medicamentsSelectionnes;
    private Boolean isDrugSelected = false;
    Vector<Object> vectDoctors = new Vector<Object>();
    Vector<Object> vectPatients = new Vector<Object>();
    Vector<Object> vectMedicaments = new Vector<Object>();
    private static SQLiteJDBCDriverConnection sqlConn = new SQLiteJDBCDriverConnection();
    DefaultTableModel model;

    AddConsultationPage(JFrame owner, DefaultTableModel model){
        super(owner, true);
        this.model = model;
        this.setTitle("Ajouter une nouvelle consultation");
        this.setSize(new Dimension(350, 300));
        this.setResizable(false);
        Container contenu = this.getContentPane();
        formPanel = new JPanel();
        formPanel.setLayout(new FlowLayout());
        doctorLabel = new JLabel("Docteur");
        doctorLabel.setPreferredSize(new Dimension(150, 20));
        formPanel.add(doctorLabel);
        // Liste des docteurs
        vectDoctors = getDoctors();
        String[] doctors = new String[vectDoctors.size()];
        int i=0;
        for(Object d: vectDoctors) {
            doctors[i] = d.toString();
            i++;
        }
        doctorField = new JComboBox(doctors);
        doctorField.setPreferredSize(new Dimension(150, 20));
        //doctorField.setSelectedIndex(1);
        formPanel.add(doctorField);
        patientLabel = new JLabel("Patient");
        patientLabel.setPreferredSize(new Dimension(150, 20));
        formPanel.add(patientLabel);
        // Liste des patients
        vectPatients = getPatients();
        String[] patients = new String[vectPatients.size()];
        i=0;
        for(Object d: vectPatients) {
            patients[i] = d.toString();
            i++;
        }
        patientField = new JComboBox(patients);
        patientField.setPreferredSize(new Dimension(150, 20));
        formPanel.add(patientField);
        dateLabel = new JLabel("Date");
        dateLabel.setPreferredSize(new Dimension(120, 20));
        formPanel.add(dateLabel);
        SqlDateModel dateModel = new SqlDateModel();
        //model.setDate(1990, 8, 24);
        //model.setDay(24); model.setMonth(0); model.setYear(1990);
        //model.setSelected(true);
        Properties p = new Properties();
        p.put("text.today", "Aujourd'hui");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(dateModel, p);
        dateField = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        dateField.setPreferredSize(new Dimension(180, 30));
        //DateFormat format = new SimpleDateFormat("dd--MMMM--yyyy");
        //dateField = new JFormattedTextField(format);

        //dateField.setColumns(10);
        formPanel.add(dateField);
        nbJourLabel = new JLabel("Nombre de jours");
        nbJourLabel.setPreferredSize(new Dimension(150, 20));
        formPanel.add(nbJourLabel);
        nbJourField = new JTextField();
        //nbJourField.setColumns(10);
        nbJourField.setPreferredSize(new Dimension(150, 20));
        formPanel.add(nbJourField);

        // liste des medicaments
        drugListLabel = new JLabel("Médicaments");
        drugListLabel.setPreferredSize(new Dimension(150, 20));
        formPanel.add(drugListLabel);
        //drugs = new String[]{"Doloprane", "Rhumagrip", "Fervex", "Dolirhume", "Fervex", "Dolirhume", "Fervex", "Dolirhume"};
        vectMedicaments = getMedicaments();
        drugs = new String[vectMedicaments.size()];
        i=0;
        for(Object d: vectMedicaments) {
            drugs[i] = d.toString();
            i++;
        }
        drugList = new JList(drugs);
        drugList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        drugList.setLayoutOrientation(JList.VERTICAL);
        drugList.setVisibleRowCount(-1);
        //drugList.setPreferredSize(new Dimension(150, 20));
        //drugList.setSelectedIndex(1);
        //drugList.setSelectedIndices(new int[]{1, 2});
        ListSelectionModel drugListModel = drugList.getSelectionModel();
        drugListModel.addListSelectionListener(this);
        JScrollPane listScroller = new JScrollPane(drugList);
        listScroller.setPreferredSize(new Dimension(150, 80));
        listScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        formPanel.add(listScroller);

        addButton = new JButton("enregistrer");
        addButton.addActionListener(this);
        formPanel.add(addButton);
        cancelButton = new JButton("annuler");
        cancelButton.addActionListener(this);
        formPanel.add(cancelButton);
        formPanel.setBorder(new TitledBorder("Enregistrer une consultation"));

        contenu.add(formPanel);
        // CENTRER L'ECRAN
        setLocationRelativeTo(null);

    }
    /*
    * Vecteur qui retourne la liste des docteurs depuis la base de données
    *
    */
    Vector<Object> getDoctors(){
        Vector<Object> data = new Vector<Object>();
        String sql = "SELECT matricule FROM Medecin";
        try (Connection conn = sqlConn.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();

            // loop through the result set
            while (rs.next()) {
                for (int i = 1; i <= columns; i++)
                {
                    data.addElement(rs.getObject(i));
                }

            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    /*
     * Vecteur qui retourne la liste des patients depuis la base de données
     *
     */
    Vector<Object> getPatients(){
        Vector<Object> data = new Vector<Object>();
        String sql = "SELECT num_ss FROM Patient";
        try (Connection conn = sqlConn.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();

            // loop through the result set
            while (rs.next()) {
                for (int i = 1; i <= columns; i++)
                {
                    data.addElement(rs.getObject(i));
                }

            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    /*
     * Vecteur qui retourne la liste des médicaments depuis la base de données
     *
     */
    Vector<Object> getMedicaments(){
        Vector<Object> data = new Vector<Object>();
        String sql = "SELECT code FROM Medicament";
        try (Connection conn = sqlConn.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();

            // loop through the result set
            while (rs.next()) {
                for (int i = 1; i <= columns; i++)
                {
                    data.addElement(rs.getObject(i));
                }

            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==addButton){
            if(dateField.getModel().getValue()!=null&&
               !nbJourField.getText().equals("")&&isDrugSelected) {
                String doctor = doctorField.getSelectedItem().toString();
                String patient = patientField.getSelectedItem().toString();
                String dateYear = String.valueOf(dateField.getModel().getYear());
                String dateMonth = String.valueOf(dateField.getModel().getMonth()+1);
                String dateDay = String.valueOf(dateField.getModel().getDay());
                //java.sql.Date date = (java.sql.Date) dateField.getModel().getValue();
                String date = dateYear+'-'+dateMonth+'-'+dateDay;
                String nbjours = nbJourField.getText();
                // SQL create new consultation
                sqlConn.createConsultation(doctor, patient, date, nbjours, medicamentsSelectionnes);
                this.dispose();
                // add new data to the table
                Object[] rowData = new Object[4];
                rowData[0] = model.getRowCount()+1;
                rowData[1] = dateField.getModel().getValue();
                rowData[2] = doctorField.getSelectedItem().toString();
                rowData[3] = patientField.getSelectedItem().toString();
                model.addRow(rowData);
            }
            else {
                JOptionPane.showMessageDialog(this,
                        "Veuillez renseigner tous les champs",
                        "Erreur", JOptionPane.ERROR_MESSAGE, null);
            }
        }
        if(e.getSource()==cancelButton){
            this.dispose();
        }

        }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        ListSelectionModel lsm = (ListSelectionModel)e.getSource();

        int firstIndex = e.getFirstIndex();
        int lastIndex = e.getLastIndex();
        boolean isAdjusting = e.getValueIsAdjusting();

        if(!isAdjusting) {
            if (lsm.isSelectionEmpty()) {
                isDrugSelected = false;
            } else {
                isDrugSelected = true;
                // Find out which indexes are selected.
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();

                medicamentsSelectionnes = new String[lsm.getSelectedItemsCount()];
                int j=0;
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        medicamentsSelectionnes[j]=drugs[i];
                        j++;
                    }
                }
            }
        }
    }
}


