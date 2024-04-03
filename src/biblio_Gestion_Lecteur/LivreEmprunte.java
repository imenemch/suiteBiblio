package biblio_Gestion_Lecteur;

public class LivreEmprunte {

	private String titre;
    private String dateEmprunt;
    private String dateRetourPrevue;

    public LivreEmprunte(String titre, String dateEmprunt, String dateRetourPrevue) {
        this.titre = titre;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
    }

    @Override
    public String toString() {
        return titre + " - Emprunté le " + dateEmprunt + " - Retour prévu le " + dateRetourPrevue;
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
}