package biblioSession;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;

public class RegisterPage extends JFrame implements ActionListener {
    private JLabel labelNom, labelPrenom, labelEmail, labelPassword, labelLogin;
    private JTextField textNom, textPrenom, textEmail;
    private JPasswordField textPassword;
    private JButton buttonRegister; // Bouton pour s'inscrire

    public RegisterPage() {
        setTitle("Inscription");
        setSize(400, 350); // Ajustement de la hauteur pour accommoder le bouton
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Utilisation d'un JPanel pour une meilleure organisation de la mise en page
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10)); // GridLayout avec espacement horizontal et vertical
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Ajout de marges

        labelNom = new JLabel("Nom :");
        panel.add(labelNom);
        textNom = new JTextField();
        panel.add(textNom);

        labelPrenom = new JLabel("Prénom :");
        panel.add(labelPrenom);
        textPrenom = new JTextField();
        panel.add(textPrenom);

        labelEmail = new JLabel("Email :");
        panel.add(labelEmail);
        textEmail = new JTextField();
        panel.add(textEmail);

        labelPassword = new JLabel("Mot de passe :");
        panel.add(labelPassword);
        textPassword = new JPasswordField();
        panel.add(textPassword);

        // Bouton S'inscrire
        buttonRegister = new JButton("S'inscrire");
        buttonRegister.addActionListener(this);
        panel.add(new JLabel()); // Ajout d'une cellule vide pour aligner le bouton sur la même ligne
        panel.add(buttonRegister);

        // Ajout du panel à la fenêtre principale
        add(panel, BorderLayout.CENTER);

        // Ajout du lien de connexion en bas de la fenêtre
        labelLogin = new JLabel("Déjà inscrit ? Cliquez ici pour vous connecter");
        labelLogin.setForeground(Color.BLUE); // Changement de couleur pour indiquer qu'il s'agit d'un lien
        labelLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Changement du curseur au survol
        labelLogin.setHorizontalAlignment(SwingConstants.CENTER);
        labelLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Redirection vers la page de connexion (LoginPage) lors du clic sur le lien
                new LoginPage();
                dispose(); // Fermer la fenêtre d'inscription
            }
        });
        add(labelLogin, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonRegister) {
            // Récupérer les valeurs des champs de saisie
            String nom = textNom.getText();
            String prenom = textPrenom.getText();
            String email = textEmail.getText();
            String password = new String(textPassword.getPassword());

            // Enregistrer l'utilisateur dans la base de données avec le rôle "lecteur"
            registerUser(nom, prenom, email, password);
        }
    }

    private void registerUser(String nom, String prenom, String email, String password) {
        // Hasher le mdp
        String hashedPassword = PswdHash.hashPassword(password);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/bibliotech", "root", "");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (nom, prenom, email, password, role) VALUES (?, ?, ?, ?, 'lecteur')")) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, email);
            stmt.setString(4, hashedPassword);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // Boîte de dialogue de confirmation améliorée
                JOptionPane.showMessageDialog(this, "Inscription réussie!\nVous pouvez maintenant vous connecter.", "Bienvenue sur BiblioTech!", JOptionPane.INFORMATION_MESSAGE);
                // Redirection vers la page de connexion (LoginPage)
                new LoginPage();
                dispose(); // Fermer la fenêtre d'inscription
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'inscription", "Désolé", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'inscription", "Désolé", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Utilisation de l'invocation de l'interface graphique Swing dans un thread dédié (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new RegisterPage();
            }
        });
    }
}
