package biblio_Gestion_Admin;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CatalogueAdmin extends JFrame {
    private JTable table;
    private JTextField searchField;

    public CatalogueAdmin() {
        setTitle("Catalogue des Livres");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

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

        // Ajout de la table à un JScrollPane pour permettre le défilement
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Ajout d'un champ de recherche
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Rechercher");
        JButton buttonEdit = new JButton("Modifier");
        JButton buttonSupp = new JButton("Supprimer");
        JButton buttonRetour = new JButton("Retour");
        JPanel searchPanel = new JPanel();
        searchPanel.add(buttonRetour);
        searchPanel.add(Box.createRigidArea(new Dimension(60, 0)));
        searchPanel.add(new JLabel("Rechercher par titre : "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(buttonEdit);
        searchPanel.add(buttonSupp);
        add(searchPanel, BorderLayout.NORTH);

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