package metier;

import dao.Boutique;
import java.util.List;

public interface IBoutiqueMetier {
    Boutique creerBoutique(String nom, String adresse);
    List<Boutique> listerBoutiques();
}
