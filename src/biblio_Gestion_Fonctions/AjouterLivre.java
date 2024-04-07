package biblio_Gestion_Fonctions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JComboBox;

public class AjouterLivre {
    private Connection connexion;

    // Constructeur prenant en paramètre la connexion à la base de données
    public AjouterLivre(Connection connexion) {
        this.connexion = connexion;
    }

    // Méthode pour obtenir la connexion à la base de données
    private Connection obtenirConnexion() {
        try {
            String url = "jdbc:mysql://localhost/bibliotech";
            String utilisateur = "root";
            String motDePasse = "";
            return DriverManager.getConnection(url, utilisateur, motDePasse);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
            return null;
        }
    }

    // Méthode pour ajouter un livre à la base de données
    public boolean ajouterLivre(String titre, String genre, String ref, String disponibilite, Date datePub, int nbCopie, int idAuteur) {
        // Obtention de la connexion à la base de données
        connexion = obtenirConnexion();
        if (connexion == null) {
            return false;
        }

        try {
            // Préparation de la requête SQL
            String sql = "INSERT INTO livres (titre, genre, ref, disponibilite, date_pub, nb_copie, id_auteur) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connexion.prepareStatement(sql);

            // Attribution des valeurs aux paramètres de la requête
            statement.setString(1, titre);
            statement.setString(2, genre);
            statement.setString(3, ref);
            statement.setString(4, disponibilite); // Utilisation de la disponibilité en tant que chaîne de caractères
            statement.setDate(5, new java.sql.Date(datePub.getTime())); // Conversion de java.util.Date à java.sql.Date
            statement.setInt(6, nbCopie);
            statement.setInt(7, idAuteur);

            // Exécution de la requête
            int rowsInserted = statement.executeUpdate();

            // Vérification si l'insertion a réussi
            if (rowsInserted > 0) {
                System.out.println("Livre ajouté avec succès !");
                return true;
            } else {
                System.out.println("Échec de l'ajout du livre.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du livre : " + e.getMessage());
            return false;
        } finally {
            // Fermeture de la connexion
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException e) {
                    System.out.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        // Exemple d'utilisation
        Connection connexion = null;
        try {
            // Connexion à la base de données
            connexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "utilisateur", "mot_de_passe");

            // Création de l'objet AjouterLivre avec la connexion
            AjouterLivre gestionLivres = new AjouterLivre(connexion);

            // Exemple d'ajout d'un livre
            gestionLivres.ajouterLivre("Titre du livre", "Genre du livre", "REF001", "Disponible", new Date(), 5, 1);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
        } finally {
            // Fermeture de la connexion
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException e) {
                    System.out.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
                }
            }
        }
    }
}
