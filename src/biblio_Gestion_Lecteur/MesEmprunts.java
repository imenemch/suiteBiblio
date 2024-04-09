package biblio_Gestion_Lecteur;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.table.TableColumnModel;
import java.text.SimpleDateFormat;
import java.text.ParseException;


public class MesEmprunts extends JFrame {
    private JTable empruntsTable;
    private JButton retournerButton;

    public MesEmprunts() {
        setTitle("Mes Emprunts");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Créer un panneau pour le bouton de retour
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Centrer le bouton
        retournerButton = new JButton("Retourner le livre");
        retournerButton.addActionListener(new RetournerButtonActionListener());
        topPanel.add(retournerButton);
        add(topPanel, BorderLayout.NORTH);


        // Créer un modèle de table par défaut pour stocker les données des emprunts
        DefaultTableModel tableModel = new DefaultTableModel();
        empruntsTable = new JTable(tableModel);
        empruntsTable.setRowHeight(30); // Augmenter la hauteur des lignes
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

    private class RetournerButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Récupérer la ligne sélectionnée dans le tableau
            int selectedRow = empruntsTable.getSelectedRow();
            if (selectedRow != -1) {
                int idEmp = (int) empruntsTable.getValueAt(selectedRow, 0);
                String dateRetourEffectuee = (String) empruntsTable.getValueAt(selectedRow, 4);
                if (dateRetourEffectuee == null) {
                    // Le livre n'a pas encore été retourné
                    // Mettre à jour la date de retour effectuée dans la base de données
                    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "")) {
                        String updateQuery = "UPDATE emprunts SET date_retour_effectue = ? WHERE id_emp = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        // Convertir la date actuelle en java.sql.Date
                        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
                        updateStatement.setDate(1, currentDate);

                        updateStatement.setInt(2, idEmp);
                        updateStatement.executeUpdate();
                        updateStatement.close();

                        // Afficher le message de succès
                        JOptionPane.showMessageDialog(MesEmprunts.this, "Livre emprunté avec succès");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    // Rafraîchir les données du tableau
                    rafraichirTableau();
                }
            } else {
                // Aucune ligne n'est sélectionnée, afficher un message d'erreur
                JOptionPane.showMessageDialog(MesEmprunts.this, "Veuillez sélectionner un emprunt.");
            }
        }


        private void rafraichirTableau() {
            // Effacer les données actuelles du tableau
            DefaultTableModel model = (DefaultTableModel) empruntsTable.getModel();
            model.setRowCount(0);
            // Recharger les données depuis la base de données
            chargerDonneesDepuisBaseDeDonnees();

            // Mettre à jour la colonne Pénalité en fonction de la date de retour
            for (int i = 0; i < model.getRowCount(); i++) {
                String dateRetourEffectuee = (String) model.getValueAt(i, 4);
                String dateRetourPrevue = (String) model.getValueAt(i, 3);
                if (dateRetourEffectuee != null && !dateRetourEffectuee.isEmpty()) {
                    try {
                        // Convertir les dates en objets Date pour pouvoir les comparer
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        // Convertir la date de retour effectuée en objet java.util.Date
                        java.util.Date retourEffectueDateUtil = format.parse(dateRetourEffectuee);
                        // Convertir java.util.Date en java.sql.Date
                        java.sql.Date retourEffectueDate = new java.sql.Date(retourEffectueDateUtil.getTime());

                        // Convertir la date de retour prévue en objet java.util.Date
                        java.util.Date retourPrevueDateUtil = format.parse(dateRetourPrevue);
                        // Convertir java.util.Date en java.sql.Date
                        java.sql.Date retourPrevueDate = new java.sql.Date(retourPrevueDateUtil.getTime());

                        // Calculer la différence en jours
                        long difference = retourEffectueDate.getTime() - retourPrevueDate.getTime();
                        long joursRetard = difference / (1000 * 60 * 60 * 24);
                        // Si la date de retour dépasse la date prévue de plus d'une semaine (7 jours)
                        if (joursRetard > 7) {
                            // Mettre à jour la colonne Pénalité avec 10£
                            model.setValueAt("10£", i, 5);
                        } else {
                            // Mettre à jour la colonne Pénalité avec "Aucune pénalité"
                            model.setValueAt("Aucune pénalité", i, 5);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }



        private void chargerDonneesDepuisBaseDeDonnees() {
            // Récupérer l'ID de l'utilisateur à partir de la session
            int idUtilisateur = SessionUtilisateur.getInstance().getId_u();

            DefaultTableModel tableModel = (DefaultTableModel) empruntsTable.getModel();
            tableModel.setRowCount(0); // Effacer les données actuelles

            // Récupérer les emprunts de l'utilisateur à partir de la base de données
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "")) {
                String query = "SELECT emprunts.id_emp, livres.titre, emprunts.date_emprunt, emprunts.date_retour_prevue, emprunts.date_retour_effectue, emprunts.penalite " +
                        "FROM emprunts " +
                        "INNER JOIN livres ON emprunts.id_livre = livres.id_livre " +
                        "WHERE emprunts.id_u = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, idUtilisateur);
                ResultSet resultSet = statement.executeQuery();

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
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MesEmprunts().setVisible(true);
        });
    }
}