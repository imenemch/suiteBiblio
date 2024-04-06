package biblio_Gestion_Lecteur;

public class Emprunt {
    private int idEmp;
    private String titre;
    private String dateEmprunt;
    private String dateRetourPrevue;
    private String dateRetourEffectuee;
    private double penalite;

    // Constructeur
    public Emprunt(int idEmp, String titre, String dateEmprunt, String dateRetourPrevue, String dateRetourEffectuee, double penalite) {
        this.idEmp = idEmp;
        this.titre = titre;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetourEffectuee = dateRetourEffectuee;
        this.penalite = penalite;
    }

    // Getters et setters
    public int getIdEmp() {
        return idEmp;
    }

    public void setIdEmp(int idEmp) {
        this.idEmp = idEmp;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDateEmprunt() {
        return dateEmprunt;
    }

    public void setDateEmprunt(String dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }

    public String getDateRetourPrevue() {
        return dateRetourPrevue;
    }

    public void setDateRetourPrevue(String dateRetourPrevue) {
        this.dateRetourPrevue = dateRetourPrevue;
    }

    public String getDateRetourEffectuee() {
        return dateRetourEffectuee;
    }

    public void setDateRetourEffectuee(String dateRetourEffectuee) {
        this.dateRetourEffectuee = dateRetourEffectuee;
    }

    public double getPenalite() {
        return penalite;
    }

    public void setPenalite(double penalite) {
        this.penalite = penalite;
    }
}
