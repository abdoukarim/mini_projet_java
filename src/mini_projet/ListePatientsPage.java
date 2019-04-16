package mini_projet;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class ListePatientsPage extends JFrame implements ActionListener, DocumentListener{
    static TableRowSorter<DefaultTableModel> sorter;
    JTextArea filterText;
    private JButton historiqueButton, deleteButton, addButton, homeButton;
    private Icon homeIcon = new ImageIcon("image/home-page-icon.png");
    private static SQLiteJDBCDriverConnection sqlConn = new SQLiteJDBCDriverConnection();
    private Icon userIcon = new ImageIcon("image/Actions-list-add-user-icon.png");
    private Icon deleteIcon = new ImageIcon("image/Button-Delete-icon.png");
    private Icon historyIcon = new ImageIcon("image/Order-history-icon.png");
    static int getSelectedRow;
    JTable tablePatients;

    ListePatientsPage(){
        super();
        this.setTitle("Liste des patients");
        this.setSize(new Dimension(550, 500));
        this.setResizable(false);
        Container contenu = this.getContentPane();
        contenu.setLayout(new BorderLayout());

        JTable tablePatients = initTable();
        contenu.add(tablePatients.getTableHeader(), BorderLayout.PAGE_START);
        contenu.add(tablePatients, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(tablePatients);


        // add a form for search feature
        JPanel searchForm = new JPanel(new FlowLayout());
        homeButton = new JButton(homeIcon);
        homeButton.addActionListener(this);
        searchForm.add(homeButton);
        JLabel searchLabel = new JLabel("Filtrer ");
        searchForm.add(searchLabel);
        filterText = new JTextArea(1,10);
        //tablePatients.setAutoCreateRowSorter(true);
        filterText.getDocument().addDocumentListener(this);

        //filterText.addActionListener(this);
        //filterText.addTextListener(this);
        searchForm.add(filterText);
        addButton = new JButton("ajouter", userIcon);
        addButton.addActionListener(this);
        searchForm.add(addButton);
        deleteButton = new JButton("supprimer", deleteIcon);
        deleteButton.addActionListener(this);
        searchForm.add(deleteButton);
        historiqueButton = new JButton("historique", historyIcon);
        historiqueButton.addActionListener(this);
        searchForm.add(historiqueButton);
        contenu.add(searchForm, BorderLayout.SOUTH);
        contenu.add(scrollPane);
        // CENTRER L'ECRAN
        setLocationRelativeTo(null);

    }

    Vector<Object> getColumnNames(){
        Vector<Object> columnNames = new Vector<Object>();
        String sql = "SELECT * FROM Patient";
        try (Connection conn = sqlConn.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();
            //  Get column names

            for (int i = 1; i <= columns; i++)
            {
                columnNames.addElement( md.getColumnName(i) );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return columnNames;
    }

    /*
     * Vecteur qui retourne la liste des patients depuis la base de données
     *
     */
    Vector<Vector<Object>> getData(){
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        String sql = "SELECT * FROM Patient";
        try (Connection conn = sqlConn.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();

            // loop through the result set
            while (rs.next()) {
                Vector<Object> row = new Vector<Object>(columns);
                for (int i = 1; i <= columns; i++)
                {
                    row.addElement( rs.getObject(i) );
                }

                data.addElement( row );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


    JTable initTable(){
        Vector<Vector<Object>> data = getData();
        Vector<Object> columnNames = getColumnNames();
        tablePatients = new JTable(new DefaultTableModel(data, columnNames));

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        sorter = new TableRowSorter<>(model);
        tablePatients.setRowSorter(sorter);
        tablePatients.setFillsViewportHeight(true);
        tablePatients.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablePatients.getColumnModel().getColumn(1).setPreferredWidth(500);
        tablePatients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //tablePatients.getSelectionModel().addListSelectionListener(this);
        tablePatients.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent event) {
                        int viewRow = tablePatients.getSelectedRow();
                        if (viewRow < 0) {
                            //Selection got filtered away.
                            //statusText.setText("");
                        } else {
                            if(!event.getValueIsAdjusting()) {
                                int modelRow =
                                        tablePatients.convertRowIndexToModel(viewRow);
                                getSelectedRow = tablePatients.getSelectedRow();
                                System.out.println(String.format("Selected Row in view: %d. " +
                                                "Selected Row in model: %d.",
                                        viewRow, modelRow));
                            }

                        }
                    }
                }
        );
        return tablePatients;

    }

    /**
     * Update the row filter regular expression from the expression in
     * the text box.
     */
    private void newFilter() {
        RowFilter<DefaultTableModel, Object> rf = null;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter(filterText.getText(), 1);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==homeButton){
            this.setVisible(false);
            HomePage hp = new HomePage();
            hp.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            hp.setVisible(true);
        }
        if(e.getSource()==historiqueButton){
            if(getSelectedRow>=0) {
                System.out.println(getSelectedRow);
                this.setVisible(false);
                ListeConsultationPage lcp = new ListeConsultationPage();
                lcp.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                lcp.setVisible(true);
            }
            else {
                JOptionPane.showMessageDialog(this,"Veuillez sélectionner un patient",
                        "Erreur", JOptionPane.ERROR_MESSAGE, null);
            }
        }

        if(e.getSource()==addButton){
            DefaultTableModel model =  (DefaultTableModel)tablePatients.getModel();
            AddPatientPage pp = new AddPatientPage(this, model);
            pp.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            pp.setVisible(true);
        }

        if(e.getSource()==deleteButton){
            if(tablePatients.getSelectedRow()>=0) {
                int response = openJOptionConfirmDialog(this,
                        "Voulez-vous supprimer la ligne sélectionnée ?", "Supprimer le patient");
                if(response==0){
                    // SUPPRIMER LE PATIENT
                    int row = tablePatients.getSelectedRow();
                    tablePatients.getValueAt(row, 0);
                    int num_ss = (int) tablePatients.getValueAt(row, 0);
                    sqlConn.deletePatient(num_ss);
                    DefaultTableModel model = (DefaultTableModel)tablePatients.getModel();
                    model.removeRow(getSelectedRow);
                }

            }
            else{
                JOptionPane.showMessageDialog(this,"Veuillez sélectionner un patient",
                        "Erreur", JOptionPane.ERROR_MESSAGE, null);
            }

        }
    }

    static int openJOptionConfirmDialog(Component comp, String question,String titre) {
        Object options[ ] = {"OUI","NON"}; // on ne tient compte que de ces boutons
        return JOptionPane.showOptionDialog(comp,question,titre, JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        newFilter();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        newFilter();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        newFilter();
    }

}
