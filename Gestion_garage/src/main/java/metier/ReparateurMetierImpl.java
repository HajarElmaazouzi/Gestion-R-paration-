package metier;

import dao.Reparateur;
import dao.Proprietaire;

import javax.persistence.*;
import java.util.List;

public class ReparateurMetierImpl implements IReparateurMetier {

    private EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("StockApp");

    @Override
    public void creerReparateur(String nom, String email, String password,
                                String telephone, String specialite, int boutiqueId) {

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            dao.Boutique b = em.find(dao.Boutique.class, boutiqueId);
            if (b == null) throw new RuntimeException("Boutique introuvable");

            Reparateur r = new Reparateur();
            r.setNom(nom);
            r.setEmail(email);
            r.setPassword(password);
            r.setTelephone(telephone);
            r.setSpecialite(specialite);
            r.setBoutique(b);

            em.persist(r);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Reparateur login(String email, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            // 🔹 Reparateur
            TypedQuery<Reparateur> q1 = em.createQuery(
                "SELECT r FROM Reparateur r WHERE r.email=:e AND r.password=:p",
                Reparateur.class
            );
            q1.setParameter("e", email);
            q1.setParameter("p", password);

            List<Reparateur> reps = q1.getResultList();
            if (!reps.isEmpty()) return reps.get(0);

            // 🔹 Proprietaire → Reparateur logique
            TypedQuery<Proprietaire> q2 = em.createQuery(
                "SELECT p FROM Proprietaire p WHERE p.email=:e AND p.password=:p",
                Proprietaire.class
            );
            q2.setParameter("e", email);
            q2.setParameter("p", password);

            List<Proprietaire> props = q2.getResultList();
            if (!props.isEmpty()) {
                Proprietaire p = props.get(0);

                Reparateur r = new Reparateur();
                r.setId(p.getId());
                r.setNom(p.getNom());
                r.setEmail(p.getEmail());
                r.setPassword(p.getPassword());
                r.setBoutique(p.getBoutique());

                return r;
            }
        } finally {
            em.close();
        }
        return null;
    }
}
