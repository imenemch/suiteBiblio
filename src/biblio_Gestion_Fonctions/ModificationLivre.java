package biblio_Gestion_Fonctions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;
import java.io.FileInputStream;
import java.io.IOException;

public class ModificationLivre extends JFrame {
    private JTextField titreField;
    private JTextField genreField;
    private JTextField referenceField;
    private JComboBox<String> disponibiliteComboBox;
    private JTextField datePublicationField;
    private JTextField auteurField;
    private JLabel coverLabel; // JLabel pour afficher la couverture
    private File selectedCover; // Fichier sélectionné pour la nouvelle couverture

    public ModificationLivre(int idLivre) {
        setTitle("Modifier Livre");
        setSize(500, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Récupérer les valeurs actuelles du livre par défaut
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
        auteurField = new JTextField(currentValues[5] + " " + currentValues[6], 20);
        centerPanel.add(auteurLabel);
        centerPanel.add(auteurField);

        // Bouton pour sélectionner une nouvelle couverture
        JButton selectCoverButton = new JButton("Sélectionner une autre couverture");
        selectCoverButton.addActionListener(e -> selectCover());
        centerPanel.add(selectCoverButton);

        // JLabel pour afficher la couverture sélectionnée
        coverLabel = new JLabel();
        centerPanel.add(coverLabel);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton modifyButton = new JButton("Modifier");
        modifyButton.addActionListener(e -> {
            modifierLivre(idLivre, titreField.getText(), genreField.getText(), referenceField.getText(),
                    (String) disponibiliteComboBox.getSelectedItem(), datePublicationField.getText(),
                    auteurField.getText());
            dispose();
        });
        bottomPanel.add(modifyButton);

        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dispose());
        bottomPanel.add(cancelButton);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private String[] getCurrentValues(int idLivre) {
        String[] values = new String[7];
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String query = "SELECT livres.titre, livres.genre, livres.ref, livres.disponibilité, livres.date_pub, auteurs.nom, auteurs.prenom FROM livres INNER JOIN auteurs ON livres.id_auteur = auteurs.id_auteur WHERE id_livre = ?";
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
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return values;
    }

    private void selectCover() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedCover = fileChooser.getSelectedFile();
            // Affichage de la couverture sélectionnée
            ImageIcon coverIcon = new ImageIcon(selectedCover.getAbsolutePath());
            coverLabel.setIcon(coverIcon);
        }
    }

    private void modifierLivre(int idLivre, String nouveauTitre, String nouveauGenre, String nouvelleReference,
                               String nouvelleDisponibilite, String nouvelleDatePublication, String nouvelAuteur) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String query = "UPDATE livres SET titre = ?, genre = ?, ref = ?, disponibilité = ?, date_pub = ?, id_auteur = ?, couverture = ? WHERE id_livre = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, nouveauTitre);
                statement.setString(2, nouveauGenre);
                statement.setString(3, nouvelleReference);
                int disponibiliteValue = nouvelleDisponibilite.equals("Disponible") ? 1 : 0;
                statement.setInt(4, disponibiliteValue);
                statement.setString(5, nouvelleDatePublication);
                statement.setString(6, nouvelAuteur);
                FileInputStream coverInputStream = new FileInputStream(selectedCover);
                statement.setBinaryStream(7, coverInputStream);
                statement.setInt(8, idLivre);
                statement.executeUpdate();
            }
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ModificationLivre(1));
    }
}

