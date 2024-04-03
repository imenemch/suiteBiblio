package biblioSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class connexionTest {

	public static void main(String[] args) {
        // Informations de connexion à la base de données
        String url = "jdbc:mysql://localhost/bibliotech";
        String login = "root";
        String passwd = "";

        // Objet de connexion à la base de données
        Connection connection = null;

        try {
            // Chargement du driver JDBC pour MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connexion à la base de données
            connection = DriverManager.getConnection(url, login, passwd);
            System.out.println("Connexion réussie à la base de données.");

            // Insertion de données dans la table Livres
            String sql = "INSERT INTO Livres (Titre, Auteur, Genre, Annee_Publication, Disponibilite) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "L'art de perdre");
            statement.setString(2, "Alice ZENITER");
            statement.setString(3, "Fiction psychologique");
            statement.setInt(4, 2022);
            statement.setBoolean(5, true);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Livre ajouté avec succès !");
            }

        } catch (ClassNotFoundException e) {
            System.out.println("Erreur de chargement du driver JDBC : " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données : " + e.getMessage());
        } finally {
            // Fermeture de la connexion à la base de données
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("Connexion à la base de données fermée.");
                } catch (SQLException e) {
                    System.out.println("Erreur lors de la fermeture de la connexion à la base de données : " + e.getMessage());
                }
            }
        }
    }
}