package biblio_Gestion_Lecteur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import biblio_Gestion_Lecteur.SessionUtilisateur;
import biblio_Gestion_Lecteur.MesEmprunts;
import biblioSession.LoginPage;


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

        // Panneau pour la recherche et les boutons
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Ajout de marges externes
        searchField = new JTextField(25); // Augmentation de la taille de la zone de texte
        JButton searchButton = new JButton("Rechercher");
        searchButton.setMargin(new Insets(5, 10, 5, 10)); // Augmentation des marges internes du bouton
        ImageIcon empruntsIcon = new ImageIcon(getClass().getResource("panier.png"));
        empruntsIcon = new ImageIcon(empruntsIcon.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)); // Augmentation de la taille de l'icône
        JButton empruntsButton = new JButton(empruntsIcon);
        empruntsButton.setMargin(new Insets(5, 10, 5, 10)); // Augmentation des marges internes du bouton


        // Création du bouton des emprunts avec texte
        JButton empruntsWithTextButton = new JButton("Voir mes emprunts", empruntsIcon);
        empruntsWithTextButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Aligner le texte à droite de l'icône

        // Bouton "Mes favoris" avec une icône favoris.png
        ImageIcon favorisIcon = new ImageIcon(getClass().getResource("favoris.png"));
        favorisIcon = new ImageIcon(favorisIcon.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
        JButton favorisButton = new JButton("Mes favoris", favorisIcon);
        favorisButton.setMargin(new Insets(5, 10, 5, 10)); // Augmentation des marges internes du bouton

        favorisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FavorisLecteur favorisLecteur = new FavorisLecteur();
                favorisLecteur.setVisible(true);
            }
        });
// Création du bouton de déconnexion
        JButton deconnexionButton = new JButton("Déconnexion");
        deconnexionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Créer une instance de la classe LoginPage et l'afficher
                LoginPage login = new LoginPage();
                login.setVisible(true);
                // Fermer la fenêtre actuelle (CatalogueLecteur)
                dispose();
            }
        });

