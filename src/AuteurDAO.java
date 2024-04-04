import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AuteurDAO {
    public static ArrayList<String> getAllAuteurs() {
        ArrayList<String> auteurs = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String query = "SELECT nom, prenom FROM auteurs";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    auteurs.add(resultSet.getString("nom") + " " + resultSet.getString("prenom"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return auteurs;
    }
}
