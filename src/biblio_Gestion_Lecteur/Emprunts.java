package biblio_Gestion_Lecteur;
import java.util.ArrayList;
import java.util.List;

public class Emprunts {
    private int idUtilisateur;
    private List<Integer> listeEmprunts;

    public Emprunts(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
        this.listeEmprunts = new ArrayList<>();
        // Charger la liste d'emprunts depuis la base de données
        chargerListeEmprunts();
    }

    private void chargerListeEmprunts() {
        // Code pour charger la liste d'emprunts depuis la base de données pour l'utilisateur donné
        // ...
    }

    public List<Integer> getListeEmprunts() {
        return listeEmprunts;
    }

    public void effectuerEmprunt(int idLivre) {
        // Code pour effectuer l'emprunt du livre et mettre à jour la liste d'emprunts dans la base de données
        // ...
    }
}
