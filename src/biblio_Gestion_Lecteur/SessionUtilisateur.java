package biblio_Gestion_Lecteur;

public class SessionUtilisateur {
    private static SessionUtilisateur instance;
    private int id_u;

    private SessionUtilisateur() {

    }

    public static SessionUtilisateur getInstance() {
        if (instance == null) {
            instance = new SessionUtilisateur();
        }
        return instance;
    }

    public void demarrerSession(int id_u) {
        this.id_u = id_u;
    }

    public int getId_u() {
        return id_u;
    }

    // Méthode pour vérifier si l'utilisateur est connecté
    public boolean estConnecte() {
        // Si l'ID de l'utilisateur est différent de 0, alors il est connecté
        return id_u != 0;
    }
}
