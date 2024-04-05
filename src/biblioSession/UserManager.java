package biblioSession;

import biblioSession.Database;
import biblioSession.User;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class UserManager
{
    private Database database;

    public UserManager(Database database)
    {
        this.database = database;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();

        // Requête SQL pour récupérer tous les utilisateurs
        String sql = "SELECT * FROM users";

        try (
                // Obtention d'une connexion à la base de données depuis l'objet Database
                Connection conn = database.getConnection();
                // Création d'un objet PreparedStatement pour exécuter la requête SQL
                PreparedStatement stmt = conn.prepareStatement(sql);
                // Exécution de la requête SQL et récupération des résultats dans un objet ResultSet
                ResultSet rs = stmt.executeQuery()
        ) {
            // Parcourir les résultats du ResultSet
            while (rs.next()) {
                // Récupérer les valeurs des colonnes pour chaque utilisateur
                int id = rs.getInt("id_u");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String role = rs.getString("role");
                int active = rs.getInt("active");
                Timestamp dateCreated = rs.getTimestamp("date_created");

                // Créer un objet User avec les données récupérées
                User user = new User(id, nom, prenom, email, password, role, active, dateCreated);

                // Ajouter l'utilisateur à la liste des utilisateurs
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Retourner la liste des utilisateurs
        return userList;
    }

    public boolean updateUser(User user) {
        // Requête SQL pour mettre à jour les informations de l'utilisateur
        String sql = "UPDATE users SET nom = ?, prenom = ?, email = ?, password = ?, role = ?, active = ? WHERE id_u = ?";

        try (
                // Obtention d'une connexion à la base de données depuis l'objet Database
                Connection conn = database.getConnection();
                // Création d'un objet PreparedStatement pour exécuter la requête SQL
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            // Paramétrage des valeurs pour la requête SQL
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getRole());
            stmt.setInt(6, user.getActive());
            stmt.setInt(7, user.getId());

            // Exécution de la requête SQL
            int rowsAffected = stmt.executeUpdate();

            // Vérification si la mise à jour a été réussie
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // En cas d'erreur, retourner false
        }
    }

    public boolean deleteUser(int userId) {
        // Requête SQL pour supprimer l'utilisateur
        String sql = "DELETE FROM users WHERE id_u = ?";

        try (
                // Obtention d'une connexion à la base de données depuis l'objet Database
                Connection conn = database.getConnection();
                // Création d'un objet PreparedStatement pour exécuter la requête SQL
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            // Paramétrage de la valeur pour la requête SQL
            stmt.setInt(1, userId);

            // Exécution de la requête SQL
            int rowsAffected = stmt.executeUpdate();

            // Vérification si la suppression a été réussie
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // En cas d'erreur, retourner false
        }
    }

}

