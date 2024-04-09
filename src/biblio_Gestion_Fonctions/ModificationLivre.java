package biblio_Gestion_Fonctions;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

import biblio_Gestion_Admin.CatalogueAdmin;

public class ModificationLivre extends JFrame {
    private JTextField titreField;
    private JTextField genreField;
    private JTextField referenceField;
    private JComboBox<String> disponibiliteComboBox;
    private JTextField datePublicationField;
    private JComboBox<String> auteurComboBox; // Barre déroulante pour les auteurs
    private JTextField nbCopiesField; // Champ pour le nombre de copies
    private CatalogueAdmin catalogueAdmin;

    public ModificationLivre(int idLivre, CatalogueAdmin catalogueAdmin) {
        this.catalogueAdmin = catalogueAdmin;
        setTitle("Modifier Livre");
        setSize(500, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocation(catalogueAdmin.getX() +700, catalogueAdmin.getY() + 200);
        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        String[] currentValues = getCurrentValues(idLivre);

        JLabel titleLabel = new JLabel("Titre:");
        titleLabel.setHorizontalAlignment(JLabel.LEFT);
        titreField = new JTextField(currentValues[0], 20);
        centerPanel.add(titleLabel);
        centerPanel.add(titreField);

        JLabel genreLabel = new JLabel("Genre:");
        genreLabel.setHorizontalAlignment(JLabel.LEFT);
        genreField = new JTextField(currentValues[1], 20);
        centerPanel.add(genreLabel);
        centerPanel.add(genreField);

        JLabel referenceLabel = new JLabel("Référence:");
        referenceLabel.setHorizontalAlignment(JLabel.LEFT);
        referenceField = new JTextField(currentValues[2], 20);
        centerPanel.add(referenceLabel);
        centerPanel.add(referenceField);

        JLabel disponibiliteLabel = new JLabel("Disponibilité:");
        disponibiliteLabel.setHorizontalAlignment(JLabel.LEFT);
        String[] disponibiliteOptions = {"Disponible", "Non disponible"};
        disponibiliteComboBox = new JComboBox<>(disponibiliteOptions);
        disponibiliteComboBox.setSelectedItem(currentValues[3]);
        centerPanel.add(disponibiliteLabel);
        centerPanel.add(disponibiliteComboBox);

        JLabel datePublicationLabel = new JLabel("Date de publication:");
        datePublicationLabel.setHorizontalAlignment(JLabel.LEFT);
        datePublicationField = new JTextField(currentValues[4], 20);
        centerPanel.add(datePublicationLabel);
        centerPanel.add(datePublicationField);

        JLabel auteurLabel = new JLabel("Auteur:");
        auteurLabel.setHorizontalAlignment(JLabel.LEFT);
        auteurComboBox = new JComboBox<>(getAuteurs()); // Remplir la barre déroulante avec les noms des auteurs
        auteurComboBox.setSelectedItem(currentValues[5] + " " + currentValues[6]);
        centerPanel.add(auteurLabel);
        centerPanel.add(auteurComboBox);

        // Champ pour le nombre de copies
        JLabel nbCopiesLabel = new JLabel("Nombre de copies:");
        nbCopiesLabel.setHorizontalAlignment(JLabel.LEFT);
        nbCopiesField = new JTextField(currentValues[7], 20);
        centerPanel.add(nbCopiesLabel);
        centerPanel.add(nbCopiesField);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton modifyButton = new JButton("Modifier");
        modifyButton.addActionListener(e -> {
            modifierLivre(idLivre, titreField.getText(), genreField.getText(), referenceField.getText(),
                    (String) disponibiliteComboBox.getSelectedItem(), datePublicationField.getText(),
                    (String) auteurComboBox.getSelectedItem(), nbCopiesField.getText()); // Convertir le texte en entier pour le nombre de copies
            dispose();
        });
        bottomPanel.add(modifyButton);

        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        bottomPanel.add(cancelButton);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private String[] getCurrentValues(int idLivre) {
        String[] values = new String[8]; // Augmentez la taille à 8 pour inclure le nombre de copies
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String query = "SELECT livres.titre, livres.genre, livres.ref, livres.disponibilité, livres.date_pub, auteurs.nom, auteurs.prenom, livres.nb_copie FROM livres INNER JOIN auteurs ON livres.id_auteur = auteurs.id_auteur WHERE livres.id_livre = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idLivre);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        values[0] = resultSet.getString("titre");
                        values[1] = resultSet.getString("genre");
                        values[2] = resultSet.getString("ref");
                        values[3] = resultSet.getString("disponibilité");
                        values[4] = resultSet.getString("date_pub");
                        values[5] = resultSet.getString("nom");
                        values[6] = resultSet.getString("prenom");
                        values[7] = resultSet.getString("nb_copie"); // Ajoutez le nombre de copies à l'index 7
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return values;
    }

    private String[] getAuteurs() {
        ArrayList<String> auteurs = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String query = "SELECT CONCAT(nom, ' ', prenom) AS auteur FROM auteurs";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        auteurs.add(resultSet.getString("auteur"));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return auteurs.toArray(new String[0]);
    }

    private void modifierLivre(int idLivre, String nouveauTitre, String nouveauGenre, String nouvelleReference,
                               String nouvelleDisponibilite, String nouvelleDatePublication, String nouvelAuteur, String nouveauNbCopie) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            // Récupérer l'identifiant de l'auteur
            int idAuteur = getIdAuteur(nouvelAuteur);

            String query = "UPDATE livres SET titre = ?, genre = ?, ref = ?, disponibilité = ?, date_pub = ?, id_auteur = ?, nb_copie = ? WHERE id_livre = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, nouveauTitre);
                statement.setString(2, nouveauGenre);
                statement.setString(3, nouvelleReference);
                int disponibiliteValue = nouvelleDisponibilite.equals("Disponible") ? 1 : 0;
                statement.setInt(4, disponibiliteValue);
                statement.setString(5, nouvelleDatePublication);
                statement.setInt(6, idAuteur);
                statement.setString(7, nouveauNbCopie);
                statement.setInt(8, idLivre);
                statement.executeUpdate();
            }
            Object[] newData = {idLivre, nouveauTitre, nouveauGenre, nouvelleReference, nouvelleDisponibilite, nouvelleDatePublication, nouvelAuteur, nouveauNbCopie}; // Créez un tableau d'objets contenant les nouvelles données du livre modifié
            catalogueAdmin.updateRowInTable(idLivre, newData); // Appelez la méthode updateRowInTable pour mettre à jour la ligne correspondante dans la table

            // Afficher un message de réussite
            JOptionPane.showMessageDialog(this, "Le livre a été modifié avec succès.");
            catalogueAdmin.updateTable(); // Appel à la méthode updateTable pour rafraîchir la table

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private int getIdAuteur(String nomPrenomAuteur) {
        String[] parts = nomPrenomAuteur.split(" ");
        String nom = parts[0];
        String prenom = parts[1];
        int idAuteur = -1;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String query = "SELECT id_auteur FROM auteurs WHERE nom = ? AND prenom = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, nom);
                statement.setString(2, prenom);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        idAuteur = resultSet.getInt("id_auteur");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return idAuteur;
    }
}
