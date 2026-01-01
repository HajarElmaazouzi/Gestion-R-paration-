package metier;

import dao.Boutique;

import javax.persistence.*;

public class BoutiqueMetierImpl implements IBoutiqueMetier {

    private EntityManager em ;
        public BoutiqueMetierImpl() { 
        	EntityManagerFactory emf = Persistence.createEntityManagerFactory("StockApp");
        	em = emf.createEntityManager();
			// TODO Auto-generated constructor stub
		}  

    @Override
    public Boutique creerBoutique(String nom, String adresse) {

        Boutique b = new Boutique(nom, adresse);

       
        em.getTransaction().begin();
        em.persist(b);
        em.getTransaction().commit();
        em.close();

        return b;
    }
}
