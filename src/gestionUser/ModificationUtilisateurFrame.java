package gestionUser;

import biblioSession.Database;
import biblio_Gestion_Admin.ListeUsers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModificationUtilisateurFrame extends JFrame {
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField emailField;
    private User user; // Stocke les informations de l'utilisateur à modifier
    private ListeUsers listeUsers; // Référence à la fenêtre principale ListeUsers

    public ModificationUtilisateurFrame(User user) {
        this.user = user; // Stocke les informations de l'utilisateur passé en paramètre
        this.listeUsers = listeUsers; // Stocke la référence à la fenêtre ListeUsers

        setTitle("Modifier Utilisateur");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Ferme seulement la fenêtre actuelle
        setLayout(new GridLayout(5, 2));

        // Création des champs de texte pour saisir les informations de l'utilisateur
        JLabel nomLabel = new JLabel("Nom :");
        nomField = new JTextField(user.getNom());
        JLabel prenomLabel = new JLabel("Prénom :");
        prenomField = new JTextField(user.getPrenom());
        JLabel emailLabel = new JLabel("Email :");
        emailField = new JTextField(user.getEmail());

        // Création du bouton de modification
        JButton modifierButton = new JButton("Modifier");
        modifierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Récupérer les valeurs modifiées
                String nom = nomField.getText().trim();
                String prenom = prenomField.getText().trim();
                String email = emailField.getText().trim();

                // Vérifier si les champs sont vides
                if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(ModificationUtilisateurFrame.this,
                            "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Mettre à jour les informations de l'utilisateur
                user.setNom(nom);
                user.setPrenom(prenom);
                user.setEmail(email);

                // Mettre à jour l'utilisateur dans la base de données
                UserManager userManager = new UserManager(new Database());
                boolean updateSuccess = userManager.updateUser(user);
                if (updateSuccess) {
                    JOptionPane.showMessageDialog(ModificationUtilisateurFrame.this,
                            "Informations utilisateur mises à jour avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    // Recharger la liste des utilisateurs dans la fenêtre principale ListeUsers
                    listeUsers.chargerTousLesUtilisateurs();
                    dispose(); // Fermer la fenêtre de modification après la mise à jour
                } else {
                    JOptionPane.showMessageDialog(ModificationUtilisateurFrame.this,
                            "Erreur lors de la mise à jour des informations de l'utilisateur.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Ajout des composants à la fenêtre
        add(nomLabel);
        add(nomField);
        add(prenomLabel);
        add(prenomField);
        add(emailLabel);
        add(emailField);
        add(new JLabel()); // Pour aligner correctement les composants
        add(modifierButton);

        setVisible(true);
    }
}
