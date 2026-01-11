package metier;

import dao.Reparateur;

public interface IReparateurMetier {
    Reparateur login(String email, String password);
    void creerReparateur(String nom, String email, String password, String telephone, String specialite, int boutiqueId);
}
