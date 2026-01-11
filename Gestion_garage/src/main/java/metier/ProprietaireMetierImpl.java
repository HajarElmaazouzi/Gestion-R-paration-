package metier;

import dao.Proprietaire;

import javax.persistence.*;

public class ProprietaireMetierImpl implements IProprietaireMetier {

    private EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("StockApp");

    @Override
    public Proprietaire login(String email, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Proprietaire> q = em.createQuery(
                "SELECT p FROM Proprietaire p WHERE p.email=:e AND p.password=:p",
                Proprietaire.class
            );
            q.setParameter("e", email);
            q.setParameter("p", password);

            return q.getResultList().isEmpty() ? null : q.getResultList().get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public void creerProprietaire(String nom, String email, String password, int boutiqueId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            dao.Boutique b = em.find(dao.Boutique.class, boutiqueId);
            if (b == null) throw new RuntimeException("Boutique introuvable");

            Proprietaire p = new Proprietaire();
            p.setNom(nom);
            p.setEmail(email);
            p.setPassword(password);
            p.setBoutique(b);

            em.persist(p);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
