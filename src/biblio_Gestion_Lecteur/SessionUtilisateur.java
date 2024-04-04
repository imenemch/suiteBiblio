package biblio_Gestion_Lecteur;

public class SessionUtilisateur {
    private static SessionUtilisateur instance;
    private int idUtilisateur;
    // Ajoutez d'autres informations sur l'utilisateur si nécessaire

    private SessionUtilisateur() {
        // Initialisez les valeurs par défaut ou récupérez-les à partir de la base de données si l'utilisateur est déjà connecté
    }

    public static SessionUtilisateur getInstance() {
        if (instance == null) {
            instance = new SessionUtilisateur();
        }
        return instance;
    }

    public void demarrerSession(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
        // Initialisez d'autres informations sur l'utilisateur si nécessaire
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    // Ajoutez d'autres méthodes pour accéder aux informations de l'utilisateur et gérer la session
}
