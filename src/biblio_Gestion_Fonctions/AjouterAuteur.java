import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class AjouterAuteur extends JFrame implements ActionListener {
    private JTextField txtNom, txtPrenom;
    private JButton btnAjouter;
    private Connection connexion;

    public AjouterAuteur(Connection connexion) {
        this.connexion = connexion;

        setTitle("Ajouter un auteur");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblNom = new JLabel("Nom de l'auteur:");
        txtNom = new JTextField(20);
        panel.add(lblNom);
        panel.add(txtNom);

        JLabel lblPrenom = new JLabel("Prénom de l'auteur:");
        txtPrenom = new JTextField(20);
        panel.add(lblPrenom);
        panel.add(txtPrenom);

        btnAjouter = new JButton("Ajouter");
        btnAjouter.addActionListener(this);
        panel.add(btnAjouter);

        add(panel);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAjouter) {
            String nom = txtNom.getText();
            String prenom = txtPrenom.getText();

            if (!nom.isEmpty() && !prenom.isEmpty()) {
                ajouterAuteur(nom, prenom);
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez saisir le nom et le prénom de l'auteur.");
            }
        }
    }

    public void ajouterAuteur(String nom, String prenom) {
        try {
            PreparedStatement preparedStatement = connexion.prepareStatement("INSERT INTO auteurs (nom, prenom) VALUES (?, ?)");
            preparedStatement.setString(1, nom);
            preparedStatement.setString(2, prenom);
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Auteur ajouté avec succès !");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de l'auteur : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        Connection connexion = null;
        try {
            connexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "");
            new AjouterAuteur(connexion);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la connexion à la base de données : " + e.getMessage());
        }
    }
}
