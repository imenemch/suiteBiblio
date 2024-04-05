package gestionUser;

import biblioSession.Database;
import biblio_Gestion_Admin.ListeUsers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AjoutUtilisateurFrame extends JFrame {
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField emailField;

    public AjoutUtilisateurFrame() {
        setTitle("Ajouter un Utilisateur");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Ferme seulement la fenêtre actuelle
        setLayout(new GridLayout(5, 2));

        // Création des champs de texte pour saisir les informations de l'utilisateur
        JLabel nomLabel = new JLabel("Nom :");
        nomField = new JTextField();
        JLabel prenomLabel = new JLabel("Prénom :");
        prenomField = new JTextField();
        JLabel emailLabel = new JLabel("Email :");
        emailField = new JTextField();

        // Création du bouton d'ajout
        JButton ajouterButton = new JButton("Ajouter");
        ajouterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Récupérer les valeurs saisies
                String nom = nomField.getText().trim();
                String prenom = prenomField.getText().trim();
                String email = emailField.getText().trim();

                // Vérifier si les champs sont vides
                if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(AjoutUtilisateurFrame.this,
                            "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Ajouter l'utilisateur
                UserManager userManager = new UserManager(new Database());
                boolean ajoutReussi = userManager.ajouterUtilisateur(nom, prenom, email);
                if (ajoutReussi) {
                    JOptionPane.showMessageDialog(AjoutUtilisateurFrame.this,
                            "Utilisateur ajouté avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    // Effacer les champs après l'ajout
                    nomField.setText("");
                    prenomField.setText("");
                    emailField.setText("");
                    // Recharger la liste des utilisateurs dans ListeUsers
                    ListeUsers.chargerTousLesUtilisateurs();
                } else {
                    JOptionPane.showMessageDialog(AjoutUtilisateurFrame.this,
                            "Erreur lors de l'ajout de l'utilisateur.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Création du bouton annuler
        JButton annulerButton = new JButton("Annuler");
        annulerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Fermer la fenêtre d'ajout d'utilisateur
                dispose();
            }
        });

        // Création d'un panel pour regrouper les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(ajouterButton);
        buttonPanel.add(annulerButton);

        // Ajout des composants à la fenêtre
        add(nomLabel);
        add(nomField);
        add(prenomLabel);
        add(prenomField);
        add(emailLabel);
        add(emailField);
        add(new JLabel()); // Pour aligner correctement les composants
        add(buttonPanel); // Ajout du panel de boutons

        setVisible(true);
    }

    public static void main(String[] args) {
        // Utilisation de l'invocation de l'interface graphique Swing dans un thread dédié (EDT)
        SwingUtilities.invokeLater(AjoutUtilisateurFrame::new);
    }
}
