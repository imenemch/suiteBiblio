package biblioSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import biblio_Gestion_Lecteur.CatalogueLecteur;
import biblioSession.UserBdd;
import biblioSession.PswdHash;

public class LoginPage extends JFrame implements ActionListener {
    private JLabel labelEmail, labelPassword, labelWelcome, labelLogo;
    private JTextField textEmail;
    private JPasswordField textPassword;
    private JButton buttonLogin, buttonRegister;

    public LoginPage() {
        setTitle("Connexion");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer la fenêtre

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel supérieur pour le logo et le sous-titre
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

// Sous-titre "Connectez-vous ici"
        JLabel labelSubtitle = new JLabel("Connectez-vous ici");
        labelSubtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        labelSubtitle.setHorizontalAlignment(SwingConstants.CENTER); // Alignement centré
        topPanel.add(labelSubtitle, BorderLayout.SOUTH); // Positionnement en bas

// Logo
        ImageIcon logoIcon = new ImageIcon("src/biblioSession/logo.png");
        Image img = logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH); // Réduire la taille du logo à 150x150 pixels
        logoIcon = new ImageIcon(img);
        JLabel labelLogo = new JLabel(logoIcon);
        labelLogo.setHorizontalAlignment(SwingConstants.CENTER); // Alignement centré
        topPanel.add(labelLogo, BorderLayout.CENTER); // Positionnement centré

        mainPanel.add(topPanel, BorderLayout.NORTH);



        // Panel pour les champs de saisie et les boutons
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        labelEmail = new JLabel("Email:");
        labelEmail.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(labelEmail);

        textEmail = new JTextField();
        textEmail.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(textEmail);

        labelPassword = new JLabel("Mot de passe:");
        labelPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(labelPassword);

        textPassword = new JPasswordField();
        textPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(textPassword);

        buttonLogin = new JButton("Se connecter");
        buttonLogin.addActionListener(this);
        buttonLogin.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(buttonLogin);

        buttonRegister = new JButton("Pas encore inscrit?");
        buttonRegister.addActionListener(this);
        buttonRegister.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(buttonRegister);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonLogin) {
            String email = textEmail.getText();
            String password = new String(textPassword.getPassword());

            UserBdd userDAO = new UserBdd();
            String hashedPassword = PswdHash.hashPassword(password); // Hasher le mot de passe saisi

            int userId = userDAO.getUserId(email, hashedPassword); // Récupérer l'ID de l'utilisateur

            if (userId != -1) { // Vérifier si l'identifiant de l'utilisateur est valide
                String userRole = userDAO.getUserRole(email); // Obtenir le rôle de l'utilisateur

                if (userRole.equals("admin")) {
                    // Rediriger vers la page du catalogue de l'administrateur
                    new biblio_Gestion_Admin.CatalogueAdmin();
                } else if (userRole.equals("lecteur")) {
                    // Rediriger vers la page du catalogue du lecteur
                    CatalogueLecteur catalogueLecteur = new CatalogueLecteur();
                    catalogueLecteur.startSession(userId); // Démarrer la session avec l'idUtilisateur
                }

                dispose(); // Fermer la fenêtre de connexion
            } else {
                JOptionPane.showMessageDialog(this, "Identifiants incorrects", "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } else if (e.getSource() == buttonRegister) {
            openRegistrationPage();
        }
    }

    private void openRegistrationPage() {
        dispose();
        new RegisterPage();
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}