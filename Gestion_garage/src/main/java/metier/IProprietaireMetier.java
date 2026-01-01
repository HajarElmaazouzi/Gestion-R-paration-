package metier;

public interface IProprietaireMetier {
    void creerProprietaire(String nom,
                           String email,
                           String password,
                           int boutiqueId);
}
