package biblio_Gestion_Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CatalogueAdmin extends JFrame {
    private JTable table;
    private JTextField searchField;

    public CatalogueAdmin() {
        setTitle("Catalogue des Livres");
        setSize(900, 600); // Agrandissement de la taille de la fenêtre
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        // Maximiser la fenêtre et la centrer sur l'écran
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Création du modèle de table
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Titre");
        model.addColumn("Genre");
        model.addColumn("Référence");
        model.addColumn("Disponibilité");
        model.addColumn("Date de publication");
        model.addColumn("Nombre de copies");
        model.addColumn("Auteur");

        // Connexion à la base de données et récupération des données
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String query = "SELECT livres.id_livre, livres.titre, livres.genre, livres.ref, livres.disponibilité, livres.date_pub, livres.nb_copie, auteurs.nom, auteurs.prenom " +
                    "FROM livres " +
                    "JOIN auteurs ON livres.id_auteur = auteurs.id_auteur";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    model.addRow(new Object[]{
                            resultSet.getInt("id_livre"),
                            resultSet.getString("titre"),
                            resultSet.getString("genre"),
                            resultSet.getString("ref"),
                            resultSet.getBoolean("disponibilité") ? "Disponible" : "Non disponible",
                            resultSet.getTimestamp("date_pub"),
                            resultSet.getInt("nb_copie"),
                            resultSet.getString("nom") + " " + resultSet.getString("prenom")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Création de la table avec le modèle de données
        table = new JTable(model);

        // Ajustement de la largeur des colonnes
        table.getColumnModel().getColumn(0).setPreferredWidth(10); // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(180); // Titre
        table.getColumnModel().getColumn(2).setPreferredWidth(40); // Genre
        table.getColumnModel().getColumn(3).setPreferredWidth(20); // Référence
        table.getColumnModel().getColumn(4).setPreferredWidth(60); // Disponibilité
        table.getColumnModel().getColumn(5).setPreferredWidth(150); // Date de publication
        table.getColumnModel().getColumn(6).setPreferredWidth(60); // Nombre de copies
        table.getColumnModel().getColumn(7).setPreferredWidth(200); // Auteur

        // Ajout de la table à un JScrollPane pour permettre le défilement
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Initialisation de searchField
        searchField = new JTextField(20);
        // Charger les images à partir du package biblio_Gestion_Admin
        ImageIcon searchIcon = new ImageIcon(getClass().getResource("search.png"));
        ImageIcon editIcon = new ImageIcon(getClass().getResource("edit.png"));
        ImageIcon deleteIcon = new ImageIcon(getClass().getResource("delete.png"));
        ImageIcon addIcon = new ImageIcon(getClass().getResource("add.png"));
        ImageIcon backIcon = new ImageIcon(getClass().getResource("back.png"));

        // Redimensionnement des icônes pour ajuster la taille des boutons
        searchIcon = new ImageIcon(searchIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        editIcon = new ImageIcon(editIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        deleteIcon = new ImageIcon(deleteIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        addIcon = new ImageIcon(addIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        backIcon = new ImageIcon(backIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));

        // Déclaration et initialisation des boutons avec les icônes redimensionnées
        JButton searchButton = new JButton(searchIcon);
        JButton buttonEdit = new JButton(editIcon);
        JButton buttonSupp = new JButton(deleteIcon);
        JButton buttonAdd = new JButton(addIcon);
        JButton buttonRetour = new JButton(backIcon);

        // Ajout de marges autour des icônes
        Insets buttonInsets = new Insets(5, 10, 5, 10); // Marges pour les boutons
        searchButton.setMargin(buttonInsets);
        buttonEdit.setMargin(buttonInsets);
        buttonSupp.setMargin(buttonInsets);
        buttonAdd.setMargin(buttonInsets);
        buttonRetour.setMargin(buttonInsets);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Utilisation d'un layout pour positionner les boutons au centre
        buttonPanel.add(buttonEdit);
        buttonPanel.add(buttonSupp);
        buttonPanel.add(buttonAdd);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Utilisation d'un layout pour positionner le champ de recherche au centre
        searchPanel.add(new JLabel("Rechercher par titre : "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Utilisation d'un layout pour positionner le bouton retour à droite
        bottomPanel.add(buttonRetour);

        JPanel mainPanel = new JPanel(new BorderLayout()); // Utilisation d'un layout border pour organiser les panels
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.NORTH);

        // Action du bouton de recherche
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                rechercherLivreParTitre(searchTerm);
            } else {
                // Si le champ de recherche est vide, recharger tous les livres dans le catalogue
                chargerTousLesLivres();
            }
        });

        // Action du bouton de modification
        buttonEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int idLivre = (int) table.getValueAt(selectedRow, 0);
                // Ouvrir la fenêtre de modification avec l'ID du livre sélectionné
                new biblio_Gestion_Fonctions.ModificationLivre(idLivre);
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un livre à modifier.");
            }
        });

        // Action du bouton de suppression
        buttonSupp.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int idLivre = (int) table.getValueAt(selectedRow, 0);
                // Supprimer le livre avec l'ID sélectionné
                supprimerLivre(idLivre);
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un livre à supprimer.");
            }
        });

        // Action du bouton d'ajout
        buttonAdd.addActionListener(e -> {
            // Ouvrir le formulaire d'ajout de livre
            new AjoutLivreForm();
        });

        setVisible(true);
    }

    // Méthode pour rechercher un livre par titre
    private void rechercherLivreParTitre(String titre) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Effacer le contenu actuel de la table

        // Connexion à la base de données et récupération des données
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String query = "SELECT livres.id_livre, livres.titre, livres.genre, livres.ref, livres.disponibilité, livres.date_pub, livres.nb_copie, auteurs.nom, auteurs.prenom " +
                    "FROM livres " +
                    "JOIN auteurs ON livres.id_auteur = auteurs.id_auteur " +
                    "WHERE livres.titre LIKE ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, "%" + titre + "%");
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    model.addRow(new Object[]{
                            resultSet.getInt("id_livre"),
                            resultSet.getString("titre"),
                            resultSet.getString("genre"),
                            resultSet.getString("ref"),
                            resultSet.getBoolean("disponibilité") ? "Disponible" : "Non disponible",
                            resultSet.getTimestamp("date_pub"),
                            resultSet.getInt("nb_copie"),
                            resultSet.getString("nom") + " " + resultSet.getString("prenom")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Méthode pour charger tous les livres dans le catalogue
    private void chargerTousLesLivres() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Effacer le contenu actuel de la table

        // Connexion à la base de données et récupération des données
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String query = "SELECT livres.id_livre, livres.titre, livres.genre, livres.ref, livres.disponibilité, livres.date_pub, livres.nb_copie, auteurs.nom, auteurs.prenom " +
                    "FROM livres " +
                    "JOIN auteurs ON livres.id_auteur = auteurs.id_auteur";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    model.addRow(new Object[]{
                            resultSet.getInt("id_livre"),
                            resultSet.getString("titre"),
                            resultSet.getString("genre"),
                            resultSet.getString("ref"),
                            resultSet.getBoolean("disponibilité") ? "Disponible" : "Non disponible",
                            resultSet.getTimestamp("date_pub"),
                            resultSet.getInt("nb_copie"),
                            resultSet.getString("nom") + " " + resultSet.getString("prenom")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Méthode pour supprimer un livre
    private void supprimerLivre(int idLivre) {
        // Appeler une méthode de la classe de suppression pour effectuer la suppression
        biblio_Gestion_Fonctions.SuppressionLivre.supprimer(idLivre);
        // Actualiser la liste des livres après la suppression
        chargerTousLesLivres();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CatalogueAdmin::new);
    }
}
