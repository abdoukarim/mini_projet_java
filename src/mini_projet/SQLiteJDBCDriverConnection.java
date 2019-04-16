package mini_projet;

import java.sql.*;
import java.util.Random;
import java.util.Vector;

public class SQLiteJDBCDriverConnection {
    private int lastRowId;

    /**
     * Connect to a sample database
     */
    public static void testConnect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:/Users/linktelecom/Documents/MASTER1-SIR/JAVA/miniprojet.sql";
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * Connect to the mini_projet.db database
     * @return the Connection object
     */
    Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:/Users/linktelecom/Documents/MASTER1-SIR/JAVA/miniprojet.sql";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void createPatient(String nom){
        String sql = "INSERT INTO Patient(nom) VALUES(?);";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nom);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deletePatient(int num_ss){
        String sql = "DELETE FROM Patient WHERE num_ss=?;";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, num_ss);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createMedecin(String nom){
        String sql = "INSERT INTO Medecin(matricule, nom) VALUES(?,?);";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            Random rand = new Random();
            int matricule = rand.nextInt(500);
            pstmt.setInt(1, matricule);
            pstmt.setString(2, nom);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteMedecin(int matricule){
        String sql = "DELETE FROM Medecin WHERE matricule=?;";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, matricule);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createMedicament(String code, String libelle, String indication, String posologie){
        String sql = "INSERT INTO Medicament(code, libelle, indications, posologie) VALUES(?,?,?,?);";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            pstmt.setString(2, libelle);
            pstmt.setString(3, indication);
            pstmt.setString(4, posologie);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteMedicament(String code){
        String sql = "DELETE FROM Medicament WHERE code=?;";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createConsultation(String doctor, String patient, String date,
                                   String nbjours, String[] medicamentsSelectionnes){
        String sqlConsulte = "INSERT INTO Consulte(medecin_matricule, patient_num_ss) " +
                " VALUES(?,?);";
        String sql = "INSERT INTO Consultation(date, medecin_matricule, " +
                "patient_num_ss) VALUES(?,?,?);";
        //String sqlLastConsultationRowId = "select last_insert_rowid();";
        String sqlLastConsultationRowId = "SELECT MAX(NUMERO) AS LAST FROM Consultation;";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlConsulte);
             PreparedStatement pstmt2 = conn.prepareStatement(sql);
             Statement pstmt3 = conn.createStatement()) {

            pstmt.setInt(1, Integer.parseInt(doctor));
            pstmt.setInt(2, Integer.parseInt(patient));
            pstmt.executeUpdate();

            pstmt2.setString(1, date);
            pstmt2.setInt(2, Integer.parseInt(doctor));
            pstmt2.setInt(3, Integer.parseInt(patient));
            pstmt2.executeUpdate();

            ResultSet rs = pstmt3.executeQuery(sqlLastConsultationRowId);
            // loop through the result set
            while (rs.next()) {
                lastRowId = rs.getInt("LAST");
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        String sqlPrescrit = "INSERT INTO Prescrit(medicament_code, consultation_numero, " +
                "nombre_de_jours) VALUES(?,?,?);";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlPrescrit)) {
            for (String drug : medicamentsSelectionnes) {
                pstmt.setString(1, drug);
                pstmt.setInt(2, lastRowId);
                pstmt.setString(3, nbjours);
                //pstmt.executeUpdate();
                pstmt.addBatch();

            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.out.println(e.getMessage());


        }

    }

    public Vector<Vector<Object>> getConsultationDetail(int id){
        String sql = "SELECT med.nom as nom_medecin, pt.nom as nom_patient, c.date as date_consultation, c.numero as numero_consultation, m.libelle as libelle_medicament, m.indications, m.posologie, p.nombre_de_jours FROM prescrit as p\n" +
                "JOIN Medicament as m on p.medicament_code=m.code\n" +
                "JOIN Consultation as c on p.consultation_numero=c.numero\n" +
                "JOIN consulte as cs on c.medecin_matricule=cs.medecin_matricule and c.patient_num_ss=cs.patient_num_ss\n" +
                "JOIN Medecin as med on med.matricule=cs.medecin_matricule \n" +
                "JOIN Patient as pt on pt.num_ss=cs.patient_num_ss\n" +
                "WHERE c.numero="+id;

        Vector<Vector<Object>> consultationDataList = new Vector<Vector<Object>>();
        try (Connection conn = this.connect();
             PreparedStatement stmt  = conn.prepareStatement(sql)){
            ResultSet rs    = stmt.executeQuery();

            Vector<Object> consultationData = new Vector<Object>();
            //new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("date_consultation")); //Throws exception


            while (rs.next()) {
                consultationData.addElement(rs.getString("nom_medecin"));
                consultationData.addElement(rs.getString("nom_patient"));
                //consultationData.addElement(new SimpleDateFormat(
                //        "yyyy-MM-dd").parse(rs.getString("date_consultation")));
                consultationData.addElement(rs.getString("date_consultation"));
                consultationData.addElement(rs.getInt("numero_consultation"));
                consultationData.addElement(rs.getString("libelle_medicament"));
                consultationData.addElement(rs.getString("indications"));
                consultationData.addElement(rs.getString("posologie"));
                consultationData.addElement(rs.getString("nombre_de_jours"));
                // add to the vector
                consultationDataList.addElement(consultationData);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return consultationDataList;
    }

    public Vector<Vector<Object>> getConsultationIds(int id){
        String sql = "SELECT med.matricule, pt.num_ss, c.date as date_consultation, c.numero as numero_consultation, m.code, p.nombre_de_jours FROM prescrit as p\n" +
                "JOIN Medicament as m on p.medicament_code=m.code\n" +
                "JOIN Consultation as c on p.consultation_numero=c.numero\n" +
                "JOIN consulte as cs on c.medecin_matricule=cs.medecin_matricule and c.patient_num_ss=cs.patient_num_ss\n" +
                "JOIN Medecin as med on med.matricule=cs.medecin_matricule \n" +
                "JOIN Patient as pt on pt.num_ss=cs.patient_num_ss\n" +
                "WHERE c.numero="+id;

        Vector<Vector<Object>> consultationDataList = new Vector<Vector<Object>>();
        try (Connection conn = this.connect();
             PreparedStatement stmt  = conn.prepareStatement(sql)){
            ResultSet rs    = stmt.executeQuery();

            Vector<Object> consultationData = new Vector<Object>();
            //new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("date_consultation")); //Throws exception


            while (rs.next()) {
                consultationData.addElement(rs.getInt("matricule"));
                consultationData.addElement(rs.getInt("num_ss"));
                consultationData.addElement(rs.getString("date_consultation"));
                consultationData.addElement(rs.getInt("numero_consultation"));
                consultationData.addElement(rs.getString("code"));
                consultationData.addElement(rs.getString("nombre_de_jours"));
                // add to the vector
                consultationDataList.addElement(consultationData);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return consultationDataList;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

    }
}
