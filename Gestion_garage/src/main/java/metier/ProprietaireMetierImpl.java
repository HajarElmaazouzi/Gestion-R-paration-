package metier;

import dao.Boutique;
import dao.Proprietaire;

import javax.persistence.*;

public class ProprietaireMetierImpl implements IProprietaireMetier {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("StockApp");

    @Override
    public void creerProprietaire(String nom,
                                  String email,
                                  String password,
                                  int boutiqueId) {

        EntityManager em = emf.createEntityManager();

        try {
            Boutique boutique = em.find(Boutique.class, boutiqueId);

            String hashedPwd = PasswordUtil.hash(password);

            Proprietaire p = new Proprietaire(
                    nom,
                    email,
                    hashedPwd,
                    boutique
            );

            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }
}