// Ajout du bouton de déconnexion au panneau de recherche
        searchPanel.add(Box.createHorizontalGlue()); // Ajout d'espace pour centrer le bouton de déconnexion
        searchPanel.add(deconnexionButton); // Ajout du bouton de déconnexion

        searchPanel.add(Box.createHorizontalGlue()); // Ajout d'espace pour centrer le bouton de déconnexion
        searchPanel.add(deconnexionButton); // Ajout du bouton de déconnexion au panneau de recherche
        searchPanel.add(new JLabel("Rechercher par titre : "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(empruntsWithTextButton); // Ajout du bouton des emprunts avec texte
        searchPanel.add(Box.createHorizontalStrut(10)); // Ajout d'un espace horizontal
        searchPanel.add(favorisButton); // Ajout du bouton "Mes favoris"

        add(searchPanel, BorderLayout.NORTH);

        // Action du bouton de recherche
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText().trim();
                rechercherLivresParTitre(searchTerm);
            }
        });

        empruntsWithTextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Créer une instance de MesEmprunts et l'afficher
                MesEmprunts mesEmprunts = new MesEmprunts();
                mesEmprunts.setVisible(true);
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

    // Méthode pour redimensionner une image à une taille fixe sans altérer sa qualité
    private ImageIcon redimensionnerImage(ImageIcon imageIcon, int width, int height) {
        Image image = imageIcon.getImage();
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return new ImageIcon(resizedImage);
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
                    if (idUtilisateur != 0) { // Vérifier si l'utilisateur est connecté
                        emprunterLivre(idLivre); // Appeler la méthode pour emprunter le livre
                       // JOptionPane.showMessageDialog(CatalogueLecteur.this, "Le livre a été emprunté avec succès.");
                        chargerTousLesLivres();
                    } else {
                        JOptionPane.showMessageDialog(CatalogueLecteur.this, "Vous devez vous connecter pour pouvoir emprunter ce livre !");
                        // Redirection vers la page de connexion
                        redirigerVersLoginPage();
                    }
                }
            }
        });

        // Création du bouton "Ajouter aux favoris" avec une icône de cœur
        ImageIcon heartIcon = new ImageIcon(getClass().getResource("heart.png")); // Assurez-vous d'avoir une image de cœur (par exemple, heart.png) dans votre répertoire d'images
        Image heartImage = heartIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH); // Réduire la taille de l'icône
        ImageIcon smallHeartIcon = new ImageIcon(heartImage);
        JButton addToFavoritesButton = new JButton(smallHeartIcon);

        // Vérifier si le livre est déjà dans les favoris de l'utilisateur
        int idUtilisateur = SessionUtilisateur.getInstance().getId_u();
        boolean isFavorite = checkIfFavorite(idUtilisateur, idLivre);
        if(isFavorite) {
            // Si le livre est déjà un favori, changer l'icône du bouton en "heartred.png"
            ImageIcon heartRedIcon = new ImageIcon(getClass().getResource("heartred.png"));
            Image heartRedImage = heartRedIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            ImageIcon smallHeartRedIcon = new ImageIcon(heartRedImage);
            addToFavoritesButton.setIcon(smallHeartRedIcon);
        }
        addToFavoritesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code pour ajouter le livre aux favoris
            }
        });

        addToFavoritesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ajouter ici le code pour ajouter le livre aux favoris
                int idUtilisateur = SessionUtilisateur.getInstance().getId_u();
                if (idUtilisateur != 0) {
                    ajouterEnFavoris(idUtilisateur, idLivre); // Appeler la méthode pour ajouter le livre en favori
                    JOptionPane.showMessageDialog(CatalogueLecteur.this, "Le livre a été ajouté aux favoris avec succès.");
                    // Changer l'icône du bouton en "heartred.png"
                    ImageIcon heartRedIcon = new ImageIcon(getClass().getResource("heartred.png"));
                    Image heartRedImage = heartRedIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                    ImageIcon smallHeartRedIcon = new ImageIcon(heartRedImage);
                    addToFavoritesButton.setIcon(smallHeartRedIcon);
                } else {
                    JOptionPane.showMessageDialog(CatalogueLecteur.this, "Vous devez vous connecter pour pouvoir ajouter ce livre aux favoris !");
                    redirigerVersLoginPage();
                }
            }
        });


        gbc.gridy = 5; // Ajouter le bouton sous les informations du livre
        bookPanel.add(addToFavoritesButton, gbc);

        // Ajout du panneau du livre au panneau principal
        booksPanel.add(bookPanel);
        booksPanel.revalidate();
    }


    public void emprunterLivre(int idLivre) {
        int idUtilisateur = SessionUtilisateur.getInstance().getId_u(); // Récupérer l'ID de l'utilisateur de la session
        if (idUtilisateur != 0) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "")) {
                // Vérifier la disponibilité du livre
                String checkAvailabilityQuery = "SELECT disponibilité, nb_copie FROM livres WHERE id_livre = ?";
                PreparedStatement checkAvailabilityStatement = connection.prepareStatement(checkAvailabilityQuery);
                checkAvailabilityStatement.setInt(1, idLivre);
                ResultSet availabilityResultSet = checkAvailabilityStatement.executeQuery();
                if (availabilityResultSet.next()) {
                    boolean disponibilite = availabilityResultSet.getBoolean("disponibilité");
                    int nbCopies = availabilityResultSet.getInt("nb_copie");
                    if (disponibilite && nbCopies > 0) { // Si le livre est disponible et il y a au moins une copie disponible
                        // Effectuer l'emprunt
                        String insertEmpruntQuery = "INSERT INTO emprunts (id_u, id_livre, date_emprunt, date_retour_prevue, date_retour_effectue, penalite) VALUES (?, ?, ?, ?, NULL, 0)";
                        PreparedStatement insertEmpruntStatement = connection.prepareStatement(insertEmpruntQuery);
                        insertEmpruntStatement.setInt(1, idUtilisateur);
                        insertEmpruntStatement.setInt(2, idLivre);
                        insertEmpruntStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                        insertEmpruntStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000))); // Date de retour prévue après une semaine
                        int rowsInserted = insertEmpruntStatement.executeUpdate();
                        if (rowsInserted > 0) {
                            // Soustraire une copie du nombre total de copies disponibles
                            String updateCopiesQuery = "UPDATE livres SET nb_copie = ? WHERE id_livre = ?";
                            PreparedStatement updateCopiesStatement = connection.prepareStatement(updateCopiesQuery);
                            updateCopiesStatement.setInt(1, nbCopies - 1);
                            updateCopiesStatement.setInt(2, idLivre);
                            updateCopiesStatement.executeUpdate();

                            // Mettre à jour la disponibilité du livre dans la base de données
                            String updateAvailabilityQuery = "UPDATE livres SET disponibilité = 0 WHERE id_livre = ?";
                            PreparedStatement updateAvailabilityStatement = connection.prepareStatement(updateAvailabilityQuery);
                            updateAvailabilityStatement.setInt(1, idLivre);
                            updateAvailabilityStatement.executeUpdate();

                            JOptionPane.showMessageDialog(CatalogueLecteur.this, "Le livre a été emprunté avec succès.");
                            chargerTousLesLivres();
                        } else {
                            System.out.println("Erreur lors de l'emprunt du livre.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(CatalogueLecteur.this, "Ce livre n'est pas disponible pour l'emprunt.");
                    }
                } else {
                    System.out.println("Livre non trouvé.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(CatalogueLecteur.this, "Vous devez vous connecter pour pouvoir emprunter ce livre !");
        }
    }


    // Méthode pour ajouter un livre en favori pour un utilisateur donné
    public void ajouterEnFavoris(int idUtilisateur, int idLivre) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "")) {
            String query = "INSERT INTO favoris (id_utilisateur, id_livre) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idUtilisateur);
            preparedStatement.setInt(2, idLivre);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Le livre a été ajouté aux favoris avec succès.");
            } else {
                System.out.println("Erreur lors de l'ajout du livre aux favoris.");
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //methode pour retirer le coeur rouge apres l'avoir retiré de la liste des favoris
    public void chargerFavoris() {
        // Effacer tous les livres actuellement affichés
        booksPanel.removeAll();
        booksPanel.revalidate();
        booksPanel.repaint();

        // Connexion à la base de données et récupération des favoris de l'utilisateur connecté
        int idUtilisateur = SessionUtilisateur.getInstance().getId_u();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "")) {
            String query = "SELECT livres.* FROM livres INNER JOIN favoris ON livres.id_livre = favoris.id_livre WHERE favoris.id_utilisateur = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idUtilisateur);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    afficherLivre(resultSet);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Méthode pour vérifier si un livre est un favori de l'utilisateur
    private boolean checkIfFavorite(int idUtilisateur, int idLivre) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "")) {
            String query = "SELECT * FROM favoris WHERE id_utilisateur = ? AND id_livre = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idUtilisateur);
                statement.setInt(2, idLivre);
                ResultSet resultSet = statement.executeQuery();
                return resultSet.next(); // Renvoie true si le livre est un favori, false sinon
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    // Méthode pour rediriger l'utilisateur vers la page de connexion
    private void redirigerVersLoginPage() {
        LoginPage login = new LoginPage();
        login.setVisible(true);
        // Fermer la fenêtre actuelle (CatalogueLecteur)
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CatalogueLecteur::new);
    }
}