package metier;

import dao.Boutique;

import javax.persistence.*;
import java.util.List;

public class BoutiqueMetierImpl implements IBoutiqueMetier {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("StockApp");

    @Override
    public Boutique creerBoutique(String nom, String adresse) {
        EntityManager em = emf.createEntityManager();
        Boutique b = new Boutique();
        b.setNom(nom);
        b.setAdresse(adresse);

        try {
            em.getTransaction().begin();
            em.persist(b);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return b;
    }

    @Override
    public List<Boutique> listerBoutiques() {
        EntityManager em = emf.createEntityManager();
        List<Boutique> boutiques;
        try {
            TypedQuery<Boutique> query = em.createQuery("SELECT b FROM Boutique b", Boutique.class);
            boutiques = query.getResultList();
        } finally {
            em.close();
        }
        return boutiques;
    }
}
