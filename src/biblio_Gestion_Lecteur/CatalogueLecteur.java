package biblio_Gestion_Lecteur;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class CatalogueLecteur extends JFrame {
    private JTable table;
    private JTextField searchField;

    public CatalogueLecteur() {
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
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "")) {
            String query = "SELECT livres.id_livre, livres.titre, livres.genre, livres.ref, livres.disponibilité, livres.date_pub, livres.nb_copie, CONCAT(auteurs.nom, ' ', auteurs.prenom) AS auteur " +
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
                            resultSet.getString("auteur")
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
        JButton emprunterButton = new JButton("Emprunter");
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Rechercher par titre : "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(emprunterButton); // Ajout du bouton "Emprunter"
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

        // Action du bouton "Emprunter"
        emprunterButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int idLivre = (int) table.getValueAt(selectedRow, 0);
                effectuerEmprunt(idLivre); // Appel de la méthode pour effectuer l'emprunt
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un livre à emprunter.");
            }
        });

        setVisible(true);
    }

    // Méthode pour rechercher un livre par titre
    private void rechercherLivreParTitre(String titre) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Effacer le contenu actuel de la table

        // Connexion à la base de données et récupération des données
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "")) {
            String query = "SELECT livres.id_livre, livres.titre, livres.genre, livres.ref, livres.disponibilité, livres.date_pub, livres.nb_copie, CONCAT(auteurs.nom, ' ', auteurs.prenom) AS auteur " +
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
                            resultSet.getString("auteur")
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
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "")) {
            String query = "SELECT livres.id_livre, livres.titre, livres.genre, livres.ref, livres.disponibilité, livres.date_pub, livres.nb_copie, CONCAT(auteurs.nom, ' ', auteurs.prenom) AS auteur " +
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
                            resultSet.getString("auteur")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void effectuerEmprunt(int idLivre) {
        if (!estUtilisateurConnecte()) {
            JOptionPane.showMessageDialog(this, "Veuillez vous connecter pour emprunter un livre.");
            return; // Arrête le processus d'emprunt si l'utilisateur n'est pas connecté
        }

        int idUtilisateur = obtenirIdUtilisateur(obtenirEmailUtilisateurConnecte());

        // Connexion à la base de données pour insérer l'emprunt
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "")) {
            // Préparer la requête SQL pour insérer l'emprunt
            String query = "INSERT INTO emprunts (id_livre, id_u, date_emprunt) VALUES (?, ?, NOW())";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idLivre);
                statement.setInt(2, idUtilisateur);
                // Exécuter la requête d'insertion
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "L'emprunt a été effectué avec succès.");
                    // Mettre à jour l'affichage des livres pour refléter les changements
                    chargerTousLesLivres();
                } else {
                    JOptionPane.showMessageDialog(this, "Une erreur s'est produite lors de l'emprunt.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Une erreur s'est produite lors de l'emprunt.");
        }
    }

    // Méthode pour vérifier si un utilisateur est connecté
    private boolean estUtilisateurConnecte() {
        // À remplacer par votre propre logique pour vérifier si un utilisateur est connecté
        // Par exemple, vous pouvez vérifier si l'e-mail de l'utilisateur est présent dans la session
        return false; // Par défaut, retourne false (l'utilisateur n'est pas connecté)
    }

    // Méthode pour obtenir l'e-mail de l'utilisateur connecté (à remplacer par votre propre méthode d'obtention de l'e-mail)
    private String obtenirEmailUtilisateurConnecte() {
        // À remplacer par votre propre logique pour obtenir l'e-mail de l'utilisateur connecté
        return "utilisateur@example.com"; // Exemple: retourne un e-mail fictif pour le moment
    }

    // Méthode pour obtenir l'ID de l'utilisateur à partir de son e-mail
    private int obtenirIdUtilisateur(String email) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "")) {
            String query = "SELECT id_u FROM users WHERE email = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("id_u");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1; // Retourne -1 si l'ID de l'utilisateur n'est pas trouvé ou s'il y a une erreur
    }

    // Méthode pour calculer la date de retour prévue (par exemple, 14 jours après la date d'emprunt)
    private java.sql.Timestamp calculerDateRetourPrevue(java.sql.Timestamp dateEmprunt) {
        // À remplacer par votre propre logique pour calculer la date de retour prévue
        // Par exemple, ajouter 14 jours à la date d'emprunt
        long millisecondsInDay = 1000 * 60 * 60 * 24;
        long millisecondsInTwoWeeks = millisecondsInDay * 14;
        return new java.sql.Timestamp(dateEmprunt.getTime() + millisecondsInTwoWeeks);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CatalogueLecteur::new);
    }
}
