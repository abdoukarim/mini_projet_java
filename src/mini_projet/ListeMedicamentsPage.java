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

public class ListeMedicamentsPage extends JFrame implements ActionListener, DocumentListener,
        ListSelectionListener {
    TableRowSorter<DefaultTableModel> sorter;
    JTextArea filterText;
    JTable tableMedicaments;
    private JButton deleteButton, addButton, homeButton;
    private Icon homeIcon = new ImageIcon("image/home-page-icon.png");
    private Icon addIcon = new ImageIcon("image/medicine-box-2-icon.png");
    private Icon deleteIcon = new ImageIcon("image/Button-Delete-icon.png");
    private static SQLiteJDBCDriverConnection sqlConn = new SQLiteJDBCDriverConnection();


    ListeMedicamentsPage(){
        super();
        this.setTitle("Liste des médicaments");
        this.setSize(new Dimension(550, 500));
        this.setResizable(false);
        Container contenu = this.getContentPane();
        contenu.setLayout(new BorderLayout());

        JTable tableMedicaments = initTable();

        //TableColumn editColumn = tableMedicaments.getColumnModel().getColumn(3);
        //editColumn.setCellEditor(new DefaultCellEditor(btnEditer));
        contenu.add(tableMedicaments.getTableHeader(), BorderLayout.PAGE_START);
        contenu.add(tableMedicaments, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(tableMedicaments);


        // add a form for search feature
        JPanel searchForm = new JPanel(new FlowLayout());
        homeButton = new JButton(homeIcon);
        homeButton.addActionListener(this);
        searchForm.add(homeButton);
        JLabel searchLabel = new JLabel("Filtrer ");
        searchForm.add(searchLabel);
        filterText = new JTextArea(1,10);
        //tableMedicaments.setAutoCreateRowSorter(true);
        filterText.getDocument().addDocumentListener(this);

        //filterText.addActionListener(this);
        //filterText.addTextListener(this);
        searchForm.add(filterText);
        addButton = new JButton("ajouter", addIcon);
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
        String sql = "SELECT * FROM Medicament";
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
     * Vecteur qui retourne la liste des medicaments depuis la base de données
     *
     */
    Vector<Vector<Object>> getData(){
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        String sql = "SELECT * FROM Medicament";
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
        tableMedicaments = new JTable(new DefaultTableModel(data, columnNames));

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        sorter = new TableRowSorter<>(model);
        tableMedicaments.setRowSorter(sorter);
        tableMedicaments.setFillsViewportHeight(true);
        //tableMedicaments.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableMedicaments.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableMedicaments.getColumnModel().getColumn(3).setPreferredWidth(100);
        tableMedicaments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //tableMedicaments.getSelectionModel().addListSelectionListener(this);
        tableMedicaments.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent event) {
                        int viewRow = tableMedicaments.getSelectedRow();
                        if (viewRow < 0) {
                            //Selection got filtered away.
                        } else {
                            if(!event.getValueIsAdjusting()) {
                                int modelRow =
                                        tableMedicaments.convertRowIndexToModel(viewRow);
                            }

                        }
                    }
                }
        );
        return tableMedicaments;

    }

    /**
     * Update the row filter regular expression from the expression in
     * the text box.
     */
    private void newFilter() {
        RowFilter<DefaultTableModel, Object> rf = null;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter(filterText.getText(), 0);
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
            DefaultTableModel model =  (DefaultTableModel)tableMedicaments.getModel();
            AddMedicamentPage mp = new AddMedicamentPage(this, model);
            mp.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            mp.setVisible(true);
        }

        if(e.getSource()==deleteButton){
            int getSelectedRow = tableMedicaments.getSelectedRow();
            if(getSelectedRow>=0) {
                int response = openJOptionConfirmDialog(this,
                        "Voulez-vous supprimer la ligne sélectionnée ?", "Supprimer le médicament");
                if(response==0){
                    // SUPPRIMER LE médicament
                    int row = tableMedicaments.getSelectedRow();
                    tableMedicaments.getValueAt(row, 0);
                    String code = (String) tableMedicaments.getValueAt(row, 0);
                    sqlConn.deleteMedicament(code);
                    DefaultTableModel model = (DefaultTableModel)tableMedicaments.getModel();
                    model.removeRow(getSelectedRow);
                }

            }
            else{
                JOptionPane.showMessageDialog(this,"Veuillez sélectionner un médicament",
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
            int viewRow = tableMedicaments.getSelectedRow();
            if (viewRow < 0) {
                //Selection got filtered away.
                //statusText.setText("");
            } else {
                int modelRow = tableMedicaments.convertRowIndexToModel(viewRow);


            }
        }
    }
}

