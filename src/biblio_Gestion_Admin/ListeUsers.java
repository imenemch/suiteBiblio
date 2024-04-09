package biblio_Gestion_Admin;

import biblioSession.Database;
import gestionUser.AjoutUtilisateurFrame;
import gestionUser.ModificationUtilisateurFrame;
import gestionUser.User;
import gestionUser.UserManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;

public class ListeUsers extends JFrame {
    private static JTable table;
    private JTextField searchField;

    public ListeUsers() {
        setTitle("Liste des Utilisateurs");
        setSize(2000, 700);
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

// Centrer la fenêtre au milieu de l'écran
        setLocationRelativeTo(null);

        searchField = new JTextField(20);
        ImageIcon searchIcon = new ImageIcon(getClass().getResource("/biblio_Gestion_Admin/search.png"));
        ImageIcon addButtonIcon = new ImageIcon(getClass().getResource("/biblio_Gestion_Admin/useradd.png"));
        ImageIcon editButtonIcon = new ImageIcon(getClass().getResource("/biblio_Gestion_Admin/edit.png"));
        ImageIcon deleteButtonIcon = new ImageIcon(getClass().getResource("/biblio_Gestion_Admin/delete.png"));

// Redimensionner les icônes
        int iconWidth = 20;
        int iconHeight = 20;
        searchIcon = new ImageIcon(searchIcon.getImage().getScaledInstance(iconWidth, iconHeight, Image.SCALE_DEFAULT));
        addButtonIcon = new ImageIcon(addButtonIcon.getImage().getScaledInstance(iconWidth, iconHeight, Image.SCALE_DEFAULT));
        editButtonIcon = new ImageIcon(editButtonIcon.getImage().getScaledInstance(iconWidth, iconHeight, Image.SCALE_DEFAULT));
        deleteButtonIcon = new ImageIcon(deleteButtonIcon.getImage().getScaledInstance(iconWidth, iconHeight, Image.SCALE_DEFAULT));

// Créer les boutons avec les icônes redimensionnées
        JButton searchButton = new JButton(searchIcon);
        JButton addButton = new JButton(addButtonIcon);
        JButton editButton = new JButton(editButtonIcon);
        JButton deleteButton = new JButton(deleteButtonIcon);

        // Création du lien vers CatalogueAdmin
        JLabel linkCatalogue = new JLabel("Accéder au Catalogue Admin");
        linkCatalogue.setForeground(Color.BLUE);
        linkCatalogue.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkCatalogue.setHorizontalAlignment(SwingConstants.LEFT); // Modification de l'alignement à gauche
        linkCatalogue.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Redirection vers la page CatalogueAdmin lors du clic sur le lien
                new CatalogueAdmin();
                dispose(); // Fermer la fenêtre actuelle
            }
        });

        // Ajout des actions aux boutons
        searchButton.addActionListener(e -> rechercherUtilisateur());
        addButton.addActionListener(e -> ajouterUtilisateur());
        editButton.addActionListener(e -> modifierUtilisateur());
        deleteButton.addActionListener(e -> supprimerUtilisateur());

        // Création du panneau de navigation
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Alignement à gauche
        navPanel.add(linkCatalogue); // Ajout du lien vers CatalogueAdmin
        navPanel.add(Box.createHorizontalStrut(20)); // Ajout de l'espace horizontal
        navPanel.add(searchField);
        navPanel.add(searchButton);
        navPanel.add(addButton);
        navPanel.add(editButton);
        navPanel.add(deleteButton);

        // Ajout du panneau de navigation à la fenêtre
        add(navPanel, BorderLayout.NORTH);

        // Création du modèle de tableau avec des colonnes vides
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Empêcher l'édition des cellules
            }
        };
        model.addColumn("ID");
        model.addColumn("Nom");
        model.addColumn("Prénom");
        model.addColumn("Email");
        model.addColumn("Rôle");
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
                    user.getDate_created()
            });
        }


        // Création de la table avec le modèle
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Centrer les cellules du tableau
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        // Augmenter la hauteur des lignes du tableau
        table.setRowHeight(30);

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
        // Effacer le contenu actuel de la table
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        // Récupérer tous les utilisateurs depuis la base de données
        UserManager userManager = new UserManager(new Database());
        List<User> userList = userManager.getAllUsers();

        // Ajouter tous les utilisateurs à la table
        for (User user : userList) {
            model.addRow(new Object[]{
                    user.getId(),
                    user.getNom(),
                    user.getPrenom(),
                    user.getEmail(),
                    user.getRole(),
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

        // Récupérer l'ID de l'utilisateur sélectionné
        int userId = (int) table.getValueAt(rowIndex, 0);

        // Récupérer les informations de l'utilisateur sélectionné depuis la base de données
        UserManager userManager = new UserManager(new Database());
        User user = userManager.getUserById(userId);

        // Ouvrir une fenêtre de modification avec les informations de l'utilisateur sélectionné
        new ModificationUtilisateurFrame(user);
    }


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
