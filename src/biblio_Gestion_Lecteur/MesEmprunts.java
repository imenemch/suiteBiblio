package biblio_Gestion_Lecteur;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import javax.swing.table.TableColumnModel;

public class MesEmprunts extends JFrame {
    private JTable empruntsTable;

    public MesEmprunts() {
        setTitle("Mes Emprunts");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Créer un modèle de table par défaut pour stocker les données des emprunts
        DefaultTableModel tableModel = new DefaultTableModel();
        empruntsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(empruntsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Récupérer l'ID de l'utilisateur à partir de la session
        int idUtilisateur = SessionUtilisateur.getInstance().getId_u();

        // Récupérer les emprunts de l'utilisateur à partir de la base de données
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "")) {
            String query = "SELECT emprunts.id_emp, livres.titre, emprunts.date_emprunt, emprunts.date_retour_prevue, emprunts.date_retour_effectue, emprunts.penalite " +
                    "FROM emprunts " +
                    "INNER JOIN livres ON emprunts.id_livre = livres.id_livre " +
                    "WHERE emprunts.id_u = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idUtilisateur);
            ResultSet resultSet = statement.executeQuery();

            // Ajouter les données des emprunts à la table
            tableModel.addColumn("ID Emprunt");
            tableModel.addColumn("Titre du Livre");
            tableModel.addColumn("Date Emprunt");
            tableModel.addColumn("Date Retour Prévue");
            tableModel.addColumn("Date Retour Effectuée");
            tableModel.addColumn("Pénalité");

            while (resultSet.next()) {
                int idEmp = resultSet.getInt("id_emp");
                String titre = resultSet.getString("titre");
                String dateEmprunt = resultSet.getString("date_emprunt");
                String dateRetourPrevue = resultSet.getString("date_retour_prevue");
                String dateRetourEffectuee = resultSet.getString("date_retour_effectue");
                double penalite = resultSet.getDouble("penalite");

                tableModel.addRow(new Object[]{idEmp, titre, dateEmprunt, dateRetourPrevue, dateRetourEffectuee, penalite});
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Ajuster la largeur des colonnes
        TableColumnModel columnModel = empruntsTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50); // ID Emprunt
        columnModel.getColumn(1).setPreferredWidth(150); // Titre du Livre
        columnModel.getColumn(2).setPreferredWidth(100); // Date Emprunt
        columnModel.getColumn(3).setPreferredWidth(120); // Date Retour Prévue
        columnModel.getColumn(4).setPreferredWidth(120); // Date Retour Effectuée
        columnModel.getColumn(5).setPreferredWidth(80); // Pénalité

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MesEmprunts().setVisible(true);
        });
    }
}
