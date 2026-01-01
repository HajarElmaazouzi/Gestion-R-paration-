package metier;

public interface IReparateurMetier {
    void creerReparateur(String nom, String email, String password,
                         String telephone, String specialite, int boutiqueId);
}
