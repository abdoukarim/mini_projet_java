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

public class ListeDoctorsPage extends JFrame implements ActionListener, DocumentListener,
        ListSelectionListener {
    static TableRowSorter<DefaultTableModel> sorter;
    JTextArea filterText;
    JTable tableDoctors;
    private JButton deleteButton, addButton, homeButton;
    private Icon homeIcon = new ImageIcon("image/home-page-icon.png");
    private Icon userIcon = new ImageIcon("image/Actions-list-add-user-icon.png");
    private Icon deleteIcon = new ImageIcon("image/Button-Delete-icon.png");
    private static SQLiteJDBCDriverConnection sqlConn = new SQLiteJDBCDriverConnection();


    ListeDoctorsPage(){
        super();
        this.setTitle("Liste des docteurs");
        this.setSize(new Dimension(550, 500));
        this.setResizable(false);
        Container contenu = this.getContentPane();
        contenu.setLayout(new BorderLayout());

        JTable tableDoctors = initTable();
        //TableColumn editColumn = tableDoctors.getColumnModel().getColumn(3);
        //editColumn.setCellEditor(new DefaultCellEditor(btnEditer));
        contenu.add(tableDoctors.getTableHeader(), BorderLayout.PAGE_START);
        contenu.add(tableDoctors, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(tableDoctors);


        // add a form for search feature
        JPanel searchForm = new JPanel(new FlowLayout());
        homeButton = new JButton(homeIcon);
        homeButton.addActionListener(this);
        searchForm.add(homeButton);
        JLabel searchLabel = new JLabel("Filtrer ");
        searchForm.add(searchLabel);
        filterText = new JTextArea(1,10);
        //tableDoctors.setAutoCreateRowSorter(true);
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

        contenu.add(searchForm, BorderLayout.SOUTH);
        contenu.add(scrollPane);
        // CENTRER L'ECRAN
        setLocationRelativeTo(null);

    }

    Vector<Object> getColumnNames(){
        Vector<Object> columnNames = new Vector<Object>();
        String sql = "SELECT * FROM Medecin";
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
     * Vecteur qui retourne la liste des docteurs depuis la base de données
     *
     */
    Vector<Vector<Object>> getData(){
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        String sql = "SELECT * FROM Medecin";
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
        tableDoctors = new JTable(new DefaultTableModel(data, columnNames));

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        sorter = new TableRowSorter<>(model);
        tableDoctors.setRowSorter(sorter);
        tableDoctors.setFillsViewportHeight(true);
        tableDoctors.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableDoctors.getColumnModel().getColumn(1).setPreferredWidth(450);
        tableDoctors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableDoctors.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent event) {
                        int viewRow = tableDoctors.getSelectedRow();
                        if (viewRow < 0) {
                            //Selection got filtered away.
                        } else {
                            if(!event.getValueIsAdjusting()) {
                                int modelRow =
                                        tableDoctors.convertRowIndexToModel(viewRow);

                            }

                        }
                    }
                }
        );
        return tableDoctors;

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
        if(e.getSource()==addButton){
            DefaultTableModel model =  (DefaultTableModel)tableDoctors.getModel();
            AddDoctorPage dp = new AddDoctorPage(this, model);
            dp.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dp.setVisible(true);
        }
        if(e.getSource()==deleteButton){
            int getSelectedRow = tableDoctors.getSelectedRow();
            if(getSelectedRow>=0) {
                int response = openJOptionConfirmDialog(this,
                        "Voulez-vous supprimer la ligne sélectionnée ?", "Supprimer le docteur");
                if(response==0){
                    // SUPPRIMER LE DOCTEUR
                    int row = tableDoctors.getSelectedRow();
                    tableDoctors.getValueAt(row, 0);
                    int matricule = (int) tableDoctors.getValueAt(row, 0);
                    sqlConn.deleteMedecin(matricule);
                    DefaultTableModel model = (DefaultTableModel)tableDoctors.getModel();
                    model.removeRow(getSelectedRow);
                }

            }
            else{
                JOptionPane.showMessageDialog(this,"Veuillez sélectionner un docteur",
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

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()) {
            int viewRow = tableDoctors.getSelectedRow();
            if (viewRow < 0) {
                //Selection got filtered away.
                //statusText.setText("");
            } else {
                int modelRow =
                        tableDoctors.convertRowIndexToModel(viewRow);
            }
        }
    }
}

