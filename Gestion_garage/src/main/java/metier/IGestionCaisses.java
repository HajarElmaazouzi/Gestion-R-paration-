package metier;

import dao.CaisseDAO;

public interface IGestionCaisses {
    
    CaisseDAO creerCaisse();
    
    void encaisserPaiement(int userId, Double montant, String description);
    
    void rembourserAvance(int userId, Double montant, String description);
    
    void effectuerRetrait(int userId, Double montant, String description);
    
    CaisseDAO consulterCaisse(int userId);
    
    CaisseDAO consulterCaisseProprietaire(int reparateurId);
    
    Double calculerMontantProprietaire(int reparateurId, double pourcentage);
}
