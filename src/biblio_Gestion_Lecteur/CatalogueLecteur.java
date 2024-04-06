package biblio_Gestion_Lecteur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import biblio_Gestion_Lecteur.SessionUtilisateur;

public class CatalogueLecteur extends JFrame {
    private JPanel booksPanel;
    private JTextField searchField;

    public void startSession(int userId) {
        SessionUtilisateur.getInstance().demarrerSession(userId);
    }

    public CatalogueLecteur() {
        setTitle("Catalogue des Livres");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panneau principal pour afficher les livres
        booksPanel = new JPanel();
        booksPanel.setLayout(new GridLayout(0, 3, 10, 10)); // 3 livres par ligne avec un espace de 10 pixels
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
                JOptionPane.showMessageDialog(CatalogueLecteur.this, "Sélectionnez un livre en cliquant dessus pour l'emprunter.");
            }
        });

        // Charger tous les livres au démarrage de l'application
        chargerTousLesLivres();

        pack(); // Ajuster la taille de la fenêtre pour qu'elle s'adapte au contenu
        setLocationRelativeTo(null); // Centrer la fenêtre sur l'écran
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

    // Méthode pour redimensionner une image à une taille fixe
    private ImageIcon redimensionnerImage(ImageIcon imageIcon, int width, int height) {
        Image image = imageIcon.getImage();
        Image nouvelleImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(nouvelleImage);
    }

    // Méthode pour afficher un livre avec une couverture redimensionnée
    private void afficherLivre(ResultSet resultSet) throws SQLException {
        int idLivre = resultSet.getInt("id_livre");
        String titre = resultSet.getString("titre");
        String genre = resultSet.getString("genre");
        String reference = resultSet.getString("ref");
        boolean disponibilite = resultSet.getBoolean("disponibilité");
        String disponibiliteText = disponibilite ? "Disponible" : "Non disponible";

        // Récupération de l'image de couverture depuis la base de données
        byte[] coverBytes = resultSet.getBytes("couverture");
        ImageIcon icon = null;
        if (coverBytes != null && coverBytes.length > 0) {
            try {
                // Convertir les bytes en une image ImageIcon
                ByteArrayInputStream bais = new ByteArrayInputStream(coverBytes);
                BufferedImage image = ImageIO.read(bais);
                icon = new ImageIcon(image);

                // Redimensionner l'image à une taille fixe (par exemple, 200x300 pixels)
                icon = redimensionnerImage(icon, 200, 300);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Création du panneau pour afficher les informations du livre avec l'image redimensionnée
        JPanel bookPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // Centrer l'image et le texte
        gbc.insets = new Insets(10, 10, 10, 10); // Ajouter de l'espace entre les livres

        // Affichage de l'image de la couverture
        if (icon != null) {
            JLabel coverLabel = new JLabel();
            coverLabel.setIcon(icon);
            bookPanel.add(coverLabel, gbc);
        } else {
            JLabel coverLabel = new JLabel("Image non disponible");
            bookPanel.add(coverLabel, gbc);
        }

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

        bookPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int choix = JOptionPane.showConfirmDialog(CatalogueLecteur.this, "Voulez-vous emprunter ce livre ?", "Emprunter un livre", JOptionPane.YES_NO_OPTION);
                if (choix == JOptionPane.YES_OPTION) {
                    int idUtilisateur = SessionUtilisateur.getInstance().getId_u();
                    emprunterLivre(idLivre); // Appeler la méthode pour emprunter le livre
                    JOptionPane.showMessageDialog(CatalogueLecteur.this, "Le livre a été emprunté avec succès.");
                    chargerTousLesLivres();
                }
            }
        });

        // Ajout du panneau du livre au panneau principal
        booksPanel.add(bookPanel);
        booksPanel.revalidate();
    }


    // Méthode pour emprunter un livre
    public void emprunterLivre(int idLivre) {
        int idUtilisateur = SessionUtilisateur.getInstance().getId_u(); // Récupérer l'ID de l'utilisateur de la session
        if (idUtilisateur != -1) {
            // Utiliser l'ID de l'utilisateur de la session pour l'emprunt
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "")) {
                String query = "INSERT INTO emprunts (id_u, id_livre, date_emprunt, date_retour_prevue, date_retour_effectue, penalite) VALUES (?, ?, ?, ?, NULL, 0)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, idUtilisateur);
                preparedStatement.setInt(2, idLivre);
                preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000))); // Date de retour prévue après une semaine

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("L'emprunt a été ajouté avec succès.");
                }
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Utilisateur non connecté !");
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(CatalogueLecteur::new);
    }
}
