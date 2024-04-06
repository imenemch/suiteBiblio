package biblio_Gestion_Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CatalogueAdmin extends JFrame {
    private JTable table;
    private JTextField searchField;

    public CatalogueAdmin() {
        setTitle("Catalogue des Livres");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Titre");
        model.addColumn("Genre");
        model.addColumn("Référence");
        model.addColumn("Disponibilité");
        model.addColumn("Date de publication");
        model.addColumn("Nombre de copies");
        model.addColumn("Auteur");
        model.addColumn("Couverture"); // Ajout de la colonne pour la couverture

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String query = "SELECT livres.id_livre, livres.titre, livres.genre, livres.ref, livres.disponibilité, livres.date_pub, livres.nb_copie, livres.couverture, auteurs.nom, auteurs.prenom " +
                    "FROM livres " +
                    "JOIN auteurs ON livres.id_auteur = auteurs.id_auteur";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    byte[] coverData = resultSet.getBytes("couverture");
                    ImageIcon coverIcon = createImageIcon(coverData);
                    model.addRow(new Object[]{
                            resultSet.getInt("id_livre"),
                            resultSet.getString("titre"),
                            resultSet.getString("genre"),
                            resultSet.getString("ref"),
                            resultSet.getBoolean("disponibilité") ? "Disponible" : "Non disponible",
                            resultSet.getTimestamp("date_pub"),
                            resultSet.getInt("nb_copie"),
                            resultSet.getString("nom") + " " + resultSet.getString("prenom"),
                            coverIcon
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        table = new JTable(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(10);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(40);
        table.getColumnModel().getColumn(3).setPreferredWidth(20);
        table.getColumnModel().getColumn(4).setPreferredWidth(60);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        table.getColumnModel().getColumn(6).setPreferredWidth(60);
        table.getColumnModel().getColumn(7).setPreferredWidth(200);
        table.setRowHeight(100); // Réglage de la hauteur des lignes pour la couverture

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        searchField = new JTextField(20);
        ImageIcon searchIcon = new ImageIcon(getClass().getResource("search.png"));
        ImageIcon editIcon = new ImageIcon(getClass().getResource("edit.png"));
        ImageIcon deleteIcon = new ImageIcon(getClass().getResource("delete.png"));
        ImageIcon addIcon = new ImageIcon(getClass().getResource("add.png"));
        ImageIcon backIcon = new ImageIcon(getClass().getResource("back.png"));

        searchIcon = new ImageIcon(searchIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        editIcon = new ImageIcon(editIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        deleteIcon = new ImageIcon(deleteIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        addIcon = new ImageIcon(addIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        backIcon = new ImageIcon(backIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));

        JButton searchButton = new JButton(searchIcon);
        JButton buttonEdit = new JButton(editIcon);
        JButton buttonSupp = new JButton(deleteIcon);
        JButton buttonAdd = new JButton(addIcon);
        JButton buttonRetour = new JButton(backIcon);

        Insets buttonInsets = new Insets(5, 10, 5, 10);
        searchButton.setMargin(buttonInsets);
        buttonEdit.setMargin(buttonInsets);
        buttonSupp.setMargin(buttonInsets);
        buttonAdd.setMargin(buttonInsets);
        buttonRetour.setMargin(buttonInsets);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(buttonEdit);
        buttonPanel.add(buttonSupp);
        buttonPanel.add(buttonAdd);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.add(new JLabel("Rechercher par titre : "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(buttonRetour);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.NORTH);

        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                rechercherLivreParTitre(searchTerm);
            } else {
                chargerTousLesLivres();
            }
        });

        buttonEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int idLivre = (int) table.getValueAt(selectedRow, 0);
                new biblio_Gestion_Fonctions.ModificationLivre(idLivre);
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un livre à modifier.");
            }
        });

        buttonSupp.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int idLivre = (int) table.getValueAt(selectedRow, 0);
                supprimerLivre(idLivre);
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un livre à supprimer.");
            }
        });

        buttonAdd.addActionListener(e -> {
            new AjoutLivreForm();
        });

        // Appliquez le rendu personnalisé pour afficher les images dans la table
        table.getColumnModel().getColumn(8).setCellRenderer(new ImageRenderer());

        setVisible(true);
    }

    private void rechercherLivreParTitre(String titre) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String query = "SELECT livres.id_livre, livres.titre, livres.genre, livres.ref, livres.disponibilité, livres.date_pub, livres.nb_copie, livres.couverture, auteurs.nom, auteurs.prenom " +
                    "FROM livres " +
                    "JOIN auteurs ON livres.id_auteur = auteurs.id_auteur " +
                    "WHERE livres.titre LIKE ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, "%" + titre + "%");
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    byte[] coverData = resultSet.getBytes("couverture");
                    ImageIcon coverIcon = createImageIcon(coverData);
                    model.addRow(new Object[]{
                            resultSet.getInt("id_livre"),
                            resultSet.getString("titre"),
                            resultSet.getString("genre"),
                            resultSet.getString("ref"),
                            resultSet.getBoolean("disponibilité") ? "Disponible" : "Non disponible",
                            resultSet.getTimestamp("date_pub"),
                            resultSet.getInt("nb_copie"),
                            resultSet.getString("nom") + " " + resultSet.getString("prenom"),
                            coverIcon
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void chargerTousLesLivres() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotech", "root", "")) {
            String query = "SELECT livres.id_livre, livres.titre, livres.genre, livres.ref, livres.disponibilité, livres.date_pub, livres.nb_copie, livres.couverture, auteurs.nom, auteurs.prenom " +
                    "FROM livres " +
                    "JOIN auteurs ON livres.id_auteur = auteurs.id_auteur";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    byte[] coverData = resultSet.getBytes("couverture");
                    ImageIcon coverIcon = createImageIcon(coverData);
                    model.addRow(new Object[]{
                            resultSet.getInt("id_livre"),
                            resultSet.getString("titre"),
                            resultSet.getString("genre"),
                            resultSet.getString("ref"),
                            resultSet.getBoolean("disponibilité") ? "Disponible" : "Non disponible",
                            resultSet.getTimestamp("date_pub"),
                            resultSet.getInt("nb_copie"),
                            resultSet.getString("nom") + " " + resultSet.getString("prenom"),
                            coverIcon
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void supprimerLivre(int idLivre) {
        biblio_Gestion_Fonctions.SuppressionLivre.supprimer(idLivre);
        chargerTousLesLivres();
    }

    // Méthode pour créer une ImageIcon à partir de données binaires
    private ImageIcon createImageIcon(byte[] imageData) {
        if (imageData != null) {
            return new ImageIcon(new ImageIcon(imageData).getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
        } else {
            return null;
        }
    }

    // Rendu personnalisé pour afficher les images dans la table
    class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setIcon((ImageIcon) value);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CatalogueAdmin::new);
    }
}
