package Controleur;

public class EtatNiveauPrecedent {
    
	public EtatNiveau etatPrecedent;
	public boolean etatVisitee;

	public EtatNiveauPrecedent(EtatNiveau ePrecedent, boolean eVisitee)
	{
		this.etatPrecedent = ePrecedent;
		this.etatVisitee = eVisitee;
	}
}