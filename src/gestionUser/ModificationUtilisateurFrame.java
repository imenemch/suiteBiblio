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
    private JComboBox<String> roleComboBox; // Liste déroulante pour le rôle
    private User user;

    public ModificationUtilisateurFrame(User user) {
        this.user = user;

        setTitle("Modifier Utilisateur");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 2));

        JLabel nomLabel = new JLabel("Nom :");
        nomField = new JTextField(user.getNom());
        JLabel prenomLabel = new JLabel("Prénom :");
        prenomField = new JTextField(user.getPrenom());
        JLabel emailLabel = new JLabel("Email :");
        emailField = new JTextField(user.getEmail());
        JLabel roleLabel = new JLabel("Rôle :");

        // Liste déroulante pour sélectionner le rôle
        roleComboBox = new JComboBox<>();
        roleComboBox.addItem("lecteur");
        roleComboBox.addItem("admin");
        roleComboBox.setSelectedItem(user.getRole());

        JButton modifierButton = new JButton("Modifier");
        JButton annulerButton = new JButton("Annuler");

        modifierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifierUtilisateur();
            }
        });

        annulerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(modifierButton);
        buttonPanel.add(annulerButton);

        add(nomLabel);
        add(nomField);
        add(prenomLabel);
        add(prenomField);
        add(emailLabel);
        add(emailField);
        add(roleLabel);
        add(roleComboBox); // Ajout de la liste déroulante pour le rôle
        add(new JLabel());
        add(buttonPanel);

        setVisible(true);
    }

    private void modifierUtilisateur() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem(); // Récupération du rôle sélectionné

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setRole(role); // Mise à jour du rôle

        UserManager userManager = new UserManager(new Database());
        boolean updateSuccess = userManager.updateUser(user);
        if (updateSuccess) {
            JOptionPane.showMessageDialog(this,
                    "Informations utilisateur mises à jour avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            ListeUsers.chargerTousLesUtilisateurs();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la mise à jour des informations de l'utilisateur.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
