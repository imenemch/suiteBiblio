import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import biblio_Gestion_Lecteur.CatalogueLecteur;

public class MainPage extends JFrame {
    public MainPage() {
        setTitle("Bibliothèque en ligne");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Centrer la fenêtre sur l'écran

        // Barre de navigation
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton browseBooksButton = new JButton("Parcourir les livres");
        navPanel.add(browseBooksButton);

        // Vidéo
        JPanel videoPanel = new JPanel(new BorderLayout());
        JLabel videoLabel = new JLabel(new ImageIcon("src/video.mp4")); // Remplacez "path_to_your_video_file" par le chemin de votre fichier vidéo
        videoPanel.add(videoLabel, BorderLayout.CENTER);

        // Call-to-action
        JPanel ctaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton signUpButton = new JButton("Inscrivez-vous maintenant");
        ctaPanel.add(signUpButton);

        // Assemblage des composants dans le conteneur principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(navPanel);
        mainPanel.add(Box.createVerticalStrut(20)); // Espace vertical
        mainPanel.add(videoPanel); // Ajout de la vidéo
        mainPanel.add(Box.createVerticalStrut(20)); // Espace vertical
        mainPanel.add(ctaPanel);

        // Ajout du conteneur principal à la fenêtre
        add(mainPanel);

        // Gestionnaire d'événements pour le bouton "Parcourir les livres"
        browseBooksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Redirection vers la page CatalogueLecteur du package biblio_Gestion_Lecteur
                CatalogueLecteur catalogue = new CatalogueLecteur(); // Création de l'instance de CatalogueLecteur
                catalogue.setVisible(true); // Rendre la fenêtre visible
                dispose(); // Fermer la fenêtre principale
            }
        });
        // Gestionnaire d'événements pour le bouton "Inscrivez-vous maintenant"
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Redirection vers la page d'inscription (RegisterPage)
                new biblioSession.RegisterPage();
                dispose(); // Fermer la fenêtre principale
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainPage mainPage = new MainPage();
                mainPage.setVisible(true);
            }
        });
    }
}
