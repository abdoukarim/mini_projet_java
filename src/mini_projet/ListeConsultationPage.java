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

public class ListeConsultationPage extends JFrame implements ActionListener, DocumentListener,
        ListSelectionListener {
    TableRowSorter<DefaultTableModel> sorter;
    JTextArea filterText;
    JTable tableHistorique;
    private JButton detailButton, homeButton, addButton, updateButton;
    private Icon homeIcon = new ImageIcon("image/home-page-icon.png");
    private Icon detailIcon = new ImageIcon("image/Data-View-Details-icon.png");
    private Icon userIcon = new ImageIcon("image/Actions-list-add-user-icon.png");
    private static SQLiteJDBCDriverConnection sqlConn = new SQLiteJDBCDriverConnection();

    ListeConsultationPage(){
        super();
        ListePatientsPage.getSelectedRow = 0;
        this.setTitle("Historique des consultations");
        this.setSize(new Dimension(550, 500));
        this.setResizable(false);
        Container contenu = this.getContentPane();
        contenu.setLayout(new BorderLayout());

        JTable tableMedicaments = initTable();


        //TableColumn editColumn = tableHistorique.getColumnModel().getColumn(3);
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
        //tableHistorique.setAutoCreateRowSorter(true);
        filterText.getDocument().addDocumentListener(this);

        //filterText.addActionListener(this);
        //filterText.addTextListener(this);
        searchForm.add(filterText);
        addButton = new JButton("ajouter", userIcon);
        addButton.addActionListener(this);
        searchForm.add(addButton);
        //updateButton = new JButton("modifier");
        //updateButton.addActionListener(this);
        //searchForm.add(updateButton);
        detailButton = new JButton("détail", detailIcon);
        detailButton.addActionListener(this);
        searchForm.add(detailButton);
        contenu.add(searchForm, BorderLayout.SOUTH);
        contenu.add(scrollPane);
        // CENTRER L'ECRAN
        setLocationRelativeTo(null);

    }

    Vector<Object> getColumnNames(){
        Vector<Object> columnNames = new Vector<Object>();
        String sql = "SELECT * FROM Consultation";
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
     * Vecteur qui retourne la liste des consultations depuis la base de données
     *
     */
    Vector<Vector<Object>> getData(){
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        String sql = "SELECT * FROM Consultation";
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
        tableHistorique = new JTable(new DefaultTableModel(data, columnNames));

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        sorter = new TableRowSorter<>(model);
        tableHistorique.setRowSorter(sorter);
        tableHistorique.setFillsViewportHeight(true);
        //tableHistorique.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableHistorique.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableHistorique.getColumnModel().getColumn(3).setPreferredWidth(100);
        tableHistorique.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //tableHistorique.getSelectionModel().addListSelectionListener(this);
        tableHistorique.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent event) {
                        int viewRow = tableHistorique.getSelectedRow();
                        if (viewRow < 0) {
                            //Selection got filtered away.
                        } else {
                            if(!event.getValueIsAdjusting()) {
                                int modelRow =
                                        tableHistorique.convertRowIndexToModel(viewRow);

                            }

                        }
                    }
                }
        );
        return tableHistorique;

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
        if(e.getSource()==detailButton){
            if(tableHistorique.getSelectedRow()!=-1){
                int row = tableHistorique.getSelectedRow();
                tableHistorique.getValueAt(row, 0);
                int selectedID = (int) tableHistorique.getValueAt(row, 0);
                DefaultTableModel model =  (DefaultTableModel)tableHistorique.getModel();
                ShowConsultationPage sc = new ShowConsultationPage(this, model, selectedID);
                sc.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                sc.setVisible(true);
            }
            else {
                JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner une consultation.",
                        "Détail de la consultation", JOptionPane.ERROR_MESSAGE, null);
            }
        }
        if(e.getSource()==addButton){
            DefaultTableModel model =  (DefaultTableModel)tableHistorique.getModel();
            AddConsultationPage cp = new AddConsultationPage(this, model);
            cp.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            cp.setVisible(true);

        }

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
            int viewRow = tableHistorique.getSelectedRow();
            if (viewRow < 0) {
                //Selection got filtered away.
            } else {
                int modelRow =
                        tableHistorique.convertRowIndexToModel(viewRow);

            }
        }
    }
}

