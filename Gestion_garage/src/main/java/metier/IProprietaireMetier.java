package metier;

import dao.Proprietaire;

public interface IProprietaireMetier {
    Proprietaire login(String email, String password);
    void creerProprietaire(String nom, String email, String password, int boutiqueId);
}
