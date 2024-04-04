package biblio_Gestion_Admin;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
public class AjoutLivreForm extends JFrame {
    private JTextField titreField;
    private JTextField genreField;
    private JTextField refField;
    private JTextField disponibiliteField;
    private JTextField datePubField;
    private JTextField nbCopieField;
    private JComboBox<String> auteurComboBox;

    public AjoutLivreForm() {
        setTitle("Ajouter un Livre");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(8, 2));

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
        disponibiliteField = new JTextField();
        add(disponibiliteLabel);
        add(disponibiliteField);

        JLabel datePubLabel = new JLabel("Date de publication:");
        datePubField = new JTextField();
        add(datePubLabel);
        add(datePubField);

        JLabel nbCopieLabel = new JLabel("Nombre de copies:");
        nbCopieField = new JTextField();
        add(nbCopieLabel);
        add(nbCopieField);

        JLabel auteurLabel = new JLabel("Auteur:");
        auteurComboBox = new JComboBox<>();
        add(auteurLabel);
        add(auteurComboBox);

        // Remplir la liste déroulante des auteurs
        remplirAuteurs();

        JButton addButton = new JButton("Ajouter");
        add(addButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ajouterLivre();
            }
        });

        setVisible(true);
    }

    private void remplirAuteurs() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String query = "SELECT nom, prenom FROM auteurs";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String nom = resultSet.getString("nom");
                    String prenom = resultSet.getString("prenom");
                    auteurComboBox.addItem(nom + " " + prenom);
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
        String disponibilite = disponibiliteField.getText().trim();
        // Supprimez la récupération de la date de publication de l'interface utilisateur
        // String datePub = datePubField.getText().trim();
        String nbCopie = nbCopieField.getText().trim();
        String auteur = auteurComboBox.getSelectedItem().toString().trim();

        // Ajoutez ici le code pour insérer les données dans la base de données
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String query = "INSERT INTO livres (titre, genre, ref, disponibilité, date_pub, nb_copie, id_auteur) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, titre);
                statement.setString(2, genre);
                statement.setString(3, ref);
                statement.setString(4, disponibilite);
                // Utilisez la date et l'heure actuelles pour la date de publication
                statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                statement.setString(6, nbCopie);
                // Récupérez l'ID de l'auteur sélectionné depuis la base de données
                int idAuteur = getIdAuteur(auteur);
                statement.setInt(7, idAuteur);
                // Exécutez la requête d'insertion
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Livre ajouté avec succès !");
                // Fermez la fenêtre après l'ajout
                dispose();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du livre : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Méthode pour obtenir l'ID de l'auteur à partir du nom et prénom
    private int getIdAuteur(String nomPrenom) {
        int idAuteur = -1; // Valeur par défaut si l'auteur n'est pas trouvé
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


    public static void main(String[] args) {
        SwingUtilities.invokeLater(AjoutLivreForm::new);
    }
}
