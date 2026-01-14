package metier;

import java.util.List;
import dao.EmpruntDAO;

public interface IGestionEmprunts {
    
    EmpruntDAO creerEmprunt(int emprunteurId, Integer preteurId, Double montant, String description);
    
    void rembourserEmprunt(Long empruntId);
    
    List<EmpruntDAO> listerEmpruntsParUtilisateur(int userId);
    
    List<EmpruntDAO> listerEmpruntsEnCours(int userId);
    
    List<EmpruntDAO> listerTousLesEmprunts();
}
