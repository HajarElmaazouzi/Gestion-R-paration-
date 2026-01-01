package metier;

import dao.Boutique;
import dao.Reparateur;

import javax.persistence.*;

public class ReparateurMetierImpl implements IReparateurMetier {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("StockApp");

    @Override
    public void creerReparateur(String nom, String email, String password,
                                String telephone, String specialite, int boutiqueId) {

        EntityManager em = emf.createEntityManager();

        try {
            Boutique b = em.find(Boutique.class, boutiqueId);
            String hashedPwd = PasswordUtil.hash(password);

            Reparateur r = new Reparateur(
                    nom, email, hashedPwd, telephone, specialite, b
            );

            em.getTransaction().begin();
            em.persist(r);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }
}
