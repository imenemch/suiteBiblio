package biblioSession;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import biblio_Gestion_Lecteur.CatalogueLecteur;
import biblioSession.UserBdd;
import biblioSession.PswdHash;

public class LoginPage extends JFrame implements ActionListener {
    private JLabel labelEmail, labelPassword;
    private JTextField textEmail;
    private JPasswordField textPassword;
    private JButton buttonLogin, buttonRegister;

    public LoginPage() {
        setTitle("Connexion");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        labelEmail = new JLabel("Email:");
        labelEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(labelEmail);
        textEmail = new JTextField();
        textEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(textEmail);

        labelPassword = new JLabel("Mot de passe:");
        labelPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(labelPassword);
        textPassword = new JPasswordField();
        textPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(textPassword);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        buttonLogin = new JButton("Se connecter");
        buttonLogin.addActionListener(this);
        buttonLogin.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(buttonLogin);

        buttonRegister = new JButton("Pas encore inscrit?");
        buttonRegister.addActionListener(this);
        buttonRegister.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(buttonRegister);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

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
                CatalogueLecteur catalogueLecteur = new CatalogueLecteur();
                catalogueLecteur.startSession(userId); // Démarrer la session avec l'idUtilisateur
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