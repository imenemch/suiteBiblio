package biblio_Gestion_Lecteur;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CatalogueLecteur extends JFrame {
    private JPanel booksPanel;
    private JTextField searchField;

    public CatalogueLecteur() {
        setTitle("Catalogue des Livres");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panneau pour afficher les livres
        booksPanel = new JPanel();
        booksPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JScrollPane scrollPane = new JScrollPane(booksPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Panneau pour la recherche et le bouton Emprunter
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Rechercher");
        JButton emprunterButton = new JButton("Emprunter");
        searchPanel.add(new JLabel("Rechercher par titre : "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(emprunterButton);
        add(searchPanel, BorderLayout.NORTH);

        // Action du bouton de recherche
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText().trim();
                rechercherLivresParTitre(searchTerm);
            }
        });

        // Action du bouton "Emprunter"
        emprunterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(CatalogueLecteur.this, "Fonctionnalité d'emprunt à implémenter.");
            }
        });

        // Charger tous les livres au démarrage de l'application
        chargerTousLesLivres();

        setVisible(true);
    }

    // Méthode pour rechercher des livres par titre
    private void rechercherLivresParTitre(String titre) {
        // Effacer tous les livres actuellement affichés
        booksPanel.removeAll();
        booksPanel.revalidate();
        booksPanel.repaint();

        // Connexion à la base de données et récupération des données
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "")) {
            String query = "SELECT * FROM livres WHERE titre LIKE ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, "%" + titre + "%");
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    afficherLivre(resultSet);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Méthode pour charger tous les livres dans le catalogue
    private void chargerTousLesLivres() {
        // Effacer tous les livres actuellement affichés
        booksPanel.removeAll();
        booksPanel.revalidate();
        booksPanel.repaint();

        // Connexion à la base de données et récupération des données
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "")) {
            String query = "SELECT * FROM livres";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    afficherLivre(resultSet);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Méthode pour afficher un livre sur le panneau
    private void afficherLivre(ResultSet resultSet) throws SQLException {
        int idLivre = resultSet.getInt("id_livre");
        String titre = resultSet.getString("titre");
        String genre = resultSet.getString("genre");
        String reference = resultSet.getString("ref");
        boolean disponibilite = resultSet.getBoolean("disponibilité");
        String disponibiliteText = disponibilite ? "Disponible" : "Non disponible";

        // Récupération de l'image de couverture depuis la base de données
        byte[] coverBytes = resultSet.getBytes("couverture");
        ImageIcon icon = new ImageIcon(coverBytes);

        // Création du panneau pour afficher les informations du livre
        JPanel bookPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // Centrer l'image et le texte
        gbc.insets = new Insets(10, 10, 10, 10); // Ajouter de l'espace entre les livres

        // Affichage de l'image de la couverture
        JLabel coverLabel = new JLabel();
        coverLabel.setIcon(icon);
        bookPanel.add(coverLabel, gbc);

        // Affichage des informations du livre
        gbc.gridy = 1;
        JLabel titleLabel = new JLabel("Titre: " + titre);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Centrer le texte horizontalement
        bookPanel.add(titleLabel, gbc);

        gbc.gridy = 2;
        JLabel genreLabel = new JLabel("Genre: " + genre);
        genreLabel.setHorizontalAlignment(SwingConstants.CENTER); // Centrer le texte horizontalement
        bookPanel.add(genreLabel, gbc);

        gbc.gridy = 3;
        JLabel referenceLabel = new JLabel("Référence: " + reference);
        referenceLabel.setHorizontalAlignment(SwingConstants.CENTER); // Centrer le texte horizontalement
        bookPanel.add(referenceLabel, gbc);

        gbc.gridy = 4;
        JLabel disponibiliteLabel = new JLabel("Disponibilité: " + disponibiliteText);
        disponibiliteLabel.setHorizontalAlignment(SwingConstants.CENTER); // Centrer le texte horizontalement
        bookPanel.add(disponibiliteLabel, gbc);

        // Ajout du panneau du livre au panneau principal
        booksPanel.add(bookPanel);
        booksPanel.revalidate();
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(CatalogueLecteur::new);
    }
}
