package biblio_Gestion_Admin;

import biblioSession.Database;
import biblioSession.User;
import biblioSession.UserManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ListeUsers extends JFrame {
    private static JTable table;
    private JTextField searchField;

    public ListeUsers() {
        setTitle("Liste des Utilisateurs");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Création de la barre de recherche et des boutons
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Rechercher");
        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        // Ajout des actions aux boutons
        searchButton.addActionListener(e -> rechercherUtilisateur());
        addButton.addActionListener(e -> ajouterUtilisateur());
        editButton.addActionListener(e -> modifierUtilisateur());
        deleteButton.addActionListener(e -> supprimerUtilisateur());

        // Création du panneau de navigation
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        navPanel.add(searchField);
        navPanel.add(searchButton);
        navPanel.add(addButton);
        navPanel.add(editButton);
        navPanel.add(deleteButton);

        // Ajout du panneau de navigation à la fenêtre
        add(navPanel, BorderLayout.NORTH);

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
        UserManager userManager = new UserManager(new Database());
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

    // Méthode pour rechercher un utilisateur par nom, prénom ou email
    private void rechercherUtilisateur() {
        String searchTerm = searchField.getText().trim();
        // Si le terme de recherche est vide, recharger tous les utilisateurs
        if (searchTerm.isEmpty()) {
            chargerTousLesUtilisateurs();
            return;
        }

        // Rechercher les utilisateurs correspondant au terme de recherche
        UserManager userManager = new UserManager(new Database());
        List<User> userList = userManager.searchUsers(searchTerm);

        // Effacer le contenu actuel de la table
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        // Ajouter les utilisateurs trouvés à la table
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

        // Afficher un message si aucun utilisateur n'est trouvé
        if (userList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun utilisateur trouvé.", "Recherche d'utilisateur", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Méthode pour charger tous les utilisateurs dans la table
    public static void chargerTousLesUtilisateurs() {
        // Récupérer tous les utilisateurs depuis la base de données
        UserManager userManager = new UserManager(new Database());
        List<User> userList = userManager.getAllUsers();

        // Effacer le contenu actuel de la table
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        // Ajouter tous les utilisateurs à la table
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
    }

    // Méthode pour ajouter un utilisateur
    private void ajouterUtilisateur() {
        // Ouvrir la fenêtre d'ajout d'utilisateur
        new AjoutUtilisateurFrame();
    }

    // Méthode pour modifier un utilisateur
    private void modifierUtilisateur() {
        // Obtenir l'index de la ligne sélectionnée dans la table
        int rowIndex = table.getSelectedRow();
        if (rowIndex == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à modifier.", "Modification d'utilisateur", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Obtenir l'ID de l'utilisateur sélectionné
        int userId = (int) table.getValueAt(rowIndex, 0);

        // Ouvrir une fenêtre pour modifier l'utilisateur avec l'ID correspondant
        // (vous devez implémenter cette fonctionnalité dans une autre classe)
        JOptionPane.showMessageDialog(this, "Fonctionnalité de modification d'utilisateur à implémenter pour l'ID : " + userId, "Modification d'utilisateur", JOptionPane.INFORMATION_MESSAGE);
    }

    // Méthode pour supprimer un utilisateur
    // Méthode pour supprimer un utilisateur
    private void supprimerUtilisateur() {
        // Obtenir l'index de la ligne sélectionnée dans la table
        int rowIndex = table.getSelectedRow();
        if (rowIndex == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à supprimer.", "Suppression d'utilisateur", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Obtenir l'ID de l'utilisateur sélectionné
        int userId = (int) table.getValueAt(rowIndex, 0);

        // Ouvrir une fenêtre de confirmation pour la suppression de l'utilisateur
        int option = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cet utilisateur ?", "Confirmation de suppression", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            // Supprimer l'utilisateur de la base de données en utilisant UserManager
            UserManager userManager = new UserManager(new Database());
            boolean deleted = userManager.deleteUser(userId);
            if (deleted) {
                JOptionPane.showMessageDialog(this, "Utilisateur supprimé avec succès.", "Suppression d'utilisateur", JOptionPane.INFORMATION_MESSAGE);
                // Recharger la liste des utilisateurs dans la table
                chargerTousLesUtilisateurs();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'utilisateur.", "Suppression d'utilisateur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public static void main(String[] args) {
        // Utilisation de l'invocation de l'interface graphique Swing dans un thread dédié (EDT)
        SwingUtilities.invokeLater(ListeUsers::new);
    }
}
