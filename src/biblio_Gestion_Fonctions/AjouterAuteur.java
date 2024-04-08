package biblio_Gestion_Fonctions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AjouterAuteur extends JFrame implements ActionListener {
    private JTextField txtNom, txtPrenom;
    private JButton btnAjouter;
    private Connection connexion;

    public AjouterAuteur(Connection connexion) {
        this.connexion = connexion;

        setTitle("Ajouter un auteur");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer la fenêtre sur l'écran

        // Création d'un titre en haut de la fenêtre
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Ajouter un auteur");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);

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

        add(titlePanel, BorderLayout.NORTH); // Ajout du titre en haut de la fenêtre
        add(panel);

        int xOffset =380;
        int yOffset = 70;
        setLocationRelativeTo(null); // Centrer la fenêtre sur l'écran
        setLocation(getX() + xOffset, getY() + yOffset); // Décaler la fenêtre
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
