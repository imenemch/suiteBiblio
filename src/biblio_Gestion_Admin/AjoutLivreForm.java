package biblio_Gestion_Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import biblio_Gestion_Admin.CatalogueAdmin;
public class AjoutLivreForm extends JFrame {
    private JTextField titreField;
    private JTextField genreField;
    private JTextField refField;
    private JComboBox<String> disponibiliteComboBox;
    private JTextField nbCopieField;
    private JComboBox<String> auteurComboBox;
    private JButton choisirImageBtn;

    private JLabel ajouterAuteurLabel;

    private File selectedImageFile;
    private  CatalogueAdmin catalogueAdmin;

    public AjoutLivreForm(CatalogueAdmin catalogueAdmin) {
        this.catalogueAdmin = catalogueAdmin;
        setTitle("Ajouter un Livre");
        setSize(400, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer l'interface
        setLayout(new GridLayout(9, 2));

        JLabel titreLabel = new JLabel("Titre:");
        titreField = new JTextField();
        add(titreLabel);
        add(titreField);

        JLabel genreLabel = new JLabel("Genre:");
        genreField = new JTextField();
        add(genreLabel);
        add(genreField);

        JLabel refLabel = new JLabel("Référence:");
        refField = new JTextField();
        add(refLabel);
        add(refField);

        JLabel disponibiliteLabel = new JLabel("Disponibilité:");
        disponibiliteComboBox = new JComboBox<>();
        disponibiliteComboBox.addItem("Disponible");
        disponibiliteComboBox.addItem("Non disponible");
        add(disponibiliteLabel);
        add(disponibiliteComboBox);

        JLabel nbCopieLabel = new JLabel("Nombre de copies:");
        nbCopieField = new JTextField();
        add(nbCopieLabel);
        add(nbCopieField);

        JLabel auteurLabel = new JLabel("Auteur:");
        auteurComboBox = new JComboBox<>();
        add(auteurLabel);
        add(auteurComboBox);

        choisirImageBtn = new JButton("Choisir une image");
        add(new JLabel("Image de couverture:"));
        add(choisirImageBtn);

        choisirImageBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedImageFile = fileChooser.getSelectedFile();
            }
        });

        remplirAuteurs();

        JButton addButton = new JButton("Ajouter");
        add(addButton);

        addButton.addActionListener(e -> ajouterLivre());

        // Création du label avec un style amélioré
        ajouterAuteurLabel = new JLabel("<html><u>Si l'auteur n'existe pas dans la liste, cliquez ici pour l'ajouter</u></html>");
        ajouterAuteurLabel.setForeground(Color.BLUE); // Couleur bleue pour le lien
        ajouterAuteurLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Curseur de la main au survol
        Font font = ajouterAuteurLabel.getFont(); // Police actuelle
        ajouterAuteurLabel.setFont(font.deriveFont(Font.BOLD)); // Police en gras
        add(ajouterAuteurLabel);


        ajouterAuteurLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                ouvrirPageAjoutAuteur();
            }
        });

        setVisible(true);
    }


    private void ouvrirPageAjoutAuteur() {
        // Ouvrir la page d'ajout d'auteur
        Connection connexion = null;
        try {
            connexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "");
            new biblio_Gestion_Fonctions.AjouterAuteur(connexion);
            // Après avoir ajouté un nouvel auteur, remplir à nouveau la liste des auteurs
            remplirAuteurs();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la connexion à la base de données : " + e.getMessage());
        }
    }


    private void remplirAuteurs() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String query = "SELECT nom, prenom FROM auteurs";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();

                // Créer une liste pour stocker les noms des auteurs
                ArrayList<String> auteurs = new ArrayList<>();

                while (resultSet.next()) {
                    String nom = resultSet.getString("nom");
                    String prenom = resultSet.getString("prenom");
                    auteurs.add(nom + " " + prenom);
                }

                // Trier les noms des auteurs par ordre alphabétique
                Collections.sort(auteurs);

                // Effacer les anciens éléments de la JComboBox
                auteurComboBox.removeAllItems();

                // Ajouter les noms triés à la JComboBox
                for (String auteur : auteurs) {
                    auteurComboBox.addItem(auteur);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void ajouterLivre() {
        String titre = titreField.getText().trim();
        String genre = genreField.getText().trim();
        String ref = refField.getText().trim();
        String disponibilite = disponibiliteComboBox.getSelectedItem().toString().trim();
        int disponibiliteValue = disponibilite.equals("Disponible") ? 1 : 0;
        String nbCopie = nbCopieField.getText().trim();
        String auteur = auteurComboBox.getSelectedItem().toString().trim();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String query = "INSERT INTO livres (titre, genre, ref, disponibilité, date_pub, nb_copie, id_auteur, couverture) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, titre);
                statement.setString(2, genre);
                statement.setString(3, ref);
                statement.setInt(4, disponibiliteValue);
                statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                statement.setString(6, nbCopie);
                int idAuteur = getIdAuteur(auteur);
                statement.setInt(7, idAuteur);
                if (selectedImageFile != null) {
                    FileInputStream fis = new FileInputStream(selectedImageFile);
                    statement.setBinaryStream(8, fis, (int) selectedImageFile.length());
                } else {
                    statement.setNull(8, java.sql.Types.BLOB);
                }
                statement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Livre ajouté avec succès !");
                catalogueAdmin.updateTable();
                dispose();
            }
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du livre : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getIdAuteur(String nomPrenom) {
        int idAuteur = -1;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String[] parts = nomPrenom.split(" ");
            String nom = parts[0];
            String prenom = parts[1];
            String query = "SELECT id_auteur FROM auteurs WHERE nom = ? AND prenom = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, nom);
                statement.setString(2, prenom);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    idAuteur = resultSet.getInt("id_auteur");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return idAuteur;
    }
    // Méthode pour vérifier si l'auteur existe déjà dans la base de données
    private boolean isAuteurExistant(String auteur) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String[] parts = auteur.split(" ");
            String nom = parts[0];
            String prenom = parts[1];
            String query = "SELECT * FROM auteurs WHERE nom = ? AND prenom = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, nom);
                statement.setString(2, prenom);
                ResultSet resultSet = statement.executeQuery();
                return resultSet.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    public static void main(String[] args) {
        CatalogueAdmin catalogueAdmin = new CatalogueAdmin();
        SwingUtilities.invokeLater(() -> new AjoutLivreForm(catalogueAdmin));
    }

}
