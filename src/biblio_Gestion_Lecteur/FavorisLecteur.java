package biblio_Gestion_Lecteur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.ByteArrayInputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class FavorisLecteur extends JFrame {
    private JPanel favoritesPanel;

    public FavorisLecteur() {
        setTitle("Mes favoris");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Fermer seulement cette fenêtre lorsque l'utilisateur clique sur "Fermer"
        setLayout(new BorderLayout());

        favoritesPanel = new JPanel();
        favoritesPanel.setLayout(new GridLayout(0, 3, 10, 10)); // 3 livres par ligne avec un espacement de 10 pixels
        JScrollPane scrollPane = new JScrollPane(favoritesPanel);
        add(scrollPane, BorderLayout.CENTER);

        chargerFavoris();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void chargerFavoris() {
        // Effacer tous les favoris actuellement affichés
        favoritesPanel.removeAll();
        favoritesPanel.revalidate();
        favoritesPanel.repaint();

        // Connexion à la base de données et récupération des favoris de l'utilisateur connecté
        int idUtilisateur = SessionUtilisateur.getInstance().getId_u();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "")) {
            String query = "SELECT livres.* FROM livres INNER JOIN favoris ON livres.id_livre = favoris.id_livre WHERE favoris.id_utilisateur = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idUtilisateur);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    afficherFavori(resultSet);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void afficherFavori(ResultSet resultSet) throws SQLException {
        int idLivre = resultSet.getInt("id_livre");
        String titre = resultSet.getString("titre");

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

        // Création du panneau pour afficher les informations du favori avec l'image redimensionnée
        JPanel favoritePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // Centrer l'image et le texte
        gbc.insets = new Insets(10, 10, 10, 10); // Ajouter de l'espace entre les favoris

        // Affichage de l'image de la couverture
        if (icon != null) {
            JLabel coverLabel = new JLabel();
            coverLabel.setIcon(icon);
            favoritePanel.add(coverLabel, gbc);
        } else {
            JLabel coverLabel = new JLabel("Image non disponible");
            favoritePanel.add(coverLabel, gbc);
        }

        // Affichage du titre du livre
        gbc.gridy = 1;
        JLabel titleLabel = new JLabel(titre);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Centrer le texte horizontalement
        favoritePanel.add(titleLabel, gbc);

        // Bouton pour retirer le favori
        JButton removeFavoriteButton = new JButton("Retirer des favoris");
        removeFavoriteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                retirerDesFavoris(idLivre);
                chargerFavoris(); // Recharger la liste des favoris après avoir retiré un favori
                // Mettre à jour l'affichage des favoris dans le catalogue
                if (getParent() instanceof CatalogueLecteur) {
                    ((CatalogueLecteur) getParent()).chargerFavoris();
                }
            }
        });
        gbc.gridy = 2;
        favoritePanel.add(removeFavoriteButton, gbc);

        // Ajout du panneau du favori au panneau principal
        favoritesPanel.add(favoritePanel);
        favoritesPanel.revalidate();
    }

    // Méthode pour retirer un livre des favoris pour un utilisateur donné
    private void retirerDesFavoris(int idLivre) {
        int idUtilisateur = SessionUtilisateur.getInstance().getId_u();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "")) {
            String query = "DELETE FROM favoris WHERE id_utilisateur = ? AND id_livre = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idUtilisateur);
            preparedStatement.setInt(2, idLivre);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Le livre a été retiré des favoris avec succès.");
            } else {
                System.out.println("Erreur lors du retrait du livre des favoris.");
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
}
