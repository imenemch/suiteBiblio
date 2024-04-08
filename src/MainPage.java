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

        // Sélection en vedette
        JPanel featuredPanel = new JPanel(new BorderLayout());
        JLabel featuredLabel = new JLabel("Sélection en vedette");
        featuredLabel.setFont(new Font("Arial", Font.BOLD, 20));
        // Ajoutez ici votre sélection en vedette de livres sous forme de composants Swing (par exemple des JLabels ou des JButtons)

        // Témoignages ou critiques
        JPanel testimonialsPanel = new JPanel(new BorderLayout());
        JLabel testimonialsLabel = new JLabel("Témoignages");
        testimonialsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        // Ajoutez ici des témoignages ou des critiques sous forme de texte ou d'autres composants Swing

        // Call-to-action
        JPanel ctaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton signUpButton = new JButton("Inscrivez-vous maintenant");
        ctaPanel.add(signUpButton);

        // Assemblage des composants dans le conteneur principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(navPanel);
        mainPanel.add(Box.createVerticalStrut(20)); // Espace vertical
        mainPanel.add(featuredPanel);
        mainPanel.add(Box.createVerticalStrut(20)); // Espace vertical
        mainPanel.add(testimonialsPanel);
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
