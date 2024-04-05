package biblioSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterPage extends JFrame implements ActionListener {
    private JLabel labelNom, labelPrenom, labelEmail, labelPassword, labelLogin;
    private JTextField textNom, textPrenom, textEmail;
    private JPasswordField textPassword;
    private JButton buttonRegister;

    public RegisterPage() {
        setTitle("Inscription");
        setSize(515, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10)); // GridLayout avec 6 lignes pour aligner tous les éléments correctement
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

        buttonRegister = new JButton("S'inscrire");
        buttonRegister.addActionListener(this);
        panel.add(buttonRegister);

        labelLogin = new JLabel("Déjà inscrit ? Cliquez ici pour vous connecter");
        labelLogin.setForeground(Color.BLUE);
        labelLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelLogin.setHorizontalAlignment(SwingConstants.CENTER);
        labelLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Redirection vers la page de connexion (LoginPage) lors du clic sur le lien
                new LoginPage();
                dispose(); // Fermer la fenêtre d'inscription
            }
        });
        panel.add(labelLogin);

        // Ajout du panel principal à la fenêtre
        add(panel);
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
        // Votre logique d'enregistrement d'utilisateur ici
        JOptionPane.showMessageDialog(this, "Fonctionnalité d'inscription non implémentée.", "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegisterPage::new);
    }
}
