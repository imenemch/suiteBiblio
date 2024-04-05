package biblio_Gestion_Admin;

import biblioSession.Database;
import biblioSession.User;
import biblioSession.UserManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ListeUsers extends JFrame {
    private JTable table;

    public ListeUsers() {
        setTitle("Liste des Utilisateurs");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Création du modèle de tableau avec des colonnes vides
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nom");
        model.addColumn("Prénom");
        model.addColumn("Email");
        model.addColumn("Rôle");
        model.addColumn("Actif");
        model.addColumn("Date de création");

        // Récupération de la liste des utilisateurs depuis la base de données
        UserManager userManager = new UserManager(new Database()); // Assurez-vous d'importer Database
        List<User> userList = userManager.getAllUsers();

        // Ajout des utilisateurs à la table
        for (User user : userList) {
            model.addRow(new Object[]{
                    user.getId(),
                    user.getNom(),
                    user.getPrenom(),
                    user.getEmail(),
                    user.getRole(),
                    user.getActive(),
                    user.getDate_created()
            });
        }

        // Création de la table avec le modèle
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Ajout de la table à un JScrollPane pour permettre le défilement si nécessaire
        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        // Utilisation de l'invocation de l'interface graphique Swing dans un thread dédié (EDT)
        SwingUtilities.invokeLater(ListeUsers::new);
    }
}
