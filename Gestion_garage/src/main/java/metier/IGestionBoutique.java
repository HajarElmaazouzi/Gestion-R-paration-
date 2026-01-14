package metier;

import java.util.List;

import dao.BoutiqueDAO;
import dao.ReparateurDAO;

public interface IGestionBoutique {
	
	void ajouterBoutique(BoutiqueDAO boutique);
	void supprimerBoutique(int idBoutique);	
	List<BoutiqueDAO> afficherBoutique();
	void removeReparateurBoutique(ReparateurDAO reparateur, int idBoutique);
	List<ReparateurDAO> ListReparateurBoutique(int idBoutique);
	


}
