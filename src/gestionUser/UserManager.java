package gestionUser;

import biblioSession.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager
{
    private Database database;

    public UserManager(Database database)
    {
        this.database = database;
    }

    public boolean ajouterUtilisateur(String nom, String prenom, String email) {
        // Requête SQL pour ajouter un nouvel utilisateur
        String sql = "INSERT INTO users (nom, prenom, email) VALUES (?, ?, ?)";

        try (
                // Obtention d'une connexion à la base de données depuis l'objet Database
                Connection conn = database.getConnection();
                // Création d'un objet PreparedStatement pour exécuter la requête SQL
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            // Paramétrage des valeurs pour la requête SQL
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, email);

            // Exécution de la requête SQL
            int rowsAffected = stmt.executeUpdate();

            // Vérification si l'ajout a été réussi
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // En cas d'erreur, retourner false
        }
    }

    public boolean emailExists(String email) {
        // Requête SQL pour vérifier si l'email existe déjà dans la base de données
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (
                // Obtention d'une connexion à la base de données depuis l'objet Database
                Connection conn = database.getConnection();
                // Création d'un objet PreparedStatement pour exécuter la requête SQL
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            // Paramétrage de la valeur pour la requête SQL
            stmt.setString(1, email);

            // Exécution de la requête SQL et récupération du résultat dans un objet ResultSet
            try (ResultSet rs = stmt.executeQuery()) {
                // Récupérer le nombre de lignes retournées
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0; // Si le nombre de lignes retournées est supérieur à 0, l'email existe déjà
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // En cas d'erreur ou si aucun résultat n'a été retourné, retourner false
        return false;
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
                Timestamp dateCreated = rs.getTimestamp("date_created");

                // Créer un objet User avec les données récupérées
                User user = new User(id, nom, prenom, email, password, role, dateCreated);

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
        String sql = "UPDATE users SET nom = ?, prenom = ?, email = ?, role = ? WHERE id_u = ?";

        try (
                Connection conn = database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());
            stmt.setInt(5, user.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

    public List<User> searchUsers(String searchTerm) {
        List<User> userList = new ArrayList<>();

        // Requête SQL pour rechercher des utilisateurs par nom, prénom ou email
        String sql = "SELECT * FROM users WHERE nom LIKE ? OR prenom LIKE ? OR email LIKE ?";

        try (
                // Obtention d'une connexion à la base de données depuis l'objet Database
                Connection conn = database.getConnection();
                // Création d'un objet PreparedStatement pour exécuter la requête SQL
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            // Paramétrage des valeurs pour la requête SQL en ajoutant des jokers (%) autour du terme de recherche
            String searchTermWithWildcards = "%" + searchTerm + "%";
            stmt.setString(1, searchTermWithWildcards);
            stmt.setString(2, searchTermWithWildcards);
            stmt.setString(3, searchTermWithWildcards);

            // Exécution de la requête SQL et récupération des résultats dans un objet ResultSet
            try (ResultSet rs = stmt.executeQuery()) {
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
                    User user = new User(id, nom, prenom, email, password, role, dateCreated);

                    // Ajouter l'utilisateur à la liste des utilisateurs
                    userList.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Retourner la liste des utilisateurs trouvés
        return userList;
    }

    public User getUserById(int userId) {
        User user = null;

        // Requête SQL pour récupérer un utilisateur par son ID
        String sql = "SELECT * FROM users WHERE id_u = ?";

        try (
                // Obtention d'une connexion à la base de données depuis l'objet Database
                Connection conn = database.getConnection();
                // Création d'un objet PreparedStatement pour exécuter la requête SQL
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            // Paramétrage de la valeur pour la requête SQL
            stmt.setInt(1, userId);

            // Exécution de la requête SQL et récupération du résultat dans un objet ResultSet
            try (ResultSet rs = stmt.executeQuery()) {
                // Vérifier si un utilisateur correspondant à l'ID a été trouvé
                if (rs.next()) {
                    // Récupérer les valeurs des colonnes pour l'utilisateur trouvé
                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    String role = rs.getString("role");
                    Timestamp dateCreated = rs.getTimestamp("date_created");

                    // Créer un objet User avec les données récupérées
                    user = new User(userId, nom, prenom, email, password, role, dateCreated);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Retourner l'utilisateur trouvé (ou null s'il n'existe pas)
        return user;
    }



}

