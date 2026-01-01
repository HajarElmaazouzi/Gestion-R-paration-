package metier;

import dao.Boutique;

public interface IBoutiqueMetier {
    Boutique creerBoutique(String nom, String adresse);
}
