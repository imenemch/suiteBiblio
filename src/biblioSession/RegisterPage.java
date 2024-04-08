package biblioSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterPage extends JFrame implements ActionListener {
    private JTextField textNom, textPrenom, textEmail;
    private JPasswordField textPassword;
    private JButton buttonRegister;

    public RegisterPage() {
        setTitle("Inscription");
        setSize(600, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer la fenêtre

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel supérieur pour le logo et le sous-titre
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Logo
        ImageIcon logoIcon = new ImageIcon("src/biblioSession/logo.png");
        Image img = logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(img);
        JLabel labelLogo = new JLabel(logoIcon);
        labelLogo.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(labelLogo, BorderLayout.CENTER);

        // Sous-titre "Inscrivez-vous ici"
        JLabel labelSubtitle = new JLabel("Inscrivez-vous ici");
        labelSubtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        labelSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(labelSubtitle, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        formPanel.add(new JLabel("Nom :"));
        textNom = new JTextField();
        formPanel.add(textNom);

        formPanel.add(new JLabel("Prénom :"));
        textPrenom = new JTextField();
        formPanel.add(textPrenom);

        formPanel.add(new JLabel("Email :"));
        textEmail = new JTextField();
        formPanel.add(textEmail);

        formPanel.add(new JLabel("Mot de passe :"));
        textPassword = new JPasswordField();
        formPanel.add(textPassword);

        buttonRegister = new JButton("S'inscrire");
        buttonRegister.addActionListener(this);
        formPanel.add(new JLabel());
        formPanel.add(buttonRegister);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JLabel labelLogin = new JLabel("Déjà inscrit ? Cliquez ici pour vous connecter");
        labelLogin.setForeground(Color.BLUE);
        labelLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelLogin.setHorizontalAlignment(SwingConstants.CENTER);
        labelLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new LoginPage();
                dispose();
            }
        });
        mainPanel.add(labelLogin, BorderLayout.SOUTH);

        add(mainPanel);
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
