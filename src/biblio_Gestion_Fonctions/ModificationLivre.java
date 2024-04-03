package biblio_Gestion_Fonctions;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.awt.GridLayout;

public class ModificationLivre extends JFrame {
    private JTextField titreField;
    private JTextField genreField;
    // Ajoutez les autres champs nécessaires pour la modification

    public ModificationLivre(int idLivre) {
        setTitle("Modifier Livre");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

        JLabel titleLabel = new JLabel("Titre:");
        titreField = new JTextField(20);
        add(titleLabel);
        add(titreField);

        JLabel genreLabel = new JLabel("Genre:");
        genreField = new JTextField(20);
        add(genreLabel);
        add(genreField);

        // Ajoutez les autres champs nécessaires pour la modification

        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> {
            // Appeler une méthode de la classe de modification pour effectuer la modification
            modifierLivre(idLivre, titreField.getText(), genreField.getText());
            dispose();
        });
        add(saveButton);

        setVisible(true);
    }

    private void modifierLivre(int idLivre, String nouveauTitre, String nouveauGenre) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String query = "UPDATE livres SET titre = ?, genre = ? WHERE id_livre = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, nouveauTitre);
                statement.setString(2, nouveauGenre);
                statement.setInt(3, idLivre);
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
