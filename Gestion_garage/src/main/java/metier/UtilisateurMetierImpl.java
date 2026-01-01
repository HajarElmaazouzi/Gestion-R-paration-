package metier;

import dao.Proprietaire;
import dao.Reparateur;

import javax.persistence.*;

public class UtilisateurMetierImpl {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("StockApp");

    public Object login(String email, String password) {

        EntityManager em = emf.createEntityManager();
        String hashedPwd = PasswordUtil.hash(password);

        try {
            TypedQuery<Proprietaire> q1 = em.createQuery(
                "SELECT p FROM Proprietaire p WHERE p.email = :email AND p.password = :pwd",
                Proprietaire.class
            );
            q1.setParameter("email", email);
            q1.setParameter("pwd", hashedPwd);
            return q1.getSingleResult();

        } catch (NoResultException e) {
            try {
                TypedQuery<Reparateur> q2 = em.createQuery(
                    "SELECT r FROM Reparateur r WHERE r.email = :email AND r.password = :pwd",
                    Reparateur.class
                );
                q2.setParameter("email", email);
                q2.setParameter("pwd", hashedPwd);
                return q2.getSingleResult();
            } catch (NoResultException ex) {
                return null;
            }
        } finally {
            em.close();
        }
    }
}
